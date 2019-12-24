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
import com.open_source.adapter.CatogeryAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyWorkFragement extends Fragment implements View.OnClickListener {
    private static final String TAG = MyWorkFragement.class.getSimpleName();
    public static Boolean flag = false;
    View rootView;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView img_add, nodata;
    ProgressHUD progressHUD;
    RecyclerView recyclerView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_list, container, false);
        context = getActivity();
        init_view();
        return rootView;
    }

    private void init_view() {
        Log.e(TAG, "===============: " + "init_view");
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.my_work));
        img_add = rootView.findViewById(R.id.img_add);
        nodata = rootView.findViewById(R.id.nodata);
        img_add.setVisibility(View.VISIBLE);
        img_add.setBackgroundResource(R.drawable.add_catogery);
        img_add.setOnClickListener(this);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Log.e(TAG, "===============: "+ SharedPref.getSharedPreferences(context,Constants.SHOWCASE));
        if (!SharedPref.getSharedPreferences(context, Constants.SHOWCASE).equals("show")) {
            ((ServiceProviderHome) context).ShowHint(this);
        }
        FunMyWork();
    }

    public void FunMyWork() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetMyWorkList(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> array_list = response.body().getObject();
                            CatogeryAdapter catogeryAdapter = new CatogeryAdapter(context, array_list, "my_work");
                            recyclerView.setAdapter(catogeryAdapter);

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add:
                startActivity(new Intent(context, AddWork.class));
               /* if (flag == false) {
                    // Log.e(TAG, "=======: "+"click" );
                    flag = true;
                    callProfileAPI();
                }*/
                break;
        }
    }


}
