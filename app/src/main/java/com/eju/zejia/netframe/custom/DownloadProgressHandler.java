package com.eju.zejia.netframe.custom;

import android.os.Looper;
import android.os.Message;

import com.eju.zejia.netframe.progress.ProgressBean;


/**
 * Created by Sandy on 2016/6/2/0002.
 */
public abstract class DownloadProgressHandler extends ProgressHandler {

    private static final int DOWNLOAD_PROGRESS = 1;
    protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

    @Override
    protected void sendMessage(ProgressBean progressBean) {
        mHandler.obtainMessage(DOWNLOAD_PROGRESS,progressBean).sendToTarget();

    }

    @Override
    protected void handleMessage(Message message){
        switch (message.what){
            case DOWNLOAD_PROGRESS:
                ProgressBean progressBean = (ProgressBean)message.obj;
                onProgress(progressBean.getBytesRead(),progressBean.getContentLength(),progressBean.isDone());
        }
    }

}
