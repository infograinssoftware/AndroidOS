package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.adapter.FavouritesAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.ParallaxRecyclerView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FavouritesActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FavouritesActivity.class.getSimpleName();
    ImageView img_filter;
    private Context context;
    private ParallaxRecyclerView recyclerView;
    private FavouritesAdapter favouritesAdapter;
    private List<RetroObject> favouritesList = new ArrayList<>();
    private Toolbar toolbar;
    private TextView toolbar_title;
    private String str_badroom="",str_bathroom="",str_type="",str_purpose="";
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_searches);
        context = this;
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (ParallaxRecyclerView) findViewById(R.id.recyclerView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        img_filter = findViewById(R.id.img_notification);
        img_filter.setImageDrawable(getResources().getDrawable(R.drawable.filter));
        toolbar_title.setText(R.string.favourites);
        img_filter.setOnClickListener(this);
        callFavouriteListAPI();
    }

    //-------------------------- Back Pressed -----------------------------
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
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------- Status Bar ------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }

    public void callFavouriteListAPI() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getFavouriteListAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }

                    Log.e(TAG, "onResponse: " + response.toString());

                    try {
                        if (response.body().getStatus() == 200) {
                            img_filter.setVisibility(View.VISIBLE);
                            favouritesList = response.body().getObject();
                            favouritesAdapter = new FavouritesAdapter(context, favouritesList, "");
                            recyclerView.setAdapter(favouritesAdapter);

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            ((ImageView) findViewById(R.id.img_nodata)).setVisibility(View.VISIBLE);
                            // Utility.ShowToastMessage(context, response.body().getMessage());
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
            case R.id.img_notification:
                Intent i = new Intent(this, FavFilterActivity.class);
                startActivityForResult(i, 2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                str_type = data.getStringExtra(Constants.TYPE);
                str_purpose = data.getStringExtra(Constants.PURPOSE);
                if (data.getStringExtra(Constants.BATHROOM).contains("Bathrooms"))
                    str_bathroom = data.getStringExtra(Constants.BATHROOM).replace("Bathrooms", "");
                else
                    str_bathroom = data.getStringExtra(Constants.BATHROOM).replace("Bathroom", "");
                if (data.getStringExtra(Constants.BADROOM).contains("Bedrooms"))
                    str_badroom = data.getStringExtra(Constants.BADROOM).replace("Bedrooms", "");
                else
                    str_badroom = data.getStringExtra(Constants.BADROOM).replace("Bedroom", "");
                if (Utility.isConnectingToInternet(context)) {
                    progressHUD = ProgressHUD.show(context, true, false, null);
                    RetrofitClient.getAPIService().GetFavFilter(SharedPref.getSharedPreferences(context,Constants.TOKEN),str_type,str_purpose,
                            data.getStringExtra(Constants.LATITUDE)+"",
                            data.getStringExtra(Constants.LONGITUDE)+"",
                            str_bathroom,str_badroom,data.getStringExtra(Constants.RADIUS)).enqueue(new Callback<RetrofitUserData>() {
                        @Override
                        public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                            if (progressHUD != null && progressHUD.isShowing()) {
                                progressHUD.dismiss();
                            }
                            try {
                                if (response.body().getStatus() == 200) {
                                    favouritesList.clear();
                                    favouritesList = response.body().getObject();
                                    favouritesAdapter = new FavouritesAdapter(context, favouritesList, "");
                                    recyclerView.setAdapter(favouritesAdapter);

                                } else if (response.body().getStatus() == 401) {
                                    SharedPref.clearPreference(context);
                                    startActivity(new Intent(context, WelcomeActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                } else {
                                    favouritesList.clear();
                                    favouritesAdapter.notifyDataSetChanged();
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
    }
}

