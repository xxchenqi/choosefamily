package com.eju.zejia.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Sidney on 2016/7/21.
 */
public class FitListView extends ListView {
    public FitListView(Context context) {
        super(context);
    }

    public FitListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
