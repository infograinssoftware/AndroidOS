package com.open_source.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CalenderTask extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CalenderTask.class.getSimpleName();
    String days, cal_type;
    Integer cal_date_array[];
    Bundle bundle;
    ArrayList<String> array_id = new ArrayList<>();
    ArrayList<String> array_date = new ArrayList<>();
    private LinearLayout layout_task;
    private Context context;
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_result);
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
        toolbar_title.setText(R.string.task_list);
        layout_task = findViewById(R.id.layout_task);
        ((TextView) findViewById(R.id.btn_submit)).setOnClickListener(this);
        bundle = getIntent().getExtras();
        if (getIntent().hasExtra("Days")) {
            if (bundle.getString("Days", "").equals("30")) {
                days = "30";
                cal_type = bundle.getString("start");
                cal_date_array = new Integer[]{2, 3, 9, 9, 9, 11, 11, 13, 20, 21, 23, 25, 27, 30, 30};
            } else if (bundle.getString("Days", "").equals("45")) {
                days = "45";
                cal_type = bundle.getString("start");
                cal_date_array = new Integer[]{3, 5, 14, 14, 14, 17, 17, 20, 30, 32, 35, 37, 40, 45, 45};
            }
            TaskDetail();
        } else {
            ((TextView) findViewById(R.id.btn_submit)).setVisibility(View.GONE);
            TaskView();
        }
    }

    private void TaskView() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetCalTaks(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PROPERTYID)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> arrayList = response.body().getObject();
                            for (int i = 0; i < arrayList.size(); i++) {
                                View view = LayoutInflater.from(context).inflate(R.layout.row_task, layout_task, false);
                                try {
                                    ((TextView) view.findViewById(R.id.title)).setText(arrayList.get(i).getName());
                                    final TextView date = view.findViewById(R.id.date);
                                    date.setText(arrayList.get(i).getTask_dates());
                                    array_id.add(arrayList.get(i).getId());
                                    array_date.add(arrayList.get(i).getTask_dates());
                                    cal_type = arrayList.get(i).getCalendar_type();
                                    TextView txt_date = view.findViewById(R.id.change_date);
                                    txt_date.setVisibility(View.GONE);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                layout_task.addView(view);

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
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void TaskDetail() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetTaskList(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> arrayList = response.body().getObject();
                            for (int i = 0; i < arrayList.size(); i++) {
                                View view = LayoutInflater.from(context).inflate(R.layout.row_task, layout_task, false);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                Calendar c = Calendar.getInstance();
                                try {
                                    c.setTime(sdf.parse(cal_type));
                                    c.add(Calendar.DAY_OF_MONTH, cal_date_array[i]);
                                    ((TextView) view.findViewById(R.id.title)).setText(arrayList.get(i).getName());
                                    final TextView date = view.findViewById(R.id.date);
                                    date.setText(sdf.format(c.getTime()));
                                    TextView txt_date = view.findViewById(R.id.change_date);
                                    final int finalI = i;
                                    txt_date.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int mYear, mMonth, mDay;
                                            final Calendar c = Calendar.getInstance();
                                            mYear = c.get(Calendar.YEAR);
                                            mMonth = c.get(Calendar.MONTH);
                                            mDay = c.get(Calendar.DAY_OF_MONTH);
                                            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                            date.setText(Utility.fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));
                                                            array_date.set(finalI, date.getText().toString());
                                                            //startActivity(new Intent(context, CalenderTask.class).putExtra("Days", String.valueOf(days)).putExtra("start", dayOfMonth+"/" +(monthOfYear + 1)+"/" +year));

                                                        }
                                                    }, mYear, mMonth, mDay);
                                            //datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                            datePickerDialog.show();
                                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                                            //  datePicker(date);
                                        }
                                    });
                                    array_id.add(arrayList.get(i).getId());
                                    array_date.add(sdf.format(c.getTime()));
                                    //  textViewsArray[i].setText();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                layout_task.addView(view);

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
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String id = TextUtils.join(",", array_id);
                String date = TextUtils.join(",", array_date);
               /* Log.e(TAG, "==============: "+id);
                Log.e(TAG, "==============: "+date);*/
                Fun_Submit_Task(id, date);
                break;
        }
    }

    private void Fun_Submit_Task(String id, String date) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SubmitCal(SharedPref.getSharedPreferences(context, Constants.TOKEN), id, date,
                    getIntent().getExtras().getString(Constants.PROPERTY_ID),
                    getIntent().getExtras().getString(Constants.OFFER_ID),
                    bundle.getString("Days", "")).enqueue(new Callback<RetrofitUserData>() {
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
}
