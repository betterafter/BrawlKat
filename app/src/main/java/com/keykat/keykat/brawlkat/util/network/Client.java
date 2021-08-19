package com.keykat.keykat.brawlkat.util.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.keykat.keykat.brawlkat.home.activity.kat_ExceptionActivity;
import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.GetTopPackageNameKt;
import com.keykat.keykat.brawlkat.util.kat_Data;
import com.keykat.keykat.brawlkat.util.parser.kat_brawlersParser;
import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_mapsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_BrawlerRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_ClubRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PlayerRankingParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_PowerPlaySeasonRankingParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    private                 Socket                          socket                  = null;

    private                 InputStream                     data;

    // data 배열 리스트 ...............................................................................
    public                  static ArrayList<String>        resData;
    public                  ArrayList<String>               resOffiData;
    public                  ArrayList<String>               resRankingData;
    // .............................................................................................

    public                  getApiThread                    getThread;
    public                  getAllTypeApiThread             officialApiThread;

    public                  boolean                         socketFail = false;
    public                  static boolean                  firstInit = false;

    public                  static boolean                  isGetApiThreadStop;

    private                 final String                    boundaryCode = "this_is_a_kat_data_boundary!";

    //private               String                          GCPIPADDRESS = "35.237.9.225";
    private                 final String                    ORACLEIPADDRESS = "193.122.98.86";

    public                  Context                         context;
    private                 int                             timeout = 10000;


    public Client(Context context){
        this.context = context;
    }


    // 플레이어 및 클럽 전적 검색 스레드
    // starlist.pro에서 가져옴
    // 한번만 통신
    public class getAllTypeApiThread extends Thread{

        private final String tag;
        private final String type;
        private final String apiType;
        Context context;

        public getAllTypeApiThread(String tag, String type, String apiType, Context context){
            this.tag = tag;
            this.type = type;
            this.apiType = apiType;
            this.context = context;
        }

        public void run(){

            try{

                while(true){

                    ConnectivityManager manager
                            = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                    if(networkInfo == null){
                        Intent errorIntent = new Intent(context, kat_ExceptionActivity.class);
                        errorIntent.putExtra("which", "kat_LoadBeforeMainActivity");
                        errorIntent.putExtra("cause", "error.INTERNET");
                        errorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(errorIntent);

                        break;
                    }

                    if(tag == null) continue;
                    SocketAddress socketAddress = new InetSocketAddress(ORACLEIPADDRESS, 9000);
                    Socket socket = new Socket();
                    socket.connect(socketAddress);

                    byte[] bytes;
                    String result = null;

                    // 데이터 보내기
                    // playerTag를 먼저 보냄.
                    if(apiType.equals("official"))
                        result = "%23" + tag;
                    else if(apiType.equals("nofficial"))
                        result = tag;
                    result = type + "/" + result + "/" + apiType;
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes(StandardCharsets.UTF_8);
                    os.write(bytes);
                    os.flush();

                    // os 를 flush한 후 데이터 종료 완료를 알리기 위해 개행문자를 보내 데이터 수신을 완료한다.
                    String end = "\n";
                    os.write(end.getBytes());
                    os.flush();

                    InputStream data = socket.getInputStream();
                    InputStreamReader input = new InputStreamReader(data);
                    BufferedReader reader = new BufferedReader(input);
                    result = reader.readLine();

                    int startidx = 0; int split;

                    // API 데이터 파싱
                    String splited;
                    resOffiData = new ArrayList<>();

                    while (true) {

                        split = result.indexOf(boundaryCode, startidx);

                        if(split == -1) break;
                        splited = result.substring(startidx, split);

                        resOffiData.add(splited);
                        startidx = split + boundaryCode.length();
                    }


                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    break;
                }
            }

            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // 랭킹 검색 스레드
    // starlist.pro에서 가져옴
    // 한번 통신
    public class getRankingApiThread extends Thread{

        String countryCode;
        String Id;
        String status;
        kat_LoadingDialog dialog;

        public getRankingApiThread(String countryCode, String Id, String status, kat_LoadingDialog dialog){
            this.countryCode = countryCode;
            this.Id = Id;
            this.status = status;
            this.dialog = dialog;
        }

        public getRankingApiThread(String countryCode, String Id, String status){
            this.countryCode = countryCode;
            this.Id = Id;
            this.status = status;
        }


        public void run(){

            try{

                while(true){

                    SocketAddress socketAddress = new InetSocketAddress(ORACLEIPADDRESS, 9000);
                    Socket socket = new Socket();
                    socket.connect(socketAddress);

                    byte[] bytes;
                    String result;

                    // 데이터 보내기
                    if(status.equals("PowerPlay"))
                        result = "rankings" + "/" + countryCode + "PowerPlay" + Id + "/" + "official";
                    else if(status.equals("Brawler"))
                        result = "rankings" + "/" + countryCode + Id + "/" + "official";
                    else
                        result = "rankings" + "/" + countryCode + "/" + "official";
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes(StandardCharsets.UTF_8);
                    os.write(bytes);
                    os.flush();

                    // os 를 flush한 후 데이터 종료 완료를 알리기 위해 개행문자를 보내 데이터 수신을 완료한다.
                    String end = "\n";
                    os.write(end.getBytes());
                    os.flush();

                    InputStream data = socket.getInputStream();
                    InputStreamReader input = new InputStreamReader(data);
                    BufferedReader reader = new BufferedReader(input);
                    result = reader.readLine();

                    int startidx = 0; int split = 0;

                    // API 데이터 파싱
                    String splited;
                    resRankingData = new ArrayList<>();


                    while (split != -1) {

                        split = result.indexOf(boundaryCode, startidx);

                        if(split == -1) break;
                        splited = result.substring(startidx, split);

                        resRankingData.add(splited);
                        startidx = split + boundaryCode.length();
                    }

                    // 파싱 할 부분 ...................................................................

                    if(status.equals("PowerPlay")){
                        kat_official_PowerPlaySeasonRankingParser powerPlaySeasonRankingParser;

                        powerPlaySeasonRankingParser = new kat_official_PowerPlaySeasonRankingParser(resRankingData.get(0));

                        if(countryCode.equals("global")) {
                            if (!kat_Data.PowerPlaySeasonRankingArrayList.containsKey(Id))
                                kat_Data.PowerPlaySeasonRankingArrayList.put(Id, powerPlaySeasonRankingParser.DataParser());
                        }
                        else {
                            if (kat_Data.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode)) {
                                if (!kat_Data.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(Id)) {
                                    kat_Data.MyPowerPlaySeasonRankingArrayList
                                            .get(countryCode)
                                            .put(Id, powerPlaySeasonRankingParser.DataParser());
                                }
                            }
                            else{
                                kat_Data.MyPowerPlaySeasonRankingArrayList.put(countryCode,
                                        new HashMap<String, ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>>());
                                kat_Data.MyPowerPlaySeasonRankingArrayList
                                        .get(countryCode)
                                        .put(Id, powerPlaySeasonRankingParser.DataParser());
                            }
                        }
                    }

                    else if(status.equals("Brawler")){
                        kat_official_BrawlerRankingParser brawlerRankingParser;

                        brawlerRankingParser = new kat_official_BrawlerRankingParser(resRankingData.get(0));

                        if(countryCode.equals("global")) {
                            if (!kat_Data.BrawlerRankingArrayList.containsKey(Id))
                                kat_Data.BrawlerRankingArrayList.put(Id, brawlerRankingParser.DataParser());
                        }
                        else{
                            if(kat_Data.MyBrawlerRankingArrayList.containsKey(countryCode)){
                                if(!kat_Data.MyBrawlerRankingArrayList.get(countryCode).containsKey(Id)) {
                                    kat_Data.MyBrawlerRankingArrayList
                                            .get(countryCode)
                                            .put(Id, brawlerRankingParser.DataParser());
                                }
                            }
                            else{
                                kat_Data.MyBrawlerRankingArrayList.put(countryCode,
                                        new HashMap<String, ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>>());

                                kat_Data.MyBrawlerRankingArrayList
                                        .get(countryCode)
                                        .put(Id, brawlerRankingParser.DataParser());
                            }
                        }
                    }

                    else{
                        kat_official_ClubRankingParser clubRankingParser;
                        kat_official_PlayerRankingParser playerRankingParser;
                        kat_official_PowerPlaySeasonParser powerPlaySeasonParser;

                        clubRankingParser = new kat_official_ClubRankingParser(resRankingData.get(0));
                        playerRankingParser = new kat_official_PlayerRankingParser(resRankingData.get(1));
                        powerPlaySeasonParser = new kat_official_PowerPlaySeasonParser(resRankingData.get(2));

                        if(countryCode.equals("global")) {
                            kat_Data.PlayerRankingArrayList = playerRankingParser.DataParser();
                            kat_Data.ClubRankingArrayList = clubRankingParser.DataParser();
                            kat_Data.PowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        }
                        else{
                            kat_Data.MyPlayerRankingArrayList = playerRankingParser.DataParser();
                            kat_Data.MyClubRankingArrayList = clubRankingParser.DataParser();
                            kat_Data.MyPowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        }
                    }

                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    if(dialog != null) dialog.dismiss();
                    break;
                }
            }

            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // 실시간 통신
    // 복합 데이터
    public class getApiThread extends Thread{

        public void run(){

            InputStreamReader input;
            BufferedReader reader;

            int time = 1000 * 60;
            try {
                while (!isGetApiThreadStop) {

                    // usageEvent를 이용하여 현재 앱이 실행 중인지를 확인하고, 실행 중이 아니라면 데이터를 가져오는 것을
                    // 못하게 막기
                    // 이를 위해 앱에 액세스 기능을 허용해야 하는데, 앱 시작할 때 허용할 수 있게 만들 것.
                    String currName = GetTopPackageNameKt.getTopPackageName(context);
                    if(!currName.equals("") && !currName.toLowerCase().contains("brawlkat")){
                        sleep(time);
                        continue;
                    }

                    SocketAddress socketAddress = new InetSocketAddress(ORACLEIPADDRESS, 9000);
                    Socket socket = new Socket();
                    socket.connect(socketAddress);

                    byte[] bytes;
                    String result;

                    // 데이터 보내기

                    // starlist api는 서버에 보낼 데이터가 없기 때문에 개행문자만을 보내 수신 종료한다.
                    result = "/" + "/" + "nofficial";
                    result += "\n";
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes(StandardCharsets.UTF_8);
                    os.write(bytes);
                    os.flush();

                    data = socket.getInputStream();
                    input = new InputStreamReader(data);
                    reader = new BufferedReader(input);

                    result = reader.readLine();

                    int startidx = 0; int split = 0;

                    // API 데이터 파싱
                    String splited;
                    resData = new ArrayList<>();

                    while (true) {

                        split = result.indexOf(boundaryCode, startidx);

                        if(split == -1) break;
                        splited = result.substring(startidx, split);
                        resData.add(splited);
                        startidx = split + boundaryCode.length();
                    }

                    kat_eventsParser eventsParser;
                    kat_brawlersParser brawlersParser;
                    kat_mapsParser mapsParser;


                    eventsParser = new kat_eventsParser(resData.get(0));
                    brawlersParser = new kat_brawlersParser(resData.get(1));
                    mapsParser = new kat_mapsParser(resData.get(2));

                    kat_Data.EventArrayList = eventsParser.DataParser();
                    kat_Data.BrawlersArrayList = brawlersParser.DataParser();
                    kat_Data.mapData = mapsParser.DataParser();

                    firstInit = true;
                    reader.close();

                    os.close();
                    socket.close();

                    sleep(time);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                try {
                    // 소켓 종료.
                    if(data != null) data.close();
                    if (socket != null) socket.close() ;
                }
                catch (Exception e){
                    // TODO : process exceptions.
                }
            }
        }
    }






    public void init(){

        isGetApiThreadStop = false;

        getThread = new getApiThread();
        getThread.start();
    }

    public void remove(){
        isGetApiThreadStop = true;
        getThread = null;
    }




    public boolean isGetApiThreadAlive(){
        if(getThread != null) return true;
        else return false;
    }




    public void AllTypeInit(String tag, String type, String apiType, Context context){

        resOffiData = new ArrayList<>();

        officialApiThread = new getAllTypeApiThread(tag, type, apiType, context);
        if(officialApiThread.getState() == Thread.State.NEW) officialApiThread.start();
    }

    public void RankingInit(String countryCode, String Id, String status, kat_LoadingDialog dialog){
        getRankingApiThread getRankingApiThread = new getRankingApiThread(countryCode, Id, status, dialog);
        getRankingApiThread.start();
    }

    public void RankingInit(String countryCode, String Id, String status){
        getRankingApiThread getRankingApiThread = new getRankingApiThread(countryCode, Id, status);
        getRankingApiThread.start();
    }

    public getRankingApiThread RankingResearch(String countryCode, String Id, String status){
        getRankingApiThread t = new getRankingApiThread(countryCode, Id, status);

        return t;
    }


    public ArrayList<String> getData() {
        return resData;
    }
    public ArrayList<String> getAllTypeData() {
        return resOffiData;
    }

    public getAllTypeApiThread apiThread(){
        return officialApiThread;
    }
}
