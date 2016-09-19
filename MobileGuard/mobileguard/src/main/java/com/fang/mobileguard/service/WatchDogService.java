package com.fang.mobileguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fang.mobileguard.activity.EnterPsdActivity;
import com.fang.mobileguard.db.dao.AppLockDao;

import java.util.List;

/**
 * Created by Administrator on 2016/9/7.
 */
public class WatchDogService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private List<String> mPacknameList;
    private InnerReceiver mInnerReceiver;
    private String mSkipPackagename = null;
    private MyContentObserver mContentObserver;
//Todo 已知问题：APP退出WatchDogService不能正常工作
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDao = AppLockDao.getInstance(this);
        isWatch = true;
        watch();
        //接受来自EnterPsdActivity的广播，得到不要再去监听以及解锁的应用packagename
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);
        //注册一个内容观察者,观察数据库的变化,一旦数据有删除或者添加,则需要让mPacknameList重新获取一次数据
        mContentObserver = new MyContentObserver(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, mContentObserver);
        super.onCreate();
    }

    private void watch() {

        new Thread(){
            @Override
            public void run() {
                mPacknameList = mDao.findAll();
                while (isWatch){
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    String packagename = runningTaskInfo.topActivity.getPackageName();
//高版本已经失效，参见
// http://stackoverflow.com/questions/28066231/
// how-to-gettopactivity-name-or-get-current-running-application-package-name-in-lo?noredirect=1&lq=1
                 /* ActivityManager mActivityManager =(ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

                    if(Build.VERSION.SDK_INT > 20){
                        String mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
                    }
                    else{
                        String mpackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                    }*/

                    if (mPacknameList.contains(packagename) && !packagename.equals(mSkipPackagename)){
                        //弹出输入密码的activity
                        Intent intent  = new Intent(getApplicationContext(),EnterPsdActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packagename",packagename);
                        startActivity(intent);
                    }
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        //停止看门狗循环
        isWatch = false;
        //注销广播接受者
        if(mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        //注销内容观察者
        if(mContentObserver!=null){
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackagename = intent.getStringExtra("packagename");
        }
    }

    private class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    mPacknameList = mDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }
}
