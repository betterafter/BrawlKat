package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository
import com.keykat.keykat.brawlkat.service.util.NotificationData
import com.keykat.keykat.brawlkat.util.KatData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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