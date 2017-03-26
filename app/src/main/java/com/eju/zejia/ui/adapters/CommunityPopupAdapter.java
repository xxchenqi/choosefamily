package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.R;
import com.eju.zejia.databinding.ItemPopupCommunityBinding;
import com.eju.zejia.ui.activities.MainActivity;

import java.util.List;

/**
 * Created by Sidney on 2016/7/26.
 */
public class CommunityPopupAdapter extends BaseArrayAdapter<MainActivity.FilterItemBean> {

    public CommunityPopupAdapter(Context context, List<MainActivity.FilterItemBean> items) {
        super(context, items);
    }

    public void select(int position) {
        int size = getCount();
        for (int i = 0; i < size; i++) {
            if (position == i) {
                getItem(i).setCheck(true);
            } else {
                getItem(i).setCheck(false);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemPopupCommunityBinding binding;
        if (null == convertView || null == convertView.getTag()) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_popup_community, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemPopupCommunityBinding) convertView.getTag();
        }
        MainActivity.FilterItemBean bean = getItem(position);
        setText(binding.text1, bean.getName());
        if (bean.isCheck()) {
            binding.ivCheck.setVisibility(View.VISIBLE);
            binding.text1.setTextColor(getColor(R.color.orange_FF7800));
        } else {
            binding.ivCheck.setVisibility(View.GONE);
            binding.text1.setTextColor(getColor(R.color.black_282323));
        }
        return convertView;
    }
}