package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.eju.zejia.R;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.ui.holder.ViewHolder;

import java.util.List;

/**
 * ----------------------------------------
 * 注释:生活状态适配器
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/28 11:22
 * ----------------------------------------
 */
public class LifeStatusAdapter extends CommonBaseAdapter<CommunityFeatureBean> {

    public LifeStatusAdapter(Context context, List<CommunityFeatureBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, CommunityFeatureBean communityFeatureBean) {
        holder.setText(R.id.tv_item_life_name, communityFeatureBean.getTagName())
                .setText(R.id.tv_item_life_content, communityFeatureBean.getDesc() != null
                        ? communityFeatureBean.getDesc() : "");
        LinearLayout ll_item_life = holder.getView(R.id.ll_item_life);
        ImageView iv_life_status = holder.getView(R.id.iv_life_status);
        switch (communityFeatureBean.getTagName()) {
            case "宅生活":
                iv_life_status.setImageResource(R.drawable.state_zhai_icon);
                break;
            case "小资生活":
                iv_life_status.setImageResource(R.drawable.state_xiaozi_icon);
                break;
            case "工作狂":
                iv_life_status.setImageResource(R.drawable.state_work_icon);
                break;
            case "社交达人":
                iv_life_status.setImageResource(R.drawable.state_social_icon);
                break;
            default:
                iv_life_status.setImageResource(R.drawable.ask_life_icon);
                break;
        }


        if (communityFeatureBean.getIsCheck() == 1) {
            ll_item_life.setBackgroundResource(R.drawable.new_state_checked_btn);
        } else {
            ll_item_life.setBackgroundResource(R.drawable.new_state_check_btn);
        }
    }
}
