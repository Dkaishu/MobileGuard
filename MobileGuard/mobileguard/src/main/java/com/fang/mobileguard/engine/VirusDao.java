package com.fang.mobileguard.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/15.
 */
public class VirusDao {
    //1,指定访问数据库的路径
    public static String path = "data/data/com.fang.mobileguard/files/antivirus.db";
    //2,开启数据库,查询数据库中表对应的md5码

    /**
     *
     * @return 数据库中表对应的md5码的List
     */
    public static List<String> getVirusList(){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<String>();
        while(cursor.moveToNext()){
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
