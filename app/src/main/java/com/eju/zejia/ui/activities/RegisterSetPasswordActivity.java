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
import com.eju.zejia.databinding.ActivityRegisterSetPasswordBinding;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.InputValidate;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:手机注册-设置密码界面
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/26 11:07
 * ----------------------------------------
 */
public class RegisterSetPasswordActivity extends BaseActivity {
    //数据绑定器
    private ActivityRegisterSetPasswordBinding binding;
    //密码
    private String password;
    //手机号
    private String phoneNumber;
    //择家服务
    private ZejiaService zejiaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_set_password);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //登录
        binding.tvLogin.setOnClickListener(v -> navigator.to(LoginActivity.class));
        //确认注册
        binding.btnRegister.setOnClickListener(v -> register());
        binding.btnRegister.setClickable(false);
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String contents = s.toString();
                if (StringUtils.isNotNullString(contents)) {
                    binding.btnRegister.setTextColor(UIUtils.getColor(R.color.brown_693119));
                    binding.btnRegister.setBackgroundResource(R.drawable.bg_btn_login);
                    binding.btnRegister.setClickable(true);
                } else {
                    binding.btnRegister.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnRegister.setBackgroundResource(R.drawable.bg_btn_no_login);
                    binding.btnRegister.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_register_set_password);
    }

    @Override
    protected void processArgument(Bundle bundle) {
        super.processArgument(bundle);
        phoneNumber = bundle.getString(ExtraConstant.EXTRA_PHONE_NUMBER);
    }

    /**
     * 注册
     */
    private void register() {
        password = binding.etPassword.getText().toString();
        if (InputValidate.checkPassword(password)) {
            prepareRemoteRegisterInfo();
        } else {
            UIUtils.showToastSafe(getResources().getString(
                    R.string.label_login_bePassword));
        }
    }

    /**
     * 注册请求
     */
    private void prepareRemoteRegisterInfo() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("mobile", phoneNumber)
                .putAlways("pwd", password)
                .build();
        zejiaService.doRequest(this, Constants.API.USER_REG, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");
                    if (null == response) {
                        return;
                    }
                    if (code == 200) {
                        UserLoginBean userLoginBean = GsonFactory
                                .changeGsonToBean(response.toString(), UserLoginBean.class);
                        if (userLoginBean == null) {
                            return;
                        }
                        //注册成功统计
                        CountControl.getInstance().registerSuccess(userLoginBean.getSessionId(),
                                CountControl.Register_Type_Phone);
                        Intent intent = new Intent();
                        intent.putExtra(ExtraConstant.EXTRA_PHONE_NUMBER, phoneNumber);
                        intent.putExtra(ExtraConstant.EXTRA_PASSWORD, password);
                        navigator.to(LoginActivity.class, intent);
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
