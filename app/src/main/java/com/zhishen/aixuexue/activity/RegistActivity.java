package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.homefragment.NewsWebActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.SendCodeRequest;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class RegistActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_regist_mobile)
    EditText etRegistMobile;
    @BindView(R.id.checkbox_regist)
    CheckBox checkboxRegist;
    @BindView(R.id.iv_regist_ok)
    ImageView ivRegistOk;
    @BindView(R.id.et_regist_code)
    EditText etRegistCode;
    @BindView(R.id.et_regist_passworld)
    EditText etRegistPassword;
    @BindView(R.id.tv_regist_sendcode)
    TextView tvRegistSendcode;
    private CountDownTimer countDownTimer;
    private String serviceCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        setTitle("注册");
        etRegistMobile.addTextChangedListener(new TextChange());
        etRegistCode.addTextChangedListener(new TextChange());
        etRegistPassword.addTextChangedListener(new TextChange());
        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvRegistSendcode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                tvRegistSendcode.setText("重新发送");
            }
        };
    }

    @OnClick({R.id.iv_regist_ok, R.id.tv_regist_sendcode, R.id.tv_regist_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_regist_ok:
                String mobile = etRegistMobile.getText().toString();
                String password = etRegistPassword.getText().toString();
                String code = etRegistCode.getText().toString();
                if (StringUtils.isEmpty(mobile)) {
                    CommonUtils.showToastShort(mActivity, "请输入手机号");
                    return;
                }
                if (StringUtils.isEmpty(password)) {
                    CommonUtils.showToastShort(mActivity, "请输入密码");
                    return;
                }
                if (StringUtils.isEmpty(code)) {
                    CommonUtils.showToastShort(mActivity, "请输入验证码");
                    return;
                }
                if (!checkboxRegist.isChecked()) {
                    CommonUtils.showToastShort(mActivity, "请勾选注册协议");
                    return;
                }
                if (!serviceCode.equals(code)) {
                    CommonUtils.showToastShort(mActivity, "验证码不正确");
                    return;
                }
                regist(mobile, password, mobile);
                break;
            case R.id.tv_regist_sendcode:
                if (StringUtils.isEmpty(etRegistMobile.getText().toString())) {
                    CommonUtils.showToastShort(mActivity, "请输入手机号");
                    return;
                }
                String string = tvRegistSendcode.getText().toString();
                if (string.equals("获取验证码") || string.equals("重新发送")) {
                    getCode();
                }
                break;
            case R.id.tv_regist_agreement:
                Intent intent = new Intent(mActivity, NewsWebActivity.class);
                intent.putExtra(NewsWebActivity.NEWS_TITLE, "注册协议");
                intent.putExtra("url", LocalUserManager.getInstance().getAppconfig().getGvrp());
                intent.putExtra(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_URL);
                startActivity(intent);
                break;
        }
    }


    private void getCode() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, mActivity);
        SendCodeRequest request = new SendCodeRequest(etRegistMobile.getText().toString());
        countDownTimer.start();
        CommonUtils.showDialog(mActivity, "发送中");
        api.getSecurityCode(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataT -> {
                    CommonUtils.cencelDialog();
                    if (dataT.code != 0) {
                        CommonUtils.showToastShort(mActivity, dataT.msg);
                        return;
                    }
                    if (dataT.data instanceof String) {
                        serviceCode = (String) dataT.data;
                    }
                    CommonUtils.showToastShort(mActivity, "发送成功");
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    throwable.printStackTrace();
                    CommonUtils.showToastShort(mActivity, R.string.Server_busy);
                });
    }

    private void regist(String mobile, String password, String nick) {
        CommonUtils.showDialog(mActivity, R.string.registing);
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        api.register(mobile, password, nick)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    Logger.json(jsonObject.toJSONString());
                    int status = jsonObject.getInteger("code");
                    if (status == 1) {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(mActivity, "注册成功");
                        startActivity(new Intent(mActivity, LoginActivity.class).putExtra("mobile", mobile).putExtra("password", password));
                        finish();
                    } else if (status == -1) {
                        CommonUtils.cencelDialog();

                        CommonUtils.showToastShort(mActivity, "该手机号码已被注册");
                    } else if (status == -2) {
                        CommonUtils.cencelDialog();

                        CommonUtils.showToastShort(mActivity, "手机号码格式不正确!");
                    }
                }, throwable -> {
                    CommonUtils.showToastShort(mActivity, "服务器繁忙，请稍后再试！");
                    CommonUtils.cencelDialog();
                });
    }

    class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {

            boolean Sign2 = etRegistMobile.getText().length() > 0;
            boolean Sign3 = etRegistCode.getText().length() > 0;
            boolean Sign4 = etRegistPassword.getText().length() > 0;

            if (Sign2 & Sign3 & Sign4) {
                ivRegistOk.setImageResource(R.drawable.login_lightduihao);
                ivRegistOk.setEnabled(true);
            } else {
                ivRegistOk.setImageResource(R.drawable.regist_duihao);
                ivRegistOk.setEnabled(false);
            }
        }

    }
}
