package com.eju.zejia.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.eju.zejia.ui.fragments.BaseFragment;

import java.util.List;

/**
 * ----------------------------------------
 * 注释:社区特色适配器
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 13:56
 * ----------------------------------------
 */
public class CommunityFeaturePopAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> datas;

    public CommunityFeaturePopAdapter(FragmentManager fm, List<BaseFragment> datas) {
        super(fm);
        this.datas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
