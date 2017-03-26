package com.eju.zejia.utils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;

import org.json.JSONArray;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Sidney on 2016/7/20.
 */
public class Validator {

    public static boolean isEmail(String text) {
        return !TextUtils.isEmpty(text)
                && Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    public static boolean isNullOrBlank(String text) {
        return null == text || TextUtils.isEmpty(text.trim());
    }

    public static boolean isNotNullOrBlank(String text) {
        return !isNullOrBlank(text);
    }

    public static boolean isNullOrEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    public static boolean isNullOrEmpty(Bundle bundle) {
        return null == bundle || bundle.isEmpty();
    }

    public static boolean isNullOrEmpty(JSONArray array) {
        return null == array || array.length() == 0;
    }

    public static boolean isNullOrEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }
}
