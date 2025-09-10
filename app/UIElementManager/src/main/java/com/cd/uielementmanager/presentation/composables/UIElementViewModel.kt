package com.cd.uielementmanager.presentation.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.Toast
import androidx.compose.ui.geometry.Rect
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.domain.contents.BoundsContent
import com.cd.uielementmanager.domain.contents.PositionContent
import com.cd.uielementmanager.domain.contents.SizeContent
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.contents.UIElementContent
import com.cd.uielementmanager.domain.domain_utils.AppErrorCodes
import com.cd.uielementmanager.domain.use_cases.GetTrainingFlowUseCase
import com.cd.uielementmanager.domain.use_cases.SendPackageNameUseCase
import com.cd.uielementmanager.domain.use_cases.SendScreenshotUseCase
import com.cd.uielementmanager.domain.use_cases.SendUIElementsUseCase
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.mapToDataUiResponseStatus
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

/**
 * ViewModel for UI element tracking following clean architecture
 * Matches the pattern from training flow feature
 */
class UIElementViewModel() : ViewModel() {


    // State flows for tracked elements - organized by screen name
    private val _trackedElements =
        MutableStateFlow<Map<String, Map<String, UIElementContent>>>(emptyMap())
    val trackedElements: StateFlow<Map<String, Map<String, UIElementContent>>> =
        _trackedElements.asStateFlow()

    private val _sendUiElementsStateFlow: MutableStateFlow<DataUiResponseStatus<Unit>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

    val sendUiElementsStateFlow = _sendUiElementsStateFlow.asStateFlow()

    private val _sendPackageNameStateFlow: MutableStateFlow<DataUiResponseStatus<PackageNameResponse>> =
        MutableStateFlow(DataUiResponseStatus.Companion.none())

    val sendPackageNameUseCase = _sendPackageNameStateFlow.asStateFlow()

    // Training flow state management
    private val _trainingFlowStateFlow =
        MutableStateFlow<DataUiResponseStatus<TrainingFlowContent>>(
            DataUiResponseStatus.none()
        )
    val trainingFlowState = _trainingFlowStateFlow.asStateFlow()

    // Current training step index
    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex = _currentStepIndex.asStateFlow()

    private var currentScreen: String? = null


    fun setCurrentScreen(screen: String) {
        val previousScreen = currentScreen
        if (previousScreen != null && previousScreen != screen) {
            clearElementsForScreen(previousScreen)
        }
        currentScreen = screen
    }


    fun clearElementsForScreen(screen: String) {

        _trackedElements.update { screenMap ->
            screenMap - screen
        }

        // If clearing the current screen, reset the current screen reference
        if (currentScreen == screen) {
            currentScreen = null
        }
    }


    fun getCurrentScreen(): String? {
        return currentScreen
    }

    /**
     * Register a UI element for tracking
     * @param elementScreenName Screen name from the trackElement call
     * @param tag Unique identifier for the UI element
     * @param bounds Complete position and size information
     */
    fun registerElement(elementScreenName: String, tag: String, bounds: Rect) {
        val currentScreenName = currentScreen ?: return
        if (elementScreenName != currentScreenName) {
            return
        }
        val element = UIElementContent(
            tag = tag,
            bounds = BoundsContent(
                position = PositionContent(bounds.left, bounds.top),
                size = SizeContent(bounds.width.toInt(), bounds.height.toInt())
            ),
        )
        _trackedElements.update { screenMap ->
            val screenElements = screenMap[currentScreenName] ?: emptyMap()
            screenMap + (currentScreenName to (screenElements + (tag to element)))
        }
    }


    /**
     * Get tracked elements for the current screen
     */
    fun getTrackedElements(): Map<String, UIElementContent> {
        val screenName = currentScreen ?: return emptyMap()
        return _trackedElements.value[screenName] ?: emptyMap()
    }


