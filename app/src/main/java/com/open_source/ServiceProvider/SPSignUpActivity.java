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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.open_source.BuildConfig;
import com.open_source.Listener.Onpaymentrecieve;
import com.open_source.R;
import com.open_source.activity.ConfirmPaymentMessage;
import com.open_source.activity.StripeWeb;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.CommanApiCall;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.Payment;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.soundcloud.android.crop.Crop;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardMultilineWidget;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
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


public class SPSignUpActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, Onpaymentrecieve {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = SPSignUpActivity.class.getSimpleName();
    final int PERMISSION_REQUEST_CODE = 101;
    private ProgressHUD progressHUD;
    private Context context;
    private EditText et_lname, et_fname, et_email, et_pass, et_contact, et_conpass, et_buss_desc, et_buss_Spec, et_buss_offer,
            et_buss_start, cet_zip;
    private TextInputLayout tl_pass;
    private MaterialSpinner spn_propretor, spn_miles, spn_buss_name;
    private TextView business_since;
    private AutoCompleteTextView cet_location;
    private ImageView img_logo;
    private CircleImageView img_profile;
    private String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private Button btn_submit, pick_file;
    private String cardExpiry;
    private RadioGroup rg_pref, rg_id_proof;
    private String str = "", str_buss_miles = "", str_buss_type = "", str_buss_name = "", str_pref = "", id_proof = "", token = "",
            str_lat = "", str_lng = "", method_type = "", str_availability = "";
    private File photoFile = null;
    private int year = Calendar.getInstance().get(Calendar.YEAR);
    private File file = null, logo_file = null, Id_proof_file = null;
    private ArrayList<String> array_id = new ArrayList<>();
    private ArrayAdapter<String> serviceadapter;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 5;
    private HashMap<String, String> service_map = new HashMap<>();
    private CheckBox checkbox;
    private Dialog dialog;
    private ArrayList<String> arrayList = new ArrayList<>();
    private CheckBox check_8to5, check_5_to12, all;
    private String imageFilePath = "";
    private Button btn_paypal, btn_credit, pay_paynow;
    private ImageView cross;
    private Card cardToSave;
    private CardMultilineWidget cardMultilineWidget;
    private LinearLayout lay_credit;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);
    public AutoCompleteAdapter autoCompleteAdapter;
    private PlacesClient placesClient;
    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_provider_signup);
        context = SPSignUpActivity.this;
        init();
    }

    private void init() {
        token = FirebaseInstanceId.getInstance().getToken();
        if (SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN).equals("")) {
            SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
        }
        et_fname = findViewById(R.id.first_name);
        et_lname = findViewById(R.id.last_name);
        et_email = findViewById(R.id.email);
        et_pass = findViewById(R.id.pass);
        tl_pass = findViewById(R.id.tl_pass);
        et_conpass = findViewById(R.id.conpass);
        cet_location = findViewById(R.id.cet_location);
        spn_buss_name = findViewById(R.id.spn_buss_name);
        cet_zip = findViewById(R.id.cet_zip);
        et_contact = findViewById(R.id.phone_number);
        et_contact.addTextChangedListener(new PhoneNumberTextWatcher(et_contact));
        et_buss_desc = findViewById(R.id.business_desc);
        et_buss_Spec = findViewById(R.id.specility_desc);
        et_buss_offer = findViewById(R.id.offer_desc);
        et_buss_start = findViewById(R.id.started_desc);
        btn_submit = findViewById(R.id.btn_submit);
        spn_propretor = findViewById(R.id.spn_propretor);
        spn_miles = findViewById(R.id.spn_miles);
        business_since = findViewById(R.id.business_since);
        img_profile = findViewById(R.id.img_profile);
        img_logo = findViewById(R.id.img_logo);
        rg_pref = findViewById(R.id.rg_pref);
        rg_id_proof = findViewById(R.id.rg_id_proof);
        img_logo.setOnClickListener(this);
        img_profile.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        business_since.setOnClickListener(this);
        cet_location.setOnClickListener(this);
        pick_file = findViewById(R.id.pick_file);
        pick_file.setOnClickListener(this);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
        check_8to5 = findViewById(R.id.check_8to5);
        check_8to5.setOnCheckedChangeListener(this);
        check_5_to12 = findViewById(R.id.check_5_to12);
        check_5_to12.setOnCheckedChangeListener(this);
        all = findViewById(R.id.all);
        all.setOnCheckedChangeListener(this);

        GetService();
        rg_pref.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = rg_pref.getCheckedRadioButtonId();
                View radioButton = rg_pref.findViewById(radioButtonID);
                int idx = rg_pref.indexOfChild(radioButton);
                RadioButton r = (RadioButton) rg_pref.getChildAt(idx);
                str_pref = r.getText().toString();
            }
        });
        rg_id_proof.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = rg_id_proof.getCheckedRadioButtonId();
                View radioButton = rg_id_proof.findViewById(radioButtonID);
                int idx = rg_id_proof.indexOfChild(radioButton);
                RadioButton r = (RadioButton) rg_id_proof.getChildAt(idx);
                id_proof = r.getText().toString();
            }
        });
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
        cet_location.setThreshold(1);
        cet_location.setOnItemClickListener(autocompleteClickListener);
        autoCompleteAdapter = new AutoCompleteAdapter(this, placesClient);
        cet_location.setAdapter(autoCompleteAdapter);
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
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Place place = task.getPlace();
                            cet_location.setText(place.getAddress());
                            //place_location = String.valueOf(place.getName());
                            String latLng = String.valueOf(place.getLatLng());
                            latLng = latLng.replace("lat/lng: ", "");
                            latLng = latLng.replace("(", "");
                            latLng = latLng.replace(")", "");
                            String[] array_latLng = latLng.split(",");
                            str_lat = array_latLng[0];
                            str_lng = array_latLng[1];
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            cet_location.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void GetService() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetAllService(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ArrayList<RetroObject> array = response.body().getObject();
                            for (int i = 0; i < array.size(); i++) {
                                array_id.add(array.get(i).getName());
                                service_map.put(array.get(i).getName(), array.get(i).getService_id());

                            }
                            serviceadapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, array_id);
                            serviceadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spn_buss_name.setAdapter(serviceadapter);

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
                CheckBlankEntry();
                break;
            case R.id.img_profile:
                str = "1";
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
            case R.id.img_logo:
                str = "2";
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
            case R.id.business_since:
                final Dialog d = new Dialog(SPSignUpActivity.this);
                d.setTitle(R.string.year_picker);
                d.setContentView(R.layout.yeardialog);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = d.getWindow();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                Button set = (Button) d.findViewById(R.id.button1);
                Button cancel = (Button) d.findViewById(R.id.button2);
                TextView year_text = (TextView) d.findViewById(R.id.year_text);
                year_text.setText("" + year);
                final NumberPicker nopicker = (NumberPicker) d.findViewById(R.id.numberPicker1);
                nopicker.setMaxValue(year + 50);
                nopicker.setMinValue(year - 100);
                nopicker.setWrapSelectorWheel(false);
                nopicker.setValue(year);
                nopicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        business_since.setText(String.valueOf(nopicker.getValue()));
                        d.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });
                d.show();
                break;

            case R.id.cet_location:
                /*if (Utility.checkPlayServices(context)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(SPSignUpActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
                break;
            case R.id.pick_file:
                str = "3";
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
            case R.id.btn_paypal:
                dialog.dismiss();
                method_type = Payment.PAYPAL;
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(Payment.SP_AMOUNT), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                break;


            case R.id.cross:
                if (dialog.isShowing())
                    dialog.dismiss();
                break;


            case R.id.btn_credit:
                dialog.dismiss();
                startActivity(new Intent(context, StripeWeb.class));
               // lay_credit.setVisibility(View.VISIBLE);
                break;


            case R.id.paynow:
                if (lay_credit.getVisibility() == View.VISIBLE) {
                    cardToSave = cardMultilineWidget.getCard();
                    if (cardToSave == null) {
                        Utility.ShowToastMessage(context, getString(R.string.invalidata));
                    } else {
                        if (cardToSave.getNumber().equals("")) {
                            Utility.ShowToastMessage(context, getString(R.string.card_number));
                        } else if (cardToSave.getExpMonth() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardexpirymonth));
                        } else if (cardToSave.getExpYear() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardyear));
                        } else if (cardToSave.getCVC().isEmpty()) {
                            Utility.ShowToastMessage(context, getString(R.string.cradcvv));
                        } else {
                            cardExpiry = cardToSave.getExpMonth() + "/" + cardToSave.getExpYear();
                            // Fun_Server_Payment();
                            CommanApiCall.GetStripToken(context, cardMultilineWidget.getCard().getNumber().toString(),
                                    cardMultilineWidget.getCard().getExpMonth(), cardMultilineWidget.getCard().getExpYear(),
                                    cardMultilineWidget.getCard().getCVC().toString(), Payment.SP_AMOUNT, "sp");
                        }
                    }
                }
                break;


        }

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
                    if (str.equalsIgnoreCase("1")) {
                        Uri outputFileUri = getCaptureImageOutputUri();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(intent, 1);

                    } else {
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
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(SPSignUpActivity.this);
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
            Glide.with(context).load(imagepath).into(img_profile);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SPSignUpActivity.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(SPSignUpActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(SPSignUpActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(SPSignUpActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                if (str.equalsIgnoreCase("1")) {
                    Uri imageUri = getPickImageResultUri(data);
                    beginCrop(imageUri);

                } else {
                    Bitmap b = decodeFile(photoFile);
                    if (b != null) {
                        imageFilePath = photoFile.getAbsolutePath();
                    } else {

                    }
                    if (str.equalsIgnoreCase("2")) {
                        logo_file = new File(imageFilePath);
                        Glide.with(context).load(imageFilePath).into(img_logo);
                    } else {
                        Id_proof_file = new File(imageFilePath);
                        ((TextView) findViewById(R.id.txt_file_name)).setText(Id_proof_file.getName());
                    }

                }

            } else if (requestCode == 2) {
                if (str.equalsIgnoreCase("1")) {
                    Uri selectedImage = data.getData();
                    beginCrop(selectedImage);
                } else {
                    Uri selectedImage = data.getData();
                    String path = null;
                    try {
                        path = Utility.getFilePath(context, selectedImage);
                        if (str.equalsIgnoreCase("2")) {
                            logo_file = new File(path);
                            try {
                                logo_file = new Compressor(this).compressToFile(logo_file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Glide.with(context).load(path).into(img_logo);
                        } else {
                            Id_proof_file = new File(path);
                            try {
                                Id_proof_file = new Compressor(this).compressToFile(Id_proof_file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ((TextView) findViewById(R.id.txt_file_name)).setText(Id_proof_file.getName());
                        }

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            } /*else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(context, data);
                    cet_location.setText(place.getAddress());
                    //place_location = String.valueOf(place.getName());
                    String latLng = String.valueOf(place.getLatLng());
                    latLng = latLng.replace("lat/lng: ", "");
                    latLng = latLng.replace("(", "");
                    latLng = latLng.replace(")", "");
                    String[] array_latLng = latLng.split(",");
                    str_lat = array_latLng[0];
                    str_lng = array_latLng[1];
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(context, data);
                    // TODO: Handle the error.
                    //Log.i("====", status.getStatusMessage());

                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            } */else if (requestCode == PAYPAL_REQUEST_CODE) {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    com.paypal.android.sdk.payments.PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {
                            String paymentDetails = confirm.toJSONObject().toString(4);
                            Log.i("=====transaction_id", paymentDetails);
                            JSONObject jsonObject = new JSONObject(paymentDetails);
                            String transaction_id = jsonObject.getJSONObject("response").getString("id");
                            Log.e(TAG, "====transaction_id: " + transaction_id);
                            FunPayment(transaction_id);

                        } catch (JSONException e) {
                            Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                        }
                    }
                } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                    Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                    startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE, "sp"));
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));

                }
            }
        }


    }

    private void FunPayment(String transaction_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FunPayment(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    transaction_id, method_type, Payment.SP_AMOUNT).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, ConfirmPaymentMessage.class).
                                    putExtra(Constants.MESSAGE, "success").
                                    putExtra(Constants.PURPOSE, getString(R.string.sp_payment)).putExtra(Constants.TYPE, "sp"));

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

    private void CheckBlankEntry() {
        tl_pass.setErrorEnabled(false);
        if (spn_propretor != null && spn_propretor.getSelectedItem() != null) {
            str_buss_type = spn_propretor.getSelectedItem().toString();
        }
        if (spn_buss_name != null && spn_buss_name.getSelectedItem() != null) {
            str_buss_name = spn_buss_name.getSelectedItem().toString();
        }
        if (spn_miles != null && spn_miles.getSelectedItem() != null) {
            str_buss_miles = spn_miles.getSelectedItem().toString();
        }
        if (et_fname.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_fname);
        else if (et_lname.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_lname);
        else if (et_contact.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_contact);
        else if (et_contact.getText().toString().length() < 10)
            Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
        else if (et_email.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_email);
        else if (!Utility.isValidEmail(et_email.getText().toString()))
            Utility.ShowToastMessage(context, R.string.msg_error_email);
        else if (et_pass.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_pwd);
        else if (!Utility.isValidPassword(et_pass.getText().toString())) {
            tl_pass.setErrorEnabled(true);
            tl_pass.setError(getString(R.string.msg_error_valid_pwd));
        }
        //Utility.ShowToastMessage(context, R.string.msg_error_valid_pwd);
        else if (et_conpass.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, R.string.msg_error_confirm_pwd);
        else if (!et_pass.getText().toString().equals(et_conpass.getText().toString()))
            Utility.ShowToastMessage(context, R.string.val_pass_conpass);
        else if (cet_location.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.pick_location));
        else if (cet_zip.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_zip_code));
        else if (str_buss_name.isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_buss_cat));
        else if (str_buss_type.isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_buss_type));
        else if (business_since.getText().toString().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_buss_since));
        else if (et_buss_desc.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_buss_desc));
        else if (et_buss_start.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_start_buss));
        else if (et_buss_Spec.getText().toString().trim().isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_specility));
        else if (str_buss_miles.isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_buss_req_dist));
        else if (file == null)
            Utility.ShowToastMessage(context, getString(R.string.val_pick_profile));
        else if (logo_file == null)
            Utility.ShowToastMessage(context, getString(R.string.val_buss_logo));
        else if (str_pref.isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.val_noti_pref));
        else if (id_proof.isEmpty())
            Utility.ShowToastMessage(context, getString(R.string.id_profe));
        else if (Id_proof_file == null)
            Utility.ShowToastMessage(context, getString(R.string.id_profe_file));
        else if (!checkbox.isChecked())
            Utility.ShowToastMessage(context, getString(R.string.term_condition));
        else {
            arrayList.clear();
            str_availability = "";
            if (check_8to5.isChecked())
                arrayList.add("1");
            if (check_5_to12.isChecked())
                arrayList.add("2");
            if (all.isChecked())
                arrayList.add("3");
            str_availability = TextUtils.join(",", arrayList);
            str_buss_name = service_map.get(str_buss_name);
            FunSingup();
        }


    }

    private void FunSingup() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            MultipartBody.Part profile_img = null, logo_img = null, Id_proof_img = null;

            if (file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                profile_img = MultipartBody.Part.createFormData("profileImage", file.getName(), reqFile);
            }
            if (logo_file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), logo_file);
                logo_img = MultipartBody.Part.createFormData("business_logo", logo_file.getName(), reqFile);
            }
            if (Id_proof_file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), Id_proof_file);
                Id_proof_img = MultipartBody.Part.createFormData("id_proof_file", Id_proof_file.getName(), reqFile);
            }

            RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), et_fname.getText().toString());
            RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), et_lname.getText().toString());
            RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), et_contact.getText().toString().replace("-", ""));
            RequestBody email = RequestBody.create(MediaType.parse("text/plain"), et_email.getText().toString());
            RequestBody pass = RequestBody.create(MediaType.parse("text/plain"), et_pass.getText().toString());
            RequestBody buss_desc = RequestBody.create(MediaType.parse("text/plain"), et_buss_desc.getText().toString());
            RequestBody buss_spec = RequestBody.create(MediaType.parse("text/plain"), et_buss_Spec.getText().toString());
            RequestBody buss_start = RequestBody.create(MediaType.parse("text/plain"), et_buss_start.getText().toString());
            RequestBody buss_since = RequestBody.create(MediaType.parse("text/plain"), business_since.getText().toString());

            RequestBody buss_miles = RequestBody.create(MediaType.parse("text/plain"), str_buss_miles);
            RequestBody buss_name = RequestBody.create(MediaType.parse("text/plain"), str_buss_name);
            RequestBody buss_type = RequestBody.create(MediaType.parse("text/plain"), str_buss_type);
            RequestBody buss_pref = RequestBody.create(MediaType.parse("text/plain"), str_pref);
            RequestBody user_type = RequestBody.create(MediaType.parse("text/plain"), "4");
            RequestBody deviceType = RequestBody.create(MediaType.parse("text/plain"), "android");
            RequestBody str_token = RequestBody.create(MediaType.parse("text/plain"), token + "");
            RequestBody location = RequestBody.create(MediaType.parse("text/plain"), cet_location.getText().toString());
            RequestBody latitute = RequestBody.create(MediaType.parse("text/plain"), str_lat);
            RequestBody longitute = RequestBody.create(MediaType.parse("text/plain"), str_lng);
            RequestBody str_zip = RequestBody.create(MediaType.parse("text/plain"), cet_zip.getText().toString());
            RequestBody str_id_proof = RequestBody.create(MediaType.parse("text/plain"), id_proof);
            RequestBody availability = RequestBody.create(MediaType.parse("text/plain"), str_availability);

            RetrofitClient.getAPIService().getSignUpSP(profile_img, logo_img, Id_proof_img, str_id_proof, first_name, last_name, user_type, contact, email, pass,
                    str_token, deviceType, buss_name, buss_type, buss_since, buss_desc, buss_start,
                    buss_spec, buss_miles, buss_pref, location, latitute, longitute, str_zip, availability).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            SharedPref.setSharedPreference(context, Constants.TOKEN, userData.getToken());
                            FunPay();

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
        dialog = new Dialog(context);
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
        lay_credit = dialog.findViewById(R.id.lay_credit);
        btn_paypal = dialog.findViewById(R.id.btn_paypal);
        btn_credit = dialog.findViewById(R.id.btn_credit);
        pay_paynow=dialog.findViewById(R.id.paynow);
        cross = dialog.findViewById(R.id.cross);
        cardMultilineWidget = (CardMultilineWidget)dialog.findViewById(R.id.card_multiline_widget);
        btn_paypal.setOnClickListener(this);
        btn_credit.setOnClickListener(this);
        pay_paynow.setOnClickListener(this);
        cross.setOnClickListener(this);
        dialog.show();
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
    public void onrecieve(String token) {
        if (dialog!=null && dialog.isShowing() && context!=null)
        dialog.dismiss();
        method_type= Payment.STRIPE;
       // FunPayment(token);
    }
}
