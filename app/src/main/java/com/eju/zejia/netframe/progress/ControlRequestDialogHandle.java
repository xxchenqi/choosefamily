package com.eju.zejia.netframe.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.eju.zejia.netframe.interfaces.ProgressCancelListener;


/**
 * Created by Sandy on 2016/6/1/0001.
 */
public class ControlRequestDialogHandle extends Handler {

    public static final int SHOW = 1;
    public static final int DISMISS = 2;

    private ProgressDialog dialog;

    private Context mContext;
    private boolean mCancelable;
    private ProgressCancelListener mProgressCancelListener;

    public ControlRequestDialogHandle(Context context, ProgressCancelListener mProgressCancelListener,
                                 boolean cancelable) {
        super();
        this.mContext = context;
        this.mProgressCancelListener = mProgressCancelListener;
        this.mCancelable = cancelable;
    }

    private void initProgressDialog(){
        if (dialog == null) {
            dialog = new ProgressDialog(mContext);

            dialog.setCancelable(mCancelable);

            if (mCancelable) {
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        mProgressCancelListener.onCancelProgress();
                    }
                });
            }

            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    private void dismissProgressDialog(){
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW:
                initProgressDialog();
                break;
            case DISMISS:
                dismissProgressDialog();
                break;
        }
    }

}
