/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.dynamicSky;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class DynamicSkyBackground {

    //~ Static fields/initializers ---------------------------------------------

    private static final Sphere sphereMesh = new Sphere(40, 40, 900, false, true);

    //~ Instance fields --------------------------------------------------------

    private ViewPort viewPort = null;
    private AssetManager assetManager = null;
    private Vector3f lightPosition = new Vector3f();

    private Geometry skyGeom = null;
    private Material skyMaterial = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DynamicSkyBackground object.
     *
     * @param  assetManager  DOCUMENT ME!
     * @param  viewPort      DOCUMENT ME!
     * @param  rootNode      DOCUMENT ME!
     */
    public DynamicSkyBackground(final AssetManager assetManager, final ViewPort viewPort, final Node rootNode) {
        this.assetManager = assetManager;
        this.viewPort = viewPort;

        skyGeom = getSkyGeometry();
        rootNode.attachChild(skyGeom);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Geometry getSkyGeometry() {
        final Geometry geom = new Geometry("Sky", sphereMesh);
        geom.setQueueBucket(Bucket.Sky);
        geom.setCullHint(Spatial.CullHint.Never);
        geom.setShadowMode(ShadowMode.Off);

        skyMaterial = getDynamicSkyMaterial();
        geom.setMaterial(skyMaterial);
        return geom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Material getDynamicSkyMaterial() {
        final Material skyMat = new Material(assetManager, "MatDefs/dynamic_sky.j3md");
        skyMat.setTexture("glow_texture", assetManager.loadTexture("Textures/glow.png"));
        skyMat.setTexture("color_texture", assetManager.loadTexture("Textures/sky.png"));
        skyMat.setVector3("lightPosition", lightPosition);

        return skyMat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  position  DOCUMENT ME!
     */
    public void updateLightPosition(final Vector3f position) {
        lightPosition = position;
        skyMaterial.setVector3("lightPosition", lightPosition);

        // make the sky follow the camera
        skyGeom.setLocalTranslation(viewPort.getCamera().getLocation());
    }
}
