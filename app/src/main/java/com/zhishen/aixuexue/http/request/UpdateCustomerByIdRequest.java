package com.zhishen.aixuexue.http.request;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class UpdateCustomerByIdRequest extends BaseAesRequestData{

    private long userId;
    private String city;
    private String usernick;
    private String tel;
    private String sex;
    private String sign;
    private String avatar;

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUsernick(String usernick) {
        this.usernick = usernick;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
