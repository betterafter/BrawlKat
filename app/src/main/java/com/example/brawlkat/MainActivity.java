package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class MainActivity extends AppCompatActivity {

    static String MyKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImVjNTI3ODA0LTMxNWUtNGU2OC1iYzU0LTkwYjVkNjRmNzdkOSIsImlhdCI6MTU5MjkyNjQxMSwic3ViIjoiZGV2ZWxvcGVyL2U2OTYzYTM0LTJhYjktZjI1Ny0yYWVlLTZhMTc2NmE5NTJiMSIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMjIyLjIzNy4zMy4yMzUiXSwidHlwZSI6ImNsaWVudCJ9XX0.FpXYTAJyDZkXJsGmEHTSL7g_C2RGhUnuhaRY6omtc_IIfQ8ZCxl7_Z5UQoCoX3GFTRxf2egsWAMvt6tNOfPt8g";
    static String MyBethanyKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjQ1MWMwOTQyLWJkYWEtNDYzNy04NzA2LTYxMzFiZDA1ZjhlNSIsImlhdCI6MTU4NjUwMTI5NCwic3ViIjoiZGV2ZWxvcGVyL2U2OTYzYTM0LTJhYjktZjI1Ny0yYWVlLTZhMTc2NmE5NTJiMSIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMTIxLjE0MS4xOTIuMjMwIl0sInR5cGUiOiJjbGllbnQifV19.Zxwtpe_jlqJaUWObRzSgwQfoW8kGXS7SntBrBhrfDRR98uqRws92nUKhVrikrUGXzZRGwnvReWnn0zJovunhrQ";
    static String MyMobileKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImUwNTRjOGJmLTcyYzktNDFiYy05Y2JhLTdjYTdkMDA0OTlkYiIsImlhdCI6MTU4Njc0MTg1OCwic3ViIjoiZGV2ZWxvcGVyL2U2OTYzYTM0LTJhYjktZjI1Ny0yYWVlLTZhMTc2NmE5NTJiMSIsInNjb3BlcyI6WyJicmF3bHN0YXJzIl0sImxpbWl0cyI6W3sidGllciI6ImRldmVsb3Blci9zaWx2ZXIiLCJ0eXBlIjoidGhyb3R0bGluZyJ9LHsiY2lkcnMiOlsiMjIzLjYyLjIxNi4zNyJdLCJ0eXBlIjoiY2xpZW50In1dfQ.ph0PqIVaYUpqsloGzMFb63qpV22m7EY5Y2Zj73iO8RMzImfo0oyyXEZZGBs_lWP1BMN8VfXYIXlPu0rfuzKOZA";
    static String playerInfoURL = "https://api.brawlstars.com/v1/players/";

    GetUserData getUserData;
    String playerTag = null;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserData = new GetUserData();
        textView = (TextView)findViewById(R.id.res);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    private class GetUserDataThread extends Thread {

        public void run(){
            String res = getUserData.ShowPlayerInformation(MyKey, playerInfoURL + "%23" + playerTag);
            textView.setText(res);
        }
    }



    public void onClick(View biew){
        AppCompatEditText EditText = (AppCompatEditText) findViewById(R.id.playerTag);
        playerTag = EditText.getText().toString();
        GetUserDataThread getUserDataThread = new GetUserDataThread();
        getUserDataThread.start();
    }

    public void onClientTestClick(View view){
        Intent intent = new Intent(getApplicationContext(), ClientTestActivity.class);
        startActivity(intent);
    }
}
