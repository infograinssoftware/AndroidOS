package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.adapter.ItemDecorationAlbumColumns;
import com.open_source.adapter.ListingAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingActivity extends BaseActivity {
    private static final String TAG = ListingActivity.class.getSimpleName();
    Context context;
    RecyclerView recyclerView;
    ListingAdapter listingAdapter;
    List<RetroObject> listingList = new ArrayList<>();
    ProgressHUD progressHUD;
    Bundle bundle;
    Toolbar h_toolbar;
    TextView toolbar_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        context = this;
        init();
    }

    public void init() {
        h_toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(h_toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = (TextView)h_toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.filter_list);
       // toolbar_title.setCompoundDrawables(getResources().getDrawable(R.drawable.serach_black), null, null, null);
        //bundle = getIntent().getExtras();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        GetData();
    }

    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }

    //------------------- Converting dp to pixel -----------------------
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void GetData(){
        if(Utility.isConnectingToInternet(context)){
            listingList.clear();
            String str_bathroom,str_badroom;
            progressHUD= ProgressHUD.show(context,true,false,null);
            if (getIntent().getExtras().getString(Constants.BATHROOM,"").contains("Bathrooms"))
                str_bathroom = getIntent().getExtras().getString(Constants.BATHROOM,"").replace("Bathrooms", "");
            else
                str_bathroom = getIntent().getExtras().getString(Constants.BATHROOM,"").replace("Bathroom", "");
            if ( getIntent().getExtras().getString(Constants.BADROOM,"").contains("Bedrooms"))
                str_badroom =  getIntent().getExtras().getString(Constants.BADROOM,"").replace("Bedrooms", "");
            else
                str_badroom =  getIntent().getExtras().getString(Constants.BADROOM,"").replace("Bedroom", "");
           /* Log.e("==", SharedPref.getSharedPreferences(context, Constants.TOKEN));
            Log.e("==",  getIntent().getExtras().getString(Constants.TYPE,""));
            Log.e("==",  getIntent().getExtras().getString(Constants.BADROOM,""));
            Log.e("==", getIntent().getExtras().getString(Constants.BATHROOM,""));
            Log.e("==", getIntent().getExtras().getString(Constants.PURPOSE,""));
            Log.e("==", getIntent().getExtras().getString(Constants.POST,""));
            Log.e("==", getIntent().getExtras().getString(Constants.LOCATION_LATITUDE,""));
            Log.e("==", getIntent().getExtras().getString(Constants.LOCATION_LONGITUDE,""));*/
            RetrofitClient.getAPIService().getSellFilter(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PURPOSE,""),
                    getIntent().getExtras().getString(Constants.TYPE,""),
                    str_badroom, str_bathroom,
                    getIntent().getExtras().getString(Constants.LOCATION_LATITUDE,""),
                    getIntent().getExtras().getString(Constants.LOCATION_LONGITUDE,""),
                    getIntent().getExtras().getString(Constants.POST,""), getIntent().getExtras().getString(Constants.RENTER_TYPE,""),
                    getIntent().getExtras().getString(Constants.PROPERTY_FOR,""),
                    getIntent().getExtras().getString(Constants.MIN_BUDGET,""),
                    getIntent().getExtras().getString(Constants.MAX_BUDGET,""),
                    getIntent().getExtras().getString(Constants.RADIUS,"")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if(progressHUD!=null&&progressHUD.isShowing()){
                        progressHUD.dismiss();
                    }

                    try{
                        if(response.body().getStatus()==200){
                           // Utility.ShowToastMessage(context,response.body().getMessage());
                            listingList=response.body().getObject();
                            listingAdapter = new ListingAdapter(context, listingList);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(2, dpToPx(2), true));
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(listingAdapter);
                            //finish();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context,response.body().getMessage());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if(progressHUD!=null&&progressHUD.isShowing()){
                        progressHUD.dismiss();
                    }
                    Log.e(TAG, "onFailure: "+t.toString() );
                }
            });
        }else {
            listingList.clear();
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }
}
