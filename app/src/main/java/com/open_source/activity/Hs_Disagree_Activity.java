package com.open_source.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Hs_Disagree_Activity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = Hs_Disagree_Activity.class.getSimpleName();
    EditText edit_reason;
    TextView btn_send, edit_date, edit_time;
    private Context context;
    private ProgressHUD progressHUD;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_disagree);
        context = this;
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.walk_through);
        edit_date = findViewById(R.id.date);
        edit_time = findViewById(R.id.edit_time);
        edit_reason = findViewById(R.id.edit_reason);
        btn_send = findViewById(R.id.btn_send);

        edit_time.setOnClickListener(this);
        edit_date.setOnClickListener(this);
        btn_send.setOnClickListener(this);


    }

    private void SendData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().HS_Disagree(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    edit_date.getText().toString(),
                    edit_time.getText().toString(),
                    getIntent().getExtras().getString(Constants.Walk_Through_Id),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID), edit_reason.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            finish();


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

    private void PickDate() {
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = "";
                        formatted_date = Utility.fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        edit_date.setText(formatted_date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void PickTime() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        edit_time.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (edit_reason.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.reason));
                } else if (edit_date.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.error_select_date);
                } else if (edit_time.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.select_time);
                } else {
                    SendData();
                }
                break;
            case R.id.edit_time:
                PickTime();
                break;

            case R.id.date:
                PickDate();
                break;
        }


    }
}
