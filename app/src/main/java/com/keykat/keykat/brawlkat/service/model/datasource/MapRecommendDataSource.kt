package com.keykat.keykat.brawlkat.service.model.datasource

import android.content.Context
import com.keykat.keykat.brawlkat.service.model.data.NotificationData
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.network.BaseApiDataThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapRecommendDataSource(
    context: Context
) {
    private val baseApiDataThread = BaseApiDataThread(context)

    fun getPlayerInfoData() = KatData.eventsPlayerData.value

    fun getMapRecommendViewData(viewCallback: (NotificationData) -> (Unit)) {
        CoroutineScope(Dispatchers.IO).launch {
            baseApiDataThread.getData(viewCallback)
        }
    }
}