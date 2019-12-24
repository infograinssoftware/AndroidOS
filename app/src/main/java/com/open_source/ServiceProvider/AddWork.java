package com.open_source.ServiceProvider;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ConfirmPaymentMessage;
import com.open_source.activity.StripeWeb;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.SubCatogeryAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.Subcatogery;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddWork extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = AddWork.class.getSimpleName();
    public static final int PAYPAL_REQUEST_CODE = 101;
    final int PERMISSION_REQUEST_CODE = 101;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    public static ArrayList<Subcatogery> array_list = new ArrayList<>();
    public static SubCatogeryAdapter subCatogeryAdapter;
    public static int work_status = 0;
    ArrayList<Subcatogery> array_images = new ArrayList<>();
    ArrayList<String> array_days = new ArrayList<>();
    ArrayList<String> start_time = new ArrayList<>();
    ArrayList<String> end_time = new ArrayList<>();
    ArrayList<String> array_id = new ArrayList<>();
    ArrayList<String> array_server_days = new ArrayList<>();
    ArrayAdapter<String> serviceadapter;
    HashMap<String, String> service_map = new HashMap<>();
    DaysAdapter daysAdapter;
    ImageAdapter imageAdapter;
    Context context;
    ProgressHUD progressHUD;
    MaterialSpinner spn_catogery;
    Spinner spn_days_from, spn_days_to;
    RecyclerView recycle_sub_catogery, recycle_days, recycle_service_img;
    ImageView img_add_sub_cat, cross, img_add_service;
    Dialog dialog;
    TextView toolbar_title;
    EditText et_service, et_price, et_work_description;
    Button btn_cancle, btn_set, btn_submit;
    Toolbar toolbar;
    File photoFile = null, file_image;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);
    private String imageFilePath = "", str_catogery = "", str_start = "", str_end = "", sub_cat_name = "",
            sab_cat_price = "", str_working_days = "", str_image_id = "", method_type = "";


    public static void DeleteView(int position) {
        array_list.remove(position);
        subCatogeryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_work);
        context = AddWork.this;
        initview();
    }

    private void initview() {
        array_days.add(getString(R.string.monday));
        array_days.add(getString(R.string.tuesday));
        array_days.add(getString(R.string.wednesday));
        array_days.add(getString(R.string.thusday));
        array_days.add(getString(R.string.friday));
        array_days.add(getString(R.string.saturday));
        array_days.add(getString(R.string.sunday));
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.add_work);
        spn_catogery = findViewById(R.id.spn_buss_catogery);
        spn_days_from = findViewById(R.id.spn_days_from);
        spn_days_to = findViewById(R.id.spn_days_to);
        recycle_sub_catogery = findViewById(R.id.recycle_sub_catogery);
        recycle_days = findViewById(R.id.recycle_days);
        recycle_service_img = findViewById(R.id.recycle_service_img);
        et_work_description = findViewById(R.id.et_work_description);
        img_add_sub_cat = findViewById(R.id.img_add_sub_cat);
        img_add_service = findViewById(R.id.img_add_service);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        img_add_service.setOnClickListener(this);
        img_add_sub_cat.setOnClickListener(this);
        GetService();
        String value = "AM";
        start_time.add(getString(R.string.start_time));
        end_time.add(getString(R.string.end_time));
        for (int i = 1, j = 1; i < 25; i++) {
            if (i == 13) {
                value = "PM";
                j = 1;
            }
            start_time.add(j + ":00" + value);
            end_time.add(j + ":00" + value);
            j++;
        }
        FunSetAdapter();
    }

    private void FunSetAdapter() {
        ArrayAdapter<String> adapterStart = new ArrayAdapter<String>(this, R.layout.spinner_text, start_time);
        adapterStart.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_days_to.setAdapter(adapterStart);
        ArrayAdapter<String> adapterEnd = new ArrayAdapter<String>(this, R.layout.spinner_text, end_time);
        adapterEnd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_days_from.setAdapter(adapterEnd);

        recycle_sub_catogery.setLayoutManager(new LinearLayoutManager(this));
        subCatogeryAdapter = new SubCatogeryAdapter(context, array_list);
        recycle_sub_catogery.setAdapter(subCatogeryAdapter);

        recycle_days.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        daysAdapter = new DaysAdapter(context, array_days);
        recycle_days.setAdapter(daysAdapter);

        recycle_service_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(context, array_images);
        recycle_service_img.setAdapter(imageAdapter);
    }

    private void GetService() {
        if (Utility.isConnectingToInternet(context)) {
           // progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetAllService(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
//                    if (progressHUD != null && progressHUD.isShowing()) {
//                        progressHUD.dismiss();
//                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> array = response.body().getObject();
                            for (int i = 0; i < array.size(); i++) {
                                array_id.add(array.get(i).getName());
                                service_map.put(array.get(i).getName(), array.get(i).getService_id());

                            }
                            serviceadapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, array_id);
                            serviceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_catogery.setAdapter(serviceadapter);

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
//                    if (progressHUD != null && progressHUD.isShowing()) {
//                        progressHUD.dismiss();
//                    }
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
            case R.id.img_add_sub_cat:
                dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.dialog_add_sabcatogery);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
                et_service = dialog.findViewById(R.id.service_type);
                et_price = dialog.findViewById(R.id.service_price);
                btn_set = dialog.findViewById(R.id.btn_set);
                btn_cancle = dialog.findViewById(R.id.btn_cancle);
                cross = dialog.findViewById(R.id.cross);
                cross = dialog.findViewById(R.id.cross);
                cross.setOnClickListener(this);
                btn_set.setOnClickListener(this);
                btn_cancle.setOnClickListener(this);
                dialog.show();
                break;
            case R.id.btn_cancle:
                dialog.dismiss();
                break;
            case R.id.cross:
                dialog.dismiss();
                break;
            case R.id.btn_set:
                if (et_service.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_catogery));
                } else if (et_price.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_sab_catogery));
                } else {
                    Subcatogery subcatogery = new Subcatogery();
                    subcatogery.setName(et_service.getText().toString());
                    subcatogery.setPrice(et_price.getText().toString());
                    array_list.add(subcatogery);
                    subCatogeryAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                break;
            case R.id.img_add_service:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectImage();
                }

                break;
            case R.id.btn_submit:
                str_start = "";
                str_end = "";
                sub_cat_name = "";
                sab_cat_price = "";
                str_working_days = "";
                str_image_id = "";
                if (spn_catogery != null && spn_catogery.getSelectedItem() != null) {
                    str_catogery = spn_catogery.getSelectedItem().toString();
                }
                if (spn_days_from.getSelectedItem() != null) {
                    str_start = spn_days_from.getSelectedItem().toString();
                }
                if (spn_days_to.getSelectedItem() != null) {
                    str_end = spn_days_to.getSelectedItem().toString();
                }
                if (str_catogery.isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_buss_cat));
                else if (array_list.size() <= 0)
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_buss_skills));
                else if (array_server_days.size() <= 0)
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_working_days));
                else if (str_start.equals(getString(R.string.start_time)))
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_working_satrt_time));
                else if (str_end.equals(getString(R.string.end_time)))
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_working_end_time));
                else if (array_images.size() <= 0)
                    Utility.ShowToastMessage(context, getString(R.string.val_msg_work_image));
                else {
                    for (int i = 0; i < array_list.size(); i++) {
                        if (i == 0) {
                            sub_cat_name = array_list.get(i).getName();
                            sab_cat_price = array_list.get(i).getPrice();
                        } else {
                            sub_cat_name = sub_cat_name + "," + array_list.get(i).getName();
                            sab_cat_price = sab_cat_price + "," + array_list.get(i).getPrice();
                        }

                    }
                    for (int i = 0; i < array_server_days.size(); i++) {
                        if (i == 0)
                            str_working_days = array_server_days.get(i);
                        else
                            str_working_days = str_working_days + "," + array_server_days.get(i);
                    }
                    str_start = str_start + " to " + str_end;
                    for (int i = 0; i < array_images.size(); i++) {
                        if (i == 0)
                            str_image_id = array_images.get(i).getId();
                        else
                            str_image_id = str_image_id + "," + array_images.get(i).getId();
                    }
                    str_catogery = service_map.get(str_catogery);
//                    Log.e(TAG, "=============: " + sub_cat_name);
//                    Log.e(TAG, "=============: " + sab_cat_price);
//                    Log.e(TAG, "=============: " + str_working_days);
//                    Log.e(TAG, "=============: " + str_start);
//                    Log.e(TAG, "=============: " + str_image_id);
                    FunWorkUpload();
                }
                break;
        }
    }

    private void FunWorkUpload() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UploadWork(SharedPref.getSharedPreferences(context, Constants.TOKEN), str_catogery,
                    sub_cat_name, sab_cat_price, str_working_days, str_start, et_work_description.getText().toString(), str_image_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            work_status = 1;
                            array_list.clear();
                            if (response.body().getPayment_status().equals("0"))
                                FunPay();
                            else
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

    private void FunPay() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_payment);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        final LinearLayout lay_credit = dialog.findViewById(R.id.lay_credit);
        Button btn_paypal = dialog.findViewById(R.id.btn_paypal);
        Button btn_credit = dialog.findViewById(R.id.btn_credit);
        ImageView cross = dialog.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing())
                    dialog.dismiss();
                finish();
            }
        });
        btn_paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                method_type = "paypal";
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(39.99), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
            }
        });
        btn_credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(context, StripeWeb.class));
            }
        });
        dialog.show();
    }

    //----------------------select image-------------------------------------------------
    private void selectImage() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                 /*   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);*/
                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        Uri photoUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(pictureIntent, 1);
                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void Fun_Upload(final String path) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Part profile_img = null;
            if (file_image != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file_image);
                profile_img = MultipartBody.Part.createFormData("new_image", file_image.getName(), reqFile);
            }
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UploadImage(profile_img, SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Subcatogery subcatogery = new Subcatogery();
                            subcatogery.setId(response.body().getUserData().getImage_id());
                            subcatogery.setName(path);
                            array_images.add(subcatogery);
                            imageAdapter.notifyDataSetChanged();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Bitmap b = decodeFile(photoFile);
                if (b != null) {
                    imageFilePath = photoFile.getAbsolutePath();
                } else {

                }
                file_image = new File(imageFilePath);
                Fun_Upload(imageFilePath);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String path = null;
                try {
                    path = Utility.getFilePath(context, selectedImage);
                    file_image = new File(path);
                    try {
                        file_image = new Compressor(this).compressToFile(file_image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Fun_Upload(path);
            } else if (requestCode == PAYPAL_REQUEST_CODE) {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    com.paypal.android.sdk.payments.PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {
                            String paymentDetails = confirm.toJSONObject().toString(4);
                         //   Log.i("=====transaction_id", paymentDetails);
                            JSONObject jsonObject = new JSONObject(paymentDetails);
                            String transaction_id = jsonObject.getJSONObject("response").getString("id");
                          //  Log.e(TAG, "====transaction_id: " + transaction_id);
                            FunPayment(transaction_id);

                        } catch (JSONException e) {
                           // Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                        }
                    }
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                   // Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                    startActivity(new Intent(context, ConfirmPaymentMessage.class).
                            putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE,"add_work"));
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                  //  Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));

                }
            }
        }
    }

    private void FunPayment(String transaction_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunPayment(SharedPref.getSharedPreferences(context, Constants.TOKEN), transaction_id, method_type, "39.99").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            array_list.clear();
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

    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());
        // photoFile = new File(photoFile);
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        array_list.clear();
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

    public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.MyViewHolder> {
        Context context;
        ArrayList<String> List;

        public DaysAdapter(Context context, ArrayList<String> List) {
            this.context = context;
            this.List = List;
        }

        @NonNull
        @Override
        public DaysAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DaysAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_days, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DaysAdapter.MyViewHolder holder, final int position) {
            holder.txt_day.setText(List.get(position));
            holder.chk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((CheckBox) view).isChecked()) {
                        array_server_days.add(List.get(position));
                    } else {
                        array_server_days.remove(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return List.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CheckBox chk;
            TextView txt_day;

            public MyViewHolder(View itemView) {
                super(itemView);
                chk = itemView.findViewById(R.id.chk);
                txt_day = itemView.findViewById(R.id.txt_day);
            }
        }
    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
        Context context;
        ArrayList<Subcatogery> List;

        public ImageAdapter(Context context, ArrayList<Subcatogery> List) {
            this.context = context;
            this.List = List;
        }

        @Override
        public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, final int position) {
            Glide.with(context).load(List.get(position).getName()).into(holder.img_work);
            holder.img_delete.setOnClickListener(new OnMyClick(position));

        }

        @Override
        public int getItemCount() {
            return List.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView img_work, img_delete;

            public MyViewHolder(View itemView) {
                super(itemView);
                img_work = itemView.findViewById(R.id.img_work);
                img_delete = itemView.findViewById(R.id.img_delete);
            }
        }

        private class OnMyClick implements View.OnClickListener {
            int position;

            public OnMyClick(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_delete:
                        if (Utility.isConnectingToInternet(context)) {
                            progressHUD = ProgressHUD.show(context, true, false, null);
                            RetrofitClient.getAPIService().DeleteImage(SharedPref.getSharedPreferences(context, Constants.TOKEN), List.get(position).getId()).enqueue(new Callback<RetrofitUserData>() {
                                @Override
                                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                    if (progressHUD != null && progressHUD.isShowing()) {
                                        progressHUD.dismiss();
                                    }
                                    try {
                                        if (response.body().getStatus() == 200) {
                                            array_images.remove(position);
                                            //array_images.remove(List.get(position).getId());
                                            imageAdapter.notifyDataSetChanged();

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

                        break;
                }

            }
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddWork.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(AddWork.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(AddWork.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(AddWork.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();

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

}
