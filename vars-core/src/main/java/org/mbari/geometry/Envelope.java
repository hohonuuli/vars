/*
 * @(#)Envelope.java   2010.02.18 at 01:38:24 PST
 *
 * Copyright 2009 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.mbari.geometry;

import java.awt.geom.Rectangle2D;

/**
 * Represents a rectangular area (
 * @author brian
 *
 * @param <T>
 */
public class Envelope<T extends Number> {

    private final Point2D<T> corner0;
    private final Point2D<T> corner1;
    private Point2D<T> maximum;
    private Point2D<T> minimum;

    /**
     * Constructs ...
     *
     * @param corner0
     * @param corner1
     */
    public Envelope(Point2D<T> corner0, Point2D<T> corner1) {
        this.corner0 = corner0;
        this.corner1 = corner1;
        calculate();
    }

    /**
     * Constructs ...
     *
     * @param x0 x value of corner 1
     * @param y0 y value of corner 1
     * @param x1 x value of corner 2
     * @param y1 y value of corner 2
     */
    public Envelope(T x0, T y0, T x1, T y1) {
        this(new Point2D<T>(x0, y0), new Point2D<T>(x1, y1));
    }

    private void calculate() {
        T minX;
        T maxX;
        if (corner0.getX().doubleValue() < corner1.getX().doubleValue()) {
            minX = corner0.getX();
            maxX = corner1.getX();
        }
        else {
            minX = corner1.getX();
            maxX = corner0.getX();

        }

        T minY;
        T maxY;
        if (corner0.getY().doubleValue() < corner1.getY().doubleValue()) {
            minY = corner0.getY();
            maxY = corner1.getY();
        }
        else {
            minY = corner1.getY();
            maxY = corner0.getY();

        }

        minimum = new Point2D<T>(minX, minY);
        maximum = new Point2D<T>(maxX, maxY);
    }

    /**
     *
     * @param p
     * @return
     */
    public boolean contains(Point2D<T> p) {

        double minX = minimum.getX().doubleValue();
        double maxX = maximum.getX().doubleValue();
        double minY = minimum.getY().doubleValue();
        double maxY = maximum.getY().doubleValue();
        double x = p.getX().doubleValue();
        double y = p.getY().doubleValue();

        return (x >= minX) && (x <= maxX) && (y >= minY) && (y <= maxY);

    }

    public double getWidth() {
        return getMaximum().getX().doubleValue() - getMinimum().getX().doubleValue();
    }

    public double getHeight() {
        return getMaximum().getY().doubleValue() - getMinimum().getY().doubleValue();
    }

    /**
     * @return
     */
    public Point2D<T> getCorner0() {
        return corner0;
    }

    /**
     * @return
     */
    public Point2D<T> getCorner1() {
        return corner1;
    }

    /**
     * @return
     */
    public Point2D<T> getMaximum() {
        return maximum;
    }

    /**
     * @return
     */
    public Point2D<T> getMinimum() {
        return minimum;
    }

    public Rectangle2D asRectangle() {
        return new Rectangle2D.Double(getMinimum().getX().doubleValue(), getMinimum().getY().doubleValue(), getWidth(), getHeight());
    }
    
}
