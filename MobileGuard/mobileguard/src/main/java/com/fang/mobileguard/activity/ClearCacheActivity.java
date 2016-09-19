package com.fang.mobileguard.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;



import com.fang.mobileguard.R;
import com.sdsmdg.tastytoast.TastyToast;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/9/15.
 */
public class ClearCacheActivity extends AppCompatActivity{
    protected static final int UPDATE_CACHE_APP = 100;
    protected static final int CHECK_CACHE_APP = 101;
    protected static final int CHECK_FINISH = 102;
    protected static final int CLEAR_CACHE = 103;
    protected static final String tag = "CacheClearActivity";
    //********************************************false表示一件清理不可用，true表可用*************
    private boolean realRun = false;
    //**********************************************************************************
    private Button bt_clear;
    private ProgressBar pb_bar;
    private TextView tv_name;
    private LinearLayout ll_add_text;
    private PackageManager mPm;
    private int mIndex = 0;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_CACHE_APP:
                    //更新UI
                    //8.在线性布局中添加有缓存应用条目
                    View view = View.inflate(getApplicationContext(), R.layout.linearlayout_cache_item, null);

                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_item_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_memory_info = (TextView)view.findViewById(R.id.tv_memory_info);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);

                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon.setBackgroundDrawable(cacheInfo.icon);
                    tv_item_name.setText(cacheInfo.name);
                    tv_memory_info.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));

                    ll_add_text.addView(view, 0);

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //清除单个选中应用的缓存内容(PackageMananger)

						/* 以下代码如果要执行成功则需要系统应用才可以去使用的权限
						 * android.permission.DELETE_CACHE_FILES
						 * try {
							Class<?> clazz = Class.forName("android.content.pm.PackageManager");
							//2.获取调用方法对象
							Method method = clazz.getMethod("deleteApplicationCacheFiles", String.class,IPackageDataObserver.class);
							//3.获取对象调用方法
							method.invoke(mPm, cacheInfo.packagename,new IPackageDataObserver.Stub() {
								@Override
								public void onRemoveCompleted(String packageName, boolean succeeded)
										throws RemoteException {
									//删除此应用缓存后,调用的方法,子线程中
									Log.i(tag, "onRemoveCompleted.....");
								}
							});
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
                            //源码开发课程(源码(handler机制,AsyncTask(异步请求,手机启动流程)源码))
                            //通过查看系统日志,获取开启清理缓存activity中action和data
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.parse("package:"+cacheInfo.packagename));
                            startActivity(intent);
                        }
                    });
                    break;
                case CHECK_CACHE_APP:
                    tv_name.setText((String)msg.obj);
                    break;
                case CHECK_FINISH:
                    tv_name.setText("扫描完成");
                    break;
                case CLEAR_CACHE:
                    //从线性布局中移除所有的条目
                    ll_add_text.removeAllViews();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_clear_cache);
        initUI();
        initData();
    }

    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mPm = getPackageManager();
/* 参数0是表示不接受任何flag信息，当然也能够返回得到一些基本的包信息！但是如 PERMISSIONS ，RECEIVERS
   等等就返回不了，如果flag值不匹配 而方法中强行获取相对应的值，返回值为Null，已经做过测试。
   延伸：PackageManager.GET_ACTIVITIES+ PackageManager.GET_ACTIVITIES等于3；参数中填入3则得到这两个的flag对应的信息。
   注意点：PackageManager.GET_ACTIVITIES|PackageManager.GET_ACTIVITIES和上为一样的效果。因为相或的话同位只要有一个1则为1。
*/
                List<PackageInfo> installedPackages = mPm.getInstalledPackages(PackageManager.GET_RECEIVERS);
                pb_bar.setMax(installedPackages.size());
                for (PackageInfo packageInfo :installedPackages) {
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);
                    try {
                        Thread.sleep(100 + new Random().nextInt(50));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mIndex++;
                    pb_bar.setProgress(mIndex);
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    String name = null;
                    try {
                        name = mPm.getApplicationInfo(packageName, 0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = name;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = CHECK_FINISH;
                mHandler.sendMessage(msg);

            }
        }.start();


    }

    private void getPackageCache(String packageName) {
         IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {

            public void onGetStatsCompleted(PackageStats stats,
                                            boolean succeeded) {
                //子线程中方法,用到消息机制

                //4.获取指定包名的缓存大小
                long cacheSize = stats.cacheSize;
                //5.判断缓存大小是否大于0
                if(cacheSize>0){
                    //6.告知主线程更新UI
                    Message msg = Message.obtain();
                    msg.what = UPDATE_CACHE_APP;
                    CacheInfo cacheInfo = null;
                    try {
                        //7.维护有缓存应用的javabean
                        cacheInfo = new CacheInfo();
                        cacheInfo.cacheSize = cacheSize;
                        cacheInfo.packagename = stats.packageName;
                        cacheInfo.name = mPm.getApplicationInfo(stats.packageName, 0).loadLabel(mPm).toString();
                        cacheInfo.icon = mPm.getApplicationInfo(stats.packageName, 0).loadIcon(mPm);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    msg.obj = cacheInfo;
                    mHandler.sendMessage(msg);
                }
            }
        };
        //1.获取指定类的字节码文件
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            //2.获取调用方法对象
            Method method = clazz.getMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            //3.获取对象调用方法
            method.invoke(mPm, packageName,mStatsObserver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initUI() {
        bt_clear = (Button) findViewById(R.id.bt_clear);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_name = (TextView) findViewById(R.id.tv_name);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
        if (realRun){
            bt_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.获取指定类的字节码文件
                    try {
                        Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                        //2.获取调用方法对象
                        Method method = clazz.getMethod("freeStorageAndNotify", long.class,IPackageDataObserver.class);
                        //3.获取对象调用方法
                        method.invoke(mPm, Long.MAX_VALUE,new IPackageDataObserver.Stub() {
                            @Override
                            public void onRemoveCompleted(String packageName, boolean succeeded)
                                    throws RemoteException {
                                //清除缓存完成后调用的方法(考虑权限)
                                Message msg = Message.obtain();
                                msg.what = CLEAR_CACHE;
                                mHandler.sendMessage(msg);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            TastyToast.makeText(getApplicationContext(),"谨慎操作",TastyToast.LENGTH_SHORT, TastyToast.WARNING);}

    }

    class CacheInfo{
        public String name;
        public Drawable icon;
        public String packagename;
        public long cacheSize;
    }
}
