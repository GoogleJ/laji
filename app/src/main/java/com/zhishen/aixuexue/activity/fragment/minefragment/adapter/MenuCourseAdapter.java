package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jerome on 2018/6/29
 */
public class MenuCourseAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<HomeBean.UserMenuBean.UserMenu> list;

    public MenuCourseAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInfalter = LayoutInflater.from(mContext);
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
        if (convertView == null){
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
        return convertView;
    }


    class MenuHolder {
        @BindView(R.id.ivAvatar)
        ImageView ivAvatar;
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public MenuHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
