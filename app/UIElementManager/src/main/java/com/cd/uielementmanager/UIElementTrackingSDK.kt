package com.cd.uielementmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.cd.uielementmanager.presentation.utils.FunctionHelper.showToast
import com.cd.uielementmanager.service.UIElementTrackingService
import androidx.core.net.toUri

/**
 * Main entry point for the UI Element Tracking SDK
 * Provides simple API to start/stop tracking UI elements across the app
 */
object UIElementTrackingSDK {

    private const val TAG = "UIElementTrackingSDK"
    private const val OVERLAY_PERMISSION_REQUEST_CODE = 1234

    /**
     * Start tracking UI elements
     * Automatically checks for overlay permission and requests it if needed
     *
     * @param activity The activity to use for permission request
     */
    fun startService(activity: Activity) {
        if (!Settings.canDrawOverlays(activity)) {
            requestOverlayPermission(activity)
        }
        try {
            UIElementTrackingService.startService(activity)
        } catch (e: Exception) {
            activity.showToast("Failed to start tracking service ${e.localizedMessage}")
        }
    }

    /**
     * Stop tracking UI elements
     */
    fun stopService(context: Context) {
        UIElementTrackingService.stopService(context)
        Log.d(TAG, "Tracking service stopped")
    }

    /**
     * Check if tracking service is currently running
     */
    fun isRunning(): Boolean {
        return UIElementTrackingService.isRunning()
    }

    /**
     * Request overlay permission
     * Opens system settings for the user to grant permission
     */
    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${activity.packageName}".toUri()
        )
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}