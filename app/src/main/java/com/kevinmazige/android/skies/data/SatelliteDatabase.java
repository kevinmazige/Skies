package com.kevinmazige.android.skies.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Room database. Only one instance of the database is created for use by the repository.
 */
@Database(entities = {Satellite.class, SatelliteCategory.class, PassObject.class}, version = 1, exportSchema = false)
public abstract class SatelliteDatabase extends RoomDatabase {

    public abstract SatelliteDao satelliteDao();

    public abstract SatelliteCategoryDao satelliteCategoryDao();

    public abstract PassDao passDao();

    private static volatile SatelliteDatabase sInstance = null;

    private static Context mContext;

    /**
     * Returns an instance of Room Database.
     *
     * @param context application context
     * @return The singleton SatelliteDatabase
     */

    public static synchronized SatelliteDatabase getInstance(final Context context) {
        mContext = context;
        if (sInstance == null) {
            synchronized (SatelliteDatabase.class) {
                if (sInstance == null) {

                    //the callback initializes the database with data parsed from a csv file.
                    sInstance = Room.databaseBuilder(mContext.getApplicationContext(),
                            SatelliteDatabase.class, "satellite_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sSatelliteDatabaseCallback)
                            .build();
                }
            }
        }
        return sInstance;
    }

    private static SatelliteDatabase.Callback sSatelliteDatabaseCallback = new SatelliteDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsyncTask(sInstance).execute();
        }
    };

    /**
     * Load initial data into database
     */

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final SatelliteDao mSatelliteDao;
        private final SatelliteCategoryDao mSatelliteCategoryDao;

        PopulateDbAsyncTask(SatelliteDatabase db) {
            mSatelliteDao = db.satelliteDao();
            mSatelliteCategoryDao = db.satelliteCategoryDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //loads all satellite data into database.
            if (mSatelliteDao.getRandomSatellite() == null) {
                List<Satellite> initialData = DataUtils.parseCSV(mContext);
                for (Satellite satellite : initialData) {
                    mSatelliteDao.insert(satellite);
                }
            }

            //loads category data into database
            if (mSatelliteCategoryDao.getRandomCategory() == null) {
                for (SatelliteCategory satelliteCategory : DataUtils.CATEGORIES) {
                    mSatelliteCategoryDao.insert(satelliteCategory);
                }
            }

            return null;
        }
    }
}












