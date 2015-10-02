/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.abstractAndInterfaces;

import com.jme3.texture.Texture;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface TextureLoadListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     * @param  t   DOCUMENT ME!
     */
    void fireTextureLoaded(Integer id, Texture t);
}
