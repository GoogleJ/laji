package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jerome on 2018/6/30
 */
public class FollowBean implements Parcelable {

    /**
     * id : 1231231
     * headUrl : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_teacher.png
     * ico : http://192.168.10.8:8888/MAMP/zmhphp/education/image/map_point_badge_teacher.png
     * longitude : null
     * latitude : null
     * distance : 1.2KM
     * name : 老师1
     * sex : 男
     * course : ["语文","数学","英语"]
     * summary : 1黑格伯爵提供更优秀的数字化...
     * address : 陕西省西安市雁塔区高新二路14号i创途众创公园inno park205
     * focus : true
     */
    private String id;
    private String headUrl;
    private String ico;
    private String longitude;
    private String latitude;
    private String distance;
    private String name;
    private String sex;
    private String summary;
    private String address;
    private boolean focus;
    private List<String> course;

    public String getId() {
        return id;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getIco() {
        return ico;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public String getSex() {
        return sex;
    }

    public String getSummary() {
        return summary;
    }

    public String getAddress() {
        return address;
    }

    public boolean isFocus() {
        return focus;
    }

    public List<String> getCourse() {
        return course;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.headUrl);
        dest.writeString(this.ico);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeString(this.distance);
        dest.writeString(this.name);
        dest.writeString(this.sex);
        dest.writeString(this.summary);
        dest.writeString(this.address);
        dest.writeByte(this.focus ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.course);
    }

    public FollowBean() {
    }

    protected FollowBean(Parcel in) {
        this.id = in.readString();
        this.headUrl = in.readString();
        this.ico = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.distance = in.readString();
        this.name = in.readString();
        this.sex = in.readString();
        this.summary = in.readString();
        this.address = in.readString();
        this.focus = in.readByte() != 0;
        this.course = in.createStringArrayList();
    }

    public static final Parcelable.Creator<FollowBean> CREATOR = new Parcelable.Creator<FollowBean>() {
        @Override
        public FollowBean createFromParcel(Parcel source) {
            return new FollowBean(source);
        }

        @Override
        public FollowBean[] newArray(int size) {
            return new FollowBean[size];
        }
    };
}
