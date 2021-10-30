package com.keykat.keykat.brawlkat.service.maprecommendservice.repository

import com.keykat.keykat.brawlkat.service.model.data.NotificationData
import com.keykat.keykat.brawlkat.common.util.parser.kat_official_playerInfoParser

interface MapRecommendRepository {
    fun getPlayerInfoData(): kat_official_playerInfoParser.playerData?
    fun getMapRecommendData(viewCallback: (NotificationData) -> (Unit))
}