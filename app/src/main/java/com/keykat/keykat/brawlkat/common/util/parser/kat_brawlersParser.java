package com.keykat.keykat.brawlkat.common.util.parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class kat_brawlersParser implements Serializable{

    private                 String                                            data;
    private                 ArrayList<HashMap<String, Object> >               parsedData = new ArrayList<>();

    public kat_brawlersParser(String data){
        this.data = data;
    }

    public ArrayList<HashMap<String, Object> > DataParser() throws Exception {


        JSONObject jsonObject = new JSONObject(data);
        if(!jsonObject.isNull("list")) {
            JSONArray list = (JSONArray) jsonObject.get("list");

            for (int i = 0; i < list.length(); i++) {

                HashMap<String, Object> map = new HashMap<>();
                JSONObject element = (JSONObject) list.get(i);

                map.put("id", element.get("id"));
                map.put("name", element.get("name"));
                map.put("imageUrl", element.get("imageUrl"));

                if (!element.isNull("starPowers")) {
                    JSONArray starPowers = element.getJSONArray("starPowers");
                    ArrayList<StarPowers> starPowersArrayList = new ArrayList<>();
                    for (int j = 0; j < starPowers.length(); j++) {
                        JSONObject elem = starPowers.getJSONObject(j);
                        StarPowers sp = new StarPowers();
                        sp.setId(elem.getString("id"));
                        sp.setName(elem.getString("name"));
                        sp.setImageUrl(elem.getString("imageUrl"));
                        starPowersArrayList.add(sp);
                    }
                    map.put("starPowers", starPowersArrayList);
                }

                if (!element.isNull("gadgets")) {
                    JSONArray gadgets = element.getJSONArray("gadgets");
                    ArrayList<Gadgets> gadgetsArrayList = new ArrayList<>();
                    for (int j = 0; j < gadgets.length(); j++) {
                        JSONObject elem = gadgets.getJSONObject(j);
                        Gadgets gg = new Gadgets();
                        gg.setId(elem.getString("id"));
                        gg.setName(elem.getString("name"));
                        gg.setImageUrl(elem.getString("imageUrl"));
                        gadgetsArrayList.add(gg);
                    }
                    map.put("gadgets", gadgetsArrayList);
                }
                parsedData.add(map);
            }
        }

        return parsedData;
    }

    public class StarPowers implements Serializable{
        String id, name, imageUrl;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public class Gadgets implements Serializable{
        String id, name, imageUrl;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

//    public void testPrint(ArrayList<HashMap<String, Object>> arrayList){
//        for(HashMap<String, Object> arr : arrayList){
//
//            System.out.println(arr.get("id"));
//            System.out.println(arr.get("name"));
//            System.out.println(arr.get("imageUrl"));
//
//        }
//    }
}
