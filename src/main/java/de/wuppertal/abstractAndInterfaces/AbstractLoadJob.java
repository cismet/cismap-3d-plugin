/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.abstractAndInterfaces;

import com.jme3.asset.AssetManager;

import de.wuppertal.terrain.Wuppertal3D.LOADPRIORITY;

import java.util.TreeSet;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractLoadJob {

    //~ Static fields/initializers ---------------------------------------------

    private static TreeSet<Integer> unprocessableIds;

    //~ Instance fields --------------------------------------------------------

    private int id;
    private LOADPRIORITY prio;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractLoadJob object.
     *
     * @param  id    DOCUMENT ME!
     * @param  prio  DOCUMENT ME!
     */
    public AbstractLoadJob(final int id, final LOADPRIORITY prio) {
        this.id = id;
        this.prio = prio;
        if (unprocessableIds == null) {
            unprocessableIds = new TreeSet<Integer>();
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public LOADPRIORITY getPrio() {
        return prio;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  am  DOCUMENT ME!
     */
    public abstract void doJob(AssetManager am);

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean isProcessable(final Integer id) {
        return !unprocessableIds.contains(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    protected void addUnProcessableId(final int id) {
        synchronized (unprocessableIds) {
            unprocessableIds.add(new Integer(id));
        }
    }

    @Override
    public boolean equals(final Object o) {
        return ((AbstractLoadJob)o).id == id;
    }
}
