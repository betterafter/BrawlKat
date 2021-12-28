package com.keykat.keykat.brawlkat.common.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_clubLogParser implements Serializable {

    private                         String                          data;

    public kat_clubLogParser(String data){
        this.data = data;
    }

    public clubLogData DataParser() throws Exception {

        if(data.equals("{none}")){
            clubLogData clubLogData = new clubLogData();
            clubLogData.setStatus("forbidden");

            return clubLogData;
        }


        JSONObject jsonObject = new JSONObject(data);
        clubLogData clubLogData = new clubLogData();

        clubLogData.setStatus("access");
        JSONObject updated = jsonObject.getJSONObject("club").getJSONObject("updated");
        clubLogData.setData(updated.getString("data"));
        clubLogData.setHistory(updated.getString("history"));

        JSONArray history = jsonObject.getJSONArray("history");
        ArrayList<Object> historyData = new ArrayList<>();
        for(int i = 0; i < history.length(); i++){

            JSONObject elem = (JSONObject) history.get(i);
            JSONObject elemData = (JSONObject)elem.get("data");

            if(elem.getString("type").equals("members")){
                joinData joinData = new joinData();
                joinData.setJoin(elemData.getBoolean("joined"));
                JSONObject joinPlayer = elemData.getJSONObject("player");
                joinData.setPlayerName(joinPlayer.getString("name"));
                joinData.setPlayerTag(joinPlayer.getString("tag"));
                joinData.setTimeStamp(elem.getString("timestamp"));

                historyData.add(joinData);
            }
            else if(elem.getString("type").equals("roles")){
                promoteData promoteData = new promoteData();
                promoteData.setPromote(elemData.getBoolean("promote"));
                JSONObject promotePlayer = elemData.getJSONObject("player");
                promoteData.setPlayerName(promotePlayer.getString("name"));
                promoteData.setPlayerTag(promotePlayer.getString("tag"));
                promoteData.setOldRole(elemData.getString("old"));
                promoteData.setNewRole(elemData.getString("new"));
                promoteData.setTimeStamp(elem.getString("timestamp"));

                historyData.add(promoteData);
            }
            else if(elem.getString("type").equals("settings")){
                settingData settingData = new settingData();
                settingData.setType(elemData.getString("type"));
                settingData.setNewType(elemData.getString("new"));
                settingData.setOldType(elemData.getString("old"));
                settingData.setTimeStamp(elem.getString("timestamp"));

                historyData.add(settingData);
            }
        }

        clubLogData.setHistoryData(historyData);

        return clubLogData;
    }

    public class clubLogData implements Serializable{
        String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        String data;
        String history;
        ArrayList<Object> historyData;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getHistory() {
            return history;
        }

        public void setHistory(String history) {
            this.history = history;
        }

        public ArrayList<Object> getHistoryData() {
            return historyData;
        }

        public void setHistoryData(ArrayList<Object> historyData) {
            this.historyData = historyData;
        }
    }

    public class promoteData implements Serializable{
        boolean promote;
        String playerName;
        String playerTag;
        String oldRole;
        String newRole;
        String timeStamp;

        public boolean isPromote() {
            return promote;
        }

        public void setPromote(boolean promote) {
            this.promote = promote;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public String getPlayerTag() {
            return playerTag;
        }

        public void setPlayerTag(String playerTag) {
            this.playerTag = playerTag;
        }

        public String getOldRole() {
            return oldRole;
        }

        public void setOldRole(String oldRole) {
            this.oldRole = oldRole;
        }

        public String getNewRole() {
            return newRole;
        }

        public void setNewRole(String newRole) {
            this.newRole = newRole;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    public class settingData implements Serializable{
        String type;
        String oldType;
        String newType;
        String timeStamp;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOldType() {
            return oldType;
        }

        public void setOldType(String oldType) {
            this.oldType = oldType;
        }

        public String getNewType() {
            return newType;
        }

        public void setNewType(String newType) {
            this.newType = newType;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }

    public class joinData implements Serializable{
        boolean join;
        String playerName;
        String playerTag;
        String timeStamp;

        public boolean isJoin() {
            return join;
        }

        public void setJoin(boolean join) {
            this.join = join;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public String getPlayerTag() {
            return playerTag;
        }

        public void setPlayerTag(String playerTag) {
            this.playerTag = playerTag;
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}