    /**
     * Extract and send UI data to server using clean architecture
     */
    fun sendScreenShot(context: Context, flowId: Int, rootView: View) {
        if (_sendUiElementsStateFlow.value is DataUiResponseStatus.Loading) {
            return
        }
        _sendUiElementsStateFlow.value = DataUiResponseStatus.loading()

        viewModelScope.launch {
            _sendUiElementsStateFlow.value = try {
                val screenshotFile = captureScreenshot(rootView, context)

                val screenshotPart = MultipartBody.Part.createFormData(
                    "screenshot",
                    screenshotFile.name,
                    screenshotFile.asRequestBody("image/png".toMediaTypeOrNull())
                )

                val sendScreenshotUseCase = SendScreenshotUseCase()

                val response =
                    sendScreenshotUseCase.invoke(
                        flowId = flowId,
                        screenshotPart,
                        context
                    ).mapToDataUiResponseStatus()

                if (response is DataUiResponseStatus.Success) {
                    screenshotFile.delete()
                }
                response
            } catch (exception: Exception) {
                DataUiResponseStatus.failure(
                    exception.localizedMessage ?: exception.message ?: "",
                    AppErrorCodes.UNKNOWN_ERROR
                )
            }
        }
    }

    fun sendPackageName(context: Context, callback: (Int) -> Unit) {
        if (_sendPackageNameStateFlow.value is DataUiResponseStatus.Loading) {
            return
        }
        _sendPackageNameStateFlow.value = DataUiResponseStatus.loading()

        viewModelScope.launch {
            _sendPackageNameStateFlow.value = try {
                val packageName = context.packageName
                val sendPackageNameUseCase = SendPackageNameUseCase()
                val response =
                    sendPackageNameUseCase.invoke(packageName, context).mapToDataUiResponseStatus()


                if (response is DataUiResponseStatus.Success) {
                    callback(response.data.flowId)
                    Toast.makeText(context, "package uploaded successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                response
            } catch (exception: Exception) {
                DataUiResponseStatus.failure(
                    exception.localizedMessage ?: exception.message ?: "",
                    AppErrorCodes.UNKNOWN_ERROR
                )
            }
        }
    }

    private fun createScreenInfo(context: Context): RequestBody {
        val displayMetrics = context.resources.displayMetrics
        val gson = Gson()
        val screenInfoJson = gson.toJson(
            mapOf("width" to displayMetrics.widthPixels, "height" to displayMetrics.heightPixels)
        )
        return screenInfoJson.toRequestBody("text/plain".toMediaType())
    }

    private suspend fun captureScreenshot(view: View, context: Context): File =
        withContext(Dispatchers.Main) {
            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            withContext(Dispatchers.IO) {
                val timestamp = System.currentTimeMillis()
                val file = File(context.cacheDir, "screenshot_$timestamp.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                file
            }
        }

    /**
     * Clear tracked elements for a specific screen or the current screen
     * @param screenName Optional screen name to clear. If null, clears current screen
     */
    fun clearTrackedElements(screenName: String? = null) {
        val screen = screenName ?: currentScreen ?: return
        _trackedElements.update { screenMap ->
            screenMap - screen
        }
        _sendUiElementsStateFlow.value = DataUiResponseStatus.none()
    }

    /**
     * Fetch training flow data from server
     * @param context Application context
     */
    fun fetchTrainingFlow(context: Context) {
        viewModelScope.launch {
            _trainingFlowStateFlow.value = DataUiResponseStatus.loading()
            try {
                val getTrainingFlowUseCase = GetTrainingFlowUseCase()
                val response = getTrainingFlowUseCase.invoke(context)
                    .mapToDataUiResponseStatus()
                _trainingFlowStateFlow.value = response
                // Reset step index when new flow is loaded
                if (response is DataUiResponseStatus.Success && response.data.steps.isNotEmpty()) {
                    _currentStepIndex.value = 0
                } else {
                    _currentStepIndex.value = -1
                }
            } catch (exception: Exception) {
                _trainingFlowStateFlow.value = DataUiResponseStatus.failure(
                    exception.localizedMessage ?: exception.message ?: "",
                    AppErrorCodes.UNKNOWN_ERROR
                )
            }
        }
    }

    /**
     * Navigate to next training step
     */
    fun nextTrainingStep() {
        val flow = (_trainingFlowStateFlow.value as? DataUiResponseStatus.Success)?.data
        flow?.let {
            _currentStepIndex.update { current ->
                if (current < it.steps.size - 1) current + 1 else current
            }
        }
    }

    override fun onCleared() {
        HttpClientManager.clearInstance()
        super.onCleared()
    }
}