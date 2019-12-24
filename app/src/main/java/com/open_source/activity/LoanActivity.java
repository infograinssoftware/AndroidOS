package com.open_source.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.open_source.R;


public class LoanActivity extends BaseActivity {
    private static final String TAG = LoanActivity.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    TextView toolbar_title;
    WebView webview;
    ProgressDialog pDialog;
    String url="https://apply.loansimple.com/#/create-account?referrerId=ssanders%40loansimple.com";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_webview);
        context = this;
        init();
    }

    private void init() {
        webview = (WebView) findViewById(R.id.webview);
        webview.clearHistory();
        webview.clearCache(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        webview.setWebViewClient(new MyWebViewClient());
        // webview.setWebViewClient(new SadadPaymentActivity.MyWebViewClient());
        webview.loadUrl(url);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview.canGoBack()) {
                        webview.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            pDialog.dismiss();
            Log.e(TAG, "onReceivedError: " + failingUrl);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            if (pDialog.isShowing())
            pDialog.dismiss();
        }
    }
}
