package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import com.htmessage.sdk.client.HTClient;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.AboutUsActivity;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.ChangePassActivity;
import com.zhishen.aixuexue.activity.LoginActivity;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CacheHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Jerome on 2018/7/3
 */
public class UserSettingActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.tv_setting_cachesize)
    TextView tv_setting_cachesize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        try {
            long folderSize = CacheHelper.getFolderSize(new File(getCacheDir().getAbsolutePath() + "/GlideDisk"));
            String formatSize = CacheHelper.getFormatSize(folderSize);
            tv_setting_cachesize.setText(formatSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.tvPrivacy, R.id.btnLogout, R.id.tv_setting_changepass, R.id.tv_setting_about, R.id.tv_setting_clearcache})
    void onSettingClick(View v) {
        switch (v.getId()) {
            case R.id.tvPrivacy:
                readyGo(UserPrivacyActivity.class);
                break;
            case R.id.btnLogout:
                showLogout();
                break;
            case R.id.tv_setting_changepass:
                readyGo(ChangePassActivity.class);
                break;
            case R.id.tv_setting_about:
                readyGo(AboutUsActivity.class);
                break;
            case R.id.tv_setting_clearcache:
                new AlertDialog.Builder(this)
                        .setTitle("清除缓存")
                        .setPositiveButton("清除", (dialog, which) -> {
                            CacheHelper.clearAllCache(UserSettingActivity.this);
                            tv_setting_cachesize.setText("0M");
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }

    private void showLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("确定退出爱学学?");
        builder.setPositiveButton("确定", (dialog, which) -> logout());
        builder.setNegativeButton("放弃", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }

    private void logout() {
        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                AiApp.getInstance().setUserJson(null);
                finish();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                LocalUserManager.getInstance().setHasUnReadInviateNubmer(false);
                startActivity(intent);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
