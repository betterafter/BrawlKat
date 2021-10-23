package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository

class MapRecommendPresenter(
    private val mapRecommendRepository: MapRecommendRepository,
    private val mainView: MapRecommendContract.MainView
) : MapRecommendContract.Presenter {

    override fun fetchPlayerBrawlersArrayList(): ArrayList<String> {

        val list = ArrayList<String>()
        mapRecommendRepository.getPlayerInfoData()?.brawlerData?.forEach { data ->
            list.add(data.name)
        }
        return list
    }
}