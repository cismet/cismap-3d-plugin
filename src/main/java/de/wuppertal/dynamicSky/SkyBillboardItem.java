/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.dynamicSky;

import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Quad;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class SkyBillboardItem extends Geometry {

    //~ Instance fields --------------------------------------------------------

    private BillboardControl billBoadControl = new BillboardControl();
    private Quad quad;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SkyBillboardItem object.
     *
     * @param  name   DOCUMENT ME!
     * @param  scale  DOCUMENT ME!
     */
    public SkyBillboardItem(final String name, final Float scale) {
        super(name);

        quad = new Quad(scale, scale);

        setQueueBucket(Bucket.Transparent);
        setCullHint(CullHint.Never);

        setMesh(quad);

        addControl(billBoadControl);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  rotation  DOCUMENT ME!
     */
    public void setRotation(final Float rotation) {
        setRotationEnabled();
        this.rotate(new Quaternion().fromAngles(0, 0, rotation));
    }

    /**
     * DOCUMENT ME!
     */
    public void removeBillboardController() {
        removeControl(billBoadControl);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setRotationEnabled() {
        billBoadControl.setAlignment(BillboardControl.Alignment.AxialZ);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setRotationDisabled() {
        billBoadControl.setAlignment(BillboardControl.Alignment.Screen);
    }
}
