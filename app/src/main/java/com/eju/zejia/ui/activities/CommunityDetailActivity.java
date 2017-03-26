package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.Community;
import com.eju.zejia.data.models.House;
import com.eju.zejia.data.models.Site;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityCommunityDetailBinding;
import com.eju.zejia.databinding.ItemAreaInfoBinding;
import com.eju.zejia.databinding.ItemBaseInfoBinding;
import com.eju.zejia.databinding.ItemSiteBinding;
import com.eju.zejia.interfaces.IEventCallback;
import com.eju.zejia.ui.adapters.BaseInfoAdapter;
import com.eju.zejia.ui.adapters.SiteAdapter;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.ImageUtil;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.Tuple2;
import com.eju.zejia.utils.UIUtils;
import com.eju.zejia.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaeger.library.StatusBarUtil;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * 社区详情页面
 * <p>
 * Created by Sidney on 2016/7/21.
 */
public class CommunityDetailActivity extends BaseActivity {

    public static final String EXTRA_COMMUNITY_ID = "communityId";
    public static final String EXTRA_POSITION = "position";
    public static final String SHARE_IMAGE = "houseImage";

    private ActivityCommunityDetailBinding binding;
    private ItemBaseInfoBinding baseInfoBinding;
    private ItemAreaInfoBinding areaInfoBinding;
    private ZejiaService zejiaService;
    private Gson gson;
    private long communityId;
    private Community community;
    private String sessionId;
    private Drawable cloneBgBtnBack;
    private int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_detail);
        StatusBarUtil.setTranslucent(this, 0);
        baseInfoBinding = DataBindingUtil.findBinding(findViewById(R.id.llBaseInfo));
        areaInfoBinding = DataBindingUtil.findBinding(findViewById(R.id.llAreaInfo));
        gson = GsonFactory.getGson();
        zejiaService = DataManager.getInstance().getZejiaService();
        setUpToolbar(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        actionBar.setTitle("");
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_btn_back, null);
//        cloneBgBtnBack = drawable.getConstantState().newDrawable();
//        cloneBgBtnBack.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        toolbar.setNavigationIcon(R.drawable.bg_white_btn_back);
        sessionId = PrefUtil.getString(context, getString(R.string.prefs_sessionId), null);
        setUpParallax();
        binding.scroll.setVisibility(View.GONE);
        if (communityId == 0) return;
        prepareRemoteCommunityInfo();
        areaInfoBinding.rlEat.setOnClickListener(view -> clickEat());
        areaInfoBinding.rlPlay.setOnClickListener(view -> clickPlay());
        areaInfoBinding.rlStudy.setOnClickListener(view -> clickStudy());
        areaInfoBinding.rlBuy.setOnClickListener(view -> clickBuy());
        areaInfoBinding.rlMedicine.setOnClickListener(view -> clickMedicine());
    }

    @Override
    protected void processArgument(Bundle bundle) {
        communityId = bundle.getLong(EXTRA_COMMUNITY_ID);
        position = bundle.getInt(EXTRA_POSITION);
    }

    @Override
    public String getPageName() {
        return getString(R.string.page_act_community_detail);
    }

    private void setUpParallax() {
        ViewCompat.setTransitionName(binding.ivParallax, SHARE_IMAGE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        binding.ivParallax.getLayoutParams().height = (int) (displaymetrics.widthPixels * 0.75);
        binding.collapsingToolbar.setTitleEnabled(false);
        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (null == community) return;
            int maxScroll = appBarLayout.getTotalScrollRange();
            if (-verticalOffset >= maxScroll && !binding.collapsingToolbar.isTitleEnabled()) {
                binding.collapsingToolbar.setTitle(community.getName());
                binding.collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK);
                binding.collapsingToolbar.setTitleEnabled(true);
                toolbar.setNavigationIcon(R.drawable.bg_btn_back);
            } else if (binding.collapsingToolbar.isTitleEnabled()) {
                binding.collapsingToolbar.setTitleEnabled(false);
                toolbar.setNavigationIcon(R.drawable.bg_white_btn_back);
            }

        });
        binding.flLocation.setOnClickListener(view -> {
            if (TextUtils.isEmpty(community.getLongitude()) || TextUtils.isEmpty(community.getLatitude())) {
                Timber.w("Click event is rejected, because the longitude or latitude not exist.");
                return;
            }
            goLocation();
        });
    }

    private void setUpFollowImage(Community community) {
        if (community.getIsFollow() == Community.IS_FOLLOW) {
            binding.ivFollow.setImageResource(R.drawable.like_icon);
        } else {
            binding.ivFollow.setImageResource(R.drawable.unlike_icon);
        }
    }

    private void updateCommunity(Community community) {
        binding.tvName.setText(community.getName());
        ImageUtil.load(context, community.getPanoUrl(), R.drawable.details_pic, binding.ivParallax);
        binding.tvAddress.setText(community.getAddress());
        binding.tvAvgPrice.setText(String.format(getString(R.string.avg_price_suffix), community.getAvgPrice()));
        binding.tvRegion.setText(community.getRegion());
        binding.tvPlate.setText(community.getPlate());
        binding.tvBuildAge.setText(String.format(getString(R.string.build_age), community.getBuildAge()));
        setUpFollowImage(community);
        binding.ivFollow.setOnClickListener(v -> {
            IEventCallback eventCallback = new IEventCallback() {
                @Override
                public void onSuccess() {
                    setUpFollowImage(community);
                }

                @Override
                public void onError(Exception e) {

                }
            };
            if (community.getIsFollow() == Community.IS_FOLLOW) {
                deleteFollow(community, eventCallback);
            } else {
                addFollow(community, eventCallback);
            }
        });

        if (TextUtils.isEmpty(community.getPano360Url())) {
            binding.iv360.setVisibility(View.GONE);
        } else {
            binding.iv360.setVisibility(View.VISIBLE);
            binding.iv360.setOnClickListener(view -> {
//                Bundle extras = new Bundle();
//                extras.putString(WebViewActivity.EXTRA_TITLE, community.getName());
//                extras.putString(WebViewActivity.EXTRA_URL, community.getPano360Url());
//                navigator.to(WebViewActivity.class, extras);

                // TODO: 2016/8/13
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(community.getPano360Url()));
                navigator.toIntent(intent);
            });
        }

        updateBaseInfo(community);
        updateAreaInfo(community);
        updateSiteInfo(community);
    }

    //社区基本信息
    private void updateBaseInfo(Community community) {
        boolean notExist = true;
        List<Tuple2<Integer, String>> items = new ArrayList<>();
        if (Validator.isNotNullOrBlank(community.getBuildAge())) {
            items.add(new Tuple2<>(R.string.build_age_label, "" + community.getBuildAge()));
            notExist = false;
        }
        if (community.getArea() > 0) {
            items.add(new Tuple2<>(R.string.area_label, "" + community.getArea()));
            notExist = false;
        }
        if (community.getHouseCount() > 0) {
            items.add(new Tuple2<>(R.string.house_count_label, "" + community.getHouseCount() * 100));
            notExist = false;
        }
        if (Validator.isNotNullOrBlank(community.getParkCount())) {
            items.add(new Tuple2<>(R.string.park_count_label, "" + community.getParkCount()));
            notExist = false;
        }
        if (community.getCapacityRate() > 0) {
            items.add(new Tuple2<>(R.string.capacity_rate_label, "" + community.getCapacityRate()));
            notExist = false;
        }
        if (community.getGreenRate() > 0) {
            items.add(new Tuple2<>(R.string.green_rate_label, Math.round(community.getGreenRate() * 100) + getString(R.string.percent)));
            notExist = false;
        }

        if (notExist) {
            baseInfoBinding.llBaseInfo.setVisibility(View.GONE);
        } else {
            baseInfoBinding.grid.setOnTouchListener((v, event) -> true);

            BaseInfoAdapter baseInfoAdapter = new BaseInfoAdapter(context, items);
            baseInfoBinding.grid.setAdapter(baseInfoAdapter);
            UIUtils.setGridViewHeightBasedOnChildren(baseInfoBinding.grid);
        }
    }

    //周边信息
    private void updateAreaInfo(Community community) {
        boolean notExist = true;
        if (community.getChi() > 0) {
            notExist = false;
            areaInfoBinding.tvEatInfo.setText(String.format(getString(R.string.eat_info), community.getChi()));
            areaInfoBinding.tvEatDetail.setText(community.getChiName());
        } else {
            areaInfoBinding.rlEat.setVisibility(View.GONE);
            areaInfoBinding.lineEat.setVisibility(View.GONE);
        }
        if (community.getMai() > 0) {
            notExist = false;
            areaInfoBinding.tvBuyInfo.setText(String.format(getString(R.string.buy_info), community.getMai()));
            areaInfoBinding.tvBuyDetail.setText(community.getMaiName());
        } else {
            areaInfoBinding.rlBuy.setVisibility(View.GONE);
            areaInfoBinding.lineBuy.setVisibility(View.GONE);
        }
        if (community.getYi() > 0) {
            notExist = false;
            areaInfoBinding.tvMedicineInfo.setText(String.format(getString(R.string.medicine_info), community.getYi()));
            areaInfoBinding.tvMedicineDetail.setText(community.getYiName());
        } else {
            areaInfoBinding.rlMedicine.setVisibility(View.GONE);
            areaInfoBinding.lineMedicine.setVisibility(View.GONE);
        }
        if (community.getWan() > 0) {
            notExist = false;
            areaInfoBinding.tvPlayInfo.setText(String.format(getString(R.string.play_info), community.getWan()));
            areaInfoBinding.tvPlayDetail.setText(community.getWanName());
        } else {
            areaInfoBinding.rlPlay.setVisibility(View.GONE);
            areaInfoBinding.linePlay.setVisibility(View.GONE);
        }
        if (community.getXue() > 0) {
            notExist = false;
            areaInfoBinding.tvStudyInfo.setText(String.format(getString(R.string.study_info), community.getXue()));
            areaInfoBinding.tvStudyDetail.setText(community.getXueName());
        } else {
            areaInfoBinding.rlStudy.setVisibility(View.GONE);
            areaInfoBinding.lineStudy.setVisibility(View.GONE);
        }

        areaInfoBinding.tvShowAllInfo.setOnClickListener(v -> {
            goAround(AroundActivity.EAT);
        });

        if (notExist) {
            areaInfoBinding.llAreaInfo.setVisibility(View.GONE);
        }
    }

    //房源信息
    private void updateSiteInfo(Community community) {
        List<Site> sites = community.getSiteList();
        for (int i = 0; i < sites.size(); i++) {
            Site site = sites.get(i);
            if (site.getHouseCount() == 0) {
                sites.remove(site);
                i--;
            }
        }

        if (Validator.isNullOrEmpty(sites)) {
            binding.tvSiteInfo.setVisibility(View.GONE);
            return;
        }
        int size = sites.size();
        for (int i = 0; i < size; i++) {
            Site site = sites.get(i);

            ItemSiteBinding siteBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_site, binding.llArea, false);
            binding.llArea.addView(siteBinding.getRoot());
            siteBinding.tvSaleNum.setText("" + site.getHouseCount());
            int logo = site.getName().equals("soufun") ? R.drawable.soufang_logo : R.drawable.lianjia_logo;
            siteBinding.ivLogo.setImageResource(logo);

            int minSize = 3;
            SiteAdapter adapter = new SiteAdapter(context, minSize, site.getHouseList(), site.getName());
            siteBinding.list.setAdapter(adapter);
            siteBinding.list.setOnItemClickListener((parent, view, position, id) -> {
                House house = site.getHouseList().get(position);
                String url = house.getInfoUrl();
                if (TextUtils.isEmpty(url)) {
                    return;
                }

                // TODO: 2016/8/6
                Bundle extras = new Bundle();
                extras.putString(WebViewActivity.EXTRA_URL, url);
                extras.putString(WebViewActivity.EXTRA_TITLE, UIUtils.getString(R.string.housing_details));
                navigator.to(WebViewActivity.class, extras);
            });
            UIUtils.setListViewHeightBasedOnChildren(siteBinding.list);

            siteBinding.rlSite.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString(HouseListActivity.EXTRA_SITE, site.getName());
                bundle.putLong(HouseListActivity.EXTRA_COMMUNITY_ID, communityId);
                navigator.to(HouseListActivity.class, bundle);
            });
        }
    }

    private void prepareRemoteCommunityInfo() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("communityId", communityId)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_COMMUNITY_INFO, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    JSONObject response = jsonObject.optJSONObject("response");
                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                        return;
                    }
                    if (null == response) {
                        // TODO: 2016/7/26
                        return;
                    }
                    binding.scroll.setVisibility(View.VISIBLE);
                    Type type = new TypeToken<Community>() {
                    }.getType();
                    community = gson.fromJson(response.toString(), type);
                    Timber.d("Receive community %s", community);

                    updateCommunity(community);
                });
    }

    private void addFollow(Community community, IEventCallback eventCallback) {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("communityId", community.getId())
                .build();
        zejiaService.doRequest(this, Constants.API.ADD_FOLLOW, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                        //rollback
                        community.setIsFollow(0);
                        eventCallback.onError(null);
                    } else {
                        community.setIsFollow(Community.IS_FOLLOW);
                        eventCallback.onSuccess();
                    }
                });
    }

    private void deleteFollow(Community community, IEventCallback eventCallback) {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("communityId", community.getId())
                .build();
        zejiaService.doRequest(this, Constants.API.DELETE_FOLLOW, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                        //rollback
                        community.setIsFollow(Community.IS_FOLLOW);
                        eventCallback.onError(null);
                    } else {
                        community.setIsFollow(0);
                        eventCallback.onSuccess();
                    }
                });
    }

    private void goLocation() {
        Intent intent_map = new Intent(this, BaiduMapActivity.class);
        String latitude = community.getLatitude();
        String longitude = community.getLongitude();
        String name = community.getName();
        intent_map.putExtra("NAME", name);
        intent_map.putExtra("LATITUDE", latitude);
        intent_map.putExtra("LONGITUDE", longitude);
        startActivity(intent_map);
    }

    @Override
    public void onBackPressed() {
        if (null != community) {
            Bundle extras = new Bundle();
            extras.putInt(MyFollowActivity.EXTRA_UPDATE_POSITION, position);
            extras.putInt(MyFollowActivity.EXTRA_UPDATE_STATUS, community.getIsFollow());
            Intent intent = new Intent();
            intent.putExtras(extras);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void clickEat() {
        goAround(AroundActivity.EAT);
    }

    private void clickPlay() {
        goAround(AroundActivity.PLAY);
    }

    private void clickStudy() {
        goAround(AroundActivity.STUDY);
    }

    private void clickBuy() {
        goAround(AroundActivity.BUY);
    }

    private void clickMedicine() {
        goAround(AroundActivity.CURE);
    }

    private void goAround(int type) {
        Intent intent_around = new Intent(this, AroundActivity.class);
        String name = community.getName();
        intent_around.putExtra("NAME", name);
        intent_around.putExtra("TYPE", type);
        intent_around.putExtra("COMMUNITYID", communityId);
        intent_around.putExtra("LATITUDE", community.getLatitude());
        intent_around.putExtra("LONGITUDE", community.getLongitude());
        startActivity(intent_around);
    }
}
