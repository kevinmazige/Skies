package com.kevinmazige.android.skies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * A Model class that holds information about satellite categories.
 * This class defines the table for the Room database with primary key
 */

@Entity(tableName = DataSatelliteCategoryNames.TABLE_NAME)
public class SatelliteCategory {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DataSatelliteCategoryNames.COL_ID)
    private int mId;

    @ColumnInfo(name = DataSatelliteCategoryNames.COL_NAME)
    private String mName;

    public SatelliteCategory(@NonNull String name, @NonNull int id) {
        this.mId = id;
        this.mName = name;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
