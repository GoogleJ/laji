package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerome on 2018/7/28
 */
public class PraiseBean implements Parcelable {

    /**
     * pid : 847
     * praises : [{"pid":"834","zid":"798","userId":"70001068","avatar":null,"nickname":"missdanni","time":"2018-07-28 15:49:51"},{"pid":"847","zid":"798","userId":"70001149","avatar":"http://zs-3-km.oss-cn-beijing.aliyuncs.com/1532246962051178.png","nickname":"12345678911","time":"2018-07-28 18:03:59"}]
     * code : 1
     */

    private int pid;
    private List<PraisesBean> praises;

    public int getPid() {
        return pid;
    }

    public List<PraisesBean> getPraises() {
        return praises;
    }

    public static class PraisesBean {
        /**
         * pid : 834
         * zid : 798
         * userId : 70001068
         * avatar : null
         * nickname : missdanni
         * time : 2018-07-28 15:49:51
         */

        private String pid;
        private String zid;
        private String userId;
        private Object avatar;
        private String nickname;
        private String time;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getZid() {
            return zid;
        }

        public void setZid(String zid) {
            this.zid = zid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Object getAvatar() {
            return avatar;
        }

        public void setAvatar(Object avatar) {
            this.avatar = avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pid);
        dest.writeList(this.praises);
    }

    public PraiseBean() {
    }

    protected PraiseBean(Parcel in) {
        this.pid = in.readInt();
        this.praises = new ArrayList<PraisesBean>();
        in.readList(this.praises, PraisesBean.class.getClassLoader());
    }

    public static final Creator<PraiseBean> CREATOR = new Creator<PraiseBean>() {
        @Override
        public PraiseBean createFromParcel(Parcel source) {
            return new PraiseBean(source);
        }

        @Override
        public PraiseBean[] newArray(int size) {
            return new PraiseBean[size];
        }
    };
}
