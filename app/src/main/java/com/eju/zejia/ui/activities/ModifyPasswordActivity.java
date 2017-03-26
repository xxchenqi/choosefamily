package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityModifyPasswordBinding;
import com.eju.zejia.utils.InputValidate;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:修改密码页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/8/4 10:23
 * ----------------------------------------
 */
public class ModifyPasswordActivity extends BaseActivity {
    //数据绑定器
    private ActivityModifyPasswordBinding binding;
    //择家服务
    private ZejiaService zejiaService;
    //手机号
    private String phoneNumber;
    //旧密码
    private String old_password;
    //新密码
    private String new_password;
    //重复密码
    private String again_password;
    //用户id
    private String sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_password);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        phoneNumber = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_mobile), "");
        sessionId = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_sessionId), "");
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //确认修改
        binding.btnModify.setOnClickListener(v ->modifyPassword());
        binding.btnModify.setClickable(false);
        //et监听
        binding.etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                old_password = s.toString();
                checkPassword();
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
                new_password = s.toString();
                checkPassword();
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
                again_password = s.toString();
                checkPassword();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkPassword() {
        if (StringUtils.isNotNullString(old_password) &&
                StringUtils.isNotNullString(new_password) &&
                StringUtils.isNotNullString(again_password)) {
            binding.btnModify.setTextColor(UIUtils.getColor(R.color.brown_693119));
            binding.btnModify.setBackgroundResource(R.drawable.bg_btn_login);
            binding.btnModify.setClickable(true);
        } else {
            binding.btnModify.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
            binding.btnModify.setBackgroundResource(R.drawable.bg_btn_no_login);
            binding.btnModify.setClickable(false);
        }
    }
    /**
     * 修改密码
     */
    private void modifyPassword() {

        if (new_password.equals(again_password)) {
            if (InputValidate.checkPassword(new_password)) {
                prepareRemoteModifyPwd();
            } else {
                UIUtils.showToastSafe(getResources().getString(
                        R.string.label_login_bePassword));
            }
        } else {
            UIUtils.showLongToastSafe(UIUtils.getString(R.string.label_password_equals));
        }

    }

    /**
     * 修改密码请求
     */
    private void prepareRemoteModifyPwd() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("mobile", phoneNumber)
                .putAlways("pwd", new_password)
                .putAlways("oldPwd", old_password)
                .putAlways("type", 1)
                .build();
        zejiaService.doRequest(this, Constants.API.MODIFY_PWD, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
                        UIUtils.showLongToastSafe("密码修改成功");
                        finish();
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_modify_password);
    }
}
