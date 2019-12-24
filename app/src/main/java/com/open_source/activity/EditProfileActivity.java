package com.open_source.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import com.open_source.util.RuntimePermissionsActivity;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

public class EditProfileActivity extends RuntimePermissionsActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SecurityQuestion {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private final int PERMISSION_REQUEST_CODE = 101;
    ArrayList<String> arrayList = new ArrayList<>();
    CheckBox check_8to5, check_5_to12, all;
    ArrayList<String> arrayList_que = new ArrayList<>();
    private Context context;
    private ProgressHUD progressHUD;
    private CircleImageView profile_img;
    private ImageView camera;
    private ImageView cross;
    private File file;
    private RadioGroup radio_group, radio_group_privacy;
    private RadioButton male, female, radio_private, radio_public;
    private TextView ctv_login, ctv_password, user_type, email_status, resent;
    private EditText cet_first_name, cet_last_name, cet_contact, cet_email, cet_current_password, cet_new_password,
            cet_conform_password, cet_username, cet_website, cet_bio, cet_category, cet_page, cet_business, answer;
    private RelativeLayout btn_submit;
    private Button btn_submit_dialog;
    private Dialog dialog;
    private Spinner spn_question;
    private String gender = "", profile_type = "", str_availability = "", question_id = "";
    private HashMap<String, String> array_map = new HashMap<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        context = this;
        init();
    }

    public void init() {
        bind_view();
        CommanApiCall.LoadQuestionData(context, "getprofile");
        // LoadData();
        callProfileAPI();
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    gender = "male";
                } else if (checkedId == R.id.female) {
                    gender = "female";
                }
            }
        });
        radio_group_privacy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_public) {
                    profile_type = "public";
                } else if (checkedId == R.id.radio_private) {
                    profile_type = "private";
                }
            }
        });

        spn_question.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_question.getSelectedItem() != null) {
                    if (!spn_question.getSelectedItem().toString().equals(getString(R.string.item_select_question))) {
                        answer.setVisibility(View.VISIBLE);
                    } else {
                        answer.setText("");
                        answer.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bind_view() {
        profile_img = (CircleImageView) findViewById(R.id.profile_img);
        camera = (ImageView) findViewById(R.id.camera);
        user_type = (TextView) findViewById(R.id.user_type);
        ctv_login = (TextView) findViewById(R.id.ctv_login);
        ctv_password = (TextView) findViewById(R.id.ctv_password);
        cet_first_name = (EditText) findViewById(R.id.cet_first_name);
        cet_last_name = (EditText) findViewById(R.id.cet_last_name);
        cet_contact = (EditText) findViewById(R.id.cet_contact);
        cet_contact.addTextChangedListener(new PhoneNumberTextWatcher(cet_contact));
        cet_email = (EditText) findViewById(R.id.cet_email);
        cet_username = (EditText) findViewById(R.id.cet_username);
        cet_website = (EditText) findViewById(R.id.cet_website);
        cet_bio = (EditText) findViewById(R.id.cet_bio);
        cet_category = (EditText) findViewById(R.id.cet_category);
        cet_page = (EditText) findViewById(R.id.cet_page);
        cet_business = (EditText) findViewById(R.id.cet_business);
        btn_submit = (RelativeLayout) findViewById(R.id.btn_submit);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        radio_group_privacy = (RadioGroup) findViewById(R.id.radio_group_privacy);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);
        email_status = findViewById(R.id.email_status);
        answer = findViewById(R.id.answer);

        radio_private = (RadioButton) findViewById(R.id.radio_private);
        radio_public = (RadioButton) findViewById(R.id.radio_public);
        check_8to5 = findViewById(R.id.check_8to5);
        check_8to5.setOnCheckedChangeListener(this);
        check_5_to12 = findViewById(R.id.check_5_to12);
        check_5_to12.setOnCheckedChangeListener(this);
        all = findViewById(R.id.all);
        all.setOnCheckedChangeListener(this);
        resent = (TextView) findViewById(R.id.resent);
        resent.setOnClickListener(this);
        spn_question = findViewById(R.id.spn_question);


        camera.setOnClickListener(this);
        ctv_password.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestAppPermissions(Utility.requestedPermissions,
                                R.string.camera_permission_needed,
                                PERMISSION_REQUEST_CODE);
                        //requestCameraPermission();
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
                } else if (radio_group.getCheckedRadioButtonId() == -1) {
                    Utility.ShowToastMessage(context, R.string.msg_error_gender);
                } else if (cet_contact.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact);
                } else if (cet_contact.getText().toString().length() < 10) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if (!Utility.isValidEmail(cet_email.getText().toString())) {
                    Utility.ShowToastMessage(context, R.string.msg_error_email);
                } else if (radio_group_privacy.getCheckedRadioButtonId() == -1) {
                    Utility.ShowToastMessage(context, getString(R.string.sel_pro_type));
                } else if (spn_question.getSelectedItem() != null && spn_question.getSelectedItem().toString().equals(getString(R.string.item_select_question))) {
                    Utility.ShowToastMessage(context, R.string.msg_select_security_question);
                } else if (answer.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_provide_answer);
                } else {
                    question_id = array_map.get(spn_question.getSelectedItem().toString());
                    Log.e(TAG, "onClick: " + question_id);
                    arrayList.clear();
                    str_availability = "";
                    if (check_8to5.isChecked())
                        arrayList.add("1");
                    if (check_5_to12.isChecked())
                        arrayList.add("2");
                    if (all.isChecked())
                        arrayList.add("3");
                    str_availability = TextUtils.join(",", arrayList);
                    callUpdateProfileAPI();
                }
                break;

            case R.id.ctv_login:
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                break;

            case R.id.ctv_password:
                ShowDialogConfirmPass();
                break;

            case R.id.cross:
                dialog.dismiss();
                break;

            case R.id.btn_submit_dialog:
                if (cet_current_password.getText().toString().equals("") && cet_current_password.getText().length() < 6) {
                    cet_current_password.setError(getString(R.string.msg_current_password));
                } else if (cet_new_password.getText().toString().equals("") && cet_new_password.getText().length() < 6) {
                    cet_new_password.setError(getString(R.string.msg_new_password));
                } else if (!Utility.isValidPassword(cet_new_password.getText().toString())) {
                    cet_new_password.setError(getString(R.string.msg_error_valid_pwd));
                    //  Utility.ShowToastMessage(context, R.string.msg_error_valid_pwd);
                } else if (cet_conform_password.getText().toString().equals("") && cet_conform_password.getText().length() < 6) {
                    cet_conform_password.setError(getString(R.string.msg_confirm_password));
                } else {
                    if (cet_new_password.getText().toString().equals(cet_conform_password.getText().toString())) {
                        callChangePasswordAPI();
                    } else {
                        cet_conform_password.setError(getString(R.string.msg_match_password));
                    }
                }
                break;
            case R.id.resent:
                Fun_Resend();
                break;
        }
    }

    private void Fun_Resend() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().ResendEmail(SharedPref.getSharedPreferences(context, Constants.TOKEN), cet_email.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
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

    //---------------------- Sign Up ----------------------------------
    public void callUpdateProfileAPI() {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Part profile_img = null;
            if (file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                profile_img = MultipartBody.Part.createFormData("profileImage", file.getName(), reqFile);
            }
            RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), cet_first_name.getText().toString());
            RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), cet_last_name.getText().toString());
            RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), cet_contact.getText().toString().replace("-", ""));
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), cet_email.getText().toString());
            RequestBody username = RequestBody.create(MediaType.parse("text/plain"), cet_username.getText().toString());
            RequestBody website = RequestBody.create(MediaType.parse("text/plain"), cet_website.getText().toString());
            RequestBody bio = RequestBody.create(MediaType.parse("text/plain"), cet_bio.getText().toString());
            RequestBody page = RequestBody.create(MediaType.parse("text/plain"), cet_page.getText().toString());
            RequestBody category = RequestBody.create(MediaType.parse("text/plain"), cet_category.getText().toString());
            RequestBody req_gender = RequestBody.create(MediaType.parse("text/plain"), gender);
            RequestBody req_profile_type = RequestBody.create(MediaType.parse("text/plain"), profile_type);
            RequestBody req_business_name = RequestBody.create(MediaType.parse("text/plain"), cet_business.getText().toString());
            RequestBody availability = RequestBody.create(MediaType.parse("text/plain"), str_availability);
            RequestBody questionid = RequestBody.create(MediaType.parse("text/plain"), question_id);
            RequestBody str_ans = RequestBody.create(MediaType.parse("text/plain"), answer.getText().toString().trim());

            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getUpdateProfileAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    profile_img,
                    first_name,
                    last_name,
                    contact,
                    email,
                    username,
                    website,
                    bio,
                    page,
                    category,
                    req_gender, req_profile_type, req_business_name, availability, questionid, str_ans).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }

                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            SharedPref.setSharedPreference(context, Constants.FIRST_NAME, userData.getFirst_name());
                            SharedPref.setSharedPreference(context, Constants.LAST_NAME, userData.getLast_name());
                            SharedPref.setSharedPreference(context, Constants.MOBILE_NUMBER, userData.getMobileNumber());
                            SharedPref.setSharedPreference(context, Constants.EMAIL, userData.getEmail());
                            SharedPref.setSharedPreference(context, Constants.USER_PROFILE_IMAGE, userData.getUrl() + userData.getProfileImage());


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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    //---------------------- Get Profile --------------------------
    public void callProfileAPI() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getProfileAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }

                    try {
                        if (response.body().getStatus() == 200) {
//                            Utility.ShowToastMessage(context,response.body().getMessage());
                            UserData userData = response.body().getUserData();
                            Glide.with(context).load(userData.getUrl() + userData.getProfileImage()).into(profile_img);
                            cet_first_name.setText(userData.getFirst_name());
                            cet_last_name.setText(userData.getLast_name());
                            if (!userData.getMobileNumber().isEmpty() && userData.getMobileNumber().length() >= 10)
                                cet_contact.setText(Utility.formatNumbersAsCode(userData.getMobileNumber()));
                            else
                                cet_contact.setText(userData.getMobileNumber());
                            cet_email.setText(userData.getEmail());
                            cet_username.setText(userData.getUsername());
                            cet_website.setText(userData.getWebsite());
                            cet_bio.setText(userData.getBio());
                            cet_page.setText(userData.getPage());
                            cet_category.setText(userData.getCategory());
                            cet_business.setText(userData.getBusiness_name());
                            if (userData.getGender().equalsIgnoreCase("female")) {
                                female.setChecked(true);
                                male.setChecked(false);
                                gender = "female";
                            } else if (userData.getGender().equalsIgnoreCase("male")) {
                                male.setChecked(true);
                                female.setChecked(false);
                                gender = "male";
                            }

                            if (userData.getProfile_status().equalsIgnoreCase("public")) {
                                radio_public.setChecked(true);
                                radio_private.setChecked(false);
                                profile_type = "public";
                            } else if (userData.getProfile_status().equalsIgnoreCase("private")) {
                                radio_private.setChecked(true);
                                radio_public.setChecked(false);
                                profile_type = "private";
                            }

                            if (userData.getEmail_verification().equals("1")) {
                                email_status.setText(R.string.varified);
                                email_status.setTextColor(getResources().getColor(R.color.green));
                            } else {
                                resent.setVisibility(View.VISIBLE);
                                email_status.setText(R.string.not_varified);
                                email_status.setTextColor(getResources().getColor(R.color.gmail));
                            }


                            /*if (cet_email.getText().toString().isEmpty()) {
                                cet_email.setEnabled(true);
                            } else {
                                cet_email.setEnabled(false);
                            }*/

                            if (!userData.getLogin_type().equals("")) {
                                ctv_password.setVisibility(View.GONE);
                            }

                            if (userData.getUserType().equals("1")) {
                                user_type.setText(R.string.buyer_seller);
                            } else if (userData.getUserType().equals("2")) {
                                user_type.setText(R.string.investor);
                            } else if (userData.getUserType().equals("3")) {
                                user_type.setText(R.string.agent);
                            }

                            if (userData.getAvailability().contains("1"))
                                check_8to5.setChecked(true);
                            if (userData.getAvailability().contains("2"))
                                check_5_to12.setChecked(true);
                            if (userData.getAvailability().contains("3"))
                                all.setChecked(true);

                            answer.setText(userData.getAnswer());
                            if (!userData.getQuestion_id().isEmpty())
                                spn_question.setSelection(Integer.valueOf(userData.getQuestion_id()));


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

    //--------------------- Change Password -----------------------
    public void callChangePasswordAPI() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getChangePasswordAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    cet_current_password.getText().toString(),
                    cet_new_password.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }

                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            dialog.dismiss();
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

    //------------------Confirm Password--------------------------
    public void ShowDialogConfirmPass() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_confirm_password);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;

        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        cet_current_password = dialog.findViewById(R.id.current_password);
        cet_new_password = dialog.findViewById(R.id.new_password);
        cet_conform_password = dialog.findViewById(R.id.conform_password);
        btn_submit_dialog = dialog.findViewById(R.id.btn_submit_dialog);

        cross = dialog.findViewById(R.id.cross);
        cross.setOnClickListener(this);

        btn_submit_dialog.setOnClickListener(this);
        dialog.show();
    }

    //---------------------- Select Image--------------------------
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

