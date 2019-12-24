package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.CircleImageView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedLikeActivity extends AppCompatActivity {
    private static final String TAG = FeedLikeActivity.class.getSimpleName();
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    String property_id = "";
    int i = 0, page = 0;
    Boolean load = true;
    RecyclerView recyclerView_likes;
    FeedLikeAdapter feedLikeAdapter;
    ArrayList<RetroObject> array_like = new ArrayList<>();
    ArrayList<RetroObject> runtime_array_like = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feed_likes);
        context = FeedLikeActivity.this;
        init();
    }

    private void init() {
        property_id = getIntent().getExtras().getString(Constants.PROPERTY_ID, "");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.all_likes);
        recyclerView_likes = findViewById(R.id.recyclerView);
        recyclerView_likes.setLayoutManager(new LinearLayoutManager(this));
        feedLikeAdapter = new FeedLikeAdapter(context, array_like);
        recyclerView_likes.setAdapter(feedLikeAdapter);
        recyclerView_likes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 0 && load == true) {
                        load = false;
                        FunGetLike();
                    }
                }
            }
        });
        FunGetLike();
    }

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

    private void FunGetLike() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().getLikeAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            load = false;
                            runtime_array_like = response.body().getObject();
                            array_like.addAll(runtime_array_like);
                            if (runtime_array_like.size() == 20) {
                                page = page + 1;
                                load = true;
                            }
                            runtime_array_like.clear();
                            feedLikeAdapter.setItems(array_like);
                            feedLikeAdapter.notifyDataSetChanged();

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }


    public class FeedLikeAdapter extends RecyclerView.Adapter<FeedLikeAdapter.MyViewHolder> {
        Context context;
        List<RetroObject> comment_list;

        public FeedLikeAdapter(Context context, List<RetroObject> comment_list) {
            this.context = context;
            this.comment_list = comment_list;
        }

        public void setItems(List<RetroObject> persons) {
            this.comment_list = persons;
        }

        @NonNull
        @Override
        public FeedLikeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FeedLikeAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            Glide.with(context).load(comment_list.get(position).getProfileImage()).into(holder.commentor_img);
            holder.txt_comment.setText(comment_list.get(position).getName());
            holder.txt_datetime.setText(comment_list.get(position).getCreated_at());
        }

        @Override
        public int getItemCount() {
            return comment_list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView commentor_img;
            TextView txt_comment, txt_datetime;

            public MyViewHolder(View itemView) {
                super(itemView);
                commentor_img = itemView.findViewById(R.id.commentor_img);
                txt_comment = itemView.findViewById(R.id.txt_comment);
                txt_datetime = itemView.findViewById(R.id.txt_datetime);
            }
        }
    }
}
