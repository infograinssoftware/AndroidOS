package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.open_source.R;
import com.open_source.activity.BottomNavigationViewHelper;
import com.open_source.retrofitPack.Constants;


public class MyPostDashboard extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MyPostDashboard.class.getSimpleName();
    Context context;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_post);
        context = this;
        init();
    }

    public void init() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.KEY)) {
             if (getIntent().getExtras().getString(Constants.KEY).equals("16"))
                bottomNavigationView.setSelectedItemId(R.id.running);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.my_post);
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_post:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new UserMyPost()).commit();
                break;

            case R.id.awarded:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new UserMyAwardPost()).commit();
                break;

            case R.id.running:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new UserMyRunningPost()).commit();
                break;

            case R.id.completed:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,new UserMyCompletePost()).commit();
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = new UserMyPost();
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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
