package com.open_source.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.fragment.RentFormFirst;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class RenterReferenceCheckActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = RenterReferenceCheckActivity.class.getSimpleName();
    final int PERMISSION_REQUEST_CODE = 101;
    Toolbar toolbar;
    TextView txt_title, txt_start_date, txt_end_date;
    RelativeLayout rel_signature;
    File signature_file = null;
    String[] requestedPermissions = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    String signaturepath = "", str_type = "";
    ImageView signature_image;
    EditText et_user_name, et_mobile, et_security_check;
    ProgressHUD progressHUD;
    CheckBox check_agree;
    int id_rent = -1;
    RadioGroup radio_group_rent;
    private Context context;
    private int SIGNATURE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rent_reference_check);
        context = this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txt_title = findViewById(R.id.toolbar_title);
        txt_title.setText(R.string.disclaimer_form);
        rel_signature = findViewById(R.id.rel_signature);
        rel_signature.setOnClickListener(this);
        ((Button) findViewById(R.id.button_submit)).setOnClickListener(this);
        signature_image = findViewById(R.id.signature_image);
        et_user_name = findViewById(R.id.et_user_name);
        et_mobile = findViewById(R.id.et_mobile);
        et_security_check = findViewById(R.id.et_security_check);
        check_agree = findViewById(R.id.checkbox);

        radio_group_rent = findViewById(R.id.rent_selection);
        radio_group_rent.setOnCheckedChangeListener(this);
        txt_start_date = findViewById(R.id.txt_start_date);
        txt_start_date.setOnClickListener(this);
        txt_end_date = findViewById(R.id.end_date);
        txt_end_date.setOnClickListener(this);


        if ((!SharedPref.getSharedPreferences(context, Constants.FIRST_NAME).isEmpty()) && (!SharedPref.getSharedPreferences(context, Constants.LAST_NAME).isEmpty())) {
            String name = SharedPref.getSharedPreferences(context, Constants.FIRST_NAME) + " " + SharedPref.getSharedPreferences(context, Constants.LAST_NAME);
            et_user_name.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
            et_user_name.setEnabled(false);
        }
        if (!SharedPref.getSharedPreferences(context, Constants.MOBILE_NUMBER).isEmpty()) {
            et_mobile.setText(SharedPref.getSharedPreferences(context, Constants.MOBILE_NUMBER));
            et_mobile.setEnabled(false);
        }

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ((EditText) findViewById(R.id.et_date)).setText(date);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_start_date:
                PickDate("start_date");
                break;
            case R.id.end_date:
                PickDate("end_date");
                break;
            case R.id.button_submit:
                if (id_rent == -1)
                    Utility.ShowToastMessage(context, getString(R.string.select_renter_type));
                else if (txt_start_date.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_rent_date));
                else if (txt_end_date.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_rent_enddate));
                else if (Utility.compaireDate(txt_start_date.getText().toString(), txt_end_date.getText().toString()) < 0)
                    Utility.ShowToastMessage(context, getString(R.string.end_date_validation));
                else if (et_user_name.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.enter_name);
                else if (et_mobile.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.enter_your_contact_no);
                else if (signature_file == null)
                    Utility.ShowToastMessage(context, getString(R.string.sign_val));
                else if (!check_agree.isChecked())
                    Utility.ShowToastMessage(context, getString(R.string.check_disclaimer));
                else {
                    //1(vacation) , 2(long_term)
                    if (id_rent == 0)
                        str_type = "vacation";
                    else
                        str_type = "long_term";
                    FunUpload();

                }

                break;
            case R.id.rel_signature:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        startActivityForResult(new Intent(context, SignatureActivity.class), SIGNATURE_REQUEST);
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    startActivityForResult(new Intent(context, SignatureActivity.class), SIGNATURE_REQUEST);
                }
                break;
        }
    }

    private void FunUpload() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart(Constants.NAME, SharedPref.getSharedPreferences(context, Constants.FIRST_NAME) + " " + SharedPref.getSharedPreferences(context, Constants.LAST_NAME));
            builder.addFormDataPart(Constants.MOBILE, et_mobile.getText().toString());
            builder.addFormDataPart(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID, ""));
            builder.addFormDataPart(Constants.SOCIAL_SECURITY_NUMBER, et_security_check.getText().toString());
            builder.addFormDataPart(Constants.START_DATE, txt_start_date.getText().toString());
            builder.addFormDataPart(Constants.END_DATE, txt_end_date.getText().toString());
            builder.addFormDataPart(Constants.RENTER_TYPE, str_type);
            builder.addFormDataPart(Constants.SIGNATURE_IMAGE, signature_file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), signature_file));
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().RentDisclaimer(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, RentFormFirst.class).putExtra(Constants.TYPE, getIntent().getExtras().getString(Constants.TYPE)).
                                            putExtra(Constants.ADDRESS, getIntent().getExtras().getString(Constants.ADDRESS)).
                                            putExtra(Constants.RENT_AMOUNT, getIntent().getExtras().getString(Constants.RENT_AMOUNT)).
                                            putExtra(Constants.PROPERTY_ID,getIntent().getExtras().getString(Constants.PROPERTY_ID)).
                                            putExtra(Constants.USER_ID, getIntent().getExtras().getString(Constants.USER_ID)).
                                            putExtra(Constants.RENTER_TYPE, getIntent().getExtras().getString(Constants.RENTER_TYPE)));
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

    private void PickDate(final String str) {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = "";
                        int month = monthOfYear + 1;
                        String formattedMonth = "" + month;
                        String formattedDayOfMonth = "" + dayOfMonth;
                        if(month < 10){

                            formattedMonth = "0" + month;
                        }
                        if(dayOfMonth < 10){

                            formattedDayOfMonth = "0" + dayOfMonth;
                        }
                        formatted_date=formattedMonth + "-" + formattedDayOfMonth + "-" + year;
                        if (str.equals("start_date")) {
                            txt_start_date.setText(formatted_date);
                        } else if (str.equals("end_date")) {
                            txt_end_date.setText(formatted_date);
                        }
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

/*    public static String fun_dateFormat(String date) {
        String formatted_date = "";
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        try {
            Date d = format.parse(date);
            formatted_date = format.format(d);
            if (formatted_date.isEmpty()) {
                formatted_date = date;
            }
           System.out.println("=====date"+d);
           System.out.println("======Formated"+formatted_date);
        } catch (Exception e) {
            formatted_date = date;
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep" + e);
        }
        return formatted_date;
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNATURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                System.out.println("path signature" + data.getStringExtra("image"));
                signaturepath = data.getStringExtra("image");
                if (!signaturepath.isEmpty()) {
                    signature_file = new File(signaturepath);
                    ((LinearLayout) findViewById(R.id.lay_signature)).setVisibility(View.VISIBLE);
                    Glide.with(context).load(signature_file).into(signature_image);
                }

            } else {
                Toast.makeText(context, "Cancel signature!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(RenterReferenceCheckActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(RenterReferenceCheckActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.storage_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(RenterReferenceCheckActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(context, SignatureActivity.class), SIGNATURE_REQUEST);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.permission_needed));
                builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).create().show();
            }
        }
    }

    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rent_selection:
                id_rent = radio_group_rent.indexOfChild(radio_group_rent.findViewById(radio_group_rent.getCheckedRadioButtonId()));
                if (id_rent == 0) {
                    ((TextView) findViewById(R.id.level_security_check)).setVisibility(View.GONE);
                    ((EditText) findViewById(R.id.et_security_check)).setVisibility(View.GONE);
                } else {
                    ((TextView) findViewById(R.id.level_security_check)).setVisibility(View.VISIBLE);
                    ((EditText) findViewById(R.id.et_security_check)).setVisibility(View.VISIBLE);
                }

                break;
        }
    }
}
