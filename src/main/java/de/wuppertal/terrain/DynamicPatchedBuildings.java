/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.math.Vector3f;

import de.wuppertal.Coord;
import de.wuppertal.abstractAndInterfaces.AbstractPatchedNode;
import de.wuppertal.abstractAndInterfaces.AbstractSinglePatch;
import de.wuppertal.terrain.GeometryLoadJob.GEOMETRYJOBTYPE;
import de.wuppertal.terrain.Wuppertal3D.CAMERAMOVEMENTSTATUS;
import de.wuppertal.terrain.Wuppertal3D.LOADPRIORITY;
import de.wuppertal.tools.gmlGeometryParser.CityGML2J3O;

import java.io.File;

import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class DynamicPatchedBuildings extends AbstractPatchedNode {

    //~ Static fields/initializers ---------------------------------------------

    private static final int iNeighborhoodLow = 1;
    public static final String sceneGraphID = "buildings_patched";

    //~ Instance fields --------------------------------------------------------

    private boolean bGeometryOnly;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DynamicPatchedBuildings object.
     *
     * @param  folderSrc      DOCUMENT ME!
     * @param  folderDst      DOCUMENT ME!
     * @param  offsetGlobal   DOCUMENT ME!
     * @param  fPatchSize     DOCUMENT ME!
     * @param  ts             DOCUMENT ME!
     * @param  bCreationMode  DOCUMENT ME!
     * @param  bGeometryOnly  DOCUMENT ME!
     */
    public DynamicPatchedBuildings(final File folderSrc,
            final File folderDst,
            final Coord offsetGlobal,
            final int fPatchSize,
            final Wuppertal3D ts,
            final boolean bCreationMode,
            final boolean bGeometryOnly) {
        super(sceneGraphID, fPatchSize, ts, offsetGlobal);
        this.bGeometryOnly = bGeometryOnly;
        if (bCreationMode) {
            final CityGML2J3O app = new CityGML2J3O(folderSrc, folderDst, true, false);
            app.setOffsetx(offsetGlobal.x());
            app.setOffsety(offsetGlobal.y());
            app.startParsingMultiThreaded(4);
        }
        this.setLocalTranslation(0, -ts.MIN_TERRAIN_HEIGHT, 0);
        this.positionsCache = createPositionsVector(iNeighborhoodLow, fPatchSize);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void updateControl(final CAMERAMOVEMENTSTATUS camStatus, final Vector3f currentCamPosition) {
        // die aktuellen IDs die sichtbar sein sollten berechnen
        final Vector<Integer> currentShouldIds = getShouldIds(camStatus, currentCamPosition);
        final Vector<AbstractSinglePatch> patched2dettach = new Vector<AbstractSinglePatch>();
        // existierende patches updaten
        for (final AbstractSinglePatch patchAbstract : mapPatches) {
            final SingleBuildingPatch patch = (SingleBuildingPatch)patchAbstract; // bad code
            patch.updateControl(patched2dettach, currentShouldIds, camStatus);
            currentShouldIds.remove(patch.getId());
        }
        // alte die nicht mehr verwendet werden sollen detachen
        for (final AbstractSinglePatch patch : patched2dettach) {
            detachPatchFromGL(patch);
            mapPatches.remove(patch);
        }
        // wenn die kamera sich gerade schnell bewegt => keine neuen hinzufügen
        if (camStatus == CAMERAMOVEMENTSTATUS.BEAMING) {
            // abbrechen: keine neuen hinzufügen
            return;
        }
        // neue patches hinzufügen
        for (final Integer id2Add : currentShouldIds) {
            final SingleBuildingPatch patch = new SingleBuildingPatch(id2Add, this, true);
            wup3d.addGeometry2BeLoaded(id2Add, patch, LOADPRIORITY.HIGH, GEOMETRYJOBTYPE.BUILDING);
            if (!bGeometryOnly) {
                wup3d.addTexture2BeLoaded(id2Add, -1, patch, LOADPRIORITY.HIGH);
            }
            mapPatches.add(patch);
        }
    }

    @Override
    protected Integer getIdByCamPosition(final float xGlobal, float zGlobal) {
        zGlobal -= 5000000;
        final int ixGlobal = (int)(xGlobal / 1000);
        final int izGlobal = (int)(zGlobal / 1000);
        return (ixGlobal * 1000) + izGlobal;
    }
}
