/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal;

import com.infomatiq.jsi.Rectangle;

import com.jme3.math.Vector3f;

import javax.vecmath.Point3f;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class Coord {

    //~ Instance fields --------------------------------------------------------

    private double x;
    private double y;
    private double z;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Coord object.
     *
     * @param  c  DOCUMENT ME!
     */
    public Coord(final Coord c) {
        this.x = c.x;
        this.y = c.y;
        this.z = c.z;
    }

    /**
     * Creates a new Coord object.
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     * @param  z  DOCUMENT ME!
     */
    public Coord(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a new Coord object.
     *
     * @param  x  DOCUMENT ME!
     * @param  y  DOCUMENT ME!
     * @param  z  DOCUMENT ME!
     */
    public Coord(final String x, final String y, final String z) {
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
        this.z = Double.parseDouble(z);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double x() {
        return x;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double y() {
        return y;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double z() {
        return z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  x  DOCUMENT ME!
     */
    public void x(final double x) {
        this.x = x;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  y  DOCUMENT ME!
     */
    public void y(final double y) {
        this.y = y;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  z  DOCUMENT ME!
     */
    public void z(final double z) {
        this.z = z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getDist3d(final Coord c) {
        return Math.sqrt(Math.pow(c.x - x, 2) + Math.pow(c.y - y, 2) + Math.pow(c.z - z, 2));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getDist2d(final Coord c) {
        return Math.sqrt(Math.pow(c.x - x, 2) + Math.pow(c.y - y, 2));
    }

    @Override
    public String toString() {
        return "[" + x + ";" + y + ";" + z + "]";
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void minus(final Coord c) {
        x -= c.x;
        y -= c.y;
        z -= c.z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void plus(final Coord c) {
        x += c.x;
        y += c.y;
        z += c.z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  _x  DOCUMENT ME!
     * @param  _y  DOCUMENT ME!
     * @param  _z  DOCUMENT ME!
     */
    public void minus(final double _x, final double _y, final double _z) {
        x -= _x;
        y -= _y;
        z -= _z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  _x  DOCUMENT ME!
     * @param  _y  DOCUMENT ME!
     * @param  _z  DOCUMENT ME!
     */
    public void plus(final double _x, final double _y, final double _z) {
        x += _x;
        y += _y;
        z += _z;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  _x  DOCUMENT ME!
     * @param  _y  DOCUMENT ME!
     */
    public void minus(final double _x, final double _y) {
        minus(_x, _y, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  _x  DOCUMENT ME!
     * @param  _y  DOCUMENT ME!
     */
    public void plus(final double _x, final double _y) {
        plus(_x, _y, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   _x  DOCUMENT ME!
     * @param   _y  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord plusNew(final double _x, final double _y) {
        return new Coord(x + _x, y + _y, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Rectangle getAsRectangle() {
        return new Rectangle((float)x, (float)y, (float)x, (float)y);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord round() {
        return new Coord(Math.round(x()), Math.round(y()), Math.round(z()));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getVec3f() {
        return new Vector3f((float)x, (float)y, (float)z);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Point3f getPoint3f() {
        return new Point3f((float)x, (float)y, (float)z);
    }
}
