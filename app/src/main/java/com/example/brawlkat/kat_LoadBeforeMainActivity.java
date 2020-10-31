package com.example.brawlkat;

import android.content.Intent;
import android.os.Bundle;

import com.example.brawlkat.database.kat_database;
import com.example.brawlkat.database.kat_favoritesDatabase;
import com.example.brawlkat.database.kat_myAccountDatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class kat_LoadBeforeMainActivity extends AppCompatActivity {

    // database .....................................................................................................//
    public              static                          kat_database                            katabase;
    public              static                          kat_favoritesDatabase                   kataFavoritesBase;
    public              static                          kat_myAccountDatabase                   kataMyAccountBase;
    // ..............................................................................................................//


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        if(this.getClass().getName().equals("com.example.brawlkat.kat_LoadBeforeMainActivity")) {
            katabase = new kat_database(getApplicationContext(), "kat", null, 1);
            kataFavoritesBase = new kat_favoritesDatabase(getApplicationContext(), "katfav", null, 1);
            kataMyAccountBase = new kat_myAccountDatabase(getApplicationContext(), "katma", null, 1);

            if(kataMyAccountBase.size() == 1) {
                kat_SearchThread kset = new kat_SearchThread(this, kat_Player_MainActivity.class);
                String tag = kataMyAccountBase.getTag();
                String realTag = tag.substring(1);
                kset.SearchStart(realTag, "players");
            }
            else{
                Intent intent = new Intent(this, kat_Player_MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
