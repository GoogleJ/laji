package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.main.MainActivity;
import com.zhishen.aixuexue.manager.LocalUserManager;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jerome on 2018/6/29
 */
public class MenuAdapter extends BaseAdapter implements MainActivity.UnReadMessageClick {
    private Context mContext;
    private LayoutInflater mInfalter;
    private List<HomeBean.UserMenuBean.UserMenu> list;
    private int unReadNumber;
    private boolean hasInviteMessage;

    public MenuAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInfalter = LayoutInflater.from(mContext);
        ((MainActivity) mContext).setClick(this);
        List<HTConversation> list1 = HTClient.getInstance().conversationManager().getAllConversations();
        if (list1 != null && list1.size() > 0) {
            for (int i = 0; i < list1.size(); i++) {
                HTConversation conversation = list1.get(i);
                unReadNumber += conversation.getUnReadCount();
            }
        }
        hasInviteMessage = LocalUserManager.getInstance().getHasUnReadInviateNubmer();
        Log.d("1212", "适配器第一次初始化值    " + unReadNumber + "     " + hasInviteMessage);
    }

    public void addData(List<HomeBean.UserMenuBean.UserMenu> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public HomeBean.UserMenuBean.UserMenu getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuHolder holder;
        if (convertView == null) {
            convertView = mInfalter.inflate(R.layout.item_user_menu, null);
            holder = new MenuHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MenuHolder) convertView.getTag();
        }
        HomeBean.UserMenuBean.UserMenu menu = getItem(position);
        if (TextUtils.isEmpty(menu.getIco())) {
            holder.ivAvatar.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            BitmapUtil.loadNormalImg(holder.ivAvatar, menu.getIco(), R.mipmap.ic_launcher_round);
        }
        holder.tvTitle.setText(menu.getTitle());
        if (menu.getId().equals("my_huihua")) {
            if (unReadNumber > 0) {
                holder.unread_msg_number.setVisibility(View.VISIBLE);
                holder.unread_msg_number.setText(unReadNumber + "");
            } else {
                holder.unread_msg_number.setVisibility(View.GONE);
            }
        } else if (menu.getId().equals("my_tongxunlu")) {
            if (hasInviteMessage) {
                holder.unread_msg_number.setVisibility(View.VISIBLE);
                holder.unread_msg_number.setText("");
            } else {
                holder.unread_msg_number.setVisibility(View.GONE);
            }

        } else {
            holder.unread_msg_number.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public void setUnReadMessageNumber(int count) {
        Log.d("1212", "接口回调值  ====   " + count);
        unReadNumber = count;
        notifyDataSetChanged();
    }

    @Override
    public void hasInviteMessage(boolean hasMessage) {
        hasInviteMessage = hasMessage;
        notifyDataSetChanged();
    }

    class MenuHolder {
        @BindView(R.id.ivAvatar)
        ImageView ivAvatar;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.unread_msg_number)
        TextView unread_msg_number;

        public MenuHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
