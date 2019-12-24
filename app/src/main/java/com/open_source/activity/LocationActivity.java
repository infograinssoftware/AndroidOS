package com.open_source.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.LocationProvider;
import com.open_source.util.RuntimePermissionsActivity;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

public class LocationActivity extends RuntimePermissionsActivity implements View.OnClickListener, LocationProvider.LocationCallback {

    private static final String TAG = LocationActivity.class.getSimpleName();
    private static Runnable runnable;
    final int PERMISSION_REQUEST_CODE = 101;
    Context context;
    SwitchCompat stw_your_phone;
    RelativeLayout btn_next;
    boolean doubleBackToExitPressedOnce = false;
    Location mLastLocation;
    private LocationProvider mLocationProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
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
        stw_your_phone = (SwitchCompat) findViewById(R.id.stw_your_phone);
        if (hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            stw_your_phone.setChecked(true);
        }
        btn_next = (RelativeLayout) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);

        stw_your_phone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        requestAppPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                R.string.location_permission_needed,
                                PERMISSION_REQUEST_CODE);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (stw_your_phone.isChecked()) {
                    if (hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        requestAppPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                R.string.location_permission_needed,
                                PERMISSION_REQUEST_CODE);
                    }
                } else {
                    if (hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(new Intent(context, LoginActivity.class));
                    } else {
                        Utility.ShowToastMessage(context, R.string.location_permission_needed);
                    }
                }
                break;
        }
    }

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
//            System.exit(0);
        }

    }

    @Override
    public void handleNewLocation(Location location) {
        mLastLocation = location;
        Log.e(TAG, "handleNewLocation: " + location.getLatitude() + "" + location.getLongitude());
        SharedPref.setSharedPreference(context, Constants.LATITUDE, String.valueOf(location.getLatitude()));
        SharedPref.setSharedPreference(context, Constants.LONGITUDE, String.valueOf(location.getLongitude()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
    }
}
