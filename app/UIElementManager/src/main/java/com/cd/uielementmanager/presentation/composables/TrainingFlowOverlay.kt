package com.cd.uielementmanager.presentation.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.graphics.toColorInt
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cd.uielementmanager.R
import com.cd.uielementmanager.domain.contents.HighlightedElementContent
import com.cd.uielementmanager.domain.contents.TrainingStepContent
import com.cd.uielementmanager.domain.contents.UIElementContent
import com.cd.uielementmanager.presentation.beans.ButtonHandlerBean
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import com.cd.uielementmanager.presentation.utils.FunctionHelper.getErrorMessage
import com.cd.uielementmanager.presentation.utils.TextToSpeechManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.min

@Composable
fun TrainingFlowOverlay(viewModel: UIElementViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val ttsManager = remember { TextToSpeechManager(context) }

    Box(modifier = modifier) {
        HandleTrainingFlowStateFlow(viewModel, ttsManager)
    }
    LaunchedEffect(Unit) {
        viewModel.fetchTrainingFlow(context)
    }

    DisposableEffect(ttsManager) {
        onDispose {
            ttsManager.shutdown()
        }
    }
}

@Composable
private fun HandleTrainingFlowStateFlow(
    viewModel: UIElementViewModel,
    ttsManager: TextToSpeechManager
) {
    val context = LocalContext.current
    var isErrorResponseHandled by remember {
        mutableStateOf(false)
    }
    when (val response = viewModel.trainingFlowState.collectAsStateWithLifecycle().value) {
        is DataUiResponseStatus.Loading -> {
            LoadingSection()
            isErrorResponseHandled = false
        }

        is DataUiResponseStatus.Failure -> {
            if (!isErrorResponseHandled) {
                ErrorAlertDialog(
                    context.getErrorMessage(response.errorMessage, response.errorCode),
                    positiveButton = ButtonHandlerBean(stringResource(R.string.dismiss), {
                        isErrorResponseHandled = true
                    })
                )
            }
        }

        is DataUiResponseStatus.Success -> {
            StepDetails(viewModel, response.data.steps, ttsManager)
        }

        is DataUiResponseStatus.None -> {
            // no need to handle it
        }
    }
}

@Composable
private fun StepDetails(
    viewModel: UIElementViewModel,
    steps: List<TrainingStepContent>,
    ttsManager: TextToSpeechManager
) {
    val currentStepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()
    val trackedElements by viewModel.trackedElements.collectAsStateWithLifecycle()
    val density = LocalDensity.current
    val currentStep = steps.getOrNull(currentStepIndex)
    if (currentStep != null) {
        val currentScreenElements = trackedElements[currentStep.screenName]
        val elementToHighlight =
            currentScreenElements?.get(currentStep.highlightedElementContent.elementId)
        if (elementToHighlight != null) {
            val xDp = with(density) { elementToHighlight.bounds.position.x.toDp() }
            val yDp = with(density) { elementToHighlight.bounds.position.y.toDp() }
            val widthDp = with(density) { elementToHighlight.bounds.size.width.toDp() }
            val heightDp = with(density) { elementToHighlight.bounds.size.height.toDp() }
            val borderStrokeWidthDp = with(density) {
                currentStep.highlightedElementContent.borderStrokeWidth?.toDp()
            } ?: 1.dp
            val shape = getBorderShape(density, currentStep.highlightedElementContent)
            val borderColor =
                parseHexColor(currentStep.highlightedElementContent.borderColor) ?: Color.Red
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(false) {}) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val fullScreenPath = Path().apply {
                        addRect(Rect(0f, 0f, size.width, size.height))
                    }
                    val cutoutPath = getCutoutShape(
                        currentStep.highlightedElementContent,
                        elementToHighlight,
                        density
                    )
                    val combinedPath = Path().apply {
                        addPath(fullScreenPath)
                        op(this, cutoutPath, PathOperation.Difference)
                    }
                    drawPath(path = combinedPath, color = Color.Black.copy(alpha = 0.7f))
                }
                Box(Modifier.offset(x = xDp, y = yDp)) {
                    Box(
                        Modifier
                            .width(widthDp)
                            .height(heightDp)
                            .clip(shape)
                            .border(
                                width = borderStrokeWidthDp,
                                color = borderColor,
                                shape = shape
                            )
                            .clickable {
                                viewModel.nextTrainingStep()
                            }
                    )
                    if (currentStep.instructions.isNotEmpty()) {
                        InstructionsSection(currentStep.instructions, ttsManager)
                    }
                }
            }
        }
    }

}

