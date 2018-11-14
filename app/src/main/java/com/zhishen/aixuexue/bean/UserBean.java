package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome on 2018/7/10
 */
public class UserBean implements Parcelable {
    //朋友圈评论相关数据结构
    private String userId;
    private String userName;

    public UserBean(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    //个人信息相关数据结构
    private String isDelete;  //删除标记(0.正常1.删除)
    private int role;
    private String avatar;
    private String usernick;
    private int isTalking;  //0正常1禁言
    private int isAdmin; //是否管理员
    private int isOnline;//是否在线
    private int systemId; //系统ID
    private int contDays;//连续签到时间
    private String password;
    private int meetingAuth;
    private int integralSum;
    private int ensFxid;
    private String tel;
    private int ensTel;
    private double lat;  //经度
    private double lng;
    private int fatherId;
    private String hxpassword;
    private int activityAuth;
    private String loginTimes;
    private int lastloginTimes;
    private int lastSigninTime;
    private String createTimes;
    private int enShowTel;
    private int groupId;
    private int time;
    private String city;
    private String sign;
    private String sex;

    public String getIsDelete() {
        return isDelete;
    }

    public int getRole() {
        return role;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsernick() {
        return usernick;
    }

    public int getIsTalking() {
        return isTalking;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public int getSystemId() {
        return systemId;
    }

    public int getContDays() {
        return contDays;
    }

    public String getPassword() {
        return password;
    }

    public int getMeetingAuth() {
        return meetingAuth;
    }

    public int getIntegralSum() {
        return integralSum;
    }

    public int getEnsFxid() {
        return ensFxid;
    }

    public String getTel() {
        return tel;
    }

    public int getEnsTel() {
        return ensTel;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getFatherId() {
        return fatherId;
    }

    public String getHxpassword() {
        return hxpassword;
    }

    public int getActivityAuth() {
        return activityAuth;
    }

    public String getLoginTimes() {
        return loginTimes;
    }

    public int getLastloginTimes() {
        return lastloginTimes;
    }

    public int getLastSigninTime() {
        return lastSigninTime;
    }

    public String getCreateTimes() {
        return createTimes;
    }

    public int getEnShowTel() {
        return enShowTel;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getTime() {
        return time;
    }

    public String getCity() {
        return city;
    }

    public String getSign() {
        return sign;
    }

    public String getSex() {
        return sex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.isDelete);
        dest.writeInt(this.role);
        dest.writeString(this.avatar);
        dest.writeString(this.usernick);
        dest.writeInt(this.isTalking);
        dest.writeInt(this.isAdmin);
        dest.writeInt(this.isOnline);
        dest.writeInt(this.systemId);
        dest.writeInt(this.contDays);
        dest.writeString(this.password);
        dest.writeInt(this.meetingAuth);
        dest.writeInt(this.integralSum);
        dest.writeInt(this.ensFxid);
        dest.writeString(this.tel);
        dest.writeInt(this.ensTel);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.fatherId);
        dest.writeString(this.hxpassword);
        dest.writeInt(this.activityAuth);
        dest.writeString(this.loginTimes);
        dest.writeInt(this.lastloginTimes);
        dest.writeInt(this.lastSigninTime);
        dest.writeString(this.createTimes);
        dest.writeInt(this.enShowTel);
        dest.writeInt(this.groupId);
        dest.writeInt(this.time);
        dest.writeString(this.city);
        dest.writeString(this.sign);
        dest.writeString(this.sex);
    }

    protected UserBean(Parcel in) {
        this.userId = in.readString();
        this.userName = in.readString();
        this.isDelete = in.readString();
        this.role = in.readInt();
        this.avatar = in.readString();
        this.usernick = in.readString();
        this.isTalking = in.readInt();
        this.isAdmin = in.readInt();
        this.isOnline = in.readInt();
        this.systemId = in.readInt();
        this.contDays = in.readInt();
        this.password = in.readString();
        this.meetingAuth = in.readInt();
        this.integralSum = in.readInt();
        this.ensFxid = in.readInt();
        this.tel = in.readString();
        this.ensTel = in.readInt();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.fatherId = in.readInt();
        this.hxpassword = in.readString();
        this.activityAuth = in.readInt();
        this.loginTimes = in.readString();
        this.lastloginTimes = in.readInt();
        this.lastSigninTime = in.readInt();
        this.createTimes = in.readString();
        this.enShowTel = in.readInt();
        this.groupId = in.readInt();
        this.time = in.readInt();
        this.city = in.readString();
        this.sign = in.readString();
        this.sex = in.readString();
    }

    public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
