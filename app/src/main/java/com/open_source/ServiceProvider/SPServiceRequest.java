package com.open_source.ServiceProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.ConfirmPaymentMessage;
import com.open_source.activity.StripeWeb;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.ImageAdapter;
import com.open_source.modal.MilesTone;
import com.open_source.modal.PropertyImg;
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


public class SPServiceRequest extends AppCompatActivity implements View.OnClickListener {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = SPServiceRequest.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title, btn_submit, btn_cancle,view_accept,view_complete;
    CircleImageView req_image;
    String request_id = "", request_status = "0", status = "0", milestone_id = "", amount = "", type = "",user_id="";
    ProgressHUD progressHUD;
    Bundle bundle;
    EditText edit_review;
    RatingBar rattingbar;
    Dialog dialog;
    Button btn_review, btn_payment, btn_message;
    RecyclerView recycle_service_img;
    UserData userData;
    LinearLayout layout_milestone;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_request);
        context = SPServiceRequest.this;
        init_view();
    }

    private void init_view() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.service_request);
        recycle_service_img = findViewById(R.id.recycle_service_img);
        layout_milestone = findViewById(R.id.lay_milestone);
        req_image = findViewById(R.id.req_image);
        ((TextView) findViewById(R.id.req_number)).setOnClickListener(this);
        ((TextView) findViewById(R.id.btn_accept)).setOnClickListener(this);
        btn_review = findViewById(R.id.btn_review);
        btn_payment = findViewById(R.id.btn_payment);
        btn_message = findViewById(R.id.btn_message);
        view_accept = findViewById(R.id.view_accept);
        view_complete = findViewById(R.id.view_complete);
        btn_review.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
        btn_message.setOnClickListener(this);
        request_id = getIntent().getExtras().getString(Constants.REQUEST_ID, "");
        if (SharedPref.getSharedPreferences(context, Constants.USER_TYPE).equals("4"))
            type = "sp_request";
        else
            type = "user_request";
        FunGetData();

        /* bundle = getIntent().getExtras();
        Bundle extra = getIntent().getBundleExtra("extra");
        ArrayList<PropertyImg> objects = (ArrayList<PropertyImg>) extra.getSerializable("objects");
        if (objects.size() > 0) {
            ((TextView) findViewById(R.id.txt_img)).setVisibility(View.VISIBLE);
            recycle_service_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ImageAdapter imageAdapter = new ImageAdapter(context, objects);
            recycle_service_img.setAdapter(imageAdapter);
        }*/

        /*if (bundle != null) {
            request_status = getIntent().getExtras().getString(Constants.REQUEST_STATUS, "");
            //request_id = bundle.getString(Constants.REQUEST_ID, "");
            if (bundle.getString(Constants.TYPE, "").equalsIgnoreCase("sp_request")) {
                status = "1";
                if (request_status.equalsIgnoreCase("1")) {
                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                    btn_payment.setVisibility(View.VISIBLE);
                    // btn_review.setVisibility(View.VISIBLE);
                } else if (request_status.equalsIgnoreCase("2")) {
                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                    btn_payment.setVisibility(View.GONE);
                    //btn_review.setVisibility(View.VISIBLE);
                }
            } else if (bundle.getString(Constants.TYPE, "").equalsIgnoreCase("user_request")) {
                btn_payment.setVisibility(View.GONE);
                status = "2";
                if (request_status.equalsIgnoreCase("0"))
                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                if (request_status.equalsIgnoreCase("1")) {
                    // btn_review.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.btn_accept)).setText("Complete");
                } else if (request_status.equalsIgnoreCase("2")) {
                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                    //btn_review.setVisibility(View.VISIBLE);
                }

            }

            TextView view_accept = findViewById(R.id.view_accept);
            TextView view_complete = findViewById(R.id.view_complete);
            if (!getIntent().getExtras().getString(Constants.CREATED_AT).isEmpty()) {
                String s[] = Utility.parseDateToddMMyyyy(getIntent().getExtras().getString(Constants.CREATED_AT)).split(" ");
                ((TextView) findViewById(R.id.txt_request_date)).setText((s[0]));
            }

            if (!getIntent().getExtras().getString(Constants.ACCEPT_AT).isEmpty()) {
                String s[] = Utility.parseDateToddMMyyyy(getIntent().getExtras().getString(Constants.ACCEPT_AT)).split(" ");
                ((TextView) findViewById(R.id.txt_accept_date)).setText(s[0]);

            }
            if (!getIntent().getExtras().getString(Constants.COMPLETE_AT).isEmpty()) {
                String s[] = Utility.parseDateToddMMyyyy(getIntent().getExtras().getString(Constants.COMPLETE_AT)).split(" ");
                ((TextView) findViewById(R.id.txt_complete_date)).setText(s[0]);
            }
            GradientDrawable bgShape_accept1 = (GradientDrawable) view_accept.getBackground();
            bgShape_accept1.setColor(getResources().getColor(R.color.grey));
            GradientDrawable bgShape_comp1 = (GradientDrawable) view_complete.getBackground();
            bgShape_comp1.setColor(getResources().getColor(R.color.grey));
            if (request_status.equalsIgnoreCase("1")) {
                GradientDrawable bgShape_accept = (GradientDrawable) view_accept.getBackground();
                bgShape_accept.setColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) findViewById(R.id.txt_accept)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            } else if (request_status.equalsIgnoreCase("2")) {
                GradientDrawable bgShape_accept = (GradientDrawable) view_accept.getBackground();
                bgShape_accept.setColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) findViewById(R.id.txt_accept)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                GradientDrawable bgShape_comp = (GradientDrawable) view_complete.getBackground();
                bgShape_comp.setColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) findViewById(R.id.txt_complete)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
            }
        }*/

    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunGetRequestData(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    request_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            if (SharedPref.getSharedPreferences(context, Constants.USER_TYPE).equals("4"))
                                user_id = userData.getUserid();
                            else
                                user_id = userData.getSp_id();
                            Glide.with(context).load(userData.getProfileImage()).into(req_image);
                            ((TextView) findViewById(R.id.txt_des_date)).setText(userData.getService_date());
                            ((TextView) findViewById(R.id.txt_problem)).setText(userData.getProblem());
                            ((TextView) findViewById(R.id.req_email)).setText(userData.getEmail());
                            ((TextView) findViewById(R.id.req_name)).setText(userData.getFirst_name()+" "+userData.getLast_name());
                            ((TextView) findViewById(R.id.req_number)).setText(userData.getMobileNumber());
                            ArrayList<PropertyImg> objects = userData.getProperty_img();
                            if (objects.size() > 0) {
                                ((TextView) findViewById(R.id.txt_img)).setVisibility(View.VISIBLE);
                                recycle_service_img.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                ImageAdapter imageAdapter = new ImageAdapter(context, objects);
                                recycle_service_img.setAdapter(imageAdapter);
                            }
                            request_status = userData.getRequest_status();
                            if ((request_status.equalsIgnoreCase("1") || request_status.equalsIgnoreCase("2")) && userData.getRate_status().equals("0"))
                                btn_review.setVisibility(View.VISIBLE);
                            if (type.equalsIgnoreCase("sp_request")) {
                                status = "1";
                                if (request_status.equalsIgnoreCase("1")) {
                                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                                    btn_payment.setVisibility(View.VISIBLE);
                                    // btn_review.setVisibility(View.VISIBLE);
                                } else if (request_status.equalsIgnoreCase("2")) {
                                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                                    btn_payment.setVisibility(View.GONE);
                                    //btn_review.setVisibility(View.VISIBLE);
                                }
                            }
                            else if (type.equalsIgnoreCase("user_request")) {
                                btn_payment.setVisibility(View.GONE);
                                status = "2";
                                if (request_status.equalsIgnoreCase("0"))
                                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                                if (request_status.equalsIgnoreCase("1")) {
                                    // btn_review.setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.btn_accept)).setText(R.string.level_complete);
                                } else if (request_status.equalsIgnoreCase("2")) {
                                    ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                                    //btn_review.setVisibility(View.VISIBLE);
                                }

                            }
                            if (!userData.getCreated_at().isEmpty()) {
                                String s[] = Utility.parseDateToddMMyyyy(userData.getCreated_at()).split(" ");
                                ((TextView) findViewById(R.id.txt_request_date)).setText((s[0]));
                            }

                            if (!userData.getAccepted_at().isEmpty()) {
                                String s[] = Utility.parseDateToddMMyyyy(userData.getAccepted_at()).split(" ");
                                ((TextView) findViewById(R.id.txt_accept_date)).setText(s[0]);

                            }
                            if (!userData.getCompleted_at().isEmpty()) {
                                String s[] = Utility.parseDateToddMMyyyy(userData.getCompleted_at()).split(" ");
                                ((TextView) findViewById(R.id.txt_complete_date)).setText(s[0]);
                            }
                            GradientDrawable bgShape_accept1 = (GradientDrawable) view_accept.getBackground();
                            bgShape_accept1.setColor(getResources().getColor(R.color.grey));
                            GradientDrawable bgShape_comp1 = (GradientDrawable) view_complete.getBackground();
                            bgShape_comp1.setColor(getResources().getColor(R.color.grey));
                            if (request_status.equalsIgnoreCase("1")) {
                                GradientDrawable bgShape_accept = (GradientDrawable) view_accept.getBackground();
                                bgShape_accept.setColor(getResources().getColor(R.color.colorPrimaryDark));
                                ((TextView) findViewById(R.id.txt_accept)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            } else if (request_status.equalsIgnoreCase("2")) {
                                GradientDrawable bgShape_accept = (GradientDrawable) view_accept.getBackground();
                                bgShape_accept.setColor(getResources().getColor(R.color.colorPrimaryDark));
                                ((TextView) findViewById(R.id.txt_accept)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                GradientDrawable bgShape_comp = (GradientDrawable) view_complete.getBackground();
                                bgShape_comp.setColor(getResources().getColor(R.color.colorPrimaryDark));
                                ((TextView) findViewById(R.id.txt_complete)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                ((TextView) findViewById(R.id.btn_accept)).setVisibility(View.GONE);
                            }
                            ArrayList<MilesTone> arrayList_milestone = userData.getMilestoneData();
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
                                        if (value[0].equals("0") && type.equals("user_request")) {
                                            FunPayDialog(value[2]);
                                            milestone_id = value[1];
                                            amount = value[2];
                                        }
                                    }
                                });
                                ((TextView) myLayout.findViewById(R.id.created_at)).setText(Utility.parseDateToddMMyyyy(arrayList_milestone.get(i).getCreated_at()));
                                if (arrayList_milestone.get(i).getStatus().equals("0") && type.equals("user_request")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                    paid.setText(getString(R.string.pay_now));
                                }
                                if (arrayList_milestone.get(i).getStatus().equals("1") && type.equals("user_request")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                } else if (arrayList_milestone.get(i).getStatus().equals("1") && type.equals("sp_request")) {
                                    paid.setVisibility(View.VISIBLE);
                                    btn_unpaid.setVisibility(View.GONE);
                                } else if (arrayList_milestone.get(i).getStatus().equals("0") && type.equals("sp_request")) {
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
//                                wordTwo1 = new SpannableString(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " +arrayList_milestone.get(i).getAmount());

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_payment:
                startActivity(new Intent(context, MilestoneRequest.class).
                        putExtra(Constants.POST_ID, request_id).
                        putExtra(Constants.USERID, userData.getUserid()).
                        putExtra(Constants.KEY, "request"));
                break;
            case R.id.btn_message:
                context.startActivity(new Intent(context, ChatActivity.class).
                        putExtra(Constants.PROPERTY_ID,request_id).
                        putExtra(Constants.USER_ID,user_id).
                        putExtra(Constants.CHAT_STATUS, "3").
                        putExtra(Constants.TONAME, userData.getFirst_name() + " " + userData.getLast_name()).
                        putExtra(Constants.PROFILE_IMAGE, userData.getProfileImage()));
                break;
            case R.id.btn_accept:
                if (!request_id.isEmpty()) {
                    if (Utility.isConnectingToInternet(context)) {
                        progressHUD = ProgressHUD.show(context, true, false, null);
                        RetrofitClient.getAPIService().SPAcceptRequest(SharedPref.getSharedPreferences(context, Constants.TOKEN), request_id, status).enqueue(new Callback<RetrofitUserData>() {
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

                } else {
                    Utility.ShowToastMessage(context, getString(R.string.bad_error));
                }

                break;
            case R.id.btn_cancle:
                dialog.dismiss();
                finish();
                break;
            case R.id.btn_review:
                FunDialog();
                break;
            case R.id.btn_submit:
                /*if ((rattingbar.getRating() == 0.0) && (edit_review.getText().toString().isEmpty())) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rate));
                } else {
                    String ratting = "0.0", review = "";
                    ratting = String.valueOf(rattingbar.getRating());
                    if (!edit_review.getText().toString().isEmpty()) {
                        review = edit_review.getText().toString();
                    }
                    SendFeedback(ratting, review, dialog);
                }*/
                if ((rattingbar.getRating() == 0.0)) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rate));
                } else if (edit_review.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rate_comment));
                } else {
                    SendFeedback(String.valueOf(rattingbar.getRating()),user_id,edit_review.getText().toString());
                }
                break;
            case R.id.req_number:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userData.getMobileNumber())));
              /*  if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SPServiceRequest.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData.getMobileNumber())));
                }*/
                break;
        }
    }

    private void SendFeedback(String ratting,String id,String review) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunRatting(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    id,
                    request_id, "1", ratting, review).enqueue(new Callback<RetrofitUserData>() {
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

    private void FunPay(String transection_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunMilestonePay(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                   request_id, userData.getSp_id(), milestone_id,
                    "paypal", amount, transection_id, "request").enqueue(new Callback<RetrofitUserData>() {
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


}
