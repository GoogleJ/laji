package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome on 2018/7/14
 */
public class PageBean implements Parcelable {

    /**
     * pageNo : 1
     * pageSize : 15
     * count : 15
     * totalPage : 1
     * limit : 15
     * index : 0
     */

    private int pageNo;
    private int pageSize;
    private int count;
    private int totalPage;
    private int limit;
    private int index;

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCount() {
        return count;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getLimit() {
        return limit;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageNo);
        dest.writeInt(this.pageSize);
        dest.writeInt(this.count);
        dest.writeInt(this.totalPage);
        dest.writeInt(this.limit);
        dest.writeInt(this.index);
    }

    public PageBean() {
    }

    protected PageBean(Parcel in) {
        this.pageNo = in.readInt();
        this.pageSize = in.readInt();
        this.count = in.readInt();
        this.totalPage = in.readInt();
        this.limit = in.readInt();
        this.index = in.readInt();
    }

    public static final Parcelable.Creator<PageBean> CREATOR = new Parcelable.Creator<PageBean>() {
        @Override
        public PageBean createFromParcel(Parcel source) {
            return new PageBean(source);
        }

        @Override
        public PageBean[] newArray(int size) {
            return new PageBean[size];
        }
    };
}
