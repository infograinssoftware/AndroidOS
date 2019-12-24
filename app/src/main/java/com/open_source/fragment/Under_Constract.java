package com.open_source.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Under_Constract extends Fragment {
    private static final String TAG = Under_Constract.class.getSimpleName();
    ProgressHUD progressHUD;
    List<UserList> makeOrderList = new ArrayList<>();
    ArrayList<RetroObject> ObjectList = new ArrayList<>();
    String img;
    View rootView;
    Boolean load = true;
    private Context context;
    private PostListAdapter postListAdapter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_contract, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    private void init() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        FunGetList();
    }

    private void FunGetList() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetPropertyList(SharedPref.getSharedPreferences(context, Constants.TOKEN), "2").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getStatus() == 200) {
                                ObjectList.clear();
                                ObjectList = response.body().getObject();
                                for (int i = 0; i < ObjectList.size(); i++) {
                                    if (ObjectList.get(i).getProperty_img().size() != 0) {
                                        img = ObjectList.get(i).getProperty_img().get(0).getFile_name();
                                    } else {
                                        img = "";
                                    }
                                    makeOrderList.add(new UserList(ObjectList.get(i).getId(), img, ObjectList.get(i).getType(), ObjectList.get(i).getLocation(),""));

                                }
                                postListAdapter = new PostListAdapter(context, makeOrderList, "under_cont");
                                recyclerView.setAdapter(postListAdapter);
                            }

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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
}
