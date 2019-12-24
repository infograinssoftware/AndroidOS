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
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open_source.LiveFeeds.WrapContentLinearLayoutManager;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AwardJobAdapter;
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

public class SpJobAwartedFragment extends Fragment {
    private static final String TAG = SpJobAwartedFragment.class.getSimpleName();
    public static RecyclerView recyclerView;
    public static ArrayList<RetroObject> arrayList;
    public static AwardJobAdapter awardJobAdapter;
    Toolbar toolbar;
    ProgressHUD progressHUD;
    Context context;
    View rootView;
    TextView toolbar_title;


    public static void deletePos(int id) {
        arrayList.remove(id);
        if (awardJobAdapter != null) {
            awardJobAdapter.notifyDataSetChanged();
        }
    }

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
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.awarded_project);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        FunGetData();
    }

    private void FunGetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunSpAwarted(SharedPref.getSharedPreferences(context, Constants.TOKEN),"1").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            arrayList=response.body().getObject();
                            awardJobAdapter = new AwardJobAdapter(context, arrayList, "1","sp");
                            recyclerView.setAdapter(awardJobAdapter);
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

}
