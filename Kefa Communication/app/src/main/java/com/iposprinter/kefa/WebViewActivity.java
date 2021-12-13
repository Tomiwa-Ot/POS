package com.iposprinter.kefa;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class WebViewActivity extends AppCompatActivity {

    private static final String BTC_ENDPOINT = "https://tomiwa.com.ng/kefa/ramp";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        setTitle("");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.page_progress_bar);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new BtcWebViewClient(progressBar));
        webView.loadUrl(BTC_ENDPOINT);
    }
}