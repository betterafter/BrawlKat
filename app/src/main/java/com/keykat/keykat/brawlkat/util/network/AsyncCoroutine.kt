package com.keykat.keykat.brawlkat.util.network

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_RankingFragment
import com.keykat.keykat.brawlkat.home.ranking.util.setRankingView
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.parser.kat_official_ClubRankingParser
import com.keykat.keykat.brawlkat.util.parser.kat_official_PlayerRankingParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AsyncCoroutine {

    companion object {

        private val loadingTexts = arrayOf(
            "데이터를 불러오는 중입니다.", "데이터를 불러오는 중입니다..", "데이터를 불러오는 중입니다...",
            "데이터를 불러오는 중입니다.", "데이터를 불러오는 중입니다..", "데이터를 불러오는 중입니다...",
            "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다.", "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다..",
            "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다...",
            "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다.", "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다..",
            "맨 처음 실행할 때는 조금 오래걸릴 수 있습니다...",
            "조금만 기다려주세요.", "조금만 기다려주세요..", "조금만 기다려주세요...",
            "조금만 기다려주세요.", "조금만 기다려주세요..", "조금만 기다려주세요..."
        )

        var finished: Boolean = false

        fun changeLoadingText(textView: TextView) {

            GlobalScope.launch {
                var index = 0
                while (true) {
                    textView.text = loadingTexts[index]
                    index++
                    if (index >= loadingTexts.size) index = 0
                    if (finished) break
                    delay(500)
                }
            }
        }


        fun accessRankingFragment(
            fragmentTransaction: FragmentTransaction,
            id: Int,
            fragment: kat_RankingFragment
        ) {

            GlobalScope.launch {
                while (true) {
                    if (KatData.PlayerRankingArrayList.size > 0 &&
                        KatData.ClubRankingArrayList.size > 0 &&
                        KatData.PowerPlaySeasonArrayList.size > 0
                    ) {
                        fragmentTransaction.replace(id, fragment)
                        fragmentTransaction.commit()
                        break
                    }
                    delay(0)
                }
            }
        }


        fun playerDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            list: ArrayList<kat_official_PlayerRankingParser.playerData>
        ) {

            GlobalScope.launch {
                while (true) {
                    if (KatData.MyPlayerRankingArrayList.size > 0
                        && KatData.PlayerRankingArrayList.size > 0
                    ) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView(activity)
                            rankingView.playerSetView(activity, linearLayout, list)
                        }
                        KatData.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun clubDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            list: ArrayList<kat_official_ClubRankingParser.clubData>
        ) {
            GlobalScope.launch {
                while (true) {
                    if (
                        KatData.MyClubRankingArrayList.size > 0
                        && KatData.ClubRankingArrayList.size > 0
                    ) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView(activity)
                            rankingView.clubSetView(activity, linearLayout, list)
                        }
                        KatData.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun brawlerGlobalDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            initId: String
        ) {

            GlobalScope.launch {
                while (true) {
                    if (KatData.BrawlerRankingArrayList.containsKey(initId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView(activity)
                            rankingView.brawlerSetView(
                                activity, linearLayout,
                                KatData.BrawlerRankingArrayList[initId]
                            )
                        }
                        KatData.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun brawlerDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            initId: String
        ) {

            GlobalScope.launch {
                val countryCode = KatData.kataCountryBase.countryCode

                KatData.MyBrawlerRankingArrayList[countryCode]?.let { it ->
                    while(true) {
                        if (KatData.MyBrawlerRankingArrayList.containsKey(countryCode) &&
                            it.containsKey(initId)
                        ) {
                            activity.runOnUiThread {
                                val rankingView = setRankingView(activity)
                                rankingView.brawlerSetView(
                                    activity, linearLayout,
                                    KatData.MyBrawlerRankingArrayList[countryCode]!![initId]
                                )
                            }
                            KatData.dialog.cancel()
                            break
                        }
                    }
                }
            }
        }


        fun powerPlayGlobalDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            seasonId: String
        ) {
            GlobalScope.launch {
                while (true) {
                    if (KatData.PowerPlaySeasonRankingArrayList.containsKey(seasonId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView(activity)
                            rankingView.powerPlaySetView(
                                activity, linearLayout,
                                KatData.PowerPlaySeasonRankingArrayList[seasonId]
                            )
                        }
                        KatData.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun powerPlayDatabaseChanged(
            activity: Activity,
            linearLayout: LinearLayout,
            seasonId: String
        ) {

            GlobalScope.launch {
                val countryCode = KatData.kataCountryBase.countryCode

                KatData.MyPowerPlaySeasonRankingArrayList[countryCode]?.let { it ->
                    while (true) {
                        if (KatData.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) &&
                            it.containsKey(
                                seasonId
                            )
                        ) {
                            activity.runOnUiThread {
                                val rankingView = setRankingView(activity)
                                rankingView.powerPlaySetView(
                                    activity, linearLayout,
                                    it[seasonId]
                                )
                            }
                            KatData.dialog.cancel()
                            break
                        }
                    }
                }
            }
        }
    }
}