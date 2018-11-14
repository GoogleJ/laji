package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.orhanobut.logger.Logger;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.SendCodeRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.main.MainActivity;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.PreferenceManager;
import com.zhishen.aixuexue.runtimepermissions.MPermissionUtils;
import com.zhishen.aixuexue.runtimepermissions.PermissionsManager;
import com.zhishen.aixuexue.runtimepermissions.PermissionsResultAction;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.StringUtils;
import com.zhishen.aixuexue.util.UpdateLocalLoginTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    private final String TAG = LoginActivity.class.getName();

    @BindView(R.id.tv_login_codelogin)
    TextView tvLoginCodelogin;
    @BindView(R.id.tv_login_passwordlogin)
    TextView tvLoginPasswordlogin;
    @BindView(R.id.et_login_mobile)
    EditText etLoginMobile;
    @BindView(R.id.tv_login_code)
    TextView tvLoginCode;
    @BindView(R.id.et_login_code)
    EditText etLoginCode;
    @BindView(R.id.iv_login_duigou)
    ImageView ivLoginDuigou;
    @BindView(R.id.tv_login_forgetpassword)
    TextView tvLoginForgetPassword;
    @BindView(R.id.tv_login_sendcode)
    TextView tvLoginSendcode;
    private boolean isCodeLogin = true;
    private String mobile;

    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setTitle("登录");
        setRightTextColor(getResources().getColor(R.color.font_blue));
        requestPermissions();
        initLocation();
        etLoginCode.addTextChangedListener(new TextChange());
        etLoginMobile.addTextChangedListener(new TextChange());

        switchLogin();

        countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvLoginSendcode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                tvLoginSendcode.setText("重新发送");
            }
        };
    }

    /**
     * 请求清单文件所有权限
     */
    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(mActivity, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

            boolean Sign2 = etLoginMobile.getText().length() > 0;
            boolean Sign3 = etLoginCode.getText().length() > 0;

            if (Sign2 & Sign3) {
                ivLoginDuigou.setImageResource(R.drawable.login_lightduihao);
                ivLoginDuigou.setEnabled(true);
            } else {
                if (isCodeLogin) {
                    ivLoginDuigou.setImageResource(R.drawable.regist_duihao);
                } else {
                    ivLoginDuigou.setImageResource(R.drawable.regist_duihao);
                }
//                ivLoginDuigou.setEnabled(false);
            }
        }

    }

    @OnClick({R.id.tv_login_codelogin, R.id.tv_login_passwordlogin, R.id.tv_login_sendcode, R.id.iv_login_duigou, R.id.tv_login_forgetpassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_passwordlogin:
                switchLogin();
                break;
            case R.id.tv_login_sendcode:
                String string = tvLoginSendcode.getText().toString();
                if (string.equals("获取验证码") || string.equals("重新发送")) {
                    getCode();
                }
                break;
            case R.id.iv_login_duigou:
                String mobile = etLoginMobile.getText().toString();
                String password = etLoginCode.getText().toString();
                if (StringUtils.isEmpty(mobile)) {
                    CommonUtils.showToastShort(mActivity, "请输入手机号");
                    return;
                }
                if (StringUtils.isEmpty(password)) {
                    if (isCodeLogin) {
                        CommonUtils.showToastShort(mActivity, "请输入验证码");
                        return;
                    } else {
                        CommonUtils.showToastShort(mActivity, "请输入密码");
                        return;
                    }
                }
                loginService(mobile, password);
                break;
            case R.id.tv_login_forgetpassword:
                this.mobile = etLoginMobile.getText().toString();
                if (StringUtils.isEmpty(this.mobile)) {
                    CommonUtils.showToastShort(mActivity, "请输入手机号");
                    return;
                }
                startActivity(new Intent(mActivity, ForgetPasswordActivity.class).putExtra("mobile", this.mobile));
                break;
        }
    }

    private void switchLogin() {
        if (isCodeLogin) {
            tvLoginCodelogin.setText("密码登录");
            tvLoginPasswordlogin.setText("验证码登录");
            tvLoginCode.setText("密码");
            etLoginCode.setHint("请输入密码");
            etLoginCode.setText("");
            etLoginCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            etLoginCode.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            ivLoginDuigou.setImageResource(R.drawable.regist_duihao);
            tvLoginForgetPassword.setVisibility(View.VISIBLE);
            tvLoginSendcode.setVisibility(View.GONE);
            showRightTextView("注册", view1 -> startActivity(new Intent(mActivity, RegistActivity.class)));
            hideRightText(false);
            isCodeLogin = false;
        } else {
            CommonUtils.showToastShort(mActivity, "暂不支持验证码登录");
//            tvLoginCodelogin.setText("验证码登录");
//            tvLoginPasswordlogin.setText("密码登录");
//            tvLoginPasswordlogin.setTextColor(getResources().getColor(R.color.font_gray_white));
//            tvLoginCode.setText("验证码");
//            etLoginCode.setHint("请输入验证码");
//            etLoginCode.setText("");
//            etLoginCode.setInputType(InputType.TYPE_CLASS_NUMBER);
//            ivLoginDuigou.setImageResource(R.drawable.regist_duihao);
//            tvLoginForgetPassword.setVisibility(View.GONE);
//            tvLoginSendcode.setVisibility(View.VISIBLE);
//            hideRightText(true);
//            isCodeLogin = true;
        }
    }

    /**
     * 连接消息服务器
     */
    private void loginService(String mobile, String password) {
        CommonUtils.showDialog(mActivity, "正在登录...");
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, this);
        api.login(mobile, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Logger.json(jsonObject.toJSONString());
                        int code = jsonObject.getInteger("code");
                        switch (code) {
                            case 1:
                                JSONObject userJson = jsonObject.getJSONObject("user");
                                loginIm(userJson);
                                break;
                            case -1:
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(mActivity, R.string.Account_does_not_exist);
                                break;
                            case -2:
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(mActivity, R.string.Incorrect_password);
                                break;
                            case -3:
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(mActivity, R.string.Account_has_been_disabled);
                                break;
                            default:
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(mActivity, R.string.Server_busy);
                                break;
                        }
                    }
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    CommonUtils.showToastShort(mActivity, R.string.Server_busy);
                });
    }

    private void loginIm(final JSONObject userJson) {
        String userId = userJson.getString(Constant.JSON_KEY_HXID);
        String password = userJson.getString(Constant.JSON_KEY_PASSWORD);
        if (TextUtils.isEmpty(password)) {
            password = userJson.getString("password");
        }
        HTClient.getInstance().login(userId, password, new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.showToastShort(mActivity, "登录成功");
                        CommonUtils.cencelDialog();
                    }
                });
                if (userJson == null) {
                    return;
                }
                //保存好友列表到本地
                saveFriends(userJson);
                //上传最近登录时间
                UpdateLocalLoginTimeUtils.sendLocalTimeToService(mActivity);
                //登录成功跳转主页面
                startActivity(new Intent(mActivity, MainActivity.class));
                finish();
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(mActivity, "登录失败");
                    }
                });

            }
        });
    }

    private void saveFriends(JSONObject object) {
        JSONArray friends = object.getJSONArray("friend");
        if (object.containsKey("friend")) {
            object.remove("friend");
        }
        AiApp.getInstance().setUserJson(object);
        Map<String, User> userlist = new HashMap<String, User>();
        if (friends != null) {
            for (int i = 0; i < friends.size(); i++) {
                JSONObject friend = friends.getJSONObject(i);
                User user = CommonUtils.Json2User(friend);
                userlist.put(user.getUsername(), user);
            }
            List<User> users = new ArrayList<User>(userlist.values());
            ContactsManager.getInstance().saveContactList(users);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CommonUtils.observeSoftKeyboard(mActivity, new CommonUtils.OnSoftKeyboardChangeListener() {
            @Override
            public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
                if (visible && softKeybardHeight > 0) {
                    PreferenceManager.getInstance().setSoftKeybardHeight(softKeybardHeight);
                    getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
                }
            }


        });
    }


    /**
     * 发送验证码
     */
    @SuppressLint("CheckResult")
    private void getCode() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, mActivity);
        SendCodeRequest request = new SendCodeRequest(etLoginMobile.getText().toString());
        countDownTimer.start();
        CommonUtils.showDialog(LoginActivity.this, "发送中");
        api.getSecurityCode(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(dataT -> {
                    CommonUtils.cencelDialog();

                    if (dataT.code != 0) {
                        CommonUtils.showToastShort(LoginActivity.this, dataT.msg);
                        return;
                    }

                    CommonUtils.showToastShort(LoginActivity.this, "发送成功");
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    throwable.printStackTrace();
                    CommonUtils.showToastShort(LoginActivity.this, "未知错误");
                });
    }

    @Override
    protected void onDestroy() {
        countDownTimer.cancel();
        super.onDestroy();
    }
}

