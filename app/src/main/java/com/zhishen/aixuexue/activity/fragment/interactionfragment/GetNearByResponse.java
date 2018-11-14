package com.zhishen.aixuexue.activity.fragment.interactionfragment;

public class GetNearByResponse {
    private String id;
    private String title;
    private String headUrl;//头像地址 在地图、底部window展示
    private String iconUrl;//icon图片地址（设计图机构列表视图 头像右下角小图标）
    private String desc; //描述信息 用户返回签名  机构返回地址（参考设计图）
    private double distance;//距离 单位：米
    private double lat; //纬度
    private double lon; //经度
    private String customerServiceId;
    private String tel; //电话号？暂时没有用到的地方
    private String sex; //性别 暂时没有用到的地方

    public String getServiceMobile() {
        return customerServiceId;
    }

    public void setServiceMobile(String serviceMobile) {
        this.customerServiceId = serviceMobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
