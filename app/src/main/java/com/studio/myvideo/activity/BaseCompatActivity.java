package com.studio.myvideo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.studio.myvideo.R;
import com.studio.myvideo.utils.Constant;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/28.
 */

public class BaseCompatActivity extends AppCompatActivity {
    // 上下文
    protected Context mContext;
    // Log标签
    protected String TAG;
    private ExitReceiver exitReceiver = new ExitReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        mContext = this;
        TAG = getClass().getSimpleName();
        exitReceiver = new ExitReceiver();
        //生成一个IntentFilter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.SYSTEM_EXIT);
        //将BroadcastReceiver对象注冊到系统其中
        mContext.registerReceiver(exitReceiver, filter);

    }

    /**
     * 统一设置布局文件，初始化ButterKnife
     */
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.e(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(exitReceiver);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);

        overridePendingTransition(R.anim.activity_in_right_left, R.anim.activity_out_right_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        overridePendingTransition(R.anim.activity_in_right_left, R.anim.activity_out_right_left);
    }

    @Override
    public void finish() {
        super.finish();
        Log.e(TAG, "finish()");
        overridePendingTransition(R.anim.activity_in_left_right, R.anim.activity_out_left_right);
    }

    private class ExitReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.SYSTEM_EXIT)) {
                try {
                    android.util.Log.e("008923982", "========");
                    //关闭界面
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
