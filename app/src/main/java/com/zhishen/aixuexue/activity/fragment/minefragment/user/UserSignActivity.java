package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jerome on 2018/7/7
 */
public class UserSignActivity extends BaseActivity {

    private Unbinder unbinder = null;
    private CharSequence temp;
    private int selectionStart;
    private int selectionEnd;
    private int MAX_COUNT = 30;
    @BindView(R.id.tvNum) TextView tvNum;
    @BindView(R.id.etContent) EditText etContent;
    @BindView(R.id.btn_rtc) TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        tvNum.setText(String.valueOf(MAX_COUNT));
        textView.setText("提交");
        textView.setVisibility(View.VISIBLE);

        textView.setOnClickListener(view -> {
            String content = etContent.getText().toString().trim();
            if (TextUtils.isEmpty(content)) {
                CommonUtils.showToastShort(this, "请输入签名内容");
                return;
            }
            UpdateCustomerByIdRequest request = new UpdateCustomerByIdRequest();
            request.setUserId(Long.parseLong(AiApp.getInstance().getUsername()));
            request.setSign(content);

            ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this)
                    .updateCustomerById(request.decode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindToLifecycle())
                    .subscribe(responseDataT -> {
                        if (responseDataT.code != 0) {
                            CommonUtils.showToastShort(UserSignActivity.this, responseDataT.msg);
                            return;
                        }
                        CommonUtils.showToastShort(UserSignActivity.this, "修改成功");
                        Intent intent = new Intent(UserSignActivity.this, UserMoreActivity.class);
                        intent.putExtra("sign", content);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }, throwable -> {
                        throwable.printStackTrace();
                        CommonUtils.showToastShort(UserSignActivity.this, "未知错误");
                    });
        });

    }

    @OnTextChanged(R.id.etContent) void onTextChanged(CharSequence s, int start, int before, int count) {
        temp = s;
    }

    @OnTextChanged(R.id.etContent) void afterTextChanged(Editable s) {
        int number = MAX_COUNT - s.length();
        tvNum.setText("" + number);
        selectionStart = etContent.getSelectionStart();
        selectionEnd = etContent.getSelectionEnd();
        if (temp.length() > MAX_COUNT) {
            s.delete(selectionStart - 1, selectionEnd);
            int tempSelection = selectionEnd;
            etContent.setText(s);
            etContent.setSelection(tempSelection);//设置光标在最后
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
