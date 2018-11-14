package com.zhishen.aixuexue.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.github.chrisbanes.photoview.PhotoView;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowBigImageActivity extends Activity {

    @BindView(R.id.photoView)
    PhotoView photoView;
    private String localPath;
    private String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_big_image);
        ButterKnife.bind(this);
        getData();
    }

    @OnClick(R.id.photoView)
    public void onClick() {
        finish();
    }

    private void getData() {
        localPath = getIntent().getStringExtra("localPath");
        if (TextUtils.isEmpty(localPath)) {
            finish();
            return;
        }
        if (localPath.equals("false")) {
            finish();
            return;
        }
        Log.e("1212", "------localPath：" + localPath);
        if (localPath.contains("http://") || localPath.startsWith("http") || localPath.contains("https://")) {
            path = localPath;
        } else {
            Uri uri = Uri.fromFile(new File(localPath));
            path = uri.getPath();
        }
        BitmapUtil.loadNormalImg(photoView, path, R.drawable.default_image);
    }
}
