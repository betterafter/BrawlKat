package com.keykat.keykat.brawlkat.service.maprecommendservice.repository

import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser

interface MapRecommendRepository {
    fun getPlayerInfoData(): kat_official_playerInfoParser.playerData?
    fun getMapRecommendData(viewCallback: () -> (Unit))
}