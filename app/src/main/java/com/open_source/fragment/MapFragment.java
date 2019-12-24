package com.open_source.fragment;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.open_source.LiveFeeds.NewsFeedsActivity;
import com.open_source.R;
import com.open_source.ServiceProvider.UserService;
import com.open_source.activity.FilterActivity;
import com.open_source.activity.LoanActivity;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.MainActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.adapter.SlidingImageAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.NotificationConstant;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED;
import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, View.OnTouchListener {
    static final float COORDINATE_OFFSET = 0.00001f;
    private static final String TAG = MapFragment.class.getSimpleName();
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    public LinearLayout lay_search;
    public ImageView search, icon_live_feeds, icon_hammer, icon_serach;
    public CardView card_search;
    public FloatingActionButton fab, fb_hs, fb_hammer, fb_news_feed, fb_radius, fb_location, fb_loan;
    Context context;
    View rootView;
    TextView txvButtonCancelDrawing;
    MapView mapView;

    ArrayList<RetroObject> listMarker = new ArrayList<RetroObject>();
    double latitude, logitude;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ProgressHUD progressHUD;
    Map<String, String> mMarkers = new HashMap<String, String>();
    Toolbar h_toolbar;
    Dialog dialog;
    //SliderLayout sliderLayout;
    ViewPager viewPager;
    BottomSheetBehavior mBottomSheetBehavior2;
    RelativeLayout fabLayout_hammer, fabLayout_hs, fabLayout_newfeed, fabLayout_raduis, fabLayout_location, fabLayout_loan;
    boolean isFABOpen = false;
    int liveFeed = 0, serviceProvider = 0;
    View bottomSheet2;
    ArrayList<String> arrayList_advertizement = new ArrayList<>();
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LinearLayout lay_radius;

    FrameLayout fram_map;
    ArrayList<LatLng> val = new ArrayList<>();
    boolean isDrawable = false;
    Polygon drawnRoutePolygon;
    ArrayList<Polyline> drawnLines = new ArrayList<>();
    LatLngBounds.Builder bounds = new LatLngBounds.Builder();
    Marker centerMarker;

    private AutoCompleteTextView ctv_title_location_name;
    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {    super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        bindview();
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }

        getChildFragmentManager().beginTransaction().replace(R.id.mapView, mapFragment).commit();
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
                    loadMap(latitude, logitude);
                    callNearBySellAPI(txt.getText().toString());
                }
            });
            lay_radius.addView(view);
        }

        load_advertizement();
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

        if (!SharedPref.getSharedPreferences(context, Constants.SHOWCASE).equals("show")) {
            ((MainActivity) context).ShowHint(this);
        }

        ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
        initialisePlacePicker();
    }

    private void bindview() {
        fram_map = rootView.findViewById(R.id.fram_map);
        txvButtonCancelDrawing = rootView.findViewById(R.id.txvButtonCancelDrawing);
        txvButtonCancelDrawing.setOnClickListener(this);

        h_toolbar = rootView.findViewById(R.id.toolbar);
        h_toolbar.setVisibility(View.GONE);
        lay_search = rootView.findViewById(R.id.lay_search);
        icon_serach = rootView.findViewById(R.id.icon_serach);
        ctv_title_location_name = rootView.findViewById(R.id.title_location_name);
        icon_live_feeds = rootView.findViewById(R.id.icon_live_feeds);
        icon_live_feeds.setOnClickListener(this);
        icon_hammer = rootView.findViewById(R.id.icon_hammer);
        icon_hammer.setOnClickListener(this);
        lay_search.setOnClickListener(this);
        icon_serach.setOnClickListener(this);
        bottomSheet2 = rootView.findViewById(R.id.bottom_sheet2);
        ((CardView) rootView.findViewById(R.id.cancle)).setOnClickListener(this);
        lay_radius = rootView.findViewById(R.id.lay_radius);
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        ((ImageView) rootView.findViewById(R.id.icon_hs)).setOnClickListener(this);
        card_search = rootView.findViewById(R.id.card_search);
        viewPager = rootView.findViewById(R.id.imageSlider);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabLayout_hammer = (RelativeLayout) rootView.findViewById(R.id.fabLayout_hammer);
        fabLayout_hs = (RelativeLayout) rootView.findViewById(R.id.fabLayout_hs);
        fabLayout_newfeed = (RelativeLayout) rootView.findViewById(R.id.fabLayout_newfeed);
        fabLayout_raduis = (RelativeLayout) rootView.findViewById(R.id.fabLayout_raduis);
        fabLayout_loan = (RelativeLayout) rootView.findViewById(R.id.fabLayout_loan);
        //fabLayout_draw = (RelativeLayout) rootView.findViewById(R.id.fabLayout_draw);
        fabLayout_location = rootView.findViewById(R.id.fabLayout_location);
        fb_hs = rootView.findViewById(R.id.fb_hs);
        fb_hs.setOnClickListener(this);

        fb_location = rootView.findViewById(R.id.fb_location);
        fb_location.setOnClickListener(this);

        fb_hammer = rootView.findViewById(R.id.fb_hammer);
        fb_hammer.setOnClickListener(this);

        fb_news_feed = rootView.findViewById(R.id.fb_news_feed);
        fb_news_feed.setOnClickListener(this);

        fb_radius = rootView.findViewById(R.id.fb_radius);
        fb_radius.setOnClickListener(this);

        fb_loan = rootView.findViewById(R.id.fb_loan);
        fb_loan.setOnClickListener(this);
        ctv_title_location_name.setOnClickListener(this);
    }

    public void Draw_Map() {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(getResources().getColor(R.color.colorPrimaryDark));
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(getResources().getColor(R.color.transparent_primary));
        drawnRoutePolygon = mMap.addPolygon(rectOptions);
        txvButtonCancelDrawing.setVisibility(View.VISIBLE);
    }

    private void drawPolyLine() {
        for (int i = 0; i < val.size(); i++) {
            drawnLines.add( mMap.addPolyline(new PolylineOptions()
                    .addAll(val)
                    .width(5)
                    .color(Color.RED)));
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (drawnRoutePolygon !=null)
            drawnRoutePolygon.remove();
        if (drawnLines.size()!=0){
            for (int i = 0; i<drawnLines.size(); i++){
                drawnLines.get(i).remove();
            }
        }
        if (centerMarker!=null)
            centerMarker.remove();

        float x = event.getX();
        float y = event.getY();
        int x_co = Math.round(x);
        int y_co = Math.round(y);
        mMap.getProjection();
        Point x_y_points = new Point(x_co, y_co);
        LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
        latitude = latLng.latitude;
        logitude = latLng.longitude;
        int eventaction = event.getAction();
        switch (eventaction) {
            case MotionEvent.ACTION_DOWN:
                // finger touches the screen
                val.add(new LatLng(latitude, logitude));

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                val.add(new LatLng(latitude, logitude));

            case MotionEvent.ACTION_UP:
                // finger leaves the screen
                drawPolyLine();
                break;
        }
        return false;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    private void loadMap(Double lat, Double logi) {
        mMap.clear();
        latitude = lat;
        logitude = logi;
        LatLng latLng = new LatLng(lat, logi);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    private void load_advertizement() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().Advertizement(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            for (int i = 0; i < response.body().getUserData().getAdvertisment_list().size(); i++) {
                                if (getActivity() != null) {
                                    arrayList_advertizement.add(response.body().getUserData().getAdvertisment_list().get(i).getImage());
                                }
                            }
                            viewPager.setAdapter(new SlidingImageAdapter(context, arrayList_advertizement, "advertizement"));
                            TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabDots);
                            tabLayout.setupWithViewPager(viewPager, true);
                            NUM_PAGES = arrayList_advertizement.size();
                            // Auto start of viewpager
                            starttimer();
                            for (int i = 0; i < response.body().getUserData().getNotification_list().size(); i++) {
                                if (response.body().getUserData().getNotification_list().get(i).getNoti_type().equals(NotificationConstant.LikeNotification) || response.body().getUserData().getNotification_list().get(i).getNoti_type().equals(NotificationConstant.CommentNotification)) {
                                    liveFeed = liveFeed + response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                }
                                if (response.body().getUserData().getNotification_list().get(i).getNoti_type().equals(NotificationConstant.MilestoneRequest) || response.body().getUserData().getNotification_list().get(i).getNoti_type().equals(NotificationConstant.BidOnProject)) {
                                    serviceProvider = serviceProvider + response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                }
                            }
                            if (liveFeed > 0) {
                                ((TextView) rootView.findViewById(R.id.txt_feeds)).setVisibility(View.VISIBLE);
                                ((TextView) rootView.findViewById(R.id.txt_feeds)).setText(String.valueOf(liveFeed));
                            }
                            if (serviceProvider > 0) {
                                ((TextView) rootView.findViewById(R.id.txt_hammer)).setVisibility(View.VISIBLE);
                                ((TextView) rootView.findViewById(R.id.txt_hammer)).setText(String.valueOf(serviceProvider));
                            }
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        }
    }

    private void starttimer() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Log.e(TAG, "==========: "+SharedPref.getSharedPreferences(context, Constants.ADDRESS));
        if (!SharedPref.getSharedPreferences(context, Constants.ADDRESS).isEmpty()) {
            latitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LATITUDE));
            logitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LONGITUDE));
            // Log.e(TAG, "homelatitude: " + latitude + " logitude: " + logitude);
            loadMap(latitude, logitude);
            callNearBySellAPI("");
        }
    }

    private void callNearBySellAPI(String radius) {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().getNearBySellAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    String.valueOf(latitude), String.valueOf(logitude), "sale", radius).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    Log.e(TAG, response.toString());
                    try {
                        if (response.body().getStatus() == 200) {
                            listMarker = response.body().getObject();
                            Log.e(TAG, "onResponse: " + listMarker.size());
                            for (int i = 0; i < listMarker.size(); i++) {
                                LatLng point = new LatLng(Double.parseDouble(listMarker.get(i).getLatitude()) + i * COORDINATE_OFFSET, Double.parseDouble(listMarker.get(i).getLongitude()) + i * COORDINATE_OFFSET);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(point);
                                //markerOptions.anchor(0.5f,1.0f);
                                markerOptions.title(listMarker.get(i).getPurpose() + "," + listMarker.get(i).getType());
                                markerOptions.snippet(listMarker.get(i).getLocation());
                                if (mMap != null) {
                                    if (listMarker.get(i).getPurpose().equalsIgnoreCase("For Sell")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
                                    } else if (listMarker.get(i).getPurpose().equalsIgnoreCase("Wanted")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_map));
                                    } else if (listMarker.get(i).getPurpose().equalsIgnoreCase("Foreclosure Sell")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_map));
                                    } else if (listMarker.get(i).getPurpose().equalsIgnoreCase("Short Sell")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_map));
                                    } else if (listMarker.get(i).getPurpose().equalsIgnoreCase("For Sell By Owner")) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_map));
                                    } else {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon));
                                        //Log.e(TAG, "=======: " + "else");
                                    }

                                    Marker mkr = mMap.addMarker(markerOptions);
                                    String value = listMarker.get(i).getId();
                                    mMarkers.put(mkr.getId(), value);

                                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                        @Override
                                        public void onInfoWindowClick(Marker marker) {
                                            String value = mMarkers.get(marker.getId());
                                            //String array[]=value.split(",");
                                            if (value != null)
                                                startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, value));
                                        }
                                    });
                                    LatLng latLng = new LatLng(latitude, logitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                                }
                            }
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else {
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
            Utility.ShowToastMessage(context, R.string.internet_connection);
        }
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
                            ctv_title_location_name.setText(place.getAddress());
                            String str_latLng = String.valueOf(place.getLatLng());
                            str_latLng = str_latLng.replace("lat/lng: ", "");
                            str_latLng = str_latLng.replace("(", "");
                            str_latLng = str_latLng.replace(")", "");
                            String[] array_latLng = str_latLng.split(",");
                            latitude = Double.valueOf(array_latLng[0]);
                            logitude = Double.valueOf(array_latLng[1]);
                            loadMap(latitude, logitude);
                            Log.i("====", "address: " + place.getAddress());
                            callNearBySellAPI("");
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
    public void onClick(View view) {
        switch (view.getId()) {
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

            case R.id.cancle:
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.icon_serach:
                startActivity(new Intent(context, FilterActivity.class).
                        putExtra(Constants.LOCATION, ctv_title_location_name.getText().toString()).
                        putExtra(Constants.LATITUDE, String.valueOf(latitude)).
                        putExtra(Constants.LONGITUDE, String.valueOf(logitude)));
                break;

            case R.id.fb_hammer:
                closeFABMenu();
                ((TextView) rootView.findViewById(R.id.txt_hammer)).setVisibility(View.GONE);
                if (serviceProvider > 0)
                    Utility.ReadNoti(context, NotificationConstant.BidOnProject + "," + NotificationConstant.MilestoneRequest);
                startActivity(new Intent(context, UserService.class));
                break;

            case R.id.fb_location:
                closeFABMenu();
                loadMap(Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LATITUDE)), Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LONGITUDE)));
                callNearBySellAPI("");
                break;

            case R.id.fb_news_feed:
                closeFABMenu();
                ((TextView) rootView.findViewById(R.id.txt_feeds)).setVisibility(View.GONE);
                if (liveFeed > 0)
                    Utility.ReadNoti(context, NotificationConstant.LikeNotification + "," + NotificationConstant.CommentNotification + "," + NotificationConstant.FollowRequest + "," + NotificationConstant.FollowRequestAccept);
                startActivity(new Intent(context, NewsFeedsActivity.class));
                break;

            case R.id.fb_hs:
                closeFABMenu();
                showDialog();
                /*if (!isDrawable ) {
                    isDrawable = true;
                    mMap.getUiSettings().setScrollGesturesEnabled(false);
                    fram_map.setOnTouchListener(this);
                } else {
                    isDrawable = false;
                    mMap.getUiSettings().setScrollGesturesEnabled(true);
                    fram_map.setOnTouchListener(null);
                    if (val.size()!=0) {
                        for (int i=0; i<val.size();i++){
                            bounds.include(val.get(i));
                        }
                        try {
                            latitude = bounds.build().getCenter().latitude;
                            logitude = bounds.build().getCenter().longitude;
                            Log.e(TAG, "onClick: "+ latitude+" "+logitude);
                            loadMap(latitude, logitude);

                            Draw_Map();
                            closeFABMenu();
                            new GetNameFromLocation().execute(mMap.getCameraPosition().zoom);

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }*/
                break;

            case R.id.txvButtonCancelDrawing:
                if (drawnRoutePolygon !=null) {
                    drawnRoutePolygon.remove();
                    for (int i = 0; i<drawnLines.size(); i++){
                        drawnLines.get(i).remove();
                    }
                    centerMarker.remove();
                    bounds = new LatLngBounds.Builder();
                    val = new ArrayList<>();
                    txvButtonCancelDrawing.setVisibility(View.GONE);
                }
                break;

            case R.id.fb_radius:
                closeFABMenu();
                if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else if (mBottomSheetBehavior2.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                break;

            case R.id.fb_loan:
                startActivity(new Intent(context, LoanActivity.class));
                break;

        }
    }

    public void showDialog() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_alert);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        ((TextView) dialog.findViewById(R.id.detail)).setText(R.string.msg_hs);

        ((TextView) dialog.findViewById(R.id.btn_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HS_WEB)));
            }
        });
        ((TextView) dialog.findViewById(R.id.btn_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
                loadMap(latitude, logitude);
                Log.i("====", "address: " + place.getAddress());
                callNearBySellAPI("");

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                // TODO: Handle the error.
                //Log.i("====", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) {
            try {
                mapView.onDestroy();
            } catch (NullPointerException e) {
                Log.e(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fabLayout_hammer.setVisibility(View.VISIBLE);
        fabLayout_hs.setVisibility(View.VISIBLE);
        fabLayout_newfeed.setVisibility(View.VISIBLE);
        fabLayout_raduis.setVisibility(View.VISIBLE);
        fabLayout_loan.setVisibility(View.VISIBLE);
        fabLayout_location.setVisibility(View.VISIBLE);
      //  fabLayout_draw.setVisibility(View.VISIBLE);
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
        fabLayout_hammer.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout_hs.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout_newfeed.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        fabLayout_raduis.animate().translationY(-getResources().getDimension(R.dimen.standard_235));
        fabLayout_loan.animate().translationY(-getResources().getDimension(R.dimen.standard_280));
    //    fabLayout_draw.animate().translationY(-getResources().getDimension(R.dimen.standard_330));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab.animate().rotationBy(-180);
        fabLayout_hammer.animate().translationY(0);
        fabLayout_hs.animate().translationY(0);
        fabLayout_newfeed.animate().translationY(0);
        fabLayout_location.animate().translationY(0);
        fabLayout_raduis.animate().translationY(0);
        fabLayout_loan.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout_hammer.setVisibility(View.GONE);
                    fabLayout_hs.setVisibility(View.GONE);
                    fabLayout_newfeed.setVisibility(View.GONE);
                    fabLayout_raduis.setVisibility(View.GONE);
                    fabLayout_loan.setVisibility(View.GONE);
                    fabLayout_location.setVisibility(View.GONE);
                    //fabLayout_draw.setVisibility(View.GONE);
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

    private double distance(LatLng fromLocationLatLng, LatLng toLocationLatLng) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(fromLocationLatLng.latitude);
        startPoint.setLongitude(fromLocationLatLng.longitude);
        Location endPoint = new Location("locationA");
        endPoint.setLatitude(toLocationLatLng.latitude);
        endPoint.setLongitude(toLocationLatLng.longitude);
        double distance = startPoint.distanceTo(endPoint);
        return (distance / 1000);
    }

    public class GetNameFromLocation extends AsyncTask<Float, String, String>{
        float zoomLevel;
        double searchRadius;
        @Override
        protected String doInBackground(Float... floats) {
            zoomLevel = floats[0];
            String result = "";
            try {
                result =  new Geocoder(getContext(), Locale.getDefault()).getFromLocation(latitude, logitude, 1).get(0).getAddressLine(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            ctv_title_location_name.setText(s);
            centerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, logitude))
                    .title(s)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            for (int i=0;i<val.size(); i++){
                double value = distance(new LatLng(latitude,logitude), val.get(i));
                if (i==0){
                    searchRadius = distance(new LatLng(latitude,logitude), val.get(i));
                } else if (value < searchRadius){
                    searchRadius = value;
                }
            }
            Log.e(TAG, "onPostExecute: "+searchRadius );
            callNearBySellAPI(String.valueOf(Math.round(searchRadius)));

            bounds = new LatLngBounds.Builder();
            val = new ArrayList<>();
            super.onPostExecute(s);
        }
    }
}
