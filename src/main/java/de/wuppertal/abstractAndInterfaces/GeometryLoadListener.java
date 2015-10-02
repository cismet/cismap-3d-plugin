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
public interface GeometryLoadListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     * @param  n   DOCUMENT ME!
     */
    void fireGeometryLoaded(Integer id, Node n);
}
