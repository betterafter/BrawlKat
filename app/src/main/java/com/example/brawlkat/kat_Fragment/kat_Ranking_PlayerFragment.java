package com.example.brawlkat.kat_Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.brawlkat.R;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_LoadingDialog;
import com.example.brawlkat.kat_dataparser.kat_official_PlayerRankingParser;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PlayerFragment extends Fragment {

    private kat_LoadingDialog dialog;

    public kat_Ranking_PlayerFragment(){}

    public kat_Ranking_PlayerFragment(kat_LoadingDialog dialog){
        this.dialog = dialog;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_player, container, false);
        final LinearLayout player_ranking_player_layout = view.findViewById(R.id.player_ranking_player_layout);

        globalClick(player_ranking_player_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_player_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_player_mycountry);

        globalButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent){
                globalClick(player_ranking_player_layout);
                return false;
            }
        });

        MyButton.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent motionEvent){
                myCountryClick(player_ranking_player_layout);
                return false;
            }
        });

        return view;
    }

    public void globalClick(LinearLayout player_ranking_player_layout){

        player_ranking_player_layout.removeAllViews();
        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList
                = kat_LoadBeforeMainActivity.PlayerRankingArrayList;

        setView(player_ranking_player_layout, PlayerRankingArrayList);

    }

    public void myCountryClick(LinearLayout player_ranking_player_layout){

        player_ranking_player_layout.removeAllViews();
        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList
                = kat_LoadBeforeMainActivity.MyPlayerRankingArrayList;

        setView(player_ranking_player_layout, PlayerRankingArrayList);


    }


    public class getRankingDataThread extends Thread{

        ArrayList<kat_official_PlayerRankingParser.playerData> arrayList = new ArrayList<>();
        kat_LoadingDialog dialog;

        public getRankingDataThread(ArrayList<kat_official_PlayerRankingParser.playerData> arrayList, kat_LoadingDialog dialog){
            this.arrayList = arrayList;
            this.dialog = dialog;
        }

        public void run(){
            while(true){
                if(arrayList.size() > 0){
                    if(dialog != null) dialog.dismiss();
                    break;
                }
            }
        }
    }

    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_PlayerRankingParser.playerData> PlayerRankingArrayList){

        getRankingDataThread getRankingDataThread = new getRankingDataThread(PlayerRankingArrayList, dialog);
        getRankingDataThread.start();

        LayoutInflater layoutInflater =
                (LayoutInflater) Objects.requireNonNull(getActivity()).getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i = 0; i < PlayerRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            kat_official_PlayerRankingParser.playerData playerData = PlayerRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);

            GlideImage(kat_RankingFragment.ImageUrl(playerData.getIconId()),
                    kat_RankingFragment.width / 20,
                    kat_RankingFragment.height / 20,
                    player_ranking_player_image);

            player_ranking_player_name.setText(playerData.getName());
            player_ranking_player_tag.setText(playerData.getTag());
            player_ranking_player_club.setText(playerData.getClubName());
            player_ranking_player_trophies.setText(playerData.getTrophies());

            player_ranking_player_layout.addView(itemView);
        }
    }


    public void GlideImage(String url, int width, int height, ImageView view){

        Glide.with(getActivity().getApplicationContext())
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
