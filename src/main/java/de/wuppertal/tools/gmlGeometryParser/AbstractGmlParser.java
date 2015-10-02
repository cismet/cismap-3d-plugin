/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.gmlGeometryParser;

import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;

import de.wuppertal.Coord;
import de.wuppertal.CoordVector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;

import java.io.File;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractGmlParser {

    //~ Instance fields --------------------------------------------------------

    private Vector<File> files2Parse;
    private volatile int iRunningThreads;
    private volatile int iFiles2Parse;
    private volatile int iFilesParsed;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractGmlParser object.
     *
     * @param  files2Parse  DOCUMENT ME!
     */
    public AbstractGmlParser(final Vector<File> files2Parse) {
        this.files2Parse = files2Parse;
        iFiles2Parse = 0;
        iFilesParsed = 0;
    }

    /**
     * Creates a new AbstractGmlParser object.
     *
     * @param  fileRoot  DOCUMENT ME!
     */
    public AbstractGmlParser(final File fileRoot) {
        files2Parse = new Vector<File>();
        iFiles2Parse = 0;
        iFilesParsed = 0;
        for (final File f : fileRoot.listFiles()) {
            if (isGML(f)) {
                files2Parse.add(f);
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   f  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isGML(final File f) {
        final String ending = f.getName();
        if (ending.endsWith(".gml") || ending.endsWith(".GML")) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void startParsing() {
        System.out.println("start Parsing");
        for (final File f : files2Parse) {
            parseFile(f);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  iThreads  DOCUMENT ME!
     */
    public void startParsingMultiThreaded(final int iThreads) {
        iFiles2Parse = files2Parse.size();
        final Thread[] threads = new Thread[iThreads];
        final Vector<File>[] files4Thread = getFiles4Thread(iThreads);
        for (int i = 0; i < iThreads; i++) {
            final int iThreadId = i;
            final Thread t = new Thread() {

                    @Override
                    public void run() {
                        try {
                            setName("ParsingThread " + iThreadId);
                            for (final File f : files4Thread[iThreadId]) {
                                parseFile(f);
                                iFilesParsed++;
                                System.out.println("Parsed File " + iFilesParsed + " of " + iFiles2Parse);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            iRunningThreads--;
                        }
                    }
                };
            t.start();
            iRunningThreads++;
            threads[i] = t;
        }
        while (iRunningThreads > 0) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        allFilesFinished();
    }

    /**
     * DOCUMENT ME!
     */
    public void allFilesFinished() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param   iThreads  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Vector<File>[] getFiles4Thread(final int iThreads) {
        final Vector<File>[] files4Thread = new Vector[iThreads];
        for (int iThread = 0; iThread < iThreads; iThread++) {
            files4Thread[iThread] = new Vector<File>();
        }
        int i = 0;
        for (final File f : files2Parse) {
            files4Thread[i % iThreads].add(f);
            i++;
        }
        return files4Thread;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   f  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean parseFile(final File f) {
        try {
            final SAXBuilder builder = new SAXBuilder();
            final Document doc = builder.build(f);
            final Iterator<Element> processDescendants = doc.getDescendants(new ElementFilter());
            while (processDescendants.hasNext()) {
                final Element e = processDescendants.next();
                final String currentName = e.getName();
                parseGMLClass(currentName, f, e, processDescendants);
            }
            finishedParsing(f);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentName         DOCUMENT ME!
     * @param  f                   DOCUMENT ME!
     * @param  e                   DOCUMENT ME!
     * @param  processDescendants  DOCUMENT ME!
     */
    protected abstract void parseGMLClass(String currentName, File f, Element e, Iterator<Element> processDescendants);

    /**
     * DOCUMENT ME!
     *
     * @param  f                   DOCUMENT ME!
     * @param  e                   DOCUMENT ME!
     * @param  gmlType             DOCUMENT ME!
     * @param  processDescendants  DOCUMENT ME!
     */
    public void parsePosList(final File f,
            final Element e,
            final GMLTYPE gmlType,
            final Iterator<Element> processDescendants) {
        final StringTokenizer st = new StringTokenizer(e.getValue(), " ");
        double x;
        double y;
        double z;
        final CoordVector vec = new CoordVector();
        while (st.hasMoreTokens()) {
            x = Double.parseDouble(st.nextToken());
            y = Double.parseDouble(st.nextToken());
            z = Double.parseDouble(st.nextToken());
            vec.add(new Coord(x, y, z));
        }
        handleCoordVector(vec, f, e, gmlType, processDescendants);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vec                 DOCUMENT ME!
     * @param  f                   DOCUMENT ME!
     * @param  e                   DOCUMENT ME!
     * @param  gmlType             DOCUMENT ME!
     * @param  processDescendants  DOCUMENT ME!
     */
    protected abstract void handleCoordVector(CoordVector vec,
            File f,
            Element e,
            GMLTYPE gmlType,
            Iterator<Element> processDescendants);

    /**
     * DOCUMENT ME!
     *
     * @param  f  DOCUMENT ME!
     */
    protected abstract void finishedParsing(File f);

    /**
     * DOCUMENT ME!
     *
     * @param   p00  DOCUMENT ME!
     * @param   p10  DOCUMENT ME!
     * @param   p01  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getNormal(final Vector3f p00, final Vector3f p10, final Vector3f p01) {
        final Triangle t = new Triangle(p00, p10, p01);
        t.calculateNormal();
        final Vector3f vecNormal = t.getNormal();
        return vecNormal;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vecTriangle  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getNormal(final CoordVector vecTriangle) {
        return getNormal(vecTriangle.getCoord(0), vecTriangle.getCoord(1), vecTriangle.getCoord(2));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   p0  DOCUMENT ME!
     * @param   p1  DOCUMENT ME!
     * @param   p2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getNormal(final Coord p0, final Coord p1, final Coord p2) {
        return getNormal(p0.getVec3f(), p1.getVec3f(), p2.getVec3f());
    }
}
