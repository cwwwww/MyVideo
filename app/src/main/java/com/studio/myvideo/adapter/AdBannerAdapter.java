package com.studio.myvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.activity.WebActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/8/6.
 */

public class AdBannerAdapter extends StaticPagerAdapter {
    private  JSONArray bannerArray;
    private Context mContext;

    public AdBannerAdapter(JSONArray bannerArray) {
        this.bannerArray = bannerArray;
//            if (bannerArray!=null&&bannerArray.length() == 1) {
//                bannerArray.put(bannerArray.opt(0));
//            }
    }

    public AdBannerAdapter(JSONArray bannerArray, Context context) {
        this.bannerArray = bannerArray;
        this.mContext = context;
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
                            mContext.startActivity(intent);
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
