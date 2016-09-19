package com.fang.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.fang.mobileguard.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/8/31.
 */
public class LockScreenService extends Service {
    private IntentFilter intentFilter;
    private InnerReceiver innerReceiver;
    @Override
    public void onCreate() {

        //锁屏action
        intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, intentFilter);

        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        if(innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
        super.onDestroy();
    }

    class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //清理手机正在运行的进程
            ProcessInfoProvider.killAll(context);
        }
    }
}
