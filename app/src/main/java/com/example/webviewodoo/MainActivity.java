package com.example.webviewodoo;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView web;
    ProgressBar bar;
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    SwipeRefreshLayout swipe;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

        web = findViewById(R.id.web_view);
        bar = findViewById(R.id.progressBar2);
        swipe = findViewById(R.id.swipe);
        shared = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = shared.edit();
        loadWeb();

        bar.setMax(100);
        bar.setProgress(1);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.reload();
            }
        });

        CookieSyncManager.createInstance(this).sync();
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadWeb(){
        WebSettings webSettings = web.getSettings();
        web.loadUrl("http://172.17.20.233:8069/");
        webSettings.setJavaScriptEnabled(true);
        WebViewDatabase.getInstance(getApplicationContext()).clearHttpAuthUsernamePassword();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.getCacheMode();

        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading...");
                bar.setProgress(progress);

                if (progress == 100)
                    setTitle(R.string.app_name);
            }
        });

        web.setWebViewClient(new WebClient(bar, swipe, shared, editor));

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