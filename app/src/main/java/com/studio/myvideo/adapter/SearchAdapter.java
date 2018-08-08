package com.studio.myvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.VideoDetailActivity;
import com.studio.myvideo.activity.WebActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/16.
 */

public class SearchAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public SearchAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return array == null ? 0 : (int) Math.ceil(array.length() * 1.00 / 2);
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setContentArray(JSONArray contentArray) {
//        if (contentArray != null) {
        array = contentArray;
//        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final JSONObject entity = array.optJSONObject(position * 2);
        JSONObject entity1 = null;
        if (array.length() > (position * 2 + 1)) {
            entity1 = array.optJSONObject(position * 2 + 1);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_search_content, null);
            holder = new ViewHolder();
            holder.image_video_pic = convertView.findViewById(R.id.image_video_pic);
            holder.txt_video_content = convertView.findViewById(R.id.txt_video_content);
            holder.txt_video_time = convertView.findViewById(R.id.txt_video_time);
            holder.rl_video = convertView.findViewById(R.id.rl_video);
            holder.image_video_pic1 = convertView.findViewById(R.id.image_video_pic1);
            holder.txt_video_content1 = convertView.findViewById(R.id.txt_video_content1);
            holder.txt_video_time1 = convertView.findViewById(R.id.txt_video_time1);
            holder.rl_video_1 = convertView.findViewById(R.id.rl_video_1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Glide.with(context).load(entity.optString("video_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_video_pic);
        try {
            ImageLoader.getInstance().displayImage(entity.optString("video_image"), holder.image_video_pic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txt_video_content.setText(entity.optString("title_str"));
        holder.txt_video_time.setText(entity.optString("publish_time"));
        holder.rl_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //跳转到视频详情界面
                    if ("1".equals(entity.optString("is_video"))){
                        Intent intent = new Intent(context, VideoDetailActivity.class);
                        intent.putExtra("video_id", entity.optString("video_id"));
                        context.startActivity(intent);
                    }else {
                        String url=entity.optString("video_url");
                        if (url!=null&&url.length()>0&&url.startsWith("http")){
                            Intent intent=new Intent(context, WebActivity.class);
                            intent.putExtra("url",url);
                            context.startActivity(intent);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
        if (entity1 != null) {
            holder.rl_video_1.setVisibility(View.VISIBLE);
//            Glide.with(context).load(entity1.optString("video_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_video_pic1);
            try {
                ImageLoader.getInstance().displayImage(entity1.optString("video_image"), holder.image_video_pic1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.txt_video_content1.setText(entity1.optString("title_str"));
            holder.txt_video_time1.setText(entity1.optString("publish_time"));
        } else {
            holder.rl_video_1.setVisibility(View.INVISIBLE);
        }
        final JSONObject finalEntity = entity1;
        holder.rl_video_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity != null) {
                    try {
                        //跳转到视频详情界面
                        if ("1".equals(finalEntity.optString("is_video"))){
                            Intent intent = new Intent(context, VideoDetailActivity.class);
                            intent.putExtra("video_id", finalEntity.optString("video_id"));
                            context.startActivity(intent);
                        }else {
                            String url=finalEntity.optString("video_url");
                            if (url!=null&&url.length()>0&&url.startsWith("http")){
                                Intent intent=new Intent(context, WebActivity.class);
                                intent.putExtra("url",url);
                                context.startActivity(intent);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }



            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView image_video_pic;
        TextView txt_video_content;
        TextView txt_video_time;
        AutoRelativeLayout rl_video;
        ImageView image_video_pic1;
        TextView txt_video_content1;
        TextView txt_video_time1;
        AutoRelativeLayout rl_video_1;
    }
}
