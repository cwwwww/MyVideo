package com.studio.myvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

//import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.studio.myvideo.R;
import com.studio.myvideo.activity.YanyuanDetailActivity;
import com.studio.myvideo.view.CircleImageView;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/4/24.
 */

public class YanyuanContentAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public YanyuanContentAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return array == null ? 0 : (int) Math.ceil(array.length() * 1.00 / 4);
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
        array = contentArray;
//        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final JSONObject entity1 = array.optJSONObject(position * 4);
        JSONObject entity2 = null;
        JSONObject entity3 = null;
        JSONObject entity4 = null;
        if (array.length() > (position * 4 + 1)) {
            entity2 = array.optJSONObject(position * 4 + 1);
        }
        if (array.length() > (position * 4 + 2)) {
            entity3 = array.optJSONObject(position * 4 + 2);
        }
        if (array.length() > (position * 4 + 3)) {
            entity4 = array.optJSONObject(position * 4 + 3);
        }
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_yanyuan_content, null);
            holder = new ViewHolder();
            holder.ll_yanyuan_1 = view.findViewById(R.id.ll_yanyuan_1);
            holder.image_yanyuan_pic_1 = view.findViewById(R.id.image_yanyuan_pic_1);
            holder.txt_yanyuan_name_1 = view.findViewById(R.id.txt_yanyuan_name_1);

            holder.ll_yanyuan_2 = view.findViewById(R.id.ll_yanyuan_2);
            holder.image_yanyuan_pic_2 = view.findViewById(R.id.image_yanyuan_pic_2);
            holder.txt_yanyuan_name_2 = view.findViewById(R.id.txt_yanyuan_name_2);

            holder.ll_yanyuan_3 = view.findViewById(R.id.ll_yanyuan_3);
            holder.image_yanyuan_pic_3 = view.findViewById(R.id.image_yanyuan_pic_3);
            holder.txt_yanyuan_name_3 = view.findViewById(R.id.txt_yanyuan_name_3);

            holder.ll_yanyuan_4 = view.findViewById(R.id.ll_yanyuan_4);
            holder.image_yanyuan_pic_4 = view.findViewById(R.id.image_yanyuan_pic_4);
            holder.txt_yanyuan_name_4 = view.findViewById(R.id.txt_yanyuan_name_4);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
//        Glide.with(context).load(entity1.optString("actor_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_yanyuan_pic_1);
        try {
            ImageLoader.getInstance().displayImage(entity1.optString("actor_image"), holder.image_yanyuan_pic_1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txt_yanyuan_name_1.setText(entity1.optString("actor_name"));

        if (entity2 != null) {
            holder.ll_yanyuan_2.setVisibility(View.VISIBLE);
            try {
                ImageLoader.getInstance().displayImage(entity2.optString("actor_image"), holder.image_yanyuan_pic_2);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Glide.with(context).load(entity2.optString("actor_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_yanyuan_pic_2);
            holder.txt_yanyuan_name_2.setText(entity2.optString("actor_name"));
        } else {
            holder.ll_yanyuan_2.setVisibility(View.INVISIBLE);
        }

        if (entity3 != null) {
            holder.ll_yanyuan_3.setVisibility(View.VISIBLE);
            try {
                ImageLoader.getInstance().displayImage(entity3.optString("actor_image"), holder.image_yanyuan_pic_3);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Glide.with(context).load(entity3.optString("actor_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_yanyuan_pic_3);
            holder.txt_yanyuan_name_3.setText(entity3.optString("actor_name"));
        } else {
            holder.ll_yanyuan_3.setVisibility(View.INVISIBLE);
        }
        if (entity4 != null) {
            holder.ll_yanyuan_4.setVisibility(View.VISIBLE);
            try {
                ImageLoader.getInstance().displayImage(entity4.optString("actor_image"), holder.image_yanyuan_pic_4);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Glide.with(context).load(entity4.optString("actor_image")).placeholder(context.getResources().getDrawable(R.drawable.default_image)).into(holder.image_yanyuan_pic_4);
            holder.txt_yanyuan_name_4.setText(entity4.optString("actor_name"));
        } else {
            holder.ll_yanyuan_4.setVisibility(View.INVISIBLE);
        }

        holder.ll_yanyuan_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity1 != null) {
                    Intent intent = new Intent(context, YanyuanDetailActivity.class);
                    intent.putExtra("actor_id", entity1.optString("actor_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity = entity2;
        holder.ll_yanyuan_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity != null) {
                    Intent intent = new Intent(context, YanyuanDetailActivity.class);
                    intent.putExtra("actor_id", finalEntity.optString("actor_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity1 = entity3;
        holder.ll_yanyuan_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity1 != null) {
                    Intent intent = new Intent(context, YanyuanDetailActivity.class);
                    intent.putExtra("actor_id", finalEntity1.optString("actor_id"));
                    context.startActivity(intent);
                }

            }
        });
        final JSONObject finalEntity2 = entity4;
        holder.ll_yanyuan_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalEntity2 != null) {
                    Intent intent = new Intent(context, YanyuanDetailActivity.class);
                    intent.putExtra("actor_id", finalEntity2.optString("actor_id"));
                    context.startActivity(intent);
                }

            }
        });

        return view;
    }


    class ViewHolder {
        AutoLinearLayout ll_yanyuan_1, ll_yanyuan_2, ll_yanyuan_3, ll_yanyuan_4;
        CircleImageView image_yanyuan_pic_1, image_yanyuan_pic_2, image_yanyuan_pic_3, image_yanyuan_pic_4;
        TextView txt_yanyuan_name_1, txt_yanyuan_name_2, txt_yanyuan_name_3, txt_yanyuan_name_4;
    }
}