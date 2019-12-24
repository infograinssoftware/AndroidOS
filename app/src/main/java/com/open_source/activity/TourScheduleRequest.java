package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TourScheduleRequest extends BaseActivity {
    private static final String TAG = Renter_property_detail.class.getSimpleName();
    Context context;
    TextView toolbar_title, txt_accept, txt_decline;
    ProgressHUD progressHUD;
    Toolbar toolbar;
    String type = "", status = "";
    ImageView pro_image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rent_detail);
        context = TourScheduleRequest.this;
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pro_image = findViewById(R.id.pro_image);
        txt_accept = findViewById(R.id.txt_accept);
        txt_decline = findViewById(R.id.txt_decline);
        txt_accept.setVisibility(View.GONE);
        txt_decline.setVisibility(View.GONE);
        toolbar_title.setText(R.string.title_schedule_tour);
        if (!getIntent().getExtras().getString(Constants.PROPERTY_ID,"").isEmpty())
        GetData();
        else
            Utility.ShowToastMessage(context,R.string.something_wrong);
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID,"")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData=response.body().getUserData();
                            Glide.with(context).load(userData.getProperty_img().get(0).getFile_name()).into(pro_image);
                            ((TextView) findViewById(R.id.pro_address)).setText(userData.getLocation());
                            ((TextView) findViewById(R.id.pro_type)).setText(userData.getType());
                            ((TextView) findViewById(R.id.pro_city)).setText(userData.getCity() + ", " + userData.getSociety());
                            ((TextView) findViewById(R.id.txt_beds)).setText(userData.getBadroom());
                            ((TextView) findViewById(R.id.txt_bath)).setText(userData.getBathroom());
                            ((TextView) findViewById(R.id.txt_area)).setText(userData.getArea_square());
                            ((TextView) findViewById(R.id.txt_name)).setText(getIntent().getExtras().getString(Constants.FIRST_NAME,""));
                            ((TextView) findViewById(R.id.level_address)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.txt_address)).setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.txt_address)). setText(getIntent().getExtras().getString(Constants.EMAIL,""));
                            ((TextView) findViewById(R.id.txt_contact)).setText(getIntent().getExtras().getString(Constants.MOBILE,""));
                            ((TextView) findViewById(R.id.txt_date)).setText(getIntent().getExtras().getString(Constants.REQUEST_DATE,""));
                            ((TextView) findViewById(R.id.txt_time)).setText(getIntent().getExtras().getString(Constants.REQUEST_TIME,""));

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

                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.payment:
                startActivity(new Intent(context, RentPayListActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
