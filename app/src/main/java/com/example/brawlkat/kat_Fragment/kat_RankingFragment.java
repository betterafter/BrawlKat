package com.example.brawlkat.kat_Fragment;

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
import com.example.brawlkat.R;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_Player_RankingAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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


    public kat_RankingFragment(kat_LoadingDialog dialog){
        this.dialog = dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog  = new kat_LoadingDialog(getActivity());


        options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .priority(Priority.HIGH)
                .format(DecodeFormat.PREFER_RGB_565);

        DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
        height = metrics.heightPixels;
        width = metrics.widthPixels;

    }

    @Override
    public void onStart() {
        super.onStart();

        countryChangeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                dialog.show();
                if(kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode().equals("KR")){

                    kat_LoadBeforeMainActivity.kataCountryBase.insert("AF", "Afghanistan");
                    countryChangeButton.setText("Afghanistan");

                    kat_LoadBeforeMainActivity.MyPlayerRankingArrayList.clear();
                    kat_LoadBeforeMainActivity.MyClubRankingArrayList.clear();
                    kat_LoadBeforeMainActivity.MyPowerPlaySeasonArrayList.clear();

                    kat_LoadBeforeMainActivity.client.RankingInit("AF", "", "");

                    MyCountryDatabaseChangeThread myCountryDatabaseChangeThread = new MyCountryDatabaseChangeThread();
                    myCountryDatabaseChangeThread.start();
                }
                else{
                    kat_LoadBeforeMainActivity.kataCountryBase.insert("KR", "Korea, Republic of");

                    countryChangeButton.setText("Korea, Republic of");

                    kat_LoadBeforeMainActivity.MyPlayerRankingArrayList.clear();
                    kat_LoadBeforeMainActivity.MyClubRankingArrayList.clear();
                    kat_LoadBeforeMainActivity.MyPowerPlaySeasonArrayList.clear();

                    kat_LoadBeforeMainActivity.client.RankingInit("KR", "", "");

                    MyCountryDatabaseChangeThread myCountryDatabaseChangeThread = new MyCountryDatabaseChangeThread();
                    myCountryDatabaseChangeThread.start();
                }
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

        fragmentStateAdapter = new kat_Player_RankingAdapter(this, dialog);
        viewPager2.setAdapter(fragmentStateAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
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
                if(kat_LoadBeforeMainActivity.MyPlayerRankingArrayList.size() > 0 &&
                        kat_LoadBeforeMainActivity.MyClubRankingArrayList.size() > 0 &&
                        kat_LoadBeforeMainActivity.MyPowerPlaySeasonArrayList.size() > 0
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


    public static String ImageUrl(String iconId){
        return "https://www.starlist.pro/assets/profile/" + iconId + ".png?v=1";
    }
}
