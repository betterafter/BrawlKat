package com.keykat.keykat.brawlkat.home.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.KatData;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.keykat.keykat.brawlkat.common.UtilsKt.idChecker;

public class kat_SearchAccountForSaveActivity extends AppCompatActivity {

    TextInputEditText AccountInputEditText;
    Button searchButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_search_account_for_save_activity);

        AccountInputEditText = findViewById(R.id.account_input_edit_text);
        AccountInputEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        AccountInputEditText.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                onSearchClick();
            } else if (i == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
            }
            return true;
        });

        searchButton = findViewById(R.id.search_account_for_save_click);
        searchButton.setOnTouchListener((v, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                onSearchClick();
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        KatData.currentActivity = this;
    }

    public void onSearchClick() {
        String text = Objects.requireNonNull(AccountInputEditText.getText()).toString();
        if (!idChecker(text)) {
            Toast.makeText(this, getString(R.string.account_save_error_text), Toast.LENGTH_SHORT).show();
            return;
        }

        kat_LoadingDialog dialog = new kat_LoadingDialog(this);
        dialog.show();

        kat_SearchThread kset = new kat_SearchThread(kat_SearchAccountForSaveActivity.this,
                kat_Player_MainActivity.class, dialog);
        kset.SearchStart(
                text,
                "players",
                getApplicationContext()
        );

        AccountInputEditText.setText("");
    }
}
