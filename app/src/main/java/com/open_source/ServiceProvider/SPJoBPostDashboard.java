package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.open_source.R;
import com.open_source.activity.BottomNavigationViewHelper;
import com.open_source.retrofitPack.Constants;

public class SPJoBPostDashboard extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = SPJoBPostDashboard.class.getSimpleName();
    Context context;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_job_post_dashboard);
        context = this;
        init();
    }


    public void init() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.KEY)) {
            if (getIntent().getExtras().getString(Constants.KEY).equals("15"))
                bottomNavigationView.setSelectedItemId(R.id.awarded);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.bids);
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bids:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpJobBidFragment()).commit();
                break;

            case R.id.awarded:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpJobAwartedFragment()).commit();
                break;

            case R.id.running:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpRunningJobFragement()).commit();
                break;

            case R.id.completed:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SpCompleteJobFragement.newInstance(getString(R.string.completed_project))).commit();
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = new SpJobBidFragment();
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //-------------------------- Back Pressed -----------------------------
    /*@Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }*/

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
        finish();
        super.onBackPressed();
        /*Handler handler = new Handler();
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
            System.exit(0);
        }*/

    }

}
