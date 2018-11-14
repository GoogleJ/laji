package com.zhishen.aixuexue.activity.fragment.worldfragment.multi.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.entity
 * describe：for PictureSelector media entity.
 * email：893855882@qq.com
 * data：2017/5/24
 */

public class LocalMedia implements Parcelable {
    public String path;
    public String name;
    public int duration;
    public int mimeType;
    public String pictureType;
    public int width;
    public int height;
    public int index;

    public boolean isChecked;
    public boolean isSelectVideo;//是否选中Video
    public boolean isVideo;

    public LocalMedia(String path) {
        this.path = path;
    }

    public LocalMedia(String path, String pictureType, int w, int h, int duration) {
        this.path = path;
        this.pictureType = pictureType;
        this.width = w;
        this.height = h;
        this.duration = duration;
        this.isVideo = duration == 0?false:true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeInt(this.duration);
        dest.writeInt(this.mimeType);
        dest.writeString(this.pictureType);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.index);
        dest.writeByte(this.isChecked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelectVideo ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
    }

    public LocalMedia() {
    }

    protected LocalMedia(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.duration = in.readInt();
        this.mimeType = in.readInt();
        this.pictureType = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.index = in.readInt();
        this.isChecked = in.readByte() != 0;
        this.isSelectVideo = in.readByte() != 0;
        this.isVideo = in.readByte() != 0;
    }

    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {
        @Override
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }

        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }
    };
}
