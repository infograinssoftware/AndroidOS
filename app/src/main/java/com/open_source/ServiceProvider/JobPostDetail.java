package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.VideoPlay;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.ImageAdapter;
import com.open_source.adapter.ServiceRattingAdapter;
import com.open_source.modal.PropertyImg;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.MyVideoView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class JobPostDetail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = JobPostDetail.class.getSimpleName();
    Toolbar toolbar;
    Context context;
    ProgressHUD progressHUD;
    CircleImageView user_img;
    RecyclerView recycle_bid_user, recycle_problem_img;
    UserData userData;
    RatingBar rattingbar;
    MyVideoView video_thumb;
    FrameLayout layvideoframe;
    String videolink = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_job_detail);
        context = JobPostDetail.this;
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
        ((Button) findViewById(R.id.btn_bid)).setOnClickListener(this);
        recycle_bid_user = findViewById(R.id.recycle_bid_user);
        recycle_problem_img = findViewById(R.id.recycle_problem_img);
        user_img = findViewById(R.id.user_img);
        rattingbar = findViewById(R.id.rattingbar);
        video_thumb = findViewById(R.id.video_thumb);
        layvideoframe=findViewById(R.id.layvideoframe);
        layvideoframe.setOnClickListener(this);
        if (getIntent().getExtras().getString(Constants.KEY).equals("my_bid")) {
            ((Button) findViewById(R.id.btn_bid)).setVisibility(View.GONE);
        }
        FunGetData();
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
                            Glide.with(context).load(userData.getProfileImage()).into(user_img);
                            String name = userData.getFirst_name() + " " + userData.getLast_name();
                            ((TextView) findViewById(R.id.user_name)).setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                            ((TextView) findViewById(R.id.description)).setText(userData.getDescription());
                            if (!userData.getService_date().isEmpty())
                                ((TextView) findViewById(R.id.txt_date)).setText(userData.getService_date());
                            rattingbar.setRating(Float.parseFloat(userData.getClient_rating()));
                            ((TextView) findViewById(R.id.txt_toal_rate)).setText(userData.getTotal_client_rate());

                            ((TextView) findViewById(R.id.budget)).setText("$" + userData.getMin_budget() + "- $" + userData.getMax_budget());
//                            ((TextView) findViewById(R.id.budget)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + userData.getMin_budget() + SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+ " " + userData.getMax_budget());

                            ((TextView) findViewById(R.id.location)).setText(userData.getLocation());
                            if (!userData.getVideo_url().isEmpty()) {
                                videolink = userData.getVideo_url();
                                ((TextView) findViewById(R.id.level_video)).setVisibility(View.VISIBLE);
                                layvideoframe.setVisibility(View.VISIBLE);
                                video_thumb.setVideoPath(userData.getVideo_url());
                                video_thumb.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mediaPlayer) {
                                       mediaPlayer.setLooping(true);
                                        mediaPlayer.start();
                                    }
                                });
                                if (!userData.getVideo_thumbnail().isEmpty()) {
                                    // Glide.with(context).load(userData.getVideo_thumbnail()).into(video_thumb);
                                }
                            }

                            if (userData.getBid_status() == 1)
                            ((Button) findViewById(R.id.btn_bid)).setVisibility(View.GONE);
                            ArrayList<PropertyImg> arrayList = userData.getProperty_img();

                            if (arrayList.size() > 0) {
                                ((TextView) findViewById(R.id.level_image)).setVisibility(View.VISIBLE);
                                recycle_problem_img.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                ImageAdapter imageAdapter = new ImageAdapter(context, arrayList);
                                recycle_problem_img.setAdapter(imageAdapter);
                            }

                            ArrayList<RetroObject> arrayList_bid = userData.getObject();
                            if (arrayList_bid.size() > 0) {
                                recycle_bid_user.setLayoutManager(new LinearLayoutManager(context));
                                ServiceRattingAdapter serviceRattingAdapter = new ServiceRattingAdapter(context, arrayList_bid, "jobpost");
                                recycle_bid_user.setAdapter(serviceRattingAdapter);
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
            case R.id.btn_bid:
                startActivity(new Intent(context, PostBidActivity.class).putExtra(Constants.ID, userData.getPost_id()));
                break;
            case R.id.layvideoframe:
                startActivity(new Intent(context, VideoPlay.class).putExtra(Constants.VideoLink, videolink));
                break;

        }
    }
}
