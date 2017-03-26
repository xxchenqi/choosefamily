package com.eju.zejia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eju.zejia.R;
import com.eju.zejia.common.ExtraConstant;
import com.eju.zejia.common.RequestCodeConstant;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.local.CacheService;
import com.eju.zejia.data.models.Community;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityContentMainBinding;
import com.eju.zejia.databinding.ActivityMainBinding;
import com.eju.zejia.interfaces.IEventCallback;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAdapter;
import com.eju.zejia.ui.adapters.CommunityAdapter;
import com.eju.zejia.ui.adapters.CommunityPopupAdapter;
import com.eju.zejia.ui.views.SuperSwipeRefreshLayout;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_UPDATE_POSITION = "updatePosition";
    public static final String EXTRA_UPDATE_STATUS = "updateStatus";
    public static final int REQUEST_UPDATE = 10010;


    private Toolbar toolbar;
    private TextView tv_main_person;
    private ActivityContentMainBinding mainBinding;
    private ZejiaService zejiaService;
    private CacheService cacheService;
    private Gson gson;
    private CommunityAdapter adapter;
    private RectF areaRectF;
    private RectF priceRectF;
    private RectF sortRectF;
    private PopupWindow popupWindow;
    private View anchor;
    private int currentPage = 1;
    private String sessionId;
    private int priceCompose;
    private int regionCompose;
    private int orderCompose;
    private String photoUrl;
    private String nickname;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushOneActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(view -> changePage());
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        photoUrl = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_photoUrl), "");
        nickname = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_nickname), "");
        binding.tvMainName.setText(nickname);
        if (StringUtils.isNotNullString(photoUrl)) {
            Glide.with(this).load(photoUrl).error(R.drawable.default_avatar).into(binding.civAvatar);
        }
        binding.tvMainPerson.setOnClickListener(v ->
                startActivityForResult(new Intent(this, PersonalInformationActivity.class)
                        , RequestCodeConstant.REQUEST_REFRESH));
        binding.civAvatar.setOnClickListener(v ->
                startActivityForResult(new Intent(this, PersonalInformationActivity.class)
                        , RequestCodeConstant.REQUEST_REFRESH));
        binding.tvMainCare.setOnClickListener(v -> navigator.to(MyFollowActivity.class));
        binding.tvMainSetting.setOnClickListener(v -> navigator.to(SettingActivity.class));
        binding.ivFilter.setOnClickListener(v -> {
            startActivityForResult(new Intent(context, FilterActivity.class),
                    RequestCodeConstant.REQUEST_FILTER);
            overridePendingTransition(R.anim.slide_in_right, -1);
        });

        // TODO: 2016/7/29
        mainBinding = DataBindingUtil.findBinding(findViewById(R.id.main_content));
        gson = GsonFactory.getGson();
        zejiaService = DataManager.getInstance().getZejiaService();
        cacheService = DataManager.getInstance().getCacheService();
        mainBinding.container.getForeground().setAlpha(0);
        sessionId = PrefUtil.getString(context, getString(R.string.prefs_sessionId), null);
        setUpRecycler();

        setUpPopupWindow();

