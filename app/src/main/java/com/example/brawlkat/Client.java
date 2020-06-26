package com.example.brawlkat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    String res;
    Socket socket = null;            //Server와 통신하기 위한 Socket
    BufferedReader in = null;        //Server로부터 데이터를 읽어들이기 위한 입력스트림
    BufferedReader in2 = null;        //키보드로부터 읽어들이기 위한 입력스트림
    PrintWriter out = null;            //서버로 내보내기 위한 출력 스트림
    InetAddress ia = null;

    public String clientTest(){

        try {
            ia = InetAddress.getByName("222.237.33.235");    //서버로 접속
            socket = new Socket(ia,9000);

            System.out.println(socket.toString());

            res = "succeed";
        }catch(IOException e) {
            e.printStackTrace();
            res = "fail1";
        }


        return res;
    }



}
