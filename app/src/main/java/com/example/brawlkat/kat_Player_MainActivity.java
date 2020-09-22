package com.example.brawlkat;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.brawlkat.dataparser.kat_brawlersParser;
import com.example.brawlkat.dataparser.kat_clubLogParser;
import com.example.brawlkat.dataparser.kat_mapsParser;
import com.example.brawlkat.dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerParser;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class kat_Player_MainActivity extends AppCompatActivity {

    public              static String                                                           official = "official";
    public              static String                                                           nofficial = "nofficial";

    private             LinearLayout                                                            player_layout_userInfo;
    private             LinearLayout                                                            player_layout_recent_userSearch;
    private             LinearLayout                                                            player_layout_recent_clubSearch;
    private             TextInputEditText                                                       player_user_search;
    private             TextInputEditText                                                       player_club_search;

    public              static Client                                                           client = new Client();




    public              static String                                                           playerTag;
    private             String                                                                  clubTag;

    public              kat_official_playerParser                                               official_playerParser;
    public              kat_official_playerInfoParser                                           official_playerInfoParser;
    public              kat_official_playerBattleLogParser                                      official_playerBattleLogParser;
    public              kat_official_clubInfoParser                                             official_clubInfoParser;
    public              kat_clubLogParser                                                       clubLogParser;
    private             ArrayList<String>                                                       offi_PlayerArrayList;
    public              kat_official_playerInfoParser.playerData                                playerData;
    public              kat_official_clubInfoParser.clubData                                    clubData;
    public              kat_clubLogParser.clubLogData                                           clubLogData;
    public              static ArrayList<kat_official_playerBattleLogParser.playerBattleData>   playerBattleDataList = new ArrayList<>();

    private             static final int                                                        ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;

    public              kat_Service_OverdrawActivity                                            katService;
    private             boolean                                                                 bound = false;
    private             boolean                                                                 endClickToUnbind = false;

    public              kat_database                                                            katabase;
    private             FragmentManager                                                         fragmentManager;
    private             FragmentTransaction                                                     fragmentTransaction;
    public              LayoutInflater                                                          layoutInflater;

    public              kat_mapsParser                                                          mapsParser;
    public              kat_brawlersParser                                                      brawlersParser;
    public              static HashMap<String, kat_mapsParser.mapData>                          mapData;
    public              static ArrayList<HashMap<String, Object>>                               BrawlersArrayList = new ArrayList<>();


    public              static Stack<ArrayList<kat_official_playerBattleLogParser.playerBattleData>> playerBattleDataListStack = new Stack<>();
    public              static Stack<kat_Player_PlayerDetailActivity>                                player_playerDetailActivities = new Stack<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_main);

        unbindThread ubt = new unbindThread();
        if(!ubt.isAlive()) ubt.start();

        player_user_search = (TextInputEditText)findViewById(R.id.player_user_searchInput);
        player_club_search = (TextInputEditText)findViewById(R.id.player_club_searchInput);

        client.init();


        katabase = new kat_database(getApplicationContext(), "kat", null, 1);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 앱이 처음 실행될 때 이전에 본인의 계정을 저장했었다면 그 계정 태그를 가져와 playerTag에 저장해준다.

        //katService.getPlayerTag = playerTag;
        test t = new test();
        t.start();
    }

    private class test extends Thread{
        public void run(){
            while(true){
                try {
                    System.out.println("size : " + playerBattleDataListStack.size());
                    sleep(1000);
                }
                catch (Exception e){

                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getMapDataThread mdt = new getMapDataThread();
        if(!mdt.isAlive()) mdt.start();
    }

    // 맵 데이터 받아오기
    private class getMapDataThread extends Thread{
        public void run(){
            try{
                while(true) {
                    if(client.getData() == null || client.getData().size() <= 2) continue;
                    if (client.getData().get(2) != null) {

                        mapsParser = new kat_mapsParser(client.getData().get(2));
                        mapData = mapsParser.DataParser();

                        brawlersParser = new kat_brawlersParser(client.getData().get(1));
                        BrawlersArrayList = brawlersParser.DataParser();

                        int time = 1000 * 60 * 30;
                        sleep(time);
                    }
                }
            }
            catch (Exception e){
               // e.printStackTrace();
            }
        }
    }


    // 전적 검색 클릭
    public void onUserSearchClick(View view){
        Intent intent = new Intent(kat_Player_MainActivity.this, kat_Player_RecentSearchActivity.class);
        intent.putExtra("type", "players");
        startActivity(intent);
    }

    public void onClubSearchClick(View view){
        Intent intent = new Intent(kat_Player_MainActivity.this, kat_Player_RecentSearchActivity.class);
        intent.putExtra("type", "clubs");
        startActivity(intent);
    }




    public void onMyAccountDecisionClick(View view){

        // 만약 저장된 본인 계정을 변경했다면 playerTag 역시 변경해준다.

        katService.getPlayerTag = playerTag;
    }



    private class testThread extends Thread{
        public void run(){
            while(true){
                if(katService != null && playerTag != null)
                    katService.getPlayerTag = playerTag;
            }
        }
    }


    // service 시작 및 종료
    public void onStartClick(View view){
        endClickToUnbind = false;
        getPermission();
        testThread tt = new testThread();
        tt.start();
    }

    public void onEndClick(View view){
        Intent intent = new Intent(this, kat_Service_OverdrawActivity.class);
        if(katService != null && !endClickToUnbind) {
            endClickToUnbind = true;
            unbindService(katConnection);
        }
    }


    public class unbindThread extends Thread{

        public void run(){
            while(true){
                try{
                    if(katService == null) continue;
                    if(katService.unbindCall && bound){
                        katService.unbindCall = false;
                        endClickToUnbind = true;
                        unbindService(katConnection);
                        bound = false;
                        sleep(3000);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void getPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {// 체크

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            else {
                Intent intent = new Intent(kat_Player_MainActivity.this, kat_Service_OverdrawActivity.class);
                intent.putExtra("playerTag", playerTag);
                bindService(intent, katConnection, Context.BIND_AUTO_CREATE);
                //startService(intent);
            }
        }

        else {
            Intent intent = new Intent(kat_Player_MainActivity.this, kat_Service_OverdrawActivity.class);
            intent.putExtra("playerTag", playerTag);
            bindService(intent, katConnection, Context.BIND_AUTO_CREATE);
            //startService(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // TODO 동의를 얻지 못했을 경우의 처리

            } else {
                Intent intent = new Intent(kat_Player_MainActivity.this, kat_Service_OverdrawActivity.class);
                intent.putExtra("playerTag", playerTag);
                bindService(intent, katConnection, Context.BIND_AUTO_CREATE);
                startService(intent);
            }
        }
    }



    public ServiceConnection katConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            kat_Service_OverdrawActivity.LocalBinder binder = (kat_Service_OverdrawActivity.LocalBinder) iBinder;
            katService = binder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };



}
