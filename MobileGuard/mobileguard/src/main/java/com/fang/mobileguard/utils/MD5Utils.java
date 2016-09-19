package com.fang.mobileguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/8/1.
 */
public class MD5Utils {
    /**
     * 将String利用MD5加密，返回32位小写字符串
     * @param str
     * @return
     */
    public static String parseStrToMd5L32(String str){
        String reStr = null;
        str = str + "mobileGUARD-!FANG";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes){
                int bt = b&0xff;
                if (bt < 16){
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }
}
