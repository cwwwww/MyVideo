package com.studio.myvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.studio.myvideo.R;
import com.studio.myvideo.adapter.SearchAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.utils.Utility;
import com.studio.myvideo.utils.Utils;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/14.
 */

public class SearchActivity extends BaseActivity {
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_search_cancel)
    TextView tv_search_cancel;
    @BindView(R.id.list_search_content)
    ListView list_search_content;
    @BindView(R.id.ll_no_search)
    AutoLinearLayout ll_no_search;
    @BindView(R.id.scrollView)
    PullToRefreshScrollView scrollView;

    private OkHttpClient client;
    private String search_content = "";
    private String cat_id = "";
    private String label_id = "";
    private int page = 1;
    private JSONArray array = new JSONArray();
    private JSONArray array1 = new JSONArray();
    private SearchAdapter adapter;
    private int tag = 0;
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
//                    adapter=new SearchAdapter(mContext,array);
//                    list_search_content.setAdapter(adapter);
                    adapter.setContentArray(array);
                    Utility.setListViewHeightBasedOnChildren(list_search_content);
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
        setContentView(R.layout.activity_search);
        Intent intent = this.getIntent();
        try {
            cat_id = intent.getStringExtra("cat_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            label_id = intent.getStringExtra("label_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tag = intent.getIntExtra("tag", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initListview();
        init();
    }

    private void init() {
        try {
            client = new OkHttpClient();
            et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        search_content = et_search.getText().toString().trim();
                        if (search_content == null || "".equals(search_content)) {
                            Toast.makeText(mContext, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                        } else {
                            Utils.closeKeybord(et_search, mContext);
                            getDate(search_content, cat_id, label_id);
                        }

                        return true;
                    }
                    return false;
                }
            });
            adapter = new SearchAdapter(SearchActivity.this, array);
            list_search_content.setAdapter(adapter);
            if (tag == 0) {
                getDate("", cat_id, label_id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                page=1;
                flag=0;
                getDate(search_content,cat_id,label_id);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                flag=1;
                page++;
                getDate(search_content,cat_id,label_id);
            }
        });
    }

    private void getDate(String search_content, String cat_id, String label_id) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("search", search_content + "")
                    .add("cat_id", cat_id + "")
                    .add("label_id", label_id + "")
                    .add("page", String.valueOf(page))
                    .add("size", "20")
                    .build();
            Request request = new Request.Builder().url(SPUtils.get(mContext,"ip_url","")+Constant.VIDEO_SEARCH_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(0);
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

    @OnClick({R.id.tv_search_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_search_cancel:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        try {
            Utils.closeKeybord(et_search, mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
