package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.MyPropertyAdapter;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.modal.UserList;
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


public class OtherUserProfile extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = OtherUserProfile.class.getSimpleName();
    Context context;
    ArrayList<UserList> sellList = new ArrayList<>();
    Toolbar toolbar;
    ProgressHUD progressHUD;
    RecyclerView recyclerView;
    CircleImageView profile_img;
    Button btn_follow;
    String img;
    LinearLayout txt_following, txt_followers;
    UserData userData;
    private MyPropertyAdapter myPropertyAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed_user_profile);
        context = this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) findViewById(R.id.toolbar_title)).setText(getIntent().getExtras().getString(Constants.NAME));
        recyclerView = (RecyclerView) findViewById(R.id.property_grid);

        profile_img = findViewById(R.id.profile_img);
        btn_follow = findViewById(R.id.btn_follow);
        txt_following = findViewById(R.id.lay_following);
        txt_following.setOnClickListener(this);
        txt_followers = findViewById(R.id.lay_followers);
        txt_followers.setOnClickListener(this);
        btn_follow.setOnClickListener(this);
        if (SharedPref.getSharedPreferences(context, Constants.USER_ID).equals(getIntent().getExtras().getString(Constants.USER_ID)))
            btn_follow.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        GetData();

    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().ViewUserProfile(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.USER_ID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            Glide.with(context).load(userData.getUserinfo().getUrl() + userData.getUserinfo().getProfileImage()).into(profile_img);
                            ((TextView) findViewById(R.id.ctv_name)).setText(userData.getUserinfo().getFirst_name() + " " + userData.getUserinfo().getLast_name());
                            ((TextView) findViewById(R.id.ctv_email)).setText(userData.getUserinfo().getEmail());
                            ((TextView) findViewById(R.id.txt_post)).setText(userData.getUserinfo().getProperty_count());
                            ((TextView) findViewById(R.id.txt_following)).setText(String.valueOf(userData.getFollowing().size()));
                            ((TextView) findViewById(R.id.txt_followers)).setText(String.valueOf(userData.getFollowers().size()));

                            // 1 = pending, 2 = confirm, 3 = reject
                            if (userData.getObject().get(0).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.TOKEN))) {
                                SetAdapter();
                            } else {
                                if (userData.getUserinfo().getProfile_status().equals("public")) {
                                    SetAdapter();
                                    if (userData.getUserinfo().getFollower_status().equals("1") || userData.getUserinfo().getFollower_status().equals("2")) {
                                        btn_follow.setText(getString(R.string.message));
                                       // btn_follow.setEnabled(false);
                                    }
                                } else {
                                    if (userData.getUserinfo().getFollower_status().equals("0")) {

                                    } else if (userData.getUserinfo().getFollower_status().equals("1")) {
                                        btn_follow.setText(getString(R.string.following));
                                        btn_follow.setBackgroundResource(R.drawable.round_button_fad);
                                        btn_follow.setEnabled(false);

                                    } else if (userData.getUserinfo().getFollower_status().equals("2")) {
                                        SetAdapter();
                                        btn_follow.setText(getString(R.string.message));
                                        //btn_follow.setEnabled(false);

                                    }

                                }
                            }

                            //  ((TextView)findViewById(R.id.txt_property)).setText(userData.getUserinfo().getProperty_count());
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
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

    private void SetAdapter() {
        for (int i = 0; i < userData.getObject().size(); i++) {
            if (userData.getObject().get(i).getProperty_img().size() != 0) {
                img = userData.getObject().get(i).getProperty_img().get(0).getFile_name();
            } else {
                img = "";
            }
            sellList.add(new UserList(userData.getObject().get(i).getProperty_id(), img, userData.getObject().get(i).getType(), userData.getObject().get(i).getLocation(), userData.getObject().get(i).getPurpose()));
        }
        myPropertyAdapter = new MyPropertyAdapter(context, sellList, "other");
        recyclerView.setAdapter(myPropertyAdapter);
    }

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
            case R.id.btn_follow:
                if (btn_follow.getText().equals(getString(R.string.message)))
                startActivity(new Intent(context, ChatActivity.class).putExtra(Constants.PROPERTY_ID, userData.getObject().get(0).getProperty_id()).
                        putExtra(Constants.USER_ID, getIntent().getExtras().getString(Constants.USER_ID)).putExtra(Constants.TONAME, userData.getUserinfo().getFirst_name() + " " + userData.getUserinfo().getLast_name()).
                        putExtra(Constants.CHAT_STATUS, "1").
                        putExtra(Constants.PROFILE_IMAGE, userData.getUserinfo().getUrl() + userData.getUserinfo().getProfileImage()));
                else
                Fun_Follow();
                break;
            case R.id.lay_followers:
                startActivity(new Intent(context, FolloReqActivity.class).putExtra(Constants.FOLLOWERS,userData.getFollowers()).putExtra(Constants.TYPE,Constants.FOLLOWERS));
                break;
            case R.id.lay_following:
                startActivity(new Intent(context, FolloReqActivity.class).putExtra(Constants.FOLLOWERS,userData.getFollowing()).putExtra(Constants.TYPE,Constants.FOLLOWING));
                break;
        }
    }

    private void Fun_Follow() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FollowReq(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.USER_ID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            btn_follow.setText(getString(R.string.following));
                            btn_follow.setBackgroundResource(R.drawable.round_button_fad);

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
