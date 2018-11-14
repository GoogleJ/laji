package com.zhishen.aixuexue.activity.fragment.findfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class GetGroupRequest extends BaseAesRequestData {
    private int page;
    private String type;

    public GetGroupRequest(String type) {
        this.type = type;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setType(String type) {
        this.type = type;
    }
}
