package com.zhishen.aixuexue.activity.fragment.homefragment;

import com.zhishen.aixuexue.bean.CollectBean;
import com.zhishen.aixuexue.bean.CouponBean;
import com.zhishen.aixuexue.bean.DiscountBean;
import com.zhishen.aixuexue.bean.FollowBean;
import com.zhishen.aixuexue.bean.HomeBean;
import com.zhishen.aixuexue.bean.HomeNoticeBean;
import com.zhishen.aixuexue.bean.NearOrganBean;
import com.zhishen.aixuexue.bean.NewsBean;
import com.zhishen.aixuexue.bean.NewsComment;
import com.zhishen.aixuexue.bean.PraiseBean;
import com.zhishen.aixuexue.bean.SystemNoticeBean;
import com.zhishen.aixuexue.bean.UserBean;
import com.zhishen.aixuexue.bean.WorldBean;
import com.zhishen.aixuexue.http.BaseResponseDataT;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Jerome on 2018/6/29
 */
public interface ResApi {

    int USER_TYPE_NEWS = 1; //资讯
    int USER_TYPE_TCH = 2;  // 老师
    int USER_TYPE_ORGAN = 3;//机构
    int USER_TYPE_COURSE = 4;//课程
    int USER_TYPE_FRIENDS = 5;//好友

    //用户信息
    @POST("api/Customer/getCustomerById")
    Flowable<BaseResponseDataT<UserBean>> getUserInfo(@Query("data") String data);

    //我的统计关注数量
    @POST("api/my/myCount")
    Flowable<BaseResponseDataT<List<HomeBean.UserMenuBean.UserMenu>>> getStatistCount(@Query("data") String data);

    //新闻资讯
    @POST("api/news/getHomeCountList")
    Flowable<BaseResponseDataT<NewsBean>> getNews(@Query("data") String data);

    //新闻分类
    @POST("api/news/listNewsByType")
    Flowable<BaseResponseDataT<NewsBean>> getNewsType(@Query("data") String data);

    //资讯点赞
    @POST("api/praise/addPraise")
    Flowable<BaseResponseDataT<NewsBean>> setPraise(@Query("data") String data);

    //资讯评论
    @POST("api/comment/getAppCommentList")
    Flowable<BaseResponseDataT<NewsComment>> getNewsComment(@Query("data")String data);

    //资讯发布评论
    @POST("api/comment/addComment")
    Flowable<BaseResponseDataT<String>> addNewsComment(@Query("data")String data);

    //取消关注 老师/机构
    @POST("api/blackAndWhite/cancelBlackAndWhite")
    Flowable<BaseResponseDataT<String>> follow(@Query("data")String data);

    //通知
    @POST("api/notify/listUserNotify")
    Flowable<BaseResponseDataT<HomeNoticeBean>> getNotice(@Query("data") String data);

    //关注的人
    @POST("api/blackAndWhite/attentionFriend")
    Flowable<BaseResponseDataT<List<FollowBean>>> getFollowTches(@Query("data") String data);

    //关注机构
    @POST("api/blackAndWhite/getCompanyByFromId")
    Flowable<BaseResponseDataT<List<FollowBean>>> getFollowOrgans(@Query("data") String data);

    //我的收藏
    @POST("api/collect/MyCollection")
    Flowable<BaseResponseDataT<CollectBean>> getCollects(@Query("data") String data);

    //优惠券
    @POST("api/coupons/getCoupons")
    Flowable<BaseResponseDataT<List<CouponBean>>> getCoupons(@Query("data") String data);

    //系统通知
    @POST("api/notify/listXtNotify")
    Flowable<BaseResponseDataT<SystemNoticeBean>> getSysNotice(@Query("data") String data);

    //优惠活动
    @POST("api/activity/getActivityList")
    Flowable<BaseResponseDataT<List<DiscountBean>>> getDiscounts();

    //搜索机构
    @POST("api/company/listCompanyByCriteriaFormMS")
    Flowable<BaseResponseDataT<NearOrganBean>> searchOrgan(@Query("data")String data);

    //附近机构
    @POST("api/company/listCompanyByCriteriaFormMS")
    Flowable<BaseResponseDataT<NearOrganBean>> getNearOrgans(@Query("data") String data);

    //朋友圈
    @POST("fetchTimeline")
    Flowable<BaseResponseDataT<List<WorldBean>>> getFriends(@QueryMap Map<String,Object> params);

    //获取指定ID朋友圈内容
    @POST("fetchOtherTimeline")
    Flowable<BaseResponseDataT<List<WorldBean>>> getOtherFriends(@QueryMap Map<String,Object> params);

    //朋友圈点赞
    @POST("praiseTimeline")
    Flowable<BaseResponseDataT<PraiseBean>> posterPraise(@QueryMap Map<String,Object> params);

    //朋友圈评论
    @POST("commentTimeline")
    Flowable<BaseResponseDataT<String>> posterComment(@QueryMap Map<String,Object> params);

    //发布朋友圈
    @POST("publishnew")
    Flowable<BaseResponseDataT<WorldBean>> posterFriends(@QueryMap Map<String,Object> params);

}
