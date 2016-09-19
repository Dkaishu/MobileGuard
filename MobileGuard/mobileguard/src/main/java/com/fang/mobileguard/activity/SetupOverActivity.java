package com.fang.mobileguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;
import com.orhanobut.logger.Logger;


/**
 * Created by Administrator on 2016/8/2.
 */
public class SetupOverActivity extends AppCompatActivity {

    private final static String tag = "SetupOverActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Logger.init(tag);

        /**
         * 判断手机防盗设置是否完成，
         */
        boolean setup_over = SpUtils.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            setContentView(R.layout.activity_setup_over);
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }
        initUI();
    }

    private void initUI() {
        TextView tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_phone.setText(SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, ""));

        TextView tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
