package com.fang.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fang.mobileguard.engine.ProcessInfoProvider;

/**
 * Created by Administrator on 2016/9/6.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //杀死进程
        ProcessInfoProvider.killAll(context);
    }
}
