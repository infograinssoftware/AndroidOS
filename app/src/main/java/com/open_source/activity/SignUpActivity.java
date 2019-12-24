package com.open_source.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.open_source.BuildConfig;
import com.open_source.Interface.SecurityQuestion;
import com.open_source.R;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.CommanApiCall;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.CircleImageView;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SignUpActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SecurityQuestion {
    private static final String TAG = SignUpActivity.class.getSimpleName();
    final int PERMISSION_REQUEST_CODE = 101;
    Context context;
    ProgressHUD progressHUD;
    CircleImageView profile_img;
    TextView upload_your_image;
    File file;
    RadioGroup radio_group;
    RadioButton rb_buy_sell, rb_investor, rb_agent;
    TextView ctv_login;
    EditText cet_first_name, cet_last_name, cet_contact, cet_email, cet_password, cet_confirm_password, answer;
    RelativeLayout btn_submit;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    String userType = "", question_id = "";
    String MobilePattern = "[0-9]{12}";
    String token, str_availability = "";
    ArrayList<String> arrayList = new ArrayList<>();
    CheckBox check_8to5, check_5_to12, all;
    ArrayList<String> arrayList_que = new ArrayList<>();
    private HashMap<String, String> array_map = new HashMap<>();
    private Spinner spn_question;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
        getToken();
        if (SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN).equals("")) {
            SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
        }
        System.out.println("<<<<<<FirebaseToken" + token);
        init();
    }

    public void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token =instanceIdResult.getToken();
                SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
                Log.e(LoginActivity.class.getSimpleName(), "onSuccess: " + token);
            }
        });
    }

    public void init() {
        bind_view();
        CommanApiCall.LoadQuestionData(context,"signup");
       // LoadData();
        spn_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_question.getSelectedItem() != null) {
                    answer.setText("");
                    if (!spn_question.getSelectedItem().toString().equals(getString(R.string.item_select_question))) {
                        answer.setVisibility(View.VISIBLE);
                    } else {
                        answer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


 /*   private void LoadData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().QUESTION_lIST().enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {


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

    }*/

    private void bind_view() {
        profile_img = (CircleImageView) findViewById(R.id.profile_img);
        upload_your_image = (TextView) findViewById(R.id.upload_your_image);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        rb_buy_sell = (RadioButton) findViewById(R.id.rb_buy_sell);
        rb_investor = (RadioButton) findViewById(R.id.rb_investor);
        rb_agent = (RadioButton) findViewById(R.id.rb_agent);
        ctv_login = (TextView) findViewById(R.id.ctv_login);
        cet_first_name = (EditText) findViewById(R.id.cet_first_name);
        cet_last_name = (EditText) findViewById(R.id.cet_last_name);
        cet_contact = (EditText) findViewById(R.id.cet_contact);
        cet_contact.addTextChangedListener(new PhoneNumberTextWatcher(cet_contact));
        cet_email = (EditText) findViewById(R.id.cet_email);
        cet_password = (EditText) findViewById(R.id.cet_password);
        cet_confirm_password = (EditText) findViewById(R.id.cet_confirm_password);
        answer = findViewById(R.id.answer);
        btn_submit = (RelativeLayout) findViewById(R.id.btn_submit);
        upload_your_image.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        ctv_login.setOnClickListener(this);
        userType = getIntent().getExtras().getString(Constants.USER_TYPE, "");
        check_8to5 = findViewById(R.id.check_8to5);
        check_8to5.setOnCheckedChangeListener(this);
        check_5_to12 = findViewById(R.id.check_5_to12);
        check_5_to12.setOnCheckedChangeListener(this);
        all = findViewById(R.id.all);
        all.setOnCheckedChangeListener(this);
        spn_question = findViewById(R.id.spn_question);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upload_your_image:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectImage();
                }
                break;

            case R.id.btn_submit:
                if (cet_first_name.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_fname);
                } else if (cet_last_name.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_lname);
                } else if (userType.isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_user_type);
                } else if (cet_contact.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact);
                } else if (cet_contact.getText().toString().length() < 10) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if (!Utility.isValidEmail(cet_email.getText().toString())) {
                    Utility.ShowToastMessage(context, R.string.msg_error_email);
                } else if (cet_password.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_pwd);
                } else if (!Utility.isValidPassword(cet_password.getText().toString())) {
                    cet_password.setError(getString(R.string.msg_error_valid_pwd));
                    // Utility.ShowToastMessage(context, R.string.msg_error_valid_pwd);
                } else if (cet_password.getText().length() < 6) {
                    cet_password.setError(getString(R.string.msg_error_valid_pwd));
                    //Utility.ShowToastMessage(context, R.string.msg_error_valid_pwd);
                } else if (cet_confirm_password.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_confirm_pwd);
                } else if (!cet_confirm_password.getText().toString().equals(cet_password.getText().toString())) {
                    Utility.ShowToastMessage(context, R.string.pass_incorrect);
                } else if (spn_question.getSelectedItem().toString().equals(getString(R.string.item_select_question))) {
                    Utility.ShowToastMessage(context, R.string.msg_select_security_question);
                } else if (answer.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_provide_answer);
                } else {
                    question_id = array_map.get(spn_question.getSelectedItem().toString());
                    arrayList.clear();
                    str_availability = "";
                    if (check_8to5.isChecked())
                        arrayList.add("1");
                    if (check_5_to12.isChecked())
                        arrayList.add("2");
                    if (all.isChecked())
                        arrayList.add("3");
                    str_availability = TextUtils.join(",", arrayList);
                    //Log.e(TAG, "=======: "+str);
                    callSignUpAPI();
                }
                break;

            case R.id.ctv_login:
                super.onBackPressed();
                break;
        }
    }

    //---------------------- Sign Up ----------------------------------
    public void callSignUpAPI() {
        if (Utility.isConnectingToInternet(context)) {

            MultipartBody.Part profile_img = null;

            if (file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                profile_img = MultipartBody.Part.createFormData("profileImage", file.getName(), reqFile);
            }

            RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), cet_first_name.getText().toString());
            RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), cet_last_name.getText().toString());
            RequestBody requestUserType = RequestBody.create(MediaType.parse("text/plain"), userType);
            RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), cet_contact.getText().toString().replace("-", ""));
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), cet_email.getText().toString());
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), cet_password.getText().toString());
            RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN));
            RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "android");
            RequestBody availability = RequestBody.create(MediaType.parse("text/plain"), str_availability);

            RequestBody questionid = RequestBody.create(MediaType.parse("text/plain"), question_id);
            RequestBody str_ans = RequestBody.create(MediaType.parse("text/plain"), answer.getText().toString().trim());

            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getSignUpAPI(profile_img,
                    first_name,
                    last_name,
                    requestUserType,
                    contact,
                    email,
                    password,
                    deviceId,
                    deviceType, availability, questionid, str_ans).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            if (userData.getUserType().equals("2")) {
                                SharedPref.setSharedPreference(context, Constants.TOKEN, userData.getToken());
                                startActivity(new Intent(context, InvestorQuesActivity.class));
                            } else {
                                startActivity(new Intent(context, LoginActivity.class).
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                            /*UserData userData = response.body().getUserData();
                            SharedPref.setSharedPreference(context, Constants.TOKEN, userData.getToken());
                            SharedPref.setSharedPreference(context, Constants.USER_ID, userData.getUser_id());
                            //Log.e(TAG, "======userid: "+userData.getUser_id());
                            SharedPref.setSharedPreference(context, Constants.FIRST_NAME, userData.getFirst_name());
                            SharedPref.setSharedPreference(context, Constants.LAST_NAME, userData.getLast_name());
                            SharedPref.setSharedPreference(context, Constants.USER_TYPE, userData.getUserType());
                            SharedPref.setSharedPreference(context, Constants.EMAIL, userData.getEmail());
                            SharedPref.setSharedPreference(context, Constants.MOBILE_NUMBER, userData.getMobileNumber());
                            SharedPref.setSharedPreference(context, Constants.USER_PROFILE_IMAGE, userData.getUrl() + userData.getProfileImage());
                            SharedPref.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                            startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
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
                    Uri outputFileUri = getCaptureImageOutputUri();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(intent, 1);
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(SignUpActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } else {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private void beginCrop(Uri source) {
        File pro = new File(Utility.MakeDir(Constants.SDCARD_FOLDER_PATH, context), System.currentTimeMillis() + ".jpg");
        Uri destination1 = Uri.fromFile(pro);
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(SignUpActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String imagepath = Crop.getOutput(result).getPath();
            file = new File(imagepath);
            Glide.with(context).load(imagepath).into(profile_img);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Uri imageUri = getPickImageResultUri(data);
                beginCrop(imageUri);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                beginCrop(selectedImage);
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
        }
    }

    //------------------- Back Pressed ----------------------------
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

    //------------------ Hide Status Bar --------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_8to5:
                if (isChecked)
                    all.setChecked(false);

                break;
            case R.id.check_5_to12:
                all.setChecked(false);

                break;
            case R.id.all:
                if (isChecked) {
                    check_5_to12.setChecked(false);
                    check_8to5.setChecked(false);
                    all.setChecked(true);
                }

                break;
        }
    }

    @Override
    public void onrecieve(ArrayList<RetroObject> question) {
        if (question.size()>0)
        {
            arrayList_que.add(getString(R.string.item_select_question));
            for (int i = 0; i < question.size(); i++) {
                arrayList_que.add(question.get(i).getQuestion());
                array_map.put(question.get(i).getQuestion(), question.get(i).getQuestion_id());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, arrayList_que);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_question.setAdapter(spinnerArrayAdapter);
        }

    }
}