package com.keykat.keykat.brawlkat.service.util;

import com.keykat.keykat.brawlkat.util.parser.kat_eventsParser;
import com.keykat.keykat.brawlkat.util.parser.kat_official_playerInfoParser;

import java.util.ArrayList;
import java.util.HashMap;

public class kat_BrawlerRecommendation {

    private final kat_official_playerInfoParser.playerData playerData;
    private final ArrayList<kat_eventsParser.pair> eventArrayList;

    public kat_BrawlerRecommendation(
            kat_official_playerInfoParser.playerData playerData,
            ArrayList<kat_eventsParser.pair> eventArrayList
    ) {
        this.playerData = playerData;
        this.eventArrayList = eventArrayList;
    }

    int[] level  = new int[]{
            501, 525, 550, 575, 600, 625, 650, 675, 700, 725, 750, 775,
            800, 825, 850, 875, 900, 925, 950, 975, 1050, 1100, 1150,
            1200, 1250, 1300, 1350, 1400, 1450, 1500
    };

    int[] rewards = new int[]{
            20, 50, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160, 170,
            180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290,
            300, 310, 320, 330, 340, 350
    };

    int[] resets = new int[]{
            500, 524, 549, 574, 599, 624, 649, 674, 699, 724, 749, 774,
            799, 824, 849, 874, 885, 900, 920, 940, 960, 980, 1000, 1020,
            1040, 1060, 1080, 1100, 1120, 1140, 1150
    };

    public int expectedStarPoint(int current){

        for(int i = level.length - 1; i > 0; i--){
            if(current >= level[i]){
                return rewards[i];
            }
        }
        return 0;
    }

    public int expectedNextStarPoint(int current){

        if(current >= 1500) return 0;

        for(int i = level.length - 2; i > 0; i--){
            if(current >= level[i]){
                return rewards[i + 1] - rewards[i];
            }
        }
        return rewards[0];
    }

    public int leftTrophyToNextLevel(int current){

        if(current < 501){
            return 501 - current;
        }

        if(current > 1500){
            return 0;
        }

        for(int i = 1; i < level.length - 1; i++){
            if(current >= level[i] && current < level[i + 1]){
                return level[i + 1] - current;
            }
        }

        return 0;
    }

    public String recommend(){

        if(playerData == null) return "";
        ArrayList<kat_official_playerInfoParser.playerBrawlerData> playerBrawlerData = playerData.getBrawlerData();
        int MAX = 10000000;
        int minDifference = MAX;
        int Trophy = 0;
        int index = -1;
        double winRate = 0;

        for(int i = 1; i < playerBrawlerData.size(); i++){


            int trophies = playerBrawlerData.get(i).getTrophies();
            int difference = 10000000;
            double rate = 0;

            for(int j = 0; j < level.length; j++){
                if(trophies < level[j]){
                    difference = Math.abs(trophies - level[j]);
                    break;
                }
            }
            rate = averageWinRate(playerBrawlerData.get(i).getId());
            if(difference != 0){
                if(minDifference > difference){
                    index = i; minDifference = difference; winRate = rate; Trophy = trophies;
                }
                else if(minDifference == difference){
                    if(Trophy < trophies){
                        index = i; minDifference = difference; winRate = rate; Trophy = trophies;
                    }
                    else if(Trophy == trophies){
                        if(winRate <= rate){
                            index = i; minDifference = difference; winRate = rate; Trophy = trophies;
                        }
                    }
                }
            }
            else if(difference == 0){
                if(minDifference == MAX && winRate <= rate && Trophy <= trophies){
                    index = i; winRate = rate; Trophy = trophies;
                }
            }
        }
        return playerBrawlerData.get(index).getId();
    }


    public double averageWinRate(String brawler){

        double winRate = 0;
        if(eventArrayList == null) return winRate;

        for(int i = 0; i < eventArrayList.size(); i++){
            ArrayList<HashMap<String, Object>> wins = eventArrayList.get(i).getWins();
            for(int j = 0; j < wins.size(); j++){
                if(wins.get(j).get("brawler").toString().equals(brawler.toLowerCase())) {
                    double currWinRate;
                    currWinRate = (Double) wins.get(j).get("winRate");
                    winRate += currWinRate;
                }
            }
        }
        winRate /= eventArrayList.size();

        return winRate;
    }
}
