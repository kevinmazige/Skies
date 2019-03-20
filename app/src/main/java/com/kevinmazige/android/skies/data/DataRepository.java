package com.kevinmazige.android.skies.data;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Context;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.api.SatellitePositions;
import com.kevinmazige.android.skies.api.VisualPasses;
import com.kevinmazige.android.skies.api.WebApi;
import com.kevinmazige.android.skies.api.WhatsUp;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles data sources and executes on the correct thread
 */
public class DataRepository {

    private static final int PAGE_SIZE = 20;
    private static final String BASE_URL = "https://www.n2yo.com/";
    private static String API_KEY;


    private static volatile DataRepository sInstance = null;
    private final SatelliteDao mSatelliteDao;
    private final SatelliteCategoryDao mSatelliteCategoryDao;
    private final PassDao mPassDao;
    private final ExecutorService mIoExecutor;

    private LiveData<PagedList<Satellite>> mListOfSatellites;
    private LiveData<PagedList<SatelliteCategory>> mListOfSatelliteCategories;

    private WebApi mWebApi;
    private MutableLiveData<SatellitePositions> mSatellitePositions = new MutableLiveData<>();
    private MutableLiveData<VisualPasses> mVisualPasses = new MutableLiveData<>();
    private MutableLiveData<WhatsUp> mWhatsUp = new MutableLiveData<>();

    private DataRepository(SatelliteDao satelliteDao, SatelliteCategoryDao satelliteCategoryDao, PassDao passDao,
                           ExecutorService executorService) {
        mIoExecutor = executorService;
        mSatelliteDao = satelliteDao;
        mSatelliteCategoryDao = satelliteCategoryDao;
        mPassDao = passDao;

        mWebApi = new Retrofit.Builder()
                .client(new OkHttpClient.Builder().build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WebApi.class);
    }

    /**
     * Guarantees that only one instance of the repository is used for the entire application
     */
    public static DataRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    SatelliteDatabase database = SatelliteDatabase.getInstance(context);
                    sInstance = new DataRepository(database.satelliteDao(),
                            database.satelliteCategoryDao(), database.passDao(),
                            Executors.newSingleThreadExecutor());

