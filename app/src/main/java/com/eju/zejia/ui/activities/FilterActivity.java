package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.Filter;
import com.eju.zejia.data.models.FilterBean;
import com.eju.zejia.databinding.ActivityFilterBinding;
import com.eju.zejia.ui.adapters.FilterAdapter;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rx.internal.schedulers.ExecutorScheduler;
import timber.log.Timber;


public class FilterActivity extends BaseActivity implements GestureDetector.OnGestureListener {

//    private static final String mJsonResponse =
//            "{" +
////                "\"code\": \"200\",\"msg\": \"成功\"," +
////                "\"response\": {" +
//                    "\"filterCheck\": {}," +
//                    "\"filterValue\": {" +
//                        "\"areaCompose\": [" +
//                            "{\"code\": 1,\"name\": \"50平以下\"}," +
//                            "{\"code\": 2,\"name\": \"50-70平\"}," +
//                            "{\"code\": 3,\"name\": \"70-90平\"}," +
//                            "{\"code\": 4,\"name\": \"90-110平\"}]," +
//                        "\"buildAgeCompose\": [" +
//                            "{\"code\": 1,\"name\": \"不限\"}," +
//                            "{\"code\": 2,\"name\": \"2年内\"}," +
//                            "{\"code\": 3,\"name\": \"5年内\"}," +
//                            "{\"code\": 4,\"name\": \"10年内\"}]," +
//                        "\"communityCompose\": [" +
//                            "{\"code\": 1,\"name\": \"品牌开发商\"}," +
//                            "{\"code\": 2,\"name\": \"品牌物业\"}," +
//                            "{\"code\": 3,\"name\": \"车位充足\"}]," +
//                        "\"familyCompose\": [" +
//                            "{\"code\": 1,\"name\": \"我\"}," +
//                            "{\"code\": 2,\"name\": \"伴侣\"}," +
//                            "{\"code\": 3,\"name\": \"孩子\"}," +
//                            "{\"code\": 4,\"name\": \"老人\"}]," +
//                        "\"houseLifeCompose\": [" +
//                            "{\"code\": 1,\"name\": \"满五唯一\"}," +
//                            "{\"code\": 2,\"name\": \"满五\"}," +
//                            "{\"code\": 3,\"name\": \"满二\"}]," +
//                        "\"houseTypeCompose\": [" +
//                            "{\"code\": 1,\"name\": \"普通住宅\"}," +
//                            "{\"code\": 2,\"name\": \"商住两用\"}," +
//                            "{\"code\": 3,\"name\": \"别墅\"},{\"code\": 4,\"name\": \"不限\"}]," +
//                        "\"infrastructureCompose\": [" +
//                            "{\"code\": 1,\"name\": \"菜场\"}," +
//                            "{\"code\": 2,\"name\": \"便利店\"}," +
//                            "{\"code\": 3,\"name\": \"中型超市\"}," +
//                            "{\"code\": 4,\"name\": \"咖啡店\"}]," +
//                        "\"layoutCompose\": [" +
//                            "{\"code\": 1,\"name\": \"一室\"}," +
//                            "{\"code\": 2,\"name\": \"二室\"}," +
//                            "{\"code\": 3,\"name\": \"三室\"}," +
//                            "{\"code\": 4,\"name\": \"四室及以上\"}]," +
//                        "\"lifeCompose\": [" +
//                            "{\"code\": 1,\"name\": \"买菜卖水果\"}," +
//                            "{\"code\": 2,\"name\": \"散步\"}," +
//                            "{\"code\": 3,\"name\": \"遛狗\"}," +
//                            "{\"code\": 4,\"name\": \"叫外卖\"}]," +
//                        "\"orderComposes\": [" +
//                            "{\"code\": 1,\"name\": \"智能\"}," +
//                            "{\"code\": 2,\"name\": \"可售房源\"}," +
//                            "{\"code\": 3,\"name\": \"均价\"}," +
//                            "{\"code\": 4,\"name\": \"总价\"}]," +
//                        "\"priceCompose\": [" +
//                            "{\"code\": 1,\"name\": \"100万以下\"}," +
//                            "{\"code\": 2,\"name\": \"100-150万\"}," +
//                            "{\"code\": 3,\"name\": \"150-200万\"}," +
//                            "{\"code\": 4,\"name\": \"200-300万\"}]," +
//                        "\"regionComposes\": [" +
//                            "{\"code\": 1,\"name\": \"浦东\"}," +
//                            "{\"code\": 2,\"name\": \"闵行\"}," +
//                            "{\"code\": 3,\"name\": \"宝山\"}," +
//                            "{\"code\": 4,\"name\": \"徐汇\"}]," +
//                        "\"supportComposes\": [" +
//                            "{\"code\": 1,\"name\": \"大行超市\"}," +
//                            "{\"code\": 2,\"name\": \"幼儿园\"}," +
//                            "{\"code\": 3,\"name\": \"三甲医院\"}," +
//                            "{\"code\": 4,\"name\": \"商圈\"}]" +
//                    "}" +
////                "}" +
//            "}";

//    private String[][] familyItems = {
//            {"我", "伴侣", "孩子", "老人"},
//            {"买菜买水果", "散步", "遛狗", "叫外卖", "逛街购物", "健身", "打羽毛球", "看电影", "聚会", "撸串"}};
//
//    private String[][] communityItems = {
//            {"品牌开发商", "品牌物业", "车位充足", "学区房", "地铁房", "不要动迁房", "不要老公房"},
//            {"菜场", "便利店", "中型超市", "咖啡店", "邮局", "银行", "街道医院", "餐厅"},
//            {"大型超市", "幼儿园", "三甲医院", "商圈", "公园", "地铁站", "中小学"}};
//
//    private String[][] houseItems = {
//            {"一室", "二室", "三室", "四室及以上"},
//            {"普通住宅", "商住两用", "别墅", "不限"},
//            {"不限", "2年内", "5年内", "10年内"},
//            {"50平以下", "50-70平", "70-90平", "90-110平", "110-130平", "130-150平", "150-200平", "200平以上"},
//            {"满五唯一", "满五", "满二"}};

