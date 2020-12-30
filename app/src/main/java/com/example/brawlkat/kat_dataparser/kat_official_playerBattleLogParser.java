package com.example.brawlkat.kat_dataparser;

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
            if(!event.isNull("mode"))
                pbd.setEventMode(event.getString("mode"));
            pbd.setEventMap(event.getString("map"));

            // 전투 정보
            JSONObject battle = (JSONObject) item.get("battle");


            if(!battle.isNull("rank"))
                pbd.setRank(battle.getString("rank"));

            // trophyChange 가 없는 경우 :
            // 1. 동점      2. 이벤트 모드
            if(battle.isNull("trophyChange")){
                // 동점일 경우
                if(!battle.isNull("result"))
                    pbd.setBattleResult(battle.getString("result"));
                else if(!battle.isNull("rank"))
                    pbd.setBattleResult(battle.getString("rank"));

                // 그 외에 이벤트 모드일 경우 무승부로 처리
                else
                    pbd.setBattleResult("draw");
            }
            // 랭크가 없는 경우 : 3 vs 3 맵 또는 이벤트 맵인데 이벤트 맵은 위에서 걸러지므로 3 vs 3 맵만 판별하면 된다.
            else if(battle.isNull("rank")) {
                pbd.setBattleResult(battle.getString("result"));
                pbd.setBattleDuration(battle.getString("duration"));
            }
            // 그 외의 경우 무승부로 처리해버린다.
            else {
                pbd.setBattleResult(battle.getString("rank"));
            }
            // 트로피 변화가 있으면 트로피 변화를 세팅해준다.
            if(!battle.isNull("trophyChange"))
                pbd.setBattleTrophyChange(Integer.toString(battle.getInt("trophyChange")));






            // all team : [ team 1 : [ {memeber1}, {memeber2} ],    team 2 : [ {memeber3}, {member4} ] ]
            // 전체 팀의 수
            ArrayList<Object> teamsArrayList = new ArrayList<>();

            // 스타 플레이어 태그 가져오기
            if(!battle.isNull("starPlayer")){
                JSONObject starPlayer = battle.getJSONObject("starPlayer");
                pbd.setStarPlayer(starPlayer.getString("tag"));
            }




            //////////////////// teams 중심 ////////////////////////////
            // 3 vs 3 모드인 경우 팀 형태로 데이터를 파싱해줘야 한다.
            if(!battle.isNull("teams")){
                JSONArray battleTeam = (JSONArray) battle.get("teams");
                for(int j = 0; j < battleTeam.length(); j++) {

                    team teams = new team();

                    // 각 팀의 정보
                    JSONArray teamItem = (JSONArray) battleTeam.get(j);
                    ArrayList<playTeamInfo> eachTeamItem = new ArrayList<>();

                    // 각 팀의 멤버의 정보
                    for (int k = 0; k < teamItem.length(); k++) {

                        JSONObject teamInfo = (JSONObject) teamItem.get(k);
                        playTeamInfo info = new playTeamInfo();

                        info.setTag(teamInfo.getString("tag"));
                        info.setName(teamInfo.getString("name"));

                        JSONObject brawlers = (JSONObject) teamInfo.get("brawler");
                        info.setBrawler_Id(brawlers.getString("id"));
                        info.setBrawler_name(brawlers.getString("name"));
                        if(!brawlers.isNull("power"))
                            info.setBrawler_power(brawlers.getString("power"));
                        if(!brawlers.isNull("trophies"))
                            info.setBrawler_trophies(brawlers.getString("trophies"));

                        eachTeamItem.add(info);
                    }
                    teams.setPlayTeamInfo(eachTeamItem);
                    teamsArrayList.add(teams);
                }
            }

            /////////////////////////// player 중심 /////////////////////////
            // solo showdown 같이 개인전인 경우 플레이어 중심으로 데이터를 파싱해줘야 한다.
            else if(!battle.isNull("players")){
                JSONArray battlePlayer = (JSONArray) battle.get("players");
                player player = new player();
                ArrayList<playTeamInfo> eachTeamItem = new ArrayList<>();

                for(int k = 0; k < battlePlayer.length(); k++){

                    JSONObject playerInfo = (JSONObject) battlePlayer.get(k);
                    playTeamInfo info = new playTeamInfo();

                    info.setTag(playerInfo.getString("tag"));
                    info.setName(playerInfo.getString("name"));

                    JSONObject brawlers = (JSONObject) playerInfo.get("brawler");
                    info.setBrawler_Id(brawlers.getString("id"));
                    info.setBrawler_name(brawlers.getString("name"));
                    if(!brawlers.isNull("power"))
                        info.setBrawler_power(brawlers.getString("power"));
                    if(!brawlers.isNull("trophies"))
                        info.setBrawler_trophies(brawlers.getString("trophies"));

                    eachTeamItem.add(info);
                }
                player.setPlayTeamInfo(eachTeamItem);
                teamsArrayList.add(player);
            }


            // 해당 player or team 으로 이루어진 teamsArrayList를 pbd에 넣고 battleData에 pbd를 넣어준다.
            pbd.setTeamOrPlayer(teamsArrayList);
            battleData.add(pbd);
        }

        return battleData;
    }



    public class playerBattleData implements Serializable {

        String battleTime;
        String eventId, eventMode, eventMap;
        String battleResult, battleDuration, battleTrophyChange;
        String battleStarPlayerTag;
        String starPlayer;
        String rank;

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        ArrayList<Object> teamOrPlayer = new ArrayList<>();

        public ArrayList<Object> getTeamOrPlayer() {
            return teamOrPlayer;
        }

        public void setTeamOrPlayer(ArrayList<Object> teamOrPlayer) {
            this.teamOrPlayer = teamOrPlayer;
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

        public String getStarPlayer() {
            return starPlayer;
        }

        public void setStarPlayer(String starPlayer) {
            this.starPlayer = starPlayer;
        }
    }

    // 팀 기반 모드인 경우
    public class team implements Serializable {

        ArrayList<playTeamInfo> playTeamInfo = new ArrayList<>();

        public ArrayList<playTeamInfo> getPlayTeamInfo() {
            return playTeamInfo;
        }

        public void setPlayTeamInfo(ArrayList<playTeamInfo> playTeamInfo) {
            this.playTeamInfo = playTeamInfo;
        }
    }

    // 개인전인 경우
    public class player implements Serializable {

        ArrayList<playTeamInfo> playTeamInfo = new ArrayList<>();

        public ArrayList<kat_official_playerBattleLogParser.playTeamInfo> getPlayTeamInfo() {
            return playTeamInfo;
        }

        public void setPlayTeamInfo(ArrayList<kat_official_playerBattleLogParser.playTeamInfo> playTeamInfo) {
            this.playTeamInfo = playTeamInfo;
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
