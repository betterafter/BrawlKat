package com.example.brawlkat.dataparser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_official_playerBattleLogParser implements Serializable {

    private                                 String                              data;

    public kat_official_playerBattleLogParser(String data){
        this.data = data;
    }


    public ArrayList<playerBattleData> DataParser() throws Exception{

        JSONObject jsonObject = new JSONObject(data);
        JSONArray items = (JSONArray) jsonObject.get("items");

        ArrayList<playerBattleData> battleData = new ArrayList<>();

        // item 배열 파싱
        for(int i = 0; i < items.length(); i++){

            JSONObject item = (JSONObject) items.get(i);
            playerBattleData pbd = new playerBattleData();

            // 플레이 시작 시간
            pbd.setBattleTime(item.getString("battleTime"));

            // 플레이 이벤트 정보
            JSONObject event = (JSONObject) item.get("event");
            pbd.setEventId(event.getString("id"));
            pbd.setEventMode(event.getString("mode"));
            pbd.setEventMap(event.getString("map"));

            // 전투 정보
            JSONObject battle = (JSONObject) item.get("battle");
            if(battle.isNull("rank")) {
                pbd.setBattleResult(battle.getString("result"));
                pbd.setBattleDuration(battle.getString("duration"));
            }
            else {
                pbd.setBattleResult(battle.getString("rank"));
            }
            if(!battle.isNull("trophyChange"))
                pbd.setBattleTrophyChange(Integer.toString(battle.getInt("trophyChange")));

            // all team : [ team 1 : [ {memeber1}, {memeber2} ],    team 2 : [ {memeber3}, {member4} ] ]
            // 전체 팀의 수
            ArrayList<team> teamsArrayList = new ArrayList<>();
            JSONArray battleTeam = (JSONArray) battle.get("teams");
            for(int j = 0; j < battleTeam.length(); j++){

                team teams = new team();

                // 각 팀의 정보
                JSONArray teamItem = (JSONArray) battleTeam.get(j);
                ArrayList<playTeamInfo> eachTeamItem = new ArrayList<>();

                // 각 팀의 멤버의 정보
                for(int k = 0; k < teamItem.length(); k++){

                    JSONObject teamInfo = (JSONObject) teamItem.get(k);
                    playTeamInfo info = new playTeamInfo();

                    info.setTag(teamInfo.getString("tag"));
                    info.setName(teamInfo.getString("name"));

                    JSONObject brawlers = (JSONObject) teamInfo.get("brawler");
                    info.setBrawler_Id(brawlers.getString("id"));
                    info.setBrawler_name(brawlers.getString("name"));
                    info.setBrawler_power(brawlers.getString("power"));
                    info.setBrawler_trophies(brawlers.getString("trophies"));

                    eachTeamItem.add(info);
                }
                teams.setPlayTeamInfos(eachTeamItem);
                teamsArrayList.add(teams);
            }
            pbd.setTeams(teamsArrayList);

            battleData.add(pbd);
        }

        return battleData;
    }



    public class playerBattleData implements Serializable {

        String battleTime;
        String eventId, eventMode, eventMap;
        String battleResult, battleDuration, battleTrophyChange;
        String battleStarPlayerTag;
        ArrayList<team> teams = new ArrayList<>();

        public ArrayList<team> getTeams() {
            return teams;
        }

        public void setTeams(ArrayList<team> teams) {
            this.teams = teams;
        }

        public String getBattleTime() {
            return battleTime;
        }

        public void setBattleTime(String battleTime) {
            this.battleTime = battleTime;
        }

        public String getEventId() {
            return eventId;
        }

        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        public String getEventMode() {
            return eventMode;
        }

        public void setEventMode(String eventMode) {
            this.eventMode = eventMode;
        }

        public String getEventMap() {
            return eventMap;
        }

        public void setEventMap(String eventMap) {
            this.eventMap = eventMap;
        }

        public String getBattleResult() {
            return battleResult;
        }

        public void setBattleResult(String battleResult) {
            this.battleResult = battleResult;
        }

        public String getBattleDuration() {
            return battleDuration;
        }

        public void setBattleDuration(String battleDuration) {
            this.battleDuration = battleDuration;
        }

        public String getBattleTrophyChange() {
            return battleTrophyChange;
        }

        public void setBattleTrophyChange(String battleTrophyChange) {
            this.battleTrophyChange = battleTrophyChange;
        }

        public String getBattleStarPlayerTag() {
            return battleStarPlayerTag;
        }

        public void setBattleStarPlayerTag(String battleStarPlayerTag) {
            this.battleStarPlayerTag = battleStarPlayerTag;
        }
    }

    public class team implements Serializable {

        ArrayList<playTeamInfo> playTeamInfos = new ArrayList<>();

        public ArrayList<playTeamInfo> getPlayTeamInfos() {
            return playTeamInfos;
        }

        public void setPlayTeamInfos(ArrayList<playTeamInfo> playTeamInfos) {
            this.playTeamInfos = playTeamInfos;
        }
    }

    public class playTeamInfo implements Serializable{

        String tag, name;
        String brawler_Id, brawler_name, brawler_power, brawler_trophies;

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

        public String getBrawler_Id() {
            return brawler_Id;
        }

        public void setBrawler_Id(String brawler_Id) {
            this.brawler_Id = brawler_Id;
        }

        public String getBrawler_name() {
            return brawler_name;
        }

        public void setBrawler_name(String brawler_name) {
            this.brawler_name = brawler_name;
        }

        public String getBrawler_power() {
            return brawler_power;
        }

        public void setBrawler_power(String brawler_power) {
            this.brawler_power = brawler_power;
        }

        public String getBrawler_trophies() {
            return brawler_trophies;
        }

        public void setBrawler_trophies(String brawler_trophies) {
            this.brawler_trophies = brawler_trophies;
        }
    }




}
