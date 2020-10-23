package com.example.brawlkat.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_favoritesDatabase extends SQLiteOpenHelper {


    // 1. 타입 (플레이어 또는 클럽),
    // 2. 태그
    // 3. 계정명
    // 4. 자신의 계정인지 확인하는 bool 값
    // 을 저장하는 검색기록 저장 데이터베이스

    public kat_favoritesDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE kataFavoritesBase (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "searchType TEXT," +
                "searchTag TEXT," +
                "userAccount TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int size(){

        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToFirst();

        int dbSzie = cursor.getCount();
        return dbSzie;
    }

    public void insert(String Type, String Tag, String Account){

        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        // 테이블이 비었으면 그냥 삽입하고 종료
        if(cursor.getCount() <= 0){
            database.execSQL("INSERT INTO kataFavoritesBase VALUES(null, '" + Type + "', '" + Tag + "', '" + Account + "');");
            return;
        }

        // 테이블이 비어있지 않다면 현재 저장하고자 하는 데이터와 중복되는 데이터가 있는지 체크
        do{
            if(cursor.getString(2).equals(Tag)){
                return;
            }
        }
        while(cursor.moveToPrevious());

        // 중복되는 데이터가 없으면 그냥 삽입
        database.execSQL("INSERT INTO kataFavoritesBase VALUES(null, '" + Type + "', '" + Tag + "', '" + Account + "');");
    }

    public boolean isFavorites(String Tag){
        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        // 테이블이 비었으면 그냥 삽입하고 종료
        if(cursor.getCount() <= 0) return false;

        // 테이블이 비어있지 않다면 현재 저장하고자 하는 데이터와 중복되는 데이터가 있는지 체크
        do{
            if(cursor.getString(2).equals(Tag)){
                return true;
            }
        }
        while(cursor.moveToPrevious());

        return false;
    }

    public ArrayList<ArrayList<String> > get(String type){

        ArrayList<ArrayList<String> > resultList = new ArrayList<>();

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase WHERE searchType = " + "'" + type + "'", null);
        cursor.moveToLast();

        if(cursor.getCount() <= 0) return resultList;
        do {
            if(cursor.getString(1).equals(type)) {
                ArrayList<String> inner = new ArrayList<>();
                for (int i = 1; i < 5; i++) {
                    inner.add(cursor.getString(i));
                }

                resultList.add(inner);
            }
        }
        while (cursor.moveToPrevious());


        return resultList;
    }

    public void SetFavorites(String tag){

        SQLiteDatabase database = getWritableDatabase();
        System.out.println("your tag : " + tag);
        String sql = "UPDATE kataFavoritesBase SET isUserAccount = 'YES' WHERE searchTag = " + tag;
        database.execSQL(sql);

    }

    public void SetNotFavorites(String tag){

        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE kataFavoritesBase SET isUserAccount = 'NO' WHERE searchTag = tag";
        database.execSQL(sql);
    }

    public void delete(String tag){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM kataFavoritesBase WHERE searchTag = " + "'" + tag + "'");
    }


    public void print(){

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        if(cursor.getCount() > 0) {
            do {
                System.out.println(cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3));
            }
            while (cursor.moveToPrevious());
        }
    }
}
