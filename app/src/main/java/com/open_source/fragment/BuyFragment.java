package com.open_source.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.open_source.R;
import com.open_source.activity.FilterActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.adapter.CurrencyListAdapter;
import com.open_source.adapter.ViewPagerAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import open_source.com.dachshundtablayout.DachshundTabLayout;
import open_source.com.dachshundtablayout.indicator.LineMoveIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED;
import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class BuyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = BuyFragment.class.getSimpleName();
    public static Boolean visible_status = false;
    Context context;
    DachshundTabLayout tabLayout;
    ViewPager viewPager;
    View rootView;
    double latitude, logitude;
    RelativeLayout lay_search;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    FloatingActionButton fab;
    RelativeLayout fabLayout_location, fabLayout_raduis;
    boolean isFABOpen = false;
    BottomSheetBehavior mBottomSheetBehavior2;
    ViewPagerAdapter adapter;
    ImageView icon_serach;
    private LinearLayout lay_radius;

    private ArrayList<RetroObject> currencyList = new ArrayList<>();
    private CurrencyListAdapter currencyListAdapter;
    private RecyclerView currencyListRV;
    private EditText edtSearchCurrency;
    private BottomSheetDialog currencyChangeDialog;
    private ProgressHUD progressHUD;

    private AutoCompleteTextView ctv_title_location_name;
    private AutoCompleteAdapter autoCompleteAdapter;
    private PlacesClient placesClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_buy, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        //Log.e(TAG, "=========: " + "init");
        // search = (ImageView) rootView.findViewById(R.id.search);
        icon_serach = rootView.findViewById(R.id.icon_serach);
        icon_serach.setOnClickListener(this);
        ctv_title_location_name = rootView.findViewById(R.id.title_location_name);
        ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
        latitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LATITUDE));
        logitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LONGITUDE));

        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (DachshundTabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setAnimatedIndicator(new LineMoveIndicator(tabLayout));
        tabLayout.setupWithViewPager(viewPager);
        //search.setOnClickListener(this);
        lay_search = rootView.findViewById(R.id.lay_search);
        lay_search.setOnClickListener(this);
        ctv_title_location_name.setOnClickListener(this);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabLayout_location = (RelativeLayout) rootView.findViewById(R.id.fabLayout_location);
        fabLayout_raduis = (RelativeLayout) rootView.findViewById(R.id.fabLayout_raduis);

        ((FloatingActionButton) rootView.findViewById(R.id.fb_radius)).setOnClickListener(this);
        ((FloatingActionButton) rootView.findViewById(R.id.fb_location)).setOnClickListener(this);
        ((CardView)rootView.findViewById(R.id.cancle)).setOnClickListener(this);
        final View bottomSheet2 = rootView.findViewById(R.id.bottom_sheet2);
        lay_radius = rootView.findViewById(R.id.lay_radius);
        String[] array_radius = context.getResources().getStringArray(R.array.array_redius);
        for (int i = 0; i < array_radius.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.test, lay_radius, false);
            final TextView txt = view.findViewById(R.id.txt);
            txt.setText(array_radius[i]);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                    } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                    int pos = viewPager.getCurrentItem();
                    Fragment activeFragment = adapter.getItem(pos);
                    if (pos == 0) {
                        ((DiscoverFragment) activeFragment).radius(txt.getText().toString());
                    }
                    if (pos == 1) {
                        ((RecommendedFragment) activeFragment).radius(txt.getText().toString());
                    }
                    if (pos == 2) {
                        ((RecentlyViewFragment) activeFragment).radius(txt.getText().toString());
                    }
                }
            });
            lay_radius.addView(view);
        }
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        if(SharedPref.getSharedPreferences(context, Constants.CURRENCY)==null ){
            getCurrencyListAPI();             // Select currency functionality for dynamic currecny
        } else if(SharedPref.getSharedPreferences(context, Constants.CURRENCY).length()==0 ){
            getCurrencyListAPI();             // Select currency functionality for dynamic currecny
        }
        initialisePlacePicker();
    }

    private void inflateSelectCurrencyDialoge(String flagUrl){
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
                    Utility.ShowToastMessage(context, R.string.error_select_currency);
                }
            }
        });
        currencyChangeDialog.setContentView(view);
        currencyChangeDialog.show();
        currencyChangeDialog.setCancelable(false);

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
                    } //Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
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

                            setupViewPager(viewPager);
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

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(DiscoverFragment.newInstance(String.valueOf(latitude), String.valueOf(logitude)), getString(R.string.discover));
        adapter.addFragment(RecommendedFragment.newInstance(String.valueOf(latitude), String.valueOf(logitude)), getString(R.string.recommended));
        adapter.addFragment(RecentlyViewFragment.newInstance(String.valueOf(latitude), String.valueOf(logitude)), getString(R.string.recently_view));
        viewPager.setAdapter(adapter);
    }

    private void initialisePlacePicker(){
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(), getResources().getString(R.string.mapkey));
        }
        placesClient = Places.createClient(getContext());
