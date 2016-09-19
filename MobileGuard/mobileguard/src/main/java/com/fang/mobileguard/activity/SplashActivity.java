package com.fang.mobileguard.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;
import com.fang.mobileguard.utils.StreamUtils;
import com.fang.mobileguard.utils.ToastUtils;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//已知问题：AlartDialog时，按返回键导致无法进入HomeActivity
public class SplashActivity extends AppCompatActivity {
    /**
     * 日志打印tag
     */
    private final static String tag = "SplashActivity";
    /**
     * 是否更新的状态码
     */
    private final static int ENTER_HOME = 101;
    private final static int NEED_UPDATE = 102;
    /**
     * 请求版本信息时，错误状态码
     */
    private final static int URLEXCEPTION = 201;
    private final static int IOEXCEPTION = 202;
    private final static int JSONEXCEPTION = 203;

    /**
     * UI组件成员变量
     */
    private TextView tv_version_name;
    /**
     * 版本信息成员变量
     */
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;
    private String mVersionName;
    private String mVersionCode;
    /**
     * 消息处理机制，判断是否更新，报告请求错误
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ENTER_HOME:
                    //enterHomeActivity
                    enterHomeActivity();
                    break;
                case NEED_UPDATE:
                    //弹出对话框，提示更新
                    showUpdateDialog();
                    break;
                case URLEXCEPTION:
                    ToastUtils.show(getApplicationContext(),"获取版本信息失败:MalformedURLException");
                    //为确保抛异常后仍能继续
                    enterHomeActivity();
                    break;
                case IOEXCEPTION:
                    //ToastUtils.show(getApplicationContext(),"获取版本信息失败:IOException");
                    TastyToast.makeText(getApplicationContext(), "获取版本信息失败:IOException",
                            TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    enterHomeActivity();
                    break;
                case JSONEXCEPTION:
                    ToastUtils.show(getApplicationContext(),"获取版本信息失败:JSONException");
                    enterHomeActivity();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //在继承的是Activity才奏效
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        Logger.init(tag);
        x.Ext.init(getApplication());

        initUI();
        initData();
        initDB();
        //创建快捷方式
        if (!SpUtils.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)){initShortCut();}
    }

    /**
     * 生成快捷方式
     */
    private void initShortCut() {
        //1,给intent维护图标,名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_2));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getResources().getString(R.string.shortcut_name));
        //2,点击快捷方式后跳转到的activity
        //2.1维护开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3,发送广播
        sendBroadcast(intent);
        //4,告知sp已经生成快捷方式
        SpUtils.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        initAddressDB("address.db");
        initAddressDB("commonnum.db");
        initAddressDB("antivirus.db");
    }