                    API_KEY = context.getResources().getString(R.string.n2y0_api_key);
                }
            }
        }
        return sInstance;
    }

    /***********************************************************************************************
     * Satellite database dao methods
     **********************************************************************************************/

    /**
     * Inserts a satellite into the table
     */
    public void insert(final Satellite... satellite) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSatelliteDao.insert(satellite);
            }
        });
    }

    /**
     * Deletes all satellites from the table
     */
    public void deleteAllSatellites() {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mSatelliteDao.deleteAll();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a given satellite from the table
     */
    public void delete(final Satellite... satellite) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSatelliteDao.delete(satellite);
            }
        });
    }

    /**
     * Returns all satellites in table for paging based on a dynamic SQL query
     */
    public LiveData<PagedList<Satellite>> getSatellites(final String category, final Boolean showOnlyFavorites) {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mListOfSatellites = new LivePagedListBuilder<>(mSatelliteDao.getSatellites
                            (DataUtils.getSatellites(category, showOnlyFavorites)), PAGE_SIZE).build();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return mListOfSatellites;
    }

    /**
     * Returns a Satellite based on the norad id
     */
    public LiveData<Satellite> getSatellite(int norad_id) {
        return mSatelliteDao.getSatellite(norad_id);
    }


    /**
     * Returns a random Satellite
     */
    public Satellite getRandomSatellite() {
        try {
            return (Satellite) mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mSatelliteDao.getRandomSatellite();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Updates Satellite data
     */
    public void updateSatellites(Satellite... satellites) {
        insert(satellites);
    }

    /***********************************************************************************************
     *Category database dao methods
     **********************************************************************************************/

    /**
     * Inserts a category into the table
     */
    public void insert(final SatelliteCategory... category) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mSatelliteCategoryDao.insert(category);
            }
        });
    }

    /**
     * Delete all categories from the table
     */
    public void deleteAllCategories() {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mSatelliteCategoryDao.deleteAll();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns all categories in the table for Paging
     */
    public LiveData<PagedList<SatelliteCategory>> getAll() {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mListOfSatelliteCategories = new LivePagedListBuilder<>(
                            mSatelliteCategoryDao.getAll(), PAGE_SIZE).build();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return mListOfSatelliteCategories;
    }

    /**
     * Returns a random Satellite Category
     */
    public SatelliteCategory getRandomCategory() {
        try {
            return (SatelliteCategory) mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mSatelliteCategoryDao.getRandomCategory();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    /***********************************************************************************************
     * Pass database dao methods
     **********************************************************************************************/

    /**
     * Deletes a given pass from the table
     */
    public void deletePass(final PassObject... passObject) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPassDao.delete(passObject);
            }
        });
    }

    /**
     * Delete all passes from the table
     */
    public void deleteAllPasses() {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mPassDao.deleteAll();
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a given pass into the table
     */
    public void insertPass(final PassObject... passObject) {
        mIoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mPassDao.insert(passObject);
            }
        });
    }

    /**
     * Returns next visual pass
     */
    public LiveData<PassObject> getNextPass() {
        return mPassDao.getNextPass();
    }


    /***********************************************************************************************
     * Web api methods
     **********************************************************************************************/

    /**
     * Gets data about satellites above the user's location
     */
    public MutableLiveData<WhatsUp> getWhatsUp(final double lat, final double lng, final double alt,
                                               final int rad, final int cat) {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mWebApi.getWhatsUp(lat, lng, alt, rad, cat, API_KEY).enqueue(new Callback<WhatsUp>() {
                        @Override
                        public void onResponse(Call<WhatsUp> call, Response<WhatsUp> response) {
                            mWhatsUp.postValue(response.body());
                        }

                        @Override
                        public void onFailure(Call<WhatsUp> call, Throwable t) {
                            mWhatsUp.postValue(null);
                            call.cancel();
                        }
                    });
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return mWhatsUp;
    }

    /**
     * Gets satellite path data
     */
    public MutableLiveData<SatellitePositions> getSatPositions(final int id, final double lat, final double lng,
                                                               final double alt, final int secs) {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mWebApi.getSatellitePositions(id, lat, lng, alt, secs, API_KEY).enqueue(new Callback<SatellitePositions>() {
                        @Override
                        public void onResponse(Call<SatellitePositions> call, Response<SatellitePositions> response) {
                            mSatellitePositions.postValue(response.body());

                        }

                        @Override
                        public void onFailure(Call<SatellitePositions> call, Throwable t) {
                            mSatellitePositions.postValue(null);
                            call.cancel();
                        }
                    });
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return mSatellitePositions;
    }

    /**
     * Gets visual passes for favourite satellites
     */
    public LiveData<VisualPasses> getVisualPasses(final int id, final double lat, final double lng,
                                                  final double alt, final int days, final int minVisibility) {
        try {
            mIoExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    mWebApi.getVisualPasses(id, lat, lng, alt, days, minVisibility, API_KEY).enqueue(new Callback<VisualPasses>() {
                        @Override
                        public void onResponse(Call<VisualPasses> call, Response<VisualPasses> response) {
                            mVisualPasses.postValue(response.body());
                        }

                        @Override
                        public void onFailure(Call<VisualPasses> call, Throwable t) {
                            mVisualPasses.postValue(null);
                            call.cancel();
                        }
                    });
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return mVisualPasses;
    }

    /*
     * returns WebApi object needed for retrofit calls
     */
    public WebApi getWebApi() {
        return mWebApi;
    }

    /*
     * returns n2y0 api key
     */
    public String getApiKey() {
        return API_KEY;
    }

    /*
     * clears the last retrieved satellite pass data. This is important because there is only
     * one instance of the data repository and if it is not cleared, it would persist across
     * activities.
     */
    public void clearPathData() {
        mSatellitePositions.postValue(null);
    }

}

















