package com.keykat.keykat.brawlkat.home.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.favorite.activity.kat_FavoritesFragment;
import com.keykat.keykat.brawlkat.home.ranking.activity.kat_RankingFragment;
import com.keykat.keykat.brawlkat.home.search.activity.kat_SearchFragment;
import com.keykat.keykat.brawlkat.home.setting.activity.SettingsActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity;
import com.keykat.keykat.brawlkat.service.activity.kat_Service_OverdrawActivity;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.AsyncCoroutine;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

public class kat_Player_MainActivity extends AppCompatActivity {

    public kat_SearchFragment kat_searchFragment;
    public kat_FavoritesFragment kat_favoritesFragment;
    public kat_RankingFragment kat_rankingFragment;
    //.................................................................................................................//

    //.........................................service.................................................................//
    public ImageButton serviceButton;
    public static Intent serviceIntent;
    public static Intent foregroundServiceIntent;
    private static long mLastClickTime = 0;
    //.................................................................................................................//
    public static String official = "official";
    public static String playerTag;
    public static kat_official_playerInfoParser.playerData MyPlayerData;
    public kat_official_playerInfoParser.playerData playerData;

    private static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1;


    public LayoutInflater layoutInflater;

    private boolean firstStart = true;

    public static kat_Player_MainActivity kat_player_mainActivity;

    private InterstitialAd interstitialAd;

    private SharedPreferences sharedPreferences;

    @SuppressLint({"ClickableViewAccessibility", "NonConstantResourceId"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        layoutInflater = (LayoutInflater) getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-5909086836185335/8059778643");

        // 브롤러 추천 서비스 알림창을 on 했을 때
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sharedPreferences.getBoolean(getString(R.string.notify_service), false)) {
            foregroundServiceIntent = new Intent(
                    getApplicationContext(),
                    kat_Service_BrawlStarsNotifActivity.class
            );
            startForegroundService(foregroundServiceIntent);
        }

        serviceIntent = new Intent(kat_Player_MainActivity.this, kat_Service_OverdrawActivity.class);
        KatData.dialog = new kat_LoadingDialog(this);

        kat_player_mainActivity = this;

        kat_searchFragment = new kat_SearchFragment(kat_Player_MainActivity.this);
        kat_favoritesFragment = new kat_FavoritesFragment();
        kat_rankingFragment = new kat_RankingFragment();

        // 하단 네비게이션바 세팅 //////////////////////////////////////////////////////////////////////////////////////////////////
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            kat_searchFragment = new kat_SearchFragment(
                    kat_Player_MainActivity.this
            );
            kat_favoritesFragment = new kat_FavoritesFragment();
            kat_rankingFragment = new kat_RankingFragment();

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
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });
        ///////////////////////////////////////////////////////////////////////////////////////////


        serviceButton = findViewById(R.id.main_serviceButton);
        serviceButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "너무 빨리 눌렀습니다. 천천히 눌러주세요.",
                                Toast.LENGTH_SHORT);

                        toast.show();
                        return false;
                    } else {
                        if (KatData.Companion.isForegroundServiceStart()) {
                            stopService(serviceIntent);
                            KatData.Companion.setForegroundServiceStart(false);
                        } else {
                            KatData.dialog.show();
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                            interstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    getPermission();
                                    KatData.Companion.setForegroundServiceStart(true);
                                    KatData.dialog.dismiss();
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    super.onAdFailedToLoad(errorCode);
                                    getPermission();
                                    KatData.Companion.setForegroundServiceStart(true);
                                    KatData.dialog.dismiss();
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
    }


    @Override
    protected void onStart() {

        super.onStart();

        Intent intent = getIntent();
        if (intent != null) {
            MyPlayerData = (kat_official_playerInfoParser.playerData) intent.getSerializableExtra("playerData");
            if (firstStart) {
                firstStart = false;
                setFrag(0);
            }
        }

        if (KatData.kataMyAccountBase.size() > 0) {
            playerTag = KatData.kataMyAccountBase.getTag().substring(1);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        KatData.currentActivity = this;
        AsyncCoroutine.Companion.setFinished(true);
    }


    // 프레그먼트 교체
    public void setFrag(int n) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (n) {
            case 0:
                fragmentTransaction.replace(R.id.Main_Frame, kat_searchFragment);
                fragmentTransaction.commit();
                break;

            case 1:
                fragmentTransaction.replace(R.id.Main_Frame, kat_favoritesFragment);
                fragmentTransaction.commit();
                break;

            case 2:
                AsyncCoroutine.Companion.accessRankingFragment(
                        fragmentTransaction,
                        R.id.Main_Frame,
                        kat_rankingFragment
                );
                break;
        }
    }


    // service .....................................................................................


    public void getPermission() {

        if (KatData.Companion.isForegroundServiceStart()) return;
        // 마시멜로우 이상일 경우
        if (!Settings.canDrawOverlays(this)) {// 체크

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
        } else {
            kat_Service_OverdrawActivity.getPlayerTag = playerTag;
            startService(serviceIntent);
            KatData.Companion.setForegroundServiceStart(true);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAffinity(this);
        finishAffinity();
        finish();
    }
}
