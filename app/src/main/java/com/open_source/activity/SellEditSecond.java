package com.open_source.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
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
import androidx.fragment.app.Fragment;
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
import com.open_source.BuildConfig;
import com.open_source.R;
import com.open_source.adapter.DisclosureAdapter;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.Subcatogery;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.NonScrollListView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.open_source.videocompression.MediaController;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

public class SellEditSecond extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = SellEditSecond.class.getSimpleName();
    public static EditText ed_other;
    final int PERMISSION_REQUEST_CODE = 101;
    Context context;
    Toolbar toolbar;
    BottomSheetBehavior mBottomSheetBehavior2;
    ImageView imgpickvideo, imgplay, addvideo, delete_video, property_profile;
    String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    String str_discloser = "", str_post = "fixed", str_auction = "", video_id = "", str_fixed_price = "",
            imageFilePath = "", str_image_id = "", str_bathroom, str_badroom;
    File file_pro_img = null, CompressvideoFile = null, videofile = null, photoFile = null;
    int imageClick = -1, profile_img_id = -1;
    RadioButton rb_fixed, rb_auction, rb_both, rb_oso, rb_all;
    LinearLayout layoutfixed, lay_start_date, lay_start_date1, lay_end_date, lay_end_date1, lay_start_time, lay_end_time,
            lay_action_fixed_price, lay_dec_suggection, lay_trns_suggection, lay_pro_img;
    CardView card_auction;
    TextView start_date, end_date, start_date1, et_time, start_time, end_time;
    RadioGroup rg_price_type, rg_pubpvt;
    EditText property_price, ed_desc, ed_trans, min_bid_price, auction_fixed_price;
    NonScrollListView desclosurelist;
    DisclosureAdapter disclosureAdapter;
    ArrayList<String> rowlist = new ArrayList<>();
    ArrayList<String> array_suggestion = new ArrayList<>();
    ArrayList<Subcatogery> array_clipart = new ArrayList<>();
    ArrayList<Subcatogery> img_array_List = new ArrayList<>();
    RelativeLayout btn_upload;
    int idx = -1, idx_pubpvt = -1;
    Uri videoUri;
    Bitmap video_bitmap;
    String strcurrdate;
    Boolean rent_status = false;
    ProgressHUD progressHUD;
    Bundle bundle;
    Button upload_video;
    TextView toolbar_title;
    EditText ed_video_detail;
    RecyclerView recycle_pro_image;
    ImageAdapter imageAdapter;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String strvideofile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_properties_next);
        context = SellEditSecond.this;
        init();
    }

    @SuppressLint("ResourceType")
    private void init() {
        bundle = getIntent().getExtras();
        for (String key : bundle.keySet()) {
            Log.d("Bundle Debug", key + " = \"" + bundle.get(key) + "\"");
        }
        DisclosureAdapter.box1.clear();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.sell_property);
        ((ImageView) findViewById(R.id.info_icon)).setOnClickListener(this);
        delete_video = findViewById(R.id.delete_video);
        delete_video.setOnClickListener(this);
        imgpickvideo = findViewById(R.id.imgvideo);
        imgplay = findViewById(R.id.imgplay);
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
        property_profile = findViewById(R.id.property_profile);
        property_profile.setOnClickListener(this);
        //  rg_price_type = findViewById(R.id.rg_price_type);
        rg_pubpvt = findViewById(R.id.rg_pubpvt);
        property_price = findViewById(R.id.property_price);
        auction_fixed_price = findViewById(R.id.auction_fixed_price);
        ed_desc = findViewById(R.id.ed_desc);
        ed_other = findViewById(R.id.ed_other);
        ed_trans = findViewById(R.id.ed_trans);
        min_bid_price = findViewById(R.id.min_bid_price);
        btn_upload = findViewById(R.id.btn_upload);
        upload_video = findViewById(R.id.upload_video);
        addvideo = findViewById(R.id.addvideo);
        addvideo.setOnClickListener(this);
        desclosurelist = findViewById(R.id.desclosurelist);
        ed_video_detail = findViewById(R.id.ed_video_detail);
        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        start_date1.setOnClickListener(this);
        et_time.setOnClickListener(this);
        start_time.setOnClickListener(this);
        end_time.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
        imgpickvideo.setOnClickListener(this);
        upload_video.setOnClickListener(this);
        lay_dec_suggection = findViewById(R.id.lay_dec_suggection);
        lay_trns_suggection = findViewById(R.id.lay_trns_suggection);
        lay_pro_img = findViewById(R.id.lay_pro_img);
        ((CardView) findViewById(R.id.card_disclosure)).setVisibility(View.GONE);
        ((CardView) findViewById(R.id.card_fixtures)).setVisibility(View.GONE);
        recycle_pro_image = findViewById(R.id.recycle_pro_image);
        ((ImageView) findViewById(R.id.add_images)).setOnClickListener(this);
        SetData();
       /* SetData();
        rg_price_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                    case 2:
                        // rg_pubpvt.check(0);
                        idx = 1;
                        str_auction = "Public";
                        str_post = "both";
                        property_price.setText("");
                        layoutfixed.setVisibility(View.GONE);
                        card_auction.setVisibility(View.VISIBLE);
                        lay_action_fixed_price.setVisibility(View.VISIBLE);
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

    private void SetData() {
        rowlist.add("Has the property every been a crack house");
        rowlist.add("Is there asbestos in the duct work ?");
        rowlist.add("Has the basement ever leaked");
        rowlist.add("Is there flood area");
        rowlist.add("The fire is not in the area");
        rowlist.add("The earthquakes is not in the area");
        rowlist.add("Other");

        array_suggestion.add("House with open-concept kitchen");
        array_suggestion.add("This gorgeous and nearly perfect house will stun you with its modern and dazzling interior finishes.");
        array_suggestion.add("Welcome home! This bright and spacious multi-level home with an open floor plan");
        array_suggestion.add("House with vaulted ceilings & skylights bringing in tons of natural light");
        array_suggestion.add("Magnificent 4 bedroom, 4 bath home with 2,212 square feet in Centennial! Fantastic Park View");
        array_suggestion.add("walking distance to Cherry Creek schools.");
        array_suggestion.add("Neighborhood features a community pool and building park");
        array_suggestion.add("park with walking trails, and beautiful greenbelt for outdoor activities");

        array_clipart.add(new Subcatogery("164", "http://192.168.1.55//osrealstate//uploads//service_images//house-icon1.png"));
        array_clipart.add(new Subcatogery("165", "http://192.168.1.55//osrealstate//uploads//service_images//house-icon2.jpeg"));
        array_clipart.add(new Subcatogery("166", "http://192.168.1.55//osrealstate//uploads//service_images//house-icon3.jpeg"));
        array_clipart.add(new Subcatogery("167", "http://192.168.1.55//osrealstate//uploads//service_images//house-icon4.png"));

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
                    Subcatogery subcatogery = new Subcatogery();
                    subcatogery.setId(value[0]);
                    subcatogery.setName(value[1]);
                    img_array_List.add(subcatogery);
                    imageAdapter.notifyDataSetChanged();
                }
            });
            lay_pro_img.addView(view);
        }
        final View bottomSheet2 = findViewById(R.id.bottom_sheet2);
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString(Constants.PROPERTY_FOR, "").equalsIgnoreCase("2")) {
                rent_status = true;
                card_auction.setVisibility(View.GONE);
                layoutfixed.setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layout_type)).setVisibility(View.GONE);
                ((CardView) findViewById(R.id.card_disclosure)).setVisibility(View.GONE);
                toolbar_title.setText(R.string.title_rent_property);


            } else {
                if (bundle.getString(Constants.POST).equalsIgnoreCase("fixed")) {
                    idx = 0;
                    rb_fixed.setChecked(true);
                    // ((RadioButton) rg_price_type.getChildAt(0)).setChecked(true);
                    layoutfixed.setVisibility(View.VISIBLE);
                    card_auction.setVisibility(View.GONE);
                    lay_action_fixed_price.setVisibility(View.GONE);
                    property_price.setText(bundle.getString(Constants.FIXED_PRICE, ""));
                } else if (bundle.getString(Constants.POST).equalsIgnoreCase("both")) {
                    idx = 2;
                    rb_both.setChecked(true);
                    // ((RadioButton) rg_price_type.getChildAt(2)).setChecked(true);
                    layoutfixed.setVisibility(View.GONE);
                    card_auction.setVisibility(View.VISIBLE);
                    lay_action_fixed_price.setVisibility(View.VISIBLE);
                    start_date.setText(bundle.getString(Constants.START_DATE, ""));
                    end_date.setText(bundle.getString(Constants.END_DATE, ""));
                    start_time.setText(bundle.getString(Constants.START_TIME, ""));
                    end_time.setText(bundle.getString(Constants.END_TIME, ""));
                    auction_fixed_price.setText(bundle.getString(Constants.FIXED_PRICE, ""));
                    min_bid_price.setText(bundle.getString(Constants.MINIMUM_PRICE, ""));

                } else if (bundle.getString(Constants.POST).equalsIgnoreCase("auction")) {
                    idx = 1;
                    rb_auction.setChecked(true);
                    //  ((RadioButton) rg_price_type.getChildAt(1)).setChecked(true);
                    layoutfixed.setVisibility(View.GONE);
                    card_auction.setVisibility(View.VISIBLE);
                    lay_action_fixed_price.setVisibility(View.GONE);
                    start_date.setText(bundle.getString(Constants.START_DATE, ""));
                    end_date.setText(bundle.getString(Constants.END_DATE, ""));
                    min_bid_price.setText(bundle.getString(Constants.MINIMUM_PRICE, ""));
                    start_time.setText(bundle.getString(Constants.START_TIME, ""));
                    end_time.setText(bundle.getString(Constants.END_TIME, ""));
                }
                str_auction = bundle.getString(Constants.AUCTION_TYPE);
                if (bundle.getString(Constants.AUCTION_TYPE, "").equalsIgnoreCase("public")) {
                    ((RadioButton) rg_pubpvt.getChildAt(0)).setChecked(true);
                } else if (bundle.getString(Constants.AUCTION_TYPE, "").equalsIgnoreCase("private")) {
                    ((RadioButton) rg_pubpvt.getChildAt(1)).setChecked(true);
                }
            }

            ed_trans.setText(bundle.getString(Constants.TRANSACTION_HISTORY));
            ed_desc.setText(bundle.getString(Constants.DESCRIPTION));
            img_array_List = bundle.getParcelableArrayList(Constants.IMAGE);
            recycle_pro_image.setLayoutManager(new GridLayoutManager(context, 2));
            Log.e(TAG, "=========: "+img_array_List.get(0).getId() );
            profile_img_id=Integer.valueOf(img_array_List.get(0).getId());
            Glide.with(context).load(img_array_List.get(0).getName()).into(property_profile);
            imageAdapter = new ImageAdapter(context, img_array_List);
            recycle_pro_image.setAdapter(imageAdapter);
            str_post = bundle.getString(Constants.POST);
            ed_video_detail.setText(bundle.getString(Constants.VIDEO_DESC));
            video_id = bundle.getString(Constants.VIDEOID, "");
            if (!bundle.getString(Constants.VIDEO, "").isEmpty()) {
                try {
                    if (!bundle.getString(Constants.VIDEOTHUMB, "").isEmpty()) {
                        Glide.with(context).load(bundle.getString(Constants.VIDEOTHUMB, "")).into(imgpickvideo);
                        imgplay.setVisibility(View.VISIBLE);
                    } else {
                        AsyncTaskRunner runner = new AsyncTaskRunner();
                        runner.execute(bundle.getString(Constants.VIDEO), "");
                    }
                    // addvideo.setVisibility(View.VISIBLE);
                    start_date1.setText(bundle.getString(Constants.VIDEO_DATE));
                    et_time.setText(bundle.getString(Constants.VIDEO_TIME));
                    delete_video.setVisibility(View.VISIBLE);

                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
            /*if (bundle.getInt(Constants.BID_STATUS) == 1) {
                start_date.setEnabled(false);
                start_time.setEnabled(false);

            }*/
            DisclosureAdapter.box1.clear();
            disclosureAdapter = new DisclosureAdapter(context, rowlist, bundle.getString(Constants.DISCLOSER, ""));
            desclosurelist.setAdapter(disclosureAdapter);
        }
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
            case R.id.addvideo:
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
                if (!video_id.isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.video_delete));
                } else {
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
                                progressHUD = ProgressHUD.show(context, true, false, null);
                                File video_file = null;
                                if (video_bitmap != null) {
                                    video_file = persistImage(video_bitmap, "video_thumb");
                                }
                                Fun_uploadVideo(videofile, video_file);
                            }
                        }
                    }
                }

                break;

            case R.id.btn_upload:
                if (DisclosureAdapter.box1.size() > 0) {
                    str_discloser = android.text.TextUtils.join(",", DisclosureAdapter.box1);
                }
                if (idx == 0 && property_price.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_price));
                } else if (idx == 1 && start_date.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_date));
                } else if (idx == 1 && end_date.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_enddate));
                } else if (idx == 1 && min_bid_price.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_bid_price));
                } else if ((str_post.equalsIgnoreCase("both")) && (auction_fixed_price.getText().toString().trim().isEmpty() && rent_status == false)) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_pro_price));
                } else if (idx == 1 && Utility.compaireDate(start_date.getText().toString(), end_date.getText().toString()) < 0 && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.end_date_validation));
                } else if (idx == 1 && start_time.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_start_time));
                } else if (idx == 1 && end_time.getText().toString().isEmpty() && rent_status == false) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_end_time));
                } else if (ed_other.getText().toString().trim().isEmpty() && str_discloser.contains("Other")) {
                    Utility.ShowToastMessage(context, getString(R.string.val_other_disclosure));
                } else if (ed_desc.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_desc));
                } else if (ed_trans.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_tran));
                } else if (img_array_List.size() <= 0) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_error_image_upload));
                } else if (!video_id.isEmpty() && start_date1.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_video_view));
                } else if (!video_id.isEmpty() && et_time.getText().toString().isEmpty()) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_video_time));
                } else {
                    if (SharedPref.getSharedPreferences(context, Constants.TOKEN).equalsIgnoreCase("guest")) {
                        Utility.ShowToastMessage(context, getString(R.string.message_guest));
                        startActivity(new Intent(context, LoginActivity.class));
                        finishAffinity();
                    } else {
                        if (str_discloser.contains("Other")) {
                            str_discloser = str_discloser.replace("Other", ed_other.getText().toString());
                        }
                        Subcatogery subcatogery = new Subcatogery();
                        subcatogery.setId(String.valueOf(profile_img_id));
                        subcatogery.setName("");
                        img_array_List.add(0, subcatogery);
                        if (img_array_List.size() > 0) {
                            // Log.e(TAG, "=========: "+img_array_List.get(0).getId() );
                            for (int i = 0; i < img_array_List.size(); i++) {
                                if (i == 0)
                                    str_image_id = img_array_List.get(i).getId();
                                else
                                    str_image_id = str_image_id + "," + img_array_List.get(i).getId();
                            }
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
   /* private void VideoWarning() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Video Upload Alert")
                .setMessage("Are you sure you don't want to upload video?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        strupload = true;
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        strupload = false;
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }*/

    private void selectVideo() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.add_photo));
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
            builder.addFormDataPart(Constants.VIDEOID, bundle.getString(Constants.VIDEOID, ""));
            MultipartBody requestBody = builder.build();
              /*
              RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), videoFile);
              MultipartBody.Part body = MultipartBody.Part.createFormData(Constants.VIDEO, videoFile.getName(), requestFile);
              RequestBody videoid = RequestBody.create(MediaType.parse("text/plain"), bundle.getString(Constants.VIDEOID, ""));
              RequestBody token = RequestBody.create(MediaType.parse("text/plain"), SharedPref.);*/
            RetrofitClient.getAPIService().Upload_Video(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            video_id = response.body().getUserData().getVedio_id();
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

    private void FunUpload() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart(Constants.FIRST_NAME, bundle.getString(Constants.FIRST_NAME, ""));
            builder.addFormDataPart(Constants.PROPERTY_ID, bundle.getString(Constants.PROPERTY_ID, ""));
            builder.addFormDataPart(Constants.LAST_NAME, bundle.getString(Constants.LAST_NAME, ""));
            builder.addFormDataPart(Constants.MOBILE, bundle.getString(Constants.MOBILE, "").replace("-", ""));
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
            builder.addFormDataPart(Constants.LOCATION, bundle.getString(Constants.LOCATION, ""));
            builder.addFormDataPart(Constants.PROPERTY_FOR, bundle.getString(Constants.PROPERTY_FOR, ""));
            builder.addFormDataPart(Constants.RENT_AMOUNT, bundle.getString(Constants.RENT_AMOUNT, ""));
            builder.addFormDataPart(Constants.PETS, bundle.getString(Constants.PETS, ""));
            builder.addFormDataPart(Constants.PETS_DETAIL, bundle.getString(Constants.PETS_DETAIL, ""));
            builder.addFormDataPart(Constants.SMOKING, bundle.getString(Constants.SMOKING, ""));
            builder.addFormDataPart(Constants.SMOKING_DETAIL, bundle.getString(Constants.SMOKING_DETAIL, ""));
            builder.addFormDataPart(Constants.PARKING, bundle.getString(Constants.PARKING, ""));
            builder.addFormDataPart(Constants.PARKING_DETAIL, bundle.getString(Constants.PARKING_DETAIL, ""));
            builder.addFormDataPart(Constants.AREA, bundle.getString(Constants.AREA, ""));
            builder.addFormDataPart(Constants.MINIMUM_PRICE, min_bid_price.getText().toString());
            builder.addFormDataPart(Constants.DISCLOSER, bundle.getString(Constants.DISCLOSER, ""));
            builder.addFormDataPart(Constants.DESCRIPTION, ed_desc.getText().toString());
            builder.addFormDataPart(Constants.TRANSACTION_HISTORY, ed_trans.getText().toString());
            builder.addFormDataPart(Constants.VIDEO_DATE, start_date1.getText().toString());
            builder.addFormDataPart(Constants.VIDEOTIME, et_time.getText().toString());
            builder.addFormDataPart(Constants.POST, str_post);
            builder.addFormDataPart(Constants.VIDEO, video_id);
            builder.addFormDataPart(Constants.IMAGEID, str_image_id);
            builder.addFormDataPart(Constants.OPEN_HOUSE_DESC, ed_video_detail.getText().toString().trim());
            if (str_post.equalsIgnoreCase("both"))
                str_fixed_price = auction_fixed_price.getText().toString();
            else
                str_fixed_price = property_price.getText().toString();
            builder.addFormDataPart(Constants.FIXED_PRICE, str_fixed_price);
            builder.addFormDataPart(Constants.AUCTION_TYPE, str_auction);
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().Update_Property(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
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


    //----------------------select image-------------------------------------------------
    private void selectImage() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.add_photo));
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(SellEditSecond.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(SellEditSecond.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(SellEditSecond.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(SellEditSecond.this, requestedPermissions, PERMISSION_REQUEST_CODE);
                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            File getImage = context.getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } else {
            File getImage = context.getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        }
        return outputFileUri;
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
            } else if (requestCode == Crop.REQUEST_CROP) {
                //handleCrop(resultCode, data);
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
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                    /*Glide.with(context)
                            .load(videoUri)
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
                    video_bitmap = ThumbnailUtils.createVideoThumbnail(strvideofile, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    imgpickvideo.setImageBitmap(video_bitmap);
                    /*Glide.with(context)
                            .load(videoUri)
                            .centerCrop()
                            //.placeholder(R.drawable.ic_video_place_holder)
                            .into(imgpickvideo);*/
                }

            }
        }
    }

    private void Fun_Upload(final String str_path) {
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
                                subcatogery.setName(str_path);
                                img_array_List.add(subcatogery);
                                imageAdapter.notifyDataSetChanged();
                            } else {
                                profile_img_id = Integer.valueOf(response.body().getUserData().getImage_id());
                                Glide.with(context).load(str_path).into(property_profile);

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
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (imageClick == 0 || imageClick == 2)
                    selectImage();
                else
                    selectVideo();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.camera_permission_needed));
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

    public long compaireDate(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        long elapsed = 0;
        try {
            Date d1 = sdf.parse(startTime);
            Date d2 = sdf.parse(endTime);
            elapsed = d2.getTime() - d1.getTime();
            // System.out.println("d<><><>" + elapsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return elapsed;
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
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
                    rb_auction.setChecked(false);
                    rb_both.setChecked(false);
                    rb_oso.setChecked(false);
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
                File video_file = null;
                CompressvideoFile = new File(MediaController.cachedFile.getPath());
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

    private class AsyncTaskRunner extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            Log.e(TAG, "=======: " + params[0]);
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(params[0], new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(params[0]);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }

               /* int time = Integer.parseInt(params[0])*1000;
                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";*/
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imgpickvideo.setImageBitmap(bitmap);
                imgplay.setVisibility(View.VISIBLE);
            }
            // Log.e(TAG, "=======: "+bitmap.toString() );

        }

        @Override
        protected void onPreExecute() {
        }

    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
        Context context;
        ArrayList<Subcatogery> List;

        public ImageAdapter(Context context, ArrayList<Subcatogery> List) {
            this.context = context;
            this.List = List;
            if (this.List.size() > 0)
                this.List.remove(0);
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
                                            img_array_List.remove(position);
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
