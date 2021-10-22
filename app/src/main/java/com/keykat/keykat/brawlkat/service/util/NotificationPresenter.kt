package com.keykat.keykat.brawlkat.service.util

class NotificationPresenter(
    private val view: NotificationContract.View
): NotificationContract.Presenter {
    private val notificationDataSource = NotificationDataSource()

    override fun loadData() {
        notificationDataSource.getBaseData {
            view.updateService()
        }
    }
}