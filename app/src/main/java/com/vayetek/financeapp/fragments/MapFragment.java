package com.vayetek.financeapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.vayetek.financeapp.R;
import com.vayetek.financeapp.models.GoogleSearchModel;
import com.vayetek.financeapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private MapView mapView;
    private ProgressBar progressBar;
    private View rootView;
    private GoogleMap googleMaps;
    private GoogleApiClient mGoogleApiClient;
    private Location myLastLocation;


    public static MapFragment newInstance() {

        Bundle args = new Bundle();

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.map_fragment, container, false);
        initializeView();
        return rootView;
    }


    private void initializeMapView(View rootView, Bundle savedInstanceState) {
        mapView = (MapView) rootView.findViewById(R.id.map_view);
        //MapsInitializer.initialize(getContext());
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeMapView(rootView, savedInstanceState);
    }

    private void initializeView() {
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMaps = googleMap;
        //this.progressBar.setVisibility(View.GONE);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.804402, 10.165068), 11));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
            }
        });
        googleMaps.setOnMarkerClickListener(this);

        startLocationUpdates();
    }

    private void reinitMarker() {

    }

    private static final int UPDATE_INTERVAL = 10000;
    private static final int FASTEST_INTERVAL = 5000;

    protected void startLocationUpdates() {
        Log.d("startLocationUpdates()", "startLocationUpdates: ");
        // Create the location request
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Log.d("location", "startLocationUpdates: ");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, (LocationListener) this);
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        myLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLastLocation != null) {
            progressBar.setVisibility(View.GONE);
            googleMaps.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastLocation.getLatitude(),
                    myLastLocation.getLongitude()), 14));
            googleMaps.setMyLocationEnabled(true);

            Call<ResponseBody> call = Utils.getGoogleApiRetrofitServices().nearbySearch(getString(R.string.google_api_key),
                    myLastLocation.getLatitude() + "," + myLastLocation.getLongitude(), 2000, "finance", "");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("code", "onResponse: " + response.code());
                    Log.d("message", "onResponse: " + response.message());
                    if (response.code() != 200) {
                        return;
                    }
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        Gson gson = Utils.getGson();
                        List<GoogleSearchModel> nearbySearchResults = new ArrayList<>();
                        int size = jsonArray.length();
                        for (int i = 0; i < size; i++) {
                            GoogleSearchModel googleSearchModel = gson.fromJson(jsonArray.getString(i), GoogleSearchModel.class);
                            nearbySearchResults.add(googleSearchModel);
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.valueOf(googleSearchModel.geometry.location.lat),
                                    Double.valueOf(googleSearchModel.geometry.location.lng))).title(googleSearchModel.name)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin));
                            googleMaps.addMarker(marker);
                            Log.d("googleSearchModel", "onResponse: " + googleSearchModel.name);
                        }

                    } catch (NullPointerException | JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        }
    }

    Marker marker;

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.

        this.marker = marker;
        // Check if a click count was set, then display the click count.
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_pin_pressed));
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        mapView.onStart();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
