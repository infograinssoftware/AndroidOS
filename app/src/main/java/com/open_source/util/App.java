package com.open_source.util;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.request.target.ViewTarget;
import com.open_source.R;

public class App extends Application {

    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        ViewTarget.setTagId(R.id.glide_tag);
    }
}
