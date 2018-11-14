package com.zhishen.aixuexue.activity.group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.utils.MessageUtils;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.adapter.GroupsListAdapter;
import com.zhishen.aixuexue.activity.fragment.interactionfragment.ChatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupListActivity extends BaseActivity {
    @BindView(R.id.groupListView)
    ListView groupListView;
    private GroupsListAdapter adapter;
    private List<HTGroup> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        ButterKnife.bind(this);
        setTitle(R.string.group_chat);
        initData();
        initView();
        setListener();
    }

    private void setListener() {
        groupListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(mActivity, ChatActivity.class);
            intent.putExtra("userId", adapter.getItem(i).getGroupId());
            intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
            startActivity(intent);
        });
    }

    private void initView() {
        adapter = new GroupsListAdapter(mActivity, list);
        groupListView.setAdapter(adapter);
    }

    private void initData() {
        list = HTClient.getInstance().groupManager().getAllGroups();
    }

    @Override
    public void onResume() {
        refresh();
        super.onResume();

    }

    private void refresh() {
        list.clear();
        list.addAll(HTClient.getInstance().groupManager().getAllGroups());
        adapter.notifyDataSetChanged();
    }
}