//        prepareFilterList();
//        JSONObject data = cacheService.getMatchCommunity(this);
//        processData(1, data);
        prepareRemoteData(1);

        TextView tv_main_search = (TextView) findViewById(R.id.tv_main_search);
        tv_main_search.setOnClickListener(view -> mapTest());
    }

    @Override
    public String getPageName() {
        return getString(R.string.page_act_main);
    }

    private void setUpRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mainBinding.recycler.setLayoutManager(layoutManager);
        adapter = new CommunityAdapter(this);
        mainBinding.recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new CommunityAdapter.OnItemClickListener() {
            @Override
            public void onFollowClick(ImageView view, int position) {
                // TODO: 2016/7/29
                Community community = adapter.getItem(position);
                IEventCallback eventCallback = new IEventCallback() {
                    @Override
                    public void onSuccess() {
                        if (community.getIsFollow() == Community.IS_FOLLOW) {
//                            AnimatorSet animatorSet = new AnimatorSet();
//
//                            ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
//                            rotationAnim.setDuration(800);
//                            rotationAnim.setInterpolator(new AccelerateInterpolator());
//
//                            ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(view, "scaleX", 0.2f, 1.2f);
//                            bounceAnimX.setDuration(300);
//                            bounceAnimX.setInterpolator(new OvershootInterpolator(4));
//
//                            ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(view, "scaleY", 0.2f, 1.2f);
//                            bounceAnimY.setDuration(300);
//                            bounceAnimY.setInterpolator(new OvershootInterpolator(4));
//                            bounceAnimY.addListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
                            view.setImageResource(R.drawable.home_collect_selected_icon);
//                                }
//                            });
//                            animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
//                            animatorSet.start();
                        } else {
                            view.setImageResource(R.drawable.home_collect_default_icon);
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                };
                if (community.getIsFollow() == Community.IS_FOLLOW) {
                    deleteFollow(community, position, eventCallback);
                } else {
                    addFollow(community, position, eventCallback);
                }
            }

            @Override
            public void onImageClick(View view, int position) {
                Community community = adapter.getItem(position);
                Bundle extras = new Bundle();
                extras.putLong(CommunityDetailActivity.EXTRA_COMMUNITY_ID, community.getId());
                extras.putInt(CommunityDetailActivity.EXTRA_POSITION, position);
                // TODO: 2016/8/8
                navigator.toForResult(CommunityDetailActivity.class, REQUEST_UPDATE, extras);
//                navigator.to(CommunityDetailActivity.class,view,"houseImage");
            }
        });
        View footer = getLayoutInflater().inflate(R.layout.item_footer, null);
        mainBinding.swipe.setFooterView(footer);
        mainBinding.swipe.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListenerAdapter() {
            @Override
            public void onRefresh() {
                prepareRemoteData(1);
            }
        });
        mainBinding.swipe.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListenerAdapter() {
            @Override
            public void onLoadMore() {
                prepareRemoteData(currentPage + 1);
            }
        });
    }

    private void prepareRemoteData(int page) {
        Timber.d("Request page %s", page);
        int startIndex = (page - 1) * Constants.PAGE_COUNT;
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("startIndex", startIndex)
                .putAlways("pageCount", Constants.PAGE_COUNT)
                .putIfNotZero("priceCompose", priceCompose)
                .putIfNotZero("regionCompose", regionCompose)
                .putIfNotZero("orderCompose", orderCompose)
                .build();

//        if(1==1){
//            List<Community> communities=new ArrayList<>();
//            Community community=new Community();
//            community.setName("abc");
//            community.setPanoUrl("http://cdn1.dooioo.com/gimage03/M00/62/78/wKgA8lbNicGAZ28TAAEmiVTccyI332.jpg");
//            communities.add(community);
//            adapter.updateItems(communities, false);
//            return;
//        }

        zejiaService.doRequest(this, Constants.API.GET_MATCH_COMMUNITY, data, new RxProgressSubscriberAdapter<JSONObject>() {
            @Override
            public void onCancel() {
                if (mainBinding.swipe.isRefreshing()) {
                    mainBinding.swipe.setRefreshing(false);
                }
                mainBinding.swipe.setLoadMore(false);
            }

            @Override
            public void onError(Throwable e) {
                if (mainBinding.swipe.isRefreshing()) {
                    mainBinding.swipe.setRefreshing(false);
                }
                mainBinding.swipe.setLoadMore(false);
                UIUtils.showLongToastSafe("网络请求失败");
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                if (mainBinding.swipe.isRefreshing()) {
                    mainBinding.swipe.setRefreshing(false);
                }
                mainBinding.swipe.setLoadMore(false);
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
                currentPage = page;
//                cacheService.saveMatchCommunity(context, response);
                processData(page, response);
            }
        });
    }

    private void prepareFilterList() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_FILTER_LIST, data, jsonObject -> {
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
            int count = response.optInt("count");
            if (count == 0) {
                adapter.clear();
                return;
            }
//            DataManager.getInstance().getCacheService().saveFilterList(context, response);
        });
    }

    private void processData(int page, JSONObject response) {
        if (null == response) return;
        JSONArray communityList = response.optJSONArray("communityList");
        int count = response.optInt("count");
        if (count == 0) {
            adapter.clear();
            return;
        }
        Type type = new TypeToken<List<Community>>() {
        }.getType();
        List<Community> communities = gson.fromJson(communityList.toString(), type);
        if (page == 1) {
            adapter.updateItems(communities, false);
        } else {
            adapter.insertItems(communities, true);
        }
    }

    private void addFollow(Community community, int position, IEventCallback eventCallback) {
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
                        adapter.notifyItemChanged(position);
                        eventCallback.onError(null);
                    } else {
                        community.setIsFollow(Community.IS_FOLLOW);
                        eventCallback.onSuccess();
                    }
                });
    }

    private void deleteFollow(Community community, int position, IEventCallback eventCallback) {
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
                        adapter.notifyItemChanged(position);
                        eventCallback.onError(null);
                    } else {
                        community.setIsFollow(0);
                        eventCallback.onSuccess();
                    }
                });
    }

    // TODO: 2016/8/3 remove the following classes
    public static class FilterBean {
        private List<FilterItemBean> priceCompose;
        private List<FilterItemBean> regionComposes;
        private List<FilterItemBean> orderCompose;

        public List<FilterItemBean> getPriceCompose() {
            return priceCompose;
        }

        public void setPriceCompose(List<FilterItemBean> priceCompose) {
            this.priceCompose = priceCompose;
        }

        public List<FilterItemBean> getRegionComposes() {
            return regionComposes;
        }

        public void setRegionComposes(List<FilterItemBean> regionComposes) {
            this.regionComposes = regionComposes;
        }

        public List<FilterItemBean> getOrderCompose() {
            return orderCompose;
        }

        public void setOrderCompose(List<FilterItemBean> orderCompose) {
            this.orderCompose = orderCompose;
        }
    }

    public static class FilterItemBean {
        private int code;
        private String name;
        private boolean check;

        public boolean isCheck() {
            return check;
        }

        public void setCheck(boolean check) {
            this.check = check;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private void setUpPopupWindow() {
        String[] areas = getResources().getStringArray(R.array.area);
        List<FilterItemBean> regionComposes = new ArrayList<>();
        for (int i = 0; i < areas.length; i++) {
            FilterItemBean filterItemBean = new FilterItemBean();
            if (i == 0) {
                filterItemBean.name = areas[0];
                regionComposes.add(filterItemBean);
                continue;
            }
            String[] pair = areas[i].split(",");
            if (pair.length < 2) continue;
            filterItemBean.code = Integer.valueOf(pair[1]);
            filterItemBean.name = pair[0];
            regionComposes.add(filterItemBean);
        }

        String[] prices = getResources().getStringArray(R.array.price);
        List<FilterItemBean> priceComposes = new ArrayList<>();
        for (int i = 0; i < prices.length; i++) {
            FilterItemBean filterItemBean = new FilterItemBean();
            filterItemBean.code = i;
            filterItemBean.name = prices[i];
            priceComposes.add(filterItemBean);
        }

        String[] orders = getResources().getStringArray(R.array.sort);
        List<FilterItemBean> orderComposes = new ArrayList<>();
        for (int i = 0; i < orders.length; i++) {
            FilterItemBean filterItemBean = new FilterItemBean();
            filterItemBean.code = i + 1;
            filterItemBean.name = orders[i];
            orderComposes.add(filterItemBean);
        }

        int unitHeight = getResources().getDimensionPixelSize(R.dimen.DIMEN_48DP);
        int displayCount = 5;
        mainBinding.llArea.setOnClickListener(v ->
                showPopupWindow(regionComposes, mainBinding.llFilter.getWidth(),
                        unitHeight * displayCount,
                        mainBinding.llArea,
                        mainBinding.tvArea,
                        mainBinding.ivArea));
        mainBinding.llPrice.setOnClickListener(v ->
                showPopupWindow(priceComposes, mainBinding.llFilter.getWidth(),
                        unitHeight * displayCount,
                        mainBinding.llPrice,
                        mainBinding.tvPrice,
                        mainBinding.ivPrice));
        mainBinding.llIntell.setOnClickListener(v ->
                showPopupWindow(orderComposes, mainBinding.llFilter.getWidth(),
                        unitHeight * 4,
                        mainBinding.llIntell,
                        mainBinding.tvIntell,
                        mainBinding.ivIntell));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int areaLoc[] = new int[2];
        int priceLoc[] = new int[2];
        int intellLoc[] = new int[2];
        int btnWidth = mainBinding.llArea.getWidth();
        int btnHeight = mainBinding.llArea.getHeight();
        mainBinding.llArea.getLocationOnScreen(areaLoc);
        mainBinding.llPrice.getLocationOnScreen(priceLoc);
        mainBinding.llIntell.getLocationOnScreen(intellLoc);

        areaRectF = new RectF(areaLoc[0], areaLoc[1], areaLoc[0] + btnWidth, areaLoc[1] + btnHeight);
        priceRectF = new RectF(priceLoc[0], priceLoc[1], priceLoc[0] + btnWidth, priceLoc[1] + btnHeight);
        sortRectF = new RectF(intellLoc[0], intellLoc[1], intellLoc[0] + btnWidth, intellLoc[1] + btnHeight);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            float x = ev.getX();
            float y = ev.getY();

            if ((anchor == mainBinding.llPrice && priceRectF.contains(x, y))
                    || (anchor == mainBinding.llIntell && sortRectF.contains(x, y))
                    || (anchor == mainBinding.llArea && areaRectF.contains(x, y))) {
                return true;
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
            return;
        }
        super.onBackPressed();
    }

    private void showPopupWindow(List<FilterItemBean> items, int width, int height, View anchor, TextView anchorText, ImageView anchorImage) {
        if (null == items || items.isEmpty()) return;
        View root = LayoutInflater.from(this).inflate(R.layout.popup_community, null);
        ListView listView = (ListView) root.findViewById(R.id.list);

        CommunityPopupAdapter popupAdapter = new CommunityPopupAdapter(this, items);
        listView.setAdapter(popupAdapter);

        this.anchor = anchor;
        popupWindow = new PopupWindow(root, width, height, true);
        int selected = -1;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).isCheck()) {
                selected = i;
                break;
            }
        }
        if (selected >= 0) {
            listView.setSelection(selected);
        }
        listView.setOnItemClickListener((parent, view, position, id) -> {
            FilterItemBean bean = items.get(position);
            popupAdapter.select(position);
            String name = bean.getName();
            anchorText.setText(name);
            popupWindow.dismiss();
            if (anchor == mainBinding.llPrice) {
                priceCompose = bean.code;
            } else if (anchor == mainBinding.llArea) {
                regionCompose = bean.code;
            } else {
                orderCompose = bean.code;
            }

            // TODO: 2016/8/5
            prepareRemoteData(1);
        });
        popupWindow.setOnDismissListener(() -> {
            anchorImage.animate()
                    .rotation(0).start();
            anchorText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_282323, null));
            mainBinding.container.getForeground().setAlpha(0);
        });
