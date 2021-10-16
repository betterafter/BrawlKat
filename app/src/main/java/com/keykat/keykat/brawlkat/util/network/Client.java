package com.keykat.keykat.brawlkat.util.network;

import android.content.Context;

import com.keykat.keykat.brawlkat.home.util.kat_LoadingDialog;
import com.keykat.keykat.brawlkat.util.KatData;
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

    private InputStream data;

    // data 배열 리스트 ...............................................................................
    public static ArrayList<String> resData;
    public ArrayList<String> resOffiData;
    public ArrayList<String> resRankingData;
    // .............................................................................................

    public BaseApiDataThread getThread;
    public getAllTypeApiThread officialApiThread;

    public static boolean isGetApiThreadStop;

    private final String boundaryCode = "this_is_a_kat_data_boundary!";

    //private               String                          GCPIPADDRESS = "35.237.9.225";
    private final String ORACLEIPADDRESS = "193.122.98.86";

    private final Context context;


    public Client(Context context) {
        this.context = context;
    }


    // 플레이어 및 클럽을 검색할 때 작동하는 스레드. 앱을 실행할 때 저장한 유저 정보가 있다면 불러오고 그렇지 않으면 굳이 불러올 필요가 없음.
    // 이후 필요할 때마다 발동시키면 됨.
    public class getAllTypeApiThread extends Thread {

        private final String tag;
        private final String type;
        private final String apiType;
        Context context;

        public getAllTypeApiThread(String tag, String type, String apiType, Context context) {
            this.tag = tag;
            this.type = type;
            this.apiType = apiType;
            this.context = context;
        }

        public void run() {

            try {

                while (true) {
                    if (tag == null) continue;
                    SocketAddress socketAddress = new InetSocketAddress(ORACLEIPADDRESS, 9000);
                    Socket socket = new Socket();
                    socket.connect(socketAddress);

                    byte[] bytes;
                    String result = null;

                    // 데이터 보내기
                    // playerTag를 먼저 보냄.
                    if (apiType.equals("official"))
                        result = "%23" + tag;
                    else if (apiType.equals("nofficial"))
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

                    int startidx = 0;
                    int split;

                    // API 데이터 파싱
                    String splited;
                    resOffiData = new ArrayList<>();

                    while (true) {
                        split = result.indexOf(boundaryCode, startidx);

                        if (split == -1) break;
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
            } catch (Exception e) {
                KatData.serverProblemDialog();
                e.printStackTrace();
            }
        }
    }


    // brawlify에서 가져오는 랭킹 데이터로 매번 업데이트할 필요가 없음. 어차피 랭킹이 그렇게 자주 바뀌는 시스템이 아니기 때문.
    // 앱을 실행할 때 맨 처음에 한번만 불러오거나 필요할 때만 불러오게 하면 됨.
    public class getRankingApiThread extends Thread {

        String countryCode;
        String Id;
        String status;
        kat_LoadingDialog dialog;

        public getRankingApiThread(String countryCode, String Id, String status, kat_LoadingDialog dialog) {
            this.countryCode = countryCode;
            this.Id = Id;
            this.status = status;
            this.dialog = dialog;
        }

        public getRankingApiThread(String countryCode, String Id, String status) {
            this.countryCode = countryCode;
            this.Id = Id;
            this.status = status;
        }


        public void run() {

            try {

                while (true) {

                    SocketAddress socketAddress = new InetSocketAddress(ORACLEIPADDRESS, 9000);
                    Socket socket = new Socket();
                    socket.connect(socketAddress);

                    byte[] bytes;
                    String result;

                    // 데이터 보내기
                    if (status.equals("PowerPlay"))
                        result = "rankings" + "/" + countryCode + "PowerPlay" + Id + "/" + "official";
                    else if (status.equals("Brawler"))
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

                    int startidx = 0;
                    int split = 0;

                    // API 데이터 파싱
                    String splited;
                    resRankingData = new ArrayList<>();


                    while (split != -1) {

                        split = result.indexOf(boundaryCode, startidx);

                        if (split == -1) break;
                        splited = result.substring(startidx, split);

                        resRankingData.add(splited);
                        startidx = split + boundaryCode.length();
                    }

                    // 파싱 할 부분 ...................................................................

                    if (status.equals("PowerPlay")) {
                        kat_official_PowerPlaySeasonRankingParser powerPlaySeasonRankingParser;

                        powerPlaySeasonRankingParser = new kat_official_PowerPlaySeasonRankingParser(resRankingData.get(0));

                        if (countryCode.equals("global")) {
                            if (!KatData.PowerPlaySeasonRankingArrayList.containsKey(Id))
                                KatData.PowerPlaySeasonRankingArrayList.put(Id, powerPlaySeasonRankingParser.DataParser());
                        } else {
                            if (KatData.MyPowerPlaySeasonRankingArrayList.containsKey(countryCode)) {
                                if (!KatData.MyPowerPlaySeasonRankingArrayList.get(countryCode).containsKey(Id)) {
                                    KatData.MyPowerPlaySeasonRankingArrayList
                                            .get(countryCode)
                                            .put(Id, powerPlaySeasonRankingParser.DataParser());
                                }
                            } else {
                                KatData.MyPowerPlaySeasonRankingArrayList.put(countryCode,
                                        new HashMap<>());
                                KatData.MyPowerPlaySeasonRankingArrayList
                                        .get(countryCode)
                                        .put(Id, powerPlaySeasonRankingParser.DataParser());
                            }
                        }
                    } else if (status.equals("Brawler")) {
                        kat_official_BrawlerRankingParser brawlerRankingParser;

                        brawlerRankingParser = new kat_official_BrawlerRankingParser(resRankingData.get(0));

                        if (countryCode.equals("global")) {
                            if (!KatData.BrawlerRankingArrayList.containsKey(Id))
                                KatData.BrawlerRankingArrayList.put(Id, brawlerRankingParser.DataParser());
                        } else {
                            if (KatData.MyBrawlerRankingArrayList.containsKey(countryCode)) {
                                if (!KatData.MyBrawlerRankingArrayList.get(countryCode).containsKey(Id)) {
                                    KatData.MyBrawlerRankingArrayList
                                            .get(countryCode)
                                            .put(Id, brawlerRankingParser.DataParser());
                                }
                            } else {
                                KatData.MyBrawlerRankingArrayList.put(countryCode,
                                        new HashMap<String, ArrayList<kat_official_BrawlerRankingParser.brawlerRankingData>>());

                                KatData.MyBrawlerRankingArrayList
                                        .get(countryCode)
                                        .put(Id, brawlerRankingParser.DataParser());
                            }
                        }
                    } else {
                        kat_official_ClubRankingParser clubRankingParser;
                        kat_official_PlayerRankingParser playerRankingParser;
                        kat_official_PowerPlaySeasonParser powerPlaySeasonParser;

                        clubRankingParser = new kat_official_ClubRankingParser(resRankingData.get(0));
                        playerRankingParser = new kat_official_PlayerRankingParser(resRankingData.get(1));
                        powerPlaySeasonParser = new kat_official_PowerPlaySeasonParser(resRankingData.get(2));

                        if (countryCode.equals("global")) {
                            KatData.PlayerRankingArrayList = playerRankingParser.DataParser();
                            KatData.ClubRankingArrayList = clubRankingParser.DataParser();
                            KatData.PowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        } else {
                            KatData.MyPlayerRankingArrayList = playerRankingParser.DataParser();
                            KatData.MyClubRankingArrayList = clubRankingParser.DataParser();
                            KatData.MyPowerPlaySeasonArrayList = powerPlaySeasonParser.DataParser();
                        }
                    }

                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    if (dialog != null) dialog.dismiss();
                    break;
                }
            } catch (Exception e) {
                KatData.serverProblemDialog();
                e.printStackTrace();
            }
        }
    }


    public void init() {

        if(getThread == null)
            getThread = new BaseApiDataThread(context);

        getThread.start();
    }

    public void remove() throws InterruptedException {
        getThread.interrupt();
    }

    public boolean isGetApiThreadAlive() {
        return getThread != null;
    }


    public void AllTypeInit(String tag, String type, String apiType, Context context) {

        resOffiData = new ArrayList<>();

        officialApiThread = new getAllTypeApiThread(tag, type, apiType, context);
        if (officialApiThread.getState() == Thread.State.NEW) officialApiThread.start();
    }

    public void RankingInit(String countryCode, String Id, String status) {
        getRankingApiThread getRankingApiThread = new getRankingApiThread(countryCode, Id, status);
        getRankingApiThread.start();
    }

    public ArrayList<String> getData() {
        return resData;
    }

    public ArrayList<String> getAllTypeData() {
        return resOffiData;
    }

    public getAllTypeApiThread apiThread() {
        return officialApiThread;
    }
}
