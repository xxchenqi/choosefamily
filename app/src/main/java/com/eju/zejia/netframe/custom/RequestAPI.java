package com.eju.zejia.netframe.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.eju.zejia.BuildConfig;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.models.ResponseData;
import com.eju.zejia.netframe.converter.JsonConverterFactory;
import com.eju.zejia.netframe.interfaces.ApiUrls;
import com.eju.zejia.netframe.interfaces.DownLoadListener;
import com.eju.zejia.netframe.interfaces.RequestDataListener;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberListener;
import com.eju.zejia.netframe.progress.ProgressSubscriber;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.internal.schedulers.ExecutorScheduler;
import rx.plugins.RxJavaErrorHandler;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subjects.Subject;
import timber.log.Timber;

/**
 * Created by Sandy on 2016/6/1/0001.
 */
public class RequestAPI {

    private static final String baseUrl = Constants.ZEJIA_API_URL;
//    private static final String baseUrl = "http://172.28.70.47/api_v4/";//测试环境
//    private static final String baseUrl = "http://api.51shizhong.com/api_v4/";//正式环境

    private static final int DEFAULT_TIMEOUT = 1000;//超时时间
    private Retrofit mRxRetrofit;
    private final ApiUrls mDoRequest;
    private final OkHttpClient.Builder okBuilder;

    private RequestAPI() {
        okBuilder = new OkHttpClient.Builder();
        //缓存目录  待添加！！！
//        Cache cache = new Cache(getCacheDir(),10*1024*1024);

        //配置OkHttp
        okBuilder
                //设置超时
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
                //失败重连
                .retryOnConnectionFailure(true)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    okhttp3.Response response = chain.proceed(request);
                    if (BuildConfig.DEBUG) {
                        Timber.d("Request url is %s", request.url());
                        Timber.d("Request header is %s", request.headers());
                        Timber.d("Response header is %s", response.headers());
                    }
                    return response;
                })
