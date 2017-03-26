package com.eju.zejia.data.local;

import android.content.Context;

import com.eju.zejia.config.Constants;
import com.eju.zejia.utils.IOUtil;
import com.eju.zejia.utils.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import timber.log.Timber;

/**
 * Created by Sidney on 2016/8/3.
 */
public class CacheService {

    public static final String CACHE_FOLDER_NAME = "zejia";
    public static final String CACHE_MATCH_COMMUNITY = "match_community";
    public static final String CACHE_FILTER_LIST = "filter_list";

    public void saveMatchCommunity(Context context, JSONObject jsonObject) {
        save(context, CACHE_MATCH_COMMUNITY, jsonObject.toString());
    }

    public JSONObject getMatchCommunity(Context context) {
        String result = load(context, CACHE_MATCH_COMMUNITY);
        if (null == result) return null;
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }

    public void saveFilterList(Context context, JSONObject jsonObject) {
        save(context, CACHE_FILTER_LIST, jsonObject.toString());
    }

    public JSONObject getFilterList(Context context) {
        String result = load(context, CACHE_FILTER_LIST);
        if (null == result) return null;
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            return null;
        }
    }


    private String getRealKey(String key) {
        return String.format("%s~%s", key, Constants.VERSION);
    }

    private File checkDir(Context context) {
        File dir = new File(context.getCacheDir(), CACHE_FOLDER_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private void save(Context context, String key, String value) {
        File dir = checkDir(context);
        File file = new File(dir, getRealKey(key));
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            IOUtil.writeText(value, new FileOutputStream(file));
        } catch (IOException e) {
            Timber.e(e, "File %s not found", key);
        }
    }

    private String load(Context context, String key) {
        File dir = checkDir(context);
        File file = new File(dir, getRealKey(key));
        try {
            if (!file.exists()) {
                return null;
            }
            IOUtil.readText(new FileInputStream(file));
        } catch (IOException e) {
            Timber.e(e, "File %s not found", key);
        }
        return null;
    }
}
