package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import android.widget.Button

class MapRecommendContract {
    interface MainView {

    }

    interface ViewpagerView {
        fun setOnPlayerRecommendButtonClick()
        fun setOnAllRecommendButtonClick()
    }

    interface RecyclerView {
       fun updateRecommendState(state: Boolean)
    }

    interface Presenter {
        fun fetchPlayerBrawlersArrayList(): ArrayList<String>
    }

    interface ViewPagerPresenter {
        fun setOnPlayerRecommendClicked()
        fun setOnAllRecommendClicked()
    }
}