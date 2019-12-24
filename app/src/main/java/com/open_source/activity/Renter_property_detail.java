package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.modal.PropertyData;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.USER_DATA_RENT;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Renter_property_detail extends BaseActivity implements View.OnClickListener {
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
        context = Renter_property_detail.this;
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
        txt_accept.setOnClickListener(this);
        txt_decline.setOnClickListener(this);
        ((TextView)findViewById(R.id.txt_date)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.txt_time)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.level_date)).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.level_time)).setVisibility(View.GONE);
        if (getIntent().getExtras().getString(Constants.STATUS, "").equalsIgnoreCase("request"))
            toolbar_title.setText(R.string.rent_request);
        else
            toolbar_title.setText(getString(R.string.property_detail));
        type = getIntent().getExtras().getString(Constants.TYPE, "");
        if (type.equalsIgnoreCase("owner")) {
            ((LinearLayout) findViewById(R.id.lay_bottom)).setVisibility(View.GONE);
        } else if (type.equalsIgnoreCase("renter")) {
            ((LinearLayout) findViewById(R.id.lay_bottom)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.card_ranter)).setVisibility(View.GONE);
        }
        GetData();
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetRentDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.REQUEST_ID, ""),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID, "")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            if (userData.getConfirmation().equalsIgnoreCase("1")) {
                                txt_accept.setVisibility(View.GONE);
                                txt_decline.setVisibility(View.GONE);
                            }
                            PropertyData propertyData = userData.getProperty_data();
                            USER_DATA_RENT user_data_rent = userData.getUser_data();
                            ((TextView) findViewById(R.id.pro_type)).setText("");
                            ((TextView) findViewById(R.id.pro_city)).setText(propertyData.getCity() + ", " + propertyData.getSociety());
                             Log.e(TAG, "=========: "+propertyData.getUrl() + propertyData.getProperty_images() );
                             Glide.with(context).load(propertyData.getUrl()+ propertyData.getProperty_images()).into(pro_image);
                            ((TextView) findViewById(R.id.pro_address)).setText(propertyData.getLocation());
                            ((TextView) findViewById(R.id.txt_beds)).setText(propertyData.getBadroom());
                            ((TextView) findViewById(R.id.txt_bath)).setText(propertyData.getBathroom());
                            ((TextView) findViewById(R.id.txt_area)).setText(propertyData.getArea_square());
                            ((TextView) findViewById(R.id.txt_name)).setText(user_data_rent.getFirst_name() +" "+user_data_rent.getLast_name());
                            ((TextView) findViewById(R.id.txt_contact)).setText(user_data_rent.getMobileNumber());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rent, menu);//Menu Resource, Menu
        MenuItem menuItem=menu.findItem(R.id.payment);
        if (getIntent().getExtras().getString(Constants.STATUS, "").equalsIgnoreCase("request"))
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_accept:
                status = "1";
                FunRequest(status);
                break;
            case R.id.txt_decline:
                status = "0";
                FunRequest(status);
                break;
        }
    }

    private void FunRequest(String status) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UpdateRequest(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.REQUEST_ID, ""),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID, ""), status).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, MainActivity.class));
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
