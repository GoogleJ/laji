package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerome on 2018/7/10
 */
public class CommonComments implements Parcelable {

    private String content;
    private int commentsId;
    private UserBean replyUser; // 回复人信息
    private UserBean commentsUser;  // 评论人信息

    public CommonComments(String content, int commentsId, UserBean replyUser, UserBean commentsUser) {
        this.content = content;
        this.commentsId = commentsId;
        this.replyUser = replyUser;
        this.commentsUser = commentsUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCommentsId() {
        return commentsId;
    }

    public void setCommentsId(int commentsId) {
        this.commentsId = commentsId;
    }

    public UserBean getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(UserBean replyUser) {
        this.replyUser = replyUser;
    }

    public UserBean getCommentsUser() {
        return commentsUser;
    }

    public void setCommentsUser(UserBean commentsUser) {
        this.commentsUser = commentsUser;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.commentsId);
        dest.writeParcelable(this.replyUser, flags);
        dest.writeParcelable(this.commentsUser, flags);
    }

    public CommonComments() {
    }

    protected CommonComments(Parcel in) {
        this.content = in.readString();
        this.commentsId = in.readInt();
        this.replyUser = in.readParcelable(UserBean.class.getClassLoader());
        this.commentsUser = in.readParcelable(UserBean.class.getClassLoader());
    }

    public static final Creator<CommonComments> CREATOR = new Creator<CommonComments>() {
        @Override
        public CommonComments createFromParcel(Parcel source) {
            return new CommonComments(source);
        }

        @Override
        public CommonComments[] newArray(int size) {
            return new CommonComments[size];
        }
    };
}
