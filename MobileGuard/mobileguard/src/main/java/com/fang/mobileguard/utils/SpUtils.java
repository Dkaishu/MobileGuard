package com.fang.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

/** SharedPreferences用于保存设置的状态，密码等。
 * Created by Administrator on 2016/7/31.
 */
public class SpUtils {

    private static SharedPreferences sp;
    //读方法和写方法

    /**
     * 向sp中写入配置
     * @param context
     * @param key 配置的名称
     * @param value 是否开启
     */
    public static void putBoolean(Context context,String key,Boolean value){
        if (sp == null){
         sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     *
     * @param context
     * @param key 配置的名称
     * @param defValue 为当sp不存在时的默认值，false即可
     * @return boolean 是否开启
     */
    public static boolean getBoolean(Context context,String key,Boolean defValue){
        if (sp == null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        //defValue
        return sp.getBoolean(key,defValue);
    }
    /**
     * 向sp中写入配置
     * @param context
     * @param key
     * @param value
     */
    public static void putString(Context context,String key,String value){
        if (sp == null){
         sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     *
     * @param context
     * @param key
     * @param defValue 为当sp不存在时的默认值，空字符串即可
     * @return boolean
     */
    public static String getString(Context context,String key,String defValue){
        if (sp == null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        //defValue
        return sp.getString(key,defValue);
    }

    /**
     * 从sp中移除指定节点
     * @param ctx	上下文环境
     * @param key	需要移除节点的名称
     */
    public static void remove(Context ctx, String key) {
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    /**
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(Context context,String key,int defValue) {
        if (sp == null){
            sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        //defValue
        return sp.getInt(key,defValue);
    }

    /**
     *
     * @param ctx
     * @param key
     * @param value
     */
    public static void putInt(Context ctx,String key,int value){
        //(存储节点文件名称,读写方式)
        if(sp == null){
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }
}






