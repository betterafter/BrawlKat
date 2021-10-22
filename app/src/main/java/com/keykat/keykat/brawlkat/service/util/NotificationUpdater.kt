package com.keykat.keykat.brawlkat.service.util

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.Html
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.keykat.keykat.brawlkat.R
import com.keykat.keykat.brawlkat.common.glideImageWithNotification
import com.keykat.keykat.brawlkat.util.KatData
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser
import java.lang.Exception

class NotificationUpdater(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    private var playerData: kat_official_playerInfoParser.playerData?
    private lateinit var notification: NotificationCompat.Builder

    private val scv = smallContentView()
    private val bcv = bigContentView()

    init {
        setSmallNotification()
    }

    private fun smallContentView(): RemoteViews {
        val contentView = RemoteViews(context.packageName, R.layout.main_notification)
        return try {

            playerData?.let {
                // 스타 포인트 연산
                val seasonRewardsCalculator = kat_SeasonRewardsCalculator(it)
                val seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator()

                // 뷰 연결
                contentView.setTextViewText(R.id.title, it.name)
                contentView.setTextViewText(R.id.explain_text, " after season end")
                contentView.setTextViewText(R.id.text, "$seasonRewards points")
            }

            // 인텐트 등록
            val homeIntent = Intent(
                context,
                kat_ButtonBroadcastReceiver::class.java
            )
            homeIntent.action = "main.HOME"
            val analyticsIntent = Intent(
                context,
                kat_ButtonBroadcastReceiver::class.java
            )
            analyticsIntent.action = "main.ANALYTICS"
            @SuppressLint("UnspecifiedImmutableFlag") val homePendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    homeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            // 종료버튼과 펜딩 인텐트 연결
            @SuppressLint("UnspecifiedImmutableFlag") val analyticsPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    analyticsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            contentView.setOnClickPendingIntent(R.id.main_home, homePendingIntent)
            contentView.setOnClickPendingIntent(R.id.main_analytics, analyticsPendingIntent)
            contentView
        } catch (e: Exception) {
            e.printStackTrace()
            contentView
        }
    }

    private fun bigContentView(): RemoteViews {
        val bigContentView = ctView(R.layout.main_notification_big)
        return try {

            playerData?.let {
                // 스타 포인트 연산
                val seasonRewardsCalculator = kat_SeasonRewardsCalculator(it)
                val seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator()
                bigContentView.setTextViewText(R.id.title, it.name)
                bigContentView.setTextViewText(R.id.explain_text, " after season end")
                bigContentView.setTextViewText(R.id.text, "$seasonRewards points")
                val recommendation = kat_BrawlerRecommendation()
                recommendation.init()

                val rIndex = recommendationBrawlerIndex

                val currentTrophy = it.brawlerData[rIndex].trophies
                val nextTrophy = recommendation.leftTrophyToNextLevel(currentTrophy)
                val currentStarPoint = recommendation.expectedStarPoint(currentTrophy)
                val nextStarPoint = recommendation.expectedNextStarPoint(currentTrophy)
                val currentTrophyText = Html.fromHtml(
                    "<b><small>현재 트로피</small></b> <font color=#FFC107><b>"
                            + currentTrophy + "</b></font>"
                )
                val nextTrophyText = Html.fromHtml(
                    "<b><small>다음 레벨까지 </small><font color=#FFC107>" +
                            nextTrophy + "개</font></b> <small><b>남음</b></small>"
                )
                val currentStarPointText = Html.fromHtml(
                    "<b><small>예상 스타 포인트 보상</small> " +
                            "<font color=#e11ec0>" + currentStarPoint + "</font></b>"
                )
                val nextStarPointText = Html.fromHtml(
                    "<b><small>다음 레벨 이후 </small><font color=#e11ec0>" +
                            nextStarPoint + "</font><small>개 추가될 예정</small>" + "</b>"
                )
                bigContentView.setTextViewText(R.id.big_current_trophy, currentTrophyText)
                bigContentView.setTextViewText(R.id.big_next_trophy, nextTrophyText)
                bigContentView.setTextViewText(R.id.big_current_starpoint, currentStarPointText)
                bigContentView.setTextViewText(R.id.big_next_starpoint, nextStarPointText)
            }


            // 인텐트 등록
            val homeIntent = Intent(
                context,
                kat_ButtonBroadcastReceiver::class.java
            )
            homeIntent.action = "main.HOME"
            val analyticsIntent = Intent(
                context,
                kat_ButtonBroadcastReceiver::class.java
            )
            analyticsIntent.action = "main.ANALYTICS"
            @SuppressLint("UnspecifiedImmutableFlag") val homePendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    homeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            // 종료버튼과 펜딩 인텐트 연결
            @SuppressLint("UnspecifiedImmutableFlag") val analyticsPendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    0,
                    analyticsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            bigContentView.setOnClickPendingIntent(R.id.main_home, homePendingIntent)
            bigContentView.setOnClickPendingIntent(R.id.main_analytics, analyticsPendingIntent)
            bigContentView
        } catch (e: Exception) {
            e.printStackTrace()
            bigContentView
        }
    }

    fun update() {
        playerData = KatData.eventsPlayerData.value
        println(playerData?.name)
        try {
            if (playerData != null) {
                notification = NotificationCompat.Builder(
                    context, "channel"
                )
                    .setSmallIcon(R.drawable.kat_notification_icon)
                    .setColor(context.resources.getColor(R.color.semiBlack))
                    .setColorized(true)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(scv)
                    .setCustomBigContentView(bcv)
                    .setShowWhen(false)
            } else {
                setSmallNotification()
            }
            if (playerData != null) {
                val url = urlForBigContentViewRecommendBrawler()
                if (url == "") return

                glideImageWithNotification(
                    context,
                    R.id.main_notification_big_img,
                    bcv,
                    notification.build(),
                    1,
                    url
                )
            }
            updaterNotify()

        } catch (e: Exception) {
            e.printStackTrace()
            setSmallNotification()
        }
    }

    private fun setSmallNotification() {
        notification = NotificationCompat.Builder(
            context, "channel"
        )
            .setSmallIcon(R.drawable.kat_notification_icon)
            .setColor(context.resources.getColor(R.color.semiBlack))
            .setColorized(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(scv)
            .setShowWhen(false)
    }




    fun getUpdatedNotification() = notification

    private fun updaterNotify() {
        notificationManager.notify(1, notification.build())
    }

    private fun urlForBigContentViewRecommendBrawler(): String {

        val brawlersArrayList = KatData.BrawlersArrayList
        var index = 0

        try {
            val brawlerRecommendation = kat_BrawlerRecommendation()
            brawlerRecommendation.init()
            val id = brawlerRecommendation.recommend()

            for (i in brawlersArrayList.indices) {
                if ((brawlersArrayList[i]["id"] as Int).toString() == id) {
                    index = i
                    break
                }
            }
            return brawlersArrayList[index]["imageUrl"].toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private val recommendationBrawlerIndex: Int
        get() = try {

            val brawlerRecommendation = kat_BrawlerRecommendation()
            brawlerRecommendation.init()
            val id = brawlerRecommendation.recommend()
            var index = 0

            playerData?.let {
                for (i in it.brawlerData.indices) {
                    if (it.brawlerData[i].id == id) {
                        index = i
                        break
                    }
                }
            }
            index
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }

    private fun ctView(contentViewId: Int): RemoteViews {
        return RemoteViews(context.packageName, contentViewId)
    }

    init {
        playerData = KatData.eventsPlayerData.value
    }
}