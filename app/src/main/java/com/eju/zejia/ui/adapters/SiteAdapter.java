package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.R;
import com.eju.zejia.data.models.House;
import com.eju.zejia.databinding.ItemHouseDetailBinding;
import com.eju.zejia.utils.ImageUtil;

import java.util.List;

/**
 * Created by Sidney on 2016/7/26.
 */
public class SiteAdapter extends BaseArrayAdapter<House> {

    private int initMinSize;
    private int minSize;
    private boolean collapsed;
    private String site;

    public SiteAdapter(Context context, int minSize, List<House> items, String site) {
        super(context, items);
        this.initMinSize = minSize;
        if (items != null) {
            collapsed = items.size() > initMinSize;
            this.minSize = collapsed ? initMinSize : items.size();
        }
        this.site = site;
    }

    public boolean triggerShowAll() {
        if (collapsed) {
            collapsed = false;
            notifyDataSetChanged();
        } else {
            collapsed = true;
        }
        return !collapsed;
    }

    @Override
    public int getCount() {
        if (items == null) return 0;
        return collapsed ? minSize : items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHouseDetailBinding binding;
        if (convertView == null || convertView.getTag() == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.item_house_detail, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemHouseDetailBinding) convertView.getTag();
        }

        House house = items.get(position);
        site = site.equals("soufun") ? getContext().getString(R.string.fangtianxia) : getContext().getString(R.string.lianjia);
        setText(binding.tvHouseName, house.getTitle());
        setFormatText(binding.tvHousePrice, R.string.sale_price, house.getSellPrice());
        setFormatText(binding.tvAvgPrice, R.string.avg_price, house.getAvgPrice());
        setText(binding.tvHouseLayout, house.getLayout());
        setText(binding.tvHouseArea, house.getArea() + getContext().getString(R.string.area_unit));
        setText(binding.tvHouseSite, site);
        ImageUtil.load(getContext(), house.getLogo(), R.drawable.housing_pic, binding.ivHouse);
        return convertView;
    }

}
