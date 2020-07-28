package com.example.brawlkat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    Socket socket = null;
    String result = "";
    OutputStream sendToServer;
    InputStream data;
    ArrayList<String> resData = new ArrayList<>();

    public ArrayList<String> getdata(String... send){

        try {

            int sendDataCount = 2;
            for(int i = 0; i < sendDataCount; i++){
                socket = new Socket("35.237.9.225", 9000);
                System.out.println(socket.isConnected());

                sendToServer = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(sendToServer, true);
                writer.println(send[i]);

                sendToServer.flush();

                data = socket.getInputStream();
                InputStreamReader input = new InputStreamReader(data);
                BufferedReader reader = new BufferedReader(input);
                result = reader.readLine();

                System.out.println("getData : " + result);
                resData.add(result);


                socket.close();
            }


        }catch(IOException e) {
            e.printStackTrace();

        }
        try {
            // 소켓 종료.

            if(sendToServer != null) sendToServer.close() ;
            if(data != null) data.close();
            if (socket != null) socket.close() ;
        } catch (Exception e) {
            // TODO : process exceptions.
        }
        return resData;
    }

}
