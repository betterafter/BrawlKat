package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import android.content.Context
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.network.BaseApiDataThread

class MapRecommendDataSource(
    context: Context
) {
    val baseApiDataThread = BaseApiDataThread(context)

    fun getPlayerInfoData() = KatData.eventsPlayerData.value
    fun getMapRecommendViewData(viewCallback: () -> (Unit)) {
        if(KatData.EventArrayList.size <= 0 || KatData.BrawlersArrayList.size <= 0) {
            baseApiDataThread.getData(viewCallback)
        }
    }
}