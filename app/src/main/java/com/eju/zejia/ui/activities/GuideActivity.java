package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.eju.zejia.R;
import com.eju.zejia.databinding.ActivityGuideBinding;
import com.eju.zejia.ui.adapters.GuideAdapter;
import com.eju.zejia.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ----------------------------------------
 * 注释:引导页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/21 17:06
 * ----------------------------------------
 */
public class GuideActivity extends BaseFragmentActivity {
    //数据绑定器
    private ActivityGuideBinding binding;
    //适配器
    private GuideAdapter adapter;
    //数据源
    private List<ImageView> imageViews;
    //第几张图
    private int pos;
    //监听起始x位置
    private float x1 = 0;
    //监听结束x位置
    private float x2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        imageViews = new ArrayList<>();
        initImageViews();
        adapter = new GuideAdapter(imageViews);
        binding.vpGuide.setAdapter(adapter);
        ((RadioButton) binding.rgGuide.getChildAt(0)).setChecked(true);
        binding.vpGuide.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                ((RadioButton) binding.rgGuide.getChildAt(pos)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        binding.vpGuide.setOnTouchListener((v, event) -> {
            if (pos != 2) {
                return false;
            }

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = event.getX();
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x2 = event.getX();
                if (x1 - x2 > 50) {
                    if (pos == 2) {
                        navigator.to(ChooseLoginActivity.class);
                        finish();
                    }
                }
            }
            return false;
        });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_guide);
    }


    /**
     * 初始化图片
     */
    private void initImageViews() {
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            if (i == 0) {
                imageView.setImageResource(R.drawable.guide_page1);
            } else if (i == 1) {
                imageView.setImageResource(R.drawable.guide_page2);
            } else {
                imageView.setImageResource(R.drawable.guide_page3);
            }
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViews.add(imageView);
        }
    }


}
