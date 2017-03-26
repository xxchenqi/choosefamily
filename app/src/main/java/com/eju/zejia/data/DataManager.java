package com.eju.zejia.data;

import android.content.Context;

import com.eju.zejia.data.local.CacheService;
import com.eju.zejia.data.local.SettingService;
import com.eju.zejia.data.remote.ApiServiceFactory;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.netframe.custom.RequestAPI;

/**
 * Created by Sidney on 2016/7/19.
 */
public final class DataManager {

    private static final Object lock = new Object();
    private static DataManager sInstance;

    private ZejiaService zejiaService;

    private DataManager() {
        ApiServiceFactory factory = new ApiServiceFactory();
        zejiaService = factory.createZejiaService();
    }

    public static DataManager getInstance() {
        synchronized (lock) {
            if (null == sInstance) {
                sInstance = new DataManager();
            }
        }
        return sInstance;
    }

    public ZejiaService getZejiaService() {
        return zejiaService;
    }

    public void requestZejiaService(Context context) {

    }

    public SettingService getSettingService() {
        return new SettingService();
    }

    public CacheService getCacheService() {
        return new CacheService();
    }
}
