package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhishen.aixuexue.activity.fragment.homefragment.ResApi;
import com.zhishen.aixuexue.http.AesCBC;
import com.zhishen.aixuexue.http.BaseAesRequestData;
import com.zhishen.aixuexue.main.AiApp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jerome on 2018/7/14
 */
public class ParamsMap extends BaseAesRequestData {
    private static final String TAG = ParamsMap.class.getSimpleName();

    private static Gson gson;

    /**
     * Map<String,Object> params = new HashMap<>();
     * AesCBC.getInstance().encrypt(gson.toJson(params));
     */
    static {
        gson = new GsonBuilder().enableComplexMapKeySerialization()
                .create();
    }

    /**
     * 获取用户关注数量
     * @return
     */
    public static String getUserCount() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", AiApp.getInstance().getUsername());
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    public static String loadImage(String base64){
        Map<String, Object> params = new HashMap<>();
        params.put("photo", base64);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 用户信息
     */
    public static String getUserInfo(String userId){
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 我的收藏
     */
    public static String getMyCollects(int page, String uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", page);
        params.put("customerId", uid);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 优惠券
     */
    public static String getCoupon(String uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", uid);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 关注机构
     */
    public static String getFollow(String fid, int contentType) {
        Map<String, Object> params = new HashMap<>();
        params.put("fromUserid", AiApp.getInstance().getUsername());
        params.put("toContentid", fid);
        params.put("operateType", 2);
        params.put("contentType", contentType);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 关注的人
     */
    public static String getFollowPeople(String uid,int page){
        Map<String, Object> params = new HashMap<>();
        params.put("fromUserid", uid);
        params.put("operateType", 2);
        params.put("page", page);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 系统消息
     */
    public static String getPage(int page) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 机构分类
     */
    public static String getNearOrgan(int page) {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNum", page);
        //params.put("regionVo", page); //区域
        //params.put("courseCategoryId", page);//分类
        //params.put("commentDesc", page); //排序
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 搜索机构
     */
    public static String searchOrgan(int page,String content){
        Map<String, Object> params = new HashMap<>();
        /*params.put("regionVo", "");
        params.put("courseCategoryId", "");
        params.put("commentDesc", "");*/
        params.put("pageNum", page);
        params.put("companyName", content);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 点赞
     */
    public static String getPraise(String typeID){
        Map<String, Object> params = new HashMap<>();
        params.put("typeId", typeID);
        params.put("praiseType", 1);
        params.put("customerId", AiApp.getInstance().getUsername());
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 首页通知
     */
    public static String getNotice(int page, String uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("userId", uid);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 首页新闻
     */
    public static String getNews(int page) {
        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 新闻分类
     */
    public static String getNewsType(int page,String typeID){
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", page);
        params.put("type", typeID);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 资讯-评论
     * @return
     */
    public static String getNewsComment(int page,String cid) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetType", ResApi.USER_TYPE_NEWS);
        params.put("targetId", cid);
        params.put("pageNo", page);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 资讯-发布评论
     * @param cid
     * @param content
     * @return
     */
    public static String addNewsComment(String cid,String content) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetId", cid);
        params.put("content", content);
        params.put("score", 0);
        params.put("status", 0);
        params.put("fromId", AiApp.getInstance().getUsername());
        params.put("targetType",ResApi.USER_TYPE_NEWS);
        return AesCBC.getInstance().encrypt(gson.toJson(params));
    }

    /**
     * 朋友圈
     */
    public static Map<String, Object> getCircleFriends(int page) {
        Map<String, Object> params = new HashMap<>();
        AiApp app = AiApp.getInstance();
        params.put("session", app.getUserSession());
        params.put("isFriend", 1);
        params.put("category", 0);
        params.put("currentPage", page);
        params.put("pageSize", 20);
        params.put("isFold",1 );
        return params;
    }

    /**
     * 获取他人朋友圈动态动态
     * @param page
     * @param uid
     * @return
     */
    public static Map<String,Object> getOtherCircle(int page,String uid){
        Map<String, Object> params = new HashMap<>();
        params.put("currentPage", page);
        params.put("pageSize", 30);
        params.put("userId", uid);
        params.put("session", AiApp.getInstance().getUserSession());
        return params;
    }

    /**
     * 朋友圈发布
     * @param imagestr 多张以逗号隔开
     * @param /coverImage 视频第一帧图片
     */
    public static Map<String,Object> publishCircle(String category,
                    String content,String imagestr,String localtion, LatLng latLng){
        Map<String, Object> params = new HashMap<>();
        AiApp app = AiApp.getInstance();
        params.put("userId", app.getUsername());
        params.put("category", category);
        params.put("coordinate", latLng.latitude+","+latLng.longitude);
        params.put("lng", latLng.longitude);
        params.put("lat", latLng.latitude);
        params.put("content", content);
        params.put("location", TextUtils.isEmpty(localtion)?"":localtion);
        params.put("restrict", 0); //表示所有好友都能看见
        params.put("imagestr", TextUtils.isEmpty(imagestr)?"":imagestr);
        params.put("coverImage",""); //相当于无用
        params.put("videopath", "");//相当于无用 地址统一按imagestr
        params.put("session", app.getUserSession());
        return params;
    }

    /**
     * 朋友圈-点赞
     * @param aid
     * @return
     */
    public static Map<String,Object> posterPraise(String aid){
        Map<String, Object> params = new HashMap<>();
        AiApp app = AiApp.getInstance();
        params.put("tid", aid);
        params.put("userId",app.getUsername());
        params.put("session", app.getUserSession());
        return params;
    }

    /**
     * 朋友圈-评论
     * @param content
     * @param aid
     * @return
     */
    public static Map<String,Object> posterComment(String content,String aid){
        Map<String, Object> params = new HashMap<>();
        AiApp app = AiApp.getInstance();
        params.put("tid", aid);
        params.put("userId",app.getUsername());
        params.put("content",content);
        params.put("session", app.getUserSession());
        return params;
    }

    /**
     * 朋友圈-评论
     * @return
     */
    public static Map<String,Object> posterComment(){
        Map<String, Object> params = new HashMap<>();

        return params;
    }
}
