package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_official_PlayerRankingParser implements Serializable {

    private                         String                      data;

    public kat_official_PlayerRankingParser(String data){
        this.data = data;
    }

    public ArrayList<playerData> DataParser() throws Exception{

        ArrayList<playerData> returnData = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = (JSONArray) jsonObject.get("items");

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject item = (JSONObject) jsonArray.get(i);
            playerData playerData = new playerData();

            if(!item.isNull("tag"))
                playerData.setTag(item.getString("tag"));
            if(!item.isNull("name"))
                playerData.setName(item.getString("name"));
            if(!item.isNull("nameColor"))
                playerData.setNameColor(item.getString("nameColor"));
            if(!item.isNull("icon")){
                JSONObject icon = item.getJSONObject("icon");
                playerData.setIconId(icon.getString("id"));
            }
            if(!item.isNull("trophies"))
                playerData.setTrophies(item.getString("trophies"));
            if(!item.isNull("rank"))
                playerData.setRank(item.getString("rank"));
            if(!item.isNull("club")){
                JSONObject club = item.getJSONObject("club");
                playerData.setClubName(club.getString("name"));
            }

            returnData.add(playerData);
        }

        return  returnData;
    }


    public class playerData implements Serializable{
        private String tag, name, nameColor, iconId, trophies, rank, clubName;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameColor() {
            return nameColor;
        }

        public void setNameColor(String nameColor) {
            this.nameColor = nameColor;
        }

        public String getIconId() {
            return iconId;
        }

        public void setIconId(String iconId) {
            this.iconId = iconId;
        }

        public String getTrophies() {
            return trophies;
        }

        public void setTrophies(String trophies) {
            this.trophies = trophies;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getClubName() {
            return clubName;
        }

        public void setClubName(String clubName) {
            this.clubName = clubName;
        }
    }
}
