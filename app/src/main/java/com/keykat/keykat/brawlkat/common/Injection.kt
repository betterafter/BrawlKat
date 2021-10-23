package com.keykat.keykat.brawlkat.common

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository
import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepositoryImpl
import com.keykat.keykat.brawlkat.service.model.datasource.MapRecommendDataSource

object Injection {
    fun provideMapRecommendRepository(
        mapRecommendDataSource: MapRecommendDataSource
    ): MapRecommendRepository = MapRecommendRepositoryImpl(
        mapRecommendDataSource
    )
}