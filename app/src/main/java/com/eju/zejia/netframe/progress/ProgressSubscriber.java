package com.eju.zejia.netframe.progress;

import android.content.Context;
import android.widget.Toast;

import com.eju.zejia.netframe.interfaces.ProgressCancelListener;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAdapter;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAllListener;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberListener;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberStartListener;
import com.eju.zejia.utils.UIUtils;

import rx.Subscriber;

/**
 * Created by Sandy on 2016/6/1/0001.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private RxProgressSubscriberListener mListener;
    private Context mContext;
    private ControlRequestDialogHandle mControlRequestDialogHandle;

    public ProgressSubscriber(RxProgressSubscriberListener listener, Context context) {
        this.mListener = listener;
        this.mContext = context;
        mControlRequestDialogHandle = new ControlRequestDialogHandle(context, this, true);
    }

    @Override
    public void onStart() {
        if (mListener instanceof RxProgressSubscriberStartListener) {
            ((RxProgressSubscriberStartListener) mListener).start();
        }else{
            showProgress();
        }
    }

    @Override
    public void onCompleted() {
        dismissProgress();
    }

    @Override
    public void onError(Throwable e) {
        dismissProgress();
        if (mListener instanceof RxProgressSubscriberAllListener ||
                mListener instanceof RxProgressSubscriberStartListener ) {
            ((RxProgressSubscriberAllListener) mListener).onError(e);
        } else {
            UIUtils.showLongToastSafe("网络请求失败");
//            Toast.makeText(mContext, "网络请求失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNext(T t) {
        mListener.onNext(t);
    }

    /**
     * 此方法来取消订阅，也就是取消请求以及对话框
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
        if(mListener instanceof RxProgressSubscriberAdapter){
            ((RxProgressSubscriberAdapter)mListener).onCancel();
        }
    }

    private void showProgress() {
        if (null != mControlRequestDialogHandle) {
            mControlRequestDialogHandle.obtainMessage(
                    ControlRequestDialogHandle.SHOW
            ).sendToTarget();
        }
    }

    private void dismissProgress() {
        if (null != mControlRequestDialogHandle) {
            mControlRequestDialogHandle.obtainMessage(
                    ControlRequestDialogHandle.DISMISS
            ).sendToTarget();
            mControlRequestDialogHandle = null;
        }
    }


}
