package com.open_source.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.AboutUsActivity;
import com.open_source.activity.ContactUsActivity;

import com.open_source.activity.LoginActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.CircleImageView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuestYouFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = GuestYouFragment.class.getSimpleName();
    Context context;
    View rootView;
    CircleImageView profile_img;
    TextView toolbar_title, ctv_policy, ctv_logout, ctv_login, ctv_name, ctv_email, ctv_search, ctv_saved_searches,
            ctv_favourites, ctv_dashboard, ctv_about, ctv_contact_us, ctv_notification,
            ctv_rate_us, ctv_privacy_policy, ctv_fb, ctv_twitter, ctv_version;
    Toolbar toolbar;
    ProgressHUD progressHUD;
    Dialog dialog;
    ImageView close, edit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_you_guest, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        ctv_login = rootView.findViewById(R.id.ctv_login);
        ctv_about = rootView.findViewById(R.id.ctv_about);
        ctv_contact_us = rootView.findViewById(R.id.ctv_contact_us);
        ctv_privacy_policy = rootView.findViewById(R.id.ctv_privacy_policy);
        ctv_fb = rootView.findViewById(R.id.ctv_fb);
        ctv_twitter = rootView.findViewById(R.id.ctv_twitter);
        ctv_version = rootView.findViewById(R.id.ctv_version);
        toolbar_title.setText(R.string.you);
        ctv_login.setOnClickListener(this);
        ctv_about.setOnClickListener(this);
        ctv_contact_us.setOnClickListener(this);
        ctv_privacy_policy.setOnClickListener(this);
        ctv_fb.setOnClickListener(this);
        ctv_twitter.setOnClickListener(this);
        ctv_version.setOnClickListener(this);
        ctv_login.setVisibility(View.VISIBLE);

       /* if (!SharedPref.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
            ctv_login.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
            profile_img.setVisibility(View.GONE);
            ctv_name.setVisibility(View.GONE);
            ctv_email.setVisibility(View.GONE);
        } else {
            ctv_logout.setVisibility(View.VISIBLE);
            String img = SharedPref.getSharedPreferences(context, Constants.USER_PROFILE_IMAGE);
            Log.e(TAG, "<<<<<<<<0" + img);
            Glide.with(context).load(R.drawable.you_gold).into(profile_img);
            if (img.length() < 0) {
                Log.e(TAG, "<<<<<<<<1" + img);
                Glide.with(context).load(SharedPref.getSharedPreferences(context, Constants.USER_PROFILE_IMAGE)).into(profile_img);
                //Glide.with(context).load(R.drawable.you).into(profile_img);
            }
            ctv_name.setText(SharedPref.getSharedPreferences(context, Constants.FIRST_NAME));
            ctv_email.setText(SharedPref.getSharedPreferences(context, Constants.EMAIL));
        }*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_login:
                startActivity(new Intent(context, LoginActivity.class));
                getActivity().finish();
                break;
//            case R.id.ctv_favourites:
//                if (!SharedPref.getSharedPreferences(context, Constants.EVENT_CHECK).equals("1")) {
//                    Utility.ShowToastMessage(context, getString(R.string.you_need_to_login_first));
//                } else {
//                    startActivity(new Intent(context, FavouritesActivity.class));
//                }
//                break;

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
        }
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
}
