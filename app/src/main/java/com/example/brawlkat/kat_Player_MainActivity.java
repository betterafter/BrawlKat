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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.brawlkat.database.kat_database;
import com.example.brawlkat.database.kat_favoritesDatabase;
import com.example.brawlkat.dataparser.kat_brawlersParser;
import com.example.brawlkat.dataparser.kat_clubLogParser;
import com.example.brawlkat.dataparser.kat_mapsParser;
import com.example.brawlkat.dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.dataparser.kat_official_playerInfoParser;
import com.example.brawlkat.dataparser.kat_official_playerParser;
import com.example.brawlkat.katfragment.kat_FavoritesFragment;
import com.example.brawlkat.katfragment.kat_RankingFragment;
import com.example.brawlkat.katfragment.kat_SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class kat_Player_MainActivity extends AppCompatActivity {

    // 하단 네비게이션 바 관련
    private             BottomNavigationView                                                    bottomNavigationView;
    private             FragmentManager                                                         fragmentManager;
    private             FragmentTransaction                                                     fragmentTransaction;

    private             kat_SearchFragment                                                      kat_searchFragment;
    private             kat_FavoritesFragment                                                   kat_favoritesFragment;
    private             kat_RankingFragment                                                     kat_rankingFragment;
    //.................................................................................................................//

    public              ImageButton                                                             serviceButton;
    private             boolean                                                                 isServiceStart = false;

    public              static String                                                           official = "official";
    public              static String                                                           nofficial = "nofficial";


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


    public              static kat_database                                                     katabase;
    public              static kat_favoritesDatabase                                            kataFavoritesBase;

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
        setContentView(R.layout.main);

        // 하단 네비게이션바 세팅 //////////////////////////////////////////////////////////////////////////////////////////////////
        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.action_search:
                        setFrag(0);
                        break;
                    case R.id.action_favorites:
                        setFrag(1);
                        break;
                    case R.id.action_ranking:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        kat_searchFragment = new kat_SearchFragment(kat_Player_MainActivity.this);
        kat_favoritesFragment = new kat_FavoritesFragment();
        kat_rankingFragment = new kat_RankingFragment();

        // ...........................................................................................................

        unbindThread ubt = new unbindThread();
        if(!ubt.isAlive()) ubt.start();

        client.init();

        katabase = new kat_database(getApplicationContext(), "kat", null, 1);
        kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 1);


        serviceButton = (ImageButton)findViewById(R.id.main_serviceButton);
        serviceButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isServiceStart) {
                        if (katService != null && !endClickToUnbind) {
                            endClickToUnbind = true;
                            unbindService(katConnection);
                            isServiceStart = false;
                        }
                    } else {
                        endClickToUnbind = false;
                        getPermission();
                        testThread tt = new testThread();
                        tt.start();
                        isServiceStart = true;
                    }
                }
                return false;
            }
        });


        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // setFrag(0)를 할 때 Recent_Search로 넘어가지 않는 오류가 있었는데, Recent_Search에서 fragment 를 담을 Main_Frame이 없다고 한다.
        // 생각해보니 Recent_Search는 MainActivity를 상속 받기 때문에 MainActivity의 onCreate나 onStart도 상속을 받아서
        // setFrag 메소드를 실행해버린다. 그래서 recent_Search에 자꾸 fragment 화면을 띄우려고 한 것.
        // setFrag를 MainActivity에서만 실행하게 해준다.
        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_MainActivity")) setFrag(0);
    }


    @Override
    protected void onStart() {

        super.onStart();
        getMapDataThread mdt = new getMapDataThread();
        if (!mdt.isAlive()) mdt.start();

        //test t = new test();
        //t.start();

    }


    private class test extends Thread{
        public void run(){
            while(true){
                try{
                    sleep(3000);
                    setFrag(0);
                    break;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    // 프레그먼트 교체
    public void setFrag(int n)
    {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (n)
        {
            case 0:
                fragmentTransaction.replace(R.id.Main_Frame, kat_searchFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentTransaction.replace(R.id.Main_Frame, kat_favoritesFragment);
                fragmentTransaction.commit();
                break;

            case 2:
                fragmentTransaction.replace(R.id.Main_Frame, kat_rankingFragment);
                fragmentTransaction.commit();
                break;


        }
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

    private class testThread extends Thread{
        public void run(){
            while(true){
                if(katService != null && playerTag != null)
                    katService.getPlayerTag = playerTag;
            }
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
                        isServiceStart = false;
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
