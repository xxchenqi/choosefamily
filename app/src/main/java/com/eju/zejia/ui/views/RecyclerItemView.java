package com.eju.zejia.ui.views;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import com.eju.zejia.utils.UIUtils;

import java.util.List;


public abstract class RecyclerItemView {

    protected Context mContext;
    protected ViewGroup mParent;

    protected ViewDataBinding mBinding;

    public RecyclerItemView(ViewGroup parent) {
        mParent = parent;
        mContext = parent.getContext();
    }

    protected abstract View inflateView();
    public abstract ViewHolder getViewHolder();

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder() {
            super(inflateView());
        }

        public abstract void onBindViewHolder(int position);
    }


    public final static class Divider extends RecyclerItemView {

        private int mSize;
        private int mColor;

        public Divider(ViewGroup parent, int size, int color) {
            super(parent);

            mSize = size;
            mColor = color;
        }

        @Override
        protected View inflateView() {
            View divider = new View(mContext);
            divider.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, mSize));
            divider.setBackgroundColor(mColor);
            return divider;
        }

        @Override
        public ViewHolder getViewHolder() {
            return new ViewHolder() {
                @Override
                public void onBindViewHolder(int position) {

                }
            };
        }
    }


}
