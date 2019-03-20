package com.kevinmazige.android.skies.ui.main;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.SatelliteCategory;
import com.kevinmazige.android.skies.paging.SatelliteCategoryAdapter;
import com.kevinmazige.android.skies.ui.SettingsActivity;
import com.kevinmazige.android.skies.ui.detail.WhatsUpActivity;
import com.kevinmazige.android.skies.ui.favourite.FavouriteActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SatelliteCategoryViewModel mSatelliteCategoryViewModel;
    private RecyclerView mRecyclerView;
    private SatelliteCategoryAdapter mSatelliteCategoryAdapter;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mPreferencesEditor;

    public static final String SHARED_PREFERENCES_FILE = "com.kevinmazige.android.skies";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";

    /*
     * Activity that display's list of satellite categories and prompt's the user to see what satellites
     * are currently in the skies above them
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.whats_up_activity_name);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        mPreferencesEditor = mPreferences.edit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView = findViewById(R.id.recycler_view_main);
        mSatelliteCategoryAdapter = new SatelliteCategoryAdapter(this);

        SatelliteCategoryAdapter.ItemAction itemAction = new SatelliteCategoryAdapter.ItemAction() {
            @Override
            public void onClick(String categoryName, int categoryInt) {
                Intent intent = new Intent(getApplicationContext(), WhatsUpActivity.class);
                intent.putExtra(WhatsUpActivity.EXTRA_CATEGORY_NAME, categoryName);
                intent.putExtra(WhatsUpActivity.EXTRA_CATEGORY_INT, categoryInt);
                startActivity(intent);
            }
        };
        mSatelliteCategoryAdapter.setItemOnClickAction(itemAction);

        mRecyclerView.setAdapter(mSatelliteCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SatelliteCategoryViewModelFactory satelliteCategoryViewModelFactory =
                SatelliteCategoryViewModelFactory.createFactory(this);

        mSatelliteCategoryViewModel = ViewModelProviders.of(
                this, satelliteCategoryViewModelFactory).get(SatelliteCategoryViewModel.class);

        mSatelliteCategoryViewModel.getAll().observe(this, new Observer<PagedList<SatelliteCategory>>() {
            @Override
            public void onChanged(@Nullable PagedList<SatelliteCategory> satelliteCategories) {
                mSatelliteCategoryAdapter.submitList(satelliteCategories);
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /**
         * updates global variable with current user location
         */
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                mPreferencesEditor.putLong(LATITUDE, Double.doubleToRawLongBits(mCurrentLocation.getLatitude()));
                mPreferencesEditor.putLong(LONGITUDE, Double.doubleToRawLongBits(mCurrentLocation.getLongitude()));
                mPreferencesEditor.putLong(ALTITUDE, Double.doubleToRawLongBits(mCurrentLocation.getAltitude()));
                mPreferencesEditor.apply();
            }
        };

        mLocationRequest = new LocationRequest()
                .setInterval(30000)
                .setFastestInterval(30000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /*
         * Prompt the user to allow the app access to their location if they haven't granted it
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    /**
     * Processes the result of requesting permission to access the user's location. If unsuccessful,
     * request again.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                    }
                    ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                    break;
                }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // launch the settings activity if the user clicks on the settings option in menu
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_whats_up) {
            /*
            Bring What's up activity to the top - do nothing
             */
        } else if (id == R.id.nav_favourites) {
            // launch favourites activiy
            Intent intent = new Intent(this, FavouriteActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            //another way to launch the settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
