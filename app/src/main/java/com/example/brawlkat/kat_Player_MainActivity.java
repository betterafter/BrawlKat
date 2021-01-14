package com.example.brawlkat;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.brawlkat.kat_Fragment.kat_FavoritesFragment;
import com.example.brawlkat.kat_Fragment.kat_RankingFragment;
import com.example.brawlkat.kat_Fragment.kat_SearchFragment;
import com.example.brawlkat.kat_Fragment.kat_SettingFragment;
import com.example.brawlkat.kat_dataparser.kat_clubLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_clubInfoParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerBattleLogParser;
import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class kat_Player_MainActivity extends kat_LoadBeforeMainActivity {

    // 하단 네비게이션 바 관련
    private             BottomNavigationView                                                    bottomNavigationView;
    private             FragmentManager                                                         fragmentManager;
    private             FragmentTransaction                                                     fragmentTransaction;

    public              kat_SearchFragment                                                      kat_searchFragment;
    public              kat_FavoritesFragment                                                   kat_favoritesFragment;
    public              kat_RankingFragment                                                     kat_rankingFragment;
    public              kat_SettingFragment                                                     kat_settingFragment;
    //.................................................................................................................//

    //.........................................service.................................................................//
    public              ImageButton                                                             serviceButton;
    public              static boolean                                                          isForegroundServiceAlreadyStarted = false;
    public              static boolean                                                          isServiceStart = false;
    public              static Intent                                                           serviceIntent;
    public              static Intent                                                           foregroundServiceIntent;
    private             static long                                                             mLastClickTime = 0;
    //.................................................................................................................//


    public              static String                                                           official = "official";
    public              static String                                                           nofficial = "nofficial";

    public              static String                                                           playerTag;

    public              kat_official_playerInfoParser                                           official_playerInfoParser;
    public              kat_official_playerBattleLogParser                                      official_playerBattleLogParser;
    public              kat_official_clubInfoParser                                             official_clubInfoParser;
    public              kat_clubLogParser                                                       clubLogParser;


    public              static kat_official_playerInfoParser.playerData                         MyPlayerData;
    public              kat_official_playerInfoParser.playerData                                playerData;
    public              kat_official_clubInfoParser.clubData                                    clubData;
    public              kat_clubLogParser.clubLogData                                           clubLogData;
    public              static ArrayList<kat_official_playerBattleLogParser.playerBattleData>   playerBattleDataList = new ArrayList<>();

    private             static final int                                                        ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;


    public              LayoutInflater                                                          layoutInflater;

    public              static Stack<ArrayList<kat_official_playerBattleLogParser.playerBattleData>> playerBattleDataListStack = new Stack<>();


    private             boolean                                                                 firstStart = true;

    public              static kat_Player_MainActivity                                          kat_player_mainActivity;

    public              kat_LoadingDialog                                                       dialog;


    public              static int                                                              deviceWidth;
    public              static int                                                              deviceHeight;

    private             InterstitialAd                                                          interstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_MainActivity")) {

            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-5909086836185335/8059778643");

            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
            deviceWidth = dm.widthPixels;
            deviceHeight = dm.heightPixels;

            // 포그라운드 서비스가 실행됐는지 확인하고 실행되지 않았다면 실행
            foregroundServiceIntent = new Intent(getApplicationContext(), kat_Service_BrawlStarsNotifActivity.class);
            if(kat_LoadBeforeMainActivity.kataSettingBase.getData("ForegroundService") == 1){

                ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(1000);

                boolean isMainServiceExist = false;
                for(ActivityManager.RunningServiceInfo info : rs) {
                    String className = info.service.getClassName();
                    String packageName = info.service.getPackageName();
                    if(className.equals("com.example.brawlkat.kat_Service_BrawlStarsNotifActivity")){
                        isForegroundServiceAlreadyStarted = true;
                        isMainServiceExist = true;
                        break;
                    }
                }
                if(!isMainServiceExist){
                    isForegroundServiceAlreadyStarted = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(foregroundServiceIntent);
                    }
                    else{
                        startService(foregroundServiceIntent);
                    }
                }
            }


            serviceIntent = new Intent(kat_Player_MainActivity.this, kat_Service_OverdrawActivity.class);
            dialog = new kat_LoadingDialog(this);

            kat_player_mainActivity = this;

            kat_searchFragment = new kat_SearchFragment(kat_Player_MainActivity.this);
            kat_favoritesFragment = new kat_FavoritesFragment(kat_Player_MainActivity.this);
            kat_rankingFragment = new kat_RankingFragment(dialog);
            kat_settingFragment = new kat_SettingFragment(kat_Player_MainActivity.this);

            // 하단 네비게이션바 세팅 //////////////////////////////////////////////////////////////////////////////////////////////////
            bottomNavigationView = findViewById(R.id.bottomNavi);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    kat_searchFragment = new kat_SearchFragment(kat_Player_MainActivity.this);
                    kat_favoritesFragment = new kat_FavoritesFragment(kat_Player_MainActivity.this);
                    kat_rankingFragment = new kat_RankingFragment(dialog);
                    kat_settingFragment = new kat_SettingFragment(kat_Player_MainActivity.this);

                    switch (menuItem.getItemId()) {
                        case R.id.action_search:
                            setFrag(0);
                            break;
                        case R.id.action_favorites:
                            setFrag(1);
                            break;
                        case R.id.action_ranking:
                            setFrag(2);
                            break;
                        case R.id.action_setting:
                            setFrag(3);
                            break;
                    }
                    return true;
                }
            });


            // ...........................................................................................................

            serviceButton = (ImageButton) findViewById(R.id.main_serviceButton);
            serviceButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "너무 빨리 눌렀습니다. 천천히 눌러주세요.",
                                    Toast.LENGTH_SHORT);

                            toast.show();
                            return false;
                        }

                        else {
                            if (isServiceStart) {
                                stopService(serviceIntent);
                                isServiceStart = false;
                            } else {
                                interstitialAd.loadAd(new AdRequest.Builder().build());
                                interstitialAd.setAdListener(new AdListener(){
                                    @Override
                                    public void onAdClosed() {
                                        super.onAdClosed();
                                        getPermission();
                                        isServiceStart = true;
                                    }

                                    @Override
                                    public void onAdFailedToLoad(int errorCode) {
                                        super.onAdFailedToLoad(errorCode);

                                    }

                                    @Override
                                    public void onAdLoaded() {
                                        super.onAdLoaded();
                                        if (interstitialAd.isLoaded()) {
                                            interstitialAd.show();
                                        }
                                    }
                                });
                            }
                        }

                        mLastClickTime = SystemClock.elapsedRealtime();
                    }
                    return false;
                }
            });

            // setFrag(0)를 할 때 Recent_Search로 넘어가지 않는 오류가 있었는데, Recent_Search에서 fragment 를 담을 Main_Frame이 없다고 한다.
            // 생각해보니 Recent_Search는 MainActivity를 상속 받기 때문에 MainActivity의 onCreate나 onStart도 상속을 받아서
            // setFrag 메소드를 실행해버린다. 그래서 recent_Search에 자꾸 fragment 화면을 띄우려고 한 것.
            // setFrag를 MainActivity에서만 실행하게 해준다.
        }
    }



    @Override
    protected void onStart() {

        super.onStart();

        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_MainActivity")) {

            Intent intent = getIntent();
            if (intent != null) {
                MyPlayerData = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra("playerData");
                if(firstStart) {
                    firstStart = false;
                    setFrag(0);
                }
            }

            if(kataMyAccountBase.size() > 0){
                playerTag = kataMyAccountBase.getTag().substring(1);
            }
        }
    }



    private class setBaseDataThread extends Thread{
        public void run(){
            while(true){
                if(PlayerRankingArrayList != null && ClubRankingArrayList != null
                        && PowerPlaySeasonArrayList != null) {
                    if (PlayerRankingArrayList.size() > 0 && ClubRankingArrayList.size() > 0
                            && PowerPlaySeasonArrayList.size() > 0) {
                        if(dialog != null) dialog.dismiss();
                        break;
                    }
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
                dialog.show();
                setBaseDataThread baseDataThread = new setBaseDataThread();
                baseDataThread.start();

                try {
                    baseDataThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fragmentTransaction.replace(R.id.Main_Frame, kat_rankingFragment);
                fragmentTransaction.commit();
                break;

            case 3:
                fragmentTransaction.replace(R.id.Main_Frame, kat_settingFragment);
                fragmentTransaction.commit();
        }
    }



    // service .....................................................................................


    public void getPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {// 체크

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            else {
                kat_Service_OverdrawActivity.getPlayerTag = playerTag;
                startService(serviceIntent);
            }
        }

        else {
            kat_Service_OverdrawActivity.getPlayerTag = playerTag;
            startService(serviceIntent);
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
                kat_Service_OverdrawActivity.getPlayerTag = playerTag;
                startService(serviceIntent);
            }
        }
    }


    //..............................................................................................





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(this.getClass().getName().equals("com.example.brawlkat.kat_Player_MainActivity")){
            ActivityCompat.finishAffinity(this);
            finishAffinity();
            finish();
        }
    }

    //앱종료
    public void AppFinish(){

        moveTaskToBack(true);						// 태스크를 백그라운드로 이동
        //finishAndRemoveTask();						// 액티비티 종료 + 태스크 리스트에서 지우기
        finish();
        //android.os.Process.killProcess(android.os.Process.myPid());	// 앱 프로세스 종료
        //System.exit(0);
    }
}
