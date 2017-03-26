package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.R;
import com.eju.zejia.databinding.ItemBaseInfoItemBinding;
import com.eju.zejia.databinding.ItemFollowBinding;
import com.eju.zejia.utils.Tuple2;
import com.eju.zejia.utils.Tuple3;

import java.util.List;

/**
 * Created by Sidney on 2016/8/2.
 */
public class BaseInfoAdapter extends BaseArrayAdapter<Tuple2<Integer, String>> {
    public BaseInfoAdapter(Context context, List<Tuple2<Integer, String>> items) {
        super(context, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemBaseInfoItemBinding binding;
        if (convertView == null || convertView.getTag() == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_base_info_item, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemBaseInfoItemBinding) convertView.getTag();
        }
        Tuple2<Integer, String> tuple2 = getItem(position);
        setText(binding.text1, getContext().getString(tuple2.t1));
        setText(binding.text2, tuple2.t2);
        if(tuple2.t1 == R.string.build_age_label){
            binding.image.setImageResource(R.drawable.year_icon);
        }
        else if(tuple2.t1==R.string.area_label){
            binding.image.setImageResource(R.drawable.area_icon);
        }
        else if(tuple2.t1==R.string.house_count_label){
            binding.image.setImageResource(R.drawable.house_icon);
        }
        else if(tuple2.t1==R.string.park_count_label){
            binding.image.setImageResource(R.drawable.pot_icon);
        }
        else if(tuple2.t1==R.string.capacity_rate_label){
            binding.image.setImageResource(R.drawable.plot_ratio_icon);
        }
        else {
            binding.image.setImageResource(R.drawable.green_icon);
        }
        return convertView;
    }
}
