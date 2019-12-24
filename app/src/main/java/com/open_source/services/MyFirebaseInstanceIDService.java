package com.open_source.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyFirebaseInstanceIDService {
    public static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public void onTokenRefresh() {
        try {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            System.out.println("<<<<<<SerFirebaseToken " + refreshedToken);
            SharedPref.setSharedPreference(getApplicationContext(),Constants.FIREBASE_TOKEN,refreshedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}