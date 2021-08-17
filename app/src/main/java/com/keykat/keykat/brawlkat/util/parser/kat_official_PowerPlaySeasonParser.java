package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_official_PowerPlaySeasonParser implements Serializable {

    private                         String                      data;

    public kat_official_PowerPlaySeasonParser(String data){
        this.data = data;
    }

    public ArrayList<powerPlaySeasonsData> DataParser() throws Exception {

        ArrayList<powerPlaySeasonsData> returnData = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("items");

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject item = (JSONObject)jsonArray.get(i);
            powerPlaySeasonsData powerPlaySeasonsData = new powerPlaySeasonsData();

            if(!item.isNull("id"))
                powerPlaySeasonsData.setId(item.getString("id"));
            if(!item.isNull("startTime"))
                powerPlaySeasonsData.setStartTime(item.getString("startTime"));
            if(!item.isNull("endTime"))
                powerPlaySeasonsData.setEndTime(item.getString("endTime"));

            returnData.add(powerPlaySeasonsData);
        }
        return returnData;
    }


    public class powerPlaySeasonsData implements Serializable{
        String id, startTime, endTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
