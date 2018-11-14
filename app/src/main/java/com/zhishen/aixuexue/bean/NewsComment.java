package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Jerome on 2018/7/24
 */
public class NewsComment implements Parcelable {

    private PageBean page;

    public String headUrl = "";
    public String user_name = "";
    public String user_id = "";
    public int score = 0;
    public String content = "";
    public String date = "";

    public List<NewsComment> list;


    public NewsComment(String headUrl, String user_name, String user_id, int score, String content, String date) {
        this.headUrl = headUrl;
        this.user_name = user_name;
        this.user_id = user_id;
        this.score = score;
        this.content = content;
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.page, flags);
        dest.writeString(this.headUrl);
        dest.writeString(this.user_name);
        dest.writeString(this.user_id);
        dest.writeInt(this.score);
        dest.writeString(this.content);
        dest.writeString(this.date);
        dest.writeTypedList(this.list);
    }

    public NewsComment() {
    }

    protected NewsComment(Parcel in) {
        this.page = in.readParcelable(PageBean.class.getClassLoader());
        this.headUrl = in.readString();
        this.user_name = in.readString();
        this.user_id = in.readString();
        this.score = in.readInt();
        this.content = in.readString();
        this.date = in.readString();
        this.list = in.createTypedArrayList(NewsComment.CREATOR);
    }

    public static final Creator<NewsComment> CREATOR = new Creator<NewsComment>() {
        @Override
        public NewsComment createFromParcel(Parcel source) {
            return new NewsComment(source);
        }

        @Override
        public NewsComment[] newArray(int size) {
            return new NewsComment[size];
        }
    };
}