    /**
     * 初始化归属地数据库
     */
    private void initAddressDB(String dbName) {
        //1,在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if(file.exists()){
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        //2,输入流读取第三方资产目录下的文件
        try {
            //特别注意：db文件是要放在main/assets下的
            stream = getAssets().open(dbName);
            //3,将读取的内容写入到指定文件夹的文件中去
            fos = new FileOutputStream(file);
            //4,每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while( (temp = stream.read(bs))!=-1){
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(stream!=null && fos!=null){
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /*private void initAddressDB(String dbName) {
        File files = getFilesDir();
        File file = new File(files,dbName);
        if (file.exists()){return;}

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            //输入流读取第三方资产目录下的文件
            is = getAssets().open(dbName);
            fos = new FileOutputStream(file);
            //每次的读取内容大小
            byte[] bt = new byte[1024];
            int temp = -1;
            while ((temp = is.read(bt)) != -1){
                fos.write(bt,0,temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null && fos != null){
                try {
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);

    }

    /**
     * 初始化版本信息，并发出状态码给Handler
     */
    private void initData() {
        tv_version_name.setText("当前版本： "+getVersionName());
        mLocalVersionCode = getmVersionCode();
        //根据设置判断是否自动更新
        if (SpUtils.getBoolean(this, ConstantValue.OPEN_UPDATE,false)){
            checkVersion();
        }else {
            //enterHomeActivity();]
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,1500);
        }

    }

    /**
     * 向服务发出请求，获取版本信息。
     */
    public void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();
                //确保splash停留3s，
                //获取开始时间
                long startTime = System.currentTimeMillis();

                try {
                    // http://192.168.168.109:8080
                    URL url = new URL("http://192.168.168.109:8080/update_info.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(6000);
                    if (conn.getResponseCode() == 200){
                        InputStream is = conn.getInputStream();
                        String json_version_info = StreamUtils.Stream2String(is);
                        JSONObject jsonObject = new JSONObject(json_version_info);

                        //debug调试,解决问题
                        mVersionName = jsonObject.getString("version");
                        mVersionDes = jsonObject.getString("versionDes");
                        mVersionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        
                        //日志打印
                        //Log.i(tag, json_version_info);
                        //System.out.println(json_version_info);
                        Logger.d(json_version_info);
                        Log.i(tag, mVersionName);
                        Log.i(tag, mVersionDes);
                        Logger.i(mVersionDes);
                        Log.i(tag, mVersionCode);
                        Log.i(tag, mDownloadUrl);

                        if (mLocalVersionCode < Integer.parseInt(mVersionCode)){
                            msg.what=NEED_UPDATE;}else {msg.what= ENTER_HOME;}

                    }else {Log.i(tag,"获取版本信息失败");}


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what=URLEXCEPTION;
                    Log.i(tag,"获取版本信息失败:MalformedURLException");
                }
                catch (IOException e){
                    e.printStackTrace();
                    msg.what=IOEXCEPTION;
                    Log.i(tag,"获取版本信息失败:IOException");
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what=JSONEXCEPTION;
                }finally {

                    long endTime = System.currentTimeMillis();
                    if (endTime-startTime < 3000){
                        try {
                            Thread.sleep((3000-(endTime-startTime)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //注意这句要放在sleep语句后面，否则，当无新版本时，会看不到splash界面而直接进入HomeActivity
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 弹出展示是否下载的对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("有新版本，是否更新")
                .setIcon(R.drawable.splash_alert)
                .setMessage(mVersionDes);
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                enterHomeActivity();
            }
        });
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadAPK();
            }
        });
        builder.create().show();

    }

    private void downloadAPK() {
        //1.判断SD卡是否可用
        //2.获取路径
        //3.下载并放在指定路径
        //4.调用系统Activity安装
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "mobileguard.apk";
            RequestParams params = new RequestParams(mDownloadUrl);
            params.setSaveFilePath(path);


            //params.set;
// 有上传文件时使用multipart表单, 否则上传原始文件流.
// params.setMultipart(true);
// 上传文件方式 1
// params.uploadFile = new File("/sdcard/test.txt");
// 上传文件方式 2
// params.addBodyParameter("uploadFile", new File("/sdcard/test.txt"));
            Callback.Cancelable cancelable
                    = x.http().get(params,
                    /**
                     * 1. callback的泛型:
                     * callback参数默认支持的泛型类型参见{@link org.xutils.http.loader.LoaderFactory},
                     * 例如: 指定泛型为File则可实现文件下载, 使用params.setSaveFilePath(path)指定文件保存的全路径.
                     * 默认支持断点续传(采用了文件锁和尾端校验续传文件的一致性).
                     * 其他常用类型可以自己在LoaderFactory中注册,
                     * 也可以使用{@link org.xutils.http.annotation.HttpResponse}
                     * 将注解HttpResponse加到自定义返回值类型上, 实现自定义ResponseParser接口来统一转换.
                     * 如果返回值是json形式, 那么利用第三方的json工具将十分容易定义自己的ResponseParser.
                     * 如示例代码{@link org.xutils.sample.http.BaiduResponse}, 可直接使用BaiduResponse作为
                     * callback的泛型.
                     *
                     * 2. callback的组合:
                     * 可以用基类或接口组合个种类的Callback, 见{@link org.xutils.common.Callback}.
                     * 例如:
                     * a. 组合使用CacheCallback将使请求检测缓存或将结果存入缓存(仅GET请求生效).
                     * b. 组合使用PrepareCallback的prepare方法将为callback提供一次后台执行耗时任务的机会,
                     * 然后将结果给onCache或onSuccess.
                     * c. 组合使用ProgressCallback将提供进度回调.
                     * ...(可参考{@link org.xutils.image.ImageLoader}
                     * 或 示例代码中的 {@link org.xutils.sample.download.DownloadCallback})
                     *
                     * 3. 请求过程拦截或记录日志: 参考 {@link org.xutils.http.app.RequestTracker}
                     *
                     * 4. 请求Header获取: 参考 {@link org.xutils.http.app.RequestInterceptListener}
                     *
                     * 5. 其他(线程池, 超时, 重定向, 重试, 代理等): 参考 {@link org.xutils.http.RequestParams}
                     *
                     **/
                    new Callback.CommonCallback<File>() {
                        @Override
                        public void onSuccess(File result) {
                            //Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();
                            //File apk = result;
                            install(result);
                        }

                        @Override
                        public void onError(Throwable ex, boolean isOnCallback) {
                            //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                            if (ex instanceof HttpException) { // 网络错误
                                HttpException httpEx = (HttpException) ex;
                                int responseCode = httpEx.getCode();
                                String responseMsg = httpEx.getMessage();
                                String errorResult = httpEx.getResult();
                                // ...
                            } else { // 其他错误
                                // ...
                            }
                            //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(CancelledException cex) {
                            //Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFinished() {

                        }
                    });

// cancelable.cancel(); // 取消请求
        }
    }

    /**
     * 安装apk文件
     * @param result 即下载的apk文件
     */
    private void install(File result) {
        //系统应用界面,源码,安装apk入口
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
		/*//文件作为数据源
		intent.setData(Uri.fromFile(file));
		//设置安装的类型
		intent.setType("application/vnd.android.package-archive");*/
        intent.setDataAndType(Uri.fromFile(result),"application/vnd.android.package-archive");
//		startActivity(intent);
        startActivityForResult(intent, 0);
    }

    /**
     * （系统安装界面）无论安装还是取消都进入HomeActivity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHomeActivity();
    }

    /**
     * 获取本地版本名称
     * @return 返回值为null，则获取失败。
     */
    public String getVersionName(){
        try {
            return getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 获取本地版本号
     * @return 返回值为0，则获取失败。
     */
    public int getmVersionCode(){
        try {
            return getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return 0;
    }

    /**
     * 进入主页
     */
    private void enterHomeActivity() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
