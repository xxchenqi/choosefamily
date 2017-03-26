package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.House;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityHouseListBinding;
import com.eju.zejia.netframe.interfaces.RxProgressSubscriberAdapter;
import com.eju.zejia.ui.adapters.HouseAdapter;
import com.eju.zejia.ui.views.SuperSwipeRefreshLayout;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Sidney on 2016/8/6.
 */
public class HouseListActivity extends BaseActivity {

    public static final String EXTRA_SITE = "site";
    public static final String EXTRA_COMMUNITY_ID = "communityId";

    private ActivityHouseListBinding binding;
    private ZejiaService zejiaService;
    private Gson gson;
    private String sessionId;
    private String site;
    private int currentPage = 1;
    private int count;
    private HouseAdapter adapter;
    private long communityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house_list);
        gson = GsonFactory.getGson();
        zejiaService = DataManager.getInstance().getZejiaService();
        setUpToolbar(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        actionBar.setTitle("");
        sessionId = PrefUtil.getString(context, getString(R.string.prefs_sessionId), null);
        String title;
        if ("lianjia".equals(site)) {
            title = getString(R.string.lianjia);
        } else {
            title = getString(R.string.fangtianxia);
        }
        binding.tvTitle.setText(title);

        setUpListView();
        prepareRemoteData(1);
    }

    @Override
    protected void processArgument(Bundle bundle) {
        super.processArgument(bundle);
        site = bundle.getString(EXTRA_SITE);
        communityId = bundle.getLong(EXTRA_COMMUNITY_ID);
    }

    private void setUpListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recycler.setLayoutManager(layoutManager);
        adapter = new HouseAdapter(this, site);
        binding.recycler.setAdapter(adapter);
        adapter.setOnItemClickListener(new HouseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                House house = adapter.getItem(position);
                String url = house.getInfoUrl();
                if (TextUtils.isEmpty(url)) {
                    return;
                }

                // TODO: 2016/8/6
                Bundle extras = new Bundle();
                extras.putString(WebViewActivity.EXTRA_URL, url);
                extras.putString(WebViewActivity.EXTRA_TITLE, UIUtils.getString(R.string.housing_details));
                navigator.to(WebViewActivity.class, extras);
            }
        });
        View footer = getLayoutInflater().inflate(R.layout.item_footer, null);
        binding.swipe.setFooterView(footer);
        binding.swipe.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListenerAdapter() {
            @Override
            public void onRefresh() {
                prepareRemoteData(1);
            }
        });
        binding.swipe.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListenerAdapter() {
            @Override
            public void onLoadMore() {
                prepareRemoteData(currentPage + 1);
            }
        });
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_house_list);
    }

    private void prepareRemoteData(int page) {
        if (binding.swipe.isLoadMore()) {
            if (count != 0 && adapter.getItemCount() >= count) {
                if (binding.swipe.isRefreshing()) {
                    binding.swipe.setRefreshing(false);
                }
                binding.swipe.setLoadMore(false);
                UIUtils.showToastSafe(R.string.no_more_data);
                return;
            }
        }
        Timber.d("Request page %s", page);
        int startIndex = (page - 1) * Constants.PAGE_COUNT;
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("site", site)
                .putAlways("communityId", communityId)
                .putAlways("startIndex", startIndex)
                .putAlways("pageCount", Constants.PAGE_COUNT)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_HOUSE_LIST, data, new RxProgressSubscriberAdapter<JSONObject>() {
            @Override
            public void onCancel() {
                if (binding.swipe.isRefreshing()) {
                    binding.swipe.setRefreshing(false);
                }
                binding.swipe.setLoadMore(false);
            }

            @Override
            public void onError(Throwable e) {
                if (binding.swipe.isRefreshing()) {
                    binding.swipe.setRefreshing(false);
                }
                binding.swipe.setLoadMore(false);
                UIUtils.showLongToastSafe("网络请求失败");
            }

            @Override
            public void onNext(JSONObject jsonObject) {
                if (binding.swipe.isRefreshing()) {
                    binding.swipe.setRefreshing(false);
                }
                binding.swipe.setLoadMore(false);
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
                processData(page, response);
            }
        });
    }

    private void processData(int page, JSONObject response) {
        if (null == response) return;
        JSONArray houseList = response.optJSONArray("houseList");
        int count = response.optInt("count");
        if (count == 0) return;
        this.count = count;
        Type type = new TypeToken<List<House>>() {
        }.getType();
        if (count < Constants.PAGE_COUNT) {
            binding.swipe.setLoadMore(false);
        }
        List<House> houses = gson.fromJson(houseList.toString(), type);
        if (page == 1) {
            adapter.updateItems(houses, false);
        } else {
            adapter.insertItems(houses, true);
        }
        Timber.d("count is %s, current page is %s", count, page);
    }
}
