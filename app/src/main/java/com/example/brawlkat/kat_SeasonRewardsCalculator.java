package com.example.brawlkat;

import com.example.brawlkat.kat_dataparser.kat_official_playerInfoParser;

import java.util.ArrayList;

public class kat_SeasonRewardsCalculator {

    kat_official_playerInfoParser.playerData playerData;

    public kat_SeasonRewardsCalculator(kat_official_playerInfoParser.playerData playerData){
        this.playerData = playerData;
    }

    public int SeasonsRewardsCalculator(){

        int result = 0;
        ArrayList<kat_official_playerInfoParser.playerBrawlerData> playerBrawlerData = playerData.getBrawlerData();
        for(int i = 0; i < playerBrawlerData.size(); i++){
            int trophies = playerBrawlerData.get(i).getTrophies();
            result += SeasonsRewardAndResetTrophies(trophies).get(0);
        }

        return result;
    }

    public int SeasonsResetTrophiesCalculator(){
        int result = 0;
        ArrayList<kat_official_playerInfoParser.playerBrawlerData> playerBrawlerData = playerData.getBrawlerData();
        for(int i = 0; i < playerBrawlerData.size(); i++){
            int trophies = playerBrawlerData.get(i).getTrophies();
            result += SeasonsRewardAndResetTrophies(trophies).get(1);
        }

        return result;
    }

    // ArrayList[0] = reward      ArrayList[1] = reset
    public ArrayList<Integer> SeasonsRewardAndResetTrophies(int beforeTrophies){

        int size = 31;
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

        int[][] range = new int[size][2];

        int curr = 501;
        for(int i = 0; i < size; i++){

            if(i == 0){
                range[i][0] = 501;
                range[i][1] = 524;
            }
            else if(i == size - 1){
                range[i][0] = curr;
                range[i][1] = 100000000;
            }
            else {
                range[i][0] = curr;
                range[i][1] = curr + 24;
            }
            curr = range[i][1] + 1;
        }


        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(beforeTrophies >= range[i][0] && beforeTrophies <= range[i][1]){
                result.add(rewards[i]); result.add(resets[i]); return result;
            }
        }
        result.add(0); result.add(beforeTrophies);
        return result;
    }
}
