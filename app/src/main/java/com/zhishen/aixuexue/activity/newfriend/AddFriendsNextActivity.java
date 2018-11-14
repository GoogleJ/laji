package com.zhishen.aixuexue.activity.newfriend;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AddFriendsNextActivity extends BaseActivity {

    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.tv_search)
    TextView tv_search;
    @BindView(R.id.re_search)
    RelativeLayout re_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_next);
        ButterKnife.bind(this);
        setTitle("添加好友");
        setListener();
    }

    private void setListener() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    re_search.setVisibility(View.VISIBLE);
                    tv_search.setText(et_search.getText().toString().trim());
                } else {
                    re_search.setVisibility(View.GONE);
                    tv_search.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public String getInputString() {
        return et_search.getText().toString().trim();
    }

    public void back(View view) {
        finish();
    }

    @OnClick({R.id.re_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_search:
                searchUser();
                break;
        }
    }

    private void searchUser() {
        if (TextUtils.isEmpty(getInputString())) {
            return;
        }
        CommonUtils.showDialog(mActivity, getString(R.string.are_finding_contact));
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        api.searchUser(getInputString(), AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Log.d("AddFriendsNextActivity", "---------" + jsonObject.toJSONString());
                        CommonUtils.cencelDialog();
                        int code = jsonObject.getInteger("code");
                        if (code == 1) {
                            JSONObject json = jsonObject.getJSONObject("user");
                            startActivity(new Intent(mActivity, UserDetailNewActivity.class).putExtra(Constant.KEY_USER_INFO, json.toJSONString()));
                        } else if (code == -1) {
                            CommonUtils.showToastShort(getBaseContext(), R.string.User_does_not_exis);
                        } else if (code == 0) {
                            CommonUtils.showToastShort(getBaseContext(), R.string.server_is_busy_try_again);
                        } else {
                            CommonUtils.showToastShort(getBaseContext(), R.string.server_is_busy_try_again);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getBaseContext(), R.string.server_is_busy_try_again);
                    }
                });
    }
}
