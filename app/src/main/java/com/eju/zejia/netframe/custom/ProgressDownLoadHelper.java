package com.eju.zejia.netframe.custom;

import android.util.Log;

import com.eju.zejia.netframe.interfaces.ProgressDownLoadListener;
import com.eju.zejia.netframe.progress.ProgressBean;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by Sandy on 2016/6/2/0002.
 */
public class ProgressDownLoadHelper {
    private static ProgressBean progressBean = new ProgressBean();
    private static ProgressHandler mProgressHandler;

    public static OkHttpClient.Builder addProgress(OkHttpClient.Builder builder) {

        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }

        final ProgressDownLoadListener progressListener = new ProgressDownLoadListener() {
            //该方法在子线程中运行
            @Override
            public void onProgress(long progress, long total, boolean done) {
                Log.d("progress:", String.format("%d%% done\n", (100 * progress) / total));
                if (mProgressHandler == null) {
                    return;
                }

                progressBean.setBytesRead(progress);
                progressBean.setContentLength(total);
                progressBean.setDone(done);
                mProgressHandler.sendMessage(progressBean);

            }
        };

        //添加拦截器，自定义ResponseBody，添加下载进度
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();

            }
        });

        return builder;
    }

    public static void setProgressHandler(ProgressHandler progressHandler) {
        mProgressHandler = progressHandler;
    }
}
