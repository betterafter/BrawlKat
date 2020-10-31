package com.example.brawlkat;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_SearchAccountForSaveActivity extends AppCompatActivity {

    TextInputEditText AccountInputEditText;
    Button searchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_search_account_for_save_activity);

        AccountInputEditText = findViewById(R.id.account_input_edit_text);
        AccountInputEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        searchButton = findViewById(R.id.search_account_for_save_click);
        searchButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                onSearchClick();
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onSearchClick(){
        kat_SearchThread kset = new kat_SearchThread(this, kat_Player_MainActivity.class);
        kset.SearchStart(AccountInputEditText.getText().toString(), "players");
    }

}
