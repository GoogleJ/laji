package com.zhishen.aixuexue.activity.group;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.adapter.CreateGroupAdpter;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.BaseResponseDataT;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.http.request.CreateGroupRequest;
import com.zhishen.aixuexue.http.response.GroupResponse;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.PhotoUtil;
import com.zhishen.aixuexue.weight.GroupPopWindow;
import com.zhishen.aixuexue.weight.TakePopWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

public class CreateGroupsActivity extends BaseActivity implements GroupPopWindow.OnItemClickListener, TakePopWindow.OnItemClickListener {
    private static final int CODE_GALLERY_REQUEST = 160;
    private static final int CODE_CAMERA_REQUEST = 161;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 171;
    private static final int STORAGE_PERMISSIONS_REQUEST_CODE = 172;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.tv_create_grouptype)
    TextView tvCreateGrouptype;
    @BindView(R.id.rl_creategroup_type)
    RelativeLayout rlCreategroupType;
    @BindView(R.id.et_creategroup_name)
    EditText etCreategroupName;
    @BindView(R.id.et_creategroup_describe)
    EditText etCreategroupDescribe;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.ivAvatar)
    ImageView imageView;
    @BindView(R.id.ll_create_avatar)
    RelativeLayout ll_create_avatar;
    private String avatarUrl;
    private String picPath;
    private List<User> contacts;
    private List<String> userIdList = new ArrayList<>();
    private CreateGroupAdpter adpter;
    private GroupPopWindow popWindow;
    private TakePopWindow selectPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_groups);
        ButterKnife.bind(this);
        initView();
        getContactsListInDb();
    }

    private void refreshList(List<User> userList) {
        sortList(userList);
        adpter = new CreateGroupAdpter(this, userList);
        listview.setAdapter(adpter);
    }


    public void getContactsListInDb() {
        contacts = sortList(new ArrayList<User>(ContactsManager.getInstance().getContactList().values()));
        adpter = new CreateGroupAdpter(CreateGroupsActivity.this, contacts);
        listview.setAdapter(adpter);
    }

    public List<User> sortList(List<User> users) {
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(users, comparator);
        return users;
    }




    public class PinyinComparator implements Comparator<User> {

        @Override
        public int compare(User o1, User o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();
            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }

        }
    }

    public void setUserIdList(User user) {
        if (userIdList.contains(user.getUsername())) {
            return;
        }
        userIdList.add(user.getUsername());
        Log.d("1212", "   chuangjianqun  " + userIdList.size());
    }

    public void deleteUserIdList(User user) {
        userIdList.remove(user.getUsername());
        Log.d("1212", "   chuangjianqun  222222222    " + userIdList.size());

    }

    private void initView() {
        setTitle("创建群");
        setRightTextColor(getResources().getColor(R.color.font_blue));
        showRightTextView("创建", view -> {
            if (TextUtils.isEmpty(avatarUrl)) {
                CommonUtils.showToastShort(mActivity, "请上传群头像");
                return;
            } else if (TextUtils.isEmpty(etCreategroupName.getText().toString())) {
                CommonUtils.showToastShort(mActivity, "请输入群名称");
                return;
            } else if (TextUtils.isEmpty(etCreategroupDescribe.getText().toString())) {
                CommonUtils.showToastShort(mActivity, "请输入群描述");
                return;
            } else if (TextUtils.equals(tvCreateGrouptype.getText().toString(), "选择群类型")) {
                CommonUtils.showToastShort(mActivity, "请选择群类型");
                return;
            } else if (userIdList.size() < 2) {
                CommonUtils.showToastShort(mActivity, "请至少选择2名好友");
                return;
            }
            createGroupInService();
        });
        popWindow = new GroupPopWindow(mActivity, R.layout.view_pop_top_group);
        selectPopWindow = new TakePopWindow(this);
        selectPopWindow.setOnItemClickListener(this);
        popWindow.setOnItemClickListener(this);
        etSearch.addTextChangedListener(textWatcher);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("1212", "item   zhixing le " + i);
                CheckBox checkBox = view.findViewById(R.id.create_checkbox);
                checkBox.toggle();
                adpter.getIsSelected().put(i, checkBox.isChecked());
                if (checkBox.isChecked() == true) {
                    setUserIdList(adpter.getItem(i));
                } else {
                    deleteUserIdList(adpter.getItem(i));
                }
            }
        });
    }

    private void createGroupInService() {
        AppApi api = ServiceFactory.createRetrofitService(AppApi.class, Constant.BASE_TEMP, mActivity);
        CommonUtils.showDialog(mActivity, "正在创建...");
        Location latlng = LocalUserManager.getInstance().getLatlng();
        double latitude = 34.237139;
        double longitude = 108.895641;
        if (latlng != null) {
            latitude = latlng.getLatitude();
            longitude = latlng.getLongitude();
        }
        CreateGroupRequest request = new CreateGroupRequest();
        request.setDescri(etCreategroupDescribe.getText().toString());
        request.setCreator(AiApp.getInstance().getUsername());
        request.setGroup_type("1");
        request.setLat(latitude + "");
        request.setLon(longitude + "");
        request.setName(etCreategroupName.getText().toString());
        request.setVersion("1.1");
        request.setType("GROUPTYPE002");
        request.setUserList(userIdList);
        request.setImgurl(avatarUrl);
        api.createGroup(request.decode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponseDataT<GroupResponse>>() {
                    @Override
                    public void accept(BaseResponseDataT<GroupResponse> dataT) throws Exception {
                        CommonUtils.cencelDialog();
                        if (dataT.code == 0) {
                            Intent intent = new Intent();
                            HTGroup htGroup = new HTGroup();
                            htGroup.setImgUrl(dataT.data.getImgurl());
                            htGroup.setGroupId(String.valueOf(dataT.data.getGid()));
                            htGroup.setGroupName(dataT.data.getName());
                            htGroup.setGroupDesc(dataT.data.getDescri());
                            htGroup.setOwner(dataT.data.getCreator());
                            HTClient.getInstance().groupManager().saveGroup(htGroup);
                            intent.putExtra("Group", htGroup);
                            mActivity.setResult(10000, intent);
                            finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonUtils.cencelDialog();
                    }
                });

    }

    @OnClick({R.id.rl_creategroup_type, R.id.ll_create_avatar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_creategroup_type:
                view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                popWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.ll_create_avatar:
                showPopWindow();
                break;
        }


    }

    private void showPopWindow() {
        selectPopWindow.showAtLocation(findViewById(android.R.id.content),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 选择群类型点击事件
     *
     * @param v
     * @param s
     */
    @Override
    public void setOnItemClick(View v, String s) {
        popWindow.dismiss();
        tvCreateGrouptype.setText(s);
    }

    /**
     * 选择相机点击事件
     *
     * @param v
     */
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
            }
            Log.d("onActivityResult", "picPath->" + picPath);
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
        UploadFileUtils uploadFileUtils = new UploadFileUtils(mActivity, file.getName(), file.getAbsolutePath());
        uploadFileUtils.asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {

            }

            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                CommonUtils.cencelDialog();
                avatarUrl = Constant.baseImgUrl + file.getName();
                runOnUiThread(() -> BitmapUtil.loadCircleImg(imageView, avatarUrl, R.drawable.default_avatar));
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                CommonUtils.showToastShort(mActivity, "上传失败");
                CommonUtils.cencelDialog();
            }
        });
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                String newText = etSearch.getText().toString();
                List<User> usersTemp = new ArrayList<User>();
                for (User user : contacts) {
                    if (user.getNick().contains(newText)) {
                        usersTemp.add(user);
                    }
                }
                refreshList(usersTemp);
            } else {
                refreshList(contacts);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
