package com.eju.zejia.utils;

import android.text.TextUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验手机号和邮箱
 *
 * @author len
 */
public class InputValidate {

    /**
     * 验证手机格式
     */
//	public static boolean checkedIsTelephone(String str) {
//		/*
//		移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
//		联通：130、131、132、152、155、156、185、186
//		电信：133、153、180、189、177（1349卫通）
//		总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
//		*/
//		String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[3578]"代表第二位可以为3、5、7、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//		if (TextUtils.isEmpty(str)) return false;
//		else return str.matches(telRegex);
//	}

    /**
     * 验证手机格式
     */
    public static boolean checkedIsTelephone(String str) {
        if (str.length() != 11) {
            return false;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil
                    .parse("+86" + str, "CH");
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto);
            return isValid;
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: "
                    + e.toString());
            return false;
        }
    }

    //判断email格式是否正确
    public static boolean checkedIsEmail(String str) {
        String isEmail = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(isEmail);
        Matcher m = p.matcher(str);

        return m.matches();
    }

    // 根据产品规则，昵称是由字母、数字、汉字或下划线组成,限4-16个字符,不能以下划线开头或结尾
    public static boolean checkNickname(String str) {
        String telRegex = "^(?!_)(?!.*?_$)([\u4e00-\u9fa5a-zA-Z0-9_]){2,16}$";
        if (TextUtils.isEmpty(str))
            return false;
        else
            return str.matches(telRegex);
//					&& ((str.length() >= 4) && (str.length() <= 16));
    }

    // 根据产品规则，密码只能是a-z，A-Z,0-9，长度为6-18位
    public static boolean checkPassword(String str) {
        String telRegex = "^[A-Za-z0-9]+";
        if (TextUtils.isEmpty(str))
            return false;
        else
            return str.matches(telRegex)
                    && ((str.length() >= 6) && (str.length() <= 18));
    }

    // 验证验证码
    public static boolean checkVierfyNumber(String str) {
        return !"".equals(str) && str.length() == 4;
    }
}
