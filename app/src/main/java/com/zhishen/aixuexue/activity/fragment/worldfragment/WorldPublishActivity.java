package com.zhishen.aixuexue.activity.fragment.worldfragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.ParamsMap;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxException;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.RxSchedulers;
import com.zhishen.aixuexue.activity.fragment.worldfragment.adapter.PreviewSelectAdapter;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.MultiImageSelectorActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;
import com.zhishen.aixuexue.bean.LocalBean;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.util.AppUtils;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.PhotoUtil;
import com.zhishen.aixuexue.weight.TakePopWindow;
import com.zhishen.aixuexue.weight.manager.NoScrollGridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by Jerome on 2018/7/13
 */
@SuppressLint("CheckResult")
public class WorldPublishActivity extends BaseActivity implements TakePopWindow.OnItemClickListener {

    private Unbinder unbinder = null;
    private PreviewSelectAdapter mAdapter;
    private List<String> selectImage = new ArrayList<>(); //上传集合
    private static final int CODE_GALLERY_REQUEST = 160;
    private static final int CODE_CAMERA_REQUEST = 161;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 171;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 172;
    private ArrayList<LocalMedia> defaultDataArray = new ArrayList<>();
    public static final String LOCAL_INFO = "local_info";
    private LocalBean localBean;
    private String category = "s0";
    private TakePopWindow selectPopWindow;
    @BindView(R.id.tvLocal)
    TextView tvLocal;
    @BindView(R.id.btn_rtc)
    TextView btn_rtc;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_publish);
        unbinder = ButterKnife.bind(this);
        setTitleCenter(AppUtils.getActivityLabel(this));

        btn_rtc.setText("发布");
        btn_rtc.setVisibility(View.VISIBLE);
        selectPopWindow = new TakePopWindow(this);
        selectPopWindow.setOnItemClickListener(this);
        btn_rtc.setTextColor(ContextCompat.getColor(this, R.color.blue));
        mRecyclerView.setLayoutManager(new NoScrollGridLayoutManager(this, 4));
        mRecyclerView.setAdapter(mAdapter = new PreviewSelectAdapter(this));
        mAdapter.setSelectItemClickListener(new PreviewSelectAdapter.MultiSelectItemClickListener() {
            @Override
            public void addImageClick() {
                hideKeyboard();
                showPopWindow();
            }

            @Override
            public void selectImageClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePreviewActivity.PREVIEW_LIST, defaultDataArray);
                bundle.putInt(ImagePreviewActivity.START_ITEM_POSITION, 0);
                bundle.putInt(ImagePreviewActivity.START_IAMGE_POSITION, position);
                readyGo(ImagePreviewActivity.class, bundle);
            }

            @Override
            public void playVideo(String videoPath) {
                Bundle bundle = new Bundle();
                bundle.putString(VideoViewActivity.VIDEO_PATH, videoPath);
                readyGo(VideoViewActivity.class, bundle);
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.btn_rtc, R.id.llContent})
    void onMethodClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                closeEditPublish();
                break;
            case R.id.btn_rtc:
                posterCircleFriends();
                break;
            case R.id.llContent:
                readyGoForResult(WorldLocalActivity.class, 11, null);
                break;
        }
    }

    private void closeEditPublish() {
        String content = etContent.getText().toString().trim();
        if (!TextUtils.isEmpty(content) || null != selectImage && !selectImage.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("是否将此次内容保存");
            builder.setPositiveButton("保存", (dialogInterface, i) -> {
                onBackPressed();
            });
            builder.setNegativeButton("不保存", (dialogInterface, i) -> {
                onBackPressed();
                etContent.getText().clear();
                selectImage.clear();
            });
            builder.show();
        } else {
            onBackPressed();
        }
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSIONS_REQUEST_CODE);
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
            multiImages();
        }
    }

    private void multiImages() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        bundle.putInt(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        bundle.putInt(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
        bundle.putSerializable(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
        readyGoForResult(MultiImageSelectorActivity.class, CODE_GALLERY_REQUEST, bundle);
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
                multiImages();
            } else {
                toast("请允许获取读写权限");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST:
                    if (!TextUtils.isEmpty(PhotoUtil.file.getAbsolutePath())) {
                        File imageFile = new File(PhotoUtil.file.getAbsolutePath());
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
                        selectImage.add(PhotoUtil.file.getAbsolutePath());
                        if (selectImage.size() < 9) {
                            LocalMedia localMedia = new LocalMedia();
                            localMedia.path = PhotoUtil.file.getAbsolutePath();
                            localMedia.pictureType = "image/png";
                            localMedia.width = 0;
                            localMedia.height = 0;
                            localMedia.duration = 0;
                            localMedia.index = defaultDataArray.size() + 1;
                            defaultDataArray.add(localMedia);
                        } else {
                            toast("最多选9张图片");
                        }
                    }
                    break;
                case CODE_GALLERY_REQUEST:
                    if (null != data) {
                        defaultDataArray = (ArrayList<LocalMedia>) data.getSerializableExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    }
                    break;
                case 11:
                    if (null != data) {
                        localBean = data.getParcelableExtra(WorldPublishActivity.LOCAL_INFO);
                        if (null != localBean) {
                            tvLocal.setText(localBean.getPoiItem().getTitle());
                        }
                    }
                    return;
            }
            compress(defaultDataArray);
        }
    }

    private String imageUrl = "";

    private void compress(ArrayList<LocalMedia> photos) {
        if (photos == null || photos.isEmpty()) {
            return;
        }
        if (photos.size() == 0) {
            category = "s0";
        }
        if (isVideo(photos)) {
            LocalMedia localMedia = photos.get(0);
            if (localMedia.width > localMedia.height) {
                category = "s3";
            } else {
                category = "s4";
            }
            mAdapter.updateItem(photos);
            //视频地址,并将其产生缩略图片保存使用
            selectImage.add(localMedia.path);
            selectImage.add(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Axx/Video/video_thumb.png");
            upLoadFiles();
        } else {
            if (photos.size() == 1) {
                category = "s1";
            } else {
                category = "s2";
            }
            Flowable.just(photos)
                    .map(localMedias -> getImageData(localMedias))
                    .map(list -> Luban.with(this).load(list).get())
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(files -> {
                        selectImage.clear();
                        for (File file : files) {
                            selectImage.add(file.getAbsolutePath());
                        }
                        upLoadFiles();
                    });

        }
        mAdapter.updateItem(photos);
    }

    private List<String> getImageData(List<LocalMedia> data) {
        List<String> list = new ArrayList<>();
        for (LocalMedia media : data) {
            list.add(media.path);
        }
        return list;
    }

    private void upLoadFiles() {
        Log.d("upLoadFiles", "------>"+selectImage.size());
        CommonUtils.showDialog(this, "上传中...");
        List<File> files = new ArrayList<>();
        for (String videoPath : selectImage) {
            files.add(new File(videoPath));
        }
        doAction(files);
    }

    int num = 0;
    StringBuffer sb = new StringBuffer();
    private void doAction(List<File> files) {
        File file = files.get(num);
        new UploadFileUtils(this, file.getName(), file.getAbsolutePath()).asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
            }

            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                runOnUiThread(() -> {
                    ++num;
                    sb.setLength(0);
                    sb.append(file.getName()).append(",");
                    if (num == files.size()) {
                        autoGenerateAddress();
                        num = 0;
                    } else {
                        Log.d("onSuccessfiles", "---->"+files.size());
                        doAction(files);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                CommonUtils.cencelDialog();
                toast("上传失败");
                e.printStackTrace();
            }
        });
    }

    private void autoGenerateAddress() {
        selectImage.clear();
        CommonUtils.cencelDialog();
        toast("上传完成");
        String url = sb.substring(0, sb.lastIndexOf(","));
        Log.d("autoGenerateAddress-->", url);
        imageUrl = url;  //视频和图片地址
    }

    double latitude, longtude;
    String location;

    private void posterCircleFriends() {

        String content = etContent.getText().toString().trim();

        if (imageUrl.length() == 0 && TextUtils.isEmpty(content)) {
            toast("说点什么吧");
            return;
        }

        if (localBean != null) {
            location = localBean.getPoiItem().getTitle();
            latitude = localBean.getPoiItem().getLatLonPoint().getLatitude();
            longtude = localBean.getPoiItem().getLatLonPoint().getLongitude();
        }

        CommonUtils.showDialog(this, "正在发布..");

        Map<String, Object> params = ParamsMap.publishCircle(category, content, imageUrl, location, new LatLng(latitude, longtude));
        Log.d("params", new Gson().toJson(params));
        ServiceFactory.createRetrofitServiceNoAes(ResApi.class, Constant.NEW_API_HOST, this)
                .posterFriends(params)
                .compose(RxSchedulers.ioFlowable())
                .compose(RxSchedulers.flowOtherTransformer())
                .compose(bindToLifecycle())
                .subscribe(bean -> {
                    CommonUtils.cencelDialog();
                    toast("发布成功");
                    onBackPressed();
                }, t -> {
                    CommonUtils.cencelDialog();
                    toast(RxException.getMessage(t));
                    t.printStackTrace();
                });
    }

    private boolean isVideo(List<LocalMedia> data) {
        if (null != data && !data.isEmpty()) {
            return data.get(0).isVideo;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != unbinder) {
            unbinder.unbind();
        }
    }
}
