package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserMoreActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.UpdateCustomerByIdRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChooseLocationActivity extends BaseActivity {

    private ListView lv_chooselocation;
    private Adapter adapter;
    private TextView tv_chooselocation_title;

    ArrayList<OfflineMapProvince> provinces;
    ArrayList<OfflineMapCity> cities;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        OfflineMapManager amapManager = new OfflineMapManager(this, null);

        tv_chooselocation_title = findViewById(R.id.tv_chooselocation_title);
        lv_chooselocation = findViewById(R.id.lv_chooselocation);

        String province = getIntent().getStringExtra("province");
        if (!TextUtils.isEmpty(province)) {
            cities = amapManager.getItemByProvinceName(province).getCityList();
            tv_chooselocation_title.setText(province);
        } else {
            provinces = amapManager.getOfflineMapProvinceList();
            tv_chooselocation_title.setText("中国");
        }

        adapter = new Adapter();
        lv_chooselocation.setAdapter(adapter);

        lv_chooselocation.setOnItemClickListener((adapterView, view, i, l) -> {
            if (cities == null) {
                Constant.chooseLocation = provinces.get(i).getProvinceName();
                Intent intent = new Intent(this, ChooseLocationActivity.class);
                intent.putExtra("province", provinces.get(i).getProvinceName());
                startActivity(intent);
            } else {
                Constant.chooseLocation = Constant.chooseLocation + " - " + cities.get(i).getCity();

                UpdateCustomerByIdRequest request = new UpdateCustomerByIdRequest();
                request.setUserId(Long.parseLong(AiApp.getInstance().getUsername()));
                request.setCity(Constant.chooseLocation);

                CommonUtils.showDialog(this, "修改中");
                ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this)
                        .updateCustomerById(request.decode())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe(responseDataT -> {
                            CommonUtils.cencelDialog();
                            if (responseDataT.code != 0) {
                                CommonUtils.showToastShort(ChooseLocationActivity.this, responseDataT.msg);
                                return;
                            }

                            JSONObject userJson = AiApp.getInstance().getUserJson();
                            userJson.put(Constant.JSON_KEY_CITY, Constant.chooseLocation);
                            AiApp.getInstance().setUserJson(userJson);

                            CommonUtils.showToastShort(ChooseLocationActivity.this, "修改成功");
                            Intent intent = new Intent(ChooseLocationActivity.this, UserMoreActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("location", Constant.chooseLocation);
                            Constant.chooseLocation = "";
                            startActivity(intent);
                        }, throwable -> {
                            CommonUtils.cencelDialog();
                            throwable.printStackTrace();
                            CommonUtils.showToastShort(ChooseLocationActivity.this, "未知错误");
                        });
            }
        });

    }

    public void back(View view) {
        if (cities == null) {
            Constant.chooseLocation = "";
        }
        finish();
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cities == null ? provinces.size() : cities.size();
        }

        @Override
        public Object getItem(int i) {
            return cities == null ? provinces.get(i) : cities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();
                view = View.inflate(viewGroup.getContext(), R.layout.item_chooselocation, null);
                holder.tv_item_chooselocation = view.findViewById(R.id.tv_item_chooselocation);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (cities == null) {
                holder.tv_item_chooselocation.setText(provinces.get(i).getProvinceName());
            } else {
                holder.tv_item_chooselocation.setText(cities.get(i).getCity());
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_item_chooselocation;
    }
}
