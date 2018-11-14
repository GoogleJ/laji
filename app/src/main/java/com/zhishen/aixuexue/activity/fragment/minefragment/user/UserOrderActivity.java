package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter.OrderAdapter;
import com.zhishen.aixuexue.util.ViewUtils;

import java.util.Arrays;

import butterknife.BindView;

/**
 * Created by Jerome on 2018/7/3
 */
public class UserOrderActivity extends BaseActivity{

    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;
    private String[] mTitles = new String[]{"全部","待支付","已支付","已完成"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order);

        mViewPager.setCurrentItem(1);
        mViewPager.setOffscreenPageLimit(mTitles.length);
        mViewPager.setAdapter( new OrderAdapter(getSupportFragmentManager(), Arrays.asList(mTitles)));
        mTabLayout.setupWithViewPager(mViewPager);
        ViewUtils.setIndicator(mTabLayout,10,10);
    }
}
