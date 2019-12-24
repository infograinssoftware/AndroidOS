package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nex3z.flowlayout.FlowLayout;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.SlidingImageAdapter;
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

public class UserSPWorkDetail extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    FlowLayout flowLayout;
    Context context;
    LinearLayout viewPagerCountDots, layout_ratting, lay_service;
    SlidingImageAdapter slidingImage_adapter;
    ArrayList<String> sliderList = new ArrayList<>();
    ProgressHUD progressHUD;
    ViewPager upcoming_pager;
    Toolbar toolbar;
    TextView toolbar_title, txt_apply;
    String user_id, service_id, userid;
    private int dotsCount;
    private ImageView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sp_detail);
        context = UserSPWorkDetail.this;
        initview();
    }

    private void initview() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txt_apply = findViewById(R.id.txt_apply);
        txt_apply.setOnClickListener(this);
        lay_service = findViewById(R.id.lay_service);
        flowLayout = findViewById(R.id.flowlayout);
        ((TextView) findViewById(R.id.txt_more)).setOnClickListener(this);
        viewPagerCountDots = findViewById(R.id.viewPagerCountDots);
        upcoming_pager = findViewById(R.id.upcoming_pager);
        upcoming_pager.setOnPageChangeListener(this);
        layout_ratting = findViewById(R.id.layout_ratting);
        userid = getIntent().getExtras().getString(Constants.USER_ID, "");
        service_id = getIntent().getExtras().getString(Constants.SERVICE_ID, "");
        toolbar_title.setText(getIntent().getExtras().getString(Constants.NAME, ""));
        GetData();
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            sliderList.clear();
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SPDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN), userid, service_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            user_id = userData.getUser_id();
                            ArrayList<RateReview> arrayList_rate = userData.getRate_review();
                            for (int i = 0; i < userData.getProperty_img().size(); i++) {
                                sliderList.add(userData.getProperty_img().get(i).getFile_name());
                            }
                            slidingImage_adapter = new SlidingImageAdapter(context, sliderList,"");
                            upcoming_pager.setAdapter(slidingImage_adapter);
                            setUiPageViewController();
                            ((TextView) findViewById(R.id.txt_desc)).setText(userData.getWork_description());
                            ((TextView) findViewById(R.id.txt_working_hour)).setText(userData.getWorking_hours());
                            String days[] = userData.getWorking_days().split(",");
                            String sab_cat_name[] = userData.getSub_cat_name().split(",");
                            String sab_cat_price[] = userData.getSub_cat_price().split(",");


                            for (int i = 0; i < days.length; i++) {
                                View myLayout = getLayoutInflater().inflate(R.layout.my_layout, flowLayout, false);
                                TextView txt_days = myLayout.findViewById(R.id.txt);
                                TextView txt_first = myLayout.findViewById(R.id.txt_first);
                                txt_days.setText(days[i]);
                                String str = days[i].substring(0, 1);
                                txt_first.setText(str);
                                flowLayout.addView(myLayout);
                            }

                            for (int i = 0; i < sab_cat_name.length; i++) {
                                View myLayout = getLayoutInflater().inflate(R.layout.my_service, lay_service, false);
                                TextView txt_service = myLayout.findViewById(R.id.txt_service);
                                TextView txt_price = myLayout.findViewById(R.id.txt_price);
                                txt_service.setText(sab_cat_name[i]);

                                txt_price.setText("$" + sab_cat_price[i]);
//                                txt_price.setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + sab_cat_price[i]);

                                lay_service.addView(myLayout);
                            }

                            if (arrayList_rate.size() > 0) {
                                ((TextView) findViewById(R.id.lay_ratting)).setVisibility(View.VISIBLE);
                                if (arrayList_rate.size() > 2) {
                                    ((TextView) findViewById(R.id.txt_more)).setVisibility(View.VISIBLE);
                                    for (int i = 0; i < 2; i++) {
                                        View myLayout = getLayoutInflater().inflate(R.layout.layout_ratting_review, layout_ratting, false);
                                        RatingBar ratingBar = myLayout.findViewById(R.id.rattingbar);
                                        ((TextView) myLayout.findViewById(R.id.name)).setText(arrayList_rate.get(i).getName());
                                        ((TextView) myLayout.findViewById(R.id.desc)).setText(arrayList_rate.get(i).getComment());
                                        CircleImageView img = myLayout.findViewById(R.id.user_img);
                                        ((RatingBar) myLayout.findViewById(R.id.rattingbar)).setRating(Float.parseFloat(arrayList_rate.get(i).getRate()));
                                        Glide.with(context).load(arrayList_rate.get(i).getProfileImage()).into(img);
                                        layout_ratting.addView(myLayout);
                                    }
                                } else {
                                    ((TextView) findViewById(R.id.txt_more)).setVisibility(View.GONE);
                                    for (int i = 0; i < arrayList_rate.size(); i++) {
                                        View myLayout = getLayoutInflater().inflate(R.layout.layout_ratting_review, layout_ratting, false);
                                        RatingBar ratingBar = myLayout.findViewById(R.id.rattingbar);
                                        ((TextView) myLayout.findViewById(R.id.name)).setText(arrayList_rate.get(i).getName());
                                        ((TextView) myLayout.findViewById(R.id.desc)).setText(arrayList_rate.get(i).getComment());
                                        CircleImageView img = myLayout.findViewById(R.id.user_img);
                                        ((RatingBar) myLayout.findViewById(R.id.rattingbar)).setRating(Float.parseFloat(arrayList_rate.get(i).getRate()));
                                        Glide.with(context).load(arrayList_rate.get(i).getProfileImage()).into(img);
                                        layout_ratting.addView(myLayout);
                                    }
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

                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }


    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void setUiPageViewController() {
        dotsCount = slidingImage_adapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            viewPagerCountDots.addView(dots[i], layoutParams);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_apply:
                startActivity(new Intent(context, UserApply.class).putExtra(Constants.USER_ID, user_id));
                break;
            case R.id.txt_more:
                startActivity(new Intent(context, RateReviewList.class).putExtra(Constants.USER_ID, userid).putExtra(Constants.SERVICE_ID, service_id));
                break;
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
