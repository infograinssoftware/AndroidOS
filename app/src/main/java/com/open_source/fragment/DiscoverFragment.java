package com.open_source.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.open_source.Interface.rediuslick;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.ItemDecorationAlbumColumns;
import com.open_source.adapter.ListingAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment implements rediuslick{
    private static final String TAG = DiscoverFragment.class.getSimpleName();
    Context context;
    View rootView;
    TextView txvNoDataFound;
    RecyclerView recyclerView;
    ListingAdapter listingAdapter;
    List<RetroObject> listingList = new ArrayList<>();
    ProgressHUD progressHUD;
    String radius="500";
    static String latitudes="0.0", logitudes="0.0";

    public static DiscoverFragment newInstance(String latitude, String logitude) {
        DiscoverFragment frag = new DiscoverFragment();
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
        radius="";
        context = getActivity();
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
            Log.e(TAG, "latitudes: " + latitudes + " logitudes: " + logitudes);
        }
    }

    //------------------------ Property List --------------------
    public void callPropertySearchAPI() {
        progressHUD = ProgressHUD.show(context, true, false, null);
        RetrofitClient.getAPIService().getPropertySearchAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                latitudes,
                logitudes,
                "discover",this.radius).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }

                try {
                    if (response.body().getStatus() == 200) {
                        listingList = response.body().getObject();
                        if (listingList.size()!=0) {
                            setPropertyListAdapter();
                            txvNoDataFound.setVisibility(View.GONE);
                        } else {
                            txvNoDataFound.setVisibility(View.VISIBLE);
                        }
                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    } else {
                        txvNoDataFound.setVisibility(View.VISIBLE);
                        Utility.ShowToastMessage(context,response.body().getMessage());
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
            if (BuyFragment.visible_status==true)
            callPropertySearchAPI();
            BuyFragment.visible_status=false;
            // recyclerView.setAdapter(listingAdapter);
            Log.e(TAG, "=========: " + "onResume");
        }
    }

    public void setPropertyListAdapter() {
        listingAdapter = new ListingAdapter(context, listingList);
        recyclerView.setAdapter(listingAdapter);
    }

    //------------------- Converting dp to pixel -----------------------
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        BuyFragment.visible_status=true;
        Log.e(TAG, "=========: " + "setUserVisibleHint");
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void radius(String radius) {
         this.radius=radius;
         callPropertySearchAPI();
    }
}
