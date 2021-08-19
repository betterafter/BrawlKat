package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.ranking.util.kat_Player_RankingAdapter;
import com.keykat.keykat.brawlkat.util.kat_Data;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class kat_RankingFragment extends Fragment {

    private                         ViewPager2                                  viewPager2;
    private                         FragmentStateAdapter                        fragmentStateAdapter;
    private                         Button                                      countryChangeButton;

    public                          String                                      checkCountryCode = "KR";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        kat_Data.client.RankingInit(kat_Data.kataCountryBase.getCountryCode(), "", "");
    }


    @Override
    public void onStart() {
        super.onStart();
        countryChangeButton.setOnClickListener(view -> {
            Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(),
                    kat_CountrySelectionPopUpActivity.class);
            startActivityForResult(intent, 1111);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking, container, false);

        viewPager2 = view.findViewById(R.id.player_ranking_viewpager);
        TabLayout tabLayout = view.findViewById(R.id.player_ranking_tablayout);
        countryChangeButton = view.findViewById(R.id.player_ranking_countrychange);

        countryChangeButton.setText(kat_Data.kataCountryBase.getCountryCode());

        fragmentStateAdapter = new kat_Player_RankingAdapter(this);
        viewPager2.setAdapter(fragmentStateAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    if(position == 0) tab.setText("플레이어");
                    else if(position == 1) tab.setText("클럽");
                    else if(position == 2) tab.setText("브롤러");
                    else if(position == 3) tab.setText("파워플레이");
                }).attach();

        return view;
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

            getActivity().runOnUiThread(() -> {
                kat_Data.dialog.show();
                countryChangeButton.setText(checkCountryCode);
                MyCountryDatabaseChangeThread myCountryDatabaseChangeThread = new MyCountryDatabaseChangeThread();
                myCountryDatabaseChangeThread.start();
            });
        }
    }

    private class MyCountryDatabaseChangeThread extends Thread{
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void run(){
            while(true){
                if(kat_Data.MyPlayerRankingArrayList.size() > 0 &&
                        kat_Data.MyClubRankingArrayList.size() > 0 &&
                        kat_Data.MyPowerPlaySeasonArrayList.size() > 0
                ){
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        fragmentStateAdapter.notifyDataSetChanged();
                        viewPager2.setAdapter(fragmentStateAdapter);
                    });
                    kat_Data.dialog.cancel();
                    break;
                }
            }
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
