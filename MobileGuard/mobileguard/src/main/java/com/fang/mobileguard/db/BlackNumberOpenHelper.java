package com.fang.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/8/20.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper{
    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

/*    public BlackNumberOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中表的方法
        db.execSQL("create table blacknumber " +
                "(_id integer primary key autoincrement , phone varchar(20), mode varchar(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
