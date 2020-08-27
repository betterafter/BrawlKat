package com.example.brawlkat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class kat_database extends SQLiteOpenHelper {


    // 1. 타입 (플레이어 또는 클럽),
    // 2. 태그
    // 3. 계정명
    // 4. 자신의 계정인지 확인하는 bool 값
    // 을 저장하는 검색기록 저장 데이터베이스

    public kat_database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE katabase (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "searchType TEXT," +
                "searchTag TEXT," +
                "userAccount TEXT," +
                "isUserAccount TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public int size(){

        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToFirst();

        int dbSzie = cursor.getCount();
        return dbSzie;
    }

    public void insert(String Type, String Tag, String Account, String isAccount){

        SQLiteDatabase database = getWritableDatabase();

        //database.execSQL("DELETE FROM katabase WHERE searchTag = " + "'" + Tag + "'");

        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToLast();

        do{
            if(cursor.getString(2).equals(Tag)){
                return;
            }
        }
        while(cursor.moveToPrevious());

        database.execSQL("INSERT INTO katabase VALUES(null, '" + Type + "', '" + Tag + "', '" + Account + "', '" + isAccount + "'); " +
                "WHERE ");
    }

    public ArrayList<ArrayList<String> > get(){

        ArrayList<ArrayList<String> > resultList = new ArrayList<>();

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToLast();

        do{
            ArrayList<String> inner = new ArrayList<>();
            for(int i = 1; i < 5; i++){
                inner.add(cursor.getString(i));
            }

            resultList.add(inner);
        }
        while(cursor.moveToPrevious());

        return resultList;
    }

    public void delete(){
        SQLiteDatabase database = getWritableDatabase();

        if(size() > 9){
            Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
            cursor.moveToFirst();

            String currTag = cursor.getString(2);
            database.execSQL("DELETE FROM katabase WHERE searchTag = " + "'" + currTag + "'");
        }

    }


    public void print(){

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToLast();

        do{
            System.out.println(cursor.getString(2));
        }
        while(cursor.moveToPrevious());
    }
}
