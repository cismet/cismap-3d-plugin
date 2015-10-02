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
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class DynamicSky extends Node {

    //~ Instance fields --------------------------------------------------------

    private DynamicSun dynamicSun;
    private DynamicSkyBackground dynamicBackground;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DynamicSky object.
     *
     * @param  assetManager     DOCUMENT ME!
     * @param  viewPort         DOCUMENT ME!
     * @param  rootNode         DOCUMENT ME!
     * @param  distanceScaling  DOCUMENT ME!
     */
    public DynamicSky(final AssetManager assetManager,
            final ViewPort viewPort,
            final Node rootNode,
            final float distanceScaling) {
        super("Sky");
        dynamicSun = new DynamicSun(assetManager, viewPort, rootNode, distanceScaling);
        rootNode.attachChild(dynamicSun);

        dynamicBackground = new DynamicSkyBackground(assetManager, viewPort, rootNode);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector3f getSunDirection() {
        return dynamicSun.getSunDirection();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vecCamPosition  DOCUMENT ME!
     */
    public void updateTimeIncrement(final Vector3f vecCamPosition) {
        dynamicSun.updateTimeIncrement(vecCamPosition);
        dynamicBackground.updateLightPosition(dynamicSun.getSunSystem().getPosition());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vecCamPosition  DOCUMENT ME!
     */
    public void updateTime(final Vector3f vecCamPosition) {
        dynamicSun.updateTime(vecCamPosition);
        dynamicBackground.updateLightPosition(dynamicSun.getSunSystem().getPosition());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DirectionalLight getSunLight() {
        return dynamicSun.getSunLight();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public SunSystem getSunSystem() {
        return dynamicSun.getSunSystem();
    }
}
