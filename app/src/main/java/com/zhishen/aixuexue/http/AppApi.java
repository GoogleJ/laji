package com.zhishen.aixuexue.http;


import com.alibaba.fastjson.JSONObject;
import com.zhishen.aixuexue.bean.AppConfigData;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.http.response.GroupListRespone;
import com.zhishen.aixuexue.http.response.GroupResponse;
import com.zhishen.aixuexue.http.response.HomeAleartBean;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


/**
 * retrofit 接口
 */
public interface AppApi {

    /**
     * @param data 获取验证码
     * @return
     */
    @Multipart
    @POST("getSecurityCode")
    Observable<BaseResponseDataT> getMobileCode(@Part("data") String data);

    @POST("api/config/getAppConfigVersion")
    Observable<BaseResponseDataT<AppConfigData>> getAppVersion();

    @POST("api/Customer/updateCustomerById")
    Observable<BaseResponseDataT> updateCustomerById(@Query("data") String data);

    @POST("api/config/getAppConfigInfos")
    Flowable<BaseResponseDataT<HomeBean>> getPageData();

    //首页弹出框
    @POST("api/config/alert")
    Flowable<BaseResponseDataT<HomeAleartBean>> getAleart();

    /**
     * 登录
     *
     * @param data
     * @return
     */
    @Multipart
    @POST("login")
    Observable<JSONObject> login(@Part("usertel") String data, @Part("password") String password);

    /**
     * 注册
     *
     * @param data
     * @return
     */
    @Multipart
    @POST("register")
    Observable<JSONObject> register(@Part("usertel") String data, @Part("password") String password, @Part("usernick") String usernick);


    /**
     * 修改密码，忘记密码
     *
     * @param data
     * @param password
     * @return
     */
    @Multipart
    @POST("resetPassword")
    Observable<JSONObject> resetPassword(@Part("tel") String data, @Part("newPassword") String password);

    /**
     * 上传本地时间
     *
     * @return
     */
    @Multipart
    @POST("updateLocalTimestamp")
    Observable<JSONObject> updateLocalTimestamp(@Part("session") String session);

    /**
     * 上传聊天记录
     *
     * @return
     */
    @Multipart
    @POST("uploadMessage")
    Observable<JSONObject> uploadMessage(@Part("fromId") String fromId, @Part("toId") String toId, @Part("chattype") String chattype, @Part("message") String message, @Part("session") String session);

    /**
     * 获取好友列表
     *
     * @return
     */
    @Multipart
    @POST("fetchFriends")
    Observable<JSONObject> fetchFriends(@Part("session") String session);

    /**
     * 搜索好友
     *
     * @param userId
     * @param session
     * @return
     */
    @Multipart
    @POST("searchUser")
    Observable<JSONObject> searchUser(@Part("userId") String userId, @Part("session") String session);

    /**
     * 添加朋友
     *
     * @param userId
     * @param session
     * @return
     */
    @Multipart
    @POST("addFriend")
    Observable<JSONObject> addFriend(@Part("userId") String userId, @Part("session") String session);

    /**
     * 删除好友
     *
     * @param userId
     * @param session
     * @return
     */
    @Multipart
    @POST("removeFriend")
    Observable<JSONObject> removeFriend(@Part("userId") String userId, @Part("session") String session);

    /**
     * 查询好友详细资料
     *
     * @param userId
     * @param session
     * @return
     */
    @Multipart
    @POST("getUserInfo")
    Observable<JSONObject> getUserInfo(@Part("userId") String userId, @Part("session") String session);

    @Multipart
    @POST("api/group/listGroup")
    Observable<BaseResponseDataT<List<GroupListRespone>>> listGroup(@Part("data") String data);

    @Multipart
    @POST("api/group/getGroup")
    Observable<BaseResponseDataT<GroupListRespone>> getGroup(@Part("data") String data);

    @Multipart
    @POST("api/group/addGroup")
    Observable<BaseResponseDataT> addGroup(@Part("data") String data);

    @Multipart
    @POST("api/group/createGroup")
    Observable<BaseResponseDataT<GroupResponse>> createGroup(@Part("data") String data);

    @Multipart
    @POST("group/mucMembers.php")
    Observable<JSONObject> lookGroupMember(@Part("gid") String gid, @Part("uid") String uid);

    @Multipart
    @POST("api/Customer/getSecurityCode")
    Observable<BaseResponseDataT> getSecurityCode(@Part("data") String data);
}
