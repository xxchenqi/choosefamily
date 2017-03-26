package com.eju.zejia.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.eju.zejia.R;
import com.eju.zejia.common.ExtraConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.UserLoginBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityLoginBinding;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.InputValidate;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释: 手机登录页
 * <p>
 * 作者: cq
 * <p>
 * 时间: 2016/7/21 17:28
 * ----------------------------------------
 */
public class LoginActivity extends BaseActivity {
    //数据绑定器
    private ActivityLoginBinding binding;
    //择家服务
    private ZejiaService zejiaService;
    //手机长度
    private int phone_len;
    //密码
    private String password;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String phone = intent.getStringExtra(ExtraConstant.EXTRA_PHONE_NUMBER);
        String password = intent.getStringExtra(ExtraConstant.EXTRA_PASSWORD);
        if (StringUtils.isNotNullString(phone) && StringUtils.isNotNullString(password)) {
            prepareRemoteLoginInfo(phone, password);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //注册
        binding.tvRegister.setOnClickListener(view -> navigator.to(RegisterActivity.class));
        //登录
        binding.btnAffirmLogin.setOnClickListener(view -> login());
        binding.btnAffirmLogin.setClickable(false);
        //忘记密码
        binding.tvForgetPassword.setOnClickListener(view ->
                navigator.to(ForgetPasswordActivity.class));

        //TODO 临时定义
        binding.btnAffirmLogin.setClickable(true);
        binding.btnAffirmLogin.setTextColor(UIUtils.getColor(R.color.brown_693119));
        binding.btnAffirmLogin.setBackgroundResource(R.drawable.bg_btn_login);
        binding.etPhone.setText("130 2016 9902");
        binding.etPassword.setText("111111");

        //et监听
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contents = s.toString();
                phone_len = s.toString().length();
                check();

                if (phone_len == 4) {
                    if (contents.substring(3).equals(" ")) {
                        contents = contents.substring(0, 3);
                        binding.etPhone.setText(contents);
                        binding.etPhone.setSelection(contents.length());
                    } else {
                        contents = contents.substring(0, 3) + " " + contents.substring(3);
                        binding.etPhone.setText(contents);
                        binding.etPhone.setSelection(contents.length());
                    }
                } else if (phone_len == 9) {
                    if (contents.substring(8).equals(" ")) { // -
                        contents = contents.substring(0, 8);
                        binding.etPhone.setText(contents);
                        binding.etPhone.setSelection(contents.length());
                    } else {// +
                        contents = contents.substring(0, 8) + " " + contents.substring(8);
                        binding.etPhone.setText(contents);
                        binding.etPhone.setSelection(contents.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void check() {
        if (phone_len == 13 && StringUtils.isNotNullString(password)) {
            binding.btnAffirmLogin.setTextColor(UIUtils.getColor(R.color.brown_693119));
            binding.btnAffirmLogin.setBackgroundResource(R.drawable.bg_btn_login);
            binding.btnAffirmLogin.setClickable(true);
        } else {
            binding.btnAffirmLogin.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
            binding.btnAffirmLogin.setBackgroundResource(R.drawable.bg_btn_no_login);
            binding.btnAffirmLogin.setClickable(false);
        }
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_login);
    }

    /**
     * 登录
     */
    public void login() {
        String phone = binding.etPhone.getText().toString().replaceAll(" ", "");
        String password = binding.etPassword.getText().toString();
        if (StringUtils.isNotNullString(phone) &&
                StringUtils.isNotNullString(password)) {
            if (!InputValidate.checkedIsTelephone(phone)) {// 不是电话
                UIUtils.showToastSafe(getResources().getString(
                        R.string.affirm_login));
            } else {
                if (!InputValidate.checkPassword(password)) {
                    UIUtils.showToastSafe(getResources().getString(
                            R.string.label_login_bePassword));
                } else {
                    //登录请求
                    prepareRemoteLoginInfo(phone, password);
                }
            }
        } else {
            UIUtils.showLongToastSafe(
                    UIUtils.getString(R.string.label_login_hasContent));
        }
    }

    /**
     * 登录请求
     *
     * @param phone
     * @param password
     */
    private void prepareRemoteLoginInfo(String phone, String password) {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("mobile", phone)
                .putAlways("pwd", password)
                .build();

        zejiaService.doRequest(this, Constants.API.USER_LOGIN, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");

                    if (code == 200) {
                        UserLoginBean userLoginBean = GsonFactory
                                .changeGsonToBean(response.toString(), UserLoginBean.class);
                        if (userLoginBean == null) {
                            return;
                        }
                        // 存储参数,成功登录统计
                        CountControl.getInstance().loginSuccess(userLoginBean.getSessionId());

                        PrefUtil.putBoolean(LoginActivity.this,
                                UIUtils.getString(R.string.prefs_is_login), true);
                        PrefUtil.putString(LoginActivity.this,
                                UIUtils.getString(R.string.prefs_sessionId),
                                userLoginBean.getSessionId());
                        PrefUtil.putString(LoginActivity.this,
                                UIUtils.getString(R.string.prefs_nickname),
                                userLoginBean.getNickname());
                        PrefUtil.putString(LoginActivity.this,
                                UIUtils.getString(R.string.prefs_photoUrl),
                                userLoginBean.getPhotoUrl());

                        int inHome = userLoginBean.getInHome();
                        if (inHome != 0) {
                            //首页
                            MyActivityManager.getInstance().finishAllActivity();
                            PrefUtil.putBoolean(LoginActivity.this,
                                    UIUtils.getString(R.string.prefs_is_setting),
                                    true);
                            navigator.to(MainActivity.class);
                            finish();
                        } else {
                            //社区特色
                            MyActivityManager.getInstance().finishAllActivity();
                            navigator.to(CommunityFeatureActivity.class);
                            finish();
                        }

                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            // 获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

}
