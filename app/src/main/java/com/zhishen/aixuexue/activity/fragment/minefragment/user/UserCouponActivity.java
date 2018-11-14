package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter.CouponAdapter;
import com.zhishen.aixuexue.bean.CouponBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/2
 * 优惠券
 */
@SuppressLint("CheckResult")
public class UserCouponActivity extends BaseActivity {

    private Unbinder unbinder;
    private CouponAdapter mAdapter;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new CouponAdapter());
        mAdapter.setEmptyView(ViewUtils.getCommEmptyRes(this, mRecyclerView,"暂无优惠券",R.mipmap.ic_empty_coupon));
        getDataFromServer();
    }

    protected void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getCoupons(ParamsMap.getCoupon(AiApp.getInstance().getUsername()))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(couponBeans -> showCoupons(couponBeans), t ->  toast(RxException.getMessage(t)));
    }

    private void showCoupons(List<CouponBean> list) {
        if (list == null || list.isEmpty()) {
            return ;
        }
        mAdapter.setNewData(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
