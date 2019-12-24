package com.open_source.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;


public class PaymentWebView extends BaseActivity {
    private static final String TAG = PaymentWebView.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    TextView toolbar_title;
    WebView webview;
    ProgressDialog pDialog;
    Boolean status = false;
    String proprty_id, property_amount, contract_id = "1", agreement_id = "1", closing_date, remaining_amount, token, pay_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_webview);
        context = this;
        init();
    }

    private void init() {
/*        toolbar = findViewById(R.id.h_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.disclosure));*/
        proprty_id = getIntent().getExtras().getString(Constants.PROPERTY_ID, "");
        property_amount = getIntent().getExtras().getString(Constants.AMOUNT, "");
        closing_date = getIntent().getExtras().getString(Constants.CLOSING_DATE, "");
        remaining_amount = getIntent().getExtras().getString(Constants.REMAINING_AMOUNT, "");
        token = SharedPref.getSharedPreferences(context, Constants.TOKEN);
        webview = (WebView) findViewById(R.id.webview);
        webview.clearHistory();
        webview.clearCache(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        pDialog = new ProgressDialog(context);
        pDialog.setTitle(getString(R.string.payment));
        pDialog.setMessage(getString((R.string.loading)));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        http:
//192.168.1.137/osrealstate/api/paypal/pay_now/?property_id=65&property_amount=50&contract_id=79
        // &agreement_id=79&closing_date=02-08-2018&&remaining_amount=101&token=33c6d8beca1d3f1413358edb19cf7efb

        pay_url = "http://infograins.in/INFO01/osrealstate/api/paypal/pay_now/?property_id="
                + proprty_id
                + "&property_amount="
                + property_amount
                + "&contract_id="
                + contract_id
                + "&agreement_id="
                + agreement_id
                + "&closing_date="
                + closing_date
                + "&remaining_amount="
                + remaining_amount
                + "&token="
                + token;
        webview.setWebViewClient(new MyWebViewClient());
        // webview.setWebViewClient(new SadadPaymentActivity.MyWebViewClient());
        webview.loadUrl(pay_url);


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
            if (status == false) {
                pDialog.show();
            }

            Log.e(TAG, "onLoadResource: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished: " + url);
            pDialog.dismiss();
            status = true;
            if (url.contains("response_message=success")) {
                Utility.ShowToastMessage(context, getString(R.string.payment_Success));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "success"));
                finish();
            } else if (url.contains("checkout/done")) {
                Utility.ShowToastMessage(context, getString(R.string.payment_Success));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "success"));
                finish();

            } else if (url.contains("response_message=failed")) {
                Utility.ShowToastMessage(context, getString(R.string.payment_failed));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed"));
                finish();

            }

        }
    }

}

