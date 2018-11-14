package com.zhishen.aixuexue.weight.filterview.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.weight.filterview.FilterEntity;

import java.util.List;

public class FilterRightAdapter extends BaseListAdapter<FilterEntity> {

    public FilterRightAdapter(Context context, List<FilterEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterEntity filterEntity) {
        for (FilterEntity entity : getData()) {
            entity.setSelected(filterEntity != null && entity.getKey().equals(filterEntity.getKey()));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_filter_one, null);
            holder.ivImage = convertView.findViewById(R.id.ivAvatar);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FilterEntity entity = getItem(position);

        holder.tvTitle.setText(entity.getKey());
        holder.tvTitle.setTextColor(ContextCompat.getColor(mContext,
                entity.isSelected()?R.color.colorPrimary:R.color.font_black_3));
        return convertView;
    }

    static class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }
}
