package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.utils.MessageUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.emojicon.Emojicon;
import com.zhishen.aixuexue.activity.file.FileBrowserActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.group.GroupInfoActivity;
import com.zhishen.aixuexue.activity.video.CaptureVideoActivity;
import com.zhishen.aixuexue.activity.voice.VoiceRecorderView;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.IMAction;
import com.zhishen.aixuexue.manager.NotifierManager;
import com.zhishen.aixuexue.runtimepermissions.MPermissionUtils;
import com.zhishen.aixuexue.util.ACache;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.HTPathUtils;
import com.zhishen.aixuexue.util.ImageUtils;
import com.zhishen.aixuexue.weight.ChatInputView;
import com.zhishen.aixuexue.weight.StatusBarHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;

/**
 * 聊天界面
 */
public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static ChatActivity activityInstance;
    private static final int REQUEST_CODE_MAP = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CODE_LOCAL = 3;
    private static final int REQUEST_CODE_SELECT_VIDEO = 4;
    private static final int REQUEST_CODE_SELECT_FILE = 5;
    private static final int REQUEST_CODE_SELECT_RP = 6;
    private static final int REQUEST_CODE_SELECT_NEAR_IMAGE = 9;
    public String chatTo;//对方的id
    public int chatType = 1;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.inputView)
    ChatInputView chatInputView;
    @BindView(R.id.swiperefrshlayout)
    SwipeRefreshLayout refreshlayout;
    @BindView(R.id.voice_recorder)
    VoiceRecorderView voiceRecorderView;
    private MyBroadcastReciver myBroadcastReciver;
    private List<HTMessage> htMessageList;
    private ChatAdapter adapter;
    private JSONObject extJSON = new JSONObject();
    private static int[] itemNamesSingle = {R.string.attach_take_pic, R.string.attach_picture};//R.string.attach_location, R.string.attach_video, R.string.attach_voice_call, R.string.attach_file
    private static int[] itemIconsSingle = {R.drawable.takephoto, R.drawable.pictures};//R.drawable.location, R.drawable.videos, R.drawable.video_voice_call, R.drawable.fiel_type
    private static int[] itemNamesGroup = {R.string.attach_take_pic, R.string.attach_picture};//R.string.attach_location, R.string.attach_video, R.string.attach_video_call, R.string.attach_file
    private boolean isTop = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    HTMessage htMessage = (HTMessage) msg.obj;
                    htMessageList.add(htMessage);
                    refreshListView();
                    break;
                case 1001:
                    HTMessage htMessage1 = (HTMessage) msg.obj;
                    refreshListView();
                    upLoadMessage(htMessage1);
                    break;
                case 1002:
                    refreshListView();
                    break;
                case 1003:
                    String filePath = (String) msg.obj;
                    sendImageMessage(filePath);
                    break;
            }
        }
    };
    private File cameraFile;
    private ArrayList<String> photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        View viewById = findViewById(R.id.title);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewById.getLayoutParams();
        layoutParams.topMargin = 0;
        viewById.setLayoutParams(layoutParams);

        if (hasKitKat() && !hasLollipop()) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        StatusBarHelper.setStatusBarLightMode(this);

        ButterKnife.bind(this);
        activityInstance = this;
        chatTo = getIntent().getExtras().getString("userId");
        chatType = getIntent().getExtras().getInt("chatType", MessageUtils.CHAT_SINGLE);
        initData();
        Log.d("1212", AiApp.getInstance().getUsername() + "  -----" + chatTo);
        initView();
        initReciver();
        if (chatType == MessageUtils.CHAT_SINGLE) {
            String userNick = getIntent().getStringExtra("userNick");
            User user = ContactsManager.getInstance().getContactList().get(chatTo);
            if (user != null) {
                userNick = user.getNick();
                if (TextUtils.isEmpty(userNick)) {
                    userNick = chatTo;
                }
            } else {
                if (TextUtils.isEmpty(userNick)) {
                    userNick = chatTo;
                }
            }
            setTitle(userNick);
            showRightView(R.drawable.chat_more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2018/7/2 跳转个人页面
                    startActivity(new Intent(ChatActivity.this, UserDetailNewActivity.class).putExtra("userId", chatTo));
                }
            });

        } else if (chatType == MessageUtils.CHAT_GROUP) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(chatTo);
            String groupName;
            if (htGroup == null) {
                groupName = getIntent().getStringExtra("groupName");
            } else {
                groupName = htGroup.getGroupName();
            }
            if (TextUtils.isEmpty(groupName)) {
                groupName = chatTo;
            }
            setTitle(groupName);
            showRightView(R.drawable.chat_more, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2018/7/2 跳转群设置页面
                    startActivity(new Intent(ChatActivity.this, GroupInfoActivity.class).putExtra("groupId", chatTo));
                    Log.d("1212", "chatactivity  走了  ");
                }
            });
        }
    }

    private void initData() {
        htMessageList = HTClient.getInstance().messageManager().getMessageList(chatTo);
        extJSON.put(Constant.JSON_KEY_NICK, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_NICK));
        extJSON.put(Constant.JSON_KEY_AVATAR, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_AVATAR));
        extJSON.put(Constant.JSON_KEY_HXID, AiApp.getInstance().getUserJson().getString(Constant.JSON_KEY_HXID));
        extJSON.put("otherNick", getIntent().getStringExtra("userNick"));
        extJSON.put("otherAvatar", getIntent().getStringExtra("userAvatar"));
        extJSON.put("otherUserId", chatTo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatType == MessageUtils.CHAT_GROUP) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(chatTo);
