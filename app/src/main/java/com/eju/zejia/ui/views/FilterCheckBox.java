package com.eju.zejia.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CompoundButton;

import com.eju.zejia.R;
import com.eju.zejia.data.models.FilterBean;


public class FilterCheckBox extends CompoundButton {

    private static final int CHECK_PADDING_IN_FILTER_ITEM = 16;

    private boolean mDebug = false;
    private boolean isInMeasure = false;
    private Paint mPaint;

    private Drawable mCheckedDrawable;

    private FilterBean.FilterItemBean mItemBean;


    public FilterCheckBox(Context context) {
        this(context, null);
    }

    public FilterCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FilterCheckBox, defStyleAttr, 0);

        Drawable d = a.getDrawable(R.styleable.FilterCheckBox_checkDrawable);
        if(null != d) {
            setCheckedDrawable(d);
        }

        boolean debug = a.getBoolean(R.styleable.FilterCheckBox_debug, false);
        setDebug(debug);

        a.recycle();

        setGravity(Gravity.CENTER);
        setFocusable(true);
        setClickable(true);
    }

    public void setDebug(boolean debug) {
        if(mDebug != debug) {
            mDebug = debug;
            if(null == mPaint) {
                mPaint = new Paint();
            }
        }
    }

//    @Override
//    public CharSequence getAccessibilityClassName() {
//        return this.getClass().getName();
//    }

    public void setCheckedDrawable(Drawable drawable) {
        if(mCheckedDrawable != drawable) {
            if(null != mCheckedDrawable) {
                mCheckedDrawable.setCallback(null);
                unscheduleDrawable(mCheckedDrawable);
            }

            mCheckedDrawable = drawable;
            if(null != drawable) {
                drawable.setCallback(this);
                if(drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                drawable.setVisible(VISIBLE == getVisibility(), false);
                setMinHeight(mCheckedDrawable.getIntrinsicHeight());
            }
        }

        refreshDrawableState();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isInMeasure = true;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        isInMeasure = false;
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public int getCompoundPaddingLeft() {
        int padding = 0;

        // ignore compound padding in onMeasure()
        if(isInMeasure) return padding;

        final Drawable drawable = mCheckedDrawable;
        if(null != drawable) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int checkedWidth = drawableWidth + CHECK_PADDING_IN_FILTER_ITEM;
            if(isChecked()) {
                padding += checkedWidth / 2;
            }
        }
        return padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Drawable drawable = mCheckedDrawable;
        if(null != drawable) {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();

            int textWidth = (int)getLayout().getLineMax(0);
            int contentWidth = width + CHECK_PADDING_IN_FILTER_ITEM + textWidth;
            int left = (getWidth() - contentWidth) / 2;

            int right = left + width;
            int top = (getHeight() - height) / 2;
            int bottom = top + height;

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);

            if(mDebug) {
                mPaint.setColor(Color.RED);
                canvas.drawLine(left, 0, left, getHeight(), mPaint);
                canvas.drawLine(right, 0, right, getHeight(), mPaint);

                mPaint.setColor(Color.MAGENTA);
                int tLeft = right + CHECK_PADDING_IN_FILTER_ITEM;
                canvas.drawLine(tLeft, 0, tLeft, getHeight(), mPaint);
            }
        }

        if(mDebug) {
            mPaint.setColor(Color.GREEN);
            float left = getLayout().getLineLeft(0);
            canvas.drawLine(left, 0, left, getHeight(), mPaint);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if(null != mCheckedDrawable) {
            final int[] states = getDrawableState();
            mCheckedDrawable.setState(states);
            invalidate();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mCheckedDrawable;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();

        if(null != mCheckedDrawable) {
            mCheckedDrawable.jumpToCurrentState();
        }
    }

    public FilterBean.FilterItemBean getItemBean() {
        return mItemBean;
    }

    public void setItemBean(FilterBean.FilterItemBean itemBean) {
        mItemBean = itemBean;
    }
}
