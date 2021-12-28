package com.keykat.keykat.brawlkat.service.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.keykat.keykat.brawlkat.service.systembarservice.RestartService

class AlarmTool: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val restartIntent = Intent(context, RestartService::class.java)
        context.startForegroundService(restartIntent)
    }
}