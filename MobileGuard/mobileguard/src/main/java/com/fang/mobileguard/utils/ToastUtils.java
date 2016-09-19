package com.fang.mobileguard.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/7/28.
 */
public class ToastUtils {
    public static void show(Context context,String string){
        Toast.makeText(context,string,Toast.LENGTH_SHORT).show();
    }
}
