package com.zhishen.aixuexue.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.media.UMusic;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.runtimepermissions.MPermissionUtils;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.StatusBarHelper;

/**
 * Created by yangfaming on 2018/6/20.
 */

public class BaseActivity extends RxAppCompatActivity {
    public Activity mActivity;
    private Toast mToast = null;
    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE};
    private String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        StatusBarHelper.setStatusBarLightMode(this);
        super.onCreate(savedInstanceState);
        mActivity = this;
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        PushAgent.getInstance(mActivity).onAppStart();//统计每个activity启动数据
    }

    public void toast(String content) {
        mToast.setText(content);
        mToast.show();
    }

    public void back(View view) {
        finish();
    }

    public void setTitle(int title) {
        TextView textView = this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }

    }

    public void setTitleCenter() {
        TextView textView = this.findViewById(R.id.tv_title);
        textView.setGravity(Gravity.CENTER);
    }

    public void setTitleCenter(String title) {
        TextView textView = this.findViewById(R.id.tv_title);
        textView.setGravity(Gravity.CENTER);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void setTitle(String title) {
        TextView textView = this.findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(title);
        }
    }

    public void hideBackView() {
        ImageView iv_back = this.findViewById(R.id.iv_back);
        if (iv_back != null) {
            iv_back.setVisibility(View.GONE);
        }
    }

    public void hintTitleBar() {
        RelativeLayout title = this.findViewById(R.id.title);
        if (title != null) {
            title.setVisibility(View.GONE);
        }
    }

    public void showTitleBar() {
        RelativeLayout title = this.findViewById(R.id.title);
        if (title != null) {
            title.setVisibility(View.VISIBLE);
        }
    }

    public void changeBackView(int icon, View.OnClickListener onClickListener) {
        ImageView iv_back = this.findViewById(R.id.iv_back);
        if (iv_back != null) {
            iv_back.setVisibility(View.VISIBLE);
//            iv_back.setText(icon);
        }
        if (onClickListener != null) {
            iv_back.setOnClickListener(onClickListener);
        }
    }

    public void showRightView(int res, View.OnClickListener onClickListener) {
        ImageView ivRight = this.findViewById(R.id.iv_right);
        if (ivRight != null) {
            ivRight.setImageResource(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightTextView(int res, View.OnClickListener onClickListener) {
        TextView ivRight = this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setText(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    public void showRightTextView(String res, View.OnClickListener onClickListener) {
        TextView ivRight = this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setText(res);
            ivRight.setVisibility(View.VISIBLE);
            if (onClickListener != null) {
                ivRight.setOnClickListener(onClickListener);
            }
        }
    }

    public void setRightTextColor(int res) {
        TextView ivRight = this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            ivRight.setTextColor(res);
            ivRight.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightText(boolean show) {
        TextView ivRight = this.findViewById(R.id.btn_rtc);
        if (ivRight != null) {
            if (show) {
                ivRight.setVisibility(View.GONE);
            } else {
                ivRight.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setTitleBarColor(int res) {
        RelativeLayout relativeLayout = this.findViewById(R.id.title);
        if (relativeLayout != null) {
            relativeLayout.setBackgroundColor(res);
        }
    }

    public void share() {
        if (Build.VERSION.SDK_INT >= 23) {
            MPermissionUtils.requestPermissionsResult(mActivity, 1, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new MPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    initShare();
//                    initTextShare();
//                    initImageShare();
//                    initVideoShare();
//                    initMusicShare();
                }

                @Override
                public void onPermissionDenied() {
                    MPermissionUtils.showTipsDialog(mActivity, "文件存储");
                }
            });
        } else {
            initShare();
        }


    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 分享图文链接
     */
    private void initShare() {
        final UMImage thumb = new UMImage(mActivity, R.drawable.icon_single);
        UMWeb web = new UMWeb("http://www.baidu.com");
        web.setTitle("分享的标题");
        web.setThumb(thumb);
        web.setDescription("分享的内容");
        new ShareAction(mActivity)
                .setDisplayList(displaylist)
                .withMedia(web)
                .setCallback(shareListener).open();
    }

    /**
     * 纯文本分享,暂不支持纯文本分享
     */
    private void initTextShare() {
        new ShareAction(mActivity)
                .withText("纯文本分享")
//                .withMedia(new UMImage(mActivity, R.drawable.umeng_socialize_qq))
                .setDisplayList(displaylist)
                .setCallback(shareListener)
                .open();
    }

    /**
     * 多图片分享，暂时只支持新浪微博和QQ
     */
    private void initImageShare() {
        UMImage image = new UMImage(mActivity, R.drawable.umeng_socialize_qq);
        new ShareAction(mActivity)
                .withMedias(image, image, image)
                .withText("纯文本分享")
                .setDisplayList(displaylist)
                .setCallback(shareListener)
                .open();
    }

    /**
     * 视频分享、、、视频只能使用网络视频
     */
    private void initVideoShare() {
        final UMImage thumb = new UMImage(mActivity, R.drawable.icon_single);
        UMVideo umVideo = new UMVideo("http://p8wuad2l9.bkt.clouddn.com/44c1072d1ed5f2f78ed874b2bf3fc799.mp4");
        umVideo.setTitle("This is music title");//视频的标题
        umVideo.setThumb(thumb);//视频的缩略图
        umVideo.setDescription("my description");//视频的描述
        new ShareAction(mActivity)
                .withMedia(umVideo)
                .setDisplayList(displaylist)
                .setCallback(shareListener)
                .open();
    }

    /**
     * 音乐分享，，音乐只能使用网络音乐
     */
    private void initMusicShare() {
        final UMImage thumb = new UMImage(mActivity, R.drawable.icon_single);
        UMusic music = new UMusic("https://i.y.qq.com/v8/playsong.html?songid=101800569&source=yqq#wechat_redirect");
        music.setTitle("This is music title");//视频的标题
        music.setThumb(thumb);//视频的缩略图
        music.setmTargetUrl("https://i.y.qq.com/v8/playsong.html?songid=101800569&source=yqq#wechat_redirect");//音乐的跳转链接
        new ShareAction(mActivity)
                .withMedia(music)
                .setDisplayList(displaylist)
                .setCallback(shareListener)
                .open();
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {
            if (!UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.WEIXIN)) {
                CommonUtils.showToastShort(mActivity, "没有安装微信应用");
                return;
            }
            if (!UMShareAPI.get(mActivity).isInstall(mActivity, SHARE_MEDIA.QQ)) {
                CommonUtils.showToastShort(mActivity, "没有安装QQ应用");
                return;
            }
        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Log.d(TAG, "------" + throwable.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    protected void readyGo(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    protected boolean isCompatible(int apiLevel) {
        return Build.VERSION.SDK_INT >= apiLevel;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    private void GPSisopen(LocationManager locationManager) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了获取定位服务，请先打开GPS");
            dialog.setPositiveButton("设置", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //界面跳转
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });
            dialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            //调用显示方法！
            dialog.show();
        }
    }

    public void initLocation() {
        Criteria c = new Criteria();
        c.setPowerRequirement(Criteria.POWER_LOW);//设置耗电量为低耗电
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);//设置精度标准为粗糙
        c.setAltitudeRequired(false);//设置海拔不需要
        c.setBearingRequired(false);//设置导向不需要
        c.setAccuracy(Criteria.ACCURACY_LOW);//设置精度为低
        c.setCostAllowed(false);//设置成本为不需要
//... Criteria 还有其他属性
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String bestProvider = manager.getBestProvider(c, true);
//得到定位信息
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = null;
        if (!TextUtils.isEmpty(bestProvider)) {
            location = manager.getLastKnownLocation(bestProvider);
        }
        if (null == location) {
            //如果没有最好的定位方案则手动配置
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else if (manager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }
        if (null == location) {
            Log.i(TAG, "获取定位失败!");
            return;
        }
        LocalUserManager.getInstance().setLatlng(location);
        Log.d("1212", location.getLatitude() + " ======  " + location.getLongitude());

//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER)) {
//            if (ActivityCompat.checkSelfPermission(this,
//                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "权限不够", Toast.LENGTH_LONG).show();
//                return;
//            }
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
//                @Override
//            /*当地理位置发生改变的时候调用*/
//                public void onLocationChanged(Location location) {
//                    LocalUserManager.getInstance().setLatlng(location);
//                    Log.d("1212", location.getLatitude() + " ======  " + location.getLongitude());
//                }
//
//                /* 当状态发生改变的时候调用*/
//                @Override
//                public void onStatusChanged(String s, int i, Bundle bundle) {
//                    Log.d("GPS_SERVICES", "状态信息发生改变");
//
//                }
//
//                /*当定位者启用的时候调用*/
//                @Override
//                public void onProviderEnabled(String s) {
//                    Log.d("TAG", "onProviderEnabled: ");
//
//                }
//
//                @Override
//                public void onProviderDisabled(String s) {
//                    Log.d("TAG", "onProviderDisabled: ");
//                }
//            });
//        }
    }
}
