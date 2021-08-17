package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

public class kat_mapsParser implements Serializable {

    private                 String                                 data;
    private                 HashMap<String, mapData>               parsedData = new HashMap<>();

    public kat_mapsParser(String data){
        this.data = data;
    }

    public HashMap<String, mapData> DataParser() throws Exception{

        JSONObject jsonObject = new JSONObject(data);

        if(!jsonObject.isNull("list")) {
            JSONArray lists = jsonObject.getJSONArray("list");

            for (int i = 0; i < lists.length(); i++) {

                JSONObject elem = lists.getJSONObject(i);

                String id = elem.getString("id");

                mapData md = new mapData();

                md.setMapImageUrl(elem.getString("imageUrl"));
                md.setName(elem.getString("name"));

                if (!elem.isNull("environment")) {
                    JSONObject environment = elem.getJSONObject("environment");
                    md.setEventBannerUrl(environment.getString("imageUrl"));
                }

                if (!elem.isNull("gameMode")) {
                    JSONObject gameMode = elem.getJSONObject("gameMode");
                    md.setGameModeIconUrl(gameMode.getString("imageUrl"));
                }
                parsedData.put(id, md);
            }
        }
        return parsedData;
    }


    public class mapData implements Serializable {

        String name;
        String mapImageUrl;
        String eventBannerUrl;
        String gameModeIconUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMapImageUrl() {
            return mapImageUrl;
        }

        public void setMapImageUrl(String mapImageUrl) {
            this.mapImageUrl = mapImageUrl;
        }

        public String getEventBannerUrl() {
            return eventBannerUrl;
        }

        public void setEventBannerUrl(String eventBannerUrl) {
            this.eventBannerUrl = eventBannerUrl;
        }

        public String getGameModeIconUrl() {
            return gameModeIconUrl;
        }

        public void setGameModeIconUrl(String gameModeIconUrl) {
            this.gameModeIconUrl = gameModeIconUrl;
        }
    }
}
