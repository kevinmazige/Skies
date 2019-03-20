package com.kevinmazige.android.skies.ui.path;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.api.SatellitePositions;
import com.kevinmazige.android.skies.data.Satellite;
import com.kevinmazige.android.skies.utils.Utils;

import java.util.List;

import static com.kevinmazige.android.skies.ui.detail.WhatsUpActivity.DELAY;
import static com.kevinmazige.android.skies.ui.main.MainActivity.ALTITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LATITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LONGITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.SHARED_PREFERENCES_FILE;

/*
 * Activity to plot and track satellite across path
 */
public class PathActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_SAT_ID = "com.kevinmazige.android.skies.ui.detail.SAT_ID";
    public static final String EXTRA_SAT_NAME = "com.kevinmazige.android.skies.ui.detail.SAT_NAME";

    public static final String EXTRA_FAVOURITE_STATUS = "com.kevinmazige.android.skies.ui.detail.FAVOURITE_STATUS";
    private static final String ZOOM = "zoom";

    private static final int NUMBER_OF_DATA_POINTS = 300;

    private GoogleMap mMap;
    private int mSatelliteId;
    private String mSatelliteName;
    private Double mLatitude;
    private Double mLongitude;
    private Double mAltitude;
    private boolean mIsFavourite;
    private SharedPreferences mPreferences;

    private PathViewModel mPathViewModel;
    private Bitmap mBitmap;
    private List<SatellitePositions.Positions> mPositions;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private Marker mMarker;
    private PolylineOptions mPolylineOptions;
    private Boolean initialPlot = true;
    private CoordinatorLayout mCoordinatorLayout;
    private Float mZoom;
    private Polyline mPolyline;
    private Boolean snackBarShown = false;
    private Boolean clearOldPath = false;

    @Override
    protected void onPause() {
        // clear old path data if activity is about to be shut down
        mPathViewModel.clearPathData();

        // stop the repeating runnable
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // remember user changed zoom across configuration changes
        outState.putFloat(ZOOM, mMap.getCameraPosition().zoom);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path);

        if (savedInstanceState != null) {
            mZoom = savedInstanceState.getFloat(ZOOM);
        } else {
            mZoom = 2.5f;
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.path_map);
        mapFragment.getMapAsync(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        mCoordinatorLayout = findViewById(R.id.path_activity_coordinator);

        mPreferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        mLatitude = Double.longBitsToDouble(mPreferences.getLong(LATITUDE, 0));
        mLongitude = Double.longBitsToDouble(mPreferences.getLong(LONGITUDE, 0));
        mAltitude = Double.longBitsToDouble(mPreferences.getLong(ALTITUDE, 0));

        // Get the satellite path to display form the intent
        Intent intent = getIntent();
        mSatelliteId = intent.getIntExtra(EXTRA_SAT_ID, -1);
        mSatelliteName = intent.getStringExtra(EXTRA_SAT_NAME);
        mIsFavourite = intent.getBooleanExtra(EXTRA_FAVOURITE_STATUS, false);

        // Set the satellite name as the activity label
        Toolbar toolbar = findViewById(R.id.path_toolbar);
        toolbar.setTitle(mSatelliteName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // instantiate the view model for all the ui data
        PathViewModelFactory pathViewModelFactory =
                PathViewModelFactory.createFactory(this);

        mPathViewModel = ViewModelProviders.of(
                this, pathViewModelFactory).get(PathViewModel.class);

        //Create Satellite icon bitmap
        mBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.satellite),
                100, 100, true);

        //Format path properties
        mPolylineOptions = new PolylineOptions().color(
                PathActivity.this.getColor(R.color.colorPrimaryDark));
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        mapSettings.setMyLocationButtonEnabled(true);

        LatLng location = new LatLng(mLatitude, mLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, mZoom));

        final Observer<SatellitePositions> mObserver = new Observer<SatellitePositions>() {

            @Override
            public void onChanged(@Nullable SatellitePositions satellitePositions) {

                /*
                 * Only clear the old path when the activity is recreated - not when we add data to
                 * the currently plotted path
                 */
                if (clearOldPath) {
                    mPathViewModel.clearPathData();
                    clearOldPath = false;
                }

                if (satellitePositions != null && satellitePositions.getPositions() != null
                        && satellitePositions.getPositions().size() > 4) {

                    mPositions = satellitePositions.getPositions();

                    plotMarker(0);
                    addPath();

                    if (!initialPlot) {
                        mHandler.postDelayed(mRunnable, DELAY);
                    }

                    /*
                     * only center the map on the satellite on initial creation. after that, the user
                     * has complete command of map
                     */

                    if (initialPlot) {
                        centerMap(0);
                        initialPlot = false;
                    } else {
                        mHandler.postDelayed(mRunnable, DELAY);
                    }
                }
            }
        };

        mPathViewModel.getSatellitePositions(mSatelliteId, mLatitude, mLongitude, mAltitude,
                NUMBER_OF_DATA_POINTS).observe(this, mObserver);

        mRunnable = new Runnable() {
            int pos = 2;

            @Override
            public void run() {

                // if there is no data connection, notify the user
                if (!Utils.isNetWorkAvailable(getApplicationContext())) {
                    if (!snackBarShown) {
                        mPositions = null;
                        Snackbar.make(mCoordinatorLayout, " No connection.", Snackbar.LENGTH_LONG).show();
                        snackBarShown = true;
                    }
                    clearOldPath = true;
                }

                // create and plot satellite path
                if (mPositions == null) {
                    mMap.clear();
                    mPolylineOptions = new PolylineOptions().color(PathActivity.this.getColor(R.color.colorPrimaryDark));
                    mPathViewModel.clearPathData();
                    mPathViewModel.getSatellitePositions(mSatelliteId, mLatitude, mLongitude, mAltitude,
                            NUMBER_OF_DATA_POINTS);
                    clearOldPath = true;
                }

                //As long as you have enouth positions in array, plot the next one
                if (mPositions != null && mPositions.size() > 4) {
                    plotMarker(pos);
                    pos += 2;
                }

                //get new positions before you run out of old ones so that path doesn't have a gap
                if (mPositions != null && pos >= mPositions.size() - 10) {
                    pos = 2;
                    mPathViewModel.getSatellitePositions(mSatelliteId, mLatitude, mLongitude, mAltitude,
                            NUMBER_OF_DATA_POINTS);

                } else {
                    mHandler.postDelayed(mRunnable, DELAY);
                }
            }
        };
        mHandler.postDelayed(mRunnable, DELAY);
    }


    // plots marker at given position based on index argument
    private void plotMarker(int i) {
        if (mMarker != null) {
            mMarker.remove();
        }

        mMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(mPositions.get(i).getSatlatitude(),
                        mPositions.get(i).getSatlongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(mBitmap))
                .anchor(0.5f, 0.5f)
        );
    }

    // center map on position derived from index argument
    private void centerMap(int i) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                mPositions.get(i).getSatlatitude(), mPositions.get(i).getSatlongitude()), mZoom));
    }

    // add path to map
    private void addPath() {
        for (SatellitePositions.Positions position : mPositions) {
            mPolylineOptions.add(new LatLng(position.getSatlatitude(), position.getSatlongitude()));
            mPolyline = mMap.addPolyline(mPolylineOptions);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.path, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_add_to_favorites);

        /*
         * Colour the favourite icon to math the favourite status of the currently plotted satellite
         */
        if (mIsFavourite) {
            item.setIcon(getResources().getDrawable(R.drawable.ic_fave_red));
        } else {
            item.setIcon(getResources().getDrawable(R.drawable.ic_fave));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // if the favourite icon is clicked, toggle the favourite status of the satellite
        if (id == R.id.action_add_to_favorites) {
            toggleSatelliteFavouriteStatus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // add or remove satellite to and from list of favourites
    public void toggleSatelliteFavouriteStatus() {

        if (mIsFavourite) {
            mIsFavourite = false;
        } else {
            mIsFavourite = true;
        }

        final Observer<Satellite>[] observers = new Observer[1];

        final LiveData<Satellite> satelliteLiveData = mPathViewModel.getSatellite(mSatelliteId);

        observers[0] = new Observer<Satellite>() {
            @Override
            public void onChanged(@Nullable Satellite satellite) {
                satelliteLiveData.removeObserver(observers[0]);
                satellite.setFavourite(mIsFavourite);
                mPathViewModel.updateSatellites(satellite);
            }
        };

        satelliteLiveData.observe(this, observers[0]);

        if (mIsFavourite) {
            Snackbar.make(mCoordinatorLayout, mSatelliteName + " Added to favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(mCoordinatorLayout, mSatelliteName + " Removed from favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        invalidateOptionsMenu();
    }
}