/*    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }*/

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
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(EditProfileActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String imagepath = Crop.getOutput(result).getPath();
            File file1 = new File(imagepath);
            try {
                file = new Compressor(this).compressToFile(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //Checking the request code of our request
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//
//            } else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setMessage(R.string.permission_needed);
//                builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        intent.addCategory(Intent.CATEGORY_DEFAULT);
//                        intent.setData(Uri.parse("package:" + context.getPackageName()));
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                        startActivity(intent);
//                    }
//                }).create().show();
//            }
//        }
//    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            selectImage();
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

   /* private void LoadData() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().QUESTION_lIST().enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            arrayList_que.add(getString(R.string.item_select_question));
                            for (int i = 0; i < response.body().getObject().size(); i++) {
                                arrayList_que.add(response.body().getObject().get(i).getQuestion());
                                array_map.put(response.body().getObject().get(i).getQuestion(), response.body().getObject().get(i).getQuestion_id());
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList_que);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_question.setAdapter(spinnerArrayAdapter);
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }*/

    @Override
    public void onrecieve(ArrayList<RetroObject> question) {
        if (question.size() > 0) {
            arrayList_que.add(getString(R.string.item_select_question));
            for (int i = 0; i < question.size(); i++) {
                arrayList_que.add(question.get(i).getQuestion());
                array_map.put(question.get(i).getQuestion(), question.get(i).getQuestion_id());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayList_que);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_question.setAdapter(spinnerArrayAdapter);
        }
    }
}
