package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.bean.UserBean;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.UpdateCustomerByIdRequest;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.PhotoUtil;
import com.zhishen.aixuexue.weight.TakePopWindow;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by Jerome on 2018/7/7
 */
@SuppressLint("CheckResult")
public class UserInfoActivity extends BaseActivity implements TakePopWindow.OnItemClickListener {

    private Unbinder unbinder = null;
    private TakePopWindow selectPopWindow;
    @BindView(R.id.tvWX) TextView tvWX;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvPhone) TextView tvPhone;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    private static final int CODE_GALLERY_REQUEST = 160;
    private static final int CODE_CAMERA_REQUEST = 161;
    private static final int CODE_CHANGENICK_REQUEST = 162;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 171;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 172;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));
        selectPopWindow = new TakePopWindow(this);
        selectPopWindow.setOnItemClickListener(this);

        getDataFromServer();
    }

    @OnClick({R.id.llContainer, R.id.llNickname, R.id.llCode, R.id.llMore})
    void onMethodClick(View v) {
        switch (v.getId()) {
            case R.id.llContainer:
                showPopWindow();
                break;
            case R.id.llNickname:
                readyGoForResult(UserNickNameActivity.class, CODE_CHANGENICK_REQUEST, null);
                break;
            case R.id.llCode:
                readyGo(UserImgCodeActivity.class);
                break;
            case R.id.llMore:
                if (bean != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("location", bean.getCity());
                    bundle.putString("sex", bean.getSex());
                    bundle.putString("sign", bean.getSign());
                    readyGo(UserMoreActivity.class,bundle);
                }
                break;
        }
    }

    private void getDataFromServer() {
        ServiceFactory.createRetrofitService(ResApi.class, Constant.BASE_TEMP, this)
                .getUserInfo(ParamsMap.getUserInfo(AiApp.getInstance().getUsername()))
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowTransformer())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(userBean -> showUserInfo(userBean), t -> toast(RxException.getMessage(t)));
    }

    UserBean bean;

    private void showUserInfo(UserBean userBean) {
        if (userBean == null) return;
        bean = userBean;
        tvName.setText(userBean.getUsernick());
        tvPhone.setText(userBean.getTel());
        tvWX.setText(userBean.getUserId());
        BitmapUtil.loadCircleImg(ivAvatar, userBean.getAvatar(), R.drawable.default_avatar);
    }

    private void showPopWindow() {
        selectPopWindow.showAtLocation(findViewById(android.R.id.content),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void setOnItemClick(View v) {
        selectPopWindow.dismiss();
        switch (v.getId()) {
            case R.id.tvCamera:
                autoObtainCameraPermission();
                break;
            case R.id.tvPhoto:
                autoObtainStoragePermission();
                break;
        }
    }

    private void autoObtainCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                toast("您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (PhotoUtil.hasSdcard()) {
                // 自定义拍照
                PhotoUtil.takePicture(this, CODE_CAMERA_REQUEST);
            } else {
                toast("设备没有SD卡");
            }
        }
    }

    private void autoObtainStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                toast("您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            PhotoUtil.albumPhoto(this, CODE_GALLERY_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (PhotoUtil.hasSdcard()) {
                    PhotoUtil.takePicture(this, CODE_CAMERA_REQUEST);
                } else {
                    toast("设备没有SD卡");
                }
            } else {
                toast("请允许打开相机");
            }
        } else if (requestCode == STORAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoUtil.albumPhoto(this, CODE_GALLERY_REQUEST);
            } else {
                toast("请允许获取读写权限");
            }
        }
    }

    String picPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST:
                    picPath = PhotoUtil.file.getAbsolutePath();
                    break;
                case CODE_GALLERY_REQUEST:
                    if (PhotoUtil.hasSdcard()) {
                        picPath = PhotoUtil.getPath(this, data.getData());
                    } else {
                        toast("设备没有SD卡");
                    }
                    break;
                case CODE_CHANGENICK_REQUEST:
                    tvName.setText(data.getStringExtra("nick"));
                    return;
            }
            Log.d("onActivityResult", "picPath->" + picPath);
            if (picPath != null) {
                CommonUtils.showDialog(UserInfoActivity.this, "上传中...");
                Flowable.just(Arrays.asList(new String[]{picPath}))
                        .observeOn(Schedulers.io())
                        .map(list -> Luban.with(this).load(list).get())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(files -> getLoadUrl(files));

            }
            //uploadAvatar();
        }
    }

    private void getLoadUrl(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        File file = files.get(0);
        UploadFileUtils uploadFileUtils = new UploadFileUtils(UserInfoActivity.this, file.getName(), file.getAbsolutePath());
        uploadFileUtils.asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {

            }

            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                uploadAvatar(Constant.baseImgUrl + file.getName());
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                toast("修改失败");
                CommonUtils.cencelDialog();
            }
        });
    }

    private void uploadAvatar(String url) {
        UpdateCustomerByIdRequest request = new UpdateCustomerByIdRequest();
        request.setUserId(Long.parseLong(AiApp.getInstance().getUsername()));
        request.setAvatar(url);
        ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, UserInfoActivity.this)
                .updateCustomerById(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(responseDataT -> {
                    CommonUtils.cencelDialog();
                    if (responseDataT.code != 0) {
                        CommonUtils.showToastShort(UserInfoActivity.this, responseDataT.msg);
                        return;
                    }
                    BitmapUtil.loadCircleImg(ivAvatar, url, R.drawable.default_avatar);
                    AiApp.getInstance().setUserAvatar(url);
                    toast("修改成功");
                }, throwable -> {
                    CommonUtils.cencelDialog();
                    throwable.printStackTrace();
                    toast("未知错误");
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
