package com.vayetek.financeapp.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.OnMapClickListener;
import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.MainViewPagerAdapter;
import com.vayetek.financeapp.config.Const;
import com.vayetek.financeapp.fragments.MarketFragment;
import com.vayetek.financeapp.fragments.NewsFragment;
import com.vayetek.financeapp.fragments.ToolsFragment;
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

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MaterialSearchView materialSearchView;
    private GoogleApiClient mGoogleApiClient;
    private Location myLastLocation;
    private String queryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        setContentView(R.layout.activity_main);
        initializeToolbar();
        initializeSearchView();
        initiliazeTabView();

        hasPermissions(this, PERMISSIONS);
    }

    private void initiliazeTabView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) ((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                ((TextView) ((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                ((TextView) ((LinearLayout) tabLayout.getTabAt(2).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                switch (tab.getPosition()) {
                    case 0:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 1:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 2:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons(tabLayout);
    }

    private void setupTabIcons(TabLayout tabLayout) {
        LinearLayout tabOne = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabOne.getChildAt(0)).setText("MARKET");
        ((TextView) tabOne.getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));

        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        LinearLayout tabTow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabTow.getChildAt(0)).setText("NEWS");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTow);
        LinearLayout tabThree = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabThree.getChildAt(0)).setText("TOOLS");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        ((TextView) tabTow.getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
        ((TextView) tabThree.getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
    }


    private void setupViewPager(ViewPager viewPager) {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MarketFragment(), "MARKET");
        adapter.addFragment(new NewsFragment(), "NEWS");
        adapter.addFragment(new ToolsFragment(), "TOOLS");
        viewPager.setAdapter(adapter);
    }

    private void initializeSearchView() {
        materialSearchView = (MaterialSearchView) findViewById(R.id.material_search);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String queryText) {

                //Do some magic
                if (queryText.length() > 0) {
                    materialSearchView.setLoading(true);
                    MainActivity.this.queryText = queryText;
                    if (hasPermissions(MainActivity.this, PERMISSIONS)) {
                        startLocationUpdates();
                    }

                }
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        //materialSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        materialSearchView.setSuggestions(new String[]{});
        materialSearchView.setVoiceSearch(false); //or false
        materialSearchView.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClickListener() {
                Log.d("onMapClickListener", "onMapClickListener: onMapClickListener");
                Intent intent = new Intent(MainActivity.this, ToolsActivity.class);
                intent.putExtra("toolsRequest", 6);
                startActivity(intent);
            }
        });
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            materialSearchView.showSearch();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MaterialSearchView.REQUEST_VOICE:
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            materialSearchView.setQuery(searchWrd, false);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            Log.d("location", "startLocationUpdates: ");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, (LocationListener) this);
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLastLocation != null) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Request Permissions
    private String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((MainActivity) context, permissions, Const.MY_PERMISSIONS_REQUEST_LOCALISATION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSIONS_REQUEST_LOCALISATION: {

            }
            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        myLastLocation = location;
        Call<ResponseBody> call = Utils.getGoogleApiRetrofitServices().nearbySearch(getString(R.string.google_api_key),
                myLastLocation.getLatitude() + "," + myLastLocation.getLongitude(), 2000, "finance", queryText);
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
                    ArrayList<String> suggestions = new ArrayList<>();
                    List<GoogleSearchModel> nearbySearchResults = new ArrayList<>();
                    int size = jsonArray.length();
                    for (int i = 0; i < size; i++) {
                        GoogleSearchModel googleSearchModel = gson.fromJson(jsonArray.getString(i), GoogleSearchModel.class);
                        suggestions.add(googleSearchModel.name + " - " + googleSearchModel.vicinity);
                        nearbySearchResults.add(googleSearchModel);
                    }
                    if (size > 0) {
                        Log.d("suggestion", "onResponse: " + suggestions.toString());
                        materialSearchView.setSuggestions(suggestions.toArray(new String[suggestions.size()]));
                        //materialSearchView.showSuggestions();
                    } else {
                        materialSearchView.setSuggestions(new String[]{});
                    }
                } catch (NullPointerException | JSONException | IOException e) {
                    e.printStackTrace();
                }
                materialSearchView.setLoading(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                materialSearchView.setLoading(false);
                t.printStackTrace();
            }
        });

    }
}
