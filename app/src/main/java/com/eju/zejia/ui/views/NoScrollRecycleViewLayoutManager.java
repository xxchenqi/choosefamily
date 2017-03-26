package com.eju.zejia.ui.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;


public class NoScrollRecycleViewLayoutManager extends LinearLayoutManager {

    public NoScrollRecycleViewLayoutManager(Context context) {
        super(context);
    }

//    public NoScrollRecycleViewLayoutManager(Context context, int orientation, boolean reverseLayout) {
//        super(context, orientation, reverseLayout);
//    }

//    public NoScrollRecycleViewLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
