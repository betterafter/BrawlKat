package com.example.brawlkat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    Socket socket = null;
    String result = "";
    OutputStream sendToServer;
    InputStream data;

    public String clientTest(String playerTag){

        try {
            socket = new Socket("35.237.9.225", 9000);
            System.out.println(socket.isConnected());

            sendToServer = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(sendToServer, true);
            writer.println(playerTag);

            sendToServer.flush();

            data = socket.getInputStream();
            InputStreamReader input = new InputStreamReader(data);
            BufferedReader reader = new BufferedReader(input);



            result = reader.readLine();
            System.out.println(result);

            socket.close();

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
        return result;
    }

}
