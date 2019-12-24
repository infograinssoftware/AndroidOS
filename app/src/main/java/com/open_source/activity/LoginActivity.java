package com.open_source.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.LocationProvider;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener, FacebookCallback<LoginResult>, GoogleApiClient.OnConnectionFailedListener,  LocationProvider.LocationCallback {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    Context context;
    RelativeLayout btn_facebook, btn_gmail, btn_email, btn_dealer, btn_submit;
    TextView ctv_terms_condition, ctv_policy;
    Dialog dialog, dialog1;
    ImageView close, close1;
    RadioGroup radio_group;
    //RadioButton rb_buy_sell, rb_investor, rb_agent;
    CallbackManager callbackManager;
    ProgressHUD progressHUD;
    String flag = "", auth_id, profile_image, first_name, last_name, email = "", userType = "";
    String token;
    GoogleSignInClient mGoogleSignInClient;
    private LocationProvider mLocationProvider;
//    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        getToken();
        if (SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN).equals("")) {
            SharedPref.setSharedPreference(context, Constants.FIREBASE_TOKEN, token);
        }
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
        mLocationProvider = new LocationProvider(this, this);
        if (mLocationProvider != null)
            mLocationProvider.connect();
        ctv_terms_condition = (TextView) findViewById(R.id.ctv_terms_condition);
        btn_facebook = (RelativeLayout) findViewById(R.id.btn_facebook);
        btn_gmail = (RelativeLayout) findViewById(R.id.btn_gmail);
        btn_email = (RelativeLayout) findViewById(R.id.btn_email);
        btn_dealer = (RelativeLayout) findViewById(R.id.btn_dealer);
        ctv_terms_condition.setOnClickListener(this);
        btn_email.setOnClickListener(this);
        btn_dealer.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_gmail.setOnClickListener(this);

        FacebookSdk.sdkInitialize(context);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (radio_group.getCheckedRadioButtonId() == -1) {
                    Utility.ShowToastMessage(context, R.string.msg_error_user_type);
                } else {
                    dialog1.dismiss();
                    if (flag.equals("facebook")) {
                        if (userType.equals("4")) {
                            Utility.ShowToastMessage(context, getString(R.string.sp_val_on_fb));
                        } else if (userType.equals("2")) {
                            Utility.ShowToastMessage(context, getString(R.string.invertor_val_on_fb));
                        } else if (userType.equals("3")) {
                            Utility.ShowToastMessage(context, getString(R.string.agent_val_on_fb));
                        } else {
                            if (Utility.isConnectingToInternet(context)) {
                                flag = "facebook";
                                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                            } else {
                                Utility.ShowToastMessage(context, R.string.internet_connection);
                            }
                        }

                    } else if (flag.equals("google")) {
                        if (userType.equals("4")) {
                            Utility.ShowToastMessage(context, getString(R.string.sp_val_on_fb));
                        } else if (userType.equals("2")) {
                            Utility.ShowToastMessage(context, getString(R.string.invertor_val_on_fb));
                        } else if (userType.equals("3")) {
                            Utility.ShowToastMessage(context, getString(R.string.agent_val_on_fb));
                        } else {
                            if (Utility.isConnectingToInternet(context)) {
                                flag = "google";
                                /*googleSignup();
                                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                                startActivityForResult(intent, RC_SIGN_IN);*/
                                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                startActivityForResult(signInIntent, RC_SIGN_IN);
                            } else {
                                Utility.ShowToastMessage(context, R.string.internet_connection);
                            }
                        }

                    } else if (flag.equals("normal")) {
                        startActivity(new Intent(context, LoginEmailActivity.class).putExtra(Constants.USER_TYPE, userType));
                    }
                }
                break;

            case R.id.ctv_terms_condition:
                showDialogTermsCondition();
                break;

            case R.id.btn_facebook:
                flag = "facebook";
                showDialogUserType();
                break;

            case R.id.btn_gmail:
                flag = "google";
                showDialogUserType();
                break;

            case R.id.btn_email:
                flag = "normal";
                showDialogUserType();
                break;

            case R.id.btn_dealer:
                SharedPref.setSharedPreference(context, Constants.TOKEN, "guest");
                startActivity(new Intent(context, MainActivity.class));
                break;

            case R.id.close:
                dialog.dismiss();
                break;

            case R.id.close1:
                dialog1.dismiss();
                break;
        }
    }

    private void googleSignup() {
        /*if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((FragmentActivity) context);
            googleApiClient.disconnect();
        }
        GoogleSignInOptions gSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gSignInOptions)
                .build();*/
    }

    private void socialLoginEmpty() {
        auth_id = "";
        profile_image = "";
        first_name = "";
        last_name = "";
        email = "";
    }

    //--------------------- Social Login ------------------------
    public void callSocialLoginAPI(String flag, String profile_image, String auth_id, String first_name, String last_name, String email) {
        //Log.e(TAG, "========: "+SharedPref.getSharedPreferences(context,Constants.FIREBASE_TOKEN) );
        progressHUD = ProgressHUD.show(context, true, false, null);
        Log.e(TAG, "callSocialLoginAPI: " + " " + profile_image + " " + auth_id + " " + first_name + " " + last_name + " " + email + " " + userType);
        /*if (profile_image.equals(null)) {
            profile_image = "";
        }*/
        RetrofitClient.getAPIService().getSocialLoginAPI(flag, profile_image, auth_id,
                first_name, last_name, email, userType, SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN), "android")
                .enqueue(new Callback<RetrofitUserData>() {
                    @Override
                    public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        try {
                            if (response.body().getStatus() == 200) {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                                UserData userData = response.body().getUserData();
                                SharedPref.setSharedPreference(context, Constants.EVENT_CHECK, "1");
                                SharedPref.setSharedPreference(context, Constants.TOKEN, userData.getToken());
                                SharedPref.setSharedPreference(context, Constants.USER_ID, userData.getUser_id());
                                SharedPref.setSharedPreference(context, Constants.FIRST_NAME, userData.getFirst_name());
                                SharedPref.setSharedPreference(context, Constants.LAST_NAME, userData.getLast_name());
                                SharedPref.setSharedPreference(context, Constants.USER_TYPE, userData.getUserType());
                                SharedPref.setSharedPreference(context, Constants.EMAIL, userData.getEmail());
                                SharedPref.setSharedPreference(context, Constants.MOBILE_NUMBER, userData.getMobileNumber());
                                SharedPref.setSharedPreference(context, Constants.CURRENCY, userData.getCurrency());
                                SharedPref.setSharedPreference(context, Constants.CURRENCY_SYMBOL, userData.getCurrency_symbol());
                                Log.e(TAG, "onResponse: " + userData.getUrl() + userData.getProfileImage());
                                SharedPref.setSharedPreference(context, Constants.USER_PROFILE_IMAGE, userData.getUrl() + userData.getProfileImage());
                                startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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

    //-------------------- Choose User Type ---------------------------
    public void showDialogUserType() {
        dialog1 = new Dialog(context, R.style.myDialog);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dialog_user_type);
        dialog1.setCancelable(false);
        close1 = dialog1.findViewById(R.id.close1);
        radio_group = dialog1.findViewById(R.id.radio_group);
        btn_submit = dialog1.findViewById(R.id.btn_submit);
       // 1=seller/buyer,2=investor,3=agent,4=SP,5=renter, 6 = Admin, 7 = HS , 8 = DHS, 9 = Lender, 10 = Title
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_buy_sell) {
                    userType = "1";
                } else if (checkedId == R.id.rb_renter) {
                    userType = "5";
                } else if (checkedId == R.id.rb_investor) {
                    userType = "2";
                } else if (checkedId == R.id.rb_agent) {
                    userType = "3";
                } else if (checkedId == R.id.rb_sp) {
                    userType = "4";
                }


            }
        });
        close1.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        dialog1.show();
    }

    //-------------------- Terms and Condition ---------------------------
    public void showDialogTermsCondition() {
        dialog = new Dialog(context, R.style.myDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_terms_condition);
        dialog.setCancelable(false);
        close = dialog.findViewById(R.id.close);
        ctv_policy = dialog.findViewById(R.id.ctv_policy);
        callPrivacyPolicyAPI();
        close.setOnClickListener(this);
        dialog.show();
    }

    //------------------------ Privacy Policy -------------------
    public void callPrivacyPolicyAPI() {
        progressHUD = ProgressHUD.show(context, true, false, null);
        RetrofitClient.getAPIService().getPrivacyPolicyAPI().enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }

                try {
                    if (response.body().getStatus() == 200) {
                        UserData userData = response.body().getUserData();
                        ctv_policy.setText(Html.fromHtml(userData.getDescription()));
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


    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.e(TAG, "onSuccess: " + loginResult);
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    socialLoginEmpty();
                    Log.e(TAG, "onCompleted: Social Login=======>" + object.toString());
                    auth_id = object.getString("id");
                    first_name = object.getString("first_name");
                    last_name = object.getString("last_name");
                    if (object.has("email")) {
                        email = object.getString("email");
                    }
                    profile_image = "https://graph.facebook.com/" + auth_id + "/picture?type=large";
                    Log.e(TAG, "======= " + profile_image);
                    if (!email.isEmpty() && !first_name.isEmpty() && !last_name.isEmpty()) {
                        callSocialLoginAPI("facebook", profile_image, auth_id, first_name, last_name, email);
                    } else {
                        Utility.ShowToastMessage(context, getString(R.string.social_login_email_validation));
                    }

                    LoginManager.getInstance().logOut();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }

    @Override
    public void onCancel() {
        Utility.ShowToastMessage(context, getString(R.string.facebook_login_cancel));
    }

    @Override
    public void onError(FacebookException error) {
        Utility.ShowToastMessage(context, getString(R.string.error_in_facebook));
        Log.e(TAG, "onError: " + error.getMessage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            Log.e(TAG, "handleSignInResult: " + acct.getDisplayName() + "," + acct.getGivenName());
            if (acct != null) {
                first_name = acct.getGivenName();
                last_name = acct.getFamilyName();
                auth_id = acct.getId();
                email = acct.getEmail();
                Uri personPhoto = null;
                if (acct.getPhotoUrl() != null) {
                    personPhoto = acct.getPhotoUrl();
                    profile_image = personPhoto.toString();
                }
                Log.e(TAG, "handleSignInResult: " + "," + personPhoto);
                mGoogleSignInClient.signOut();
                if (!email.isEmpty() && !first_name.isEmpty() && !last_name.isEmpty()) {
                    callSocialLoginAPI("google", profile_image, auth_id, first_name, last_name, email);
                } else {
                    Utility.ShowToastMessage(context, getString(R.string.social_login_email_validation));
                }
            }
        } catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode() + e.getMessage() + "," + e.toString());
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((FragmentActivity) context);
            googleApiClient.disconnect();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.stopAutoManage((FragmentActivity) context);
            googleApiClient.disconnect();
        }*/
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    //-------------------------- Status Bar -----------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: " + location.getLatitude() + "" + location.getLongitude());
        SharedPref.setSharedPreference(context, Constants.LATITUDE, String.valueOf(location.getLatitude()));
        SharedPref.setSharedPreference(context, Constants.LONGITUDE, String.valueOf(location.getLongitude()));
        Utility.GetAddress(context, location.getLatitude(), location.getLongitude());
    }
}

