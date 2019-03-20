package com.kevinmazige.android.skies.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.SatelliteCategory;

/*
 * Viewmodel for main activity
 */

public class SatelliteCategoryViewModel extends ViewModel {

    private final DataRepository mRepository;

    public SatelliteCategoryViewModel(DataRepository repository) {
        mRepository = repository;
    }

    /**
     * Inserts a satellite category into the table
     */
    public void insert(SatelliteCategory satelliteCategory) {
        mRepository.insert(satelliteCategory);
    }

    /**
     * Delete all categories from the table
     */
    public void deleteAllCategories() {
        mRepository.deleteAllCategories();
    }

    /**
     * Returns all categories in the table for Paging
     */
    public LiveData<PagedList<SatelliteCategory>> getAll() {
        return mRepository.getAll();
    }


    /**
     * Returns a random Satellite Category
     */
    public SatelliteCategory getRandomCategory() {
        return mRepository.getRandomCategory();
    }

}
