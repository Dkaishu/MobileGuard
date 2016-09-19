package com.fang.mobileguard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.orhanobut.logger.Logger;

/**自定义组合控件
 * 设置中心界面，将一个条目的XML转换为View的类，
 * Created by Administrator on 2016/7/30.
 */
public class SettingItemView extends RelativeLayout {

    private TextView tv_setting_item_desc;
    private CheckBox cb_setting_item;

    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    private static final String NAMESPACE = "http://schemas.android.com/apk/com.fang.mobileguard";
    private static final String tag = "SettingItemView";



    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_item_view,this);
        initAttrs(attrs);

        TextView tv_setting_item_title = (TextView) findViewById(R.id.tv_setting_item_title);
        tv_setting_item_desc = (TextView) findViewById(R.id.tv_setting_item_desc);
        cb_setting_item = (CheckBox) findViewById(R.id.cb_setting_item);

        tv_setting_item_title.setText(mDestitle);


    }

    private void initAttrs(AttributeSet attrs) {
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");

        Logger.i(tag, mDestitle);
        Logger.i(tag, mDesoff);
        Logger.i(tag, mDeson);
    }

    /**
     * 判断cb是否被选中
     * @return
     */
    public boolean isChecked(){
        return cb_setting_item.isChecked();
    }

    /**
     * cb改变，描述跟着变
     * @param isChecked
     */
    public void setChecked(boolean isChecked){
        cb_setting_item.setChecked(isChecked);
        if (isChecked){
            tv_setting_item_desc.setText(mDeson);
        }else {            tv_setting_item_desc.setText(mDesoff);
        }
    }


}
