package com.open_source.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;
import android.widget.TextView;

import open_source.com.dachshundtablayout.DachshundTabLayout;
import open_source.com.dachshundtablayout.indicator.LineMoveIndicator;

import com.open_source.R;
import com.open_source.adapter.ViewPagerAdapter;
import com.open_source.fragment.PurchedFragement;
import com.open_source.fragment.Under_Constract;

public class PostMakeOfferListActivity extends BaseActivity {
    private static final String TAG = PostMakeOfferListActivity.class.getSimpleName();
    private ViewPager viewPager;
    private DachshundTabLayout tabLayout;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_make_offer);
        context = this;
        init();
    }

     public void init() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar_title=(TextView)findViewById(R.id.toolbar_title);
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
       /* 1=sold,2=under contract*/
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Under_Constract(), getString(R.string.under_contract));
        adapter.addFragment(new PurchedFragement(), getString(R.string.purched_property));
        viewPager.setAdapter(adapter);
    }

    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        finish();
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

    //--------------------------- Status Bar ------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }
}
