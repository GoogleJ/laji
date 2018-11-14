package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.adapter.WorldLocalAdapter;
import com.zhishen.aixuexue.bean.LocalBean;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.weight.manager.LinearLayoutManagerPlus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/14
 */
public class WorldLocalActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private WorldLocalAdapter mAdapter;
    @BindView(R.id.btn_rtc) TextView tvOver;
    @BindView(R.id.tvLocal) TextView tvLocal;
    @BindView(R.id.mRecyclerView) RecyclerView mRecyclerView;
    private List<LocalBean> list = new ArrayList<>();
    private AMapLocationClient mLocationClient;
    private double latitude;
    private double longitude;
    private LocalBean item;
    private String cityCode;
    private LatLng latLng;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_local);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        tvOver.setVisibility(View.VISIBLE);
        tvOver.setText(getString(R.string.mis_action_done));
        tvOver.setTextColor(ContextCompat.getColor(this, R.color.blue));
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new LinearLayoutManagerPlus(this));
        mRecyclerView.setAdapter(mAdapter = new WorldLocalAdapter());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            item = mAdapter.getItem(position);
            for(LocalBean bean: list){
                bean.setSelected(false);
            }
            item.setSelected(true);
            mAdapter.notifyDataSetChanged();
        });
        initAMapLocation();
    }

    @OnClick({R.id.btn_rtc,R.id.tvDisplay}) void onOverClick(View v){
        if (v.getId() == R.id.btn_rtc){
            Intent intent = getIntent();
            intent.putExtra(WorldPublishActivity.LOCAL_INFO, item);
            setResult(Activity.RESULT_OK, intent);
        }
        onBackPressed();
    }

    private void initAMapLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation.getErrorCode() != 0) {
                Log.d("onLocationChanged", "code:" + aMapLocation.getErrorCode()
                        + ", errInfo:" + aMapLocation.getErrorInfo());
                return;
            }
            tvLocal.setText(aMapLocation.getCity());
            cityCode = aMapLocation.getCityCode();
            latitude = aMapLocation.getLatitude();
            longitude = aMapLocation.getLongitude();
            latLng = new LatLng(latitude, longitude);
            mLocationClient.stopLocation();
            initPoiSearch();
            poiSearch.searchPOIAsyn();
        });
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    private void initPoiSearch() {
        query = new PoiSearch.Query("附近", "", cityCode);
        query.setPageSize(30);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 300));
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                for(PoiItem p:poiResult.getPois()){
                    list.add(new LocalBean(p));
                }
                mAdapter.setNewData(list);
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
