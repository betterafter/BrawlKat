package com.keykat.keykat.brawlkat.service.maprecommendservice.repository

import com.keykat.keykat.brawlkat.service.maprecommendservice.util.MapRecommendDataSource
import com.keykat.keykat.brawlkat.util.KatData

class MapRecommendRepositoryImpl(
    private val mapRecommendDataSource: MapRecommendDataSource
) : MapRecommendRepository {

    override fun getPlayerInfoData() = mapRecommendDataSource.getPlayerInfoData()

    override fun getMapRecommendData(viewCallback: () -> (Unit)) {
        mapRecommendDataSource.getMapRecommendViewData(viewCallback)
    }
}