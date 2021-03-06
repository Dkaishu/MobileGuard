package com.fang.mobileguard.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ToastUtils;

/**
 * Created by Administrator on 2016/9/11.
 */
public class EnterPsdActivity extends AppCompatActivity {
    private String packagename;
    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private EditText et_psd;
    private Button bt_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_enter_psd);
        //获取包名
        packagename = getIntent().getStringExtra("packagename");
        initUI();
        initData();

    }

    private void initData() {
        //通过传递过来的包名获取拦截应用的图标以及名称
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packagename,0);
            Drawable icon = applicationInfo.loadIcon(pm);
            iv_app_icon.setBackgroundDrawable(icon);
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if(!TextUtils.isEmpty(psd)){
                    if(psd.equals("123")){
                        //解锁,进入应用,告知看门口不要再去监听以及解锁的应用,发送广播
                        Intent intent = new Intent("android.intent.action.SKIP");//自定义action
                        intent.putExtra("packagename",packagename);
                        sendBroadcast(intent);

                        finish();
                    }else{
                        ToastUtils.show(getApplicationContext(), "密码错误");
                    }
                }else{
                    ToastUtils.show(getApplicationContext(), "请输入密码");
                }
            }
        });
    }

    private void initUI() {
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);

        et_psd = (EditText) findViewById(R.id.et_psd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    public void onBackPressed() {
        //通过隐式意图,跳转到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
