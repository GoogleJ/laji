package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.NewsFragment;
import com.zhishen.aixuexue.bean.HomeBean;

import java.util.List;

/**
 * Created by Jerome on 2018/7/4
 */
public class NewsAdapter extends FragmentStatePagerAdapter {

    private List<BaseLazyFragment> fragments;
    private List<HomeBean.HomeNewsCategory> list;

    public NewsAdapter(FragmentManager fm, List<BaseLazyFragment> fragments, List<HomeBean.HomeNewsCategory> data) {
        super(fm);
        this.fragments = fragments;
        this.list = data;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment = fragments.get(position);
        Log.d("getItem", list.get(position).typeid + "-----");
        bundle.putString(NewsFragment.NEWS_TYPE_ID, list.get(position).typeid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).title;
    }
}
