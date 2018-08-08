package com.studio.myvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.utils.Constant;
import com.studio.myvideo.utils.SPUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/25.
 */

public class FuctionVideoAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public FuctionVideoAdapter(Context context, JSONArray array){
        this.context=context;
        this.array=array;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return array==null?0:array.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setContentArray(JSONArray contentArray){
        if (contentArray!=null){
            array=contentArray;
        }
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        final JSONObject entity= (JSONObject) array.opt(position);
        if (view == null) {
            view = inflater.inflate(R.layout.item_grid_fuction_content, null);
            holder = new ViewHolder();
            holder.image_video_pic = view.findViewById(R.id.image_video_pic);
            holder.txt_video_content = view.findViewById(R.id.txt_video_content);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        try {
            ImageLoader.getInstance().displayImage(SPUtils.get(context,"ip_url","")+entity.optString("cat_image"), holder.image_video_pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Glide.with(context).load(entity.optString("cat_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_video_pic);
        holder.txt_video_content.setText(entity.optString("cat_name"));
        return view;
    }

    class ViewHolder {
        ImageView image_video_pic;
        TextView txt_video_content;
    }
}
