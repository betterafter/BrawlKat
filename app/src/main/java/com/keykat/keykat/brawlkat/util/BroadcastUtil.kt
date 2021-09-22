//package com.keykat.keykat.brawlkat.util
//
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import androidx.preference.PreferenceManager
//import com.keykat.keykat.brawlkat.R
//import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity
//import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity
//import com.keykat.keykat.brawlkat.service.util.kat_ActionBroadcastReceiver
//
//fun registerBroadcastReceiver(activity: Activity) {
//
//    var broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver
//    val screenOnCode = activity.getString(R.string.screen_on)
//    val screenOffCode = activity.getString(R.string.screen_off)
//    val checkStartCode = activity.getString(R.string.check_start)
//    val checkEndCode = activity.getString(R.string.check_end)
//
//    val preference = PreferenceManager.getDefaultSharedPreferences(activity)
//
//    if(!preference.getBoolean(activity.getString(R.string.notify_service), false)) return
//    if (broadcastReceiver != null) return
//
//    val filter = IntentFilter()
//    filter.addAction(screenOnCode)
//    filter.addAction(screenOffCode)
//    filter.addAction(checkStartCode)
//    filter.addAction(checkEndCode)
//
//    broadcastReceiver = kat_ActionBroadcastReceiver(
//        kat_Player_MainActivity.kat_player_mainActivity
//    )
//    activity.registerReceiver(broadcastReceiver, filter)
//
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = checkStartCode
//    activity.sendBroadcast(threadCheckIntent)
//}
//
//
//fun unregisterBroadcastReceiver(activity: Activity) {
//
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = activity.getString(R.string.check_end)
//    activity.sendBroadcast(threadCheckIntent)
//
//    val broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver
//    if (broadcastReceiver != null) {
//        activity.unregisterReceiver(broadcastReceiver)
//        kat_Service_BrawlStarsNotifActivity.broadcastReceiver = null
//    }
//}
//
//
//fun registerBroadcastReceiver(context: Context) {
//
//    var broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver
//    val screenOnCode = context.getString(R.string.screen_on)
//    val screenOffCode = context.getString(R.string.screen_off)
//    val checkStartCode = context.getString(R.string.check_start)
//    val checkEndCode = context.getString(R.string.check_end)
//
//    val preference = PreferenceManager.getDefaultSharedPreferences(context)
//
//    if(!preference.getBoolean(context.getString(R.string.notify_service), false)) return
//    if (broadcastReceiver != null) return
//
//    val filter = IntentFilter()
//    filter.addAction(screenOnCode)
//    filter.addAction(screenOffCode)
//    filter.addAction(checkStartCode)
//    filter.addAction(checkEndCode)
//
//    broadcastReceiver = kat_ActionBroadcastReceiver(
//        kat_Player_MainActivity.kat_player_mainActivity
//    )
//    context.registerReceiver(broadcastReceiver, filter)
//
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = checkStartCode
//    context.sendBroadcast(threadCheckIntent)
//}
//
//
//fun unregisterBroadcastReceiver(context: Context) {
//
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = context.getString(R.string.check_end)
//    context.sendBroadcast(threadCheckIntent)
//
//    val broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver
//    if (broadcastReceiver != null) {
//        context.unregisterReceiver(broadcastReceiver)
//        kat_Service_BrawlStarsNotifActivity.broadcastReceiver = null
//    }
//}
//
//fun sendCheckStartBroadcast(context: Context) {
//    val checkStartCode = context.getString(R.string.check_start)
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = checkStartCode
//    context.sendBroadcast(threadCheckIntent)
//}
//
//fun sendCheckEndBroadcast(context: Context) {
//    val checkEndCode = context.getString(R.string.check_end)
//    val threadCheckIntent = Intent()
//    threadCheckIntent.action = checkEndCode
//    context.sendBroadcast(threadCheckIntent)
//}
