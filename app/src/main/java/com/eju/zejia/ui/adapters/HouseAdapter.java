package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.R;
import com.eju.zejia.data.models.House;
import com.eju.zejia.databinding.ItemHouseDetailBinding;
import com.eju.zejia.utils.ImageUtil;

/**
 * Created by Sidney on 2016/7/26.
 */
public class HouseAdapter extends BaseRecyclerViewAdapter<House, HouseAdapter.ItemViewHolder> {

    private String site;
    private OnItemClickListener onItemClickListener;

    public HouseAdapter(Context context, String site) {
        super(context);
        this.site = site;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHouseDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_house_detail, parent, false);
        return new ItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        ItemHouseDetailBinding binding = holder.binding;
        House house = items.get(position);
        setText(binding.tvHouseName, house.getTitle());
        setFormatText(binding.tvHousePrice, R.string.sale_price, house.getPrice());
        setFormatText(binding.tvAvgPrice, R.string.avg_price, house.getAvgPrice());
        setText(binding.tvHouseLayout, house.getLayout());
        setText(binding.tvHouseArea, house.getArea() + context.getString(R.string.area_unit));
        if ("lianjia".equals(site)) {
            site = context.getString(R.string.lianjia);
        } else {
            site = context.getString(R.string.fangtianxia);
        }
        setText(binding.tvHouseSite, site);
        ImageUtil.load(context, house.getLogo(), R.drawable.housing_pic, binding.ivHouse);

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ItemHouseDetailBinding binding;

        public ItemViewHolder(ItemHouseDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            if (null != onItemClickListener) {
                binding.getRoot().setOnClickListener(v -> onItemClickListener.onItemClick(v, getLayoutPosition()));
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
