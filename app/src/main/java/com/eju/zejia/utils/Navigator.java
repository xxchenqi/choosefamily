package com.eju.zejia.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.eju.zejia.common.RequestCodeConstant;
import com.eju.zejia.ui.activities.PersonalInformationActivity;

import java.util.List;

/**
 * 路由工具类
 * <p>
 * Created by Sidney on 2016/7/19.
 */
public class Navigator {

    private Context context;

    public Navigator(Context context) {
        this.context = context;
    }

    public static Navigator newInstance(Context context) {
        return new Navigator(context);
    }

    public Navigator to(Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
        return this;
    }

    public Navigator to(Class<? extends Activity> clazz, Intent intent) {
        intent.setClass(context, clazz);
        context.startActivity(intent);
        return this;
    }

    public Navigator to(Class<? extends Activity> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        context.startActivity(intent);
        return this;
    }

    public Navigator toIntent(Intent intent) {
        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (Validator.isNullOrEmpty(infos)) {
            return this;
        }
        context.startActivity(intent);
        return this;
    }

    public Navigator toForResult(Class<? extends Activity> clazz, int requestCode, Bundle bundle) {
        if (!(context instanceof Activity)) {
            return this;
        }
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
        return this;
    }

    public Navigator to(Class<? extends Activity> clazz, View sharedElement, String sharedName) {
        if (!(context instanceof Activity)) {
            return this;
        }
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, clazz);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        sharedElement, sharedName);
        ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
        return this;
    }

    public void end() {
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    public void endWith(Class<? extends Activity> clazz) {
        to(clazz)
                .end();
    }
}