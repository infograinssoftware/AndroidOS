package com.open_source.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.crashlytics.android.Crashlytics;
import com.open_source.R;
import com.open_source.ServiceProvider.ServiceProviderHome;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    Context context;
    private AlertDialog alertDialog;
    private TextView titleSelectLanguage, txvEnglish, txvOther, buttonSubmitLanguage;
    private String language = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        context = this;
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                showChangeLanguageDialogue(context);
            }
        }, secondsDelayed * 9000);  //9000
    }

    private void tokenHandler(){
        if (SharedPref.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
            if (SharedPref.getSharedPreferences(context, Constants.USER_TYPE).equalsIgnoreCase("4")) {
                startActivity(new Intent(context, ServiceProviderHome.class));
                finish();
            } else {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

        } else {
            startActivity(new Intent(context, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    private void showChangeLanguageDialogue(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View promptView = inflater.inflate(R.layout.dialog_change_language, null);
        titleSelectLanguage = promptView.findViewById(R.id.titleSelectLanguage);
        txvEnglish = promptView.findViewById(R.id.txvEnglish);
        txvOther= promptView.findViewById(R.id.txvOther);
        buttonSubmitLanguage= promptView.findViewById(R.id.buttonSubmitLanguage);

        txvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = Constants.LAN_ENGLISH;
                changeBackground(txvEnglish, txvOther);

            }
        });

        txvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                language = "es";
                changeBackground(txvOther, txvEnglish);
            }
        });

        buttonSubmitLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (language.length()!= 0){
                    SharedPref.setSharedPreference(context, Constants.LANGUAGE, language);
                    alertDialog.dismiss();
                    tokenHandler();
                }
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        alertDialog.show();
//        alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tokenHandler();
            }
        });
    }

    public void changeBackground(TextView selected, TextView otherOne){
        selected.setBackground(context.getResources().getDrawable(R.drawable.border_gray_back_primary));
        selected.setTextColor(context.getResources().getColor(R.color.white));
        otherOne.setBackground(context.getResources().getDrawable(R.drawable.border_gray));
        otherOne.setTextColor(context.getResources().getColor(R.color.black));
        buttonSubmitLanguage.setBackground(context.getResources().getDrawable(R.drawable.border_gray_back_primary));
        buttonSubmitLanguage.setTextColor(context.getResources().getColor(R.color.white));
    }

}
