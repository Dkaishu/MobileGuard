package com.fang.mobileguard.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.engine.CommonnumDao;

import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class CommonNumberQueryActivity extends AppCompatActivity {
    ExpandableListView elv_common_number;
    private List<CommonnumDao.Group> mGroup;
    private MyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_common_number);
        initUI();
        intData();
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
    }

    private void intData() {
        CommonnumDao c = new CommonnumDao();
        mGroup = c.getGroup();
        mAdapter = new MyAdapter();
        elv_common_number.setAdapter(mAdapter);
        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(mGroup.get(groupPosition).childList.get(childPosition).number);
                return false;
            }
        });
    }

    private void startCall(String number) {
        //开启系统的打电话界面
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }


    private class MyAdapter extends BaseExpandableListAdapter{
        @Override
        public int getGroupCount() {
            return mGroup.size();
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childList.size();
        }
        @Override
        public Object getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mGroup.get(groupPosition).childList.get(childPosition);
        }
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public boolean hasStableIds() {
            return false;
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView  tv = new TextView(getApplicationContext());
            tv.setText(mGroup.get(groupPosition).name);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setTextColor(Color.RED);
            tv.setPadding(100,10,0,10);
            return tv;
        }
        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View  view = View.inflate(getApplicationContext(),R.layout.elv_child_item,null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            tv_name.setText(mGroup.get(groupPosition).childList.get(childPosition).name);
            tv_number.setText(mGroup.get(groupPosition).childList.get(childPosition).number);
            return view;
        }
        //孩子节点是否响应事件
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
