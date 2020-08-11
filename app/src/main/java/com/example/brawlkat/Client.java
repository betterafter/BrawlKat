package com.example.brawlkat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    private                 ArrayList<Socket>           sockets                 = new ArrayList<>();
    private                 Socket                      socket                  = null;
    private                 InputStream                 data;
    private                 ArrayList<String>           resData                 = new ArrayList<>();
    public                  getApiThread                getThread;
    public                  kat_OverdrawActivity        kat_overdrawActivity;


    public Client(kat_OverdrawActivity kat_overdrawActivity){
        this.kat_overdrawActivity = kat_overdrawActivity;
    }


    private class getApiThread extends Thread{

        public void run(){

            try {
                while (true) {

                    if(kat_overdrawActivity.programDeactivate) break;

                    socket = new Socket("35.237.9.225", 9000);

                    data = socket.getInputStream();

                    InputStreamReader input = new InputStreamReader(data);
                    BufferedReader reader = new BufferedReader(input);
                    String result = reader.readLine();

                    int startidx = 0; int split = 0;

                    // API 데이터 파싱
                    String splited;
                    while (split != -1) {

                        split = result.indexOf("}{", startidx);

                        if (split == -1) splited = result.substring(startidx);
                        else splited = result.substring(startidx, split + 1);

                        resData.add(splited);
                        startidx = split + 1;
                    }

                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    int time = 1000 * 60 * 5;
                    sleep(time);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                // 소켓 종료.
                if(data != null) data.close();
                if (socket != null) socket.close() ;
            }
            catch (Exception e) {
                // TODO : process exceptions.
            }
        }
    }

    public void init(){
        getThread = new getApiThread();
        if(!getThread.isAlive()) getThread.start();
    }



    public ArrayList<String> getdata(String... send) {
        return resData;
    }

}
