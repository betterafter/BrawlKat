package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_official_ClubRankingParser implements Serializable {

    private                         String                      data;

    public kat_official_ClubRankingParser(String data){
        this.data = data;
    }

    public ArrayList<clubData> DataParser() throws Exception {

        ArrayList<clubData> returnData = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(data);
        JSONArray jsonArray = jsonObject.getJSONArray("items");

        for(int i = 0; i < jsonArray.length(); i++){

            JSONObject item = (JSONObject) jsonArray.get(i);
            clubData clubData = new clubData();

            if(!item.isNull("tag"))
                clubData.setTag(item.getString("tag"));
            if(!item.isNull("name"))
                clubData.setName(item.getString("name"));
            if(!item.isNull("badgeId"))
                clubData.setBadgeId(item.getString("badgeId"));
            if(!item.isNull("trophies"))
                clubData.setTrophies(item.getString("trophies"));
            if(!item.isNull("rank"))
                clubData.setRank(item.getString("rank"));
            if(!item.isNull("memberCount"))
                clubData.setMemberCount(item.getString("memberCount"));

            returnData.add(clubData);
        }


        return  returnData;
    }


    public class clubData implements Serializable {
        private String tag, name, badgeId, trophies, rank, memberCount;

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

        public String getBadgeId() {
            return badgeId;
        }

        public void setBadgeId(String badgeId) {
            this.badgeId = badgeId;
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

        public String getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(String memberCount) {
            this.memberCount = memberCount;
        }
    }
}
