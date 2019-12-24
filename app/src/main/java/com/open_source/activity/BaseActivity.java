package com.open_source.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import java.util.Locale;

public  class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getSharedPreferences(this, Constants.LANGUAGE)==null
                || SharedPref.getSharedPreferences(this, Constants.LANGUAGE).length()==0){
            SharedPref.setSharedPreference(this, Constants.LANGUAGE, Constants.LAN_ENGLISH);
        }
        changeLocale(this, SharedPref.getSharedPreferences(this, Constants.LANGUAGE));
    }

    public static void changeLocale(Context context, String locale) {
        Resources res = context.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = new Locale(locale);
        res.updateConfiguration(conf, res.getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
