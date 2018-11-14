package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class AddReportRequest extends BaseAesRequestData {
    private String formUserid;
    private String informId;
    private String informType;
    private String informContentOne;
    private String informContentTwo;
    private String informImgOne;
    private String informImgTwo;
    private String informImgThree;
    private String operateType;

    public void setInformId(String informId) {
        this.informId = informId;
    }

    public void setInformType(String informType) {
        this.informType = informType;
    }

    public void setInformContentOne(String informContentOne) {
        this.informContentOne = informContentOne;
    }

    public void setInformImgOne(String informImgOne) {
        this.informImgOne = informImgOne;
    }

    public void setInformImgTwo(String informImgTwo) {
        this.informImgTwo = informImgTwo;
    }

    public void setInformImgThree(String informImgThree) {
        this.informImgThree = informImgThree;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public void setFormUserid(String formUserid) {
        this.formUserid = formUserid;
    }

    public void setInformContentTwo(String informContentTwo) {
        this.informContentTwo = informContentTwo;
    }
}
