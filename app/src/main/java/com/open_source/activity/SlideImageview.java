package com.open_source.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.open_source.R;
import com.open_source.adapter.PagerAdpter;
import com.open_source.retrofitPack.Constants;
import java.util.ArrayList;


public class SlideImageview extends BaseActivity {
    ViewPager viewPager;
    Context context;
    ArrayList<String> sliderList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_image);
        context=SlideImageview.this;
        viewPager=findViewById(R.id.upcoming_pager);
        //viewPager.setOnPageChangeListener(context);
        sliderList = getIntent().getStringArrayListExtra(Constants.IMAGE);
        PagerAdpter pagerAdpter = new PagerAdpter(context, sliderList);
        viewPager.setAdapter(pagerAdpter);

    }
}
