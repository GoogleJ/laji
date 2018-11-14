package com.zhishen.aixuexue.activity.fragment.minefragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.bean.HomeBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jerome on 2018/6/29
 */
public class MenuInfoAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInfalter;
    private List<HomeBean.UserMenuBean.UserMenu> list;

    public MenuInfoAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInfalter = LayoutInflater.from(mContext);
    }

    public void addData(List<HomeBean.UserMenuBean.UserMenu> data) {
        this.list = data == null ? new ArrayList<>() : data;
        notifyDataSetChanged();
    }

    public void update(List<HomeBean.UserMenuBean.UserMenu> data){
        list.clear();
        list.addAll(data);
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
            convertView = mInfalter.inflate(R.layout.item_text_menu, null);
            holder = new MenuHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MenuHolder) convertView.getTag();
        }
        HomeBean.UserMenuBean.UserMenu menu = getItem(position);
        holder.tvCount.setText(menu.getValue());
        holder.tvTitle.setText(menu.getTitle());
        return convertView;
    }

    class MenuHolder {
        @BindView(R.id.tvCount) TextView tvCount;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public MenuHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
