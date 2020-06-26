package com.example.brawlkat;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ClientTestActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_test);

        textView = (TextView)findViewById(R.id.result);

        GetUserDataThread getUserDataThread = new GetUserDataThread();
        getUserDataThread.start();
    }

    private class GetUserDataThread extends Thread {

        public void run(){
            Client client = new Client();
            textView.setText(client.clientTest());
        }
    }
}
