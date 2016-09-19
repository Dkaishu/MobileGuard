package com.fang.mobileguard.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fang.mobileguard.R;
import com.fang.mobileguard.db.dao.BlackNumberDao;
import com.fang.mobileguard.db.dommain.BlackNumberInfo;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.AbstractSequentialList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/19.
 */
public class BlackNumberActivity extends AppCompatActivity{

    private Button bt_blacknumber_add;
    private ListView lv_blacknumber;

    private int mode = 1;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private MyAdapter mAdapter;
    private boolean mIsLoading = false;
    private int mCount;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (mAdapter == null){
                mAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(mAdapter);
            }else {
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_black_number);

        initUI();
        initData();
    }

    private void initUI() {
        //添加黑名单
        bt_blacknumber_add = (Button) findViewById(R.id.bt_blacknumber_add);
        bt_blacknumber_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        //lv
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//				OnScrollListener.SCROLL_STATE_FLING	飞速滚动
//				OnScrollListener.SCROLL_STATE_IDLE	 空闲状态
//				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL	拿手触摸着去滚动状态
                if (mBlackNumberList != null){
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            &&lv_blacknumber.getLastVisiblePosition()>=mBlackNumberList.size()-1
                            &&!mIsLoading){
                        if (mCount > mBlackNumberList.size()){
                            new Thread(){
                                @Override
                                public void run() {
                                    //1,获取操作黑名单数据库的对象2,查询部分数据3,添加下一页数据的过程4,通知数据适配器刷新
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                                    mBlackNumberList.addAll(moreData);
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        }

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void initData() {
        //获取数据库中所有电话号码
        new Thread(){
            public void run() {
                //1,获取操作黑名单数据库的对象
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //2,查询部分数据
                mBlackNumberList = mDao.find(0);
                mCount = mDao.getCount();
                //3,通过消息机制告知主线程可以去使用包含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view_dialog_add_blacknumber = View.inflate(getApplicationContext(),R.layout.view_dialog_add_blacknumber,null);
        dialog.setView(view_dialog_add_blacknumber,5,5,5,5);

        final EditText et_blacknumber_phone = (EditText) view_dialog_add_blacknumber.findViewById(R.id.et_blacknumber_phone);
        RadioGroup rg_blacknumber = (RadioGroup) view_dialog_add_blacknumber.findViewById(R.id.rg_blacknumber);

        Button bt_blacknumber_submit = (Button) view_dialog_add_blacknumber.findViewById(R.id.bt_blacknumber_submit);
        Button bt_blacknumber_cancel = (Button)view_dialog_add_blacknumber.findViewById(R.id.bt_blacknumber_cancel);
        //rg_blacknumber.check(mode);
        //针对mode上一次旳赋值，初始化，或者，根据mode的值。设置对应radiobutton为check
        int lastMode = rg_blacknumber.getCheckedRadioButtonId();
        if (lastMode == R.id.rb_blacknumber_sms){mode = 1;}
        if (lastMode == R.id.rb_blacknumber_phone){mode = 2;}
        if (lastMode == R.id.rb_blacknumber_all){mode = 3;}

        rg_blacknumber.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_blacknumber_sms:
                        mode = 1;
                        break;
                    case R.id.rb_blacknumber_phone:
                        mode = 2;
                        break;
                    case R.id.rb_blacknumber_all:
                        mode = 3;
                        break;
                }
            }
        });
        bt_blacknumber_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_blacknumber_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_blacknumber_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    //2,数据库插入当前输入的拦截电话号码
                    mDao.insert(phone, mode+"");
                    //3,让数据库和集合保持同步(1.数据库中数据重新读一遍,2.手动向集合中添加一个对象(插入数据构建的对象))
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode+"";
                    //4,将对象插入到集合的最顶部
                    mBlackNumberList.add(0, blackNumberInfo);
                    //5,通知数据适配器刷新(数据适配器中的数据有改变了)
                    if(mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                    //6,隐藏对话框
                    dialog.dismiss();
                }else {
                    TastyToast.makeText(getApplicationContext(),
                            "请输入号码", TastyToast.LENGTH_SHORT, TastyToast.WARNING);}
            }
        });
        dialog.show();
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

//经典：listView优化
            //利用ViewHolder避免重复findView操作
            ViewHolder holder = null;
            if (convertView == null){
                convertView = View.inflate(getApplicationContext(),R.layout.listview_blacknumber_item,null);
                holder = new ViewHolder();
                holder.tv_blacknumber_item_phone = (TextView) convertView.findViewById(R.id.tv_blacknumber_item_phone);
                holder.tv_blacknumber_item_mode = (TextView) convertView.findViewById(R.id.tv_blacknumber_item_mode);
                holder.iv_blacknumber_item_delete = (ImageView) convertView.findViewById(R.id.iv_blacknumber_item_delete);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv_blacknumber_item_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1,数据库删除
                    mDao.delete(mBlackNumberList.get(position).phone);
                    //2,集合中的删除
                    mBlackNumberList.remove(position);
                    //3,通知数据适配器刷新
                    if(mAdapter!=null){
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });
            holder.tv_blacknumber_item_phone.setText(mBlackNumberList.get(position).phone);
            int mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode) {
                case 1:
                    holder.tv_blacknumber_item_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_blacknumber_item_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_blacknumber_item_mode.setText("拦截所有");
                    break;
            }

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_blacknumber_item_phone;
        TextView tv_blacknumber_item_mode;
        ImageView iv_blacknumber_item_delete;
    }
}
