package com.zhishen.aixuexue.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.htmessage.sdk.model.HTGroup;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by yangfaming on 2018/07/13
 */

public class GroupsListAdapter extends BaseAdapter {
    private List<HTGroup> htGroups;
    private Context context;

    public GroupsListAdapter(Context context, List<HTGroup> htGroups) {
        this.htGroups = htGroups;
        this.context = context;

    }

    @Override
    public int getCount() {
        return htGroups.size();
    }

    @Override
    public HTGroup getItem(int i) {
        return htGroups.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_groups, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        HTGroup item = getItem(i);
        holder.textView.setText(item.getGroupName());
        String imgUrl = item.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
            if (!imgUrl.startsWith("http") || !imgUrl.contains(Constant.baseImgUrl)) {
                imgUrl = Constant.baseImgUrl + imgUrl;
            }
        }
        BitmapUtil.loadCircleImg(holder.iv_avatar, imgUrl, R.drawable.user_icon_chat_default);
        return view;
    }

    private class ViewHolder {
        private CircleImageView iv_avatar;
        private TextView textView;

        public ViewHolder(View view) {
            iv_avatar = view.findViewById(R.id.iv_avatar);
            textView = view.findViewById(R.id.tv_name);
        }
    }

}
