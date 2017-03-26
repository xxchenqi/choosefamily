package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.eju.zejia.R;
import com.eju.zejia.common.ExtraConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.CommunityFeatureBean;
import com.eju.zejia.data.models.ParentCommunityFeatureBean;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityCommunityFeatureBinding;
import com.eju.zejia.interfaces.ICommunityFeatureCallback;
import com.eju.zejia.ui.adapters.CommunityFeatureAdapter;
import com.eju.zejia.ui.adapters.CommunityFeaturePopAdapter;
import com.eju.zejia.ui.fragments.BaseFragment;
import com.eju.zejia.ui.fragments.CommunityFeatureFragment;
import com.eju.zejia.ui.views.ZoomOutPageTransformer;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ----------------------------------------
 * 注释:社区特色页
 * <p>
 * 作者: cq
 * <p>
 * 时间: on 2016/7/27 10:35
 * ----------------------------------------
 */
public class CommunityFeatureActivity extends BaseFragmentActivity implements ICommunityFeatureCallback {
    //数据绑定器
    private ActivityCommunityFeatureBinding binding;
    //适配器
    private CommunityFeatureAdapter adapter;
    //适配器
    private CommunityFeaturePopAdapter featurePopAdapter;
    //展示动画
    private Animation mShowAction;
    //隐藏动画
    private Animation mHiddenAction;
    //主数据源
    private List<CommunityFeatureBean> datas;
    //生活状态数据源
    private List<CommunityFeatureBean> datas_life;
    //保存数据源
    private List<CommunityFeatureBean> datas_save;
    //社区特色1级标签保存的数据源
    private List<CommunityFeatureBean> datas_parent_save;
    //生活状态标签保存数据源
    private List<CommunityFeatureBean> datas_life_save;
    //零时数据源
    private List<CommunityFeatureBean> datas_cache;
    //删除数据源
    private Set<CommunityFeatureBean> datas_delete;
    //碎片集合
    private List<BaseFragment> fragments;
    //是否是第一次请求
    private boolean isFirst;
    //用户id
    private String sessionId;
    //择家服务
    private ZejiaService zejiaService;
    //是否是登陆过来
    private boolean login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_feature);
        if (login) {
            binding.toolbar.setNavigationIcon(R.drawable.bg_btn_back);
            //回退
            binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        //初始化
        sessionId = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_sessionId), "");
        zejiaService = DataManager.getInstance().getZejiaService();
        datas = new ArrayList<>();
        datas_save = new ArrayList<>();
        datas_cache = new ArrayList<>();
        datas_delete = new HashSet<>();
        fragments = new ArrayList<>();
        datas_life = new ArrayList<>();
        datas_life_save = new ArrayList<>();
        datas_parent_save = new ArrayList<>();
        initAnimations();

        //vp动画
        binding.vpPopFeature.setPageTransformer(true, new ZoomOutPageTransformer());
        //父控件的touch传递给viewpager
        binding.llFeatureInner.setOnTouchListener((v, event) -> binding.vpPopFeature.dispatchTouchEvent(event));
        //下一步
        binding.btnNext.setOnClickListener(v -> {
            //清空社区特色1级数据,然后在遍历添加进去(以防回退到当前页面数据源改变bug)
            datas_parent_save.clear();
            for (int i = 0; i < datas.size(); i++) {
                if (datas.get(i).getIsCheck() == 1) {
                    datas_parent_save.add(datas.get(i));
                }
            }
            Bundle bundle = new Bundle();
            bundle.putSerializable(ExtraConstant.EXTRA_COMMUNITY_FEATURE_DATA, (Serializable) datas_save);
            bundle.putSerializable(ExtraConstant.EXTRA_LIFE_DATA, (Serializable) datas_life);
            bundle.putSerializable(ExtraConstant.EXTRA_PARENT_DATA, (Serializable) datas_parent_save);
            bundle.putSerializable(ExtraConstant.EXTRA_LIFE_SAVE_DATA, (Serializable) datas_life_save);
            navigator.to(LifeStatusActivity.class, bundle);
        });

        binding.btnNext.setClickable(false);

        //item点击
        binding.gvData.setOnItemClickListener((parent, view, position, id) -> {
            binding.llFeature.setVisibility(View.VISIBLE);
            binding.llFeature.startAnimation(mShowAction);
            //保存里的数据全部为选中(隐藏的时候缓存里的数据会全部为未选中)
            for (int i = 0; i < datas_save.size(); i++) {
                datas_save.get(i).setIsCheck(1);
            }
            if (isFirst) {//是否是第一次进来
                //刷新碎片里的数据
                for (int i = 0; i < fragments.size(); i++) {
                    CommunityFeatureFragment fragment = (CommunityFeatureFragment) fragments.get(i);
                    fragment.adapter.notifyDataSetChanged();
                }
            }
            isFirst = true;
            //选中当前点击的vp
            binding.vpPopFeature.setCurrentItem(position, false);
        });

        //隐藏点击
        binding.ivPopClose.setOnClickListener(v -> dismiss());
