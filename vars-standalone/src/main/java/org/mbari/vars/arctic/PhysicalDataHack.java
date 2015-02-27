package org.mbari.vars.arctic;

import vars.annotation.PhysicalData;

/**
 * Scala Floats and Double are instances of AnyVal which does not allow the
 * values to be assigned to nulls. JPA on the other hand uses nulls just fine
 * and sets the database value to NULL when they are present.
 *
 * As a workaround this class allows null values to be set. We can call this hack
 * from Scala. If I need more of this type of hack I should just use reflection
 * in the future.
 *
 * @author Brian Schlining
 * @since 2015-02-27T09:41:00
 */
public class PhysicalDataHack {

    public static float BAD_FLOAT = Float.NaN;
    public static double BAD_DOUBLE = Double.NaN;


    public static void setDepth(PhysicalData pd, float depth) {
        if (Float.isNaN(depth)) {
            pd.setDepth(null);
        }
        else {
            pd.setDepth(depth);
        }
    }

    public static void setSalinity(PhysicalData pd, float salinity) {
        if (Float.isNaN(salinity)) {
            pd.setSalinity(null);
        }
        else {
            pd.setSalinity(salinity);
        }
    }

    public static void setTemperature(PhysicalData pd, float temperature) {
        if (Float.isNaN(temperature)) {
            pd.setTemperature(null);
        }
        else {
            pd.setTemperature(temperature);
        }
    }

    public static void setLatitude(PhysicalData pd, double latitude) {
        if (Double.isNaN(latitude)) {
            pd.setLatitude(null);
        }
        else {
            pd.setLatitude(latitude);
        }
    }

    public static void setLongitude(PhysicalData pd, double longitude) {
        if (Double.isNaN(longitude)) {
            pd.setLongitude(null);
        }
        else {
            pd.setLongitude(longitude);
        }
    }



}
