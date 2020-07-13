package com.example.webviewodoo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView web;
    ProgressBar bar;

    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        web = findViewById(R.id.web_view);
        bar = findViewById(R.id.progressBar2);
        swipe = findViewById(R.id.swipe);
        loadWeb();

        bar.setMax(100);
        bar.setProgress(1);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.reload();
            }
        });

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadWeb(){
        WebSettings webSettings = web.getSettings();
        web.loadUrl("https://stars.mncland.com/");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webSettings.setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");

        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                bar.setProgress(progress);
            }
        });

        web.setWebViewClient(new WebClient(bar, swipe));

        swipe.setRefreshing(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK) && web.canGoBack()){
            web.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}