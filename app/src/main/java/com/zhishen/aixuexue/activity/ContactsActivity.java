package com.zhishen.aixuexue.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTChatManager;
import com.htmessage.sdk.model.CmdMessage;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.adapter.ContactsAdapter;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.activity.group.GroupListActivity;
import com.zhishen.aixuexue.activity.newfriend.AddFriendsNextActivity;
import com.zhishen.aixuexue.activity.newfriend.NewFriendsActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.dbsqlite.InviteMessgeDao;
import com.zhishen.aixuexue.dbsqlite.UserDao;
import com.zhishen.aixuexue.http.AppApi;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.http.ServiceFactory;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.IMAction;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.weight.HTAlertDialog;
import com.zhishen.aixuexue.weight.Sidebar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 通讯录
 */
public class ContactsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_search)
    EditText tvSearch;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.sidebar)
    Sidebar sidebar;
    private List<User> contacts;
    private ContactsAdapter adapter;
    private TextView tv_total, tv_unread;
    private RelativeLayout re_newfriends, re_chatroom;
    private InviteMessgeDao inviteMessgeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        re_newfriends.setOnClickListener(this);
        re_chatroom.setOnClickListener(this);
        tvSearch.addTextChangedListener(textWatcher);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    startActivity(new Intent(mActivity, UserDetailNewActivity.class)
                            .putExtra(Constant.KEY_USER_INFO, user.getUserInfo()));
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && position != adapter.getCount() + 1) {
                    User user = adapter.getItem(position - 1);
                    showItemDialog(user);
                }
                return true;
            }
        });
    }

    private void showItemDialog(User user) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(mActivity, null, new String[]{getResources().getString(R.string.delete)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        deleteContacts(user.getUsername());
                        break;
                }
                refresh();
            }
        });

    }

    public void deleteContacts(String userId) {
        Map<String, User> users = ContactsManager.getInstance().getContactList();
        User user = users.get(userId);
        users.remove(user.getUsername());
        contacts.remove(user);
        deleteContact(user);
    }

    private void deleteContact(User user) {
        CommonUtils.showDialog(mActivity, getResources().getString(R.string.deleting));
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        JSONObject userJson = AiApp.getInstance().getUserJson();
        if (userJson != null) {
            String session = userJson.getString(Constant.JSON_KEY_SESSION);
            if (session != null) {
                api.removeFriend(user.getUsername(), session)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject jsonObject) throws Exception {
                                int code = jsonObject.getIntValue("code");
                                CommonUtils.cencelDialog();
                                switch (code) {
                                    case 1:
                                        InviteMessgeDao dao = new InviteMessgeDao(mActivity);
                                        dao.deleteMessage(user.getUsername());
                                        UserDao userDao = new UserDao(mActivity);
                                        userDao.deleteContact(user.getUsername());
                                        HTClient.getInstance().conversationManager().deleteConversationAndMessage(user.getUsername());
                                        sendDeleteCmd(user);
                                        Toast.makeText(mActivity, R.string.delete_sucess, Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(mActivity, R.string.Delete_failed, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(mActivity, R.string.Delete_failed);
                            }
                        });
            }
        }
    }

    private void sendDeleteCmd(User user) {
        JSONObject userJson = AiApp.getInstance().getUserJson();
        CmdMessage customMessage = new CmdMessage();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("action", 1003);
        JSONObject data = new JSONObject();
        data.put("userId", userJson.getString("userId"));
        data.put("nick", userJson.getString("nick"));
        data.put("avatar", userJson.getString("avatar"));
        data.put("role", userJson.getString(Constant.JSON_KEY_ROLE));
        data.put("teamId", userJson.getString("teamId"));
        jsonObject.put("data", data);
        customMessage.setMsgId(UUID.randomUUID().toString());
        customMessage.setFrom(AiApp.getInstance().getUsername());
        customMessage.setTime(System.currentTimeMillis());
        customMessage.setTo(user.getUsername());
        customMessage.setBody(jsonObject.toJSONString());
        customMessage.setChatType(ChatType.singleChat);
        HTClient.getInstance().chatManager().sendCmdMessage(customMessage, new HTChatManager.HTMessageCallBack() {
            @Override
            public void onProgress() {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void initView() {
        View footerView = LayoutInflater.from(mActivity).inflate(R.layout.item_contact_list_footer, null);
        View headView = LayoutInflater.from(mActivity).inflate(R.layout.item_contact_list_header, null);
        tv_unread = headView.findViewById(R.id.tv_unread);
        tv_total = footerView.findViewById(R.id.tv_total);
        re_newfriends = headView.findViewById(R.id.re_newfriends);
        re_chatroom = headView.findViewById(R.id.re_chatroom);
        listView.addHeaderView(headView);
        listView.addFooterView(footerView, null, false);
    }

    private void initData() {
        setTitle("通讯录");
        showRightView(R.drawable.add_friend, view -> {
            startActivityForResult(new Intent(mActivity, AddFriendsNextActivity.class), 10086);
        });
        showSiderBar();
        inviteMessgeDao = new InviteMessgeDao(mActivity);
        adapter = new ContactsAdapter(mActivity, getContactsListInDb());
        listView.setAdapter(adapter);
        refreshContactsInServer();
        registerBroadReciever();
    }

    public void showSiderBar() {
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setListView(listView);
    }

    private void refreshContactsInServer() {
        AppApi api = ServiceFactory.createNewRetrofitServiceAes(AppApi.class, mActivity);
        JSONObject userJson = AiApp.getInstance().getUserJson();
        if (userJson != null) {
            String session = userJson.getString(Constant.JSON_KEY_SESSION);
            if (session != null) {
                api.fetchFriends(session).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<JSONObject>() {
                            @Override
                            public void accept(JSONObject jsonObject) throws Exception {
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
                                            contacts.clear();
                                            contacts.addAll(sortList(users));
                                            refresh();
                                        }
                                        break;
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        });
            }

        }
    }

    public List<User> getContactsListInDb() {
        contacts = sortList(new ArrayList<User>(ContactsManager.getInstance().getContactList().values()));
        return contacts;
    }

    public void clearInvitionCount() {
        inviteMessgeDao.saveUnreadMessageCount(0);
        showInvitionCount(0);
    }

    public List<User> sortList(List<User> users) {
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(users, comparator);
        return users;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_newfriends:
                startActivityForResult(new Intent(mActivity, NewFriendsActivity.class), 10086);
                clearInvitionCount();
                Intent intent = new Intent();
                intent.setAction("InviteMessageUnreadNumber");
                intent.putExtra("unReadNumber", 0);
                LocalUserManager.getInstance().setHasUnReadInviateNubmer(false);
                LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
                break;
            case R.id.re_chatroom:
                startActivity(new Intent(mActivity, GroupListActivity.class));
                break;
        }
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


    private MyBroadcastReceiver myBroadcastReceiver;

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("1212", "收到好友添加申请---");
            String action = intent.getAction();
            if (IMAction.ACTION_CONTACT_CHANAGED.equals(action)) {
                refreshContactsInLocal();
            } else if (IMAction.ACTION_INVITE_MESSAGE.equals(action)) {
                showInvitionCount(1);
            } else if (IMAction.CMD_DELETE_FRIEND.equals(action)) {
                String userId = intent.getStringExtra(Constant.JSON_KEY_HXID);
                deleteContactOnBroast(userId);
                refresh();
            } else if (IMAction.REFRESH_CONTACTS_LIST.equals(intent.getAction())) {
                refreshContactsInServer();
            }
        }
    }

    private void registerBroadReciever() {
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_CONTACT_CHANAGED);
        intentFilter.addAction(IMAction.ACTION_INVITE_MESSAGE);
        intentFilter.addAction(IMAction.CMD_DELETE_FRIEND);
        intentFilter.addAction(IMAction.REFRESH_CONTACTS_LIST);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        if (myBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(myBroadcastReceiver);
        }
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            showInvitionCount(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LocalUserManager.getInstance().getHasUnReadInviateNubmer()) {
            tv_unread.setVisibility(View.VISIBLE);
        } else {
            tv_unread.setVisibility(View.GONE);
        }
    }

    //移除用户
    public void deleteContactOnBroast(String id) {
        Map<String, User> users = ContactsManager.getInstance().getContactList();
        User user = users.get(id);
        users.remove(id);
        InviteMessgeDao dao = new InviteMessgeDao(mActivity);
        dao.deleteMessage(id);
        UserDao userDao = new UserDao(mActivity);
        userDao.deleteContact(id);
        contacts.remove(user);
        HTClient.getInstance().conversationManager().deleteConversationAndMessage(id);
    }

    public void showInvitionCount(int count) {
        if (count != 0) {
            tv_unread.setVisibility(View.VISIBLE);
        } else {
            tv_unread.setVisibility(View.GONE);
        }
//        showInvitionCount(count);
    }

    public void showContactsCount(int count) {
        if (tv_total != null) {
            tv_total.setText(String.format(getString(R.string.more_people), String.valueOf(count)));
        }
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
        showContactsCount(getContactsCount());
    }

    public int getContactsCount() {
        return contacts.size();
    }

    public void refreshContactsInLocal() {
        contacts.clear();
        contacts.addAll(sortList(new ArrayList<User>(ContactsManager.getInstance().getContactList().values())));
        refresh();
    }

    private void refreshList(List<User> users) {
        sortList(users);
        adapter = new ContactsAdapter(mActivity, users);
        listView.setAdapter(adapter);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                List<User> list = new ArrayList<>();
                String s = tvSearch.getText().toString();
                for (User user : contacts) {
                    if (user.getNick().contains(s)) {
                        list.add(user);
                    }
                }
                refreshList(list);
            } else {
                refreshList(contacts);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
