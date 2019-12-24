package com.open_source.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.open_source.R;
import com.open_source.activity.RentFormSecond;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

public class RentFormFirst extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = RentFormFirst.class.getSimpleName();
    View rootView;
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    int id, idx;
    View radioButton;
    String MobilePattern = "[0-9]{10}";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    RadioButton rb;
    TextView apt_dob, ed_curr_lease, txt_rent_location, desire_pro_type, max_budget;
    LinearLayout layout_lease, layout_pick_location;
    String str_bedroom = "", str_type = "", str_evict = "", str_vhichel = "",
            str_crime = "", str_bankrupty = "", str_occupants = "",
            str_pets_allow = "", str_desire_type = "", str_lat = "", str_lng = "";
    MaterialSpinner spn_type, spn_bedroom;
    EditText sqarefeed, rent_amount, stree_address, city, state, zip, apt_name,
            apt_licence, apt_phone, ed_occupant, ed_pets, ed_crime, ed_vechiles, ed_bankroprty, ed_evicted,
            curr_company, pre_company, curr_occupation, pre_occupation, curr_how_long, pre_how_long, curr_income,
            pre_income, curr_address, pre_address, curr_city, pre_city, curr_state, pre_state, curr_zip, pr_zip,
            ed_desire_moving, ed_how_long_address, curr_land_name, curr_land_address, curr_land_email, curr_land_phone;
    RadioGroup radio_pets_allow, radio_vchiles,
            radio_crime, radio_occupants, radio_bankruptcy, bank_evicted;
    TextInputLayout layout_pets, layput_occupant, layout_vechile, layout_crime, layout_bankrupty, layout_evicted;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rent_first);
        context = RentFormFirst.this;
        init();
    }

  /*  @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_rent_first, container, false);
        context = getActivity();
        init();
        return rootView;
    }*/


    private void init() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.rental_application);
        spn_type = findViewById(R.id.spn_property_type);
        spn_bedroom = findViewById(R.id.spn_bedroom);
        desire_pro_type = findViewById(R.id.desire_pro_type);
        sqarefeed = findViewById(R.id.sqare_feed);
        rent_amount = findViewById(R.id.rent_amount);
        stree_address = findViewById(R.id.street_address);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip);
        apt_name = findViewById(R.id.applicant_name);
        apt_dob = findViewById(R.id.applicant_Dob);
        apt_licence = findViewById(R.id.applicant_driver_licence);
        apt_phone = findViewById(R.id.applicant_phone);
        apt_phone.addTextChangedListener(new PhoneNumberTextWatcher(apt_phone));
        ed_occupant = findViewById(R.id.ed_occupant);
        ed_pets = findViewById(R.id.ed_pets);
        ed_crime = findViewById(R.id.ed_crime);
        ed_vechiles = findViewById(R.id.ed_vechiles);
        ed_bankroprty = findViewById(R.id.ed_bankroprty);
        ed_evicted = findViewById(R.id.ed_evicted);
        radio_pets_allow = findViewById(R.id.radio_pets_allow);
        radio_occupants = findViewById(R.id.radio_occpants);
        radio_vchiles = findViewById(R.id.radio_vechiles);
        radio_crime = findViewById(R.id.radio_crime);
        radio_bankruptcy = findViewById(R.id.radio_bankrupty);
        bank_evicted = findViewById(R.id.radio_evicted);
        layout_pets = findViewById(R.id.layout_pets);
        layput_occupant = findViewById(R.id.layput_occupant);
        layout_vechile = findViewById(R.id.layout_vechile);
        layout_crime = findViewById(R.id.layout_crime);
        layout_bankrupty = findViewById(R.id.layout_bankrupty);
        layout_evicted = findViewById(R.id.layout_evicted);
        max_budget = findViewById(R.id.max_budget);
        curr_company = findViewById(R.id.curr_company);
        curr_occupation = findViewById(R.id.curr_occpation);
        curr_how_long = findViewById(R.id.curr_how_long);
        curr_income = findViewById(R.id.curr_income);
        curr_address = findViewById(R.id.curr_address);
        curr_state = findViewById(R.id.curr_state);
        curr_city = findViewById(R.id.curr_city);
        curr_zip = findViewById(R.id.curr_zip);

        pre_company = findViewById(R.id.pre_company);
        pre_occupation = findViewById(R.id.curr_occpation);
        pre_how_long = findViewById(R.id.pre_how_long);
        pre_income = findViewById(R.id.pre_income);
        pre_address = findViewById(R.id.pre_address);
        pre_state = findViewById(R.id.pre_state);
        pre_city = findViewById(R.id.pre_city);
        pr_zip = findViewById(R.id.pre_zip);

        ed_desire_moving = findViewById(R.id.ed_desire_moving);
        ed_how_long_address = findViewById(R.id.curr_how_long_add);
        ed_curr_lease = findViewById(R.id.curr_lease_Date);
        curr_land_address = findViewById(R.id.curr_land_address);
        curr_land_name = findViewById(R.id.ed_curr_land_name);
        curr_land_email = findViewById(R.id.curr_land_email);
        curr_land_phone = findViewById(R.id.curr_land_phone);
        curr_land_phone.addTextChangedListener(new PhoneNumberTextWatcher(curr_land_phone));

        txt_rent_location = findViewById(R.id.txt_rent_location);
        radio_pets_allow.setOnCheckedChangeListener(this);
        radio_occupants.setOnCheckedChangeListener(this);
        radio_vchiles.setOnCheckedChangeListener(this);
        radio_crime.setOnCheckedChangeListener(this);
        radio_bankruptcy.setOnCheckedChangeListener(this);
        bank_evicted.setOnCheckedChangeListener(this);
        apt_dob.setOnClickListener(this);
        layout_lease = findViewById(R.id.layout_lease);
        layout_lease.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_next)).setOnClickListener(this);


        desire_pro_type.setText(getIntent().getExtras().getString(Constants.TYPE));
        max_budget.setText(getIntent().getExtras().getString(Constants.RENT_AMOUNT));
        txt_rent_location.setText(getIntent().getExtras().getString(Constants.ADDRESS));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.applicant_Dob:
                PickDate("dob");
                break;
            case R.id.layout_lease:
                PickDate("lease");
                break;
            case R.id.btn_next:
                if (spn_type.getSelectedItem() != null) {
                    if (spn_type.getSelectedItem().toString().equalsIgnoreCase("Type")) {
                        str_type = "";
                    } else {
                        str_type = spn_type.getSelectedItem().toString();
                    }
                }
                if (spn_bedroom.getSelectedItem() != null) {
                    if (spn_bedroom.getSelectedItem().toString().equalsIgnoreCase("Select No. of bathrooms")) {
                        str_bedroom = "";
                    } else {
                        str_bedroom = spn_bedroom.getSelectedItem().toString();
                    }
                }


               if (apt_name.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_name));
                else if (apt_dob.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.msg_dob));
                else if (apt_phone.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_your_contact_no));
                else if (apt_phone.getText().toString().length() < 10) {
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                } else if (str_occupants.equalsIgnoreCase("yes") && ed_occupant.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_occupant_desc));
                else if (str_pets_allow.equalsIgnoreCase("yes") && ed_pets.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_pets_desc));
                else if (str_vhichel.equalsIgnoreCase("yes") && ed_vechiles.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_vehicles_desc));
                else if (str_crime.equalsIgnoreCase("yes") && ed_crime.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_crime_desc));
                else if (str_bankrupty.equalsIgnoreCase("yes") && ed_bankroprty.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_enter_bankuptcy));
                else if (str_evict.equalsIgnoreCase("yes") && ed_evicted.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_evict_desc));
                else if (!curr_zip.getText().toString().isEmpty() && curr_zip.getText().toString().length() < 6)
                    Utility.ShowToastMessage(context, getString(R.string.enter_zip_code));
                else if (!pr_zip.getText().toString().isEmpty() && pr_zip.getText().toString().length() < 6)
                    Utility.ShowToastMessage(context, getString(R.string.enter_zip_code));
                else if (str_type.equalsIgnoreCase(""))
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_type));
                else if (str_bedroom.equalsIgnoreCase(""))
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bedroom));
                else if (sqarefeed.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_area));
                else if (stree_address.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_current_residence));
                else if (city.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_city));
                else if (state.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_state));
                else if (zip.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.enter_zip_code));
                else if (zip.getText().toString().length() < 6)
                    Utility.ShowToastMessage(context, getString(R.string.zip_code_validation));
                else if (ed_how_long_address.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.living_time));
                else if (ed_desire_moving.getText().toString().trim().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.error_curr_residance));
                else if (curr_land_phone.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.val_landload_contact);
                else if(curr_land_phone.getText().toString().length()<12)
                   Utility.ShowToastMessage(context, R.string.val_landload_contact);
                else if (!curr_land_email.getText().toString().isEmpty() && !Utility.isValidEmail(curr_land_email.getText().toString()))
                    Utility.ShowToastMessage(context, R.string.val_landloard_email);
                else
                    GOINTENT();

                break;

        }
    }

    private void GOINTENT() {
        startActivity(new Intent(context, RentFormSecond.class).
                putExtra(Constants.PROPERTY_TYPE, getIntent().getExtras().getString(Constants.TYPE)).
                putExtra(Constants.MAX_BUDGET, getIntent().getExtras().getString(Constants.RENT_AMOUNT)).
                putExtra(Constants.PREF_LOCATION, getIntent().getExtras().getString(Constants.ADDRESS)).
                putExtra(Constants.PREF_LAT, str_lat).
                putExtra(Constants.PREF_LONGI, str_lng).

                putExtra(Constants.APP_FULL_NAME, apt_name.getText().toString()).
                putExtra(Constants.APP_PHONE, apt_phone.getText().toString()).
                putExtra(Constants.APP_DOB, apt_dob.getText().toString()).
                putExtra(Constants.APP_DRIVER_LICENCE, apt_licence.getText().toString()).
                putExtra(Constants.APP_OCCUPATION, str_occupants).
                putExtra(Constants.APP_OCCUPATION_DETAIL, ed_occupant.getText().toString())
                .putExtra(Constants.APP_PETS, str_pets_allow).
                        putExtra(Constants.APP_PETS_DETAIL, ed_pets.getText().toString()).
                        putExtra(Constants.APP_VEHICLES, str_vhichel).
                        putExtra(Constants.APP_VEHICLES_DETAIL, ed_vechiles.getText().toString()).
                        putExtra(Constants.APP_CRIME, str_crime).
                        putExtra(Constants.APP_CRIME_DETAIL, ed_crime.getText().toString()).
                        putExtra(Constants.APP_BANKRUPTCY, str_bankrupty).
                        putExtra(Constants.APP_BANKRUPTCY_DETAIL, ed_bankroprty.getText().toString()).
                        putExtra(Constants.APP_EVICTED, str_evict).
                        putExtra(Constants.APP_EVICTED_DETAIL, ed_evicted.getText().toString()).


                        putExtra(Constants.CURR_EMP_COMP, curr_company.getText().toString()).
                        putExtra(Constants.CURR_EMP_OCCUP, curr_occupation.getText().toString()).
                        putExtra(Constants.CURR_EMP_HOW_LONG, curr_how_long.getText().toString()).
                        putExtra(Constants.CURR_EMP_INCOME, curr_income.getText().toString()).
                        putExtra(Constants.CURR_EMP_ADDRESS, curr_address.getText().toString()).
                        putExtra(Constants.CURR_EMP_CITY, curr_city.getText().toString()).
                        putExtra(Constants.CURR_EMP_STATE, curr_state.getText().toString()).
                        putExtra(Constants.CURR_EMP_ZIP, curr_zip.getText().toString()).


                        putExtra(Constants.PREV_EMP_COMP, pre_company.getText().toString()).
                        putExtra(Constants.PREV_EMP_OCCUP, pre_occupation.getText().toString()).
                        putExtra(Constants.PREV_EMP_HOW_LONG, pre_how_long.getText().toString()).
                        putExtra(Constants.PREV_EMP_INCOME, pre_income.getText().toString()).
                        putExtra(Constants.PREV_EMP_ADDRESS, pre_address.getText().toString()).
                        putExtra(Constants.PREV_EMP_CITY, pre_city.getText().toString()).
                        putExtra(Constants.PREV_EMP_STATE, pre_state.getText().toString()).
                        putExtra(Constants.PREV_EMP_ZIP, pr_zip.getText().toString()).


                        putExtra(Constants.CURR_LAND_NAME, curr_land_name.getText().toString()).
                        putExtra(Constants.CURR_LAND_ADDRESS, curr_land_address.getText().toString()).
                        putExtra(Constants.CURR_LAND_EMAIL, curr_land_email.getText().toString()).
                        putExtra(Constants.CURR_LAND_PHONE, curr_land_phone.getText().toString()).

                        putExtra(Constants.CURR_RES_TYPE, str_type).
                        putExtra(Constants.CURR_RES_AREA, sqarefeed.getText().toString()).
                        putExtra(Constants.CURR_RES_BEDROOM, str_bedroom).
                        putExtra(Constants.CURR_RES_RENT, rent_amount.getText().toString()).
                        putExtra(Constants.CURR_RES_ADDRESS, stree_address.getText().toString()).
                        putExtra(Constants.CURR_RES_CITY, city.getText().toString()).
                        putExtra(Constants.CURR_RES_STATE, state.getText().toString()).
                        putExtra(Constants.CURR_RES_ZIP, zip.getText().toString()).
                        putExtra(Constants.CURR_RES_EXP_DATE, ed_curr_lease.getText().toString()).
                        putExtra(Constants.CURR_RES_RENT, rent_amount.getText().toString()).
                        putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID, "")).
                        putExtra(Constants.USER_ID, getIntent().getExtras().getString(Constants.USER_ID, "")).
                        putExtra(Constants.CURR_RES_MOV_DESIRE, ed_desire_moving.getText().toString()));


    }

    private void PickDate(final String type) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = "";
                        formatted_date = fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        if (type.equalsIgnoreCase("dob"))
                            apt_dob.setText(formatted_date);
                        else if (type.equalsIgnoreCase("lease"))
                            ed_curr_lease.setText(formatted_date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String fun_dateFormat(String date) {
        String formatted_date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = dateFormat.parse(date);
            formatted_date = dateFormat.format(d);
            if (formatted_date.isEmpty()) {
                formatted_date = date;
            }
            //System.out.println("=====date"+d);
            //System.out.println("======Formated"+);
        } catch (Exception e) {
            formatted_date = date;
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep" + e);
        }
        return formatted_date;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.radio_pets_allow:
                id = radio_pets_allow.getCheckedRadioButtonId();
                radioButton = radio_pets_allow.findViewById(id);
                idx = radio_pets_allow.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_pets_allow = rb.getText().toString();
                if (str_pets_allow.equalsIgnoreCase("yes"))
                    layout_pets.setVisibility(View.VISIBLE);
                else
                    layout_pets.setVisibility(View.GONE);
                ed_pets.setText("");
                break;
            case R.id.radio_occpants:
                id = radio_occupants.getCheckedRadioButtonId();
                radioButton = radio_occupants.findViewById(id);
                idx = radio_occupants.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_occupants = rb.getText().toString();
                if (str_occupants.equalsIgnoreCase("yes"))
                    layput_occupant.setVisibility(View.VISIBLE);
                else {
                    layput_occupant.setVisibility(View.GONE);
                    ed_occupant.setText("");
                }

                break;
            case R.id.radio_vechiles:
                id = radio_vchiles.getCheckedRadioButtonId();
                radioButton = radio_vchiles.findViewById(id);
                idx = radio_vchiles.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_vhichel = rb.getText().toString();
                if (str_vhichel.equalsIgnoreCase("yes"))
                    layout_vechile.setVisibility(View.VISIBLE);
                else {
                    layout_vechile.setVisibility(View.GONE);
                    ed_vechiles.setText("");
                }

                break;
            case R.id.radio_crime:
                id = radio_crime.getCheckedRadioButtonId();
                radioButton = radio_crime.findViewById(id);
                idx = radio_crime.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_crime = rb.getText().toString();
                if (str_crime.equalsIgnoreCase("yes"))
                    layout_crime.setVisibility(View.VISIBLE);
                else {
                    layout_crime.setVisibility(View.GONE);
                    ed_crime.setText("");
                }

                break;
            case R.id.radio_bankrupty:
                id = radio_bankruptcy.getCheckedRadioButtonId();
                radioButton = radio_bankruptcy.findViewById(id);
                idx = radio_bankruptcy.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_bankrupty = rb.getText().toString();
                if (str_bankrupty.equalsIgnoreCase("yes"))
                    layout_bankrupty.setVisibility(View.VISIBLE);
                else {
                    layout_bankrupty.setVisibility(View.GONE);
                    ed_bankroprty.setText("");
                }
                break;
            case R.id.radio_evicted:
                id = bank_evicted.getCheckedRadioButtonId();
                radioButton = bank_evicted.findViewById(id);
                idx = bank_evicted.indexOfChild(radioButton);
                rb = (RadioButton) group.getChildAt(idx);
                str_evict = rb.getText().toString();
                if (str_evict.equalsIgnoreCase("yes"))
                    layout_evicted.setVisibility(View.VISIBLE);
                else {
                    layout_evicted.setVisibility(View.GONE);
                    ed_evicted.setText("");
                }
                break;

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
}
