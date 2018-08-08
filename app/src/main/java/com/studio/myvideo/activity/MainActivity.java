package com.studio.myvideo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.adapter.MyFragmentPagerAdapter;
import com.studio.myvideo.fragment.FuctionFragment;
import com.studio.myvideo.fragment.HomeFragment;
import com.studio.myvideo.fragment.YanyuanFragment;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.ImageOptions;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.view.MainViewPager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp_home)
    MainViewPager homeViewPager;//fragment容器
    @BindView(R.id.tv_home)
    TextView homeText;//首页
    @BindView(R.id.tv_yanyuan)
    TextView yanyuanText;//演员
    @BindView(R.id.tv_fuction)
    TextView fuctionText;//分类
    @BindView(R.id.top_search_image)
    ImageView imageSearch;//搜索
    @BindView(R.id.arl_layout)
    AutoRelativeLayout arlLayout;
    @BindView(R.id.iv_bottom_ad)
    ImageView ivBottomAd;
    @BindView(R.id.ly_add_delete)
    LinearLayout lyAddDelete;
    private ArrayList<Fragment> mFragmentList;
    private OkHttpClient client;//网络

    // TODO: 2018/8/7
    private String bottomAdPicPath;
    private ImageLoader imageLoader;
    private String bottomUrl = "";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            Log.e("main_data_1", data.getString("data"));
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    ((HomeFragment) mFragmentList.get(0)).setViewData(object);

                    // TODO: 2018/8/7  添加广告 2  ---底部广告
                    JSONObject bottom_advertising = object.optJSONObject("bottom_advertising");
                    if (bottom_advertising.optInt("is_open") == 1) {//开启
                        bottomAdPicPath = bottom_advertising.optString("image");
                        bottomUrl = bottom_advertising.optString("url");
                        imageLoader.displayImage(bottomAdPicPath, ivBottomAd, ImageOptions.getBottomAdDefaultOptions());//显示

                    } else {//不开启
                        arlLayout.setVisibility(View.GONE);
                    }

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
        PushAgent.getInstance(this).onAppStart();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initContentFragment();
        getDate();
        initBoot();
        initEvent();
    }

    private void initEvent() {
        // 广告删除
        lyAddDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arlLayout.setVisibility(View.GONE);
            }
        });

        //广告点击
        arlLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bottomUrl.equals("")) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra("url", bottomUrl);
                    startActivity(intent);
                }
            }
        });

    }

    private void initBoot() {
        imageLoader = ImageLoader.getInstance();
    }

    private void initContentFragment() {
        client = new OkHttpClient();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new HomeFragment());
        mFragmentList.add(new YanyuanFragment());
        mFragmentList.add(new FuctionFragment());
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        homeViewPager.setAdapter(adapter);
        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        homeViewPager.setOffscreenPageLimit(3);
        homeText.setSelected(true);
        homeViewPager.setCurrentItem(0);
        homeViewPager.setNoScroll(true);
        homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                homeText.setSelected(arg0 == 0 ? true : false);
                yanyuanText.setSelected(arg0 == 1 ? true : false);
                fuctionText.setSelected(arg0 == 2 ? true : false);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @OnClick({R.id.tv_home, R.id.tv_yanyuan, R.id.tv_fuction, R.id.top_search_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_home://首页
                setSelected(0);
                break;
            case R.id.tv_yanyuan://演员
                setSelected(1);
                break;
            case R.id.tv_fuction://分类
                setSelected(2);
                break;
            case R.id.top_search_image://搜索
                Intent intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra("tag", 1);
                startActivity(intent);
                break;
        }
    }

    public void setSelected(int index) {
        if (homeViewPager.getCurrentItem() != index) {//不然cpu会有损耗
            homeText.setSelected(index == 0 ? true : false);
            yanyuanText.setSelected(index == 1 ? true : false);
            fuctionText.setSelected(index == 2 ? true : false);
            homeViewPager.setCurrentItem(index);
        }
    }

    /**
     * 获取数据
     */
    private void getDate() {
        try {
            JSONObject object = new JSONObject();
            RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), object.toString());
            Request request = new Request.Builder().url(SPUtils.get(mContext, "ip_url", "") + Constant.VIDEO_MAIN_URL).post(formBody).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("main_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("main_get_1_data", rps + "======");
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putString("data", rps);
                        msg.setData(data);
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
