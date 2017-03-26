package com.eju.zejia.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson 工厂
 * <p>
 * Created by Sidney on 2016/7/18.
 */
public class GsonFactory {

    private static Gson gson;
    private static final Object MUTEX_INSTANCE = new Object();

    public static Gson newGson() {
        return new GsonBuilder().create();
    }

    public static Gson getGson() {
        synchronized (MUTEX_INSTANCE) {
            if (null == gson) {
                gson = newGson();
            }
            return gson;
        }
    }

    public static <T> T changeGsonToBean(String gsonString, Class<T> cls) {
        if (gson == null) {
            getGson();
        }
        T t = null;
        try {
            t = gson.fromJson(gsonString, cls);
        } catch (Exception e) {
        } finally {
            return t;
        }
    }

}
