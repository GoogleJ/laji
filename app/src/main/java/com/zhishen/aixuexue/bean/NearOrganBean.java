package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jerome on 2018/7/5
 */
public class NearOrganBean implements Parcelable {


    /**
     * id : 12312513
     * score : 5
     * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/WX20180704-180109.png
     * ico :
     * tag : ["1对1","小班","资深外教","主题活动"]
     * distance : 1.5km
     * name : 美联数学（旺座中心）
     * summary : 科技路沿线 英语
     */

    private PageBean page;
    private String id;
    private int score;
    private String headUrl;
    private String ico;
    private String distance;
    private String name;
    private String summary;
    private List<String> tag;
    private double lat;
    private double lon;

    private List<NearOrganBean> list;

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getIco() {
        return ico;
    }

    public String getDistance() {
        return distance;
    }

    public PageBean getPage() {
        return page;
    }

    public List<NearOrganBean> getList() {
        return list;
    }

    public String getName() {
        return name;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getTag() {
        return tag;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.id);
        dest.writeInt(this.score);
        dest.writeString(this.headUrl);
        dest.writeString(this.ico);
        dest.writeString(this.distance);
        dest.writeString(this.name);
        dest.writeString(this.summary);
        dest.writeStringList(this.tag);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeTypedList(this.list);
    }

    public NearOrganBean() {
    }

    protected NearOrganBean(Parcel in) {
        this.page = in.readParcelable(PageBean.class.getClassLoader());
        this.id = in.readString();
        this.score = in.readInt();
        this.headUrl = in.readString();
        this.ico = in.readString();
        this.distance = in.readString();
        this.name = in.readString();
        this.summary = in.readString();
        this.tag = in.createStringArrayList();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.list = in.createTypedArrayList(NearOrganBean.CREATOR);
    }

    public static final Creator<NearOrganBean> CREATOR = new Creator<NearOrganBean>() {
        @Override
        public NearOrganBean createFromParcel(Parcel source) {
            return new NearOrganBean(source);
        }

        @Override
        public NearOrganBean[] newArray(int size) {
            return new NearOrganBean[size];
        }
    };
}
