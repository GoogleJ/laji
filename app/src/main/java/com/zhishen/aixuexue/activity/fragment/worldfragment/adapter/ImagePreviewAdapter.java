package com.zhishen.aixuexue.activity.fragment.worldfragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;
import com.zhishen.aixuexue.util.BitmapUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Jerome on 2018/7/19
 */
public class ImagePreviewAdapter extends PagerAdapter {

    private Context context;
    private List<LocalMedia> imageList;
    private int itemPosition;
    private PhotoView photoView;
    public ImagePreviewAdapter(Context context, List<LocalMedia> imageList, int itemPosition) {
        this.context = context;
        this.imageList = imageList;
        this.itemPosition = itemPosition;
    }

    @Override
    public int getCount() {
        return imageList==null?0:imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final PhotoView image = new PhotoView(context);
        image.setEnabled(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setMaximumScale(2.0F);
        image.setMinimumScale(0.8F);
        String path = imageList.get(position).path;
        if (new File(path).isFile()) {//本地图片走这里
            Glide.with(image.getContext()).load(path).into(image);
        } else {//网络图片走这里
            BitmapUtil.loadImg(image, path);
        }
        image.setOnClickListener(v -> {
            image.setEnabled(false);
            ((Activity)context).onBackPressed();
        });
        container.addView(image);
        return image;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        photoView = (PhotoView) object;
        //photoView.setTag(Utils.getNameByPosition(itemPosition,position));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // photoView.setTransitionName(Utils.getNameByPosition(itemPosition,position));
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public PhotoView getPhotoView() {
        return photoView;
    }
}
