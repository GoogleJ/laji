package com.zhishen.aixuexue.activity.group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.GroupManager;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.BaseActivity;
import com.zhishen.aixuexue.activity.fragment.minefragment.user.UserDetailNewActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.main.AiApp;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.util.ACache;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.util.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupMemberActivity extends BaseActivity {

    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.et_search)
    EditText editText;
    @BindView(R.id.tv_groupmember_descr)
    TextView tv_groupmember_descr;
    //群里面已经存在的列表
    private List<String> exitingMembers = new ArrayList<String>();
    // 新添加的列表
    private List<String> addList = new ArrayList<String>();
    private GroupMemberAdapter adapter;
    private String groupId;
    //获取到的好友列表
    private List<User> friendList = new ArrayList<>();
    private int type;
    private String owner;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_member);
        ButterKnife.bind(this);
        groupId = getIntent().getStringExtra("groupId");
        type = getIntent().getIntExtra("type", 0);//1删除2添加
        owner = getIntent().getStringExtra("owner");
        setTitle("群成员");
        setRightTextColor(getResources().getColor(R.color.font_blue));
        listview.setEmptyView(findViewById(R.id.ll_find_emptyview));
        if (type == 1) {//删除
            tv_groupmember_descr.setText("群成员");
            if (groupId != null) {
                JSONArray jsonArray = ACache.get(getApplicationContext()).getAsJSONArray(AiApp.getInstance().getUsername() + groupId);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String userid = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_HXID);
                        String nick = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_NICK);
                        String avatar = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_AVATAR);
                        User user = new User(userid, avatar, nick);
                        friendList.add(user);
                    }
                }
            }
            Log.d("1212", "要移除的群成员数量    " + friendList.size());
            refreshList(friendList, true);
            showRightTextView("踢出", view -> {
                save();
            });
        }
        if (type == 2) {//添加
            tv_groupmember_descr.setText("好友列表");
            if (groupId != null) {
                JSONArray jsonArray = ACache.get(getApplicationContext()).getAsJSONArray(AiApp.getInstance().getUsername() + groupId);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        exitingMembers.add(jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_HXID));
                    }
                }
            }
            // 获取好友列表
            friendList = new ArrayList<>(ContactsManager.getInstance().getContactList().values());
            // 对list进行排序
            Collections.sort(friendList, new PinyinComparator() {
            });
            Log.d("1212", "  要添加的好友列表数量   " + friendList.size());
            refreshList(friendList, true);
            showRightTextView("邀请", view -> {
                save();
            });
        }
        if (type == 3) {//群成员详情
            tv_groupmember_descr.setText("群成员");
            if (groupId != null) {
                JSONArray jsonArray = ACache.get(getApplicationContext()).getAsJSONArray(AiApp.getInstance().getUsername() + groupId);
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String userid = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_HXID);
                        String nick = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_NICK);
                        String avatar = jsonArray.getJSONObject(i).getString(Constant.JSON_KEY_AVATAR);
                        User user = new User(userid, avatar, nick);
                        friendList.add(user);
                    }
                }
            }
            Log.d("1212", "  群里的群成员数量   " + friendList.size());
            refreshList(friendList, true);
        }
        editText.addTextChangedListener(textWatcher);
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            CheckBox checkBox = view.findViewById(R.id.create_checkbox);
            checkBox.toggle();
            if (type == 3) {
                Intent intent = new Intent(mActivity, UserDetailNewActivity.class);
                intent.putExtra(Constant.JSON_KEY_HXID, friendList.get(i).getUsername());
                startActivity(intent);
            } else {
                adapter.getIsSelected().put(i, checkBox.isChecked());
                if (checkBox.isChecked()) {
                    setUserIdList(adapter.getItem(i));
                } else {
                    deleteUserIdList(adapter.getItem(i));
                }
            }
        });
    }

    private void refreshList(List<User> jsonObjectList, boolean isShow) {
        adapter = new GroupMemberAdapter(jsonObjectList, exitingMembers);
        adapter.setShow(isShow);
        listview.setAdapter(adapter);
    }


    private void save() {
        if (addList.size() == 0) {
            CommonUtils.showToastShort(mActivity, R.string.check_friend);
            return;
        }
        if (type == 1) {
            deleteMembers(addList);
        }
        if (type == 2) {
            existsGroupAddMembers(addList);
        }
    }

    /**
     * 邀请好友入群
     *
     * @param addMembers
     */
    private void existsGroupAddMembers(List<String> addMembers) {
        final ProgressDialog progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getString(R.string.joining_group));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        Map<String, String> map = new HashMap<>();
        for (String userId : addMembers) {
            User user = ContactsManager.getInstance().getContactList().get(userId);
            if (user != null) {
                map.put(userId, user.getNick());
            } else {
                map.put(userId, userId);
            }
            Log.d("1212", map.toString() + " === " + userId);
        }
        HTClient.getInstance().groupManager().addMembers(map, groupId, new GroupManager.CallBack() {
            @Override
            public void onSuccess(String data) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.joining_group_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.joining_group_failed, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    /**
     * 群主移除群成员
     *
     * @param deleteMembers
     */

    public void deleteMembers(List<String> deleteMembers) {
        CommonUtils.showDialog(mActivity, getString(R.string.deleting));
        for (String memberUserId : deleteMembers) {
            if (memberUserId.equals(owner)) {
                CommonUtils.showToastShort(this, "群创建者不能被移除!");
                return;
            }
            String memberUserNick = memberUserId;
            User user = ContactsManager.getInstance().getContactList().get(memberUserId);
            if (user != null) {
                memberUserNick = user.getNick();
            }
            HTClient.getInstance().groupManager().deleteMember(groupId, memberUserId, memberUserNick, new GroupManager.CallBack() {
                @Override
                public void onSuccess(String data) {
                    count += 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("1212", deleteMembers.size() + "    " + count);
                            if (deleteMembers.size() == count) {
                                CommonUtils.cencelDialog();
                                CommonUtils.showToastShort(getApplicationContext(), R.string.delete_sucess);
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    });
                }

                @Override
                public void onFailure() {
                }
            });
        }

    }

    /**
     * 删除选中的好友
     *
     * @param user
     */
    private void deleteUserIdList(User user) {
        addList.remove(user.getUsername());
        Log.d("1212", addList.size() + "   移除的 ");

    }

    /**
     * 添加选中的好友
     *
     * @param user
     */
    private void setUserIdList(User user) {
        if (exitingMembers.contains(user.getUsername()) && groupId != null) {
            return;
        }
        if (addList.contains(user.getUsername())) {
            return;
        }
        addList.add(user.getUsername());
        Log.d("1212", addList.size() + "   添加的 ");
    }

    class GroupMemberAdapter extends BaseAdapter {
        private List<User> jsonObjects;
        private List<String> exitedMembers;
        private boolean isShow;
        private Map<Integer, Boolean> isSelected;

        public Map<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        public GroupMemberAdapter(List<User> jsonObjects, List<String> exitedMembers) {
            this.exitedMembers = exitedMembers;
            this.jsonObjects = jsonObjects;
            isSelected = new HashMap<Integer, Boolean>();
            intData();
        }

        private void intData() {
            for (int i = 0; i < jsonObjects.size(); i++) {
                getIsSelected().put(i, false);
            }
        }

        public void setShow(boolean isShow) {
            this.isShow = isShow;
//            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return jsonObjects.size();
        }

        @Override
        public User getItem(int i) {
            return jsonObjects.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            GroupMemberViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_creategroup_list, viewGroup, false);
                holder = new GroupMemberViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (GroupMemberViewHolder) view.getTag();
            }
            User user = getItem(i);
            String userid = user.getUsername();
            holder.textView.setText(user.getNick());
            BitmapUtil.loadCircleImg(holder.imageView, user.getAvatar(), R.drawable.default_avatar);
            if (type == 3) {
                holder.checkBox.setVisibility(View.GONE);
            } else {
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(getIsSelected().get(i));
                if (exitedMembers != null) {
                    if (exitedMembers != null && exitedMembers.contains(userid)) {
                        holder.checkBox.setChecked(true);
                        holder.checkBox.setEnabled(false);
                    }
                }
            }
            holder.checkBox.setOnClickListener(view1 -> {
                getIsSelected().put(i, holder.checkBox.isChecked());
                if (holder.checkBox.isChecked()) {
                    setUserIdList(user);
                } else {
                    deleteUserIdList(user);
                }
            });

            return view;
        }

    }

    class GroupMemberViewHolder {
        private ImageView imageView;
        private TextView textView;
        private CheckBox checkBox;

        public GroupMemberViewHolder(View view) {
            imageView = view.findViewById(R.id.iv_avatar);
            textView = view.findViewById(R.id.tv_name);
            checkBox = view.findViewById(R.id.create_checkbox);
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

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.length() > 0) {
                List<User> objectList = new ArrayList<>();
                String s = editText.getText().toString();
                for (User object : friendList) {
                    if (object.getNick().contains(s)) {
                        objectList.add(object);
                    }
                }
                refreshList(objectList, true);
            } else {
                refreshList(friendList, true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    private class MyOnCheckedStateListener implements CheckBox.OnCheckedChangeListener {
        private User user;

        public MyOnCheckedStateListener(User user) {
            this.user = user;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                setUserIdList(user);
            } else {
                deleteUserIdList(user);
            }
        }
    }


}
