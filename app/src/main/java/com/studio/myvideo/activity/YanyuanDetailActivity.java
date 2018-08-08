package com.studio.myvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.adapter.SearchAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
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

public class YanyuanDetailActivity extends BaseActivity {
    @BindView(R.id.ll_yanyuan_detail_back)
    AutoLinearLayout ll_yanyuan_detail_back;
    @BindView(R.id.image_yanyuan_head)
    CircleImageView image_yanyuan_head;
    @BindView(R.id.txt_actor_name)
    TextView txt_actor_name;
    @BindView(R.id.txt_actor_grade)
    TextView txt_actor_grade;
    @BindView(R.id.txt_actor_msg)
    TextView txt_actor_msg;
    @BindView(R.id.list_yanyuan_works)
    ListView list_yanyuan_works;

    private String actor_id = "";
    private OkHttpClient client;
    private JSONArray array = new JSONArray();
    private SearchAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Log.e("main_data_1222", data.getString("data"));
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    JSONObject object1 = object.optJSONObject("actor_info");
                    txt_actor_name.setText(object1.optString("actor_name"));
                    txt_actor_grade.setText(object1.optString("grade_name"));
                    txt_actor_msg.setText(object1.optString("somatotype"));
//                    Glide.with(mContext).load(object1.optString("actor_image")).placeholder(getResources().getDrawable(R.drawable.default_image)).into(image_yanyuan_head);
                    try {
                        ImageLoader.getInstance().displayImage(object1.optString("actor_image"), image_yanyuan_head);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    array = object.optJSONArray("list");
                    adapter.setContentArray(array);

                } else {
                    Log.e("main_data_2", obj.optString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yanyuan_detail);
        Intent intent = this.getIntent();
        actor_id = intent.getStringExtra("actor_id");
        Log.e("cccc093", actor_id + "==");
        init();
        getDate(actor_id);
    }

    private void init() {
        client = new OkHttpClient();
        adapter = new SearchAdapter(mContext, array);
        list_yanyuan_works.setAdapter(adapter);
    }

    @OnClick({R.id.ll_yanyuan_detail_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_yanyuan_detail_back:
                finish();
                break;

        }
    }

    private void getDate(String video_id) {
        try {
            RequestBody formBody = new FormBody.Builder().add("actor_id", video_id).build();
            Request request = new Request.Builder().url(SPUtils.get(mContext, "ip_url", "") + Constant.ACTOR_DETAIL_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("search_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("search_gesst_data", rps + "======");
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
