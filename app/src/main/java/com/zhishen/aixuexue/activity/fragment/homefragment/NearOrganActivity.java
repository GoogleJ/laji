package com.zhishen.aixuexue.activity.fragment.homefragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.LocationUtil;
import com.zhishen.aixuexue.weight.filterview.FilterData;
import com.zhishen.aixuexue.weight.filterview.FilterView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;

/**
 * Created by Jerome on 2018/7/5
 */
@SuppressLint("CheckResult")
public class NearOrganActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private NearOrganAdapter mAdapter;
    private FilterData filterData;
    private AMapLocationClient mLocationClient;
    private LatLng latLng;
    private boolean isErr;
    private int currentPage = 1;
    private int totalPage;
    @BindView(R.id.mRefreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.realFilterView)
    FilterView realFilterView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_organ);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));
        initAMapLocation();

        /*realFilterView.post(() -> {
            filterData = new FilterData();
            filterData.setNears(DataServer.getNearData());
            filterData.setLanguage(DataServer.getLanguage());
            filterData.setSorts(DataServer.getSortData());
            realFilterView.setFilterData(this, filterData);
            mRecyclerView.setPadding(0, realFilterView.getHeight()+10, 0, 0);
        });*/
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter = new NearOrganAdapter());
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Bundle bundle = new Bundle();
            NearOrganBean organBean = mAdapter.getItem(position);
            bundle.putString(MechanismActivity.MECHAIN_ID, organBean.getId());
            readyGo(MechanismActivity.class, bundle);
        });

        realFilterView.setOnFilterClickListener(position -> {
            realFilterView.show(position);
        });

        realFilterView.setOnItemNearClickListener((leftEntity, rightEntity) -> {

        });

        realFilterView.setOnItemLanguageClickListener((leftEntity, rightEntity) -> {

        });

        realFilterView.setOnItemSortClickListener(entity -> {

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
                Log.d("currentPage;", currentPage + "----");
                if (currentPage > totalPage) {
                    refreshLayout.finishLoadMore();
                } else {
                    if (isErr) {
                        getDataFromServer();
                        refreshLayout.finishLoadMore();
                    } else {
                        refreshLayout.finishLoadMore(false);
                    }
                }
            }
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
            latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
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

    @OnClick(R.id.tvSearch)
    void onClick() {
        readyGo(NearSearchActivity.class);
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
                        isErr = false;
                    }
                });
    }

    private List<NearOrganBean> convertData(List<NearOrganBean> organBean) {
        for (NearOrganBean bean : organBean) {
            bean.setDistance(LocationUtil.calculateLineDistance(latLng, new LatLng(bean.getLat(), bean.getLon())));
        }
        return organBean;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    @Override
    public void onBackPressed() {
        if (realFilterView.isShowing()) {
            realFilterView.hide();
        } else {
            super.onBackPressed();
        }
    }
}
