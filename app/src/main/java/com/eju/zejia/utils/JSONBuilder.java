package com.eju.zejia.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class JSONBuilder {

    private JSONObject jsonObject = new JSONObject();

    private JSONBuilder() {
    }

    public static JSONBuilder newBuilder() {
        return new JSONBuilder();
    }

    public static JSONBuilder wrap(Map<String, ?> map) {
        JSONBuilder delegate = new JSONBuilder();
        if (null == map) return delegate;
        JSONObject json = new JSONObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                json.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        delegate.jsonObject = json;
        return delegate;
    }

    public static JSONBuilder wrap(String s) {
        JSONBuilder delegate = new JSONBuilder();
        try {
            delegate.jsonObject = new JSONObject(s);
        } catch (JSONException e) {
            delegate.jsonObject = new JSONObject();
        }
        return delegate;
    }

    public int length() {
        return jsonObject.length();
    }

    public JSONBuilder putAlways(String name, Object value) {
        tryCatch(() -> jsonObject.put(name, value));
        return this;
    }

    public JSONBuilder putIfAvailable(String name, Object value) {
        try {
            if (value == null) return this;
            jsonObject.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    public JSONBuilder putIfNotZero(String name, int value) {
        if (value == 0) return this;
        tryCatch(() -> jsonObject.put(name, value));
        return this;
    }

    public JSONBuilder putIfNotNull(String name, Object value) {
        if (value == null) return this;
        tryCatch(() -> jsonObject.put(name, value));
        return this;
    }

    public JSONBuilder putIfNotEmpty(String name, Map<?, ?> value) {
        if (value == null || value.isEmpty()) return this;
        tryCatch(() -> jsonObject.put(name, value));
        return this;
    }

    public JSONBuilder putIfNotEmpty(String name, Collection<?> value) {
        if (value == null || value.isEmpty()) return this;
        tryCatch(() -> jsonObject.put(name, value));
        return this;
    }

    public String asString() {
        if (jsonObject != null) {
            return jsonObject.toString();
        }
        return null;
    }

    private interface Catchable {
        void done() throws JSONException;
    }

    private void tryCatch(Catchable catchable) {
        try {
            catchable.done();
        } catch (JSONException e) {
            throw new RuntimeException();
        }
    }

    public JSONObject build() {
        return jsonObject;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
