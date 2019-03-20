package com.kevinmazige.android.skies.data;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Room data access object
 */

@Dao
public interface SatelliteCategoryDao {

    /**
     * Inserts a category into the table
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SatelliteCategory... category);

    /**
     * Delete all categories from the table
     */
    @Query("DELETE FROM satellite_category")
    void deleteAll();

    /**
     * Returns all categories in the table for Paging
     */
    @Query("SELECT * FROM satellite_category ORDER BY name ASC")
    DataSource.Factory<Integer, SatelliteCategory> getAll();

    /**
     * Returns a random Satellite Category
     */
    @Query("SELECT * FROM satellite_category ORDER BY random() LIMIT 1")
    SatelliteCategory getRandomCategory();
}
