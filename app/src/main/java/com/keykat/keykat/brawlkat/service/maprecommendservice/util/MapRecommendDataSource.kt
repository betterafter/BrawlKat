package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import com.keykat.keykat.brawlkat.util.KatData

class MapRecommendDataSource {
    fun getPlayerInfoData() = KatData.eventsPlayerData.value
}