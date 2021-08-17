package com.keykat.keykat.brawlkat.util.parser;

import android.content.res.AssetManager;

import com.keykat.keykat.brawlkat.splash.activity.kat_LoadBeforeMainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class kat_countryCodeParser {

    kat_LoadBeforeMainActivity kat_loadBeforeMAinActivity;

    public kat_countryCodeParser(kat_LoadBeforeMainActivity kat_loadBeforeMAinActivity){
        this.kat_loadBeforeMAinActivity = kat_loadBeforeMAinActivity;
    }

    public HashMap<String, String> DataParser() throws Exception {

        HashMap<String, String> returnMap = new HashMap<>();

        // countryCode.json 가져와서 String으로 치환 ....................................................
        AssetManager assetManager = kat_loadBeforeMAinActivity.getAssets();

        try{
            InputStream is = assetManager.open("jsons/countryCode.json");
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer buffer = new StringBuffer();
            String line = bufferedReader.readLine();
            while(line != null){
                buffer.append(line + "\n");
                line = bufferedReader.readLine();
            }

            String jsonData = buffer.toString();
         // ........................................................................................

            // String 으로 치환한 json 데이터를 파싱......................................................

            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("items");

            for(int i = 0; i < jsonArray.length(); i++){

                JSONObject item = (JSONObject) jsonArray.get(i);
                if(!item.isNull("name") && !item.isNull("code")){
                    returnMap.put(item.getString("code"), item.getString("name"));
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return returnMap;
    }
}
