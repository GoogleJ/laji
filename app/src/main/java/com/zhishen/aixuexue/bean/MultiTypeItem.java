package com.zhishen.aixuexue.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Jerome on 2018/6/27
 */
public class MultiTypeItem<T> implements MultiItemEntity {

    public static final int HOME_HEADER_MENU = 10006;
    public static final int HOME_HEADER_NOTICE = 10007;
    public static final int HOME_NEWS_LEFT = 10008;
    public static final int HOME_NEWS_IMG = 10009;
    public static final int HOME_NEWS_BOTTOM_IMGS = 20001;
    public static final int HOME_NEWS_VIDEO = 20002;
    public static final int HOME_NEWS_TXT = 20003;

    public static final int NEWS_DETAIL_HEADER = 20004;
    public static final int NEWS_DETAIL_COMMENT = 20005;

    public static final int MINE_USER_INFO = 30001;
    public static final int MINE_USER_MENU = 30002;
    public static final int MINE_USER_COURSE = 30003;
    public static final int MINE_USER_TOOLS = 30004;

    public static final int WORLD_TEXT_TYPE = 40001;
    public static final int WORLD_TXTIMG_TYPE = 40003;
    public static final int WORLD_VIDEO_TYPE  = 40004;
    public static final int WORLD_MIN_VIDEO_TYPE = 40005;

    private int itemType;
    private T data;

    public MultiTypeItem(int itemType, T data) {
        this.itemType = itemType;
        this.data = data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public T getData() {
        return data;
    }

}