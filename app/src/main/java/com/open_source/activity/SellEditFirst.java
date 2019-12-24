package com.open_source.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.Subcatogery;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SellEditFirst extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = SellEditFirst.class.getSimpleName();
    Context context;
    UserData userData;
    ProgressHUD progressHUD;
    EditText cet_first_name, cet_last_name, cet_contact, et_areasquare, edit_desc_pets, edit_desc_smoking,
            edit_desc_parking, edit_rent_amount;
    RadioGroup radio_group_sell, radio_group_rent, radio_pets, radio_smoking, radio_parking;
    RadioButton rb_investor, rb_agent, rb_owners;
    SwitchCompat stw_private, stw_for_sale, stw_for_rent, stw_wanted, stw_forclosure_sale, stw_short_sale,
            stw_home, stw_plot, stw_commercial, stw_flat, stw_sell_by_ownwer, stw_Condominium,
            stw_SingleFamily, stw_Townhouse, stw_Semi_detached_House, stw_Duplex_Triplex;
    Spinner spin_bedroom, spin_bathroom;
    Toolbar h_toolbar;
    CardView card_rent;
    Boolean flag = false;
    int id = 0, id_pets = -1, id_smoking = -1, id_parking = -1, idx_rent = -1;
    ArrayList<Subcatogery> array_img = new ArrayList<>();
    LinearLayout lay_bathroom, lay_bedroom;
    TextView ctv_close, ctv_clear, btn_upload_next, cet_society, cet_city, toolbar_title;
    String str_sell_type = "", str_purpose = "", str_type = "", str_lat = "",
            str_lng = "", str_bedroom = "", str_bathroom = "",
            str_pets = "No", str_parking = "No", str_smoking = "No", str_rent_status = "", str_renter_type = "";


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    String MobilePattern = "[0-9]{10}";


    private AutoCompleteTextView cet_location;
    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_property);
        context = SellEditFirst.this;
        init();
    }

    private void init() {
        h_toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(h_toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = h_toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.sell_property);
        card_rent = findViewById(R.id.card_rent);
        ctv_close = findViewById(R.id.ctv_close);
        ctv_clear = findViewById(R.id.ctv_clear);
        cet_first_name = findViewById(R.id.cet_first_name);
        cet_last_name = findViewById(R.id.cet_last_name);
        cet_contact = findViewById(R.id.cet_contact);
        cet_contact.addTextChangedListener(new PhoneNumberTextWatcher(cet_contact));
        cet_society = findViewById(R.id.cet_society);
        cet_city = findViewById(R.id.cet_city);
        cet_location = findViewById(R.id.cet_location);
        et_areasquare = findViewById(R.id.et_areasquare);
        radio_group_sell = findViewById(R.id.radio_group_sell);
        rb_investor = findViewById(R.id.rb_investor);
        rb_agent = findViewById(R.id.rb_agent);
        rb_owners = findViewById(R.id.rb_owners);
        edit_desc_pets = findViewById(R.id.edit_desc_pets);
        edit_desc_parking = findViewById(R.id.edit_desc_parking);
        edit_desc_smoking = findViewById(R.id.edit_desc_smoking);
        edit_rent_amount = findViewById(R.id.edit_rent_amount);
        stw_private = findViewById(R.id.stw_private);
        radio_group_rent = findViewById(R.id.rent_selection);
        stw_for_sale = findViewById(R.id.stw_for_sale);
        stw_flat = findViewById(R.id.stw_flat);
        stw_for_rent = findViewById(R.id.stw_for_rent);
        radio_pets = findViewById(R.id.radio_pets);
        radio_smoking = findViewById(R.id.radio_smoking);
        radio_parking = findViewById(R.id.radio_parking);
        stw_wanted = findViewById(R.id.stw_wanted);
        stw_forclosure_sale = findViewById(R.id.stw_forclosure_sale);
        stw_short_sale = findViewById(R.id.stw_short_sale);
        stw_sell_by_ownwer = findViewById(R.id.stw_sell_by_ownwer);
        stw_home = findViewById(R.id.stw_home);
        stw_plot = findViewById(R.id.stw_plot);
        stw_commercial = findViewById(R.id.stw_commercial);
        stw_sell_by_ownwer = findViewById(R.id.stw_sell_by_ownwer);
        stw_Condominium = findViewById(R.id.stw_Condominium);
        stw_SingleFamily = findViewById(R.id.stw_SingleFamily);
        stw_Townhouse = findViewById(R.id.stw_Townhouse);
        stw_Semi_detached_House = findViewById(R.id.stw_Semi_detached_House);
        stw_Duplex_Triplex = findViewById(R.id.stw_Duplex_Triplex);

        spin_bedroom = findViewById(R.id.spin_bedroom);
        spin_bathroom = findViewById(R.id.spin_bathroom);
        btn_upload_next = findViewById(R.id.btn_upload_next);
        lay_bathroom = findViewById(R.id.layout_bathroom);
        lay_bedroom = findViewById(R.id.layout_bedroom);
        ctv_close.setVisibility(View.GONE);
        ctv_close.setOnClickListener(this);
        ctv_clear.setOnClickListener(this);
        ctv_clear.setVisibility(View.GONE);
        btn_upload_next.setOnClickListener(this);

        radio_pets.setOnCheckedChangeListener(this);
        radio_parking.setOnCheckedChangeListener(this);
        radio_smoking.setOnCheckedChangeListener(this);
        // cet_location.setOnClickListener(this);

        stw_for_sale.setClickable(false);
        stw_wanted.setClickable(false);
        stw_forclosure_sale.setClickable(false);
        stw_short_sale.setClickable(false);
        stw_short_sale.setClickable(false);
        stw_sell_by_ownwer.setClickable(false);

        stw_home.setClickable(false);
        stw_plot.setClickable(false);
        stw_flat.setClickable(false);
        stw_commercial.setClickable(false);

        stw_Condominium.setClickable(false);
        stw_SingleFamily.setClickable(false);
        stw_Townhouse.setClickable(false);
        stw_Semi_detached_House.setClickable(false);
        stw_Duplex_Triplex.setClickable(false);
        FunGetSell();
        initialisePlacePicker();
    }

    private void initialisePlacePicker() {
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
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Place place = task.getPlace();
                            cet_location.setText(place.getAddress());
                            String latLng = String.valueOf(place.getLatLng());
                            latLng = latLng.replace("lat/lng: ", "");
                            latLng = latLng.replace("(", "");
                            latLng = latLng.replace(")", "");
                            String[] array_latLng = latLng.split(",");
                            str_lat = array_latLng[0];
                            str_lng = array_latLng[1];
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            List<Address> addresses = null;
                            try {
                                addresses = geocoder.getFromLocation(Double.parseDouble(str_lat), Double.parseDouble(str_lng), 1);
                                cet_city.setText(addresses.get(0).getLocality());
                                cet_society.setText(addresses.get(0).getAdminArea());
                                cet_city.setText(addresses.get(0).getAddressLine(0));
                                ;
                                cet_society.setText(addresses.get(0).getAddressLine(1));
                                ;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void FunGetSell() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    getIntent().getExtras().getString(Constants.PROPERTY_ID, "")).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            cet_first_name.setText(userData.getSeller_firstname());
                            cet_last_name.setText(userData.getSeller_lastname());
                            if (userData.getSeller_contact().length() >= 10)
                                cet_contact.setText(Utility.formatNumbersAsCode(userData.getSeller_contact()));
                            else
                                cet_contact.setText(userData.getSeller_contact());
                            cet_location.setText(userData.getLocation());
                            cet_city.setText(userData.getCity());
                            cet_society.setText(userData.getSociety());
                            str_lat = userData.getLocation_latitude();
                            str_lng = userData.getLocation_longitude();
                            et_areasquare.setText(userData.getArea_square());
                            str_type = userData.getType();
                            str_purpose = userData.getPurpose();
                            if (userData.getProperty_for().equals("2")) {
                                flag = true;
                                ((CardView) findViewById(R.id.card_sell_type)).setVisibility(View.GONE);
                                ((CardView) findViewById(R.id.card_purpose)).setVisibility(View.GONE);
                                card_rent.setVisibility(View.VISIBLE);

                                /*-----------------------------------------*/
                                edit_rent_amount.setText(userData.getRent_amount());

                                toolbar_title.setText(R.string.title_rent_property);
                                if (userData.getPets().equalsIgnoreCase("No")) {
                                    ((RadioButton) radio_pets.getChildAt(1)).setChecked(true);
                                    id_pets = 1;
                                } else {
                                    ((RadioButton) radio_pets.getChildAt(0)).setChecked(true);
                                    edit_desc_pets.setText(userData.getPets_details());
                                    id_pets = 0;
                                }
                                if (userData.getSmoking_allowed().equalsIgnoreCase("No")) {
                                    ((RadioButton) radio_smoking.getChildAt(1)).setChecked(true);
                                    id_smoking = 1;
                                } else {
                                    ((RadioButton) radio_smoking.getChildAt(0)).setChecked(true);
                                    edit_desc_smoking.setText(userData.getSmoking_details());
                                    id_smoking = 0;
                                }
                                if (userData.getParking().equalsIgnoreCase("No")) {
                                    ((RadioButton) radio_parking.getChildAt(1)).setChecked(true);
                                    id_parking = 1;
                                } else {
                                    ((RadioButton) radio_parking.getChildAt(0)).setChecked(true);
                                    edit_desc_parking.setText(userData.getParking_details());
                                    id_parking = 0;
                                }
                               /* if (userData.getRenter_type().equals(""))
                                {
                                    str_renter_type="";

                                }
                                else {
                                    str_renter_type="";
                                }*/


                            } else {
                                if (userData.getSell_type().equalsIgnoreCase(getString(R.string.investor))) {
                                    ((RadioButton) radio_group_sell.getChildAt(0)).setChecked(true);
                                } else if (userData.getSell_type().equalsIgnoreCase(getString(R.string.agent))) {
                                    ((RadioButton) radio_group_sell.getChildAt(1)).setChecked(true);
                                } else if (userData.getSell_type().equalsIgnoreCase(getString(R.string.owners))) {
                                    ((RadioButton) radio_group_sell.getChildAt(2)).setChecked(true);
                                }

                                if (str_purpose.contains(getString(R.string.for_sell_by_owner))) {
                                    stw_sell_by_ownwer.setChecked(true);
                                }
                                if (str_purpose.contains(getString(R.string.short_sale))) {
                                    stw_short_sale.setChecked(true);
                                }
                                if (str_purpose.contains(getString(R.string.foreclosure_sale))) {
                                    stw_forclosure_sale.setChecked(true);
                                }
                                if (str_purpose.contains(getString(R.string.wanted))) {
                                    stw_wanted.setChecked(true);
                                }
                                if (str_purpose.contains(getString(R.string.for_sale))) {
                                    stw_for_sale.setChecked(true);
                                }

                            }
                            if (str_type.contains(getString(R.string.home))) {
                                stw_home.setChecked(true);
                            }
                            if (str_type.contains(getString(R.string.plot))) {
                                stw_plot.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.flat))) {
                                stw_flat.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.commercial))) {
                                stw_commercial.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.condominium))) {
                                stw_Condominium.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.single_family))) {
                                stw_SingleFamily.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.town_house))) {
                                stw_Townhouse.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.semi_detached_house))) {
                                stw_Semi_detached_House.setChecked(true);

                            }
                            if (str_type.contains(getString(R.string.duplex))) {
                                stw_Duplex_Triplex.setChecked(true);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_close:
                break;

            case R.id.cet_location:
                /*if (Utility.checkPlayServices(context)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build((Activity) context);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
                break;

            case R.id.ctv_clear:
                break;

            case R.id.btn_upload_next:
                if (!str_type.equals("Plot")) {
                    if (spin_bedroom.getSelectedItem().toString().equals("Select No. of bedrooms")) {
                        str_bedroom = "";
                    } else {
                        str_bedroom = spin_bedroom.getSelectedItem().toString();
                    }
                    if (spin_bathroom.getSelectedItem().toString().equals("Select No. of bathrooms")) {
                        str_bathroom = "";
                    } else {
                        str_bathroom = spin_bathroom.getSelectedItem().toString();
                    }
                }
                int radioButtonID = radio_group_sell.getCheckedRadioButtonId();
                View radioButton = radio_group_sell.findViewById(radioButtonID);
                int idx = radio_group_sell.indexOfChild(radioButton);
                if (flag == true) {
                    int radioButtonID1 = radio_group_rent.getCheckedRadioButtonId();
                    View radioButton1 = radio_group_rent.findViewById(radioButtonID1);
                    idx_rent = radio_group_rent.indexOfChild(radioButton1);
                }
                if (cet_first_name.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_fname);
                } else if (cet_last_name.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_lname);
                } else if (cet_contact.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact);
                } else if (cet_contact.getText().toString().length() > 13) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if (cet_contact.getText().toString().length() < 10) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if (idx == -1 && flag == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_selltype));
                } else if (str_purpose.equals("") && flag == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_purpose));
                } else if (str_type.equals("")) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_type));
                } else if (idx_rent == -1 && flag == true) {
                    Utility.ShowToastMessage(context, getString(R.string.select_renter_type));
                } else if (flag == true && edit_rent_amount.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.val_rent_amount));
                } else if (flag == true && id_pets == -1) {
                    Utility.ShowToastMessage(context, getString(R.string.val_pets_allowed));
                } else if (flag == true && id_pets == 0 && edit_desc_pets.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.if_yes_describe_pets_allowed));
                } else if (flag == true && id_smoking == -1) {
                    Utility.ShowToastMessage(context, getString(R.string.val_smoking_allow));
                } else if (flag == true && id_smoking == 0 && edit_desc_smoking.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.if_yes_describe_smoking_allowed));
                } else if (flag == true && id_parking == -1) {
                    Utility.ShowToastMessage(context, getString(R.string.val_parking_allow));
                } else if (flag == true && id_parking == 0 && edit_desc_parking.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.if_yes_describe_parking_allowed));
                } else if (str_lat.isEmpty() && str_lng.isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pick_ocation));
                } else if (str_bedroom.equals("") && !str_type.equals(getString(R.string.plot))) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bedroom));
                } else if (str_bathroom.equals("") && !str_type.equals(getString(R.string.plot))) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bathroom));
                } else if (et_areasquare.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_area));
                } else {
                    array_img.clear();
                    if (flag == true) {
                        str_rent_status = "2";
                        if (id_pets == 0)
                            str_pets = "Yes";
                        if (id_smoking == 0)
                            str_smoking = "Yes";
                        if (id_parking == 0)
                            str_parking = "Yes";
                        RadioButton r = (RadioButton) radio_group_rent.getChildAt(idx_rent);
                        str_renter_type = r.getText().toString();
                        if (str_renter_type.equals(getString(R.string.vacation_renter)))
                            str_renter_type = Constants.Str_VacationalRental;
                        else
                            str_renter_type = Constants.Str_LongTermRental;
                    } else {
                        str_pets = "";
                        str_smoking = "";
                        str_parking = "";
                        str_rent_status = "1";
                        edit_rent_amount.setText("");
                        RadioButton r = (RadioButton) radio_group_sell.getChildAt(idx);
                        str_sell_type = r.getText().toString();

                    }
                    if (userData.getProperty_img().size() > 0) {
                        for (int i = 0; i < userData.getProperty_img().size(); i++) {
                            array_img.add(new Subcatogery(userData.getProperty_img().get(i).getImage_id(), userData.getProperty_img().get(i).getFile_name()));
                        }
                        startActivity(new Intent(context, SellEditSecond.class).putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID)).
                                putExtra(Constants.FIRST_NAME, cet_first_name.getText().toString().trim()).
                                putExtra(Constants.LAST_NAME, cet_last_name.getText().toString().trim()).
                                putExtra(Constants.MOBILE, cet_contact.getText().toString().trim()).putExtra(Constants.SELL_TYPE, str_sell_type).
                                putExtra(Constants.PURPOSE, str_purpose).putExtra(Constants.TYPE, str_type).
                                putExtra(Constants.LOCATION, cet_location.getText().toString()).putExtra(Constants.BADROOM, str_bedroom).
                                putExtra(Constants.BATHROOM, str_bathroom).putExtra(Constants.LOCATION_LATITUDE, str_lat).
                                putExtra(Constants.LOCATION_LONGITUDE, str_lng).putExtra(Constants.SOCIETY, cet_society.getText().toString().trim()).
                                putExtra(Constants.CITY, cet_city.getText().toString()).putExtra(Constants.AREA, et_areasquare.getText().toString().trim()).
                                putExtra(Constants.POST, userData.getPost()).putExtra(Constants.FIXED_PRICE, userData.getFixed_price()).
                                putExtra(Constants.AUCTION_TYPE, userData.getAuction_type()).putExtra(Constants.START_DATE, userData.getStart_date()).
                                putExtra(Constants.END_DATE, userData.getEnd_date()).putExtra(Constants.MINIMUM_PRICE, userData.getMinimum_price()).
                                putExtra(Constants.TRANSACTION_HISTORY, userData.getTransaction_history()).
                                putExtra(Constants.DESCRIPTION, userData.getDiscription()).putExtra(Constants.DISCLOSER, userData.getDiscloser()).
                                putParcelableArrayListExtra(Constants.IMAGE, array_img).putExtra(Constants.VIDEO, userData.getVedio()).putExtra(Constants.VIDEOTHUMB, userData.getVideo_thumbnail()).
                                putExtra(Constants.VIDEO_DATE, userData.getVedio_date()).putExtra(Constants.START_TIME, userData.getStart_time()).
                                putExtra(Constants.END_TIME, userData.getEnd_time()).
                                putExtra(Constants.VIDEO_TIME, userData.getVedio_time()).putExtra(Constants.VIDEOID, userData.getVedio_id()).
                                putExtra(Constants.VIDEO_DESC, userData.getOpen_house_desc()).
                                putExtra(Constants.PROPERTY_FOR, str_rent_status).putExtra(Constants.RENT_AMOUNT, edit_rent_amount.getText().toString()).
                                putExtra(Constants.PETS, str_pets).
                                putExtra(Constants.PETS_DETAIL, edit_desc_pets.getText().toString()).
                                putExtra(Constants.SMOKING, str_smoking).
                                putExtra(Constants.SMOKING_DETAIL, edit_desc_smoking.getText().toString()).
                                putExtra(Constants.PARKING, str_parking).
                                putExtra(Constants.PARKING_DETAIL, edit_desc_parking.getText().toString()).
                                putExtra(Constants.RENTER_TYPE, str_renter_type));

                    } else {
                        Utility.ShowToastMessage(context, "Server Error");
                    }


                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
           /* if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                *//*cet_location.setText(place.getAddress());
                String latLng = String.valueOf(place.getLatLng());
                latLng = latLng.replace("lat/lng: ", "");
                latLng = latLng.replace("(", "");
                latLng = latLng.replace(")", "");
                String[] array_latLng = latLng.split(",");
                str_lat = array_latLng[0];
                str_lng = array_latLng[1];
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(str_lat), Double.parseDouble(str_lng), 1);
                    cet_city.setText(addresses.get(0).getLocality());
                    cet_society.setText(addresses.get(0).getAdminArea());
                    cet_city.setText(addresses.get(0).getAddressLine(0));;
                    cet_society.setText(addresses.get(0).getAddressLine(1)); ;
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*

                //Log.i("====", "latlong: " + place.getLatLng());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(context, data);
                // TODO: Handle the error.
                //Log.i("====", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }*/
        }
    }

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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radio_pets:
                id_pets = radio_pets.indexOfChild(radio_pets.findViewById(radio_pets.getCheckedRadioButtonId()));
                if (id_pets == 1)
                    edit_desc_pets.setText("");
                break;
            case R.id.radio_smoking:
                id_smoking = radio_smoking.indexOfChild(radio_smoking.findViewById(radio_smoking.getCheckedRadioButtonId()));
                if (id_smoking == 1)
                    edit_desc_smoking.setText("");
                break;
            case R.id.radio_parking:
                id_parking = radio_parking.indexOfChild(radio_parking.findViewById(radio_parking.getCheckedRadioButtonId()));
                if (id_parking == 1)
                    edit_desc_parking.setText("");
                break;
        }
    }
}
