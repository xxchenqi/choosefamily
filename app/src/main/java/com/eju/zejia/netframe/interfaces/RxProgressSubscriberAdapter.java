package com.eju.zejia.netframe.interfaces;

/**
 * Created by Sandy on 2016/6/1/0001.
 */
public abstract class RxProgressSubscriberAdapter<T> implements RxProgressSubscriberAllListener<T> {
    public void onCancel() {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(T t) {

    }
}
