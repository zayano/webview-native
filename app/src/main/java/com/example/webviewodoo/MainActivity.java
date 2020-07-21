package com.example.webviewodoo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    WebView web;
    ProgressBar bar;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences shared;
    SharedPreferences.Editor editor;
    CookieManager cookieManager;
    Context context = this;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;
    boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        web = findViewById(R.id.web_view);
        bar = findViewById(R.id.progressBar2);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        shared = getSharedPreferences("prefs", MODE_PRIVATE);
        editor = shared.edit();

        CookieSyncManager.createInstance(context);
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        bar.setMax(100);

        loadWeb();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                web.reload();
            }
        });
    }


    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility", "AddJavascriptInterface"})
    public void loadWeb() {
        WebSettings webSettings = web.getSettings();
        web.loadUrl("http://172.17.20.233:8069/");
        web.addJavascriptInterface(new WebAppInterface(context), "Android");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.getCacheMode();

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            web.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webSettings.setSavePassword(true);
        webSettings.setSaveFormData(true);

        web.requestFocus(View.FOCUS_DOWN);
        web.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!view.hasFocus()) {
                            view.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                bar.setProgress(progress);
            }
        });

        web.setWebViewClient(new MyWebClient(bar, shared, editor, cookieManager, swipeRefreshLayout, context, web));
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        web.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        web.destroy();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeRefreshLayout.getViewTreeObserver().addOnScrollChangedListener(mOnScrollChangedListener =
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (web.getScrollY() == 0)
                            swipeRefreshLayout.setEnabled(true);
                        else
                            swipeRefreshLayout.setEnabled(false);

                    }
                });
    }

    @Override
    public void onStop() {
        swipeRefreshLayout.getViewTreeObserver().removeOnScrollChangedListener(mOnScrollChangedListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
        } else {
            Toast toast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            doubleBackToExitPressedOnce = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


}