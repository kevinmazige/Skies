package com.kevinmazige.android.skies.ui.favourite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.Satellite;
import com.kevinmazige.android.skies.paging.SatelliteDataAdapter;
import com.kevinmazige.android.skies.ui.detail.WhatsUpActivity;

import java.util.ArrayList;

/*
 * Shows user list of satellites under a given category
 */
public class AddToFavouritesSatelliteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FavouriteViewModel mFavouriteViewModel;
    private SatelliteDataAdapter mSatelliteDataAdapter;
    private static final Boolean SHOW_ONLY_FAVOURITES = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_favourites_satellite);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String category = intent.getStringExtra(WhatsUpActivity.EXTRA_CATEGORY_NAME);
        int id = intent.getIntExtra(WhatsUpActivity.EXTRA_CATEGORY_INT, -1);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToFavorites(view);
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view_add_to_favourites_satellite);
        mSatelliteDataAdapter = new SatelliteDataAdapter(this);

        SatelliteDataAdapter.ItemAction itemAction = new SatelliteDataAdapter.ItemAction() {

            @Override
            public void onClick(TextView textView, Satellite s, int position) {
                toggleSatelliteSelection(textView, s, position);
            }
        };

        mSatelliteDataAdapter.setItemOnClickAction(itemAction);

        mRecyclerView.setAdapter(mSatelliteDataAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FavouriteViewModelFactory favouriteViewModelFactory =
                FavouriteViewModelFactory.createFactory(this);

        mFavouriteViewModel = ViewModelProviders.of(
                this, favouriteViewModelFactory).get(FavouriteViewModel.class);


        mFavouriteViewModel.getSatellitesByCategory(category, SHOW_ONLY_FAVOURITES)
                .observe(this, new Observer<PagedList<Satellite>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<Satellite> satellites) {
                        mSatelliteDataAdapter.submitList(satellites);
                    }
                });
    }

    // highlight and keep track of selected satellites
    private void toggleSatelliteSelection(TextView textView, Satellite s, int position) {

        if (mSatelliteDataAdapter.isSelected(s.getName())) {
            mSatelliteDataAdapter.removeSelectedSatellite(textView, s, position);
        } else {
            mSatelliteDataAdapter.addSelectedSatellite(textView, s, position);
        }
        mSatelliteDataAdapter.notifyItemChanged(position);
    }

    // add any selected satellites to favourites
    private void addToFavorites(View view) {
        if (mSatelliteDataAdapter.getSelectedSatellites().size() != 0) {
            Snackbar.make(view, "Added to favourites", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            ArrayList<Satellite> temp = mSatelliteDataAdapter.getSelectedSatellites();

            for (Satellite satellite : temp) {
                satellite.setFavourite(true);
            }

            mFavouriteViewModel.updateSatellites(temp.toArray(new Satellite[temp.size()]));
            mSatelliteDataAdapter.clearAllSelectedSatellites();
        } else {
            Snackbar.make(view, "Nothing selected", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}


















