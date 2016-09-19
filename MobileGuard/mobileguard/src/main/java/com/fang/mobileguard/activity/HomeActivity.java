package com.fang.mobileguard.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.utils.ConstantValue;
import com.fang.mobileguard.utils.MD5Utils;
import com.fang.mobileguard.utils.SpUtils;
import com.fang.mobileguard.utils.ToastUtils;
import com.orhanobut.logger.Logger;

import org.xutils.x;

/**
 * Created by Administrator on 2016/7/28.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * 日志打印tag
     */
    private final static String tag = "HomeActivity";
    /**
     * 是否更新的状态码
     */
    private final static int ENTER_HOME = 101;
    private final static int NEED_UPDATE = 102;
    /**
     * 请求版本信息时，错误状态码
     */
    private final static int URLEXCEPTION = 201;
    private final static int IOEXCEPTION = 202;
    private final static int JSONEXCEPTION = 203;

    /**
     * UI组件成员变量
     */
    private GridView gv_home;
    /**
     * 成员变量
     */
    private String[] mTitleStrs;
    private int[] mIconID;
    private String mDownloadUrl;
    private String mVersionName;
    private String mVersionCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //上一句不奏效
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_home);

        Logger.init(tag);
        x.Ext.init(getApplication());

        initUI();
        initData();
    }

    private void initData() {
        //准备数据(文字(9组),图片(9张))
        mTitleStrs = new String[]{
                "手机防盗", "通信卫士", "软件管理",
                "进程管理", "流量统计", "手机杀毒",
                "缓存清理", "高级工具", "设置中心"
        };
        mIconID = new int[]{
                R.drawable.icon_1,
                R.drawable.icon_2,
                R.drawable.icon_3,
                R.drawable.icon_4,
                R.drawable.icon_5,
                R.drawable.icon_6,
                R.drawable.icon_7,
                R.drawable.icon_8,
                R.drawable.icon_9,
        };
        gv_home.setAdapter(new MyAdapter() {
        });

        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        showAlertDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(),ProcessManagerActivity.class));
                        break;
                    case 4:
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(),AntiVirus.class));
                        break;
                    case 6:
                        startActivity(new Intent(getApplicationContext(),ClearCacheActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(),AdvToolsActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    private void showAlertDialog() {
        String psd = SpUtils.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetPsdDialog();
        } else {
            showConfirmPsdDialog();
        }
    }

    /**
     * 弹出输入密码对话框
     */
    private void showConfirmPsdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        dialog.setView(view);
        dialog.show();

        Button bt_confirm_psd_no = (Button) view.findViewById(R.id.bt_confirm_psd_no);
        Button bt_confirm_psd_yes = (Button) view.findViewById(R.id.bt_confirm_psd_yes);
        bt_confirm_psd_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et_confirm_psd = ((EditText) view.findViewById(R.id.et_confirm_psd)).getText().toString();
                if (!TextUtils.isEmpty(et_confirm_psd)) {
                    String psd = SpUtils.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    Logger.i("输入的密码：" + MD5Utils.parseStrToMd5L32(et_confirm_psd));
                    Logger.i("原密码：" + psd);
                    if (MD5Utils.parseStrToMd5L32(et_confirm_psd).equals(psd)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                    } else {
                        ToastUtils.show(getApplicationContext(), "密码错误!");
                    }

                } else {
                    ToastUtils.show(getApplicationContext(), "请输入密码!");
                }
            }
        });
        bt_confirm_psd_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * 弹出设置密码对话框
     */
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
        dialog.setView(view);
        dialog.show();
//注意view.findViewById，不是findViewById
        Button bt_set_psd_no = (Button) view.findViewById(R.id.bt_set_psd_no);
        Button bt_set_psd_yes = (Button) view.findViewById(R.id.bt_set_psd_yes);
        bt_set_psd_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et_set_psd = ((EditText) view.findViewById(R.id.et_set_psd)).getText().toString();
                String et_set_psd_confirm = ((EditText) view.findViewById(R.id.et_set_psd_confirm)).getText().toString();
                if (!TextUtils.isEmpty(et_set_psd) && !TextUtils.isEmpty(et_set_psd_confirm)) {
                    if (et_set_psd.equals(et_set_psd_confirm)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        SpUtils.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, MD5Utils.parseStrToMd5L32(et_set_psd));
                        Logger.i("初次设置的密码：" + MD5Utils.parseStrToMd5L32(et_set_psd));

                    } else {
                        ToastUtils.show(getApplicationContext(), "密码输入不一致!");
                    }

                } else {
                    ToastUtils.show(getApplicationContext(), "请输入密码!");
                }
            }
        });
        bt_set_psd_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    private void initUI() {

        gv_home = (GridView) findViewById(R.id.gv_home);

    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mIconID.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStrs[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

/*        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(getApplicationContext(),R.layout.gridview_item,null);
            TextView tv_title_item = (TextView) view.findViewById(R.id.tv_title_item);
            ImageView iv_icon_item = (ImageView) view.findViewById(R.id.iv_icon_item);

            tv_title_item.setText(mTitleStrs[i]);
            iv_icon_item.setBackgroundResource(mIconID[i]);

            return view1;*/

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title_item);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon_item);

            tv_title.setText(mTitleStrs[position]);
            iv_icon.setBackgroundResource(mIconID[position]);

            return view;
        }
    }
}
