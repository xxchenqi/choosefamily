package com.eju.zejia.data.remote;

import android.content.Context;

import com.eju.zejia.netframe.custom.RequestAPI;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberListener;
import com.eju.zejia.utils.DeviceUtil;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.ManifestUtil;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Scheduler;

/**
 * Created by Sidney on 2016/7/18.
 */
public class ZejiaService {

    public void doRequest(Context context, String path, JSONObject data,
                          RxProgressSubscriberListener<JSONObject> listener) {
        JSONObject commonData = JSONBuilder.newBuilder()
                .putAlways("version", ManifestUtil.getVersionName(context))
                .putAlways("os", ManifestUtil.getOSVersion())
                .putAlways("channel", ManifestUtil.getMetaData(context, "CHANNEL_NAME"))
                .putAlways("deviceToken", null)
                .putAlways("plat", "android")
                .putAlways("language", DeviceUtil.getLanguage())
                .build();
        JSONObject requestData = JSONBuilder.newBuilder()
                .putIfNotNull("data", data)
                .putAlways("common", commonData)
                .build();
        RequestAPI.getInstance()
                .doRxRequest(context, listener, path, requestData);
    }

    public void doRequest(Context context, String path, JSONObject data,
                          RxProgressSubscriberListener<JSONObject> listener, Scheduler scheduler) {
        JSONObject commonData = JSONBuilder.newBuilder()
                .putAlways("version", ManifestUtil.getVersionName(context))
                .putAlways("os", ManifestUtil.getOSVersion())
                .putAlways("channel", ManifestUtil.getMetaData(context, "CHANNEL_NAME"))
                .putAlways("deviceToken", null)
                .putAlways("plat", "android")
                .putAlways("language", DeviceUtil.getLanguage())
                .build();
        JSONObject requestData = JSONBuilder.newBuilder()
                .putIfNotNull("data", data)
                .putAlways("common", commonData)
                .build();
        RequestAPI.getInstance()
                .doRxRequest(context, listener, path, requestData, scheduler);
    }

    public void doRequestFormData(Context context, String path, JSONObject data,
                                  RxProgressSubscriberListener<JSONObject> listener, File file) {
        JSONObject commonData = JSONBuilder.newBuilder()
                .putAlways("version", ManifestUtil.getVersionName(context))
                .putAlways("os", ManifestUtil.getOSVersion())
                .putAlways("channel", ManifestUtil.getMetaData(context, "CHANNEL_NAME"))
                .putAlways("deviceToken", null)
                .putAlways("plat", "android")
                .putAlways("language", DeviceUtil.getLanguage())
                .build();
        JSONObject requestData = JSONBuilder.newBuilder()
                .putIfNotNull("data", data)
                .putAlways("common", commonData)
                .build();
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),
                file);
        RequestAPI.getInstance()
                .doRxFormDataRequest(context, listener, path, requestData, requestBody);
    }

}
