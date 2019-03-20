package com.kevinmazige.android.skies.api;

import java.util.List;

/**
 * This POJO class is used by the retrofit library to parse the satellite positions JSON response
 * received from the n2yo web api.
 */

public class SatellitePositions {
    // Since the member variables are private, retrofit requires public setters and getters
    private MetaData info;
    private List<Positions> positions = null;

    //Getter methods
    public MetaData getInfo() {
        return info;
    }

    public List<Positions> getPositions() {
        return positions;
    }

    //Setter methods
    public void setInfo(MetaData info) {
        this.info = info;
    }

    public void setPositions(List<Positions> positions) {
        this.positions = positions;
    }

    //These inner classes are used to represent the nested objects in the JSON response
    public class MetaData {
        private String satname;
        private int satid;
        private int transactionscount;

        public String getSatname() {
            return satname;
        }

        public int getSatid() {
            return satid;
        }

        public int getTransactionscount() {
            return transactionscount;
        }

        public void setSatname(String satname) {
            this.satname = satname;
        }

        public void setSatid(int satid) {
            this.satid = satid;
        }

        public void setTransactionscount(int transactionscount) {
            this.transactionscount = transactionscount;
        }

    }

    public class Positions {
        private double satlatitude;
        private double satlongitude;
        private double sataltitude;
        private double azimuth;
        private double elevation;
        private double ra;
        private double dec;
        private long timestamp;

        public double getSataltitude() {
            return sataltitude;
        }

        public double getSatlatitude() {
            return satlatitude;
        }

        public double getSatlongitude() {
            return satlongitude;
        }

        public double getAzimuth() {
            return azimuth;
        }

        public double getElevation() {
            return elevation;
        }

        public double getRa() {
            return ra;
        }

        public double getDec() {
            return dec;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setSatlatitude(double satlatitude) {
            this.satlatitude = satlatitude;
        }

        public void setSatlongitude(double satlongitude) {
            this.satlongitude = satlongitude;
        }

        public void setSataltitude(double sataltitude) {
            this.sataltitude = sataltitude;
        }

        public void setAzimuth(double azimuth) {
            this.azimuth = azimuth;
        }

        public void setElevation(double elevation) {
            this.elevation = elevation;
        }

        public void setRa(double ra) {
            this.ra = ra;
        }

        public void setDec(double dec) {
            this.dec = dec;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
