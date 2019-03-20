package com.kevinmazige.android.skies.api;

import java.util.List;

/**
 * This POJO class is used by the retrofit library to parse the what's up JSON response received
 * from the n2yo web api.
 */

public class WhatsUp {

    // Since the member variables are private, retrofit requires public setters and getters
    private MetaData info;
    private List<Satellite> above = null;

    public WhatsUp(MetaData info, List<Satellite> above) {
        this.info = info;
        this.above = above;
    }

    //Getter methods
    public MetaData getInfo() {
        return info;
    }

    public List<Satellite> getSatellites() {
        return above;
    }

    //Setter methods
    public void setInfo(MetaData info) {
        this.info = info;
    }

    public void setAbove(List<Satellite> above) {
        this.above = above;
    }

    //These inner classes are used to represent the nested objects in the JSON response
    public class MetaData {
        private String category;
        private int transactionscount;
        private int satcount;

        public MetaData(String category, int transactionscount, int satcount) {
            this.category = category;
            this.transactionscount = transactionscount;
            this.satcount = satcount;
        }

        public String getCategory() {
            return category;
        }

        public int getTransactionscount() {
            return transactionscount;
        }

        public int getSatcount() {
            return satcount;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setTransactionscount(int transactionscount) {
            this.transactionscount = transactionscount;
        }

        public void setSatcount(int satcount) {
            this.satcount = satcount;
        }
    }

    public class Satellite {
        private int satid;
        private String satname;
        private String intDesignator;
        private String launchDate;
        private double satlat;
        private double satlng;
        private double satalt;

        public Satellite(int satid, String satname, String intDesignator, String launchDate,
                         double satlat, double satlng, double satalt) {
            this.satid = satid;
            this.satname = satname;
            this.intDesignator = intDesignator;
            this.launchDate = launchDate;
            this.satlat = satlat;
            this.satlng = satlng;
            this.satalt = satalt;
        }

        public int getSatid() {
            return satid;
        }

        public String getSatname() {
            return satname;
        }

        public String getIntDesignator() {
            return intDesignator;
        }

        public String getLaunchDate() {
            return launchDate;
        }

        public double getSatlat() {
            return satlat;
        }

        public double getSatlng() {
            return satlng;
        }

        public double getSatalt() {
            return satalt;
        }

        public void setSatid(int satid) {
            this.satid = satid;
        }

        public void setSatname(String satname) {
            this.satname = satname;
        }

        public void setIntDesignator(String intDesignator) {
            this.intDesignator = intDesignator;
        }

        public void setLaunchDate(String launchDate) {
            this.launchDate = launchDate;
        }

        public void setSatlat(double satlat) {
            this.satlat = satlat;
        }

        public void setSatlng(double satlng) {
            this.satlng = satlng;
        }

        public void setSatalt(double satalt) {
            this.satalt = satalt;
        }
    }
}