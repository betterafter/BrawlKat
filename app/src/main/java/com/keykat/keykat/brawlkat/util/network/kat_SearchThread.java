package com.keykat.keykat.brawlkat.util.network;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.keykat.keykat.brawlkat.common.model.datasource.SharedPreferenceManager;
import com.keykat.keykat.brawlkat.common.ui.ClubNotFoundDialog;
import com.keykat.keykat.brawlkat.common.ui.PlayerNotFoundDialog;
import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
import com.keykat.keykat.brawlkat.home.activity.kat_SearchAccountForSaveActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.parser.kat_clubLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_clubInfoParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerBattleLogParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_SearchThread extends AppCompatActivity {

    Activity fromActivity;
    Class toClass;
    kat_LoadingDialog kat_loadingDialog;


    public static boolean SearchDataOnOverdraw = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public kat_SearchThread(Activity fromActivity, Class toclass) {
        this.fromActivity = fromActivity;
        this.toClass = toclass;
    }

    public kat_SearchThread(Activity fromActivity, Class toclass, kat_LoadingDialog kat_loadingDialog) {
        this.fromActivity = fromActivity;
        this.toClass = toclass;
        this.kat_loadingDialog = kat_loadingDialog;
    }


    public class SearchThread extends Thread {

        String tag;
        String type;
        ArrayList<String> sendData;
        Context context;

        public SearchThread(String tag, String type, Context context) {
            this.tag = tag;
            this.type = type;
            this.context = context;
        }

        public void run() {

            SearchDataOnOverdraw = true;
            KatData.playerTag = tag;
            sendData = new ArrayList<>();

            if (type.equals("players")) {
                KatData.client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if (KatData.client.getAllTypeData().size() <= 0) {
                    try {
                        Client.getAllTypeApiThread apiThread = KatData.client.apiThread();
                        apiThread.join();
                        sendData.add(KatData.client.getAllTypeData().get(0));
                        sendData.add(KatData.client.getAllTypeData().get(1));
                        playerSearch(sendData);

                    } catch (Exception e) {
                        e.printStackTrace();
                        if (kat_loadingDialog != null) kat_loadingDialog.dismiss();
                    }
                }
            } else if (type.equals("clubs")) {

                KatData.client.AllTypeInit(tag, type, kat_Player_MainActivity.official, context);
                if (KatData.client.getAllTypeData().size() <= 0) {
                    try {
                        Client.getAllTypeApiThread apiThread = KatData.client.apiThread();
                        apiThread.join();
                        sendData.add(KatData.client.getAllTypeData().get(0));
                        sendData.add(KatData.client.getAllTypeData().get(1));
                        clubSearch(sendData);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (kat_loadingDialog != null) kat_loadingDialog.dismiss();
                    }

                }
            }
        }
    }

    public void playerSearch(ArrayList<String> sendData) {

        // 제대로 가져오지 못했을 경우 알림
        if (sendData.get(0).equals("{none}")) {
            if (kat_loadingDialog != null) kat_loadingDialog.dismiss();
            setPlayerErrorDialog();
        }


        // 제대로 가져왔을 경우
        else {
            KatData.official_playerInfoParser
                    = new kat_official_playerInfoParser(sendData.get(0));
            KatData.official_playerBattleLogParser
                    = new kat_official_playerBattleLogParser(sendData.get(1));


            try {
                // 자신의 플레이어 데이터를 따로 저장. (맵 승률 서비스를 위해 따로 저장하는 리스트)
                SharedPreferenceManager sharedPreferenceManager
                        = new SharedPreferenceManager(fromActivity.getApplicationContext());
                String account = sharedPreferenceManager.getAccount();

                if (account != null) {
                    if (account.equals(KatData.official_playerInfoParser.DataParser().getTag())) {
                        // 알람창 강제 종료되는 것 방지
                        if (sendData.get(0).length() > 50)
                            KatData.eventsPlayerData.postValue(
                                    KatData.official_playerInfoParser.DataParser()
                            );
                    }
                }

                KatData.playerData = KatData.official_playerInfoParser.DataParser();
                if (!KatData.client.getAllTypeData().get(1).equals("{none}")
                        && KatData.client.getAllTypeData().get(1).length() > 30
                        && KatData.client.getAllTypeData().get(1) != null) {

                    KatData.playerBattleDataList
                            = KatData.official_playerBattleLogParser.DataParser();

                    KatData.playerBattleDataListStack
                            .add(KatData.official_playerBattleLogParser.DataParser());
                }

                String type = "players";
                String tag = KatData.playerData.getTag();
                String name = KatData.playerData.getName();
                String isAccount = "NO";

                KatData.katabase.delete(type);
                KatData.katabase.insert(type, tag, name, isAccount);

                if (fromActivity == null) {
                    SearchDataOnOverdraw = false;
                    return;
                }

                if (fromActivity instanceof kat_SearchAccountForSaveActivity) {
                    sharedPreferenceManager.putAccount(tag);

                    if (sendData.get(0).length() > 50)
                        KatData.eventsPlayerData.postValue(
                                KatData.official_playerInfoParser.DataParser()
                        );
                }

                ActivityManager manager
                        = (ActivityManager) fromActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName = info.get(0).topActivity;
                String topActivityName = componentName.getShortClassName().substring(1);

                String com = topActivityName;
                String to = fromActivity.getClass().getName();

                if (fromActivity != null) {
                    if (!com.equals(to) && !com.contains(to) && !to.contains(com))
                        return;
                }

                Intent intent = new Intent(fromActivity, toClass);
                System.out.println(KatData.playerData);
                intent.putExtra("playerData", KatData.playerData);
                fromActivity.startActivity(intent);

                if (KatData.dialog != null) KatData.dialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clubSearch(ArrayList<String> sendData) {

        // 제대로 가져오지 못했을 경우 알림
        if (sendData.get(0).equals("{none}")) {
            if (kat_loadingDialog != null) kat_loadingDialog.dismiss();
            setClubErrorDialog();
        }

        // 제대로 가져왔을 경우
        else {
            KatData.official_clubInfoParser = new kat_official_clubInfoParser(sendData.get(0));
            KatData.clubLogParser = new kat_clubLogParser(sendData.get(1));

            try {
                KatData.clubData = KatData.official_clubInfoParser.DataParser();
                KatData.clubLogData = KatData.clubLogParser.DataParser();

                String type = "clubs";
                String tag = KatData.clubData.getTag();
                String name = KatData.clubData.getName();
                String isAccount = "no";

                KatData.katabase.delete(type);
                KatData.katabase.insert(type, tag, name, isAccount);

                ActivityManager manager = (ActivityManager) fromActivity.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
                ComponentName componentName = info.get(0).topActivity;
                String topActivityName = componentName.getShortClassName().substring(1);

                String com = topActivityName;
                String to = fromActivity.getClass().getName();

                if (fromActivity != null) {
                    if (!com.equals(to) && !com.contains(to) && !to.contains(com))
                        return;
                }

                Intent intent = new Intent(fromActivity, toClass);
                intent.putExtra("clubData", KatData.clubData);
                intent.putExtra("clubLogData", KatData.clubLogData);

                fromActivity.startActivity(intent);

                if (KatData.dialog != null) KatData.dialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setPlayerErrorDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> PlayerNotFoundDialog.Companion.init(fromActivity), 0);
    }

    private void setClubErrorDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> ClubNotFoundDialog.Companion.init(fromActivity), 0);
    }

    public void SearchStart(String tag, String type, Context context) {
        SearchThread searchThread = new SearchThread(tag, type, context);
        searchThread.start();
    }
}
