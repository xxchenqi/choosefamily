package com.eju.zejia.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Sandy on 2016/8/2/0002.
 */
public class KeyBoardManager {
	/**
	 * 关闭键盘
	 */
	public static void closeKeyBoard(Activity activity) {
		if (activity.getCurrentFocus() != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				View view = activity.getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}
			}
		}
	}

	/**
	 * 打开输入法
	 */
	public static void openKeyBoard(Activity activity, View view) {
		// TODO Auto-generated method stub
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// 接受软键盘输入的编辑文本或其它视图
		inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 关闭输入法
	 */
	public static void closeInput(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	/**
	 * 关闭输入法
	 */
	public static void closeInput(Activity activity, ViewGroup view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 若返回true，则表示输入法打开
	 * 
	 * @return
	 */
	public static boolean isInputType(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

}