//            refreshGroupMembersInserver(htGroup.getGroupId());
        }
    }

    private void initView() {
        refreshlayout.setOnRefreshListener(this);
        refreshlayout.setEnabled(false);
        if (chatType == MessageUtils.CHAT_SINGLE) {
            chatInputView.initView(mActivity, refreshlayout, itemNamesSingle, itemIconsSingle);
        } else {
            chatInputView.initView(mActivity, refreshlayout, itemNamesGroup, itemIconsSingle);
        }
        if (chatType == MessageUtils.CHAT_GROUP) {
            chatInputView.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 1 && Constant.ATHOLDER.equals(String.valueOf(s.charAt(start)))) {
//                       sendAtMessage();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        chatInputView.getEditText().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String str = getInputView().getText().toString();
                    Editable edit = getInputView().getEditableText();
                    int selectPos = getInputView().getSelectionStart();
                    selectPos = selectPos == 0 ? -1 : selectPos;
                    int lastAtpos = str.lastIndexOf(Constant.ATHOLDER);
                    lastAtpos = lastAtpos == -1 ? 0 : lastAtpos;
                    int lastTabpos = str.lastIndexOf(Constant.PLACEHOLDER);
                    boolean isDeleteAt = (lastTabpos + 1) == selectPos;
                    if (isDeleteAt) {
                        edit.delete(lastAtpos, lastTabpos + 1);
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String trim = chatInputView.getEditText().getText().toString().trim();
                    chatInputView.getEditText().getText().clear();
                    if (!TextUtils.isEmpty(trim)) {
                        sendTextMessage(trim);
                    }
                    return true;
                }
                return false;
            }
        });

        chatInputView.setInputViewLisenter(new MyInputViewLisenter());
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatInputView.hideSoftInput();
                chatInputView.interceptBackPress();
                return false;
            }
        });

        adapter = new ChatAdapter(htMessageList, mActivity, chatTo, chatType);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HTMessage htMessage = adapter.getItem(i);
                if (htMessage != null) {
                    if (htMessage.getType() == HTMessage.Type.TEXT) {
                        int action = htMessage.getIntAttribute("action", 0);
                        if (action == 10005 || action == 10004) {
                            return false;
                        }
                        if (action == 10001 || action == 10002 || action == 10007 || action == 20000 || action == 20001) {
//                            showCardMsgDialog(htMessage, i);
                            return true;
                        }
                    }
//                    showMsgDialog(htMessage, i);
                }
                return true;
            }
        });
