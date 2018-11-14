package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.worldfragment.adapter.WorldTypeAdapter;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.WorldBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * 发现界面
 * Created by Jerome on 2018/6/15.
 */
@SuppressLint("CheckResult")
public class WorldFragment extends BaseLazyFragment {

    private int page = 1;
    private int circleType ;
    private WorldBean worldBean;
    private WorldTypeAdapter mAdapter;
    private List<MultiTypeItem> list = new ArrayList<>();
    @BindView(R.id.iv_back) ImageView ivGoback;
    @BindView(R.id.iv_right) ImageView ivCamera;
    @BindView(R.id.tv_title) TextView tvTitle;
    @BindView(R.id.mRefreshLayout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;


    @Override
    protected void initDependencies() {

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_world;
    }

    @Override
    protected void initEventView() {
        setUserVisibleHint(true);

        ivGoback.setVisibility(View.GONE);
        ivCamera.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.world_circle));
        ivCamera.setImageResource(R.mipmap.ic_world_camera);

        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new WorldTypeAdapter());
        mAdapter.setEmptyView(ViewUtils.getCommEmpty(getActivity(), mRecyclerView, "暂无动态"));

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            worldBean = (WorldBean) mAdapter.getData().get(position).getData();
            switch (view.getId()) {
                case R.id.ivPlay:
                case R.id.ivPhoto:
                    playVideo();
                    break;
                case R.id.tvPraise:
                    posterPraise(view);
                    break;
                case R.id.tvComments:

                    break;
                case R.id.tvMore:

                    break;
            }
        });

        CommonUtils.showDialog(mContext, "加载中..");
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mRefreshLayout.finishRefresh(1000);
            getDataFromServer();
        });
    }


    @OnClick(R.id.iv_right) void onCameraClick() {
        readyGo(WorldPublishActivity.class);
    }

    @OnLongClick(R.id.iv_right) boolean onCameraLongClick(View v) {

        return true;
    }

    /**
     * 视频播放
     */
    private void playVideo() {
        if (worldBean == null) return;
        Bundle bundle = new Bundle();
        bundle.putString(VideoViewActivity.VIDEO_PATH, worldBean.getImagestr().get(1));
        readyGo(VideoViewActivity.class, bundle);
    }

    /**
     * 点赞
     */
    private void posterPraise(View view) {
        TextView tvPraise = view.findViewById(R.id.tvPraise);
        if (worldBean == null) return;
        ServiceFactory.createRetrofitServiceNoAes(ResApi.class, Constant.NEW_API_HOST, getContext())
                .posterPraise(ParamsMap.posterPraise(worldBean.getId()))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowOtherTransformer())
                .compose(bindUntilEvent(FragmentEvent.PAUSE))
                .subscribe(praiseBean -> {
                    toast("评论成功");
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_world_praise_press);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// 设置边界
                    tvPraise.setCompoundDrawables(drawable,null,null,null);
                    tvPraise.setText(String.valueOf(praiseBean.getPraises().size()));
                }, t -> {
                    toast(RxException.getMessage(t));
                    t.printStackTrace();
                });
    }

    @Override
    protected void getDataFromServer() {
        circleType = WorldFakeActivity.MINE_WORLD_CIRCLE;
        ResApi resApi = ServiceFactory.createRetrofitServiceNoAes(ResApi.class, Constant.NEW_API_HOST, getContext());
        if (circleType == WorldFakeActivity.MINE_WORLD_CIRCLE) {
            //获取所有好友的动态
            resApi.getFriends(ParamsMap.getCircleFriends(page))
                    .compose(RxSchedulers.ioFlowable()).compose(RxSchedulers.flowOtherTransformer()).compose(bindUntilEvent(FragmentEvent.PAUSE))
                    .subscribe(worldBeans -> showWorld(worldBeans), t -> {
                        CommonUtils.cencelDialog();
                        toast(RxException.getMessage(t));
                    });
        } else {
            //获取某人的朋友圈动态
            resApi.getOtherFriends(ParamsMap.getOtherCircle(page, getArguments().getString(WorldFakeActivity.CIRCLE_UID)))
                    .compose(RxSchedulers.ioFlowable()).compose(RxSchedulers.flowOtherTransformer()).compose(bindUntilEvent(FragmentEvent.PAUSE))
                    .subscribe(worldBeans -> showWorld(worldBeans), t -> {
                        CommonUtils.cencelDialog();
                        toast(RxException.getMessage(t));
                    });
        }
    }

    private void showWorld(List<WorldBean> worldBeans) {
        CommonUtils.cencelDialog();
        list.clear();
        if (null == worldBeans || worldBeans.isEmpty()) {
            return;
        }
        for (WorldBean worldBean : worldBeans) {
            switch (worldBean.getCategory()) {
                case MultiTypeItem.WORLD_TEXT_TYPE:
                    list.add(new MultiTypeItem<>(MultiTypeItem.WORLD_TEXT_TYPE, worldBean));
                    break;
                case MultiTypeItem.WORLD_TXTIMG_TYPE:
                    list.add(new MultiTypeItem<>(MultiTypeItem.WORLD_TXTIMG_TYPE, worldBean));
                    break;
                case MultiTypeItem.WORLD_VIDEO_TYPE:
                    list.add(new MultiTypeItem<>(MultiTypeItem.WORLD_VIDEO_TYPE, worldBean));
                    break;
                case MultiTypeItem.WORLD_MIN_VIDEO_TYPE:
                    list.add(new MultiTypeItem<>(MultiTypeItem.WORLD_MIN_VIDEO_TYPE, worldBean));
                    break;
            }
        }
        mAdapter.setNewData(list);
    }
}
