package com.zhishen.aixuexue.http.request;

import com.zhishen.aixuexue.http.BaseAesRequestData;

/**
 * Created by yangfaming on 2018/7/14.
 */

public class GroupListRequest extends BaseAesRequestData {
    private String userId;
    private double lat;
    private double lng;

    public GroupListRequest(String userId, double lat, double lng) {
        this.userId = userId;
        this.lat = lat;
        this.lng = lng;
    }
}
