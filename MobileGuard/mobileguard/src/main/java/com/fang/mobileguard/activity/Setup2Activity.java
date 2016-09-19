package com.fang.mobileguard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;
import com.fang.mobileguard.utils.ToastUtils;
import com.fang.mobileguard.view.SettingItemView;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2016/8/2.
 */
public class Setup2Activity extends AppCompatActivity {
    private final static String tag = "Setup2Activity";
    private SettingItemView siv_sim_bound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Logger.init(tag);

        setContentView(R.layout.activity_setup2);

        initUI();

    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1,回显(读取已有的绑定状态,用作显示,sp中是否存储了sim卡的序列号)
        String sim_number = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        //2,判断是否序列卡号为""
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setChecked(false);
        } else {
            siv_sim_bound.setChecked(true);
        }

        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3,获取原有的状态
                boolean isCheck = siv_sim_bound.isChecked();
                //4,将原有状态取反
                //5,状态设置给当前条目
                siv_sim_bound.setChecked(!isCheck);
                if (!isCheck) {
                    //6,存储(序列卡号)
                    //6.1获取sim卡序列号TelephoneManager
                    TelephonyManager manager = (TelephonyManager)
                            getSystemService(Context.TELEPHONY_SERVICE);
                    //6.2获取sim卡的序列卡号
                    String simSerialNumber = manager.getSimSerialNumber();
                    //6.3存储
                    SpUtils.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);
                    Logger.i(simSerialNumber);
                } else {
                    //7,将存储序列卡号的节点,从sp中删除掉
                    SpUtils.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

    public void nextPage(View view) {
        String serialNumber = SpUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);

            finish();

            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtils.show(this, "请绑定sim卡");
        }
    }

    public void prePage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);

        finish();

        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

}
