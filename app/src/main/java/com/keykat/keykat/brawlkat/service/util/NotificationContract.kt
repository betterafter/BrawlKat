package com.keykat.keykat.brawlkat.service.util

import kotlinx.coroutines.Deferred

interface NotificationContract {

    interface Presenter {
        fun loadData(tag: String, type: String, apiType: String)
    }

    interface View {
        fun updateService(notificationData: NotificationData)
    }
}