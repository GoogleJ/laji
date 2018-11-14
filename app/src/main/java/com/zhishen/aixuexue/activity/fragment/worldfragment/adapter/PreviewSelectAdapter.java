package com.zhishen.aixuexue.activity.fragment.worldfragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.MultiImageSelectorActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/13
 */
public class PreviewSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    public static int ITEM_TYPE_IMAGE = 1;
    private int ITEM_TYPE_ADD = 2;
    private int ITEM_TYPE_VIDEO = 3;
    private ArrayList<LocalMedia> mList = new ArrayList<>();
    private LayoutInflater mInflater = null;
    private MultiSelectItemClickListener selectItemClickListener;

    public PreviewSelectAdapter(Context mContext) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void setSelectItemClickListener(MultiSelectItemClickListener selectItemClickListener) {
        if (null == selectItemClickListener) {
            throw new NullPointerException("select Listener is null");
        }
        this.selectItemClickListener = selectItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1:
                return new ImageHolder(mInflater.inflate(R.layout.item_published_grida, parent, false));
            case 2:
                return new AddImageHolder(mInflater.inflate(R.layout.item_publish_add, parent, false));
            case 3:
                return new VideoHolder(mInflater.inflate(R.layout.item_publish_video, parent,false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageHolder) {
            bindItemImage((ImageHolder) holder, position);
        } else if (holder instanceof AddImageHolder) {
            bindItemAddImage((AddImageHolder) holder);
        } else if(holder instanceof VideoHolder){
            bindVideoImage((VideoHolder) holder);
        }
    }

    public int getDataCount(){
        return mList.size();
    }

    @Override
    public int getItemCount() {
        if (isVideo()){
            return mList.size();
        }
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isVideo()){
            return ITEM_TYPE_VIDEO;
        } else {
            if (position + 1 == getItemCount()) {
                return ITEM_TYPE_ADD;
            }
            return ITEM_TYPE_IMAGE;
        }
    }

    public List<LocalMedia> getDataList() {
        return mList;
    }

    public void updateItem(List<LocalMedia> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    private void bindVideoImage(VideoHolder holder) {
        String videoURL = mList.get(0).path;
        Bitmap bitmap = BitmapUtil.getVideoThumbnail(videoURL, 400, 400, MediaStore.Images.Thumbnails.MINI_KIND);
        holder.ivThumb.setImageBitmap(bitmap);
        BitmapUtil.saveBitmap(mContext, bitmap);
        holder.ivPlay.setOnClickListener(view -> {
            if (null != selectItemClickListener){
                selectItemClickListener.playVideo(videoURL);
            }
        });
    }

    private void bindItemImage(ImageHolder holder, int position) {

        Glide.with(mContext)
                .load(new File(mList.get(position).path))
                .apply(new RequestOptions().fitCenter())
                .into(holder.ivAvatar);
        holder.ivAvatar.setOnClickListener(v -> {
            if (null != selectItemClickListener) {
                selectItemClickListener.selectImageClick(position);
            }
        });
    }

    private void bindItemAddImage(AddImageHolder holder) {
        if (isVideo()){
            holder.ivAdd.setVisibility(View.GONE);
        } else {
            if (getItemCount() >= 9) {
                holder.ivAdd.setVisibility(View.GONE);
            }
            holder.ivAdd.setOnClickListener(v -> {
                if (null != selectItemClickListener) {
                    selectItemClickListener.addImageClick();
                }
            });
        }
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;

        public ImageHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
        }
    }

    class AddImageHolder extends RecyclerView.ViewHolder {

        public ImageView ivAdd;

        public AddImageHolder(View itemView) {
            super(itemView);
            ivAdd = itemView.findViewById(R.id.ivAdd);
        }
    }

    private class VideoHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumb;
        public ImageView ivPlay;
        public VideoHolder(View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            ivPlay = itemView.findViewById(R.id.ivPlay);
        }
    }

    public  boolean isVideo(){
        if (mList != null && !mList.isEmpty()) {
            return mList.get(0).isVideo;
        }
        return false;
    }

    public interface MultiSelectItemClickListener {

        void addImageClick();

        void selectImageClick(int position);

        void playVideo(String videoPath);
    }


}
