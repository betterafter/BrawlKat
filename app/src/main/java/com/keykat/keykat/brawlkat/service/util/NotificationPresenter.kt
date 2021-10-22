package com.keykat.keykat.brawlkat.service.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationPresenter(
    private val view: NotificationContract.View
) : NotificationContract.Presenter {
    private val notificationDataSource = NotificationDataSource()

    override fun loadData(
        tag: String,
        type: String,
        apiType: String
    ) {
        var notificationData: NotificationData

        CoroutineScope(Dispatchers.IO).launch {
            withContext(CoroutineScope(Dispatchers.Default).coroutineContext) {
                notificationData = NotificationData(null, null, null, null)
                notificationDataSource.getBaseData().onSuccess {
                    notificationData = it.copy(
                        playerArrayList = notificationData.playerArrayList
                    )
                }.onFailure {
                }
                notificationDataSource.getPlayerData(tag, type, apiType).onSuccess {
                    notificationData = it.copy(
                        eventArrayList = notificationData.eventArrayList,
                        brawlerArrayList = notificationData.brawlerArrayList,
                        mapData = notificationData.mapData
                    )
                }.onFailure {
                    println(it)
                }
                view.updateService(notificationData)
            }
        }
    }
}