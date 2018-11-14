package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.UpdateCustomerByIdRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jerome on 2018/7/7
 */
public class UserNickNameActivity extends BaseActivity {

    private Unbinder unbinder = null;
    @BindView(R.id.etContent)
    EditText etContent;
    private int type;
    private HTGroup htGroup;
    private String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_nickname);
        unbinder = ButterKnife.bind(this);
        if (getIntent().hasExtra("groupId")) {
            type = getIntent().getIntExtra("type", 1);
            groupId = getIntent().getStringExtra("groupId");
            htGroup = HTClient.getInstance().groupManager().getGroup(groupId);
            if (htGroup == null) {
                finish();
                return;
            }
        }
        setTitleCenter(AppUtils.getActivityLabel(this));
        if (type == 1) {
            etContent.setText(htGroup.getGroupName());
        } else {
            etContent.setText(AiApp.getInstance().getUserNick());
        }

        TextView textView = findViewById(R.id.btn_rtc);
        textView.setVisibility(View.VISIBLE);
        textView.setText("提交");
    }

    @SuppressLint("CheckResult")
    @OnClick({R.id.ivClear, R.id.btn_rtc})
    void onMethodClick(View view) {
        if (view.getId() == R.id.ivClear) {
            etContent.getText().clear();
        } else if (view.getId() == R.id.btn_rtc) {
            String value = etContent.getText().toString();
            if (type == 1) {
                if (!value.equals(htGroup.getGroupName())) {
                    if (value.length() > 18) {
                        CommonUtils.showToastShort(mActivity, R.string.group_groupname_dot_18);
                        return;
                    }
                    CommonUtils.showDialog(this, getString(R.string.are_uploading));
                    HTClient.getInstance().groupManager().updateGroupName(htGroup.getGroupId(), value, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_NICK), new GroupManager.CallBack() {
                        @Override
                        public void onSuccess(String s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.cencelDialog();
                                    CommonUtils.showToastShort(getApplicationContext(), R.string.Modify_the_group_name_successful);
                                    setResult(RESULT_OK, new Intent().putExtra("value", value));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onFailure() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CommonUtils.cencelDialog();
                                    CommonUtils.showToastShort(getApplicationContext(), R.string.change_groupName_failed);
                                }
                            });
                        }
                    });
                }

                return;
            } else {
                String trim = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    CommonUtils.showToastShort(this, "请输入用户昵称");
                    return;
                }
                UpdateCustomerByIdRequest request = new UpdateCustomerByIdRequest();
                request.setUserId(Long.parseLong(AiApp.getInstance().getUsername()));
                request.setUsernick(trim);
                ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this)
                        .updateCustomerById(request.decode())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindToLifecycle())
                        .subscribe(responseDataT -> {
                            if (responseDataT.code != 0) {
                                CommonUtils.showToastShort(UserNickNameActivity.this, responseDataT.msg);
                                return;
                            }

                            CommonUtils.showToastShort(UserNickNameActivity.this, "修改成功");
                            Intent intent = new Intent();
                            intent.putExtra("nick", trim);
                            AiApp.getInstance().setUserNick(trim);
                            this.setResult(Activity.RESULT_OK, intent);
                            finish();
                        }, throwable -> {
                            throwable.printStackTrace();
                            CommonUtils.showToastShort(UserNickNameActivity.this, "未知错误");
                        });
            }


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
