/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mbari.geometry;

import java.util.Comparator;

/**
 *
 * @author brian
 */
public class YComparator implements Comparator<Point2D> {

    public int compare(Point2D o1, Point2D o2) {
        Double n1 = o1.getY().doubleValue();
        Double n2 = o2.getY().doubleValue();
        return n1.compareTo(n2);
    }

}
