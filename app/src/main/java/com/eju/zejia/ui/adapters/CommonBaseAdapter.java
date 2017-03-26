package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.eju.zejia.ui.holder.ViewHolder;

import java.util.List;

/**
 * ----------------------------------------
 * 注释:万能适配器
 * <p>
 * 作者: cq
 * <p>
 * 时间: 2016/7/27 10:58
 * ----------------------------------------
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    private List<T> mDatas;
    private LayoutInflater mInflater;
    private int layoutId;

    protected CommonBaseAdapter(Context context, List<T> datas, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }

    public void updateDatas(List<T> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(mContext, convertView, parent, layoutId, position);
        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    protected abstract void convert(ViewHolder holder, T t);


}
