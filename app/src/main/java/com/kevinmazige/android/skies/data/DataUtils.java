package com.kevinmazige.android.skies.data;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;
import android.support.annotation.NonNull;

import com.kevinmazige.android.skies.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUtils {

    /**
     * Data for category table
     */
    public static final List<SatelliteCategory> CATEGORIES = new ArrayList<SatelliteCategory>(
            Arrays.asList(
                    new SatelliteCategory("All", 0),
                    new SatelliteCategory("Amateur radio", 18),
                    new SatelliteCategory("Beidou Navigation System", 35),
                    new SatelliteCategory("Brightest", 1),
                    new SatelliteCategory("Celestis", 45),
                    new SatelliteCategory("CubeSats", 32),
                    new SatelliteCategory("Disaster monitoring", 8),
                    new SatelliteCategory("Earth resources", 6),
                    new SatelliteCategory("Education", 29),
                    new SatelliteCategory("Engineering", 28),
                    new SatelliteCategory("Experimental", 19),
                    new SatelliteCategory("Flock", 48),
                    new SatelliteCategory("Galileo", 22),
                    new SatelliteCategory("Geodetic", 27),
                    new SatelliteCategory("Geostationary", 10),
                    new SatelliteCategory("Global Positioning System (GPS) Constellation", 50),
                    new SatelliteCategory("Global Positioning System (GPS) Operational", 20),
                    new SatelliteCategory("Globalstar", 17),
                    new SatelliteCategory("Glonass Operational", 21),
                    new SatelliteCategory("GOES", 5),
                    new SatelliteCategory("Gonets", 40),
                    new SatelliteCategory("Gorizont", 12),
                    new SatelliteCategory("Intelsat", 11),
                    new SatelliteCategory("Iridium", 15),
                    new SatelliteCategory("IRNSS", 46),
                    new SatelliteCategory("ISS", 2),
                    new SatelliteCategory("Lemur", 49),
                    new SatelliteCategory("Military", 30),
                    new SatelliteCategory("Molniya", 14),
                    new SatelliteCategory("Navy Navigation Satellite System", 24),
                    new SatelliteCategory("NOAA", 4),
                    new SatelliteCategory("O3B Networks", 43),
                    new SatelliteCategory("Orbcomm", 16),
                    new SatelliteCategory("Parus", 38),
                    new SatelliteCategory("QZSS", 47),
                    new SatelliteCategory("Radar Calibration", 31),
                    new SatelliteCategory("Raduga", 13),
                    new SatelliteCategory("Russian LEO Navigation", 25),
                    new SatelliteCategory("Satellite-Based Augmentation System", 23),
                    new SatelliteCategory("Search & rescue", 7),
                    new SatelliteCategory("Space & Earth Science", 26),
                    new SatelliteCategory("Strela", 39),
                    new SatelliteCategory("Tracking and Data Relay Satellite System", 9),
                    new SatelliteCategory("Tselina", 44),
                    new SatelliteCategory("Tsikada", 42),
                    new SatelliteCategory("Tsiklon", 41),
                    new SatelliteCategory("TV", 34),
                    new SatelliteCategory("Weather", 3),
                    new SatelliteCategory("westford Needles", 37),
                    new SatelliteCategory("XM and Sirius", 33),
                    new SatelliteCategory("Yaogan", 36)));

    /*
     * A raw query at runtime for getting satellites by categories or by favorite status
     */
    public static SimpleSQLiteQuery getSatellites(String category, Boolean showOnlyFavorites) {

        SimpleSQLiteQuery query;

        if (category.equals("All")) {
            if (showOnlyFavorites == true) {

                //show only favourite satellites (they can be of any category)
                query = new SimpleSQLiteQuery("SELECT * FROM satellite WHERE favorite = ? " +
                        "ORDER BY name", new Object[]{"1"});
            } else {
                //show all satellites
                query = new SimpleSQLiteQuery("SELECT * FROM satellite ORDER BY name");
            }
        } else {
            if (showOnlyFavorites == true) {
                //show only the favourite satellites in a certain category
                query = new SimpleSQLiteQuery("SELECT * FROM satellite WHERE category = ? AND" +
                        " favorite = ? ORDER BY name", new Object[]{category, "1"});
            } else {
                //select all satellites by category
                query = new SimpleSQLiteQuery("SELECT * FROM satellite WHERE category = ? " +
                        "ORDER BY name", new Object[]{category});
            }
        }
        return query;
    }

    /**
     * Returns a list of every satellite that will go into the database. This data is retrieved
     * from a csv file in res/raw
     */
    public static List<Satellite> parseCSV(@NonNull Context context) {
        final String DELIMITER = ",";
        List<Satellite> mSatelliteData = new ArrayList<>();

        InputStream inputStream = context.getResources().openRawResource(R.raw.satellite_data);

        BufferedReader bufferedReader = null;
        String line;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            bufferedReader.readLine();
            while ((line = bufferedReader.readLine()) != null) {

                String[] objectData = line.split(DELIMITER);

                Integer id;
                String name, intlCode, launchDate, period, status, beacon, category, magnitude, prn;
                String longitude, sv;
                Boolean favourite;

                if (objectData.length >= 1 && objectData[0].length() > 0) {
                    id = Integer.parseInt(objectData[0]);
                } else {
                    //if there is no id, then move on to the next line
                    continue;
                }

                if (objectData.length >= 2 && objectData[1].length() > 0) {
                    name = objectData[1];
                } else {
                    name = null;
                }

                if (objectData.length >= 3 && objectData[2].length() > 0) {
                    intlCode = objectData[2];
                } else {
                    intlCode = null;
                }

                if (objectData.length >= 4 && objectData[3].length() > 0) {
                    launchDate = objectData[3];
                } else {
                    launchDate = null;
                }

                if (objectData.length >= 5 && objectData[4].length() > 0) {
                    period = objectData[4];
                } else {
                    period = null;
                }

                if (objectData.length >= 6 && objectData[5].length() > 0) {
                    status = objectData[5];
                } else {
                    status = null;
                }

                if (objectData.length >= 7 && objectData[6].length() > 0) {
                    beacon = objectData[6];
                } else {
                    beacon = null;
                }

                if (objectData.length >= 8 && objectData[7].length() > 0) {
                    category = objectData[7];
                } else {
                    category = null;
                }

                if (objectData.length >= 9 && objectData[8].length() > 0) {
                    magnitude = objectData[8];
                } else {
                    magnitude = null;
                }

                if (objectData.length >= 10 && objectData[9].length() > 0) {
                    prn = objectData[9];
                } else {
                    prn = null;
                }

                if (objectData.length >= 11 && objectData[10].length() > 0) {
                    longitude = objectData[10];
                } else {
                    longitude = null;
                }

                if (objectData.length >= 12 && objectData[11].length() > 0) {
                    sv = objectData[11];
                } else {
                    sv = null;
                }

                if (objectData.length >= 13 && objectData[12].length() > 0) {
                    favourite = Boolean.valueOf(objectData[12]);
                } else {
                    favourite = false;
                }

                Satellite satObject = new Satellite(id, name, intlCode, launchDate, period, status,
                        beacon, category, magnitude, prn, longitude, sv, favourite);

                mSatelliteData.add(satObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return mSatelliteData;
    }

}
