package com.kevinmazige.android.skies.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * A Model class that holds information about satellites.
 * This class defines the table for the Room database with primary key
 */

@Entity(tableName = DataSatelliteNames.TABLE_NAME)
public class Satellite {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = DataSatelliteNames.COL_NORAD_ID)
    private Integer mNoradId;

    @ColumnInfo(name = DataSatelliteNames.COL_NAME)
    @NonNull
    private String mName;

    @ColumnInfo(name = DataSatelliteNames.COL_INTERNATIONAL_CODE)
    private String mInternationalCode;

    @ColumnInfo(name = DataSatelliteNames.LAUNCH_DATE)
    private String mLaunchDate;

    //period in minutes
    @ColumnInfo(name = DataSatelliteNames.COL_PERIOD)
    private String mPeriod;

    @ColumnInfo(name = DataSatelliteNames.COL_STATUS)
    private String mStatus;

    //frequency in MHz
    @ColumnInfo(name = DataSatelliteNames.COL_BEACON)
    private String mBeaconMhz;

    @ColumnInfo(name = DataSatelliteNames.COL_CATEGORY)
    private String mCategory;

    @ColumnInfo(name = DataSatelliteNames.COL_MAGNITUDE)
    private String mMagnitude;

    @ColumnInfo(name = DataSatelliteNames.COL_PRN)
    private String mPRN;

    @ColumnInfo(name = DataSatelliteNames.COL_LONGITUDE)
    private String mLongitude;

    @ColumnInfo(name = DataSatelliteNames.COL_SV)
    private String mSV;

    @ColumnInfo(name = DataSatelliteNames.COL_FAVORITE)
    private Boolean mFavourite;

    public Satellite(@NonNull Integer noradId, @NonNull String name, String internationalCode,
                     String launchDate, String period, String status, String beaconMhz, String category,
                     String magnitude, String pRN, String longitude, String sV, Boolean favourite) {

        this.mNoradId = noradId;
        this.mName = name;
        this.mInternationalCode = internationalCode;
        this.mLaunchDate = launchDate;
        this.mPeriod = period;
        this.mStatus = status;
        this.mBeaconMhz = beaconMhz;
        this.mCategory = category;
        this.mMagnitude = magnitude;
        this.mPRN = pRN;
        this.mLongitude = longitude;
        this.mSV = sV;
        this.mFavourite = favourite;
    }

    /**
     * Getter methods
     */

    @NonNull
    public Integer getNoradId() {
        return mNoradId;
    }

    @NonNull
    public String getName() {
        return mName;
    }

    public String getInternationalCode() {
        return mInternationalCode;
    }

    public String getLaunchDate() {
        return mLaunchDate;
    }

    public String getPeriod() {
        return mPeriod;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getBeaconMhz() {
        return mBeaconMhz;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getMagnitude() {
        return mMagnitude;
    }

    public String getPRN() {
        return mPRN;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getSV() {
        return mSV;
    }

    public Boolean getFavourite() {
        return mFavourite;
    }

    /**
     * Setter methods
     */

    public void setNoradId(@NonNull int mNoradId) {
        this.mNoradId = mNoradId;
    }

    public void setName(@NonNull String mName) {
        this.mName = mName;
    }

    public void setInternationalCode(String mInternationalCode) {
        this.mInternationalCode = mInternationalCode;
    }

    public void setLaunchDate(String mLaunchDate) {
        this.mLaunchDate = mLaunchDate;
    }

    public void setPeriod(String mPeriod) {
        this.mPeriod = mPeriod;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public void setCategory(String mCategory) {
        this.mCategory = mCategory;
    }

    public void setBeaconMhz(String mBeaconMhz) {
        this.mBeaconMhz = mBeaconMhz;
    }

    public void setMagnitude(String mMagnitude) {
        this.mMagnitude = mMagnitude;
    }

    public void setPRN(String mPRN) {
        this.mPRN = mPRN;
    }

    public void setLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setSV(String mSV) {
        this.mSV = mSV;
    }

    public void setFavourite(Boolean mIsFavourite) {
        this.mFavourite = mIsFavourite;
    }
}




























