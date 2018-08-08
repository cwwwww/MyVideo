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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.WebActivity;
import com.studio.myvideo.adapter.HomeContentAdapter;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/29.
 */

public class ContentFragment extends BaseFragment {
    @BindView(R.id.id_rollviewpager)
    RollPagerView id_rollviewpager;
    @BindView(R.id.list_video_content)
    ListView list_video_content;
    @BindView(R.id.scrollView)
    PullToRefreshScrollView scrollView;

    private Unbinder unbinder;
    private View view;
    private JSONObject objectData;
    private int selectionPosition;
    private JSONArray bannerArray = new JSONArray();
    private JSONArray array1 = new JSONArray();
    private HomeContentAdapter contentAdapter;
    private OkHttpClient client;


    public static ContentFragment newInstance(JSONObject object,int position) {
        ContentFragment fragment = new ContentFragment();
        fragment.objectData=object;
        fragment.selectionPosition=position;
        return fragment;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                scrollView.onRefreshComplete();
            }catch (Exception e){
                e.printStackTrace();
            }
            Bundle data = msg.getData();
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    objectData=object;
                    bannerArray = object.optJSONArray("banner_list").optJSONArray(selectionPosition);
                    try {
                        Log.e("array.length--", bannerArray.length() + "==");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (bannerArray != null && bannerArray.length() > 0) {
                        id_rollviewpager.setVisibility(View.VISIBLE);
                    } else {
                        id_rollviewpager.setVisibility(View.GONE);
                    }
                    loadImagesViewpager(object.optJSONArray("banner_list").optJSONArray(selectionPosition));
                    //加载首页内容
                    setHomeContent(object.optJSONArray("data_list").optJSONArray(selectionPosition));
                } else {
                    Log.e("main_data_2", obj.optString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_content, null);
        unbinder = ButterKnife.bind(this, view);
        initListview();
        init();
        Log.e("selection_index",selectionPosition+"==");
        Log.e("selection_object",objectData.toString());
        return view;
    }
    private void init(){
        client = new OkHttpClient();
        bannerArray = objectData.optJSONArray("banner_list").optJSONArray(selectionPosition);
        if (bannerArray != null && bannerArray.length() > 0) {
            id_rollviewpager.setVisibility(View.VISIBLE);
        } else {
            id_rollviewpager.setVisibility(View.GONE);
        }
        //加载轮播图
        loadImagesViewpager(bannerArray);
        //加载首页内容
        setHomeContent(objectData.optJSONArray("data_list").optJSONArray(selectionPosition));

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
                getDate();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                scrollView.onRefreshComplete();
            }
        });
    }

    private void getDate() {
        try {
            JSONObject object = new JSONObject();
            RequestBody formBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), object.toString());
            Request request = new Request.Builder().url(SPUtils.get(mContext,"ip_url","")+ Constant.VIDEO_MAIN_URL).post(formBody).build();
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
                        Log.e("main_get_data", rps + "======");
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

    /**
     * 加载轮播图的图片
     *
     * @param bannerArray
     */
    private void loadImagesViewpager(JSONArray bannerArray) {
        //为轮播图加载图片
        id_rollviewpager.setAdapter(new TestNomalAdapter(bannerArray));
    }

    /**
     * 加载首页内容
     *
     * @param contentArray
     */
    private void setHomeContent(JSONArray contentArray) {
        array1 = contentArray;
        if (contentAdapter != null) {
            contentAdapter.setContentArray(contentArray);
        } else {
            contentAdapter = new HomeContentAdapter(mContext, array1);
            list_video_content.setAdapter(contentAdapter);
        }
        Utility.setListViewHeightBasedOnChildren(list_video_content);
    }

    public class TestNomalAdapter extends StaticPagerAdapter {
        JSONArray bannerArray;

        public TestNomalAdapter(JSONArray bannerArray) {
            this.bannerArray = bannerArray;
//            if (bannerArray!=null&&bannerArray.length() == 1) {
//                bannerArray.put(bannerArray.opt(0));
//            }
        }

        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView view = new ImageView(container.getContext());
            try {
                final JSONObject object = (JSONObject) bannerArray.opt(position);
                Log.d("url测试", object.optString("image"));
//                Glide.with(mContext).load(object.optString("image")).placeholder(R.drawable.default_image).into(view);
                try {
                    ImageLoader.getInstance().displayImage(object.optString("image"), view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String url=object.optString("url");
                            if (url!=null&&url.length()>0&&url.startsWith("http")){
                                Intent intent=new Intent(mContext, WebActivity.class);
                                intent.putExtra("url",url);
                                startActivity(intent);
                            }

                        } catch (Exception e) {
                            Log.e("cccc=-=-",e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                Log.d("error---测试", e.getMessage());
                e.printStackTrace();
            }

            return view;
        }

        @Override
        public int getCount() {
            return bannerArray.length() == 1 ? 1 : bannerArray.length();//防止后台传递的图片url数组长度为1时，轮播图闪动
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
