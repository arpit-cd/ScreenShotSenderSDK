package com.cd.screenshotsender.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.View
import androidx.core.app.NotificationCompat
import com.cd.screenshotsender.data.network.HttpClientManager
import com.cd.screenshotsender.presentation.FileUploadTracker
import com.cd.screenshotsender.presentation.overlay.TrackingOverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel


internal class ScreenShotSenderService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "screenshot_sender_sdk"
        private const val ACTION_STOP_SERVICE = "com.cd.screenshotsender.STOP_TRACKING"

        internal fun setRootView(view: View) {
            instance?.overlayManager?.setRootView(view)
        }

        internal fun isServiceRunning(): Boolean {
            return instance != null
        }

        private var instance: ScreenShotSenderService? = null
    }

    private lateinit var fileUploadTracker: FileUploadTracker
    private lateinit var overlayManager: TrackingOverlayManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        instance = this
        fileUploadTracker = FileUploadTracker()
        overlayManager = TrackingOverlayManager(this, fileUploadTracker)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                stopSelf()
                return START_NOT_STICKY
            }

            else -> {
                startForeground(NOTIFICATION_ID, createNotification())
                overlayManager.showOverlay()
            }
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        overlayManager.hideOverlay()
        serviceScope.cancel()
        instance = null
        HttpClientManager.clearInstance()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screenshot Sender",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when Screenshot Sender is active"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String = "Tracking Screenshots..."): Notification {
        val stopIntent = Intent(this, ScreenShotSenderService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Screenshot Sender")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                stopPendingIntent
            )
            .build()
    }


}