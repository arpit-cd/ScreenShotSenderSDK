package com.cd.screenshotsender.presentation.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.cd.screenshotsender.presentation.FileUploadTracker
import com.cd.screenshotsender.presentation.utils.DataUiResponseStatus
import com.cd.screenshotsender.presentation.utils.FunctionHelper.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Manages the overlay window for UI element tracking FAB
 */
internal class TrackingOverlayManager(
    private val context: Context,
    private val fileUploadTracker: FileUploadTracker
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: ViewBasedTrackingOverlay? = null
    private var isOverlayShown = false
    private var rootView: View? = null
    private lateinit var packageName: String
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Show the tracking overlay
     */
    fun showOverlay() {
        if (isOverlayShown && overlayView != null) {
            return
        }
        try {
            val trackingOverlay = ViewBasedTrackingOverlay(
                context = context,
                onSendClicked = {
                    sendScreenShotToServer()
                },
            )
            observeUploadStatus(trackingOverlay)
            val params = WindowManager.LayoutParams().apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                format = PixelFormat.TRANSLUCENT
                gravity = Gravity.BOTTOM or Gravity.END
                x = 32
                y = 100
            }
            windowManager.addView(trackingOverlay, params)
            overlayView = trackingOverlay
            isOverlayShown = true
        } catch (e: Exception) {
            e.printStackTrace()
            isOverlayShown = false
            context.showToast("Error showing overlay ${e.localizedMessage}")
        }
    }

    /**
     * Hide the tracking overlay
     */
    fun hideOverlay() {
        if (!isOverlayShown) return
        try {
            overlayView?.let {
                windowManager.removeView(it)
                overlayView = null
                isOverlayShown = false
            }
        } catch (e: Exception) {
            context.showToast("Error while hiding overlay ${e.localizedMessage}")
        }
    }

    /**
     * Observe upload status and update FAB states accordingly
     */
    private fun observeUploadStatus(overlay: ViewBasedTrackingOverlay) {
        coroutineScope.launch {
            fileUploadTracker.sendScreenShotStateFlow.collectLatest { status ->
                when (status) {
                    is DataUiResponseStatus.Loading -> {
                        overlay.showLoading()
                    }

                    is DataUiResponseStatus.Success -> {
                        overlay.showSuccess()
                        context.showToast("Upload successful!")
                        delay(3000)
                        overlay.resetToNormalState()
                    }

                    is DataUiResponseStatus.Failure -> {
                        overlay.showError()
                        context.showToast("Upload failed: ${status.errorMessage}")
                        delay(3000)
                        overlay.resetToNormalState()
                    }

                    else -> {
                        // no need to handle things
                    }
                }
            }
        }
    }

    /**
     * Get the root view for screenshot capture
     * This tries multiple approaches to find a suitable root view
     */
    private fun getRootView(): View? {
        return rootView ?: findRootViewFromWindowManager()
    }

    /**
     * Try to find the root view from the window manager
     */
    @SuppressLint("PrivateApi")
    private fun findRootViewFromWindowManager(): View? {
        return try {
            // Method 1: Try to get root views from WindowManager via reflection
            val windowManagerGlobal = Class.forName("android.view.WindowManagerGlobal")
                .getMethod("getInstance")
                .invoke(null)
            val getViewRootNames = windowManagerGlobal.javaClass
                .getDeclaredMethod("getViewRootNames")
            val getRootView = windowManagerGlobal.javaClass
                .getDeclaredMethod("getRootView", String::class.java)
            val viewRootNames = getViewRootNames.invoke(windowManagerGlobal) as Array<*>
            for (name in viewRootNames) {
                val rootView = getRootView.invoke(windowManagerGlobal, name) as? View
                if (rootView != null && rootView != overlayView && isMainActivityView(rootView)) {
                    return rootView
                }
            }
            for (name in viewRootNames) {
                val rootView = getRootView.invoke(windowManagerGlobal, name) as? View
                if (rootView != null && rootView != overlayView) {
                    return rootView
                }
            }
            null
        } catch (e: Exception) {
            context.showToast("Unable to find root view ${e.localizedMessage}")
            null
        }
    }

    /**
     * Check if this view likely belongs to the main activity
     */
    private fun isMainActivityView(view: View): Boolean {
        return try {
            val context = view.context
            val className = context.javaClass.simpleName
            className.contains("Activity") ||
                    className.contains("MainActivity") ||
                    view.width > 500 && view.height > 500 // Large views are likely main content
        } catch (_: Exception) {
            false
        }
    }


    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    private fun temporarilyHideOverlay(callback: suspend () -> Unit) {
        coroutineScope.launch {
            try {
                overlayView?.visibility = View.GONE
                callback()
            } catch (e: Exception) {
                context.showToast("Error capturing screenshot: ${e.localizedMessage}")
            } finally {
                overlayView?.visibility = View.VISIBLE
            }
        }
    }

    private fun sendScreenShotToServer() {
        temporarilyHideOverlay {
            val view = getRootView()
            if (view != null) {
                fileUploadTracker.sendScreenShot(context, view, packageName)
            } else {
                context.showToast("No view available for screenshot. Please ensure app is in foreground.")
            }
        }
    }
}