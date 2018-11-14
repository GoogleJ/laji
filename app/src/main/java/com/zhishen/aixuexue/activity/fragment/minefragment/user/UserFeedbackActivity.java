package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.AddReportRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CommonUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserFeedbackActivity extends BaseActivity {

    EditText etContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        setTitleCenter(AppUtils.getActivityLabel(this));
        etContent = findViewById(R.id.etContent);
    }

    @SuppressLint("CheckResult")
    public void submit(View view) {
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            CommonUtils.showToastShort(this, "请输入内容");
            return;
        }

        AddReportRequest reportRequest = new AddReportRequest();
        reportRequest.setFormUserid(AiApp.getInstance().getUsername());
        reportRequest.setInformContentTwo(content);
        reportRequest.setOperateType("2");

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .addReport(reportRequest.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(baseResponseDataT -> {
                    if (baseResponseDataT.code == 0) {
                        CommonUtils.showToastShort(UserFeedbackActivity.this, "反馈成功");
                        finish();
                        return;
                    }

                    CommonUtils.showToastShort(UserFeedbackActivity.this, baseResponseDataT.msg);
                }, throwable -> {

                });
    }

    public void back(View view) {
        finish();
    }
}
