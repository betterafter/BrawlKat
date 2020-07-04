package com.example.brawlkat;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class GetUserData extends AppCompatActivity {

    String res = "";

    public String ShowPlayerInformation(String dataFromServer){

        System.out.println(dataFromServer);

        try{

            String data = dataFromServer;
            JSONObject jsonObject = new JSONObject(data);
            JSONArray myBrawler = (JSONArray) jsonObject.get("brawlers");
            res = res + "-------------------------" +  jsonObject.get("name") + "-------------------------" + '\n';
            res = res + "현재 트로피 : " + jsonObject.get("trophies") + "      " + "최대 트로피 : " + jsonObject.get("highestTrophies") + '\n';
            res = res + "3 vs 3 : " + jsonObject.get("3vs3Victories") + "      " + "솔로 : " + jsonObject.get("soloVictories")
                    + "      " + "듀오 : " + jsonObject.get("duoVictories") + '\n';

            res = res + "-------------------------내 브롤러 정보-------------------------" + '\n';

            for(int i = 0; i < myBrawler.length(); i++){
                JSONObject object = (JSONObject)myBrawler.get(i);
                res += "-----" + object.get("name") + "-----\n";
                res += "파워 레벨 : " + object.get("power") + "      " + "랭크 : " + object.get("power") + '\n';
                res += "현재 트로피 : " + object.get("trophies") + "      " + "최대 트로피 : " + object.get("highestTrophies") + '\n';
                res += "----------\n\n";
            }
        }
        catch(Exception e){
            System.out.println("fail to get userdata");
            e.printStackTrace();
        }
        return res;
    }
}