//                .cache(cache)
                /*.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        return null;
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        return null;
                    }
                })*/;
        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
            @Override
            public void handleError(Throwable e) {
                super.handleError(e);
                Timber.e(e, "Error occurs.");
            }
        });
        mRxRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())//解码后json字符串稍微占用一点时间
                .addConverterFactory(JsonConverterFactory.create())//原装json字符串返回占用时间少
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okBuilder.build())
                .build();

        mDoRequest = mRxRetrofit.create(ApiUrls.class);
    }

    private static class SingleClass {
        private static final RequestAPI INSTANCE = new RequestAPI();
    }

    /**
     * 单例
     *
     * @return 此对象
     */
    public static RequestAPI getInstance() {
        return SingleClass.INSTANCE;
    }

    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    //共封装三种请求方式，前两种包含ProgressDialog(推荐使用第一种)

    /**
     * 第一种
     * 利用RxJava返回的是json字符串
     *
     * @param context  上下文
     * @param listener 外部自己实现监听，内部subscriber
     * @param path     url地址
     * @param params   post请求参数
     */
    public void doRxRequest(Context context, RxProgressSubscriberListener<JSONObject> listener,
                            String path, Map<String, String> params) {
        ProgressSubscriber<JSONObject> subscriber = new ProgressSubscriber<JSONObject>(listener, context);
        Observable<JSONObject> observable = mDoRequest.getRxJsonData(path, params);
        toSubscribe(context, observable, subscriber);
    }

    public Subscription doRxRequest(Context context, RxProgressSubscriberListener<JSONObject> listener,
                                      String path, JSONObject params) {
        ProgressSubscriber<JSONObject> subscriber = new ProgressSubscriber<JSONObject>(listener, context);
        Observable<JSONObject> observable = mDoRequest.getRxJsonData(path, params);
        return toSubscribe(context, observable, subscriber);
    }

    public Subscription doRxRequest(Context context, RxProgressSubscriberListener<JSONObject> listener,
                                    String path, JSONObject params, Scheduler scheduler) {
        ProgressSubscriber<JSONObject> subscriber = new ProgressSubscriber<JSONObject>(listener, context);
        Observable<JSONObject> observable = mDoRequest.getRxJsonData(path, params);
        return toSubscribe(context, observable, subscriber, scheduler);
    }

    public Subscription doRxFormDataRequest(Context context, RxProgressSubscriberListener<JSONObject> listener,
                                            String path, JSONObject params, RequestBody file) {
        ProgressSubscriber<JSONObject> subscriber = new ProgressSubscriber<JSONObject>(listener, context);
        Observable<JSONObject> observable = mDoRequest.getRxJavaMultipartData(path, params,file);
        return toSubscribe(context, observable, subscriber);
    }

    //==============================================================================================

    public <T> Subscription doRxBeanRequest(Context context, RxProgressSubscriberListener<ResponseData<T>> listener,
                                            String path, JSONObject params) {
        ProgressSubscriber<ResponseData<T>> subscriber = new ProgressSubscriber<>(listener, context);
        Observable<ResponseData<T>> observable = mDoRequest.getRxJavaBeanData(path, params);
        return toSubscribe(context, observable, subscriber);
    }

    /**
     * 第二种(未测试)
     * 利用RxJava返回的是JavaBean此方法需后台定制为以下格式，返回过滤掉code码和msg的data数据(慎用！！！)
     * int      code
     * Str      msg
     * List<t>  data
     *
     * @param context  上下文
     * @param listener 外部自己实现监听，内部subscriber
     * @param path     url地址
     * @param params   post请求参数
     */
    public void doRxBeanRequest(Context context, RxProgressSubscriberListener<List<Subject>> listener,
                                String path, Map<String, String> params) {
        ProgressSubscriber<List<Subject>> subscriber = new ProgressSubscriber<>(listener, context);
        Observable<List<Subject>> observable =
                mDoRequest.getRxJavaBeanData(path, params).map(new HttpResultFunc<List<Subject>>());

        toSubscribe(observable, subscriber);
    }

    /**
     * 线程管理
     *
     * @param o 观察者
     * @param s 自己控制
     */
    private void toSubscribe(Observable o, Subscriber s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    private Subscription toSubscribe(Context context, Observable o, Subscriber s) {
        Observable<?> observable = o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (context != null) {
            if (context instanceof RxAppCompatActivity) {
                observable.compose(((RxAppCompatActivity) context).bindToLifecycle());
            } else if (context instanceof RxFragmentActivity) {
                observable.compose(((RxFragmentActivity) context).bindToLifecycle());
            }
        }
        return observable.subscribe(s);
    }

    private Subscription toSubscribe(Context context, Observable o, Subscriber s, Scheduler scheduler) {
        Observable<?> observable = o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(scheduler);
        if (context != null) {
            if (context instanceof RxAppCompatActivity) {
                observable.compose(((RxAppCompatActivity) context).bindToLifecycle());
            } else if (context instanceof RxFragmentActivity) {
                observable.compose(((RxFragmentActivity) context).bindToLifecycle());
            }
        }
        return observable.subscribe(s);
    }

    //==============================================================================================
    //==============================================================================================
    //==============================================================================================
    //==============================================================================================

    /**
     * 第三种
     * 返回的是json字符串
     *
     * @param context  上下文
     * @param listener 外部自己实现监听
     * @param path     url地址
     * @param params   post请求参数
     */
    public void doReqeust(final Context context, final RequestDataListener<Response<JSONObject>> listener,
                          String path, Map<String, String> params) {
        mDoRequest.getData(path, params).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void doUpLoad(final Context context, final RequestDataListener<Response<JSONObject>> listener,
                         String path, MultipartBody.Builder params) {
        mDoRequest.upLoad(path, params.build()).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                listener.success(response);
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }
    /*
    上传示例：
        String url = "http://i.upload.file.dc.cric.com/FileWriterInner.php";
        MultipartBody.Builder form = new MultipartBody.Builder();
        File file = new File(Environment.getExternalStorageDirectory()+"/12565.jpg");
        form.setType(MultipartBody.FORM);
        form.addFormDataPart("permit_code","A003");
        form.addFormDataPart("file_category","SZJS");
        form.addFormDataPart("key","1e1a8af8390f8cdcb709a49db0a19357");
        form.addFormDataPart("pfile", "12565.jpg", RequestBody.create(MediaType.parse("image"),file));

        RequestAPI.getInstance().doReqeust(this, new RequestDataListener<Response<JSONObject>>() {
            @Override
            public void success(Response<JSONObject> response) {
                Log.d("API：","返回的jsonObject "+response.body());
            }
        },url,form);
     */

    /**
     * 下载(此方法返回是在子线程)
     *
     * @param context  上下文
     * @param listener 外部自己实现监听
     * @param urlPath  url地址
     * @param filePath 文件存储路径
     */
    public void downLoad(final Context context, final DownLoadListener<File> listener,
                         final String urlPath, final String filePath) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressNumberFormat("%1d KB/%2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        ProgressDownLoadHelper.setProgressHandler(new DownloadProgressHandler() {
            @Override
            protected void onProgress(long bytesRead, long contentLength, boolean done) {
//                Log.d("是否在主线程中运行", String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
//                Log.d("onProgress",String.format("%d%% done\n",(100 * bytesRead) / contentLength));
//                Log.d("done","--->" + String.valueOf(done));
                dialog.setMax((int) (contentLength / 1024));
                dialog.setProgress((int) (bytesRead / 1024));
                if (done) {
                    dialog.dismiss();
                }
            }
        });
        okBuilder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES);
        ProgressDownLoadHelper.addProgress(okBuilder);
        Retrofit rxRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
                .client(okBuilder.build())
                .build();
        ApiUrls apiUrls = rxRetrofit.create(ApiUrls.class);
        apiUrls.downLoad(urlPath).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                final File file = new File(filePath);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("zzzzzzzzzz", "文件是否存在" + file.exists());
                            if (file.exists()) {
                                file.delete();
                                Log.d("zzzzzzzzzz", "删除。。。" + file.exists());
                            }
                            saveBytesToFile(response.body().bytes(), file);
                            listener.success(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }) {
                }.start();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "网络请求失败", Toast.LENGTH_LONG).show();
            }
        });
    }
    /*下载示例
    String url = "http://check.51shizhong.com/resource/apk/ClassClockRoom_1.1.0_2010031003_20160530.apk";
    String file = Environment.getExternalStorageDirectory()
            + "/clock/CameraCache/clock.apk";
    RequestAPI.getInstance().downLoad(this, new DownLoadListener<File>() {
        @Override
        public void success(File file) {
            Log.d("zzzzzz","返回的file "+file.length());
        }
    },url,file);*/
    //==============================================================================================

    public void saveBytesToFile(byte[] bytes, File file) {
        FileOutputStream fileOuputStream = null;
        try {
            fileOuputStream = new FileOutputStream(file);
            fileOuputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fileOuputStream) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 用来统一处理后台放回的code,并将Result中的Data部分剥离出来返回给subscriber(方法二会用到)
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<BaseResult<T>, T> {

        @Override
        public T call(BaseResult<T> baseResult) {
            if (baseResult.getCode() != 1) {
                Log.d("API：", "返回的错误code = " + baseResult.getCode());
            }
            return baseResult.getData();
        }
    }

}
