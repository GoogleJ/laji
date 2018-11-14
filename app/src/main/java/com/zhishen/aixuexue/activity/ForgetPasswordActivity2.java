package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.UpdatePassWRequest;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ForgetPasswordActivity2 extends BaseActivity implements TextWatcher {

    String mobile;

    EditText et_forgetpassword2_pas1;
    EditText et_forgetpassword2_pas2;
    TextView tv_forgetpassword2_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);

        mobile = getIntent().getStringExtra("mobile");

        et_forgetpassword2_pas1 = findViewById(R.id.et_forgetpassword2_pas1);
        et_forgetpassword2_pas2 = findViewById(R.id.et_forgetpassword2_pas2);
        tv_forgetpassword2_done = findViewById(R.id.tv_forgetpassword2_done);

        et_forgetpassword2_pas1.addTextChangedListener(this);

        et_forgetpassword2_pas2.addTextChangedListener(this);

        tv_forgetpassword2_done.setOnClickListener(view -> {
            if (canChange) {
                resetPassword();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    private boolean canChange = false;

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (et_forgetpassword2_pas1.getText().toString().length() < 6 &&
                et_forgetpassword2_pas2.getText().toString().length() < 6) {
            return;
        }

        if (!canChange && et_forgetpassword2_pas1.getText().toString().equals(et_forgetpassword2_pas2.getText().toString())) {
            tv_forgetpassword2_done.setBackgroundResource(R.drawable.shape_forgetpassword2);
            canChange = true;
        } else if (canChange && !et_forgetpassword2_pas1.getText().toString().equals(et_forgetpassword2_pas2.getText().toString())) {
            tv_forgetpassword2_done.setBackgroundResource(R.drawable.shape_forgetpassword);
            canChange = false;
        }
    }

    @SuppressLint("CheckResult")
    private void resetPassword() {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        CommonUtils.showDialog(mActivity, "正在重置...");
        api.resetPassword(mobile, et_forgetpassword2_pas1.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(jsonObject -> {
                    int code = jsonObject.getIntValue("code");
                    CommonUtils.cencelDialog();
                    switch (code) {
                        case 1:
                            CommonUtils.showToastShort(ForgetPasswordActivity2.this, "修改成功");

                            Intent intent = new Intent(ForgetPasswordActivity2.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            return;
                        default:
                            break;
                    }
                }, throwable -> {
                    CommonUtils.showToastShort(ForgetPasswordActivity2.this, "修改失败");
                    CommonUtils.cencelDialog();
                });

    }
}
