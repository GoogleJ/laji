package com.zhishen.aixuexue.activity.fragment.worldfragment.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.homefragment.adapter.NineGridViewClickAdapter;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.WorldBean;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.weight.comment.CommentsView;
import com.zhishen.aixuexue.weight.layout.NineGridView;

/**
 * Created by Jerome on 2018/7/10
 */
public class WorldTypeAdapter extends BaseMultiItemQuickAdapter<MultiTypeItem, BaseViewHolder> {


    public WorldTypeAdapter() {
        super(null);
        addItemType(MultiTypeItem.WORLD_TEXT_TYPE, R.layout.view_world_txt);
        addItemType(MultiTypeItem.WORLD_TXTIMG_TYPE, R.layout.view_world_imgs);
        addItemType(MultiTypeItem.WORLD_VIDEO_TYPE, R.layout.view_world_video);
        addItemType(MultiTypeItem.WORLD_MIN_VIDEO_TYPE, R.layout.view_world_min_video);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiTypeItem item) {
        switch (item.getItemType()) {
            case MultiTypeItem.WORLD_TEXT_TYPE: {
                WorldBean worldBean = (WorldBean) item.getData();
                showUserInfo(helper, worldBean);
            }
            break;
            case MultiTypeItem.WORLD_TXTIMG_TYPE: {
                WorldBean worldBean = (WorldBean) item.getData();
                if (worldBean == null) return;
                NineGridView nineLayout = helper.getView(R.id.nineGridLayout);
                nineLayout.setAdapter(new NineGridViewClickAdapter(mContext,worldBean.getImageList()));
                NineGridView.setImageLoader(new GlideImageLoader());

                showUserInfo(helper, worldBean);
            }
            break;
            case MultiTypeItem.WORLD_VIDEO_TYPE: {
                WorldBean worldBean = (WorldBean) item.getData();
                if (worldBean == null) return;
                ImageView ivPhoto = helper.getView(R.id.ivPhoto);

                showUserInfo(helper, worldBean);
                if (worldBean.getImagestr() != null && !worldBean.getImagestr().isEmpty()) {
                    BitmapUtil.loadNormalImg(ivPhoto, worldBean.getImagestr().get(0), R.drawable.default_image);
                }
                helper.addOnClickListener(R.id.ivPlay);
            }
            break;
            case MultiTypeItem.WORLD_MIN_VIDEO_TYPE: {
                WorldBean worldBean = (WorldBean) item.getData();
                if (worldBean == null) return;
                ImageView ivPhoto = helper.getView(R.id.ivPhoto);

                showUserInfo(helper, worldBean);
                if (worldBean.getImagestr() != null && !worldBean.getImagestr().isEmpty()) {
                    BitmapUtil.loadNormalImg(ivPhoto, worldBean.getImagestr().get(0), R.drawable.default_image);
                }

                helper.addOnClickListener(R.id.ivPlay).addOnClickListener(R.id.ivPhoto);
            }
            break;
            default:
                break;
        }
    }

    //通用
    private void showUserInfo(BaseViewHolder helper, WorldBean item) {
        ImageView ivAvatar = helper.getView(R.id.ivAvatar);
        CommentsView commentView = helper.getView(R.id.commentView);
        LinearLayout llContent = helper.getView(R.id.llContent);

        BitmapUtil.loadCircleImg(ivAvatar, item.getAvatar(), R.drawable.default_avatar);
        helper.setText(R.id.tvName, item.getUsernick()).setText(R.id.tvTime, item.getTime())
                .setGone(R.id.tvContent, !TextUtils.isEmpty(item.getContent())).setText(R.id.tvContent, item.getContent())
                .setText(R.id.tvLocal, item.getLocation()).setText(R.id.tvComments, String.valueOf(item.getCommentAmount()))
                .setText(R.id.tvPraise, String.valueOf(item.getPraisesAmount())).addOnClickListener(R.id.tvPraise).addOnClickListener(R.id.tvComments)
                .setGone(R.id.tvMore, item.getComment().size() > 3).addOnClickListener(R.id.tvMore).addOnClickListener(R.id.tvComments);
        if (item.getComment() != null && !item.getComment().isEmpty()) {
            llContent.setVisibility(View.VISIBLE);
            commentView.setList(item.getCombomComments());
            commentView.notifyDataSetChanged();
        } else {
            llContent.setVisibility(View.GONE);
        }
    }

    private class GlideImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            BitmapUtil.loadCornerImg(imageView, url, R.drawable.default_image, 5);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }
}