//        popupWindow.setFocusable(false);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchable(true);
        popupWindow.update();
        popupWindow.showAsDropDown(anchor, 0, 1);

        anchorImage.animate()
                .rotation(180).start();
        anchorText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.orange_FF7800, null));
        mainBinding.container.getForeground().setAlpha(255);
    }

    private void mapTest() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ExtraConstant.EXTRA_LOGIN,true);
        navigator.to(CommunityFeatureActivity.class,bundle);
//        Intent intent = new Intent(this, AlwaysAddressActivity.class);
//        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodeConstant.REQUEST_REFRESH) {
            if (PersonalInformationActivity.REFRESH) {
                photoUrl = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_photoUrl), "");
                nickname = PrefUtil.getString(this, UIUtils.getString(R.string.prefs_nickname), "");
                binding.tvMainName.setText(nickname);
                if (StringUtils.isNotNullString(photoUrl)) {
                    Glide.with(this).load(photoUrl).error(R.drawable.default_avatar).into(binding.civAvatar);
                }
            }
        } else if (requestCode == RequestCodeConstant.REQUEST_FILTER && resultCode == Activity.RESULT_OK) {
            prepareRemoteData(1);
        } else if (requestCode == REQUEST_UPDATE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            int position = extras.getInt(EXTRA_UPDATE_POSITION);
            int follow = extras.getInt(EXTRA_UPDATE_STATUS);
            Community community = adapter.getItem(position);
            if (follow != community.getIsFollow()) {
                community.setIsFollow(follow);
                adapter.notifyItemChanged(position);
            }
        }
    }
}