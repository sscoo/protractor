package com.example.protractoroverlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.NotificationCompat

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createOverlay()
    }

    override fun onDestroy() {
        overlayView?.let(windowManager::removeView)
        overlayView = null
        super.onDestroy()
    }

    private fun createOverlay() {
        if (overlayView != null) return

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val root = inflater.inflate(R.layout.overlay_protractor, null)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 120
            y = 220
        }

        val protractorView = root.findViewById<ProtractorOverlayView>(R.id.protractorView)
        val angleLabel = root.findViewById<TextView>(R.id.angleLabel)
        val closeButton = root.findViewById<ImageButton>(R.id.closeButton)

        protractorView.setOnAngleChangeListener { angle ->
            angleLabel.text = String.format("%.1f°", angle)
        }

        closeButton.setOnClickListener {
            stopSelf()
        }

        root.setOnTouchListener(DragTouchListener(params) { updated ->
            windowManager.updateViewLayout(root, updated)
        })

        windowManager.addView(root, params)
        overlayView = root
    }

    private fun buildNotification(): Notification {
        createChannelIfNeeded()
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_protractor)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Overlay Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private class DragTouchListener(
        private val params: WindowManager.LayoutParams,
        private val onMove: (WindowManager.LayoutParams) -> Unit
    ) : View.OnTouchListener {
        private var initialX: Int = 0
        private var initialY: Int = 0
        private var touchX: Float = 0f
        private var touchY: Float = 0f

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    false
                }

                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - touchX).toInt()
                    params.y = initialY + (event.rawY - touchY).toInt()
                    onMove(params)
                    true
                }

                else -> false
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "protractor_overlay"
        private const val NOTIFICATION_ID = 4201
    }
}
