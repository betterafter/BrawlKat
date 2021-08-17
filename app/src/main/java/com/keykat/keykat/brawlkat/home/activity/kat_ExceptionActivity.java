package com.keykat.keykat.brawlkat.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.keykat.keykat.brawlkat.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_ExceptionActivity extends AppCompatActivity {

    String which = "";
    String cause = "";

    TextView errorText;
    Button errorButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exception_activity);

        errorText = findViewById(R.id.error_text);
        errorButton = findViewById(R.id.error_button);

        Intent intent = getIntent();
        which = intent.getStringExtra("which");
        cause =  intent.getStringExtra("cause");

        if(which != null && cause != null) {
            if (which.equals("kat_LoadBeforeMainActivity") && cause.equals("error.INTERNET")) {
                errorText.setText("인터넷에 연결되어 있지 않습니다. 다시 시도해보세요.");
            }
        }

    }

    public void onErrorCheckClick(View view){
        finish();
    }
}
