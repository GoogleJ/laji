package com.zhishen.aixuexue.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.zhishen.aixuexue.weight.layout.ImageInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jerome on 2018/7/11
 */
public class WorldBean implements Parcelable {


    /**
     * id : 535
     * userId : 70001139
     * category : N
     * content : 不知道说啥
     * imagestr : 1531811994423.png,1531811994434.png,1531811994333.png,1531811994631.png
     * coordinate : null
     * location : 0
     * time : 2018-07-17 15:19:54
     * lat : 34.235459
     * lng : 108.900457
     * qeohash : wqj6y06uh81v787c62vtuh
     * avatar : null
     * usernick : 18192828708
     * sign : null
     * hxpassword : 111647
     * praisesAmount : 2
     * commentAmount : 1
     * praises : [{"pid":"767","zid":"535","userId":"70001139","avatar":null,"nickname":"18192828708","time":"2018-07-17 15:22:21"},{"pid":"768","zid":"535","userId":"70001149","avatar":null,"nickname":"12345678911","time":"2018-07-17 17:11:44"}]
     * comment : [{"cid":"654","content":"发个评论试试","zid":"535","userId":"70001139","avatar":null,"nickname":"18192828708","replyContent":null,"replyUid":null,"replyAvatar":null,"replyNickname":null,"time":"2018-07-17 15:21:46","replyTime":""}]
     */

    private String id;
    private String userId;
    private String category;
    private String content;
    private String imagestr;
    private String coordinate;
    private String location;
    private String time;
    private double lat;
    private double lng;
    private String qeohash;
    private String avatar;
    private String usernick;
    private String sign;
    private String hxpassword;
    private int praisesAmount;
    private int commentAmount;
    private List<PraisesBean> praises;
    private List<CommentBean> comment;


