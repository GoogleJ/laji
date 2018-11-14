package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import android.accounts.NetworkErrorException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Jerome on 2018/7/14
 */
public class RxException {

    private static final String  UNCONNECT_NETWORK_EXCEPTION = "网络未连接,请先连接网络";
    private static final String CONNECTION_TIMEOUT = "网络连接超时，请检查您的网络状态，稍后重试";
    private static final String CONNECT_EXCEPTION = "网络连接异常，请检查您的网络状态";
    private static final String UNKNOWN_HOST_EXCEPTION = "网络异常，请检查您的网络状态";
    private static final String USERNOTLOGIN = "您还没有登录";

    public static String getMessage(Throwable t) {
        if (t instanceof SocketTimeoutException){
            return CONNECTION_TIMEOUT;
        } else if (t instanceof ConnectException){
            return CONNECT_EXCEPTION;
        } else if (t instanceof UnknownHostException){
            return UNKNOWN_HOST_EXCEPTION;
        } else if (t instanceof NetworkErrorException){
            return UNCONNECT_NETWORK_EXCEPTION;
        } else if (t instanceof ParamsException){
            return t.getMessage().toString();
        } else if (t instanceof IllegalArgumentException) {
            return USERNOTLOGIN;
        } else {
            return t.getMessage().toString();
        }
    }

   public static class ParamsException extends Exception{

        private String msg;

        public ParamsException(String msg) {
            super(msg);
            this.msg = msg;
        }

        @Override
        public String getMessage() {
            return msg;
        }
    }

}
