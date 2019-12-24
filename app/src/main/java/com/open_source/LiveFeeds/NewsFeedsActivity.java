package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonParseException;
import com.open_source.R;
import com.open_source.activity.InboxListActivity;
import com.open_source.activity.InviteUserActivity;
import com.open_source.activity.LocationInfoActivity;
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

;


public class NewsFeedsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView feeds_recyclerView;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    NewsFeedAdapter feed_adapter;
    int i = 0, page = 0;
    Boolean load = true;
    ImageView img_overflow, img_cross, img_notification, img_follow;
    ArrayList<RetroObject> feeds_aaray = new ArrayList<>();
    ArrayList<RetroObject> runtime_feeds_aaray = new ArrayList<>();
    BottomSheetBehavior mBottomSheetBehavior2;
    String property_id = "", str_img, str_type = "", str_purpose = "", str_radius = "",lat="",longi="";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fresco.initialize(this);
        setContentView(R.layout.activity_news_feeds);
        context = NewsFeedsActivity.this;
        Log.e("=================: ", "onCreate");
        init();
    }

    private void init() {
        feeds_recyclerView = findViewById(R.id.feeds_recyclerView);
        feeds_recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        //feeds_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        img_notification = findViewById(R.id.img_notification);
        img_notification.setImageDrawable(getResources().getDrawable(R.drawable.filter));
        img_notification.setVisibility(View.VISIBLE);
        img_notification.setOnClickListener(this);
       /* img_follow=findViewById(R.id.img_follow);
        img_follow.setVisibility(View.VISIBLE);
        img_follow.setOnClickListener(this);*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View bottomSheet2 = findViewById(R.id.bottom_sheet2);
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        img_cross = findViewById(R.id.img_cross);
        img_cross.setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.lay_os)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.lay_social)).setOnClickListener(this);
        mBottomSheetBehavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        toolbar_title.setText(R.string.news_feeds);
        feed_adapter = new NewsFeedAdapter(context, feeds_aaray);
        feeds_recyclerView.setAdapter(feed_adapter);
        feeds_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 0 && load == true) {
                        load = false;
                        FunGetFeeds();
                    }
                }
            }
        });
        feeds_recyclerView.setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.notfound)).setVisibility(View.GONE);
        FunGetFeeds();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds, menu);
        return true;
    }


    private void FunGetFeeds() {
        if (Utility.isConnectingToInternet(context)) {
            mSwipeRefreshLayout.setRefreshing(true);
            RetrofitClient.getAPIService().getNewFeed(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    page + "", str_type, str_purpose, lat + "", longi + "", str_radius).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            load = false;
                            runtime_feeds_aaray = response.body().getObject();
                            feeds_aaray.addAll(runtime_feeds_aaray);
                            if (runtime_feeds_aaray.size() == 20) {
                                page = page + 1;
                                load = true;
                            }
                            runtime_feeds_aaray.clear();
                            feed_adapter.setItems(feeds_aaray);
                            feed_adapter.notifyDataSetChanged();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            mSwipeRefreshLayout.setRefreshing(false);
                            load = false;
                            feeds_aaray.clear();
                            feed_adapter.notifyDataSetChanged();
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            if (response.body().getMessage().equalsIgnoreCase("No property found") && page == 0) {
                                feeds_recyclerView.setVisibility(View.GONE);
                                ((ImageView) findViewById(R.id.notfound)).setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    Log.e("onFailure: ", t.getMessage());
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            });
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

 /*   @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
           /* case R.id.profile:
                startActivity(new Intent(context, MainActivity.class).putExtra(Constants.TYPE, "Feeds"));
                break;*/
            case R.id.saved_feed:
                startActivity(new Intent(context, SavedFeedActivity.class).putExtra(Constants.TYPE, "Search History"));
                break;
            case R.id.share_feeds:
                startActivity(new Intent(context, InboxListActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        feeds_aaray.clear();
        page = 0;
        FunGetFeeds();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_cross:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                break;
            case R.id.lay_os:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                startActivity(new Intent(context, InviteUserActivity.class).putExtra(Constants.PROPERTY_ID, property_id).putExtra(Constants.TYPE, "shared"));
                break;
            case R.id.lay_social:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                Log.e("=============: ", Constants.SHARE_URL + "/" + property_id);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, Constants.SHARE_URL + "/" + property_id);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_social)));
                break;
            case R.id.img_notification:
                page = 0;
                Intent i = new Intent(this, FeedFilterActivity.class);
                startActivityForResult(i, 1);
                break;
          /*  case R.id.img_follow:
                startActivity(new Intent(context,FolloReqActivity.class));
                break;*/
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                str_type = data.getStringExtra(Constants.TYPE);
                str_purpose = data.getStringExtra(Constants.PURPOSE);
                feeds_aaray.clear();
                feeds_recyclerView.setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.notfound)).setVisibility(View.GONE);
                if (!data.getStringExtra(Constants.RADIUS).isEmpty()) {
                    lat = SharedPref.getSharedPreferences(context, Constants.LATITUDE);
                    longi =SharedPref.getSharedPreferences(context, Constants.LONGITUDE);
                    str_radius = data.getStringExtra(Constants.RADIUS);
                } else {
                    lat = "";
                    longi ="";
                }
                FunGetFeeds();
            }
            if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder> {
        private final String TAG = NewsFeedAdapter.class.getSimpleName();
        Context context;
        List<RetroObject> feed_array;

        public NewsFeedAdapter(Context context, ArrayList<RetroObject> feeds_aaray) {
            this.context = context;
            feed_array = feeds_aaray;
        }

        @NonNull
        @Override
        public NewsFeedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NewsFeedAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_newsfeeds, parent, false));

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            try {
                if (feed_array.size() > 0) {
                    Glide.with(context).load(feed_array.get(position).getOwner_image()).into(holder.pro_owner_img);
                    holder.pro_owner_name.setText(feed_array.get(position).getFirst_name() + " " + feed_array.get(position).getLast_name());
                    holder.pro_type.setText(feed_array.get(position).getProperty_type());
                    if (feed_array.get(position).getCreated_at() != null && !feed_array.get(position).getCreated_at().isEmpty())
                        holder.txt_feeds_time.setText(Utility.parseDateToddMMyyyy(feed_array.get(position).getCreated_at()));
                    //Log.e(TAG, "===========: " + feed_array.get(position).getLike_status());
                    if (feed_array.get(position).getLike_status().equalsIgnoreCase("1"))
                        holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.heart_red));
                    else
                        holder.img_like.setImageDrawable(getResources().getDrawable(R.drawable.feed_like));
                    if (feed_array.get(position).getSave_status().equalsIgnoreCase("1"))
                        holder.img_save_post.setImageDrawable(getResources().getDrawable(R.drawable.savedfill));
                    else
                        holder.img_save_post.setImageDrawable(getResources().getDrawable(R.drawable.feed_save));
                    // holder.simpleDraweeView.setImageURI(feed_array.get(position).getProperty_img().get(0).getFile_name());
                    Glide.with(context).load(feed_array.get(position).getProperty_img().get(0).getFile_name()).
                            thumbnail(0.1f).
                            into(holder.pro_img);
                    holder.pro_address.setText(feed_array.get(position).getLocation());
                    if (feed_array.get(position).getPurpose().equalsIgnoreCase("Rent")) {
                        holder.img_sell.setVisibility(View.GONE);
                        holder.img_rent.setVisibility(View.VISIBLE);
                    } else {
                        holder.img_sell.setVisibility(View.VISIBLE);
                        holder.img_rent.setVisibility(View.GONE);
                    }
                    holder.txt_total_like.setText(String.valueOf(feed_array.get(position).getTotal_like()));
                    holder.txt_total_comments.setText(String.valueOf(feed_array.get(position).getTotal_comments()));
                    holder.lay_like.setOnClickListener(new OnMyClick(position));
                    holder.lay_comment.setOnClickListener(new OnMyClick(position));
                    holder.lay_share.setOnClickListener(new OnMyClick(position));
                    holder.img_save_post.setOnClickListener(new OnMyClick(position));
                    holder.lay_likes.setOnClickListener(new OnMyClick(position));
                    holder.rel_root.setOnClickListener(new OnMyClick(position));
                    holder.pro_owner_name.setOnClickListener(new OnMyClick(position));
                    String boldText = "@" + feed_array.get(position).getFirst_name() + " " + feed_array.get(position).getLast_name();
                    String normalText = feed_array.get(position).getDiscription();
                    SpannableString str = new SpannableString(boldText + " " + normalText);
                    str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (feed_array.get(position).getDiscription() != null) {
                        if (!feed_array.get(position).getDiscription().isEmpty()) {
                            holder.txt_caption.setText(str);
                            holder.txt_caption.setVisibility(View.VISIBLE);
                        }

                    } else {
                        holder.txt_caption.setVisibility(View.GONE);
                    }
                    if (feed_array.get(position).getLike_status().equalsIgnoreCase("1") && feed_array.get(position).getTotal_like() > 1) {
                        int value = feed_array.get(position).getTotal_like() - 1;
                        holder.txt_like_total_other.setText(getString(R.string.you) + "+" + +value + getString(R.string.more_like));
                        holder.lay_likes.setVisibility(View.VISIBLE);
                    } else if (feed_array.get(position).getLike_status().equalsIgnoreCase("0") && feed_array.get(position).getTotal_like() >= 1) {
                        holder.txt_like_total_other.setText(feed_array.get(position).getTotal_like() + getString(R.string.likes));
                        holder.lay_likes.setVisibility(View.VISIBLE);
                    } else if (feed_array.get(position).getLike_status().equalsIgnoreCase("1") && feed_array.get(position).getTotal_like() <= 1)
                        holder.lay_likes.setVisibility(View.GONE);
                }


            } catch (JsonParseException exp) {

            }
        }

        @Override
        public int getItemCount() {
            return feed_array.size();
        }

        public void setItems(List<RetroObject> persons) {
            this.feed_array = persons;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView pro_owner_img;
            TextView pro_owner_name, pro_type, txt_feeds_time, pro_address, txt_total_like, txt_total_comments, txt_caption, txt_like_total_other;
            ImageView pro_img, img_save_post, img_like;
            LinearLayout lay_like, lay_comment, lay_share, lay_likes;
            RelativeLayout img_rent, img_sell, rel_root;
            //SimpleDraweeView simpleDraweeView;

            public MyViewHolder(View itemView) {
                super(itemView);
                pro_owner_img = itemView.findViewById(R.id.pro_owner_img);
                pro_owner_img = itemView.findViewById(R.id.pro_owner_img);
                pro_owner_name = itemView.findViewById(R.id.pro_owner_name);
                pro_type = itemView.findViewById(R.id.pro_type);
                txt_feeds_time = itemView.findViewById(R.id.txt_feeds_time);
                pro_img = itemView.findViewById(R.id.pro_img);
                pro_address = itemView.findViewById(R.id.pro_address);
                img_save_post = itemView.findViewById(R.id.img_save_post);
                lay_like = itemView.findViewById(R.id.lay_like);
                lay_comment = itemView.findViewById(R.id.lay_comment);
                lay_like = itemView.findViewById(R.id.lay_like);
                lay_share = itemView.findViewById(R.id.lay_share);
                img_rent = itemView.findViewById(R.id.img_rent);
                img_sell = itemView.findViewById(R.id.img_sell);
                img_like = itemView.findViewById(R.id.img_like);
                txt_total_like = itemView.findViewById(R.id.txt_total_like);
                txt_total_comments = itemView.findViewById(R.id.txt_total_comments);
                txt_caption = itemView.findViewById(R.id.txt_caption);
                txt_like_total_other = itemView.findViewById(R.id.txt_like_total_other);
                lay_likes = itemView.findViewById(R.id.lay_likes);
                rel_root = itemView.findViewById(R.id.rel_root);
                //simpleDraweeView=itemView.findViewById(R.id.my_image_view);

            }
        }

        private class OnMyClick implements View.OnClickListener {
            int position;

            public OnMyClick(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.lay_like:
                        if (Utility.isConnectingToInternet(context)) {
                            String str_status = "0";
                            if (feed_array.get(position).getLike_status().equalsIgnoreCase("1"))
                                str_status = "0";
                            else
                                str_status = "1";
                            RetrofitClient.getAPIService().Feed_Likes(SharedPref.getSharedPreferences(context, Constants.TOKEN), feed_array.get(position).getProperty_id(), str_status).enqueue(new Callback<RetrofitUserData>() {
                                @Override
                                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                    try {
                                        if (response.body().getStatus() == 200) {
                                            feed_array.get(position).setLike_status(response.body().getUserData().getLike_status());
                                            if (response.body().getUserData().getLike_status().equalsIgnoreCase("1")) {
                                                feed_array.get(position).setTotal_like(feed_array.get(position).getTotal_like() + 1);
                                            } else if (response.body().getUserData().getLike_status().equalsIgnoreCase("0")) {
                                                feed_array.get(position).setTotal_like(feed_array.get(position).getTotal_like() - 1);

                                            }
                                            feed_adapter.setItems(feeds_aaray);
                                            feed_adapter.notifyDataSetChanged();

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

                        break;
                    case R.id.lay_comment:
                        if (feed_array.size() > 0) {
                            context.startActivity(new Intent(context, FeedCommentsActivity.class).putExtra(Constants.PROPERTY_ID, feed_array.get(position).getProperty_id()));
                        }
                        break;

                    case R.id.img_save_post:
                        if (Utility.isConnectingToInternet(context)) {
                            String str_status = "0";
                            if (feed_array.get(position).getSave_status().equalsIgnoreCase("1"))
                                str_status = "0";
                            else
                                str_status = "1";
                            RetrofitClient.getAPIService().Feed_SAVED(SharedPref.getSharedPreferences(context, Constants.TOKEN), feed_array.get(position).getProperty_id(), str_status).enqueue(new Callback<RetrofitUserData>() {
                                @Override
                                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                    try {
                                        if (response.body().getStatus() == 200) {
                                            feed_array.get(position).setSave_status(response.body().getUserData().getSave_status());
                                            feed_adapter.setItems(feeds_aaray);
                                            feed_adapter.notifyDataSetChanged();

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

                        break;
                    case R.id.lay_likes:
                        if (feed_array.size() > 0) {
                            startActivity(new Intent(context, FeedLikeActivity.class).putExtra(Constants.PROPERTY_ID, feed_array.get(position).getProperty_id()));
                        }
                        break;
                    case R.id.rel_root:
                        if (feed_array.size() > 0) {
                            context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, feed_array.get(position).getProperty_id()));
                        }
                        break;

                    case R.id.pro_owner_name:
                        if (feed_array.size() > 0) {
                            context.startActivity(new Intent(context, OtherUserProfile.class).
                                    putExtra(Constants.USER_ID, feed_array.get(position).getUserid()).putExtra(Constants.NAME, feeds_aaray.get(position).getFirst_name() + " " + feed_array.get(position).getLast_name()));
                        }
                        break;

                    case R.id.lay_share:
                        if (feed_array.size() > 0) {
                            property_id = feed_array.get(position).getProperty_id();
                            str_img = feed_array.get(position).getProperty_img().get(0).getFile_name();
                            if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                                mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                            } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                                mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        }
                        break;
                }
            }
        }
    }

}
