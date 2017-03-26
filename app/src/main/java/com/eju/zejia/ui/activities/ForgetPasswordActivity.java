package com.eju.zejia.ui.activities;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.eju.zejia.databinding.ActivityForgetPasswordBinding;
import com.eju.zejia.utils.InputValidate;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:忘记密码页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/26 11:39
 * ----------------------------------------
 */
public class ForgetPasswordActivity extends BaseActivity {
    //数据绑定器
    private ActivityForgetPasswordBinding binding;
    //手机号
    private String phoneNumber;
    //验证码
    private String verifyNumber;
    //计时器
    private TimeCount timeCount;
    //择家服务
    private ZejiaService zejiaService;
    //edittext更改的手机
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        timeCount = new TimeCount(60000, 1000);
        zejiaService = DataManager.getInstance().getZejiaService();
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //下一步
        binding.btnNext.setOnClickListener(v -> verifyCode());
        binding.btnNext.setClickable(false);
        //获取验证码
        binding.btnVerify.setOnClickListener(v -> getVerify());
        binding.btnVerify.setClickable(false);

        //et监听
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                content = s.toString();

                if (StringUtils.isNotNullString(content) && content.length() == 13) {
                    binding.btnVerify.setTextColor(UIUtils.getColor(R.color.black_333333));
                    binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_get_verify);
                    binding.btnVerify.setClickable(true);
                } else {
                    binding.btnVerify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_count_verify);
                    binding.btnVerify.setClickable(false);
                }

                if (StringUtils.isNotNullString(verifyNumber) && verifyNumber.length() == 4
                        && StringUtils.isNotNullString(content) && content.length() == 13) {
                    binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
                    binding.btnNext.setBackgroundResource(R.drawable.bg_btn_login);
                    binding.btnNext.setClickable(true);
                } else {
                    binding.btnNext.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnNext.setBackgroundResource(R.drawable.bg_btn_no_login);
                    binding.btnNext.setClickable(false);
                }

                if (content.length() == 4) {
                    if (content.substring(3).equals(" ")) {
                        content = content.substring(0, 3);
                        binding.etPhone.setText(content);
                        binding.etPhone.setSelection(content.length());
                    } else {
                        content = content.substring(0, 3) + " " + content.substring(3);
                        binding.etPhone.setText(content);
                        binding.etPhone.setSelection(content.length());
                    }
                } else if (content.length() == 9) {
                    if (content.substring(8).equals(" ")) { // -
                        content = content.substring(0, 8);
                        binding.etPhone.setText(content);
                        binding.etPhone.setSelection(content.length());
                    } else {// +
                        content = content.substring(0, 8) + " " + content.substring(8);
                        binding.etPhone.setText(content);
                        binding.etPhone.setSelection(content.length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.etVerify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                verifyNumber = s.toString();
                int length = verifyNumber.length();

                if (StringUtils.isNotNullString(verifyNumber) && length == 4 &&
                        content.length() == 13) {
                    binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
                    binding.btnNext.setBackgroundResource(R.drawable.bg_btn_login);
                    binding.btnNext.setClickable(true);
                } else {
                    binding.btnNext.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                    binding.btnNext.setBackgroundResource(R.drawable.bg_btn_no_login);
                    binding.btnNext.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_forget_password);
    }

    /**
     * 校验验证码
     */
    private void verifyCode() {
        //下一步,验证验证码
        phoneNumber = content.toString().replaceAll(" ", "");
        verifyNumber = binding.etVerify.getText().toString();

        if (InputValidate.checkedIsTelephone(phoneNumber)) {
            if (InputValidate.checkVierfyNumber(verifyNumber)) {
                prepareRemoteCheckVerifyInfo();
            } else {
                UIUtils.showToastSafe(
                        UIUtils.getString(R.string.label_please_input_true_verify_number));
            }
        } else {
            UIUtils.showToastSafe(UIUtils.getString(R.string.label_please_input_true_phone));
        }
    }

    /**
     * 校验验证码请求
     */
    private void prepareRemoteCheckVerifyInfo() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("mobile", phoneNumber)
                .putAlways("vCode", verifyNumber)
                .build();
        zejiaService.doRequest(this, Constants.API.CHECK_VCODE, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
                        Bundle bundle = new Bundle();
                        bundle.putString(ExtraConstant.EXTRA_PHONE_NUMBER, phoneNumber);
                        navigator.to(ForgetSetPasswordActivity.class, bundle);
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }

    /**
     * 获取验证码
     */
    private void getVerify() {

        phoneNumber = binding.etPhone.getText().toString().replaceAll(" ", "");
        verifyNumber = binding.etVerify.getText().toString();

        if (InputValidate.checkedIsTelephone(phoneNumber)) {
            timeCount.start();
            prepareRemoteGetVerifyInfo();
        } else {
            UIUtils.showToastSafe(UIUtils.getString(R.string.label_please_input_true_phone));
        }

    }

    /**
     * 获取验证码请求
     */
    private void prepareRemoteGetVerifyInfo() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("mobile", phoneNumber)
                .putAlways("type", 2)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_VCODE, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                        timeCount.cancel();
                        binding.btnVerify.setText(UIUtils.getString(R.string.get_verify_code));
                        binding.btnVerify.setTextColor(UIUtils.getColor(R.color.black_333333));
                        binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_get_verify);
                        binding.btnVerify.setClickable(true);
                    }
                });
    }

    /**
     * 计时器类
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            phoneNumber = content.replace(" ", "");
            binding.btnVerify.setText(UIUtils.getString(R.string.get_verify_code));
            if (StringUtils.isNotNullString(content) && content.length() == 13) {
                binding.btnVerify.setTextColor(UIUtils.getColor(R.color.black_333333));
                binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_get_verify);
                binding.btnVerify.setClickable(true);
            } else {
                binding.btnVerify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_count_verify);
                binding.btnVerify.setClickable(false);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            binding.btnVerify.setClickable(false);
            binding.btnVerify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
            binding.btnVerify.setBackgroundResource(R.drawable.bg_btn_count_verify);
            binding.btnVerify.setText(String.format(UIUtils.getString(R.string.second),
                    String.valueOf(millisUntilFinished / 1000)));
        }
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
