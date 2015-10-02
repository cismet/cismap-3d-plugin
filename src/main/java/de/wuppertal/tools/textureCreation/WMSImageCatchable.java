/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.textureCreation;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public interface WMSImageCatchable {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  imageEvent  DOCUMENT ME!
     */
    void fireImageReciveEvent(WMSImageCatchJob imageEvent);
}
