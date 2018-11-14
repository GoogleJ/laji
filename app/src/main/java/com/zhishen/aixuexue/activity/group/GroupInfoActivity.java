package com.zhishen.aixuexue.activity.group;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.htmessage.sdk.model.HTGroup;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserNickNameActivity;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.IMAction;
import com.zhishen.aixuexue.util.ACache;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.PhotoUtil;
import com.zhishen.aixuexue.weight.TakePopWindow;

import java.io.File;
import java.util.ArrayList;
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

public class GroupInfoActivity extends BaseActivity implements TakePopWindow.OnItemClickListener {

    @BindView(R.id.iv_group_bg)
    ImageView ivGroupBg;
    @BindView(R.id.iv_group_avatar)
    ImageView ivGroupAvatar;
    @BindView(R.id.tv_groupinfo_name)
    TextView tvGroupinfoName;
    @BindView(R.id.tv_groupinfo_id)
    TextView tvGroupinfoId;
    @BindView(R.id.tv_groupinfo_number)
    TextView tvGroupinfoNumber;
    @BindView(R.id.rv_groupinfo)
    RecyclerView rvGroupinfo;
    @BindView(R.id.tv_groupinfo_groupname)
    TextView tvGroupinfoGroupname;
    @BindView(R.id.tv_groupinfo_grouptype)
    TextView tvGroupinfoGrouptype;
    @BindView(R.id.btn_groupinfo_delete)
    Button btnGroupinfoDelete;
    @BindView(R.id.btn_groupinfo_deleteandback)
    Button btnGroupinfoDeleteandback;
    private Unbinder unbinder;
    private GroupInfoAdapter adapter;
    private String gid;
    private String userId;
    private HTGroup htGroup;
    private List<JSONObject> membersJSONArray = new ArrayList<>();
    private TakePopWindow selectPopWindow;
    private static final int REQUEST_CODE_ADD_USER = 101;
    private static final int REQUEST_GROUP_NAME = 100;
    private static final int REQUEST_CODE_DELETE_USER = 102;
    private static final int CODE_GALLERY_REQUEST = 160;
    private static final int CODE_CAMERA_REQUEST = 161;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 171;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 172;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        transparentStatusBar();
        unbinder = ButterKnife.bind(this);
        gid = getIntent().getStringExtra("groupId");
        userId = AiApp.getInstance().getUsername();
        selectPopWindow = new TakePopWindow(this);
        selectPopWindow.setOnItemClickListener(this);
        setLayout();
        initData();
    }

    private void initData() {
        if (gid == null) {
            finish();
            return;
        }
        htGroup = HTClient.getInstance().groupManager().getGroup(gid);
        if (htGroup == null) {
            finish();
            return;
        }
        if (isManager(htGroup.getOwner())) {
            btnGroupinfoDelete.setVisibility(View.VISIBLE);
            btnGroupinfoDeleteandback.setVisibility(View.GONE);
        } else {
            btnGroupinfoDeleteandback.setVisibility(View.VISIBLE);
            btnGroupinfoDelete.setVisibility(View.GONE);
        }
        tvGroupinfoGroupname.setText(htGroup.getGroupName());
        tvGroupinfoName.setText(htGroup.getGroupName());
        tvGroupinfoId.setText("ID:" + htGroup.getGroupId());
        BitmapUtil.loadCircleImg(ivGroupAvatar, htGroup.getImgUrl(), R.drawable.user_icon_chat_default);
        BitmapUtil.loadNormalImg(ivGroupBg,
                htGroup.getImgUrl()
                , R.drawable.default_image, new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        ivGroupBg.setImageBitmap(BitmapUtil.rsBlur(mActivity, resource, 3, 0.125f));
                        return true;
                    }
                });
        JSONArray jsonArrayCache = ACache.get(getApplicationContext()).getAsJSONArray(userId + gid);
        arrayToList(jsonArrayCache, membersJSONArray);
        adapter = new GroupInfoAdapter(this, membersJSONArray, htGroup.getOwner().equals(userId));
        rvGroupinfo.setAdapter(adapter);
        refreshMembers();
        showTotalMember(membersJSONArray.size());
        adapter.setClicklistener((position, position1) -> {
            if (position == 1) {
                Intent intent = new Intent(mActivity, GroupMemberActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupId", gid);
                intent.putExtra("owner", htGroup.getOwner());
                startActivityForResult(intent, REQUEST_CODE_DELETE_USER);
            } else if (position == 2) {
                Intent intent = new Intent(mActivity, GroupMemberActivity.class);
                intent.putExtra("type", 2);
                intent.putExtra("groupId", gid);
                startActivityForResult(intent, REQUEST_CODE_ADD_USER);
            } else {
                Intent intent = new Intent(mActivity, UserDetailNewActivity.class);
                intent.putExtra(Constant.JSON_KEY_HXID, membersJSONArray.get(position1).getString(Constant.JSON_KEY_HXID));
                startActivity(intent);
            }
        });
    }

    private void setLayout() {
        rvGroupinfo.setLayoutManager(new GridLayoutManager(mActivity, 5));
    }

    private void refreshMembers() {
        AppApi api = ServiceFactory.createRetrofitServiceNoAes(AppApi.class, Constant.NEW_API_HOST.replace("api/", ""), mActivity);
        api.lookGroupMember(gid, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    if (jsonObject.containsKey("code")) {
                        int code = Integer.parseInt(jsonObject.getString("code"));
                        if (code == 1000) {
                            if (jsonObject.containsKey("data") && jsonObject.getJSONArray("data") instanceof JSONArray) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (jsonArray != null && jsonArray.size() != 0) {
                                    ACache.get(mActivity).put(userId + gid, jsonArray);
                                    arrayToList(jsonArray, membersJSONArray);
                                    showTotalMember(membersJSONArray.size());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }, throwable -> {

                });
    }


    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {
        CommonUtils.showDialog(mActivity, "正在清空...");
        new Handler().postDelayed(new Runnable() {

            public void run() {
                HTClient.getInstance().conversationManager().deleteConversationAndMessage(gid);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(IMAction.ACTION_MESSAGE_EMPTY).putExtra("id", gid));
                CommonUtils.cencelDialog();
            }

        }, 2000);
    }

    /**
     * 删除群
     */
    protected void exitGroup() {
        if (userId.equals(htGroup.getOwner())) {
            //自己是群主，解散群
            deleteGroup();
        } else {
            leaveGroup();
        }
    }


    private void arrayToList(JSONArray jsonArray, List<JSONObject> jsonObjects) {
        jsonObjects.clear();
        if (jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
            if (jsonObjectTemp.getString(Constant.JSON_KEY_HXID).equals(htGroup.getOwner())) {
                jsonObjects.add(0, jsonObjectTemp);
            } else {
                jsonObjects.add(jsonObjectTemp);
            }
        }
    }

    /**
     * 显示群人数
     *
     * @param size
     */
    private void showTotalMember(int size) {
        if (tvGroupinfoNumber != null) {
            tvGroupinfoNumber.setText(String.valueOf(size) + "人");
        }
    }


    /**
     * 群主删除群组
     */
    private void deleteGroup() {
        CommonUtils.showDialog(this, getString(R.string.deleting));
        HTClient.getInstance().groupManager().deleteGroup(gid, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.delete_sucess);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), "删除失败");
                    }
                });
            }
        });
    }


    /**
     * 群成员自动退群
     */
    private void leaveGroup() {
        CommonUtils.showDialog(this, "正在退出...");
        HTClient.getInstance().groupManager().leaveGroup(gid, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_NICK), new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.exting_group_success);
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.exting_group_failed);
                    }
                });
            }
        });
    }

    /**
     * 判断自己是否是群主管理员
     *
     * @param userId
     * @return
     */
    private boolean isManager(String userId) {
        if (AiApp.getInstance().getUsername().equals(userId)) {
            return true;
        }
        return false;
    }

    @OnClick({R.id.rl_groupinfo_member, R.id.rl_groupinfo_name, R.id.rl_groupinfo_type, R.id.rl_groupinfo_chatlog, R.id.btn_groupinfo_delete, R.id.btn_groupinfo_deleteandback, R.id.iv_group_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_groupinfo_member:
                Intent intent1 = new Intent(mActivity, GroupMemberActivity.class);
                intent1.putExtra("type", 3);
                intent1.putExtra("groupId", gid);
                intent1.putExtra("owner", htGroup.getOwner());
                startActivity(intent1);
                break;
            case R.id.rl_groupinfo_name:
                Intent intent = new Intent(mActivity, UserNickNameActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupId", gid);
                startActivityForResult(intent, REQUEST_GROUP_NAME);
                break;
            case R.id.rl_groupinfo_type:
                break;
            case R.id.rl_groupinfo_chatlog:
                clearGroupHistory();
                break;
            case R.id.btn_groupinfo_delete:
                exitGroup();
                break;
            case R.id.btn_groupinfo_deleteandback:
                exitGroup();
                break;
            case R.id.iv_group_avatar:
                showPopWindow();
                break;
        }
    }

    private void showPopWindow() {
        selectPopWindow.showAtLocation(findViewById(android.R.id.content),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ADD_USER:// 添加群成员
                    refreshMembers();
                    break;
                case REQUEST_GROUP_NAME:
                    if (data != null) {
                        String value = data.getStringExtra("value");
                        if (value != null) {
                            tvGroupinfoGroupname.setText(value);
                            tvGroupinfoName.setText(value);
                            htGroup.setGroupName(value);
                            HTClient.getInstance().groupManager().saveGroup(htGroup);
                        }
                    }
                    break;
                case REQUEST_CODE_DELETE_USER:
                    refreshMembers();
                    break;
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
            }
            if (picPath != null) {
                CommonUtils.showDialog(mActivity, "上传中...");
                Flowable.just(Arrays.asList(new String[]{picPath}))
                        .observeOn(Schedulers.io())
                        .map(list -> Luban.with(this).load(list).get())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(files -> getLoadUrl(files));

            }
        }
    }

    private void getLoadUrl(List<File> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        File file = files.get(0);
        updataAvatar(file.getAbsolutePath());
//        UploadFileUtils uploadFileUtils = new UploadFileUtils(mActivity, file.getName(), file.getAbsolutePath());
//        uploadFileUtils.asyncUploadFile(new UploadFileUtils.a() {
//            @Override
//            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
//
//            }
//
//            @Override
//            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
//                CommonUtils.cencelDialog();
//                updataAvatar(Constant.baseImgUrl + file.getName());
//            }
//
//            @Override
//            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
//                CommonUtils.showToastShort(mActivity, "上传失败");
//                CommonUtils.cencelDialog();
//            }
//        });
    }

    private void updataAvatar(String avatar) {
        HTClient.getInstance().groupManager().updateGroupImgUrlLocal(gid, avatar, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_NICK), new GroupManager.CallBack() {
            @Override
            public void onSuccess(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.update_success);
                        BitmapUtil.loadCircleImg(ivGroupAvatar, avatar, R.drawable.user_icon_chat_default);
                        BitmapUtil.loadNormalImg(ivGroupBg,
                                avatar
                                , R.drawable.default_image, new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        ivGroupBg.setImageBitmap(BitmapUtil.rsBlur(mActivity, resource, 3, 0.125f));
                                        return true;
                                    }
                                });
                    }
                });
            }

            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                        CommonUtils.showToastShort(getApplicationContext(), R.string.update_groups_failed);

                    }
                });
            }
        });
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

    private void transparentStatusBar() {
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                w.setStatusBarColor(Color.TRANSPARENT);
            } else {
                w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
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
                    CommonUtils.showToastShort(mActivity, "设备没有SD卡");
                }
            } else {
                CommonUtils.showToastShort(mActivity, "请允许打开相机");
            }
        } else if (requestCode == STORAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PhotoUtil.albumPhoto(this, CODE_GALLERY_REQUEST);
            } else {
                CommonUtils.showToastShort(mActivity, "请允许获取读写权限");
            }
        }
    }
}
