package com.eju.zejia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eju.zejia.BR;
import com.eju.zejia.BuildConfig;
import com.eju.zejia.R;
import com.eju.zejia.config.Constants;
import com.eju.zejia.control.CountControl;
import com.eju.zejia.data.DataManager;
import com.eju.zejia.databinding.ActivitySettingBinding;
import com.eju.zejia.databinding.ItemSettingNavigatorBinding;
import com.eju.zejia.netframe.custom.RequestAPI;
import com.eju.zejia.ui.views.NoScrollRecycleViewLayoutManager;
import com.eju.zejia.ui.views.RecyclerItemView;
import com.eju.zejia.ui.views.RecyclerItemView.Divider;
import com.eju.zejia.ui.views.RecyclerItemView.ViewHolder;
import com.eju.zejia.utils.JSONBuilder;
import com.eju.zejia.utils.MyActivityManager;
import com.eju.zejia.utils.PrefUtil;
import com.eju.zejia.utils.StringUtils;
import com.eju.zejia.utils.UIUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import timber.log.Timber;


public class SettingActivity extends BaseActivity {

    private static final int SPACE_SIZE = UIUtils.dip2px(12);

    private static final int DIVIDER_COLOR = Color.rgb(238, 238, 238);
    private static final int DIVIDER_SIZE= UIUtils.dip2px(1);

    private static final int TYPE_ITEM_NAVIGATOR = 1;
//    private static final int TYPE_ITEM_SWITCH = 2;
    private static final int TYPE_SEPARATOR = 3;


    private final class SettingItem {
        String title;
        Callable<String> description;
        int type;
        View.OnClickListener listener;


        SettingItem() {
            this(-1, null);
        }

        SettingItem(int titleResID, View.OnClickListener listener) {
            this(titleResID, null, listener);
        }

        SettingItem(int titleResID, Callable<String> description, View.OnClickListener listener) {
            this(titleResID, description, TYPE_ITEM_NAVIGATOR, listener);
        }


        SettingItem(int titleResID, Callable<String> description, int type, View.OnClickListener listener) {
            this.title = -1 == titleResID ? "" : UIUtils.getString(titleResID);
            this.description = description;
            this.type = -1 == titleResID ? TYPE_SEPARATOR: type;
            this.listener = listener;
        }
    }

    private ActivitySettingBinding mBinding;
    private List<SettingItem> mSettingsItems;

