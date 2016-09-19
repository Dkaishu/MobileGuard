package com.fang.mobileguard.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.engine.AddressDao;
import com.fang.mobileguard.utils.ToastUtils;

/**
 * 归属地查询
 * Created by Administrator on 2016/8/7.
 */
public class QurryToolActivity extends AppCompatActivity {

    private String mAddress;
    private TextView tv_advtools_address;
    private EditText et_advtools_qurryaddress;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tv_advtools_address.setText(mAddress);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_advtools_qurryaddress);

        initUI();
    }

    private void initUI() {
        et_advtools_qurryaddress = (EditText) findViewById(R.id.et_advtools_qurryaddress);
        tv_advtools_address = (TextView) findViewById(R.id.tv_advtools_address);
        Button bt_advtools_qurryaddress = (Button) findViewById(R.id.bt_advtools_qurryaddress);

        bt_advtools_qurryaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = et_advtools_qurryaddress.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    qurry(phone);
                } else {
                    //et抖动
                    Animation shake = AnimationUtils.loadAnimation(
                            getApplicationContext(), R.anim.shake);
                    et_advtools_qurryaddress.startAnimation(shake);
                    //手机震动
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(100);
                    //规律震动(震动规则(不震动时间,震动时间,不震动时间,震动时间.......),重复次数)
                    //vibrator.vibrate(new long[]{2000,5000,2000,5000}, -1);

                }
            }
        });
        //实时查询(监听输入框文本变化)
        et_advtools_qurryaddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                qurry(et_advtools_qurryaddress.getText().toString());
            }
        });
    }

    /**
     * 根据号码到数据库查归属地
     * @param phone 号码
     */
    private void qurry(final String phone) {
        new Thread() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

}
