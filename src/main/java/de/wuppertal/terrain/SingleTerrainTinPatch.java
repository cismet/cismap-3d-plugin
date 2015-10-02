/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;

import de.wuppertal.abstractAndInterfaces.AbstractSinglePatch;
import de.wuppertal.abstractAndInterfaces.GeometryLoadListener;
import de.wuppertal.abstractAndInterfaces.TextureLoadListener;
import de.wuppertal.terrain.Wuppertal3D.CAMERAMOVEMENTSTATUS;

import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class SingleTerrainTinPatch extends AbstractSinglePatch implements TextureLoadListener, GeometryLoadListener {

    //~ Static fields/initializers ---------------------------------------------

    public static boolean bWireframe = false;

    //~ Instance fields --------------------------------------------------------

    private volatile boolean isGeometryFileLoaded;
    private volatile boolean isTextureFileLowLoaded;
    private volatile boolean isTextureFileHighLoaded;
    private volatile boolean isTextureFileHighLoading;
    private volatile boolean isTextureHighAttached2GL;
    private Texture textureLow;
    private Texture textureHigh;
    private DynamicPatchedTerrain patchedTerrain;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SingleTerrainTinPatch object.
     *
     * @param  id              DOCUMENT ME!
     * @param  patchedTerrain  DOCUMENT ME!
     */
    public SingleTerrainTinPatch(final Integer id, final DynamicPatchedTerrain patchedTerrain) {
        super(id);
        this.patchedTerrain = patchedTerrain;
        isGeometryFileLoaded = false;
        isTextureFileLowLoaded = false;
        isTextureFileHighLoaded = false;
        isTextureFileHighLoading = false;
        isTextureHighAttached2GL = false;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * private Texture getTexturePart() { double factorx0 =
     * (boxReal.getMinX()-root.boxFullTerrain.getMinX())/root.boxFullTerrain.getWidth(); double factorx1 =
     * (boxReal.getMaxX()-root.boxFullTerrain.getMinX())/root.boxFullTerrain.getWidth(); double factory0 =
     * (root.boxFullTerrain.getMaxY()-boxReal.getMaxY())/root.boxFullTerrain.getHeight(); double factory1 =
     * (root.boxFullTerrain.getMaxY()-boxReal.getMinY())/root.boxFullTerrain.getHeight(); int x0 = (int) (factorx0 *
     * ((float)root.buffFullTerrain.getWidth())); int y0 = (int) (factory0 * ((float)root.buffFullTerrain.getHeight()));
     * int x1 = (int) (factorx1 * ((float)root.buffFullTerrain.getWidth())); int y1 = (int) (factory1 *
     * ((float)root.buffFullTerrain.getHeight())); System.out.println(factorx0+" "+factory0); BufferedImage
     * buffImagePart = root.buffFullTerrain.getSubimage(x0, y0, x1-x0, y1-y0); buffImagePart = copyImage(buffImagePart);
     * Texture t = new Texture2D(new AWTLoader().load(buffImagePart, true)); return t; } public BufferedImage
     * copyImage(BufferedImage source){ BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(),
     * source.getType()); Graphics g = b.getGraphics(); g.drawImage(source, 0, 0, null); g.dispose(); return b; }.
     *
     * @param  patches2Detach  DOCUMENT ME!
     * @param  currentIdsLow   DOCUMENT ME!
     * @param  currentIdsHigh  DOCUMENT ME!
     * @param  camStatus       DOCUMENT ME!
     */
    public void updateControl(final Vector<AbstractSinglePatch> patches2Detach,
            final Vector<Integer> currentIdsLow,
            final Vector<Integer> currentIdsHigh,
            final CAMERAMOVEMENTSTATUS camStatus) {
        if (!currentIdsLow.contains(id)) {
            // wennn this ID gar nicht mehr vorhanden ist => detaching
            isDetachingFromGL = true;
            patches2Detach.add(this);
            return;
        }
        // wenn geometry und texture geladen wurde, dieses patch aber noch nicht im GL ist => GL Laden anstoßen
        if (isGeometryFileLoaded && isTextureFileLowLoaded && !isDetachingFromGL && !isAttaching2GL && !isAttached2GL) {
            final Geometry g = (Geometry)node.getChild("geo");
            g.getMaterial().setTexture("DiffuseMap", textureLow);
            g.getMaterial().getAdditionalRenderState().setWireframe(bWireframe);
            patchedTerrain.attachPatch2GL(this);
            isAttaching2GL = true;
            return;
        }
        // attach texture high 2GL
        if (isAttached2GL && !isDetachingFromGL && currentIdsHigh.contains(id) && isTextureFileHighLoaded
                    && !isTextureHighAttached2GL && (camStatus == CAMERAMOVEMENTSTATUS.STANDING)) {
            final Geometry g = (Geometry)node.getChild("geo");
            g.getMaterial().setTexture("DiffuseMap", textureHigh);
            g.getMaterial().getAdditionalRenderState().setWireframe(bWireframe);
            isTextureHighAttached2GL = true;
            return;
        }
        // detach texture high From GL
        if (isAttached2GL && !isDetachingFromGL && isTextureFileHighLoaded && isTextureHighAttached2GL
                    && !currentIdsHigh.contains(id)) {
            final Geometry g = (Geometry)node.getChild("geo");
            g.getMaterial().setTexture("DiffuseMap", textureLow);
            g.getMaterial().getAdditionalRenderState().setWireframe(bWireframe);
            isTextureHighAttached2GL = false;
            return;
        }
    }

    @Override
    public void fireGeometryLoaded(final Integer id, final Node n) {
        if (!isDetachingFromGL) {
            this.node = n;
            node.setLocalScale(1, 1, -1);
            isGeometryFileLoaded = true;
        }
    }

    @Override
    public void fireTextureLoaded(final Integer id, final Texture t) {
        if (!isDetachingFromGL) {
            if (!isTextureFileLowLoaded && !isTextureFileHighLoaded) {
                textureLow = t;
                isTextureFileLowLoaded = true;
            } else if (!isTextureFileHighLoaded) {
                textureHigh = t;
                isTextureFileHighLoaded = true;
                isTextureFileHighLoading = false;
            }
        }
    }

    @Override
    public void setDetachedFromGL() {
        // Clean memory
        isAttached2GL = false;
        isDetachingFromGL = false;
        isTextureHighAttached2GL = false;
        textureLow = null;
        textureHigh = null;
        node = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isTextureHighFileLoading() {
        return isTextureFileHighLoading;
    }

    /**
     * DOCUMENT ME!
     */
    public void setIsTextureHighFileLoading() {
        isTextureFileHighLoading = true;
    }
}
