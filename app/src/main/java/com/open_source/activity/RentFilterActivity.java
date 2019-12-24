package com.open_source.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.open_source.R;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.TinyDB;
import com.open_source.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RentFilterActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = RentFilterActivity.class.getSimpleName();
    TinyDB tinydb;
    RadioGroup radio_group_rent;
    BottomSheetBehavior mBottomSheetBehavior2;
    private Context context;
    private TextView toolbar_title;
    private Toolbar h_toolbar;
    private SwitchCompat stw_for_sale, stw_wanted, stw_forclosure_sale, stw_short_sale,
            stw_home, stw_plot, stw_commercial, stw_flat, stw_sell_by_ownwer, stw_Condominium,
            stw_SingleFamily, stw_Townhouse, stw_Semi_detached_House, stw_Duplex_Triplex;
    private LinearLayout lay_bathroom, lay_bedroom;
    private Spinner spin_bedroom, spin_bathroom;
    private RelativeLayout btn_filter;
    private TextView ctv_close, ctv_clear,txt_radius;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int idx;
    private EditText ed_min_budget, ed_max_budget;
    private ProgressHUD progressHUD;
    private ArrayList<String> array_type = new ArrayList<>();
    private String str_purpose = "", str_type = "", str_bedroom = "",
            str_bathroom = "", str_renter_type = "",str_radius="";
    Double lat=0.0,longi=0.0;

    private AutoCompleteTextView cet_location;
    private AutoCompleteAdapter autoCompleteAdapter;
    private PlacesClient placesClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rent_filter);
        context = this;
        tinydb = new TinyDB(context);
        init();

    }

    public void init() {
        h_toolbar = (Toolbar) findViewById(R.id.h_toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.filter);
        toolbar_title.setCompoundDrawables(getResources().getDrawable(R.drawable.serach_black), null, null, null);
        ctv_close = findViewById(R.id.ctv_close);
        ctv_clear = findViewById(R.id.ctv_clear);
        stw_home = findViewById(R.id.stw_home);
        stw_plot = findViewById(R.id.stw_plot);
        stw_flat = findViewById(R.id.stw_flat);
        stw_commercial = findViewById(R.id.stw_commercial);
        stw_sell_by_ownwer = findViewById(R.id.stw_sell_by_ownwer);
        stw_Condominium = findViewById(R.id.stw_Condominium);
        stw_SingleFamily = findViewById(R.id.stw_SingleFamily);
        stw_Townhouse = findViewById(R.id.stw_Townhouse);
        stw_Semi_detached_House = findViewById(R.id.stw_Semi_detached_House);
        stw_Duplex_Triplex = findViewById(R.id.stw_Duplex_Triplex);
        spin_bedroom = findViewById(R.id.spin_bedroom);
        spin_bathroom = findViewById(R.id.spin_bathroom);
        btn_filter = findViewById(R.id.btn_filter);
        cet_location = findViewById(R.id.cet_location);
        lay_bathroom = findViewById(R.id.layout_bathroom);
        lay_bedroom = findViewById(R.id.layout_bedroom);
        txt_radius=findViewById(R.id.txt_radius);
        btn_filter.setOnClickListener(this);

        stw_home.setOnCheckedChangeListener(this);
        stw_plot.setOnCheckedChangeListener(this);
        stw_flat.setOnCheckedChangeListener(this);
        stw_commercial.setOnCheckedChangeListener(this);
        stw_Condominium.setOnCheckedChangeListener(this);
        stw_SingleFamily.setOnCheckedChangeListener(this);
        stw_Townhouse.setOnCheckedChangeListener(this);
        stw_Semi_detached_House.setOnCheckedChangeListener(this);
        stw_Duplex_Triplex.setOnCheckedChangeListener(this);
        cet_location.setText(getIntent().getExtras().getString(Constants.LOCATION));
         lat = Double.valueOf(getIntent().getExtras().getString(Constants.LATITUDE, ""));
        longi =Double.valueOf( getIntent().getExtras().getString(Constants.LONGITUDE, ""));
        cet_location.setOnClickListener(this);
        ctv_close.setOnClickListener(this);
        ctv_clear.setOnClickListener(this);

        ed_min_budget = findViewById(R.id.ed_min_budget);
        ed_max_budget = findViewById(R.id.ed_max_budget);
        ed_min_budget.setText(tinydb.getString(Constants.MIN_BUDGET));
        ed_max_budget.setText(tinydb.getString(Constants.MAX_BUDGET));
        radio_group_rent = findViewById(R.id.rent_selection);

        array_type = tinydb.getListString(Constants.RENT_TYPE_FILTER);
        str_renter_type = tinydb.getString(Constants.RENTER_TYPE);

        if (!tinydb.getString(Constants.RENT_RADIUS).isEmpty())
            txt_radius.setText(tinydb.getString(Constants.RENT_RADIUS));
       /* Log.e(TAG, "========: " + array_type);
        Log.e(TAG, "========: " + array_purpose);
        Log.e(TAG, "========: " + str_post);*/

        if (str_renter_type.equalsIgnoreCase(getString(R.string.vacation_renter))) {
            ((RadioButton) radio_group_rent.getChildAt(0)).setChecked(true);
        } else if (str_renter_type.equalsIgnoreCase(getString(R.string.long_term_renter))) {
            ((RadioButton) radio_group_rent.getChildAt(1)).setChecked(true);
        }
       /* Log.e(TAG, "========: " + array_type);
        Log.e(TAG, "========: " + array_purpose);
        Log.e(TAG, "========: " + str_post);*/

        if (array_type.contains(getString(R.string.home))) {
            stw_home.setChecked(true);
        }
        if (array_type.contains(getString(R.string.plot))) {
            stw_plot.setChecked(true);

        }
        if (array_type.contains(getString(R.string.flat))) {
            stw_flat.setChecked(true);

        }
        if (array_type.contains(getString(R.string.commercial))) {
            stw_commercial.setChecked(true);

        }
        if (array_type.contains(getString(R.string.condominium))) {
            stw_Condominium.setChecked(true);

        }
        if (array_type.contains(getString(R.string.single_family))) {
            stw_SingleFamily.setChecked(true);

        }
        if (array_type.contains(getString(R.string.town_house))) {
            stw_Townhouse.setChecked(true);

        }
        if (array_type.contains(getString(R.string.semi_detached_house))) {
            stw_Semi_detached_House.setChecked(true);

        }
        if (array_type.contains(getString(R.string.duplex))) {
            stw_Duplex_Triplex.setChecked(true);
        }
        radio_group_rent.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = radio_group_rent.getCheckedRadioButtonId();
                View radioButton = radio_group_rent.findViewById(radioButtonID);
                idx = radio_group_rent.indexOfChild(radioButton);
                switch (idx) {
                    case 0:
                        str_renter_type = getString(R.string.vacation_renter);
                        Fun_save("rent");
                        break;
                    case 1:
                        str_renter_type = getString(R.string.long_term_renter);
                        Fun_save("rent");
                        break;

                }
            }
        });

        ((CardView) findViewById(R.id.card_radius)).setOnClickListener(this);
        ((CardView) findViewById(R.id.cancle)).setOnClickListener(this);
        final View bottomSheet2 = findViewById(R.id.bottom_sheet2);
        LinearLayout lay_radius = findViewById(R.id.lay_radius);
        ((ImageView) findViewById(R.id.location_reset)).setOnClickListener(this);
        String[] array_radius = context.getResources().getStringArray(R.array.array_redius);
        for (int i = 0; i < array_radius.length; i++) {
            View view = getLayoutInflater().inflate(R.layout.test, lay_radius, false);
            final TextView txt = view.findViewById(R.id.txt);
            txt.setText(array_radius[i]);
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_radius.setText(txt.getText().toString());
                    if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                    } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            });
            lay_radius.addView(view);
        }
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
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
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Place place = task.getPlace();
                            cet_location.setText(place.getAddress());
                            //  place_location = String.valueOf(place.getName());
                            String latLng = String.valueOf(place.getLatLng());
                            latLng = latLng.replace("lat/lng: ", "");
                            latLng = latLng.replace("(", "");
                            latLng = latLng.replace(")", "");
                            String[] array_latLng = latLng.split(",");
                            lat = Double.valueOf(array_latLng[0]);
                            longi = Double.valueOf(array_latLng[1]);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_close:
                finish();
                break;
            case R.id.location_reset:
                lat = Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LATITUDE));
                longi = Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LONGITUDE));
                cet_location.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
                break;
            case R.id.cancle:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.card_radius:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.ctv_clear:
                //tinydb.clear();
                txt_radius.setText("");
                cet_location.setText("");
                lat =0.0;
                longi =0.0;
                radio_group_rent.clearCheck();
                spin_bathroom.setSelection(0);
                spin_bedroom.setSelection(0);
                str_bathroom = "";
                str_bedroom = "";
                stw_home.setChecked(false);
                stw_plot.setChecked(false);
                stw_flat.setChecked(false);
                stw_commercial.setChecked(false);
                stw_Condominium.setChecked(false);
                stw_SingleFamily.setChecked(false);
                stw_Townhouse.setChecked(false);
                stw_Semi_detached_House.setChecked(false);
                stw_Duplex_Triplex.setChecked(false);
                str_type = "";
                array_type.clear();
                ed_min_budget.setText("");
                ed_max_budget.setText("");
                tinydb.putString(Constants.RENT_RADIUS,"");
                break;

            case R.id.btn_filter:
                str_purpose = "";
                str_type = "";
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
                if (array_type.size() > 0) {
                    str_type = TextUtils.join(",", array_type);
                }
                if (cet_location.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pick_ocation));
                } else {
                    str_radius=txt_radius.getText().toString();
                    if (txt_radius.getText().toString().equals(getString(R.string.select_radius)))
                    {
                        str_radius="";
                    }
                    int radioButtonID = radio_group_rent.getCheckedRadioButtonId();
                    View radioButton = radio_group_rent.findViewById(radioButtonID);
                    int idx_rent = radio_group_rent.indexOfChild(radioButton);
                    if (idx_rent != -1) {
                        RadioButton r = (RadioButton) radio_group_rent.getChildAt(idx_rent);
                        str_renter_type = r.getText().toString();
                    }
                    tinydb.putString(Constants.RENT_RADIUS,str_radius);
                    tinydb.putString(Constants.MIN_BUDGET, ed_min_budget.getText().toString());
                    tinydb.putString(Constants.MAX_BUDGET, ed_max_budget.getText().toString());
                    startActivity(new Intent(context, ListingActivity.class).
                            putExtra(Constants.MIN_BUDGET, ed_min_budget.getText().toString())
                            .putExtra(Constants.TYPE, str_type).putExtra(Constants.LOCATION_LATITUDE, lat+"")
                            .putExtra(Constants.LOCATION_LONGITUDE, longi+"").putExtra(Constants.BADROOM, str_bedroom)
                            .putExtra(Constants.BATHROOM, str_bathroom).putExtra(Constants.MAX_BUDGET, ed_max_budget.getText().toString()).
                             putExtra(Constants.RENTER_TYPE, str_renter_type).putExtra(Constants.PROPERTY_FOR, "2").
                                    putExtra(Constants.RADIUS, str_radius));
                }
                break;

            case R.id.cet_location:
                /*if (Utility.checkPlayServices(context)) {
                    try {
                        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(RentFilterActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.stw_home:
                if (isChecked) {
                    //str_type = getString(R.string.home);
                    if (!array_type.contains(getString(R.string.home)))
                        array_type.add(getString(R.string.home));
                } else {
                    array_type.remove(getString(R.string.home));
                }
                Fun_save("type");
                break;
            case R.id.stw_plot:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.plot)))
                        array_type.add(getString(R.string.plot));
                    str_bathroom = "";
                    str_bedroom = "";
                    //str_type = getString(R.string.plot);
                } else {
                    array_type.remove(getString(R.string.plot));
                }
                Fun_save("type");
                break;
            case R.id.stw_flat:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.flat)))
                        array_type.add(getString(R.string.flat));
                    // str_type = getString(R.string.flat);
                } else {
                    array_type.remove(getString(R.string.flat));
                }
                Fun_save("type");
                break;
            case R.id.stw_commercial:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.commercial)))
                        array_type.add(getString(R.string.commercial));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.commercial));
                }
                Fun_save("type");
                break;


            case R.id.stw_Condominium:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.condominium)))
                        array_type.add(getString(R.string.condominium));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.condominium));
                }
                Fun_save("type");
                break;


            case R.id.stw_SingleFamily:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.single_family)))
                        array_type.add(getString(R.string.single_family));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.single_family));
                }
                Fun_save("type");
                break;

            case R.id.stw_Townhouse:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.town_house)))
                        array_type.add(getString(R.string.town_house));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.town_house));
                }
                Fun_save("type");
                break;
            case R.id.stw_Semi_detached_House:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.semi_detached_house)))
                        array_type.add(getString(R.string.semi_detached_house));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.semi_detached_house));
                }
                Fun_save("type");
                break;
            case R.id.stw_Duplex_Triplex:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.duplex)))
                        array_type.add(getString(R.string.duplex));
                    //str_type = getString(R.string.commercial);
                } else {
                    array_type.remove(getString(R.string.duplex));
                }
                Fun_save("type");
                break;

        }

    }

    private void Fun_save(String type) {
        if (type.equals("type"))
            tinydb.putListString(Constants.RENT_TYPE_FILTER, array_type);
        else if (type.equals("rent")) {
            tinydb.putString(Constants.RENTER_TYPE, str_renter_type);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
           /* if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(context, data);
                cet_location.setText(place.getAddress());
                //  place_location = String.valueOf(place.getName());
                String latLng = String.valueOf(place.getLatLng());
                latLng = latLng.replace("lat/lng: ", "");
                latLng = latLng.replace("(", "");
                latLng = latLng.replace(")", "");
                String[] array_latLng = latLng.split(",");
                lat = Double.valueOf(array_latLng[0]);
                longi = Double.valueOf(array_latLng[1]);
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


}
