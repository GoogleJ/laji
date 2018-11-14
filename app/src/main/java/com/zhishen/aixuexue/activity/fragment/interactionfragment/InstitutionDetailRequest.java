package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class InstitutionDetailRequest extends BaseAesRequestData{
    private String id;
    private String customerId;

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
