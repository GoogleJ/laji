package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.util.AppUtils;

/**
 * Created by Jerome on 2018/7/3
 */
public class UserPrivacyActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_privacy);

        setTitleCenter(AppUtils.getActivityLabel(this));
    }
}
