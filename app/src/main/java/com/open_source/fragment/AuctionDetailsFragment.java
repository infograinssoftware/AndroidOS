package com.open_source.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.PaymentActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuctionDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = AuctionDetailsFragment.class.getSimpleName();
    static String property_ids = "";
    Context context;
    View rootView;
    TextView ctv_place_name, ctv_when, ctv_where, ctv_min_bid_price, ctv_curr_bid_amount, ctv_bid_amount;
    EditText cet_bid_amount;
    Button btn_bid_place, btn_ok, btn_buy;
    ProgressHUD progressHUD;
    Dialog dialog;
    boolean shouldStopLoop = false;


    public static AuctionDetailsFragment newInstance(String property_id) {
        AuctionDetailsFragment frag = new AuctionDetailsFragment();
        property_ids = property_id;
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
        rootView = inflater.inflate(R.layout.fragment_auction_detail, container, false);
        context = getActivity();
        // mHandler.post(runnable);
        init();
        return rootView;
    }

    @Override
    public void onPause() {
       // mHandler.removeCallbacks(runnable);
        super.onPause();
    }

    public void init() {
        ctv_place_name = rootView.findViewById(R.id.ctv_place_name);
        ctv_when = rootView.findViewById(R.id.ctv_when);
        ctv_where = rootView.findViewById(R.id.ctv_where);
        ctv_min_bid_price = rootView.findViewById(R.id.ctv_min_bid_price);
        ctv_curr_bid_amount = rootView.findViewById(R.id.ctv_curr_bid_amount);
        cet_bid_amount = rootView.findViewById(R.id.cet_bid_amount);
        btn_bid_place = rootView.findViewById(R.id.btn_bid_place);
        ctv_bid_amount = rootView.findViewById(R.id.ctv_bid_amount);
        btn_buy=rootView.findViewById(R.id.btn_buy);
        ((Button) rootView.findViewById(R.id.btn_buy)).setOnClickListener(this);
        callPropertyDetailsByIdAPI();
        btn_bid_place.setOnClickListener(this);
        btn_buy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bid_place:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context,getString(R.string.message_guest));
                   // startActivity(new Intent(context, LoginActivity.class));
                } else {
                    if (cet_bid_amount.getText().toString().isEmpty()) {
                        Utility.ShowToastMessage(context, getString(R.string.msg_bid_amount));
                    } else {
                        Fun_Bid(cet_bid_amount.getText().toString());
                    }
                }
                break;

            case R.id.btn_ok:
                dialog.dismiss();
                break;

            case  R.id.btn_buy:
                startActivity(new Intent(context, PaymentActivity.class).
                        putExtra(Constants.PROPERTY_ID, property_ids).
                        putExtra(Constants.PROPERTY_AMOUNT,ctv_min_bid_price.getText().toString()));
                break;
        }
    }

    private void Fun_Bid(String stramount) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Fun_BID(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    property_ids, stramount).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    Log.e(TAG, "onResponse: " + response.toString());
                    try {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getMessage().equals("success")) {
                                ctv_curr_bid_amount.setText(cet_bid_amount.getText().toString());
                                cet_bid_amount.setText("");
                                showAuctionDialog();
                                //callPropertyDetailsByIdAPI();
                            }
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

    //------------------- Property Details ------------------------------
    public void callPropertyDetailsByIdAPI() {
        // progressHUD = ProgressHUD.show(context, true, false, null);
        Log.e(TAG, "property_ids: " + property_ids + " TOKEN: " + SharedPref.getSharedPreferences(context, Constants.TOKEN));
        RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_ids).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                /*if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }*/
                Log.e(TAG, "onResponse: " + response.toString());
                try {
                    if (response.body().getStatus() == 200) {
//                        Utility.ShowToastMessage(context, response.body().getMessage());
                        UserData userData = response.body().getUserData();
                        ctv_place_name.setText(userData.getSociety() + ", " + userData.getCity());
                        ctv_when.setText(getString(R.string.level_start_date) + userData.getStart_date() + getString(R.string.level_end_date) + userData.getEnd_date() + getString(R.string.level_start_time) + userData.getStart_time() + getString(R.string.level_end_time) + userData.getEnd_time());
                        ctv_where.setText(userData.getLocation());

                        ctv_min_bid_price.setText("$" + userData.getMinimum_price());
//                        ctv_min_bid_price.setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + userData.getMinimum_price());

                        if (!userData.getCurrent_max_bid().isEmpty()) {
                            ctv_curr_bid_amount.setText("$" + userData.getCurrent_max_bid());
//                            ctv_curr_bid_amount.setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + userData.getCurrent_max_bid());
                        }
                        if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                            btn_bid_place.setVisibility(View.GONE);
                            cet_bid_amount.setVisibility(View.GONE);
                            ctv_bid_amount.setVisibility(View.GONE);
                        }
                        if (userData.getBidStatus() == 0 || userData.getIs_sold().equalsIgnoreCase("1") &&!userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                            //ctv_curr_bid_amount.setVisibility(View.GONE);
                            btn_bid_place.setVisibility(View.GONE);
                            cet_bid_amount.setVisibility(View.GONE);
                            ctv_bid_amount.setVisibility(View.GONE);
                            shouldStopLoop=true;
                           // mHandler.removeCallbacks(runnable);
                        }

                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    } else {
                      Utility.ShowToastMessage(context,response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                /*if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }*/
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }

    //------------------------ Auction Dialog --------------------
    public void showAuctionDialog() {
        dialog = new Dialog(context, R.style.myDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_auction);
        dialog.setCancelable(false);
        btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shouldStopLoop=true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            shouldStopLoop=false;
        } else {
            shouldStopLoop=true;
        }
    }

}
