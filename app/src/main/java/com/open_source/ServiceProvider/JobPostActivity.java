package com.open_source.ServiceProvider;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
import com.open_source.util.Action;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.open_source.videocompression.MediaController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class JobPostActivity extends AppCompatActivity implements View.OnClickListener {
    final int PERMISSION_REQUEST_CODE = 101;
    final int VIDEOCAMERA_REQUEST_CODE = 3;
    final int VIDEOGALLERY_REQUEST_CODE = 4;
    ProgressHUD progressHUD;
    File photoFile = null, file_image, videofile = null, CompressvideoFile = null;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    ArrayList<Subcatogery> array_images = new ArrayList<>();
    JobPostActivity.ImageAdapter imageAdapter;
    RecyclerView recycle_attach;
    Toolbar toolbar;
    Context context;
    String service_id = "", video_id = "";
    EditText ed_min_budget, ed_max_budget, problem_description;
    TextView date;
    private AutoCompleteTextView location;
    double latitude, logitude;
    Uri videoUri;
    Bitmap video_bitmap;
    ArrayList<String> dataT = new ArrayList<>();
    private int mYear, mMonth, mDay;
    private ImageView imgpickvideo, imgplay, delete_video;
    private String imageFilePath = "", str_problem_images = "", strvideofile = "";
    private String TAG = JobPostActivity.class.getSimpleName();
    private int type = 0;


    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_service);
        context = JobPostActivity.this;
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.post_a_project);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) findViewById(R.id.post_catogery)).setText(getIntent().getExtras().getString(Constants.SUB_CAT_NAME));
        service_id = getIntent().getExtras().getString(Constants.SERVICE_ID, "");
        location = findViewById(R.id.location);
        location.setOnClickListener(this);
        ((TextView) findViewById(R.id.attachment)).setOnClickListener(this);
        date = findViewById(R.id.date);
        date.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_post)).setOnClickListener(this);
        ed_min_budget = findViewById(R.id.ed_min_budget);
        ed_max_budget = findViewById(R.id.ed_max_budget);
        problem_description = findViewById(R.id.problem_description);
        recycle_attach = findViewById(R.id.recycle_attach);
        imgpickvideo = findViewById(R.id.imgvideo);
        imgplay = findViewById(R.id.imgplay);
        delete_video = findViewById(R.id.delete_video);
        imgplay.setOnClickListener(this);
        imgpickvideo.setOnClickListener(this);
        delete_video.setOnClickListener(this);
        recycle_attach.setLayoutManager((new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)));
        imageAdapter = new ImageAdapter(context, array_images);
        recycle_attach.setAdapter(imageAdapter);
        initialisePlacePicker();
    }

    private void initialisePlacePicker(){
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(),getResources().getString(R.string.mapkey));
        }
        placesClient = Places.createClient(this);
        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {
        location.setThreshold(1);
        location.setOnItemClickListener(autocompleteClickListener);
        autoCompleteAdapter = new AutoCompleteAdapter(this, placesClient);
        location.setAdapter(autoCompleteAdapter);
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
                            location.setText(place.getAddress());
                            String str_latLng = String.valueOf(place.getLatLng());
                            str_latLng = str_latLng.replace("lat/lng: ", "");
                            str_latLng = str_latLng.replace("(", "");
                            str_latLng = str_latLng.replace(")", "");
                            String[] array_latLng = str_latLng.split(",");
                            latitude = Double.valueOf(array_latLng[0]);
                            logitude = Double.valueOf(array_latLng[1]);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            location.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_video:
                if (!video_id.isEmpty()) {
                    if (Utility.isConnectingToInternet(context)) {
                        progressHUD = ProgressHUD.show(context, true, false, null);
                        RetrofitClient.getAPIService().DeleteImage(SharedPref.getSharedPreferences(context, Constants.TOKEN), video_id).enqueue(new Callback<RetrofitUserData>() {
                            @Override
                            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                if (progressHUD != null && progressHUD.isShowing()) {
                                    progressHUD.dismiss();
                                }
                                try {
                                    if (response.body().getStatus() == 200) {
                                        video_id = "";
                                        videofile = null;
                                        delete_video.setVisibility(View.GONE);
                                        imgplay.setVisibility(View.GONE);
                                        imgpickvideo.setImageResource(0);
                                        imgpickvideo.setBackground(getResources().getDrawable(R.drawable.videoadd));

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
                    videofile = null;
                    delete_video.setVisibility(View.GONE);
                    imgplay.setVisibility(View.GONE);
                    imgpickvideo.setImageResource(0);
                    imgpickvideo.setBackground(getResources().getDrawable(R.drawable.videoadd));
                }
                break;
            case R.id.location:
               /* if (Utility.checkPlayServices(context)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(JobPostActivity.this);
                        startActivityForResult(intent, 5);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
                break;

            case R.id.attachment:
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

            case R.id.date:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.setText(Utility.fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year));

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
               // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                break;
            case R.id.imgvideo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectVideo();
                    } else {
                        type = 1;
                        requestCameraPermission();
                    }
                } else {
                    selectVideo();
                }
                break;
            case R.id.btn_post:
                if (ed_min_budget.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_min_budget));
                else if (ed_max_budget.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_max_budget));
                else if (location.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_desired_service_location));
                else if (date.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter) + getString(R.string.desired_date));
                else if (problem_description.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_problem_description));
                else if (array_images.size() < 5)
                    Utility.ShowToastMessage(context, getString(R.string.val_sp_upload_check));
                else {
                    int min = Integer.parseInt(ed_min_budget.getText().toString());
                    int max = Integer.parseInt(ed_max_budget.getText().toString());
                    if (max < min) {
                        Utility.ShowToastMessage(context, getString(R.string.val_budget));
                    } else {
                        for (int i = 0; i < array_images.size(); i++) {
                            if (i == 0)
                                str_problem_images = array_images.get(i).getId();
                            else
                                str_problem_images = str_problem_images + "," + array_images.get(i).getId();
                        }
                        FunPost();
                    }
                }
                break;
        }
    }

    private void selectVideo() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.add_video));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(takeVideoIntent, VIDEOCAMERA_REQUEST_CODE);

                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, VIDEOGALLERY_REQUEST_CODE);
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void FunPost() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunAddPost(SharedPref.getSharedPreferences(context, Constants.TOKEN), service_id,
                    ed_min_budget.getText().toString(), ed_max_budget.getText().toString(), location.getText().toString(),
                    latitude + "", logitude + "", problem_description.getText().toString(),
                    str_problem_images, date.getText().toString(), video_id).enqueue(new Callback<RetrofitUserData>() {
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
                    Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
                    startActivityForResult(i, 200);
                   /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);*/
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(JobPostActivity.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(JobPostActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(JobPostActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.storage_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(JobPostActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
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

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dataT.clear();
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Bitmap b = decodeFile(photoFile);
                if (b != null) {
                    imageFilePath = photoFile.getAbsolutePath();
                    dataT.add(imageFilePath);
                    Fun_Upload_MultiImage(dataT);
                } else {

                }
                //file_image = new File(imageFilePath);

                //Fun_Upload(imageFilePath, "image");
            } /*else if (requestCode == 2) {
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
                Fun_Upload(path, "image");
            }*/ else if (requestCode == VIDEOCAMERA_REQUEST_CODE) {
                videoUri = data.getData();
                try {
                    strvideofile = Utility.getFilePath(context, videoUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                videofile = new File(strvideofile);
                long length = Integer.parseInt(String.valueOf(videofile.length()));
                if (length > 40000000) {
                    Utility.ShowToastMessage(context, getString(R.string.file_size_limit));
                } else {
                    imgplay.setVisibility(View.VISIBLE);
                    delete_video.setVisibility(View.VISIBLE);
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                    if (length > 1024 * 1024) {
                        new VideoCompressor().execute();
                    } else {
                        /*File video_file = null;
                        if (video_bitmap != null) {
                            video_file = persistImage(video_bitmap, "video_thumb");
                        }*/
                        // progressHUD = ProgressHUD.show(context, true, false, null);
                        Fun_Upload("", "video");
                    }
                }

            } else if (requestCode == VIDEOGALLERY_REQUEST_CODE) {
                videoUri = data.getData();
                try {
                    strvideofile = Utility.getFilePath(context, videoUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                videofile = new File(strvideofile);
                long length = Integer.parseInt(String.valueOf(videofile.length()));
                if (length > 40000000) {
                    Utility.ShowToastMessage(context, getString(R.string.file_size_limit));
                } else {
                    imgplay.setVisibility(View.VISIBLE);
                    delete_video.setVisibility(View.VISIBLE);
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                    if (length > 1024 * 1024) {
                        new VideoCompressor().execute();
                    } else {
                        /*File video_file = null;
                        if (video_bitmap != null) {
                            video_file = persistImage(video_bitmap, "video_thumb");
                        }*/
                        //progressHUD = ProgressHUD.show(context, true, false, null);
                        Fun_Upload("", "video");
                    }
                }

            }
            if (requestCode == 5) {
                /*if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(context, data);
                    location.setText(place.getAddress());
                    String str_latLng = String.valueOf(place.getLatLng());
                    str_latLng = str_latLng.replace("lat/lng: ", "");
                    str_latLng = str_latLng.replace("(", "");
                    str_latLng = str_latLng.replace(")", "");
                    String[] array_latLng = str_latLng.split(",");
                    latitude = Double.valueOf(array_latLng[0]);
                    logitude = Double.valueOf(array_latLng[1]);
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(context, data);
                    // TODO: Handle the error.
                    //Log.i("====", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }*/
            } else if (requestCode == 200 && resultCode == AppCompatActivity.RESULT_OK) {
                String[] all_path = data.getStringArrayExtra("all_path");
                if (all_path.length > 0) {
                    for (String string : all_path) {
                        dataT.add(string);
                        Log.e(TAG, "==========: " + string);
                   /* CustomGallery item = new CustomGallery();
                    item.sdcardPath = string;

                    dataT.add(item);*/
                    }
                    Fun_Upload_MultiImage(dataT);
                }

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

    private void Fun_Upload(final String path, final String type) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Part profile_img = null, thubnail_file = null;
            if (type.equals("video")) {
                if (videofile != null) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), videofile);
                    profile_img = MultipartBody.Part.createFormData("new_image", videofile.getName(), reqFile);
                }
//                if (thubnail != null) {
//                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), thubnail);
//                    thubnail_file = MultipartBody.Part.createFormData("video_thumbnail", thubnail.getName(), reqFile);
//                }
            } else if (type.equals("image")) {
                if (file_image != null) {
                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file_image);
                    profile_img = MultipartBody.Part.createFormData("new_image", file_image.getName(), reqFile);
                }
            }
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UploadVideo(profile_img,
                    SharedPref.getSharedPreferences(context, Constants.TOKEN), type).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            if (type.equals("image")) {
                                Subcatogery subcatogery = new Subcatogery();
                                subcatogery.setId(response.body().getUserData().getImage_id());
                                subcatogery.setName(path);
                                array_images.add(subcatogery);
                                imageAdapter.notifyDataSetChanged();
                            } else if (type.equals("video")) {
                                video_id = response.body().getUserData().getImage_id();
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

    private void Fun_Upload_MultiImage(final ArrayList<String> img_arry) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (int i = 0; i < img_arry.size(); i++) {
                File file = new File(img_arry.get(i));
                builder.addFormDataPart(Constants.IMAGE_ARRY, file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            }
            MultipartBody requestbody = builder.build();
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UploadMultiImage(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestbody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            for (int i = 0; i < response.body().getObject().size(); i++) {
                                Subcatogery subcatogery = new Subcatogery();
                                subcatogery.setId(response.body().getObject().get(i).getImage_id());
                                subcatogery.setName(img_arry.get(i));
                                array_images.add(subcatogery);
                            }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (type == 1)
                    selectVideo();
                else
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

    private class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressHUD = ProgressHUD.show(context, true, false, null);
            //progressHUD = ProgressHUD.show(context, true, false, null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(strvideofile);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                videofile = new File(MediaController.cachedFile.getPath());
                /*File video_file = null;
                if (video_bitmap != null) {
                    video_file = persistImage(video_bitmap, "video_thumb");
                }*/
                Fun_Upload("", "video");
                Log.e("====Compression", "Compression successfully!");
                Log.e("====Compre File Path", "" + MediaController.cachedFile.getPath());
                Log.e("====Compressed length", MediaController.cachedFile.getPath().length() + "");
            } else {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Utility.ShowToastMessage(context, getString(R.string.videofailed));
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
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull JobPostActivity.ImageAdapter.MyViewHolder holder, final int position) {
            Glide.with(context).load(List.get(position).getName()).into(holder.img_work);
            holder.img_delete.setOnClickListener(new JobPostActivity.ImageAdapter.OnMyClick(position));

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


}
