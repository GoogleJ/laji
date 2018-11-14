package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.fragment.homefragment.NewsWebActivity;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
import com.zhishen.aixuexue.bean.MultiTypeItem;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.manager.PreferenceManager;
import com.zhishen.aixuexue.util.BitmapUtil;
import com.zhishen.aixuexue.weight.NoScrollGridView;
import com.zhishen.aixuexue.weight.VerticalScrollLayout;
import com.zhishen.aixuexue.weight.banner.MZBannerView;
import com.zhishen.aixuexue.weight.banner.holder.MZHolderCreator;
import com.zhishen.aixuexue.weight.banner.holder.MZViewHolder;
import com.zhishen.aixuexue.weight.layout.NineGridView;

import java.util.List;

/**
 * Created by Jerome on 2018/6/27
 */
public class HomeTypeAdapter extends BaseMultiItemQuickAdapter<MultiTypeItem, BaseViewHolder> {

    private MenuAdapter mMenuAdapter = null;
    private NoticeAdapter mNoticeAdapter = null;
    private MenuOnItemClickListener menuOnItemClickListener;

    public HomeTypeAdapter() {
        super(null);
        addItemType(MultiTypeItem.HOME_HEADER_MENU, R.layout.view_home_header_banner);
        addItemType(MultiTypeItem.HOME_HEADER_NOTICE, R.layout.view_home_header_notice);
        addItemType(MultiTypeItem.HOME_NEWS_LEFT, R.layout.view_home_news_left_img);
        addItemType(MultiTypeItem.HOME_NEWS_IMG, R.layout.view_home_news_img_video);
        addItemType(MultiTypeItem.HOME_NEWS_BOTTOM_IMGS, R.layout.view_home_news_bottom_imgs);
        addItemType(MultiTypeItem.HOME_NEWS_TXT, R.layout.view_home_news_txt);
        addItemType(MultiTypeItem.HOME_NEWS_VIDEO, R.layout.view_home_news_img_video);
    }

