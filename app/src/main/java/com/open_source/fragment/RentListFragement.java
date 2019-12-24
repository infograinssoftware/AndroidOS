package com.open_source.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.open_source.R;
import com.open_source.activity.RentFilterActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.adapter.AutoCompleteAdapter;
import com.open_source.adapter.PostListAdapter;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.modal.UserList;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.App;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentListFragement extends Fragment implements View.OnClickListener {
    static double loc_latitude, loc_logitude;
    static String img, loc_address = "";
    View rootview;
    Toolbar h_toolbar;
    ImageView search, icon_rent,icon_serach;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Context context;
    ProgressHUD progressHUD;
    List<UserList> savedSearchesList = new ArrayList<>();
    ArrayList<RetroObject> ObjectList = new ArrayList<>();
    PostListAdapter postListAdapter;
    RecyclerView recyclerView;
    TextView ctv_close, ctv_rent, toolbar_title;

    private AutoCompleteTextView ctv_title_location_name;
    public AutoCompleteAdapter autoCompleteAdapter;
    PlacesClient placesClient;

    public static RentListFragement newInstance(Double latitude, Double logitude, String address) {
        RentListFragement rentListFragement = new RentListFragement();
        loc_latitude = latitude;
        loc_logitude = logitude;
        loc_address = address;
        Bundle args = new Bundle();
        rentListFragement.setArguments(args);
        return rentListFragement;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootview = inflater.inflate(R.layout.fragement_rent_list, container, false);
        context = getActivity();
        init();
        return rootview;
    }

    private void init() {
        h_toolbar = rootview.findViewById(R.id.h_toolbar);
        ctv_close = rootview.findViewById(R.id.ctv_close);
        toolbar_title = rootview.findViewById(R.id.toolbar_title);
        ctv_rent = rootview.findViewById(R.id.ctv_clear);
        search = rootview.findViewById(R.id.search);
        icon_rent = rootview.findViewById(R.id.icon_rent);
        icon_rent.setOnClickListener(this);
        icon_serach = rootview.findViewById(R.id.icon_serach);
        icon_serach.setOnClickListener(this);
        ctv_title_location_name = rootview.findViewById(R.id.title_location_name);
        ctv_close.setVisibility(View.GONE);
        ctv_rent.setText(R.string.map);
        toolbar_title.setText(R.string.rentel_property);
        ctv_rent.setOnClickListener(this);
        search.setOnClickListener(this);
        ctv_rent.setTextColor(getResources().getColor(R.color.black));
        ctv_title_location_name.setText(loc_address);
        recyclerView = rootview.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FunLoadData();
        initialisePlacePicker();
    }

    private void FunLoadData() {
        if (Utility.isConnectingToInternet(context)) {
            savedSearchesList.clear();
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().getNearBySellAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    String.valueOf(loc_latitude), String.valueOf(loc_logitude), "rent", "").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            ObjectList = response.body().getObject();
                            for (int i = 0; i < ObjectList.size(); i++) {
                                if (ObjectList.get(i).getProperty_img().size() != 0) {
                                    img = ObjectList.get(i).getProperty_img().get(0).getFile_name();
                                } else {
                                    img = "";
                                }
                                savedSearchesList.add(new UserList(ObjectList.get(i).getId(), img, ObjectList.get(i).getType(), ObjectList.get(i).getLocation(), ObjectList.get(i).getPurpose()));
                            }
                            postListAdapter = new PostListAdapter(context, savedSearchesList, "Rent");
                            recyclerView.setAdapter(postListAdapter);

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else {
                            postListAdapter = new PostListAdapter(context, savedSearchesList, "Rent");
                            recyclerView.setAdapter(postListAdapter);
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
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }
    }

    private void initialisePlacePicker(){
        if (!Places.isInitialized()) {
            Places.initialize(App.getContext(), getResources().getString(R.string.mapkey));
        }
        placesClient = Places.createClient(getContext());
        initAutoCompleteTextView();
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
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Place place = task.getPlace();
                            ctv_title_location_name.setText(place.getAddress());
                            String str_latLng = String.valueOf(place.getLatLng());
                            str_latLng = str_latLng.replace("lat/lng: ", "");
                            str_latLng = str_latLng.replace("(", "");
                            str_latLng = str_latLng.replace(")", "");
                            String[] array_latLng = str_latLng.split(",");
                            loc_latitude = Double.valueOf(array_latLng[0]);
                            loc_logitude = Double.valueOf(array_latLng[1]);
                            FunLoadData();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                /*if (Utility.checkPlayServices(context)) {
                    try {
                        startActivityForResult(new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity()), PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }*/
                break;
            case R.id.icon_rent:
                getFragmentManager().beginTransaction().replace(R.id.container, RentPropertyFragement.newInstance("Rent")).commit();
                break;
            case R.id.ctv_clear:
                getFragmentManager().beginTransaction().replace(R.id.container, new RentMapFragement()).commit();
                break;

            case R.id.icon_serach:
                /*Log.e("====", String.valueOf(latitude));
                Log.e("====", String.valueOf(logitude));*/
                startActivity(new Intent(context, RentFilterActivity.class).
                        putExtra(Constants.LOCATION, ctv_title_location_name.getText().toString()).
                        putExtra(Constants.LATITUDE, String.valueOf(loc_latitude)).
                        putExtra(Constants.LONGITUDE, String.valueOf(loc_logitude)));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
           /* if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                ctv_title_location_name.setText(place.getAddress());
                String str_latLng = String.valueOf(place.getLatLng());
                str_latLng = str_latLng.replace("lat/lng: ", "");
                str_latLng = str_latLng.replace("(", "");
                str_latLng = str_latLng.replace(")", "");
                String[] array_latLng = str_latLng.split(",");
                loc_latitude = Double.valueOf(array_latLng[0]);
                loc_logitude = Double.valueOf(array_latLng[1]);
                FunLoadData();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                //Log.i("====", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }*/
        }
    }
}
