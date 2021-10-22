package com.keykat.keykat.brawlkat.service.util

interface NotificationContract {

    interface Presenter {
        fun loadData()
    }

    interface View {
        fun updateService()
    }
}