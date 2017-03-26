package com.eju.zejia.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.eju.zejia.R;
import com.eju.zejia.camera.CameraDialog;
import com.eju.zejia.camera.CameraImage;
import com.eju.zejia.camera.ResultCameraHandler;
import com.eju.zejia.common.ResultCodeConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.control.OtherLoginControl;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.UploadPhotoBean;
import com.eju.zejia.data.models.UserInfoBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityPersonalInformationBinding;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAllListener;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.io.File;

/**
 * ----------------------------------------
 * 注释:个人信息
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/29 14:00
 * ----------------------------------------
 */
public class PersonalInformationActivity extends BaseActivity {
    public static boolean REFRESH = false;

    //数据绑定器
    private ActivityPersonalInformationBinding bind;
    //择家服务
    private ZejiaService zejiaService;
    //用户id
    private String sessionId;
    //友盟
    private UMShareAPI mShareAPI;
    // 照相Image
    private CameraImage cameraImage;
    //手机号
    private String phoneNumber;
    //qq
    private String qq;
    //微信
    private String weixin;
    //微博
    private String weibo;
    //权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        REFRESH = false;
        mShareAPI = UMShareAPI.get(this);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_personal_information);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        sessionId = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_sessionId), "");
        qq = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_qq), "");
        weixin = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_weixin), "");
        weibo = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_weibo), "");
        cameraImage = new CameraImage(this);
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        prepareRemoteGetUserInfo();
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_personal_information);
    }

    /**
     * 获取用户信息请求
     */
    private void prepareRemoteGetUserInfo() {
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();


        zejiaService.doRequest(this, Constants.API.GET_USER_INFO, data,
                new RxProgressSubscriberAllListener<JSONObject>() {
                    @Override
                    public void onError(Throwable e) {
                        //从缓存中拿
                        UIUtils.showLongToastSafe("网络请求失败");
                        getCache();
                        click();
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("msg");
                        JSONObject response = jsonObject.optJSONObject("response");
                        if (code == 200) {
                            UserInfoBean userInfoBean = GsonFactory
                                    .changeGsonToBean(response.toString(), UserInfoBean.class);
                            bind.tvNickname.setText(userInfoBean.getNickname());
                            bind.tvPhone.setText(userInfoBean.getMobile());
                            phoneNumber = userInfoBean.getMobile();
                            if (StringUtils.isNotNullString(userInfoBean.getPhotoUrl())) {
                                Glide.with(PersonalInformationActivity.this)
                                        .load(userInfoBean.getPhotoUrl())
                                        .error(R.drawable.default_avatar)
                                        .into(bind.cvHead);
                            }
                            qq = userInfoBean.getQq();
                            weixin = userInfoBean.getWeixin();
                            weibo = userInfoBean.getWeibo();
                            if (StringUtils.isNotNullString(userInfoBean.getQq())) {
                                bind.tvQq.setText(UIUtils.getString(R.string.bounded));
                                bind.tvQq.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            } else {
                                bind.tvQq.setText(UIUtils.getString(R.string.unbounded));
                                bind.tvQq.setTextColor(UIUtils.getColor(R.color.gray_999999));
                            }
                            if (StringUtils.isNotNullString(userInfoBean.getWeixin())) {
                                bind.tvWx.setText(UIUtils.getString(R.string.bounded));
                                bind.tvWx.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            } else {
                                bind.tvWx.setText(UIUtils.getString(R.string.unbounded));
                                bind.tvWx.setTextColor(UIUtils.getColor(R.color.gray_999999));
                            }
                            if (StringUtils.isNotNullString(userInfoBean.getWeibo())) {
                                bind.tvSina.setText(UIUtils.getString(R.string.bounded));
                                bind.tvSina.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            } else {
                                bind.tvSina.setText(UIUtils.getString(R.string.unbounded));
                                bind.tvSina.setTextColor(UIUtils.getColor(R.color.gray_999999));
                            }
                            savePref(userInfoBean);
                            click();
                        } else {
                            UIUtils.showLongToastSafe(message);
                        }
                    }
                });


    }


    /**
     * 账号绑定请求
     */
    private void prepareRemoteUpdateBindAccount(String type, String value) {
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("type", type)//账号类型,值可以为”weixin”，”qq”,”weibo”之一
                .putAlways("value", value)//账号(第三方id)
                .build();
        zejiaService.doRequest(this, Constants.API.UPDATE_BIND_ACCOUNT, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");

                    if (code == 200) {
                        //保存数据库状态
                        if ("weixin".equals(type)) {
                            bind.tvWx.setText("已绑定");
                            bind.tvWx.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            weixin="ok";

                        } else if ("qq".equals(type)) {
                            bind.tvQq.setText("已绑定");
                            bind.tvQq.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            qq="ok";
                        } else {
                            bind.tvSina.setText("已绑定");
                            bind.tvSina.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
                            weibo="ok";
                        }
                        UIUtils.showLongToastSafe("绑定成功");
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


    /**
     * 上传头像请求
     */
    private void prepareRemoteUploadPhoto(File imageUri) {
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();

        zejiaService.doRequestFormData(this, Constants.API.UPLOAD_PHOTO, data, jsonObject -> {

            int code = jsonObject.optInt("code");
            String message = jsonObject.optString("msg");
            JSONObject response = jsonObject.optJSONObject("response");

            if (code == 200) {
                UploadPhotoBean uploadPhotoBean = GsonFactory
                        .changeGsonToBean(response.toString(), UploadPhotoBean.class);
                if (uploadPhotoBean == null) {
                    return;
                }
                PrefUtil.putString(this,
                        UIUtils.getString(R.string.prefs_photoUrl),
                        uploadPhotoBean.getImgPath());
                if (StringUtils.isNotNullString(uploadPhotoBean.getImgPath())) {
                    Glide.with(PersonalInformationActivity.this).load(uploadPhotoBean.getImgPath())
                            .error(R.drawable.default_avatar)
                            .into(bind.cvHead);
                }
                UIUtils.showLongToastSafe("修改头像成功");
                REFRESH = true;
            } else {
                UIUtils.showLongToastSafe(message);
            }
        }, imageUri);
    }


    /**
     * 退出登录请求
     */
    private void prepareRemoteLogoff() {
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();
        zejiaService.doRequest(this, Constants.API.LOG_OFF, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");

                    if (code == 200) {
                        CountControl.getInstance().loginOut(false);
                        removePref();
                        navigator.to(ChooseLoginActivity.class);
                        MyActivityManager.getInstance().finishAllActivity();
                        finish();
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


    /**
     * 点击事件
     */
    private void click() {
        //上传头像
        bind.cvHead.setOnClickListener(v -> {
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            } else {
                new CameraDialog(this, cameraImage).creatView();
            }

        });
        //修改昵称
        bind.llNickname.setOnClickListener(v -> {
            Intent intent_name = new Intent(this, ModifyNickNameActivity.class);
            startActivityForResult(intent_name, 1);
        });
        //修改密码
        bind.llPassword.setOnClickListener(v -> {
            if (StringUtils.isNotNullString(phoneNumber)) {
                navigator.to(ModifyPasswordActivity.class);
            } else {
                UIUtils.showLongToastSafe("第三方登录不能修改密码");
            }
        });
        //绑定微信
        bind.llWx.setOnClickListener(v -> {
            if (StringUtils.isNullString(weixin)) {
                OtherLoginControl.binding(this, mShareAPI,
                        SHARE_MEDIA.WEIXIN);
            } else {
                UIUtils.showLongToastSafe("已绑定微信");
            }
        });
        //绑定qq
        bind.llQq.setOnClickListener(v -> {
            if (StringUtils.isNullString(qq)) {
                OtherLoginControl.binding(this, mShareAPI,
                        SHARE_MEDIA.QQ);
            } else {
                UIUtils.showLongToastSafe("已绑定qq");
            }
        });
        //绑定微博
        bind.llSina.setOnClickListener(v -> {
            if (StringUtils.isNullString(weibo)) {
                OtherLoginControl.binding(this, mShareAPI,
                        SHARE_MEDIA.SINA);
            } else {
                UIUtils.showLongToastSafe("已绑定微博");
            }
        });

        //退出登录
        bind.llExitLogin.setOnClickListener(v -> prepareRemoteLogoff());
    }


    /**
     * 清除数据
     */
    private void removePref() {
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_is_login));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_is_setting));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_sessionId));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_nickname));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_photoUrl));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_mobile));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_weixin));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_qq));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_weibo));
    }

    /**
     * 保存数据
     *
     * @param userInfoBean
     */
    private void savePref(UserInfoBean userInfoBean) {
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_nickname),
                userInfoBean.getNickname());
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_photoUrl),
                userInfoBean.getPhotoUrl());
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_mobile),
                userInfoBean.getMobile());
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_weibo),
                userInfoBean.getWeixin());
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_qq),
                userInfoBean.getQq());
        PrefUtil.putString(this,
                UIUtils.getString(R.string.prefs_weibo),
                userInfoBean.getWeibo());
    }

    /**
     * 获取缓存数据
     */
    private void getCache() {
        phoneNumber = PrefUtil.getString(PersonalInformationActivity.this,
                UIUtils.getString(R.string.prefs_mobile), "");
        bind.tvNickname.setText(PrefUtil.getString(PersonalInformationActivity.this,
                UIUtils.getString(R.string.prefs_nickname), ""));
        bind.tvPhone.setText(phoneNumber);
        String photoUrl = PrefUtil.getString(PersonalInformationActivity.this,
                UIUtils.getString(R.string.prefs_photoUrl), "");
        if (StringUtils.isNotNullString(photoUrl)) {
            Glide.with(PersonalInformationActivity.this).
                    load(photoUrl).error(R.drawable.default_avatar).into(bind.cvHead);
        }
        if (StringUtils.isNotNullString(
                PrefUtil.getString(PersonalInformationActivity.this,
                        UIUtils.getString(R.string.prefs_qq), ""))) {
            bind.tvQq.setText(UIUtils.getString(R.string.bounded));
            bind.tvQq.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
        } else {
            bind.tvQq.setText(UIUtils.getString(R.string.unbounded));
            bind.tvQq.setTextColor(UIUtils.getColor(R.color.gray_999999));
        }
        if (StringUtils.isNotNullString(PrefUtil.getString(
                PersonalInformationActivity.this,
                UIUtils.getString(R.string.prefs_weixin), ""))) {
            bind.tvWx.setText(UIUtils.getString(R.string.bounded));
            bind.tvWx.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
        } else {
            bind.tvWx.setText(UIUtils.getString(R.string.unbounded));
            bind.tvWx.setTextColor(UIUtils.getColor(R.color.gray_999999));
        }
        if (StringUtils.isNotNullString(PrefUtil.getString(
                PersonalInformationActivity.this,
                UIUtils.getString(R.string.prefs_weibo), ""))) {
            bind.tvSina.setText(UIUtils.getString(R.string.bounded));
            bind.tvSina.setTextColor(UIUtils.getColor(R.color.orange_FF7700));
        } else {
            bind.tvSina.setText(UIUtils.getString(R.string.unbounded));
            bind.tvSina.setTextColor(UIUtils.getColor(R.color.gray_999999));
        }
    }

    //绑定
    public void refresh() {
        prepareRemoteUpdateBindAccount(OtherLoginControl.third_source, OtherLoginControl.THIRD_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        // 设置头像返回
        if (requestCode == CameraImage.CAMERA_WITH_DATA
                || requestCode == CameraImage.PHOTO_REQUEST_CUT
                || requestCode == CameraImage.PHOTO_REQUEST_GALLERY) {
            ResultCameraHandler
                    .getInstance()
                    .setCrop(true)
                    .getPhotoFile(this,
                            data, requestCode, resultCode, cameraImage,
                            imageUri -> uploadingHeadImage(imageUri));
        } else if (resultCode == ResultCodeConstant.RESULT_NICKNAME) {
            //昵称保存返回
            bind.tvNickname.setText(PrefUtil.getString(this,
                    UIUtils.getString(R.string.prefs_nickname),
                    ""));

        }
    }

    /**
     * 上传
     *
     * @param imageUri 图片文件流
     */
    private void uploadingHeadImage(File imageUri) {
        prepareRemoteUploadPhoto(imageUri);
    }

}
