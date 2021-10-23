package com.keykat.keykat.brawlkat.service.util

import com.keykat.keykat.brawlkat.service.model.data.NotificationData

interface NotificationContract {

    interface Presenter {
        fun loadData(tag: String, type: String, apiType: String)
    }

    interface View {
        fun updateService(notificationData: NotificationData)
    }
}