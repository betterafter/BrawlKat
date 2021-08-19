package com.keykat.keykat.brawlkat.util.network

import android.app.Activity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_RankingFragment
import com.keykat.keykat.brawlkat.home.ranking.util.setRankingView
import com.keykat.keykat.brawlkat.util.kat_Data
import com.keykat.keykat.brawlkat.util.parser.kat_official_BrawlerRankingParser
import com.keykat.keykat.brawlkat.util.parser.kat_official_ClubRankingParser
import com.keykat.keykat.brawlkat.util.parser.kat_official_PlayerRankingParser
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonRankingParser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AsyncCoroutine {

    companion object{

        val loading_texts = arrayOf(
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

        fun change_loadingText(textView: TextView){

            GlobalScope.launch {
            var index = 0
                while(true) {
                    textView.text = loading_texts[index]
                    index++
                    if(index >= loading_texts.size) index = 0
                    if(finished) break
                    delay(500)
                }
            }
        }


        fun accessRankingFragment(fragmentTransaction: FragmentTransaction, id: Int, fragment: kat_RankingFragment){

            GlobalScope.launch {
                while (true) {
                    if (kat_Data.PlayerRankingArrayList.size > 0 &&
                        kat_Data.ClubRankingArrayList.size > 0 &&
                        kat_Data.PowerPlaySeasonArrayList.size > 0
                    ) {
                        fragmentTransaction.replace(id, fragment)
                        fragmentTransaction.commit()
                        break
                    }
                    delay(0)
                }
            }
        }



        fun player_DatabaseChanged(activity: Activity,
                                   linearLayout: LinearLayout,
                                   list: ArrayList<kat_official_PlayerRankingParser.playerData>){

            GlobalScope.launch {
                while (true) {
                    if (kat_Data.MyPlayerRankingArrayList.size > 0 && kat_Data.PlayerRankingArrayList.size > 0) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.playerSetView(activity, linearLayout, list)
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }



        fun club_DatabaseChanged(activity: Activity,
                                 linearLayout: LinearLayout,
                                 list: ArrayList<kat_official_ClubRankingParser.clubData>){
            GlobalScope.launch {
                while (true) {
                    if (kat_Data.MyClubRankingArrayList.size > 0 && kat_Data.ClubRankingArrayList.size > 0) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.clubSetView(activity, linearLayout, list)
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun brawler_global_DatabaseChanged(activity: Activity,
                                 linearLayout: LinearLayout,
                                           initId: String){

            GlobalScope.launch {
                while (true) {
                    if (kat_Data.BrawlerRankingArrayList.containsKey(initId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.brawlerSetView(
                                activity, linearLayout,
                                kat_Data.BrawlerRankingArrayList[initId]
                            )
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun brawler_DatabaseChanged(activity: Activity,
                                           linearLayout: LinearLayout,
                                    initId: String){

            GlobalScope.launch {
                val countryCode = kat_Data.kataCountryBase.countryCode

                while (true) {
                    if (kat_Data.MyBrawlerRankingArrayList.containsKey(countryCode) &&
                        kat_Data.MyBrawlerRankingArrayList[countryCode]!!.containsKey(initId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.brawlerSetView(
                                activity, linearLayout,
                                kat_Data.MyBrawlerRankingArrayList[countryCode]!![initId]
                            )
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun powerplay_global_DatabaseChanged(activity: Activity,
                                           linearLayout: LinearLayout,
                                             seasonId: String){
            GlobalScope.launch {
                while (true) {
                    if (kat_Data.PowerPlaySeasonRankingArrayList.containsKey(seasonId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.powerPlaySetView(
                                activity, linearLayout,
                                kat_Data.PowerPlaySeasonRankingArrayList[seasonId]
                            )
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }


        fun powerplay_DatabaseChanged(activity: Activity,
                                    linearLayout: LinearLayout,
                                    seasonId: String) {

            GlobalScope.launch {
                val countryCode = kat_Data.kataCountryBase.countryCode

                while (true) {
                    if (kat_Data.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) &&
                        kat_Data.MyPowerPlaySeasonRankingArrayList[countryCode]!!.containsKey(seasonId)) {
                        activity.runOnUiThread {
                            val rankingView = setRankingView()
                            rankingView.powerPlaySetView(
                                activity, linearLayout,
                                kat_Data.MyPowerPlaySeasonRankingArrayList[countryCode]!![seasonId]
                            )
                        }
                        kat_Data.dialog.cancel()
                        break
                    }
                }
            }
        }
    }
}