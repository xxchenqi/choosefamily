package com.eju.zejia.ui.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.data.models.Community;
import com.eju.zejia.data.models.Follow;
import com.eju.zejia.data.remote.ZejiaService;
import com.eju.zejia.databinding.ActivityMyFollowBinding;
import com.eju.zejia.interfaces.IEventCallback;
import com.eju.zejia.ui.adapters.FollowAdapter;
import com.eju.zejia.utils.GsonFactory;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.UIUtils;
import com.eju.zejia.utils.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 我的关注页面
 * <p>
 * Created by Sidney on 2016/7/25.
 */
public class MyFollowActivity extends BaseActivity {

    public static final String EXTRA_UPDATE_POSITION = "updatePosition";
    public static final String EXTRA_UPDATE_STATUS = "updateStatus";
    public static final int REQUEST_UPDATE = 10010;

    private ActivityMyFollowBinding binding;
    private ZejiaService zejiaService;
    private Gson gson;
    private FollowAdapter adapter;
    private String sessionId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_follow);
        setUpToolbar(R.id.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        gson = GsonFactory.getGson();
        zejiaService = DataManager.getInstance().getZejiaService();
        sessionId = PrefUtil.getString(context, getString(R.string.prefs_sessionId), null);
        setUpListView();
    }

    @Override
    public String getPageName() {
        return getString(R.string.page_act_follow);
    }

    private void setUpListView() {
        adapter = new FollowAdapter(context);
        binding.list.setAdapter(adapter);
        binding.list.setOnItemClickListener((parent, view, position, id) -> {
            Follow follow = adapter.getItem(position);
            Bundle extras = new Bundle();
            extras.putLong(CommunityDetailActivity.EXTRA_COMMUNITY_ID, follow.getId());
            extras.putInt(CommunityDetailActivity.EXTRA_POSITION, position);
            navigator.toForResult(CommunityDetailActivity.class, REQUEST_UPDATE, extras);
        });
        binding.list.setOnItemLongClickListener((parent, view, position, id) -> {
            UIUtils.showAlertDialog(context, R.string.msg_confirm_delete, R.string.ok, R.string.cancel, new IEventCallback() {
                @Override
                public void onSuccess() {
                    Follow follow = adapter.getItem(position);
                    deleteFollow(follow, new IEventCallback() {
                        @Override
                        public void onSuccess() {
                            adapter.removeItem(position);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
            return true;
        });

        prepareRemoteData();
    }

    private void prepareRemoteData() {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();
        zejiaService.doRequest(this, Constants.API.GET_FOLLOW_LIST, data,
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
                    JSONArray dataList = response.optJSONArray("dataList");
                    if (Validator.isNullOrEmpty(dataList)) {
                        // TODO: 2016/8/1
                        return;
                    }
                    Type type = new TypeToken<List<Follow>>() {
                    }.getType();
                    List<Follow> follows = gson.fromJson(dataList.toString(), type);
                    adapter.updateItems(follows);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        if (requestCode == REQUEST_UPDATE) {
            Bundle extras = data.getExtras();
            int position = extras.getInt(EXTRA_UPDATE_POSITION);
            int follow = extras.getInt(EXTRA_UPDATE_STATUS);
            if (follow != Community.IS_FOLLOW) {
                adapter.removeItem(position);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteFollow(Follow follow, IEventCallback eventCallback) {
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .putAlways("communityId", follow.getId())
                .build();
        zejiaService.doRequest(this, Constants.API.DELETE_FOLLOW, data,
                jsonObject -> {
                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");
                    if (code != 200) {
                        UIUtils.showLongToastSafe(message);
                        //rollback
                        eventCallback.onError(null);
                    } else {
                        eventCallback.onSuccess();
                    }
                });
    }
}
