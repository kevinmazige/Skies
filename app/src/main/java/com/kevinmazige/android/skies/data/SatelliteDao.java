package com.kevinmazige.android.skies.data;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;

import java.util.List;

/**
 * Room data access object
 */

@Dao
public interface SatelliteDao {

    /**
     * Inserts a satellite into the table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Satellite... satellite);

    /**
     * Deletes all satellites from the table
     */
    @Query("DELETE FROM satellite")
    void deleteAll();

    /**
     * Deletes a given satellite from the table
     */
    @Delete
    void delete(Satellite... satellite);

    /**
     * Returns all satellites in table for paging based on a dynamic SQL query
     */
    @RawQuery(observedEntities = Satellite.class)
    DataSource.Factory<Integer, Satellite> getSatellites(SupportSQLiteQuery query);

    /**
     * Returns favourite satellites synchronously
     */
    @Query("SELECT * FROM satellite WHERE favorite = 1 ORDER BY name")
    LiveData<List<Satellite>> getFavouriteSatellitesSynchronously();


    /**
     * Returns a Satellite based on the norad id
     */
    @Query("SELECT * FROM satellite WHERE NORAD_ID = :norad_id")
    LiveData<Satellite> getSatellite(int norad_id);


    /**
     * Returns a random Satellite
     */
    @Query("SELECT * FROM satellite ORDER BY random() LIMIT 1")
    Satellite getRandomSatellite();

}
