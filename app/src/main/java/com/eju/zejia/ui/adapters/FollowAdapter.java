package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.eju.zejia.R;
import com.eju.zejia.data.models.Follow;
import com.eju.zejia.data.models.House;
import com.eju.zejia.databinding.ItemFollowBinding;
import com.eju.zejia.utils.ImageUtil;

import java.util.List;

/**
 * 我的关注
 * <p>
 * Created by Sidney on 2016/7/27.
 */
public class FollowAdapter extends BaseArrayAdapter<Follow> {

    public FollowAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemFollowBinding binding;
        if (convertView == null || convertView.getTag() == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_follow, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemFollowBinding) convertView.getTag();
        }
        Follow follow = getItem(position);
        setText(binding.tvName, follow.getName());
        setText(binding.tvRegion, follow.getRegion());
        setText(binding.tvPlate, follow.getPlate());
        setFormatText(binding.tvBuildAge, R.string.build_age, "" + follow.getBuildAge());
        setFormatText(binding.tvPrice, R.string.avg_price, "" + follow.getAvgPrice());
        ImageUtil.load(getContext(), follow.getThumbUrl(), R.drawable.follow_pic, binding.ivHouse);
        return convertView;
    }
}