//        binding.vDismiss.setOnClickListener(v -> dismiss());
        //保存
        binding.btnSave.setOnClickListener(v -> saveClick());

        prepareRemoteGetTagList();
    }

    @Override
    protected void processArgument(Bundle bundle) {
        super.processArgument(bundle);
        login = bundle.getBoolean(ExtraConstant.EXTRA_LOGIN,false);
    }

    /**
     * 获取标签列表请求
     */
    private void prepareRemoteGetTagList() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();

        zejiaService.doRequest(this, Constants.API.GET_TAG_LIST, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");
                    if (response == null) {
                        return;
                    }
                    if (code == 200) {
                        ParentCommunityFeatureBean parentCommunityFeatureBean = GsonFactory
                                .changeGsonToBean(response.toString(),
                                        ParentCommunityFeatureBean.class);
                        if (parentCommunityFeatureBean == null) {
                            return;
                        }

                        List<CommunityFeatureBean> tagList = parentCommunityFeatureBean.getTagList();
                        //给子标签添加父id
                        for (int i = 0; i < tagList.size(); i++) {
                            List<CommunityFeatureBean> children = tagList.get(i).getChildren();
                            for (int j = 0; j < children.size(); j++) {
                                children.get(j).setParent_id(tagList.get(i).getId());
                                if (children.get(j).getIsCheck() == 1) {
                                    //保存2级标签选中的
                                    datas_save.add(children.get(j));
                                }
                            }
                            if (tagList.get(i).getIsCheck() == 1) {
                                if (tagList.get(i).getType() == 3) {
                                    //保存生活状态选中的
                                    datas_life_save.add(tagList.get(i));
                                } else {
                                    //保存社区特色1级标签选中的
                                    datas_parent_save.add(tagList.get(i));
                                }
                            }
                        }
                        //如果有保存数据，下一步可点击
                        if (datas_save.size() > 0 || datas_parent_save.size() > 0) {
                            binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
                            binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.yellow_FBE558));
                            binding.btnNext.setClickable(true);
                        }

                        //抽取社区特色
                        for (int i = 0; i < tagList.size(); i++) {
                            if (tagList.get(i).getType() == 1 || tagList.get(i).getType() == 2) {
                                datas.add(tagList.get(i));
                            } else {
                                datas_life.add(tagList.get(i));
                            }
                        }

                        adapter = new CommunityFeatureAdapter(this, datas, R.layout.item_community_feature);
                        binding.gvData.setAdapter(adapter);

                        //初始化viewpager数据
                        for (int i = 0; i < datas.size(); i++) {
                            BaseFragment fragment = new CommunityFeatureFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(ExtraConstant.EXTRA_COMMUNITY_FEATURE_DATA, datas.get(i));
                            fragment.setArguments(bundle);
                            fragments.add(fragment);
                        }
                        featurePopAdapter = new CommunityFeaturePopAdapter(getSupportFragmentManager(), fragments);
                        //vp缓存数
                        binding.vpPopFeature.setOffscreenPageLimit(datas.size());
                        binding.vpPopFeature.setAdapter(featurePopAdapter);
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_community_feature);
    }

    /**
     * 初始化动画
     */
    private void initAnimations() {
        mShowAction = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
        mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.push_up_out);
    }

    @Override
    public void itemClick(CommunityFeatureBean bean) {
        if (bean.getIsCheck() == 1) {//选中
            datas_cache.add(bean);
        } else {//未选中
            datas_cache.remove(bean);
            //未选中的加进未选中的集合里
            datas_delete.add(bean);
        }
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        binding.llFeature.setVisibility(View.GONE);
        binding.llFeature.startAnimation(mHiddenAction);
        //缓存数据里的数据全部设置为未选中
        for (int i = 0; i < datas_cache.size(); i++) {
            datas_cache.get(i).setIsCheck(0);
        }
        //刷新数据
        for (int i = 0; i < fragments.size(); i++) {
            CommunityFeatureFragment fragment = (CommunityFeatureFragment) fragments.get(i);
            fragment.adapter.notifyDataSetChanged();
        }
        //清楚缓存数据
        datas_cache.clear();
        datas_delete.clear();
    }


    /**
     * 保存数据
     */
    public void saveClick() {
        //保存
//        if (datas_cache.size() != 0) {
        //遍历未选中的集合
        for (CommunityFeatureBean bean : datas_delete) {
            //从保存数据里删除未选中的
            datas_save.remove(bean);
        }
        //保存数据里添加已选中的缓存数据
        datas_save.addAll(datas_cache);
        //初始主数据都为未选中
        for (int j = 0; j < datas.size(); j++) {
            datas.get(j).setIsCheck(0);
        }
        //设置主数据源里是否选中(保存数据里的id=主数据的id)
        for (int i = 0; i < datas_save.size(); i++) {
            int id = datas_save.get(i).getParent_id();
            for (int j = 0; j < datas.size(); j++) {
                if (datas.get(j).getId() == id) {
                    datas.get(j).setIsCheck(1);
                }
            }
        }
//        } else {
//            //遍历未选中的集合
//            for (CommunityFeatureBean bean : datas_delete) {
//                datas_save.remove(bean);
//            }
//            //初始主数据都为未选中
//            for (int j = 0; j < datas.size(); j++) {
//                datas.get(j).setIsCheck(0);
//            }
//            //设置主数据源里是否选中(保存数据里的id=主数据的id)
//            for (int i = 0; i < datas_save.size(); i++) {
//                int id = datas_save.get(i).getParent_id();
//                for (int j = 0; j < datas.size(); j++) {
//                    if (datas.get(j).getId() == id) {
//                        datas.get(j).setIsCheck(1);
//                    }
//                }
//            }
//        }
        //清除数据
        datas_delete.clear();
        datas_cache.clear();
        adapter.notifyDataSetChanged();

        binding.llFeature.setVisibility(View.GONE);
        binding.llFeature.startAnimation(mHiddenAction);
        //下一步按钮是否可点击
        if (datas_save.size() != 0) {
            binding.btnNext.setTextColor(UIUtils.getColor(R.color.brown_693119));
            binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.yellow_FBE558));
            binding.btnNext.setClickable(true);
        } else {
            binding.btnNext.setTextColor(UIUtils.getColor(R.color.gray_A0A0A0));
            binding.btnNext.setBackgroundColor(UIUtils.getColor(R.color.gray_DDDDDD));
            binding.btnNext.setClickable(false);
        }

    }
}
