package com.open_source.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.open_source.R;
import com.open_source.ServiceProvider.SPSignUpActivity;
import com.open_source.ServiceProvider.ServiceProviderHome;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.LocationProvider;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginEmailActivity extends BaseActivity implements View.OnClickListener, LocationProvider.LocationCallback {
    private static final String TAG = LoginEmailActivity.class.getSimpleName();
    Context context;
    EditText cet_email, cet_password;
    RelativeLayout btn_submit;
    TextView ctv_signup, ctv_forget_pwd;
    ProgressHUD progressHUD;
    String token, userType = "";
    Dialog dialog1;
    ImageView close1;
    RadioGroup radio_group;
    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        context = this;
        getToken();
        if (SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN).equals("")) {
            SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
        }
        init();
    }

    public void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token =instanceIdResult.getToken();
                SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
                Log.e(LoginActivity.class.getSimpleName(), "onSuccess: " + token);
            }
        });
    }

    public void init() {
        mLocationProvider = new LocationProvider(this, this);
        if (mLocationProvider != null)
            mLocationProvider.connect();
        cet_email = (EditText) findViewById(R.id.cet_email);
        cet_password = (EditText) findViewById(R.id.cet_password);
        btn_submit = (RelativeLayout) findViewById(R.id.btn_submit);
        ctv_signup = (TextView) findViewById(R.id.ctv_signup);
        ctv_forget_pwd = (TextView) findViewById(R.id.ctv_forget_pwd);

        ctv_forget_pwd.setOnClickListener(this);
        ctv_signup.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_signup:
                showDialogUserType();
                break;

            case R.id.btn_submit:
                if (!Utility.isValidEmail(cet_email.getText().toString())) {
                    Utility.ShowToastMessage(context, R.string.msg_error_email);
                } else if (cet_password.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_pwd);
                } else {
                    callLoginAPI();
                }
                break;

            case R.id.ctv_forget_pwd:
                startActivity(new Intent(context, ForgetPasswordActivity.class));
                break;
            case R.id.close1:
                dialog1.dismiss();
                break;
        }
    }

    public void showDialogUserType() {
        dialog1 = new Dialog(context, R.style.myDialog);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_user_type);
        dialog1.setCancelable(false);
        close1 = dialog1.findViewById(R.id.close1);
        radio_group = dialog1.findViewById(R.id.radio_group);
        btn_submit = dialog1.findViewById(R.id.btn_submit);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_buy_sell) {
                    userType = "1";
                    startActivity(new Intent(context, SignUpActivity.class).putExtra(Constants.USER_TYPE, userType));
                }
                else if (checkedId == R.id.rb_renter) {
                    userType = "5";
                    startActivity(new Intent(context, SignUpActivity.class).putExtra(Constants.USER_TYPE, userType));
                } else if (checkedId == R.id.rb_investor) {
                    userType = "2";
                    startActivity(new Intent(context, SignUpActivity.class).putExtra(Constants.USER_TYPE, userType));
                } else if (checkedId == R.id.rb_agent) {
                    userType = "3";
                    startActivity(new Intent(context, SignUpActivity.class).putExtra(Constants.USER_TYPE, userType));
                } else if (checkedId == R.id.rb_sp) {
                    userType = "4";
                    startActivity(new Intent(context, SPSignUpActivity.class).putExtra(Constants.USER_TYPE, userType));
                }
                dialog1.dismiss();
            }
        });
        close1.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        dialog1.show();
    }

    //------------------ Login ----------------------------
    public void callLoginAPI() {
        if (Utility.isConnectingToInternet(context)) {
            Log.e(TAG, "========: " + SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN));
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getLoginAPI(cet_email.getText().toString(),
                    cet_password.getText().toString(), SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN),
                    "android", getIntent().getStringExtra(Constants.USER_TYPE)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            if (userData==null){
                                Log.e(TAG, "onResponse: im nullll" );
                            } if (userData.getUserType()==null){
                                Log.e(TAG, "onResponse: user type is nulllll" );
                            }if (userData.getQuestionaire_status()==null){
                                Log.e(TAG, "onResponse: user type is nulllll" );
                            }
                            SharedPref.setSharedPreference(context, Constants.TOKEN, userData.getToken());
                            SharedPref.setSharedPreference(context, Constants.USER_ID, userData.getUser_id());
                            SharedPref.setSharedPreference(context, Constants.FIRST_NAME, userData.getFirst_name());
                            SharedPref.setSharedPreference(context, Constants.LAST_NAME, userData.getLast_name());
                            SharedPref.setSharedPreference(context, Constants.USER_TYPE, userData.getUserType());
                            SharedPref.setSharedPreference(context, Constants.EMAIL, userData.getEmail());
                            SharedPref.setSharedPreference(context, Constants.MOBILE_NUMBER, userData.getMobileNumber());
                            SharedPref.setSharedPreference(context, Constants.USER_PROFILE_IMAGE, userData.getUrl() + userData.getProfileImage());
                            SharedPref.setSharedPreference(context, Constants.CURRENCY, userData.getCurrency());
                            SharedPref.setSharedPreference(context, Constants.CURRENCY_SYMBOL, userData.getCurrency_symbol());
                            if (userData.getUserType().equals("2") && userData.getQuestionaire_status().equals("0")) {
                                Utility.ShowToastMessage(context, getString(R.string.val_questionnarir));
                                startActivity(new Intent(context, InvestorQuesActivity.class));
                            } else if (userData.getUserType().equals("4")) {
                                startActivity(new Intent(context, ServiceProviderHome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                SharedPref.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                                startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                SharedPref.setSharedPreference(context, Constants.EVENT_CHECK, "1");
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: " + location.getLatitude() + "" + location.getLongitude());
        SharedPref.setSharedPreference(context, Constants.LATITUDE, String.valueOf(location.getLatitude()));
        SharedPref.setSharedPreference(context, Constants.LONGITUDE, String.valueOf(location.getLongitude()));
        Utility.GetAddress(context, location.getLatitude(), location.getLongitude());
    }
}

