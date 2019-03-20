package com.kevinmazige.android.skies.api;

import java.util.List;

/**
 * This POJO class is used by the retrofit library to parse the visual passes JSON response received
 * from the n2yo web api.
 */
public class VisualPasses {

    // Since the member variables are private, retrofit requires public setters and getters
    private MetaData info;
    private List<Pass> passes;

    //Getter methods
    public MetaData getInfo() {
        return info;
    }

    public List<Pass> getPasses() {
        return passes;
    }

    //Setter methods
    public void setInfo(MetaData info) {
        this.info = info;
    }

    public void setPasses(List<Pass> passes) {
        this.passes = passes;
    }

    //These inner classes are used to represent the nested objects in the JSON response
    public class MetaData {
        private int satid;
        private String satname;
        private int transactionscount;
        private int passescount;

        public MetaData(int satid, String satname, int transactionscount, int passescount) {
            this.satid = satid;
            this.satname = satname;
            this.transactionscount = transactionscount;
            this.passescount = passescount;
        }

        public int getSatid() {
            return satid;
        }

        public String getSatname() {
            return satname;
        }

        public int getTransactionscount() {
            return transactionscount;
        }

        public int getPassescount() {
            return passescount;
        }

        public void setSatid(int satid) {
            this.satid = satid;
        }

        public void setSatname(String satname) {
            this.satname = satname;
        }

        public void setTransactionscount(int transactionscount) {
            this.transactionscount = transactionscount;
        }

        public void setPassescount(int passescount) {
            this.passescount = passescount;
        }
    }

    public class Pass {
        private double startAz;
        private String startAzCompass;
        private double startEl;
        private long startUTC;
        private double maxAz;
        private String maxAzCompass;
        private double maxEl;
        private long maxUTC;
        private double endAz;
        private String endAzCompass;
        private double endEl;
        private long endUTC;
        private double mag;
        private int duration;

        public double getStartAz() {
            return startAz;
        }

        public String getStartAzCompass() {
            return startAzCompass;
        }

        public double getStartEl() {
            return startEl;
        }

        public long getStartUTC() {
            return startUTC;
        }

        public double getMaxAz() {
            return maxAz;
        }

        public String getMaxAzCompass() {
            return maxAzCompass;
        }

        public double getMaxEl() {
            return maxEl;
        }

        public long getMaxUTC() {
            return maxUTC;
        }

        public double getEndAz() {
            return endAz;
        }

        public String getEndAzCompass() {
            return endAzCompass;
        }

        public double getEndEl() {
            return endEl;
        }

        public long getEndUTC() {
            return endUTC;
        }

        public double getMag() {
            return mag;
        }

        public int getDuration() {
            return duration;
        }

        public void setStartAz(double startAz) {
            this.startAz = startAz;
        }

        public void setStartAzCompass(String startAzCompass) {
            this.startAzCompass = startAzCompass;
        }

        public void setStartEl(double startEl) {
            this.startEl = startEl;
        }

        public void setStartUTC(long startUTC) {
            this.startUTC = startUTC;
        }

        public void setMaxAz(double maxAz) {
            this.maxAz = maxAz;
        }

        public void setMaxAzCompass(String maxAzCompass) {
            this.maxAzCompass = maxAzCompass;
        }

        public void setMaxEl(double maxEl) {
            this.maxEl = maxEl;
        }

        public void setMaxUTC(long maxUTC) {
            this.maxUTC = maxUTC;
        }

        public void setEndAz(double endAz) {
            this.endAz = endAz;
        }

        public void setEndAzCompass(String endAzCompass) {
            this.endAzCompass = endAzCompass;
        }

        public void setEndEl(double endEl) {
            this.endEl = endEl;
        }

        public void setEndUTC(long endUTC) {
            this.endUTC = endUTC;
        }

        public void setMag(double mag) {
            this.mag = mag;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }
    }
}

