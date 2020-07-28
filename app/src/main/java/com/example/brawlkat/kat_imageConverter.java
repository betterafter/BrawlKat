package com.example.brawlkat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;

public class kat_imageConverter extends AppCompatActivity {

    private                     String                  accessToken;
    private                     String                  url;
    private                     HttpURLConnection       connection;
    private                     InputStream             inputStream;


    public kat_imageConverter(String accessToken, String url){
        this.accessToken = accessToken;
        this.url = url;
    }

    public void HttpConnection(String u){

        try{
            URL url = new URL(u);
            connection = (HttpURLConnection)url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            System.out.println(accessToken);

            inputStream = connection.getInputStream();
        }
        catch (Exception e){

        }

    }

    public Bitmap testConverter(){
        String url = "https://www.starlist.pro/assets/map/Minecart-Madness.png?v=5";
        HttpConnection(url);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        System.out.println(inputStream);

        return bitmap;
    }

    public void ImageConverter(String url){

    }




}
