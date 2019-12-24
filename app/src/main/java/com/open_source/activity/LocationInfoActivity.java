package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.open_source.R;
import com.open_source.adapter.ViewPagerAdapter;
import com.open_source.fragment.AboutFragment;
import com.open_source.fragment.AuctionDetailsFragment;
import com.open_source.fragment.ContactFragment;
import com.open_source.fragment.MakeOfferFragment;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import open_source.com.dachshundtablayout.DachshundTabLayout;
import open_source.com.dachshundtablayout.indicator.LineMoveIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationInfoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = LocationInfoActivity.class.getSimpleName();
    Context context;
    ImageView item_image, close, like;
    TextView ctv_location_name, ctv_pro_type;
    DachshundTabLayout tabLayout;
    ViewPager viewPager;
    UserData userData;
    ProgressHUD progressHUD;
    String property_id = "", type = "", rentpay = "", user_id = "", status = "0", offer_status = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);
        context = this;
        init();
    }

    public void init() {
        item_image = (ImageView) findViewById(R.id.item_image);
        close = (ImageView) findViewById(R.id.close);
        like = (ImageView) findViewById(R.id.like);
        ctv_location_name = (TextView) findViewById(R.id.ctv_location_name);
        ctv_pro_type = (TextView) findViewById(R.id.ctv_pro_type);
        viewPager = findViewById(R.id.viewPager);
        property_id = getIntent().getExtras().getString(Constants.PROPERTY_ID);
        rentpay = getIntent().getExtras().getString(Constants.RENTAL_PAYMENT, "");
        callPropertyDetailsByIdAPI();
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                /*if(tab.getPosition()==1){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    DialogFragment newFragment = MakeOfferFragment.newInstance(property_id);
                    newFragment.show(ft, "Make Offer");
                }else if(tab.getPosition()==2){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    DialogFragment newFragment = ContactFragment.newInstance("dialog");
                    newFragment.show(ft, "Contact");
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        /*try {
            jsondata = new JSONObject(getIntent().getStringExtra(Constants.OBJECT));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        close.setOnClickListener(this);
        like.setOnClickListener(this);
    }

    private void setupViewPagerAuction(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AboutFragment.newInstance(property_id, "", ""), "About");
        adapter.addFragment(ContactFragment.newInstance(property_id, "", "", userData.getPost()), "Contact");
        adapter.addFragment(AuctionDetailsFragment.newInstance(property_id), "Auction Details");
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(-1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 2) {
                    viewPager.setCurrentItem(2, false);
                    return true;
                }
                return true;
            }
        });
    }

    private void setupViewPagerFixed(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AboutFragment.newInstance(property_id, type, rentpay), "About");
        adapter.addFragment(MakeOfferFragment.newInstance(property_id, user_id, status, offer_status), "Make Offer");
        adapter.addFragment(ContactFragment.newInstance(property_id, type, rentpay, userData.getPost()), "Contact");
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(-1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 2) {
                    viewPager.setCurrentItem(2, false);
                    return true;
                }
                return true;
            }
        });
    }

    private void setupViewPagerRent(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AboutFragment.newInstance(property_id, type, rentpay), "About");
        adapter.addFragment(ContactFragment.newInstance(property_id, type, rentpay, userData.getPost()), "Contact");
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(-1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                }
                return true;
            }
        });
    }


    private void setupViewPager(final ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AboutFragment.newInstance(property_id, "", rentpay), "About");
        adapter.addFragment(MakeOfferFragment.newInstance(property_id, user_id, status, offer_status), "Make Offer");
        adapter.addFragment(ContactFragment.newInstance(property_id, "", rentpay, userData.getPost()), "Contact");
        adapter.addFragment(AuctionDetailsFragment.newInstance(property_id), "Auction Details");
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(-1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (viewPager.getCurrentItem() == 2) {
                    viewPager.setCurrentItem(2, false);
                    return true;
                } /*else if (viewPager.getCurrentItem() == 3) {
                    viewPager.setCurrentItem(3, false);
                    return true;
                }*/
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                finish();
                break;

            case R.id.like:
                callAddFavouriteAPI();
                break;
        }
    }

    //---------------------- Select favorite ------------------------
    public void callAddFavouriteAPI() {
        progressHUD = ProgressHUD.show(context, true, false, null);
        RetrofitClient.getAPIService().getAddFavouriteAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_id).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                try {
                    if (response.body().getStatus() == 200) {
                        Utility.ShowToastMessage(context, response.body().getMessage());
                        UserData userData = response.body().getUserData();
                        if (userData.getFavourite_status().equals("0")) {
                            like.setImageResource(R.drawable.heart);
                        } else {
                            like.setImageResource(R.drawable.heart_red);
                        }
                    /*    if (isPressed) {
                            like.setImageResource(R.drawable.heart);
                        } else {
                            like.setImageResource(R.drawable.heart_red);
                        }
                        isPressed = !isPressed;*/
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
    }

    public void callPropertyDetailsByIdAPI() {
        RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_id).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                try {
                    if (response.body().getStatus() == 200) {
                        userData = response.body().getUserData();
                        if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                            status = "1";
                        }
                        offer_status = userData.getOffer_status();
                        ctv_location_name.setText(userData.getLocation());
                        if (userData.getProperty_img().size() > 0)
                            Glide.with(context).asDrawable().load(userData.getProperty_img().get(0).getFile_name()).into(item_image);
                        type = userData.getProperty_for();
                        user_id = userData.getUser_id();
                        if (userData.getFavourite_status().equals("0")) {
                            like.setImageResource(R.drawable.heart);
                        } else {
                            like.setImageResource(R.drawable.heart_red);
                        }
                        if (type.equalsIgnoreCase("2") || type.equalsIgnoreCase("3")) {
                            type = "Rent";
                            setupViewPagerRent(viewPager);
                        } else {
                            if (userData.getPost().equalsIgnoreCase("Auction")) {
                                setupViewPagerAuction(viewPager);
                            } else if (userData.getPost().equalsIgnoreCase("Fixed") || userData.getPost().equalsIgnoreCase("Fixed Price")) {
                                setupViewPagerFixed(viewPager);
                            } else if (userData.getPost().equalsIgnoreCase("Both")) {
                                setupViewPager(viewPager);
                            } else {
                                setupViewPager(viewPager);
                            }
                        }
                        ctv_pro_type.setText(userData.getType());
                        tabLayout.setAnimatedIndicator(new LineMoveIndicator(tabLayout));
                        tabLayout.setupWithViewPager(viewPager);

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
}
