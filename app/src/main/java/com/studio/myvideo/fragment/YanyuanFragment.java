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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.WebActivity;
import com.studio.myvideo.adapter.TitleAdapter;
import com.studio.myvideo.adapter.YanyuanContentAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.ImageOptions;
import com.studio.myvideo.utils.SPUtils;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

public class YanyuanFragment extends BaseFragment {
    @BindView(R.id.ll_yanyuan_title)
    AutoLinearLayout ll_yanyuan_title;
    @BindView(R.id.image_yanyuan_title)
    ImageView image_yanyuan_title;
    @BindView(R.id.list_yanyuan_content)
    PullToRefreshListView list_yanyuan_content;
    @BindView(R.id.grad_view)
    GridView grad_view;
    @BindView(R.id.iv_ad)
    ImageView ivAd;

    //    private String[] title = {"全部", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private Unbinder unbinder;
    private View view;
    private OkHttpClient client;
    private JSONArray titleArray = new JSONArray();
    private JSONArray titleArray1 = new JSONArray();
    private JSONArray titleArray2 = new JSONArray();
    private JSONArray contentArray = new JSONArray();
    private JSONArray contentArray1 = new JSONArray();
    private JSONObject jsonObject;
    private boolean flag = false;
    private String json = "{\"grade_id\": \"\", \"grade_name\": \"全部\",\"sort\": \"0\",\"is_open\": \"1\" }";
    private int page = 1;
    private int tag = 0;
    private TitleAdapter titleAdapter;
    private YanyuanContentAdapter contentAdapter;
    private int selectionPosition = 0;
    private String grade_id = "";

