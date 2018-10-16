package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.semicolon.tadlaly.R;

public class WebActivity extends AppCompatActivity {

    private WebView webView;
    String url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.webView);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.setWebViewClient(new WebViewClient());

        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            url = intent.getStringExtra("url");
            webView.loadUrl(url);

        }
    }
}
