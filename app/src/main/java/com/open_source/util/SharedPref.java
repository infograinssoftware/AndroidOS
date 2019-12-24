package com.open_source.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SharedPref {

    private static Context mContext;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    public static final String PREFERENCE = "Real_Estate";
    private static String LAN_PREFERENCE = "lan_pre";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public SharedPref(Context mContext) {
        this.mContext = mContext;
        pref=mContext.getSharedPreferences(PREFERENCE,PRIVATE_MODE);
        editor=pref.edit();

    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    public static void setSharedPreference(Context context, String name, String value) {
        mContext = context;
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        // editor.clear();
        editor.putString(name, value);
        editor.apply();
    }

    public static void setLanSharedPreference(Context context, String name, int value) {
        mContext = context;
        SharedPreferences settings = context.getSharedPreferences(LAN_PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        // editor.clear();
        Log.e("setSharedPreference: ", String.valueOf(value));
        editor.putInt(name, value);
        editor.apply();
    }

    public static void setSharedPreference(Context context, String name, int value) {
        mContext = context;
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        // editor.clear();
        Log.e("setSharedPreference: ", String.valueOf(value));
        editor.putInt(name, value);
        editor.apply();
    }

    public static int getLanSharedPreferences(Context context, String name, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(LAN_PREFERENCE, 0);
        return settings.getInt(name, defaultValue);
    }

    public static int getSharedPreferences(Context context, String name, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getInt(name, defaultValue);
    }

    public static String getSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }

    public static void removepreference(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        settings.edit().remove(name).apply();
    }

    public static void clearPreference(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        settings.edit().clear().apply();
    }
}