package com.example.brawlkat.kat_Fragment;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.brawlkat.R;
import com.example.brawlkat.kat_Player_MainActivity;
import com.example.brawlkat.kat_Service_BrawlStarsNotifActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_SettingFragment extends Fragment {

    kat_Player_MainActivity kat_player_mainActivity;
    public static boolean serviceStarted = false;

    public kat_SettingFragment(kat_Player_MainActivity kat_player_mainActivity){
        this.kat_player_mainActivity = kat_player_mainActivity;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);
        Button btn = view.findViewById(R.id.servicetestbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!checkPermission()){
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                }

                if(!serviceStarted) {
                    serviceStarted = true;
                    Intent i = new Intent(getActivity().getApplicationContext(), kat_Service_BrawlStarsNotifActivity.class);
                    kat_player_mainActivity.startService(i);
                }
            }
        });
        return view;
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
