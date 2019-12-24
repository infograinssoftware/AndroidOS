package com.open_source.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentPayment extends BaseActivity implements View.OnClickListener {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = RentPayment.class.getSimpleName();
    private Context context;
    private TextView ctv_about, toolbar_title, click_detail;
    private ProgressHUD progressHUD;
    private Toolbar toolbar;
    private ImageView img_bid,cross;
    private Button btn_pay;
    private  int amount = 0;
    private Dialog dialog;
    private Button btn_paypal, btn_credit;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_payment);
        context = this;
        init();

    }

    public void init() {
        img_bid = findViewById(R.id.img_bid);
        Glide.with(context).load(getIntent().getExtras().getString(Constants.PROPERTY_IMG, "")).into(img_bid);
        click_detail = findViewById(R.id.click_detail);
        ((TextView) findViewById(R.id.level_pro_name)).setText(getIntent().getExtras().getString(Constants.TYPE));
        ((TextView) findViewById(R.id.txtinitailamount)).setText("$" + getIntent().getExtras().getString(Constants.RENT_AMOUNT));
//        ((TextView) findViewById(R.id.txtinitailamount)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + getIntent().getExtras().getString(Constants.RENT_AMOUNT));
        amount = Integer.valueOf(getIntent().getExtras().getString(Constants.RENT_AMOUNT));
        amount = Integer.valueOf(amount) + 5;
        ((TextView) findViewById(R.id.txttotalamount)).setText("$" + String.valueOf(amount));
//        ((TextView) findViewById(R.id.txttotalamount)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + String.valueOf(amount));
        btn_pay = findViewById(R.id.pay);
        btn_pay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pay:
                FunPayStatus();
                break;

            case R.id.btn_paypal:
                close_dialog();
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                break;

            case R.id.cross:
                close_dialog();
                break;

            case R.id.btn_credit:
                close_dialog();
                startActivity(new Intent(context,StripeWeb.class));
                //lay_credit.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void close_dialog() {
        if (dialog!=null && dialog.isShowing())
        dialog.dismiss();
    }

    private void FunPayStatus() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().PAYMENT_CHECK(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.PROPERTY_ID, ""), "rent").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getMessage().equalsIgnoreCase("Available")) {
                                FunPay();
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                            //
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }  else {
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
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void FunPay() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_payment);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        final LinearLayout lay_credit = dialog.findViewById(R.id.lay_credit);
        btn_paypal = dialog.findViewById(R.id.btn_paypal);
        btn_credit = dialog.findViewById(R.id.btn_credit);
        cross = dialog.findViewById(R.id.cross);
        btn_paypal.setOnClickListener(this);
        btn_credit.setOnClickListener(this);
        cross.setOnClickListener(this);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                com.paypal.android.sdk.payments.PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("=====transaction_id", paymentDetails);
                        JSONObject jsonObject = new JSONObject(paymentDetails);
                        String transaction_id = jsonObject.getJSONObject("response").getString("id");
                        Log.e(TAG, "====transaction_id: " + transaction_id);
                        Fun_Server_Payment(transaction_id);

                    } catch (JSONException e) {
                        Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                    }
                }
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE,"user"));
                finish();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE,"user"));
                finish();
            }
        }
    }

    private void Fun_Server_Payment(String transaction_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().RentPayment(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID, ""),
                    transaction_id,
                    "paypal",
                    amount + "").enqueue(new Callback<RetrofitUserData>() {

                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        Log.e(TAG, "onResponse: " + response.toString());
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "success").putExtra(Constants.TYPE,"user"));
                            finish();

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
