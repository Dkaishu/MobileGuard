package com.fang.rocket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;

/**
 * Created by Administrator on 2016/8/18.
 */
public class SmogActivity extends Activity{
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(500);
        findViewById(R.id.iv_top).setAnimation(alphaAnimation);
        findViewById(R.id.iv_bottom).setAnimation(alphaAnimation);
        mhandler.sendEmptyMessageDelayed(0,550);
    }
}
