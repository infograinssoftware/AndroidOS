package com.open_source.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open_source.Interface.rediuslick;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.ItemDecorationAlbumColumns;
import com.open_source.adapter.ListingAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserList;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendedFragment extends Fragment implements rediuslick {

    private static final String TAG = RecommendedFragment.class.getSimpleName();
    static String latitudes = "0.0", logitudes = "0.0";
    Context context;
    View rootView;
    RecyclerView recyclerView;
    String radius="";
    List<RetroObject> listingList = new ArrayList<>();
    ProgressHUD progressHUD;
    ListingAdapter listingAdapter;
    TextView txvNoDataFound;


    public static RecommendedFragment newInstance(String latitude, String logitude) {
        RecommendedFragment frag = new RecommendedFragment();
        latitudes = latitude;
        logitudes = logitude;
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_discover, container, false);
        context = getActivity();
        radius="";
        init();
        return rootView;
    }

    public void init() {
        txvNoDataFound = rootView.findViewById(R.id.txvNoDataFound);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        listingAdapter = new ListingAdapter(context, listingList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(2, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (latitudes.equals("0.0") && logitudes.equals("0.0")) {
            latitudes = SharedPref.getSharedPreferences(context, Constants.LATITUDE);
            logitudes = SharedPref.getSharedPreferences(context, Constants.LONGITUDE);
        } else {
            Log.e(TAG, "recomended latitudes: " + latitudes + " logitudes: " + logitudes);
        }
        //callPropertySearchAPI();

    }

    //------------------------ Property List --------------------
    public void callPropertySearchAPI() {
        UserList userList = new UserList();
        progressHUD = ProgressHUD.show(context, true, false, null);
        RetrofitClient.getAPIService().getPropertySearchAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                latitudes,
                logitudes,
                "recomended", this.radius).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }

                try {
                    if (response.body().getStatus() == 200) {
//                        Utility.ShowToastMessage(context,response.body().getMessage());
                        listingList = response.body().getObject();
                        if (listingList.size()!=0) {
                            setPropertyListAdapter();
                            txvNoDataFound.setVisibility(View.GONE);

                        } else {
                            txvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    } else {
                        txvNoDataFound.setVisibility(View.VISIBLE);
                        // Utility.ShowToastMessage(context,response.body().getMessage());
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        } else {
            callPropertySearchAPI();
            // recyclerView.setAdapter(listingAdapter);
            Log.e(TAG, "=========: " + "onResume");
        }

        //INSERT CUSTOM CODE HERE
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        Log.e(TAG, "=========: " + "setUserVisibleHint");
        if (visible && isResumed()) {
            //Only manually call onResume if fragment is already visible
            //Otherwise allow natural fragment lifecycle to call onResume
            onResume();
        }
    }

    public void setPropertyListAdapter() {
        listingAdapter = new ListingAdapter(context, listingList);
        recyclerView.setAdapter(listingAdapter);
        // listingAdapter.notifyDataSetChanged();
    }

    //------------------- Converting dp to pixel -----------------------
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void radius(String radius) {
        this.radius=radius;
        callPropertySearchAPI();
    }
}
