package com.kevinmazige.android.skies.ui.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.kevinmazige.android.skies.api.WhatsUp;
import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.Satellite;

/*
 * View model for whats up activity. Each activity has its own viewmodel since they persist across
 * configuration changes and to keep data well organized.
 */
public class WhatsUpViewModel extends ViewModel {

    private final DataRepository mRepository;
    private MutableLiveData<WhatsUp> result;

    public WhatsUpViewModel(DataRepository dataRepository) {
        mRepository = dataRepository;
    }

    public MutableLiveData<WhatsUp> getWhatsUp(double lat, double lng, double alt, int rad, int cat) {
        result = mRepository.getWhatsUp(lat, lng, alt, rad, cat);
        return result;
    }

    public LiveData<Satellite> getSatellite(int id) {
        return mRepository.getSatellite(id);
    }
}
