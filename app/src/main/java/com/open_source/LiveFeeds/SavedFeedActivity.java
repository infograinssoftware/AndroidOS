package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.PostListAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserList;
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

public class SavedFeedActivity extends AppCompatActivity {
    private static final String TAG = SavedFeedActivity.class.getSimpleName();
    Context context;
    List<UserList> savedSearchesList = new ArrayList<>();
    ArrayList<RetroObject> ObjectList = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recyclerView;
    TextView toolbar_title;
    int page = 0;
    ProgressHUD progressHUD;
    String img;
    Boolean load = true;
    String type = "";
    private PostListAdapter postListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context=SavedFeedActivity.this;
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.saved_feeds);
        type=getIntent().getExtras().getString(Constants.TYPE,"");
        postListAdapter = new PostListAdapter(context, savedSearchesList, type);
        recyclerView.setAdapter(postListAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 0 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        GetData();
                    }
                }
            }
        });
        GetData();
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetSavedFeed(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ObjectList.clear();
                            load = false;
                            ObjectList = response.body().getObject();
                            for (int i = 0; i < ObjectList.size(); i++) {
                                if (ObjectList.get(i).getProperty_img().size() != 0) {
                                    img = ObjectList.get(i).getProperty_img().get(0).getFile_name();
                                } else {
                                    img = "";
                                }
                                savedSearchesList.add(new UserList(ObjectList.get(i).getId(), img, ObjectList.get(i).getType(), ObjectList.get(i).getLocation(), ""));
                            }
                            if (ObjectList.size() == 10) {
                                page = page + 1;
                                load = true;
                            }
                            postListAdapter.notifyDataSetChanged();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            load = false;
                            if (response.body().getMessage().contains("No Data Exist.") && page == 0) {
                                ((ImageView) findViewById(R.id.notfound)).setVisibility(View.VISIBLE);
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

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

}
