package com.example.brawlkat.kat_Database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class kat_countryDatabase extends SQLiteOpenHelper {

    public kat_countryDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public kat_countryDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE kataFavoritesBase (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "countryCode TEXT," +
                "countryName TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String countryCode, String coutryName){

        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        // 테이블이 비었으면 그냥 삽입하고 종료
        if(cursor.getCount() <= 0){
            database.execSQL("INSERT INTO kataFavoritesBase VALUES(null, '" + countryCode + "', '" + coutryName + "');");
            return;
        }

        String deleteSql = "DELETE FROM kataFavoritesBase";
        database.execSQL(deleteSql);

        // 중복되는 데이터가 없으면 그냥 삽입
        database.execSQL("INSERT INTO kataFavoritesBase VALUES(null, '" + countryCode + "', '" + coutryName + "');");
    }

    public String getCountryCode(){
        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToFirst();

        return cursor.getString(1);
    }

    public String getCountryName(){
        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        return cursor.getString(2);
    }

    public int size(){
        SQLiteDatabase database = getWritableDatabase();

        // 일단 전체 테이블을 선택하고 커서를 마지막으로 보낸다.
        Cursor cursor = database.rawQuery("SELECT * FROM kataFavoritesBase", null);
        cursor.moveToLast();

        return cursor.getCount();
    }
}
