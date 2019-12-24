package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.NotificationAdapter;
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

public class SPNotificationFragement extends Fragment {
    private static final String TAG = SPNotificationFragement.class.getSimpleName();
    View rootView;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    int i = 0, page = 0;
    Boolean load = true;
    ProgressHUD progressHUD;
    NotificationAdapter notificationAdapter;
    RecyclerView recyclerView;
    List<RetroObject> notificationList = new ArrayList<>();
    List<RetroObject> loadlist = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();
        init_view();
        return rootView;
    }

    private void init_view() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.notification));
        //((ImageView) rootView.findViewById(R.id.notfound)).setVisibility(View.VISIBLE);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        notificationAdapter = new NotificationAdapter(context, loadlist);
        recyclerView.setAdapter(notificationAdapter);
         FunGetData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (page != 0 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        FunGetData();
                    }

                }
            }
        });
    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            //Log.e(TAG, "=========:page"+page);
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Notification_List(SharedPref.getSharedPreferences(context, Constants.TOKEN), page + "").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            notificationList.clear();
                            load = false;
                            notificationList = response.body().getObject();
                            Log.e(TAG, "=========:size" + notificationList.size());
                            if (notificationList.size() == 10) {
                                page = page + 1;
                                load = true;
                            }
                            loadlist.addAll(response.body().getObject());
                            notificationAdapter.notifyDataSetChanged();

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else {
                            load = false;
                            if (response.body().getMessage().contains("No Data Exist.") && page == 0) {
                                ((ImageView) rootView.findViewById(R.id.notfound)).setVisibility(View.VISIBLE);
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                            //
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    public class SPNotificationAdapter extends RecyclerView.Adapter<SPNotificationAdapter.MyViewHolder> {
        private final String TAG = SPNotificationAdapter.class.getSimpleName();
        List<RetroObject> arrayList;
        Context context;

        public SPNotificationAdapter(Context context, List<RetroObject> loadlist) {
            this.arrayList = loadlist;
            this.context = context;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_search_list, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}
