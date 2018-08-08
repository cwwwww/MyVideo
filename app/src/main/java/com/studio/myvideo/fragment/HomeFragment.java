package com.studio.myvideo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studio.myvideo.R;
import com.studio.myvideo.adapter.HomeRecycleAdapter;
import com.studio.myvideo.view.MainViewPager;

import org.json.JSONArray;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/29.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;//二级分类
    @BindView(R.id.test_viewpager)
    MainViewPager test_viewpager;//容器

    private Unbinder unbinder;
    private View view;

    /**
     * 加载数据相关
     */
    private JSONArray array = new JSONArray();
    private HomeRecycleAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private int selectionPosition = 0;
    private JSONObject objectData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, null);
        unbinder = ButterKnife.bind(this, view);
        init();
        return view;
    }

    //控件实例化
    private void init() {
        try {
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
            recycler_view.setLayoutManager(linearLayoutManager);
            adapter = new HomeRecycleAdapter(mContext, array, R.layout.item_recycle_home);
            recycler_view.setAdapter(adapter);
            adapter.setSelectedPosition(selectionPosition);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(new HomeRecycleAdapter.OnRecycleViewItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    try {
                        selectionPosition = position;
                        adapter.setSelectedPosition(position);
                        adapter.notifyDataSetChanged();
                        test_viewpager.setCurrentItem(position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //设置数据
    public void setViewData(JSONObject object) {
        try {
            objectData = object;
            //设置标题栏信息
            setTitleMessage(object.optJSONArray("position_list"));
        } catch (Exception e) {
            Log.e("home_error_1", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 设置标题栏信息
     *
     * @param titleArray
     */
    private void setTitleMessage(JSONArray titleArray) {
        array = titleArray;
        if (adapter != null) {
            adapter.setTitleArray(titleArray);
        } else {
            recycler_view.setHasFixedSize(true);
            recycler_view.setItemAnimator(new DefaultItemAnimator());
            linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(OrientationHelper.HORIZONTAL);
            recycler_view.setLayoutManager(linearLayoutManager);
            adapter = new HomeRecycleAdapter(mContext, titleArray, R.layout.item_recycle_home);
            recycler_view.setAdapter(adapter);
            adapter.setSelectedPosition(selectionPosition);
            adapter.notifyDataSetChanged();
            adapter.setOnItemClickListener(new HomeRecycleAdapter.OnRecycleViewItemClickListener() {
                @Override
                public void OnItemClick(View view, int position) {
                    selectionPosition = position;
                    adapter.setSelectedPosition(position);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        test_viewpager.setNoScroll(false);
        test_viewpager.setAdapter(new simpleAdapter(getFragmentManager()));
        test_viewpager.setOffscreenPageLimit(array.length());
        test_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                selectionPosition = position;
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class simpleAdapter extends FragmentPagerAdapter {
        public simpleAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return array.length();
        }

        @Override
        public Fragment getItem(int position) {
            return ContentFragment.newInstance(objectData,position);
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
