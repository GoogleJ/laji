package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jerome on 2018/6/30
 */
public class CollectBean implements Parcelable {

    /**
     * id : 21
     * actionID : openWeb
     * image : http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180630-144214.png
     * title : Java大牛 带你从0到上线开发企业级电商项目
     * tag : 高级
     * studyCount : 56人学习
     * activityPrice : ¥500
     * originalPrice : 活动价格：￥300
     */
    private PageBean page;
    private String id;
    private String actionID;
    private String image;
    private String title;
    private String tag;
    private String studyCount;
    private String activityPrice;
    private String originalPrice;

    private List<CollectBean> list;


    public PageBean getPage() {
        return page;
    }

    public String getId() {
        return id;
    }

    public String getActionID() {
        return actionID;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public String getStudyCount() {
        return studyCount;
    }

    public String getActivityPrice() {
        return activityPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public List<CollectBean> getList() {
        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.id);
        dest.writeString(this.actionID);
        dest.writeString(this.image);
        dest.writeString(this.title);
        dest.writeString(this.tag);
        dest.writeString(this.studyCount);
        dest.writeString(this.activityPrice);
        dest.writeString(this.originalPrice);
        dest.writeTypedList(this.list);
    }

    public CollectBean() {
    }

    protected CollectBean(Parcel in) {
        this.page = in.readParcelable(PageBean.class.getClassLoader());
        this.id = in.readString();
        this.actionID = in.readString();
        this.image = in.readString();
        this.title = in.readString();
        this.tag = in.readString();
        this.studyCount = in.readString();
        this.activityPrice = in.readString();
        this.originalPrice = in.readString();
        this.list = in.createTypedArrayList(CollectBean.CREATOR);
    }

    public static final Creator<CollectBean> CREATOR = new Creator<CollectBean>() {
        @Override
        public CollectBean createFromParcel(Parcel source) {
            return new CollectBean(source);
        }

        @Override
        public CollectBean[] newArray(int size) {
            return new CollectBean[size];
        }
    };
}
