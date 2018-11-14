package com.zhishen.aixuexue.activity.fragment.worldfragment.multi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhishen.aixuexue.BuildConfig;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.worldfragment.ImagePreviewActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.adapter.GridImgAdapter;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.utils.FileUtils;
import com.zhishen.aixuexue.weight.StatusBarHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jerome on 2018/7/21
 */
public class MultiImageSelectorActivity extends AppCompatActivity {

    private  ArrayList<LocalMedia> resultList;

    private static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 110;
    private static final int REQUEST_CAMERA = 100;

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    /** Max image size，int，*/
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Select mode，{@link #MODE_MULTI} by default */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** Whether show camera，true by default */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** Original data set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    public static final String EXTRA_RESULT = "select_result";
    public static final int DEFAULT_IMAGE_SIZE = 9;
    public static final String VIDEO_PATH = "#circle_video";

    private File imageFile;
    private Toast mToast;
    private static final int LOADER_ALL = 0;
    private static final int LOADER_IMG_ALL = 1;
    private static final int LOADER_CATEGORY = 2;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private GridImgAdapter gridImgAdapter;

    // 全部模式下条件
    private String NOT_GIF = "!='image/gif'";
    private boolean isGif;
    private long videoMaxS = 0;
    private long videoMinS = 0;
    private String DURATION = "duration";
    private Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MIS_NO_ACTIONBAR);
        setContentView(R.layout.mis_activity_default);
        mContext = MultiImageSelectorActivity.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        StatusBarHelper.setStatusBarLightMode(this);

        TextView mSubmitButton = findViewById(R.id.commit);
        TextView tvPreview = findViewById(R.id.tvPreview);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView tvTitle = findViewById(R.id.tvTitle);
        if(toolbar != null){
            toolbar.setTitle("");
            tvTitle.setText(getResources().getString(R.string.image_select));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.top_bar_back);
        }

        final int mode = selectMode();
        if(mode == MODE_MULTI) {
            resultList = (ArrayList<LocalMedia>) getIntent().getSerializableExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }
        mSubmitButton.setText(getString(R.string.mis_action_done));
        mSubmitButton.setOnClickListener(view -> {
            Intent data = new Intent();
            data.putExtra(EXTRA_RESULT, gridImgAdapter.getSelectedImages());
            setResult(RESULT_OK, data);
            finish();
        });
        tvPreview.setOnClickListener(view -> {
            if (!resultList.isEmpty()) {
                Intent intent = new Intent(this, ImagePreviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ImagePreviewActivity.PREVIEW_LIST, resultList);
                bundle.putInt(ImagePreviewActivity.START_ITEM_POSITION, 0);
                bundle.putInt(ImagePreviewActivity.START_IAMGE_POSITION, 1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.mRecyclerView);
        mRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,4));
        mRecyclerView.setAdapter(gridImgAdapter = new GridImgAdapter(this,showCamera(),4));
        gridImgAdapter.showSelectIndicator( mode== MODE_MULTI);

        gridImgAdapter.setOnItemClickListener(position -> {
            LocalMedia image = gridImgAdapter.getItem(position);
            if (gridImgAdapter.isShowCamera()) {
                if (position == 0) {
                    showCameraAction();
                } else {
                    selectImageFromGrid(image, mode);
                }
            } else {
                selectImageFromGrid(image, mode);
            }
        });

        getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    private void selectImageFromGrid(LocalMedia image, int mode) {
        if (image != null){
            if (mode == MODE_MULTI){

            } else if (mode == MODE_SINGLE){

            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID };

        // 媒体文件数据库字段
        private String[] PROJECTION = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                DURATION};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            if (id == LOADER_ALL) { //加载视频和图片
                String all_condition = getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), false);
                cursorLoader = new CursorLoader(
                        mContext, QUERY_URI,
                        PROJECTION, all_condition,
                        SELECTION_ALL_ARGS, MediaStore.Files.FileColumns._ID + " DESC");

            } else if(id == LOADER_IMG_ALL) { //只加载图片
                cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4]+">0 AND "+IMAGE_PROJECTION[3]+"=? OR "+IMAGE_PROJECTION[3]+"=? ",
                        new String[]{"image/jpeg", "image/png","image/jpg"}, IMAGE_PROJECTION[2] + " DESC");

            }else if(id == LOADER_CATEGORY){ //分类加载
                cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4]+">0 AND "+IMAGE_PROJECTION[0]+" like '%"+args.getString("path")+"%'",
                        null, IMAGE_PROJECTION[2] + " DESC");
            }
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            List<LocalMedia> images = new ArrayList<>();
            if (data != null) {
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(PROJECTION[1]));
                        String pictureType = data.getString(data.getColumnIndexOrThrow(PROJECTION[2]));
                        int w = data.getInt(data.getColumnIndexOrThrow(PROJECTION[3]));
                        int h = data.getInt(data.getColumnIndexOrThrow(PROJECTION[4]));
                        int duration = data.getInt(data.getColumnIndexOrThrow(PROJECTION[5]));

                        images.add(new LocalMedia(path,pictureType,w,h,duration));

                    } while (data.moveToNext());
                    gridImgAdapter.setData(images);
                    if(resultList != null && resultList.size()>0){
                        gridImgAdapter.setDefaultSelected(resultList);
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
    }
    // 获取图片or视频
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };


    /**
     * 获取视频(最长或最小时间)
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = videoMaxS == 0 ? Long.MAX_VALUE : videoMaxS;
        if (exMaxLimit != 0) maxS = Math.min(maxS, exMaxLimit);

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                Math.max(exMinLimit, videoMinS),
                Math.max(exMinLimit, videoMinS) == 0 ? "" : "=",
                maxS);
    }

    private boolean showCamera(){
        return getIntent() == null || getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    }

    private int selectMode(){
        return getIntent() == null ? MODE_MULTI : getIntent().getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
    }

    private int selectImageCount(){
        return getIntent().getIntExtra(EXTRA_SELECT_COUNT,DEFAULT_IMAGE_SIZE);
    }

    /**
     * Open camera
     */
    private void showCameraAction() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermission(Manifest.permission.CAMERA, getString(R.string.mis_permission_rationale_write_storage),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(this.getPackageManager()) != null) {
                try {
                    imageFile = FileUtils.createTmpFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imageFile != null && imageFile.exists()) {
                    Uri uri= Uri.fromFile(imageFile);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider", imageFile);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else {
                    Toast.makeText(this, R.string.mis_error_image_not_exist, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.mis_msg_no_camera, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, (dialog, which) ->
                            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode))
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_STORAGE_WRITE_ACCESS_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showCameraAction();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (imageFile != null) {
                    // notify system the image has change
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

                    Intent cameraData = new Intent();
                    resultList.add(new LocalMedia(imageFile.getAbsolutePath(),"iamge/png",0,0,0));
                    cameraData.putExtra(EXTRA_RESULT, resultList);
                    setResult(RESULT_OK, cameraData);
                    finish();
                }
            }else{
                // delete tmp file
                while (imageFile != null && imageFile.exists()){
                    boolean success = imageFile.delete();
                    if(success){
                        imageFile = null;
                    }
                }
            }
        }
    }

    private void toast(String title) {
        mToast.setText(title);
        mToast.show();
    }

}
