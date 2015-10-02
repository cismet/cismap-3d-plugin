/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import de.wuppertal.Coord;
import de.wuppertal.tools.terrainCreation.TerrainCreatorHelper;
import de.wuppertal.tools.textureCreation.WMSImageCatcher;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class StaticTerrain {

    //~ Static fields/initializers ---------------------------------------------

    public static final String sceneGraphID = "terrain_static";

    //~ Instance fields --------------------------------------------------------

    private Material mat;
    private TerrainQuad staticTerrainNode;

    //~ Constructors -----------------------------------------------------------

    /**
     * regular constructor.
     *
     * @param  yOffset  DOCUMENT ME!
     * @param  am       DOCUMENT ME!
     * @param  cam      DOCUMENT ME!
     */
    public StaticTerrain(final float yOffset, final AssetManager am, final Camera cam) {
        staticTerrainNode = (TerrainQuad)am.loadModel(sceneGraphID + ".j3o");
        final TerrainLodControl controlSurround = new TerrainLodControl(staticTerrainNode, cam);
        staticTerrainNode.addControl(controlSurround);
        staticTerrainNode.setShadowMode(ShadowMode.Off);
        staticTerrainNode.setLocalTranslation(staticTerrainNode.getLocalTranslation().add(0, yOffset, 0));
        // refrenced textures within the j3o file should be load automatically
        mat = staticTerrainNode.getMaterial();
    }

    /**
     * for test cases, admins or first launch only use this constructor to create a j3o File (creationMode==true) and a
     * jpg texture File. forces TextureLoading from WMS and new calculation of j30 File
     *
     * @param  yOffset             DOCUMENT ME!
     * @param  dgmResolution       DOCUMENT ME!
     * @param  srcFolder           DOCUMENT ME!
     * @param  dstFolder           DOCUMENT ME!
     * @param  iTextureResolution  DOCUMENT ME!
     * @param  wmsExampleQuery     DOCUMENT ME!
     * @param  am                  DOCUMENT ME!
     * @param  cam                 DOCUMENT ME!
     * @param  ts                  DOCUMENT ME!
     */
    public StaticTerrain(final float yOffset,
            final int dgmResolution,
            final File srcFolder,
            final File dstFolder,
            final int iTextureResolution,
            final String wmsExampleQuery,
            final AssetManager am,
            final Camera cam,
            final Wuppertal3D ts) {
        final long t0 = System.currentTimeMillis();
        final TerrainCreatorHelper inst = TerrainCreatorHelper.getInstance();
        final File worldFile = new File(srcFolder, dgmResolution + "terrain_static.wld");
        final File heightDataFile = new File(srcFolder, dgmResolution + "terrain_static.txt");
        final Coord cDeltas = inst.getDeltasFromWorldFile(worldFile);
        // Coord cllSurround = inst.getLowerLeftFromWorldFile(worldFile); Coord urSurround =
        // cllSurround.plusNew(dgmResolution*cDeltasSurround.x(), dgmResolution*cDeltasSurround.y()); Load Heightdata
        // Replace all values<"minValue" with minReplaceValue. Example: replace all heights<0 with -1000
        final float[] mapSurround = inst.getJMEHeightDataFromRasterFile(
                true,
                heightDataFile,
                cDeltas.x(),
                cDeltas.y(),
                0,
                -1000);
        // create Jmonkey TerrainQuad
        staticTerrainNode = new TerrainQuad(sceneGraphID, (dgmResolution / 16) + 1, dgmResolution + 1, mapSurround);
        staticTerrainNode.setLocalTranslation((float)(cDeltas.x() * dgmResolution) / 2,
            -ts.MIN_TERRAIN_HEIGHT,
            (float)(cDeltas.y() * dgmResolution)
                    / 2);
        staticTerrainNode.setLocalScale((float)cDeltas.x(), 1, (float)cDeltas.y());
        // load Texture from WMS
        final WMSImageCatcher catcher = WMSImageCatcher.getInstance(false);
        catcher.setParameterByExampleGetMapRequest(wmsExampleQuery);
        // set Box for region
        catcher.setBBox(ts.getBBoxFullTerrain());
        // set iomage size
        catcher.setImageHeight(iTextureResolution);
        catcher.setImageWidth(iTextureResolution);
        final BufferedImage img = catcher.getImage(true);
        // try saving the image
        try {
            ImageIO.write(img, "jpeg", new File(dstFolder, sceneGraphID + ".jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("could not write image for static terrain from WMS");
        }
        // Texture texture = new Texture2D(new AWTLoader().load(img, true));
        final Texture texture = am.loadTexture(sceneGraphID + ".jpg");
        texture.setWrap(WrapMode.Clamp);
        // set material
        mat = new Material(am, "Common/MatDefs/Light/Lighting.j3md"); // ... specify .j3md file to use (unshaded).
        mat.setTexture("DiffuseMap", texture);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.DarkGray.mult(2));
        mat.setFloat("Shininess", 12);
        mat.getAdditionalRenderState().setWireframe(false);
        staticTerrainNode.setMaterial(mat);
        // enable LOD
        final TerrainLodControl controlSurround = new TerrainLodControl(staticTerrainNode, cam);
        staticTerrainNode.addControl(controlSurround);
        staticTerrainNode.setShadowMode(ShadowMode.Off);
        // Expoprt to j3o
        final BinaryExporter exporter = new BinaryExporter();
        try {
            exporter.save(staticTerrainNode, new File(dstFolder, sceneGraphID + ".j3o"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("could not write j3o for staticDGM");
        }
        final long t1 = System.currentTimeMillis();
        System.out.println("StaticTerrain creation finished in " + (t1 - t0) + " ms");
        // nach dem Speichern
        staticTerrainNode.setLocalTranslation(staticTerrainNode.getLocalTranslation().add(0, yOffset, 0));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSceneGraphID() {
        return sceneGraphID;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public TerrainQuad getStaticTerrainNode() {
        return staticTerrainNode;
    }
    /**
     * DOCUMENT ME!
     */
    public void switchWireFrame() {
        mat.getAdditionalRenderState().setWireframe(!mat.getAdditionalRenderState().isWireframe());
        staticTerrainNode.setMaterial(mat);
    }
}