//        adapter.setOnResendViewClick(new ChatAdapter.OnResendViewClick() {
//            @Override
//            public void resendMessage(HTMessage htMessage) {
//                showReSendDialog(htMessage);
//            }
//
//            @Override
//            public void onRedMessageClicked(HTMessage htMessage,String evnId) {
//                OpenRedMessage(evnId,htMessage);//点击红包
//            }
//
//            @Override
//            public void onTransferMessageClicked(HTMessage htMessage, String transferId) {
//                //转账
//
//            }
//
//            @Override
//            public void onAvatarLongClick(JSONObject userJson) {
//                if (chatType == MessageUtils.CHAT_GROUP && !HTApp.getInstance().getUsername().equals(toChatUsername)) {
//                    HTAtMessageHelper.get().refreshGroupMembersInserver(getActivity(),toChatUsername);
//                    presenter.inputAtUsername(userJson);
//                }
//            }
//        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() == listView.getCount() - 1) {
                        refreshlayout.setEnabled(false);
                        isTop = false;
                    } else {
                        isTop = true;
                    }
                } else {
                    refreshlayout.setEnabled(false);
                    isTop = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = listView.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        isTop = true;
                        refreshlayout.setEnabled(true);
                    }
                } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
                    if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == listView.getHeight()) {
                        isTop = false;
                        refreshlayout.setEnabled(false);
                    }
                }
            }
        });
    }

    private void initReciver() {
        myBroadcastReciver = new MyBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_FORWORD);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_EMPTY);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_READ);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        intentFilter.addAction(IMAction.RP_IS_HAS_OPEND);
        intentFilter.addAction(IMAction.ACTION_REMOVED_FROM_GROUP);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(myBroadcastReciver, intentFilter);
    }

    public EditText getInputView() {
        return chatInputView == null ? null : chatInputView.getEditText();
    }

    /**
     * 发送图片消息
     *
     * @param filepath 图片url
     */
    private void sendImageMessage(String filepath) {
        clearAction();
        Bitmap bmp = BitmapFactory.decodeFile(filepath);
        Bitmap bitmap = rotateBitmap(bmp, readPictureDegree(filepath));
        String size = bitmap.getWidth() + "," + bitmap.getHeight();
        Log.d("size---->", size);
        HTMessage htMessage = HTMessage.createImageSendMessage(chatTo, filepath, size);
        sendMessage(htMessage);
    }

    /**
     * 发送文件
     *
     * @param filePath 文件路径
     * @param fileSize 文件大小
     */
    private void sendFileMessage(String filePath, long fileSize) {
        clearAction();
        HTMessage htMessage = HTMessage.createFileSendMessage(chatTo, filePath, fileSize);
        sendMessage(htMessage);
    }

    /**
     * 发送位置
     *
     * @param latitude        纬度
     * @param longitude       经度
     * @param locationAddress 地址
     * @param thumbailPath    预览图路径
     */
    private void sendLocationMessage(double latitude, double longitude, String locationAddress, String thumbailPath) {
        clearAction();
        HTMessage htMessage = HTMessage.createLocationSendMessage(chatTo, latitude, longitude, locationAddress, thumbailPath);
        sendMessage(htMessage);
    }

    /**
     * 发送语音消息
     *
     * @param filePath 语音路径
     * @param length   语音长度
     */
    private void sendVoiceMessage(String filePath, int length) {
        clearAction();
        HTMessage htMessage = HTMessage.createVoiceSendMessage(chatTo, filePath, length);
        sendMessage(htMessage);
    }

    /**
     * 视频
     *
     * @param videoPath 视频路径
     * @param thumbPath 视频预览图
     * @param duration  视频时长
     */
    public void sendVideoMessage(String videoPath, String thumbPath, int duration) {
        clearAction();
        HTMessage htMessage = HTMessage.createVideoSendMessage(chatTo, videoPath, thumbPath, duration);
        sendMessage(htMessage);
    }

    /**
     * 发送文本消息
     *
     * @param content 文本内容
     */
    public void sendTextMessage(String content) {
        clearAction();
        HTMessage htMessage = HTMessage.createTextSendMessage(chatTo, content);
        sendMessage(htMessage);
    }

    public void sendMessage(final HTMessage htMessage) {
        htMessage.setAttributes(extJSON.toJSONString());
        if (chatType == MessageUtils.CHAT_GROUP) {
            htMessage.setChatType(ChatType.groupChat);
        }
        HTClient.getInstance().chatManager().sendMessage(htMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {
                Message message = handler.obtainMessage();
                message.what = 1000;
                message.obj = htMessage;
                handler.sendMessage(message);

            }

            @Override
            public void onSuccess() {
                htMessage.setStatus(HTMessage.Status.SUCCESS);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = htMessage;
                handler.sendMessage(message);
                Log.d("SMACK---->", htMessage.toXmppMessageBody());
            }

            @Override
            public void onFailure() {
                htMessage.setStatus(HTMessage.Status.FAIL);
                HTClient.getInstance().messageManager().saveMessage(htMessage, false);
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.obj = htMessage;
                handler.sendMessage(message);

            }
        });
    }

    /**
     * 旋转图片，使图片保持正确的方向。
     *
     * @param bitmap  原始图片
     * @param degrees 原始图片的角度
     * @return Bitmap 旋转后的图片
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {

        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (null != bitmap) {
            bitmap.recycle();
        }
        return bmp;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return degree;
        }
        return degree;
    }

    public String getToChatUsername() {
        return chatTo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //send the video视频回调
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
                        File file = new File(new HTPathUtils(chatTo, mActivity).getVideoPath(), "th_video" + System.currentTimeMillis() + ".png");
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            ThumbBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            Bitmap bitmap = ImageUtils.decodeScaleImage(file.getAbsolutePath());
                            ACache.get(mActivity).put(file.getAbsolutePath(), bitmap);
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                            Log.d("1212", "videopath   " + videoPath + "   videoabsolutpath    " + file.getAbsolutePath());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //send the file文件回调
                    if (data != null) {
                        String path = data.getStringExtra(FileBrowserActivity.EXTRA_DATA_PATH);
                        Uri uri = Uri.parse(path);
                        if (uri != null) {
                            sendFileByUri(uri, path);
                        }
                    }
                    break;
                case REQUEST_CODE_CAMERA://相机回调
                    if (cameraFile != null && cameraFile.exists()) {
                        sendImageMessage(cameraFile.getAbsolutePath());
//                        List<String> list = new ArrayList<>();
//                        list.add(cameraFile.getAbsolutePath());
//                        compressMore(list);
                    }

                    break;
                case REQUEST_CODE_LOCAL://本地相册回调
                    if (data != null) {
                        photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        if (photos != null) {
//                            compressMore(list);
                            for (int i = 0; i < photos.size(); i++) {
                                sendImageMessage(photos.get(i));
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_MAP://地图位置信息回调
                    double latitude = data.getDoubleExtra("latitude", 0);
                    double longitude = data.getDoubleExtra("longitude", 0);
                    String locationAddress = data.getStringExtra("address");
                    String thumbailPath = data.getStringExtra("thumbnailPath");
                    if (locationAddress != null && !locationAddress.equals("") & new File(thumbailPath).exists()) {
                        sendLocationMessage(latitude, longitude, locationAddress, thumbailPath);
                    } else {
                        CommonUtils.showToastShort(mActivity, R.string.unable_to_get_loaction);
                    }
                    break;
                case REQUEST_CODE_SELECT_NEAR_IMAGE:
                    if (data != null) {
                        String path = data.getStringExtra("path");
                        Log.d("1212", "path   " + path);
                        sendImageMessage(path);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        activityInstance = this;
        NotifierManager.getInstance().cancel(Integer.parseInt(chatTo));
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityInstance = null;
    }

    @Override
    public void onDestroy() {
        isTop = false;
        if (myBroadcastReciver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(myBroadcastReciver);
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String username = intent.getStringExtra("userId");
        if (chatTo.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    private void sendFileByUri(Uri uri, String path) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = mActivity.getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        } else {
            filePath = path;
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            CommonUtils.showToastShort(mActivity, R.string.File_does_not_exist);
            return;
        }
        //limit the size < 10M
        if (file.length() > 10 * 1024 * 1024) {
            CommonUtils.showToastShort(mActivity, R.string.The_file_is_not_greater_than_10_m);
            return;
        }
        sendFileMessage(filePath, file.length());
    }

    private void resendMessage(HTMessage htMessage) {
        htMessageList.remove(htMessage);
        HTClient.getInstance().messageManager().deleteMessage(htMessage.getUsername(), htMessage.getMsgId());
        htMessage.setLocalTime(System.currentTimeMillis());
        htMessage.setAttributes(htMessage.getAttributes().toJSONString());
        htMessage.setStatus(HTMessage.Status.CREATE);
        sendMessage(htMessage);
        refreshListView();
    }

    public void deleteMessage(HTMessage htMessage) {
        HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());
        htMessageList.remove(htMessage);
        refreshListView();
    }

    public void copyMessage(HTMessage htMessage) {
//        HTMessageUtils.getCopyMsg(mActivity, htMessage, chatTo);
    }

    /**
     * 转发消息
     *
     * @param htMessage
     */
    public void forwardMessage(HTMessage htMessage) {
//        HTMessageUtils.getForWordMessage(mActivity, htMessage, chatTo, extJSON);
    }

    /**
     * 撤回消息
     *
     * @param htMessage
     * @param position
     */
    public void withdrawMessage(final HTMessage htMessage, final int position) {
        long msgTime = htMessage.getTime();
        long nowTime = System.currentTimeMillis();
        if ((nowTime - msgTime) / (1000 * 60) < 30) {
            final ProgressDialog progressDialog = new ProgressDialog(mActivity);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.rebacking));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            CmdMessage cmdMessage = new CmdMessage();
            cmdMessage.setTo(chatTo);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", 6000);
            jsonObject.put("msgId", htMessage.getMsgId());
            cmdMessage.setBody(jsonObject.toString());
            if (chatType == MessageUtils.CHAT_GROUP) {
                cmdMessage.setChatType(ChatType.groupChat);
            }
            HTClient.getInstance().chatManager().sendCmdMessage(cmdMessage, new HTChatManager.HTMessageCallBack() {
                @Override
                public void onProgress() {

                }

                @Override
                public void onSuccess() {
                    HTClient.getInstance().messageManager().deleteMessage(chatTo, htMessage.getMsgId());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
//                            HTMessage message = HTMessageUtils.creatWithDrowMsg(htMessage);
//                            htMessageList.set(position, message);
//                            chatView.refreshListView();
                        }
                    });

                }

                @Override
                public void onFailure() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            CommonUtils.showToastShort(mActivity, R.string.reback_failed);
                        }
                    });
                }
            });

        } else {
            CommonUtils.showToastShort(mActivity, R.string.reback_not_more_than_30);
        }
    }

    public void onNewMessage(HTMessage htMessage) {
        if (htMessage.getUsername().contains(chatTo)) {
            if (!htMessageList.contains(htMessage)) {
                htMessageList.add(htMessage);
            }
            refreshListView();
            HTClient.getInstance().conversationManager().markAllMessageRead(chatTo);
        }

    }

    public void refreshListView() {
        adapter.notifyDataSetChanged();
        if (listView.getCount() > 0 && !isTop) {
            listView.setSelection(listView.getCount() - 1);
        }
    }

    private void clearAction() {
        if (extJSON.containsKey("action")) {
            extJSON.remove("action");
        }
    }

    public void messageHasRead() {
        if (TextUtils.isEmpty(chatTo)) {
            return;
        }
        htMessageList.clear();
        htMessageList.addAll(HTClient.getInstance().messageManager().getMessageList(chatTo));
        refreshListView();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMoreMessages();
            }
        }, 500);
        refreshlayout.setRefreshing(false);
    }

    private void loadMoreMessages() {
        if (htMessageList == null || htMessageList.size() == 0) {
            return;
        }
        HTMessage message = htMessageList.get(0);
        if (message != null) {
            List<HTMessage> htMessages = HTClient.getInstance().messageManager().loadMoreMsgFromDB(chatTo, message.getTime(), 20);
            if (htMessages.size() == 0) {
                CommonUtils.showToastShort(mActivity, R.string.not_more_msg);
            } else {
                Collections.reverse(htMessages);
                htMessageList.addAll(0, htMessages);
                refreshListView();
            }
        } else {
            CommonUtils.showToastShort(mActivity, R.string.not_more_msg);
        }
    }

    private class MyBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(IMAction.ACTION_MESSAGE_WITHDROW)) {
                String msgId = intent.getStringExtra("msgId");//收到撤回消息
            } else if (intent.getAction().equals(IMAction.ACTION_MESSAGE_FORWORD)) {//转发消息
                HTMessage message = intent.getParcelableExtra("message");
            } else if (intent.getAction().equals(IMAction.ACTION_NEW_MESSAGE)) {
                HTMessage message = intent.getParcelableExtra("message");
                onNewMessage(message);
            } else if (IMAction.ACTION_MESSAGE_EMPTY.equals(intent.getAction())) {
                String id = intent.getStringExtra("id");
                if (chatTo.equals(id)) {
                    htMessageList.clear();
                    refreshListView();
                    //清空消息
                }
            } else if (IMAction.CMD_DELETE_FRIEND.equals(intent.getAction())) {//你已被对方移除
                String userId = intent.getStringExtra("userId");
                if (mActivity != null) {
                    if (userId.equals(chatTo)) {
                        CommonUtils.showToastShort(mActivity, getString(R.string.just_delete_friend));
                        finish();
                    }
                }
            } else if (IMAction.ACTION_MESSAGE_READ.equals(intent.getAction())) {//标记消息已读
                String userId = intent.getStringExtra("userId");
                if (chatTo.equals(userId)) {
                    messageHasRead();
                }
            } else if (IMAction.ACTION_REMOVED_FROM_GROUP.equals(intent.getAction())) {//您已被移除该群组!
                String groupId = intent.getStringExtra("groupId");
                if (mActivity != null) {
                    if (groupId.equals(chatTo)) {
                        CommonUtils.showToastShort(mActivity, getString(R.string.just_remove_group));
                        finish();
                    }
                }
            }
        }
    }

    private class MyInputViewLisenter implements ChatInputView.InputViewLisenter {

        @Override
        public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {

            return voiceRecorderView.onPressToSpeakBtnTouch(v, event, new VoiceRecorderView.EaseVoiceRecorderCallback() {

                @Override
                public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                    sendVoiceMessage(voiceFilePath, voiceTimeLength);
                }
            });
        }

        @Override
        public void onBigExpressionClicked(Emojicon emojicon) {

        }

        @Override
        public void onSendButtonClicked(String content) {
            sendTextMessage(content);
        }

        @Override
        public boolean onEditTextLongClick() {
            return false;
        }

        @Override
        public void onEditTextUp() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.smoothScrollToPosition(listView.getCount() - 1);
                }
            }, 500);
        }


        @Override
        public void onAlbumItemClicked() {
            MPermissionUtils.requestPermissionsResult(mActivity, 100, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new MPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    selectPicFromLocal();//暂时有问题
                }

                @Override
                public void onPermissionDenied() {

                }
            });
        }

        @Override
        public void onPhotoItemClicked() {

            selectPicFromCamera();
        }

        @Override
        public void onLocationItemClicked() {
            selectLocation();

        }

        @Override
        public void onVideoItemClicked() {
            selectVideo();
        }

        @Override
        public void onCallItemClicked() {
            selectCall();

        }

        @Override
        public void onFileItemClicked() {
            selectFile();

        }

        @Override
        public void onRedPackageItemClicked() {

        }

        @Override
        public void onTransferItemClicked() {

        }

        @Override
        public void onMoreButtonClick() {

        }

        @Override
        public void onCardItemClicked() {

        }

    }

    /**
     * 从图库选择
     */
    private void selectPicFromLocal() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(false)
                .setShowGif(false)
                .setPreviewEnabled(true)
                .setSelected(photos)
                .start(mActivity, REQUEST_CODE_LOCAL);
    }

    public void selectLocation() {
//        startActivityForResult(new Intent(mActivity, GdMapActivity.class), REQUEST_CODE_MAP);
    }

    public void selectVideo() {
        Intent intent = new Intent(mActivity, CaptureVideoActivity.class);
        HTPathUtils htPathUtils = new HTPathUtils(chatTo, mActivity);
        String filePath = htPathUtils.getVideoPath() + "/" + System.currentTimeMillis() + ".mp4";
        intent.putExtra("EXTRA_DATA_FILE_NAME", filePath);
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
    }

    public void selectFile() {
        startActivityForResult(new Intent(mActivity, FileBrowserActivity.class), REQUEST_CODE_SELECT_FILE);
    }

    /**
     * 从相机选择
     */
    public void selectPicFromCamera() {

        if (!CommonUtils.isSdcardExist()) {
            CommonUtils.showToastShort(mActivity, R.string.sd_card_does_not_exist);
            return;
        }
        cameraFile = new File(new HTPathUtils(chatTo, mActivity).getImagePath() + "/" + AiApp.getInstance().getUsername()
                + System.currentTimeMillis() + ".png");
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    private void compressMore(final List<String> pathList) {
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final ArrayList<String> newList = new ArrayList<>();//压缩后的图片路径
        final Handler handler = new Handler();
        class Task implements Runnable {
            String path;

            Task(String path) {
                this.path = path;
            }

            @Override
            public void run() {
//                Luban.get(mActivity)
//                        .load(new File(path))                     //传人要压缩的图片
//                        .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
//                        .setCompressListener(new OnCompressListener() { //设置回调
//                            @Override
//                            public void onStart() {
//                                //  AppManager.I().currentActivity().showDialog("加载中...");
//                            }
//
//                            @Override
//                            public void onSuccess(final File file) {
//                                Message message = handler.obtainMessage();
//                                message.what = 1003;
//                                message.obj = file.getPath();
//                                handler.sendMessage(message);
//                                newList.add(file.getPath());
//                                if (!taskList.isEmpty()) {
//                                    Runnable runnable = taskList.pop();
//                                    handler.post(runnable);
//                                } else {
//                                    //完成之后的个人操作
//                                }
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                            }
//                        }).launch();    //启动压缩
            }
        }
        //循环遍历原始路径 添加至linklist中
        for (String path : pathList) {
            taskList.add(new Task(path));
        }
        handler.post(taskList.pop());
    }

    public void selectCall() {
//        HTAlertDialog dialog = new HTAlertDialog(getActivity(), null, new String[]{getContext().getString(com.zhishen.education.R.string.attach_video_call), getContext().getString(com.zhishen.education.R.string.attach_voice_call)});
//        if (chatType == MessageUtils.CHAT_GROUP) {
//            dialog = new HTAlertDialog(getActivity(), null, new String[]{getContext().getString(com.zhishen.education.R.string.attach_video_call)});
//        }
//        dialog.init(new HTAlertDialog.OnItemClickListner() {
//            @Override
//            public void onClick(int position) {
//                switch (position) {
//                    case 0:
//                        startVideoCall();
//                        break;
//                    case 1:
//                        startVoiceCall();
//                        break;
//                }
//            }
//        });
    }

    /**
     * make a voice call
     */
    private void startVoiceCall() {
//        Intent intent = new Intent(mActivity, VoiceOutgoingActivity.class);
//        intent.putExtra("mode", AnyRTCVideoLayout.AnyRTC_V_1X3.ordinal());
//        intent.putExtra("isOutgoing", true);
//        intent.putExtra("userId", chatTo);
//        startActivity(intent);
    }

    /**
     * make a video call
     */
    private void startVideoCall() {
//        if (chatType == MessageUtils.CHAT_GROUP) {
//            Intent intent = new Intent(mActivity, PreVideoCallActivity.class);
//            intent.putExtra("mode", AnyRTCVideoLayout.AnyRTC_V_1X3.ordinal());
//            intent.putExtra("groupId", chatTo);
//            intent.putExtra("isAgain", false);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(mActivity, VideoOutgoingActivity.class);
//            intent.putExtra("mode", AnyRTCVideoLayout.AnyRTC_V_3X3_auto.ordinal());
//            intent.putExtra("isOutgoing", true);
//            intent.putExtra("userId", chatTo);
//            startActivity(intent);
//        }
    }

    private void upLoadMessage(HTMessage htMessage) {
        String chatType = "1";
        if (htMessage.getChatType() == ChatType.groupChat) {
            chatType = "2";
        }
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        api.uploadMessage(htMessage.getFrom(), htMessage.getTo(), chatType, htMessage.toXmppMessageBody(), AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> Log.d("-----上传聊天记录jsonObject:", jsonObject.toJSONString()), throwable -> Log.d(ChatActivity.class.getName(), throwable.getMessage()));
    }


}
