package com.keykat.keykat.brawlkat.service.systembarservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.keykat.keykat.brawlkat.R

class RestartService : Service() {

    private var notification: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var channel: NotificationChannel? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        initChannel()
        initNotificationManager()
        initNotification()
        startBrawlStarsNotificationService()

        return START_STICKY
    }

    private fun initChannel() {
        channel = NotificationChannel(
            "restart",
            "restart service",
            NotificationManager.IMPORTANCE_LOW
        )
    }

    private fun initNotificationManager() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channel?.let { notificationManager?.createNotificationChannel(it) }
    }

    private fun initNotification() {
        notification = NotificationCompat.Builder(this, "restart")
            .setSmallIcon(R.drawable.kat_notification_icon).apply {
                startForeground(1, build())
            }
    }

    private fun startBrawlStarsNotificationService() {
        val intent = Intent(this, BrawlStarsNotificationService::class.java)
        startService(intent)

        stopForeground(true)
        stopSelf()
    }
}

