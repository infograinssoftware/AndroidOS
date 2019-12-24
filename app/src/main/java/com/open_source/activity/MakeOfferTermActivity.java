package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.open_source.Interface.OnMyDialogResult;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.Payment;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeOfferTermActivity extends BaseActivity implements View.OnClickListener, OnMyDialogResult {
    private static final String TAG = MakeOfferTermActivity.class.getSimpleName();
    CheckBox check_term;
    Context context;
    ProgressHUD progressHUD;
    String method_type, property_id = "", str_amount = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_offer_term);
        context = MakeOfferTermActivity.this;
        init_view();
    }

    private void init_view() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.make_offer);
        check_term = findViewById(R.id.check_term);
        ((Button) findViewById(R.id.btn_continue)).setOnClickListener(this);
        property_id = getIntent().getStringExtra(Constants.PROPERTY_ID);
        str_amount = getIntent().getStringExtra(Constants.AMOUNT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                if (!check_term.isChecked()) {
                    Utility.ShowToastMessage(context, R.string.msg_term);
                } else {
                    Utility.FunPay(MakeOfferTermActivity.this, Constants.OFFER_AMOUNT);
                }
                break;
        }
    }

    @Override
    public void finish(String result) {
        switch (result) {
            case Constants.CREDIT:
                Intent i = new Intent(this, com.open_source.activity.PaymentActivity.class);
                startActivityForResult(i, Payment.STRIPE_REQUEST_CODE);
                break;

            case Constants.ACH:
                Intent i1 = new Intent(this, StripeWeb.class);
                startActivityForResult(i1, Payment.PLAID_REQUEST_CODE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Payment.PAYPAL_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                com.paypal.android.sdk.payments.PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetails);
                        String transaction_id = jsonObject.getJSONObject("response").getString("id");
                        method_type = Payment.PAYPAL;
                        FunPlaid(method_type, transaction_id, "", "", "");
                        //Fun_Server_Payment(transaction_id);
                    } catch (JSONException e) {
                        Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                    }
                }
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                //Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "user"));
                finish();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "user"));
                finish();
            }
        } else if (requestCode == Payment.PLAID_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log.e("======: ", data.getStringExtra(Constants.PUBLIC_TOKEN));
                Log.e("========: ", data.getStringExtra(Constants.ACCOUNT_ID));
                method_type = Payment.Plaid;
                if (!data.getStringExtra(Constants.PUBLIC_TOKEN).isEmpty() && !data.getStringExtra(Constants.ACCOUNT_ID).isEmpty())
                    FunPlaid(method_type, "", data.getStringExtra(Constants.ACCOUNT_ID), data.getStringExtra(Constants.PUBLIC_TOKEN), "");

            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Log.e("======: ", "Cancel");

            }
        } else if (requestCode == Payment.STRIPE_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                Log.e("======: ", data.getStringExtra(Constants.PUBLIC_TOKEN));
                method_type = Payment.STRIPE;
                if (!data.getStringExtra(Constants.PUBLIC_TOKEN).isEmpty()) {
                    FunPlaid(method_type, "", "", "", data.getStringExtra(Constants.PUBLIC_TOKEN));
                }
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Utility.ShowToastMessage(context, data.getStringExtra(Constants.MESSAGE));
                Log.e("======: ", "Cancel");

            }
        }
    }

    /*public void callMakeOfferAPI() {
        //progressHUD = ProgressHUD.show(context, true, false, null);
        //Log.e(TAG, "callMakeOfferAPI: " + property_ids);
        RetrofitClient.getAPIService().getMakeOfferAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_id,
                str_amount).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }

                try {
                    if (response.body().getStatus() == 200) {
                        Utility.ShowToastMessage(context, response.body().getMessage());
                       *//* cet_price.setText("");
                        offer_status = "1";
                        btn_submit.setBackgroundResource(R.drawable.round_button_fad);*//*

                    } else {
                        Utility.ShowToastMessage(context, response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }*/

    private void FunPlaid(String method_type, String paypal_transection_id, String plaid_ACCOUNT_ID, String plaid_PUBLIC_TOKEN, String stripe_token) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().PlaidRequest(SharedPref.getSharedPreferences(context, Constants.TOKEN),property_id,str_amount,method_type,
                    paypal_transection_id,plaid_ACCOUNT_ID,plaid_PUBLIC_TOKEN,stripe_token).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        Log.e(TAG, "onResponse: " + response.toString());
                        if (response.body().getStatus() == 200) {
                            context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            // callMakeOfferAPI();

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    //Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }
}

    /*private void Fun_Server_Payment(String transaction_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Buy(SharedPref.getSharedPreferences(context, Constants.TOKEN), transaction_id,
                    property_id, method_type, intial_amount, remaing_amount, closing_date, "1", "1").enqueue(new Callback<RetrofitUserData>() {

                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        Log.e(TAG, "onResponse: " + response.toString());
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "success").putExtra(Constants.TYPE, "sell"));
                            finish();

                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    //Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }*/

