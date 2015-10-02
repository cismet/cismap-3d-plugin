/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.triangulation;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.Triangulator;

import de.wuppertal.CoordVector;

import java.util.Vector;

import javax.vecmath.Point3f;
/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class J3DTriangulator {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   polygon  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Vector<CoordVector> triangulate(final CoordVector polygon) {
        final Vector<CoordVector> vecTriangles = new Vector<CoordVector>();
        final Triangulator tr = new Triangulator();
        final GeometryInfo gi = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        gi.setCoordinates(polygon.getPoints());
        final int[] index = new int[polygon.size()];
        for (int i = 0; i < polygon.size(); i++) {
            index[i] = i;
        }

        gi.setContourCounts(new int[] { 1 });
        gi.setStripCounts(new int[] { polygon.size() });
        gi.setCoordinateIndices(index);

        tr.triangulate(gi); // ginfo contains the geometry.
        gi.convertToIndexedTriangles();
        final Point3f[] cordinates = gi.getCoordinates();
        final int[] indis = gi.getCoordinateIndices();

        CoordVector vecTemp = new CoordVector();
        for (final int i : indis) {
            vecTemp.add(cordinates[i]);
            if (vecTemp.size() == 3) {
                vecTriangles.add(vecTemp.clone());
                vecTemp = new CoordVector();
            }
        }
        if (vecTemp.size() > 0) {
            System.out.println("LOST VERTICES DURING TRIANGULATION!!!!!");
            new RuntimeException();
        }
//              System.out.println(polygon.size()+"  => #triangles="+vecTriangles.size());

        return vecTriangles;
    }
}
