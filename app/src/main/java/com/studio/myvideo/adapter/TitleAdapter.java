package com.studio.myvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.studio.myvideo.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/6/24.
 */

public class TitleAdapter extends BaseAdapter {
    private Context context;
    private JSONArray array;
    private LayoutInflater inflater;
    private int selectedPosition=0;

    public TitleAdapter(Context context, JSONArray array) {
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
        array = contentArray;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        final JSONObject entity = array.optJSONObject(position);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_gradview_title, null);
            holder = new ViewHolder();
            holder.btn_yanyuan_search_1 = view.findViewById(R.id.btn_yanyuan_search_1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.btn_yanyuan_search_1.setText(entity.optString("grade_name"));
        if (selectedPosition==position){
            holder.btn_yanyuan_search_1.setSelected(true);
        }else {
            holder.btn_yanyuan_search_1.setSelected(false);
        }
        return view;
    }

    public void setSelectedPosition(int position){
        selectedPosition=position;
    }

    class ViewHolder {
        Button btn_yanyuan_search_1;
    }
}
