package com.fang.mobileguard.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.fang.mobileguard.R;
import com.fang.mobileguard.engine.ProcessInfoProvider;
import com.fang.mobileguard.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/6.
 */
public class UpdateWidgetService extends Service {
    protected static final String tag = "UpdateWidgetService";
    private Timer mTimer;
    private InnerReceiver mInnerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        //管理进程总数和可用内存数更新(定时器)
        startTimer();

        //注册开锁,解锁广播接受者
        IntentFilter intentFilter = new IntentFilter();
        //开锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //解锁action
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);

        super.onCreate();
    }

    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //开启定时更新任务
                startTimer();
            }else{
                //关闭定时更新任务
                cancelTimerTask();
            }
        }
    }

    private void cancelTimerTask() {
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }

    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //ui定时刷新
                updateAppWidget();
            }
        }, 0, 5000);

    }

    private void updateAppWidget() {
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
        remoteViews.setTextViewText(R.id.tv_process_count,"进程总数:"+ ProcessInfoProvider.getProcessCount(this));
        remoteViews.setTextViewText(R.id.tv_process_memory,"可用内存:"+
                Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this)));

        //点击窗体小部件,进入应用
        //1:在那个控件上响应点击事件2:延期的意图
/*          <action android:name="android.intent.action.HOME" />
            <category android:name="android.intent.category.DEFAULT" />*/
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        //通过延期意图发送广播,在广播接受者中杀死进程,匹配规则看action
        Intent broadCastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadCastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_clear,broadcast);

        //上下文环境,窗体小部件对应广播接受者的字节码文件
        ComponentName componentName = new ComponentName(this,MyAppWidgetProvider.class);
        //更新窗体小部件
        aWM.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onDestroy() {
        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        //调用onDestroy即关闭服务,关闭服务的方法在移除最后一个窗体小部件的时调用,定时任务也没必要维护
        cancelTimerTask();
        super.onDestroy();
    }
}
