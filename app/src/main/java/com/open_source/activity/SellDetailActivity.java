package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.open_source.R;
import com.open_source.adapter.ProImgAdapter;
import com.open_source.modal.DisclosreDoc;
import com.open_source.modal.PropertyImg;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellDetailActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {
    private static final String TAG = SellDetailActivity.class.getSimpleName();
    ProgressHUD progressHUD;
    List<RetroObject> sellList = new ArrayList<>();
    Toolbar toolbar;
    TextView toolbar_title;
    String property_id = "";
    String videolink = "";
    String discloser;
    RecyclerView image_recycle;
    List<PropertyImg> proList = new ArrayList<>();
    ArrayList<DisclosreDoc> array_doc;
    UserData userData = null;
    MenuItem EditItem, EditItem1;
    LayoutInflater layoutInflater;
    private RelativeLayout rel_disclosure;
    private ImageView video_thumb, property_img, img_disclosure;
    private Context context;
    private RecyclerView recyclerView;
    private ProImgAdapter proImgAdapter;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_detail);
        context = SellDetailActivity.this;
        layoutInflater = getLayoutInflater();
        init();
    }

    private void init() {
        Bundle b = getIntent().getExtras();
        if (b.containsKey(Constants.NOTIFICATION_ID)) {
            property_id = b.getString(Constants.ID, "");
        } else {
            property_id = b.getString(Constants.ID, "");
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (RecyclerView) findViewById(R.id.image_recycle);
        rel_disclosure = (RelativeLayout) findViewById(R.id.rel_disclosure);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.property_detail);
        ((Button) findViewById(R.id.btn_invite)).setOnClickListener(this);
        video_thumb = findViewById(R.id.video_thumb);
        video_thumb.setOnClickListener(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        image_recycle = findViewById(R.id.image_recycle);
        property_img = findViewById(R.id.property_img);
        img_disclosure = findViewById(R.id.img_disclosure);
        img_disclosure.setOnClickListener(this);
        if (property_id != null || !property_id.isEmpty()) {
            FunDetail();
        }
    }

    private void FunDetail() {
        if (Utility.isConnectingToInternet(context)) {
            //Log.e("======", property_id);
            //  Log.e("======", SharedPref.getSharedPreferences(context, Constants.TOKEN));
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().SellDetail(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            userData = response.body().getUserData();
                            ((TextView) findViewById(R.id.pro_status)).setText(userData.getSold_status());
                            ((TextView) findViewById(R.id.pro_area)).setText(userData.getArea_square());
                            ((TextView) findViewById(R.id.description)).setText(userData.getDiscription());
                            ((TextView) findViewById(R.id.user_fname)).setText(userData.getSeller_firstname());
                            ((TextView) findViewById(R.id.user_lname)).setText(userData.getSeller_lastname());
                            if (!userData.getBadroom().equalsIgnoreCase("0")) {
                                ((TextView) findViewById(R.id.pro_bedroom)).setText(userData.getBadroom());
                                ((TextView) findViewById(R.id.pro_bathroom)).setText(userData.getBathroom());
                            } else {
                                ((TextView) findViewById(R.id.pro_bedroom)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.pro_bathroom)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.bedroom)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.bathroom)).setVisibility(View.GONE);
                            }

                            ((TextView) findViewById(R.id.user_contact)).setText(userData.getSeller_contact());
                            ((TextView) findViewById(R.id.postlevel)).setText(userData.getPost());
                            ((TextView) findViewById(R.id.pro_post)).setText(userData.getPost());
                            ((TextView) findViewById(R.id.pro_purpose)).setText(userData.getPurpose());
                            if (userData.getProperty_for().equalsIgnoreCase("2")) {
                                ((TextView) findViewById(R.id.postlevel)).setVisibility(View.GONE);
                                ((RelativeLayout) findViewById(R.id.lay_sell)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.level_rent)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.rent_amount)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.sell_type)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.sell)).setVisibility(View.GONE);

                                if (!userData.getRent_amount().isEmpty()) {
                                    ((TextView) findViewById(R.id.rent_amount)).setText("$" + getFormatedAmount(Integer.parseInt(userData.getRent_amount())));
//                                    ((TextView) findViewById(R.id.rent_amount)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + getFormatedAmount(Integer.parseInt(userData.getRent_amount())));
                                }
                            } else if (userData.getPost().equalsIgnoreCase("fixed")) {
                                ((TextView) findViewById(R.id.start_date)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.date)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.end_date)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.enddate)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.start_time)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.starttime)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.end_time)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.endtime)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.min_bid_price)).setVisibility(View.GONE);
                                ((TextView) findViewById(R.id.bid)).setVisibility(View.GONE);
                                //EditItem1.setVisible(false);
                            } else {
                                if (userData.getPost().equalsIgnoreCase("auction"))
                                {
                                    ((TextView) findViewById(R.id.start_date)).setText(userData.getStart_date());
                                    ((TextView) findViewById(R.id.end_date)).setText(userData.getEnd_date());
                                    ((TextView) findViewById(R.id.start_time)).setText(userData.getStart_time());
                                    ((TextView) findViewById(R.id.end_time)).setText(userData.getEnd_time());
                                    ((TextView) findViewById(R.id.homeprice)).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.home_price)).setVisibility(View.GONE);
                                }
                                else if (userData.getPost().equalsIgnoreCase("both"))
                                {
                                    ((TextView) findViewById(R.id.start_date)).setText(userData.getStart_date());
                                    ((TextView) findViewById(R.id.end_date)).setText(userData.getEnd_date());
                                    ((TextView) findViewById(R.id.start_time)).setText(userData.getStart_time());
                                    ((TextView) findViewById(R.id.end_time)).setText(userData.getEnd_time());

                                }

                            }
                           /* if (!userData.getStart_date().isEmpty())
                                ((TextView) findViewById(R.id.start_date)).setText(userData.getStart_date());
                            if (!userData.getEnd_date().isEmpty())
                                ((TextView) findViewById(R.id.end_date)).setText(userData.getEnd_date());
                            if (!userData.getStart_time().isEmpty())
                                ((TextView) findViewById(R.id.start_time)).setText(userData.getStart_time());
                            if (!userData.getEnd_time().isEmpty())
                                ((TextView) findViewById(R.id.end_time)).setText(userData.getEnd_time());*/
                            ((TextView) findViewById(R.id.pro_type)).setText(userData.getType());
                            ((TextView) findViewById(R.id.sell_type)).setText(userData.getSell_type());
                            if (!userData.getMinimum_price().isEmpty())
                            ((TextView) findViewById(R.id.min_bid_price)).setText("$" + getFormatedAmount(Integer.parseInt(userData.getMinimum_price())));
