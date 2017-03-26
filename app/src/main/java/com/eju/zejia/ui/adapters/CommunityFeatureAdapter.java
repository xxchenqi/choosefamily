package com.eju.zejia.ui.adapters;

import android.content.Context;
import android.widget.ImageView;

import com.eju.zejia.R;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.ui.holder.ViewHolder;

import java.util.List;

/**
 * ----------------------------------------
 * 注释:社区特色适配器
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 11:00
 * ----------------------------------------
 */
public class CommunityFeatureAdapter extends CommonBaseAdapter<CommunityFeatureBean> {
    public CommunityFeatureAdapter(Context context, List<CommunityFeatureBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(ViewHolder holder, CommunityFeatureBean communityFeatureBean) {
        String tagName = communityFeatureBean.getTagName();
        holder.setText(R.id.tv_item_community_feature, tagName);
        ImageView iv_item_community_feature = holder.getView(R.id.iv_item_community_feature);

        if (communityFeatureBean.getIsCheck() == 1) {


            switch (tagName) {
                case "四通八达":
                    iv_item_community_feature.setImageResource(R.drawable.choose_traffic_highlight_btn);
                    break;
                case "观景居所":
                    iv_item_community_feature.setImageResource(R.drawable.choose_view_highlight_btn);
                    break;
                case "教育社区":
                    iv_item_community_feature.setImageResource(R.drawable.choose_eduaction_highlight_btn);
                    break;
                case "养老居所":
                    iv_item_community_feature.setImageResource(R.drawable.choose_old_highlight_btn);
                    break;
                case "区域中心":
                    iv_item_community_feature.setImageResource(R.drawable.choose_center_highlight_btn);
                    break;
                case "投资精选":
                    iv_item_community_feature.setImageResource(R.drawable.choose_investment_highlight_btn);
                    break;
                case "经济小窝":
                    iv_item_community_feature.setImageResource(R.drawable.choose_small_highlight_btn);
                    break;
                case "舒适住宅":
                    iv_item_community_feature.setImageResource(R.drawable.choose_comfort_highlight_btn);
                    break;
                case "高档大宅":
                    iv_item_community_feature.setImageResource(R.drawable.choose_grade_highlight_btn);
                    break;
                default:
                    iv_item_community_feature.setImageResource(R.drawable.choose_ask_highlight_btn);
                    break;
            }
        } else {
            switch (tagName) {
                case "四通八达":
                    iv_item_community_feature.setImageResource(R.drawable.choose_traffic_btn);
                    break;
                case "观景居所":
                    iv_item_community_feature.setImageResource(R.drawable.choose_view_btn);
                    break;
                case "教育社区":
                    iv_item_community_feature.setImageResource(R.drawable.choose_eduaction_btn);
                    break;
                case "养老居所":
                    iv_item_community_feature.setImageResource(R.drawable.choose_old_btn);
                    break;
                case "区域中心":
                    iv_item_community_feature.setImageResource(R.drawable.choose_center_btn);
                    break;
                case "投资精选":
                    iv_item_community_feature.setImageResource(R.drawable.choose_investment_btn);
                    break;
                case "经济小窝":
                    iv_item_community_feature.setImageResource(R.drawable.choose_small_btn);
                    break;
                case "舒适住宅":
                    iv_item_community_feature.setImageResource(R.drawable.choose_comfort_btn);
                    break;
                case "高档大宅":
                    iv_item_community_feature.setImageResource(R.drawable.choose_grade_btn);
                    break;
                default:
                    iv_item_community_feature.setImageResource(R.drawable.choose_ask_btn);
                    break;
            }

        }
    }
}