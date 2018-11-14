package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome on 2018/7/5
 */
public class DiscountBean implements Parcelable {

    /**
     * id : 0001
     * type : openWeb
     * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_person.png
     * activityImageUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180704-165748.png
     * title : 山西智深教育开业优惠
     * url : https://www.baidu.com
     */

    private String id;
    private String type;
    private String headUrl;
    private String activityImageUrl;
    private String title;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getActivityImageUrl() {
        return activityImageUrl;
    }

    public void setActivityImageUrl(String activityImageUrl) {
        this.activityImageUrl = activityImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.type);
        dest.writeString(this.headUrl);
        dest.writeString(this.activityImageUrl);
        dest.writeString(this.title);
        dest.writeString(this.url);
    }

    public DiscountBean() {
    }

    protected DiscountBean(Parcel in) {
        this.id = in.readString();
        this.type = in.readString();
        this.headUrl = in.readString();
        this.activityImageUrl = in.readString();
        this.title = in.readString();
        this.url = in.readString();
    }

    public static final Creator<DiscountBean> CREATOR = new Creator<DiscountBean>() {
        @Override
        public DiscountBean createFromParcel(Parcel source) {
            return new DiscountBean(source);
        }

        @Override
        public DiscountBean[] newArray(int size) {
            return new DiscountBean[size];
        }
    };
}
