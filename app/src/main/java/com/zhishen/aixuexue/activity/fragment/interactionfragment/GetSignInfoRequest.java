package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class GetSignInfoRequest extends BaseAesRequestData{
    private String userId;

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
