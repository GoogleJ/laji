package com.zhishen.aixuexue.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.AddReportRequest;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.ScreenUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ReportActivity extends BaseActivity {

    private String id;

    private List<String> imgs = new ArrayList<>();

    private TextView tv_reason;
    private TextView tv_reason1;
    private TextView tv_report_contentcount;
    private EditText et_report;

    private ImageView iv_report_receivenotice;
    private ImageView iv_report_img1;
    private ImageView iv_report_img2;
    private ImageView iv_report_img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        tv_reason = findViewById(R.id.tv_reason);
        tv_reason1 = findViewById(R.id.tv_reason1);
        tv_report_contentcount = findViewById(R.id.tv_report_contentcount);
        et_report = findViewById(R.id.et_report);
        iv_report_receivenotice = findViewById(R.id.iv_report_receivenotice);
        iv_report_img1 = findViewById(R.id.iv_report_img1);
        iv_report_img2 = findViewById(R.id.iv_report_img2);
        iv_report_img3 = findViewById(R.id.iv_report_img3);

        id = getIntent().getStringExtra("id");

        imgs.add("");
        imgs.add("");
        imgs.add("");

        et_report.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                tv_report_contentcount.setText("" + length);
            }
        });
    }

    @SuppressLint("CheckResult")
    public void report(View view) {
        //举报
        if (tv_reason.getText().equals("请选择")) {
            CommonUtils.showToastShort(this, "请选择举报理由");
            return;
        }

        String content = et_report.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            CommonUtils.showToastShort(this, "请输入举报内容");
            return;
        }

        AddReportRequest request = new AddReportRequest();
        request.setFormUserid(AiApp.getInstance().getUsername());
        request.setInformId(id);
        request.setOperateType("1");
        request.setInformContentOne(tv_reason.getText().toString());
        request.setInformContentTwo(content);
        request.setInformType("3");

        if (!TextUtils.isEmpty(imgs.get(0))) {
            request.setInformImgOne(imgs.get(0));

        }

        if (!TextUtils.isEmpty(imgs.get(1))) {
            request.setInformImgTwo(imgs.get(1));

        }

        if (!TextUtils.isEmpty(imgs.get(2))) {
            request.setInformImgThree(imgs.get(2));
        }

        ServiceFactory.createRetrofitService(Api.class, Constant.BASE_TEMP, this)
                .addReport(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(responseDataT -> {
                    if (responseDataT.code != 0) {
                        CommonUtils.showToastShort(this, responseDataT.msg);
                        return;
                    }

                    CommonUtils.showToastShort(this, "举报成功！");
                    Intent intent = new Intent();
                    intent.putExtra("result", "success");
                    this.setResult(1, intent);
                    finish();
                }, throwable -> {
                    CommonUtils.showToastShort(this, "未知错误");
                });
    }

    public void selectReason(View view) {
        //选择理由
        Intent intent = new Intent(this, ReportReasonActivity.class);
        startActivityForResult(intent, 3);
    }

    private boolean receiveNotice = false;

    public void receiveNotice(View view) {
        //是否接收举报的通知
        if (receiveNotice) {
            iv_report_receivenotice.setImageResource(R.drawable.ic_addcomment_invisiable);
        } else {
            iv_report_receivenotice.setImageResource(R.drawable.ic_addcomment_visiable);
        }

        receiveNotice = !receiveNotice;
    }

    public void setImg1(View view) {
        currentIndex = 1;
        showPopUp();
    }

    public void setImg2(View view) {
        currentIndex = 2;
        showPopUp();
    }

    public void setImg3(View view) {
        currentIndex = 3;
        showPopUp();
    }

    private int currentIndex = -1;

    private void setImg(Bitmap img) {

        CommonUtils.showDialog(ReportActivity.this, "上传中...");
        FileOutputStream fileOutputStream = null;
        File f = new File(getCacheDir().getAbsolutePath() + "/reportcache");
        if (!f.exists()) {
            f.mkdirs();
        }

        File file = new File(f, System.currentTimeMillis() + AiApp.getInstance().getUsername() + ".jpg");

        try {
            fileOutputStream = new FileOutputStream(file);

            img.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            new UploadFileUtils(ReportActivity.this, file.getName(), file.getAbsolutePath())
                    .asyncUploadFile(new UploadFileUtils.a() {
                        @Override
                        public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {

                        }

                        @Override
                        public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(ReportActivity.this, "上传成功");

                            String url = Constant.baseImgUrl + file.getName();

                            if (currentIndex == 1) {
                                imgs.remove(0);
                                imgs.add(0, url);
                                iv_report_img1.setImageBitmap(img);
                            } else if (currentIndex == 2) {
                                imgs.remove(1);
                                imgs.add(1, url);
                                iv_report_img2.setImageBitmap(img);
                            } else if (currentIndex == 3) {
                                imgs.remove(2);
                                imgs.add(2, url);
                                iv_report_img3.setImageBitmap(img);
                            }
                        }

                        @Override
                        public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                            CommonUtils.cencelDialog();
                            CommonUtils.showToastShort(ReportActivity.this, "上传失败");
                        }
                    });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && resultCode == 1) {
            String reason = data.getStringExtra("reason");

            tv_reason.setText(reason);
            tv_reason1.setText(reason + "  （选填）");
            return;
        }

        Bitmap bitmap = null;
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            if (cursor.moveToNext()) {
                String path = cursor.getString(0);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                options.inSampleSize = getFitInSampleSize(ScreenUtil.dip2px(ReportActivity.this, 75), ScreenUtil.dip2px(ReportActivity.this, 75), options);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
            }
        }

        if (bitmap == null) {
            return;
        }

        System.gc();

        setImg(bitmap);
    }

    private int getFitInSampleSize(int reqWidth, int reqHeight, BitmapFactory.Options options) {
        int inSampleSize = 1;
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            int widthRatio = Math.round((float) options.outWidth / (float) reqWidth);
            int heightRatio = Math.round((float) options.outHeight / (float) reqHeight);
            inSampleSize = Math.min(widthRatio, heightRatio);
        }
        return inSampleSize;
    }

    private void getImgFromXiangCe() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);

    }

    private void getImgFromCam() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    View popup;
    PopupWindow mPopWindow;

    private void showPopUp() {
        if (mPopWindow == null) {
            popup = View.inflate(this, R.layout.popup_mechanism_more, null);
            mPopWindow = new PopupWindow(popup, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            tv_report = popup.findViewById(R.id.tv_report);
            tv_delete = popup.findViewById(R.id.tv_delete);
            tv_cancel = popup.findViewById(R.id.tv_cancel);
            ll_popup_window = popup.findViewById(R.id.ll_popup_window);

            tv_report.setText("拍照");
            tv_delete.setText("相册");
        }
        mPopWindow.setBackgroundDrawable(new ColorDrawable());
        mPopWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        handlePopup(mPopWindow);
        showPopup(popup);
    }

    TextView tv_report;
    TextView tv_delete;
    TextView tv_cancel;
    LinearLayout ll_popup_window;

    //给popup设置监听
    private void handlePopup(PopupWindow mPopWindow) {
        tv_report.setOnClickListener(view -> {
            getImgFromCam();
            mPopWindow.dismiss();
        });

        tv_delete.setOnClickListener(view -> {
            getImgFromXiangCe();
            mPopWindow.dismiss();
        });

        tv_cancel.setOnClickListener(view -> mPopWindow.dismiss());

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());

        ll_popup_window.setOnClickListener(view -> mPopWindow.dismiss());

        mPopWindow.setOnDismissListener(() -> backgroundAlpha(1f));
    }

    private void showPopup(View popup) {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
        );
        translateAnimation.setDuration(300);
        popup.startAnimation(translateAnimation);
        backgroundAlpha(0.6f);
    }

    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }
}
