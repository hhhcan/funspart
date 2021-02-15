package com.can.funspart.viewimpl.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.can.funspart.R;
import com.can.funspart.base.BaseActivity;
import com.can.funspart.utils.ThemeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by forezp on 16/9/25.
 */
public class WebviewActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.webview)
    WebView webview;
    private String url;
    public static String EXTRA_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        applyKitKatTranslucency();
        initData();
        initView();
    }

    private void initData(){
        Intent intent=getIntent();
        if(intent!=null){
            url=  intent.getStringExtra(EXTRA_URL);
        }
    }

    private void initView(){
        toolbar.setBackgroundColor(ThemeUtils.getThemeColor());
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backThActivity();
            }
        });
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);// 支持JS
        //支持插件
        //设置自适应屏幕，两者合用
//        settings.setUseWideViewPort(true); //将图片调整到适合webview的大小
//        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //settings.setBuiltInZoomControls(true);// 显示放大缩小按钮
        //settings.setUseWideViewPort(true);// 支持双击放大缩小
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            /**
             * 所有跳转的链接都在此方法中回调
             * 打开网页时不调用系统浏览器， 而是在本WebView中显示。
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

                toolbar.setTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
        webview.loadUrl(url);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy(){
        //特别注意，在android开发中一定要合理的利用资源
        // 使用webview 进行网页播放，需要主动在ondestroy()方法中执行,释放webview 内存资源。
        webview.loadUrl(null);
        webview.clearHistory();
        ((ViewGroup) webview.getParent()).removeView(webview);
        webview.destroy();
        webview = null;
        super.onDestroy();
    }
}
