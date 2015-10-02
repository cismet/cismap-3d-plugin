/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.dynamicSky;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class DynamicSun extends Node {

    //~ Instance fields --------------------------------------------------------

    private SunSystem sunSystem;
    private SkyBillboardItem sun;

    private DirectionalLight sunLight = null;
    private Vector3f lightDir;
    private Vector3f lightPosition = new Vector3f();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DynamicSun object.
     *
     * @param  assetManager  DOCUMENT ME!
     * @param  viewPort      DOCUMENT ME!
     * @param  rootNode      DOCUMENT ME!
     * @param  scaling       DOCUMENT ME!
     */
    public DynamicSun(final AssetManager assetManager,
            final ViewPort viewPort,
            final Node rootNode,
            final float scaling) {
        sunSystem = new SunSystem(new Date(), 0, 0, 0, scaling);
        lightDir = sunSystem.getPosition();
        sunLight = getSunLight();
        rootNode.addLight(sunLight);

        sunSystem.setSiteLatitude(51f);
        sunSystem.setSiteLongitude(7f);
        updateLightPosition();

        final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/sun.png"));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        mat.getAdditionalRenderState().setDepthWrite(false);

        sun = new SkyBillboardItem("sun", scaling / 10);
        sun.setMaterial(mat);
        attachChild(sun);

        setQueueBucket(Bucket.Sky);
        setCullHint(CullHint.Never);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SunSystem getSunSystem() {
        return sunSystem;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected DirectionalLight getSunLight() {
        final DirectionalLight dl = new DirectionalLight();
        dl.setDirection(lightDir);
        dl.setColor(ColorRGBA.White.mult(1.5f));
        return dl;
    }

    /**
     * DOCUMENT ME!
     */
    protected void updateLightPosition() {
        lightDir = sunSystem.getDirection();
        lightPosition = sunSystem.getPosition();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getSunDirection() {
        return sunSystem.getPosition();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vecCamLocation  DOCUMENT ME!
     */
    public void updateTimeIncrement(final Vector3f vecCamLocation) {
        // make everything follow the camera
        setLocalTranslation(vecCamLocation);
        sunSystem.updateSunPosition(0, 0, 10); // increment by 30 seconds
        updateLightPosition();
        sunLight.setDirection(lightDir);
        sun.setLocalTranslation(lightPosition.mult(0.95f));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vecCamLocation  DOCUMENT ME!
     */
    public void updateTime(final Vector3f vecCamLocation) {
        // make everything follow the camera
        setLocalTranslation(vecCamLocation);
        sunSystem.updateSunPosition(0, 0, 0); // increment by 30 seconds
        updateLightPosition();
        sunLight.setDirection(lightDir);
        sun.setLocalTranslation(lightPosition.mult(1f));
    }
}
