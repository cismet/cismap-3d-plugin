/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.abstractAndInterfaces;

import com.jme3.scene.Node;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractSinglePatch {

    //~ Instance fields --------------------------------------------------------

    protected Integer id;
    protected Node node;
    protected volatile boolean isAttached2GL;
    protected volatile boolean isAttaching2GL;
    protected volatile boolean isDetachingFromGL;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractSinglePatch object.
     *
     * @param  id  DOCUMENT ME!
     */
    public AbstractSinglePatch(final Integer id) {
        this.id = id;
        isAttached2GL = false;
        isAttaching2GL = false;
        isDetachingFromGL = false;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void setAttached2GL() {
        isAttached2GL = true;
        isAttaching2GL = false;
    }

    /**
     * DOCUMENT ME!
     */
    public void setDetachedFromGL() {
        isAttached2GL = false;
        isDetachingFromGL = false;
        node = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAttached2GL() {
        return isAttached2GL;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDetachingFromGL() {
        return isDetachingFromGL;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Node getNode() {
        return node;
    }
}
