package com.open_source.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.open_source.R;
import com.open_source.activity.MainActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.App;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SellPropertyFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SellPropertyFragment.class.getSimpleName();
    public static String post_type = "";
    public static int status = 0;
    Context context;
    View rootView;
    int id = 0, idx = 0;
    View radioButton;
    RadioButton rb;
    String str_rent = "", str_rent_status = "1", str_pets = "", str_parking = "", str_smoking = "";
    EditText cet_first_name, cet_last_name, cet_contact, et_areasquare, edit_desc_pets, edit_desc_smoking,
            edit_desc_parking, edit_rent_amount;
    RadioGroup radio_group_sell, radio_rent, radio_pets, radio_smoking, radio_parking;
    RadioButton rb_investor, rb_agent, rb_owners;
    SwitchCompat stw_private, stw_for_sale, stw_for_rent, stw_wanted, stw_forclosure_sale, stw_short_sale,
            stw_home, stw_plot, stw_commercial, stw_flat, stw_sell_by_ownwer, stw_Condominium,
            stw_SingleFamily, stw_Townhouse, stw_Semi_detached_House, stw_Duplex_Triplex;
    Spinner spin_bedroom, spin_bathroom;
    Toolbar h_toolbar;
    CardView card_rent;
    ArrayList<String> array_purpose = new ArrayList<>();
    ArrayList<String> array_type = new ArrayList<>();
    LinearLayout lay_bathroom, lay_bedroom, layout_rent;
    TextView ctv_close, ctv_clear, btn_upload_next, cet_society, cet_city, ctv_plot, toolbar_title;
    String str_fname, str_lname, str_number = "", str_sell_type = "", str_purpose = "For Rent", str_type = "", str_City = "", str_society = "", str_lat = "",
            str_lng = "", str_bedroom = "", str_bathroom = "", str_area, place_location = "", purposetype = "", str_post_type;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    //String MobilePattern = "[0-9]{12}";

    private AutoCompleteTextView cet_location;
    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;


    public static SellPropertyFragment newInstance(String type) {
        SellPropertyFragment sellPropertyFragment = new SellPropertyFragment();
        //Log.e(TAG, "==========: " + "newInstance");
        if (status == 0)
            post_type = type;
        Bundle args = new Bundle();
        sellPropertyFragment.setArguments(args);
        return sellPropertyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_sell_property, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        h_toolbar = rootView.findViewById(R.id.h_toolbar);
        ctv_close = rootView.findViewById(R.id.ctv_close);
        ctv_clear = rootView.findViewById(R.id.ctv_clear);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        card_rent = rootView.findViewById(R.id.card_rent);
        toolbar_title.setText(R.string.sell_property);
        card_rent.setVisibility(View.GONE);
        ((TextView)rootView.findViewById(R.id.ctv_for_rent)).setVisibility(View.VISIBLE);
       /* if (post_type.equals("Rent")) {
            ((CardView) rootView.findViewById(R.id.card_sell_type)).setVisibility(View.GONE);
            ((CardView) rootView.findViewById(R.id.card_purpose)).setVisibility(View.GONE);
            card_rent.setVisibility(View.VISIBLE);
            toolbar_title.setText(R.string.title_rent_property);

        }*/
        cet_first_name = rootView.findViewById(R.id.cet_first_name);
        cet_last_name = rootView.findViewById(R.id.cet_last_name);
        cet_contact = rootView.findViewById(R.id.cet_contact);
        cet_contact.addTextChangedListener(new PhoneNumberTextWatcher(cet_contact));
        cet_society = rootView.findViewById(R.id.cet_society);
        cet_city = rootView.findViewById(R.id.cet_city);
        cet_location = rootView.findViewById(R.id.cet_location);
        et_areasquare = rootView.findViewById(R.id.et_areasquare);
        radio_group_sell = rootView.findViewById(R.id.radio_group_sell);
        rb_investor = rootView.findViewById(R.id.rb_investor);
        rb_agent = rootView.findViewById(R.id.rb_agent);
        rb_owners = rootView.findViewById(R.id.rb_owners);
        radio_rent = rootView.findViewById(R.id.radio_rent);
        radio_pets = rootView.findViewById(R.id.radio_pets);
        radio_smoking = rootView.findViewById(R.id.radio_smoking);
        radio_parking = rootView.findViewById(R.id.radio_parking);
        stw_private = rootView.findViewById(R.id.stw_private);
        stw_for_sale = rootView.findViewById(R.id.stw_for_sale);
        stw_flat = rootView.findViewById(R.id.stw_flat);
        stw_wanted = rootView.findViewById(R.id.stw_wanted);
        stw_forclosure_sale = rootView.findViewById(R.id.stw_forclosure_sale);
        stw_short_sale = rootView.findViewById(R.id.stw_short_sale);
        stw_for_rent=rootView.findViewById(R.id.stw_for_rent);
        stw_for_rent.setVisibility(View.VISIBLE);
        stw_sell_by_ownwer = rootView.findViewById(R.id.stw_sell_by_ownwer);
        stw_home = rootView.findViewById(R.id.stw_home);
        stw_plot = rootView.findViewById(R.id.stw_plot);
        stw_commercial = rootView.findViewById(R.id.stw_commercial);
        stw_sell_by_ownwer = rootView.findViewById(R.id.stw_sell_by_ownwer);
        stw_Condominium = rootView.findViewById(R.id.stw_Condominium);
        stw_SingleFamily = rootView.findViewById(R.id.stw_SingleFamily);
        stw_Townhouse = rootView.findViewById(R.id.stw_Townhouse);
        stw_Semi_detached_House = rootView.findViewById(R.id.stw_Semi_detached_House);
        stw_Duplex_Triplex = rootView.findViewById(R.id.stw_Duplex_Triplex);
        edit_rent_amount = rootView.findViewById(R.id.edit_rent_amount);
        ctv_plot = rootView.findViewById(R.id.ctv_plot);
        spin_bedroom = rootView.findViewById(R.id.spin_bedroom);
        spin_bathroom = rootView.findViewById(R.id.spin_bathroom);
        btn_upload_next = rootView.findViewById(R.id.btn_upload_next);
        lay_bathroom = rootView.findViewById(R.id.layout_bathroom);
        lay_bedroom = rootView.findViewById(R.id.layout_bedroom);
        layout_rent = rootView.findViewById(R.id.layout_rent);
        edit_desc_pets = rootView.findViewById(R.id.edit_desc_pets);
        edit_desc_parking = rootView.findViewById(R.id.edit_desc_parking);
        edit_desc_smoking = rootView.findViewById(R.id.edit_desc_smoking);
        ctv_close.setVisibility(View.GONE);
        ctv_close.setOnClickListener(this);
        ctv_clear.setOnClickListener(this);
        ctv_clear.setVisibility(View.GONE);
        btn_upload_next.setOnClickListener(this);
        cet_location.setOnClickListener(this);

        stw_for_sale.setOnCheckedChangeListener(this);
        stw_for_rent.setOnCheckedChangeListener(this);
        stw_wanted.setOnCheckedChangeListener(this);
        stw_forclosure_sale.setOnCheckedChangeListener(this);
        stw_short_sale.setOnCheckedChangeListener(this);
        stw_short_sale.setOnCheckedChangeListener(this);
        stw_sell_by_ownwer.setOnCheckedChangeListener(this);

        stw_home.setOnCheckedChangeListener(this);
        stw_plot.setOnCheckedChangeListener(this);
        stw_flat.setOnCheckedChangeListener(this);
        stw_commercial.setOnCheckedChangeListener(this);

        stw_Condominium.setOnCheckedChangeListener(this);
        stw_SingleFamily.setOnCheckedChangeListener(this);
        stw_Townhouse.setOnCheckedChangeListener(this);
        stw_Semi_detached_House.setOnCheckedChangeListener(this);
        stw_Duplex_Triplex.setOnCheckedChangeListener(this);


        if (!SharedPref.getSharedPreferences(context, Constants.FIRST_NAME).isEmpty()) {
            cet_first_name.setText(SharedPref.getSharedPreferences(context, Constants.FIRST_NAME));
            //cet_first_name.setEnabled(false);
        }
        if (!SharedPref.getSharedPreferences(context, Constants.LAST_NAME).isEmpty()) {
            cet_last_name.setText(SharedPref.getSharedPreferences(context, Constants.LAST_NAME));
            //cet_last_name.setEnabled(false);
        }
        if (!SharedPref.getSharedPreferences(context, Constants.MOBILE_NUMBER).isEmpty()) {
            cet_contact.setText(Utility.formatNumbersAsCode(SharedPref.getSharedPreferences(context, Constants.MOBILE_NUMBER)));
            //cet_contact.setEnabled(false);
        }
        initialisePlacePicker();
    }

    private void initialisePlacePicker(){
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(), getResources().getString(R.string.mapkey));
        }
        placesClient = Places.createClient(getContext());
        initAutoCompleteTextView();
    }

    private void initAutoCompleteTextView() {
        cet_location.setThreshold(1);
        cet_location.setOnItemClickListener(autocompleteClickListener);
        autoCompleteAdapter = new AutoCompleteAdapter(getContext(), placesClient);
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
                            //place_location = String.valueOf(place.getName());
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
                                cet_society.setText(addresses.get(0).getAddressLine(1));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_close:
                break;

            case R.id.cet_location:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete
                        .IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(context);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;

            case R.id.ctv_clear:
                break;

            case R.id.btn_upload_next:
                //  startActivity(new Intent(context, SellPropertyNext.class));
                str_fname = cet_first_name.getText().toString();
                str_lname = cet_last_name.getText().toString();
                str_number = cet_contact.getText().toString();
                str_society = cet_society.getText().toString();
                str_City = cet_city.getText().toString();
                str_area = et_areasquare.getText().toString();
                if (!str_type.contains(getString(R.string.plot))) {
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
      /*          if (!post_type.equals("Rent")) {
                    int radioButtonID = radio_group_sell.getCheckedRadioButtonId();
                    View radioButton = radio_group_sell.findViewById(radioButtonID);
                    idx = radio_group_sell.indexOfChild(radioButton);
                    Log.e(TAG, "==========: " + "if");
                }
                Log.e(TAG, "==========: " + post_type);
                Log.e(TAG, "==========: " + idx);*/
                int radioButtonID = radio_group_sell.getCheckedRadioButtonId();
                View radioButton = radio_group_sell.findViewById(radioButtonID);
                idx = radio_group_sell.indexOfChild(radioButton);
                if (str_fname.isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_fname);
                } else if (str_lname.isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_lname);
                } else if (str_number.isEmpty()) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact);
                } else if (str_number.length() > 12 || str_number.length() < 10) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if ((idx == -1)) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_selltype));
                } else if (!stw_for_sale.isChecked() && !stw_for_rent.isChecked() && !stw_forclosure_sale.isChecked() && !stw_wanted.isChecked() && !stw_sell_by_ownwer.isChecked() && !stw_short_sale.isChecked()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_purpose));
                } else if (!stw_home.isChecked() && !stw_plot.isChecked() && !stw_flat.isChecked() && !stw_commercial.isChecked() && !stw_Condominium.isChecked() && !stw_SingleFamily.isChecked() && !stw_Townhouse.isChecked() && !stw_Semi_detached_House.isChecked() && !stw_Duplex_Triplex.isChecked()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_type));
                } else if (str_lat.isEmpty() && str_lng.isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pick_ocation));
                } else if (str_bedroom.equals("") && !str_type.contains(getString(R.string.plot))) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bedroom));
                } else if (str_bathroom.equals("") && !str_type.contains(getString(R.string.plot))) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bathroom));
                } else if (str_area.isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_area));
                } else {
                    //str_purpose = TextUtils.join(",", array_purpose);
                    // str_type = TextUtils.join(",", array_type);
                    //Log.e(TAG, "==========: " + str_purpose);
                   // Log.e(TAG, "==========: " + str_type);
                    str_rent_status = "1";
                    edit_rent_amount.setText("");
                    RadioButton r = (RadioButton) radio_group_sell.getChildAt(idx);
                    str_sell_type = r.getText().toString();
                   /* if (post_type.equals("Rent")) {
                        str_rent_status = "2";
                    } else {
                        str_rent_status = "1";
                        edit_rent_amount.setText("");
                        RadioButton r = (RadioButton) radio_group_sell.getChildAt(idx);
                        str_sell_type = r.getText().toString();
                    }*/
                    startActivity(new Intent(context, SellPropertyNext.class).putExtra(Constants.FIRST_NAME, str_fname).
                            putExtra(Constants.LAST_NAME, str_lname).
                            putExtra(Constants.MOBILE, str_number).putExtra(Constants.SELL_TYPE, str_sell_type).
                            putExtra(Constants.PURPOSE, str_purpose).putExtra(Constants.TYPE, str_type).
                            putExtra(Constants.LOCATION, cet_location.getText().toString()).putExtra(Constants.BADROOM, str_bedroom).
                            putExtra(Constants.BATHROOM, str_bathroom).putExtra(Constants.LOCATION_LATITUDE, str_lat).
                            putExtra(Constants.LOCATION_LONGITUDE, str_lng).putExtra(Constants.SOCIETY, str_society).putExtra(Constants.PROPERTY_FOR, str_rent_status).
                            putExtra(Constants.CITY, str_City).putExtra(Constants.AREA, str_area).
                            putExtra(Constants.RENT_AMOUNT, "").putExtra(Constants.PETS, "").
                            putExtra(Constants.PETS_DETAIL, "").putExtra(Constants.SMOKING, "").
                            putExtra(Constants.SMOKING_DETAIL, "").
                            putExtra(Constants.PARKING, "").putExtra(Constants.PARKING_DETAIL, "").
                            putExtra(Constants.RENTER_TYPE, ""));
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.stw_for_sale:
                if (isChecked) {
                    str_purpose = getString(R.string.for_sale);
                    stw_wanted.setChecked(false);
                    stw_forclosure_sale.setChecked(false);
                    stw_short_sale.setChecked(false);
                    stw_sell_by_ownwer.setChecked(false);
                    stw_for_rent.setChecked(false);
                    card_rent.setVisibility(View.GONE);

                }
                break;
            case R.id.stw_for_rent:
                if (isChecked) {
                    MainActivity.bottomNavigationView.setSelectedItemId(R.id.navigation_rent);
                    getFragmentManager().beginTransaction().replace(R.id.container, new RentPropertyFragement()).commit();
                   // str_purpose = getString(R.string.for_rent);
                    stw_for_sale.setChecked(false);
                    stw_wanted.setChecked(false);
                    stw_forclosure_sale.setChecked(false);
                    stw_short_sale.setChecked(false);
                    stw_sell_by_ownwer.setChecked(false);

                }

                break;
            case R.id.stw_wanted:
                if (isChecked) {
                    str_purpose = getString(R.string.wanted);
                    stw_for_sale.setChecked(false);
                    stw_forclosure_sale.setChecked(false);
                    stw_short_sale.setChecked(false);
                    stw_sell_by_ownwer.setChecked(false);
                    stw_for_rent.setChecked(false);
                    card_rent.setVisibility(View.GONE);

                }
                break;
            case R.id.stw_forclosure_sale:
                if (isChecked) {
                    str_purpose = getString(R.string.foreclosure_sale);
                    stw_for_sale.setChecked(false);
                    stw_wanted.setChecked(false);
                    stw_short_sale.setChecked(false);
                    stw_sell_by_ownwer.setChecked(false);
                    stw_for_rent.setChecked(false);
                    card_rent.setVisibility(View.GONE);

                }
                break;
            case R.id.stw_short_sale:
                if (isChecked) {
                    str_purpose = getString(R.string.short_sale);
                    stw_for_sale.setChecked(false);
                    stw_wanted.setChecked(false);
                    stw_forclosure_sale.setChecked(false);
                    stw_sell_by_ownwer.setChecked(false);
                    stw_for_rent.setChecked(false);
                    card_rent.setVisibility(View.GONE);

                }
                break;


            case R.id.stw_sell_by_ownwer:
                if (isChecked) {
                    str_purpose = getString(R.string.for_sell_by_owner);
                    stw_for_sale.setChecked(false);
                    stw_wanted.setChecked(false);
                    stw_forclosure_sale.setChecked(false);
                    stw_short_sale.setChecked(false);
                    stw_for_rent.setChecked(false);
                    card_rent.setVisibility(View.GONE);
                }
                break;


            case R.id.stw_home:
                if (isChecked) {
                    str_type = getString(R.string.home);
                    stw_flat.setChecked(false);
                    stw_plot.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);


                }
                break;
            case R.id.stw_plot:
                if (isChecked) {
                    str_bathroom = "";
                    str_bedroom = "";
                    str_type = getString(R.string.plot);
                    stw_flat.setChecked(false);
                    stw_home.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.GONE);
                    lay_bedroom.setVisibility(View.GONE);
                }
                break;
            case R.id.stw_flat:
                if (isChecked) {
                    str_type = getString(R.string.flat);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.stw_commercial:
                if (isChecked) {
                    str_type = getString(R.string.commercial);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.stw_Condominium:
                if (isChecked) {
                    str_type = getString(R.string.condominium);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;

            case R.id.stw_SingleFamily:
                // Log.e(TAG, "=========: "+"isclick" );
                if (isChecked) {
                    str_type = getString(R.string.single_family);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.stw_Townhouse:
                if (isChecked) {
                    str_type = getString(R.string.town_house);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;


            case R.id.stw_Semi_detached_House:
                if (isChecked) {
                    str_type = getString(R.string.semi_detached_house);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;

            case R.id.stw_Duplex_Triplex:
                if (isChecked) {
                    str_type = getString(R.string.duplex);
                    stw_plot.setChecked(false);
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                }
                break;


        }
    }

/*    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.stw_for_sale:
                if (isChecked) {
                    array_purpose.add(getString(R.string.for_sale));
                } else {
                    array_purpose.remove(getString(R.string.for_sale));
                }
                break;
            case R.id.stw_wanted:
                if (isChecked) {
                    array_purpose.add(getString(R.string.wanted));
                } else {
                    array_purpose.remove(getString(R.string.wanted));
                }
                break;
            case R.id.stw_forclosure_sale:
                if (isChecked) {
                    array_purpose.add(getString(R.string.foreclosure_sale));

                } else {
                    array_purpose.remove(getString(R.string.foreclosure_sale));
                }
                break;
            case R.id.stw_short_sale:
                if (isChecked) {
                    array_purpose.add(getString(R.string.short_sale));
                } else {
                    array_purpose.remove(getString(R.string.short_sale));
                }
                break;


            case R.id.stw_sell_by_ownwer:
                if (isChecked) {
                    array_purpose.add(getString(R.string.for_sell_by_owner));
                } else {
                    array_purpose.remove(getString(R.string.for_sell_by_owner));
                }
                break;

            case R.id.stw_home:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.home));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.home));
                }
                break;
            case R.id.stw_plot:
                if (isChecked) {
                    array_type.clear();
                    array_type.add(getString(R.string.plot));
                    stw_home.setChecked(false);
                    stw_flat.setChecked(false);
                    stw_commercial.setChecked(false);
                    stw_Condominium.setChecked(false);
                    stw_SingleFamily.setChecked(false);
                    stw_Townhouse.setChecked(false);
                    stw_Semi_detached_House.setChecked(false);
                    stw_Duplex_Triplex.setChecked(false);
                    str_bathroom = "";
                    str_bedroom = "";
                    lay_bathroom.setVisibility(View.GONE);
                    lay_bedroom.setVisibility(View.GONE);
                } else {
                    array_type.remove(getString(R.string.plot));
                }
                break;
            case R.id.stw_flat:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.flat));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.flat));
                }
                break;
            case R.id.stw_commercial:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.commercial));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.commercial));
                }
                break;
            case R.id.stw_Condominium:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.condominium));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.condominium));
                }
                break;

            case R.id.stw_SingleFamily:
                // Log.e(TAG, "=========: "+"isclick" );
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.single_family));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.single_family));
                }
                break;
            case R.id.stw_Townhouse:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.town_house));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.town_house));
                }
                break;


            case R.id.stw_Semi_detached_House:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.semi_detached_house));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.semi_detached_house));
                }
                break;

            case R.id.stw_Duplex_Triplex:
                if (isChecked) {
                    if (array_type.contains(getString(R.string.plot))) {
                        stw_plot.setChecked(false);
                        array_type.remove(getString(R.string.plot));
                    }
                    array_type.add(getString(R.string.duplex));
                    lay_bathroom.setVisibility(View.VISIBLE);
                    lay_bedroom.setVisibility(View.VISIBLE);

                } else {
                    array_type.remove(getString(R.string.duplex));
                }
                break;


        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // status = 1;
                Place place = Autocomplete.getPlaceFromIntent(data);
                cet_location.setText(place.getAddress());
                //place_location = String.valueOf(place.getName());
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
//                    cet_city.setText(addresses.get(0).getAddressLine(0));;
//                    cet_society.setText(addresses.get(0).getAddressLine(1)); ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Log.i("====", "latlong: " + place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // TODO: Handle the error.
                //Log.i("====", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}