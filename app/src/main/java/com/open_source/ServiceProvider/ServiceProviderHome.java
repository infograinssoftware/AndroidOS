package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.open_source.R;
import com.open_source.activity.BottomNavigationViewHelper;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

;


public class ServiceProviderHome extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = ServiceProviderHome.class.getSimpleName();
    private static Runnable runnable;
    Context context;
    BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    Animation mEnterAnimation, mExitAnimation;
    ChainTourGuide mTourGuideHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_home);
        context = this;
        init();
        //  Utility.GetInboxData(context);
    }

    public void init() {
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);
        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.KEY)) {
            if (getIntent().getExtras().getString(Constants.KEY).equals("17") || (getIntent().getExtras().getString(Constants.KEY).equals("20")))
                bottomNavigationView.setSelectedItemId(R.id.my_account);
            else if (getIntent().getExtras().getString(Constants.KEY).equals("sp"))
                bottomNavigationView.setSelectedItemId(R.id.navigation);
        } else {
            bottomNavigationView.setSelectedItemId(R.id.job);
        }


    }

    public void ShowHint(MyWorkFragement fragment1) {
        mEnterAnimation = new AlphaAnimation(0f, 1f);
        mEnterAnimation.setDuration(600);
        mEnterAnimation.setFillAfter(true);
        mExitAnimation = new AlphaAnimation(1f, 0f);
        mExitAnimation.setDuration(600);
        mExitAnimation.setFillAfter(true);
        runOverlayListener_ContinueMethod(fragment1);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_work:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyWorkFragement()).commit();
                break;

            case R.id.history:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, SpCompleteJobFragement.newInstance(getString(R.string.history))).commit();
                break;

            case R.id.navigation:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SPNotificationFragement()).commit();
                break;

            case R.id.job:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new ServiceJobList()).commit();
                break;

            case R.id.my_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyProfileFragement()).commit();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = new MyProfileFragement();
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
            System.exit(0);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AddWork.work_status == 1) {
            bottomNavigationView.setSelectedItemId(R.id.my_work);
            /*MyWorkFragement fragment = (MyWorkFragement) getFragmentManager().findFragmentById(R.id.container);
            fragment.FunMyWork();*/

        }

    }

    public void runOverlayListener_ContinueMethod(MyWorkFragement fragment1) {
        SharedPref.setSharedPreference(context, Constants.SHOWCASE, "show");
        ChainTourGuide tourGuide1 = ChainTourGuide.init(this)
                .setToolTip(new ToolTip()
                        .setDescription(getString(R.string.level_add_work))
                        .setBackgroundColor(Color.parseColor("#E36A4B"))
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
                .playLater(fragment1.img_add);
        Sequence sequence = new Sequence.SequenceBuilder()
                .add(tourGuide1, tourGuide1)
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
}
