package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhishen.aixuexue.activity.fragment.worldfragment.ImagePreviewActivity;
import com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean.LocalMedia;
import com.zhishen.aixuexue.weight.layout.ImageInfo;
import com.zhishen.aixuexue.weight.layout.NineGridView;
import com.zhishen.aixuexue.weight.layout.NineGridViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/27
 */
public class NineGridViewClickAdapter extends NineGridViewAdapter {

    private int statusHeight;
    private GridItemClickListener gridItemClickListener;

    public NineGridViewClickAdapter(Context context, List<ImageInfo> imageInfo) {
        super(context, imageInfo);
        statusHeight = getStatusHeight(context);
    }

    public void setGridItemClickListener(GridItemClickListener gridItemClickListener) {
        if (gridItemClickListener == null) {
            throw new NullPointerException("listener is null");
        }
        this.gridItemClickListener = gridItemClickListener;
    }

    @Override
    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List<ImageInfo> imageInfo) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < imageInfo.size(); i++) {
            ImageInfo info = imageInfo.get(i);
            View imageView;
            if (i < nineGridView.getMaxSize()) {
                imageView = nineGridView.getChildAt(i);
            } else {
                //如果图片的数量大于显示的数量，则超过部分的返回动画统一退回到最后一个图片的位置
                imageView = nineGridView.getChildAt(nineGridView.getMaxSize() - 1);
            }
            info.imageViewWidth = imageView.getWidth();
            info.imageViewHeight = imageView.getHeight();
            int[] points = new int[2];
            imageView.getLocationInWindow(points);
            info.imageViewX = points[0];
            info.imageViewY = points[1] - statusHeight;
            list.add(info.bigImageUrl);
        }

        ArrayList<LocalMedia> localMedias = new ArrayList<>();
        for (String path : list) {
            localMedias.add(new LocalMedia(path));
        }

        if (!list.isEmpty()) {
            Intent intent = new Intent(context, ImagePreviewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ImagePreviewActivity.PREVIEW_LIST, new ArrayList<>(localMedias));
            bundle.putInt(ImagePreviewActivity.START_ITEM_POSITION, 0);
            bundle.putInt(ImagePreviewActivity.START_IAMGE_POSITION, index);
            intent.putExtras(bundle);
            context.startActivity(intent);
        }
    }

    public int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    public interface GridItemClickListener {

        void getPositionImage(int index, List<String> list);
    }
}
