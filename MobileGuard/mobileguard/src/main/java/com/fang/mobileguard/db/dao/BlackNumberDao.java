package com.fang.mobileguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fang.mobileguard.db.BlackNumberOpenHelper;
import com.fang.mobileguard.db.dommain.BlackNumberInfo;
import com.fang.mobileguard.utils.ConstantValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/20.
 */
public class BlackNumberDao {
    //BlackNumberDao单例模式
    //1,私有化构造方法
    //2,声明一个当前类的对象
    //3,提供一个静态方法,如果当前类的对象为空,创建一个新的对象

    private BlackNumberOpenHelper blackNumberOpenHelper;

    private BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    private static BlackNumberDao blackNumberDao = null;
    public static BlackNumberDao getInstance(Context context){
        if (blackNumberDao == null){
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    //增删改查的方法
    public void insert(String phone , String mode){
        //开启数据库,做写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone",phone);
        cv.put("mode",mode);
        db.insert("blacknumber",null,cv);
        db.close();
    }
    public void delete(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        db.delete("blacknumber","phone = ?",new String[]{phone});
        db.close();
    }
    public void update(String phone , String mode){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mode",mode);
        db.update("blacknumber",cv, "phone = ?",new String[]{phone});
        db.close();
    }
    //一次查询20条,逆序，index为起始点
    public List<BlackNumberInfo> find(int index){
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20;",
                new String[]{index+""});
        List<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }
    //查询所有
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }

    //获取数据库中条目总数
    public int getCount(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;", null);
        if(cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * 获取号码为phone的模式mode，1:短信	2:电话	3:所有	0:没有此条数据
     * @param phone 想要查询的号码
     * @return 1:短信	2:电话	3:所有	0:没有此条数据
     */
    public int getMode(String phone){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone = ?",new String[]{phone}, null, null, null);
        if(cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}


