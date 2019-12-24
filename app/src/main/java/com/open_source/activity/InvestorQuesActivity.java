package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvestorQuesActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = InvestorQuesActivity.class.getSimpleName();
    ProgressHUD progressHUD;
    ArrayList<String> array_pref = new ArrayList<>();
    ArrayList<String> array_pro_type = new ArrayList<>();
    CheckBox check_wood, check_concrete, check_other, check_condos, check_single_family, check_plexes_apar, check_commercial;
    EditText et_cash_buyer, et_neighbour, et_price_range, et_town, et_bed_bath, et_sqare, et_pref_constrct,
            et_signle_multi, et_rehub_buy, ed_conmetic_fixer, et_close_property,
            et_invest_where, et_invest_how_many, et_goal_one_year, et_goal_two_year, et_goal_five_year;
    private Context context;
    private RadioGroup rg_cashbuyer, rg_town, rg_bed_bath, rg_sqare, rg_invertment, rg_risk_tolerance;
    private int id_cash = -1, id_area = -1, id_town = -1, id_bed_bath = -1, id_invertment = -1, id_tolerance = -1;
    private String str_risk_tolerance = "", str_const = "", str_pro_type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.investor_questionaries);
        context = this;
        init_view();
    }

    private void init_view() {
        rg_cashbuyer = findViewById(R.id.rg_cashbuyer);
        rg_town = findViewById(R.id.rg_town);
        rg_bed_bath = findViewById(R.id.rg_bed_bath);
        rg_sqare = findViewById(R.id.rg_sqare);
        rg_invertment = findViewById(R.id.rg_invertment);
        rg_risk_tolerance = findViewById(R.id.rg_risk_tolerance);
        check_wood = findViewById(R.id.check_wood);
        check_concrete = findViewById(R.id.check_concrete);
        check_other = findViewById(R.id.check_other);
        check_condos = findViewById(R.id.check_condos);
        check_single_family = findViewById(R.id.check_single_family);
        check_plexes_apar = findViewById(R.id.check_plexes_apar);
        check_commercial = findViewById(R.id.check_commercial);
        rg_cashbuyer.setOnCheckedChangeListener(this);
        rg_town.setOnCheckedChangeListener(this);
        rg_bed_bath.setOnCheckedChangeListener(this);
        rg_sqare.setOnCheckedChangeListener(this);
        rg_invertment.setOnCheckedChangeListener(this);
        rg_risk_tolerance.setOnCheckedChangeListener(this);
        check_wood.setOnCheckedChangeListener(this);
        check_concrete.setOnCheckedChangeListener(this);
        check_other.setOnCheckedChangeListener(this);
        check_condos.setOnCheckedChangeListener(this);
        check_single_family.setOnCheckedChangeListener(this);
        check_plexes_apar.setOnCheckedChangeListener(this);
        check_commercial.setOnCheckedChangeListener(this);
        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(this);
        et_cash_buyer = findViewById(R.id.et_cash_buyer);
        et_neighbour = findViewById(R.id.et_neighbour);
        et_price_range = findViewById(R.id.et_price_range);
        et_town = findViewById(R.id.et_town);
        et_bed_bath = findViewById(R.id.et_bed_bath);
        et_sqare = findViewById(R.id.et_sqare);
        et_pref_constrct = findViewById(R.id.et_pref_constrct);
        et_signle_multi = findViewById(R.id.et_signle_multi);
        et_rehub_buy = findViewById(R.id.et_rehub_buy);
        ed_conmetic_fixer = findViewById(R.id.ed_conmetic_fixer);
        et_close_property = findViewById(R.id.et_close_property);
        et_invest_where = findViewById(R.id.et_invest_where);
        et_invest_how_many = findViewById(R.id.et_invest_how_many);
        et_goal_one_year = findViewById(R.id.et_goal_one_year);
        et_goal_two_year = findViewById(R.id.et_goal_two_year);
        et_goal_five_year = findViewById(R.id.et_goal_five_year);
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_cashbuyer:
                id_cash = rg_cashbuyer.indexOfChild(rg_cashbuyer.findViewById(rg_cashbuyer.getCheckedRadioButtonId()));
                if (id_cash == 0)
                    et_cash_buyer.setVisibility(View.GONE);
                else
                    et_cash_buyer.setVisibility(View.VISIBLE);
                break;

            case R.id.rg_town:
                id_town = rg_town.indexOfChild(rg_town.findViewById(rg_town.getCheckedRadioButtonId()));
                if (id_town == 0)
                    et_town.setVisibility(View.VISIBLE);
                else
                    et_town.setVisibility(View.GONE);
                break;

            case R.id.rg_bed_bath:
                id_bed_bath = rg_bed_bath.indexOfChild(rg_bed_bath.findViewById(rg_bed_bath.getCheckedRadioButtonId()));
                if (id_bed_bath == 0)
                    et_bed_bath.setVisibility(View.VISIBLE);
                else
                    et_bed_bath.setVisibility(View.GONE);
                break;

            case R.id.rg_sqare:
                id_area = rg_sqare.indexOfChild(rg_sqare.findViewById(rg_sqare.getCheckedRadioButtonId()));
                if (id_area == 0)
                    et_sqare.setVisibility(View.VISIBLE);
                else
                    et_sqare.setVisibility(View.GONE);
                break;

            case R.id.rg_invertment:
                id_invertment = rg_invertment.indexOfChild(rg_invertment.findViewById(rg_invertment.getCheckedRadioButtonId()));
                if (id_invertment == 0) {
                    et_invest_where.setVisibility(View.VISIBLE);
                    et_invest_how_many.setVisibility(View.VISIBLE);
                } else {
                    et_invest_where.setVisibility(View.GONE);
                    et_invest_how_many.setVisibility(View.GONE);
                }
                break;
            case R.id.rg_risk_tolerance:
                id_tolerance = rg_risk_tolerance.indexOfChild(rg_risk_tolerance.findViewById(rg_risk_tolerance.getCheckedRadioButtonId()));
                RadioButton r = (RadioButton) rg_risk_tolerance.getChildAt(id_tolerance);
                str_risk_tolerance = r.getText().toString();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check_wood:
                if (isChecked)
                    array_pref.add(String.valueOf(check_wood.getText()));
                else
                    array_pref.remove(String.valueOf(check_wood.getText()));
                break;
            case R.id.check_concrete:
                if (isChecked)
                    array_pref.add(String.valueOf(check_concrete.getText()));
                else
                    array_pref.remove(String.valueOf(check_concrete.getText()));
                break;
            case R.id.check_other:
                if (isChecked) {
                    array_pref.add(String.valueOf(check_other.getText()));
                    ((EditText) findViewById(R.id.et_pref_constrct)).setVisibility(View.VISIBLE);
                } else {
                    array_pref.remove(String.valueOf(check_other.getText()));
                    if (!et_pref_constrct.getText().toString().isEmpty()) {
                        array_pref.remove(et_pref_constrct.getText().toString());
                    }
                    et_pref_constrct.setText("");
                    ((EditText) findViewById(R.id.et_pref_constrct)).setVisibility(View.GONE);
                }

                break;
            case R.id.check_condos:
                if (isChecked)
                    array_pro_type.add(String.valueOf(check_condos.getText()));
                else
                    array_pro_type.remove(String.valueOf(check_condos.getText()));
                break;
            case R.id.check_plexes_apar:
                if (isChecked)
                    array_pro_type.add(String.valueOf(check_plexes_apar.getText()));
                else
                    array_pro_type.remove(String.valueOf(check_plexes_apar.getText()));
                break;
            case R.id.check_single_family:
                if (isChecked)
                    array_pro_type.add(String.valueOf(check_single_family.getText()));
                else
                    array_pro_type.remove(String.valueOf(check_single_family.getText()));
                break;
            case R.id.check_commercial:
                if (isChecked)
                    array_pro_type.add(String.valueOf(check_commercial.getText()));
                else
                    array_pro_type.remove(String.valueOf(check_commercial.getText()));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (id_cash == -1)
                    Utility.ShowToastMessage(context, R.string.cash_buyer);
                else if (id_cash == 1 && et_cash_buyer.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.hint_cash_buyer);
                else if (et_neighbour.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_neightbourhood);
                else if (et_price_range.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_price_rang);
                else if (id_town == -1)
                    Utility.ShowToastMessage(context, R.string.ques_part_town);
                else if (id_town == 0 && et_town.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.desc_town));
                else if (id_bed_bath == -1)
                    Utility.ShowToastMessage(context, getString(R.string.ques_bed_bath));
                else if (id_bed_bath == 0 && et_bed_bath.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_bed_required));
                else if (id_area == -1)
                    Utility.ShowToastMessage(context, getString(R.string.ques_footage));
                else if (id_area == 0 && et_sqare.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_area_require));
                else if (array_pref.size() <= 0)
                    Utility.ShowToastMessage(context, getString(R.string.ques_construction));
                else if (array_pref.size() > 0 && (array_pref.contains(getString(R.string.other))) && (et_pref_constrct.getText().toString().isEmpty()))
                    Utility.ShowToastMessage(context, getString(R.string.please_describe));
                else if (et_signle_multi.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_signle_multifamily);
                else if (et_rehub_buy.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_rehub);
                else if (ed_conmetic_fixer.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_fixer);
                else if (et_close_property.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, R.string.ques_close_time);
                else if (id_invertment == -1)
                    Utility.ShowToastMessage(context, R.string.ques_own_invertment);
                else if (id_invertment == 0 && et_invest_where.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.val_invest_property));
                else if (id_invertment == 0 && et_invest_how_many.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.how_many_unit));
                else if (array_pro_type.size() <= 0)
                    Utility.ShowToastMessage(context, getString(R.string.ques_intersred_property));
                else if (str_risk_tolerance.isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.ques_risk_tolerance));
                else if (et_goal_one_year.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.describe_within_one_year));
                else if (et_goal_two_year.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.describe_within_two_year));
                else if (et_goal_five_year.getText().toString().isEmpty())
                    Utility.ShowToastMessage(context, getString(R.string.describe_within_five_year));
                else {
                    str_const = "";
                    str_pro_type = "";
                    if (array_pref.contains(getString(R.string.other)))
                        array_pref.remove(getString(R.string.other));
                    array_pref.add(et_pref_constrct.getText().toString());
                    str_const = TextUtils.join(",", array_pref);
                    str_pro_type = TextUtils.join(",", array_pro_type);
                    Log.e(TAG, "======: " + str_const);
                    Log.e(TAG, "======: " + str_pro_type);
                    FunServer();
                }
                break;
        }
    }

    private void FunServer() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            if (id_cash == 0)
                builder.addFormDataPart(Constants.CASH_BUYER, "Yes");
            else
                builder.addFormDataPart(Constants.CASH_BUYER, et_cash_buyer.getText().toString());
            if (id_town == 0)
                builder.addFormDataPart(Constants.buy_town, et_town.getText().toString());
            else
                builder.addFormDataPart(Constants.buy_town, "No");
            if (id_bed_bath == 0)
                builder.addFormDataPart(Constants.no_bedrooms_bath, et_bed_bath.getText().toString());
            else
                builder.addFormDataPart(Constants.no_bedrooms_bath, "No");
            if (id_area == 0)
                builder.addFormDataPart(Constants.square_feet, et_sqare.getText().toString());
            else
                builder.addFormDataPart(Constants.square_feet, "No");
            if (id_invertment == 0) {
                builder.addFormDataPart(Constants.where_invest_property, et_invest_where.getText().toString());
                builder.addFormDataPart(Constants.property_unit, et_invest_how_many.getText().toString());
            } else
                builder.addFormDataPart(Constants.investment_property, "No");
            builder.addFormDataPart(Constants.neighbourhood, et_neighbour.getText().toString().trim());
            builder.addFormDataPart(Constants.price_range, et_price_range.getText().toString().trim());
            builder.addFormDataPart(Constants.family_property_type, et_signle_multi.getText().toString());
            builder.addFormDataPart(Constants.looking_for_property, et_rehub_buy.getText().toString());
            builder.addFormDataPart(Constants.rehabs_or_fixer, ed_conmetic_fixer.getText().toString());
            builder.addFormDataPart(Constants.property_closing_time, et_close_property.getText().toString());
            builder.addFormDataPart(Constants.construction_type, str_const);
            builder.addFormDataPart(Constants.interested_property_type, str_pro_type);
            builder.addFormDataPart(Constants.investment_style, str_risk_tolerance);
            builder.addFormDataPart(Constants.one_year_goal, et_goal_one_year.getText().toString());
            builder.addFormDataPart(Constants.two_year_goal, et_goal_two_year.getText().toString());
            builder.addFormDataPart(Constants.five_year_goal, et_goal_five_year.getText().toString());
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().InvestorQues(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, LoginActivity.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

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
}
