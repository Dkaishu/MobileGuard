package com.fang.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fang.mobileguard.R;

/**自定义组合控件,toast样式
 * 设置中心界面，将一个条目的XML转换为View的类，
 * Created by Administrator on 2016/7/30.
 */
public class SettingClickView extends RelativeLayout {

    private TextView tv_setting_item_desc;
    TextView tv_setting_item_title;

    public SettingClickView(Context context) {
        this(context,null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_click_view,this);

        tv_setting_item_title = (TextView) findViewById(R.id.tv_setting_item_title);
        tv_setting_item_desc = (TextView) findViewById(R.id.tv_setting_item_desc);

    }

    /**
     * @param title	设置标题内容
     */
    public void setTitle(String title){
        tv_setting_item_title.setText(title);
    }

    /**
     * @param des	设置描述内容
     */
    public void setDes(String des){
        tv_setting_item_desc.setText(des);
    }



}
