package com.studio.myvideo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.adapter.VideoDetailAdapter;
import com.studio.myvideo.play.DataInter;
import com.studio.myvideo.play.MonitorDataProvider;
import com.studio.myvideo.play.ReceiverGroupManager;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.ImageOptions;
import com.studio.myvideo.utils.PUtil;
import com.studio.myvideo.utils.SPUtils;
import com.studio.myvideo.utils.Utility;
import com.umeng.analytics.MobclickAgent;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/6/15.
 * 视屏的详情页面
 */

public class VideoDetailActivity extends BaseCompatActivity implements OnPlayerEventListener {
    @BindView(R.id.ll_detail_back)
    AutoLinearLayout ll_detail_back;
    @BindView(R.id.txt_detail_content)
    TextView txt_detail_content;
    @BindView(R.id.ll_detail_show)
    AutoLinearLayout ll_detail_show;
    @BindView(R.id.image_detail_show)
    ImageView image_detail_show;
    @BindView(R.id.ll_detail_content)
    AutoLinearLayout ll_detail_content;
    @BindView(R.id.video_length_time)
    TextView video_length_time;
    @BindView(R.id.video_birth_date)
    TextView video_birth_date;
    @BindView(R.id.video_performer)
    TextView video_performer;
    @BindView(R.id.txt_video_sponsor)
    TextView txt_video_sponsor;
    @BindView(R.id.list_video_type)
    ListView list_video_type;
    @BindView(R.id.videoView)
    BaseVideoView mVideoView;
    @BindView(R.id.image_play_default)
    ImageView image_play_default;
    @BindView(R.id.roll_video_pic)
    RollPagerView roll_video_pic;
    @BindView(R.id.txt_video_content)
    TextView txt_video_content;
    @BindView(R.id.txt_video_time)
    TextView txt_video_time;
    @BindView(R.id.rl_video_ijk)
    AutoRelativeLayout relativeLayout;
    @BindView(R.id.ll_content)
    AutoLinearLayout ll_content;
    @BindView(R.id.rl_play_layout)
    AutoRelativeLayout rl_play_layout;
    @BindView(R.id.image_play_start)
    ImageView image_play_start;
    @BindView(R.id.iv_ad)
    ImageView ivAd;
    @BindView(R.id.iv_bottom_ad)
    ImageView ivBottomAd;
    @BindView(R.id.arl_layout)
    AutoRelativeLayout arlLayout;
    @BindView(R.id.ly_add_delete)
    LinearLayout lyAddDelete;


    private VideoDetailAdapter adapter;
    private JSONArray array = new JSONArray();
    private JSONArray rollArray = new JSONArray();
    private OkHttpClient client;
    private String video_id = "";
    private String video_url = "";
    private boolean flag = false;

    private Visualizer mVisualizer;
    private int margin;
    private boolean permissionSuccess;
    private ReceiverGroup mReceiverGroup;
    private boolean isLandscape;
    private long mDataSourceId;
    private boolean userPause;

    String image_url = "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg";
    //    String video_url = "https://www.ava122.com/20180715/rYuw7aMe/index.m3u8";
    String title = "";

    // TODO: 2018/8/7
    private ImageLoader imageLoader;
    private String adPath;
    private String adUrl = "";

