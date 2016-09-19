package com.fang.mobileguard.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.fang.mobileguard.service.UpdateWidgetService;

/**
 * Created by Administrator on 2016/9/6.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
    //创建第一个和多创建一个窗体小部件调用方法
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //如果Service已经运行，则只调用onStart()
        context.startService(new Intent(context, UpdateWidgetService.class));
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    //当窗体小部件宽高发生改变的时候调用方法,创建小部件的时候,也调用此方法
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        //开启服务
        context.startService(new Intent(context, UpdateWidgetService.class));
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    //创建第一个窗体小部件的方法调用
    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, UpdateWidgetService.class));
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, UpdateWidgetService.class));
        super.onDisabled(context);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }
}
