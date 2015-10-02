/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.terrainCreation;

import com.infomatiq.jsi.Point;
import com.infomatiq.jsi.SpatialIndex;
import com.infomatiq.jsi.rtree.RTree;

import de.wuppertal.BBox;
import de.wuppertal.Coord;
import de.wuppertal.CoordVector;

import gnu.trove.TIntProcedure;

import java.awt.Polygon;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TerrainCreatorHelper implements TIntProcedure {

    //~ Static fields/initializers ---------------------------------------------

    public static int DEFAULTMINDELTA = 20;

    private static TerrainCreatorHelper instance;

    //~ Instance fields --------------------------------------------------------

    private BufferedWriter bw;
    private Coord pos;
    private CoordVector vecPointCloud;
    private double[] heights;
    private TreeMap<Double, TreeMap<Double, Double>> mapXYZ;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TerrainCreatorHelper object.
     */
    private TerrainCreatorHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static TerrainCreatorHelper getInstance() {
        if (instance == null) {
            instance = new TerrainCreatorHelper();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bReduce2Min      DOCUMENT ME!
     * @param   rasterFile       DOCUMENT ME!
     * @param   deltaX           DOCUMENT ME!
     * @param   deltaY           DOCUMENT ME!
     * @param   minValue         DOCUMENT ME!
     * @param   minReplaceValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float[] getJMEHeightDataFromRasterFile(final boolean bReduce2Min,
            final File rasterFile,
            final double deltaX,
            final double deltaY,
            final float minValue,
            final float minReplaceValue) {
        final CoordVector cVec = readRasterPoints(rasterFile);
        cVec.updateBBox3d();
        final float[][] map = rasterVector2FloatMap(cVec, deltaX, deltaY, bReduce2Min);

        return replaceMinValue(floatMap2FloatVec(map), minValue, minReplaceValue);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   values           DOCUMENT ME!
     * @param   minValue         DOCUMENT ME!
     * @param   minReplaceValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private float[] replaceMinValue(final float[] values, final float minValue, final float minReplaceValue) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] <= minValue) {
                values[i] = minReplaceValue;
            }
        }
        return values;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   worldFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector readWorldFile(final File worldFile) {
        final CoordVector vec = new CoordVector();
        try {
            final BufferedReader br = new BufferedReader(new FileReader(worldFile));
            final double xDelta = Double.parseDouble(br.readLine());
            // skip twice
            Double.parseDouble(br.readLine());
            Double.parseDouble(br.readLine());
            final double yDelta = Double.parseDouble(br.readLine());
            vec.add(new Coord(xDelta, yDelta, 1));
            final double xPos = Double.parseDouble(br.readLine());
            final double yPos = Double.parseDouble(br.readLine());
            vec.add(new Coord(xPos, yPos, 1));
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vec;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   values              DOCUMENT ME!
     * @param   valuesSmalerIgnore  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getMin(final float[] values, final float valuesSmalerIgnore) {
        float min = Float.MAX_VALUE;
        for (final float f : values) {
            if (f >= valuesSmalerIgnore) {
                min = Math.min(min, f);
            }
        }
        return min;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   values  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getMax(final float[] values) {
        float max = Float.MIN_VALUE;
        for (final float f : values) {
            max = Math.max(max, f);
        }
        return max;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   worldFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord getDeltasFromWorldFile(final File worldFile) {
        return readWorldFile(worldFile).getCoord(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   worldFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Coord getLowerLeftFromWorldFile(final File worldFile) {
        return readWorldFile(worldFile).getCoord(1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   map          DOCUMENT ME!
     * @param   borderValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float[][] enlargeFloatMap2Square2N(final float[][] map, final float borderValue) {
        final float widthx = map.length;
        final float widthy = map[0].length;
        float widthx2n = getNextGreater2N(widthx) + 1;
        float widthy2n = getNextGreater2N(widthy) + 1;
        widthx2n = Math.max(widthx2n, widthy2n);
        widthy2n = widthx2n;
        System.out.println("enlarge2: " + widthx2n + " " + widthy2n);
        // neue Breite = x0 + alteBreite +x1
        final int x0 = (int)Math.floor((widthx2n - widthx) / 2f);
        final int x1 = (int)Math.ceil((widthx2n - widthx) / 2f);
        // neue Höhe = y0 + alteHöhe + y1
        final int y0 = (int)Math.floor((widthy2n - widthy) / 2f);
        final int y1 = (int)Math.ceil((widthy2n - widthy) / 2f);
        final float[][] mapRet = new float[x0 + map.length + x1][y0 + map[0].length + y1];
        // Fill border
        for (int x = 0; x < mapRet.length; x++) {
            for (int y = 0; y < mapRet[0].length; y++) {
                // if is Rand
                if ((x < x0) || (y < y0) || (x >= (x0 + map.length - 1)) || (y >= (y0 + map[0].length - 1))) {
                    mapRet[x][y] = borderValue;
                } else {
                    // System.out.println(map[x-x0][y-y0]);
                    mapRet[x][y] = map[x - x0][y - y0];
                }
            }
        }
        return mapRet;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   f  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private float getNextGreater2N(float f) {
        f = Math.abs(f);
        float n = 1;
        while (n < f) {
            n *= 2;
        }
        return n;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   map  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float[] floatMap2FloatVec(final float[][] map) {
        final float[] ret = new float[map.length * map[0].length];
        int indexFill = 0;
        for (int y = map[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < map.length; x++) {
                ret[indexFill] = map[x][y];
                indexFill++;
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cVectorRaster  DOCUMENT ME!
     * @param   deltaX         DOCUMENT ME!
     * @param   deltaY         DOCUMENT ME!
     * @param   bReduce2Min    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public float[][] rasterVector2FloatMap(final CoordVector cVectorRaster,
            final double deltaX,
            final double deltaY,
            final boolean bReduce2Min) {
        if (bReduce2Min) {
            cVectorRaster.reduce2Min3D();
        }
        final BBox box = cVectorRaster.getBBox();
        final int xPoints = (int)(box.getWidth() / deltaX) + 1;
        final int yPoints = (int)(box.getHeight() / deltaY) + 1;
        if ((xPoints * yPoints) != cVectorRaster.size()) {
            throw new RuntimeException("incomplete raster");
        }
        final float[][] ret = new float[xPoints][yPoints];
        int index = 0;
        for (int x = 0; x < xPoints; x++) {
            for (int y = 0; y < yPoints; y++) {
                ret[x][y] = (float)cVectorRaster.getCoord(index).z();
                index++;
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fSrc  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CoordVector readRasterPoints(final File fSrc) {
        CoordVector vecRasterPoints = null;
        try {
            final BufferedReader br = new BufferedReader(new FileReader(fSrc));
            String line = "";
            vecRasterPoints = new CoordVector();
            while ((line = br.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(line, " ");
                if (st.countTokens() == 3) {
                    vecRasterPoints.add2d(new Coord(
                            st.nextToken().replace(",", "."),
                            st.nextToken().replace(",", "."),
                            st.nextToken().replace(",", ".")));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            if (fSrc != null) {
                System.err.println("could not readRasterPoints from File: " + fSrc.getAbsolutePath());
            } else {
                System.err.println("could not readRasterPoints from File. File is null!");
            }
        }

        return vecRasterPoints;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fPointCloudSrc  DOCUMENT ME!
     * @param  fDest           DOCUMENT ME!
     * @param  dest2NSize      DOCUMENT ME!
     */
    public void convertPointCloud2Raster(final File fPointCloudSrc, final File fDest, final int dest2NSize) {
        try {
            vecPointCloud = readRasterPoints(fPointCloudSrc);
            mapXYZ = new TreeMap<Double, TreeMap<Double, Double>>();
            final Coord cllReduced = vecPointCloud.reduce2Min();
            final BBox bboxPointCloud = vecPointCloud.getBBox();

            final SpatialIndex si = new RTree();
            si.init(null);
            int iIndexRectangle = 0;
            heights = new double[vecPointCloud.size()];
            for (final Coord c : vecPointCloud.getCoords()) {
                si.add(c.getAsRectangle(), iIndexRectangle);
                heights[iIndexRectangle] = c.z();
                iIndexRectangle++;
            }

            // Adaptive Erstellung des rasters damit später 2n
            final CoordVector vec3dRasterPoints = bboxPointCloud.createCoordsInBBox(dest2NSize, true);
            final double scalex = vec3dRasterPoints.getCoord(0).x();
            final double scaley = vec3dRasterPoints.getCoord(0).y();
            vec3dRasterPoints.remove(0);
            bw = new BufferedWriter(new FileWriter(fDest));

            int iLinesWritten = 0;
            final int iLinesAtAll = vec3dRasterPoints.size();
            for (final Coord p : vec3dRasterPoints.getCoords()) {
                pos = p;
                pos.z(0);
                si.nearestNUnsorted(new Point((float)pos.x(), (float)pos.y()), this, 3, 10000f);
                iLinesWritten++;
                if ((iLinesWritten % 10000) == 0) {
                    System.out.println(iLinesWritten + " von " + iLinesAtAll);
                }
            }
            ;
            System.out.print(mapXYZ.size() + " x ");
            int width = -1;
            for (final Entry<Double, TreeMap<Double, Double>> row : mapXYZ.entrySet()) {
                if (width == -1) {
                    width = row.getValue().size();
                    System.out.println(width);
                } else if (width != row.getValue().size()) {
                    System.out.println(row.getValue().size() + "BAD LINE");
                }
                for (final Entry<Double, Double> cell : row.getValue().entrySet()) {
                    bw.write(row.getKey() + " " + cell.getKey() + " " + cell.getValue() + "\n");
                }
            }
            bw.flush();
            bw.close();

            // Write WorldFile
            String name = fDest.getName();
            name = name.replace(".txt", "");
            bw = new BufferedWriter(new FileWriter(new File(fDest.getParentFile(), name + ".wld")));
            bw.write(String.valueOf(scalex));
            bw.newLine();
            bw.write("0");
            bw.newLine();
            bw.write("0");
            bw.newLine();
            bw.write(String.valueOf(scaley));
            bw.newLine();
            bw.write(String.valueOf(cllReduced.x()));
            bw.newLine();
            bw.write(String.valueOf(cllReduced.y()));
            bw.close();
            System.out.println("read " + vecPointCloud.size() + " coordinates");
            System.out.println(vecPointCloud.getBBox().getAsString());
            System.out.println("all done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute(final int i) {
        try {
            final Coord nearest = vecPointCloud.getCoords().get(i);
            if (nearest.getDist2d(pos) > DEFAULTMINDELTA) {
                pos.z(-Float.MAX_VALUE);
                // pos.z(50);
            } else {
                pos.z(heights[i]);
            }
            TreeMap<Double, Double> row = mapXYZ.get(pos.x());
            if (row == null) {
                row = new TreeMap<Double, Double>();
                row.put(pos.y(), pos.z());
                mapXYZ.put(pos.x(), row);
            } else if (!row.containsKey(pos.y())) {
                row.put(pos.y(), pos.z());
            } else {
                // Mitteln
                row.put(pos.y(), (row.get(pos.y()) + pos.z()) / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   f  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Polygon readPolygon(final File f) {
        Polygon p = null;
        try {
            p = new Polygon();
            final BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            final Vector<Double> values = new Vector<Double>();
            while ((line = br.readLine()) != null) {
                final StringTokenizer st = new StringTokenizer(line, " ");
                while (st.hasMoreTokens()) {
                    values.add(Double.parseDouble(st.nextToken()));
                    if (values.size() == 2) {
                        p.addPoint((int)Math.round(values.firstElement()), (int)Math.round(values.lastElement()));
                        values.clear();
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vec  DOCUMENT ME!
     * @param  f    DOCUMENT ME!
     */
    public void writeCoordVector2File(final CoordVector vec, final File f) {
        try {
            int iLines = 0;
            final int iFlushAfterLines = 1000;
            final BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            for (final Coord c : vec) {
                bw.write(c.x() + " " + c.y() + " " + c.z());
                bw.newLine();
                iLines++;
                if ((iLines % iFlushAfterLines) == 0) {
                    bw.flush();
                }
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
