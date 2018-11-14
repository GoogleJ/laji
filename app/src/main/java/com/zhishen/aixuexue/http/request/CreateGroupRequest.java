package com.zhishen.aixuexue.http.request;

import com.zhishen.aixuexue.http.BaseAesRequestData;

import java.util.List;

/**
 * Created by yangfaming on 2018/7/17.
 */

public class CreateGroupRequest extends BaseAesRequestData {
    private String name;
    private String creator;
    private String descri;
    private String imgurl;
    private String version;
    private String type;
    private String group_type;
    private String lon;
    private String lat;
    private List<String> userList;

    public void setName(String name) {
        this.name = name;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDescri(String descri) {
        this.descri = descri;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGroup_type(String group_type) {
        this.group_type = group_type;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
