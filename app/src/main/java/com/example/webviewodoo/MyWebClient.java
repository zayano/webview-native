package com.example.webviewodoo;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.content.Context.MODE_PRIVATE;

class WebClient extends WebViewClient{
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipe;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private String cookies;
    String session;
    String loadUrl;

    public WebClient(ProgressBar bar, SwipeRefreshLayout swipe, SharedPreferences shared, SharedPreferences.Editor editor) {
        this.progressBar = bar;
        this.swipe = swipe;
        this.shared = shared;
        this.editor = editor;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(5);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(100);
        swipe.setRefreshing(false);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookies);

    }

    @Override
    public void onLoadResource(WebView view, String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookies = cookieManager.getCookie(url);
        Log.d("ONloadResource","cookie is " + cookies);
        super.onLoadResource(view, url);
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        handler.proceed();
    }
}
