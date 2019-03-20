package com.kevinmazige.android.skies.ui.path;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.kevinmazige.android.skies.api.SatellitePositions;
import com.kevinmazige.android.skies.api.WebApi;
import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.Satellite;

/*
 * View model for path activity
 */
public class PathViewModel extends ViewModel {

    private final DataRepository mRepository;
    private MutableLiveData<SatellitePositions> result;

    public PathViewModel(DataRepository dataRepository) {
        mRepository = dataRepository;
    }

    public MutableLiveData<SatellitePositions>
    getSatellitePositions(final int id, final double lat, final double lng, final double alt,
                          final int secs) {
        result = mRepository.getSatPositions(id, lat, lng, alt, secs);
        return result;
    }

    public void clearPathData() {
        mRepository.clearPathData();
    }

    public LiveData<Satellite> getSatellite(int id) {
        return mRepository.getSatellite(id);
    }

    public void updateSatellites(Satellite... satellites) {
        mRepository.updateSatellites(satellites);
    }

    public WebApi getWebApi() {
        return mRepository.getWebApi();
    }

    public String getApiKey() {
        return mRepository.getApiKey();
    }
}
