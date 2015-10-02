/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class BBox {

    //~ Static fields/initializers ---------------------------------------------

    private static double DEFAULTNOZVALUE = -10000;

    //~ Instance fields --------------------------------------------------------

    private Coord ll;
    private Coord lr;
    private Coord ul;
    private Coord ur;
    private double minz;
    private double maxz;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BBox object.
     *
     * @param  ll  DOCUMENT ME!
     * @param  ur  DOCUMENT ME!
     */
    public BBox(final Coord ll, final Coord ur) {
        this.ll = new Coord(ll.x(), ll.y(), ll.z());
        this.ur = new Coord(ur.x(), ur.y(), ur.z());
        this.lr = new Coord(ur.x(), ll.y(), ll.z());
        this.ul = new Coord(ll.x(), ur.y(), ur.z());
        this.minz = DEFAULTNOZVALUE;
        this.maxz = DEFAULTNOZVALUE;
    }

    /**
     * Creates a new BBox object.
     *
     * @param  llx  DOCUMENT ME!
     * @param  lly  DOCUMENT ME!
     * @param  urx  DOCUMENT ME!
     * @param  ury  DOCUMENT ME!
     */
    public BBox(final double llx, final double lly, final double urx, final double ury) {
        this(new Coord(llx, lly, 0), new Coord(urx, ury, 0));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   wmsBBoxRequest  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BBox getBBoxByWMSValue(final String wmsBBoxRequest) {
        final StringTokenizer st = new StringTokenizer(wmsBBoxRequest, ",");
        final double llx = Double.parseDouble(st.nextToken());
        final double lly = Double.parseDouble(st.nextToken());
        final double urx = Double.parseDouble(st.nextToken());
        final double ury = Double.parseDouble(st.nextToken());
        return new BBox(llx, lly, urx, ury);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getWidth() {
        return ur.x() - ll.x();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getHeight() {
        return ur.y() - ll.y();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StringBuilder getAsWMSRequestParameter() {
        final StringBuilder bboxString = new StringBuilder();
        bboxString.append("BBOX=");
        bboxString.append(ll.x());
        bboxString.append(",");
        bboxString.append(ll.y());
        bboxString.append(",");
        bboxString.append(ur.x());
        bboxString.append(",");
        bboxString.append(ul.y());
        return bboxString;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   box     DOCUMENT ME!
     * @param   deltaX  DOCUMENT ME!
     * @param   deltaY  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Vector<BBox> getTiles(final BBox box, final double deltaX, final double deltaY) {
        final Vector<BBox> vecBBoxTiles = new Vector<BBox>();
        for (double xTemp = box.ll.x(); xTemp < box.ur.x(); xTemp += deltaX) {
            for (double yTemp = box.ll.y(); yTemp < box.ur.y(); yTemp += deltaY) {
                vecBBoxTiles.add(new BBox(xTemp, yTemp, xTemp + deltaX, yTemp + deltaY));
            }
        }
        return vecBBoxTiles;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   deltaX  DOCUMENT ME!
     * @param   deltaY  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<BBox> getTiles(final double deltaX, final double deltaY) {
        return BBox.getTiles(this, deltaX, deltaY);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bLinewise  DOCUMENT ME!
     * @param   deltaX     DOCUMENT ME!
     * @param   deltaY     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector createCoordsInBBox(final boolean bLinewise, final double deltaX, final double deltaY) {
        Coord[][] coords = null;
        if (bLinewise) {
            coords = createCoordsOfBBox2DLinewise(deltaX, deltaY);
        } else {
            coords = createCoordsOfBBox2DLinewise(deltaX, deltaY);
        }
        final CoordVector ret = new CoordVector();
        for (int i = 0; i < coords.length; i++) {
            for (int j = 0; j < coords[0].length; j++) {
                ret.add2d(coords[i][j]);
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   estimated2NSize          DOCUMENT ME!
     * @param   bAddDeltaXYAsFirstCoord  DOCUMENT ME!
     *
     * @return  2D Koordinaten innerhalb des Rasters. ACHTUNG
     */
    public CoordVector createCoordsInBBox(final int estimated2NSize, final boolean bAddDeltaXYAsFirstCoord) {
        final double widthX = getWidth() / ((double)estimated2NSize);
        final double widthY = getHeight() / ((double)estimated2NSize);

        final CoordVector cvec = createCoordsInBBox(true, widthX, widthY);
        System.out.println(cvec.size() + "  " + ((estimated2NSize + 1) * (estimated2NSize + 1)));
        if (bAddDeltaXYAsFirstCoord) {
            cvec.addFirst(new Coord(widthX, widthY, 1));
        }
        return cvec;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   deltaX  DOCUMENT ME!
     * @param   deltaY  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord[][] createCoordsOfBBox2DLinewise(final double deltaX, final double deltaY) {
        final int iCols = (int)(getWidth() / deltaX) + 1;
        final int iRows = (int)(getHeight() / deltaY) + 1;
        final Coord[][] ret = new Coord[iRows][iCols];
        int ix;
        int iy;
        ix = iy = 0;
        // for(double yTemp = ll.y();yTemp <=ur.y();yTemp+=deltaY ){
        // for(double xTemp = ll.x();xTemp <=ur.x();xTemp+=deltaX ){
        // ret[iy][ix] = new Coord(xTemp, yTemp, 0);
        // ix++;
        // }
        // iy++;
        // ix=0;
        // }
        double yTemp = ll.y();
        double xTemp = ll.x();
        for (iy = 0; iy < iCols; iy++) {
            for (ix = 0; ix < iRows; ix++) {
                ret[iy][ix] = new Coord(xTemp, yTemp, 0);
                xTemp += deltaX;
            }
            xTemp = ll.x();
            yTemp += deltaY;
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   deltaX  DOCUMENT ME!
     * @param   deltaY  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord[][] createCoordsOfBBox2DColumnwise(final double deltaX, final double deltaY) {
        final int iCols = (int)(getWidth() / deltaX) + 1;
        final int iRows = (int)(getHeight() / deltaY) + 1;
        final Coord[][] ret = new Coord[iCols][iRows];
        int ix;
        int iy;
        ix = iy = 0;
        // for(double xTemp = ll.x();xTemp <=ur.x();xTemp+=deltaX ){
        // for(double yTemp = ll.y();yTemp <=ur.y();yTemp+=deltaY ){
        // ret[ix][iy] = new Coord(xTemp, yTemp, 0);
        // ix++;
        // }
        // iy++;
        // ix=0;
        // }
        double yTemp = ll.y();
        double xTemp = ll.x();
        for (ix = 0; ix < iRows; ix++) {
            for (iy = 0; iy < iCols; iy++) {
                ret[ix][iy] = new Coord(xTemp, yTemp, 0);
                yTemp += deltaY;
            }
            yTemp = ll.y();
            xTemp += deltaX;
        }

        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean contains2d(final Coord c) {
        return (c.x() > ll.x()) && (c.x() < ur.x()) && (c.y() > ll.y()) && (c.y() < ur.y());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean containsOrTouch2d(final Coord c) {
        return contains2d(c) || touch2d(c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   c  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean touch2d(final Coord c) {
        final boolean bTouchLeft = (c.x() == ll.x()) && (c.y() >= ll.y()) && (c.y() <= ur.y());
        final boolean bTouchRight = (c.x() == ur.x()) && (c.y() >= ll.y()) && (c.y() <= ur.y());
        final boolean bTouchUpper = (c.y() == ll.y()) && (c.x() >= ll.x()) && (c.x() <= ur.x());
        final boolean bTouchBotom = (c.y() == ur.y()) && (c.x() >= ll.x()) && (c.x() <= ur.x());
        return bTouchLeft || bTouchRight || bTouchUpper || bTouchBotom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void grow2d(final Coord c) {
        if (!containsOrTouch2d(c)) {
            if (c.x() <= ll.x()) {
                ll.x(c.x());
                ul.x(c.x());
            } else if (c.x() >= ur.x()) {
                ur.x(c.x());
                lr.x(c.x());
            }
            if (c.y() <= ll.y()) {
                ll.y(c.y());
                lr.y(c.y());
            } else if (c.y() >= ur.y()) {
                ur.y(c.y());
                ul.y(c.y());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timesSize  DOCUMENT ME!
     */
    public void grow2d(final float timesSize) {
        final double width = getWidth();
        final double height = getHeight();

        ll.x(ll.x() - (width * timesSize));
        ul.x(ul.x() - (width * timesSize));

        ur.x(ur.x() + (width * timesSize));
        lr.x(lr.x() + (width * timesSize));

        ll.y(ll.y() - (height * timesSize));
        lr.y(lr.y() - (height * timesSize));
        ur.y(ur.y() + (height * timesSize));
        ul.y(ul.y() + (height * timesSize));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void grow3d(final Coord c) {
        grow2d(c);
        if (this.minz == DEFAULTNOZVALUE) {
            this.minz = c.z();
        }
        if (this.maxz == DEFAULTNOZVALUE) {
            this.maxz = c.z();
        }
        if (this.minz > c.z()) {
            this.minz = c.z();
        }
        if (this.maxz < c.z()) {
            this.maxz = c.z();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean is3d() {
        return (this.minz != DEFAULTNOZVALUE) && (this.maxz != DEFAULTNOZVALUE);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public double getMaxZ() {
        if (!is3d()) {
            throw new RuntimeException();
        }
        return maxz;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public double getMinZ() {
        if (!is3d()) {
            throw new RuntimeException();
        }
        return minz;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getElevation() {
        return getMaxZ() - getMinZ();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAsString() {
        return "width=" + getWidth() + ", height=" + getHeight() + ", ll=" + ll.toString() + ", ur=" + ur.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  roundFactor  DOCUMENT ME!
     */
    public void growAndRound2d(final double roundFactor) {
        double minx = ll.x();
        if ((minx % roundFactor) != 0) {
            minx = Math.floor(minx / roundFactor) * roundFactor;
        }
        double maxx = ur.x();
        if ((maxx % roundFactor) != 0) {
            maxx = Math.floor(maxx / roundFactor) * roundFactor;
            maxx += roundFactor;
        }
        double miny = ll.y();
        if ((miny % roundFactor) != 0) {
            miny = Math.floor(miny / roundFactor) * roundFactor;
        }
        double maxy = ur.y();
        if ((maxy % roundFactor) != 0) {
            maxy = Math.floor(maxy / roundFactor) * roundFactor;
            maxy += roundFactor;
        }
        grow2d(new Coord(minx, miny, 0));
        grow2d(new Coord(maxx, maxy, 0));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMinX() {
        return ll.x();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMinY() {
        return ll.y();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMaxX() {
        return ur.x();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMaxY() {
        return ur.y();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord getCenter2d() {
        return new Coord(getMinX() + (getWidth() / 2), getMinY() + (getHeight() / 2), 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bBox  DOCUMENT ME!
     */
    public void grow2d(final BBox bBox) {
        grow2d(bBox.ll);
        grow2d(bBox.lr);
        grow2d(bBox.ul);
        grow2d(bBox.ur);
    }
    @Override
    public BBox clone() {
        return new BBox(getMinX(), getMinY(), getMaxX(), getMaxY());
    }
}
