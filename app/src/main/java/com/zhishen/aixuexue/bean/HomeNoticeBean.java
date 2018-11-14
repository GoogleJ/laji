package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/16
 */
public class HomeNoticeBean implements Parcelable {

    private PageBean page;
    private String image; //notice左边的图片
    private List<NoticeBean> list;

    public PageBean getPage() {
        return page;
    }

    public String getImage() {
        return image;
    }

    public List<NoticeBean> getList() {
        return list;
    }

    public static class NoticeBean implements Serializable {
        private String id;
        private String title;
        private String typeid;
        private String date;
        private String source;
        private String ico;
        private String desc;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getTypeid() {
            return typeid;
        }

        public String getDate() {
            return date;
        }

        public String getSource() {
            return source;
        }

        public String getIco() {
            return ico;
        }

        public String getDesc() {
            return desc;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.image);
        dest.writeList(this.list);
    }

    public HomeNoticeBean() {
    }

    protected HomeNoticeBean(Parcel in) {
        this.page = in.readParcelable(PageBean.class.getClassLoader());
        this.image = in.readString();
        this.list = new ArrayList<NoticeBean>();
        in.readList(this.list, NoticeBean.class.getClassLoader());
    }

    public static final Creator<HomeNoticeBean> CREATOR = new Creator<HomeNoticeBean>() {
        @Override
        public HomeNoticeBean createFromParcel(Parcel source) {
            return new HomeNoticeBean(source);
        }

        @Override
        public HomeNoticeBean[] newArray(int size) {
            return new HomeNoticeBean[size];
        }
    };
}
