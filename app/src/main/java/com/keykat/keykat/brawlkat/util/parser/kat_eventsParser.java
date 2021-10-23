package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class kat_eventsParser {

    private                 String                                  data;
    private                 ArrayList<pair>                         slot = new ArrayList<>();


    public kat_eventsParser(String data){
        this.data = data;
    }

    public static class pair{
        private final HashMap<String, Object> info;
        private final ArrayList<HashMap<String, Object> > wins;

        public pair(HashMap<String, Object> info, ArrayList<HashMap<String, Object> > wins){
            this.info = info;
            this.wins = wins;
        }

        public HashMap<String, Object> getInfo(){
            return this.info;
        }

        public ArrayList<HashMap<String, Object> > getWins(){
            return this.wins;
        }
    }

    public ArrayList<pair> DataParser() throws Exception {

        JSONObject jsonObject = new JSONObject(data);
        if(!jsonObject.isNull("active")) {
            JSONArray active = (JSONArray) jsonObject.get("active");


            for (int i = 0; i < active.length(); i++) {

                HashMap<String, Object> map = new HashMap<>();
                JSONObject element = (JSONObject) active.get(i);


                // 게임 모드 유형
                JSONObject name = (JSONObject) element.get("slot");
                map.put("name", name.get("name"));


                JSONObject mapName = (JSONObject) element.get("map");
                map.put("mapName", mapName.get("name"));


                // 시작 시간 & 종료 시간
                map.put("startTime", element.get("startTime"));
                map.put("endTime", element.get("endTime"));


                // 맵 정보
                JSONObject info = (JSONObject) element.get("map");
                // 맵 정보 - 맵 타입
                JSONObject mapTypeImageUrl = (JSONObject) info.get("environment");
                map.put("mapTypeImageUrl", mapTypeImageUrl.get("imageUrl"));
                // 맵 정보 - 게임 모드 타입
                JSONObject gameModeTypeImageUrl = (JSONObject) info.get("gameMode");
                map.put("gameModeTypeImageUrl", gameModeTypeImageUrl.get("imageUrl"));

                // 브롤러 별 승률
                JSONArray stats = (JSONArray) info.get("stats");
                ArrayList<HashMap<String, Object>> winsArrayList = new ArrayList<>();

                for (int j = 0; j < stats.length(); j++) {

                    JSONObject elem = (JSONObject) stats.get(j);
                    HashMap<String, Object> wins = new HashMap<>();

                    // 브롤러
                    wins.put("brawler", elem.get("brawler"));
                    // 승률
                    wins.put("winRate", elem.getDouble("winRate"));
                    // 픽률
                    wins.put("useRate", elem.getDouble("useRate"));

                    winsArrayList.add(wins);
                }

                pair p = new pair(map, winsArrayList);
                slot.add(p);
            }
        }
        return slot;
    }
}
