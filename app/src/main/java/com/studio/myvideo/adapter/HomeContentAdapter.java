package com.studio.myvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.VideoDetailActivity;
import com.studio.myvideo.activity.WebActivity;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/16.
 */

public class HomeContentAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public HomeContentAdapter(Context context, JSONArray array){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final JSONObject entity= (JSONObject) array.opt(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_home_video, null);
            holder = new ViewHolder();
            holder.image_video_pic = convertView.findViewById(R.id.image_video_pic);
            holder.txt_video_content = convertView.findViewById(R.id.txt_video_content);
            holder.txt_video_time = convertView.findViewById(R.id.txt_video_time);
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

        holder.txt_video_content.setText(entity.optString("title"));
        holder.txt_video_time.setText(entity.optString("publish_time"));
        holder.image_video_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //跳转到视频详情界面
                    Log.e("aaa",entity.optString("is_video")+"========");
                    if ("1".equals(entity.optString("is_video"))){
                        Log.e("aaa","--------");
                        Intent intent = new Intent(context, VideoDetailActivity.class);
                        intent.putExtra("video_id", entity.optString("video_id"));
                        context.startActivity(intent);
                    }else {
                        Log.e("aaa","========");
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
        return convertView;
    }

    class ViewHolder {
        ImageView image_video_pic;
        TextView txt_video_content;
        TextView txt_video_time;
    }
}
