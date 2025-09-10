package com.cd.screenshotsender.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.core.net.toUri
import com.cd.screenshotsender.presentation.utils.FunctionHelper.showToast
import com.cd.screenshotsender.presentation.service.ScreenShotSenderService

object ScreenShotSenderSDK {

    private const val OVERLAY_PERMISSION_REQUEST_CODE = 1234

    fun startSDK(activity: Activity, rootView: View) {
        if (!Settings.canDrawOverlays(activity)) {
            requestOverlayPermission(activity)
        }
        if (isSDKRunning()) return
        try {
            val intent = Intent(activity, ScreenShotSenderService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.startForegroundService(intent)
            } else {
                activity.startService(intent)
            }
            ScreenShotSenderService.Companion.setRootView(rootView)
        } catch (e: Exception) {
            activity.showToast("Failed to start tracking service ${e.localizedMessage}")
        }
    }

    fun stopSDK(context: Context) {
        context.stopService(Intent(context, ScreenShotSenderService::class.java))
    }

    fun isSDKRunning(): Boolean {
        return ScreenShotSenderService.Companion.isServiceRunning()
    }

    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${activity.packageName}".toUri()
        )
        activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}