package com.example.brawlkat;

import com.example.brawlkat.kat_dataparser.kat_brawlersParser;
import com.example.brawlkat.kat_dataparser.kat_eventsParser;
import com.example.brawlkat.kat_dataparser.kat_mapsParser;
import com.example.brawlkat.kat_dataparser.kat_official_BrawlerRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_ClubRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_PlayerRankingParser;
import com.example.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonParser;
import com.example.brawlkat.kat_dataparser.kat_official_PowerPlaySeasonRankingParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {

    private                 Socket                          socket                  = null;

    private                 InputStream                     data;
    private                 OutputStream                    tagdata;

    // data 배열 리스트 ...............................................................................
    public                  static ArrayList<String>        resData;
    public                  ArrayList<String>               resOffiData;
    public                  ArrayList<String>               resRankingData;
    public                  ArrayList<String>               resPowerPlayOrBrawlerRankingData;
    // .............................................................................................

    public                  getApiThread                    getThread;
    public                  kat_Service_OverdrawActivity    kat_Service_overdrawActivity;
    public                  getAllTypeApiThread             officialApiThread;

    public                  boolean                         socketFail = false;
    public                  boolean                         firstInit = false;

    private                 firstInitThread                 firstInitThread;
    private                 getRankingApiThread             getRankingApiThread;


    public Client(){

    }

    public Client(kat_Service_OverdrawActivity kat_Service_overdrawActivity){
        this.kat_Service_overdrawActivity = kat_Service_overdrawActivity;
    }

    // 플레이어 및 클럽 전적 검색 스레드
    public class getAllTypeApiThread extends Thread{

        private String tag;
        private String type;
        private String apiType;

        public getAllTypeApiThread(String tag, String type, String apiType){
            this.tag = tag;
            this.type = type;
            this.apiType = apiType;
        }

        public void run(){

            try{

                while(true){

                    if(tag == null) continue;
                    Socket socket = new Socket("35.237.9.225", 9000);

                    byte[] bytes = null;
                    String result = null;

                    // 데이터 보내기

                    // playerTag를 먼저 보냄.
                    if(apiType.equals("official"))
                        result = "%23" + tag;
                    else if(apiType.equals("nofficial"))
                        result = tag;
                    result = type + "/" + result + "/" + apiType;
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes("UTF-8");
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
                    resOffiData = new ArrayList<>();

                    while (split != -1) {

                        split = result.indexOf("}{", startidx);

                        if (split == -1) splited = result.substring(startidx);
                        else splited = result.substring(startidx, split + 1);

                        resOffiData.add(splited);
                        System.out.println(splited);
                        startidx = split + 1;
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
                if(e instanceof SocketTimeoutException){
                    socketFail = true;
                }
            }
        }
    }

    // 랭킹 검색 스레드
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

                    System.out.println("ranking thread start");

                    Socket socket = new Socket("35.237.9.225", 9000);

                    byte[] bytes = null;
                    String result = null;

                    // 데이터 보내기
                    if(status.equals("PowerPlay"))
                        result = "rankings" + "/" + countryCode + "PowerPlay" + Id + "/" + "official";
                    else if(status.equals("Brawler"))
                        result = "rankings" + "/" + countryCode + Id + "/" + "official";
                    else
                        result = "rankings" + "/" + countryCode + "/" + "official";
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes("UTF-8");
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

                        split = result.indexOf("}{", startidx);

                        if (split == -1) splited = result.substring(startidx);
                        else splited = result.substring(startidx, split + 1);

                        System.out.println(splited);
                        resRankingData.add(splited);
                        startidx = split + 1;
                    }

                    // 파싱 할 부분 ...................................................................

                    if(status.equals("PowerPlay")){
                        kat_official_PowerPlaySeasonRankingParser powerPlaySeasonRankingParser;

                        powerPlaySeasonRankingParser = new kat_official_PowerPlaySeasonRankingParser(resRankingData.get(0));

                        if(countryCode.equals("global")) {
                            if (!kat_LoadBeforeMainActivity.PowerPlaySeasonRankingArrayList.containsKey(Id))
                                kat_LoadBeforeMainActivity.PowerPlaySeasonRankingArrayList.put(Id, powerPlaySeasonRankingParser.DataParser());
                        }
                        else {
                            if (kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode)) {
                                if (!kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(Id)) {
                                    kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList
                                            .get(countryCode)
                                            .put(Id, powerPlaySeasonRankingParser.DataParser());
                                }
                            }
                            else{
                                kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList.put(countryCode,
                                        new HashMap<String, ArrayList<kat_official_PowerPlaySeasonRankingParser.powerPlaySeasonRankingData>>());
                                kat_LoadBeforeMainActivity.MyPowerPlaySeasonRankingArrayList
                                        .get(countryCode)
                                        .put(Id, powerPlaySeasonRankingParser.DataParser());
                            }
                        }
                    }

                    else if(status.equals("Brawler")){
                        kat_official_BrawlerRankingParser brawlerRankingParser;

                        brawlerRankingParser = new kat_official_BrawlerRankingParser(resRankingData.get(0));

                        if(countryCode.equals("global")) {
                            if (!kat_LoadBeforeMainActivity.BrawlerRankingArrayList.containsKey(Id))
                                kat_LoadBeforeMainActivity.BrawlerRankingArrayList.put(Id, brawlerRankingParser.DataParser());
                        }
                        else{
                            if(kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.containsKey(countryCode)){
                                if(!kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.get(countryCode).containsKey(Id)) {
                                    kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                                            .get(countryCode)
                                            .put(Id, brawlerRankingParser.DataParser());
                                }
                            }
                            else{
                                kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList.put(countryCode,
                                        new HashMap<String, ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>>());

                                kat_LoadBeforeMainActivity.MyBrawlerRankingArrayList
                                        .get(countryCode)
                                        .put(Id, brawlerRankingParser.DataParser());
                            }
                        }

                        //else
                        //    if()
                        // 이렇게 적으니까 else if로 되어버림...
                    }

                    else{
                        kat_official_ClubRankingParser clubRankingParser;
                        kat_official_PlayerRankingParser playerRankingParser;
                        kat_official_PowerPlaySeasonParser powerPlaySeasonParser;

                        clubRankingParser = new kat_official_ClubRankingParser(resRankingData.get(0));
                        playerRankingParser = new kat_official_PlayerRankingParser(resRankingData.get(1));
                        powerPlaySeasonParser = new kat_official_PowerPlaySeasonParser(resRankingData.get(2));

                        if(countryCode.equals("global")) {
                            kat_LoadBeforeMainActivity.PlayerRankingArrayList = playerRankingParser.DataParser();
                            kat_LoadBeforeMainActivity.ClubRankingArrayList = clubRankingParser.DataParser();
                            kat_LoadBeforeMainActivity.PowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        }
                        else{
                            kat_LoadBeforeMainActivity.MyPlayerRankingArrayList = playerRankingParser.DataParser();
                            kat_LoadBeforeMainActivity.MyClubRankingArrayList = clubRankingParser.DataParser();
                            kat_LoadBeforeMainActivity.MyPowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        }
                    }

                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    if(dialog != null) dialog.dismiss();
                    System.out.println("ranking Thread finish");
                    break;
                }
            }

            catch (Exception e){
                e.printStackTrace();
                if(e instanceof SocketTimeoutException){
                    socketFail = true;
                }
            }
        }

    }

    public class getApiThread extends Thread{

        public void run(){

            OutputStream sendData = null;
            PrintWriter bw = null;
            InputStreamReader input = null;
            BufferedReader reader = null;

            try {
                while (true) {

                    socket = new Socket("35.237.9.225", 9000);

                    byte[] bytes = null;
                    String result = null;

                    // 데이터 보내기

                    // starlist api는 서버에 보낼 데이터가 없기 때문에 개행문자만을 보내 수신 종료한다.
                    result = "/" + "/" + "nofficial";
                    result += "\n";
                    OutputStream os = socket.getOutputStream();
                    bytes = result.getBytes("UTF-8");
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
                    while (split != -1) {

                        split = result.indexOf("}{", startidx);

                        if (split == -1) splited = result.substring(startidx);
                        else splited = result.substring(startidx, split + 1);

                        resData.add(splited);
                        startidx = split + 1;
                    }

                    kat_eventsParser eventsParser;
                    kat_brawlersParser brawlersParser;
                    kat_mapsParser mapsParser;

                    eventsParser = new kat_eventsParser(resData.get(0));
                    brawlersParser = new kat_brawlersParser(resData.get(1));
                    mapsParser = new kat_mapsParser(resData.get(2));

                    kat_LoadBeforeMainActivity.EventArrayList = eventsParser.DataParser();
                    kat_LoadBeforeMainActivity.BrawlersArrayList = brawlersParser.DataParser();
                    kat_LoadBeforeMainActivity.mapData = mapsParser.DataParser();


                    System.out.println("mapdata size : " + kat_LoadBeforeMainActivity.mapData.size());

                    firstInit = true;
                    reader.close();

                    os.close();
                    socket.close();

                    int time = 1000 * 60;
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

    public class firstInitThread extends Thread{
        public void run(){
            while(true){
                System.out.println("Client/firstInitThread/ : waiting...");
                if(firstInit){
                    break;
                }
            }
        }
    }



    public void init(){
        getThread = new getApiThread();
        if(!getThread.isAlive()) getThread.start();

        firstInitThread = new firstInitThread();
        firstInitThread.start();
    }

    public firstInitThread getFirstInitThread(){
        return firstInitThread;
    }


    public void AllTypeInit(String tag, String type, String apiType){

        resOffiData = new ArrayList<>();

        officialApiThread = new getAllTypeApiThread(tag, type, apiType);
        if(!officialApiThread.isAlive()) officialApiThread.start();
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
    public getRankingApiThread getRankingApiThread() { return getRankingApiThread; }
}
