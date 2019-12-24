package com.open_source.fragment;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.open_source.LiveFeeds.FolloReqActivity;
import com.open_source.R;
import com.open_source.activity.AboutUsActivity;
import com.open_source.activity.ActivityFaq;
import com.open_source.activity.ContactUsActivity;
import com.open_source.activity.DashBoardActivity;
import com.open_source.activity.EditProfileActivity;
import com.open_source.activity.LoginActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.CurrencyListAdapter;
import com.open_source.adapter.MyPropertyAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.modal.UserList;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YouFragement extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{
    private static final String TAG = GuestYouFragment.class.getSimpleName();
    Context context;
    View rootView;
    CircleImageView profile_img;
    TextView toolbar_title, ctv_name, ctv_email, edit, dashboard, ctv_logout, ctv_policy;
    Toolbar toolbar;
    ProgressHUD progressHUD;
    Dialog dialog;
    RecyclerView recyclerView;
    LinearLayout txt_following, txt_followers;
    View view;
    ArrayList<RetroObject> ObjectList = new ArrayList<>();
    ArrayList<UserList> sellList = new ArrayList<>();
    int page = 0;
    String img;
    RelativeLayout lay_certified, lay_not_certified, lay_notfound, rel_follow;
    Boolean load = true;
    UserData userData;
    RelativeLayout rel_you_noti;
    ImageView img_overflow, close,icon_hs;

    private MyPropertyAdapter myPropertyAdapter;
    private ArrayList<RetroObject> currencyList = new ArrayList<>();
    private CurrencyListAdapter currencyListAdapter;
    private RecyclerView currencyListRV;
    private EditText edtSearchCurrency;
    private BottomSheetDialog currencyChangeDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        rootView = inflater.inflate(R.layout.fragement_you, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    private void init() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        img_overflow = rootView.findViewById(R.id.img_overflow);
        rel_you_noti = rootView.findViewById(R.id.rel_you_noti);
        lay_notfound = rootView.findViewById(R.id.lay_notfound);
        rel_follow = rootView.findViewById(R.id.rel_follow);
        rel_follow.setVisibility(View.VISIBLE);
        rel_follow.setOnClickListener(this);
        toolbar_title.setText(R.string.profile);
        img_overflow.setVisibility(View.VISIBLE);
        img_overflow.setOnClickListener(this);
        rel_you_noti.setVisibility(View.VISIBLE);
        rel_you_noti.setOnClickListener(this);
        profile_img = rootView.findViewById(R.id.profile_img);
        view = rootView.findViewById(R.id.view);
        ctv_name = rootView.findViewById(R.id.ctv_name);
        ctv_email = rootView.findViewById(R.id.ctv_email);
        edit = rootView.findViewById(R.id.edit);
        dashboard = rootView.findViewById(R.id.ctv_dashboard);
        lay_certified = rootView.findViewById(R.id.layout_certified);
        lay_not_certified = rootView.findViewById(R.id.layout_notcertified);
        txt_following = rootView.findViewById(R.id.rel_following);
        txt_followers = rootView.findViewById(R.id.rel_followers);
        icon_hs=rootView.findViewById(R.id.icon_hs);
        icon_hs.setOnClickListener(this);
        txt_following.setOnClickListener(this);
        txt_followers.setOnClickListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.property_grid);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        myPropertyAdapter = new MyPropertyAdapter(context, sellList, "you");
        recyclerView.setAdapter(myPropertyAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    Log.e(TAG, String.valueOf(page));
                    Log.e(TAG, load.toString());
                    if (page != 0 && load == true) {
                        load = false;
                        Log.e(TAG, "=====onScrollStateChanged");
                        FunGetList();
                    }
                }
            }
        });

        callProfileAPI();
        FunGetList();

        dashboard.setOnClickListener(this);
        edit.setOnClickListener(this);

       // fun_date();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_hs:
                showDialog();
                break;
            case R.id.edit:
                startActivity(new Intent(context, EditProfileActivity.class));
                break;
            case R.id.ctv_dashboard:
                startActivity(new Intent(context, DashBoardActivity.class));
                break;
            case R.id.img_overflow:
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.menu_you, popup.getMenu());
                popup.setOnMenuItemClickListener(this);
                popup.show();
                break;
            case R.id.rel_you_noti:
                startActivity(new Intent(context, NotificationFragment.class));
                break;
            case R.id.close:
                dialog.dismiss();
                break;
            case R.id.rel_follow:
                startActivity(new Intent(context, FolloReqActivity.class));
                break;
            case R.id.rel_followers:
                if (userData.getFollowers()!=null)
                startActivity(new Intent(context, FolloReqActivity.class).putExtra(Constants.FOLLOWERS,userData.getFollowers()).putExtra(Constants.TYPE,Constants.FOLLOWERS));
                break;
            case R.id.rel_following:
                startActivity(new Intent(context, FolloReqActivity.class).putExtra(Constants.FOLLOWERS,userData.getFollowing()).putExtra(Constants.TYPE,Constants.FOLLOWING));
                break;
        }
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_alert);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        ((TextView) dialog.findViewById(R.id.detail)).setText(R.string.msg_hs);

        ((TextView) dialog.findViewById(R.id.btn_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HS_WEB)));
            }
        });
        ((TextView) dialog.findViewById(R.id.btn_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void FunGetList() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetSellList(SharedPref.getSharedPreferences(context, Constants.TOKEN), page + "").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ObjectList.clear();
                            load = false;
                            ObjectList = response.body().getObject();
                            for (int i = 0; i < ObjectList.size(); i++) {
                                if (ObjectList.get(i).getProperty_img().size() != 0) {
                                    img = ObjectList.get(i).getProperty_img().get(0).getFile_name();
                                } else {
                                    img = "";
                                }
                                sellList.add(new UserList(ObjectList.get(i).getId(), img, ObjectList.get(i).getType(), ObjectList.get(i).getLocation(), ObjectList.get(i).getPurpose()));
                            }
                            if (ObjectList.size() == 10) {
                                page = page + 1;
                                load = true;
                            }
                            myPropertyAdapter.notifyDataSetChanged();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else if (response.body().getStatus() == 404){
                            load = false;
                                lay_notfound.setVisibility(View.VISIBLE);
                                view.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                        } else {
                            load = false;
                            if (response.body().getMessage().contains("No Data Exist.") && page == 0) {
                                lay_notfound.setVisibility(View.VISIBLE);
                                view.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            // Utility.ShowToastMessage(context, response.body().getMessage());
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
                    Utility.ShowToastMessage(context, getString(R.string.msg_server_failed));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    private void inflateSelectCurrencyDialoge(String flagUrl){                // Show change currency list dialogue
        View view = getLayoutInflater().inflate(R.layout.dialog_select_currency, null);
        currencyChangeDialog = new BottomSheetDialog(context);

        currencyListRV = view.findViewById(R.id.currencyListRV);
        edtSearchCurrency = view.findViewById(R.id.edtSearch);
        currencyListRV.setLayoutManager(new LinearLayoutManager(context));
        TextView txvButtonSubmit = view.findViewById(R.id.txvButtonSubmit);

        currencyListAdapter = new CurrencyListAdapter(context, currencyList, flagUrl);
        currencyListRV.setAdapter(currencyListAdapter);

        txvButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currencyListAdapter.selectedCurrency.length()!=0){
                    getUpdateCurrencyAPI(currencyListAdapter.selectedCurrency, currencyListAdapter.currencySymbol);
                } else {
                    Utility.ShowToastMessage(context, getString(R.string.error_select_currency));
                }
            }
        });
        currencyChangeDialog.setContentView(view);
        currencyChangeDialog.show();

        edtSearchCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()!=0){
                    currencyListAdapter.getFilter().filter(s);
                }
            }
        });
    }

    public void getCurrencyListAPI() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getCurrencyList().enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    } try {
                        if (response.body().getStatus() == 200) {
                            currencyList  = response.body().getObject();
                            if (currencyList.size()!=0)
                                inflateSelectCurrencyDialoge(response.body().getFlagUrl());
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            context.startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    }//Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        }  else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }

    private void getUpdateCurrencyAPI(final String currency, final String currencySymbol) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getUpdateCurrencyAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN), currency).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        Log.e(TAG, "onResponse: " + response.toString());
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            SharedPref.setSharedPreference(context, Constants.CURRENCY, currency);
                            SharedPref.setSharedPreference(context, Constants.CURRENCY_SYMBOL, currencySymbol);
                            if (currencyChangeDialog!=null){
                                currencyChangeDialog.cancel();
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
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    //Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.changeCurrency:               // click of change currency dialog
//                inflateSelectCurrencyDialoge();
                getCurrencyListAPI();
                break;*/

            case R.id.about:
                startActivity(new Intent(context, AboutUsActivity.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(context, ContactUsActivity.class));
                break;
            case R.id.faq:
                startActivity(new Intent(context, ActivityFaq.class));
                break;
            case R.id.privacy_policy:
                showDialogTermsCondition();
                break;
            case R.id.like_facebbook:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tt555054")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/tt555054")));
                }
                break;
            case R.id.logout:
                logoutAPI();
                break;
            case R.id.like_twitter:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/search?q=@Chandan83973953")));
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://mobile.twitter.com/search?q=@Chandan83973953")));
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "=========: "+"onResume" );
        profile_update();
    }

    //---------------------- Get Profile --------------------------
    public void callProfileAPI() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().getProfileAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            String img = SharedPref.getSharedPreferences(context, Constants.USER_PROFILE_IMAGE);
                            ((TextView) rootView.findViewById(R.id.tab_counter_text)).setText(userData.getNo_of_request());
                            ((TextView) rootView.findViewById(R.id.txt_following)).setText(String.valueOf(userData.getFollowing().size()));
                            ((TextView) rootView.findViewById(R.id.txt_followers)).setText(String.valueOf(userData.getFollowers().size()));
                            ((TextView)rootView.findViewById(R.id.txt_post)).setText(String.valueOf(userData.getProperty_count()));
                            ctv_email.setText(userData.getEmail());
                            ((TextView) rootView.findViewById(R.id.noticount_text)).setText(userData.getTotal_notification());
                            //  Log.e(TAG, "=========: "+userData.getUrl()+userData.getProfileImage() );
                            if (img.length() < 0) {
                                Glide.with(context).load(userData.getUrl() + userData.getProfileImage()).into(profile_img);
                            }
                            if (userData.getUserType().equals("2") || userData.getUserType().equals("3")) {
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

    public void profile_update() {
        String img = SharedPref.getSharedPreferences(getActivity(), Constants.USER_PROFILE_IMAGE);
        Glide.with(getActivity()).load(SharedPref.getSharedPreferences(getActivity(), Constants.USER_PROFILE_IMAGE)).into(profile_img);
        if (img.length() < 0) {
            Glide.with(getActivity()).load(R.drawable.user).into(profile_img);
        }
        ctv_name.setText(SharedPref.getSharedPreferences(getActivity(), Constants.FIRST_NAME));
        ctv_email.setText(SharedPref.getSharedPreferences(getActivity(), Constants.EMAIL));

    }
}
