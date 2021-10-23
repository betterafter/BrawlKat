package com.keykat.keykat.brawlkat.service.maprecommendservice.repository

import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendDataSource
import com.keykat.keykat.brawlkat.service.model.data.NotificationData

class MapRecommendRepositoryImpl(
    private val mapRecommendDataSource: MapRecommendDataSource
) : MapRecommendRepository {

    override fun getPlayerInfoData() = mapRecommendDataSource.getPlayerInfoData()

    override fun getMapRecommendData(viewCallback: (NotificationData) -> (Unit)) {
        mapRecommendDataSource.getMapRecommendViewData(viewCallback)
    }
}