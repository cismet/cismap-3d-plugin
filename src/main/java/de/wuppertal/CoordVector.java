/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal;

import java.awt.Polygon;

import java.util.Iterator;
import java.util.Vector;

import javax.vecmath.Point3f;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class CoordVector implements Iterable<Coord> {

    //~ Instance fields --------------------------------------------------------

    private Vector<Coord> coords;
    private BBox box;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CoordVector object.
     */
    public CoordVector() {
        coords = new Vector<Coord>();
        box = null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void add2d(final Coord c) {
        if (box == null) {
            box = new BBox(c, c);
        }
        box.grow2d(c);
        coords.add(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndexOf(final Coord c) {
        return coords.indexOf(c);
    }

    /**
     * WARNING No update of BBox!!!
     *
     * @param  c  DOCUMENT ME!
     */
    public void add(final Coord c) {
        coords.add(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void addFirst(final Coord c) {
        coords.add(0, c);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BBox getBBox() {
        return box;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<Coord> getCoords() {
        return coords;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int size() {
        return coords.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   index  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord getCoord(final int index) {
        return coords.get(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord getLast() {
        return coords.get(size() - 1);
    }

    /**
     * DOCUMENT ME!
     */
    public void updateBBox2d() {
        box = new BBox(coords.firstElement(), coords.firstElement());
        for (final Coord c : coords) {
            box.grow2d(c);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void updateBBox3d() {
        box = new BBox(coords.firstElement(), coords.firstElement());
        for (final Coord c : coords) {
            box.grow3d(c);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord remove(final int i) {
        final Coord c = coords.remove(i);
        updateBBox2d();
        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord reduce2Min() {
        final double minx = box.getMinX();
        final double miny = box.getMinY();
        for (final Coord c : coords) {
            c.minus(minx, miny);
        }
        updateBBox2d();
        return new Coord(minx, miny, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord reduce2Min3D() {
        final double minx = box.getMinX();
        final double miny = box.getMinY();
        final double minz = box.getMinZ();
        for (final Coord c : coords) {
            c.minus(minx, miny);
        }
        updateBBox3d();
        return new Coord(minx, miny, minz);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord reduce2Min_3Donly() {
        final double minz = box.getMinZ();
        for (final Coord c : coords) {
            c.minus(0, 0, minz);
        }
        updateBBox3d();
        return new Coord(box.getMinX(), box.getMinY(), minz);
    }

    @Override
    public Iterator<Coord> iterator() {
        return coords.iterator();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  x             DOCUMENT ME!
     * @param  y             DOCUMENT ME!
     * @param  z             DOCUMENT ME!
     * @param  updateBBox2d  DOCUMENT ME!
     * @param  updateBBox3d  DOCUMENT ME!
     */
    public void plusAll(final double x,
            final double y,
            final double z,
            final boolean updateBBox2d,
            final boolean updateBBox3d) {
        for (final Coord c : coords) {
            c.plus(x, y, z);
        }
        if (updateBBox3d) {
            updateBBox3d();
        } else if (updateBBox2d) {
            updateBBox2d();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   outerBorder  Punkte außerhalb werden ignoriert, Punkte innerhalb werden weiter verwendet
     * @param   innerBorder  Punkte innerhalb der inner Border wird zOffset Höhenwert abgezogen
     * @param   zOffset      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector getFiltered(final Polygon outerBorder, final Polygon innerBorder, final double zOffset) {
        final CoordVector ret = new CoordVector();
        int iCoordsDone = 0;
        for (final Coord c : coords) {
            if (outerBorder.contains(c.x(), c.y())) {
                if (innerBorder.contains(c.x(), c.y())) {
                    ret.add(new Coord(c.x(), c.y(), c.z() - zOffset));
                } else {
                    ret.add(new Coord(c.x(), c.y(), c.z()));
                }
            }
            iCoordsDone++;
            if ((iCoordsDone % 1000) == 0) {
                System.out.println(iCoordsDone + "/" + coords.size());
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector getReversed() {
        final CoordVector vec = new CoordVector();
        for (int i = coords.size() - 1; i >= 0; i--) {
            vec.add(coords.get(i));
        }
        return vec;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Point3f[] getPoints() {
//              boolean doIgnoreLast
//              if(getCoord(0).getDist3d(getLast())<0.001){
//
//              }
        final Point3f[] points = new Point3f[size()];
        int i = 0;
        for (final Coord c : getCoords()) {
            points[i] = c.getPoint3f();

            i++;
        }

        return points;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector cloneFull() {
        final CoordVector ret = new CoordVector();
        for (final Coord c : getCoords()) {
            ret.add(new Coord(c));
        }
        return ret;
    }

    @Override
    public CoordVector clone() {
        final CoordVector ret = new CoordVector();
        ret.coords = (Vector<Coord>)coords.clone();
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  point3f  DOCUMENT ME!
     */
    public void add(final Point3f point3f) {
        add(new Coord(point3f.x, point3f.y, point3f.z));
    }
}
