package com.zhishen.aixuexue.activity.fragment.minefragment.follow;

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
import com.zhishen.aixuexue.activity.fragment.minefragment.adapter.FollowTchAdapter;
import com.zhishen.aixuexue.bean.FollowBean;
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
 * Created by Jerome on 2018/6/30
 */
@SuppressLint("CheckResult")
public class FollowPeopleActivity extends BaseActivity {


    private int page = 1;
    private Unbinder unbinder;
    private int position = -1;
    private FollowTchAdapter mAdapter;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_tch);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new FollowTchAdapter());
        mAdapter.setEmptyView(ViewUtils.getCommEmptyRes(this, mRecyclerView,
                "暂无关注的人",R.mipmap.ic_empty_teacher));
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.tvFocus) {
                this.position = position;
                FollowBean followBean = mAdapter.getItem(position);
                followTeacher(followBean);
            }
        });

        getDataFromServer();
    }

    private void followTeacher(FollowBean followBean) {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .follow(ParamsMap.getFollow(followBean.getId(),ResApi.USER_TYPE_FRIENDS))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransString())
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(s -> {
                    toast("操作成功");
                    mAdapter.remove(this.position);
                }, t -> toast(RxException.getMessage(t)));
    }


    private void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getFollowTches(ParamsMap.getFollowPeople(AiApp.getInstance().getUsername(),page))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(tchBeans -> showFollowData(tchBeans), t -> toast(RxException.getMessage(t)));
    }

    private void showFollowData(List<FollowBean> tchBeans) {
        if (tchBeans == null || tchBeans.isEmpty()){
            return;
        }
        mAdapter.setNewData(tchBeans);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
