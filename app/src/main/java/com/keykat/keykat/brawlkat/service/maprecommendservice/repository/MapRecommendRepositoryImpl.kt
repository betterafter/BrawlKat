package com.keykat.keykat.brawlkat.service.maprecommendservice.repository

import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendDataSource
import com.keykat.keykat.brawlkat.service.util.NotificationData
import com.keykat.keykat.brawlkat.util.KatData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapRecommendRepositoryImpl(
    private val mapRecommendDataSource: MapRecommendDataSource
) : MapRecommendRepository {

    override fun getPlayerInfoData() = mapRecommendDataSource.getPlayerInfoData()

    override fun getMapRecommendData(viewCallback: () -> (Unit)) {
        mapRecommendDataSource.getMapRecommendViewData(viewCallback)
    }
}