package com.example.brawlkat.kat_Database;

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

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToLast();

        // 테이블이 비었으면 그냥 삽입하고 종료
        if(cursor.getCount() <= 0){
            database.execSQL("INSERT INTO katabase VALUES(null, '" + Type + "', '" + Tag + "', '" + Account + "', '" + isAccount + "');");
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
        database.execSQL("INSERT INTO katabase VALUES(null, '" + Type + "', '" + Tag + "', '" + Account + "', '" + isAccount + "');");
    }

    public ArrayList<ArrayList<String> > get(String type){

        ArrayList<ArrayList<String> > resultList = new ArrayList<>();

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM katabase WHERE searchType = " + "'" + type + "'", null);
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


    public void delete(String type){
        SQLiteDatabase database = getWritableDatabase();

        if(size() > 9){
            Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
            cursor.moveToFirst();

            if(cursor.isFirst()) return;
            String currTag = cursor.getString(2);
            database.execSQL("DELETE FROM katabase WHERE searchTag = " + "'" + currTag + "'");
        }

    }


    public void print(){

        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM katabase", null);
        cursor.moveToLast();

        if(cursor.getCount() > 0) {
            do {
                System.out.println(cursor.getString(1) + ", " + cursor.getString(2) + ", " + cursor.getString(3) + ", "
                        + cursor.getString(4));
            }
            while (cursor.moveToPrevious());
        }
    }
}
