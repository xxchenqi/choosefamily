package com.eju.zejia.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * AndroidManifest 工具类
 * <p>
 * Created by Sidney on 2016/7/18.
 */
public class ManifestUtil {
    private ManifestUtil() {
    }

    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (null == packageInfo) return null;
        return packageInfo.versionName;
    }

    public static int getOSVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getMetaData(Context context, String key) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (Validator.isNullOrEmpty(applicationInfo.metaData)) {
            return null;
        }
        return applicationInfo.metaData.getString(key);
    }

}
