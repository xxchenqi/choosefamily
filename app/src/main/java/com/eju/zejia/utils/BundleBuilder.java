package com.eju.zejia.utils;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Sidney on 2016/03/25.
 */
public class BundleBuilder {

    private Bundle bundle;

    public BundleBuilder() {
        this.bundle = new Bundle();
    }

    public static BundleBuilder newBuilder() {
        return new BundleBuilder();
    }

    public BundleBuilder putIfNotNull(String key, Object value) {
        if (value == null) return this;
        return put(key, value);
    }

    public BundleBuilder put(String key, Object value) {
        if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof ArrayList) {
            bundle.putStringArrayList(key, (ArrayList<String>) value);
        }
        return this;
    }

    public Bundle build() {
        return bundle;
    }
}
