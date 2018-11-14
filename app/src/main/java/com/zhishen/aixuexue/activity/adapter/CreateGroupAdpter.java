package com.zhishen.aixuexue.activity.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.group.CreateGroupsActivity;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangfaming on 2018/7/17.
 */

public class CreateGroupAdpter extends BaseAdapter {
    private CreateGroupsActivity activity;
    private List<User> list;
    private Map<Integer, Boolean> isSelected;

    public Map<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public CreateGroupAdpter(CreateGroupsActivity activity, List<User> list) {
        this.activity = activity;
        this.list = list;
        isSelected = new HashMap<Integer, Boolean>();
        initDate();
    }

    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public User getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.item_creategroup_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = getItem(i);
        String avatar = user.getAvatar();
        String nick = user.getNick();
        holder.textView.setText(nick);
        BitmapUtil.loadCornerImg(holder.iv_avatar, avatar, R.drawable.default_avatar, 5);
        holder.checkBox.setVisibility(View.VISIBLE);
        holder.checkBox.setChecked(getIsSelected().get(i));
        return convertView;
    }


    private class ViewHolder {
        private ImageView iv_avatar;
        private TextView textView;
        private CheckBox checkBox;

        public ViewHolder(View view) {
            iv_avatar = view.findViewById(R.id.iv_avatar);
            textView = view.findViewById(R.id.tv_name);
            checkBox = view.findViewById(R.id.create_checkbox);
        }
    }

    private class MyOnCheckedStateListener implements CheckBox.OnCheckedChangeListener {
        private User user;
        private int p;

        public MyOnCheckedStateListener(User user) {
            this.user = user;
        }

        public MyOnCheckedStateListener(User user, int p) {
            this.user = user;
            this.p = p;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (b) {
            } else {

            }
        }
    }
}
