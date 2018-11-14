package com.zhishen.aixuexue.activity.fragment.homefragment.adapter;

import com.zhishen.aixuexue.http.BaseResponseDataT;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Jerome on 2018/6/28
 */
public class RxSchedulers {

    public static <T> ObservableTransformer<T, T> ioObserver() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> FlowableTransformer<T, T> ioFlowable() {
        return upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    //JAVA 处理
    public static <T> FlowableTransformer<BaseResponseDataT<T>, T> flowTransformer() {
        return upstream -> upstream.flatMap((Function<BaseResponseDataT<T>, Publisher<T>>) resultData -> {
            if (resultData.code == 0) {
                return Flowable.just(resultData.data);
            } else if (resultData.code == 500) {
                return Flowable.error(new Throwable(new RxException.ParamsException(resultData.msg)));
            }
            return Flowable.error(new Throwable(resultData.msg));
        });
    }

    public static FlowableTransformer<BaseResponseDataT<String>, String> flowTransString() {
        return upstream -> upstream.flatMap((Function<BaseResponseDataT<String>, Publisher<String>>) resultData -> {
            if (resultData.code == 0) {
                return Flowable.just(resultData.data);
            }
            return Flowable.error(new Throwable(resultData.msg));
        });
    }

    //PHP 处理
    public static <T> FlowableTransformer<BaseResponseDataT<T>, T> flowOtherTransformer() {
        return upstream -> upstream.flatMap((Function<BaseResponseDataT<T>, Publisher<T>>) resultData -> {
            if (resultData.code == 1) {
                return Flowable.just(resultData.data);
            }
            return Flowable.error(new Throwable("暂无数据"));
        });
    }


}