    // TODO: 2018/8/7
    private String bottomAdPicPath;
    private String bottomUrl = "";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            try {
                JSONObject obj = new JSONObject(data.getString("data"));
                if ("200".equals(obj.optString("status"))) {
                    JSONObject object = obj.optJSONObject("content");
                    title = object.optString("title");
                    txt_detail_content.setText(title);
                    video_length_time.setText("片长：" + object.optString("video_time"));
                    video_birth_date.setText("发行日期：" + object.optString("publish_time"));
                    video_performer.setText("演员：" + object.optString("actor_name"));
                    txt_video_sponsor.setText("赞助商：" + object.optString("publish_name"));
                    try {
                        ImageLoader.getInstance().displayImage(object.optString("video_image"), image_play_default);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    video_url = object.optString("video_url");

                    rollArray = object.optJSONArray("advertising_list");
                    if (rollArray != null && rollArray.length() > 0) {
                        roll_video_pic.setAdapter(new TestNomalAdapter(rollArray));
                        txt_video_content.setText(rollArray.optJSONObject(0).optString("advertising_title"));
                        txt_video_time.setText(rollArray.optJSONObject(0).optString("showtime"));
                    }
                    array = object.optJSONArray("label_list");
                    adapter = new VideoDetailAdapter(mContext, array);
                    list_video_type.setAdapter(adapter);
                    Utility.setListViewHeightBasedOnChildren(list_video_type);

                    // TODO: 2018/8/7  添加广告
                    JSONObject page_advertising = object.optJSONObject("page_advertising");
                    adPath = page_advertising.optString("image");
                    adUrl = page_advertising.optString("url");
                    imageLoader.displayImage(adPath, ivAd, ImageOptions.getBannerDefaultOptions());//显示

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
            } catch (Exception e) {
                Log.e("detail_error", e.getMessage());
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        try {
            Intent intent = this.getIntent();
            video_id = intent.getStringExtra("video_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initBoot();
        init();
        margin = PUtil.dip2px(this, 2);
        mVideoView = (BaseVideoView) findViewById(R.id.videoView);
        PermissionGen.with(this)
                .addRequestCode(101)
                .permissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                )
                .request();

        initEvent();
        initData();
    }

    private void initData() {
        //默认打开
        flag = true;
        ll_detail_content.setVisibility(View.VISIBLE);
        image_detail_show.setSelected(true);

        //一动到最上面
        rl_play_layout.setFocusable(true);
    }

    private void initEvent() {
        // 广告删除
        lyAddDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arlLayout.setVisibility(View.GONE);
            }
        });

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

    private void init() {
        client = new OkHttpClient();
        roll_video_pic.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    if (rollArray != null && rollArray.length() > 0) {
                        JSONObject object = rollArray.optJSONObject(position);
                        txt_video_content.setText(object.optString("advertising_title"));
                        txt_video_time.setText(object.optString("showtime"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        image_play_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_play_layout.setVisibility(View.GONE);
                //设置数据提供者 MonitorDataProvider
                MonitorDataProvider dataProvider = new MonitorDataProvider(title, image_url, video_url);
                mVideoView.setDataProvider(dataProvider);
                mVideoView.setDataSource(generatorDataSource(mDataSourceId));
                mVideoView.start();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 101)
    public void permissionSuccess() {
        permissionSuccess = true;
        initPlay();
    }

    @PermissionFail(requestCode = 101)
    public void permissionFailure() {
        permissionSuccess = false;
        initPlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
        getDate(video_id);
        if (flag == false) {
            ll_detail_content.setVisibility(View.GONE);
        } else {
            ll_detail_content.setVisibility(View.VISIBLE);
        }
        if (mVideoView.isInPlaybackState()) {
            if (!userPause)
                mVideoView.resume();
        } else {
            mVideoView.rePlay(0);
        }
    }

    private void initPlay() {
        updateVideo(false);

        mVideoView.setOnPlayerEventListener(this);
        mVideoView.setEventHandler(mOnEventAssistHandler);
        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this, null);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_HAS_NEXT, true);
        mVideoView.setReceiverGroup(mReceiverGroup);


    }

    private DataSource generatorDataSource(long id) {
        DataSource dataSource = new DataSource();
        dataSource.setId(id);
        return dataSource;
    }

    private void updateVideo(boolean landscape) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (landscape) {
            Log.e("ckks", "=========");
            ll_content.setVisibility(View.GONE);
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);

        } else {
            Log.e("ckks", "=========+++++++++");
            ll_content.setVisibility(View.VISIBLE);
            layoutParams.width = PUtil.getScreenW(this) - (margin * 2);
            layoutParams.height = layoutParams.width * 9 / 16;
            layoutParams.setMargins(margin, margin, margin, margin);
        }
        mVideoView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
        if (mVideoView.isInPlaybackState()) {
            mVideoView.pause();
        } else {
            mVideoView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
        releaseVisualizer();
    }

    @Override
    public void onBackPressed() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
            updateVideo(true);
        } else {
            isLandscape = false;
            updateVideo(false);
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    }

    private OnVideoViewEventHandler mOnEventAssistHandler = new OnVideoViewEventHandler() {
        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case DataInter.Event.CODE_REQUEST_PAUSE:
                    userPause = true;
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    if (isLandscape) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        finish();
                    }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_NEXT:
                    mDataSourceId++;
                    mVideoView.setDataSource(generatorDataSource(mDataSourceId));
                    mVideoView.start();
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    setRequestedOrientation(isLandscape ?
                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
            }
        }
    };

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                releaseVisualizer();
                updateVisualizer();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:

                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_RESUME:
                userPause = false;
                break;
        }
    }

    private void releaseVisualizer() {
        if (mVisualizer != null)
            mVisualizer.release();
    }

    private void updateVisualizer() {
        if (!permissionSuccess)
            return;
        mVisualizer = new Visualizer(mVideoView.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }

    @OnClick({R.id.ll_detail_back, R.id.ll_detail_show})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_detail_back:
                finish();
                break;
            case R.id.ll_detail_show://细节展开
                if (flag == false) {
                    flag = true;
                    ll_detail_content.setVisibility(View.VISIBLE);
                    image_detail_show.setSelected(true);
                } else {
                    flag = false;
                    ll_detail_content.setVisibility(View.GONE);
                    image_detail_show.setSelected(false);
                }
                break;

        }
    }

    private void getDate(String video_id) {
        try {
            RequestBody body = new FormBody.Builder().add("video_id", video_id).build();
            Request request = new Request.Builder().url(SPUtils.get(mContext, "ip_url", "") + Constant.VIDEO_DETAIL_URL).post(body).build();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("search_fail_error", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String rps = response.body().string();
                        Log.e("search_get_data", rps + "======");
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

    public class TestNomalAdapter extends StaticPagerAdapter {
        JSONArray bannerArray;

        public TestNomalAdapter(JSONArray bannerArray) {
            this.bannerArray = bannerArray;
        }

        @Override
        public View getView(ViewGroup container, final int position) {
            ImageView view = new ImageView(container.getContext());
            try {
                final JSONObject object = (JSONObject) bannerArray.opt(position);
                Log.d("url测试", object.optString("advertising_image"));
                try {
                    ImageLoader.getInstance().displayImage(object.optString("advertising_image"), view);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                view.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String url = object.optString("url");
                            if (url != null && url.length() > 0 && url.startsWith("http")) {
                                Intent intent = new Intent(mContext, WebActivity.class);
                                intent.putExtra("url", object.optString("advertising_url"));
                                startActivity(intent);
                            }
                        } catch (Exception e) {
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


}
