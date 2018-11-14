package com.zhishen.aixuexue.activity.converstation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.utils.MessageUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.IMAction;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.HTAlertDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ConversationActivity extends BaseActivity implements ConversationAdapter.OnConversationClickListener {

    @BindView(R.id.tv_connect_errormsg)
    TextView tvConnectErrormsg;
    @BindView(R.id.list)
    RecyclerView listView;
    @BindView(R.id.rl_error_item)
    RelativeLayout rlErrorItem;
    ConversationAdapter adapter;
    private List<HTConversation> allConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        ButterKnife.bind(this);
        setTitle("我的会话");
        getAllConversations();
        initData();
        setListener();
        registerConnectionBroadCast();
    }

    private void setListener() {
        adapter.setOnConversationClickListener(this);
    }

    private void initData() {
        listView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        adapter = new ConversationAdapter(mActivity, allConversations);
        // 设置adapter
        listView.setAdapter(adapter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (IMAction.ACTION_CONNECTION_CHANAGED.equals(intent.getAction())) {
                boolean isConnected = intent.getBooleanExtra("state", true);
                if (isConnected) {
                    rlErrorItem.setVisibility(View.GONE);
                } else {
                    rlErrorItem.setVisibility(View.VISIBLE);
                    if (CommonUtils.isNetWorkConnected(mActivity)) {
                        tvConnectErrormsg.setText(R.string.can_not_connect_chat_server_connection);
                    } else {
                        tvConnectErrormsg.setText(R.string.the_current_network);
                    }
                }

            } else if (IMAction.ACTION_NEW_MESSAGE.equals(intent.getAction()) || IMAction.ACTION_MESSAGE_WITHDROW.equals(intent.getAction())
                    || IMAction.ACTION_REMOVED_FROM_GROUP.equals(intent.getAction())) {
                //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
                refresh();
            } else if (IMAction.CMD_DELETE_FRIEND.equals(intent.getAction())) {
                String userId = intent.getStringExtra(Constant.JSON_KEY_HXID);
                HTConversation conversation = HTClient.getInstance().conversationManager().getConversation(userId);
                deleteConversation(conversation);
            } else if (IMAction.REFRESH_CONTACTS_LIST.equals(intent.getAction())) {
                refreshContactsInServer();
                refresh();
            }
        }
    };


    public void refresh() {
        refreshConversations();
        adapter.notifyDataSetChanged();
        onUnreadMsgChange();
    }

    @Override
    public void onConversationClick(int position, String userId, String userName, String avatar, HTConversation conversation) {
        markAllMessageRead(conversation);
        Intent intent = new Intent(mActivity, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userNick", userName);
        intent.putExtra("userAvatar", avatar);
        Log.d("1212", "----------conversationactivity-----" + userName+"     "+userId);
        if (conversation.getChatType() == ChatType.groupChat) {
            intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
        }
        startActivity(intent);
        Intent intent1 = new Intent("ConversationUnReadNumber");
        intent1.putExtra("unReadNumber", getUnreadMsgCount());
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent1);
    }

    @Override
    public void onConversationLongClick(int position, HTConversation conversation) {
        showItemDialog(conversation);
    }


    public void showItemDialog(final HTConversation htConversation) {
//        String topTitle = getString(R.string.stick_conversation);
//        if (htConversation.getTopTimestamp() != 0) {
//            //已经置顶的会话,显示取消置顶
//            topTitle = getString(R.string.cancle_stick_conversation);
//        }
        HTAlertDialog dialog = new HTAlertDialog(mActivity, null, new String[]{getString(R.string.delete)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                if (position == 0) {
                    deleteConversation(htConversation);
                }
//                else if (position == 1) {
//                    if (htConversation.getTopTimestamp() != 0) {//如果是置顶过的 就取消置顶
//                        conPresenter.cancelTopConversation(htConversation);
//                    } else {  //如果是没有置顶的就置顶
//                        conPresenter.setTopConversation(htConversation);
//                    }
//                }
            }
        });
    }

    //返回多少条未读消息
    private void onUnreadMsgChange() {
        getUnreadMsgCount();
    }

    private void registerConnectionBroadCast() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_CONNECTION_CHANAGED);
        intentFilter.addAction(IMAction.ACTION_NEW_MESSAGE);
        intentFilter.addAction(IMAction.ACTION_MESSAGE_WITHDROW);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        intentFilter.addAction(IMAction.ACTION_REFRESH_ALL_LIST);
        intentFilter.addAction(IMAction.ACTION_REMOVED_FROM_GROUP);
        intentFilter.addAction(IMAction.REFRESH_CONTACTS_LIST);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public List<HTConversation> getAllConversations() {
        allConversations = HTClient.getInstance().conversationManager().getAllConversations();
        return allConversations;
    }

    public void refreshConversations() {
        allConversations.clear();
        allConversations.addAll(HTClient.getInstance().conversationManager().getAllConversations());
    }


    public int getUnreadMsgCount() {
        int unreadMsgCountTotal = 0;
        if (allConversations.size() != 0 && allConversations != null) {
            for (int i = 0; i < allConversations.size(); i++) {
                unreadMsgCountTotal += allConversations.get(i).getUnReadCount();
            }
        }
        Log.d("1212", "未读消息--------------" + unreadMsgCountTotal);
        return unreadMsgCountTotal;
    }


    public void markAllMessageRead(HTConversation conversation) {
        HTClient.getInstance().conversationManager().markAllMessageRead(conversation.getUserId());
        refresh();
    }

    public void deleteConversation(HTConversation htConversation) {
        allConversations.remove(htConversation);
        HTClient.getInstance().messageManager().deleteUserMessage(htConversation.getUserId(), true);
        refresh();
    }

    private void refreshContactsInServer() {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        api.fetchFriends(AiApp.getInstance().getUserSession())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    Log.d("Conversation--------", jsonObject.toJSONString());
                    int code = jsonObject.getIntValue("code");
                    switch (code) {
                        case 1:
                            JSONArray friends = jsonObject.getJSONArray("user");
                            if (friends != null || friends.size() != 0) {
                                List<User> users = new ArrayList<User>();
                                for (int i = 0; i < friends.size(); i++) {
                                    JSONObject friend = friends.getJSONObject(i);
                                    User user = CommonUtils.Json2User(friend);
                                    users.add(user);
                                }
                                ContactsManager.getInstance().saveContactList(users);
                            }
                            break;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

}
