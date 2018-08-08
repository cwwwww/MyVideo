package com.studio.myvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.studio.myvideo.R;
import com.studio.myvideo.adapter.SearchAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.utils.Utility;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/25.
 */

public class FuctionDetailActivity extends BaseActivity {
    @BindView(R.id.ll_fuction_back)
    AutoLinearLayout ll_fuction_back;
    @BindView(R.id.txt_fuction_type)
    TextView txt_fuction_type;
    @BindView(R.id.list_fuction_detail)
    ListView list_fuction_detail;
    @BindView(R.id.ll_no_search)
    AutoLinearLayout ll_no_search;
    @BindView(R.id.scrollView)
    PullToRefreshScrollView scrollView;

    private String title_name = "";
    private String cat_id = "";
    private int page = 1;
    private OkHttpClient client;
    private JSONArray array = new JSONArray();
    private JSONArray array1 = new JSONArray();
    private SearchAdapter adapter;
    private int flag = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                scrollView.onRefreshComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bundle data = msg.getData();
            Log.e("main_data_1", data.getString("data"));
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    array1 = object.optJSONArray("list");
                    page = object.optInt("currentpage");
                    if (flag == 0) {
                        array = new JSONArray();
                    }
                    if (array1 != null && array1.length() > 0) {
                        for (int i = 0; i < array1.length(); i++) {
                            array.put(array1.opt(i));
                        }
                    }else {
                        page=page-1;
                    }
                    if (array != null && array.length() > 0) {
                        ll_no_search.setVisibility(View.GONE);
                    } else {
                        ll_no_search.setVisibility(View.VISIBLE);
                    }
                    adapter.setContentArray(array);
                    Utility.setListViewHeightBasedOnChildren(list_fuction_detail);
                } else {
                    Log.e("main_data_2", obj.optString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuction_detail);
        try {
            Intent intent = this.getIntent();
            title_name = intent.getStringExtra("cat_name");
            cat_id = intent.getStringExtra("cat_id");
            txt_fuction_type.setText(title_name);
        } catch (Exception e) {
            Log.e("fuction_error", e.getMessage());
            e.printStackTrace();
        }
        initListview();
        init();
    }

    private void init() {
        client = new OkHttpClient();
        adapter = new SearchAdapter(mContext, array);
        list_fuction_detail.setAdapter(adapter);
        getDate(cat_id);
    }

    // 初始化listview
    private void initListview() {
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);//允许下拉上拉
        // 设置下拉字符串
        scrollView.getLoadingLayoutProxy(true, false).setPullLabel(getString(R.string.pull_to_load_x));
        scrollView.getLoadingLayoutProxy(true, false).setRefreshingLabel(getString(R.string.loading_x));
        scrollView.getLoadingLayoutProxy(true, false).setReleaseLabel(getString(R.string.release_to_load_x));
        scrollView.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                page = 1;
                flag = 0;
                getDate(cat_id);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                flag = 1;
                page++;
                getDate(cat_id);
            }
        });
    }

    @OnClick({R.id.ll_fuction_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_fuction_back:
                finish();
                break;
        }
    }

    private void getDate(String cat_id) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("search", "")
                    .add("cat_id", cat_id + "")
                    .add("label_id", "")
                    .add("page", String.valueOf(page))
                    .add("size", "20")
                    .build();
            Request request = new Request.Builder().url(SPUtils.get(mContext,"ip_url","")+Constant.VIDEO_SEARCH_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("search_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("search_get_data_sea", rps + "======");
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
