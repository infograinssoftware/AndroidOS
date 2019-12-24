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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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
import com.open_source.R;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.MainActivity;
import com.open_source.activity.RentFilterActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatActivity.RESULT_CANCELED;
import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class RentMapFragement extends Fragment implements View.OnClickListener, OnMapReadyCallback, View.OnTouchListener {
    static final float COORDINATE_OFFSET = 0.00001f;
    private static final String TAG = MapFragment.class.getSimpleName();
    public ImageView search, icon_rent, icon_serach;
    public TextView  ctv_close, ctv_rent, toolbar_title, txvButtonCancelDrawing;
    public LinearLayout lay_search;
    public FloatingActionButton fab, fb_rent;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Context context;
    View rootView;
    MapView mapView;
    Marker mCurrLocationMarker;
    ArrayList<RetroObject> listMarker = new ArrayList<RetroObject>();
    double latitude, logitude;
    ProgressHUD progressHUD;
    Map<String, String> mMarkers = new HashMap<String, String>();
    Toolbar h_toolbar;
    Dialog dialog;
    BottomSheetBehavior mBottomSheetBehavior2;
    RelativeLayout fabLayout_rent, fabLayout_hs, fabLayout_raduis, fabLayout_location;
    boolean isFABOpen = false;
    private GoogleMap mMap;
    private LinearLayout lay_radius;
    private SupportMapFragment mapFragment;
    private Marker onTapLocationMarker;

    FrameLayout fram_map;
    boolean isDrawable = false;
    ArrayList<LatLng> val = new ArrayList<>();
    Polygon dtawnRoutePolygon;
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
        rootView = inflater.inflate(R.layout.fragment_rent_map, container, false);
        context = getActivity();
        init();
        return rootView;
    }

    public void init() {
        fram_map = rootView.findViewById(R.id.fram_map);
        txvButtonCancelDrawing = rootView.findViewById(R.id.txvButtonCancelDrawing);
        txvButtonCancelDrawing.setOnClickListener(this);

        h_toolbar = rootView.findViewById(R.id.h_toolbar);
        ctv_close = rootView.findViewById(R.id.ctv_close);
        toolbar_title = rootView.findViewById(R.id.toolbar_title);
        ctv_rent = rootView.findViewById(R.id.ctv_clear);
        icon_rent = rootView.findViewById(R.id.icon_rent);
        icon_rent.setOnClickListener(this);
        lay_search = rootView.findViewById(R.id.lay_search);
        icon_serach = rootView.findViewById(R.id.icon_serach);
        ctv_title_location_name = rootView.findViewById(R.id.title_location_name);
        ctv_close.setVisibility(View.GONE);
        ctv_rent.setText(R.string.list);
        toolbar_title.setText(R.string.rentel_property);
        ctv_rent.setOnClickListener(this);
        ctv_rent.setTextColor(getResources().getColor(R.color.black));
        ((ImageView) rootView.findViewById(R.id.icon_hs)).setOnClickListener(this);
        // don't recreate fragment everytime ensure last map location/state are maintained
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(this);
        }
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabLayout_hs = (RelativeLayout) rootView.findViewById(R.id.fabLayout_hs);
        fabLayout_rent = (RelativeLayout) rootView.findViewById(R.id.fabLayout_rent);
        fabLayout_raduis = (RelativeLayout) rootView.findViewById(R.id.fabLayout_raduis);
        fabLayout_location = rootView.findViewById(R.id.fabLayout_location);
        fb_rent = rootView.findViewById(R.id.fb_rent);
        fb_rent.setOnClickListener(this);
        ((FloatingActionButton) rootView.findViewById(R.id.fb_hs)).setOnClickListener(this);
        ((FloatingActionButton) rootView.findViewById(R.id.fb_radius)).setOnClickListener(this);
        ((FloatingActionButton) rootView.findViewById(R.id.fb_location)).setOnClickListener(this);
        final View bottomSheet2 = rootView.findViewById(R.id.bottom_sheet2);
        ((CardView) rootView.findViewById(R.id.cancle)).setOnClickListener(this);
        lay_radius = rootView.findViewById(R.id.lay_radius);
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
                    mMap.clear();
                    LatLng latLng = new LatLng(latitude, logitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
                    callNearBySellAPI(txt.getText().toString());
                }
            });
            lay_radius.addView(view);
        }
        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setHideable(true);
        mBottomSheetBehavior2.setPeekHeight(300);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.mapView, mapFragment).commit();
        lay_search.setOnClickListener(this);
        icon_serach.setOnClickListener(this);
        ctv_title_location_name.setOnClickListener(this);

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

        if (!SharedPref.getSharedPreferences(context, Constants.SHOWCASE_RENT).equals("show")) {
            ((MainActivity) context).ShowHint_rent(this);
        }

        if (SharedPref.getSharedPreferences(context, Constants.ADDRESS) != null) {
            ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
        }
        initialisePlacePicker();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //  mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (SharedPref.getSharedPreferences(context, Constants.ADDRESS) != null) {
            latitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LATITUDE));
            logitude = Double.parseDouble(SharedPref.getSharedPreferences(context, Constants.LONGITUDE));
            Log.e(TAG, "homelatitude: " + latitude + " logitude: " + logitude);
            LatLng latLng = new LatLng(Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LATITUDE)), Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LONGITUDE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
            callNearBySellAPI("");
        }

        final Geocoder geocoder = new Geocoder(context, Locale.getDefault());
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    Log.e(TAG, "onMapClick: "+geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0) );
                    ctv_title_location_name.setText(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0));
                    latitude = latLng.latitude;
                    logitude = latLng.longitude;
                    loadMap(latitude, logitude);
                    callNearBySellAPI("50");

                    if (onTapLocationMarker !=null){
                        onTapLocationMarker.remove();
                    }
                    onTapLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude))
                            .title(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(2000)
                            .strokeWidth(1.0f)
                            .strokeColor( getResources().getColor(R.color.colorPrimaryDark))
                            .fillColor(getResources().getColor(R.color.transparent_primary)));
                } catch (Exception e){
                    Log.e(TAG, "onMapClick: "+e.getMessage());
                }

            }
        });*/
    }

    private void callNearBySellAPI(String radius) {
        if (Utility.isConnectingToInternet(context)) {
            // progressHUD = ProgressHUD.show(context, true, false, null);
            Log.e(TAG, "latitude: " + latitude + " logitude: " + logitude);
            RetrofitClient.getAPIService().getNearBySellAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    String.valueOf(latitude), String.valueOf(logitude), "rent", radius).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    /*if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }*/
                    Log.e(TAG, response.toString());
                    try {
                        if (response.body().getStatus() == 200) {
                            listMarker = response.body().getObject();
                            Log.e(TAG, "onResponse: " + listMarker.size());
                            for (int i = 0; i < listMarker.size(); i++) {
                                LatLng point = new LatLng(Double.parseDouble(listMarker.get(i).getLatitude()) + i * COORDINATE_OFFSET, Double.parseDouble(listMarker.get(i).getLongitude()) + i * COORDINATE_OFFSET);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(point);
                                markerOptions.title(listMarker.get(i).getType() + "," + listMarker.get(i).getRenter_type());
                                markerOptions.snippet(listMarker.get(i).getLocation());
                                if (mMap != null) {
                                    if (listMarker.get(i).getRenter_type().equalsIgnoreCase(Constants.Str_VacationalRental)) {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_map));
                                    } else {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.light_blue_map));
                                    }

                                    Marker mkr = mMap.addMarker(markerOptions);
                                    /*Marker mkr = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(listMarker.get(i).getLatitude())
                                                    , Double.parseDouble(listMarker.get(i).getLongitude())))
                                            .anchor(0.5f, 0.5f)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))
                                            .title(listMarker.get(i).getType())
                                            .snippet(listMarker.get(i).getLocation()));*/
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
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else {
//                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    /*if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }*/
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
                            LatLng latLng = new LatLng(latitude, logitude);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
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
            case R.id.ctv_clear:
                getFragmentManager().beginTransaction().replace(R.id.container, RentListFragement.newInstance(latitude, logitude, ctv_title_location_name.getText().toString())).commit();
                break;

            case R.id.fb_location:
                closeFABMenu();
                loadMap(Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LATITUDE)), Double.valueOf(SharedPref.getSharedPreferences(context, Constants.LONGITUDE)));
                callNearBySellAPI("");
                break;

            case R.id.fb_rent:
                closeFABMenu();
                getFragmentManager().beginTransaction().replace(R.id.container, RentPropertyFragement.newInstance("Rent")).commit();
                break;

            case R.id.icon_serach:
                /*Log.e("====", String.valueOf(latitude));
                Log.e("====", String.valueOf(logitude));*/
                startActivity(new Intent(context, RentFilterActivity.class).
                        putExtra(Constants.LOCATION, ctv_title_location_name.getText().toString()).
                        putExtra(Constants.LATITUDE, String.valueOf(latitude)).
                        putExtra(Constants.LONGITUDE, String.valueOf(logitude)));
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
                            Log.e(TAG, "onClick: "+ e.getMessage());
                        }
                    }
                }*/
                break;

            case R.id.txvButtonCancelDrawing:
                if (dtawnRoutePolygon!=null) {
                    dtawnRoutePolygon.remove();
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
        }
    }

    public void Draw_Map() {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(getResources().getColor(R.color.colorPrimaryDark));
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(getResources().getColor(R.color.transparent_primary));
        dtawnRoutePolygon = mMap.addPolygon(rectOptions);
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
        if (dtawnRoutePolygon!=null)
            dtawnRoutePolygon.remove();
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


    private void loadMap(Double lat, Double logi) {
        mMap.clear();
        latitude = lat;
        logitude = logi;
        LatLng latLng = new LatLng(lat, logi);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
//        ctv_title_location_name.setText(SharedPref.getSharedPreferences(context, Constants.ADDRESS));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.e(TAG, "onActivityResult: "+place );
                ctv_title_location_name.setText(place.getAddress());
                String str_latLng = String.valueOf(place.getLatLng());
                str_latLng = str_latLng.replace("lat/lng: ", "");
                str_latLng = str_latLng.replace("(", "");
                str_latLng = str_latLng.replace(")", "");
                String[] array_latLng = str_latLng.split(",");
                latitude = Double.valueOf(array_latLng[0]);
                logitude = Double.valueOf(array_latLng[1]);
                LatLng latLng = new LatLng(latitude, logitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
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
        fabLayout_hs.setVisibility(View.VISIBLE);
        fabLayout_rent.setVisibility(View.VISIBLE);
        fabLayout_raduis.setVisibility(View.VISIBLE);
        fabLayout_location.setVisibility(View.VISIBLE);
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

        fabLayout_hs.animate().translationY(-getResources().getDimension(R.dimen.standard_145));
        fabLayout_rent.animate().translationY(-getResources().getDimension(R.dimen.standard_100));
        fabLayout_raduis.animate().translationY(-getResources().getDimension(R.dimen.standard_190));
        fabLayout_location.animate().translationY(-getResources().getDimension(R.dimen.standard_55));


    }

    private void closeFABMenu() {
        isFABOpen = false;
        //fabBGLayout.setVisibility(View.GONE);
        fab.animate().rotationBy(-180);
        fabLayout_hs.animate().translationY(0);
        fabLayout_raduis.animate().translationY(0);
        fabLayout_location.animate().translationY(0);
        fabLayout_rent.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    fabLayout_rent.setVisibility(View.GONE);
                    fabLayout_hs.setVisibility(View.GONE);
                    fabLayout_raduis.setVisibility(View.GONE);
                    fabLayout_location.setVisibility(View.GONE);
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

