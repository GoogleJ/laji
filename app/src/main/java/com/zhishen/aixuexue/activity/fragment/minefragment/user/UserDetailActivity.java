package com.zhishen.aixuexue.activity.fragment.minefragment.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.utils.MessageUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.Api;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.GetUserInfoResponse;
import com.zhishen.aixuexue.activity.newfriend.AddFriendsFinalActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.dbsqlite.UserDao;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.ScreenUtil;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class UserDetailActivity extends BaseActivity {
    /**
     * 用户详情页接收两种传值
     * 1：用户完整资料的JSON字符串-userInfo-这种情况如果是好友进行刷新
     * 2：只传用户的hxid，这种情况直接从网络取数据显示-如果是好友，刷新资料
     */
    private RecyclerView recyclerview_userdetail;
    private Adapter adapter = null;
    private GetUserInfoResponse response;
    private JSONObject userJson;
    private String avatarUrl;
    private boolean isTrue = false, isTrue2 = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                userJson = (JSONObject) msg.obj;
                isTrue = true;
                if (isTrue && isTrue2) {
                    if (adapter == null) {
                        adapter = new Adapter();
                        recyclerview_userdetail.setAdapter(adapter);
                        recyclerview_userdetail.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                    }
                }
                Log.d("1212", "===========");
            } else if (msg.what == 2) {
                isTrue2 = true;
                if (isTrue && isTrue2) {
                    if (adapter == null) {
                        adapter = new Adapter();
                        recyclerview_userdetail.setAdapter(adapter);
                        recyclerview_userdetail.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
                    }
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        recyclerview_userdetail = findViewById(R.id.recyclerview_userdetail);
        initData();
        ServiceFactory.createRetrofitService(Api.class, Constant.NEW_API_APPCONFIG, this)
                .userDetail("12312511")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribe(getUserInfoResponse -> {
                    response = getUserInfoResponse;
                    handler.sendEmptyMessage(2);
                }, throwable -> {
                });

    }

    public void back(View view) {
        finish();
    }


    public void refreshInfo(final String userId, final boolean backTask) {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        CommonUtils.showDialog(mActivity, getString(R.string.now_refresh_msg));
        api.getUserInfo(userId, AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        Log.d("UserDetailActivity", "----------" + jsonObject.toJSONString());
                        int code = jsonObject.getInteger("code");
                            CommonUtils.cencelDialog();
                        switch (code) {
                            case 1:
                                JSONObject json = jsonObject.getJSONObject("user");
                                //刷新ui
                                if (isFriend(userId)) {
                                    User user = CommonUtils.Json2User(json);
                                    UserDao dao = new UserDao(mActivity);
                                    dao.saveContact(user);
                                    ContactsManager.getInstance().getContactList().put(user.getUsername(), user);
                                }
                                userJson = json;
                                Message message = new Message();
                                message.what = 1;
                                message.obj = userJson;
                                handler.sendMessage(message);
                                break;
                            default:
                                CommonUtils.cencelDialog();
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonUtils.cencelDialog();
                    }
                });
    }

    private void initData() {
        if (!TextUtils.isEmpty(getFxid())) {
            refreshInfo(getFxid(), true);
            return;
        }
        if (getUserJson() == null) {
            finish();
            return;
        }
        Message message = new Message();
        message.obj = getUserJson();
        message.what = 1;
        handler.sendMessage(message);
        if (isFriend(getUserJson().getString(Constant.JSON_KEY_HXID))) {
            refreshInfo(getUserJson().getString(Constant.JSON_KEY_HXID), false);
        } else {
            refreshInfo(getUserJson().getString(Constant.JSON_KEY_HXID), true);
        }
    }


    public String getFxid() {
        return getIntent().getStringExtra(Constant.JSON_KEY_HXID);
    }

    public JSONObject getUserJson() {
        String extra = getIntent().getStringExtra(Constant.KEY_USER_INFO);
        Log.d("1212", "-----" + extra);
        JSONObject object = JSONObject.parseObject(extra);
        return object;
    }

    /**
     * 资料是否是自己
     *
     * @param userId
     * @return
     */
    public boolean isMe(String userId) {
        return AiApp.getInstance().getUsername().equals(userId);
    }

    /**
     * 资料是否是好友
     *
     * @param userId
     * @return
     */
    public boolean isFriend(String userId) {
        return ContactsManager.getInstance().getContactList().containsKey(userId);
    }


    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new Head(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userdetail_head, parent, false));
                case 1:
                    return new Course(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userdetail_course, parent, false));
                default:
                    return new FriendCircle(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userdetail_friendcircle, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    Log.d("1212", "++++++++++++++++");
                    Head head = (Head) holder;
                    head.initData(userJson);
                    break;
                case 1:
                    Course course = (Course) holder;
                    course.initData();
                    break;
                default:
                    FriendCircle friendCircle = (FriendCircle) holder;
                    friendCircle.initData();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        //0head 1course 2friendCircle
        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else if (position == 1) {
                return 1;
            } else {
                return 2;
            }
        }

        class Head extends RecyclerView.ViewHolder {

            ImageView iv_item_userdetail_head;
            TextView tv_item_userdetail_name;
            TextView tv_item_userdetail_id;
            TextView tv_item_userdetail_add2friend;
            TextView tv_item_userdetail_sendmessage;

            Head(View itemView) {
                super(itemView);
                iv_item_userdetail_head = itemView.findViewById(R.id.iv_item_userdetail_head);
                tv_item_userdetail_name = itemView.findViewById(R.id.tv_item_userdetail_name);
                tv_item_userdetail_id = itemView.findViewById(R.id.tv_item_userdetail_id);
                tv_item_userdetail_add2friend = itemView.findViewById(R.id.tv_item_userdetail_add2friend);
                tv_item_userdetail_sendmessage = itemView.findViewById(R.id.tv_item_userdetail_sendmessage);
            }

            void initData(JSONObject object) {
                if (object == null) {
                    return;
                }
                String id = object.getString(Constant.JSON_KEY_HXID);
                String name = object.getString(Constant.JSON_KEY_NICK);
                avatarUrl = object.getString(Constant.JSON_KEY_AVATAR);
                if (!TextUtils.isEmpty(avatarUrl)) {
                    if (!avatarUrl.contains("http:")) {
                        avatarUrl = Constant.NEW_API_HOST + "upload/" + avatarUrl;
                    }
                }
                CommonUtils.loadUserAvatar(mActivity, avatarUrl, iv_item_userdetail_head);
                tv_item_userdetail_name.setText(name);
                tv_item_userdetail_id.setText(id);

                tv_item_userdetail_add2friend.setOnClickListener(v -> {//添加好友
                    startActivity(new Intent(mActivity, AddFriendsFinalActivity.class).putExtra(Constant.KEY_USER_INFO, object.toJSONString()));
                });
                tv_item_userdetail_sendmessage.setOnClickListener(view -> {//发送消息
                    Intent intent = new Intent(mActivity, ChatActivity.class);
                    intent.putExtra("userId", object.getString(Constant.JSON_KEY_HXID));
                    intent.putExtra("userNick", object.getString(Constant.JSON_KEY_NICK));
                    intent.putExtra("userAvatar", object.getString(Constant.JSON_KEY_AVATAR));
                    intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
                    startActivity(intent.putExtra("userId", object.getString(Constant.JSON_KEY_HXID)));
                });
                if (isMe(id)) {//是自己两个按钮都隐藏
                    tv_item_userdetail_add2friend.setVisibility(View.GONE);
                    tv_item_userdetail_sendmessage.setVisibility(View.GONE);
                    return;
                }
                if (isFriend(id)) {//是好友则显示发送消息反之显示添加好友
                    tv_item_userdetail_sendmessage.setVisibility(View.VISIBLE);
                    tv_item_userdetail_add2friend.setVisibility(View.GONE);
                } else {
                    tv_item_userdetail_add2friend.setVisibility(View.VISIBLE);
                    tv_item_userdetail_sendmessage.setVisibility(View.GONE);
                }

            }
        }

        class Course extends RecyclerView.ViewHolder {
            RecyclerView recyclerview_userdetail_course;
            InnerAdapter adapter;

            Course(View itemView) {
                super(itemView);
                recyclerview_userdetail_course = itemView.findViewById(R.id.recyclerview_userdetail_course);
            }

            void initData() {
                adapter = new InnerAdapter(response.getHistoryCourse(), true);
                recyclerview_userdetail_course.setAdapter(adapter);
                recyclerview_userdetail_course.setLayoutManager(new LinearLayoutManager(UserDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }

        }

        class FriendCircle extends RecyclerView.ViewHolder {
            RecyclerView recyclerview_userdetail_friendcircle;
            InnerAdapter adapter;

            FriendCircle(View itemView) {
                super(itemView);
                recyclerview_userdetail_friendcircle = itemView.findViewById(R.id.recyclerview_userdetail_friendcircle);
            }

            void initData() {
                adapter = new InnerAdapter(response.getCircleFriends(), false);
                recyclerview_userdetail_friendcircle.setAdapter(adapter);
                recyclerview_userdetail_friendcircle.setLayoutManager(new LinearLayoutManager(UserDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            }
        }
    }

    class InnerAdapter extends RecyclerView.Adapter<InnerAdapter.ViewHoder> {

        List<GetUserInfoResponse.Bean> data;
        boolean isCourse;

        InnerAdapter(List<GetUserInfoResponse.Bean> data, boolean isCourse) {
            this.data = data;
            this.isCourse = isCourse;
        }

        @NonNull
        @Override
        public InnerAdapter.ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHoder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_userdetail_inner, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull InnerAdapter.ViewHoder holder, int position) {
            GetUserInfoResponse.Bean bean = data.get(position);

            BitmapUtil.loadCornerImg(holder.iv_item_userdetail_inner, bean.getHeadUrl(), R.drawable.default_image, 3);
            if (isCourse) {
                holder.tv_item_userdetail_inner.setText(bean.getTitle());
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHoder extends RecyclerView.ViewHolder {
            TextView tv_item_userdetail_inner;
            ImageView iv_item_userdetail_inner;

            ViewHoder(View itemView) {
                super(itemView);

                tv_item_userdetail_inner = itemView.findViewById(R.id.tv_item_userdetail_inner);
                iv_item_userdetail_inner = itemView.findViewById(R.id.iv_item_userdetail_inner);

                if (isCourse) {
                    tv_item_userdetail_inner.setVisibility(View.VISIBLE);
                } else {
                    tv_item_userdetail_inner.setVisibility(View.GONE);
                    ViewGroup.LayoutParams layoutParams = iv_item_userdetail_inner.getLayoutParams();
                    layoutParams.width = ScreenUtil.dip2px(UserDetailActivity.this, 120);
                    iv_item_userdetail_inner.setLayoutParams(layoutParams);
                }
            }
        }
    }

}