//        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {
        ctv_title_location_name.setThreshold(1);
        ctv_title_location_name.setOnItemClickListener(autocompleteClickListener);
        autoCompleteAdapter = new AutoCompleteAdapter(getContext(), placesClient);
        ctv_title_location_name.setAdapter(autoCompleteAdapter);
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
                            ctv_title_location_name.setText(task.getPlace().getName() + "\n" + task.getPlace().getAddress());
                            String str_latLng = String.valueOf(place.getLatLng());
                            str_latLng = str_latLng.replace("lat/lng: ", "");
                            str_latLng = str_latLng.replace("(", "");
                            str_latLng = str_latLng.replace(")", "");
                            String[] array_latLng = str_latLng.split(",");
                            latitude = Double.valueOf(array_latLng[0]);
                            logitude = Double.valueOf(array_latLng[1]);
                            setupViewPager(viewPager);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            ctv_title_location_name.setText(e.getMessage());
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
            case R.id.icon_serach:
                startActivity(new Intent(context, FilterActivity.class).
                        putExtra(Constants.LOCATION, ctv_title_location_name.getText().toString()).
                        putExtra(Constants.LATITUDE, String.valueOf(latitude)).putExtra(Constants.LONGITUDE, String.valueOf(logitude)));
                break;

            case R.id.cancle:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.lay_search:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(context);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.title_location_name:
                fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                intent = new Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(context);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;

            case R.id.fb_radius:
                //closeFABMenu();
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.fb_location:
                //closeFABMenu();
                ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
                latitude = Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LATITUDE));
                logitude = Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LONGITUDE));
                setupViewPager(viewPager);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                ctv_title_location_name.setText(place.getAddress());
                String str_latLng = String.valueOf(place.getLatLng());
                str_latLng = str_latLng.replace("lat/lng: ", "");
                str_latLng = str_latLng.replace("(", "");
                str_latLng = str_latLng.replace(")", "");
                String[] array_latLng = str_latLng.split(",");
                latitude = Double.valueOf(array_latLng[0]);
                logitude = Double.valueOf(array_latLng[1]);
                setupViewPager(viewPager);

            }  else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // TODO: Handle the error.
                //Log.i("====", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        visible_status = true;
        Log.e(TAG, "=========: " + "onResume");
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        Log.e(TAG, "=========: " + "setUserVisibleHint");

    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout_location.setVisibility(View.VISIBLE);
        fabLayout_raduis.setVisibility(View.VISIBLE);
        //  fabBGLayout.setVisibility(View.VISIBLE);

        fab.animate().rotationBy(180).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (fab.getRotation() != 180) {
                    fab.setRotation(180);
                }
            }

            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        fabLayout_location.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fabLayout_raduis.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        //fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout_location.animate().translationY(0);
        fabLayout_raduis.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout_location.setVisibility(View.GONE);
                    fabLayout_raduis.setVisibility(View.GONE);
                }
                if (fab.getRotation() != -180) {
                    fab.setRotation(-180);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }
}
