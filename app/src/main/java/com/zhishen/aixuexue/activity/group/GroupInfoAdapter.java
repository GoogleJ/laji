package com.zhishen.aixuexue.activity.group;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/27.
 */

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.GroupInfoHodler> {
    private Context context;
    private List<JSONObject> list;
    private boolean isOwner;

    public GroupInfoAdapter(Context context, List<JSONObject> list, boolean isOwner) {
        this.context = context;
        this.list = list;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public GroupInfoHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupInfoHodler(LayoutInflater.from(context).inflate(R.layout.item_groupinfo_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupInfoHodler holder, int position) {

        if (getItemViewType(position) == 1) {
            BitmapUtil.loadCornerImg(holder.imageView, "", R.drawable.groupinfo_remove, 5);
        } else if (getItemViewType(position) == 2) {
            BitmapUtil.loadCornerImg(holder.imageView, "", R.drawable.groupinfo_add, 5);
        } else {
            JSONObject jsonObject = list.get(position);
            String avatar = jsonObject.getString(Constant.JSON_KEY_AVATAR);
            BitmapUtil.loadCornerImg(holder.imageView, avatar, R.drawable.default_avatar, 5);
        }
        if (clicklistener != null) {
            holder.imageView.setOnClickListener(view -> {
                clicklistener.onItemClicklistener(getItemViewType(position),position);
            });
        }
    }

    //1:delete 2：add 3:normal
    @Override
    public int getItemViewType(int position) {
        if (isOwner && position == getItemCount() - 1) {
            return 1;//删除
        } else if (isOwner && position == getItemCount() - 2) {
            return 2;
        } else if (!isOwner && position == getItemCount() - 1) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 10) {
            return 10;
        } else if (isOwner && list.size() + 2 <= 10) {
            return list.size() + 2;
        } else if (!isOwner && list.size() + 1 <= 10) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    public class GroupInfoHodler extends RecyclerView.ViewHolder {
        ImageView imageView;

        public GroupInfoHodler(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_group_item_avatar);
        }
    }

    private OnGroupInfoItemOnClicklistener clicklistener;

    public void setClicklistener(OnGroupInfoItemOnClicklistener clicklistener) {
        this.clicklistener = clicklistener;
    }

    public interface OnGroupInfoItemOnClicklistener {
        void onItemClicklistener(int position,int position1);
    }
}
