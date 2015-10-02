/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.abstractAndInterfaces;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import de.wuppertal.Coord;
import de.wuppertal.terrain.Wuppertal3D;
import de.wuppertal.terrain.Wuppertal3D.CAMERAMOVEMENTSTATUS;

import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractPatchedNode extends Node {

    //~ Instance fields --------------------------------------------------------

    protected float fPatchSize;
    protected Wuppertal3D wup3d;
    protected Vector2f[] positionsCache;
    protected Vector<AbstractSinglePatch> mapPatches;
    protected Stack<AbstractSinglePatch> patches2Attach;
    protected Stack<AbstractSinglePatch> patches2Detach;
    protected Coord offsetGlobal;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractPatchedNode object.
     *
     * @param  scenegraphid  DOCUMENT ME!
     * @param  fPatchSize    DOCUMENT ME!
     * @param  wup3d         DOCUMENT ME!
     * @param  offsetGlobal  DOCUMENT ME!
     */
    public AbstractPatchedNode(final String scenegraphid,
            final float fPatchSize,
            final Wuppertal3D wup3d,
            final Coord offsetGlobal) {
        super(scenegraphid);
        this.offsetGlobal = offsetGlobal;
        this.fPatchSize = fPatchSize;
        this.wup3d = wup3d;
        this.patches2Attach = new Stack<AbstractSinglePatch>();
        this.patches2Detach = new Stack<AbstractSinglePatch>();
        this.mapPatches = new Vector<AbstractSinglePatch>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  camStatus           DOCUMENT ME!
     * @param  currentCamPosition  DOCUMENT ME!
     */
    protected abstract void updateControl(CAMERAMOVEMENTSTATUS camStatus, Vector3f currentCamPosition);

    /**
     * DOCUMENT ME!
     *
     * @param  tpf  DOCUMENT ME!
     */
    public void update(final float tpf) {
        // Attach max 1 per Frame
        AbstractSinglePatch patch = null;
        synchronized (patches2Attach) {
            while (!patches2Attach.isEmpty()) {
                patch = patches2Attach.pop();
                this.attachChild(patch.getNode());
                patch.setAttached2GL();
            }
        }
        // one per Frame
        synchronized (patches2Detach) {
            while (!patches2Detach.isEmpty()) {
                patch = patches2Detach.pop();
                if (this.hasChild(patch.getNode())) {
                    this.detachChild(patch.getNode());
                    patch.setDetachedFromGL();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   camStatus           DOCUMENT ME!
     * @param   currentCamPosition  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Vector<Integer> getShouldIds(final CAMERAMOVEMENTSTATUS camStatus, final Vector3f currentCamPosition) {
        final float xGlobal = currentCamPosition.x + ((float)offsetGlobal.x());
        final float zGlobal = ((float)offsetGlobal.y()) - currentCamPosition.z;
        final Vector<Integer> shouldIdsLow = new Vector<Integer>();
        for (final Vector2f pos : positionsCache) {
            shouldIdsLow.add(0, getIdByCamPosition(xGlobal + pos.x, zGlobal + pos.y));
        }
        return shouldIdsLow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   xGlobal  DOCUMENT ME!
     * @param   zGlobal  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Integer getIdByCamPosition(float xGlobal, float zGlobal);

    /**
     * DOCUMENT ME!
     *
     * @param   n           DOCUMENT ME!
     * @param   fPatchSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Vector2f[] createPositionsVector(final float n, final float fPatchSize) {
        final TreeMap<Float, Vector2f> coordsByDistance = new TreeMap<Float, Vector2f>();
        // n =1 => 9, n =2  => 25, n =3 => 49, n =4 => 81,
        // System.out.println("Start adding");
        for (float x = -n * fPatchSize; x <= (+n * fPatchSize); x += fPatchSize) {
            for (float z = -n * fPatchSize; z <= (n * fPatchSize); z += fPatchSize) {
                // addSinglePatch(new Vector3f(x, currentCamPosition.y, z));
                float sqrt = FastMath.sqrt((x * x) + (z * z));
                while (coordsByDistance.containsKey(sqrt)) {
                    sqrt -= 0.01;
                }
                coordsByDistance.put(sqrt, new Vector2f(x, z));
            }
        }
        final Vector2f[] positions = new Vector2f[coordsByDistance.size()];
        int i = 0;
        for (final Vector2f c : coordsByDistance.values()) {
            positions[i] = c;
            i++;
        }
        return positions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  patch2detach  DOCUMENT ME!
     */
    public void detachPatchFromGL(final AbstractSinglePatch patch2detach) {
        synchronized (patches2Detach) {
            if (!patches2Detach.contains(patch2detach)) {
                patches2Detach.add(patch2detach);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  patch2attach  DOCUMENT ME!
     */
    public void attachPatch2GL(final AbstractSinglePatch patch2attach) {
        synchronized (patches2Attach) {
            if (!patches2Attach.contains(patch2attach)) {
                patches2Attach.add(patches2Attach.size(), patch2attach);
            }
        }
    }
}
