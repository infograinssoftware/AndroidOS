package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;

public class DescriptionActivity extends BaseActivity{
    private static final String TAG = DescriptionActivity.class.getSimpleName();
    private Context context;
    private TextView toolbar_title,ctv_description;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        context=this;
        init();
    }

    public void init(){
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar_title=(TextView)findViewById(R.id.toolbar_title);
        ctv_description=(TextView)findViewById(R.id.ctv_description);

        Intent intent=getIntent();
        ctv_description.setText(intent.getStringExtra(Constants.DESCRIPTION));

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.description);

    }
}
