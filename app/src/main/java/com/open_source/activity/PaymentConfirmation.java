package com.open_source.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.open_source.Listener.Onpaymentrecieve;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.CommanApiCall;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.Payment;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentConfirmation extends AppCompatActivity implements View.OnClickListener, Onpaymentrecieve {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = PaymentConfirmation.class.getSimpleName();
    Toolbar toolbar;
    TextView toolbar_title, clickdetail, level_bidprice, txtbitprice, txt_calender;
    Context context;
    ProgressHUD progressHUD;
    Button btn_pay;
    String property_id, property_type, currdate, cardExpiry;
    ImageView btn_calender;
    String remaing_amount, intial_amount, method_type, closing_date;
    ImageView img_bid, cross;
    Boolean status = false;
    CardMultilineWidget cardMultilineWidget;
    LinearLayout lay_credit;
    Dialog dialog;
    Card cardToSave;
    private int mYear, mMonth, mDay;
    private Button btn_paypal, btn_credit, pay_paynow;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);
        context = PaymentConfirmation.this;
        init();
    }

    private void init() {
        clickdetail = findViewById(R.id.click_detail);
        /*level_bidprice = findViewById(R.id.level_bidprice);
        txtbitprice = findViewById(R.id.txtbitprice);*/
        btn_calender = findViewById(R.id.btn_calender);
        txt_calender = findViewById(R.id.txt_cal);
        img_bid = findViewById(R.id.img_bid);
        clickdetail.setOnClickListener(this);
        btn_pay = findViewById(R.id.pay);
        btn_pay.setOnClickListener(this);
        btn_calender.setOnClickListener(this);
        property_id = getIntent().getExtras().getString(Constants.PROPERTY_ID, "");
        property_type = getIntent().getExtras().getString(Constants.TYPE, "");
        if (!property_id.isEmpty() && !property_type.isEmpty()) {
            if (property_type.equalsIgnoreCase("auction")) {
                img_bid.setVisibility(View.VISIBLE);
            }
            FunGetData();
        } else {
            btn_pay.setVisibility(View.GONE);
            Utility.ShowToastMessage(context, getString(R.string.something_wrong));
        }

    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().PAYMENT_CONFIRMATION(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    property_id, property_type).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            // Utility.ShowToastMessage(context, response.body().getMessage());
                            remaing_amount = userData.getRemaining_amount();
                            intial_amount = userData.getInitial_amount();
                            if (userData.getActual_price() == 0) {
                                ((TextView) findViewById(R.id.txtinitailamount)).setText("$" + String.valueOf(userData.getBid_amount()));
                            } else {
                                ((TextView) findViewById(R.id.txtinitailamount)).setText("$" + String.valueOf(userData.getActual_price()));
                            }
                            ((TextView) findViewById(R.id.level_pro_name)).setText(userData.getProperty_type());
                            ((TextView) findViewById(R.id.txtprocrssfee)).setText("$" + String.valueOf(userData.getProcessing_fee()));
                            ((TextView) findViewById(R.id.txttitlefee)).setText("$" + String.valueOf(userData.getTitle_fee()));
                            ((TextView) findViewById(R.id.txttotalamount)).setText("$" + String.valueOf(userData.getInitial_amount()));
                            ((TextView) findViewById(R.id.txt_remaining_amount)).setText("$" + String.valueOf(userData.getRemaining_amount()));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.click_detail:
                if (!property_id.isEmpty()) {
                    startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID, "")));
                } else {
                    Utility.ShowToastMessage(context, getString(R.string.something_wrong));
                }
                break;
            case R.id.btn_calender:
                PickDate();
                break;
            case R.id.pay:
                if (txt_calender.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.closing_msg));
                } else {
                    FunPayStatus();

                }
                break;

            case R.id.btn_paypal:
                dialog.dismiss();
                method_type = Payment.PAYPAL;
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(intial_amount), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                break;


            case R.id.cross:
                dialog.dismiss();
                break;


            case R.id.btn_credit:
                dialog.dismiss();
                startActivity(new Intent(context,StripeWeb.class));
                //lay_credit.setVisibility(View.VISIBLE);
                break;


            case R.id.paynow:
                if (lay_credit.getVisibility() == View.VISIBLE) {
                    cardToSave = cardMultilineWidget.getCard();
                    if (cardToSave == null) {
                        Utility.ShowToastMessage(context, getString(R.string.invalidata));
                    } else {
                        if (cardToSave.getNumber().equals("")) {
                            Utility.ShowToastMessage(context, getString(R.string.card_number));
                        } else if (cardToSave.getExpMonth() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardexpirymonth));
                        } else if (cardToSave.getExpYear() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardyear));
                        } else if (cardToSave.getCVC().isEmpty()) {
                            Utility.ShowToastMessage(context, getString(R.string.cradcvv));
                        } else {
                            cardExpiry = cardToSave.getExpMonth() + "/" + cardToSave.getExpYear();
                            CommanApiCall.GetStripToken(context, cardMultilineWidget.getCard().getNumber().toString(),
                                    cardMultilineWidget.getCard().getExpMonth(), cardMultilineWidget.getCard().getExpYear(),
                                    cardMultilineWidget.getCard().getCVC().toString(), intial_amount, "buy");
                        }
                    }
                }
                break;

        }
    }

    private void FunPayStatus() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().PAYMENT_CHECK(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id, "sold").enqueue(new Callback<RetrofitUserData>() {
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
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void FunPay() {
        closing_date = txt_calender.getText().toString();
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
        lay_credit = dialog.findViewById(R.id.lay_credit);
        btn_paypal = dialog.findViewById(R.id.btn_paypal);
        btn_credit = dialog.findViewById(R.id.btn_credit);
        pay_paynow = dialog.findViewById(R.id.paynow);
        cross = dialog.findViewById(R.id.cross);
        cardMultilineWidget = (CardMultilineWidget) dialog.findViewById(R.id.card_multiline_widget);
        btn_paypal.setOnClickListener(this);
        btn_credit.setOnClickListener(this);
        pay_paynow.setOnClickListener(this);
        cross.setOnClickListener(this);
        dialog.show();
    }

    private void PickDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        currdate = fun_dateFormat(mDay + "-" + (mMonth + 1) + "-" + mYear);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        txt_calender.setText(formatted_date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis() + (1000 * 60 * 60 * 24 * 20));

    }

    private String fun_dateFormat(String date) {
        String formatted_date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = dateFormat.parse(date);
            formatted_date = dateFormat.format(d);
            if (formatted_date.isEmpty()) {
                formatted_date = date;
            }
            //System.out.println("=====date"+d);
            //System.out.println("======Formated"+);
        } catch (Exception e) {
            formatted_date = date;
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep" + e);
        }
        return formatted_date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
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
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "user"));
                finish();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "user"));
                finish();
            }
        }
    }

    private void Fun_Server_Payment(String transaction_id) {
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
    }

    @Override
    public void onrecieve(String token) {
        if (dialog != null && dialog.isShowing() && context != null) {
            dialog.dismiss();
            method_type = Payment.STRIPE;
            // Fun_Server_Payment(token);

        }
    }
}
