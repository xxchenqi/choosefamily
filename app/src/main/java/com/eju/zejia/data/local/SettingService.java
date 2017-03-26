package com.eju.zejia.data.local;

import android.content.Context;

import com.eju.zejia.R;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;


public class SettingService {

    public static final String PREFS_VERSION = UIUtils.getString(R.string.prefs_version);

    public static void setVersion(Context context, String version) {
        PrefUtil.putString(context, PREFS_VERSION, version);
    }

    public static String getVersion(Context context) {
        return PrefUtil.getString(context, PREFS_VERSION, null);
    }
}
