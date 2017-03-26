package com.eju.zejia.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eju.zejia.application.BaseApplication;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.control.LockScreenControl;
import com.eju.zejia.utils.Navigator;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;


public abstract class BaseFragmentActivity extends RxFragmentActivity {

    protected Context context;
    protected Navigator navigator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.setmForegroundActivity(this);
        context = this;
        navigator = new Navigator(this);
        if (null != getIntent()) {
            Bundle bundle = getIntent().getExtras();
            processArgument(bundle == null ? new Bundle() : bundle);
        }
    }

    protected void processArgument(Bundle bundle) {
    }

    public abstract String getPageName();

    @Override
    protected void onStart() {
        super.onStart();
        CountControl.getInstance().skipRuning(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseApplication.setmForegroundActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockScreenControl.getInstance().registerLockReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LockScreenControl.getInstance().unRegisterLockReceiver(this);
    }
}
