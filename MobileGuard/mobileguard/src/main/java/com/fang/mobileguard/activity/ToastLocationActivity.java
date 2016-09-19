package com.fang.mobileguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;

/**
 * Created by Administrator on 2016/8/17.
 */
public class ToastLocationActivity extends Activity {
    private ImageView iv_drag;
    private Button bt_top,bt_bottom;
    private WindowManager mWM;
    private int mScreenHeight;
    private int mScreenWidth;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    private void initUI() {
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        bt_top = (Button) findViewById(R.id.bt_top);
        bt_bottom = (Button) findViewById(R.id.bt_bottom);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();


        int locationX = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        int locationY = SpUtils.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        //左上角坐标作用在iv_drag上
        //iv_drag在相对布局中,所以其所在位置的规则需要由相对布局提供

        //指定宽高都为WRAP_CONTENT
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将左上角的坐标作用在iv_drag对应规则参数上
        layoutParams.leftMargin = locationX;
        layoutParams.topMargin = locationY;
        //将以上规则作用在iv_drag上
        iv_drag.setLayoutParams(layoutParams);
        //根据位置判断上下两个bt的显示与否
        if(locationY>mScreenHeight/2){
            bt_bottom.setVisibility(View.INVISIBLE);
            bt_top.setVisibility(View.VISIBLE);
        }else{
            bt_bottom.setVisibility(View.VISIBLE);
            bt_top.setVisibility(View.INVISIBLE);
        }

        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
                mHits[mHits.length-1] = SystemClock.uptimeMillis();
                if(mHits[mHits.length-1]-mHits[0]<500){
                    //满足双击事件后,调用代码
                    int left = mScreenWidth/2 - iv_drag.getWidth()/2;
                    int top = mScreenHeight/2 - iv_drag.getHeight()/2;
                    int right = mScreenWidth/2+iv_drag.getWidth()/2;
                    int bottom = mScreenHeight/2+iv_drag.getHeight()/2;

                    //控件按以上规则显示
                    iv_drag.layout(left, top, right, bottom);

                    //存储最终位置
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                    SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                }
            }
        });

        iv_drag.setOnTouchListener(new View.OnTouchListener() {
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

                        //1,当前控件所在屏幕的(左,上)角的位置
                        int left = iv_drag.getLeft()+disX;//左侧坐标
                        int top = iv_drag.getTop()+disY;//顶端坐标
                        int right = iv_drag.getRight()+disX;//右侧坐标
                        int bottom = iv_drag.getBottom()+disY;//底部坐标

                        //添加判断防止超出屏幕范围
                        if (left<0||top<22||right>mScreenWidth||bottom>mScreenHeight-22){return true;}

                        if(top>mScreenHeight/2){
                            bt_bottom.setVisibility(View.INVISIBLE);
                            bt_top.setVisibility(View.VISIBLE);
                        }else{
                            bt_bottom.setVisibility(View.VISIBLE);
                            bt_top.setVisibility(View.INVISIBLE);
                        }
                        iv_drag.layout(left,top,right,bottom);
                        //3,重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                        SpUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                        break;

                }
                return false;
            }
        });
    }
}
