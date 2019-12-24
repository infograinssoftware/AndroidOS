package com.open_source.ServiceProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.Subcatogery;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class UserApply extends AppCompatActivity implements View.OnClickListener {
    final int PERMISSION_REQUEST_CODE = 101;
    TextView txt_date, btn_proceed, toolbar_title;
    EditText et_problem;
    Context context;
    Toolbar toolbar;
    ProgressHUD progressHUD;
    ImageView img_add_service;
    File photoFile = null, file_image;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    ArrayList<Subcatogery> array_images = new ArrayList<>();
    ImageAdapter imageAdapter;
    RecyclerView recycle_service_img;
    private int mYear, mMonth, mDay;
    private String imageFilePath = "",str_problem_images="";
    private String TAG = UserApply.class.getSimpleName();

    private AutoCompleteTextView txt_location;
    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_apply);
        context = UserApply.this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.servide_provider);
        recycle_service_img=findViewById(R.id.recycle_service_img);
        txt_location = findViewById(R.id.desired_location);
        txt_date = findViewById(R.id.date);
        et_problem = findViewById(R.id.problem);
        btn_proceed = findViewById(R.id.btn_proceed);
        img_add_service = findViewById(R.id.img_add_service);
        img_add_service.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
        txt_location.setOnClickListener(this);
        txt_date.setOnClickListener(this);
        recycle_service_img.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(context, array_images);
        recycle_service_img.setAdapter(imageAdapter);
        initialisePlacePicker();
    }

    private void initialisePlacePicker(){
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(), getResources().getString(R.string.mapkey));
        }
        placesClient = Places.createClient(this);
        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {
        txt_location.setThreshold(1);
        txt_location.setOnItemClickListener(autocompleteClickListener);
        autoCompleteAdapter = new AutoCompleteAdapter(this, placesClient);
        txt_location.setAdapter(autoCompleteAdapter);
    }

    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try {
                final AutocompletePrediction item = autoCompleteAdapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }
                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }
                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Place place = task.getPlace();
                            txt_location.setText(place.getAddress());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            txt_location.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_proceed:
                if (txt_location.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_problem_description));
                else if (txt_date.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter) + getString(R.string.desired_date));
                else if (et_problem.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_problem_description));
                else
                {
                    for (int i = 0; i < array_images.size(); i++) {
                        if (i == 0)
                            str_problem_images = array_images.get(i).getId();
                        else
                            str_problem_images = str_problem_images+ "," + array_images.get(i).getId();
                    }
                    FunApply();
                }

                break;
            case R.id.date:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txt_date.setText(Utility.fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                break;
            case R.id.desired_location:
                /*if (Utility.checkPlayServices(context)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(UserApply.this);
                        startActivityForResult(intent, 4);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
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
        }
    }

    //----------------------select image-------------------------------------------------
    private void selectImage() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery),getString(R.string.close)};
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

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(UserApply.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(UserApply.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(UserApply.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(UserApply.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void FunApply() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SP_Service_Apply(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    txt_date.getText().toString(),
                    txt_location.getText().toString(),
                    "452001", getIntent().getExtras().getString(Constants.USER_ID, ""),
                    et_problem.getText().toString(),str_problem_images).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
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
            }
            if (requestCode == 4) {
               /* if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(context, data);
                    txt_location.setText(place.getAddress());
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(context, data);
                    // TODO: Handle the error.
                    //Log.i("====", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }*/
            }

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
            holder.img_delete.setOnClickListener(new ImageAdapter.OnMyClick(position));

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