    // TODO: 2018/8/7
    private ImageLoader imageLoader;
    private String adPath;
    private String adUrl = "";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                list_yanyuan_content.onRefreshComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bundle data = msg.getData();
            Log.e("main_data_1", data.getString("data"));
            switch (msg.what) {
                case 111:
                    //region 首次含标题的数据加载
                    try {
                        JSONObject obj = new JSONObject(data.getString("data"));
                        if ("200".equals(obj.optString("status"))) {
                            JSONObject object = obj.optJSONObject("content");
                            titleArray = object.optJSONArray("grade_list");
                            contentArray = object.optJSONArray("list");
                            titleArray1.put(jsonObject);
                            titleArray2.put(jsonObject);
                            if (titleArray != null) {
                                if (titleArray.length() < 5) {
                                    for (int i = 0; i < titleArray.length(); i++) {
                                        titleArray1.put(titleArray.opt(i));
                                        titleArray2.put(titleArray.opt(i));
                                    }
                                } else {
                                    for (int i = 0; i < 4; i++) {
                                        titleArray1.put(titleArray.opt(i));
                                    }
                                    for (int i = 0; i < titleArray.length(); i++) {
                                        titleArray2.put(titleArray.opt(i));
                                    }
                                }
                                titleAdapter.setContentArray(titleArray1);
                            }
                            if (contentArray != null) {
                                contentAdapter.setContentArray(contentArray);
                            }

                            // TODO: 2018/8/7  添加广告
                            JSONObject page_advertising = object.optJSONObject("page_advertising");
                            adPath = page_advertising.optString("image");
                            adUrl = page_advertising.optString("url");
                            imageLoader.displayImage(adPath, ivAd, ImageOptions.getBannerDefaultOptions());//显示


                        } else {
                            Log.e("main_data_2", obj.optString("message"));
                        }
                    } catch (Exception e) {
                        Log.e("cccc00909", e.getMessage());
                        e.printStackTrace();
                    }
                    //endregion
                    break;
                case 222:
                    try {
                        JSONObject obj = new JSONObject(data.getString("data"));
                        if ("200".equals(obj.optString("status"))) {
                            JSONObject object = obj.optJSONObject("content");
                            page = object.optInt("currentpage");
                            contentArray1 = object.optJSONArray("list");
                            Log.e("kjflksafd", object.optJSONArray("list").toString());
                            if (tag == 2) {
                                //这个属于
                                contentArray = new JSONArray();
                            }
                            if (contentArray1 != null && contentArray1.length() > 0) {
                                for (int i = 0; i < contentArray1.length(); i++) {
                                    contentArray.put(contentArray1.opt(i));
                                }
                                contentAdapter.setContentArray(contentArray);
                            } else {
                                page = page - 1;
                            }
                            if (contentArray != null) {
                                contentAdapter.setContentArray(contentArray);
                            }
                        } else {
                            Log.e("main_data_2", obj.optString("message"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_yanyuan, null);
        unbinder = ButterKnife.bind(this, view);
        initBoot();
        initListview();
        init();
        initEvent();
        return view;
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

    private void initBoot() {
        imageLoader = ImageLoader.getInstance();
    }

    private void init() {
        try {
            jsonObject = new JSONObject(json);
            client = new OkHttpClient();
            titleAdapter = new TitleAdapter(mContext, titleArray1);
            titleAdapter.setSelectedPosition(selectionPosition);
            grad_view.setAdapter(titleAdapter);
            grad_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tag = 2;
                    page = 1;
                    selectionPosition = position;
                    titleAdapter.setSelectedPosition(position);
                    titleAdapter.notifyDataSetChanged();
                    if (flag == false) {
                        grade_id = ((JSONObject) titleArray1.opt(position)).optString("grade_id");
                        getDate(grade_id);
                    } else {
                        grade_id = ((JSONObject) titleArray2.opt(position)).optString("grade_id");
                        getDate(grade_id);
                    }
                }
            });
            contentAdapter = new YanyuanContentAdapter(mContext, contentArray);
            list_yanyuan_content.setAdapter(contentAdapter);
            getDate("");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // 初始化listview
    private void initListview() {
        list_yanyuan_content.getRefreshableView().setDivider(null);
        list_yanyuan_content.setMode(PullToRefreshBase.Mode.BOTH);//允许下拉上拉

        // 设置下拉字符串
        list_yanyuan_content.getLoadingLayoutProxy(true, false).setPullLabel(getString(R.string.pull_to_load_x));
        list_yanyuan_content.getLoadingLayoutProxy(true, false).setRefreshingLabel(getString(R.string.loading_x));
        list_yanyuan_content.getLoadingLayoutProxy(true, false).setReleaseLabel(getString(R.string.release_to_load_x));
        list_yanyuan_content.getRefreshableView().setSelector(android.R.color.transparent);
        // 设置上拉字符串
        list_yanyuan_content.getLoadingLayoutProxy(false, true).setPullLabel(getString(R.string.pull_to_load_xx));
        list_yanyuan_content.getLoadingLayoutProxy(false, true).setRefreshingLabel(getString(R.string.loading_xx));
        list_yanyuan_content.getLoadingLayoutProxy(false, true).setReleaseLabel(getString(R.string.release_to_load));
        list_yanyuan_content.getRefreshableView().setSelector(android.R.color.transparent);
        list_yanyuan_content.getRefreshableView().setOverScrollMode(View.OVER_SCROLL_NEVER);
        list_yanyuan_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                tag = 2;
                page = 1;
                getDate(grade_id);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                tag = 3;
                page++;
                getDate(grade_id);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.ll_yanyuan_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_yanyuan_title:
                if (flag == false) {
                    flag = true;
                    titleAdapter.setContentArray(titleArray2);
                } else {
                    flag = false;
                    titleAdapter.setContentArray(titleArray1);
                }
                ll_yanyuan_title.setSelected(flag);

                break;
        }
    }

    private void getDate(String grade_id) {
        try {
            RequestBody formBody = new FormBody.Builder()
                    .add("grade_id", grade_id)
                    .add("page", "" + page)
                    .add("size", 20 + "")
                    .build();
            Request request = new Request.Builder().url(SPUtils.get(mContext, "ip_url", "") + Constant.VIDEO_ACTOR_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    handler.sendEmptyMessage(0);
                    Log.e("main_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("main_get111_data", rps + "======");
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("data", rps);
                        msg.setData(data);
                        if (tag == 0) {
                            msg.what = 111;
                        } else {
                            msg.what = 222;
                        }
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Log.e("main_error_data", e.getMessage());
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            Log.e("main_url_data", e.getMessage());
            e.printStackTrace();
        }
    }

}
