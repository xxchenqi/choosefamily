package com.eju.zejia.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.eju.zejia.R;
import com.eju.zejia.common.ExtraConstant;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.interfaces.ICommunityFeatureCallback;
import com.eju.zejia.ui.adapters.CommunityFeatureTagAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ----------------------------------------
 * 注释:社区特色碎片
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 13:59
 * ----------------------------------------
 */
public class CommunityFeatureFragment extends BaseFragment {
    //初始view
    private View currentView;
    //gv
    private GridView gv_data;
    //数据
    private CommunityFeatureBean bean;
    //数据
    private List<CommunityFeatureBean> datas;
    //适配器
    public CommunityFeatureTagAdapter adapter;
    //回调
    private ICommunityFeatureCallback callback;
    //标题
    private TextView tv_pop_feature;
    //内容
    private TextView tv_pop_feature_content;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (ICommunityFeatureCallback) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_community_feature, null, false);
        gv_data = (GridView) currentView.findViewById(R.id.gv_data);
        tv_pop_feature = (TextView) currentView.findViewById(R.id.tv_pop_feature);
        tv_pop_feature_content = (TextView) currentView.findViewById(R.id.tv_pop_feature_content);

        initData();

        gv_data.setOnItemClickListener((parent, view, position, id) -> {
            CommunityFeatureBean bean = datas.get(position);
            if (bean.getIsCheck() == 0) {
                bean.setIsCheck(1);
            } else {
                bean.setIsCheck(0);
            }
            adapter.notifyDataSetChanged();
            callback.itemClick(bean);
        });
        return currentView;
    }

    @Override
    protected void processArguments(Bundle bundle) {
        super.processArguments(bundle);
        bean = (CommunityFeatureBean) bundle.getSerializable(ExtraConstant.EXTRA_COMMUNITY_FEATURE_DATA);
    }

    @Override
    public String getPageName() {
        return null;
    }

    private void initData() {
        tv_pop_feature.setText(bean.getTagName());
        if (bean.getDesc() != null) {
            tv_pop_feature_content.setText(bean.getDesc());
        } else {
            tv_pop_feature_content.setText("");
        }
        datas = new ArrayList<>();
        List<CommunityFeatureBean> children = bean.getChildren();
        for (int i = 0; i < children.size(); i++) {
            datas.add(children.get(i));
        }
        adapter = new CommunityFeatureTagAdapter(getActivity(), datas, R.layout.item_community_feature_tag);
        gv_data.setAdapter(adapter);
    }
}
