package com.open_source.ServiceProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.ConfirmPaymentMessage;
import com.open_source.activity.StripeWeb;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.MilesTone;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
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
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProjectDescription extends AppCompatActivity implements View.OnClickListener {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = ProjectDescription.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    CircleImageView user_img;
    LinearLayout layout_milestone;
    EditText edit_review;
    RatingBar rattingbar;
    Dialog dialog;
    String user_id = "", milestone_id = "", amount = "";
    UserData userData;
    private Context context;
    private TextView toolbar_title, btn_submit, btn_cancle, user_number, ctv_close;
    private ProgressHUD progressHUD;
    private Toolbar toolbar;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_running_project);
        context = this;
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.project_detail);
        ctv_close = findViewById(R.id.ctv_login);
        user_img = findViewById(R.id.user_image);
        user_number = findViewById(R.id.user_number);
        user_number.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_review)).setOnClickListener(this);
        ((Button) findViewById(R.id.btn_payment)).setOnClickListener(this);
        layout_milestone = findViewById(R.id.lay_milestone);
        ((Button) findViewById(R.id.btn_message)).setOnClickListener(this);
        if (getIntent().getExtras().getString(Constants.TYPE).equals("user") && getIntent().getExtras().getString(Constants.KEY, "").equalsIgnoreCase("2")) {
            ((Button) findViewById(R.id.btn_payment)).setVisibility(View.GONE);
            ctv_close.setVisibility(View.VISIBLE);
            ctv_close.setText(R.string.close_project);
            ctv_close.setOnClickListener(this);
            ctv_close.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

        } else if (getIntent().getExtras().getString(Constants.TYPE).equals("sp") && getIntent().getExtras().getString(Constants.KEY, "").equalsIgnoreCase("2")) {
            ctv_close.setVisibility(View.VISIBLE);
            ctv_close.setText(R.string.close_project);
            ctv_close.setOnClickListener(this);
            ctv_close.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        if (getIntent().getExtras().getString(Constants.KEY, "").equalsIgnoreCase("3")) {
            ((Button) findViewById(R.id.btn_payment)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_message)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.btn_review)).setVisibility(View.GONE);

        }
        FunDetail();
    }

    private void FunDetail() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetProjectDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.POST_ID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            if (SharedPref.getSharedPreferences(context, Constants.USER_TYPE).equals("4"))
                                user_id = getIntent().getExtras().getString(Constants.USERID);
                            else
                                user_id = userData.getSp_id();
                            if (userData.getStatus().equals("3")) {
                                ctv_close.setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_payment)).setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_message)).setVisibility(View.GONE);
                                ((Button) findViewById(R.id.btn_review)).setVisibility(View.GONE);
                            }

                            Glide.with(context).load(userData.getProfileImage()).into(user_img);
                            ((TextView) findViewById(R.id.user_name)).setText(userData.getFirst_name() + " " + userData.getLast_name());
                            ((TextView) findViewById(R.id.user_email)).setText(userData.getEmail());
                            ((TextView) findViewById(R.id.user_number)).setText(userData.getMobileNumber());
                            ((TextView) findViewById(R.id.txt_start_date)).setText(Utility.parseDateToddMMyyyy(response.body().getUserData().getProjectData().getCreated_at()));
                            ((TextView) findViewById(R.id.txt_days)).setText(response.body().getUserData().getProjectData().getDays() + getString(R.string.days));

                            ((TextView) findViewById(R.id.txt_amount)).setText("$" + response.body().getUserData().getProjectData().getAmount());
