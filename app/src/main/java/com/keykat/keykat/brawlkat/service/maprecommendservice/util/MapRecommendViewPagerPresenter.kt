package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser.pair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.HashMap

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

    fun getMapRecommendData() {
        mapRecommendRepository.getMapRecommendData {
            CoroutineScope(Dispatchers.Main).launch {
                recyclerView?.refresh()
            }
        }
    }
}