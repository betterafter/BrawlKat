package com.keykat.keykat.brawlkat.search.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.search.result.club.activity.kat_Player_ClubDetailActivity;
import com.keykat.keykat.brawlkat.search.result.player.activity.kat_Player_PlayerDetailActivity;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.keykat.keykat.brawlkat.common.UtilsKt.idChecker;

public class kat_Player_RecentSearchActivity extends AppCompatActivity {

    private TextInputEditText userAndClubSearchEditText;
    private String type;

    private kat_LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_player_club_search);

        dialog = new kat_LoadingDialog(this);

        type = getIntent().getStringExtra("type");

        userAndClubSearchEditText = findViewById(R.id.player_detail_user_club_searchInput);
        userAndClubSearchEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        userAndClubSearchEditText.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                onUserClubSearchClick(view);
            } else if (i == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
            }
            return true;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recentSearchUpdate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KatData.currentActivity = this;
    }

    // 최근 전적 검색 기록 업데이트
    public void recentSearchUpdate() {

        LinearLayout linearLayout = findViewById(R.id.player_detail_recent_search_layout);
        linearLayout.removeAllViews();
        if (KatData.katabase != null) {
            ArrayList<ArrayList<String>> resultList = KatData.katabase.get(type);

            for (int i = 0; i < 9; i++) {

                if (i >= resultList.size()) break;
                recentSearchResultList(linearLayout, getLayoutInflater(), resultList.get(i));
            }
        }
    }

    // 버튼을 클릭했을 때 최근 전적 기록을 갱신함.
    private void recentSearchResultList(LinearLayout linearLayout, LayoutInflater layoutInflater, ArrayList<String> data) {

        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.player_search_list_item, null);

        TextView tagText = view.findViewById(R.id.player_search_tag);
        TextView nameText = view.findViewById(R.id.player_search_name);

        tagText.setOnClickListener(this::listClick);

        nameText.setOnClickListener(this::listClick);


        tagText.setText(data.get(1));
        nameText.setText(data.get(2));

        linearLayout.addView(view);
    }

    // 전적 검색 클릭
    public void onUserClubSearchClick(View view) {
        String text = Objects.requireNonNull(userAndClubSearchEditText.getText()).toString();
        if (!idChecker(text)) {
            Toast.makeText(this, getString(R.string.account_save_error_text), Toast.LENGTH_SHORT).show();
            userAndClubSearchEditText.setText("");
            return;
        }

        dialog.show();

        kat_SearchThread searchThread = new kat_SearchThread(kat_Player_RecentSearchActivity.this,
                kat_Player_PlayerDetailActivity.class, dialog);

        searchThread.SearchStart(text, type, getApplicationContext());

        userAndClubSearchEditText.setText("");
    }


    // 리스트를 터치했을 때 발생 함수
    private void listClick(View view) {

        dialog.show();

        LinearLayout linearLayout = (LinearLayout) view.getParent();
        String RawTag = ((TextView) linearLayout.getChildAt(1)).getText().toString();
        String newTag = RawTag.substring(1);

        if (type.equals("players")) {
            kat_SearchThread kset = new kat_SearchThread(kat_Player_RecentSearchActivity.this,
                    kat_Player_PlayerDetailActivity.class, dialog);
            kset.SearchStart(newTag, type, getApplicationContext());
        } else {
            kat_SearchThread kset = new kat_SearchThread(kat_Player_RecentSearchActivity.this,
                    kat_Player_ClubDetailActivity.class, dialog);
            kset.SearchStart(newTag, type, getApplicationContext());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        dialog.dismiss();
    }
}
