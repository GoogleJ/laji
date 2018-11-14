package com.zhishen.aixuexue.http.response;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/24.
 */

public class GroupResponse {

    /**
     * gid : 54
     * name : 刚刚股份方法
     * creator : 70001019
     * descri : 很尴尬vv
     * imgurl : http://zs-3-km.oss-cn-beijing.aliyuncs.com/05BA6475-70F7-439E-A582-2F8DC0C46440.png
     * create_date :
     * version : 1.1
     * type : GROUPTYPE002
     * group_type : 1
     * lon : 108.900251
     * lat : 34.235533
     * userList : ["70001045","70001121","70001003","70001019"]
     * userCount : 0
     */

    private int gid;
    private String name;
    private String creator;
    private String descri;
    private String imgurl;
    private String create_date;
    private String version;
    private String type;
    private String group_type;
    private double lon;
    private double lat;
    private int userCount;
    private List<String> userList;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescri() {
        return descri;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup_type() {
        return group_type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
