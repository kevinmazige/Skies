package com.kevinmazige.android.skies.notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.kevinmazige.android.skies.api.VisualPasses;
import com.kevinmazige.android.skies.data.DataRepository;
import com.kevinmazige.android.skies.data.PassObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.kevinmazige.android.skies.notification.NotificationViewModel.FAVE_IDS;
import static com.kevinmazige.android.skies.ui.main.MainActivity.ALTITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LATITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.LONGITUDE;
import static com.kevinmazige.android.skies.ui.main.MainActivity.SHARED_PREFERENCES_FILE;

public class VisualPassWorker extends Worker {

    private Double mLatitude;
    private Double mLongitude;
    private Double mAltitude;
    private SharedPreferences mPreferences;
    private DataRepository mRepository;

    private int[] mFavouriteSatelliteIds;
    private ArrayList<PassObject> mVisualPassObjects;

    private Context mContext;
    private WorkManager mWorkManager;

    private static final String NOTIFICATION_TAG = "notificationTag";
    public static final String NAME = "name";
    public static final String ID = "id";


    public VisualPassWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;

        mRepository = DataRepository.getInstance(context);
        mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);

        //retrieve user location from shared preferences file
        mLatitude = Double.longBitsToDouble(mPreferences.getLong(LATITUDE, 0));
        mLongitude = Double.longBitsToDouble(mPreferences.getLong(LONGITUDE, 0));
        mAltitude = Double.longBitsToDouble(mPreferences.getLong(ALTITUDE, 0));

        mFavouriteSatelliteIds = null;
        mVisualPassObjects = new ArrayList<>();
        mWorkManager = WorkManager.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {

        // do this so that you don't duplicate previously scheduled notifications if the worker is
        // called in response to the addition or deletion of a satellite to a user's favourites
        mWorkManager.cancelAllWorkByTag(NOTIFICATION_TAG);

        mFavouriteSatelliteIds = getInputData().getIntArray(FAVE_IDS);

        if (mFavouriteSatelliteIds != null) {

            for (int noradId : mFavouriteSatelliteIds) {

                // web api call is done off the main thread but synchronously. Visual pass data is
                // retrived for the next 24 hours and the minimum visibility length is 5 minutes
                Call<VisualPasses> call = mRepository.getWebApi().getVisualPasses(noradId,
                        mLatitude, mLongitude, mAltitude, 1, 300,
                        mRepository.getApiKey());

                Response<VisualPasses> response;
                try {
                    response = call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    //if data get request failed, try again
                    return Result.retry();
                }

                //get request successful and valid
                if (response != null && response.code() == 200) {
                    if (response.body() != null && response.body().getInfo().getPassescount() != 0) {

                        for (int i = 0; i < response.body().getPasses().size(); i++) {

                            int id = response.body().getInfo().getSatid();
                            String name = response.body().getInfo().getSatname();

                            Long futureTime = response.body().getPasses().get(i).getStartUTC() * 1000;
                            Long currentTime = System.currentTimeMillis();
                            Long delay = futureTime - currentTime;

                            //schedule notifications for each visual pass of favourite satellites
                            mWorkManager.enqueue(new OneTimeWorkRequest.Builder(
                                    NotificationWorker.class)
                                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                    .setInputData(new Data.Builder()
                                            .putInt(ID, id)
                                            .putString(NAME, name)
                                            .build())
                                    .addTag(NOTIFICATION_TAG)
                                    .build());
                        }
                    }
                } else {
                    //get request was successful but not valid
                    return Result.retry();
                }
            }
            //get request and processing were successful
            return Result.success();
        }
        //Something went wrong. This class is never started if there are no favourite satellites
        return Result.retry();
    }
}









