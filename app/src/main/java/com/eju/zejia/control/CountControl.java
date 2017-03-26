package com.eju.zejia.control;

import android.app.Activity;

import com.baidu.location.BDLocation;
import com.eju.zejia.R;
import com.eju.zejia.application.BaseApplication;
import com.eju.zejia.common.SharedPreferencesConstant;
import com.eju.zejia.map.LocaterSingle;
import com.eju.zejia.ui.activities.BaseActivity;
import com.eju.zejia.ui.activities.BaseFragmentActivity;
import com.eju.zejia.ui.fragments.BaseFragment;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import matrix.sdk.count.WeimiCount;

/**
 * ----------------------------------------
 * 注释: 统计逻辑
 * <p>
 * 作者: cq
 * <p>
 * 时间: 2016/8/1 18:17
 * ----------------------------------------
 */
public class CountControl {
    // 新浪微博注册
    public static final String Register_Type_Sina = "1";
    // 微信注册
    public static final String Register_Type_Wechat = "2";
    // 手机注册
    public static final String Register_Type_Phone = "3";

    private static CountControl sInstance;
    private boolean isfirstLocation = true;
    private boolean isRuning;
    public long unRunningTime = 0;

    /**
     * 实例化
     */
    public static CountControl getInstance() {
        if (sInstance == null) {
            sInstance = new CountControl();
        }
        return sInstance;
    }

    // 启动app统计
    public void startApp() {
        BDLocation curr_location = LocaterSingle.getInstance().getCurrentLocation();
        if (curr_location != null && isfirstLocation) {
            isfirstLocation = false;
            WeimiCount.getInstance().recordClientStart(
                    curr_location.getLongitude(), curr_location.getLatitude());
        PrefUtil.putBoolean(UIUtils.getContext(),
                SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                true);
    }
    }

    // 注册成功统计
    public void registerSuccess(String uid, String type) {
        BDLocation location = LocaterSingle.getInstance().getCurrentLocation();
        if (location != null) {
            WeimiCount.getInstance().recordRegister(null != uid ? uid : "", type,
                    location.getLongitude(), location.getLatitude());
        }
    }

    // 登录成功统计
    public void loginSuccess(String uid) {
        WeimiCount.getInstance().recordLogin(null != uid ? uid : "");
    }

    /**
     * 登出
     *
     * @param isBackstage true：后台：false：退出
     */
    public void loginOut(boolean isBackstage) {
        String uid = PrefUtil.getString(UIUtils.getContext(),
                UIUtils.getString(R.string.prefs_sessionId), "");
        WeimiCount.getInstance().recordLogout(null != uid ? uid : "", isBackstage);
    }

    // 页面启动BaseActivity
    private void pageStart(BaseActivity activity) {
        String uid = PrefUtil.getString(UIUtils.getContext(),
                UIUtils.getString(R.string.prefs_sessionId), "");
        if (activity != null && StringUtils.isNotNullString(activity.getPageName())) {
            WeimiCount.getInstance()
                    .recordPagepath(uid, activity.getPageName());
        }
    }

    // 页面启动BaseFragment
    private void pageStart(BaseFragment fragment) {
        String uid = PrefUtil.getString(UIUtils.getContext(),
                UIUtils.getString(R.string.prefs_sessionId), "");
        if (fragment != null) {
            if (StringUtils.isNotNullString(fragment.getPageName())) {
                WeimiCount.getInstance()
                        .recordPagepath(uid, fragment.getPageName());
            }
        }
    }

    // 页面启动BaseFragment
    private void pageStart(BaseFragmentActivity activity) {
        String uid = PrefUtil.getString(UIUtils.getContext(),
                UIUtils.getString(R.string.prefs_sessionId), "");
        if (activity != null) {
            if (StringUtils.isNotNullString(activity.getPageName())) {
                WeimiCount.getInstance()
                        .recordPagepath(uid, activity.getPageName());
            }
        }
    }

    /**
     * 切换前台
     */
    public <T> void skipRuning(T t) {
        String uid = PrefUtil.getString(UIUtils.getContext(),
                UIUtils.getString(R.string.prefs_sessionId), "");
        isRuning = PrefUtil.getBoolean(
                UIUtils.getContext(),
                SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                true);
        if (!isRuning) {
            CountControl.getInstance().loginSuccess(null != uid ? uid : "");
            PrefUtil.putBoolean(UIUtils.getContext(),
                    SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                    true);
        }
        if (t instanceof BaseFragment) {
            pageStart((BaseFragment) t);
        } else if (t instanceof BaseActivity) {
            pageStart((BaseActivity) t);
        } else if (t instanceof BaseFragmentActivity && ((BaseFragmentActivity) t).getPageName() != null) {
            pageStart((BaseFragmentActivity) t);
        }
    }


    /**
     * 切换后台
     */
    public void skipUnRuning(Activity activity) {
        if (!BaseApplication.isAppOnForeground(activity)) {
            PrefUtil.putBoolean(UIUtils.getContext(),
                    SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                    false);
            loginOut(true);
        }
    }
}
