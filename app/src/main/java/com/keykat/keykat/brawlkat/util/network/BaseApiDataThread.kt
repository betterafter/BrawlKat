package com.keykat.keykat.brawlkat.util.network

import android.content.Context
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.getAppName
import com.keykat.keykat.brawlkat.util.getTopPackageName
import com.keykat.keykat.brawlkat.util.parser.kat_brawlersParser
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

// brawlify에서 가져오는 브롤러, 이벤트, 맵에 대한 데이터로 10분마다 업데이트함.
class BaseApiDataThread(val context: Context) : Thread() {

    private val oracleAddress = "193.122.98.86"
    private lateinit var data: InputStream
    private lateinit var input: InputStreamReader
    private lateinit var reader: BufferedReader
    private val boundaryCode = "this_is_a_kat_data_boundary!"

    private lateinit var eventsParser: kat_eventsParser
    private lateinit var brawlersParser: kat_brawlersParser
    private lateinit var mapsParser: kat_mapsParser

    private val time = 600000

    override fun run() {

        while (true) {
            try {
                getData(null)
                if (!checkApplicationIsRun()) break

                sleep(time.toLong())

            } catch (e: Exception) {
                // 이 스레드는 소켓 연결이 안될 때마다 불러올 필요는 없고 해당 정보가 없을 때만 다이얼로그를 띄우도록 함.
                // 어차피 해당 정보가 있긴 있으면 급한대로 업데이트 전 정보를 가져다 쓰면 되니까.
                if (KatData.EventArrayList.size <= 1
                    || KatData.BrawlersArrayList.size <= 1
                    || KatData.mapData.size <= 1
                ) KatData.serverProblemDialog()
                e.printStackTrace()
            } finally {
                try {
                    data.close()
                } catch (e: Exception) {
                    // TODO : process exceptions.
                }
            }
        }
    }

    fun getData(viewCallback: (() -> (Unit))?) {
        val socketAddress: SocketAddress = InetSocketAddress(oracleAddress, 9000)
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
        data = socket.getInputStream()
        input = InputStreamReader(data)
        reader = BufferedReader(input)
        result = reader.readLine()
        var startidx = 0
        var split: Int

        // API 데이터 파싱
        var splited: String
        val resData: ArrayList<String> = ArrayList()

        while (true) {
            split = result.indexOf(boundaryCode, startidx)
            if (split == -1) break
            splited = result.substring(startidx, split)
            resData.add(splited)
            startidx = split + boundaryCode.length
        }

        eventsParser = kat_eventsParser(resData[0])
        brawlersParser = kat_brawlersParser(resData[1])
        mapsParser = kat_mapsParser(resData[2])

        KatData.EventArrayList = eventsParser.DataParser()
        KatData.BrawlersArrayList = brawlersParser.DataParser()
        KatData.mapData = mapsParser.DataParser()

        reader.close()
        os.close()
        socket.close()

        viewCallback?.let { viewCallback() }
    }

    private fun checkApplicationIsRun(): Boolean {
        // usageEvent를 이용하여 현재 앱이 실행 중인지를 확인하고, 실행 중이 아니라면 데이터를 가져오는 것을
        // 못하게 막기
        val currName = getTopPackageName(context)
        if (currName == null || currName.isEmpty() || currName.lowercase(Locale.getDefault())
                .contains(context.getAppName())
        ) return false

        return true
    }
}

