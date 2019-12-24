package com.open_source.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.open_source.Listener.NumberPicker;
import com.open_source.R;
import com.open_source.activity.LoginActivity;
import com.open_source.activity.MainActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.CountertopsAdapter;
import com.open_source.adapter.DisclosureAdapter;
import com.open_source.adapter.FixureAdapter;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.Subcatogery;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.NonScrollListView;
import com.open_source.util.NumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.open_source.videocompression.MediaController;
import com.silencedut.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by and-02 on 13/6/18.
 */
public class SellPropertyNext extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SellPropertyNext.class.getSimpleName();
    public static EditText ed_other;
    public static View row_view;
    public static NonScrollListView innerfixurelist;
    final int PERMISSION_REQUEST_CODE = 101;
    ImageAdapter imageAdapter;
    File uploadfile = null;
    Context context;
    Toolbar toolbar;
    View rootView;
    File photoFile = null;
    Boolean rent_status = false;
    Bitmap video_bitmap;
    RecyclerView recycle_pro_image;
    NumberPicker numberPicker_first, numberPicker_second, numberPicker_third, numberPicker_four, numberPicker_five, numberPicker_six;
    BottomSheetBehavior mBottomSheetBehavior2;
    ImageView imgpickvideo, imgplay, delete_video, img_expand_fixtures, img_expand_disclosure, property_profile;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    String str_image_id = "", str_discloser = "", str_fixsure = "", str_discloser_id = "",
            str_post = "fixed", str_auction = "", video_id = "", str_fixed_price = "", str_agreement_id = "",
            str_contract_id = "", str_disclosure_level, str_patios = "", str_washer = "", str_wood = "", str_carpet = "",
            str_ceiling = "", str_fier = "", str_counterstop = "",str_bathroom, str_badroom;;
    File file_pro_img = null, CompressvideoFile = null, videofile = null;
    int imageClick = -1, profile_img_id = -1;
    LinearLayout layoutfixed, lay_start_date, lay_start_date1, lay_end_date, lay_end_date1, lay_start_time, lay_end_time,
            lay_action_fixed_price, lay_dec_suggection, lay_trns_suggection, lay_pro_img;
    CardView card_auction;
    TextView start_date, end_date, start_date1, et_time, start_time, end_time;
    RadioGroup rg_price_type, rg_pubpvt, rg_price_type_sec;
    RadioButton rb_fixed, rb_auction, rb_both, rb_oso, rb_all;
    EditText property_price, ed_desc, ed_trans, min_bid_price, auction_fixed_price, ed_video_detail;
    DisclosureAdapter disclosureAdapter;
    FixureAdapter fixureAdapter;
    CountertopsAdapter CountertopsAdapter;
    ArrayList<String> rowlist = new ArrayList<>();
    ArrayList<String> fixurearraylist = new ArrayList<>();
    ArrayList<String> disclosureArraylist = new ArrayList<>();
    ArrayList<String> array_suggestion = new ArrayList<>();
    ArrayList<Subcatogery> array_images = new ArrayList<>();
    ArrayList<Subcatogery> array_clipart = new ArrayList<>();
    RelativeLayout btn_upload;
    int idx, idx_pubpvt = -1, click = 0, clickdelete = 0;
    Uri videoUri;
    String strcurrdate;
    ProgressHUD progressHUD;
    ArrayList<String> filePaths = new ArrayList<>();
    Bundle bundle;
    Button upload_video;
    TextView toolbar_title;
    ExpandableLayout expandableLayout, expandable_layout_disclosure;
    CheckBox check1, check2, check3, check4, check5, check6;
    ArrayList<String> inner_fixtures = new ArrayList<>();
    private String imageFilePath = "";
    private NonScrollListView desclosurelist, fixurelist;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String strvideofile;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_properties_next);
        context = SellPropertyNext.this;
        init();
    }

    private void init() {
        bundle = getIntent().getExtras();
        for (String key : bundle.keySet()) {
            Log.d("Bundle Debug", key + " = \"" + bundle.get(key) + "\"");
        }
        DisclosureAdapter.box1.clear();
        rowlist.add("Has the property every been a crack house");
        rowlist.add("Is there asbestos in the duct work ?");
        rowlist.add("Has the basement ever leaked");
        rowlist.add("Is there flood area");
        rowlist.add("The fire is not in the area");
        rowlist.add("The earthquakes is not in the area");
        rowlist.add("Other");

        fixurearraylist.add(getString(R.string.breakfast_nook));
        fixurearraylist.add(getString(R.string.built_in_Microwaves));
        fixurearraylist.add(getString(R.string.cental_heating_air));
        fixurearraylist.add(getString(R.string.dishwaser));
        fixurearraylist.add(getString(R.string.energy_pan));
        fixurearraylist.add(getString(R.string.kitchen_appliences));
        fixurearraylist.add(getString(R.string.energy_efficient));
        fixurearraylist.add(getString(R.string.garbage_disposal));
        fixurearraylist.add(getString(R.string.lockable_storage));
        fixurearraylist.add(getString(R.string.lamps));
        fixurearraylist.add(getString(R.string.pool));
        fixurearraylist.add(getString(R.string.refrigetor));
        fixurearraylist.add(getString(R.string.countertops));

        inner_fixtures.add(getString(R.string.quartz));
        inner_fixtures.add(getString(R.string.granite));
        inner_fixtures.add(getString(R.string.laminate));
        inner_fixtures.add(getString(R.string.marble));
        inner_fixtures.add(getString(R.string.cement));
        inner_fixtures.add(getString(R.string.formica));
        inner_fixtures.add(getString(R.string.santa_celia));

        array_suggestion.add(getString(R.string.sugg1));
        array_suggestion.add(getString(R.string.sugg2));
        array_suggestion.add(getString(R.string.sugg3));
        array_suggestion.add(getString(R.string.sugg4));
        array_suggestion.add(getString(R.string.sugg5));
        array_suggestion.add(getString(R.string.sugg6));
        array_suggestion.add(getString(R.string.sugg7));
        array_suggestion.add(getString(R.string.sugg8));

        http:
//18.191.6.144/os-real-estate/uploads//service_images//house-icon1.png")
        array_clipart.add(new Subcatogery("164", "http://18.191.6.144//os-real-estate//uploads//service_images//house-icon1.png"));
        array_clipart.add(new Subcatogery("165", "http://18.191.6.144//os-real-estate//uploads//service_images//house-icon2.jpeg"));
        array_clipart.add(new Subcatogery("166", "http://18.191.6.144//os-real-estate//uploads//service_images//house-icon3.jpeg"));
        array_clipart.add(new Subcatogery("167", "http://18.191.6.144//os-real-estate//uploads//service_images//house-icon4.png"));

        lay_dec_suggection = findViewById(R.id.lay_dec_suggection);
        lay_trns_suggection = findViewById(R.id.lay_trns_suggection);
        lay_pro_img = findViewById(R.id.lay_pro_img);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.sell_property);
        final View bottomSheet2 = findViewById(R.id.bottom_sheet2);
        recycle_pro_image = findViewById(R.id.recycle_pro_image);
        recycle_pro_image.setLayoutManager(new GridLayoutManager(context, 2));
        imageAdapter = new ImageAdapter(context, array_images);
        recycle_pro_image.setAdapter(imageAdapter);
        ((ImageView) findViewById(R.id.add_images)).setOnClickListener(this);
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        mBottomSheetBehavior2.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        //row_view = findViewById(R.id.row_view);
        imgpickvideo = findViewById(R.id.imgvideo);
        imgplay = findViewById(R.id.imgplay);
        delete_video = findViewById(R.id.delete_video);
        layoutfixed = findViewById(R.id.layoutfixed);
        lay_action_fixed_price = findViewById(R.id.lay_action_fixed_price);
        card_auction = findViewById(R.id.card_auction);
        lay_start_date = findViewById(R.id.lay_start_date);
        lay_start_date1 = findViewById(R.id.lay_start_date1);
        lay_end_date = findViewById(R.id.lay_end_date);
        lay_end_date1 = findViewById(R.id.lay_end_date1);
        lay_start_time = findViewById(R.id.lay_start_time);
        lay_end_time = findViewById(R.id.lay_end_time);
        start_time = findViewById(R.id.et_starttime);
        end_time = findViewById(R.id.et_endtime);
        start_date = findViewById(R.id.et_startdate);
        end_date = findViewById(R.id.et_endtdate);
        start_date1 = findViewById(R.id.et_start_date1);
        et_time = findViewById(R.id.et_time);
        property_profile = findViewById(R.id.property_profile);
        property_profile.setOnClickListener(this);
        /*rg_price_type = findViewById(R.id.rg_price_type);
        rg_price_type_sec=findViewById(R.id.rg_price_type_sec);*/
        rg_pubpvt = findViewById(R.id.rg_pubpvt);
        rb_fixed = findViewById(R.id.rb_fixed);
        rb_fixed.setOnCheckedChangeListener(this);
        rb_auction = findViewById(R.id.rb_auction);
        rb_auction.setOnCheckedChangeListener(this);
        rb_both = findViewById(R.id.rb_both);
        rb_both.setOnCheckedChangeListener(this);
        rb_oso = findViewById(R.id.rb_oso);
        rb_oso.setOnCheckedChangeListener(this);
        rb_all = findViewById(R.id.rb_all);
        rb_all.setOnCheckedChangeListener(this);
        property_price = findViewById(R.id.property_price);
        auction_fixed_price = findViewById(R.id.auction_fixed_price);
        ed_video_detail = findViewById(R.id.ed_video_detail);
        ed_desc = findViewById(R.id.ed_desc);
        ed_other = findViewById(R.id.ed_other);
        ed_trans = findViewById(R.id.ed_trans);
        min_bid_price = findViewById(R.id.min_bid_price);
        auction_fixed_price.addTextChangedListener(new NumberTextWatcher(auction_fixed_price));
        min_bid_price.addTextChangedListener(new NumberTextWatcher(min_bid_price));
        property_price.addTextChangedListener(new NumberTextWatcher(property_price));
        btn_upload = findViewById(R.id.btn_upload);
        upload_video = findViewById(R.id.upload_video);
        desclosurelist = findViewById(R.id.desclosurelist);
        fixurelist = findViewById(R.id.fixurelist);
        innerfixurelist = findViewById(R.id.innerfixurelist);
        img_expand_fixtures = findViewById(R.id.img_expand_fixtures);
        img_expand_disclosure = findViewById(R.id.img_expand_disclosure);
        expandableLayout = (ExpandableLayout) findViewById(R.id.expandable_layout);
        lay_trns_suggection = findViewById(R.id.lay_trns_suggection);
        expandable_layout_disclosure = (ExpandableLayout) findViewById(R.id.expandable_layout_disclosure);
        if (bundle.getString(Constants.PROPERTY_FOR, "").equalsIgnoreCase("2")) {
            rent_status = true;
            card_auction.setVisibility(View.GONE);
            layoutfixed.setVisibility(View.GONE);
            toolbar_title.setText(R.string.title_rent_property);
            ((LinearLayout) findViewById(R.id.layout_type)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.card_disclosure)).setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.lay_check_offer)).setVisibility(View.GONE);

        }
        disclosureAdapter = new DisclosureAdapter(context, rowlist, bundle.getString(Constants.DISCLOSER, ""));
        desclosurelist.setAdapter(disclosureAdapter);
        fixureAdapter = new FixureAdapter(context, fixurearraylist);
        fixurelist.setAdapter(fixureAdapter);
        CountertopsAdapter = new CountertopsAdapter(context, inner_fixtures);
        innerfixurelist.setAdapter(CountertopsAdapter);
        expandableLayout.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                if (expanded) {
                    innerfixurelist.setVisibility(View.GONE);
                    img_expand_fixtures.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_fixtures.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
        expandable_layout_disclosure.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                // Toast.makeText(context, "clickexpand", Toast.LENGTH_SHORT).show();
                if (expanded) {
                    img_expand_disclosure.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_disclosure.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
        expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
       /* if (bundle.getString(Constants.TYPE).equalsIgnoreCase("Plot"))
            ((CardView) findViewById(R.id.card_fixtures)).setVisibility(View.GONE);
*/

        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        start_date1.setOnClickListener(this);
        et_time.setOnClickListener(this);
        start_time.setOnClickListener(this);
        end_time.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        imgpickvideo.setOnClickListener(this);
        upload_video.setOnClickListener(this);
        delete_video.setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach1)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach2)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach3)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach4)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach5)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach6)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach7)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach8)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach9)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach10)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach11)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach12)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach13)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach14)).setOnClickListener(this);
        ((Button) findViewById(R.id.doc_attach15)).setOnClickListener(this);
        ((Button) findViewById(R.id.agreement_attach)).setOnClickListener(this);
        ((Button) findViewById(R.id.contract_attach)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img1)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img2)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img3)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img4)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img5)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img6)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img7)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img8)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img9)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img10)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img11)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img12)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img13)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img14)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.img15)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.Contract_img)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.agreement_img)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.info_icon)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.info_icon_offer)).setOnClickListener(this);
        numberPicker_first = findViewById(R.id.number_picker_first);
        numberPicker_second = findViewById(R.id.number_picker_second);
        numberPicker_third = findViewById(R.id.number_picker_third);
        numberPicker_four = findViewById(R.id.number_picker_four);
        numberPicker_five = findViewById(R.id.number_picker_five);
        numberPicker_six = findViewById(R.id.number_picker_six);
        check1 = (CheckBox) findViewById(R.id.check1);
        check1.setOnCheckedChangeListener(this);
        check2 = (CheckBox) findViewById(R.id.check2);
        check2.setOnCheckedChangeListener(this);
        check3 = (CheckBox) findViewById(R.id.check3);
        check3.setOnCheckedChangeListener(this);
        check4 = (CheckBox) findViewById(R.id.check4);
        check4.setOnCheckedChangeListener(this);
        check5 = (CheckBox) findViewById(R.id.check5);
        check5.setOnCheckedChangeListener(this);
        check6 = (CheckBox) findViewById(R.id.check6);
        check6.setOnCheckedChangeListener(this);

        for (int i = 0; i < array_suggestion.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.row_suggestion, lay_dec_suggection, false);
            final TextView txt_suggestion = view.findViewById(R.id.txt_suggestion);
            txt_suggestion.setText(array_suggestion.get(i));
            txt_suggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_desc.setText(txt_suggestion.getText().toString());

                }
            });
            lay_dec_suggection.addView(view);
        }

        for (int i = 0; i < array_suggestion.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.row_suggestion, lay_trns_suggection, false);
            final TextView txt_suggestion = view.findViewById(R.id.txt_suggestion);
            txt_suggestion.setText(array_suggestion.get(i));
            txt_suggestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ed_trans.setText(txt_suggestion.getText().toString());

                }
            });
            lay_trns_suggection.addView(view);
        }

        for (int i = 0; i < array_clipart.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.item_clip_img, lay_pro_img, false);
            final ImageView img = view.findViewById(R.id.img_work);
            Glide.with(context).load(array_clipart.get(i).getName()).into(img);
            img.setTag(array_clipart.get(i).getId() + "," + array_clipart.get(i).getName());
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = String.valueOf(v.getTag());
                    String value[] = s.split(",");
                    if(profile_img_id==-1)
                    {
                        profile_img_id= Integer.parseInt(value[0]);
                        Glide.with(context).load(value[1]).into(property_profile);
                    }
                    else {
                        Subcatogery subcatogery = new Subcatogery();
                        subcatogery.setId(value[0]);
                        subcatogery.setName(value[1]);
                        array_images.add(subcatogery);
                        imageAdapter.notifyDataSetChanged();
                    }

                }
            });
            lay_pro_img.addView(view);
        }


       /* rg_price_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = rg_price_type.getCheckedRadioButtonId();
                View radioButton = rg_price_type.findViewById(radioButtonID);
                idx = rg_price_type.indexOfChild(radioButton);
                RadioButton r = (RadioButton) rg_price_type.getChildAt(idx);
                str_post = r.getText().toString();
                switch (idx) {
                    case 0:
                        ((RadioButton)rg_price_type_sec.getChildAt(0)).setChecked(false);
                        ((RadioButton)rg_price_type_sec.getChildAt(1)).setChecked(false);
                        // rg_pubpvt.clearCheck();
                        idx = 0;
                        str_post = "fixed";
                        str_auction = "";
                        start_date.setText("");
                        end_date.setText("");
                        start_time.setText("");
                        end_time.setText("");
                        min_bid_price.setText("");
                        auction_fixed_price.setText("");
                        layoutfixed.setVisibility(View.VISIBLE);
                        card_auction.setVisibility(View.GONE);
                        lay_action_fixed_price.setVisibility(View.GONE);
                        break;
                    case 1:
                        ((RadioButton)rg_price_type_sec.getChildAt(0)).setChecked(false);
                        ((RadioButton)rg_price_type_sec.getChildAt(1)).setChecked(false);
                        // rg_pubpvt.check(0);
                        str_auction = "Public";
                        str_post = "auction";
                        idx = 1;
                        property_price.setText("");
                        auction_fixed_price.setText("");
                        layoutfixed.setVisibility(View.GONE);
                        card_auction.setVisibility(View.VISIBLE);
                        lay_action_fixed_price.setVisibility(View.GONE);
                        break;


                }
            }
        });

        rg_price_type_sec.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = rg_price_type_sec.getCheckedRadioButtonId();
                View radioButton = rg_price_type_sec.findViewById(radioButtonID);
                idx = rg_price_type_sec.indexOfChild(radioButton);
                RadioButton r = (RadioButton) rg_price_type_sec.getChildAt(idx);
                str_post = r.getText().toString();
                switch (idx) {
                    case 0:
                        ((RadioButton)rg_price_type.getChildAt(0)).setChecked(false);
                        ((RadioButton)rg_price_type.getChildAt(1)).setChecked(false);
                        idx = 1;
                        str_auction = "Public";
                        str_post = "both";
                        property_price.setText("");
                        layoutfixed.setVisibility(View.GONE);
                        card_auction.setVisibility(View.VISIBLE);
                        lay_action_fixed_price.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        ((RadioButton)rg_price_type.getChildAt(0)).setChecked(false);
                        ((RadioButton)rg_price_type.getChildAt(1)).setChecked(false);
                        idx = 0;
                        str_post = "fixed"; //oso_offer
                        str_auction = "";
                        start_date.setText("");
                        end_date.setText("");
                        start_time.setText("");
                        end_time.setText("");
                        min_bid_price.setText("");
                        auction_fixed_price.setText("");
                        layoutfixed.setVisibility(View.VISIBLE);
                        card_auction.setVisibility(View.GONE);
                        lay_action_fixed_price.setVisibility(View.GONE);
                        break;

                }

            }
        });*/

        rg_pubpvt.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = rg_pubpvt.getCheckedRadioButtonId();
                View radioButton = rg_pubpvt.findViewById(radioButtonID);
                idx_pubpvt = rg_pubpvt.indexOfChild(radioButton);
                if (idx_pubpvt != -1) {
                    RadioButton r = (RadioButton) rg_pubpvt.getChildAt(idx_pubpvt);
                    str_auction = r.getText().toString();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.property_profile:
                imageClick = 2;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectImage();
                }
                break;
            case R.id.add_images:
                imageClick = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectImage();
                }
                break;
            case R.id.delete_video:
                if (!video_id.isEmpty()) {
                    DeleteVideo();
                } else {
                    videofile = null;
                    delete_video.setVisibility(View.GONE);
                    imgplay.setVisibility(View.GONE);
                    imgpickvideo.setImageResource(0);
                    imgpickvideo.setBackground(getResources().getDrawable(R.drawable.videoadd));
                }

                break;
            case R.id.info_icon:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    ((TextView) findViewById(R.id.bottomtitle)).setText(getString(R.string.video_info));
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.info_icon_offer:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    ((TextView) findViewById(R.id.bottomtitle)).setText(R.string.os_offer_msg);
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;
            case R.id.et_startdate:
                PickDate("start_date");
                break;

            case R.id.et_start_date1:
                PickDate("start_date1");
                break;

            case R.id.et_endtdate:
                PickDate("end_date");
                break;

            case R.id.et_time:
                PickTime("et_time");
                //  PickDate("end_date1");
                break;

            case R.id.et_starttime:
                PickTime("start_time");
                break;

            case R.id.et_endtime:
                PickTime("end_time");
                break;

            case R.id.imgvideo:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectVideo();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectVideo();
                }
                break;
            case R.id.upload_video:
                if (videofile == null) {
                    Utility.ShowToastMessage(context, getString(R.string.pick_video));
                } else {
                    if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                        Utility.ShowToastMessage(context, getString(R.string.message_guest));
                        startActivity(new Intent(context, LoginActivity.class));
                        finishAffinity();
                    } else {
                        long length = Integer.parseInt(String.valueOf(videofile.length()));
                        if (length > 40000000) {
                            Utility.ShowToastMessage(context, getString(R.string.file_size_limit));
                        } else if (length > 1024 * 1024) {
                            new VideoCompressor().execute();
                        } else {
                            File video_file = null;
                            if (video_bitmap != null) {
                                video_file = persistImage(video_bitmap, "video_thumb");
                            }
                            progressHUD = ProgressHUD.show(context, true, false, null);
                            Fun_uploadVideo(videofile, video_file);
                        }
                    }
                }
                break;
            case R.id.doc_attach1:
                imageClick = 1;
                click = 1;
                str_disclosure_level = getString(R.string.receipts_and_warranties);
                selectimag();
                break;
            case R.id.doc_attach2:
                imageClick = 1;
                click = 2;
                str_disclosure_level = getString(R.string.plans_and_permits);
                selectimag();
                break;
            case R.id.doc_attach3:
                imageClick = 1;
                click = 3;
                str_disclosure_level = getString(R.string.certificates_of_occupancy);
                selectimag();
                break;
            case R.id.doc_attach4:
                str_disclosure_level = getString(R.string.loan_document);
                imageClick = 1;
                click = 4;
                selectimag();
                break;
            case R.id.doc_attach5:
                imageClick = 1;
                click = 5;
                str_disclosure_level = getString(R.string.latest_utility_bills);
                selectimag();
                break;
            case R.id.doc_attach6:
                imageClick = 1;
                click = 6;
                str_disclosure_level = getString(R.string.latest_property_tax_bill);
                selectimag();
                break;
            case R.id.doc_attach7:
                imageClick = 1;
                click = 7;
                str_disclosure_level = getString(R.string.title);
                selectimag();
                break;
            case R.id.doc_attach8:
                imageClick = 1;
                click = 8;
                str_disclosure_level = getString(R.string.survay);
                selectimag();
                break;
            case R.id.doc_attach9:
                click = 9;
                imageClick = 1;
                str_disclosure_level = getString(R.string.constract_accupation);
                selectimag();
                break;
            case R.id.doc_attach10:
                imageClick = 1;
                click = 10;
                str_disclosure_level = getString(R.string.profe_insurance);
                selectimag();
                break;
            case R.id.doc_attach11:
                imageClick = 1;
                click = 11;
                str_disclosure_level = getString(R.string.utility_bill);
                selectimag();
                break;
            case R.id.doc_attach12:
                imageClick = 1;
                click = 12;
                str_disclosure_level = getString(R.string.homeownwer_associate);
                selectimag();
                break;
            case R.id.doc_attach13:
                imageClick = 1;
                click = 13;
                str_disclosure_level = getString(R.string.specility_document);
                selectimag();
                break;
            case R.id.doc_attach14:
                imageClick = 1;
                click = 14;
                str_disclosure_level = getString(R.string.floor_plan);
                selectimag();
                break;
            case R.id.doc_attach15:
                imageClick = 1;
                click = 15;
                str_disclosure_level = getString(R.string.other);
                selectimag();
                break;
            case R.id.img1:
                clickdelete = 1;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img2:
                clickdelete = 2;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img3:
                clickdelete = 3;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img4:
                clickdelete = 4;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img5:
                clickdelete = 5;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img6:
                clickdelete = 6;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img7:
                clickdelete = 7;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img8:
                clickdelete = 8;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img9:
                clickdelete = 9;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img10:
                clickdelete = 10;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img11:
                clickdelete = 11;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img12:
                clickdelete = 12;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img13:
                clickdelete = 13;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img14:
                clickdelete = 14;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.img15:
                clickdelete = 15;
                Log.e(TAG, "======: " + v.getTag());
                Delete_Disclosure(String.valueOf(v.getTag()), "");
                break;
            case R.id.btn_upload:
                filePaths.clear();
                if (check1.isChecked())
                    str_patios = String.valueOf(numberPicker_first.getValue());
                else str_patios = "";
                if (check2.isChecked())
                    str_washer = String.valueOf(numberPicker_second.getValue());
                else str_washer = "";
                if (check3.isChecked())
                    str_wood = String.valueOf(numberPicker_third.getValue());
                else str_wood = "";
                if (check4.isChecked())
                    str_carpet = String.valueOf(numberPicker_four.getValue());
                else str_carpet = "";
                if (check5.isChecked())
                    str_ceiling = String.valueOf(numberPicker_five.getValue());
                else str_ceiling = "";
                if (check6.isChecked())
                    str_fier = String.valueOf(numberPicker_six.getValue());
                else str_fier = "";

                if (CountertopsAdapter.Countertops.size() > 0) {
                    str_counterstop = android.text.TextUtils.join(",", CountertopsAdapter.Countertops);
                }
                if (FixureAdapter.boxfixure.size() > 0) {
                    str_fixsure = android.text.TextUtils.join(",", FixureAdapter.boxfixure);
                }
                if (disclosureArraylist.size() > 0) {
                    str_discloser_id = android.text.TextUtils.join(",", disclosureArraylist);
                }
                if (CountertopsAdapter.Countertops.size() > 0) {
                    str_counterstop = android.text.TextUtils.join(",", CountertopsAdapter.Countertops);
                }
               /* Log.e(TAG, "========: boxfixure" + str_fixsure);
                Log.e(TAG, "========:Countertops" + str_counterstop);
                Log.e(TAG, "========:str_agreement_id " + str_patios+","+str_washer+","+str_wood+","+str_carpet+","+str_ceiling+","+str_fier);*/
                if (idx == 0 && property_price.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_price));
                } else if (idx == 1 && start_date.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_date));
                } else if (idx == 1 && end_date.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_enddate));
                } else if (idx == 1 && Utility.compaireDate(start_date.getText().toString(), end_date.getText().toString()) < 0 && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.end_date_validation));
                } else if (idx == 1 && start_time.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_start_time));
                } else if (idx == 1 && end_time.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_end_time));
                } else if (idx == 1 && min_bid_price.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bid_price));
                } else if (str_post.equalsIgnoreCase("both") && auction_fixed_price.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_price));
                } else if (ed_other.getText().toString().trim().isEmpty() && str_discloser.contains("Other")) {
                    Utility.ShowToastMessage(context, R.string.val_other_disclosure);
                } else if (ed_desc.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_desc));
                } else if (ed_trans.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_tran));
                } else if (profile_img_id == -1) {
                    Utility.ShowToastMessage(context,getString(R.string.val_pro_profile));
                } else if (array_images.size() <= 0) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_image_upload));
                } else if (videofile != null && video_id.equals("")) {
                    Utility.ShowToastMessage(context, getString(R.string.val_video));
                } else if (!video_id.isEmpty() && start_date1.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_video_view));
                } else if (!video_id.isEmpty() && et_time.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_video_time));
                } else if (!video_id.isEmpty() && ed_video_detail.getText().toString().trim().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.how_open_house_live_work));
                } else {
                    Subcatogery subcatogery = new Subcatogery();
                    subcatogery.setId(String.valueOf(profile_img_id));
                    subcatogery.setName("");
                    array_images.add(0,subcatogery);
                    for (int i = 0; i < array_images.size(); i++) {
                        if (i == 0)
                            str_image_id = array_images.get(i).getId();
                        else
                            str_image_id = str_image_id + "," + array_images.get(i).getId();
                    }
                    Log.e(TAG, "=========================: "+str_image_id);
                    if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                        Utility.ShowToastMessage(context, getString(R.string.message_guest));
                        startActivity(new Intent(context, LoginActivity.class));
                        finishAffinity();
                    } else {
                        if (str_discloser.contains("Other")) {
                            str_discloser = str_discloser.replace("Other", ed_other.getText().toString());
                        }
                        if (bundle.getString(Constants.BATHROOM, "").contains("Bathrooms"))
                            str_bathroom = bundle.getString(Constants.BATHROOM, "").replace("Bathrooms", "");
                        else
                            str_bathroom = bundle.getString(Constants.BATHROOM, "").replace("Bathroom", "");
                        if (bundle.getString(Constants.BADROOM, "").contains("Bedrooms"))
                            str_badroom = bundle.getString(Constants.BADROOM, "").replace("Bedrooms", "");
                        else
                            str_badroom = bundle.getString(Constants.BADROOM, "").replace("Bedroom", "");
                        FunUpload();
                    }
                }
                break;
        }
    }

    private void DeleteVideo() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Delete_Video(SharedPref.getSharedPreferences(context, Constants.TOKEN), video_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            video_id = "";
                            videofile = null;
                            delete_video.setVisibility(View.GONE);
                            imgplay.setVisibility(View.GONE);
                            imgpickvideo.setImageResource(0);
                            imgpickvideo.setBackground(getResources().getDrawable(R.drawable.videoadd));
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

    private void selectVideo() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_video);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        //takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(takeVideoIntent, 4);

                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    Intent intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 3);
                    // startActivityForResult(Intent.createChooser(intent,"Select Video"),3);
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void Fun_uploadVideo(File videoFile, File thumb_file) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart(Constants.VIDEO, videoFile.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), videoFile));
            builder.addFormDataPart(Constants.VIDEOTHUMB, thumb_file.getName(), RequestBody.create(MediaType.parse("image/*"), thumb_file));
            builder.addFormDataPart(Constants.VIDEOID, "");
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().Upload_Video(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            video_id = response.body().getUserData().getVedio_id();
                            //Log.e(TAG, "========servervideo_id: " + video_id);
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    private void UploadDisclosure(String type, final String manage_status, String str_disclosure_level) {
        RequestBody reqFile = null;
        MultipartBody.Part doc_file;
        if (Utility.isConnectingToInternet(context)) {
            Log.e(TAG, "====type: " + type + "manage_status" + manage_status + "str_disclosure_level" + str_disclosure_level);
            progressHUD = ProgressHUD.show(context, true, false, null);
            if (type.equalsIgnoreCase("image")) {
                reqFile = RequestBody.create(MediaType.parse("image/*"), uploadfile);
            } else {
                reqFile = RequestBody.create(MediaType.parse("application/pdf"), uploadfile);
            }
            doc_file = MultipartBody.Part.createFormData("file", uploadfile.getName(), reqFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), str_disclosure_level);
            RequestBody filetype = RequestBody.create(MediaType.parse("text/plain"), type);
            RetrofitClient.getAPIService().Disclosure_Upload(SharedPref.getSharedPreferences(context, Constants.TOKEN), doc_file, filename, filetype).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            UserData userData = response.body().getUserData();
                            String id = userData.getDiscloser_id();
                            disclosureArraylist.add(id);
                            handle(uploadfile.getName(), Integer.valueOf(id));
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

    private void FunUpload() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            String bid_price = min_bid_price.getText().toString().replace(",", "");
            bid_price = bid_price.replace(".", "");
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            // builder.addFormDataPart(Constants.TOKEN, SharedPref.getSharedPreferences(context,Constants.TOKEN));
            builder.addFormDataPart(Constants.FIRST_NAME, bundle.getString(Constants.FIRST_NAME, ""));
            builder.addFormDataPart(Constants.LAST_NAME, bundle.getString(Constants.LAST_NAME, ""));
            builder.addFormDataPart(Constants.MOBILE, bundle.getString(Constants.MOBILE, "").replace("-",""));
            builder.addFormDataPart(Constants.SELL_TYPE, bundle.getString(Constants.SELL_TYPE, ""));
            builder.addFormDataPart(Constants.PURPOSE, bundle.getString(Constants.PURPOSE, ""));
            builder.addFormDataPart(Constants.TYPE, bundle.getString(Constants.TYPE, ""));
            builder.addFormDataPart(Constants.LOCATION_LATITUDE, bundle.getString(Constants.LOCATION_LATITUDE, ""));
            builder.addFormDataPart(Constants.LOCATION_LONGITUDE, bundle.getString(Constants.LOCATION_LONGITUDE, ""));
            builder.addFormDataPart(Constants.START_DATE, start_date.getText().toString());
            builder.addFormDataPart(Constants.END_DATE, end_date.getText().toString());
            builder.addFormDataPart(Constants.START_TIME, start_time.getText().toString());
            builder.addFormDataPart(Constants.END_TIME, end_time.getText().toString());
            builder.addFormDataPart(Constants.BATHROOM,str_bathroom );
            builder.addFormDataPart(Constants.BADROOM, str_badroom);
            builder.addFormDataPart(Constants.SOCIETY, bundle.getString(Constants.SOCIETY, ""));
            builder.addFormDataPart(Constants.CITY, bundle.getString(Constants.CITY, ""));
            builder.addFormDataPart(Constants.AREA, bundle.getString(Constants.AREA, ""));
            builder.addFormDataPart(Constants.LOCATION, bundle.getString(Constants.LOCATION, ""));
            builder.addFormDataPart(Constants.PROPERTY_FOR, bundle.getString(Constants.PROPERTY_FOR, ""));
            builder.addFormDataPart(Constants.RENT_AMOUNT, bundle.getString(Constants.RENT_AMOUNT, ""));
            builder.addFormDataPart(Constants.PETS, bundle.getString(Constants.PETS, ""));
            builder.addFormDataPart(Constants.PETS_DETAIL, bundle.getString(Constants.PETS_DETAIL, ""));
            builder.addFormDataPart(Constants.SMOKING, bundle.getString(Constants.SMOKING, ""));
            builder.addFormDataPart(Constants.SMOKING_DETAIL, bundle.getString(Constants.SMOKING_DETAIL, ""));
            builder.addFormDataPart(Constants.PARKING, bundle.getString(Constants.PARKING, ""));
            builder.addFormDataPart(Constants.PARKING_DETAIL, bundle.getString(Constants.PARKING_DETAIL, ""));
            builder.addFormDataPart(Constants.MINIMUM_PRICE, bid_price);
            builder.addFormDataPart(Constants.DISCLOSER, str_discloser);
            builder.addFormDataPart(Constants.DESCRIPTION, ed_desc.getText().toString());
            builder.addFormDataPart(Constants.TRANSACTION_HISTORY, ed_trans.getText().toString());
            builder.addFormDataPart(Constants.VIDEO_DATE, start_date1.getText().toString());
            builder.addFormDataPart(Constants.VIDEOTIME, et_time.getText().toString());
            builder.addFormDataPart(Constants.POST, str_post);
            builder.addFormDataPart(Constants.VIDEO, video_id);
            builder.addFormDataPart(Constants.DISCLOSURE_ID, str_discloser_id);
            builder.addFormDataPart(Constants.FIXTURES, str_fixsure);
            builder.addFormDataPart(Constants.AGREEMENT_FILE, str_agreement_id);
            builder.addFormDataPart(Constants.CONTRACT_FILE, str_contract_id);
            builder.addFormDataPart(Constants.VIDEO_DESC, ed_video_detail.getText().toString());
            builder.addFormDataPart(Constants.COUNTERSTOP, str_counterstop);
            builder.addFormDataPart(Constants.PATIOS, str_patios);
            builder.addFormDataPart(Constants.WASHER_DRAWER, str_washer);
            builder.addFormDataPart(Constants.WOOD_FLORING, str_wood);
            builder.addFormDataPart(Constants.CARPET_FLORING, str_carpet);
            builder.addFormDataPart(Constants.CEILING_FAN, str_ceiling);
            builder.addFormDataPart(Constants.FIRE_PLACE, str_fier);
            builder.addFormDataPart(Constants.RENTER_TYPE, getIntent().getExtras().getString(Constants.RENTER_TYPE, ""));
          /*  builder.addFormDataPart(Constants.AGREEMENT_FILE, agreementfile.getName(), RequestBody.create(MediaType.parse("application/pdf"), agreementfile));
            builder.addFormDataPart(Constants.CONTRACT_FILE, contractfile.getName(), RequestBody.create(MediaType.parse("application/pdf"), contractfile));*/
            if (str_post.equalsIgnoreCase("both"))
                str_fixed_price = auction_fixed_price.getText().toString();
            else
                str_fixed_price = property_price.getText().toString();
            str_fixed_price = str_fixed_price.replace(",", "");
            str_fixed_price = str_fixed_price.replace(".", "");
            builder.addFormDataPart(Constants.FIXED_PRICE, str_fixed_price);
            builder.addFormDataPart(Constants.AUCTION_TYPE, str_auction);
            builder.addFormDataPart(Constants.IMAGEARRAY, str_image_id);
            /*for (int i = 0; i < filePaths.size(); i++) {
                File file = new File(filePaths.get(i));
                builder.addFormDataPart(Constants.IMAGEARRAY, file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            }*/
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().Upload_Property(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                          /*  startActivity(new Intent(context, MainActivity.class));
                            finish();*/
                            if (response.body().getMessage().equalsIgnoreCase("success")) {
                                FunNotification(response.body().getUserData().getProperty_id());
                                startActivity(new Intent(context, MainActivity.class));
                                finish();
                            }
                            //finish();
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    private void FunNotification(String property_id) {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().FunNotification(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {


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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }


    //----------------------select image-------------------------------------------------
    private void selectImage() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                 /*   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 1);*/
                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        Uri photoUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(pictureIntent, 1);
                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void selectimag() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.pick_doc), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.pick_documents);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                Uri photoUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", photoFile);
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(pictureIntent, 6);
                            }
                        } else {
                            requestCameraPermission();
                        }
                    } else {
                        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return;
                            }
                            Uri photoUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            startActivityForResult(pictureIntent, 6);
                        }
                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            ((Activity) context).startActivityForResult(intent, 7);
                        } else {
                            requestCameraPermission();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        ((Activity) context).startActivityForResult(intent, 7);
                    }

                } else if (options[item].equals(getString(R.string.pick_doc))) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword,application/pdf");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            startActivityForResult(intent, 8);
                        } else {
                            requestCameraPermission();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/msword,application/pdf");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(intent, 8);
                    }

                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void PickDate(final String str) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        strcurrdate = Utility.fun_dateFormat(mDay + "-" + (mMonth + 1) + "-" + mYear);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = "";
                        formatted_date = Utility.fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        if (str.equals("start_date")) {
                            start_date.setText(formatted_date);
                        } else if (str.equals("start_date1")) {
                            start_date1.setText(formatted_date);
                        } else if (str.equals("end_date")) {
                            end_date.setText(formatted_date);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    private void PickTime(final String str) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // currentTime = mHour + ":" + mMinute;
        boolean isPM = (mHour >= 12);
        // currentTime = String.format("%02d:%02d %s", (mHour == 12 || mHour == 0) ? 12 : mHour % 12, mMinute, isPM ? "PM" : "AM");
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        boolean isPM = (hourOfDay >= 12);
                        if (str.equals("start_time")) {
                            start_time.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        } else if (str.equals("end_time")) {
                            end_time.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        } else if (str.equals("et_time")) {
                            et_time.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SellPropertyNext.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(SellPropertyNext.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(SellPropertyNext.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(SellPropertyNext.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Bitmap b = decodeFile(photoFile);
                if (b != null) {
                    imageFilePath = photoFile.getAbsolutePath();
                } else {

                }
                file_pro_img = new File(imageFilePath);
                Fun_Upload(imageFilePath);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String path = null;
                try {
                    path = Utility.getFilePath(context, selectedImage);
                    file_pro_img = new File(path);
                    try {
                        file_pro_img = new Compressor(this).compressToFile(file_pro_img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                Fun_Upload(path);
            } else if (requestCode == 4) {
                videoUri = data.getData();
                try {
                    strvideofile = Utility.getFilePath(context, videoUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                videofile = new File(strvideofile);
                long length = Integer.parseInt(String.valueOf(videofile.length()));
                if (length > 40000000) {
                    Utility.ShowToastMessage(context, getString(R.string.file_size_limit));
                } else {
                    imgplay.setVisibility(View.VISIBLE);
                    delete_video.setVisibility(View.VISIBLE);
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                  /*  Glide.with(context)
                            .load(bitmap2)
                            .centerCrop()
                            //.placeholder(R.drawable.ic_video_place_holder)
                            .into(imgpickvideo);*/
                }
            } else if (requestCode == 3) {
                videoUri = data.getData();
                try {
                    strvideofile = Utility.getFilePath(context, videoUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                videofile = new File(strvideofile);
                long length = Integer.parseInt(String.valueOf(videofile.length()));
                if (length > 40000000) {
                    Utility.ShowToastMessage(context, getString(R.string.file_size_limit));
                } else {
                    imgplay.setVisibility(View.VISIBLE);
                    delete_video.setVisibility(View.VISIBLE);
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                }
            }
            if (requestCode == 6) {
                File file = new File(imageFilePath);
                uploadfile = file;
                UploadDisclosure("image", "Disclosure", str_disclosure_level);
            } else if (requestCode == 7) {
                Uri selectedImage = data.getData();
                String path = null;
                try {
                    path = Utility.getFilePath(context, selectedImage);
                    File file = new File(path);
                    uploadfile = file;
                    UploadDisclosure("image", "Disclosure", str_disclosure_level);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 8) {
                Uri fileuri = data.getData();
                String docFilePath = Utility.getFileNameByUri(this, fileuri);
                File file = new File(docFilePath);
                uploadfile = file;
                String extention = docFilePath.substring(docFilePath.lastIndexOf("."));
                if (extention.contains(".pdf") || extention.equalsIgnoreCase(".docx") || extention.contains(".xls")) {
                    UploadDisclosure(docFilePath.substring(docFilePath.lastIndexOf(".")), "Disclosure", str_disclosure_level);
                } else {
                    Utility.ShowToastMessage(context, getString(R.string.invalid_file));

                }
            }
        }

    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    private void Fun_Upload(final String path) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Part profile_img = null;
            if (file_pro_img != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file_pro_img);
                profile_img = MultipartBody.Part.createFormData("new_image", file_pro_img.getName(), reqFile);
            }
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().UploadImage(profile_img, SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            if (imageClick == 0) {
                                Subcatogery subcatogery = new Subcatogery();
                                subcatogery.setId(response.body().getUserData().getImage_id());
                                subcatogery.setName(path);
                                array_images.add(subcatogery);
                                imageAdapter.notifyDataSetChanged();
                            } else {
                                profile_img_id = Integer.valueOf(response.body().getUserData().getImage_id());
                                Glide.with(context).load(path).into(property_profile);

                            }

                        }else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }  else {
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

    private void handledelete() {
        if (clickdelete == 1) {
            ((RelativeLayout) findViewById(R.id.lay1)).setVisibility(View.GONE);
            // ((TextView)findViewById(R.id.docname1)).setText("");
            ((Button) findViewById(R.id.doc_attach1)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach1)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 2) {
            ((RelativeLayout) findViewById(R.id.lay2)).setVisibility(View.GONE);
            //((TextView)findViewById(R.id.docname2)).setText("");
            ((Button) findViewById(R.id.doc_attach2)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach2)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        } else if (clickdelete == 3) {
            ((RelativeLayout) findViewById(R.id.lay3)).setVisibility(View.GONE);
            //((TextView)findViewById(R.id.docname3)).setText("");
            ((Button) findViewById(R.id.doc_attach3)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach3)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 4) {
            ((RelativeLayout) findViewById(R.id.lay4)).setVisibility(View.GONE);
            // ((TextView)findViewById(R.id.docname4)).setText("");
            ((Button) findViewById(R.id.doc_attach4)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach4)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 5) {
            ((RelativeLayout) findViewById(R.id.lay5)).setVisibility(View.GONE);
            // ((TextView)findViewById(R.id.docname5)).setText("");
            ((Button) findViewById(R.id.doc_attach5)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach5)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 6) {
            ((RelativeLayout) findViewById(R.id.lay6)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach6)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname6)).setText("");
            ((Button) findViewById(R.id.doc_attach6)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 7) {
            ((RelativeLayout) findViewById(R.id.lay7)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach7)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname7)).setText("");
            ((Button) findViewById(R.id.doc_attach7)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 8) {
            ((RelativeLayout) findViewById(R.id.lay8)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach8)).setEnabled(true);
            //  ((TextView)findViewById(R.id.docname8)).setText("");
            ((Button) findViewById(R.id.doc_attach8)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 9) {
            ((RelativeLayout) findViewById(R.id.lay9)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach9)).setEnabled(true);
            //((TextView)findViewById(R.id.docname9)).setText("");
            ((Button) findViewById(R.id.doc_attach9)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 10) {
            ((RelativeLayout) findViewById(R.id.lay10)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach10)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname10)).setText("");
            ((Button) findViewById(R.id.doc_attach10)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 11) {
            ((RelativeLayout) findViewById(R.id.lay11)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach11)).setEnabled(true);
            //((TextView)findViewById(R.id.docname11)).setText("");
            ((Button) findViewById(R.id.doc_attach11)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 12) {
            ((RelativeLayout) findViewById(R.id.lay12)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach12)).setEnabled(true);
            ((Button) findViewById(R.id.doc_attach12)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 13) {
            ((RelativeLayout) findViewById(R.id.lay13)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach13)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname13)).setText("");
            ((Button) findViewById(R.id.doc_attach13)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 14) {
            ((RelativeLayout) findViewById(R.id.lay14)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach14)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname14)).setText("");
            ((Button) findViewById(R.id.doc_attach14)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else if (clickdelete == 15) {
            ((RelativeLayout) findViewById(R.id.lay15)).setVisibility(View.GONE);
            ((Button) findViewById(R.id.doc_attach15)).setEnabled(true);
            // ((TextView)findViewById(R.id.docname15)).setText("");
            ((Button) findViewById(R.id.doc_attach15)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private void handle(String name, int id) {
        if (click == 1) {
            ((RelativeLayout) findViewById(R.id.lay1)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.img1)).setTag(id);
            ((Button) findViewById(R.id.doc_attach1)).setEnabled(false);
            ((Button) findViewById(R.id.doc_attach1)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 2) {
            ((RelativeLayout) findViewById(R.id.lay2)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach2)).setEnabled(false);
            ((ImageView) findViewById(R.id.img2)).setTag(id);
            ((Button) findViewById(R.id.doc_attach2)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 3) {
            ((RelativeLayout) findViewById(R.id.lay3)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach3)).setEnabled(false);
            ((ImageView) findViewById(R.id.img3)).setTag(id);
            ((Button) findViewById(R.id.doc_attach3)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 4) {
            ((RelativeLayout) findViewById(R.id.lay4)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach4)).setEnabled(false);
            ((ImageView) findViewById(R.id.img4)).setTag(id);
            ((Button) findViewById(R.id.doc_attach4)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 5) {
            ((RelativeLayout) findViewById(R.id.lay5)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach5)).setEnabled(false);
            ((ImageView) findViewById(R.id.img5)).setTag(id);
            ((Button) findViewById(R.id.doc_attach5)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 6) {
            ((RelativeLayout) findViewById(R.id.lay6)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach6)).setEnabled(false);
            ((Button) findViewById(R.id.doc_attach6)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 7) {
            ((RelativeLayout) findViewById(R.id.lay7)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach7)).setEnabled(false);
            ((ImageView) findViewById(R.id.img7)).setTag(id);
            ((Button) findViewById(R.id.doc_attach7)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 8) {
            ((RelativeLayout) findViewById(R.id.lay8)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach8)).setEnabled(false);
            ((ImageView) findViewById(R.id.img8)).setTag(id);
            ((Button) findViewById(R.id.doc_attach8)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 9) {
            ((RelativeLayout) findViewById(R.id.lay9)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach9)).setEnabled(false);
            ((Button) findViewById(R.id.doc_attach9)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 10) {
            ((RelativeLayout) findViewById(R.id.lay10)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach10)).setEnabled(false);
            ((ImageView) findViewById(R.id.img10)).setTag(id);
            ((Button) findViewById(R.id.doc_attach10)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 11) {
            ((RelativeLayout) findViewById(R.id.lay11)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach11)).setEnabled(false);
            ((ImageView) findViewById(R.id.img11)).setTag(id);
            ((Button) findViewById(R.id.doc_attach11)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 12) {
            ((RelativeLayout) findViewById(R.id.lay12)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach12)).setEnabled(false);
            ((ImageView) findViewById(R.id.img12)).setTag(id);
            ((Button) findViewById(R.id.doc_attach12)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 13) {
            ((RelativeLayout) findViewById(R.id.lay13)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach13)).setEnabled(false);
            ((ImageView) findViewById(R.id.img13)).setTag(id);
            ((Button) findViewById(R.id.doc_attach13)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 14) {
            ((RelativeLayout) findViewById(R.id.lay14)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach14)).setEnabled(false);
            ((ImageView) findViewById(R.id.img14)).setTag(id);
            ((Button) findViewById(R.id.doc_attach14)).setBackgroundColor(getResources().getColor(R.color.fade));
        } else if (click == 15) {
            ((RelativeLayout) findViewById(R.id.lay15)).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.doc_attach15)).setEnabled(false);
            ((ImageView) findViewById(R.id.img15)).setTag(id);
            ((Button) findViewById(R.id.doc_attach15)).setBackgroundColor(getResources().getColor(R.color.fade));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (imageClick == 0 || imageClick == 2) {
                    selectImage();
                } else if (imageClick == 1) {
                    selectimag();
                } else {
                    selectVideo();
                }

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.permission_needed);
                builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).create().show();
            }
        }
    }

    @Override
    public void onBackPressed() {
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

    private void Delete_Disclosure(final String disclusure_id, final String type) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().DiSclosure_Delete(SharedPref.getSharedPreferences(context, Constants.TOKEN), disclusure_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            handledelete();
                            disclosureArraylist.remove(disclusure_id);
                            /*if (type.equalsIgnoreCase("")) {

                            } else if (type.equalsIgnoreCase("agreement")) {
                                str_agreement_id = "";
                                handledelete();
                            } else if (type.equalsIgnoreCase("Contract")) {
                                str_contract_id = "";
                                handledelete();
                            }*/


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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.check1:
                if (isChecked)
                    numberPicker_first.setVisibility(View.VISIBLE);
                else
                    numberPicker_first.setVisibility(View.GONE);
                break;
            case R.id.check2:
                if (isChecked)
                    numberPicker_second.setVisibility(View.VISIBLE);
                else
                    numberPicker_second.setVisibility(View.GONE);
                break;
            case R.id.check3:
                if (isChecked)
                    numberPicker_third.setVisibility(View.VISIBLE);
                else
                    numberPicker_third.setVisibility(View.GONE);
                break;
            case R.id.check4:
                if (isChecked)
                    numberPicker_four.setVisibility(View.VISIBLE);
                else
                    numberPicker_four.setVisibility(View.GONE);
                break;
            case R.id.check5:
                if (isChecked)
                    numberPicker_five.setVisibility(View.VISIBLE);
                else
                    numberPicker_five.setVisibility(View.GONE);
                break;
            case R.id.check6:
                if (isChecked)
                    numberPicker_six.setVisibility(View.VISIBLE);
                else
                    numberPicker_six.setVisibility(View.GONE);
                break;
            case R.id.rb_fixed:
                if (isChecked) {
                    idx = 0;
                    str_post = "fixed";
                    str_auction = "";
                    start_date.setText("");
                    end_date.setText("");
                    start_time.setText("");
                    end_time.setText("");
                    min_bid_price.setText("");
                    auction_fixed_price.setText("");
                    layoutfixed.setVisibility(View.VISIBLE);
                    card_auction.setVisibility(View.GONE);
                    lay_action_fixed_price.setVisibility(View.GONE);
                    //rb_auction.setChecked(false);
                    rb_both.setChecked(false);
                    // rb_oso.setChecked(false);
                    //rb_all.setChecked(false);
                }

                break;
            case R.id.rb_auction:
                if (isChecked) {
                    str_auction = "Public";
                    str_post = "auction";
                    idx = 1;
                    property_price.setText("");
                    auction_fixed_price.setText("");
                    layoutfixed.setVisibility(View.GONE);
                    card_auction.setVisibility(View.VISIBLE);
                    lay_action_fixed_price.setVisibility(View.GONE);
                    rb_fixed.setChecked(false);
                    rb_both.setChecked(false);
                    rb_oso.setChecked(false);
                    rb_all.setChecked(false);
                }

                break;
            case R.id.rb_both:
                if (isChecked) {
                    idx = 1;
                    str_auction = "Public";
                    str_post = "both";
                    property_price.setText("");
                    layoutfixed.setVisibility(View.GONE);
                    card_auction.setVisibility(View.VISIBLE);
                    lay_action_fixed_price.setVisibility(View.VISIBLE);
                    rb_auction.setChecked(false);
                    rb_oso.setChecked(false);
                    rb_fixed.setChecked(false);
                    rb_all.setChecked(false);
                }

                break;
            case R.id.rb_oso:
                if (isChecked) {
                    idx = 0;
                    str_post = "fixed"; //oso_offer
                    str_auction = "";
                    start_date.setText("");
                    end_date.setText("");
                    start_time.setText("");
                    end_time.setText("");
                    min_bid_price.setText("");
                    auction_fixed_price.setText("");
                    layoutfixed.setVisibility(View.VISIBLE);
                    card_auction.setVisibility(View.GONE);
                    lay_action_fixed_price.setVisibility(View.GONE);
                    rb_auction.setChecked(false);
                    rb_both.setChecked(false);
                    rb_fixed.setChecked(false);
                    rb_all.setChecked(false);
                }
                break;

            case R.id.rb_all:
                if (isChecked) {
                    idx = 1;
                    str_auction = "Public";
                    str_post = "both";
                    property_price.setText("");
                    layoutfixed.setVisibility(View.GONE);
                    card_auction.setVisibility(View.VISIBLE);
                    lay_action_fixed_price.setVisibility(View.VISIBLE);
                    rb_auction.setChecked(false);
                    rb_oso.setChecked(false);
                    rb_fixed.setChecked(false);
                    rb_both.setChecked(false);

                }

                break;
        }


    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());
        // photoFile = new File(photoFile);
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            b.compress(Bitmap.CompressFormat.PNG, 60, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    private class VideoCompressor extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(context, true, false, null);
            //progressHUD = ProgressHUD.show(context, true, false, null);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(strvideofile);
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
            if (compressed) {
                CompressvideoFile = new File(MediaController.cachedFile.getPath());
                File video_file = null;
                if (video_bitmap != null) {
                    video_file = persistImage(video_bitmap, "video_thumb");
                }
                Fun_uploadVideo(CompressvideoFile, video_file);
                Log.e("====Compression", "Compression successfully!");
                Log.e("====Compre File Path", "" + MediaController.cachedFile.getPath());
                Log.e("====Compressed length", MediaController.cachedFile.getPath().length() + "");
            } else {
                if (progressHUD != null && progressHUD.isShowing()) {
                    progressHUD.dismiss();
                }
                Utility.ShowToastMessage(context, getString(R.string.videofailed));
            }

        }
    }
    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
        Context context;
        ArrayList<Subcatogery> List;

        public ImageAdapter(Context context, ArrayList<Subcatogery> List) {
            this.context = context;
            this.List = List;
        }

        @Override
        public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, final int position) {
            Glide.with(context).load(List.get(position).getName()).into(holder.img_work);
            holder.img_delete.setOnClickListener(new ImageAdapter.OnMyClick(position));

        }

        @Override
        public int getItemCount() {
            return List.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView img_work, img_delete;

            public MyViewHolder(View itemView) {
                super(itemView);
                img_work = itemView.findViewById(R.id.img_work);
                img_delete = itemView.findViewById(R.id.img_delete);
            }
        }

        private class OnMyClick implements View.OnClickListener {
            int position;

            public OnMyClick(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.img_delete:
                        if (Utility.isConnectingToInternet(context)) {
                            progressHUD = ProgressHUD.show(context, true, false, null);
                            RetrofitClient.getAPIService().DeleteImage(SharedPref.getSharedPreferences(context, Constants.TOKEN), List.get(position).getId()).enqueue(new Callback<RetrofitUserData>() {
                                @Override
                                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                    if (progressHUD != null && progressHUD.isShowing()) {
                                        progressHUD.dismiss();
                                    }
                                    try {
                                        if (response.body().getStatus() == 200) {
                                            //array_images.remove(position);
                                            array_images.remove(position);
                                            imageAdapter.notifyDataSetChanged();

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

                        break;
                }

            }
        }
    }

}