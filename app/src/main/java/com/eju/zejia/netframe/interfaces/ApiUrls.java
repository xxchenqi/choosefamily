package com.eju.zejia.netframe.interfaces;

import com.eju.zejia.data.models.ResponseData;
import com.eju.zejia.netframe.custom.BaseResult;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;
import rx.subjects.Subject;

/**
 * Created by Sandy on 2016/6/1/0001.
 */
public interface ApiUrls {

//    @GET
//    Call<JSONObject> get();

    //上传
    @POST
    Call<JSONObject> upLoad(@Url String path, @Body MultipartBody body);

    //请求
    @FormUrlEncoded
    @POST
    Call<JSONObject> getData(@Url String path, @FieldMap Map<String, String> map);

    //下载
    @Streaming
    @GET
    Call<ResponseBody> downLoad(@Url String path);


    //==============================================================================================

    @FormUrlEncoded
    @POST
    Observable<JSONObject> getRxJsonData(@Url String path, @FieldMap Map<String, String> map);

    @POST
    Observable<JSONObject> getRxJsonData(@Url String path, @Body JSONObject jsonObject);

    @FormUrlEncoded
    @POST
    Observable<BaseResult<List<Subject>>> getRxJavaBeanData(@Url String path, @FieldMap Map<String, String> map);

    @POST
    <T> Observable<ResponseData<T>> getRxJavaBeanData(@Url String path, @Body JSONObject jsonObject);

    @Multipart
    @POST
    Observable<JSONObject> getRxJavaMultipartData(@Url String path, @Part("param") JSONObject param,
                                                  @Part("file\"; filename=\""+"head.jpg") RequestBody file);

}
