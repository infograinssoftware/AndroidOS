package com.open_source.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OfferConversationActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = OfferConversationActivity.class.getSimpleName();
    OfferAdapter offerAdapter;
    ArrayList<RetroObject> arrayList = new ArrayList<>();
    LinearLayout calender_30days, calender_45days;
    Boolean flag = false;
    DatePickerDialog datePickerDialog;
    private RecyclerView offer_recycle;
    private Context context;
    private ProgressHUD progressHUD;
    private String offer_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        context = this;
        init_view();
    }

    private void init_view() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.all_offer);
        calender_30days = findViewById(R.id.calender_30days);
        calender_45days = findViewById(R.id.calender_45days);
        calender_30days.setOnClickListener(this);
        calender_45days.setOnClickListener(this);
        offer_recycle = findViewById(R.id.recyclerView);
        offer_recycle.setLayoutManager(new LinearLayoutManager(context));
     /*   offerAdapter = new OfferAdapter(context, arrayList);
        offer_recycle.setAdapter(offerAdapter);*/
        OfferDetail();
    }

    private void OfferDetail() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().AllOffer(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID),
                    getIntent().getExtras().getString(Constants.USER_ID), "0").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            //  Utility.ShowToastMessage(context, response.body().getMessage());
                            arrayList = response.body().getObject();
                            offerAdapter = new OfferAdapter(context, arrayList);
                            offer_recycle.setAdapter(offerAdapter);
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
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
            case R.id.calender_30days:
                if (flag == true)
                    startActivity(new Intent(context, CalenderTask.class).putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID)));
                else
                    datePicker(30);
                break;
            case R.id.calender_45days:
                if (flag == true)
                    startActivity(new Intent(context, CalenderTask.class).putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID)));
                else
                    datePicker(45);
                break;
        }
    }

    private void datePicker(final int days) {
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        datePickerDialog.dismiss();
                        startActivity(new Intent(context, CalenderTask.class).putExtra("Days", String.valueOf(days)).
                                putExtra("start", dayOfMonth + "/" + (monthOfYear + 1) + "/" + year).
                                putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID)).putExtra(Constants.OFFER_ID, offer_id));


                    }
                }, mYear, mMonth, mDay);
        //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {
        Context context;
        ArrayList<RetroObject> arrayList;


        public OfferAdapter(Context context, ArrayList<RetroObject> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }

        @NonNull
        @Override
        public OfferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new OfferAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_offer_by_me, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull OfferAdapter.MyViewHolder holder, int position) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.colorfullusers);
            requestOptions.error(R.drawable.colorfullusers);
            if (arrayList.get(position).getOffer_status().equals("0") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("normal")) {
                holder.card_offer_first.setVisibility(View.VISIBLE);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_first);
                holder.offer_price_first.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_first.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("1") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("normal")) {
                holder.card_sender.setVisibility(View.VISIBLE);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_sender);
                holder.offer_price_sender.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_Sender.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("1") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("couter")) {
                holder.card_sender.setVisibility(View.VISIBLE);
                holder.offer_sender.setText(R.string.counter_offer);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_sender);
                holder.offer_price_sender.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_Sender.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("0") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("normal")) {
                holder.card_me.setVisibility(View.VISIBLE);
                holder.offer_counterlevel.setText(R.string.offer);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("1") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("normal")) {
                holder.card_me.setVisibility(View.VISIBLE);
                holder.offer_counterlevel.setText(R.string.offer);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("1") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("couter")) {
                holder.card_me.setVisibility(View.VISIBLE);
                holder.offer_counterlevel.setText(R.string.counter_offer);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            } else if (arrayList.get(position).getOffer_status().equals("0") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("couter")) {
                holder.card_me.setVisibility(View.VISIBLE);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("0") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID)) && arrayList.get(position).getMake_offer().equals("couter")) {
                holder.card_offer_first.setVisibility(View.VISIBLE);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_first);
                holder.offer_price_first.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                /*holder.btn_accept.setOnClickListener(new OfferAdapter.MyClickListener(position));
                holder.btn_counter_offer.setOnClickListener(new OfferAdapter.MyClickListener(position));*/
                holder.offer_first.setText(getString(R.string.counter_offer));
                holder.created_at_first.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
            }
            if (arrayList.get(position).getOffer_status().equals("2") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                holder.card_sender.setVisibility(View.VISIBLE);
                holder.offer_sender.setText(R.string.offer_close);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_sender);
                holder.offer_price_sender.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_Sender.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
                Intent intent = getIntent();
                if (!intent.hasExtra(Constants.USER_ID)) {
                    ((TextView) findViewById(R.id.calender_level)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.lay_calender)).setVisibility(View.VISIBLE);
                    offer_id = arrayList.get(position).getOffer_id();
                }
            }
            if (arrayList.get(position).getOffer_status().equals("2") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                holder.card_me.setVisibility(View.VISIBLE);
                holder.offer_counterlevel.setText(R.string.offer_close);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
                Intent intent = getIntent();
                if (!intent.hasExtra(Constants.USER_ID)) {
                    ((TextView) findViewById(R.id.calender_level)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.lay_calender)).setVisibility(View.VISIBLE);
                    offer_id = arrayList.get(position).getOffer_id();
                }
            }

            if (arrayList.get(position).getOffer_status().equals("4") && !arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                holder.card_sender.setVisibility(View.VISIBLE);
                holder.offer_sender.setText(R.string.offer_close);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_sender);
                holder.offer_price_sender.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_Sender.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
                Intent intent = getIntent();
                if (!intent.hasExtra(Constants.USER_ID)) {
                    ((LinearLayout) findViewById(R.id.lay_calender)).setVisibility(View.VISIBLE);
                    offer_id = arrayList.get(position).getOffer_id();
                    flag = true;
                    // calender_30days.setClickable(false);
                    // calender_45days.setClickable(false);
                    if (arrayList.get(position).getCalendar_type().equals("30")) {
                        calender_30days.setVisibility(View.VISIBLE);
                        calender_45days.setVisibility(View.GONE);
                    } else if (arrayList.get(position).getCalendar_type().equals("45")) {
                        calender_30days.setVisibility(View.GONE);
                        calender_45days.setVisibility(View.VISIBLE);
                    }
                }
            }
            if (arrayList.get(position).getOffer_status().equals("4") && arrayList.get(position).getUser_id().equals(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                holder.card_me.setVisibility(View.VISIBLE);
                holder.offer_counterlevel.setText(R.string.offer_close);
                Glide.with(context).load(arrayList.get(position).getProfileImage()).apply(requestOptions).into(holder.profile_img_counter_me);
                holder.offer_counter_me.setText(getString(R.string.offer_price) + arrayList.get(position).getAmount());
                holder.created_at_me.setText(getString(R.string.offer_created_at) + arrayList.get(position).getCreated_at());
                Intent intent = getIntent();
                if (!intent.hasExtra(Constants.USER_ID)) {
                    ((LinearLayout) findViewById(R.id.lay_calender)).setVisibility(View.VISIBLE);
                    offer_id = arrayList.get(position).getOffer_id();
                    flag = true;
                    //  calender_30days.setClickable(false);
                    //  calender_45days.setClickable(false);
                    if (arrayList.get(position).getCalendar_type().equals("30")) {
                        calender_30days.setVisibility(View.VISIBLE);
                        calender_45days.setVisibility(View.GONE);
                    } else if (arrayList.get(position).getCalendar_type().equals("45")) {
                        calender_30days.setVisibility(View.GONE);
                        calender_45days.setVisibility(View.VISIBLE);
                    }
                }
            }
            holder.btn_accept.setOnClickListener(new MyClickListener(position));
            holder.btn_reject.setOnClickListener(new MyClickListener(position));
            holder.btn_counter_offer.setOnClickListener(new MyClickListener(position));
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CardView card_sender, card_me, card_offer_first;
            CircleImageView profile_img_sender, profile_img_counter_me, profile_img_first;
            TextView offer_price_sender, offer_counter_me, offer_price_first, btn_accept, btn_reject,
                    btn_counter_offer, offer_first, offer_counterlevel, offer_sender, created_at_Sender, created_at_me, created_at_first;

            public MyViewHolder(View itemView) {
                super(itemView);
                card_sender = itemView.findViewById(R.id.card_sender);
                card_me = itemView.findViewById(R.id.card_me);
                card_offer_first = itemView.findViewById(R.id.card_offer_first);
                profile_img_sender = itemView.findViewById(R.id.profile_img_sender);
                profile_img_counter_me = itemView.findViewById(R.id.profile_img_counter_me);
                profile_img_first = itemView.findViewById(R.id.profile_img_first);
                offer_price_sender = itemView.findViewById(R.id.offer_price_sender);
                offer_counter_me = itemView.findViewById(R.id.offer_counter_me);
                offer_price_first = itemView.findViewById(R.id.offer_price_first);
                btn_accept = itemView.findViewById(R.id.btn_accept);
                btn_reject = itemView.findViewById(R.id.btn_reject);
                btn_counter_offer = itemView.findViewById(R.id.btn_counter_offer);
                offer_first = itemView.findViewById(R.id.offer_first);
                offer_counterlevel = itemView.findViewById(R.id.offer_counterlevel);
                offer_sender = itemView.findViewById(R.id.offer_sender);
                created_at_Sender = itemView.findViewById(R.id.created_at_Sender);
                created_at_me = itemView.findViewById(R.id.created_at_me);
                created_at_first = itemView.findViewById(R.id.created_at_first);
            }
        }

        private class MyClickListener implements View.OnClickListener {
            int position;

            public MyClickListener(int i) {
                position = i;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_accept:
                        if (Utility.isConnectingToInternet(context)) {
                            Fun_Accept_Reject("2");
                        } else {
                            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
                        }
                        break;
                    case R.id.btn_counter_offer:
                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.counter_offer);

                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                        Window window = dialog.getWindow();

                        layoutParams.copyFrom(window.getAttributes());
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        layoutParams.gravity = Gravity.CENTER;
                        window.setAttributes(layoutParams);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
                        final EditText edit_amount = dialog.findViewById(R.id.edit_amount);

                        ((ImageView) dialog.findViewById(R.id.img_cross)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        ((TextView) dialog.findViewById(R.id.btn_send)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!edit_amount.getText().toString().isEmpty()) {
                                    if (Utility.isConnectingToInternet(context)) {
                                        //  progressHUD = ProgressHUD.show(context, true, false, null);
                                        RetrofitClient.getAPIService().CounterOffer(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                                                arrayList.get(position).getProperty_id(), arrayList.get(position).getOffer_id(), arrayList.get(position).getUser_id(),
                                                edit_amount.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                                            @Override
                                            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                                /*if (progressHUD != null && progressHUD.isShowing()) {
                                                    progressHUD.dismiss();
                                                }*/
                                                dialog.dismiss();
                                                try {
                                                    if (response.body().getStatus() == 200) {
                                                        Utility.ShowToastMessage(context, response.body().getMessage());
                                                        OfferDetail();

                                                    } else if (response.body().getStatus() == 401) {
                                                        SharedPref.clearPreference(context);
                                                        startActivity(new Intent(context, WelcomeActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                                        finish();
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
                                } else {
                                    Utility.ShowToastMessage(context, getString(R.string.enter_amount));
                                }

                            }
                        });
                        dialog.show();
                        break;
                    case R.id.btn_reject:
                        if (Utility.isConnectingToInternet(context)) {
                            Fun_Accept_Reject("3");
                        } else {
                            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
                        }
                        break;
                }
            }

            private void Fun_Accept_Reject(final String status) {
                progressHUD = ProgressHUD.show(context, true, false, null);
                RetrofitClient.getAPIService().Accept_Offer(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                        arrayList.get(position).getProperty_id(),
                        arrayList.get(position).getOffer_id(), status).enqueue(new Callback<RetrofitUserData>() {
                    @Override
                    public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        try {
                            if (response.body().getStatus() == 200) {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                                if (status.equals("2")) {
                                    OfferDetail();
                                    /*Intent intent = getIntent();
                                    if (!intent.hasExtra(Constants.USER_ID)) {
                                        ((TextView) findViewById(R.id.calender_level)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.lay_calender)).setVisibility(View.VISIBLE);
                                    }*/
                                }
                            } else if (response.body().getStatus() == 401) {
                                SharedPref.clearPreference(context);
                                startActivity(new Intent(context, WelcomeActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
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
            }
        }

    }
}
