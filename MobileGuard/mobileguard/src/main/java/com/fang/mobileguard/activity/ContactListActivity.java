package com.fang.mobileguard.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.orhanobut.logger.Logger;

import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ContactListActivity extends AppCompatActivity{
    private final static String tag = "ContactListActivity";

    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
    private MyAdapter mAdapter;
    private ListView lv_contact;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_contact_list);

        Logger.init(tag);
        x.Ext.init(getApplication());

        initUI();
        initData();
    }



    /**
     *
     * 获取系统联系人
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"},
                        null, null, null
                );
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"},
                            "raw_contact_id = ?",
                            new String[]{id},
                            null);

                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);//0表示"data1"
                        String type = indexCursor.getString(1);//1表示"mimetype"

                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            //数据非空判断
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                //7,消息机制,发送一个空的消息,告知主线程可以去使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        /*lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mAdapter != null){
                    HashMap<String,String> hashMap = mAdapter.getItem(i);
                    String phone = hashMap.get("phone");
                    //getIntent().putExtra("phone",phone);

                    //4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);


                    Logger.i(phone);
                    finish();

                }
            }
        });*/

        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                //1,获取点中条目的索引指向集合中的对象
                if(mAdapter!=null){
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    //2,获取当前条目指向集合对应的电话号码
                    String phone = hashMap.get("phone");
                    //3,此电话号码需要给第三个导航界面使用

                    //4,在结束此界面回到前一个导航界面的时候,需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone", phone);
                    setResult(0, intent);

                    finish();
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View v, ViewGroup viewGroup) {
            View view = View.inflate(getApplicationContext(),R.layout.listview_contact_item,null);
            //易错点 view.findViewById...
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);

            tv_name.setText(getItem(i).get("name"));
            tv_phone_number.setText(getItem(i).get("phone"));

            return view;
        }
    }
}
