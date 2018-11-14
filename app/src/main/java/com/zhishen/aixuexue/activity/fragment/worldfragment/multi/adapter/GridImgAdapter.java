package com.zhishen.aixuexue.activity.fragment.worldfragment.multi.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.MultiImageSelectorActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jerome on 2018/7/14
 */
public class GridImgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;
    private Context mContext;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;
    private LayoutInflater mInflater = null;
    final int mGridWidth;
    private Toast mToast;
    private boolean subSelect;
    public int videoIndex = -1;
    private OnItemClickListener onItemClickListener;
    private List<LocalMedia> mImages = new ArrayList<>(); //总图片
    private ArrayList<LocalMedia> mSelectedImages = new ArrayList<>();

    public GridImgAdapter(Context mContext, boolean showCamera, int column) {
        this.mContext = mContext;
        this.showCamera = showCamera;
        this.mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        this.mInflater = LayoutInflater.from(mContext);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (null == onItemClickListener) {
            throw new NullPointerException(" listener is null");
        }
        this.onItemClickListener = onItemClickListener;
    }

    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b)
            return;
        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public ArrayList<LocalMedia> getSelectedImages() {
        return mSelectedImages;
    }

    /**
     * 选择某个图片，改变选择状态,动态修改标记
     */
    int j = 0;
    public void select(LocalMedia image) {
        if (image.isVideo) {
            if (mSelectedImages.contains(image)) {
                mSelectedImages.remove(image);
            } else {
                mSelectedImages.add(image);
            }
        } else {
            if (mSelectedImages.contains(image)) {
                mSelectedImages.remove(image);
            } else {
                if (mSelectedImages.size() == MultiImageSelectorActivity.DEFAULT_IMAGE_SIZE) {
                    toast("最多只能选9张");
                    return;
                }
                mSelectedImages.add(image);
            }
            for (LocalMedia imgs : mSelectedImages) {
                imgs.index = ++j;
            }
            j = 0;
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     */
    public void setDefaultSelected(ArrayList<LocalMedia> resultList) {
        int i = 0;
        for (LocalMedia localMedia : resultList) {
            LocalMedia image = getImageByPath(localMedia);
            if (image != null) {
                image.index = ++i;
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }

    private LocalMedia getImageByPath(LocalMedia localMedia) {
        if (mImages != null && mImages.size() > 0) {
            for (LocalMedia image : mImages) {
                if (image.path.equalsIgnoreCase(localMedia.path)) {
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     */
    public void setData(List<LocalMedia> images) {
        mSelectedImages.clear();
        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    public LocalMedia getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isShowCamera() && viewType == TYPE_CAMERA) {
            return new CameraHolder(mInflater.inflate(R.layout.mis_list_item_camera, parent, false));
        }
        return new ImageHolder(mInflater.inflate(R.layout.mis_list_item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isShowCamera()) {
            if (position == 0) {
                bindCameraHolder((CameraHolder) holder, position);
            } else {
                bindImageHolder((ImageHolder) holder, position);
            }
        } else {
            bindImageHolder((ImageHolder) holder, position);
        }
    }

    private void bindCameraHolder(CameraHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.getImagePosition(position);
            }
        });
    }

    private void bindImageHolder(ImageHolder holder, int position) {
        LocalMedia image = getItem(position);
        if (showSelectIndicator) {
            holder.indicator.setVisibility(View.VISIBLE);
            if (mSelectedImages.contains(image)) {
                // 设置选中状态
                //holder.indicator.setImageResource(R.drawable.mis_btn_selected);
                //holder.mask.setVisibility(View.VISIBLE);
                if (image.isVideo) {
                    if (image.isChecked) {
                        holder.indicator.setImageResource(R.drawable.ic_world_check_disable);
                    } else {
                        holder.indicator.setImageResource(R.drawable.ic_world_check_enable);
                    }
                    holder.tvSign.setVisibility(View.GONE); //隐藏数字索引
                    holder.indicator.setVisibility(View.VISIBLE);
                } else {
                    //数字标记显示
                    holder.tvSign.setText(String.valueOf(image.index));
                    holder.tvSign.setVisibility(View.VISIBLE);
                    holder.indicator.setVisibility(View.GONE);
                }
                holder.mask.setVisibility(View.VISIBLE);
            } else {
                // 未选择
                //holder.indicator.setImageResource(R.drawable.mis_btn_unselected);

                holder.tvSign.setVisibility(View.GONE);
                holder.indicator.setVisibility(View.VISIBLE);
                holder.indicator.setImageResource(R.drawable.ic_world_check_disable);
                holder.mask.setVisibility(View.GONE);
            }
        } else {
            holder.indicator.setVisibility(View.GONE);
        }
        File imageFile = new File(image.path);
        if (imageFile.exists()) {
            // 显示图片
            Glide.with(mContext)
                    .load(imageFile)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.mis_default_error)
                            .override(mGridWidth, mGridWidth)
                            .centerCrop()
                    ).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.mis_default_error);
        }

        //视频时长
        if (image.isVideo) {
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText(durationFormat(image.duration));
        } else {
            holder.tvDuration.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(view -> {
            if (mSelectedImages.size() > 0) {
                if (mSelectedImages.get(0).isVideo && image.isVideo && !mSelectedImages.contains(image)) {
                    toast("只能选1个视频");
                    return;
                }
                if (mSelectedImages.get(0).isVideo && !image.isVideo) {
                    toast("图片和视频不能同时选");
                    return;
                }
                if (!mSelectedImages.get(0).isVideo && image.isVideo) {
                    toast("图片和视频不能同时选");
                    return;
                }
            }

            select(image);

            if (onItemClickListener != null) {
                onItemClickListener.getImagePosition(position);
            }
        });
    }

    public String durationFormat(int timeMs) {
        StringBuffer mFormatBuilder = new StringBuffer();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public class CameraHolder extends RecyclerView.ViewHolder {

        public CameraHolder(View itemView) {
            super(itemView);
        }
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tvDuration;
        ImageView indicator;
        TextView tvSign;
        View mask;

        public ImageHolder(View view) {
            super(view);
            tvSign = view.findViewById(R.id.tvSign);
            tvDuration = view.findViewById(R.id.tvDuration);
            image = view.findViewById(R.id.image);
            mask = view.findViewById(R.id.mask);
            indicator = view.findViewById(R.id.checkmark);
        }
    }

    public interface OnItemClickListener {

        void getImagePosition(int position);
    }

    private void toast(String title) {
        mToast.setText(title);
        mToast.show();
    }

}
