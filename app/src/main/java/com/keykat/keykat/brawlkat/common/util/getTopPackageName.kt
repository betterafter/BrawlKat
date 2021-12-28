package com.keykat.keykat.brawlkat.common.util

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.LongSparseArray

fun getTopPackageName(context: Context): String? {

    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    var lastRunAppTimeStamp = 0L
    val INTERVAL: Long = 10000
    val end = System.currentTimeMillis()
    // 1 minute ago
    val begin = end - INTERVAL
    val packageNameMap: LongSparseArray<Any> = LongSparseArray<Any>()
    val usageEvents = usageStatsManager.queryEvents(begin, end)

    while (usageEvents.hasNextEvent()) {

        val event = UsageEvents.Event()
        usageEvents.getNextEvent(event)

        packageNameMap.put(event.timeStamp, event.packageName)
        if (event.timeStamp > lastRunAppTimeStamp) {
            lastRunAppTimeStamp = event.timeStamp
        }
    }
    val result = packageNameMap.get(lastRunAppTimeStamp) ?: return ""
    return result.toString()
}