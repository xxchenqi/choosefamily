package com.eju.zejia.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;

import java.util.List;
import java.util.Locale;

/**
 * 设备信息工具类
 * <p>
 * Created by Sidney on 2016/7/18.
 */
public class DeviceUtil {
    private DeviceUtil() {
    }

    public static boolean isSDCardEnabled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        boolean hasPermission = packageManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName())
                == PackageManager.PERMISSION_GRANTED;
        if (hasPermission && Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
            return Environment.getExternalStorageDirectory() != null;
        return false;
    }

    public static boolean isActivitySupported(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        return resolveInfoList.size() > 0;
    }

    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}
