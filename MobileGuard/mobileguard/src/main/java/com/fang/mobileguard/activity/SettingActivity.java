package com.fang.mobileguard.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fang.mobileguard.R;
import com.fang.mobileguard.service.AddressService;
import com.fang.mobileguard.service.BlackNumberService;
import com.fang.mobileguard.service.WatchDogService;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.ServiceUtil;
import com.fang.mobileguard.utils.SpUtils;
import com.fang.mobileguard.view.SettingClickView;
import com.fang.mobileguard.view.SettingItemView;

/**
 * Created by Administrator on 2016/7/30.
 */
public class SettingActivity extends AppCompatActivity {
    private String[] mToastStyleDes;
    private int mToastStyle;
    private SettingClickView scv_toast_style;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();

        setContentView(R.layout.activity_home_setting);
        //fen功能模块初始化
        initUpdata();
        initAddress();
        initToastStyle();
        initToastLocation();
        initBlacknumber();
        initAppLock();
    }

    /**
     * 初始化程序锁方法
     */
    private void initAppLock() {
        final SettingItemView siv_app_lock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean isRunning = ServiceUtil.isRunning(this, "com.fang.mobileguard.service.WatchDogService");
        siv_app_lock.setChecked(isRunning);

        siv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = siv_app_lock.isChecked();
                siv_app_lock.setChecked(!isCheck);
                if(!isCheck){
                    //开启服务
                    startService(new Intent(getApplicationContext(), WatchDogService.class));
                }else{
                    //关闭服务
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }
            }
        });
    }


    /**
     *黑名单模式是否开启
     */
    private void initBlacknumber() {
        /**
         * 自动更新开关
         */
        final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);

        boolean open_blacknumber = SpUtils.getBoolean(this,ConstantValue.BLACKNUMBER,false);
        siv_blacknumber.setChecked(open_blacknumber);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = siv_blacknumber.isChecked();
                siv_blacknumber.setChecked(!isChecked);
                //sp中存储当前状态
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.BLACKNUMBER,!isChecked);
                if (!isChecked){
                    startService(new Intent(getApplicationContext(),BlackNumberService.class));
                }else {
                    stopService(new Intent(getApplicationContext(),BlackNumberService.class));

                }
            }
        });

    }


    /**
     * 双击居中view所在屏幕位置的处理方法
     */
    private void initToastLocation() {
        SettingClickView scv_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    /**
     * 自动更新开关
     */
    private void initUpdata() {

        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);

        boolean open_update = SpUtils.getBoolean(this,ConstantValue.OPEN_UPDATE,false);
        siv_update.setChecked(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = siv_update.isChecked();
                siv_update.setChecked(!isChecked);
                //sp中存储当前状态
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE,!isChecked);
            }
        });
/*
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

        //boolean open_address = SpUtils.getBoolean(this,ConstantValue.OPEN_ADDRESS,false);
        //siv_address.setChecked(open_address);
        boolean isRunning = ServiceUtil.isRunning(this, "com.fang.mobileguard.service.AddressService");
        siv_address.setChecked(isRunning);
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = siv_address.isChecked();
                siv_address.setChecked(!isChecked);

                if (!isChecked){
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else {
                    //单纯的关闭并不能终止服务，依然会执行逻辑，要在AddressService的onDestroy()中写相应方法
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
                //sp中存储当前状态
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_ADDRESS,!isChecked);
            }
        });
*/

    }

   /**
     * 归属地显示开关
     */
    private void initAddress() {

        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);

        //boolean open_address = SpUtils.getBoolean(this,ConstantValue.OPEN_ADDRESS,false);
        //siv_address.setChecked(open_address);
        boolean isRunning = ServiceUtil.isRunning(this, "com.fang.mobileguard.service.AddressService");
        siv_address.setChecked(isRunning);
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = siv_address.isChecked();
                siv_address.setChecked(!isChecked);

                if (!isChecked){
                    startService(new Intent(getApplicationContext(),AddressService.class));
                }else {
                    //单纯的关闭并不能终止服务，依然会执行逻辑，要在AddressService的onDestroy()中写相应方法
                    stopService(new Intent(getApplicationContext(),AddressService.class));
                }
                //sp中存储当前状态
                SpUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN_ADDRESS,!isChecked);
            }
        });
    }

    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        //话述(产品)
        scv_toast_style.setTitle("设置归属地显示风格");
        //1,创建描述文字所在的string类型数组
        mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
        //2,SP获取吐司显示样式的索引值(int),用于获取描述文字

        mToastStyle = SpUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);

        //3,通过索引,获取字符串数组中的文字,显示给描述内容控件
        scv_toast_style.setDes(mToastStyleDes[mToastStyle]);
        //4,监听点击事件,弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //5,显示吐司样式的对话框
                showToastStyleDialog();
            }
        });
    }
    /**
     * 创建选中显示样式的对话框
     */
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.splash_alert);
        builder.setTitle("请选择归属地样式");
        //选择单个条目事件监听
		/*
		 * 1:string类型的数组描述颜色文字数组
		 * 2:弹出对画框的时候的选中条目索引值
		 * 3:点击某一个条目后触发的点击事件
		 * */
        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//which选中的索引值
                //(1,记录选中的索引值,2,关闭对话框,3,显示选中色值文字)
                SpUtils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                dialog.dismiss();
                scv_toast_style.setDes(mToastStyleDes[which]);
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
