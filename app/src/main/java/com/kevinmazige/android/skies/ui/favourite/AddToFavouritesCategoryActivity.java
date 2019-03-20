package com.kevinmazige.android.skies.ui.favourite;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.data.SatelliteCategory;
import com.kevinmazige.android.skies.paging.SatelliteCategoryAdapter;
import com.kevinmazige.android.skies.ui.detail.WhatsUpActivity;
import com.kevinmazige.android.skies.ui.main.SatelliteCategoryViewModel;
import com.kevinmazige.android.skies.ui.main.SatelliteCategoryViewModelFactory;

/*
 * Helps user find satellites by first displaying categories
 */
public class AddToFavouritesCategoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SatelliteCategoryAdapter mSatelliteCategoryAdapter;
    private SatelliteCategoryViewModel mSatelliteCategoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_favourites_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recycler_view_add_to_favourites_category);
        mSatelliteCategoryAdapter = new SatelliteCategoryAdapter(this);

        SatelliteCategoryAdapter.ItemAction itemAction = new SatelliteCategoryAdapter.ItemAction() {
            @Override
            public void onClick(String categoryName, int categoryInt) {
                Intent intent = new Intent(getApplicationContext(), AddToFavouritesSatelliteActivity.class);
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
    }

}
