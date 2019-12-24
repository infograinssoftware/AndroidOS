package com.open_source.activity;

import android.content.Context;

import androidx.appcompat.widget.Toolbar;


/**
 * Created by AND-06 on 01-Nov-17.
 */

public class SadadPaymentActivity extends BaseActivity {

    public static final String TAG = SadadPaymentActivity.class.getSimpleName();
    String url_string = "https://sbcheckout.payfort.com/FortAPI/paymentPage";
    String SERVER_URL = "https://sbpaymentservices.payfort.com/FortAPI/paymentApi";
    int f_amount = 0;
    String hash_key_code = "", hash_hey = "", param = "", merchant_reference = "", success_response = "";
    Context appContext;
   // ResponseTask responseTask;
    Toolbar mToolbar;
    String sponsorPackage;
    //private WebView webview;

    /*@SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sadad_payment);
        appContext = this;
        init();
    }

    private void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((CustomTextView) mToolbar.findViewById(R.id.toolbar_title)).setText(getResources().getString(R.string.payment));
        ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
        webview = (WebView) findViewById(R.id.webview);
        webview.clearHistory();
        webview.clearCache(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setBuiltInZoomControls(true);
        merchant_reference = Utility.getRandomString(5);
        f_amount = Integer.parseInt(getIntent().getStringExtra(Constants.UserConstants.PRI_FROM));
        f_amount = f_amount * 100;
        hash_key_code = "TESTSHAINaccess_code=gEOcKcPzKPV8VQu5MYoBamount=" + f_amount + "command=PURCHASEcurrency=SARcustomer_email=nilesh@payfort.comlanguage=enmerchant_identifier=xzmAhHapmerchant_reference=" + merchant_reference + "order_description=sponsorPackagepayment_option=SADADTESTSHAIN";
        try {
            hash_hey = Utility.hash256(hash_key_code);
            Log.e(TAG, "hash_hey =====> " + hash_hey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        param = "command=PURCHASE&access_code=gEOcKcPzKPV8VQu5MYoB&merchant_identifier=xzmAhHap&merchant_reference=" + merchant_reference + "&amount=" + f_amount + "&currency=SAR&language=en&customer_email=nilesh@payfort.com&signature=" + hash_hey + "&order_description=sponsorPackage&payment_option=SADAD";
        webview.setWebViewClient(new MyWebViewClient());
        webview.postUrl(url_string, EncodingUtils.getBytes(param, "UTF-8"));
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DistrictSubscribePlan(String transactionid) {
        // sponsored_payment_for_city&user_id=&sponsored_package_id=1&transaction_id=32423&price=32&city_names=Jeddah
        String city_id = getIntent().getStringExtra(Constants.UserConstants.CITY_IDS);
        city_id = city_id.substring(1, city_id.length() - 1);
        city_id = city_id.replaceAll(", ", ",");

        String city_name = getIntent().getStringExtra(Constants.UserConstants.CITY_NAMES);
        city_name = city_name.substring(1, city_name.length() - 1);
        city_name = city_name.replaceAll(", ", ",");

        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put(Constants.Actions.ACTION, Constants.Actions.sponsored_payment_for_district);
        jsonObject.put(Constants.UserConstants.USER_ID, Utility.getSharedPreferences(appContext, Constants.UserConstants.UID));
        jsonObject.put(Constants.UserConstants.SPONSORED_PACKAGE_ID, city_id);
        jsonObject.put(Constants.UserConstants.TRANSACTION_ID, transactionid);
        jsonObject.put(Constants.UserConstants.PRI_FROM, getIntent().getStringExtra(Constants.UserConstants.PRI_FROM));
        jsonObject.put(Constants.UserConstants.DISTRICT_NAMES, city_name);
        jsonObject.put(Constants.UserConstants.Sponsored_duration, getIntent().getStringExtra(Constants.UserConstants.Sponsored_duration));
        jsonObject.put(Constants.START_DATE, getIntent().getStringExtra(Constants.START_DATE));
        jsonObject.put(Constants.END_DATE, getIntent().getStringExtra(Constants.END_DATE));
        Utility.ShowLoading(appContext, appContext.getResources().getString(R.string.loading_msg));
        responseTask = new ResponseTask(appContext, jsonObject);
        responseTask.setListener(new ResponseListener() {
            @Override
            public void onGetPickSuccess(String result) {
                Utility.HideDialog();
                if (result == null) {
                    Utility.ShowToastMessage(appContext, appContext.getResources().getString(R.string.server_not_responding));
                } else {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.getString(Constants.ResponseKeys.RESULT).equals("1")) {
                            Utility.ShowToastMessage(appContext, json.getString(Constants.ResponseKeys.MSG));
                            startActivity(new Intent(appContext, HomeActivity.class));
                            finish();
                        } else {
                            if (!json.getString(Constants.ResponseKeys.MSG).equalsIgnoreCase(appContext.getResources().getString(R.string.no_record))) {
                                Utility.ShowToastMessage(appContext, json.getString(Constants.ResponseKeys.MSG));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        responseTask.execute();
    }

    private void CitySubscribePlan(String transactionid) {
        // sponsored_payment_for_city&user_id=&sponsored_package_id=1&transaction_id=32423&price=32&city_names=Jeddah
        String city_id = getIntent().getStringExtra(Constants.UserConstants.CITY_IDS);
        city_id = city_id.substring(1, city_id.length() - 1);
        city_id = city_id.replaceAll(", ", ",");

        String city_name = getIntent().getStringExtra(Constants.UserConstants.CITY_NAMES);
        city_name = city_name.substring(1, city_name.length() - 1);
        city_name = city_name.replaceAll(", ", ",");

        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put(Constants.Actions.ACTION, Constants.Actions.sponsored_payment_for_city);
        jsonObject.put(Constants.UserConstants.USER_ID, Utility.getSharedPreferences(appContext, Constants.UserConstants.UID));
        jsonObject.put(Constants.UserConstants.SPONSORED_PACKAGE_ID, city_id);
        jsonObject.put(Constants.UserConstants.TRANSACTION_ID, transactionid);
        jsonObject.put(Constants.UserConstants.PRI_FROM, getIntent().getStringExtra(Constants.UserConstants.PRI_FROM));
        jsonObject.put(Constants.UserConstants.CITY_NAMES, city_name);
        jsonObject.put(Constants.UserConstants.Sponsored_duration, getIntent().getStringExtra(Constants.UserConstants.Sponsored_duration));
        jsonObject.put(Constants.START_DATE, getIntent().getStringExtra(Constants.START_DATE));
        jsonObject.put(Constants.END_DATE, getIntent().getStringExtra(Constants.END_DATE));
        Utility.ShowLoading(appContext, appContext.getResources().getString(R.string.loading_msg));
        responseTask = new ResponseTask(appContext, jsonObject);
        responseTask.setListener(new ResponseListener() {
            @Override
            public void onGetPickSuccess(String result) {
                Utility.HideDialog();
                if (result == null) {
                    Utility.ShowToastMessage(appContext, appContext.getResources().getString(R.string.server_not_responding));
                } else {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.getString(Constants.ResponseKeys.RESULT).equals("1")) {
                            Utility.ShowToastMessage(appContext, json.getString(Constants.ResponseKeys.MSG));
                            onBackPressed();
                        } else {
                            if (!json.getString(Constants.ResponseKeys.MSG).equalsIgnoreCase(appContext.getResources().getString(R.string.no_record))) {
                                Utility.ShowToastMessage(appContext, json.getString(Constants.ResponseKeys.MSG));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        responseTask.execute();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ((ProgressBar) findViewById(R.id.progress)).setVisibility(View.GONE);
            if (url.contains("https://smartbooking.net/response.php?")) {
                success_response = url.replace("https://smartbooking.net/response.php?", "");
                Log.e(TAG, "success_response ======> " + success_response);

                if (success_response.contains("response_message=Success")) {
                    try {
                        List<NameValuePair> paramsList = URLEncodedUtils.parse(new URI(url), "utf-8");
                        for (NameValuePair parameter : paramsList) {
                            if (parameter.getName().equals("fort_id")) {
                                Log.e(TAG, "fort_id=====> " + parameter.getValue());
                                if (getIntent().getStringExtra(Constants.UserConstants.IS_ADD).equals("0")) {
                                    CitySubscribePlan(parameter.getValue());
                                } else {
                                    DistrictSubscribePlan(parameter.getValue());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utility.ShowToastMessage(appContext, getResources().getString(R.string.payment_failed));
                }
            }
        }
    }*/
}
