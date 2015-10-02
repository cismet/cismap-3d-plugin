/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

import de.wuppertal.BBox;
import de.wuppertal.Coord;
import de.wuppertal.abstractAndInterfaces.AbstractPatchedNode;
import de.wuppertal.abstractAndInterfaces.AbstractSinglePatch;
import de.wuppertal.terrain.GeometryLoadJob.GEOMETRYJOBTYPE;
import de.wuppertal.terrain.Wuppertal3D.CAMERAMOVEMENTSTATUS;
import de.wuppertal.terrain.Wuppertal3D.LOADPRIORITY;
import de.wuppertal.tools.gmlGeometryParser.GML_TIN2J3O;
import de.wuppertal.tools.textureCreation.WMSImageCatchJob;
import de.wuppertal.tools.textureCreation.WMSImageCatchThread;
import de.wuppertal.tools.textureCreation.WMSImageCatchable;

import java.io.File;

import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class DynamicPatchedTerrain extends AbstractPatchedNode {

    //~ Static fields/initializers ---------------------------------------------

    private static final int iNeighborhoodLow = 4;
    private static final int iNeighborhoodHigh = 1;
    public static final String sceneGraphID = "terrain_patched";
    public static boolean bWireframe = false;

    //~ Instance fields --------------------------------------------------------

    private Vector2f[] positionsCacheHighRes;
    private int iTextureSizeLow;
    private int iTextureSizeHigh;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DynamicPatchedTerrain object.
     *
     * @param  yOffset           DOCUMENT ME!
     * @param  offset            DOCUMENT ME!
     * @param  wup3d             DOCUMENT ME!
     * @param  iTextureSizeLow   DOCUMENT ME!
     * @param  iTextureSizeHigh  DOCUMENT ME!
     * @param  iPatchSize        DOCUMENT ME!
     */
    public DynamicPatchedTerrain(final float yOffset,
            final Coord offset,
            final Wuppertal3D wup3d,
            final int iTextureSizeLow,
            final int iTextureSizeHigh,
            final int iPatchSize) {
        super(sceneGraphID, iPatchSize, wup3d, offset);
        this.iTextureSizeLow = iTextureSizeLow;
        this.iTextureSizeHigh = iTextureSizeHigh;
        this.fPatchSize = iPatchSize;
        this.setLocalTranslation(0, -wup3d.MIN_TERRAIN_HEIGHT + yOffset, 0);
        this.positionsCache = createPositionsVector(iNeighborhoodLow, iPatchSize);
        this.positionsCacheHighRes = createPositionsVector(iNeighborhoodHigh, iPatchSize);
    }

    /**
     * Creates a new DynamicPatchedTerrain object.
     *
     * @param  yOffset           DOCUMENT ME!
     * @param  offset            DOCUMENT ME!
     * @param  folderSrc         DOCUMENT ME!
     * @param  folderDst         DOCUMENT ME!
     * @param  iPatchSize        DOCUMENT ME!
     * @param  wmsExampleQuery   DOCUMENT ME!
     * @param  iTextureSizeLow   DOCUMENT ME!
     * @param  iTextureSizeHigh  DOCUMENT ME!
     * @param  assetManager      DOCUMENT ME!
     * @param  cam               DOCUMENT ME!
     * @param  ts                DOCUMENT ME!
     */
    public DynamicPatchedTerrain(final float yOffset,
            final Coord offset,
            final File folderSrc,
            final File folderDst,
            final int iPatchSize,
            final String wmsExampleQuery,
            final int iTextureSizeLow,
            final int iTextureSizeHigh,
            final AssetManager assetManager,
            final Camera cam,
            final Wuppertal3D ts) {
        super(sceneGraphID, iPatchSize, ts, offset);
        this.iTextureSizeLow = iTextureSizeLow;
        this.iTextureSizeHigh = iTextureSizeHigh;
        this.offsetGlobal = offset;
        this.positionsCache = createPositionsVector(iNeighborhoodLow, iPatchSize);
        this.positionsCacheHighRes = createPositionsVector(iNeighborhoodHigh, iPatchSize);
        // TIN j30 Creation
        GML_TIN2J3O.PATCHSIZE = iPatchSize;
        final GML_TIN2J3O gml2TinCreator = new GML_TIN2J3O(new File(folderSrc, sceneGraphID));
        gml2TinCreator.setOffsetx(offset.x());
        gml2TinCreator.setOffsety(offset.y());
        gml2TinCreator.setDestFile(folderDst);
        gml2TinCreator.startParsingMultiThreaded(32);
        // TextureCreation for all resolutions
        final HashMap<String, BBox> allBoxesRealById = gml2TinCreator.getAllBBoxesReal(offset);
        final int[] iTexturePatchSizes = new int[] { iTextureSizeLow, iTextureSizeHigh };
        for (final int iTextureSize : iTexturePatchSizes) {
            WMSImageCatchThread.createThreadsAndAddJobsMultiThreaded(
                wmsExampleQuery,
                allBoxesRealById,
                iTextureSize,
                8,
                new WMSImageCatchable() {

                    @Override
                    public void fireImageReciveEvent(final WMSImageCatchJob imageEvent) {
                        try {
                            ImageIO.write(
                                imageEvent.getImage(),
                                "png",
                                new File(folderDst, iTextureSize + imageEvent.getId() + ".png"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
        this.setLocalTranslation(0, -ts.MIN_TERRAIN_HEIGHT + yOffset, 0);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   camStatus           DOCUMENT ME!
     * @param   currentCamPosition  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Vector<Integer> getShouldIdsHigh(final CAMERAMOVEMENTSTATUS camStatus, final Vector3f currentCamPosition) {
        final float xGlobal = currentCamPosition.x + ((float)offsetGlobal.x());
        final float zGlobal = ((float)offsetGlobal.y()) - currentCamPosition.z;
        final Vector<Integer> shouldIdsHigh = new Vector<Integer>();
        for (final Vector2f pos : positionsCacheHighRes) {
            shouldIdsHigh.add(0, getIdByCamPosition(xGlobal + pos.x, zGlobal + pos.y));
        }
        return shouldIdsHigh;
    }

    @Override
    public void updateControl(final CAMERAMOVEMENTSTATUS camStatus, final Vector3f currentCamPosition) {
        // die aktuellen IDs die sichtbar sein sollten berechnen
        final Vector<Integer> currentShouldIdsLow = getShouldIds(camStatus, currentCamPosition);
        // die aktuellen IDs die eine hohe texturauflösung verwenden sollten, berechnen
        final Vector<Integer> currentShouldIdsHigh = getShouldIdsHigh(camStatus, currentCamPosition);

        final Vector<AbstractSinglePatch> patches2dettachCache = new Vector<AbstractSinglePatch>();
        // existierende patches updaten
        for (final AbstractSinglePatch patchAbstract : mapPatches) {
            final SingleTerrainTinPatch patch = (SingleTerrainTinPatch)patchAbstract; // bad code
            patch.updateControl(patches2dettachCache, currentShouldIdsLow, currentShouldIdsHigh, camStatus);
            currentShouldIdsLow.remove(patch.getId());
            // GGF hier das Nachladen der hohen Auflösung anstoßen
            if ((camStatus == CAMERAMOVEMENTSTATUS.STANDING) && patch.isAttached2GL() && !patch.isDetachingFromGL()
                        && !patch.isTextureHighFileLoading() && currentShouldIdsHigh.contains(patch.getId())) {
                wup3d.addTexture2BeLoaded(patch.getId(), iTextureSizeHigh, patch, LOADPRIORITY.LOW);
                patch.setIsTextureHighFileLoading();
            }
        }
        // alte die nicht mehr verwendet werden sollen detachen
        for (final AbstractSinglePatch patch : patches2dettachCache) {
            detachPatchFromGL(patch);
            mapPatches.remove(patch);
        }
        // wenn die kamera sich gerade schnell bewegt => keine neuen hinzufügen
        if (camStatus == CAMERAMOVEMENTSTATUS.BEAMING) {
            // abbrechen: keine neuen hinzufügen
            return;
        }
        // neue patches hinzufügen
        for (final Integer id2Add : currentShouldIdsLow) {
            final SingleTerrainTinPatch patch = new SingleTerrainTinPatch(id2Add, this);
            wup3d.addGeometry2BeLoaded(id2Add, patch, LOADPRIORITY.HIGH, GEOMETRYJOBTYPE.TIN);
            wup3d.addTexture2BeLoaded(id2Add, iTextureSizeLow, patch, LOADPRIORITY.HIGH);
            mapPatches.add(patch);
        }
    }

    @Override
    protected Integer getIdByCamPosition(float xGlobal, float zGlobal) {
        xGlobal -= 300000; //
        int kmx = (int)FastMath.floor(xGlobal / 1000);
        zGlobal -= 5600000;
        int kmz = (int)FastMath.floor(zGlobal / 1000);
        final float restx = xGlobal % (kmx * 1000);
        final float restz = zGlobal % (kmz * 1000);
        final int irx = (int)FastMath.floor(restx / fPatchSize) + 1;
        final int irz = (int)FastMath.floor(restz / fPatchSize);
        final int iPart = irx + (irz * 5);
        kmz *= 100;
        kmx *= 10000;
        // System.out.println(kmz+kmx+iPart);
        return kmz + kmx + iPart;
    }
}
