/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.textureCreation;

import de.wuppertal.BBox;

import java.awt.image.BufferedImage;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class WMSImageCatchJob {

    //~ Instance fields --------------------------------------------------------

    private BufferedImage image;
    private BBox boxImage;
    private String strImageId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WMSImageCatchJob object.
     *
     * @param  boxImage    DOCUMENT ME!
     * @param  strImageId  DOCUMENT ME!
     */
    public WMSImageCatchJob(final BBox boxImage, final String strImageId) {
        this.boxImage = boxImage;
        this.strImageId = strImageId;
    }
    /**
     * Creates a new WMSImageCatchJob object.
     *
     * @param  image       DOCUMENT ME!
     * @param  boxImage    DOCUMENT ME!
     * @param  strImageId  DOCUMENT ME!
     */
    public WMSImageCatchJob(final BufferedImage image, final BBox boxImage, final String strImageId) {
        this.boxImage = boxImage;
        this.image = image;
        this.strImageId = strImageId;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BBox getBBox() {
        return boxImage;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getId() {
        return strImageId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  image  DOCUMENT ME!
     */
    public void setImage(final BufferedImage image) {
        this.image = image;
    }
}
