package com.open_source.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.widget.TextView;

import com.open_source.R;
import com.open_source.adapter.ViewPagerAdapter;
import com.open_source.fragment.ManageDocFragment;
import com.open_source.fragment.UploadDocFragment;
import com.open_source.retrofitPack.Constants;

import open_source.com.dachshundtablayout.DachshundTabLayout;
import open_source.com.dachshundtablayout.indicator.LineMoveIndicator;

public class DocumnetTabActivity extends BaseActivity {
    private static final String TAG = DocumnetTabActivity.class.getSimpleName();
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    private ViewPager viewPager;
    private DachshundTabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_make_offer);
        context = this;
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        viewPager = findViewById(R.id.viewPager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.buy_property_list);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setAnimatedIndicator(new LineMoveIndicator(tabLayout));
        tabLayout.setupWithViewPager(viewPager);

    }


    private void setupViewPager(ViewPager viewPager) {
        UploadDocFragment uploadDocFragment = new UploadDocFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID));
        bundle.putString(Constants.BUYER_ID, getIntent().getExtras().getString(Constants.BUYER_ID));
        uploadDocFragment.setArguments(bundle);


        ManageDocFragment manageDocFragment = new ManageDocFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID));
        bundle.putString(Constants.BUYER_ID, getIntent().getExtras().getString(Constants.BUYER_ID));
        manageDocFragment.setArguments(bundle1);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        adapter.addFragment(uploadDocFragment, getString(R.string.upload_doc));
        adapter.addFragment(manageDocFragment, getString(R.string.manage_doc));
        viewPager.setAdapter(adapter);
    }
}
