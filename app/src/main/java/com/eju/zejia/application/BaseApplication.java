package com.eju.zejia.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;

import com.baidu.mapapi.SDKInitializer;
import com.eju.zejia.BuildConfig;
import com.eju.zejia.common.SharedPreferencesConstant;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

import matrix.sdk.count.UniqueID;
import matrix.sdk.count.WeimiCount;
import matrix.sdk.count.WeimiCountConfiguration;
import timber.log.Timber;

public class BaseApplication extends Application {

    private static BaseApplication mContext;

    public static Activity mForegroundActivity;

    private static Handler mMainHandler;

    private static Thread mMainThread;

    private static int mMainThreadId;

    private static Looper mMainThreadLooper;
    //TODO 统计APPID
    private static final String CountAppId = "1066663095";

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        mContext = (BaseApplication) getApplicationContext();
        mMainHandler = new Handler();
        mMainThread = Thread.currentThread();
        mMainThreadId = android.os.Process.myTid();
        mMainThreadLooper = getMainLooper();
        //初始化友盟
        initUmeng();
        initCount();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initUmeng() {
        PlatformConfig.setWeixin("wx82fa9c841fcad712",
                "6840ef4e0e8dd3a5210d31864eaa8367");
        PlatformConfig.setQQZone("1105501439", "2ilVV55hBQ2VBhSm");
        PlatformConfig.setSinaWeibo("742880904",
                "734910af7e4863092b7bfc0d17441024");
    }
    /**
     * 初始化统计
     */
    private void initCount() {
        String channelId = getMetaDataValue("CHANNEL_NAME");//渠道号
        if(StringUtils.isNotNullString(channelId)){
            WeimiCountConfiguration.Builder builder = new WeimiCountConfiguration.Builder(
                    getApplicationContext());
            builder.setAppId(CountAppId);
            builder.setDeviceId(UniqueID.getNDeviceID(getApplicationContext()));
            builder.setChannelId(channelId);
            WeimiCount.getInstance().init(builder.build());
            PrefUtil.putBoolean(UIUtils.getContext(),
                    SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                    true);
        }
    }


    /**
     * 获取全局上下文
     *
     * @return
     */
    public static BaseApplication getApplication() {
        return mContext;
    }

    /**
     * 获取主线程handler
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
        return mMainHandler;
    }

    /**
     * 获取主线程
     *
     * @return
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 获取主线程id
     *
     * @return
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取主线程轮询器
     *
     * @return
     */
    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }


    public static Activity getmForegroundActivity() {
        return mForegroundActivity;
    }

    public static void setmForegroundActivity(Activity mForegroundActivity) {
        BaseApplication.mForegroundActivity = mForegroundActivity;
    }

    // 获取清单文件的metaData
    private String getMetaDataValue(String key) {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            return info.metaData.getString(key);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