    public List<CommonComments> getCombomComments() {
        List<CommonComments> list = new ArrayList<>();
        for (CommentBean bean : comment) {
            CommonComments cbeans = new CommonComments();
            cbeans.setCommentsId(Integer.parseInt(bean.cid));
            cbeans.setContent(bean.content);
            cbeans.setReplyUser(null);
            cbeans.setCommentsUser(new UserBean(bean.userId, bean.nickname));
            list.add(cbeans);
        }
        return list;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public int getCategory() {
        switch (category) {
            case "s0":
                return MultiTypeItem.WORLD_TEXT_TYPE;  //文本
            case "s1":
            case "s2":
                return MultiTypeItem.WORLD_TXTIMG_TYPE; //文本+多图
            case "s3":
                return MultiTypeItem.WORLD_VIDEO_TYPE; //文本+大视频
            case "s4":
                return MultiTypeItem.WORLD_MIN_VIDEO_TYPE; //视频
            case "N": {
                if (!TextUtils.isEmpty(imagestr) && imagestr.contains(",")) {
                    return MultiTypeItem.WORLD_TXTIMG_TYPE;
                }
            }
            break;
        }
        return 0;
    }

    public String getContent() {
        return content;
    }

    public List<String> getImagestr() {
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(imagestr)) {
            if (imagestr.contains(",")) {
                List<String> imgList = Arrays.asList(imagestr.split(","));
                for (String urls : imgList) {
                    list.add("http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + urls);
                }
                return list;
            } else {
                list.add("http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + imagestr);
            }
        }
        return list;
    }

    public List<ImageInfo>getImageList(){
        List<ImageInfo> data = new ArrayList<>();
        if (!TextUtils.isEmpty(imagestr)) {
            if (imagestr.contains(",")) {
                List<String> imgList = Arrays.asList(imagestr.split(","));
                for (String urls : imgList) {
                    data.add(new ImageInfo("http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + urls,
                            "http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + urls));
                }
                return data;
            } else {
                data.add(new ImageInfo("http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + imagestr,
                        "http://zs-3-km.oss-cn-beijing.aliyuncs.com/" + imagestr));
            }
        }
        return data;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getQeohash() {
        return qeohash;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUsernick() {
        return usernick;
    }

    public String getSign() {
        return sign;
    }

    public String getHxpassword() {
        return hxpassword;
    }

    public int getPraisesAmount() {
        return praisesAmount;
    }

    public int getCommentAmount() {
        return commentAmount;
    }

    public List<PraisesBean> getPraises() {
        return praises;
    }

    public List<CommentBean> getComment() {
        return comment;
    }

    public static class PraisesBean implements Parcelable {
        /**
         * pid : 767
         * zid : 535
         * userId : 70001139
         * avatar : null
         * nickname : 18192828708
         * time : 2018-07-17 15:22:21
         */

        private String pid;
        private String zid;
        private String userId;
        private String avatar;
        private String nickname;
        private String time;

        public String getPid() {
            return pid;
        }

        public String getZid() {
            return zid;
        }

        public String getUserId() {
            return userId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public String getTime() {
            return time;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.pid);
            dest.writeString(this.zid);
            dest.writeString(this.userId);
            dest.writeString(this.avatar);
            dest.writeString(this.nickname);
            dest.writeString(this.time);
        }

        public PraisesBean() {
        }

        protected PraisesBean(Parcel in) {
            this.pid = in.readString();
            this.zid = in.readString();
            this.userId = in.readString();
            this.avatar = in.readString();
            this.nickname = in.readString();
            this.time = in.readString();
        }

        public static final Creator<PraisesBean> CREATOR = new Creator<PraisesBean>() {
            @Override
            public PraisesBean createFromParcel(Parcel source) {
                return new PraisesBean(source);
            }

            @Override
            public PraisesBean[] newArray(int size) {
                return new PraisesBean[size];
            }
        };
    }

    public static class CommentBean implements Parcelable {
        /**
         * cid : 654
         * content : 发个评论试试
         * zid : 535
         * userId : 70001139
         * avatar : null
         * nickname : 18192828708
         * replyContent : null
         * replyUid : null
         * replyAvatar : null
         * replyNickname : null
         * time : 2018-07-17 15:21:46
         * replyTime :
         */

        private String cid;
        private String content;
        private String zid;
        private String userId;
        private String avatar;
        private String nickname;
        private String replyContent;
        private String replyUid;
        private String replyAvatar;
        private String replyNickname;
        private String time;
        private String replyTime;


        public String getCid() {
            return cid;
        }

        public String getContent() {
            return content;
        }

        public String getZid() {
            return zid;
        }

        public String getUserId() {
            return userId;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getNickname() {
            return nickname;
        }

        public String getReplyContent() {
            return replyContent;
        }

        public String getReplyUid() {
            return replyUid;
        }

        public String getReplyAvatar() {
            return replyAvatar;
        }

        public String getReplyNickname() {
            return replyNickname;
        }

        public String getTime() {
            return time;
        }

        public String getReplyTime() {
            return replyTime;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.cid);
            dest.writeString(this.content);
            dest.writeString(this.zid);
            dest.writeString(this.userId);
            dest.writeString(this.avatar);
            dest.writeString(this.nickname);
            dest.writeString(this.replyContent);
            dest.writeString(this.replyUid);
            dest.writeString(this.replyAvatar);
            dest.writeString(this.replyNickname);
            dest.writeString(this.time);
            dest.writeString(this.replyTime);
        }

        public CommentBean() {
        }

        protected CommentBean(Parcel in) {
            this.cid = in.readString();
            this.content = in.readString();
            this.zid = in.readString();
            this.userId = in.readString();
            this.avatar = in.readString();
            this.nickname = in.readString();
            this.replyContent = in.readString();
            this.replyUid = in.readString();
            this.replyAvatar = in.readString();
            this.replyNickname = in.readString();
            this.time = in.readString();
            this.replyTime = in.readString();
        }

        public static final Creator<CommentBean> CREATOR = new Creator<CommentBean>() {
            @Override
            public CommentBean createFromParcel(Parcel source) {
                return new CommentBean(source);
            }

            @Override
            public CommentBean[] newArray(int size) {
                return new CommentBean[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.category);
        dest.writeString(this.content);
        dest.writeString(this.imagestr);
        dest.writeString(this.coordinate);
        dest.writeString(this.location);
        dest.writeString(this.time);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeString(this.qeohash);
        dest.writeString(this.avatar);
        dest.writeString(this.usernick);
        dest.writeString(this.sign);
        dest.writeString(this.hxpassword);
        dest.writeInt(this.praisesAmount);
        dest.writeInt(this.commentAmount);
        dest.writeTypedList(this.praises);
        dest.writeTypedList(this.comment);
    }

    public WorldBean() {
    }

    protected WorldBean(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.category = in.readString();
        this.content = in.readString();
        this.imagestr = in.readString();
        this.coordinate = in.readString();
        this.location = in.readString();
        this.time = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.qeohash = in.readString();
        this.avatar = in.readString();
        this.usernick = in.readString();
        this.sign = in.readString();
        this.hxpassword = in.readString();
        this.praisesAmount = in.readInt();
        this.commentAmount = in.readInt();
        this.praises = in.createTypedArrayList(PraisesBean.CREATOR);
        this.comment = in.createTypedArrayList(CommentBean.CREATOR);
    }

    public static final Creator<WorldBean> CREATOR = new Creator<WorldBean>() {
        @Override
        public WorldBean createFromParcel(Parcel source) {
            return new WorldBean(source);
        }

        @Override
        public WorldBean[] newArray(int size) {
            return new WorldBean[size];
        }
    };
}
