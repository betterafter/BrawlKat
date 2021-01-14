package com.example.brawlkat.kat_Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_favoritesDatabase extends SQLiteOpenHelper {


    public kat_favoritesDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE kataFavoritesBase (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "searchType TEXT, " +
                "searchTag TEXT, " +
                "userAccount TEXT," +
                "userTrophies TEXT," +
                "userHighestTropthies TEXT," +
                "userIconID TEXT," +
                "userLevel TEXT" +
                ")";
        sqLiteDatabase.execSQL(sql);
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

    public void insert(String Type, String Tag, String Account, String Trophies, String HighestTrophies, String IconId, String Level){

        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();


        //2020.11.12 - no colums 에러 : string을 잘못 썼음. string 제대로 확인할 것.
        String sql = "INSERT INTO kataFavoritesBase VALUES(null, "
                + "'" + Type + "'" + ", "
                + "'" + Tag + "'" + ", "
                + "'" + Account + "'" + ", "
                + "'" + Trophies + "'" + ", "
                + "'" + HighestTrophies + "'" + ", "
                + "'" + IconId + "'" + ", "
                + "'" + Level + "'"
                + ")";

        // 테이블이 비었으면 그냥 삽입하고 종료
        if(cursor.getCount() <= 0){
            database.execSQL(sql);
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
        database.execSQL(sql);
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
                for (int i = 1; i < 8; i++) {
                    inner.add(cursor.getString(i));
                }

                resultList.add(inner);
            }
        }
        while (cursor.moveToPrevious());


        return resultList;
    }

    public void delete(String tag){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM kataFavoritesBase WHERE searchTag = " + "'" + tag + "'");
    }

    public ArrayList< ArrayList<String> > getItem(){

        ArrayList<ArrayList<String> > result = new ArrayList<>();
        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToFirst();

        if(cursor.getCount() > 0) {
            do {
                ArrayList<String> res = new ArrayList<>();
                res.add(cursor.getString(0));
                res.add(cursor.getString(1));
                res.add(cursor.getString(2));
                res.add(cursor.getString(3));
                res.add(cursor.getString(4));
                res.add(cursor.getString(5));
                res.add(cursor.getString(6));
                res.add(cursor.getString(7));
                result.add(res);
            }
            while (cursor.moveToNext());
        }

        return result;
    }


//    public void print(){
//
//        SQLiteDatabase database = getWritableDatabase();
//
//        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
//        cursor.moveToLast();
//
//        if(cursor.getCount() > 0) {
//            do {
//                System.out.println(cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3) +
//                        ", " + cursor.getString(4) + ", " + cursor.getString(5) + ", " + cursor.getString(6) +
//                        cursor.getString(7));
//            }
//            while (cursor.moveToNext());
//        }
//    }
}
