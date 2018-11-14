package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.DiscountAdapter;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.bean.DiscountBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.AppUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/5
 */
@SuppressLint("CheckResult")
public class DiscountActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private DiscountAdapter mAdapter = null;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_discount);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new DiscountAdapter());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            DiscountBean discountBean = mAdapter.getItem(position);
            Bundle bundle = new Bundle();
            switch (discountBean.getType()){
                case "openWeb":
                    bundle.putParcelable(NewsWebActivity.NEWS_OBJECT, discountBean);
                    bundle.putInt(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_URL);
                    bundle.putString(NewsWebActivity.NEWS_TITLE, discountBean.getTitle());
                    readyGo(NewsWebActivity.class, bundle);
                    break;
            }
        });
        getDataFromServer();
    }

    protected void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getDiscounts().compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .subscribe(discountBeans -> showDiscount(discountBeans), t -> RxException.getMessage(t));
    }

    private void showDiscount(List<DiscountBean> beans) {
        if (null == beans || beans.isEmpty()) {
            return ;
        }
        mAdapter.setNewData(beans);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