//                            ((TextView) findViewById(R.id.txt_amount)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + response.body().getUserData().getProjectData().getAmount());

                            ((TextView) findViewById(R.id.txt_work_description)).setText(response.body().getUserData().getProjectData().getDescription());
                            final ArrayList<MilesTone> arrayList_milestone = userData.getMilestoneData();
                            if (userData.getRate_status().equals("1"))
                                ((Button) findViewById(R.id.btn_review)).setVisibility(View.GONE);
                            if (arrayList_milestone.size() > 0)
                                ((TextView) findViewById(R.id.level_milestone)).setVisibility(View.VISIBLE);
                            for (int i = 0; i < arrayList_milestone.size(); i++) {
                                View myLayout = getLayoutInflater().inflate(R.layout.row_milestone, layout_milestone, false);
                                TextView txt_work_description = myLayout.findViewById(R.id.txt_work_description);
                                TextView txt_amount = myLayout.findViewById(R.id.txt_amount);
                                LinearLayout lay_status = myLayout.findViewById(R.id.lay_status);
                                lay_status.setTag(arrayList_milestone.get(i).getStatus() + "," + arrayList_milestone.get(i).getId() + "," + arrayList_milestone.get(i).getAmount());
                                Button paid = myLayout.findViewById(R.id.btn_paid);
                                Button btn_unpaid = myLayout.findViewById(R.id.btn_unpaid);
                                lay_status.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String s = String.valueOf(v.getTag());
                                        String value[] = s.split(",");
                                        if (value[0].equals("0") && getIntent().getExtras().getString(Constants.TYPE).equals("user")) {
                                            FunPayDialog(value[2]);
                                            milestone_id = value[1];
                                            amount = value[2];
                                        }
                                    }
                                });
                                ((TextView) myLayout.findViewById(R.id.created_at)).setText(Utility.parseDateToddMMyyyy(arrayList_milestone.get(i).getCreated_at()));
                                if (arrayList_milestone.get(i).getStatus().equals("0") && getIntent().getExtras().getString(Constants.TYPE).equals("user")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                    paid.setText(getString(R.string.pay_now));
                                }
                                if (arrayList_milestone.get(i).getStatus().equals("1") && getIntent().getExtras().getString(Constants.TYPE).equals("user")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                } else if (arrayList_milestone.get(i).getStatus().equals("1") && getIntent().getExtras().getString(Constants.TYPE).equals("sp")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                } else if (arrayList_milestone.get(i).getStatus().equals("0") && getIntent().getExtras().getString(Constants.TYPE).equals("sp")) {
                                    paid.setVisibility(View.GONE);
                                    btn_unpaid.setVisibility(View.VISIBLE);
                                }
                                Spannable wordTwo, wordTwo1;
                                Spannable word = new SpannableString(getString(R.string.work_desc));
                                word.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                txt_work_description.setText(word);
                                wordTwo = new SpannableString("\"" + arrayList_milestone.get(i).getDescription() + "\"");
                                wordTwo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                txt_work_description.append(wordTwo);
                                Spannable word1 = new SpannableString(getString(R.string.req_amount));
                                word1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                txt_amount.setText(word1);

                                wordTwo1 = new SpannableString("$" + arrayList_milestone.get(i).getAmount());
//                                wordTwo1 = new SpannableString(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + arrayList_milestone.get(i).getAmount());

                                wordTwo1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, wordTwo1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                txt_amount.append(wordTwo1);
                                layout_milestone.addView(myLayout);
                            }

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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void FunPay(String transection_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunMilestonePay(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    userData.getPost_id(), userData.getSp_id(), milestone_id,
                    "paypal", amount, transection_id, "post").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "success").putExtra(Constants.TYPE, "milestone"));
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
            case R.id.ctv_login:
                final AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.app_name)
                        .setMessage(getString(R.string.msg_close_project))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                fun_close();
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
            case R.id.btn_review:
                FunDialog();
                break;
            case R.id.btn_payment:
                startActivity(new Intent(context, MilestoneRequest.class).
                        putExtra(Constants.POST_ID, getIntent().getExtras().getString(Constants.POST_ID, "")).
                        putExtra(Constants.USERID, getIntent().getExtras().getString(Constants.USERID)).putExtra(Constants.KEY, "post"));
                break;
            case R.id.btn_cancle:
                dialog.dismiss();

                break;
            case R.id.btn_submit:
                if ((rattingbar.getRating() == 0.0)) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rate));
                } else if (edit_review.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rate_comment));
                } else {
                    SendFeedback(String.valueOf(rattingbar.getRating()), edit_review.getText().toString());
                }
                break;
            case R.id.user_number:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userData.getMobileNumber())));
               /* if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProjectDescription.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {

                }*/
                break;
            case R.id.btn_message:
                context.startActivity(new Intent(context, ChatActivity.class).
                        putExtra(Constants.PROPERTY_ID, userData.getPost_id()).
                        putExtra(Constants.USER_ID, user_id).
                        putExtra(Constants.CHAT_STATUS, "2").
                        putExtra(Constants.TONAME, userData.getFirst_name() + " " + userData.getLast_name()).
                        putExtra(Constants.PROFILE_IMAGE, userData.getProfileImage()));
                break;

        }
    }

    private void fun_close() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunClose(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.POST_ID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            finish();

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

    private void SendFeedback(String ratting, String review) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunRatting(SharedPref.getSharedPreferences(context, Constants.TOKEN), user_id,
                    getIntent().getExtras().getString(Constants.POST_ID, ""), "2", ratting, review).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            dialog.dismiss();
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }

    private void FunDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_ratting);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        btn_submit = dialog.findViewById(R.id.btn_submit);
        btn_cancle = dialog.findViewById(R.id.btn_cancle);
        edit_review = dialog.findViewById(R.id.edit_review);
        rattingbar = dialog.findViewById(R.id.rattingbar);
        btn_submit.setOnClickListener(this);
        btn_cancle.setOnClickListener(this);
        dialog.show();
    }

    //-------------------------- Back Pressed -----------------------------
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

    private void FunPayDialog(final String amount) {
        final Dialog dialog = new Dialog(context);
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
        Button btn_paypal = dialog.findViewById(R.id.btn_paypal);
        Button btn_credit = dialog.findViewById(R.id.btn_credit);
        ImageView cross = dialog.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        btn_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            }
        });
        btn_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(context, StripeWeb.class));
            }
        });
        dialog.show();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData.getMobileNumber())));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                        FunPay(transaction_id);
                    } catch (JSONException e) {
                        // Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                    }
                }
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                //Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).
                        putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "milestone"));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                //Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));

            }
        }
    }

}


