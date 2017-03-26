package com.eju.zejia.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.eju.zejia.R;
import com.eju.zejia.application.BaseApplication;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.OtherLoginBean;
import com.eju.zejia.data.models.UserLoginBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAllListener;
import com.eju.zejia.ui.activities.CommunityFeatureActivity;
import com.eju.zejia.ui.activities.MainActivity;
import com.eju.zejia.ui.activities.PersonalInformationActivity;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;


public class OtherLoginControl {
    private static final String Third_Source_QQ = "qq";
    private static final String Third_Source_WX = "weixin";
    private static final String Third_Source_Sina = "weibo";
    public static String third_source;
    public static String THIRD_ID;
    //择家服务
    private static ZejiaService zejiaService;

    /**
     * 注销本次登录
     */
    public static void logout(Activity mActivity, UMShareAPI mShareAPI, final SHARE_MEDIA platform) {
        mShareAPI.deleteOauth(mActivity, platform, new UMAuthListener() {

            @Override
            public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> arg2) {

            }

            @Override
            public void onCancel(SHARE_MEDIA arg0, int arg1) {

            }
        });
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    public static void login(final Activity mActivity, final UMShareAPI mShareAPI, final SHARE_MEDIA platform) {
        if (platform == SHARE_MEDIA.QQ) {
            third_source = Third_Source_QQ;
        } else if (platform == SHARE_MEDIA.WEIXIN) {
            third_source = Third_Source_WX;
        } else if (platform == SHARE_MEDIA.SINA) {
            third_source = Third_Source_Sina;
        }

        mShareAPI.doOauthVerify(mActivity, platform, new UMAuthListener() {

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                String uid;
                String access_token;
                if (platform == SHARE_MEDIA.WEIXIN) {
                    uid = data.get("unionid");//openid
                } else {
                    uid = data.get("uid");
                    access_token = data.get("access_token");
                }
                if (!TextUtils.isEmpty(uid)) {
                    getUserInfo(mActivity, mShareAPI, platform, uid);
                } else {
                    UIUtils.showToastSafe("授权失败...");
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
            }
        });
    }


    /**
     * 获取授权平台的用户信息</br>
     */
    private static void getUserInfo(Activity mActivity, UMShareAPI mShareAPI, final SHARE_MEDIA platform, final String uidtrl) {
        mShareAPI.getPlatformInfo(mActivity, platform, new UMAuthListener() {

            @Override
            public void onError(SHARE_MEDIA arg0, int arg1, Throwable arg2) {
            }

            @Override
            public void onComplete(SHARE_MEDIA arg0, int arg1, Map<String, String> info) {
                if (info != null) {
                    // 修改
                    String uid;
                    String name;
                    String icon;
                    if (platform == SHARE_MEDIA.WEIXIN) {
                        uid = String.valueOf(info.get("unionid"));
                        name = String.valueOf(info.get("nickname"));
                        icon = String.valueOf(info.get("headimgurl"));
                    } else {
                        if (info.size() == 1) {
                            String result = info.get("result");
                            OtherLoginBean otherLoginBean = GsonFactory
                                    .changeGsonToBean(result, OtherLoginBean.class);
                            uid = otherLoginBean.getUid();
                            name = otherLoginBean.getScreen_name();
                            icon = otherLoginBean.getProfile_image_url();
                        } else {
                            uid = String.valueOf(info.get("uid"));
                            name = String.valueOf(info.get("screen_name"));
                            icon = String.valueOf(info
                                    .get("profile_image_url"));
                        }
                    }
                    if (StringUtils.isNullString(uid)) {
                        uid = uidtrl;
                    }
                    //三方登录请求
                    PostOtherLogin(uid, name, icon, third_source);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA arg0, int arg1) {
            }
        });
    }

    private static void PostOtherLogin(String openId, String nickname, String avatarUrl, String type) {
        zejiaService = DataManager.getInstance().getZejiaService();
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("openId", openId)
                .putAlways("nickname", nickname)
                .putAlways("avatarUrl", avatarUrl)
                .putAlways("type", type)
                .build();
        zejiaService.doRequest(BaseApplication.getmForegroundActivity(),
                Constants.API.USER_LOGIN_BY_OTHER, data,
                new RxProgressSubscriberAllListener<JSONObject>() {
                    @Override
                    public void onError(Throwable e) {
                        UIUtils.showLongToastSafe("登录失败");
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("msg");
                        JSONObject response = jsonObject.optJSONObject("response");

                        if (code == 200) {
                            UserLoginBean userLoginBean = GsonFactory
                                    .changeGsonToBean(response.toString(), UserLoginBean.class);
                            if (userLoginBean == null) {
                                return;
                            }
                            //存储参数,成功登录统计
                            Context context = BaseApplication
                                    .getmForegroundActivity();

                            CountControl.getInstance().loginSuccess(userLoginBean.getSessionId());

                            PrefUtil.putBoolean(context,
                                    UIUtils.getString(R.string.prefs_is_login), true);
                            PrefUtil.putString(context,
                                    UIUtils.getString(R.string.prefs_sessionId),
                                    userLoginBean.getSessionId());
                            PrefUtil.putString(context,
                                    UIUtils.getString(R.string.prefs_nickname),
                                    userLoginBean.getNickname());
                            PrefUtil.putString(context,
                                    UIUtils.getString(R.string.prefs_photoUrl),
                                    userLoginBean.getPhotoUrl());

                            int inHome = userLoginBean.getInHome();

                            if (inHome != 0) {
                                BaseApplication.getmForegroundActivity().setResult(Activity.RESULT_OK);
                                BaseApplication.getmForegroundActivity().
                                        startActivity(new Intent(context, MainActivity.class));
                                BaseApplication.getmForegroundActivity().finish();
                            } else {
                                BaseApplication.getmForegroundActivity().setResult(Activity.RESULT_OK);
                                BaseApplication.getmForegroundActivity().
                                        startActivity(new Intent(context, CommunityFeatureActivity.class));
                                BaseApplication.getmForegroundActivity().finish();
                            }

                        } else {
                            UIUtils.showLongToastSafe(message);
                        }
                    }
                }
        );

    }


//==============================第三方账号绑定请求=======================


    /**
     * 绑定
     */
    public static void binding(final Activity mActivity, final UMShareAPI mShareAPI, final SHARE_MEDIA platform) {

        if (platform == SHARE_MEDIA.QQ) {
            third_source = Third_Source_QQ;
        } else if (platform == SHARE_MEDIA.WEIXIN) {
            third_source = Third_Source_WX;
        } else if (platform == SHARE_MEDIA.SINA) {
            third_source = Third_Source_Sina;
        }

        mShareAPI.doOauthVerify(mActivity, platform, new UMAuthListener() {

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                // 登录成功后修改三方ID,回调到activity后去做请求，并刷新界面。
                if (platform == SHARE_MEDIA.WEIXIN) {
                    THIRD_ID = data.get("unionid");
                    ((PersonalInformationActivity) BaseApplication.mForegroundActivity).refresh();
                } else {
                    THIRD_ID = data.get("uid");
                    ((PersonalInformationActivity) BaseApplication.mForegroundActivity).refresh();
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
            }
        });
    }
}
