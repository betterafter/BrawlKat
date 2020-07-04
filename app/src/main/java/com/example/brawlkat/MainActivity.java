package com.example.brawlkat;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class MainActivity extends AppCompatActivity {

    SendUserTagToServer userTagToServer;
    GetUserData getUserData;
    Client client;

    String playerTag = null;
    TextView textView;

    String data = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new Client();
        getUserData = new GetUserData();
        textView = (TextView)findViewById(R.id.res);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    private class SendUserTagToServer extends Thread {

        public void run(){

            data = client.clientTest(playerTag);
            System.out.println(data);
            textView.setText(getUserData.ShowPlayerInformation(data));
        }
    }



    public void onClick(View view){
        AppCompatEditText EditText = (AppCompatEditText) findViewById(R.id.playerTag);
        playerTag = EditText.getText().toString();

        userTagToServer = new SendUserTagToServer();
        if(!userTagToServer.isAlive()) {
            userTagToServer.start();
        }
    }
}
