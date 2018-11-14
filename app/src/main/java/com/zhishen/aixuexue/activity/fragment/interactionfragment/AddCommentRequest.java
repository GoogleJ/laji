package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseAesRequestData;

public class AddCommentRequest extends BaseAesRequestData{
    private String targetId;
    private String fromId;
    private String targetType;
    private String content;
    private int score;
    private int status;

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setTargeType(String targetType) {
        this.targetType = targetType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
