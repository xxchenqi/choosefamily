package com.eju.zejia.netframe.interfaces;

/**
 * Created by Sandy on 2016/6/1/0001.
 */
public interface RxProgressSubscriberAllListener<T> extends RxProgressSubscriberListener<T> {
    void onError(Throwable e);
}
