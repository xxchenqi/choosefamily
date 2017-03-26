package com.eju.zejia.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.eju.zejia.control.CountControl;

/**
 * Created by Sidney on 2016/7/19.
 */
public abstract class BaseFragment extends Fragment {

    protected Activity supportActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        supportActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        processArguments(getArguments() == null ? new Bundle() : getArguments());
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    protected void processArguments(Bundle bundle) {
    }

    public abstract String getPageName();

    @Override
    public void onStart() {
        super.onStart();
        CountControl.getInstance().skipRuning(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CountControl.getInstance().skipUnRuning(supportActivity);
    }
}
