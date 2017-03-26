package com.eju.zejia.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.eju.zejia.application.BaseApplication;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.control.LockScreenControl;
import com.eju.zejia.utils.Navigator;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

/**
 * Created by Sidney on 2016/7/18.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    protected Context context;
    protected Toolbar toolbar;
    protected ActionBar actionBar;
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
    public abstract String getPageName();

    protected void processArgument(Bundle bundle) {
    }

    protected void setUpToolbar(int id) {
        setUpToolbar((Toolbar) findViewById(id));
    }

    protected void setUpToolbar(Toolbar toolbar) {
        if (null == toolbar) return;
        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockScreenControl.getInstance().registerLockReceiver(this);
    }

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
    protected void onStop() {
        super.onStop();
        CountControl.getInstance().skipUnRuning(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LockScreenControl.getInstance().unRegisterLockReceiver(this);
    }
}
