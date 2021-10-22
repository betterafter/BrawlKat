package com.keykat.keykat.brawlkat.service.util

import com.keykat.keykat.brawlkat.common.exceptions.InvalidBaseDataException
import com.keykat.keykat.brawlkat.common.exceptions.InvalidPlayerDataException
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.parser.kat_brawlersParser
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.StandardCharsets

const val IP = "193.122.98.86"
const val BOUNDARY = "this_is_a_kat_data_boundary!"

class NotificationDataSource {
    fun getBaseData(): Result<NotificationData> {

        val socketAddress: SocketAddress = InetSocketAddress(IP, 9000)
        val socket = Socket()
        socket.connect(socketAddress)
        val bytes: ByteArray

        // 데이터 보내기

        // starlist api는 서버에 보낼 데이터가 없기 때문에 개행문자만을 보내 수신 종료한다.
        var result: String = "/" + "/" + "nofficial"
        result += "\n"
        val os = socket.getOutputStream()
        bytes = result.toByteArray(StandardCharsets.UTF_8)
        os.write(bytes)
        os.flush()
        val data = socket.getInputStream()
        val input = InputStreamReader(data)
        val reader = BufferedReader(input)
        result = reader.readLine()
        var startidx = 0
        var split: Int

        // API 데이터 파싱
        var splited: String
        val resData: ArrayList<String> = ArrayList()

        while (true) {
            split = result.indexOf(BOUNDARY, startidx)
            if (split == -1) break
            splited = result.substring(startidx, split)
            resData.add(splited)
            startidx = split + BOUNDARY.length
        }

        val eventsParser = kat_eventsParser(resData[0])
        val brawlersParser = kat_brawlersParser(resData[1])
        val mapsParser = kat_mapsParser(resData[2])

        KatData.EventArrayList = eventsParser.DataParser()
        KatData.BrawlersArrayList = brawlersParser.DataParser()
        KatData.mapData = mapsParser.DataParser()

        reader.close()
        os.close()
        socket.close()

        return if (resData.isEmpty()) {
            Result.failure(InvalidBaseDataException(resData.toString()))
        } else {
            kotlin.runCatching {
                NotificationData(
                    eventsParser.DataParser(),
                    brawlersParser.DataParser(),
                    mapsParser.DataParser(),
                    null
                )
            }
        }
    }

    fun getPlayerData(
        tag: String,
        type: String,
        apiType: String
    ): Result<NotificationData> {
        val socketAddress: SocketAddress = InetSocketAddress(IP, 9000)
        val socket = Socket()
        socket.connect(socketAddress)

        val bytes: ByteArray
        var result: String? = null

        // 데이터 보내기
        // playerTag를 먼저 보냄.
        if (apiType == "official") {
            result = "%23$tag"
        } else if (apiType == "nofficial") {
            result = tag
        }
        result = type + "/" + result + "/" + apiType
        val os = socket.getOutputStream()
        bytes = result.toByteArray(StandardCharsets.UTF_8)
        os.write(bytes)
        os.flush()

        // os 를 flush한 후 데이터 종료 완료를 알리기 위해 개행문자를 보내 데이터 수신을 완료한다.
        val end = "\n"
        os.write(end.toByteArray())
        os.flush()

        val data = socket.getInputStream()
        val input = InputStreamReader(data)
        val reader = BufferedReader(input)
        result = reader.readLine()

        var startidx = 0
        var split: Int

        // API 데이터 파싱
        var splited: String
        val resData = java.util.ArrayList<String>()

        while (true) {
            split = result.indexOf(BOUNDARY, startidx)
            if (split == -1) break
            splited = result.substring(startidx, split)
            resData.add(splited)
            startidx = split + BOUNDARY.length
        }

        input.close()
        data.close()
        reader.close()
        socket.close()

        return if (resData.isEmpty()) {
            Result.failure(InvalidPlayerDataException(resData.toString()))
        } else {
            kotlin.runCatching {
                NotificationData(null, null, null, resData)
            }
        }
    }
}