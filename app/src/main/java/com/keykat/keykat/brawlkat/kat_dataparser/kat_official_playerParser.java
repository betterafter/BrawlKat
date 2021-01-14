package com.keykat.keykat.brawlkat.kat_dataparser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class kat_official_playerParser {

    private             String                          data;
    private             ArrayList<String>               slot = new ArrayList<>();

    public kat_official_playerParser(String data){
        this.data = data;
    }

    public ArrayList<String> DataParser() throws Exception {

        JSONObject jsonObject = new JSONObject(data);
        JSONArray brawlers = (JSONArray) jsonObject.get("brawlers");

        for(int i = 0; i < brawlers.length(); i++){

            JSONObject element = (JSONObject) brawlers.get(i);
            String brawler = element.get("name").toString();
            slot.add(brawler);
        }


        return slot;
    }
}
