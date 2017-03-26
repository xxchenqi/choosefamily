package com.eju.zejia.utils;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by Sidney on 2016/7/18.
 */
public class CommonUtil {
    private CommonUtil() {
    }

    public static float dip2px(Context context, float dipValue) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dipValue * density + 0.5f;
    }

    public static float px2dip(Context context, float pxValue) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return pxValue / density + 0.5f;
    }

    public static String convertFloat(float value) {
        Number number = value;
        if (number.intValue() == number.floatValue()) {
            return "" + number.intValue();
        }
        return String.format(Locale.ENGLISH, "%.2f", value);
    }

}
