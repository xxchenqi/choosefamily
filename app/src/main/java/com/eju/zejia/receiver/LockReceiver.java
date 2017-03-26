package com.eju.zejia.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eju.zejia.common.SharedPreferencesConstant;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;

public class LockReceiver extends BroadcastReceiver {
    private String action = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (action != null && action.equals(intent.getAction())) {
            return;
        }
        action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏

        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            boolean isRuning = PrefUtil.getBoolean(
                    UIUtils.getContext(),
                    SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                    true);
            if (isRuning) {
                PrefUtil.putBoolean(UIUtils.getContext(),
                        SharedPreferencesConstant.Shared_Count_IsRunningForeground,
                        false);
                CountControl.getInstance().loginOut(true);
            }
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁

        }
    }

}
