package com.kevinmazige.android.skies.ui.favourite;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;

import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.Satellite;

/*
 * view model for favourite Activities i.e FavouriteActivity, AddToFavouritesSatelliteActivity,
 * & AddToFacouritesCategoryActivity
 */
public class FavouriteViewModel extends ViewModel {

    private final DataRepository mRepository;

    public FavouriteViewModel(DataRepository mRepository) {
        this.mRepository = mRepository;
    }

    /**
     * Returns all satellites by category for Paging
     */
    public LiveData<PagedList<Satellite>> getSatellitesByCategory(String category, Boolean favourites) {
        return mRepository.getSatellites(category, favourites);
    }

    /**
     * Updates given satellite data
     */
    public void updateSatellites(Satellite... satellites) {
        mRepository.updateSatellites(satellites);
    }
}
