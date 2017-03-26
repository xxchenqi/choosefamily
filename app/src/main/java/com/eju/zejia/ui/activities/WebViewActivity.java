package com.eju.zejia.ui.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;

import com.eju.zejia.R;
import com.eju.zejia.databinding.ActivityWebviewBinding;
import com.eju.zejia.utils.UIUtils;

public class WebViewActivity extends BaseActivity {

    public static final String EXTRA_URL = "url";
    public static final String EXTRA_TITLE = "title";
    private String url;
    private String title;

    @Override
    @JavascriptInterface
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWebviewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        setUpToolbar(binding.toolbar);
        actionBar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.bg_btn_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        binding.title.setText(title);

        setUpWebView(binding);
    }

    @Override
    protected void processArgument(Bundle bundle) {
        url = bundle.getString(EXTRA_URL);
        title = bundle.getString(EXTRA_TITLE);
    }

    private void setUpWebView(ActivityWebviewBinding binding) {
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new WebViewClient());
        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.loadUrl(url);
    }

    @Override
    public String getPageName() {
        if("用户协议".equals(title)){
            return UIUtils.getString(R.string.page_act_web_agreement);
        }else if("房源详情".equals(title)){
            return UIUtils.getString(R.string.page_act_web_housing_details);
        }else if("关于我们".equals(title)) {
            return UIUtils.getString(R.string.page_act_about);
        }
        return null;
    }
}
