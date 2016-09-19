package com.fang.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.fang.mobileguard.R;
import com.fang.mobileguard.service.LocationService;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;

/**
 * 遍历短信内容，起动相应服务
 * Created by Administrator on 2016/8/6.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //手机防盗是否开启
        boolean is_openned = SpUtils.getBoolean(context, ConstantValue.OPEN_SECURITY,false);
        if (is_openned){
            //获取短信,多条
            Object[] smses = (Object[]) intent.getExtras().get("pdus");
            //3,循环遍历短信过程
            for (Object object:smses) {
                //4,获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
                //5,获取短信对象的基本信息，
                //发送者
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();

                //判断是否包含播放音乐的关键字
                if(messageBody.contains("#*alarm*#")){
                    //7,播放音乐(准备音乐,MediaPlayer)
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if(messageBody.contains("#*location*#")){
                    //8,开启获取位置服务
                    context.startService(new Intent(context,LocationService.class));
                }

                if(messageBody.contains("#*lockscrenn*#")){
                }
                if(messageBody.contains("#*wipedate*#")){
                }

            }

        }
    }
}
