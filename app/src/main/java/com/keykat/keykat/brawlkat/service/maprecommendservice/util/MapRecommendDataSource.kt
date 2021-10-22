package com.keykat.keykat.brawlkat.service.maprecommendservice.util

import android.content.Context
import com.keykat.keykat.brawlkat.common.exceptions.InvalidBaseDataException
import com.keykat.keykat.brawlkat.common.exceptions.InvalidPlayerDataException
import com.keykat.keykat.brawlkat.service.util.BOUNDARY
import com.keykat.keykat.brawlkat.service.util.IP
import com.keykat.keykat.brawlkat.service.util.NotificationData
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.network.BaseApiDataThread
import com.keykat.keykat.brawlkat.util.parser.kat_brawlersParser
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.StandardCharsets

class MapRecommendDataSource(
    context: Context
) {
    private val baseApiDataThread = BaseApiDataThread(context)

    fun getPlayerInfoData() = KatData.eventsPlayerData.value

    fun getMapRecommendViewData(viewCallback: () -> (Unit)) {
        CoroutineScope(Dispatchers.IO).launch {
            baseApiDataThread.getData(viewCallback)
        }
    }
}