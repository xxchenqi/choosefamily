package com.eju.zejia.ui.activities;

import android.os.Bundle;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.GetVersionBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.map.LocaterSingle;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberStartListener;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:启动页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/25 16:20
 * ----------------------------------------
 */
public class SplashActivity extends BaseActivity {
    //等待时间
    private final long SLEEP = 1000;
    //是否是第一次启动应用
    private boolean is_first;
    //是否登录过
    private boolean is_login;
    //是否设置过
    private boolean is_setting;
    //择家服务
    private ZejiaService zejiaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zejiaService = DataManager.getInstance().getZejiaService();
        is_first = PrefUtil.getBoolean(this,
                UIUtils.getString(R.string.prefs_is_first), true);
        is_login = PrefUtil.getBoolean(this,
                UIUtils.getString(R.string.prefs_is_login), false);
        is_setting = PrefUtil.getBoolean(this,
                UIUtils.getString(R.string.prefs_is_setting), false);
        //初始化定位
        LocaterSingle.getInstance().init(getApplicationContext(), true);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                prepareRemoteGetVersion();
            }
        }.run();
    }

    /**
     * 获取版本请求
     */
    private void prepareRemoteGetVersion() {
        zejiaService.doRequest(this, Constants.API.GET_VERSION, null,
                new RxProgressSubscriberStartListener<JSONObject>() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        jump();
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("msg");
                        JSONObject response = jsonObject.optJSONObject("response");

                        if (null == response) {
                            jump();
                            return;
                        }
                        if (code == 200) {
                            GetVersionBean getVersionBean = GsonFactory
                                    .changeGsonToBean(response.toString(), GetVersionBean.class);
                            if (getVersionBean == null) {
                                jump();
                                return;
                            }
                            //保存版本号
                            PrefUtil.putString(SplashActivity.this,
                                    UIUtils.getString(R.string.prefs_version),
                                    getVersionBean.getVersion());
                            PrefUtil.putString(SplashActivity.this,
                                    UIUtils.getString(R.string.prefs_tagVersion),
                                    getVersionBean.getTagVersion());
                            PrefUtil.putString(SplashActivity.this,
                                    UIUtils.getString(R.string.prefs_filterVersion),
                                    getVersionBean.getFilterVersion());
                            PrefUtil.putString(SplashActivity.this,
                                    UIUtils.getString(R.string.prefs_desc),
                                    getVersionBean.getDesc());
                            PrefUtil.putString(SplashActivity.this,
                                    UIUtils.getString(R.string.prefs_url),
                                    getVersionBean.getUrl());
                        }
                        jump();
                    }
                });
    }

    private void jump() {
        if (is_first) {
            //第一次启动应用跳引导页
            PrefUtil.putBoolean(this,
                    UIUtils.getString(R.string.prefs_is_first), false);
            navigator.to(GuideActivity.class);
        } else {
            if (is_login) {
                if (is_setting) {
                    //登陆并且设置过跳首页
                    navigator.to(MainActivity.class);
                } else {
                    navigator.to(CommunityFeatureActivity.class);
                }

            } else {
                //未登陆过跳选择登录页
                navigator.to(ChooseLoginActivity.class);
            }
        }
        finish();
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_splash);
    }
}
