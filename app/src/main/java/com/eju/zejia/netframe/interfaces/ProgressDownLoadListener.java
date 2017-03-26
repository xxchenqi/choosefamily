package com.eju.zejia.netframe.interfaces;

/**
 * Created by Sandy on 2016/6/2/0002.
 */
public interface ProgressDownLoadListener {

    /**
     * @param progress     已经下载或上传字节数
     * @param total        总字节数
     * @param done         是否完成
     */
    void onProgress(long progress, long total, boolean done);
}
