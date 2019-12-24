package com.open_source.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.DescriptionActivity;
import com.open_source.activity.DisclosureActivity;
import com.open_source.activity.LoanActivity;
import com.open_source.activity.RentPayment;
import com.open_source.activity.RenterReferenceCheckActivity;
import com.open_source.activity.TransitionDetailActivity;
import com.open_source.activity.VideoPlay;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.SlidingImageAdapter;
import com.open_source.modal.DisclosreDoc;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener, Serializable {
    private static final String TAG = AboutFragment.class.getSimpleName();
    static String property_ids = "", pro_type = "", rentpay = "";
    String discription = "", transaction_history = "", discloser = "", fixtures = "", agreement_file = "", videolink = "",value;
    Context context;
    View rootView;
    TextView ctv_sold, ctv_reserve, ctv_property, ctv_address, ctv_transition_detail, ctv_beds,
            ctv_bath, ctv_description, ctv_documents,ctv_fixtures, ctv_sqFeet, ctv_final_bid, txt_open_house, txt_loan;
    ProgressHUD progressHUD;
    ViewPager upcoming_pager;
    LinearLayout viewPagerCountDots;
    SlidingImageAdapter slidingImage_adapter;
    ArrayList<String> sliderList = new ArrayList<>();
    ArrayList<DisclosreDoc> array_doc = new ArrayList<>();
    Button video_house;
    UserData userData;
    private int dotsCount;
    private ImageView[] dots;


    public static AboutFragment newInstance(String property_id, String type, String rent_pay) {
        AboutFragment frag = new AboutFragment();
        property_ids = property_id;
        pro_type = type;
        rentpay = rent_pay;
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
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        ctv_sold = rootView.findViewById(R.id.ctv_sold);
        ctv_reserve = rootView.findViewById(R.id.ctv_reserve);
        ctv_property = rootView.findViewById(R.id.ctv_property);
        ctv_address = rootView.findViewById(R.id.ctv_address);
        ctv_final_bid = rootView.findViewById(R.id.ctv_final_bid);
        ctv_transition_detail = rootView.findViewById(R.id.ctv_transition_detail);
        upcoming_pager = rootView.findViewById(R.id.upcoming_pager);
        viewPagerCountDots = rootView.findViewById(R.id.viewPagerCountDots);
        ctv_description = rootView.findViewById(R.id.ctv_description);
        ctv_documents = rootView.findViewById(R.id.ctv_documents);
        ctv_fixtures=rootView.findViewById(R.id.ctv_fixtures);
        ctv_beds = rootView.findViewById(R.id.ctv_beds);
        ctv_bath = rootView.findViewById(R.id.ctv_bath);
        ctv_sqFeet = rootView.findViewById(R.id.ctv_sqFeet);
        video_house = rootView.findViewById(R.id.video_house);
        txt_open_house = rootView.findViewById(R.id.txt_open_house);
        txt_loan = rootView.findViewById(R.id.txt_loan);
        txt_loan.setOnClickListener(this);
        txt_open_house.setOnClickListener(this);
        video_house.setOnClickListener(this);

        callPropertyDetailsByIdAPI();
        ctv_transition_detail.setOnClickListener(this);
        ctv_description.setOnClickListener(this);
        ctv_documents.setOnClickListener(this);
        ctv_fixtures.setOnClickListener(this);
        upcoming_pager.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_transition_detail:
                if (transaction_history.equals("")) {
                    Utility.ShowToastMessage(context, getString(R.string.val_trans_detail));
                } else {
                    startActivity(new Intent(context, TransitionDetailActivity.class).putExtra(Constants.TRANSACTION_HISTORY, transaction_history));
                }
                break;

            case R.id.ctv_description:
                if (discription.equals("")) {
                    Utility.ShowToastMessage(context, getString(R.string.val_description));
                } else {
                    startActivity(new Intent(context, DescriptionActivity.class).putExtra(Constants.DESCRIPTION, discription));
                }
                break;


            case R.id.ctv_documents:
                if (array_doc.size() > 0) {
                    Bundle args = new Bundle();
                    args.putSerializable("ARRAYLIST", (Serializable) array_doc);
                    args.putString(Constants.DISCLOSER, discloser.trim());
                    args.putString(Constants.AGREEMENT_FILE, agreement_file);
                    args.putString(Constants.STATUS, "View");
                    startActivity(new Intent(context, DisclosureActivity.class).putExtras(args));

                } else {
                    Utility.ShowToastMessage(context, getString(R.string.val_disclosure));
                }
                break;
            case R.id.ctv_fixtures:
                if (!userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() ||
                        !userData.getFixsure_arr().getCarpet_flooring().isEmpty() || !userData.getFixsure_arr().getCeiling_fans().isEmpty() ||
                        !userData.getFixsure_arr().getCounterstop().isEmpty() || !userData.getFixsure_arr().getPrivate_patios().isEmpty() ||
                        !userData.getFixsure_arr().getWashers_dryers().isEmpty() || !userData.getFixsure_arr().getWood_flooring().isEmpty()) {
                    Bundle args = new Bundle();
                   // args.putSerializable("ARRAYLIST", (Serializable) array_doc);
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
                    args.putString(Constants.STATUS, "View");
                    startActivity(new Intent(context, DisclosureActivity.class).putExtras(args));

                } else {
                    Utility.ShowToastMessage(context,getString(R.string.no_fixture));
                }

                break;
            case R.id.video_house:
                if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && pro_type.equalsIgnoreCase("Rent")) {
                    Utility.ShowToastMessage(context, getString(R.string.val_self_apply));
                } else if (pro_type.equalsIgnoreCase("rent") && rentpay.equalsIgnoreCase("")) {
                   /* startActivity(new Intent(context, RentFormFirst.class).putExtra(Constants.TYPE, userData.getType())
                            .putExtra(Constants.ADDRESS, userData.getSociety() + "," + userData.getCity()).putExtra(Constants.RENT_AMOUNT,
                                    userData.getRent_amount()).putExtra(Constants.PROPERTY_ID, property_ids).putExtra(Constants.USER_ID, userData.getUser_id()));*/
                    if (userData.getDisclaimer_status().equals("0")) {
                        startActivity(new Intent(context, RenterReferenceCheckActivity.class).putExtra(Constants.TYPE, userData.getType())
                                .putExtra(Constants.ADDRESS, userData.getSociety() + "," + userData.getCity()).putExtra(Constants.RENT_AMOUNT,
                                        userData.getRent_amount()).putExtra(Constants.PROPERTY_ID, property_ids).
                                        putExtra(Constants.USER_ID, userData.getUser_id()).
                                        putExtra(Constants.RENTER_TYPE, userData.getRenter_type()));
                    } else if (userData.getDisclaimer_status().equals("1")) {
                        // Utility.ShowToastMessage(context, getString(R.string.msg_pending_approval));
                        startActivity(new Intent(context, RentFormFirst.class).putExtra(Constants.TYPE, userData.getType())
                                .putExtra(Constants.ADDRESS, userData.getSociety() + "," + userData.getCity()).putExtra(Constants.RENT_AMOUNT,
                                        userData.getRent_amount()).putExtra(Constants.PROPERTY_ID, property_ids).
                                        putExtra(Constants.USER_ID, userData.getUser_id()).
                                        putExtra(Constants.RENTER_TYPE, userData.getRenter_type()));
                    } else if (userData.getDisclaimer_status().equals("2")) {
                        startActivity(new Intent(context, RentFormFirst.class).putExtra(Constants.TYPE, userData.getType())
                                .putExtra(Constants.ADDRESS, userData.getSociety() + "," + userData.getCity()).putExtra(Constants.RENT_AMOUNT,
                                        userData.getRent_amount()).putExtra(Constants.PROPERTY_ID, property_ids).
                                        putExtra(Constants.USER_ID, userData.getUser_id()).
                                        putExtra(Constants.RENTER_TYPE, userData.getRenter_type()));
                    }
                } else if (pro_type.equalsIgnoreCase("rent") && rentpay.equalsIgnoreCase("RentPay")) {
                    startActivity(new Intent(context, RentPayment.class).
                            putExtra(Constants.RENT_AMOUNT, userData.getRent_amount()).
                            putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.TYPE, userData.getType()).
                            putExtra(Constants.PROPERTY_IMG, userData.getProperty_img().get(0).getFile_name()));
                } else {
                    startActivity(new Intent(context, VideoPlay.class).putExtra(Constants.VideoLink, videolink));
                }
                break;
            case R.id.txt_loan:
                startActivity(new Intent(context, LoanActivity.class));
                break;
            case R.id.txt_open_house:
                if (userData.getOpen_house_desc().equals("")) {
                    Utility.ShowToastMessage(context, R.string.val_description);
                } else {
                    startActivity(new Intent(context, DescriptionActivity.class).putExtra(Constants.DESCRIPTION, userData.getOpen_house_desc()));

                }
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {


    }

    private void setUiPageViewController() {
        dotsCount = slidingImage_adapter.getCount();
        Log.e(TAG, "dotsCount: " + String.valueOf(dotsCount));
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(context);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.z_nonselecteditem_dot));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(4, 0, 4, 0);
            viewPagerCountDots.addView(dots[i], layoutParams);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.z_selecteditem_dot));
    }

    //------------------- Property Details ------------------------------
    public void callPropertyDetailsByIdAPI() {
        //progressHUD = ProgressHUD.show(context, true, false, null);
        Log.e(TAG, "property_ids: " + property_ids + " TOKEN: " + SharedPref.getSharedPreferences(context, Constants.TOKEN));
        RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_ids).enqueue(new Callback<RetrofitUserData>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
               /* if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }*/
                sliderList.clear();
                try {
                    if (response.body().getStatus() == 200) {
                        userData = response.body().getUserData();
                        if (pro_type.equalsIgnoreCase("rent") && rentpay.equalsIgnoreCase("")) {
                            if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                                txt_open_house.setVisibility(View.GONE);
                                video_house.setVisibility(View.VISIBLE);
                                video_house.setText(R.string.apply_for_rent);
                                video_house.setBackgroundColor(getResources().getColor(R.color.fade));
                            } else {
                                txt_open_house.setVisibility(View.GONE);
                                video_house.setVisibility(View.VISIBLE);
                                video_house.setText(R.string.apply_for_rent);
                            }

                        } else if (pro_type.equalsIgnoreCase("rent") && rentpay.equalsIgnoreCase("RentPay")) {
                            txt_open_house.setVisibility(View.GONE);
                            video_house.setVisibility(View.VISIBLE);
                            video_house.setText(R.string.pay_for_rent);
                        } else {
                            txt_loan.setVisibility(View.VISIBLE);
                            if (!userData.getVedio().isEmpty()) {
                                videolink = userData.getVedio();
                                video_house.setVisibility(View.VISIBLE);
                                if (!userData.getOpen_house_desc().isEmpty()) {
                                    txt_open_house.setVisibility(View.VISIBLE);
                                }

                            }
                        }
                        if (userData.getIs_sold().equalsIgnoreCase("1")) {
                            ctv_sold.setText(R.string.sold);
                        } else if (userData.getIs_sold().equalsIgnoreCase("0")) {
                            ctv_sold.setText(R.string.for_sale);
                        } else if (userData.getIs_sold().equalsIgnoreCase("2")) {
                            ctv_sold.setText(R.string.under_contract);
                        }
                        //222,222
                        //10,000,000
                        //1,000,000,000
                        if (pro_type.equalsIgnoreCase("rent")) {
                            String value = getFormatedAmount(Integer.valueOf(userData.getRent_amount()));
                            if (value != null)
                                ctv_property.setText(getString(R.string.pro_rent) + " $" + value);
//                                ctv_property.setText(getString(R.string.pro_rent) +" "+ SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+" "+value);
                            else
                                /*------------------------------------*/
                                ctv_property.setText(getString(R.string.pro_rent) + " $" + userData.getRent_amount());
//                                ctv_property.setText(getString(R.string.pro_rent) + " "+ SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+" "+userData.getRent_amount());
//                                ctv_property.setText(getString(R.string.pro_rent) + SharedPref.getSharedPreferences(context, Constants.CURRENCY) +" "+ userData.getAmount());

                                ((LinearLayout) rootView.findViewById(R.id.lay_rent)).setVisibility(View.VISIBLE);
                            if (userData.getPets().equalsIgnoreCase("No")) {
                                ((TextView) rootView.findViewById(R.id.txt_pets)).setText(R.string.pets_status);
                            } else {
                                ((TextView) rootView.findViewById(R.id.txt_pets)).setText(getString(R.string.level_pets) + userData.getPets_details());
                            }
                            if (userData.getSmoking_allowed().equalsIgnoreCase("No")) {
                                ((TextView) rootView.findViewById(R.id.txt_smoking)).setText(R.string.smoking_status);
                            } else {
                                ((TextView) rootView.findViewById(R.id.txt_smoking)).setText(getString(R.string.level_smoking) + userData.getSmoking_details());
                            }
                            if (userData.getParking().equalsIgnoreCase("No")) {
                                ((TextView) rootView.findViewById(R.id.txt_parking)).setText(R.string.parking_status);
                            } else {
                                ((TextView) rootView.findViewById(R.id.txt_parking)).setText(getString(R.string.level_parking) + userData.getParking_details());
                            }
                        } else {
                            if (userData.getFixed_price().equalsIgnoreCase("0")) {
                                ctv_property.setText(R.string.pro_proce_not_available);
                            } else {
                                if (!userData.getFixed_price().isEmpty())
                                {
//                                   value = getFormatedAmount(Integer.valueOf(userData.getFixed_price()));
                                   value = getFormatedAmount((int)Math.round(Double.valueOf(userData.getFixed_price())));
                                }
                                if (value != null)
                                   // ctv_property.setText(getString(R.string.pro_fixed_val) + " $" + value);
                                    ctv_property.setText(userData.getPurpose()+ " $" + value);
//                                    ctv_property.setText(userData.getPurpose()+ " "+ SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+" "+value);
                                else
                                    ctv_property.setText(getString(R.string.pro_fixed_val) + " $" + userData.getFixed_price());
//                                    ctv_property.setText(userData.getPurpose() + " "+ SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+" "+ userData.getFixed_price());
                            }
                        }

                        ctv_address.setText(userData.getSociety() + "," + userData.getCity());
                        ctv_beds.setText(userData.getBadroom());
                        ctv_bath.setText(userData.getBathroom());
                        ctv_sqFeet.setText(userData.getArea_square());
                        discription = userData.getDiscription();
                        transaction_history = userData.getTransaction_history();
                        discloser = userData.getDiscloser();
                        // fixtures = userData.getFixsure();
                        array_doc = userData.getDiscloser_files();
                        agreement_file = userData.getAgreement_file();
                        if (userData.getIs_sold().equalsIgnoreCase("1")) {
                            if (userData.getPost().equalsIgnoreCase("both") || userData.getPost().equalsIgnoreCase("auction"))
                                ctv_final_bid.setText(getString(R.string.final_bid) + userData.getCurrent_max_bid());
                            else
                                ctv_final_bid.setVisibility(View.GONE);
                        } else {
                            if (userData.getPost().equalsIgnoreCase("both") || userData.getPost().equalsIgnoreCase("auction"))
                                ctv_final_bid.setText(R.string.final_bid_not_available);
                            else
                                ctv_final_bid.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < userData.getProperty_img().size(); i++) {
                            sliderList.add(userData.getProperty_img().get(i).getFile_name());
                        }
                        slidingImage_adapter = new SlidingImageAdapter(context, sliderList,"");
                        upcoming_pager.setAdapter(slidingImage_adapter);
                        if (userData.getProperty_img().size() > 0) {
                            setUiPageViewController();
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

    public String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}
