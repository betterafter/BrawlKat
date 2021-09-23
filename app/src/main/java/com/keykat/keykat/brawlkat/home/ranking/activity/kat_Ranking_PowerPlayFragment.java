package com.keykat.keykat.brawlkat.home.ranking.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.AsyncCoroutine;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class kat_Ranking_PowerPlayFragment extends Fragment {

    private LinearLayout player_ranking_powerplay_layout;
    private TextView seasonIdTextView;
    private TextView seasonDateTextView;
    private Spinner SeasonsSpinner;
    private String SeasonId;

    public kat_Ranking_PowerPlayFragment() {
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view
                = inflater.inflate(R.layout.player_ranking_powerplay, container, false);
        player_ranking_powerplay_layout = view.findViewById(R.id.player_ranking_powerplay_layout);

        final Button globalButton = view.findViewById(R.id.player_ranking_powerplay_global);
        final Button MyButton = view.findViewById(R.id.player_ranking_powerplay_mycountry);

        seasonDateTextView = view.findViewById(R.id.powerplay_seasondate);
        seasonIdTextView = view.findViewById(R.id.powerplay_seasonid);

        try {
            String[] spinnerItem = new String[KatData.PowerPlaySeasonArrayList.size()];
            for (int i = 0; i < KatData.PowerPlaySeasonArrayList.size(); i++) {
                spinnerItem[i] = "시즌 " + KatData.PowerPlaySeasonArrayList.get(i).getId();
            }

            ArrayAdapter adapter = new ArrayAdapter(
                    getActivity().getApplicationContext(), R.layout.spin_button, spinnerItem
            );

            adapter.setDropDownViewResource(R.layout.spin_dropdown);
            SeasonsSpinner = view.findViewById(R.id.player_ranking_powerplay_select);

            try {
                Field popup = Spinner.class.getDeclaredField("mPopup");
                popup.setAccessible(true);

                ListPopupWindow window = (ListPopupWindow) popup.get(SeasonsSpinner);
                window.setHeight(70); //pixel
            } catch (Exception e) {
                e.printStackTrace();
            }

            SeasonsSpinner.setAdapter(adapter);
            SeasonsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SeasonId = Integer.toString(i + 56);

                    String startTime = KatData.PowerPlaySeasonArrayList.get(i).getStartTime();
                    String endTime = KatData.PowerPlaySeasonArrayList.get(i).getEndTime();

                    seasonIdTextView.setText("시즌 " + SeasonId);
                    seasonDateTextView.setText(timeFormat(startTime) + " ~ " + timeFormat(endTime));

                    try {
                        globalClick(player_ranking_powerplay_layout, SeasonId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        globalButton.setOnClickListener(v -> {
            try {
                globalClick(player_ranking_powerplay_layout, SeasonId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        MyButton.setOnClickListener(v -> {
            myCountryClick(player_ranking_powerplay_layout, SeasonId);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        SeasonId = KatData.PowerPlaySeasonArrayList.get(0).getId();
        try {
            globalClick(player_ranking_powerplay_layout, SeasonId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void globalClick(LinearLayout player_ranking_powerplay_layout, String id) {

        KatData.dialog.show();
        if (!KatData.PowerPlaySeasonRankingArrayList.containsKey(id)) {
            KatData.client.RankingInit("global", id, "PowerPlay");
        }

        AsyncCoroutine.Companion.powerPlayGlobalDatabaseChanged(
                requireActivity(),
                player_ranking_powerplay_layout,
                id
        );
    }

    public void myCountryClick(LinearLayout player_ranking_powerplay_layout, String id) {

        String countryCode = KatData.kataCountryBase.getCountryCode();
        if (!KatData.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) ||
                (KatData.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode) &&
                        !KatData.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(id))) {

            KatData.client.RankingInit(countryCode, id, "PowerPlay");
        }

        KatData.dialog.show();
        AsyncCoroutine.Companion.powerPlayDatabaseChanged(
                requireActivity(),
                player_ranking_powerplay_layout,
                id
        );
    }


    private String timeFormat(String time) {
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);

        return year + "." + month + "." + day;
    }
}
