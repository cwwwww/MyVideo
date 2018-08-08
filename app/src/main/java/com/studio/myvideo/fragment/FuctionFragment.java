package com.studio.myvideo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.FuctionDetailActivity;
import com.studio.myvideo.activity.WebActivity;
import com.studio.myvideo.adapter.FuctionHotAdapter;
import com.studio.myvideo.adapter.FuctionVideoAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.ImageOptions;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.view.SGridView;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/13.
 */

public class FuctionFragment extends BaseFragment {
    @BindView(R.id.grid_fuction_type)
    SGridView grid_fuction_type;
    @BindView(R.id.list_fuction_hot)
    SGridView list_fuction_hot;
    @BindView(R.id.iv_ad)
    ImageView ivAd;

    private Unbinder unbinder;
    private View view;
    private OkHttpClient client;
    private FuctionVideoAdapter adapter;
    private FuctionHotAdapter hotAdapter;
    private JSONArray array = new JSONArray();
    private JSONArray hotArray = new JSONArray();

    // TODO: 2018/8/7 添加广告
    private ImageLoader imageLoader;
    private String adPath;
    private String adUrl = "";


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    array = object.optJSONArray("cat_list");
                    adapter = new FuctionVideoAdapter(mContext, array);
                    grid_fuction_type.setAdapter(adapter);
                    hotArray = object.optJSONArray("label_list");
                    hotAdapter = new FuctionHotAdapter(mContext, hotArray);
                    list_fuction_hot.setAdapter(hotAdapter);

                    // TODO: 2018/8/7  添加广告 1
                    JSONObject page_advertising = object.optJSONObject("page_advertising");
                    adPath = page_advertising.optString("image");
                    adUrl = page_advertising.optString("url");
                    imageLoader.displayImage(adPath, ivAd, ImageOptions.getBannerDefaultOptions());//显示

                } else {
                    Log.e("main_data_2", obj.optString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fuction, null);
        unbinder = ButterKnife.bind(this, view);
        initBoot();
        init();
        initEvent();
        return view;
    }

    private void initBoot() {
        imageLoader = ImageLoader.getInstance();
    }


    private void initEvent() {
        //点击事件
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!adUrl.equals("")) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", adUrl);
                    startActivity(intent);
                }
            }
        });


    }

    private void init() {
        client = new OkHttpClient();

        grid_fuction_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, FuctionDetailActivity.class);
                intent.putExtra("cat_id", ((JSONObject) array.opt(position)).optString("cat_id"));
                intent.putExtra("cat_name", ((JSONObject) array.opt(position)).optString("cat_name"));
                startActivity(intent);
            }
        });
        getDate();
    }

    private void getDate() {
        try {
            RequestBody body = new FormBody.Builder().build();
            Request request = new Request.Builder().url(SPUtils.get(mContext, "ip_url", "") + Constant.VIDEO_FUCTION_URL).post(body).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("search_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("search_get_fuction_data", rps + "======");
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
