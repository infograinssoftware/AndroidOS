package com.open_source.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;

public class DeepLinkingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deeplink);
        Intent mainIntent = getIntent();
        Uri data = mainIntent.getData();
        Log.e("========: ", data.toString());  //os://170
        String s[] = data.toString().split("//");
        startActivity(new Intent(DeepLinkingActivity.this, LocationInfoActivity.class).
                putExtra(Constants.PROPERTY_ID, s[1]));

    }
}
