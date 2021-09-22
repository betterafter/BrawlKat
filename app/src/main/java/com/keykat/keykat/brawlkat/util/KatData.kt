package com.keykat.keykat.brawlkat.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget
import com.google.android.gms.ads.AdRequest
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog
import com.keykat.keykat.brawlkat.load.activity.ServerConstructionDialog
import com.keykat.keykat.brawlkat.load.activity.ServerConstructionDialog.Companion.init
import com.keykat.keykat.brawlkat.load.activity.kat_LoadBeforeMainActivity
import com.keykat.keykat.brawlkat.util.database.*
import com.keykat.keykat.brawlkat.util.network.Client
import com.keykat.keykat.brawlkat.util.parser.*
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser.pair
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser.mapData
import com.keykat.keykat.brawlkat.util.parser.kat_official_BrawlerRankingParser.brawlerRankingData
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonParser.powerPlaySeasonsData
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.properties.Delegates

class KatData {

    companion object {

        // 데이터를 위한 여러 변수들.
        lateinit var clubLogData: kat_clubLogParser.clubLogData
        lateinit var clubData: kat_official_clubInfoParser.clubData
        lateinit var official_clubInfoParser: kat_official_clubInfoParser
        lateinit var clubLogParser: kat_clubLogParser
        lateinit var eventsPlayerData: kat_official_playerInfoParser.playerData
        lateinit var official_playerBattleLogParser: kat_official_playerBattleLogParser
        lateinit var official_playerInfoParser: kat_official_playerInfoParser
        lateinit var playerTag: String
        lateinit var playerBattleDataList: ArrayList<kat_official_playerBattleLogParser.playerBattleData>
        lateinit var playerBattleDataListStack: Stack<ArrayList<kat_official_playerBattleLogParser.playerBattleData>>
        lateinit var playerData: kat_official_playerInfoParser.playerData

        // background service를 위한 데이터
        var isBackgroundServiceStart = false


        // 화면 크기
        lateinit var metrics: DisplayMetrics
        lateinit var SCREEN_HEIGHT: Number
        lateinit var SCREEN_WIDTH: Number


        // Glide를 위한 옵션 설정
        lateinit var options: RequestOptions

        @SuppressLint("StaticFieldLeak")
        lateinit var client: Client


        // database ................................................................................
        lateinit var katabase: kat_database
        lateinit var kataFavoritesBase: kat_favoritesDatabase
        lateinit var kataMyAccountBase: kat_myAccountDatabase
        lateinit var kataCountryBase: kat_countryDatabase
        lateinit var kataSettingBase: kat_settingDatabase

        lateinit var countryCodeMap: HashMap<String, String>

        // .........................................................................................

        // .........................................................................................

        // brawlify에서 가져오는 기본 데이터들 (map, event, brawler)///////////////////////////////////////
        lateinit var mapData: HashMap<String, mapData>
        lateinit var EventArrayList: ArrayList<pair>
        lateinit var BrawlersArrayList: ArrayList<HashMap<String, Any>>
        ////////////////////////////////////////////////////////////////////////////////////////////

        var mapsParser: kat_mapsParser? = null
        var brawlersParser: kat_brawlersParser? = null


        // 데이터 업데이트를 위한 여러 리스트들
        // 랭킹 관련 리스트 --- kat_LoadBeforeMainActivity에서 할당
        lateinit var PlayerRankingArrayList: ArrayList<kat_official_PlayerRankingParser.playerData>
        lateinit var MyPlayerRankingArrayList: ArrayList<kat_official_PlayerRankingParser.playerData>

        lateinit var ClubRankingArrayList: ArrayList<kat_official_ClubRankingParser.clubData>
        lateinit var MyClubRankingArrayList: ArrayList<kat_official_ClubRankingParser.clubData>

        lateinit var BrawlerRankingArrayList: HashMap<String, ArrayList<brawlerRankingData>>
        lateinit var MyBrawlerRankingArrayList: HashMap<String, HashMap<String, ArrayList<brawlerRankingData>>>

        lateinit var PowerPlaySeasonArrayList: ArrayList<powerPlaySeasonsData>
        lateinit var MyPowerPlaySeasonArrayList: ArrayList<powerPlaySeasonsData>

        lateinit var PowerPlaySeasonRankingArrayList: HashMap<String, ArrayList<powerPlaySeasonRankingData>>
        lateinit var MyPowerPlaySeasonRankingArrayList: HashMap<String, HashMap<String, ArrayList<powerPlaySeasonRankingData>>>


        @SuppressLint("StaticFieldLeak")
        lateinit var dialog: kat_LoadingDialog
        // .........................................................................................

        // .........................................................................................
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 3

        const val ApiRootUrl = "https://api.brawlify.com"
        const val CdnRootUrl = "https://cdn.brawlify.com"
        const val WebRootUrl = "https://brawlify.com"

        lateinit var adRequest: AdRequest


        // 클라이언트에서 다이얼로그를 띄우기 위해 현재 액티비티를 가져와야 함. 그 밖에도 필요한 경우에 사용할 것.
        @SuppressLint("StaticFieldLeak")
        lateinit var currentActivity: Activity


        // 소켓 연결이 정상적으로 되지 않을 경우 서버 문제 다이얼로그가 발생함.
        // 단, 임시로 저장한 데이터들이 존재한다면 일단 그걸로 써도 되니까 실시간 통신 상황에선 굳이 다이얼로그를 안 띄워도 됨.
        @JvmStatic
        fun ServerProblemDialog() {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.postDelayed({ init(currentActivity) }, 0)
        }


        @JvmStatic
        fun GlideImage(context: Context, url: String, width: Int, height: Int, view: ImageView) {
            Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .override(width, height)
                .into(view)
        }

        @JvmStatic
        fun GlideImageWithRoundCorner(
            context: Context,
            url: String?,
            width: Int,
            height: Int,
            view: ImageView
        ) {
            Glide.with(context)
                .applyDefaultRequestOptions(options)
                .load(url)
                .apply(RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view)
        }


        @JvmStatic
        @Throws(Exception::class)
        fun GlideImageWithNotification(
            context: Context?, ImageViewId: Int, contentView: RemoteViews?,
            notif: Notification?, notificationId: Int, url: String?
        ) {
            val notificationTarget = NotificationTarget(
                context,
                ImageViewId,
                contentView,
                notif,
                notificationId
            )
            val setWidth = Math.min(SCREEN_WIDTH.toInt(), SCREEN_HEIGHT.toInt())
            Glide
                .with(context!!)
                .applyDefaultRequestOptions(options)
                .asBitmap()
                .load(url)
                .override(setWidth / 4, setWidth / 4)
                .into(notificationTarget)
        }
    }

}