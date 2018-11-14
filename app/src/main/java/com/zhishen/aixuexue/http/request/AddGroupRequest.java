package com.zhishen.aixuexue.http.request;

import com.zhishen.aixuexue.http.BaseAesRequestData;

/**
 * Created by yangfaming on 2018/7/17.
 */

public class AddGroupRequest extends BaseAesRequestData {
    private int gid;
    private String uid;

    public AddGroupRequest(int gid, String uid) {
        this.gid = gid;
        this.uid = uid;
    }
}
