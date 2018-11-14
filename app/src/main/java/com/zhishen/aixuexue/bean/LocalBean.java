package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.core.PoiItem;

/**
 * Created by Jerome on 2018/7/19
 */
public class LocalBean implements Parcelable {

    private boolean isSelected;
    private PoiItem poiItem;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public PoiItem getPoiItem() {
        return poiItem;
    }

    public void setPoiItem(PoiItem poiItem) {
        this.poiItem = poiItem;
    }


    public LocalBean(PoiItem poiItem) {
        this.poiItem = poiItem;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.poiItem, flags);
    }

    public LocalBean() {
    }

    public LocalBean(Parcel in) {
        this.isSelected = in.readByte() != 0;
        this.poiItem = in.readParcelable(PoiItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<LocalBean> CREATOR = new Parcelable.Creator<LocalBean>() {
        @Override
        public LocalBean createFromParcel(Parcel source) {
            return new LocalBean(source);
        }

        @Override
        public LocalBean[] newArray(int size) {
            return new LocalBean[size];
        }
    };
}
