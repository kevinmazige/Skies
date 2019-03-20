package com.kevinmazige.android.skies.notification;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.Satellite;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class NotificationViewModel extends ViewModel {

    public static final String TAG_GET_DATA_ONCE_A_DAY = "com.kevinmazige.android.skies.notification.GET_VISUAL_PASSES";
    public static final String FAVE_IDS = "favouriteIds";

    private final DataRepository mRepository;
    private WorkManager mWorkManager;

    public NotificationViewModel(DataRepository mRepository) {
        this.mRepository = mRepository;
        mWorkManager = WorkManager.getInstance();
    }

    public void getVisualPasses() {

        /*
         *retrieve all favourite satellites and create notifications for each of their upcoming
         * visual passes
         */
        mRepository.getSatellites("All", true)
                .observeForever(new Observer<PagedList<Satellite>>() {
                    @Override
                    public void onChanged(@Nullable PagedList<Satellite> satellites) {

                        /**
                         * Adding or removing a satellite will launch this job because of the observer.
                         * Clear the old repeating work since it is now out of date.
                         */
                        mWorkManager.cancelAllWorkByTag(TAG_GET_DATA_ONCE_A_DAY);

                        if (satellites != null && satellites.size() != 0) {
                            int[] faveIds = new int[satellites.size()];

                            for (int i = 0; i < satellites.size(); i++) {
                                faveIds[i] = satellites.get(i).getNoradId();
                            }

                            //86400000 = 1 day
                            //600000 = 10 minutes
                            /*
                             * create a notification for each visual pass for each favourited satellite
                             */
                            mWorkManager.enqueue(new PeriodicWorkRequest.Builder(VisualPassWorker.class,
                                    86400000, TimeUnit.MILLISECONDS, 600000, TimeUnit.MILLISECONDS)
                                    .setConstraints(Constraints.NONE)
                                    .addTag(TAG_GET_DATA_ONCE_A_DAY)
                                    .setInputData(new Data.Builder()
                                            .putIntArray(FAVE_IDS, faveIds)
                                            .build())
                                    .build());
                        }
                    }
                });


    }

    /*
     * cancel all currently scheduled passes and don't schedule any more
     */
    public void cancelWork() {
        mWorkManager.cancelAllWork();
    }

}
