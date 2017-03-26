package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.widget.TextView;

import com.eju.zejia.R;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.ui.holder.ViewHolder;
import com.eju.zejia.utils.UIUtils;

import java.util.List;

/**
 * ----------------------------------------
 * 注释:社区特色标签适配器
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 11:00
 * ----------------------------------------
 */
public class CommunityFeatureTagAdapter extends CommonBaseAdapter<CommunityFeatureBean> {
    public CommunityFeatureTagAdapter(Context context, List<CommunityFeatureBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, CommunityFeatureBean communityFeatureBean) {
        TextView tv_item_community_feature_tag = holder.getView(R.id.tv_item_community_feature_tag);
        tv_item_community_feature_tag.setText(communityFeatureBean.getTagName());

        if (communityFeatureBean.getIsCheck()==1) {
            tv_item_community_feature_tag.setTextColor(UIUtils.getColor(R.color.white_FFFFFF));
            tv_item_community_feature_tag.setBackgroundResource(R.drawable.bg_community_feature_tag_click);
        } else {
            tv_item_community_feature_tag.setTextColor(UIUtils.getColor(R.color.black_666666));
            tv_item_community_feature_tag.setBackgroundResource(R.drawable.bg_community_feature_tag_no_click);

        }


    }
}