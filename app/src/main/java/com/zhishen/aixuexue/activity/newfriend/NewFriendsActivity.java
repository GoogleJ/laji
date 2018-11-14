package com.zhishen.aixuexue.activity.newfriend;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.dbsqlite.InviteMessage;
import com.zhishen.aixuexue.dbsqlite.InviteMessgeDao;
import com.zhishen.aixuexue.manager.IMAction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewFriendsActivity extends BaseActivity {

    @BindView(R.id.et_search) TextView etSearch;
    @BindView(R.id.listview) ListView listView;
    private NewFriendsAdapter adapter;
    private InviteMessgeDao dao;
    private NewFriendRecivier friendRecivier;
    private List<InviteMessage> allInviteMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends);
        ButterKnife.bind(this);
        setTitle(R.string.new_friend1);
        initData();
    }

    private void initData() {
        dao = new InviteMessgeDao(mActivity);
        allInviteMessage = dao.getMessagesList();
        registerRecivier();
        adapter = new NewFriendsAdapter(mActivity, getAllInviteMessage());
        listView.setAdapter(adapter);
        saveUnreadMessageCount(0);
    }

    public List<InviteMessage> getAllInviteMessage() {
        sortData(allInviteMessage);
        return allInviteMessage;
    }

    public void registerRecivier() {
        friendRecivier = new NewFriendRecivier();
        IntentFilter intent = new IntentFilter(IMAction.ACTION_INVITE_MESSAGE);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(friendRecivier, intent);
    }

    private void unRegisterRecivier() {
        if (friendRecivier != null) {
            LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(friendRecivier);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterRecivier();
    }

    public void saveUnreadMessageCount(int count) {
        dao.saveUnreadMessageCount(count);
    }

    public void refresh() {
        allInviteMessage.clear();
        if (dao == null) {
            dao = new InviteMessgeDao(mActivity);
        }
        List<InviteMessage> messagesList = dao.getMessagesList();
        sortData(messagesList);
        allInviteMessage.addAll(messagesList);
        adapter.notifyDataSetChanged();
        saveUnreadMessageCount(0);
    }

    public void back(View v) {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
        super.onBackPressed();
    }

    @OnClick(R.id.et_search)
    public void onClick() {
        startActivity(new Intent(mActivity, AddFriendsNextActivity.class));
    }

    private class NewFriendRecivier extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_INVITE_MESSAGE.equals(intent.getAction())) {
                refresh();
            }
        }
    }

    private void sortData(List<InviteMessage> allInviteMessage) {
        if (allInviteMessage != null) {
            Collections.sort(allInviteMessage, new TimeComparable());
        }
    }

    private class TimeComparable implements Comparator<InviteMessage> {


        @Override
        public int compare(InviteMessage o1, InviteMessage o2) {
            long time = o1.getTime();
            long time1 = o2.getTime();
            if (time1 > time) {
                return 1;
            } else if (time1 == time) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
