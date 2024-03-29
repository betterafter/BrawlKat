package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import android.widget.Button
import com.keykat.keykat.brawlkat.service.model.data.NotificationData

class MapRecommendContract {
    interface MainView {
        fun setServiceButtonEnable()
    }

    interface ViewpagerView {
        fun setOnPlayerRecommendButtonClick()
        fun setOnAllRecommendButtonClick()
        fun updateMapRecommendData(notificationData: NotificationData)
    }

    interface RecyclerView {
       fun updateRecommendState(state: Boolean)
       fun refresh(notificationData: NotificationData)
    }

    interface Presenter {
        fun fetchPlayerBrawlersArrayList(): ArrayList<String>
    }

    interface ViewPagerPresenter {
        fun setOnPlayerRecommendClicked()
        fun setOnAllRecommendClicked()
    }
}