package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.UpdatePassWRequest;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.SendCodeRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChangePassActivity extends BaseActivity {

    private TextView tv_changepass_phone;
    private EditText et_changepass_verify;
    private TextView tv_changepass_verify;
    private EditText er_changepass_newpass;

    private String code;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        tv_changepass_phone = findViewById(R.id.tv_changepass_phone);
        et_changepass_verify = findViewById(R.id.et_changepass_verify);
        tv_changepass_verify = findViewById(R.id.tv_changepass_verify);
        er_changepass_newpass = findViewById(R.id.er_changepass_newpass);
        tv_changepass_phone.setText(AiApp.getInstance().getUsertel());

        tv_changepass_verify.setOnClickListener(view -> {
            String string = tv_changepass_verify.getText().toString();
            if (string.equals("获取验证码") || string.equals("重新发送")) {
                getCode();
            }
        });

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_changepass_verify.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                tv_changepass_verify.setText("重新发送");
            }
        };
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    public void submit(View view) {
        String verify = et_changepass_verify.getText().toString();
        String newPass = er_changepass_newpass.getText().toString();

        if (verify.length() == 0) {
            CommonUtils.showToastShort(this, "请输入验证码");
            return;
        }

        if (newPass.length() == 0 || newPass.length() < 6 || newPass.length() > 16) {
            CommonUtils.showToastShort(this, "请输入正确的新密码");
            return;
        }

        if (!verify.equals(code)) {
            CommonUtils.showToastShort(this, "请输入正确验证码");
            return;
        }

        UpdatePassWRequest request = new UpdatePassWRequest();
        request.setPassword(newPass);
        request.setUserId(AiApp.getInstance().getUsername());

        CommonUtils.showDialog(this, "修改中");
        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .updatePassW(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(response -> {
                    CommonUtils.cencelDialog();
                    if (response.code != 0) {
                        CommonUtils.showToastShort(ChangePassActivity.this, response.msg);
                        return;
                    }
                    CommonUtils.showToastShort(ChangePassActivity.this, "修改成功！");
                    finish();
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    throwable.printStackTrace();
                    CommonUtils.showToastShort(ChangePassActivity.this, "未知错误");
                });
    }

    @SuppressLint("CheckResult")
    private void getCode() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, mActivity);
        SendCodeRequest request = new SendCodeRequest(AiApp.getInstance().getUsertel());
        countDownTimer.start();
        api.getSecurityCode(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataT -> {
                    if (dataT.code != 0) {
                        CommonUtils.showToastShort(ChangePassActivity.this, dataT.msg);
                        return;
                    }
                    CommonUtils.showToastShort(ChangePassActivity.this, "短信已发送");
                    code = (String) dataT.data;
                }, throwable -> {
                    countDownTimer.cancel();
                    tv_changepass_verify.setText("重新发送");
                    throwable.printStackTrace();
                    CommonUtils.showToastShort(ChangePassActivity.this, "未知错误");
                });
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }
}
