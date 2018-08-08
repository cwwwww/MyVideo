package com.studio.myvideo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.kk.taurus.exoplayer.ExoMediaPlayer;
import com.kk.taurus.ijkplayer.IjkPlayer;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;
import com.kk.taurus.playerbase.log.PLog;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.xapp.jjh.logtools.config.XLogConfig;
import com.xapp.jjh.logtools.logger.LogLevel;
import com.xapp.jjh.logtools.tools.XLog;

import java.io.File;

/**
 * Created by Administrator on 2017/11/6.
 */

public class AppContext extends Application {
    public static AppContext context;
    public static final int PLAN_ID_IJK = 1;
    public static final int PLAN_ID_EXO = 2;

    private static AppContext instance;

    public static boolean ignoreMobile;

    /**
     * Called when the application is starting, before any activity, service, or
     * receiver objects (excluding content providers) have been created.
     * （当应用启动的时候，会在任何activity、Service或者接收器被创建之前调用，所以在这里进行ImageLoader 的配置）
     * 当前类需要在清单配置文件里面的application下进行name属性的设置。
     */
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //友盟
        initYM();
        context = this;// 赋值
        // 缓存图片的配置，一般通用的配置
        initImageLoader(getApplicationContext());
        instance = this;
        PLog.LOG_OPEN = true;
        XLog.init(getApplicationContext(),
                new XLogConfig()
                        //loglevel FULL为显示log ，NONE为不显示log
                        .setLogLevel(LogLevel.FULL)
                        //文件日志以及崩溃日志文件的目录
                        .setLogDir(new File(Environment.getExternalStorageDirectory(), "TestXLog"))
                        //崩溃日志文件标记名称
                        .setCrashLogTag("CrashLogTag")
                        //是否云保存文件日志（非crash日志）
                        .setFileLogAllow(true)
                        //普通文件日志标记名称
                        .setNormalLogTag("NormalLogTag")
                        //日志文件扩展名，默认.txt
                        .setFileExtensionName(XLogConfig.DEFAULT_FILE_EXTENSION_NAME)
                        //日志文件定期清理周期（单位毫秒），默认为一周（7*24*60*60*1000）
                        .setFileClearCycle(XLogConfig.DEFAULT_FILE_CLEAR_CYCLE)
                        //是否保存崩溃日志
                        .setSaveCrashLog(true)
                        //是否为普通日志信息添加消息框
                        .setMessageTable(true));

        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_IJK, IjkPlayer.class.getName(), "IjkPlayer"));
        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_EXO, ExoMediaPlayer.class.getName(), "ExoPlayer"));
        PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);

        //use default NetworkEventProducer.
        PlayerConfig.setUseDefaultNetworkEventProducer(true);

        PlayerLibrary.init(this);
    }

    private void initYM() {
        UMConfigure.init(this, "5b6aae11f29d98072b0002f5", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "2f5907e0001309097f7602386761d7dc");
        PushAgent mPushAgent = PushAgent.getInstance(this);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
        }
        //推送
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("bing", "返回token:" + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("bing", "初始化失败：" + s + ":, " + s1);
            }
        });
    }

    private void initImageLoader(Context context) {
        // TODO Auto-generated method stub
        // 创建DisplayImageOptions对象
        DisplayImageOptions defaulOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisk(true).build();
        // 创建ImageLoaderConfiguration对象
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaulOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        // ImageLoader对象的配置
        ImageLoader.getInstance().init(configuration);
    }

    public static Context getContext() {
        return context;
    }
}