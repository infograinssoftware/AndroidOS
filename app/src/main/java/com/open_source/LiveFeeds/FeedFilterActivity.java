package com.open_source.LiveFeeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.open_source.R;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.TinyDB;

import java.util.ArrayList;


public class FeedFilterActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    Context context;
    TextView toolbar_title;
    Toolbar h_toolbar;
    TinyDB tinydb;
    SwitchCompat stw_for_sale, stw_wanted, stw_forclosure_sale, stw_short_sale,
            stw_home, stw_plot, stw_commercial, stw_flat, stw_sell_by_ownwer, stw_Condominium,
            stw_SingleFamily, stw_Townhouse, stw_Semi_detached_House, stw_Duplex_Triplex;
    ArrayList<String> array_purpose = new ArrayList<>();
    ArrayList<String> array_type = new ArrayList<>();
    String str_purpose = "", str_type = "",str_radius="";
    TextView ctv_close, ctv_clear, txt_radius;
    RelativeLayout btn_filter;
    BottomSheetBehavior mBottomSheetBehavior2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        context = this;
        tinydb = new TinyDB(context);
        init();
    }

    private void init() {
        bindview();
        array_type = tinydb.getListString(Constants.FEEDS_TYPE);
        //Log.e("===========:",array_type.toString());
        array_purpose = tinydb.getListString(Constants.FEEDS_PURPOSE);

        View bottomSheet2 = findViewById(R.id.bottom_sheet2);
        LinearLayout lay_radius = findViewById(R.id.lay_radius);

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
        SetFilter();
    }

    private void bindview() {
        ((CardView) findViewById(R.id.locationview)).setVisibility(View.GONE);
        ((CardView) findViewById(R.id.card_other_info)).setVisibility(View.GONE);
        ((CardView) findViewById(R.id.posttype)).setVisibility(View.GONE);
        h_toolbar = (Toolbar) findViewById(R.id.h_toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.filter);
        toolbar_title.setCompoundDrawables(getResources().getDrawable(R.drawable.serach_black), null, null, null);
        ctv_close = findViewById(R.id.ctv_close);
        ctv_clear = findViewById(R.id.ctv_clear);
        stw_for_sale = findViewById(R.id.stw_for_sale);
        stw_wanted = findViewById(R.id.stw_wanted);
        stw_forclosure_sale = findViewById(R.id.stw_forclosure_sale);
        stw_short_sale = findViewById(R.id.stw_short_sale);
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
        btn_filter = findViewById(R.id.btn_filter);
        txt_radius = findViewById(R.id.txt_radius);

        ((CardView) findViewById(R.id.card_radius)).setOnClickListener(this);
        ((CardView) findViewById(R.id.cancle)).setOnClickListener(this);
        btn_filter.setOnClickListener(this);

        stw_for_sale.setOnCheckedChangeListener(this);
        stw_wanted.setOnCheckedChangeListener(this);
        stw_forclosure_sale.setOnCheckedChangeListener(this);
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

        ctv_close.setOnClickListener(this);
        ctv_clear.setOnClickListener(this);
    }

    private void SetFilter() {
        if (array_type.contains(getString(R.string.home)))
            stw_home.setChecked(true);
        if (array_type.contains(getString(R.string.plot)))
            stw_plot.setChecked(true);
        if (array_type.contains(getString(R.string.flat)))
            stw_flat.setChecked(true);
        if (array_type.contains(getString(R.string.commercial)))
            stw_commercial.setChecked(true);
        if (array_type.contains(getString(R.string.condominium)))
            stw_Condominium.setChecked(true);
        if (array_type.contains(getString(R.string.single_family)))
            stw_SingleFamily.setChecked(true);
        if (array_type.contains(getString(R.string.town_house)))
            stw_Townhouse.setChecked(true);
        if (array_type.contains(getString(R.string.semi_detached_house)))
            stw_Semi_detached_House.setChecked(true);
        if (array_type.contains(getString(R.string.duplex)))
            stw_Duplex_Triplex.setChecked(true);
        if (array_purpose.contains(getString(R.string.for_sale)))
            stw_for_sale.setChecked(true);
        if (array_purpose.contains(getString(R.string.wanted)))
            stw_wanted.setChecked(true);
        if (array_purpose.contains(getString(R.string.foreclosure_sale)))
            stw_forclosure_sale.setChecked(true);
        if (array_purpose.contains(getString(R.string.short_sale)))
            stw_short_sale.setChecked(true);
        if (array_purpose.contains(getString(R.string.for_sell_by_owner)))
            stw_sell_by_ownwer.setChecked(true);
        if (!tinydb.getString(Constants.FEED_RADIUS).isEmpty())
            txt_radius.setText(tinydb.getString(Constants.FEED_RADIUS));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.stw_for_sale:
                if (isChecked) {
                    if (!array_purpose.contains(getString(R.string.for_sale)))
                    array_purpose.add(getString(R.string.for_sale));
                } else {
                    array_purpose.remove(getString(R.string.for_sale));
                }
                Fun_save("purpose");
                break;
            case R.id.stw_wanted:
                if (isChecked) {
                    if (!array_purpose.contains(getString(R.string.wanted)))
                    array_purpose.add(getString(R.string.wanted));
                } else {
                    array_purpose.remove(getString(R.string.wanted));

                }
                Fun_save("purpose");
                break;
            case R.id.stw_forclosure_sale:
                if (isChecked) {
                    if (!array_purpose.contains(getString(R.string.foreclosure_sale)))
                    array_purpose.add(getString(R.string.foreclosure_sale));

                } else {
                    array_purpose.remove(getString(R.string.foreclosure_sale));
                }
                Fun_save("purpose");
                break;
            case R.id.stw_short_sale:
                if (isChecked) {
                    if (!array_purpose.contains(getString(R.string.short_sale)))
                    array_purpose.add(getString(R.string.short_sale));
                } else {
                    array_purpose.remove(getString(R.string.short_sale));
                }
                Fun_save("purpose");
                break;
            case R.id.stw_sell_by_ownwer:
                if (isChecked) {
                    if (!array_purpose.contains(getString(R.string.for_sell_by_owner)))
                    array_purpose.add(getString(R.string.for_sell_by_owner));
                } else {
                    array_purpose.remove(getString(R.string.for_sell_by_owner));
                }
                Fun_save("purpose");
                break;
            case R.id.stw_home:
                if (isChecked) {
                    if (!array_type.contains(getString(R.string.home)))
                    //str_type = getString(R.string.home);
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
        Log.e("======:",array_type.toString() );
        if (type.equals("type"))
            tinydb.putListString(Constants.FEEDS_TYPE, array_type);
        else if (type.equals("purpose"))
            tinydb.putListString(Constants.FEEDS_PURPOSE, array_purpose);

    }


    //-------------------------- Back Pressed -----------------------------
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctv_close:
                finish();
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
                stw_for_sale.setChecked(false);
                stw_wanted.setChecked(false);
                stw_forclosure_sale.setChecked(false);
                stw_short_sale.setChecked(false);
                stw_sell_by_ownwer.setChecked(false);
                stw_home.setChecked(false);
                stw_plot.setChecked(false);
                stw_flat.setChecked(false);
                stw_commercial.setChecked(false);
                stw_Condominium.setChecked(false);
                stw_SingleFamily.setChecked(false);
                stw_Townhouse.setChecked(false);
                stw_Semi_detached_House.setChecked(false);
                stw_Duplex_Triplex.setChecked(false);
                str_purpose = "";
                str_type = "";
                array_type.clear();
                array_purpose.clear();
                txt_radius.setText(getString(R.string.select_radius));
                tinydb.putListString(Constants.FEEDS_TYPE, array_type);
                tinydb.putListString(Constants.FEEDS_PURPOSE, array_purpose);
                tinydb.putString(Constants.FEED_RADIUS, "");
                break;
            case R.id.btn_filter:
                str_purpose = "";
                str_type = "";
                if (array_purpose.size() > 0) {
                    str_purpose = TextUtils.join(",", array_purpose);
                }
                if (array_type.size() > 0) {
                    str_type = TextUtils.join(",", array_type);
                }
                if (!txt_radius.getText().toString().equals(getString(R.string.select_radius)))
                {
                    tinydb.putString(Constants.FEED_RADIUS, txt_radius.getText().toString());
                    str_radius= txt_radius.getText().toString();
                }
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Constants.TYPE, str_type);
                returnIntent.putExtra(Constants.PURPOSE, str_purpose);
                returnIntent.putExtra(Constants.RADIUS, str_radius);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
        }
    }
}
