package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository

class MapRecommendViewPagerPresenter(
    private val mapRecommendRepository: MapRecommendRepository,
    private val viewPagerView: MapRecommendContract.ViewpagerView,
    private val recyclerView: MapRecommendContract.RecyclerView?
): MapRecommendContract.ViewPagerPresenter {

    override fun setOnPlayerRecommendClicked() {
        viewPagerView.setOnPlayerRecommendButtonClick()
        recyclerView?.updateRecommendState(true)
    }

    override fun setOnAllRecommendClicked() {
        viewPagerView.setOnAllRecommendButtonClick()
        recyclerView?.updateRecommendState(false)
    }
}