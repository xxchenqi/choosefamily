package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.eju.zejia.R;
import com.eju.zejia.common.ExtraConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityLifeStatusBinding;
import com.eju.zejia.ui.adapters.LifeStatusAdapter;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ----------------------------------------
 * 注释: 生活状态页面
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/28 10:34
 * ----------------------------------------
 */
public class LifeStatusActivity extends BaseActivity {
    //数据绑定器
    private ActivityLifeStatusBinding binding;
    //生活状态数据源
    private List<CommunityFeatureBean> datas_life;
    //生活状态保存数据源
    private List<CommunityFeatureBean> datas_life_save;
    //已保存的数据源
    private List<CommunityFeatureBean> datas_save;
    //父级标签的选中
    private List<CommunityFeatureBean> datas_parent_save;
    //最终保存的数据
    private List<CommunityFeatureBean> datas_final_save;
    //适配器
    private LifeStatusAdapter adapter;
    //用户id
    private String sessionId;
    //择家服务
    private ZejiaService zejiaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_life_status);
        sessionId = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_sessionId), "");
        zejiaService = DataManager.getInstance().getZejiaService();
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        //回退
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.btnNext.setOnClickListener(v -> {
            prepareRemoteSaveChooseTag();
        });
        binding.btnNext.setClickable(false);

        initdata();


        binding.gvData.setOnItemClickListener((parent, view, position, id) -> {
            if (datas_life.get(position).getIsCheck() == 1) {
                datas_life.get(position).setIsCheck(0);
                binding.btnNext.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
                binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.gray_DDDDDD));
                binding.btnNext.setClickable(false);
            } else {
                for (int i = 0; i < datas_life.size(); i++) {
                    if (position == i) {
                        datas_life.get(i).setIsCheck(1);
                    } else {
                        datas_life.get(i).setIsCheck(0);
                    }
                }
                binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
                binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.yellow_FBE558));
                binding.btnNext.setClickable(true);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_life_status);
    }

    private void initdata() {
        datas_final_save = new ArrayList<>();
        adapter = new LifeStatusAdapter(this, datas_life, R.layout.item_life_status);
        binding.gvData.setAdapter(adapter);
        if(datas_life_save.size()>0){
            binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
            binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.yellow_FBE558));
            binding.btnNext.setClickable(true);
        }

    }

    @Override
    protected void processArgument(Bundle bundle) {
        super.processArgument(bundle);
        datas_life = (List<CommunityFeatureBean>) bundle.getSerializable(ExtraConstant.EXTRA_LIFE_DATA);
        datas_save = (List<CommunityFeatureBean>) bundle.getSerializable(ExtraConstant.EXTRA_COMMUNITY_FEATURE_DATA);
        datas_parent_save = (List<CommunityFeatureBean>) bundle.getSerializable(ExtraConstant.EXTRA_PARENT_DATA);
        datas_life_save = (List<CommunityFeatureBean>) bundle.getSerializable(ExtraConstant.EXTRA_LIFE_SAVE_DATA);
    }


    /**
     * 保存标签请求
     */
    private void prepareRemoteSaveChooseTag() {
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        datas_final_save.clear();
        //生活状态选中标签添加到data_save
        for (int i = 0; i < datas_life.size(); i++) {
            if (datas_life.get(i).getIsCheck() == 1) {
                datas_final_save.add(datas_life.get(i));
                break;
            }
        }
        //社区特色1级标签选中的添加到data_save
        datas_final_save.addAll(datas_parent_save);
        datas_final_save.addAll(datas_save);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < datas_final_save.size(); i++) {
            if (i == datas_final_save.size() - 1) {
                sb.append(datas_final_save.get(i).getId());
            } else {
                sb.append(datas_final_save.get(i).getId() + ",");
            }
        }


        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("reqTagIds", sb.toString())
                .build();

        zejiaService.doRequest(this, Constants.API.SAVE_CHOOSE_TAG, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code == 200) {
//                        MyActivityManager.getInstance().finishAllActivity();
                        navigator.to(AlwaysAddressActivity.class);
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


}
