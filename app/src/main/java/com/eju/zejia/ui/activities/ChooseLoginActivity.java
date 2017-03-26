package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.control.OtherLoginControl;
import com.eju.zejia.databinding.ActivityChooseLoginBinding;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.UIUtils;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * ----------------------------------------
 * 注释: 选择登录页
 * <p>
 * 作者: cq
 * <p>
 * 时间: 2016/7/25 17:07
 * ----------------------------------------
 */
public class ChooseLoginActivity extends BaseActivity {
    //友盟API
    private UMShareAPI mShareAPI;
    //数据绑定器
    private ActivityChooseLoginBinding binding;
    //登录成功的返回码
    public static int RESULT_CODE_FROM_LOGIN_SUCCESS_ACT = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        mShareAPI = UMShareAPI.get(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_login);
        //微信登录
        binding.btnWxLogin.setOnClickListener(view ->
                OtherLoginControl.login(this, mShareAPI, SHARE_MEDIA.WEIXIN));
        //新浪微博登录
        binding.btnSinaLogin.setOnClickListener(view ->
                OtherLoginControl.login(this, mShareAPI, SHARE_MEDIA.SINA));
        //QQ登录
        binding.btnQqLogin.setOnClickListener(view ->
                OtherLoginControl.login(this, mShareAPI, SHARE_MEDIA.QQ));
        //手机登录
        binding.btnPhoneLogin.setOnClickListener(view -> navigator.to(LoginActivity.class));
        //用户隐私
        binding.tvUserPrivacy.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString(WebViewActivity.EXTRA_TITLE, "用户协议");
            bundle.putString(WebViewActivity.EXTRA_URL, Constants.API.AGREEMENT);
            navigator.to(WebViewActivity.class, bundle);
        });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_choose_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
