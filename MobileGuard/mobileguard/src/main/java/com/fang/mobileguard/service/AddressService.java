package com.fang.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.engine.AddressDao;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;
import com.orhanobut.logger.Logger;


/**
 * Created by Administrator on 2016/8/14.
 */
public class AddressService extends Service {

    private TelephonyManager mTM;
    private PhoneStateListener mPSL;

    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private TextView tv_toast;
    private WindowManager mWM;
    private String mAddress;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            tv_toast.setText(mAddress);
        };
    };
    private int[] mDrawableIds;
    private int mScreenHeight;
    private int mScreenWidth;
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mPSL = new MyPhoneStateListener();
        mTM.listen(mPSL, PhoneStateListener.LISTEN_CALL_STATE);
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        //拨出电话的监听（动态注册BroadcastReceiver）
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver,intentFilter);


        super.onCreate();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Logger.i("挂断空闲..0.0.0.0.");
                    //Toast.makeText(getApplicationContext(),incomingNumber,Toast.LENGTH_SHORT);
                    if(mWM!=null && mViewToast!=null){
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，至少有个电话活动。该活动或是拨打（dialing）或是通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃(展示吐司)
                    Logger.i("响铃..0.0.0.0.");
                    //showToast(incomingNumber);
                    //Toast.makeText(getApplicationContext(),incomingNumber,Toast.LENGTH_LONG).show();
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }

    /**
     * 模仿源码，自定义toast
     * @param incomingNumber
     */
    public void showToast(String incomingNumber) {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        //指定吐司的所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT+ Gravity.TOP;

        //吐司显示效果(吐司布局文件),xml-->view(吐司),将吐司挂在到windowManager窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);

//从sp中获取色值文字的索引,匹配图片,用作展示
        mDrawableIds = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        int toastStyleIndex = SpUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        //可以拖动
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startX;
            private int startY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN :
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE :
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();

                        int disX = moveX-startX;
                        int disY = moveY-startY;

                        params.x = params.x+disX;
                        params.y = params.y+disY;

                        //添加判断防止超出屏幕范围
                        //容错处理
                        if(params.x<0){
                            params.x = 0;
                        }
                        if(params.y<0){
                            params.y=0;
                        }
                        if(params.x>mScreenWidth-mViewToast.getWidth()){
                            params.x = mScreenWidth-mViewToast.getWidth();
                        }
                        if(params.y>mScreenHeight-mViewToast.getHeight()-22){
                            params.y = mScreenHeight-mViewToast.getHeight()-22;
                        }
                        //告知窗体吐司需要按照手势的移动,去做位置的更新
                        mWM.updateViewLayout(mViewToast, params);
                        //3,重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, params.x);
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, params.y);
                        break;
                }
                return true;
            }
        });
        //读取sp中存储吐司位置的x,y坐标值
        params.x = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);
        //在窗体上挂在一个view(权限)
        mWM.addView(mViewToast, params);

        //获取到了来电号码以后,需要做来电号码查询
        query(incomingNumber);
    }

    private void query(final String incomingNumber) {
        new Thread(){
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            };
        }.start();
    }

    @Override
    public void onDestroy() {
        //取消对电话状态的监听(开启服务的时候监听电话的对象)
        if(mTM!=null && mPSL!=null){
            mTM.listen(mPSL, PhoneStateListener.LISTEN_NONE);
        }
        //去电监听取消，广播接受者的注销
        if(mInnerOutCallReceiver!=null){
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
        super.onDestroy();
    }

    private class InnerOutCallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            showToast(getResultData());
        }
    }
}
