package com.fang.mobileguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fang.mobileguard.R;
import com.orhanobut.logger.Logger;


/**
 * Created by Administrator on 2016/8/2.
 */
public class Setup1Activity extends AppCompatActivity{
    private final static String tag = "Setup1Activity";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Logger.init(tag);

        setContentView(R.layout.activity_setup1);



    }

    public void nextPage(View viewm){
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);

        finish();
        //平移动画
        overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);

    }

}