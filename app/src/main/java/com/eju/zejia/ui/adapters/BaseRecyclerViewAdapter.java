package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sidney on 2016/7/26.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;
    protected List<T> items = new ArrayList<>();

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    protected void setText(TextView textView, String text) {
        textView.setText(text);
    }

    protected void setFormatText(TextView textView, int id, Object... params) {
        textView.setText(String.format(context.getString(id), params));
    }

    protected void setNumber(TextView textView, int number) {
        textView.setText("" + number);
    }

    protected void setNumber(TextView textView, float number) {
        textView.setText("" + number);
    }

    public void updateItems(List<T> newItems, boolean animated) {
        items.clear();
        items.addAll(newItems);
        if (animated) {
            notifyItemRangeInserted(0, items.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void insertItems(List<T> newItems, boolean animated) {
        int lastCount = items.size();
        items.addAll(newItems);
        if (animated) {
            notifyItemRangeInserted(lastCount, items.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void deleteItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public T getItem(int position) {
        return items.get(position);
    }
}
