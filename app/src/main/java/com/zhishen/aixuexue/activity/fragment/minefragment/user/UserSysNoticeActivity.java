package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.NoticeDetailActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.adapter.SystemNoticeAdapter;
import com.zhishen.aixuexue.bean.SystemNoticeBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/5
 */
@SuppressLint("CheckResult")
public class UserSysNoticeActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private SystemNoticeAdapter mAdapter = null;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sys_notice);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new SystemNoticeAdapter());
        mAdapter.setEmptyView(ViewUtils.getCommEmptyRes(this,mRecyclerView, "暂无系统消息",R.mipmap.ic_empty_sys));
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            SystemNoticeBean noticeBean = mAdapter.getItem(position);
            if (noticeBean == null) return;
            noticeBean.setRead(true);
            mAdapter.notifyItemChanged(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable(NoticeDetailActivity.NOTICE_INFO, noticeBean);
            readyGo(NoticeDetailActivity.class, bundle);

        });
        getDataFromServer();
    }

    private void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getSysNotice(ParamsMap.getPage(1))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .subscribe(notices -> showSysNotice(notices.getList()), t -> toast(RxException.getMessage(t)));
    }

    private void showSysNotice(List<SystemNoticeBean> notices) {
        if (null == notices || notices.isEmpty()) {
            mAdapter.setEmptyView(ViewUtils.getCommEmpty(this, mRecyclerView,"暂无系统消息"));
            return;
        }
        mAdapter.setNewData(notices);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
