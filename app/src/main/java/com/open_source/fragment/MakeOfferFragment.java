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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.BuyDisclosure;
import com.open_source.activity.MakeOfferTermActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.DisclosreDoc;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MakeOfferFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = MakeOfferFragment.class.getSimpleName();
    static String property_ids = "", userid = "", pro_status, offer_status = "", user_id = "", discloser = "", agreement_file = "";
    Context context;
    EditText cet_price;
    Button btn_submit;
    View rootView;
    Dialog dialog;
    UserData userData;
    ProgressHUD progressHUD;
    private Button btn_buy, btn_buy_AS_IS;
    private ArrayList<DisclosreDoc> array_doc = new ArrayList<>();

    public static MakeOfferFragment newInstance(String property_id, String user_id, String status, String offerstatus) {
        MakeOfferFragment frag = new MakeOfferFragment();
        property_ids = property_id;
        userid = user_id;
        pro_status = status;
        offer_status = offerstatus;
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
        rootView = inflater.inflate(R.layout.fragment_detail_make_offer, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        btn_submit = rootView.findViewById(R.id.btn_submit);
        cet_price = rootView.findViewById(R.id.cet_price);
        if (offer_status.equals("1")) {
            ((TextView) rootView.findViewById(R.id.offer_status)).setVisibility(View.VISIBLE);
            btn_submit.setBackgroundResource(R.drawable.round_button_fad);
        }
        btn_submit.setOnClickListener(this);

        btn_buy = rootView.findViewById(R.id.btn_buy);
        btn_buy_AS_IS = rootView.findViewById(R.id.btn_as_is);
        btn_buy.setOnClickListener(this);
        btn_buy_AS_IS.setOnClickListener(this);
        callPropertyDetailsByIdAPI();
    }

    /*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context=getActivity();
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_detail_make_offer);
        dialog.setCancelable(false);
        ctv_cancel = dialog.findViewById(R.id.ctv_cancel);
        ctv_submit = dialog.findViewById(R.id.ctv_submit);
        cet_price = dialog.findViewById(R.id.cet_price);
        ctv_cancel.setOnClickListener(this);
        ctv_submit.setOnClickListener(this);
        return dialog;
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (offer_status.equals("1")) {
                    ((TextView) rootView.findViewById(R.id.offer_status)).setVisibility(View.VISIBLE);
                    Utility.ShowToastMessage(context, getString(R.string.msg_offer_already_submitted));
                } else {
                    if (cet_price.getText().toString().isEmpty()) {
                        Utility.ShowToastMessage(context, R.string.msg_error_price);
                    } else {
                        if (SharedPref.getSharedPreferences(context, Constants.USER_ID).equals(userid)) {
                            Utility.ShowToastMessage(context, getString(R.string.msg_self_offer));
                        } else {
                            if (pro_status.equals("1")) {
                                Utility.ShowToastMessage(context, getString(R.string.pro_not_available_for_offer));
                            } else {
                                // callMakeOfferAPI();
                                startActivity(new Intent(context, MakeOfferTermActivity.class).
                                        putExtra(Constants.AMOUNT, cet_price.getText().toString()).
                                        putExtra(Constants.PROPERTY_ID, userData.getProperty_id()));

                            }

                        }

                    }
                }

                break;
            case R.id.btn_buy:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context, getString(R.string.message_guest));
                    //startActivity(new Intent(context, LoginActivity.class));
                } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_self_buy_property));
                } else {
                    startActivity(new Intent(context, BuyDisclosure.class).
                            putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.TYPE, "fixed"));
                 /*   if (!discloser.isEmpty() || !userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() || array_doc.size() > 0 ||
                            !userData.getFixsure_arr().getCarpet_flooring().isEmpty() || !userData.getFixsure_arr().getCeiling_fans().isEmpty() ||
                            !userData.getFixsure_arr().getCounterstop().isEmpty() || !userData.getFixsure_arr().getPrivate_patios().isEmpty() ||
                            !userData.getFixsure_arr().getWashers_dryers().isEmpty() || !userData.getFixsure_arr().getWood_flooring().isEmpty()) {
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) array_doc);
                        args.putString(Constants.DISCLOSER, discloser.trim());
                        args.putString(Constants.FIXTURES, userData.getFixsure_arr().getFixsure());
                        args.putString(Constants.FIRE_PLACE, userData.getFixsure_arr().getFireplace());
                        args.putString(Constants.CARPET_FLORING, userData.getFixsure_arr().getCarpet_flooring());
                        args.putString(Constants.CEILING_FAN, userData.getFixsure_arr().getCeiling_fans());
                        args.putString(Constants.COUNTERSTOP, userData.getFixsure_arr().getCounterstop());
                        args.putString(Constants.PATIOS, userData.getFixsure_arr().getPrivate_patios());
                        args.putString(Constants.WASHER_DRAWER, userData.getFixsure_arr().getWashers_dryers());
                        args.putString(Constants.WOOD_FLORING, userData.getFixsure_arr().getWood_flooring());
                        args.putString(Constants.AGREEMENT_FILE, agreement_file);
                        args.putString(Constants.STATUS, "BuyNow");
                        args.putString(Constants.TYPE, "fixed");
                        args.putString(Constants.PROPERTY_ID, userData.getProperty_id());
                        startActivity(new Intent(context, DisclosureActivity.class).putExtras(args));
                    } else {
                        startActivity(new Intent(context, PaymentConfirmation.class).
                                putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.TYPE, "fixed"));

                    }*/

                }
                break;
            case R.id.btn_as_is:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context, getString(R.string.message_guest));
                    //startActivity(new Intent(context, LoginActivity.class));
                } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, R.string.val_self_buy_property);
                } else {
                    startActivity(new Intent(context, BuyDisclosure.class).
                            putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.TYPE, "fixed"));
                /*    if (!discloser.isEmpty() || !userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() || array_doc.size() > 0 ||
                            !userData.getFixsure_arr().getCarpet_flooring().isEmpty() || !userData.getFixsure_arr().getCeiling_fans().isEmpty() ||
                            !userData.getFixsure_arr().getCounterstop().isEmpty() || !userData.getFixsure_arr().getPrivate_patios().isEmpty() ||
                            !userData.getFixsure_arr().getWashers_dryers().isEmpty() || !userData.getFixsure_arr().getWood_flooring().isEmpty()) {
                        Bundle args = new Bundle();
                        args.putSerializable("ARRAYLIST", (Serializable) array_doc);
                        args.putString(Constants.DISCLOSER, discloser.trim());
                        args.putString(Constants.FIXTURES, userData.getFixsure_arr().getFixsure());
                        args.putString(Constants.FIRE_PLACE, userData.getFixsure_arr().getFireplace());
                        args.putString(Constants.CARPET_FLORING, userData.getFixsure_arr().getCarpet_flooring());
                        args.putString(Constants.CEILING_FAN, userData.getFixsure_arr().getCeiling_fans());
                        args.putString(Constants.COUNTERSTOP, userData.getFixsure_arr().getCounterstop());
                        args.putString(Constants.PATIOS, userData.getFixsure_arr().getPrivate_patios());
                        args.putString(Constants.WASHER_DRAWER, userData.getFixsure_arr().getWashers_dryers());
                        args.putString(Constants.WOOD_FLORING, userData.getFixsure_arr().getWood_flooring());
                        args.putString(Constants.AGREEMENT_FILE, agreement_file);
                        args.putString(Constants.STATUS, "AsIs");
                        args.putString(Constants.TYPE, "fixed");
                        args.putString(Constants.PROPERTY_ID, userData.getProperty_id());
                        startActivity(new Intent(context, DisclosureActivity.class).putExtras(args));
                    } else {
                        startActivity(new Intent(context, PaymentConfirmation.class).
                                putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.TYPE, "fixed"));
                    }

*/
                }
                break;
        }
    }

    //----------------- Submit ----------------------

    public void callPropertyDetailsByIdAPI() {
        // progressHUD = ProgressHUD.show(context, true, false, null);
        Log.e(TAG, "==================: " + SharedPref.getSharedPreferences(context, Constants.USER_ID));
        Log.e(TAG, "property_ids: " + property_ids + " TOKEN: " + SharedPref.getSharedPreferences(context, Constants.TOKEN));
        RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_ids).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                try {
                    if (response.body().getStatus() == 200) {
//                        Utility.ShowToastMessage(context, response.body().getMessage());
                        userData = response.body().getUserData();
                        user_id = userData.getUser_id();
                        discloser = userData.getDiscloser();
                        agreement_file = userData.getAgreement_file();
                        array_doc = userData.getDiscloser_files();
                        if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                            btn_buy.setVisibility(View.VISIBLE);
                            btn_buy_AS_IS.setVisibility(View.VISIBLE);
                            btn_buy_AS_IS.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            btn_buy.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                            }

                        } else {
                            if ((userData.getPost().equalsIgnoreCase("Fixed") || userData.getPost().equalsIgnoreCase("Both")) && (userData.getIs_sold().equalsIgnoreCase("0"))) {
                                btn_buy.setVisibility(View.VISIBLE);
                                btn_buy_AS_IS.setVisibility(View.VISIBLE);

                            } else if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                            } else {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                            }
                        }

                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    } else {
//                        Utility.ShowToastMessage(context,response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitUserData> call, Throwable t) {
               /* if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }*/
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
