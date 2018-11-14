package com.zhishen.aixuexue.activity.fragment.interactionfragment;

import com.zhishen.aixuexue.http.BaseResponseDataT;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

    @FormUrlEncoded
    @POST("api/world/listWorldInfo")
    Observable<BaseResponseDataT<List<GetNearByResponse>>> getNearBy(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/Customer/updatePassW")
    Observable<BaseResponseDataT> updatePassW(@Field("data") String data);

    //添加关注
    @FormUrlEncoded
    @POST("api/blackAndWhite/addBlackAndWhite")
    Observable<BaseResponseDataT> addBlackAndWhite(@Field("data") String data);

    //取消关注
    @FormUrlEncoded
    @POST("api/blackAndWhite/cancelBlackAndWhite")
    Observable<BaseResponseDataT> cancelBlackAndWhite(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/inform/addReport")
    Observable<BaseResponseDataT> addReport(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/inform/upload")
    Observable<BaseResponseDataT> upload(@Field("photo") String photo);

    @FormUrlEncoded
    @POST("api/comment/addComment")
    Observable<BaseResponseDataT<String>> addComment(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/comment/getAppCommentList")
    Observable<BaseResponseDataT<GetCommentResponse>> getAppCommentList(@Field("data") String data);

    //机构详情
    @FormUrlEncoded
    @POST("api/company/getCompanyById")
    Observable<BaseResponseDataT<GetInstitutionsResponse>> institutionDetail(@Field("data") String data);

    //附近的人详情
    @GET("api.php?action=userInfo")
    Observable<GetUserInfoResponse> userDetail(@Query("id") String id);

    @FormUrlEncoded
    @POST("api/user/userSignIn")
    Flowable<BaseResponseDataT<SignDetailResponse>> userSignIn(@Field("data") String data);

    @FormUrlEncoded
    @POST("api/user/getSignInfo")
    Flowable<BaseResponseDataT<SignDetailResponse>> getSignInfo(@Field("data") String data);
}
