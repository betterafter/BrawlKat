package com.example.brawlkat.kat_dataparser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class kat_official_clubInfoParser implements Serializable {

    private                             String                              data;


    public kat_official_clubInfoParser(String data){
        this.data = data;
    }

    public clubData DataParser() throws Exception {

        JSONObject jsonObject = new JSONObject(data);

        clubData cd = new clubData();

        cd.setTag(jsonObject.getString("tag"));
        cd.setName(jsonObject.getString("name"));
        if(!jsonObject.isNull("description"))
            cd.setDescription(jsonObject.getString("description"));
        cd.setTrophies(jsonObject.getInt("trophies"));
        cd.setRequiredTrophies(jsonObject.getInt("requiredTrophies"));
        cd.setBadgeId(jsonObject.getInt("badgeId"));

        JSONArray members = jsonObject.getJSONArray("members");
        ArrayList<clubMemberData> memberData = new ArrayList<>();
        HashMap<String, Integer> roles = new HashMap<>();
        roles.put("president", 0);
        roles.put("vicePresident", 0);
        roles.put("senior", 0);
        roles.put("member", 0);
        long lowestTrophy = Long.MAX_VALUE; long highestTrophy = Long.MIN_VALUE;
        for(int i = 0; i < members.length(); i++){

            JSONObject elem = (JSONObject) members.get(i);

            clubMemberData clubMemberData = new clubMemberData();

            clubMemberData.setTag(elem.getString("tag"));
            clubMemberData.setName(elem.getString("name"));
            clubMemberData.setNameColor(elem.getString("nameColor"));


            String role = elem.getString("role");
            clubMemberData.setRole(role);

            if(roles.containsKey(role))
                roles.put(role, roles.get(role) + 1);
            else
                roles.put(role, 1);


            int MembersTrophies = elem.getInt("trophies");
            lowestTrophy = Math.min(MembersTrophies, lowestTrophy);
            highestTrophy = Math.max(MembersTrophies, highestTrophy);
            clubMemberData.setTrophies(MembersTrophies);

            JSONObject icon = (JSONObject) elem.get("icon");
            clubMemberData.setIconId(icon.getString("id"));

            memberData.add(clubMemberData);
        }
        cd.setTrophyRange((int)lowestTrophy + " ~ " + (int) highestTrophy);
        cd.setAverageTrophy(cd.getTrophies() / memberData.size());
        cd.setMembersRole(roles);
        cd.setMemberDatas(memberData);

        return cd;
    }






    public class clubData implements Serializable{
        String tag;
        String name;
        String description;
        String trophyRange;
        int averageTrophy;
        int trophies;
        int requiredTrophies;
        int badgeId;
        ArrayList<clubMemberData> memberDatas;
        HashMap<String, Integer> membersRole;


        public HashMap<String, Integer> getMembersRole() {
            return membersRole;
        }

        public void setMembersRole(HashMap<String, Integer> membersRole) {
            this.membersRole = membersRole;
        }

        public String getTrophyRange() {
            return trophyRange;
        }

        public void setTrophyRange(String trophyRange) {
            this.trophyRange = trophyRange;
        }

        public int getAverageTrophy() {
            return averageTrophy;
        }

        public void setAverageTrophy(int averageTrophy) {
            this.averageTrophy = averageTrophy;
        }

        public ArrayList<clubMemberData> getMemberDatas() {
            return memberDatas;
        }

        public void setMemberDatas(ArrayList<clubMemberData> memberDatas) {
            this.memberDatas = memberDatas;
        }

        public int getBadgeId() {
            return badgeId;
        }

        public void setBadgeId(int badgeId) {
            this.badgeId = badgeId;
        }

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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getTrophies() {
            return trophies;
        }

        public void setTrophies(int trophies) {
            this.trophies = trophies;
        }

        public int getRequiredTrophies() {
            return requiredTrophies;
        }

        public void setRequiredTrophies(int requiredTrophies) {
            this.requiredTrophies = requiredTrophies;
        }

    }

    public class clubMemberData implements Serializable{

        String name;
        String tag;
        String nameColor;
        String role;
        int trophies;
        String iconId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getNameColor() {
            return nameColor;
        }

        public void setNameColor(String nameColor) {
            this.nameColor = nameColor;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public int getTrophies() {
            return trophies;
        }

        public void setTrophies(int trophies) {
            this.trophies = trophies;
        }

        public String getIconId() {
            return iconId;
        }

        public void setIconId(String iconId) {
            this.iconId = iconId;
        }
    }
}
