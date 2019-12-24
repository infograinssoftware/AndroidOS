package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RateReview;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostBidderDetail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MyPostBidderDetail.class.getSimpleName();
    Context context;
    Toolbar toolbar;
    LinearLayout lay_ratting;
    ProgressHUD progressHUD;
    RatingBar rattingbar;
    CircleImageView user_img;
    Button btn_award;
    UserData userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_post_bidder_detail);
        context = MyPostBidderDetail.this;
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.post_service);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        lay_ratting = findViewById(R.id.lay_ratting);
        user_img = findViewById(R.id.user_img);
        rattingbar = findViewById(R.id.rattingbar);
        btn_award = findViewById(R.id.btn_award);
        btn_award.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_message)).setOnClickListener(this);
        ((TextView) findViewById(R.id.txt_days)).setText(getIntent().getExtras().getString(Constants.DAYS) +getString(R.string.days));

        ((TextView) findViewById(R.id.txt_amount)).setText("$"+getIntent().getExtras().getString(Constants.BIDAMOUNT));
//        ((TextView) findViewById(R.id.txt_amount)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " +getIntent().getExtras().getString(Constants.BIDAMOUNT));

        FunDetail();
    }

    private void FunDetail() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetMyPostBidderDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.POST_ID, ""),
                    getIntent().getExtras().getString(Constants.USERID, "")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            Glide.with(context).load(userData.getProfileImage()).into(user_img);
                            String str = userData.getFirst_name() + " " + userData.getLast_name();
                            ((TextView) findViewById(R.id.user_name)).setText(str.substring(0, 1).toUpperCase() + str.substring(1));
                            ((TextView) findViewById(R.id.description)).setText(userData.getProposal());
                            rattingbar.setRating(Float.parseFloat(userData.getBidder_rating()));
                            ((TextView) findViewById(R.id.txt_total_rate)).setText(userData.getBidder_rating());
                            GradientDrawable bgShape_accept1 = (GradientDrawable) btn_award.getBackground();
                            bgShape_accept1.setColor(getResources().getColor(R.color.green));
                            btn_award.setEnabled(true);
                            if (userData.getStatus().equals("1") || userData.getStatus().equals("2") || userData.getStatus().equals("3")) {
                                btn_award.setText(getString(R.string.awarded));
                                btn_award.setEnabled(false);
                                bgShape_accept1.setColor(getResources().getColor(R.color.lightgreen));
                            }
                            ArrayList<RateReview> arrayList_rate = userData.getRate_review();
                            if (arrayList_rate.size() > 0) {
                                ((TextView) findViewById(R.id.level_rate)).setVisibility(View.VISIBLE);
                                for (int i = 0; i < arrayList_rate.size(); i++) {
                                    View myLayout = getLayoutInflater().inflate(R.layout.layout_ratting_review, lay_ratting, false);
                                    RatingBar ratingBar = myLayout.findViewById(R.id.rattingbar);
                                    ((TextView) myLayout.findViewById(R.id.name)).setText(arrayList_rate.get(i).getName());
                                    ((TextView) myLayout.findViewById(R.id.desc)).setText(arrayList_rate.get(i).getComment());
                                    CircleImageView img = myLayout.findViewById(R.id.user_img);
                                    ((RatingBar) myLayout.findViewById(R.id.rattingbar)).setRating(Float.parseFloat(arrayList_rate.get(i).getRate()));
                                    ((TextView) myLayout.findViewById(R.id.total_rate)).setText(arrayList_rate.get(i).getRate());
                                    Glide.with(context).load(arrayList_rate.get(i).getProfileImage()).into(img);
                                    lay_ratting.addView(myLayout);
                                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_award:
                fun_award();
                break;
            case R.id.btn_message:
                context.startActivity(new Intent(context, ChatActivity.class).
                        putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.POST_ID)).
                        putExtra(Constants.USER_ID, getIntent().getExtras().getString(Constants.USERID)).
                        putExtra(Constants.CHAT_STATUS, "2").
                        putExtra(Constants.TONAME, userData.getFirst_name() + " " + userData.getLast_name()).
                        putExtra(Constants.PROFILE_IMAGE, userData.getProfileImage()));
                break;

        }
    }

    private void fun_award() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunSpHire(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.POST_ID, ""),
                    getIntent().getExtras().getString(Constants.USERID, "")).enqueue(new Callback<RetrofitUserData>() {
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
}
