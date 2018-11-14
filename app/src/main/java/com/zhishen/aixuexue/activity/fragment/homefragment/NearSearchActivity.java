package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.LatLng;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.MechanismActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.NearOrganAdapter;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.bean.NearOrganBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.LocationUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;

/**
 * Created by Jerome on 2018/7/27
 */
@SuppressLint("CheckResult")
public class NearSearchActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private boolean isErr;
    private int totalPage;
    private LatLng latLng;
    private int currentPage = 1;
    private String searchContent;
    private NearOrganAdapter mAdapter;
    private AMapLocationClient mLocationClient;
    @BindView(R.id.etContent) EditText etContent;
    @BindView(R.id.mRefreshLayout) SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_search);
        unbinder = ButterKnife.bind(this);
        initAMapLocation();
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new NearOrganAdapter());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            NearOrganBean organBean = mAdapter.getItem(position);
            bundle.putString(MechanismActivity.MECHAIN_ID, organBean.getId());
            readyGo(MechanismActivity.class, bundle);
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                currentPage = 1;
                mRefreshLayout.finishRefresh(1000);
                getDataFromServer();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++currentPage;
                if (currentPage > totalPage){
                    refreshLayout.finishLoadMore();
                } else {
                    if (isErr){
                        getDataFromServer();
                        refreshLayout.finishLoadMore();
                    } else {
                        refreshLayout.finishLoadMore(false);
                    }
                }
            }
        });
    }

    @OnClick(R.id.tvSearch) void onMethodClick(){
        searchContent = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(searchContent)) {
            toast("搜索内容不能为空");
            return ;
        }
        hideKeyboard();
        CommonUtils.showDialog(this,"搜索中..");
        getDataFromServer();
    }

    private void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getNearOrgans(ParamsMap.getNearOrgan(currentPage))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindToLifecycle())
                .subscribe(organBean -> {
                    currentPage = organBean.getPage().getPageNo();
                    totalPage = organBean.getPage().getTotalPage();
                    showNearOrgans(organBean);
                }, t -> {
                    CommonUtils.cencelDialog();
                    toast(RxException.getMessage(t));
                });
    }

    private void initAMapLocation() {
        CommonUtils.showDialog(this, "正在加载..");
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation.getErrorCode() != 0) {
                Log.d("onLocationChanged", "code:" + aMapLocation.getErrorCode()
                        + ", errInfo:" + aMapLocation.getErrorInfo());
                return;
            }
            latLng = new LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude());
            mLocationClient.stopLocation();
            getDataFromServer();
        });
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void showNearOrgans(NearOrganBean organBean) {
        CommonUtils.cencelDialog();
        if (null == organBean) return;
        Flowable.just(organBean.getList())
                .map(organBeans -> convertData(organBeans))
                .compose(RxSchedulers.ioFlowable())
                .subscribe(organBeans -> {
                    if (null != organBean.getList() && !organBean.getList().isEmpty()) {
                        isErr = true;
                        if (currentPage == 1) {
                            mAdapter.setNewData(organBean.getList());
                        } else {
                            mAdapter.addData(organBean.getList());
                        }
                    } else {
                        currentPage = 1;
                        isErr =false;
                    }
                });
    }

    private List<NearOrganBean> convertData(List<NearOrganBean> organBean){
        for (NearOrganBean bean :organBean) {
            bean.setDistance(LocationUtil.calculateLineDistance(latLng, new LatLng(bean.getLat(),bean.getLon())));
        }
        return organBean;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null){
            unbinder.unbind();
        }
    }
}
