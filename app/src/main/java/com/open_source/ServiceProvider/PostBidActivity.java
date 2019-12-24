package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
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


public class PostBidActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PostBidActivity.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    CircleImageView user_img, bid_user_img;
    EditText ed_praposal, ed_amount, ed_days;
    ProgressHUD progressHUD;
    RatingBar rattingbar;
    UserData userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_user_detail);
        context = PostBidActivity.this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.post_service);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((Button) findViewById(R.id.btn_post_bid)).setOnClickListener(this);
        user_img = findViewById(R.id.user_img);
        bid_user_img = findViewById(R.id.bid_user_img);
        ed_praposal = findViewById(R.id.ed_praposal);
        ed_amount = findViewById(R.id.ed_amount);
        ed_days = findViewById(R.id.ed_days);
        rattingbar = findViewById(R.id.rattingbar);
        FunGetData();


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
            case R.id.btn_post_bid:
                if (userData.getBid_status() == 1) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_already_post_bid));
                    } else {
                    if (ed_praposal.getText().toString().trim().isEmpty())
                        Utility.ShowToastMessage(context, getString(R.string.enter_praposal));
                    else if (ed_amount.getText().toString().trim().isEmpty())
                        Utility.ShowToastMessage(context, getString(R.string.enter_bid_amount));
                    else if (ed_days.getText().toString().trim().isEmpty())
                        Utility.ShowToastMessage(context, getString(R.string.enter_task_days));
                    else {
                        FunBid();
                    }
                }
                break;
        }
    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetServicePostDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.ID, "")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            if (userData.getBid_status() == 1) {
                                ((Button) findViewById(R.id.btn_post_bid)).setBackgroundResource(R.drawable.round_button_fad);
                            }
                            String username = userData.getFirst_name() + " " + userData.getLast_name();
                            String bid_username = SharedPref.getSharedPreferences(context, Constants.FIRST_NAME) + " " + SharedPref.getSharedPreferences(context, Constants.LAST_NAME);
                            Glide.with(context).load(userData.getProfileImage()).into(user_img);
                            ((TextView) findViewById(R.id.user_name)).setText(username.substring(0, 1).toUpperCase() + username.substring(1));
                            ((TextView) findViewById(R.id.description)).setText(userData.getDescription());
                            Glide.with(context).load(SharedPref.getSharedPreferences(context, Constants.USER_PROFILE_IMAGE)).into(bid_user_img);
                            ((TextView) findViewById(R.id.bid_user_name)).setText(bid_username.substring(0, 1).toUpperCase() + bid_username.substring(1));
                            rattingbar.setRating(Float.parseFloat((userData.getClient_rating())));
                            ((TextView) findViewById(R.id.txt_toal_rate)).setText(userData.getTotal_client_rate());

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

    private void FunBid() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunPostBid(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    ed_praposal.getText().toString(), ed_amount.getText().toString(),
                    userData.getPost_id(),
                    ed_days.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
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
