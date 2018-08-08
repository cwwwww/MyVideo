package com.studio.myvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.SearchActivity;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/17.
 */

public class VideoDetailAdapter extends BaseAdapter {

    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public VideoDetailAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return (int) Math.ceil(array.length() * 1.00 / 6);
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
        if (contentArray != null) {
            array = contentArray;
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        final JSONObject entity1 = array.optJSONObject(position * 6);
        JSONObject entity2 = null;
        JSONObject entity3 = null;
        JSONObject entity4 = null;
        JSONObject entity5 = null;
        JSONObject entity6 = null;
        if (array.length() > (position * 6 + 1)) {
            entity2 = array.optJSONObject(position * 6 + 1);
        }
        if (array.length() > (position * 6 + 2)) {
            entity3 = array.optJSONObject(position * 6 + 2);
        }
        if (array.length() > (position * 6 + 3)) {
            entity4 = array.optJSONObject(position * 6 + 3);
        }
        if (array.length() > (position * 6 + 4)) {
            entity5 = array.optJSONObject(position * 6 + 4);
        }
        if (array.length() > (position * 6 + 5)) {
            entity6 = array.optJSONObject(position * 6 + 5);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_video_type, null);
            holder = new ViewHolder();
            holder.btn_video_type1 = view.findViewById(R.id.btn_video_type1);
            holder.btn_video_type2 = view.findViewById(R.id.btn_video_type2);
            holder.btn_video_type3 = view.findViewById(R.id.btn_video_type3);
            holder.btn_video_type4 = view.findViewById(R.id.btn_video_type4);
            holder.btn_video_type5 = view.findViewById(R.id.btn_video_type5);
            holder.btn_video_type6 = view.findViewById(R.id.btn_video_type6);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.btn_video_type1.setText(entity1.optString("label_name"));
        if (entity2 != null) {
            holder.btn_video_type2.setText(entity2.optString("label_name"));
        } else {
            holder.btn_video_type2.setVisibility(View.INVISIBLE);
        }

        if (entity3 != null) {
            holder.btn_video_type3.setText(entity3.optString("label_name"));
        } else {
            holder.btn_video_type3.setVisibility(View.INVISIBLE);
        }
        if (entity4 != null) {
            holder.btn_video_type4.setText(entity4.optString("label_name"));
        } else {
            holder.btn_video_type4.setVisibility(View.INVISIBLE);
        }
        if (entity5 != null) {
            holder.btn_video_type5.setText(entity5.optString("label_name"));
        } else {
            holder.btn_video_type5.setVisibility(View.INVISIBLE);
        }
        if (entity6 != null) {
            holder.btn_video_type6.setText(entity6.optString("label_name"));
        } else {
            holder.btn_video_type6.setVisibility(View.INVISIBLE);
        }

        holder.btn_video_type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity1 != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",entity1.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity = entity2;
        holder.btn_video_type2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",finalEntity.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity3 = entity3;
        holder.btn_video_type3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity3 != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",finalEntity3.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity4 = entity4;
        holder.btn_video_type4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity4 != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",finalEntity4.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity5 = entity5;
        holder.btn_video_type5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity5 != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",finalEntity5.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity6 = entity6;
        holder.btn_video_type6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity6 != null) {
                    Intent intent=new Intent(context, SearchActivity.class);
                    intent.putExtra("label_id",finalEntity6.optString("label_id"));
                    context.startActivity(intent);
                }

            }
        });
        return view;
    }

    class ViewHolder {
        Button btn_video_type1,
                btn_video_type2,
                btn_video_type3,
                btn_video_type4,
                btn_video_type5,
                btn_video_type6;
    }
}