    public void setMenuOnItemClickListener(MenuOnItemClickListener menuOnItemClickListener) {
        if (menuOnItemClickListener == null) {
            throw new NullPointerException("menu listener is null");
        }
        this.menuOnItemClickListener = menuOnItemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiTypeItem item) {
        switch (helper.getItemViewType()) {
            case MultiTypeItem.HOME_HEADER_MENU: {  //Banner菜单
                HomeBean bean = (HomeBean) item.getData();
                if (bean == null) return;
                NoScrollGridView gvMenu = helper.getView(R.id.gvMenu);
                MZBannerView  banner =  helper.getView(R.id.banner);
                banner.setPages(bean.getHomeBanner(), (MZHolderCreator<BannerViewHolder>) () -> new BannerViewHolder());
                banner.setBannerPageClickListener((view, position) -> {
                    List<HomeBean.BannerBean> list = bean.getHomeBanner();
                    if (null != list &&!list.isEmpty()) {
                        Bundle bundle = new Bundle();
                        Intent intent = new Intent(mContext, NewsWebActivity.class);
                        bundle.putString(NewsWebActivity.NEWS_TITLE, list.get(position).getDesc());
                        bundle.putInt(NewsWebActivity.NEWS_TYPE, NewsWebActivity.NEWS_WEB_URL);
                        bundle.putSerializable(NewsWebActivity.NEWS_OBJECT, list.get(position));
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                });
                banner.setIndicatorVisible(false);

                gvMenu.setAdapter(mMenuAdapter = new MenuAdapter(mContext));
                mMenuAdapter.addData(bean.getHomeMenu());
                gvMenu.setOnItemClickListener((parent, view, position, id) -> {
                    if (menuOnItemClickListener != null) {
                        String itemId = mMenuAdapter.getItem(position).getId();
                        menuOnItemClickListener.getMenuPosition(position, itemId, mMenuAdapter.getItem(position));
                    }
                });
            }
            break;
            case MultiTypeItem.HOME_HEADER_NOTICE: {  //通知
                HomeNoticeBean notice = (HomeNoticeBean) item.getData();
                if (notice == null) return;
                ImageView ivAvatar = helper.getView(R.id.ivAvatar);
                LinearLayout llContent = helper.getView(R.id.llContent);
                helper.setGone(R.id.ivTips, !PreferenceManager.getInstance().isReadNotify());

                if (notice.getList() != null && !notice.getList().isEmpty()) {
                    llContent.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(notice.getImage())) {
                        ivAvatar.setImageResource(R.drawable.default_image);
                    } else {
                        BitmapUtil.loadNormalImg(ivAvatar, notice.getImage(), R.drawable.default_image);
                    }
                    VerticalScrollLayout scrollLayout = helper.getView(R.id.scrollLayout);
                    scrollLayout.setAdapter(mNoticeAdapter = new NoticeAdapter(mContext));
                    mNoticeAdapter.addData(notice.getList());

                    helper.addOnClickListener(R.id.scrollLayout);
                } else {
                    llContent.setVisibility(View.GONE);
                }
            }
            break;
            // 新闻区
            case MultiTypeItem.HOME_NEWS_TXT:{
                NewsBean txtNews = (NewsBean) item.getData();
                if (txtNews == null) return;
                showCommentArea(helper, txtNews);
            }
            break;
            case MultiTypeItem.HOME_NEWS_LEFT: {
                NewsBean leftNews = (NewsBean) item.getData();
                if (leftNews == null) return;
                ImageView ivAvatar = helper.getView(R.id.ivAvatar);
                if (leftNews.getImages() != null && !leftNews.getImages().isEmpty()) {
                    if (TextUtils.isEmpty(leftNews.getImages().get(0))) {
                        ivAvatar.setImageResource(R.drawable.default_image);
                    } else {
                        BitmapUtil.loadCornerImg(ivAvatar, leftNews.getImages().get(0), R.drawable.default_image, 5);
                    }
                }
                showCommentArea(helper, leftNews);
            }
            break;
            case MultiTypeItem.HOME_NEWS_IMG:
                helper.setGone(R.id.ivPlay, false);
                NewsBean btoNews = (NewsBean) item.getData();
                if (btoNews == null) return;
                ImageView ivAvatar = helper.getView(R.id.ivAvatar);
                if (btoNews.getImages() != null && !btoNews.getImages().isEmpty()) {
                    if (TextUtils.isEmpty(btoNews.getImages().get(0))) {
                        ivAvatar.setImageResource(R.drawable.default_image);
                    } else {
                        BitmapUtil.loadCornerImg(ivAvatar, btoNews.getImages().get(0), R.drawable.default_image, 5);
                    }
                }
                showCommentArea(helper, btoNews);
                break;
            case MultiTypeItem.HOME_NEWS_BOTTOM_IMGS: {
                NewsBean homeNews = (NewsBean) item.getData();
                if (homeNews == null) return;
                NineGridView nineGridLayout = helper.getView(R.id.nineGridLayout);
                nineGridLayout.setAdapter(new NineGridViewClickAdapter(mContext,homeNews.getImageList()));
                NineGridView.setImageLoader(new GlideImageLoader());

                showCommentArea(helper, homeNews);
            }
            break;
            case MultiTypeItem.HOME_NEWS_VIDEO: {
                NewsBean videoNews = (NewsBean) item.getData();
                if (videoNews == null) return;
                ImageView videoImg = helper.getView(R.id.ivAvatar);
                if (videoNews.getImages() != null && !videoNews.getImages().isEmpty()) {
                    if (TextUtils.isEmpty(videoNews.getImages().get(0))) {
                        videoImg.setImageResource(R.drawable.default_image);
                    } else {
                        BitmapUtil.loadCornerImg(videoImg, videoNews.getImages().get(0), R.drawable.default_image, 5);
                    }
                }
                helper.addOnClickListener(R.id.ivPlay).setGone(R.id.ivPlay, true);
                showCommentArea(helper, videoNews);
            }
            break;
            default:
                break;
        }
    }

    public class BannerViewHolder implements MZViewHolder<HomeBean.BannerBean> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            mImageView =  view.findViewById(R.id.ivAvatar);
            return view;
        }

        @Override
        public void onBind(Context context, int position, HomeBean.BannerBean data) {
            BitmapUtil.loadCornerImg(mImageView, data.getImage(),R.drawable.default_image,5);
        }
    }

    private void showCommentArea(BaseViewHolder helper, NewsBean homeNews) {
        helper.setText(R.id.tvTitle, homeNews.getTitle())
                .setText(R.id.tvSource, TextUtils.isEmpty(homeNews.getSource())?"暂无来源":homeNews.getSource())
                .setText(R.id.tvTime, homeNews.getDate()).setText(R.id.tvComments, homeNews.getComments());
    }

    public interface MenuOnItemClickListener {

        void getMenuPosition(int position, String itemId, HomeBean.Menu title);
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
