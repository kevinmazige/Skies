package com.kevinmazige.android.skies.ui.favourite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.Satellite;

import com.kevinmazige.android.skies.paging.SatelliteDataAdapter;
import com.kevinmazige.android.skies.ui.path.PathActivity;

import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_FAVOURITE_STATUS;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_ID;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_NAME;

/*
 * Activity for displaying list of user's favourite satellites and with button for adding more
 */
public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FavouriteViewModel mFavouriteViewModel;
    private SatelliteDataAdapter mSatelliteDataAdapter;

    private static final String SHOW_ALL_CATEGORIES = "All";
    private static final Boolean SHOW_ONLY_FAVOURITES = true;

    private TextView mSadTextView;
    private ImageView mSadImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add satellites to favourites
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FavouriteActivity.this, AddToFavouritesCategoryActivity.class);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_favourites);
        mSatelliteDataAdapter = new SatelliteDataAdapter(this);

        mSadImageView = findViewById(R.id.sad_imageView);
        mSadTextView = findViewById(R.id.sad_textView);

        hideEmptyLayout();

        // If a favourite satellite is clicked, launch the path activity
        SatelliteDataAdapter.ItemAction itemAction = new SatelliteDataAdapter.ItemAction() {
            @Override
            public void onClick(TextView textView, Satellite satellite, int position) {

                Intent intent = new Intent(getBaseContext(), PathActivity.class);
                intent.putExtra(EXTRA_SAT_ID, satellite.getNoradId());
                intent.putExtra(EXTRA_SAT_NAME, satellite.getName());
                intent.putExtra(EXTRA_FAVOURITE_STATUS, satellite.getFavourite());
                startActivity(intent);
            }
        };

        mSatelliteDataAdapter.setItemOnClickAction(itemAction);

        mRecyclerView.setAdapter(mSatelliteDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    //Swipe to remove satellites from favourites
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int postion = viewHolder.getAdapterPosition();

                        Satellite satellite = mSatelliteDataAdapter.getSatelliteAtPosition(postion);
                        satellite.setFavourite(false);
                        mFavouriteViewModel.updateSatellites(satellite);

                        Snackbar.make(viewHolder.itemView, satellite.getName() +
                                " Removed from favorites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
        );
        helper.attachToRecyclerView(mRecyclerView);

        FavouriteViewModelFactory favouriteViewModelFactory =
                FavouriteViewModelFactory.createFactory(this);

        mFavouriteViewModel = ViewModelProviders.of(
                this, favouriteViewModelFactory).get(FavouriteViewModel.class);


        mFavouriteViewModel.getSatellitesByCategory(SHOW_ALL_CATEGORIES, SHOW_ONLY_FAVOURITES)
                .observe(this, new Observer<PagedList<Satellite>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<Satellite> satellites) {
                        mSatelliteDataAdapter.submitList(satellites);

                        if (satellites.size() == 0) {
                            showEmptyLayout();
                        } else {
                            hideEmptyLayout();
                        }
                    }
                });
    }

    /*
     * If there are no favourite satellites, show the user a layout that makes that clear
     */
    private void showEmptyLayout() {
        mRecyclerView.setVisibility(View.GONE);
        mSadTextView.setVisibility(View.VISIBLE);
        mSadImageView.setVisibility(View.VISIBLE);
    }

    /*
     * Show the layout with the list of favourite satellites.
     */
    private void hideEmptyLayout() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mSadTextView.setVisibility(View.GONE);
        mSadImageView.setVisibility(View.GONE);
    }

}
