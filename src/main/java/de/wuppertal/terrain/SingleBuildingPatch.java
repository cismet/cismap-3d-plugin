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
import com.jme3.scene.Spatial;
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
public class SingleBuildingPatch extends AbstractSinglePatch implements TextureLoadListener, GeometryLoadListener {

    //~ Static fields/initializers ---------------------------------------------

    public static boolean bWireframe = false;

    //~ Instance fields --------------------------------------------------------

    private volatile boolean isGeometryFileLoaded;
    private volatile boolean isTextureFileLoaded;
    private final boolean isGeometryOnly;
    private Texture texture;
    private DynamicPatchedBuildings patchedBuildings;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SingleBuildingPatch object.
     *
     * @param  id2Add                   DOCUMENT ME!
     * @param  dynamicPatchedBuildings  DOCUMENT ME!
     * @param  isGeometryOnly           DOCUMENT ME!
     */
    public SingleBuildingPatch(final Integer id2Add,
            final DynamicPatchedBuildings dynamicPatchedBuildings,
            final boolean isGeometryOnly) {
        super(id2Add);
        this.patchedBuildings = dynamicPatchedBuildings;
        isGeometryFileLoaded = false;
        isDetachingFromGL = false;
        isAttached2GL = false;
        isAttaching2GL = false;
        this.isGeometryOnly = isGeometryOnly;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  patches2Detach  DOCUMENT ME!
     * @param  currentIds      DOCUMENT ME!
     * @param  camStatus       DOCUMENT ME!
     */
    public void updateControl(final Vector<AbstractSinglePatch> patches2Detach,
            final Vector<Integer> currentIds,
            final CAMERAMOVEMENTSTATUS camStatus) {
        if (!currentIds.contains(id)) {
            // wennn this ID gar nicht mehr vorhanden ist => detaching
            isDetachingFromGL = true;
            patches2Detach.add(this);
            return;
        }
        // wenn geometry und texture geladen wurde, dieses patch aber noch nicht im GL ist => GL Laden anstoßen
        if (isGeometryFileLoaded && (isTextureFileLoaded || isGeometryOnly) && !isDetachingFromGL && !isAttaching2GL
                    && !isAttached2GL) {
            for (final Spatial sp : node.getChildren()) {
                final Geometry g = (Geometry)sp;
                if (g != null) {
                    if (!isGeometryOnly) {
                        g.getMaterial().setTexture("DiffuseMap", texture);
                    }
                    g.getMaterial().getAdditionalRenderState().setWireframe(bWireframe);
                }
            }
            patchedBuildings.attachPatch2GL(this);
            isAttaching2GL = true;
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
            texture = t;
            isTextureFileLoaded = true;
        }
    }
}
