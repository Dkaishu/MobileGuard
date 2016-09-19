package com.fang.mobileguard.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/** 常用号码查询从数据库
 * Created by Administrator on 2016/9/3.
 */
public class CommonnumDao {
    private String path = "data/data/com.fang.mobileguard/files/commonnum.db";
    private List<Child> child;

    public List<Group> getGroup() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List groupList = new ArrayList<Group>();
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name = cursor.getString(0);
            group.idx = cursor.getString(1);
            group.childList = getChild(group.idx);
            groupList.add(group);
        }
        cursor.close();
        db.close();
        return groupList;
    }

    public List<Child> getChild(String idx) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from table"+idx+";",null);
        List childList = new ArrayList<Child>();

        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childList.add(child);
        }
        cursor.close();
        db.close();
        return childList;
    }

    public class Group{
        public String name;
        public String idx;
        public List<Child> childList;
    }

    public class Child{
        public String _id;
        public String number;
        public String name;
    }
}