package com.fang.mobileguard.utils;

import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

/**
 * Created by Administrator on 2016/7/27.
 */
public class StreamUtils {
    /**
     *将输入流转换为字符串
     * @param is
     * @return 返回输入流is转换成的String
     */
    public static String Stream2String(InputStream is){

        String tag1 = "StreamUtils";

        //读取过程中存储，保存下来，一次性转换String
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1){
                baos.write(buffer,0,temp);
                Logger.i("tag","11111111"+baos.toString());
            }
            return baos.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
