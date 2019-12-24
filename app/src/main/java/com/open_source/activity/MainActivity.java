package com.open_source.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.open_source.R;
import com.open_source.fragment.BuyFragment;
import com.open_source.fragment.GuestYouFragment;
import com.open_source.fragment.MapFragment;
import com.open_source.fragment.RentMapFragement;
import com.open_source.fragment.SellPropertyFragment;
import com.open_source.fragment.YouFragement;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.LocationProvider;
import com.open_source.util.RuntimePermissionsActivity;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class MainActivity extends RuntimePermissionsActivity implements BottomNavigationView.OnNavigationItemSelectedListener, LocationProvider.LocationCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static BottomNavigationView bottomNavigationView;
    private static Runnable runnable;
    final int PERMISSION_REQUEST_CODE = 101;
    Context context;
    boolean doubleBackToExitPressedOnce = false;
    Animation mEnterAnimation, mExitAnimation;
    ChainTourGuide mTourGuideHandler;
    Location mLastLocation;
    private LocationProvider mLocationProvider;
    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        if (hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mLocationProvider = new LocationProvider(this, this);
            if (mLocationProvider != null)
                mLocationProvider.connect();
        } else {
            requestAppPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    R.string.location_permission_needed,
                    PERMISSION_REQUEST_CODE);
        }
        init();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            mLocationProvider = new LocationProvider(this, this);
            if (mLocationProvider != null)
                mLocationProvider.connect();
        }
    }

    public void init() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (!SharedPref.getSharedPreferences(context, Constants.ADDRESS).isEmpty()) {
//            bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        } else {
            SharedPref.setSharedPreference(context, Constants.LATITUDE, "31.9685988");
            SharedPref.setSharedPreference(context, Constants.LONGITUDE, "-99.9018131");
            SharedPref.setSharedPreference(context, Constants.ADDRESS, "");
//            bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        }
            bottomNavigationView.setSelectedItemId(R.id.navigation_buy);
    }

    public void ShowHint(MapFragment fragment1) {
        mEnterAnimation = new AlphaAnimation(0f, 0.1f);
        mEnterAnimation.setDuration(10);
        mEnterAnimation.setFillAfter(true);
        mExitAnimation = new AlphaAnimation(0.1f, 0f);
        mExitAnimation.setDuration(10);
        mExitAnimation.setFillAfter(true);
        runOverlayListener_ContinueMethod(fragment1);
    }

    public void ShowHint_rent(RentMapFragement fragment1) {
        mEnterAnimation = new AlphaAnimation(0f, 0.1f);
        mEnterAnimation.setDuration(10);
        mEnterAnimation.setFillAfter(true);
        mExitAnimation = new AlphaAnimation(0.1f, 0f);
        mExitAnimation.setDuration(10);
        mExitAnimation.setFillAfter(true);
        runOverlayListener_ContinueMethod_Rent(fragment1);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_buy:
                changeFragment( new BuyFragment());
                break;

            case R.id.navigation_sell:
                changeFragment( SellPropertyFragment.newInstance(""));
                break;

            case R.id.navigation_map:
                changeFragment( new MapFragment());
                break;

            case R.id.navigation_rent:
                changeFragment( new RentMapFragement());
                break;

            case R.id.navigation_you:
                if (!SharedPref.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
                    changeFragment( new GuestYouFragment());
                } else {
                    changeFragment(new YouFragement());
                }
                Utility.GetInboxData(context);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    public void changeFragment(Fragment fragment) {
        this.fragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onBackPressed() {
        Handler handler = new Handler();
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.backpress), Toast.LENGTH_SHORT).show();
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            handler.removeCallbacks(runnable);
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLocationProvider != null)
            mLocationProvider.connect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public void runOverlayListener_ContinueMethod(final MapFragment fragment1) {
        SharedPref.setSharedPreference(context, Constants.SHOWCASE, "show");
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.serach_property))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Rectangle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.lay_search);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.view_more_option))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.fab);
      /*  ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.property_news_feed))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })
                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.icon_live_feeds);
        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.service_provider))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.icon_hammer);*/

        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2)
                .setDefaultOverlay(new Overlay()

                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(getActivity(), "default Overlay clicked", Toast.LENGTH_SHORT).show();
                                mTourGuideHandler.next();
                            }
                        })
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
                .build();
        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);

    }


    public void runOverlayListener_ContinueMethod_Rent(RentMapFragement fragment1) {
        SharedPref.setSharedPreference(context, Constants.SHOWCASE_RENT, "show");
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.search_rent_property))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Rectangle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.lay_search);

        ChainTourGuide tourGuide2 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.rent_property_list))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.ctv_rent);


        ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.view_more_option))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.fab);

        /*ChainTourGuide tourGuide3 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.add_rent_property))
                        .setBackgroundColor(Color.parseColor("#C69924"))
                        .setGravity(Gravity.BOTTOM)
                )
                .setOverlay(new Overlay()
                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setBackgroundColor(Color.parseColor("#99000000"))
                        .setStyle(Overlay.Style.Circle)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTourGuideHandler.next();
                            }
                        })

                )
                // note that there is no Overlay here, so the default one will be used
                .playLater(fragment1.icon_rent);
*/
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide2, tourGuide3)
                .setDefaultOverlay(new Overlay()

                        .setEnterAnimation(mEnterAnimation)
                        .setExitAnimation(mExitAnimation)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Toast.makeText(getActivity(), "default Overlay clicked", Toast.LENGTH_SHORT).show();
                                mTourGuideHandler.next();
                            }
                        })
                )
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.OverlayListener)
                .build();
        mTourGuideHandler = ChainTourGuide.init(this).playInSequence(sequence);
    }

    @Override
    public void handleNewLocation(Location location) {
        mLastLocation = location;
        Log.e(TAG, "handleNewLocation: " + location.getLatitude() + "" + location.getLongitude());
        SharedPref.setSharedPreference(context, Constants.LATITUDE, String.valueOf(location.getLatitude()));
        SharedPref.setSharedPreference(context, Constants.LONGITUDE, String.valueOf(location.getLongitude()));
        Utility.GetAddress(context, location.getLatitude(), location.getLongitude());
        //bottomNavigationView.setSelectedItemId(R.id.navigation_map);
    }
}
