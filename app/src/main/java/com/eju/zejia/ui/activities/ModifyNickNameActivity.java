package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;

import com.eju.zejia.R;
import com.eju.zejia.common.ResultCodeConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityModifyNicknameBinding;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

/**
 * ----------------------------------------
 * 注释:修改昵称页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/8/4 10:24
 * ----------------------------------------
 */
public class ModifyNickNameActivity extends BaseActivity {
    //数据绑定器
    private ActivityModifyNicknameBinding binding;
    //择家服务
    private ZejiaService zejiaService;
    //用户id
    private String sessionId;
    //昵称
    private String nickName;
    //输入表情前的光标位置
    private int cursorPos;
    //输入表情前EditText中的文本
    private String inputAfterText;
    //是否重置了EditText的内容
    private boolean resetText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_nickname);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        zejiaService = DataManager.getInstance().getZejiaService();
        sessionId = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_sessionId), "");
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //确认修改
        binding.btnModify.setOnClickListener(v -> prepareRemoteUpdateNickname());
        binding.btnModify.setClickable(false);
        //et监听
        binding.etNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!resetText) {
                    cursorPos = binding.etNickname.getSelectionEnd();
                    // 这里用s.toString()而不直接用s是因为如果用s，
                    // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                    // inputAfterText也就改变了，那么表情过滤就失败了
                    inputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nickName = s.toString();
                checkNickName();
                if (!resetText) {
                    if (count >= 2 && cursorPos < s.length()) {//表情符号的字符长度最小为2
                        CharSequence input = s.subSequence(cursorPos, s.length());
                        if (containsEmoji(input.toString())) {
                            resetText = true;
                            UIUtils.showLongToastSafe("不支持输入Emoji表情符号");
                            //是表情符号就将文本还原为输入表情符号之前的内容
                            binding.etNickname.setText(inputAfterText);
                            CharSequence text = binding.etNickname.getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                if (text.length() == 0) {
                                    Selection.setSelection(spanText, 0);
                                } else {
                                    Selection.setSelection(spanText, text.length() - 1);
                                }
                            }
                        }
                    }
                } else {
                    resetText = false;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 修改昵称请求
     */
    private void prepareRemoteUpdateNickname() {
        if (StringUtils.isNullString(nickName)) {
            UIUtils.showLongToastSafe("昵称不能为空");
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("nickname", nickName)
                .build();
        zejiaService.doRequest(this, Constants.API.UPDATE_USERNAME, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
                        UIUtils.showLongToastSafe("修改昵称成功");
                        PersonalInformationActivity.REFRESH = true;
                        PrefUtil.putString(this, UIUtils.getString(R.string.prefs_nickname)
                                , nickName);
                        setResult(ResultCodeConstant.RESULT_NICKNAME);
                        finish();
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }

    /**
     * 检查昵称
     */
    private void checkNickName() {
        if (StringUtils.isNotNullString(nickName)) {
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
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_modify_nick_name);
    }
}
