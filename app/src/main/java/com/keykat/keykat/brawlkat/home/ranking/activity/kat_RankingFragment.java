package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.ranking.util.kat_Player_RankingAdapter;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.kat_Data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class kat_RankingFragment extends Fragment {

    public                          kat_LoadingDialog                           dialog;
    private                         ViewPager2                                  viewPager2;
    private                         FragmentStateAdapter                        fragmentStateAdapter;
    private                         TabLayout                                   tabLayout;
    private                         Button                                      countryChangeButton;

    public                          static RequestOptions                       options;
    public                          static int                                  height;
    public                          static int                                  width;

    public                          String                                      checkCountryCode = "KR";

    private                         Context                                     mContext;


    public kat_RankingFragment(kat_LoadingDialog dialog){
        this.dialog = dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kat_Data.client.RankingInit(kat_Data.kataCountryBase.getCountryCode(), "", "");

        dialog  = new kat_LoadingDialog(getActivity());

        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        mContext = getActivity().getApplicationContext();
    }


    @Override
    public void onStart() {
        super.onStart();

        countryChangeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(getActivity().getApplicationContext(), kat_CountrySelectionPopUpActivity.class);
                startActivityForResult(intent, 1111);
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking, container, false);

        viewPager2 = view.findViewById(R.id.player_ranking_viewpager);
        tabLayout = view.findViewById(R.id.player_ranking_tablayout);
        countryChangeButton = view.findViewById(R.id.player_ranking_countrychange);

        countryChangeButton.setText(kat_Data.kataCountryBase.getCountryCode());

        fragmentStateAdapter = new kat_Player_RankingAdapter(this, dialog, mContext);
        viewPager2.setAdapter(fragmentStateAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        if(position == 0) tab.setText("플레이어");
                        else if(position == 1) tab.setText("클럽");
                        else if(position == 2) tab.setText("브롤러");
                        else if(position == 3) tab.setText("파워플레이");
                    }
                }).attach();

        return view;
    }

    private class MyCountryDatabaseChangeThread extends Thread{
        @Override
        public void run(){
            while(true){
                if(kat_Data.MyPlayerRankingArrayList.size() > 0 &&
                        kat_Data.MyClubRankingArrayList.size() > 0 &&
                        kat_Data.MyPowerPlaySeasonArrayList.size() > 0
                ){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            fragmentStateAdapter.notifyDataSetChanged();
                            viewPager2.setAdapter(fragmentStateAdapter);
                            dialog.dismiss();
                        }
                    });

                    break;
                }
            }
        }
    }

    private class checkCountryCodeChangedThread extends Thread {

        String countryCode;

        public checkCountryCodeChangedThread(String countryCode){
            this.countryCode = countryCode;
        }

        public void run(){

            kat_Data.MyPlayerRankingArrayList.clear();
            kat_Data.MyClubRankingArrayList.clear();
            kat_Data.MyPowerPlaySeasonArrayList.clear();
            checkCountryCode = kat_Data.kataCountryBase.getCountryCode();

            kat_Data.client.RankingInit(checkCountryCode, "", "");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                    countryChangeButton.setText(checkCountryCode);
                    MyCountryDatabaseChangeThread myCountryDatabaseChangeThread = new MyCountryDatabaseChangeThread();
                    myCountryDatabaseChangeThread.start();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1111){
            if(resultCode == 1112){
                //데이터 받기
                String result = data.getStringExtra("changedCountryCode");
                checkCountryCodeChangedThread checkCountryCodeChangedThread = new checkCountryCodeChangedThread(result);
                checkCountryCodeChangedThread.start();
            }
        }
    }

    public static String PlayerImageUrl(String iconId){
        return kat_Data.WebRootUrl + "/assets/profile/" + iconId + ".png?v=1";
    }
    public static String ClubImageUrl(String badgeId){
        return kat_Data.CdnRootUrl + "/club/" + badgeId + ".png?v=1";
    }

}
