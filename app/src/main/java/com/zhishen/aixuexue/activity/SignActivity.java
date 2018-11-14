package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetSignInfoRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.SignDetailResponse;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.SignLayout;


@SuppressLint("CheckResult")
public class SignActivity extends BaseActivity {
    SignLayout signlayout;
    LinearLayout ll_signrules;
    TextView tv_sign_score;
    TextView tv_sign_days;
    TextView tv_sign_desc;
    Button btn_sign;

    boolean hasSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        transparentStatusBar();

        signlayout = findViewById(R.id.signlayout);
        btn_sign = findViewById(R.id.tv_groupinfo_id);
        ll_signrules = findViewById(R.id.ll_signrules);
        tv_sign_score = findViewById(R.id.iv_group_avatar);
        tv_sign_days = findViewById(R.id.tv_sign_days);
        tv_sign_desc = findViewById(R.id.tv_sign_desc);

        GetSignInfoRequest request = new GetSignInfoRequest();
        request.setUserId(AiApp.getInstance().getUsername());

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .getSignInfo(request.decode())
                .compose(RxSchedulers.ioFlowable())
                .compose(bindToLifecycle())
                .subscribe(signDetailResponseBaseResponseDataT -> {
                    if (signDetailResponseBaseResponseDataT.code != 0) {
                        CommonUtils.showToastShort(SignActivity.this, signDetailResponseBaseResponseDataT.msg);
                        return;
                    }

                    SignDetailResponse data = signDetailResponseBaseResponseDataT.data;

                    hasSign = data.isSignIn();
                    if (hasSign) {
                        btn_sign.setText("已签到");
                    }

                    tv_sign_days.setText(data.getIntegralDesc());
                    tv_sign_score.setText(data.getIntegral());
                    tv_sign_desc.setText(data.getDescribe());

                    signlayout.items = data.getList();
                    signlayout.removeAllViews();
                    signlayout.createItem();

                    for (int i = 0; i < data.getList().size(); i++) {
                        SignDetailResponse.ListBean listBean = data.getList().get(i);

                        boolean b = Boolean.parseBoolean(listBean.getIsSignIn());
                        signlayout.setItemChecked(b, i);
                    }

                    signlayout.setVisibility(View.VISIBLE);
                    ll_signrules.setVisibility(View.VISIBLE);
                }, throwable -> {

                });
    }

    public void sign(View view) {

        if (hasSign) {
            CommonUtils.showToastShort(this, "请勿重复签到");
            return;
        }

        GetSignInfoRequest request = new GetSignInfoRequest();
        request.setUserId(AiApp.getInstance().getUsername());

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .userSignIn(request.decode())
                .compose(RxSchedulers.ioFlowable())
                .compose(bindToLifecycle())
                .subscribe(signDetailResponseBaseResponseDataT -> {
                    if (signDetailResponseBaseResponseDataT.code != 0) {
                        CommonUtils.showToastShort(SignActivity.this, signDetailResponseBaseResponseDataT.msg);
                        return;
                    }
                    signlayout.removeAllViews();

                    SignDetailResponse data = signDetailResponseBaseResponseDataT.data;

                    tv_sign_days.setText(data.getIntegralDesc());
                    tv_sign_score.setText(data.getIntegral());
                    tv_sign_desc.setText(data.getDescribe());

                    signlayout.items = data.getList();
                    signlayout.createItem();

                    for (int i = 0; i < data.getList().size(); i++) {
                        SignDetailResponse.ListBean listBean = data.getList().get(i);

                        boolean b = Boolean.parseBoolean(listBean.getIsSignIn());
                        signlayout.setItemChecked(b, i);
                    }
                    btn_sign.setText("已签到");
                    hasSign = true;
                    CommonUtils.showToastShort(this, "签到成功");
                }, throwable -> {

                });
    }

    public void back(View view) {
        finish();
    }

    private void transparentStatusBar() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                w.setStatusBarColor(Color.TRANSPARENT);
            } else {
                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

}
