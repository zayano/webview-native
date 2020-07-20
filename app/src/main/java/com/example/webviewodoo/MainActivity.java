package com.example.webviewodoo;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
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
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    SwipeRefreshLayout swipe;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt( Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
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
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);
        webSettings.setNeedInitialFocus(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.getCacheMode();
        web.requestFocus(View.FOCUS_DOWN);
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!view.hasFocus()){
                            view.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setTitle("Loading...");
                bar.setProgress(progress);

                if (progress == 100)
                    setTitle(R.string.app_name);
            }
        });

        web.setWebViewClient(new MyWebClient(bar, swipe, shared, editor));
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

    @Override
    protected void onStart() {
        super.onStart();
        swipe.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (web.getScrollY() == 0)
                            swipe.setEnabled(true);
                        else
                            swipe.setEnabled(false);

                    }
                });
    }

    @Override
    protected void onStop() {
        swipe.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }
}