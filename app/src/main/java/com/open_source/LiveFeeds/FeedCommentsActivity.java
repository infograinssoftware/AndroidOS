package com.open_source.LiveFeeds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.SQLiteHelper.SpeedyLinearLayoutManager;
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

public class FeedCommentsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = FeedCommentsActivity.class.getSimpleName();
    public static boolean isAppRunning;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    RelativeLayout lay_send;
    EditText cet_chatbox;
    int i = 0, page = 0;
    Boolean load = true;
    String property_id = "";
    FeedCommentAdapter feedCommentAdapter;
    RecyclerView recyclerView_comments;
    ArrayList<RetroObject> array_comment = new ArrayList<>();
    ArrayList<RetroObject> runtime_array_comment = new ArrayList<>();
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feed_comment);
        context = FeedCommentsActivity.this;
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
        toolbar_title.setText(getString(R.string.comments));
        recyclerView_comments = findViewById(R.id.recyclerView);
        cet_chatbox = findViewById(R.id.chatbox);
        lay_send = findViewById(R.id.lay_send);
        lay_send.setOnClickListener(this);
        recyclerView_comments.setLayoutManager(new SpeedyLinearLayoutManager(context, SpeedyLinearLayoutManager.VERTICAL, false));
        feedCommentAdapter = new FeedCommentAdapter(context, array_comment);
        recyclerView_comments.setAdapter(feedCommentAdapter);

        recyclerView_comments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 0 && load == true) {
                        load = false;
                        FunGetComment();
                    }
                }
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    property_id = getIntent().getExtras().getString(Constants.PROPERTY_ID, "");
                    array_comment.clear();
                    FunGetComment();
                    Log.e(TAG, "=======" + "broadcastReceiver");

                }
            }
        };
    }

    private void FunGetComment() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().getCommentAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            load = false;
                            runtime_array_comment = response.body().getObject();
                            array_comment.addAll(runtime_array_comment);
                            if (runtime_array_comment.size() == 200) {
                                page = page + 1;
                                load = true;
                            }
                            runtime_array_comment.clear();
                            feedCommentAdapter.setItems(array_comment);
                            feedCommentAdapter.notifyDataSetChanged();
                            recyclerView_comments.smoothScrollToPosition(array_comment.size());

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            //Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {

                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_send:
                if (!cet_chatbox.getText().toString().trim().isEmpty())
                    FunComment();
                break;
        }
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


    private void FunComment() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().PostComment(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id, "text", cet_chatbox.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            array_comment.clear();
                            FunGetComment();
                            cet_chatbox.setText("");
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

    @Override
    protected void onResume() {
        super.onResume();
        FunGetComment();
        isAppRunning = true;
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppRunning = true;
    }

    @Override
    protected void onPause() {
        isAppRunning = false;
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }

    public class FeedCommentAdapter extends RecyclerView.Adapter<FeedCommentAdapter.MyViewHolder> {
        Context context;
        List<RetroObject> comment_list;

        public FeedCommentAdapter(Context context, List<RetroObject> comment_list) {
            this.context = context;
            this.comment_list = comment_list;
        }

        public void setItems(List<RetroObject> persons) {
            this.comment_list = persons;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if (comment_list.size() > 0) {
                try {
                    Glide.with(context).load(comment_list.get(position).getProfileImage()).into(holder.commentor_img);
                    String boldText = "@" + comment_list.get(position).getName() + " ";
                    String normalText = comment_list.get(position).getMessage();
                    SpannableString str = new SpannableString(boldText + normalText);
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.txt_comment.setText(str);
                    holder.txt_datetime.setText(comment_list.get(position).getCreated_at());
                } catch (ArrayIndexOutOfBoundsException exp) {

                }
            }


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
