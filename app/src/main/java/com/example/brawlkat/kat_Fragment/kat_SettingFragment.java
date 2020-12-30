package com.example.brawlkat.kat_Fragment;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.brawlkat.R;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Service_BrawlStarsNotifActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_SettingFragment extends Fragment {

    kat_Player_MainActivity kat_player_mainActivity;
    public static boolean serviceStarted = false;
    public static boolean analyticsServiceStarted = false;
    private Intent serviceIntent;


    public kat_SettingFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceIntent = new Intent(getActivity().getApplicationContext(), kat_Service_BrawlStarsNotifActivity.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        // 세팅의 스위치 선언
        Switch foregroundServiceSwitch = view.findViewById(R.id.foregroundServiceSwitch);
        final Switch analyticsServiceSwitch = view.findViewById(R.id.analyticsServiceSwitch);

        // foreground service 버튼이 비활성화일 경우 그 자식 스위치도 비활성화를 위해 analyticsThumb 따로 빼놓음
        final Drawable analyticsThumb = analyticsServiceSwitch.getThumbDrawable();
        analyticsThumb.setColorFilter(Color.parseColor("#FF424242"), PorterDuff.Mode.MULTIPLY);

        // 초기화 : SettingFragment가 시작될 때 두 개의 서비스를 시작할지 여부 확인 (미리 체크해놓은 정보를 바탕으로)
        if(kat_LoadBeforeMainActivity.kataSettingBase.getData("ForegroundService") == 0){
            foregroundServiceSwitch.setChecked(false);
            setForegroundSwitch(false, analyticsServiceSwitch, analyticsThumb);
        }
        else {
            foregroundServiceSwitch.setChecked(true);
            setForegroundSwitch(true, analyticsServiceSwitch, analyticsThumb);
        }

        if(kat_LoadBeforeMainActivity.kataSettingBase.getData("AnalyticsService") == 0){
            analyticsServiceSwitch.setChecked(false);
            setAnalyticsSwitch(false);
        }
        else {
            analyticsServiceSwitch.setChecked(true);
            setAnalyticsSwitch(true);
        }

        // 스위치 누를 때 서비스 시작 및 종료 세팅
        analyticsServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setAnalyticsSwitch(b);
            }
        });

        foregroundServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setForegroundSwitch(b, analyticsServiceSwitch, analyticsThumb);
            }
        });

        return view;
    }

    // 포그라운드 스위치를 터치할 때
    private void setForegroundSwitch(boolean b, Switch analyticsServiceSwitch, Drawable analyticsThumb){
        if(b){
            if(!checkPermission()){
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }
            if(!serviceStarted) {
                serviceStarted = true;
                if(!kat_Player_MainActivity.isForegroundServiceAlreadyStarted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        kat_player_mainActivity.startForegroundService(serviceIntent);
                    }
                    else{
                        kat_player_mainActivity.startService(serviceIntent);
                    }
                }
                Toast myToast = Toast.makeText(kat_player_mainActivity.getApplicationContext(),
                        "서비스가 시작되었습니다.", Toast.LENGTH_SHORT);
                myToast.show();
            }
            // 데이터베이스 업데이트 및 자식 스위치인 아날리틱스 스위치 모양 변경
            kat_LoadBeforeMainActivity.kataSettingBase.update("ForegroundService", true);
            analyticsServiceSwitch.setTextColor(getResources().getColor(R.color.switchOn));
            analyticsServiceSwitch.setClickable(true);
            analyticsThumb.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
            kat_Player_MainActivity.isForegroundServiceAlreadyStarted = false;
        }else{
            kat_player_mainActivity.stopService(serviceIntent);
            serviceStarted = false;

            Toast myToast = Toast.makeText(kat_player_mainActivity.getApplicationContext(),
                    "서비스가 종료되었습니다.", Toast.LENGTH_SHORT);
            myToast.show();

            // 자식 스위치와 자신 세팅
            kat_LoadBeforeMainActivity.kataSettingBase.update("ForegroundService", false);
            kat_LoadBeforeMainActivity.kataSettingBase.update("AnalyticsService", false);
            analyticsServiceSwitch.setTextColor(getResources().getColor(R.color.switchOff));
            analyticsServiceSwitch.setChecked(false);
            analyticsThumb.setColorFilter(Color.parseColor("#FF424242"), PorterDuff.Mode.MULTIPLY);
            analyticsServiceSwitch.setClickable(false);
        }
    }

    // 아날리틱스 스위치를 터치할 때 -> 어차피 이건 BrawlStarsNotifActivity의 스레드에서 알아서 돌리니까 boolean만 업데이트 해주면 됨.
    private void setAnalyticsSwitch(boolean b){
        if(b){
            analyticsServiceStarted = true;
            kat_LoadBeforeMainActivity.kataSettingBase.update("AnalyticsService", true);
        }else{
            analyticsServiceStarted = false;
            kat_LoadBeforeMainActivity.kataSettingBase.update("AnalyticsService", false);
        }
    }



    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) kat_player_mainActivity.getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), kat_player_mainActivity.getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (kat_player_mainActivity.getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }
}
