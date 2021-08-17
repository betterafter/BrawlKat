package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.splash.activity.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.search.result.player.activity.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
import com.keykat.keykat.brawlkat.util.parser.kat_official_BrawlerRankingParser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_BrawlerFragment extends Fragment {

    private                         kat_LoadingDialog                           dialog;
    private                         String                                      initId;
    private                         String                                      initName;
    private                         LinearLayout                                player_ranking_brawler_layout;
    private                         Button                                      button;

    private                         Context                                     mContext;

    public kat_Ranking_BrawlerFragment(){};

    public kat_Ranking_BrawlerFragment(kat_LoadingDialog dialog, Context mContext){
        this.dialog = dialog;
        this.mContext = mContext;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        dialog = new kat_LoadingDialog(mContext);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initId = kat_LoadBeforeMainActivity.BrawlersArrayList.get(0).get("id").toString();
        initName = kat_LoadBeforeMainActivity.BrawlersArrayList.get(0).get("name").toString();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_brawler, container, false);
        player_ranking_brawler_layout = view.findViewById(R.id.player_ranking_brawler_layout);

        dialog.show();
        globalClick(player_ranking_brawler_layout, dialog);

        button = view.findViewById(R.id.player_ranking_brawler_select);
        button.setText(initName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity().getApplicationContext(), kat_BrawlerSelectionPopUpActivity.class);
                startActivityForResult(intent, 1011);
            }
        });

        final Button globalButton = view.findViewById(R.id.player_ranking_brawler_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_brawler_mycountry);

        globalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                globalClick(player_ranking_brawler_layout, dialog);
            }
        });

        MyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                myCountryClick(player_ranking_brawler_layout, dialog);
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1011){
            if(resultCode == 1113){
                //데이터 받기
                initId = data.getStringExtra("BrawlerId");
                initName = data.getStringExtra("BrawlerName");
                button.setText(initName);

                dialog.show();
                globalClick(player_ranking_brawler_layout, dialog);
            }
        }
    }


    public void globalClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        if(!kat_LoadBeforeMainActivity.BrawlerRankingArrayList.containsKey(initId)){
            kat_LoadBeforeMainActivity.client.RankingInit(
                    "global", initId, "Brawler"
            );
        }

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread("global");
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView
                = new setUiOnMainView(player_ranking_player_layout, dialog, databaseChangeThread, "global");
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog){

        String countryCode = kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode();

        if(!kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.containsKey(countryCode) ||
                (kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.containsKey(countryCode) &&
                !kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.get(countryCode).containsKey(initId))){
            kat_LoadBeforeMainActivity.client.RankingInit(
                    countryCode, initId, "Brawler"
            );
        }

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread(countryCode);
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout, dialog, databaseChangeThread,
                kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode());
        setUiOnMainView.start();

    }


    private class setUiOnMainView extends Thread{

        LinearLayout player_ranking_player_layout;
        kat_LoadingDialog dialog;
        DatabaseChangeThread databaseChangeThread;
        String type;

        public setUiOnMainView(LinearLayout player_ranking_player_layout,
                               kat_LoadingDialog dialog,
                               DatabaseChangeThread databaseChangeThread,
                               String type){
            this.player_ranking_player_layout = player_ranking_player_layout;
            this.dialog = dialog;
            this.databaseChangeThread = databaseChangeThread;
            this.type = type;
        }

        public void run(){

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        databaseChangeThread.join();

                        if(type.equals("global")) {
                            setView(player_ranking_player_layout, kat_LoadBeforeMainActivity.BrawlerRankingArrayList.get(initId), dialog);
                        }

                        else {
                            ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData> MyBrawlerRankingArrayList
                                    = kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                                    .get(kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode())
                                    .get(initId);

                            setView(player_ranking_player_layout, MyBrawlerRankingArrayList, dialog);
                        }

                        synchronized (this){
                            this.notify();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            synchronized (runnable) {
                getActivity().runOnUiThread(runnable);
                try {
                    runnable.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DatabaseChangeThread extends Thread{

        String type;

        public DatabaseChangeThread(String type){
            this.type = type;
        }

        @Override
        public void run(){

            String countryCode = kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode();

            while(true){

                if(type.equals("global")){
                    if(!kat_LoadBeforeMainActivity.BrawlerRankingArrayList.containsKey(initId)){
                        continue;
                    }
                    else break;
                }
                else{
                    if(!kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.containsKey(countryCode) ||
                            (kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.containsKey(countryCode) &&
                                    !kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.get(countryCode).containsKey(initId))){
                        continue;
                    }
                    else break;
                }
            }
        }
    }


    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData> BrawlerRankingArrayList,
                        kat_LoadingDialog dialog){

        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        player_ranking_player_layout.removeAllViews();

        for(int i = 0; i < BrawlerRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_BrawlerRankingParser.brawlerRankingData brawlerRankingData = BrawlerRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank = itemView.findViewById(R.id.player_Ranking_rank);

            GlideImage(kat_RankingFragment.PlayerImageUrl(brawlerRankingData.getIconId()),
                    kat_RankingFragment.height / 15,
                    kat_RankingFragment.height / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(brawlerRankingData.getName());
            player_ranking_player_tag.setText(brawlerRankingData.getTag());
            player_ranking_player_club.setText(brawlerRankingData.getClubName());
            player_ranking_player_trophies.setText(brawlerRankingData.getTrophies());
            player_ranking_player_rank.setText(brawlerRankingData.getRank());

            String nameColor = "#ffffff";
            if(brawlerRankingData.getNameColor() != null)
                nameColor = "#" + brawlerRankingData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String realTag = brawlerRankingData.getTag().substring(1);

                    kat_SearchThread kset = new kat_SearchThread(getActivity(), kat_Player_PlayerDetailActivity.class, dialog);
                    kset.SearchStart(realTag, "players", getActivity().getApplicationContext());
                }
            });

            player_ranking_player_layout.addView(itemView);
        }
        if(dialog != null) dialog.dismiss();
    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(mContext)
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .override(width, height)
                .into(view);
    }

    public void GlideImageWithRoundCorner(String url, int width, int height, ImageView view){
        Glide.with(getActivity().getApplicationContext())
                .applyDefaultRequestOptions(kat_RankingFragment.options)
                .load(url)
                .apply(new RequestOptions().circleCrop().circleCrop())
                .override(width, height)
                .into(view);


    }

}
