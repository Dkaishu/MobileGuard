package com.fang.rocket;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/18.
 */
public class RocketService extends Service{
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private WindowManager mWM;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mParams.y = (int) msg.obj;
            mWM.updateViewLayout(mViewToast,mParams);
        };
    };
    private int mScreenHeight;
    private int mScreenWidth;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        showToast();
        super.onCreate();
    }

    /**
     * 模仿源码，自定义toast
     */
    public void showToast() {
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
        mViewToast = View.inflate(this, R.layout.rocket_view, null);
        //加载动画
        AnimationDrawable animationDrawable = (AnimationDrawable) mViewToast.findViewById(R.id.iv_rocket).getBackground();
        animationDrawable.start();

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
                        //判断是否发射
                        if (params.x>mScreenWidth/3&&params.x<mScreenWidth*2/3-mViewToast.getWidth()
                                &&params.y>mScreenHeight*2/3-mViewToast.getHeight()){
                            fire();
                        }
                        break;
                }
                return true;
            }
        });
        //在窗体上挂载view(权限)
        mWM.addView(mViewToast, params);

    }

    private void fire() {
        //火箭逐渐上移
        new Thread(){
            @Override
            public void run() {
                int y = mScreenHeight;

                for (int i = 0;i <10;i++){
                    y = y - mScreenHeight/10;
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = y;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
        //启动烟雾
        startActivity(new Intent(getApplicationContext(),SmogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    //重写，关闭时移除动画，否则无法停止
    @Override
    public void onDestroy() {
        if(mWM!=null && mViewToast!=null){
            mWM.removeView(mViewToast);
        }
        super.onDestroy();
    }
}
