package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sidney on 2016/7/26.
 */
public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {

    protected List<T> items;

    public BaseArrayAdapter(Context context) {
        super(context, -1);
        items = new ArrayList<>();
    }

    public BaseArrayAdapter(Context context, List<T> items) {
        super(context, -1, items);
        this.items = items;
    }

    protected void setText(TextView textView, String text) {
        textView.setText(text);
    }

    protected void setFormatText(TextView textView, int id, Object... params) {
        textView.setText(String.format(getContext().getString(id), params));
    }

    protected void setNumber(TextView textView, int number) {
        textView.setText("" + number);
    }

    protected void setNumber(TextView textView, float number) {
        textView.setText("" + number);
    }

    protected int getColor(int id) {
        return ResourcesCompat.getColor(getContext().getResources(), id, null);
    }

    public void updateItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void insertItems(List<T> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return null == items ? 0 : items.size();
    }
}