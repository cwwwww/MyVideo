package com.studio.myvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.myvideo.R;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.utils.Utility;
import com.studio.myvideo.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/15.
 */

public class WelcomeActivity extends BaseActivity {
    @BindView(R.id.txt_version_name)
    TextView VersionName;

    private OkHttpClient client;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                String ip_address=obj.optString("url");
                if (TextUtils.isEmpty(ip_address)){
                    Toast.makeText(mContext, "未获取到ip地址，请退出重试", Toast.LENGTH_SHORT).show();
                }else {
                    SPUtils.put(mContext,"ip_url",obj.optString("url"));
                    threadWelcome();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        try {
            VersionName.setText(Utils.getVerName(mContext));
        }catch (Exception e){
            e.printStackTrace();
        }
        client=new OkHttpClient();
        getDate();
    }
    private void getDate() {
        try {
            JSONObject object = new JSONObject();
            RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), object.toString());
            Request request = new Request.Builder().url(Constant.VIDEO_GET_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("search_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("url_get_data", rps + "======");
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("data", rps);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.e("search_error_data", e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            Log.e("search_url_data", e.getMessage());
            e.printStackTrace();
        }
    }

    public void threadWelcome() {
        //线程休眠两秒钟
        new Thread() {
            @Override
            public void run() {
                //region 线程
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                WelcomeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                        finish();
                    }
                });
                //endregion
            }
        }.start();

    }

}
