package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import android.widget.Button

class MapRecommendContract {
    interface MainView {

    }

    interface ViewpagerView {
        fun setOnPlayerRecommendButtonClick(isPlayerRecommend: Boolean)
        fun setOnAllRecommendButtonClick(isPlayerRecommend: Boolean)
    }

    interface RecyclerView {
       fun updateRecommendState(state: Boolean)
    }

    interface Presenter {
        fun fetchPlayerBrawlersArrayList(): ArrayList<String>
    }
}