//                            ((TextView) findViewById(R.id.min_bid_price)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + getFormatedAmount(Integer.parseInt(userData.getMinimum_price())));

                            if (!userData.getFixed_price().isEmpty())
                            ((TextView) findViewById(R.id.home_price)).setText("$" + getFormatedAmount(Integer.parseInt(userData.getFixed_price())));
//                            ((TextView) findViewById(R.id.home_price)).setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL) + " " + getFormatedAmount(Integer.parseInt(userData.getFixed_price())));

                            ((TextView) findViewById(R.id.trasection_history)).setText(userData.getTransaction_history());
                            if (userData.getAuction_type().equals("Private")) {
                                ((RelativeLayout) findViewById(R.id.relinvite)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.auctiontype)).setText(userData.getAuction_type());
                            }
                            mapFragment.getMapAsync((OnMapReadyCallback) context);
                            proList = userData.getProperty_img();
                            if (proList.size() > 0) {
                                Glide.with(context).load(proList.get(0).getFile_name()).into(property_img);
                                if (proList.size() > 1) {
                                    proImgAdapter = new ProImgAdapter(context, proList);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                    recyclerView.setAdapter(proImgAdapter);
                                } else {
                                    ((TextView) findViewById(R.id.level_img)).setVisibility(View.GONE);
                                }
                            }


                            videolink = userData.getVedio();
                            // videolink = "http://192.168.1.118/osrealstate/uploads/property_vedio/1529314704VIDEO_20180618_150732.mp4";
                            if (!userData.getVedio().isEmpty()) {
                                try {
                                    ((TextView) findViewById(R.id.level_video)).setVisibility(View.VISIBLE);
                                    ((FrameLayout) findViewById(R.id.layframe)).setVisibility(View.VISIBLE);
                                    if (!userData.getVideo_thumbnail().isEmpty()) {
                                        Glide.with(context).load(userData.getVideo_thumbnail()).into(video_thumb);
                                    } else {
                                        AsyncTaskRunner runner = new AsyncTaskRunner();
                                        runner.execute(videolink);
                                    }
                                    //AsyncTaskRunner runner = new AsyncTaskRunner();
                                    // runner.execute(videolink);
                                    //Bitmap bitmap = retriveVideoFrameFromVideo(videolink);


                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }

                            } else {
                                ((TextView) findViewById(R.id.level_video)).setVisibility(View.GONE);
                                ((FrameLayout) findViewById(R.id.layframe)).setVisibility(View.GONE);
                            }
                            discloser = userData.getDiscloser();
                            array_doc = userData.getDiscloser_files();
                            if (!discloser.isEmpty() || !userData.getFixsure_arr().getFixsure().isEmpty() || !userData.getFixsure_arr().getFireplace().isEmpty() || array_doc.size() > 0 || !userData.getFixsure_arr().getCarpet_flooring().isEmpty() || !userData.getFixsure_arr().getCeiling_fans().isEmpty() || !userData.getFixsure_arr().getCounterstop().isEmpty() || !userData.getFixsure_arr().getPrivate_patios().isEmpty() || !userData.getFixsure_arr().getWashers_dryers().isEmpty() || !userData.getFixsure_arr().getWood_flooring().isEmpty()) {
                                rel_disclosure.setVisibility(View.VISIBLE);
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
                    Utility.ShowToastMessage(context, getString(R.string.msg_server_failed));
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
    }

    public String getFormatedAmount(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

  /*  private void UpdateDate(final ArrayList<DisclosreDoc> arraydoc, List<String> arrayfixtures) {
        if (arraydoc.size() <= 0) {
            expand_doc.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < arraydoc.size(); i++) {
                View view = layoutInflater.inflate(R.layout.row_disclosure_buy_doc, recycle_doc, false);
                TextView txt = view.findViewById(R.id.ctv_name);
                if (arraydoc.get(i).getType().equalsIgnoreCase("pdf") || arraydoc.get(i).getType().equalsIgnoreCase(".pdf")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_pdf));
                } else if (arraydoc.get(i).getType().equalsIgnoreCase("doc") || arraydoc.get(i).getType().equalsIgnoreCase(".docx")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_doc));
                } else if (arraydoc.get(i).getType().equalsIgnoreCase("image")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_picture));
                } else {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_doc));
                }
                txt.setText(arraydoc.get(i).getName());
                final TextView text = new TextView(context);
                final TextView text1 = new TextView(context);
                text.setTag(i);
                text1.setTag(arraydoc.get(i).getType());
                // str_file = array_doc.get(i).getFile();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(text.getTag());
                        String type = String.valueOf(text1.getTag());
                        startActivity(new Intent(context, PdfViewActivity.class).putExtra(Constants.DISCLOSURE_DOC,arraydoc.get(Integer.valueOf(id)).getFile()).putExtra(Constants.TYPE, type));
                    }
                });
                recycle_doc.addView(view);
            }
        }
        if (arrayfixtures.size() <= 1) {
            expand_fixture.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < arrayfixtures.size(); i++) {
                View view = layoutInflater.inflate(R.layout.row_disclosure_layout, recycle_fixture, false);
                ((TextView) view.findViewById(R.id.ctv_name)).setText(String.valueOf(i + 1) + ". " + arrayfixtures.get(i));
                recycle_fixture.addView(view);
            }
        }
    }*/

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
            case R.id.bids:
                startActivity(new Intent(context, BidListActivity.class).putExtra(Constants.PROPERTY_ID, property_id));
                break;
            case R.id.edit_property:
                startActivity(new Intent(context, SellEditFirst.class).putExtra(Constants.PROPERTY_ID, property_id));
                break;
            case R.id.offer:
                startActivity(new Intent(context,OfferListActivtiy.class).putExtra(Constants.PROPERTY_ID, property_id));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pro_detail, menu);//Menu Resource, Menu
        EditItem = menu.findItem(R.id.edit_property);
        EditItem1 = menu.findItem(R.id.bids);
        if (userData != null) {
            if (userData.getPost().equalsIgnoreCase("fixed")) {
                EditItem1.setVisible(false);
            }
            if (userData.getIs_sold().equalsIgnoreCase("2") || userData.getIs_sold().equalsIgnoreCase("1")) {
                EditItem.setVisible(false);
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng position = new LatLng(Double.parseDouble(userData.getLocation_latitude()), Double.parseDouble(userData.getLocation_longitude()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(userData.getLocation());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_red));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_invite:
                startActivity(new Intent(context, InviteUserActivity.class).putExtra(Constants.PROPERTY_ID, property_id).putExtra(Constants.TYPE, "bid"));
                // startActivity(new Intent(context, InviteUserActivity.class));
                break;
            case R.id.video_thumb:
                startActivity(new Intent(context, VideoPlay.class).putExtra(Constants.VideoLink, videolink));
                break;
            case R.id.img_disclosure:
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) array_doc);
                args.putString(Constants.DISCLOSER, discloser.trim());
                args.putString(Constants.FIXTURES, userData.getFixsure_arr().getFixsure());
                args.putString(Constants.FIRE_PLACE, userData.getFixsure_arr().getFireplace());
                args.putString(Constants.CARPET_FLORING, userData.getFixsure_arr().getCarpet_flooring());
                args.putString(Constants.CEILING_FAN, userData.getFixsure_arr().getCeiling_fans());
                args.putString(Constants.COUNTERSTOP, userData.getFixsure_arr().getCounterstop());
                args.putString(Constants.PATIOS, userData.getFixsure_arr().getPrivate_patios());
                args.putString(Constants.WASHER_DRAWER, userData.getFixsure_arr().getWashers_dryers());
                args.putString(Constants.WOOD_FLORING, userData.getFixsure_arr().getWood_flooring());
                args.putString(Constants.STATUS, "View");
                args.putString(Constants.TYPE, "fixed");
                args.putString(Constants.PROPERTY_ID, userData.getProperty_id());
                startActivity(new Intent(context, DisclosureActivity.class).putExtras(args));
                break;
        }
    }

   /* public Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {

    }*/

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
                video_thumb.setImageBitmap(bitmap);
            }
            // Log.e(TAG, "=======: "+bitmap.toString() );

        }

        @Override
        protected void onPreExecute() {
        }

    }

    //--------------------------- Status Bar ------------------------------
}

