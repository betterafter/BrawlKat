package com.example.brawlkat.kat_Thread;

import android.os.Bundle;

import com.example.brawlkat.Client;
import com.example.brawlkat.kat_LoadBeforeMainActivity;
import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_eventsParser;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_GetStarlistDataThread extends AppCompatActivity {

    private                     kat_LoadBeforeMainActivity                  kat_loadBeforeMainActivity;
    private                     Client                                      client;

    private                     kat_eventsParser                            eventsParser;
    private                     kat_brawlersParser                          brawlersParser;

    public                      static ArrayList<kat_eventsParser.pair>     EventArrayList;
    public                      static ArrayList<HashMap<String, Object>>   BrawlersArrayList;

    public kat_GetStarlistDataThread(kat_LoadBeforeMainActivity kat_loadBeforeMainActivity){
        this.kat_loadBeforeMainActivity = kat_loadBeforeMainActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = kat_loadBeforeMainActivity.client;
    }

    // starlist.pro api 데이터 받고 저장
    public class GetStarListDataThread extends Thread{

        public void run(){

            try{
                while (true){

                    if(Client.resData == null) continue;
                    if(Client.resData.size() <= 0) continue;
                    ArrayList<String> dataSet = Client.resData;
                    if(dataSet.size() < 2) continue;

                    eventsParser = new kat_eventsParser(dataSet.get(0));
                    brawlersParser = new kat_brawlersParser(dataSet.get(1));

                    EventArrayList = eventsParser.DataParser();
                    BrawlersArrayList = brawlersParser.DataParser();

                    //eventsParser.testPrint(EventArrayList);

                    System.out.println("success to get data'\n");
                    int time = 1000 * 60;
                    sleep(time);
                }
            }
            catch (Exception e){
                if(e instanceof InterruptedException){
                    System.out.println("Interrupt Exception");
                }
                else System.out.println("fail");
            }
        }
    }

    public void init(){
        GetStarListDataThread getThread = new GetStarListDataThread();
        getThread.start();
    }

    public ArrayList<kat_eventsParser.pair> getEventArrayList(){
        return EventArrayList;
    }

    public ArrayList<HashMap<String, Object>> getBrawlersArrayList(){
        return BrawlersArrayList;
    }
}
