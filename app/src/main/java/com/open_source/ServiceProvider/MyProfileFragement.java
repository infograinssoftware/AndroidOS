package com.open_source.ServiceProvider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.AboutUsActivity;
import com.open_source.activity.ContactUsActivity;
import com.open_source.activity.LoginActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.CircleImageView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProfileFragement extends Fragment implements View.OnClickListener {
    private static final String TAG = MyProfileFragement.class.getSimpleName();
    Context context;
    View rootView;
    CircleImageView profile_img;
    TextView toolbar_title, ctv_policy, ctv_login, ctv_name, ctv_email, ctv_dashbrd, ctv_about, ctv_contact_us,
            ctv_privacy_policy, ctv_fb, ctv_twitter, ctv_version, ctv_wallet;
    Toolbar toolbar;
    ProgressHUD progressHUD;
    Dialog dialog;
    ImageView close, edit;
    RelativeLayout lay_certified, lay_not_certified;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_you_guest, container, false);
        context = getActivity();
        initView();
        return rootView;

    }

    private void initView() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        ctv_login = rootView.findViewById(R.id.ctv_login);
        ctv_about = rootView.findViewById(R.id.ctv_about);
        profile_img = rootView.findViewById(R.id.profile_img);
        ctv_dashbrd = rootView.findViewById(R.id.ctv_dashbrd);
        ctv_name = rootView.findViewById(R.id.ctv_name);
        ctv_email = rootView.findViewById(R.id.ctv_email);
        ctv_contact_us = rootView.findViewById(R.id.ctv_contact_us);
        ctv_wallet = rootView.findViewById(R.id.ctv_wallet);
        ctv_privacy_policy = rootView.findViewById(R.id.ctv_privacy_policy);
        ctv_fb = rootView.findViewById(R.id.ctv_fb);
        ctv_twitter = rootView.findViewById(R.id.ctv_twitter);
        ctv_version = rootView.findViewById(R.id.ctv_version);
        lay_certified = rootView.findViewById(R.id.layout_certified);
        lay_not_certified = rootView.findViewById(R.id.layout_notcertified);
        toolbar_title.setText(R.string.you);
        ((ImageView) rootView.findViewById(R.id.edit)).setOnClickListener(this);
        ctv_login.setOnClickListener(this);
        ctv_about.setOnClickListener(this);
        ctv_contact_us.setOnClickListener(this);
        ctv_privacy_policy.setOnClickListener(this);
        ctv_fb.setOnClickListener(this);
        ctv_twitter.setOnClickListener(this);
        ctv_version.setOnClickListener(this);
        ctv_dashbrd.setOnClickListener(this);
        ctv_login.setText(getString(R.string.logout));
        ctv_login.setVisibility(View.VISIBLE);
        callProfileAPI();

    }

    //-------------------- Terms and Condition ---------------------------
    public void showDialogTermsCondition() {
        dialog = new Dialog(context, R.style.myDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_terms_condition);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
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
//                        Utility.ShowToastMessage(context,response.body().getMessage());
                        UserData userData = response.body().getUserData();
                        String str = userData.getDescription();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            ctv_policy.setText(Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            ctv_policy.setText(Html.fromHtml(str));
                        }
                        //ctv_policy.setText(str);
                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_dashbrd:
                startActivity(new Intent(context, SPDashboard.class));
                break;
            case R.id.ctv_login:
                if (Utility.isConnectingToInternet(context)) {
                    logoutAPI();
                } else {
                    Utility.ShowToastMessage(context, R.string.internet_connection);
                }
                break;
            case R.id.ctv_about:
                startActivity(new Intent(context, AboutUsActivity.class));
                break;
            case R.id.ctv_contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;
            case R.id.ctv_privacy_policy:
                showDialogTermsCondition();
                break;
            case R.id.ctv_fb:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tt555054")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tt555054")));
                }
                break;
            case R.id.ctv_twitter:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/search?q=@Chandan83973953")));
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/search?q=@Chandan83973953")));
                }
                break;
            case R.id.close:
                dialog.dismiss();
                break;
            case R.id.edit:
                startActivity(new Intent(context,SpEditProfile.class));
                break;
        }
    }

    //----------------------- Logout ----------------------------
    public void logoutAPI() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getLogoutAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    SharedPref.getSharedPreferences(context, Constants.FIREBASE_TOKEN), "android").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            SharedPref.clearPreference(context);
                            context.startActivity(new Intent(context, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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
                    Utility.ShowToastMessage(context, "Server error");
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
            RetrofitClient.getAPIService().getProfileAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            if (!userData.getProfileImage().isEmpty())
                                Glide.with(context).load(userData.getUrl() + userData.getProfileImage()).into(profile_img);
                            ctv_name.setText(userData.getFirst_name() + " " + userData.getLast_name());
                            ctv_email.setText(userData.getEmail());

                            ctv_wallet.setText(getString(R.string.wallet_balance) + "$" + userData.getBalance());
//                            ctv_wallet.setText(getString(R.string.wallet_balance) +SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " +userData.getBalance());

                            if (userData.getUserType().equals("4")) {
                                if (userData.getIs_cetified().equalsIgnoreCase("1")) {
                                    lay_certified.setVisibility(View.VISIBLE);
                                    lay_not_certified.setVisibility(View.GONE);
                                } else {
                                    lay_certified.setVisibility(View.GONE);
                                    lay_not_certified.setVisibility(View.VISIBLE);
                                }
                            }

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }
}
