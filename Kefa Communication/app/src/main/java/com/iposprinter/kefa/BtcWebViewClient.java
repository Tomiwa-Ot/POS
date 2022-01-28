package com.iposprinter.kefa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BtcWebViewClient extends WebViewClient {

    private ProgressBar progressBar;
    private Context context;

    public BtcWebViewClient(ProgressBar progressBar, Context context){
        this.progressBar = progressBar;
        this.context = context;
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if(url.contains("finish_txn")) {
            Intent intent = new Intent();
            intent.putExtra("merchant_transaction_id", url.split("=")[1]);
            ((Activity) this.context).setResult(Activity.RESULT_OK, intent);
            ((Activity) this.context).finish();
        }
        progressBar.setVisibility(View.GONE);
    }
}
