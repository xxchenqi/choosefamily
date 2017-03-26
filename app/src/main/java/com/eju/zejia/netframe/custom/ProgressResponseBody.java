package com.eju.zejia.netframe.custom;

import com.eju.zejia.netframe.interfaces.ProgressDownLoadListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Sandy on 2016/6/2/0002.
 * 下载使用的包装类
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody mResponseBody;
    private final ProgressDownLoadListener mProgressListener;

    public ProgressResponseBody(ResponseBody responseBody, ProgressDownLoadListener progressListener) {
        this.mResponseBody = responseBody;
        this.mProgressListener = progressListener;
    }
    private BufferedSource bufferedSource;
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                mProgressListener.onProgress(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }

}
