package com.zhishen.aixuexue.weight.filterview.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.weight.filterview.FilterTwoEntity;

import java.util.List;

public class FilterLeftAdapter extends BaseListAdapter<FilterTwoEntity> {

    public FilterLeftAdapter(Context context, List<FilterTwoEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterTwoEntity filterEntity) {
        for (FilterTwoEntity entity : getData()) {
            entity.setSelected(filterEntity != null && entity.getType().equals(filterEntity.getType()));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_filter_left, null);
            holder.mView = convertView.findViewById(R.id.mView);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.llRootView = convertView.findViewById(R.id.ll_root_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FilterTwoEntity entity = getItem(position);

        holder.tvTitle.setText(entity.getType());
        boolean isSelected = entity.isSelected();
        holder.mView.setVisibility(entity.isSelected()?View.VISIBLE:View.GONE);
        holder.tvTitle.setTextColor(ContextCompat.getColor(mContext,isSelected? R.color.colorPrimary:R.color.font_black_2));
        holder.llRootView.setBackgroundColor(ContextCompat.getColor(mContext,isSelected? R.color.white:R.color.font_black_6));
        return convertView;
    }

    static class ViewHolder {
        View mView;
        TextView tvTitle;
        LinearLayout llRootView;
    }
}
