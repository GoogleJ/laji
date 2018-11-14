package com.zhishen.aixuexue.manager;

/**
 * Created by huangfangyi on 2017/2/10.
 * qq 84543217
 */

public class IMAction {

    public static final String ACTION_MESSAGE_READ = "action_message_read";
    public static final String ACTION_INVITE_MESSAGE = "action_invite_message";
    public static final String ACTION_NEW_MESSAGE = "action_new_message";
    public static final String ACTION_REMOVED_FROM_GROUP = "action_removed_from_group";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";
    public static final String ACTION_CONFLICT = "action_conflict";
    public static final String ACTION_CONNECTION_CHANAGED = "action_connection_changed";
    //消息撤回
    public static final String ACTION_MESSAGE_WITHDROW = "action_message_withdrow";
    //转发消息
    public static final String ACTION_MESSAGE_FORWORD = "action_message_forword";
    //清空消息
    public static final String ACTION_MESSAGE_EMPTY = "ACTION_MESSAGE_EMPTY";
    //删除好友通知
    public static final String CMD_DELETE_FRIEND = "DELETE_FRIEND";
    //资料更新的通知
    public static final String ACTION_UPDATE_INFO = "ACTION_UPDATE_INFO";
    //已读回执
    public static final String ACTION_MSG_ASKED = "ACTION_MSG_ASKED";
    public static final String ACTION_MOMENTS = "ACTION_MOMENTS";
    //朋友圈消息已读
    public static final String ACTION_MOMENTS_READ = "ACTION_MOMENTS_READ";
    //刷新所有列表
    public static final String ACTION_REFRESH_ALL_LIST = "ACTION_REFRESH_ALL_LIST";
    //解除禁言
    public static final String ACTION_HAS_CANCLED_NO_TALK = "ACTION_HAS_CANCLED_NO_TALK";
    //被禁言
    public static final String ACTION_HAS_NO_TALK = "ACTION_HAS_NO_TALK";
    //定位的经纬度
    public static final String ACTION_LOCATION_HAS = "ACTION_LOCATION_HAS";
    //    定位失败
    public static final String ACTION_LOCATION_FAILED = "ACTION_LOCATION_FAILED";
    //    微信,支付宝支付的Action
    public static final String PAY_BY_WECHAT_RESULT = "PAY_BY_WECHAT_RESULT";
    public static final String PAY_BY_ALIPAY_RESULT = "PAY_BY_ALIPAY_RESULT";
    //设置支付密码成功
    public static final String SET_PAY_PWD_SUCCESS = "SET_PAY_PWD_SUCCESS";
    //红包已领取
    public static final String RP_IS_HAS_OPEND = "RP_IS_HAS_OPEND";
    // 二维码已付款
    public static final String QRCODE_IS_PAYED = "QRCODE_IS_PAYED";
    //微信授权
    public static final String ACTION_WECHAT_AUTH = "ACTION_WECHAT_AUTH";
    //删除了好友刷新通知
    public static final String REFRESH_CONTACTS_LIST = "REFRESH_CONTACTS_LIST";
    //位置变化的通知
    public static final String LOCATION_CHANGE_ACTION = "LOCATION_CHANGE_ACTION";
//    附近圈的消息
    public static final String ACTION_MOMENTS_NEAR_BY = "ACTION_MOMENTS_NEAR_BY";
    //附近圈消息已读
    public static final String ACTION_MOMENTS_READ_NRAR_BY = "ACTION_MOMENTS_READ_NRAR_BY";
    //直播退出的通知
    public static final String ACTION_EXIT_LIVE = "ACTION_EXIT_LIVE";

}
