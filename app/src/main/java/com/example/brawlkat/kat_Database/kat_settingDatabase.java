package com.example.brawlkat.kat_Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class kat_settingDatabase extends SQLiteOpenHelper {


    // 1. 타입 (플레이어 또는 클럽),
    // 2. 태그
    // 3. 계정명
    // 4. 자신의 계정인지 확인하는 bool 값
    // 을 저장하는 검색기록 저장 데이터베이스

    public kat_settingDatabase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE kataSettingBase (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "ForegroundService BOOLEAN," +
                "AnalyticsService BOOLEAN);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void init(){

        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO kataSettingBase VALUES(null, '" + true + "', '" + true + "');");
    }

    public void update(String change, boolean value){

        int res;
        if(value) res = 1;
        else res = 0;

        SQLiteDatabase database = getWritableDatabase();
        String Sql = "UPDATE kataSettingBase SET " + change + " = " + res;
        database.execSQL(Sql);
    }

    public int getData(String field){

        SQLiteDatabase database = getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM kataSettingBase", null);

        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex(field));
    }
}
