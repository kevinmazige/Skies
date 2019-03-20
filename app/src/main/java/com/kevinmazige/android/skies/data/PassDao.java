package com.kevinmazige.android.skies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

/**
 * Room data access object
 */
@Dao
public interface PassDao {

    /**
     * Inserts a visual pass object into the table
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PassObject... passObject);

    /**
     * Deletes all visual pass objects from the table
     */
    @Query("DELETE FROM pass")
    void deleteAll();

    /**
     * Deletes a given pass from the table
     */
    @Delete
    void delete(PassObject... passObject);

    /**
     * Returns the next Visual Pass
     */
    @Query("SELECT * FROM pass ORDER BY start ASC LIMIT 1")
    LiveData<PassObject> getNextPass();

}
