package com.open_source.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.DisclosureActivity;
import com.open_source.activity.DocumnetTabActivity;
import com.open_source.activity.PaymentConfirmation;
import com.open_source.activity.VideoPlay;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.DisclosreDoc;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ContactFragment.class.getSimpleName();
    private static final int REQUEST_PHONE_CALL = 1;
    static String property_ids = "", user_id = "", amount = "0", pro_type = "", post_type = "";
    ProgressHUD progressHUD;
    private Context context;
    private View rootView;
    private TextView ctv_name, ctv_contact, ctv_where, txtdate, txttime, txtname, txtemail, txtcontact, btn_manage_doc;
    private Button btn_message, btn_buy, btn_request, btn_tour, btn_buy_AS_IS, btn_submit_dialog;
    private ImageView imageView_cross;
    private UserData userData;
    private String discloser = "", agreement_file = "";
    private ArrayList<DisclosreDoc> array_doc = new ArrayList<>();
    private Dialog dialog;
    private String MobilePattern = "[0-9]{10}", videolink = "";
    private int mYear, mMonth, mDay, mHour, mMinute;

    public static ContactFragment newInstance(String property_id, String type, String rent_pay, String posttype) {
        ContactFragment frag = new ContactFragment();
        property_ids = property_id;
        pro_type = type;
        post_type = posttype;
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
        rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        ctv_name = rootView.findViewById(R.id.ctv_name);
        ctv_contact = rootView.findViewById(R.id.ctv_contact);
        ctv_where = rootView.findViewById(R.id.ctv_where);
        btn_message = rootView.findViewById(R.id.btn_message);
        btn_buy = rootView.findViewById(R.id.btn_buy);
        btn_request = rootView.findViewById(R.id.btn_request);
        btn_tour = rootView.findViewById(R.id.btn_tour);
        btn_buy_AS_IS = rootView.findViewById(R.id.btn_as_is);
        btn_manage_doc = rootView.findViewById(R.id.btn_manage_doc);
        btn_manage_doc.setOnClickListener(this);
        callPropertyDetailsByIdAPI();
        btn_message.setOnClickListener(this);
        ctv_contact.setOnClickListener(this);
        btn_buy.setOnClickListener(this);
        btn_buy_AS_IS.setOnClickListener(this);
        btn_request.setOnClickListener(this);
        btn_tour.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_message:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context, getString(R.string.message_guest));
                    //startActivity(new Intent(context, LoginActivity.class));
                } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_self_message));
                } else {
                    startActivity(new Intent(context, ChatActivity.class).putExtra(Constants.PROPERTY_ID, property_ids).
                            putExtra(Constants.USER_ID, user_id).putExtra(Constants.TONAME, ctv_name.getText().toString()).
                            putExtra(Constants.CHAT_STATUS, "1").
                            putExtra(Constants.PROFILE_IMAGE, userData.getUrl() + userData.getProfileImage()));
                }
                break;

            case R.id.ctv_contact:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ctv_contact.getText().toString())));
                }
                break;

            case R.id.cross:
                dialog.dismiss();
                break;

            case R.id.date:
                PickDate();
                break;

            case R.id.btn_request:
                if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_self_tour));
                } else {
                    ShowDialog();
                }

                break;

            case R.id.time:
                PickTime();
                break;

            case R.id.btn_tour:
                if (!videolink.isEmpty())
                    startActivity(new Intent(context, VideoPlay.class).putExtra(Constants.VideoLink, videolink));
                else
                    Utility.ShowToastMessage(context, getString(R.string.val_virthual_tour));

                break;

            case R.id.btn_submit_dialog:
                if (txtdate.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_tour_date));
                } else if (txttime.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_tour_time));
                } else if (txtname.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_name));
                } else if (txtemail.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_email));
                } else if (!Utility.isValidEmail(txtemail.getText().toString())) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_val_email));
                } else if (txtcontact.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_contact));
                } else if (!txtcontact.getText().toString().matches(MobilePattern)) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_valid_conatct));
                } else {
                    FunRequest();
                }

                break;

            case R.id.btn_buy:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context, getString(R.string.message_guest));
                    //startActivity(new Intent(context, LoginActivity.class));
                } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_self_buy_property));
                } else {
                    if (!discloser.isEmpty() || !userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() || array_doc.size() > 0 ||
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

                    }

                }
                break;
            case R.id.btn_as_is:
                if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                    Utility.ShowToastMessage(context, getString(R.string.message_guest));
                    //startActivity(new Intent(context, LoginActivity.class));
                } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                    Utility.ShowToastMessage(context, R.string.val_self_buy_property);
                } else {
                    if (!discloser.isEmpty() || !userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() || array_doc.size() > 0 ||
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

                }
                break;
            case R.id.btn_manage_doc:
                startActivity(new Intent(context, DocumnetTabActivity.class).
                        putExtra(Constants.PROPERTY_ID, userData.getProperty_id()).putExtra(Constants.BUYER_ID, userData.getBuyer_id()));
                break;

        }
    }

    private void FunRequest() {
        if (Utility.isConnectingToInternet(context)) {
            //progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().TourRequest(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    txtdate.getText().toString(), txttime.getText().toString(),
                    txtname.getText().toString(), txtemail.getText().toString(),
                    txtcontact.getText().toString(), property_ids).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                   /* if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }*/
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, getString(R.string.message_request));
                            dialog.dismiss();

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
                   /* if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }*/
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
        Log.e(TAG, "==================: " + SharedPref.getSharedPreferences(context, Constants.USER_ID));
        Log.e(TAG, "property_ids: " + property_ids + " TOKEN: " + SharedPref.getSharedPreferences(context, Constants.TOKEN));
        RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                property_ids).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
               /* if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }*/
                try {
                    if (response.body().getStatus() == 200) {
//                        Utility.ShowToastMessage(context, response.body().getMessage());
                        userData = response.body().getUserData();
                        user_id = userData.getUser_id();
                        ctv_name.setText(userData.getSeller_firstname() + " " + userData.getSeller_lastname());
                        ctv_contact.setPaintFlags(ctv_contact.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        ctv_contact.setText(userData.getSeller_contact());
                        ctv_where.setText(userData.getLocation());
                        discloser = userData.getDiscloser();
                        if (!userData.getVedio().isEmpty()) {
                            videolink = userData.getVedio();

                        }
                        // fixtures = userData.getFixsure();
                        array_doc = userData.getDiscloser_files();
                        agreement_file = userData.getAgreement_file();
                        //Log.e(TAG, "=======: "+userData.getUrl() + userData.getProfileImage() );
                      /*  Log.e(TAG, "onResponse: " + userData.getUser_id());
                        Log.e(TAG, "onResponse: " + SharedPref.getSharedPreferences(context, Constants.USER_ID));*/
                        if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && pro_type.equalsIgnoreCase("Rent")) {
                            btn_tour.setVisibility(View.VISIBLE);
                            btn_request.setVisibility(View.VISIBLE);
                            btn_message.setVisibility(View.VISIBLE);
                            btn_request.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            btn_message.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                        } else if (userData.getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && !pro_type.equalsIgnoreCase("Rent")) {
                            btn_buy.setVisibility(View.GONE);
                            btn_buy_AS_IS.setVisibility(View.GONE);
                            btn_message.setVisibility(View.VISIBLE);
                            btn_message.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            btn_buy_AS_IS.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            btn_buy.setBackground(getResources().getDrawable(R.drawable.round_button_fad));
                            if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                                ((TextView) rootView.findViewById(R.id.btn_manage_doc)).setVisibility(View.VISIBLE);
                            }

                        } else if (pro_type.equalsIgnoreCase("rent")) {
                            // btn_message.setVisibility(View.GONE);
                            btn_buy.setVisibility(View.GONE);
                            btn_buy_AS_IS.setVisibility(View.GONE);
                            btn_request.setVisibility(View.VISIBLE);
                            btn_tour.setVisibility(View.VISIBLE);
                            btn_message.setVisibility(View.VISIBLE);

                        } else {
                            if ((userData.getPost().equalsIgnoreCase("Fixed") || userData.getPost().equalsIgnoreCase("Both")) && (userData.getIs_sold().equalsIgnoreCase("0"))) {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                                Update_btn_status();

                            } else if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                                if ((userData.getPay_status().equals("1") && (userData.getProperty_for().equalsIgnoreCase("1")))) {
                                    ((TextView) rootView.findViewById(R.id.btn_manage_doc)).setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e(TAG, "=============: " + "else");
                                btn_message.setVisibility(View.GONE);
                                btn_buy.setVisibility(View.GONE);
                                btn_buy_AS_IS.setVisibility(View.GONE);
                            }
                        }
                        /*if (userData.getIs_sold().equalsIgnoreCase("1")) {
                            btn_buy.setVisibility(View.GONE);
                            btn_buy_AS_IS.setVisibility(View.GONE);
                        }*/
                        if (userData.getFixed_price().equalsIgnoreCase("0"))
                            amount = userData.getMinimum_price();
                        else
                            amount = userData.getFixed_price();


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

    private void Update_btn_status() {
        if (userData.getAvailability().equalsIgnoreCase("3")) {
            btn_message.setVisibility(View.VISIBLE);
        } else if (userData.getAvailability().equalsIgnoreCase("1")) {
            Calendar now = Calendar.getInstance();
            //1)08:00 to 17:00   || 2)17.00 to 23:59
            int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
            int minute = now.get(Calendar.MINUTE);
            Date date = parseDate(hour + ":" + minute);
            Date dateCompareOne = parseDate("08:00");
            Date dateCompareTwo = parseDate("17:00");
            if (dateCompareOne.before(date) && dateCompareTwo.after(date)) {
                btn_message.setVisibility(View.VISIBLE);
            } else {
                btn_message.setVisibility(View.GONE);
            }

        } else if (userData.getAvailability().equalsIgnoreCase("2")) {
            Calendar now = Calendar.getInstance();
            //1)08:00 to 17:00   || 2)17.00 to 23:59
            int hour = now.get(Calendar.HOUR_OF_DAY); // Get hour in 24 hour format
            int minute = now.get(Calendar.MINUTE);
            Date date = parseDate(hour + ":" + minute);
            Date dateCompareOne = parseDate("17:00");
            Date dateCompareTwo = parseDate("23:59");
            if (dateCompareOne.before(date) && dateCompareTwo.after(date)) {
                btn_message.setVisibility(View.VISIBLE);
            } else {
                btn_message.setVisibility(View.GONE);
            }

        }
    }

    private Date parseDate(String date) {

        final String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    private void ShowDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_rent_tour_request);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        imageView_cross = dialog.findViewById(R.id.cross);
        btn_submit_dialog = dialog.findViewById(R.id.btn_submit_dialog);
        txtdate = dialog.findViewById(R.id.date);
        txttime = dialog.findViewById(R.id.time);
        txtname = dialog.findViewById(R.id.name);
        txtemail = dialog.findViewById(R.id.email);
        txtcontact = dialog.findViewById(R.id.mobile);
        btn_submit_dialog.setOnClickListener(this);
        txtdate.setOnClickListener(this);
        txttime.setOnClickListener(this);
        imageView_cross.setOnClickListener(this);
        dialog.show();
    }

    private void PickDate() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        txtdate.setText(formatted_date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis() + (1000 * 60 * 60 * 24 * 7));

    }

    private void PickTime() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // currentTime = mHour + ":" + mMinute;
        boolean isPM = (mHour >= 12);
        // currentTime = String.format("%02d:%02d %s", (mHour == 12 || mHour == 0) ? 12 : mHour % 12, mMinute, isPM ? "PM" : "AM");
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        txttime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private String fun_dateFormat(String date) {
        String formatted_date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = dateFormat.parse(date);
            formatted_date = dateFormat.format(d);
            if (formatted_date.isEmpty()) {
                formatted_date = date;
            }
            //System.out.println("=====date"+d);
            //System.out.println("======Formated"+);
        } catch (Exception e) {
            formatted_date = date;
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep" + e);
        }
        return formatted_date;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ctv_contact.getText().toString())));
                } else {

                }
                return;
            }
        }
    }
}