    private final Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initViews(mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting));
    }

    private void initData() {
        mSettingsItems = new ArrayList<>();
        mSettingsItems.add(new SettingItem());
        mSettingsItems.add(new SettingItem(R.string.feedback, v -> shareMethod(this, SHARE_MEDIA.SINA)));
        mSettingsItems.add(new SettingItem(R.string.about_us, v -> {
            Bundle bundle = new Bundle();
            bundle.putString(WebViewActivity.EXTRA_TITLE, "关于我们");
            bundle.putString(WebViewActivity.EXTRA_URL, Constants.API.ABOUT_US);
            navigator.to(WebViewActivity.class, bundle);
        }));
        mSettingsItems.add(new SettingItem(R.string.version_update, () -> BuildConfig.VERSION_NAME, v -> checkVersionAndUpdate()));
        mSettingsItems.add(new SettingItem());
        //mSettingsItems.add(new SettingItem(R.string.give_mark));
        mSettingsItems.add(new SettingItem(R.string.clear_cache, () -> getCacheBytes(-1.0f), v -> clearCache()));
        mSettingsItems.add(new SettingItem());
    }

    private void initViews(ActivitySettingBinding binding) {
        setToolbar(binding.settingTb);
        setRecycleView(binding.settingRcv);
    }

    @Override
    public String getPageName() {
        return UIUtils.getString(R.string.page_act_setting);
    }

    private Toolbar setToolbar(Toolbar toolbar) {
        setUpToolbar(toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        return toolbar;
    }

    private RecyclerView setRecycleView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new NoScrollRecycleViewLayoutManager(this));
        recyclerView.setAdapter(new SettingAdapter());
        recyclerView.addItemDecoration(new SettingDecoration(DIVIDER_COLOR, DIVIDER_SIZE));

        return recyclerView;
    }

    private void shareMethod(Activity mActivity, final SHARE_MEDIA share_MEDIA){
        ShareAction shareAction = new ShareAction(mActivity);
        shareAction.setPlatform(share_MEDIA)
                .withText(UIUtils.getString(R.string.please_input_feedback))
                .setCallback(new UMShareListener() {

                    @Override
                    public void onResult(SHARE_MEDIA arg0) {
                        if(share_MEDIA != SHARE_MEDIA.SMS){
                            UIUtils.showToastSafe(R.string.feedback_success);
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA arg0, Throwable arg1) {
                        UIUtils.showToastSafe(R.string.feedback_fail);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        UIUtils.showToastSafe(R.string.feedback_cancel);
                    }
                });
        shareAction.share();
    }

    private void checkVersionAndUpdate() {
        final String url = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_url),
                "");
        final String versionOnServer = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_version),
                "");

        if(StringUtils.isNotNullString(versionOnServer)) {
            if(!versionOnServer.equalsIgnoreCase(BuildConfig.VERSION_NAME) && StringUtils.isNotNullString(url)) {
                RequestAPI.getInstance().downLoad(this,
                        file -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        },
                        url, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/zejia.apk");
            } else {
                UIUtils.showToastSafe(R.string.already_latest);
            }
        }
    }

    private String getCacheBytes(float specified) {
        if(0 > specified) {
            int length = getFileLength(getCacheDir());
            specified = length / 1024;
            specified /= 1024;
        }

        return String.format(getString(R.string.current_cache), specified);
    }

    private void clearCache() {
        deleteFile(getCacheDir());
        mBinding.settingRcv.getAdapter().notifyDataSetChanged();
    }

    private int getFileLength(File file) {
        int length = 0;
        for (File f : file.listFiles()) {
            if(f.isFile()) {
                Timber.d("%s", f.getAbsolutePath());
                length += f.length();
            } else {
                length += getFileLength(f);
            }
        }
        return length;
    }

    private void deleteFile(File file) {
        for (File f : file.listFiles()) {
            if(f.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            } else {
                deleteFile(f);
            }
        }
    }

    /**
     * 退出登录请求
     */
    public void prepareRemoteLogoff(View v) {
        String sessionId = PrefUtil.getString(this,
                UIUtils.getString(R.string.prefs_sessionId), "");
        if (StringUtils.isNullString(sessionId)) {
            return;
        }
        JSONObject data = JSONBuilder.newBuilder()
                .putAlways("sessionId", sessionId)
                .build();
        DataManager.getInstance().getZejiaService().doRequest(this, Constants.API.LOG_OFF, data,
                jsonObject -> {

                    int code = jsonObject.optInt("code");
                    String message = jsonObject.optString("msg");

                    if (code == 200) {
                        CountControl.getInstance().loginOut(false);
                        removePref();
                        navigator.to(ChooseLoginActivity.class);
                        MyActivityManager.getInstance().finishAllActivity();
                        finish();
                    } else {
                        UIUtils.showLongToastSafe(message);
                    }
                });
    }


    /**
     * 清除数据
     */
    private void removePref() {
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_is_login));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_is_setting));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_sessionId));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_nickname));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_photoUrl));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_mobile));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_weixin));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_qq));
        PrefUtil.remove(this, UIUtils.getString(R.string.prefs_weibo));
    }

    private final class SettingItemView extends RecyclerItemView {

        private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

        public SettingItemView(ViewGroup parent) {
            super(parent);
        }

        @Override
        protected View inflateView() {
            mBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(mParent.getContext()), R.layout.item_setting_navigator, mParent, false);
            return mBinding.getRoot();
        }

        @Override
        public ViewHolder getViewHolder() {
            return new ViewHolder() {
                @Override
                public void onBindViewHolder(int position) {
                    final ItemSettingNavigatorBinding binding
                            = (ItemSettingNavigatorBinding) mBinding;
                    final SettingItem holder = mSettingsItems.get(position);

                    binding.setVariable(BR.title, holder.title);
                    if(null != holder.description) {
                        final Future<String> future = mExecutor.submit(holder.description);
                        mExecutor.submit(() -> {
                            String description = "";
                            try {
                                description = future.get();
                            } catch (ExecutionException | InterruptedException e) {
                                description = "";
                            }
                            final String finalDescription = description;
                            mHandler.post(() -> binding.setVariable(BR.description, finalDescription));
                        });
                    }

                    itemView.setOnClickListener(holder.listener);
                }
            };
        }
    }

    private final class SettingAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerItemView itemView;

            if(TYPE_SEPARATOR == viewType) {
                itemView = new Divider(parent, SPACE_SIZE, UIUtils.getColor(R.color.gray_F3F3FF));
            } else {
                itemView = new SettingItemView(parent);
            }

            return itemView.getViewHolder();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBindViewHolder(position);
        }

        @Override
        public int getItemCount() {
            return mSettingsItems.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mSettingsItems.get(position).type;
        }
    }

    private final class SettingDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private int mDividerSize;

        SettingDecoration(int color, int dividerSize) {
            mDivider = new ColorDrawable(color);
            mDividerSize = dividerSize;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            for(int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                int top = v.getTop() - mDividerSize;

                mDivider.setBounds(0, top, parent.getWidth(), v.getTop());
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, mDividerSize, 0, 0);
        }
    }
}
