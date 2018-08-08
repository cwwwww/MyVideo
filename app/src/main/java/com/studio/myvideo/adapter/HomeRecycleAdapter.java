package com.studio.myvideo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.myvideo.R;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/6/15.
 */

public class HomeRecycleAdapter extends RecyclerView.Adapter<HomeRecycleAdapter.MyViewHoler> implements View.OnClickListener {

    private Context context;
    private JSONArray array;
    private OnRecycleViewItemClickListener listener;
    private int selectedPosition=0;
    private int ResourceID;

    public HomeRecycleAdapter(Context context, JSONArray array,int resourceID) {
        this.context = context;
        this.array = array;
        this.ResourceID=resourceID;
    }

    @Override
    public MyViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(ResourceID, null);
        view.setOnClickListener(this);
        return new MyViewHoler(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHoler holder, final int position) {
        holder.fuction_name.setText((String) array.opt(position));
        if (selectedPosition==position){
            holder.fuction_name.setTextColor(context.getResources().getColor(R.color.cf19e38));
            holder.fuction_image.setSelected(true);
            holder.fuction_image.setVisibility(View.VISIBLE);
        }else {
            holder.fuction_name.setTextColor(context.getResources().getColor(R.color.cffffff));
            holder.fuction_image.setSelected(false);
            holder.fuction_image.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        return array==null?0:array.length();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.OnItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecycleViewItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedPosition(int position){
        selectedPosition=position;
    }

    public void setTitleArray(JSONArray titleArray){
        if (titleArray!=null){
            array=titleArray;
        }
        notifyDataSetChanged();
    }

    public interface OnRecycleViewItemClickListener {
        void OnItemClick(View view, int position);
    }


    class MyViewHoler extends RecyclerView.ViewHolder {
        private TextView fuction_name;
        private ImageView fuction_image;
        private AutoLinearLayout ll_layout;

        public MyViewHoler(View itemView) {
            super(itemView);
            fuction_name = itemView.findViewById(R.id.tv_jx);
            fuction_image = itemView.findViewById(R.id.iv_jx);
            ll_layout=itemView.findViewById(R.id.ll_jx);
        }
    }


}
