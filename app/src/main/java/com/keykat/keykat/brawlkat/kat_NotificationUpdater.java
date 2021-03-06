package com.keykat.keykat.brawlkat;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.keykat.keykat.brawlkat.kat_broadcast_receiver.kat_ButtonBroadcastReceiver;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.core.app.NotificationCompat;

public class kat_NotificationUpdater {

    private                             Context                                 context;

    private                             kat_official_playerInfoParser.playerData playerData;


    public kat_NotificationUpdater(Context context){
        this.context = context;
        playerData = kat_LoadBeforeMainActivity.eventsPlayerData;
    }

    public kat_NotificationUpdater(Context context, kat_official_playerInfoParser.playerData playerData) {
        this.context = context;
        this.playerData = playerData;
    }

    public RemoteViews smallContentView(){

        RemoteViews contentView
                = new RemoteViews(context.getPackageName(), R.layout.main_notification);

        try {
            if (playerData != null) {
                // 스타 포인트 연산
                kat_SeasonRewardsCalculator seasonRewardsCalculator
                        = new kat_SeasonRewardsCalculator(playerData);
                int seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator();

                // 뷰 연결
                contentView.setTextViewText(R.id.title, playerData.getName());
                contentView.setTextViewText(R.id.explain_text, " after season end");
                contentView.setTextViewText(R.id.text, seasonRewards + " points");
            }

            // 인텐트 등록
            Intent homeIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            homeIntent.setAction("main.HOME");

            Intent analyticsIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            analyticsIntent.setAction("main.ANALYTICS");


            PendingIntent HomePendingIntent = PendingIntent.getBroadcast(context, 0, homeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // 종료버튼과 펜딩 인텐트 연결
            PendingIntent AnalyticsPendingIntent = PendingIntent.getBroadcast(context, 0, analyticsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            contentView.setOnClickPendingIntent(R.id.main_home, HomePendingIntent);
            contentView.setOnClickPendingIntent(R.id.main_analytics, AnalyticsPendingIntent);

            return contentView;
        }
        catch (Exception e){
            e.printStackTrace();
            return contentView;
        }
    }

    public RemoteViews bigContentView(){

        RemoteViews BigContentView = ctView(R.layout.main_notification_big);

        try {
            if (playerData != null) {

                // 스타 포인트 연산
                kat_SeasonRewardsCalculator seasonRewardsCalculator
                        = new kat_SeasonRewardsCalculator(playerData);
                int seasonRewards = seasonRewardsCalculator.SeasonsRewardsCalculator();

                BigContentView.setTextViewText(R.id.title, playerData.getName());
                BigContentView.setTextViewText(R.id.explain_text, " after season end");
                BigContentView.setTextViewText(R.id.text, seasonRewards + " points");

                kat_BrawlerRecommendation recommendation = new kat_BrawlerRecommendation();
                recommendation.init();
                int rIndex = getRecommendationBrawlerIndex();

                int current_trophy = playerData.getBrawlerData().get(rIndex).getTrophies();
                int next_trophy = recommendation.leftTrophyToNextLevel(current_trophy);
                int current_starPoint = recommendation.expectedStarPoint(current_trophy);
                int next_starPoint = recommendation.expectedNextStarPoint(current_trophy);

                Spanned current_trophy_text = Html.fromHtml("<b><small>현재 트로피</small></b> <font color=#FFC107><b>"
                        + current_trophy + "</b></font>");
                Spanned next_trophy_text = Html.fromHtml("<b><small>다음 레벨까지 </small><font color=#FFC107>" +
                        next_trophy + "개</font></b> <small><b>남음</b></small>");
                Spanned current_starPoint_text = Html.fromHtml("<b><small>예상 스타 포인트 보상</small> " +
                        "<font color=#e11ec0>" + current_starPoint + "</font></b>");
                Spanned next_starPoint_text = Html.fromHtml("<b><small>다음 레벨 이후 </small><font color=#e11ec0>" +
                        next_starPoint + "</font><small>개 추가될 예정</small>" + "</b>");

                BigContentView.setTextViewText(R.id.big_current_trophy, current_trophy_text);
                BigContentView.setTextViewText(R.id.big_next_trophy, next_trophy_text);
                BigContentView.setTextViewText(R.id.big_current_starpoint, current_starPoint_text);
                BigContentView.setTextViewText(R.id.big_next_starpoint, next_starPoint_text);
            }

            // 인텐트 등록
            Intent homeIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            homeIntent.setAction("main.HOME");

            Intent analyticsIntent = new Intent(context, kat_ButtonBroadcastReceiver.class);
            analyticsIntent.setAction("main.ANALYTICS");


            PendingIntent HomePendingIntent = PendingIntent.getBroadcast(context, 0, homeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // 종료버튼과 펜딩 인텐트 연결
            PendingIntent AnalyticsPendingIntent = PendingIntent.getBroadcast(context, 0, analyticsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            BigContentView.setOnClickPendingIntent(R.id.main_home, HomePendingIntent);
            BigContentView.setOnClickPendingIntent(R.id.main_analytics, AnalyticsPendingIntent);
            return BigContentView;
        }
        catch (Exception e){
            e.printStackTrace();
            return BigContentView;
        }
    }

    public void update() {

        RemoteViews scv = smallContentView();
        RemoteViews bcv = bigContentView();

        try {

            if (kat_LoadBeforeMainActivity.kataSettingBase.getData("ForegroundService") == 1) {
                if (playerData != null) {
                    kat_Service_BrawlStarsNotifActivity.notification
                            = new NotificationCompat.Builder(context, "channel")
                            .setSmallIcon(R.drawable.kat_notification_icon)
                            .setColor(context.getResources().getColor(R.color.semiBlack))
                            .setColorized(true)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(scv)
                            .setCustomBigContentView(bcv)
                            .setShowWhen(false);
                } else {
                    kat_Service_BrawlStarsNotifActivity.notification
                            = new NotificationCompat.Builder(context, "channel")
                            .setSmallIcon(R.drawable.kat_notification_icon)
                            .setColor(context.getResources().getColor(R.color.semiBlack))
                            .setColorized(true)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(scv)
                            .setShowWhen(false);
                }

                if (playerData != null) {
                    if(UrlForBigContentViewRecommendBrawler().equals("")) return;
                    GlideImageWithNotification(context,
                            R.id.main_notification_big_img,
                            bcv,
                            kat_Service_BrawlStarsNotifActivity.notification.build(),
                            1,
                            UrlForBigContentViewRecommendBrawler());
                }
                updaterNotify();
            }
        }
        catch (Exception e){
            e.printStackTrace();

            kat_Service_BrawlStarsNotifActivity.notification
                    = new NotificationCompat.Builder(context, "channel")
                    .setSmallIcon(R.drawable.kat_notification_icon)
                    .setColor(context.getResources().getColor(R.color.semiBlack))
                    .setColorized(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(scv)
                    .setShowWhen(false);

            return;
        }
    }

    public void updaterNotify() {
        if(kat_Service_BrawlStarsNotifActivity.mNotificationManager != null)
            kat_Service_BrawlStarsNotifActivity.mNotificationManager.notify(1,
                    kat_Service_BrawlStarsNotifActivity.notification.build());
    }

    public String UrlForBigContentViewRecommendBrawler() {

        try {
            ArrayList<HashMap<String, Object>> BrawlersArrayList = kat_LoadBeforeMainActivity.BrawlersArrayList;

            kat_BrawlerRecommendation brawlerRecommendation = new kat_BrawlerRecommendation();
            brawlerRecommendation.init();
            String id = brawlerRecommendation.recommend();
            int index = -1;

            for (int i = 0; i < BrawlersArrayList.size(); i++) {
                String brawlerId = Integer.toString((int) BrawlersArrayList.get(i).get("id"));
                if (brawlerId.equals(id)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) return "";
            return BrawlersArrayList.get(index).get("imageUrl").toString();
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public int getRecommendationBrawlerIndex() {

        try {
            kat_BrawlerRecommendation brawlerRecommendation = new kat_BrawlerRecommendation();
            brawlerRecommendation.init();
            String id = brawlerRecommendation.recommend();
            int index = 0;

            for (int i = 0; i < playerData.getBrawlerData().size(); i++) {
                String brawlerId = playerData.getBrawlerData().get(i).getId();
                if (brawlerId.equals(id)) {
                    index = i;
                    break;
                }
            }
            return index;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    private RemoteViews ctView(int contentViewId) {

        final RemoteViews contentView = new RemoteViews(context.getPackageName(), contentViewId);
        return contentView;
    }


    public void GlideImageWithNotification(Context context, final int ImageViewId, final RemoteViews contentView,
                                            Notification notif, int notificationId, String url) throws Exception{

        NotificationTarget notificationTarget = new NotificationTarget(
                context,
                ImageViewId,
                contentView,
                notif,
                notificationId);

        int setWidth = Math.min(kat_LoadBeforeMainActivity.SCREEN_WIDTH, kat_LoadBeforeMainActivity.SCREEN_HEIGHT);

        Glide
                .with(context)
                .applyDefaultRequestOptions(kat_LoadBeforeMainActivity.options)
                .asBitmap()
                .load(url)
                .override(setWidth / 4, setWidth / 4)
                .into(notificationTarget);
    }
}
