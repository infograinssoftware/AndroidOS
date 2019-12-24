package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.DividerItemDecoration;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FolloReqActivity extends AppCompatActivity {
    Context context;
    RecyclerView recyclerView_follow;
    ProgressHUD progressHUD;
    ArrayList<RetroObject> arrayList;
    String status = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follow_following_activity);
        context = this;
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        recyclerView_follow = findViewById(R.id.recycle_follow);
        recyclerView_follow.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_follow.addItemDecoration(new DividerItemDecoration(context));
        if (getIntent().hasExtra(Constants.FOLLOWERS)) {

            toolbar_title.setText(getIntent().getStringExtra(Constants.TYPE));
            status = "follow";
            arrayList = (ArrayList<RetroObject>) getIntent().getSerializableExtra(Constants.FOLLOWERS);
            // Log.e( "init: ",String.valueOf(arrayList.size()));
            FollowAdapter followAdapter = new FollowAdapter(context, arrayList, status);
            recyclerView_follow.setAdapter(followAdapter);

        } else {
            toolbar_title.setText(R.string.follow_request);
            status = "request";
            Fun_GetReqFollow();
        }


    }

    private void Fun_GetReqFollow() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetReqFollow(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> retro_obj = response.body().getObject();
                            FollowAdapter followAdapter = new FollowAdapter(context, retro_obj, status);
                            recyclerView_follow.setAdapter(followAdapter);

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
