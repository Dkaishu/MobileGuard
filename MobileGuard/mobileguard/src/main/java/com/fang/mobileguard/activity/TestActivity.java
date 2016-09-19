package com.fang.mobileguard.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.fang.mobileguard.R;
import com.orhanobut.logger.Logger;

import org.xutils.x;

/**
 * Created by Administrator on 2016/7/31.
 */
public class TestActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_home);

/*        Logger.init(tag);
        x.Ext.init(getApplication());

        initUI();
        initData();*/
    }
}
