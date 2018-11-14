package com.zhishen.aixuexue.activity;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.StatusBarHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.zhishen.aixuexue.activity.BaseActivity.hasKitKat;
import static com.zhishen.aixuexue.activity.BaseActivity.hasLollipop;

public class AboutUsActivity extends AppCompatActivity {

    @BindView(R.id.ll_aboutus_gzh)
    LinearLayout llAboutusGzh;
    private TextView tv_aboutus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        tv_aboutus = findViewById(R.id.tv_aboutus);
        tv_aboutus.setText(LocalUserManager.getInstance().getAppconfig().getVersion());


        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        StatusBarHelper.setStatusBarLightMode(this);
    }


    public void back(View view) {
        finish();
    }

    @OnClick(R.id.ll_aboutus_gzh)
    public void onClick() {
        HomeBean appconfig = LocalUserManager.getInstance().getAppconfig();
        if (appconfig == null) {
            return;
        }
        CommonUtils.showAlertDialogNoCancle(AboutUsActivity.this, "关注公众号", "微信号已成功复制,\n请前往微信搜索并关注\n" + "\"" + appconfig.getWeixinName() + "\"", "稍后再去", "去关注", false, view -> {
            CommonUtils.canAlertDialog();
            ClipboardManager systemService = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(appconfig.getWeixinName(), appconfig.getWeixinNumber());
            systemService.setPrimaryClip(data);
            getWechatApi();
        });
    }

    /**
     * 跳转到微信
     */
    private void getWechatApi() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(cmp);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            CommonUtils.showToastShort(AboutUsActivity.this, "检查到您手机没有安装微信，请安装后使用该功能");
        }
    }
}
