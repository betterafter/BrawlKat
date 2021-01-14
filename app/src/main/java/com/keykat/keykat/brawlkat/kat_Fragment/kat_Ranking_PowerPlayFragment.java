package com.keykat.keykat.brawlkat.kat_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.kat_LoadBeforeMainActivity;
import com.keykat.keykat.brawlkat.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.kat_Thread.kat_SearchThread;
import com.keykat.keykat.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonRankingParser;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PowerPlayFragment extends Fragment {


    private                         kat_LoadingDialog                           dialog;
    public                          boolean                                     set = false;
    private                         LinearLayout                                player_ranking_powerplay_layout;
    private                         TextView                                    seasonIdTextView;
    private                         TextView                                    seasonDateTextView;
    private                         Spinner                                     SeasonsSpinner;
    private                         String                                      SeasonId;

    private                         Context                                     mContext;

    public kat_Ranking_PowerPlayFragment(){}

    public kat_Ranking_PowerPlayFragment(kat_LoadingDialog dialog, Context mContext){
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

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.player_ranking_powerplay, container, false);
        player_ranking_powerplay_layout = view.findViewById(R.id.player_ranking_powerplay_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_powerplay_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_powerplay_mycountry);

        seasonDateTextView = view.findViewById(R.id.powerplay_seasondate);
        seasonIdTextView = view.findViewById(R.id.powerplay_seasonid);

//        String id = "시즌 " + kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(0).toString();
//        String startTime = kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(0).getStartTime();
//        String endTime = kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(0).getEndTime();
//
//        seasonIdTextView.setText(id);
//        seasonDateTextView.setText(timeFormat(startTime) + " ~ " + timeFormat(endTime));

        try {
            String[] spinnerItem = new String[kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.size()];
            for (int i = 0; i < kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.size(); i++) {
                spinnerItem[i] = "시즌 " + kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(i).getId();
            }

            ArrayAdapter adapter = new ArrayAdapter(
                    getActivity().getApplicationContext(), R.layout.spin_button, spinnerItem
            );

            adapter.setDropDownViewResource(R.layout.spin_dropdown);
            SeasonsSpinner = view.findViewById(R.id.player_ranking_powerplay_select);

            try{
                Field popup = Spinner.class.getDeclaredField("mPopup");
                popup.setAccessible(true);

                ListPopupWindow window = (ListPopupWindow)popup.get(SeasonsSpinner);
                window.setHeight(70); //pixel
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            SeasonsSpinner.setAdapter(adapter);
            SeasonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    dialog.show();
                    SeasonId = Integer.toString(i + 56);

                    String startTime = kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(i).getStartTime();
                    String endTime = kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(i).getEndTime();

                    seasonIdTextView.setText("시즌 " + SeasonId);
                    seasonDateTextView.setText(timeFormat(startTime) + " ~ " + timeFormat(endTime));

                    try {
                        globalClick(player_ranking_powerplay_layout, dialog, SeasonId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

        globalButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                try {
                    globalClick(player_ranking_powerplay_layout, dialog, SeasonId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        MyButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
                myCountryClick(player_ranking_powerplay_layout, dialog, SeasonId);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.show();

        SeasonId = kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList.get(0).getId();
        try {
            globalClick(player_ranking_powerplay_layout, dialog, SeasonId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void globalClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog, String id) throws Exception{

        if(!kat_LoadBeforeMainActivity.PowerPlaySeasonRankingArrayList.containsKey(id)){
            kat_LoadBeforeMainActivity.client.RankingInit("global", id, "PowerPlay");
        }

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread("global", id);
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout,
                dialog, databaseChangeThread, id, "global");
        setUiOnMainView.start();
    }

    public void myCountryClick(LinearLayout player_ranking_player_layout, kat_LoadingDialog dialog, String id){

        String countryCode = kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode();

        if(!kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) ||
                (kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) &&
                        !kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(id))){
            kat_LoadBeforeMainActivity.client.RankingInit(
                    countryCode, id, "PowerPlay"
            );
        }

        DatabaseChangeThread databaseChangeThread = new DatabaseChangeThread(countryCode, id);
        databaseChangeThread.start();

        setUiOnMainView setUiOnMainView = new setUiOnMainView(player_ranking_player_layout,
                dialog, databaseChangeThread, id, countryCode);
        setUiOnMainView.start();

    }


    private class setUiOnMainView extends Thread{

        LinearLayout player_ranking_player_layout;
        kat_LoadingDialog dialog;
        DatabaseChangeThread databaseChangeThread;
        String id;
        String type;

        public setUiOnMainView(LinearLayout player_ranking_player_layout,
                               kat_LoadingDialog dialog,
                               DatabaseChangeThread databaseChangeThread,
                               String id, String type){
            this.player_ranking_player_layout = player_ranking_player_layout;
            this.dialog = dialog;
            this.databaseChangeThread = databaseChangeThread;
            this.id = id;
            this.type = type;
        }

        public void run(){

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    try {
                        databaseChangeThread.join();

                        if(type.equals("global")){
                            setView(player_ranking_player_layout,
                                    kat_LoadBeforeMainActivity.PowerPlaySeasonRankingArrayList.get(id),
                                    dialog);
                        }
                        else{
                            ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData> MyPowerPlayRankingArrayList
                                    = kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList
                                    .get(kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode())
                                    .get(id);
                            setView(player_ranking_player_layout, MyPowerPlayRankingArrayList, dialog);
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
        String id;

        public DatabaseChangeThread(String type, String id){
            this.type = type;
            this.id = id;
        }

        @Override
        public void run(){

            String countryCode = kat_LoadBeforeMainActivity.kataCountryBase.getCountryCode();

            while(true){

                if(type.equals("global")){
                    if(!kat_LoadBeforeMainActivity.PowerPlaySeasonRankingArrayList.containsKey(id)){
                        continue;
                    }
                    else break;
                }
                else{
                    if(!kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) ||
                            (kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) &&
                                    !kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(id))){
                        continue;
                    }
                    else break;
                }
            }
        }
    }


    public void setView(LinearLayout player_ranking_player_layout,
                        ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData> PowerPlayRankingArrayList,
                        kat_LoadingDialog dialog){

        LayoutInflater layoutInflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        player_ranking_player_layout.removeAllViews();

        for(int i = 0; i < PowerPlayRankingArrayList.size(); i++){

            View itemView = layoutInflater.inflate(R.layout.player_ranking_item, null);
            final kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData powerPlaySeasonRankingData
                    = PowerPlayRankingArrayList.get(i);

            ImageView player_ranking_player_image = itemView.findViewById(R.id.player_ranking_image);
            TextView player_ranking_player_name = itemView.findViewById(R.id.player_ranking_name);
            TextView player_ranking_player_tag = itemView.findViewById(R.id.player_ranking_tag);
            TextView player_ranking_player_club = itemView.findViewById(R.id.player_ranking_club);
            TextView player_ranking_player_trophies = itemView.findViewById(R.id.player_ranking_trophies);
            TextView player_ranking_player_rank = itemView.findViewById(R.id.player_Ranking_rank);

            GlideImage(kat_RankingFragment.PlayerImageUrl(powerPlaySeasonRankingData.getIconId()),
                    kat_RankingFragment.height / 15,
                    kat_RankingFragment.height / 15,
                    player_ranking_player_image);

            player_ranking_player_name.setText(powerPlaySeasonRankingData.getName());
            player_ranking_player_tag.setText(powerPlaySeasonRankingData.getTag());
            player_ranking_player_club.setText(powerPlaySeasonRankingData.getClubName());
            player_ranking_player_trophies.setText(powerPlaySeasonRankingData.getTrophies());
            player_ranking_player_rank.setText(powerPlaySeasonRankingData.getRank());

            String nameColor = "#ffffff";
            if(powerPlaySeasonRankingData.getNameColor() != null)
                nameColor = "#" + powerPlaySeasonRankingData.getNameColor().substring(2);
            player_ranking_player_name.setTextColor(Color.parseColor(nameColor));

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    kat_LoadingDialog dialog = new kat_LoadingDialog(getActivity());
                    dialog.show();

                    String realTag = powerPlaySeasonRankingData.getTag().substring(1);

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


    private String timeFormat(String time){
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);

        return year + "." + month + "." + day;
    }
}
