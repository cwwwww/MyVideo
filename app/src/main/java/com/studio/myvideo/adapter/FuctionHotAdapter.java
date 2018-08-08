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

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/25.
 */

public class FuctionHotAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;

    public FuctionHotAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return array == null ? 0 : array.length();
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
        ViewHolder holder = null;
        final JSONObject entity = (JSONObject) array.opt(position);
        if (view == null) {
            view = inflater.inflate(R.layout.item_list_yanyuan_hot, null);
            holder = new ViewHolder();
            holder.btn_hot_type1 = view.findViewById(R.id.btn_hot_type1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.btn_hot_type1.setText(entity.optString("label_name"));
        holder.btn_hot_type1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchActivity.class);
                intent.putExtra("label_id", entity.optString("label_id"));
                context.startActivity(intent);
            }
        });
        return view;
    }

    class ViewHolder {
        Button btn_hot_type1;
    }
}
