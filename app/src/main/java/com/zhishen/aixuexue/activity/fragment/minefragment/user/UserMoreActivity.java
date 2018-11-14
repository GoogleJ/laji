package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.ChooseLocationActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.UpdateCustomerByIdRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.ListPopWindow;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jerome on 2018/7/7
 */
public class UserMoreActivity extends BaseActivity implements ListPopWindow.ListOnItemClickListener {

    private int sex = -1;
    private Unbinder unbinder = null;
    private ListPopWindow listPopWindow = null;
    @BindView(R.id.tvSex)
    TextView tvSex;
    @BindView(R.id.tvlocation)
    TextView tvlocation;
    @BindView(R.id.tv_sign)
    TextView tv_sign;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_more);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        listPopWindow = new ListPopWindow(this);
        listPopWindow.setOnItemClickListener(this);
        listPopWindow.setOnItemClickListener(this::getListItemClick);

        String location = getIntent().getStringExtra("location");
        if (location != null && location.length() != 0) {
            tvlocation.setText(location);
        }

        String sex = getIntent().getStringExtra("sex");
        if (sex != null && sex.length() != 0) {
            tvSex.setText(sex);
        }

        String sign = getIntent().getStringExtra("sign");
        if (sign != null && sign.length() != 0) {
            tv_sign.setText(sign);
        }
    }

    @OnClick({R.id.llSign, R.id.llSex, R.id.lllocation})
    void onMethodCliCk(View v) {
        switch (v.getId()) {
            case R.id.llSign:
                readyGo(UserSignActivity.class);
                break;
            case R.id.llSex:
                showPopupWindow();
                break;
            case R.id.lllocation:
                readyGo(ChooseLocationActivity.class);
                break;
        }
    }

    private void showPopupWindow() {
        listPopWindow.showAtLocation(findViewById(android.R.id.content),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void getListItemClick(View v) {
        String str = null;
        switch (v.getId()) {
            case R.id.tvMan:
                str = "男";
                break;
            case R.id.tvWomen:
                str = "女";
                break;
        }
        sex = (str.equals("男") ? 1 : 0);

        UpdateCustomerByIdRequest request = new UpdateCustomerByIdRequest();
        request.setUserId(Long.parseLong(AiApp.getInstance().getUsername()));
        request.setSex(str);

        String finalStr = str;
        ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this)
                .updateCustomerById(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(responseDataT -> {
                    listPopWindow.dismiss();

                    if (responseDataT.code != 0) {
                        CommonUtils.showToastShort(UserMoreActivity.this, responseDataT.msg);
                        return;
                    }

                    CommonUtils.showToastShort(UserMoreActivity.this, "修改成功");
                    tvSex.setText(finalStr);
                }, throwable -> {
                    listPopWindow.dismiss();

                    throwable.printStackTrace();
                    CommonUtils.showToastShort(UserMoreActivity.this, "未知错误");
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String location = intent.getStringExtra("location");
        if (location != null && location.length() != 0) {
            tvlocation.setText(location);
        }

        String sex = intent.getStringExtra("sex");
        if (sex != null && sex.length() != 0) {
            tvSex.setText(sex);
        }

        String sign = intent.getStringExtra("sign");
        if (sign != null && sign.length() != 0) {
            tv_sign.setText(sign);
        }
    }
}
