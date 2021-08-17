package com.keykat.keykat.brawlkat.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class kat_official_playerInfoParser implements Serializable {

    private             String                          data;

    public kat_official_playerInfoParser(String data){
        this.data = data;
    }

    public playerData DataParser() throws Exception {

        JSONObject jsonObject = new JSONObject(data);

        playerData pd = new playerData();

        pd.setTag(jsonObject.get("tag").toString());
        pd.setName(jsonObject.get("name").toString());
        if(!jsonObject.isNull("nameColor")) pd.setNameColor(jsonObject.get("nameColor").toString());

        JSONObject icon = (JSONObject) jsonObject.get("icon");
        String iconId = icon.get("id").toString();
        pd.setIconId(iconId);

        pd.setTrophies(Integer.parseInt(jsonObject.get("trophies").toString()));
        pd.setHighestTrophies(jsonObject.getInt("highestTrophies"));
        if(!jsonObject.isNull("powerPlayPoints"))
            pd.setPowerPlayPoint(jsonObject.getInt("powerPlayPoints"));
        if(!jsonObject.isNull("highestPowerPlayPoints"))
            pd.setHighestPowerPlayPoint(jsonObject.getInt("highestPowerPlayPoints"));
        pd.setExpLevel(jsonObject.getInt("expLevel"));
        pd.set_3vs3(jsonObject.getInt("3vs3Victories"));
        pd.set_solo(jsonObject.getInt("soloVictories"));
        pd.set_duo(jsonObject.getInt("duoVictories"));
        pd.set_event(jsonObject.getInt("bestRoboRumbleTime"));
        pd.set_bigBrawler(jsonObject.getInt("bestTimeAsBigBrawler"));

        if(!jsonObject.isNull("club")) {
            JSONObject club = (JSONObject) jsonObject.get("club");
            if (!club.isNull("tag")) {
                String clubId = club.get("tag").toString();
                String clubNAme = club.get("name").toString();

                club c = new club();
                c.setId(clubId);
                c.setName(clubNAme);
                pd.setClub(c);
            }
        }


        ArrayList<playerBrawlerData> pbdArrayList = new ArrayList<>();
        JSONArray brawlers = (JSONArray) jsonObject.get("brawlers");
        for(int i = 0; i < brawlers.length(); i++){

            JSONObject element = (JSONObject) brawlers.get(i);

            playerBrawlerData brawlerData = new playerBrawlerData();

            String name = element.get("name").toString();
            brawlerData.setName(name);

            String id = element.get("id").toString();
            brawlerData.setId(id);

            int power = element.getInt("power");
            brawlerData.setPower(power);

            int rank = element.getInt("rank");
            brawlerData.setRank(rank);

            int trophies = element.getInt("trophies");
            brawlerData.setTrophies(trophies);

            int highestTrophies = element.getInt("highestTrophies");
            brawlerData.setHighestTrophies(highestTrophies);

            ArrayList<starPower> spArrayList = new ArrayList<>();
            JSONArray starPower = (JSONArray) element.get("starPowers");

            for(int j = 0; j < starPower.length(); j++){

                starPower sp = new starPower();

                JSONObject elem = (JSONObject) starPower.get(j);

                String spId = elem.get("id").toString();
                sp.setId(spId);

                String spName = elem.get("name").toString();
                sp.setName(spName);

                spArrayList.add(sp);
            }
            brawlerData.setStarPowers(spArrayList);


            ArrayList<gadget> ggArrayList = new ArrayList<>();
            JSONArray gadGet = (JSONArray) element.get("gadgets");

            for(int j = 0; j < gadGet.length(); j++){

                gadget gg = new gadget();

                JSONObject elem = (JSONObject) gadGet.get(j);

                String ggId = elem.get("id").toString();
                gg.setId(ggId);

                String ggName = elem.get("name").toString();
                gg.setName(ggName);

                ggArrayList.add(gg);
            }
            brawlerData.setGadgets(ggArrayList);

            pbdArrayList.add(brawlerData);
        }


        pd.setBrawlerData(pbdArrayList);


        return pd;
    }



    public class playerData implements Serializable {

        private String Tag, name, nameColor, iconId;
        private int trophies, highestTrophies, powerPlayPoint, highestPowerPlayPoint, expLevel;
        private int _3vs3, _solo, _duo, _event, _bigBrawler;
        private club playerClub = new club();
        private ArrayList<playerBrawlerData> brawlerData = new ArrayList<>();

        public String getTag() {
            return Tag;
        }

        public void setTag(String tag) {
            Tag = tag;
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

        public int getTrophies() {
            return trophies;
        }

        public void setTrophies(int trophies) {
            this.trophies = trophies;
        }

        public int getHighestTrophies() {
            return highestTrophies;
        }

        public void setHighestTrophies(int highestTrophies) {
            this.highestTrophies = highestTrophies;
        }

        public int getPowerPlayPoint() {
            return powerPlayPoint;
        }

        public void setPowerPlayPoint(int powerPlayPoint) {
            this.powerPlayPoint = powerPlayPoint;
        }

        public int getHighestPowerPlayPoint() {
            return highestPowerPlayPoint;
        }

        public void setHighestPowerPlayPoint(int highestPowerPlayPoint) {
            this.highestPowerPlayPoint = highestPowerPlayPoint;
        }

        public int getExpLevel() {
            return expLevel;
        }

        public void setExpLevel(int expLevel) {
            this.expLevel = expLevel;
        }

        public int get_3vs3() {
            return _3vs3;
        }

        public void set_3vs3(int _3vs3) {
            this._3vs3 = _3vs3;
        }

        public int get_solo() {
            return _solo;
        }

        public void set_solo(int _solo) {
            this._solo = _solo;
        }

        public int get_duo() {
            return _duo;
        }

        public void set_duo(int _duo) {
            this._duo = _duo;
        }

        public int get_event() {
            return _event;
        }

        public void set_event(int _event) {
            this._event = _event;
        }

        public int get_bigBrawler() {
            return _bigBrawler;
        }

        public void set_bigBrawler(int _bigBrawler) {
            this._bigBrawler = _bigBrawler;
        }

        public club getClub() {
            return playerClub;
        }

        public void setClub(club club) {
            this.playerClub = club;
        }

        public ArrayList<playerBrawlerData> getBrawlerData() {
            return brawlerData;
        }

        public void setBrawlerData(ArrayList<playerBrawlerData> brawlerData) {
            this.brawlerData = brawlerData;
        }
    }

    public class playerBrawlerData implements Serializable{

        private String id, name;
        private int power, rank, trophies, highestTrophies;
        private ArrayList<starPower> starPowers;
        private ArrayList<gadget> gadgets;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getTrophies() {
            return trophies;
        }

        public void setTrophies(int trophies) {
            this.trophies = trophies;
        }

        public int getHighestTrophies() {
            return highestTrophies;
        }

        public void setHighestTrophies(int highestTrophies) {
            this.highestTrophies = highestTrophies;
        }

        public ArrayList<starPower> getStarPowers() {
            return starPowers;
        }

        public void setStarPowers(ArrayList<starPower> starPowers) {
            this.starPowers = starPowers;
        }

        public ArrayList<gadget> getGadgets() {
            return gadgets;
        }

        public void setGadgets(ArrayList<gadget> gadgets) {
            this.gadgets = gadgets;
        }
    }

    public class starPower implements Serializable{

        private String id, name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class gadget implements Serializable{

        private String id, name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class club implements Serializable{

        private String id, name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
