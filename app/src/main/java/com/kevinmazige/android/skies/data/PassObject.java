package com.kevinmazige.android.skies.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


/**
 * A Model class that holds information about passes.
 * This class defines the table for the Room database with primary key
 */

@Entity(tableName = DataPassObjectNames.TABLE_NAME)
public class PassObject {

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = DataPassObjectNames.COL_ID)
    private int norad_id;

    @ColumnInfo(name = DataPassObjectNames.COL_START)
    private Long start;

    @ColumnInfo(name = DataPassObjectNames.COL_END)
    private Long end;

    public PassObject(int norad_id, Long start, Long end) {
        this.norad_id = norad_id;
        this.start = start;
        this.end = end;
    }

    public int getNorad_id() {
        return norad_id;
    }

    public Long getEnd() {
        return end;
    }

    public Long getStart() {
        return start;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public void setNorad_id(int norad_id) {
        this.norad_id = norad_id;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
