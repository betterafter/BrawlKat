package com.keykat.keykat.brawlkat.service.systembarservice

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.keykat.keykat.brawlkat.common.model.datasource.SharedPreferenceManager
import com.keykat.keykat.brawlkat.service.model.data.NotificationData
import com.keykat.keykat.brawlkat.service.util.AlarmTool
import com.keykat.keykat.brawlkat.service.util.NotificationContract
import com.keykat.keykat.brawlkat.service.util.NotificationPresenter
import com.keykat.keykat.brawlkat.service.util.NotificationUpdater
import java.lang.Exception
import java.util.*

class BrawlStarsNotificationService : Service(), NotificationContract.View {

    var notification: NotificationCompat.Builder? = null
    var notificationManager: NotificationManager? = null
    private var channel: NotificationChannel? = null
    private var notificationPresenter: NotificationPresenter? = null
    var updater: NotificationUpdater? = null

    companion object {
        var normalExit = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        notificationPresenter = NotificationPresenter(this)
        initChannel()
        initNotificationManager()
        initNotification(NotificationData(null, null, null, null))
        fetchNotification()
        return START_STICKY
    }

    private fun initChannel() {
        channel = NotificationChannel(
            "channel",
            "brawl stars play",
            NotificationManager.IMPORTANCE_LOW
        )
    }

    private fun initNotificationManager() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        channel?.let { notificationManager?.createNotificationChannel(it) }
    }

    private fun initNotification(notificationData: NotificationData?) {
        notificationManager?.let { manager ->
            notificationData?.let { data ->
                updater = NotificationUpdater(this, manager, data)
                updater?.update()
                notification = updater?.getUpdatedNotification()
                startForeground(1, notification?.build())
            }
        }
    }

    override fun updateService(notificationData: NotificationData) {
        notificationManager?.let { manager ->
            updater = NotificationUpdater(this, manager, notificationData)
            updater?.update()
            notification = updater?.getUpdatedNotification()
            manager.notify(1, notification?.build())
        }
    }

    private fun fetchNotification() {
        try {
            val sharedPreferenceManager = SharedPreferenceManager(this)
            var playerTag = sharedPreferenceManager.getAccount()
            if (playerTag != null && playerTag != "") {
                playerTag = playerTag.substring(1)
                notificationPresenter?.loadData(playerTag, "players", "official")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!normalExit) {
            setAlarmTimer()
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarmTimer() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, 0)

        val intent = Intent(this, AlarmTool::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, sender)
    }

}