package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository
import com.keykat.keykat.brawlkat.util.KatData

class MapRecommendPresenter(
    private val mapRecommendRepository: MapRecommendRepository,
    private val mainView: MapRecommendContract.MainView,
    private val viewPagerView: MapRecommendContract.ViewpagerView,
    private val recyclerView: MapRecommendContract.RecyclerView?
) : MapRecommendContract.Presenter {

    override fun fetchPlayerBrawlersArrayList(): ArrayList<String> {

        val list = ArrayList<String>()
        mapRecommendRepository.getPlayerInfoData()?.brawlerData?.forEach { data ->
            list.add(data.name)
        }
        return list
    }

    fun setOnPlayerRecommendClicked(isPlayerRecommend: Boolean) {
        viewPagerView.setOnPlayerRecommendButtonClick(isPlayerRecommend)
        recyclerView?.updateRecommendState(true)
    }

    fun setOnAllRecommendClicked(isPlayerRecommend: Boolean) {
        viewPagerView.setOnAllRecommendButtonClick(isPlayerRecommend)
        recyclerView?.updateRecommendState(true)
    }
}