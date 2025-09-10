package com.cd.uielementmanager.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.view.View
import androidx.core.app.NotificationCompat
import com.cd.uielementmanager.presentation.composables.UIElementViewModel
import com.cd.uielementmanager.presentation.overlay.TrackingOverlayManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

/**
 * Foreground service that manages UI element tracking overlay
 */
class UIElementTrackingService : Service(), KoinComponent {

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ui_element_tracking_channel"
        private const val ACTION_STOP_SERVICE = "com.cd.uielementmanager.STOP_TRACKING"

        internal fun startService(context: Context) {
            val intent = Intent(context, UIElementTrackingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        internal fun stopService(context: Context) {
            context.stopService(Intent(context, UIElementTrackingService::class.java))
        }

        /**
         * Set root view for screenshot capture from the activity
         */
        fun setRootView(view: View) {
            instance?.overlayManager?.setRootView(view)
        }
        
        /**
         * Check if the service is currently running
         */
        internal fun isRunning(): Boolean {
            return instance != null
        }

        private var instance: UIElementTrackingService? = null
    }

    private val uiElementViewModel: UIElementViewModel by inject()
    private lateinit var overlayManager: TrackingOverlayManager
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        instance = this
        overlayManager = TrackingOverlayManager(this, uiElementViewModel)
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
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "UI Element Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when UI element tracking is active"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String = "Tracking UI elements..."): Notification {
        val stopIntent = Intent(this, UIElementTrackingService::class.java).apply {
            action = ACTION_STOP_SERVICE
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("UI Element Tracking Active")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_view) // Use a default icon
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