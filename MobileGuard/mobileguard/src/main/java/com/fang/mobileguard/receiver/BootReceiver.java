package com.fang.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;

/**接收开机广播，若sim卡不一致则发短信给安全号码
 * Created by Administrator on 2016/8/6.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        //2,sp中存储的序列卡号
        String sim_number = SpUtils.getString(context, ConstantValue.SIM_NUMBER, "");
        //3,比对不一致
        if (!sim_number.equals(simSerialNumber)){
            //发送短信
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(SpUtils.getString(context,ConstantValue.CONTACT_PHONE,""),
                    null,"sim has changed !!!",null,null);
        }
    }
}








