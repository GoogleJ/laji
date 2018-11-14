package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseLazyFragment;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.HomeTypeAdapter;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.manager.RouteManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Jerome on 2018/7/4
 */
@SuppressLint("CheckResult")
public class NewsFragment extends BaseLazyFragment {


    public static final String NEWS_TYPE_ID = "news_type_id";
    private String typeID;
    private HomeTypeAdapter mAdapter;
    private List<MultiTypeItem> list = new ArrayList<>();
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void initDependencies() {

    }

    public static NewsFragment getInstance() {
        return new NewsFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initEventView() {
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter = new HomeTypeAdapter());

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            switch (adapter.getItemViewType(position)) {
                case MultiTypeItem.HOME_NEWS_TXT:
                case MultiTypeItem.HOME_NEWS_LEFT:
                case MultiTypeItem.HOME_NEWS_BOTTOM_IMGS:
                case MultiTypeItem.HOME_NEWS_IMG:
                case MultiTypeItem.HOME_NEWS_VIDEO:
                   NewsBean homeNews = (NewsBean) mAdapter.getData().get(position).getData();
                    bundle.putSerializable(NewsWebActivity.NEWS_OBJECT, homeNews);
                    if (homeNews == null) return;
                    RouteManager.getInstance(mContext).doAction(homeNews);
                    break;
            }
        });
    }

    @Override
    protected void getDataFromServer() {
        typeID = getArguments().getString(NEWS_TYPE_ID);
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, getContext())
                .getNewsType(ParamsMap.getNewsType(1,typeID)).compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(homeNews -> showNews(homeNews.getList()), t -> toast(RxException.getMessage(t)));
    }

    private void showNews(List<NewsBean> data) {
        if (data != null && !data.isEmpty()) {
            for (NewsBean homeNews : data) {
                switch (homeNews.getStyle()) {
                    case MultiTypeItem.HOME_NEWS_LEFT:
                        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_LEFT, homeNews));
                        break;
                    case MultiTypeItem.HOME_NEWS_IMG:
                        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_IMG, homeNews));
                        break;
                    case MultiTypeItem.HOME_NEWS_BOTTOM_IMGS:
                        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_BOTTOM_IMGS, homeNews));
                        break;
                    case MultiTypeItem.HOME_NEWS_VIDEO:
                        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_VIDEO, homeNews));
                        break;
                    case MultiTypeItem.HOME_NEWS_TXT:
                        list.add(new MultiTypeItem<>(MultiTypeItem.HOME_NEWS_TXT,homeNews));
                        break;
                    default:
                        break;
                }
            }
            mAdapter.replaceData(list);
        }
    }
}
