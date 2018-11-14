package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter.NoticeAdapter;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
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
 */
@SuppressLint("CheckResult")
public class NoticeListActivity extends BaseActivity {

    private Unbinder unbinder;
    private NoticeAdapter mAdapter;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new NoticeAdapter());
        mAdapter.setEmptyView(ViewUtils.getCommEmpty(this, mRecyclerView, "暂无消息通知"));
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            HomeNoticeBean.NoticeBean noticeBean = mAdapter.getItem(position);
            if (noticeBean == null) return;
            Bundle bundle = new Bundle();
            bundle.putSerializable(NoticeDetailActivity.NOTICE_INFO, noticeBean);
            readyGo(NoticeDetailActivity.class, bundle);
        });
        getDataFromServer();
    }

    protected void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getNotice(ParamsMap.getNotice(1, AiApp.getInstance().getUsername()))
                .compose(RxSchedulers.ioFlowable()).compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(homeNoticeBean -> showNotices(homeNoticeBean.getList()), t -> toast(RxException.getMessage(t)));
    }

    private void showNotices(List<HomeNoticeBean.NoticeBean> list) {
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
