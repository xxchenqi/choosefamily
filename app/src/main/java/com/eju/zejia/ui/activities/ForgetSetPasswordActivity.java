package com.eju.zejia.ui.activities;

import android.content.Context;
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
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityForgetSetPasswordBinding;
import com.eju.zejia.utils.InputValidate;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:忘记密码-设置密码页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/26 11:47
 * ----------------------------------------
 */
public class ForgetSetPasswordActivity extends BaseActivity {
    //数据绑定器
    private ActivityForgetSetPasswordBinding binding;
    //择家服务
    private ZejiaService zejiaService;
    //手机号
    private String phoneNumber;
    //密码
    private String password;
    //重复密码
    private String password_again;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_set_password);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //确认修改
        binding.btnModify.setOnClickListener(v -> modifyPassword());
        binding.btnModify.setClickable(false);
        //et监听
        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
                if (StringUtils.isNotNullString(password) &&
                        StringUtils.isNotNullString(password_again)) {
                    binding.btnModify.setTextColor(UIUtils.getColor(R.color.brown_693119));
                    binding.btnModify.setBackgroundResource(R.drawable.bg_btn_login);
                    binding.btnModify.setClickable(true);
                } else {
                    binding.btnModify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnModify.setBackgroundResource(R.drawable.bg_btn_no_login);
                    binding.btnModify.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etPasswordAgain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password_again = s.toString();
                if (StringUtils.isNotNullString(password_again) &&
                        StringUtils.isNotNullString(password)) {
                    binding.btnModify.setTextColor(UIUtils.getColor(R.color.brown_693119));
                    binding.btnModify.setBackgroundResource(R.drawable.bg_btn_login);
                    binding.btnModify.setClickable(true);
                } else {
                    binding.btnModify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnModify.setBackgroundResource(R.drawable.bg_btn_no_login);
                    binding.btnModify.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_forget_set_password);
    }

    /**
     * 修改密码
     */
    private void modifyPassword() {
        String password = binding.etPassword.getText().toString();
        String password_again = binding.etPasswordAgain.getText().toString();

        if (password.equals(password_again)) {
            if (InputValidate.checkPassword(password)) {
                prepareRemoteRegisterInfo(password);
            } else {
                UIUtils.showToastSafe(getResources().getString(
                        R.string.label_login_bePassword));
            }
        } else {
            UIUtils.showLongToastSafe(UIUtils.getString(R.string.label_password_equals));
        }

    }


    /**
     * 注册请求
     *
     * @param password
     */
    private void prepareRemoteRegisterInfo(String password) {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("mobile", phoneNumber)
                .putAlways("pwd", password)
                .putAlways("type", 2)
                .build();
        zejiaService.doRequest(this, Constants.API.MODIFY_PWD, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
                        navigator.to(LoginActivity.class);
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


    @Override
    protected void processArgument(Bundle bundle) {
        super.processArgument(bundle);
        phoneNumber = bundle.getString(ExtraConstant.EXTRA_PHONE_NUMBER);
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
