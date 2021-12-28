package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.service.maprecommendservice.repository.MapRecommendRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapRecommendViewPagerPresenter(
    private val mapRecommendRepository: MapRecommendRepository,
    private val viewPagerView: MapRecommendContract.ViewpagerView,
    private val recyclerView: MapRecommendContract.RecyclerView?,
    private val mainView: MapRecommendContract.MainView?
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
        mapRecommendRepository.getMapRecommendData { data ->
            CoroutineScope(Dispatchers.Main).launch {
                recyclerView?.refresh(data)
                viewPagerView.updateMapRecommendData(data)
                mainView?.setServiceButtonEnable()
            }
        }
    }
}