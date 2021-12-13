package com.iposprinter.kefa;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BtcWebViewClient extends WebViewClient {

    private ProgressBar progressBar;

    public BtcWebViewClient(ProgressBar progressBar){
        this.progressBar = progressBar;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        progressBar.setVisibility(View.GONE);
    }
}
