package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.SendCodeRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgetPasswordActivity extends BaseActivity {

    private String originMobile;
    private String mobile;
    private String code = "";
    private EditText et_foregetpassword_verify;
    private TextView tv_forgetpassword_mobile;
    private TextView tv_forgetpassword_verify;
    private TextView tv_forgetpassword_next;

    private CountDownTimer countDownTimer;

    private boolean canNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mobile = getIntent().getStringExtra("mobile");
        originMobile = mobile;
        String substring = mobile.substring(0, 3);
        String substring1 = mobile.substring(7, mobile.length());
        mobile = substring + "****" + substring1;

        tv_forgetpassword_mobile = findViewById(R.id.tv_forgetpassword_mobile);
        et_foregetpassword_verify = findViewById(R.id.et_foregetpassword_verify);
        tv_forgetpassword_verify = findViewById(R.id.tv_forgetpassword_verify);
        tv_forgetpassword_next = findViewById(R.id.tv_forgetpassword_next);


        tv_forgetpassword_mobile.setText(mobile);

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_forgetpassword_verify.setText(millisUntilFinished / 1000 + "秒后重新获取");
            }

            @Override
            public void onFinish() {
                tv_forgetpassword_verify.setText("重新发送");
                tv_forgetpassword_verify.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.themecolor));
            }
        };

        tv_forgetpassword_verify.setOnClickListener(view -> {
            String string = tv_forgetpassword_verify.getText().toString();
            if (string.equals("获取验证码") || string.equals("重新发送")) {
                getCode();
            }
        });

        et_foregetpassword_verify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String string = editable.toString();

                if (!canNext && code.length() != 0 && string.equals(code)) {
                    canNext = true;
                    tv_forgetpassword_next.setBackgroundResource(R.drawable.shape_forgetpassword2);
                } else {
                    canNext = false;
                    tv_forgetpassword_next.setBackgroundResource(R.drawable.shape_forgetpassword);
                }
            }
        });

        tv_forgetpassword_next.setOnClickListener(view -> {
            if (!canNext) {
                return;
            }
            Intent intent = new Intent(this, ForgetPasswordActivity2.class);
            intent.putExtra("mobile", originMobile);
            startActivity(intent);
        });
    }

    public void back(View view) {
        finish();
    }

    @SuppressLint("CheckResult")
    private void getCode() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, mActivity);
        SendCodeRequest request = new SendCodeRequest(originMobile);
        countDownTimer.start();
        tv_forgetpassword_verify.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.textcolor3));
        api.getSecurityCode(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataT -> {
                    if (dataT.code != 0) {
                        CommonUtils.showToastShort(ForgetPasswordActivity.this, dataT.msg);
                        return;
                    }
                    CommonUtils.showToastShort(ForgetPasswordActivity.this, "短信已发送");
                    code = (String) dataT.data;
                }, throwable -> {
                    countDownTimer.cancel();
                    tv_forgetpassword_verify.setText("重新发送");
                    tv_forgetpassword_verify.setTextColor(ContextCompat.getColor(ForgetPasswordActivity.this, R.color.themecolor));
                    CommonUtils.showToastShort(ForgetPasswordActivity.this, "未知错误");
                    throwable.printStackTrace();
                });
    }


    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }
}