@Composable
private fun InstructionsSection(instructions: List<String>, ttsManager: TextToSpeechManager) {
    if (instructions.isEmpty()) return
    var currentTooltipIndex by remember { mutableIntStateOf(0.coerceIn(0, instructions.size - 1)) }
    var lastInteractionTime by remember { mutableLongStateOf(0L) }
    var dragAmount by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(currentTooltipIndex, instructions) {
        val currentInstruction =
            instructions.getOrNull(currentTooltipIndex) ?: instructions.firstOrNull() ?: ""
        ttsManager.speak(currentInstruction)
    }

    LaunchedEffect(currentTooltipIndex, lastInteractionTime) {
        val timeSinceInteraction = System.currentTimeMillis() - lastInteractionTime
        val delayTime = if (timeSinceInteraction < 5000L) 5000L else 3000L
        delay(delayTime)
        if (instructions.isNotEmpty()) {
            currentTooltipIndex = (currentTooltipIndex + 1) % instructions.size
        }
    }
    DropdownMenu(
        true,
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(0.85f),
        onDismissRequest = {},
        properties = PopupProperties(
            focusable = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (abs(dragAmount) > 50) {
                                currentTooltipIndex = if (dragAmount < 0) {
                                    (currentTooltipIndex + 1) % instructions.size
                                } else {
                                    if (currentTooltipIndex == 0) {
                                        instructions.size - 1
                                    } else {
                                        currentTooltipIndex - 1
                                    }
                                }
                                lastInteractionTime = System.currentTimeMillis()
                            }
                            dragAmount = 0f
                        },
                        onHorizontalDrag = { _, dragDelta ->
                            dragAmount += dragDelta
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    instructions.getOrNull(currentTooltipIndex) ?: instructions.firstOrNull() ?: "",
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            if (instructions.size > 1) {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(instructions.size) { iteration ->
                        val color = if (currentTooltipIndex == iteration) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    currentTooltipIndex = iteration
                                    lastInteractionTime = System.currentTimeMillis()
                                }
                        )
                    }
                }
            }
        }
    }
}

private fun getBorderShape(density: Density, highlightedElement: HighlightedElementContent): Shape {
    val borderRadiusDp = highlightedElement.borderRadius?.let {
        with(density) { it.toDp() }
    } ?: 8.dp
    return when (highlightedElement.borderShape?.trim()) {
        "circle" -> CircleShape
        "rounded" -> RoundedCornerShape(borderRadiusDp)
        else -> RectangleShape
    }
}

/**
 * Parse hex color string to Compose Color
 * @param hexColor Hex color string (e.g., "#FF0000", "FF0000")
 * @return Parsed Color or null if invalid
 */
private fun parseHexColor(hexColor: String?): Color? {
    if (hexColor == null) return null
    return try {
        val colorString = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
        Color(colorString.trim().toColorInt())
    } catch (_: Exception) {
        null
    }
}


private fun getCutoutShape(
    highlightedElement: HighlightedElementContent,
    elementToHighlight: UIElementContent,
    density: Density
): Path {
    val x = elementToHighlight.bounds.position.x
    val y = elementToHighlight.bounds.position.y
    val width = elementToHighlight.bounds.size.width.toFloat()
    val height = elementToHighlight.bounds.size.height.toFloat()
    return Path().apply {
        when (highlightedElement.borderShape?.trim()) {
            "circle" -> {
                val centerX = x + width / 2
                val centerY = y + height / 2
                val radius = min(width, height) / 2
                addOval(
                    Rect(
                        centerX - radius,
                        centerY - radius,
                        centerX + radius,
                        centerY + radius
                    )
                )
            }

            "rounded" -> {
                val cornerRadius = highlightedElement.borderRadius ?: with(density) { 8.dp.toPx() }
                addRoundRect(
                    RoundRect(
                        left = x,
                        top = y,
                        right = x + width,
                        bottom = y + height,
                        cornerRadius = CornerRadius(cornerRadius)
                    )
                )
            }

            else -> {
                addRect(Rect(x, y, x + width, y + height))
            }
        }
    }
}
