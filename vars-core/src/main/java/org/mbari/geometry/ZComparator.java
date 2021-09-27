/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.geometry;

import java.util.Comparator;

/**
 * Compares the Z values in a Point3D
 * 
 * @author brian
 */
public class ZComparator implements Comparator<Point3D> {

    public int compare(Point3D o1, Point3D o2) {
        Double n1 = ((Number) o1.getZ()).doubleValue();
        Double n2 = ((Number) o2.getZ()).doubleValue();
        return n1.compareTo(n2);
    }

}
