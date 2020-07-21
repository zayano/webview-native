package com.example.webviewodoo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

class MyWebClient extends WebViewClient {
    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences shared;
    private SharedPreferences.Editor editor;
    private String cookies;
    private CookieManager cookieManager;
    private Context context;

    public MyWebClient(
            ProgressBar bar,
            SharedPreferences shared,
            SharedPreferences.Editor editor,
            CookieManager cookieManager,
            SwipeRefreshLayout swipeRefreshLayout,
            Context context,
            WebView webView
    ) {
        this.progressBar = bar;
        this.shared = shared;
        this.editor = editor;
        this.cookieManager = cookieManager;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.context = context;
        this.webView = webView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        cookieManager.setAcceptCookie(true);
        cookies = cookieManager.getCookie(url);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm);
        Log.d("onReceivedHttpAuthRequest: ", "host : " + handler);
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        Log.d("onLoadResource", "cookie : " + cookieManager.getCookie(url));
        Log.d("onLoadResource: ", "url : " + url);
        super.onLoadResource(view, url);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        super.onSafeBrowsingHit(view, request, threatType, callback);
        Log.d("onSafeBrowsingHit: ", "request :" + request);
        Log.d("onSafeBrowsingHit: ", "safeBrowsingResponse :" + callback);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Toast.makeText(context, "Your Internet Connection May not be active Or " + error.getDescription(), Toast.LENGTH_LONG).show();
    }


}