    private static final int RIGHT_FLING_DISTANCE = UIUtils.dip2px(256);
    private static final int RIGHT_FLING_VELOCITY = UIUtils.dip2px(16);

    private String[] familyDescription;
    private String[] communityDescription;

    private String FAMILY_COMPOSE;
    private String LIFE_COMPOSE;
    private String COMMUNITY_COMPOSE;
    private String INFRASTRUCTURE_COMPOSE;
    private String SUPPORT_COMPOSE;
    private String LAYOUT_COMPOSE;
    private String HOUSE_TYPE_COMPOSE;
    private String BUILD_AGE_COMPOSE;
    private String AREA_COMPOSE;
    private String HOUSE_LIFE_COMPOSE;

    private ActivityFilterBinding mBinding;
    private ExecutorService mExecutorService;
    private Handler mHandler;

    private GestureDetector mGestureDetector;


    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_filter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mExecutorService = Executors.newSingleThreadExecutor();
        mHandler = new Handler(getMainLooper());

        initViews(mBinding = DataBindingUtil.setContentView(this, R.layout.activity_filter));
        initLocalData();
        initServerData();
    }

    private void initLocalData() {
        String[] familyTitle = getResources().getStringArray(R.array.family);
        String[] communityTitle = getResources().getStringArray(R.array.community);
        String[] houseTitle = getResources().getStringArray(R.array.house);
        familyDescription = getResources().getStringArray(R.array.family_des);
        communityDescription = getResources().getStringArray(R.array.community_des);

        FAMILY_COMPOSE = familyTitle[0];
        LIFE_COMPOSE = familyTitle[1];
        COMMUNITY_COMPOSE = communityTitle[0];
        INFRASTRUCTURE_COMPOSE = communityTitle[1];
        SUPPORT_COMPOSE = communityTitle[2];
        LAYOUT_COMPOSE = houseTitle[0];
        HOUSE_TYPE_COMPOSE = houseTitle[1];
        BUILD_AGE_COMPOSE = houseTitle[2];
        AREA_COMPOSE = houseTitle[3];
        HOUSE_LIFE_COMPOSE = houseTitle[4];
    }

    private void initServerData() {
//        FilterBean bean = GsonFactory.changeGsonToBean(mJsonResponse, FilterBean.class);
//        JSONObject obj = DataManager.getInstance().getCacheService().getFilterList(this);
//        if(null == obj) {
            String sessionId = PrefUtil.getString(context,
                    getString(R.string.prefs_sessionId), null);
            if(StringUtils.isNullString(sessionId)) {
                UIUtils.showToastSafe("请重新登录");
                return;
            }

            JSONObject data = JSONBuilder.newBuilder()
                    .putAlways("sessionId", sessionId)
                    .build();
            DataManager.getInstance().getZejiaService().doRequest(context,
                    Constants.API.GET_FILTER_LIST, data,
                    jsonObject -> {
                        int code = jsonObject.optInt("code");
                        String message = jsonObject.optString("msg");
                        JSONObject response = jsonObject.optJSONObject("response");
                        if (code != 200) {
                            UIUtils.showLongToastSafe(message);
                            return;
                        }
                        if (null == response) {
                            return;
                        }

                        analyzeJsonResponse(response.toString());
                    }, new ExecutorScheduler(mExecutorService));
//        } else {
//            mExecutorService.submit((Runnable) () -> analyzeJsonResponse(mJsonResponse));
//        }
    }

    private final List<Filter> mFilterItemList = new ArrayList<>();
    private void analyzeJsonResponse(String json) {
        Timber.d("init data");
        FilterBean bean = GsonFactory.changeGsonToBean(json, FilterBean.class);
        if (null == bean) {
            return;
        }

        FilterBean.FilterCheckBean filterCheck = bean.getFilterCheck();
        FilterBean.FilterValueBean filterValue = bean.getFilterValue();

        analyzeEach(FAMILY_COMPOSE, familyDescription[0],
                filterValue.getFamilyCompose(), null == filterCheck ? "" :filterCheck.getFamilyCompose());
        analyzeEach(LIFE_COMPOSE, familyDescription[1],
                filterValue.getLifeCompose(), null == filterCheck ? "" :filterCheck.getLifeCompose());
        addDividerToFilterList();

        analyzeEach(COMMUNITY_COMPOSE, communityDescription[0],
                filterValue.getCommunityCompose(), null == filterCheck ? "" :filterCheck.getCommunityCompose());
        analyzeEach(INFRASTRUCTURE_COMPOSE, communityDescription[1],
                filterValue.getInfrastructureCompose(), null == filterCheck ? "" :filterCheck.getInfrastructureCompose());
        analyzeEach(SUPPORT_COMPOSE, communityDescription[2],
                filterValue.getSupportComposes(), null == filterCheck ? "" :filterCheck.getSupportComposes());
        addDividerToFilterList();

        analyzeEach(LAYOUT_COMPOSE, "",
                filterValue.getLayoutCompose(), null == filterCheck ? "" :filterCheck.getLayoutCompose());
        analyzeEach(HOUSE_TYPE_COMPOSE, "",
                filterValue.getHouseTypeCompose(), null == filterCheck ? "" :filterCheck.getHouseTypeCompose());
        analyzeEach(BUILD_AGE_COMPOSE, "",
                filterValue.getBuildAgeCompose(), null == filterCheck ? "" :filterCheck.getBuildAgeCompose());
        analyzeEach(AREA_COMPOSE, "",
                filterValue.getAreaCompose(), null == filterCheck ? "" :filterCheck.getAreaCompose());
        analyzeEach(HOUSE_LIFE_COMPOSE, "",
                filterValue.getHouseLifeCompose(), null == filterCheck ? "" :filterCheck.getHouseLifeCompose());

        mHandler.post(() -> ((FilterAdapter) mBinding.filterRcv.getAdapter())
                .updateItems(mFilterItemList, true));
    }

    private void analyzeEach(String title, String description,
                             List<FilterBean.FilterItemBean> value, String check) {
        HashSet<Integer> checked = new HashSet<>();

        if(StringUtils.isNotNullString(check)) {
            String[] allCheck = check.split(",");
            for (String each : allCheck) {
                checked.add(Integer.valueOf(each));
            }
        }

        description = description.isEmpty() ?
                description :
                String.format(UIUtils.getString(R.string.filter_item_des), description);
        description = " " + description;

        addItemToFilterList(title, description, sortList(value), checked);
    }

    private void addDividerToFilterList() {
        addItemToFilterList("", "", null, null);
    }

    private void addItemToFilterList(String title, String description,
                                     List<FilterBean.FilterItemBean> list, HashSet<Integer> set) {
        Filter each = new Filter();
        each.setTitle(title);
        each.setDescription(description);
        each.setCollapse(true);
        each.setItems(list);
        each.setChecked(set);
        mFilterItemList.add(each);
    }

    private List<FilterBean.FilterItemBean> sortList(List<FilterBean.FilterItemBean> list) {
        FilterBean.FilterItemBean[] sorted = new FilterBean.FilterItemBean[list.size()];
        Arrays.sort(list.toArray(sorted),
                (l, r) -> l.getCode() - r.getCode());

        return Arrays.asList(sorted);
    }

    private void initViews(ActivityFilterBinding binding) {
        setWindowAttrs();
        setFilterRecycle(binding.filterRcv);
        setGesture();
    }

    private void setWindowAttrs() {
        WindowManager.LayoutParams wlp = getWindow().getAttributes();
//        wlp.gravity = Gravity.END | Gravity.TOP;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        getWindow().setAttributes(wlp);
    }

    private RecyclerView setFilterRecycle(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new FilterAdapter(this));
        recyclerView.addItemDecoration(new FilterAdapter.FilterDecoration());

        return recyclerView;
    }

    private void setGesture() {
        mGestureDetector = new GestureDetector(this, this);
    }

    public void onResetClick(View v) {
        for (Filter filter : mFilterItemList) {
//            final HashSet<Integer> oldSet = new HashSet<>(item.checked.size());
//            oldSet.addAll(item.checked);
            final HashSet<Integer> set = filter.getChecked();
            if(null != set) set.clear();
//            Timber.d("%s, %s->%s", item.title, oldSet, item.checked);
        }
        mBinding.filterRcv.getAdapter().notifyItemRangeChanged(0, mFilterItemList.size());
    }

    public void onConfirmClick(View v) {
        String sessionId = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_sessionId),
                "");
        JSONObject checkedList = JSONBuilder.newBuilder()
                .putAlways("familyCompose", getFilterChecked(FAMILY_COMPOSE))
                .putAlways("areaCompose", getFilterChecked(AREA_COMPOSE))
                .putAlways("buildAgeCompose", getFilterChecked(BUILD_AGE_COMPOSE))
                .putAlways("communityCompose", getFilterChecked(COMMUNITY_COMPOSE))
                .putAlways("houseLifeCompose", getFilterChecked(HOUSE_LIFE_COMPOSE))
                .putAlways("houseTypeCompose", getFilterChecked(HOUSE_TYPE_COMPOSE))
                .putAlways("infrastructureCompose", getFilterChecked(INFRASTRUCTURE_COMPOSE))
                .putAlways("lifeCompose", getFilterChecked(LIFE_COMPOSE))
                .putAlways("layoutCompose", getFilterChecked(LAYOUT_COMPOSE))
                .putAlways("supportComposes", getFilterChecked(SUPPORT_COMPOSE))
                .build();
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("filterCheck", checkedList)
                .build();
        DataManager.getInstance().getZejiaService().doRequest(this, Constants.API.SAVE_CHOOSE_FILTER, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");

                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
    }

    private String getFilterChecked(String title) {
        for (Filter filter : mFilterItemList) {
            if(title.equalsIgnoreCase(filter.getTitle())) {
                return makeCheckedString(filter.getChecked());
            }
        }
        return "";
    }

    private String makeCheckedString(HashSet<Integer> set) {
        StringBuilder builder = new StringBuilder();
        for (int integer : set) {
            builder.append(integer).append(",");
        }

        if (builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float offsetX = e2.getX() - e1.getX();
//        Timber.d("fling distance: %f", offsetX);
        if(offsetX > RIGHT_FLING_DISTANCE && velocityX > RIGHT_FLING_VELOCITY) {
            onConfirmClick(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event);
    }
}
