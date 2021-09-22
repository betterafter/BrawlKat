//package com.keykat.keykat.brawlkat.home.setting.activity;
//
//import android.app.AppOpsManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.graphics.PorterDuff;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.text.util.Linkify;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CompoundButton;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.keykat.keykat.brawlkat.R;
//import com.keykat.keykat.brawlkat.home.activity.kat_Player_MainActivity;
//import com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity;
//import com.keykat.keykat.brawlkat.service.util.kat_ActionBroadcastReceiver;
//import com.keykat.keykat.brawlkat.util.kat_Data;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//public class kat_SettingFragment extends Fragment {
//
//    kat_Player_MainActivity kat_player_mainActivity;
//    public static boolean serviceStarted = false;
//    private Intent serviceIntent;
//
//
//    public kat_SettingFragment(kat_Player_MainActivity kat_player_mainActivity){
//        this.kat_player_mainActivity = kat_player_mainActivity;
//    }
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //serviceIntent = new Intent(getActivity().getApplicationContext(), kat_Service_BrawlStarsNotifActivity.class);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.settings, container, false);
//
//        TextView fan_content_policy_text = view.findViewById(R.id.fan_content_policy_text);
//        Linkify.TransformFilter transformFilter = new Linkify.TransformFilter() {
//            @Override
//            public String transformUrl(Matcher matcher, String s) {
//                return "";
//            }
//        };
//        Pattern pattern = Pattern.compile("Supercell’s Fan Content Policy.");
//        Linkify.addLinks(fan_content_policy_text,
//                pattern,
//                "https://supercell.com/en/fan-content-policy/",
//                null,
//                transformFilter);
//
//
//                // 세팅의 스위치 선언
//        Switch foregroundServiceSwitch = view.findViewById(R.id.foregroundServiceSwitch);
//        final Switch analyticsServiceSwitch = view.findViewById(R.id.analyticsServiceSwitch);
//
//        // foreground service 버튼이 비활성화일 경우 그 자식 스위치도 비활성화를 위해 analyticsThumb 따로 빼놓음
//        final Drawable analyticsThumb = analyticsServiceSwitch.getThumbDrawable();
//        analyticsThumb.setColorFilter(Color.parseColor("#FF424242"), PorterDuff.Mode.MULTIPLY);
//
//        // 초기화 : SettingFragment가 시작될 때 두 개의 서비스를 시작할지 여부 확인 (미리 체크해놓은 정보를 바탕으로)
//        if(kat_Data.kataSettingBase.getData("ForegroundService") == 0){
//            foregroundServiceSwitch.setChecked(false);
//            setForegroundSwitch(false, analyticsServiceSwitch, analyticsThumb);
//        }
//        else {
//            foregroundServiceSwitch.setChecked(true);
//            setForegroundSwitch(true, analyticsServiceSwitch, analyticsThumb);
//        }
//
//        if(kat_Data.kataSettingBase.getData("AnalyticsService") == 0){
//            analyticsServiceSwitch.setChecked(false);
//            setAnalyticsSwitch(false);
//        }
//        else {
//            analyticsServiceSwitch.setChecked(true);
//            setAnalyticsSwitch(true);
//        }
//
//        // 스위치 누를 때 서비스 시작 및 종료 세팅
//        analyticsServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                setAnalyticsSwitch(b);
//                Intent intent = new Intent();
//                if(b){
//                    RegisterBroadcastReceiver();
//                    intent.setAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_START");
//                }else{
//                    UnregisterBroadcastReceiver();
//                    intent.setAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_END");
//                }
//                getActivity().sendBroadcast(intent);
//            }
//        });
//
//        foregroundServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                setForegroundSwitch(b, analyticsServiceSwitch, analyticsThumb);
//            }
//        });
//
//        return view;
//    }
//
//    // 포그라운드 스위치를 터치할 때
//    private void setForegroundSwitch(boolean b, Switch analyticsServiceSwitch, Drawable analyticsThumb){
//        if(b){
//            if(!checkPermission()){
//                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//            }
//            if(!serviceStarted) {
//                serviceStarted = true;
//                if(!kat_Player_MainActivity.isForegroundServiceAlreadyStarted) {
//                    kat_player_mainActivity.startForegroundService(serviceIntent);
//                    Toast myToast = Toast.makeText(kat_player_mainActivity.getApplicationContext(),
//                            "서비스가 시작되었습니다.", Toast.LENGTH_SHORT);
//                    myToast.show();
//                }
//            }
//            // 데이터베이스 업데이트 및 자식 스위치인 아날리틱스 스위치 모양 변경
//            kat_Data.kataSettingBase.update("ForegroundService", true);
//            analyticsServiceSwitch.setTextColor(getResources().getColor(R.color.switchOn));
//            analyticsServiceSwitch.setClickable(true);
//            analyticsThumb.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.MULTIPLY);
//            kat_Player_MainActivity.isForegroundServiceAlreadyStarted = false;
//        }else{
//            kat_player_mainActivity.stopService(serviceIntent);
//            serviceStarted = false;
//
//            Toast myToast = Toast.makeText(kat_player_mainActivity.getApplicationContext(),
//                    "서비스가 종료되었습니다.", Toast.LENGTH_SHORT);
//            myToast.show();
//
//            // 자식 스위치와 자신 세팅
//            kat_Data.kataSettingBase.update("ForegroundService", false);
//            kat_Data.kataSettingBase.update("AnalyticsService", false);
//            analyticsServiceSwitch.setTextColor(getResources().getColor(R.color.switchOff));
//            analyticsServiceSwitch.setChecked(false);
//            analyticsThumb.setColorFilter(Color.parseColor("#FF424242"), PorterDuff.Mode.MULTIPLY);
//            analyticsServiceSwitch.setClickable(false);
//        }
//    }
//
//    // 아날리틱스 스위치를 터치할 때 -> 어차피 이건 BrawlStarsNotifActivity의 스레드에서 알아서 돌리니까 boolean만 업데이트 해주면 됨.
////    private void setAnalyticsSwitch(boolean b){
////        if(b){
////            kat_Data.kataSettingBase.update("AnalyticsService", true);
////        }else{
////            kat_Data.kataSettingBase.update("AnalyticsService", false);
////        }
////    }
//
//
//
////    private boolean checkPermission(){
////
////        boolean granted = false;
////
////        AppOpsManager appOps = (AppOpsManager) kat_player_mainActivity.getApplicationContext()
////                .getSystemService(Context.APP_OPS_SERVICE);
////        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
////                android.os.Process.myUid(), kat_player_mainActivity.getApplicationContext().getPackageName());
////
////        if (mode == AppOpsManager.MODE_DEFAULT) {
////            granted = (kat_player_mainActivity.getApplicationContext().checkCallingOrSelfPermission(
////                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
////        } else {
////            granted = (mode == AppOpsManager.MODE_ALLOWED);
////        }
////
////        return granted;
////    }
//
////
////    private void RegisterBroadcastReceiver(){
////
////        BroadcastReceiver broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver;
////        String BROADCAST_MASSAGE_SCREEN_ON = "android.intent.action.SCREEN_ON";
////        String BROADCAST_MASSAGE_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
////
////        if(kat_Data.kataSettingBase.getData("AnalyticsService") == 0) return;
////        if(broadcastReceiver != null) return;
////
////        final IntentFilter filter = new IntentFilter();
////        filter.addAction(BROADCAST_MASSAGE_SCREEN_ON);
////        filter.addAction(BROADCAST_MASSAGE_SCREEN_OFF);
////        filter.addAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_START");
////        filter.addAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_END");
////
////        broadcastReceiver = new kat_ActionBroadcastReceiver(kat_player_mainActivity);
////
////        getActivity().registerReceiver(broadcastReceiver, filter);
////
////        Intent ThreadCheckIntent = new Intent();
////        if(kat_Data.kataSettingBase.getData("AnalyticsService") == 0){
////            ThreadCheckIntent.setAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_END");
////        }
////        else{
////            ThreadCheckIntent.setAction("com.keykat.keykat.brawlkat.service.activity.kat_Service_BrawlStarsNotifActivity.CHECK_START");
////        }
////        getActivity().sendBroadcast(ThreadCheckIntent);
////    }
////
////
////    private void UnregisterBroadcastReceiver(){
////
////        BroadcastReceiver broadcastReceiver = kat_Service_BrawlStarsNotifActivity.broadcastReceiver;
////
////        if(broadcastReceiver != null){
////            getActivity().unregisterReceiver(broadcastReceiver);
////            broadcastReceiver = null;
////        }
////    }
//}
