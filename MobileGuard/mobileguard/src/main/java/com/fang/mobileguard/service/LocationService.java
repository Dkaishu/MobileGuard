package com.fang.mobileguard.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.SpUtils;


/**
 * 获取经纬度，并发送短信给安全号码
 * Created by Administrator on 2016/8/6.
 */
public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取位置
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//允许花费
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精确度
        String bp = lm.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(bp, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //经度
                double longitude = location.getLongitude();
                //纬度
                double latitude = location.getLatitude();

                //4,发送短信(添加权限)
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(SpUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE,""),
                        null, "longitude = " + longitude + ",latitude = " + latitude, null, null);
                com.orhanobut.logger.Logger.i("longitude = " + longitude + ",latitude = " + latitude);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });


    }
}
