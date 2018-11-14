package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.htmessage.sdk.client.HTClient;
import com.trello.rxlifecycle2.components.RxActivity;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.MainActivity;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.UpdateLocalLoginTimeUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class SplashActivity extends RxActivity {
    private String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getPageVersion();

    }

    private void enterMain() {
        boolean firstLanuch = LocalUserManager.getInstance().getFirstLanuch();
        if (firstLanuch) {
            startActivity(new Intent(this, GuideActivity.class));
        } else {
            if (HTClient.getInstance().isLogined()) {
                UpdateLocalLoginTimeUtils.sendLocalTimeToService(this);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        finish();
    }

    /**
     * 获取APP版本信息，与本地版本对比，相同则不请求getAppPageData();
     */

    private void getPageVersion() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this);
        api.getAppVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(appConfigDataBaseResponseDataT -> {
                            LocalUserManager.getInstance().setPageDataVersion(appConfigDataBaseResponseDataT.data.getVersion());
                            if (!appConfigDataBaseResponseDataT.data.getVersion().equals(LocalUserManager.getInstance().getPageDataVersion()) || LocalUserManager.getInstance().getAppconfig() == null) {
                                getAppPageData();
                            } else {
                                enterMain();
                            }

                        }, e -> {
                            CommonUtils.showToastShort(this, "网络连接超时，请退出重试！");
                            Log.d(TAG, "========    pageversion    " + e.getMessage());
                        }
                );

    }

    /**
     * 获取APP配置信息数据,该方法只会在APP版本不一致的时候执行。
     */
    private void getAppPageData() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, this);
        api.getPageData().compose(RxSchedulers.ioFlowable())
                .compose(bindToLifecycle())
                .subscribe(homeBeanBaseResponseDataT -> {
                            LocalUserManager.getInstance().setAppconfig(homeBeanBaseResponseDataT.data);
                            enterMain();
                        },
                        throwable -> {
                            CommonUtils.showToastShort(this, "网络连接超时，请退出重试！");
                            Log.d(TAG, "========    pagedata    " + throwable.getMessage());
                        });
    }

}
