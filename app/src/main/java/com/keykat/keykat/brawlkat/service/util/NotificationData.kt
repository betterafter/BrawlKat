package com.keykat.keykat.brawlkat.service.util

import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser
import java.util.ArrayList
import java.util.HashMap

data class NotificationData(
    val eventArrayList: ArrayList<kat_eventsParser.pair>?,
    val brawlerArrayList: ArrayList<HashMap<String, Any>>?,
    val mapData: HashMap<String, kat_mapsParser.mapData>?,
    val playerArrayList: ArrayList<String>?
)