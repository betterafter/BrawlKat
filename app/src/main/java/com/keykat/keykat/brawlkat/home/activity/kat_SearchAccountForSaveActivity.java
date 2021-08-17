package com.keykat.keykat.brawlkat.home.activity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.keykat.keykat.brawlkat.R;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.network.kat_SearchThread;
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
        AccountInputEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    onSearchClick();
                }
                else if(i == KeyEvent.KEYCODE_BACK){
                    onBackPressed();
                }
                return true;
            }
        });

        searchButton = findViewById(R.id.search_account_for_save_click);
        searchButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    onSearchClick();
                }
                return false;
            }
        });
    }


    public void onSearchClick(){

        kat_LoadingDialog dialog = new kat_LoadingDialog(this);
        dialog.show();

        kat_SearchThread kset = new kat_SearchThread(kat_SearchAccountForSaveActivity.this,
                kat_Player_MainActivity.class, dialog);
        kset.SearchStart(AccountInputEditText.getText().toString(), "players", getApplicationContext());

        AccountInputEditText.setText("");
    }
}
