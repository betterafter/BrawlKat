package com.example.brawlkat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class kat_brawlersParser {

    private                 String                                            data;
    private                 ArrayList<HashMap<String, Object> >               parsedData = new ArrayList<>();

    public kat_brawlersParser(String data){
        this.data = data;
    }

    public ArrayList<HashMap<String, Object> > DataParser() throws Exception {

        JSONObject jsonObject = new JSONObject(data);
        JSONArray list = (JSONArray) jsonObject.get("list");

        for(int i = 0; i < list.length(); i++){

            HashMap<String, Object> map = new HashMap<>();
            JSONObject element = (JSONObject) list.get(i);

            map.put("id", element.get("id"));
            map.put("name", element.get("name"));
            map.put("imageUrl", element.get("imageUrl"));

            parsedData.add(map);
        }

        return parsedData;
    }


    public void testPrint(ArrayList<HashMap<String, Object>> arrayList){
        for(HashMap<String, Object> arr : arrayList){

            System.out.println(arr.get("id"));
            System.out.println(arr.get("name"));
            System.out.println(arr.get("imageUrl"));

        }
    }
}
