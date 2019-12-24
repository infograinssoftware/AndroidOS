package com.open_source.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RenterReferenceApproval extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RenterReferenceApproval.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    CircleImageView user_image;
    ImageView pro_image, signature_image;
    UserData userData;
    private Context context;
    private TextView toolbar_title;
    private ProgressHUD progressHUD;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rent_reference_approval);
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
        toolbar_title.setText(R.string.disclaimer_form_approval);
        user_image = findViewById(R.id.user_img);
        pro_image = findViewById(R.id.pro_image);
        signature_image = findViewById(R.id.signature_image);
        ((TextView) findViewById(R.id.txt_contact)).setOnClickListener(this);
        ((Button) findViewById(R.id.txt_approve)).setOnClickListener(this);
        ((Button) findViewById(R.id.txt_reject)).setOnClickListener(this);
        GetData();
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetRentDisclaimer(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.REQUEST_ID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            ((TextView) findViewById(R.id.txt_renter_type)).setText(userData.getRenter_type());
                            ((TextView) findViewById(R.id.txt_start_date)).setText(userData.getStart_date());
                            ((TextView) findViewById(R.id.end_date)).setText(userData.getEnd_date());
                            if (userData.getStatus().equals("2")) {
                                //((LinearLayout) findViewById(R.id.lay_bottom)).setVisibility(View.GONE);
                            }
                            if (!userData.getProfileImage().isEmpty())
                                Glide.with(context).load(userData.getProfileImage()).into(user_image);
                            ((TextView) findViewById(R.id.user_name)).setText(userData.getName());
                            ((TextView) findViewById(R.id.txt_contact)).setText(userData.getMobile());
                            ((TextView) findViewById(R.id.pro_type)).setText(userData.getType());
                            ((TextView) findViewById(R.id.pro_address)).setText(userData.getLocation());
                            Glide.with(context).load(userData.getProperty_images()).into(pro_image);
                            ((EditText) findViewById(R.id.et_user_name)).setText(userData.getName());
                            ((EditText) findViewById(R.id.et_mobile)).setText(userData.getMobile());
                            Glide.with(context).load(userData.getSignature_image()).into(signature_image);
                            ((EditText) findViewById(R.id.et_date)).setText(Utility.parseDateToddMMyyyy(userData.getCreated_at()));
                            Glide.with(context).load(userData.getSignature_image());
                            if (!userData.getSocial_security_number().isEmpty())
                                ((EditText) findViewById(R.id.et_security_check)).setText(userData.getSocial_security_number());
                            else
                                ((EditText) findViewById(R.id.et_security_check)).setText(R.string.not_available);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_contact:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData.getMobile())));
               /* if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RenterReferenceApproval.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {

                }*/
                break;
            case R.id.txt_approve:
                FunApprove("2");
                break;

            case R.id.txt_reject:
                FunApprove("3");
                break;
        }
    }

    private void FunApprove(String status) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunApprove(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.REQUEST_ID), status).enqueue(new Callback<RetrofitUserData>() {
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

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + userData.getMobile())));
            }
        }
    }
}
