package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
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

/**
 * Created by and-02 on 16/6/18.
 */

public class InviteUserActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = InviteUserActivity.class.getSimpleName();
    ProgressHUD progressHUD;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView btnsearch;
    EditText et_search;
    ArrayList<RetroObject> ObjectList = new ArrayList<>();
    ArrayList<UserList> userList = new ArrayList<>();
    String strkey = "", str_user = "";
    int i = 0, page = 0;
    RelativeLayout click_invite;
    Boolean load = true;
    private Context context;
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        context = InviteUserActivity.this;
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.h_toolbar);
        toolbar_title = findViewById(R.id.toolbar_title);
        recyclerView = findViewById(R.id.recyclerView_user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.invite);
        btnsearch = findViewById(R.id.btnsearch);
        et_search = findViewById(R.id.et_search);
        click_invite = toolbar.findViewById(R.id.click_invite);
        click_invite.setOnClickListener(this);
        btnsearch.setOnClickListener(this);
        userListAdapter = new UserListAdapter(context, userList);
        recyclerView.setAdapter(userListAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page!=0 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        GetUser();
                    }
                    // GetUser();
                    //Toast.makeText(context,"LAst",Toast.LENGTH_LONG).show();
                }
            }
        });
        // recyclerView.setOnLoadMoreListener(this);

        GetUser();
    }

    private void GetUser() {
        if (Utility.isConnectingToInternet(context)) {
            Log.e(TAG, "=====GetUser");
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().USERLIST(SharedPref.getSharedPreferences(context, Constants.TOKEN), et_search.getText().toString(), page + "","").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                        // recyclerView.onLoadMoreComplete();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ObjectList.clear();
                            load = false;
                            ObjectList = response.body().getObject();
                           /* if (page == 0) {
                                userList.clear();
                            }*/
                            for (int i = 0; i < ObjectList.size(); i++) {
                                String img = ObjectList.get(i).getUrl() + "" + ObjectList.get(i).getProfileImage();
                                userList.add(new UserList(ObjectList.get(i).getUserid(), img,
                                        ObjectList.get(i).getEmail(), ObjectList.get(i).getFirst_name(), ObjectList.get(i).getLast_name(),""));
                            }
                            if (ObjectList.size() == 10) {
                                page = page+1;
                                load = true;
                            }
                            userListAdapter.notifyDataSetChanged();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            load = false;
                            Utility.ShowToastMessage(context, response.body().getMessage());

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
                    Utility.ShowToastMessage(context, getString(R.string.msg_server_failed));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsearch:
                userList.clear();
                page = 0;
                if (et_search.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_user_name));
                } else {
                    strkey = et_search.getText().toString();
                    GetUser();
                }
                break;
            case R.id.click_invite:
                str_user = "";
                if (UserListAdapter.box1.size() > 0) {
                    str_user = android.text.TextUtils.join(",", UserListAdapter.box1);
                    Log.e(TAG, "======: " +str_user );
                    ServerFunInvite();
                } else {
                    Utility.ShowToastMessage(context, getString(R.string.msg_invite_error));
                }
                break;
        }

    }

    private void ServerFunInvite() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().BID_INVITATION(SharedPref.getSharedPreferences(context, Constants.TOKEN), getIntent().getExtras().getString(Constants.PROPERTY_ID, ""), str_user,getIntent().getExtras().getString(Constants.TYPE,"")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                        //recyclerView.onLoadMoreComplete();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            //startActivity(new Intent(context,MainActivity.class));
                            finish();
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
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    Utility.ShowToastMessage(context, getString(R.string.msg_server_failed));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }


    }

    @Override
    public void onBackPressed() {
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

    public static class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
        public static ArrayList<String> box1 = new ArrayList<>();
        Context context;
        List<UserList> postListList;
        private String TAG = UserListAdapter.class.getSimpleName();


        public UserListAdapter(Context context, List<UserList> userList) {
            this.context = context;
            postListList = userList;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_invite, parent, false));
        }

        @Override
        public void onBindViewHolder(final UserListAdapter.MyViewHolder viewHolder, final int position) {
            viewHolder.ctv_title.setText(postListList.get(position).getFname() + " " + postListList.get(position).getLname());
            viewHolder.ctv_sub_title.setText(postListList.get(position).getEmail());
            if (!postListList.get(position).getImg().isEmpty()) {
                Glide.with(context).load(postListList.get(position).getImg()).into(viewHolder.item_image);
            }
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        box1.add(postListList.get(position).getId());
                    } else {
                        box1.remove(postListList.get(position).getId());

                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return postListList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView item_image;
            TextView ctv_title, ctv_sub_title;
            RelativeLayout relview;
            CheckBox checkbox;

            public MyViewHolder(View convertView) {
                super(convertView);
                item_image = convertView.findViewById(R.id.item_image);
                ctv_title = convertView.findViewById(R.id.ctv_title);
                ctv_sub_title = convertView.findViewById(R.id.ctv_sub_title);
                relview = convertView.findViewById(R.id.relview);
                checkbox = convertView.findViewById(R.id.checkbox);

            }


        }
    }

   /* @Override
    public void onLoadMore() {
        page += 1;
        GetUser();
    }*/

  /*  public static class UserListAdapter extends BaseAdapter {
        Context context;
        List<UserList> postListList;
        private String TAG = UserListAdapter.class.getSimpleName();
        private LayoutInflater inflater;
        public static ArrayList<String> box1 = new ArrayList<>();

        public UserListAdapter(Context context, List<UserList> userList) {
            this.context = context;
            postListList = userList;
        }

        @Override
        public int getCount() {
            return postListList.size();
        }

        @Override
        public Object getItem(int position) {
            return postListList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return postListList.indexOf(getItem(position));
        }

       *//* @Override
        public int getViewTypeCount() {
            return getCount();
        }*//*

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(R.layout.row_invite, null);
                viewHolder.item_image = convertView.findViewById(R.id.item_image);
                viewHolder.ctv_title = convertView.findViewById(R.id.ctv_title);
                viewHolder.ctv_sub_title = convertView.findViewById(R.id.ctv_sub_title);
                viewHolder.relview = convertView.findViewById(R.id.relview);
                viewHolder.checkbox=convertView.findViewById(R.id.checkbox);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.ctv_title.setText(postListList.get(position).getFname() + " "+postListList.get(position).getLname());
            viewHolder.ctv_sub_title.setText(postListList.get(position).getEmail());
            if (!postListList.get(position).getImg().isEmpty()) {
                Glide.with(context).load(postListList.get(position).getImg()).into(viewHolder.item_image);
            }
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        box1.add(postListList.get(position).getId());
                    } else {
                        box1.remove(postListList.get(position).getId());

                    }
                }
            });
            return convertView;
        }

        private class ViewHolder {
            ImageView item_image;
            TextView ctv_title, ctv_sub_title;
            RelativeLayout relview;
            CheckBox checkbox;
        }
    }*/
}
