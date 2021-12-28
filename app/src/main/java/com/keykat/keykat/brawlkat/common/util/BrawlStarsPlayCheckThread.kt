//package com.keykat.keykat.brawlkat.common.util
//
//import android.annotation.SuppressLint
//import android.app.Service
//import android.app.usage.UsageEvents
//import android.app.usage.UsageStatsManager
//import android.content.Context
//import android.os.Build
//import android.util.LongSparseArray
//import androidx.annotation.RequiresApi
//import androidx.preference.PreferenceManager
//import com.keykat.keykat.brawlkat.BuildConfig
//import com.keykat.keykat.brawlkat.R
//import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity
//import com.keykat.keykat.brawlkat.service.maprecommendservice.ui.kat_Service_OverdrawActivity
//import java.util.*
//
///*
//--isBackgroundServiceOn--
//ActionBroadcastReceiver : screen 상태와 switch on/off 상태를 보고 viewModel에 전달
//viewModel : BrawlNotifActivity에 전달
//BrawlNotifActivity : 전달받은 데이터를 observing 해서 아래 스레드를 시작/중지 결정
//
//-isBackgroundServiceStart--
//Main Activity에서 실행 or 서비스로 실행했을 때 --> ViewModel의 isBackgroundServiceStart = true
//꾹 눌러서 종료하거나 알림창 등에서 종료가 되었을 때 --> isBackgroundServiceStart = false
//isBrawlStarsPlayCheckThread는 isBackgroundServiceStart의 상태를 보고 체크
// */
//class BrawlStarsPlayCheckThread(var service: Service) :
//    Thread() {
//    @RequiresApi(Build.VERSION_CODES.Q)
//    @SuppressLint("StringFormatInvalid")
//    override fun run() {
//
//        // 브롤러 추천 서비스 알림창을 on 했을 때
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(service)
//
//        while (true) {
//            try {
//                val currentAppName = getTopPackageName(service)?.lowercase(Locale.getDefault())
//                // 브롤스타즈가 실행되고 서비스가 아직 실행되지 않았다면
//                currentAppName?.let {
//                    if (it.contains(service.getString(R.string.appName))) {
//                        if (sharedPreferences.getBoolean(
//                                service.getString(R.string.auto_service),
//                                false
//                            )
//                        ) {
//                            if (KatData.isBackgroundServiceStart) {
//                                kat_Service_OverdrawActivity.getPlayerTag =
//                                    kat_Player_MainActivity.playerTag
//                                service.startService(kat_Player_MainActivity.serviceIntent)
//                                KatData.isBackgroundServiceStart = true
//                            }
//                        }
//                    }
//                }
//                sleep(3000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun getTopPackageName(context: Context): String? {
//        val usageStatsManager =
//            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//        var lastRunAppTimeStamp = 0L
//        val INTERVAL: Long = 10000
//        val end = System.currentTimeMillis()
//        // 1 minute ago
//        val begin = end - INTERVAL
//        val packageNameMap: LongSparseArray<*> = LongSparseArray<Any>()
//        val usageEvents = usageStatsManager.queryEvents(begin, end)
//        while (usageEvents.hasNextEvent()) {
//            val event = UsageEvents.Event()
//            usageEvents.getNextEvent(event)
//            if (isForeGroundEvent(event)) {
//                packageNameMap.put(event.timeStamp, event.packageName as Nothing?)
//                if (event.timeStamp > lastRunAppTimeStamp) {
//                    lastRunAppTimeStamp = event.timeStamp
//                }
//            }
//        }
//        return packageNameMap.get(lastRunAppTimeStamp).toString()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.Q)
//    private fun isForeGroundEvent(event: UsageEvents.Event?): Boolean {
//        if (event == null) {
//            return false
//        }
//        return if (BuildConfig.VERSION_CODE >= 29) {
//            event.eventType == UsageEvents.Event.ACTIVITY_RESUMED
//        } else event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
//    }
//}
//
