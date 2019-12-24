package com.open_source.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;


public class PdfViewActivity extends BaseActivity {
    private static final String TAG = PdfViewActivity.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    TextView toolbar_title;
    WebView webview;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        context = this;
        init();
        listener();
    }

    private void init() {
        toolbar = findViewById(R.id.h_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.disclosure));
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        pDialog = new ProgressDialog(context);
        pDialog.setTitle(R.string.documents);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        String pdf = getIntent().getExtras().getString(Constants.DISCLOSURE_DOC, "");
        Log.e(TAG, "init: " + pdf);
        Log.e(TAG, "init: " + pdf);
        if (Utility.isConnectingToInternet(context)) {
            if (getIntent().getExtras().getString(Constants.TYPE, "").equalsIgnoreCase("image")) {
                webview.loadUrl(pdf);
            } else {
                webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdf);
            }
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }


    }

    private void listener() {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e(TAG, "onPageStarted: "+url );
                pDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e(TAG, "onPageFinished: "+url );
                    pDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: " + "onrestart");
    }
}
