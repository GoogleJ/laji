package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.NewsAdapter;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/4
 */
public class NewsReportActivity extends BaseActivity {


    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;
    private Unbinder unbinder;
    private int position = 0;
    public static final String TAB_PAGE = "tab_page";
    private List<BaseLazyFragment> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));
        position = getIntent().getIntExtra(TAB_PAGE, 0);

        HomeBean homeBean = LocalUserManager.getInstance().getAppconfig();
        for(HomeBean.HomeNewsCategory category :homeBean.getNewsCategory()) {
            list.add(new NewsFragment());
        }
        if (null != homeBean) {
            mViewPager.setOffscreenPageLimit(list.size());
            mViewPager.setAdapter(new NewsAdapter(getSupportFragmentManager(),list,homeBean.getNewsCategory()));
            mTabLayout.setupWithViewPager(mViewPager);
            ViewUtils.setIndicator(mTabLayout, 10, 10);
            mViewPager.setCurrentItem(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
