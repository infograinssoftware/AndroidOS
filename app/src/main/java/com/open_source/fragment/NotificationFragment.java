package com.open_source.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.NotificationAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends AppCompatActivity {
    private static final String TAG = NotificationFragment.class.getSimpleName();
    Context context;
    View rootView;
    NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;
    List<RetroObject> notificationList = new ArrayList<>();
    List<RetroObject> loadlist= new ArrayList<>();
    Toolbar toolbar;
    TextView toolbar_title;
    ProgressHUD progressHUD;
    int i = 0, page = 0;
    Boolean load = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);
        context = NotificationFragment.this;
        init();
    }

    public void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.notification);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        notificationAdapter = new NotificationAdapter(context,loadlist);
        recyclerView.setAdapter(notificationAdapter);
        if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
            Utility.ShowToastMessage(context,getString(R.string.message_guest));
            //Utility.ShowToastMessage(context, getString(R.string.no_data_exist));
        } else {
            FunGetData();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page!=0 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        FunGetData();
                    }
                    // GetUser();
                    //Toast.makeText(context,"LAst",Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            //Log.e(TAG, "=========:page"+page);
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Notification_List(SharedPref.getSharedPreferences(context, Constants.TOKEN),page+"").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            notificationList.clear();
                            load = false;
                            notificationList = response.body().getObject();
                            Log.e(TAG, "=========:size"+notificationList.size() );
                            if (notificationList.size() == 10) {
                                page = page+1;
                                load = true;
                            }
                            loadlist.addAll(response.body().getObject());
                            notificationAdapter.notifyDataSetChanged();

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            load = false;
                            if (response.body().getMessage().contains("No Data Exist.") && page == 0) {
                                ((ImageView)findViewById(R.id.notfound)).setVisibility(View.VISIBLE);
                            }
                            else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                           //
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
