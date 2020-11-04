package com.example.brawlkat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Client {

    private                 Socket                          socket                  = null;
    private                 InputStream                     data;
    private                 OutputStream                    tagdata;
    public                  static ArrayList<String>        resData                 = new ArrayList<>();
    public                  static ArrayList<String>        resOffiData             = new ArrayList<>();
    public                  getApiThread                    getThread;
    public                  kat_Service_OverdrawActivity    kat_Service_overdrawActivity;
    public                  getAllTypeApiThread             officialApiThread;
    public                  boolean                         workDone = false;
    public                  boolean                         socketFail = false;

    public                  static workThread               workThread;


    public Client(){

    }

    public Client(kat_Service_OverdrawActivity kat_Service_overdrawActivity){
        this.kat_Service_overdrawActivity = kat_Service_overdrawActivity;
    }


    // 버튼 클릭 시에 해당 스레드 실행
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
                        startidx = split + 1;
                    }


                    input.close();
                    data.close();
                    reader.close();
                    socket.close();
                    workDone = true;
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

    public class workThread extends Thread{
        public void run(){
            while(true){
                if(workDone) break;
            }
        }
    }




    public void init(){
        getThread = new getApiThread();
        if(!getThread.isAlive()) getThread.start();

        workThread = new workThread();
        workThread.start();
    }

    public void AllTypeInit(String tag, String type, String apiType){
        officialApiThread = new getAllTypeApiThread(tag, type, apiType);
        if(!officialApiThread.isAlive()) officialApiThread.start();
    }


    public ArrayList<String> getData() {
        return resData;
    }
    public ArrayList<String> getAllTypeData() {
        return resOffiData;
    }

}
