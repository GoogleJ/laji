package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

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
 * Created by Jerome on 2018/6/28
 */
public class MenuAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<HomeBean.Menu> menus;

    public MenuAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void addData(List<HomeBean.Menu> data) {
        this.menus = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menus == null ? 0 : menus.size();
    }

    @Override
    public HomeBean.Menu getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MenuHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_home_menu, null);
            holder = new MenuHolder(view);
            view.setTag(holder);
        } else {
            holder = (MenuHolder) view.getTag();
        }
        HomeBean.Menu menu = getItem(position);
        if (TextUtils.isEmpty(menu.getImage())) {
            holder.ivAvatar.setImageResource(R.drawable.default_avatar);
        } else {
            BitmapUtil.loadNormalImg(holder.ivAvatar, menu.getImage(), R.drawable.default_avatar);
        }
        holder.tvTitle.setText(menu.getTitle());
        return view;
    }

    class MenuHolder {
        @BindView(R.id.ivAvatar) ImageView ivAvatar;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public MenuHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
