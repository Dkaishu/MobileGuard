package com.fang.mobileguard.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.engine.SmsBackUp;

import java.io.File;

/**
 * Created by Administrator on 2016/8/7.
 */
public class AdvToolsActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_advtools);
        //电话归属地查询方法
        initPhoneAddress();
        //短信备份方法
        initSmsBackUp();
        //常用号码查询
        initCommonNumberQuery();
        //程序锁
        initAppLock();
    }

    private void initAppLock() {
        TextView tv_advtools_main_applock = (TextView) findViewById(R.id.tv_advtools_main_applock);
        tv_advtools_main_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        TextView tv_advtools_main_commonnumber = (TextView) findViewById(R.id.tv_advtools_main_commonnumber);
        tv_advtools_main_commonnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    private void initSmsBackUp() {
        //短信备份
        TextView tv_advtools_main_sms = (TextView) findViewById(R.id.tv_advtools_main_sms);
        tv_advtools_main_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }

    private void initPhoneAddress() {
        //归属地查询
        TextView tv_advtools_main_address = (TextView) findViewById(R.id.tv_advtools_main_address);
        tv_advtools_main_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),QurryToolActivity.class));
            }
        });
    }


    private void showSmsBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.splash_alert);
        progressDialog.setTitle("短信备份");
        //2,指定进度条的样式为水平
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //3,展示进度条
        progressDialog.show();
        //4,直接调用备份短信方法即可
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"sms_fang.xml";
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setProgress(int index) {
                        //由开发者自己决定,使用对话框还是进度条
                        progressDialog.setProgress(index);
                        //pb_bar.setProgress(index);
                    }

                    @Override
                    public void setMax(int max) {
                        //由开发者自己决定,使用对话框还是进度条
                        progressDialog.setMax(max);
                        //pb_bar.setMax(max);
                    }
                });

                progressDialog.dismiss();
            }
        }.start();

    }
}
