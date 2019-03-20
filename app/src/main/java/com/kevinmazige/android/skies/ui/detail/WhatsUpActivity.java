package com.kevinmazige.android.skies.ui.detail;


import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.api.WhatsUp;
import com.kevinmazige.android.skies.data.Satellite;
import com.kevinmazige.android.skies.ui.SettingsActivity;
import com.kevinmazige.android.skies.ui.path.PathActivity;
import com.kevinmazige.android.skies.utils.Utils;

import static com.kevinmazige.android.skies.ui.main.MainActivity.ALTITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LATITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LONGITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.SHARED_PREFERENCES_FILE;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_FAVOURITE_STATUS;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_ID;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_NAME;


public class WhatsUpActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static final String EXTRA_CATEGORY_NAME = "com.kevinmazige.android.skies.ui.detail.CATEGORY_NAME";
    public static final String EXTRA_CATEGORY_INT = "com.kevinmazige.android.skies.ui.detail.CATEGORY_INT";
    private static final String ZOOM = "zoom";
    public final static int DELAY = 2000;

    private Double mLatitude;
    private Double mLongitude;
    private Double mAltitude;

    private int mSettingsRad;
    private GoogleMap mMap;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private WhatsUpViewModel mWhatsUpViewModel;
    private String mCategoryName;
    private int mCategoryId;
    private Bitmap mBitmap;
    private SharedPreferences mDefaultSharedPreferences;
    private CoordinatorLayout mCoordinatorLayout;
    Boolean mHasBeenCreated;
    private Float mZoom;

    private SharedPreferences mPreferences;
    private boolean mNoConnection = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        //start getting whats up data
        if (mRunnable != null) {
            mRunnable.run();
        }

        //get up to date radius values since these may have been changed
        mSettingsRad = Integer.parseInt(mDefaultSharedPreferences
                .getString(SettingsActivity.KEY_PREF_RADIUS_EDITTEXT, "66"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop getting whats up data
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //save current zoom across configuration changes to keep user experience consistent
        outState.putFloat(ZOOM, mMap.getCameraPosition().zoom);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_up);

        if (savedInstanceState != null) {
            mZoom = savedInstanceState.getFloat(ZOOM);
        } else {
            mZoom = 2.5f;
        }

        mCoordinatorLayout = findViewById(R.id.whatsup_activity_coordinator);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.whatsup_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_CATEGORY_NAME)) {
            mCategoryName = intent.getStringExtra(EXTRA_CATEGORY_NAME);
            mCategoryId = intent.getIntExtra(EXTRA_CATEGORY_INT, -1);

        } else {
            /*
             * when the whats up activity is launched by pressing up from a path activity launched
             * by a notification, there is no assigned category so default to all.
             */
            mCategoryName = "All";
            mCategoryId = 0;
        }


        // Set the category as the activity label
        Toolbar toolbar = findViewById(R.id.whatsup_toolbar);
        toolbar.setTitle(mCategoryName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        WhatsUpViewModelFactory whatsUpViewModelFactory =
                WhatsUpViewModelFactory.createFactory(this);

        mWhatsUpViewModel = ViewModelProviders.of(
                this, whatsUpViewModelFactory).get(WhatsUpViewModel.class);

        /**
         * Create Satellite icon bitmap
         */
        mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.satellite),
                100, 100, true);

        mLatitude = Double.longBitsToDouble(mPreferences.getLong(LATITUDE, 0));
        mLongitude = Double.longBitsToDouble(mPreferences.getLong(LONGITUDE, 0));
        mAltitude = Double.longBitsToDouble(mPreferences.getLong(ALTITUDE, 0));

        mHasBeenCreated = true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);

        mMap.setOnMarkerClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        LatLng location = new LatLng(mLatitude, mLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mZoom));


        /**
         * Draws satellite markers on map
         */
        mWhatsUpViewModel.getWhatsUp(mLatitude, mLongitude, mAltitude, mSettingsRad, mCategoryId)
                .observe(WhatsUpActivity.this, new Observer<WhatsUp>() {
                    @Override
                    public void onChanged(@Nullable WhatsUp whatsUp) {
                        if (mMap != null) {
                            mMap.clear();
                            if (whatsUp != null && whatsUp.getSatellites() != null) {

                                /**
                                 * Use the mNoConnection variable to show a snackbar message when there is not data / wifi
                                 * without repeatedly launching it because of the 2 sec runnable
                                 */
                                if (mNoConnection) {
                                    mNoConnection = false;
                                }

                                // create marker with default properties and plot in initial position
                                for (WhatsUp.Satellite satellite : whatsUp.getSatellites()) {
                                    Marker marker = mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(satellite.getSatlat(), satellite.getSatlng()))
                                            .title(satellite.getSatname())
                                            .icon(BitmapDescriptorFactory.fromBitmap(mBitmap))
                                            .anchor(0.5f, 0.5f));
                                    marker.setTag(satellite);
                                }
                            } else {
                                /*
                                 * notify user when data / wifi is lost
                                 */
                                if (!mNoConnection) {
                                    if (!Utils.isNetWorkAvailable(getApplicationContext())) {
                                        Snackbar.make(mCoordinatorLayout, " No connection.", Snackbar.LENGTH_LONG)
                                                .setAction("UNDO", null).show();
                                        mNoConnection = true;
                                    }
                                }
                            }
                        }
                    }
                });

        /**
         * Creates a runnable to get new What's up data every DELAY seconds
         */
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mWhatsUpViewModel.getWhatsUp(mLatitude, mLongitude, mAltitude, mSettingsRad, mCategoryId);
                mHandler.postDelayed(this, DELAY);
            }
        };
        mRunnable.run();
    }

    /**
     * on click, launch activity to plot and track satellite across path.
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        WhatsUp.Satellite satellite = (WhatsUp.Satellite) marker.getTag();

        final Intent intent = new Intent(this, PathActivity.class);
        intent.putExtra(EXTRA_SAT_ID, satellite.getSatid());
        intent.putExtra(EXTRA_SAT_NAME, satellite.getSatname());


        final Observer<Satellite>[] observers = new Observer[1];

        final LiveData<Satellite> satelliteLiveData = mWhatsUpViewModel.getSatellite(satellite.getSatid());

        observers[0] = new Observer<Satellite>() {
            @Override
            public void onChanged(@Nullable Satellite satellite) {
                /*
                 * since you only want to do this once, remove observer after retrieving satellite
                 * favourite status
                 */
                if (satellite != null) {
                    satelliteLiveData.removeObserver(observers[0]);
                    intent.putExtra(EXTRA_FAVOURITE_STATUS, satellite.getFavourite());
                    startActivity(intent);
                }
            }
        };

        satelliteLiveData.observe(this, observers[0]);

        return true;
    }
}