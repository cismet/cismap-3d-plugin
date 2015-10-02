/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

import de.wuppertal.abstractAndInterfaces.AbstractLoadJob;
import de.wuppertal.abstractAndInterfaces.GeometryLoadListener;
import de.wuppertal.terrain.Wuppertal3D.LOADPRIORITY;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class GeometryLoadJob extends AbstractLoadJob {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum GEOMETRYJOBTYPE {

        //~ Enum constants -----------------------------------------------------

        TIN, BUILDING
    }

    //~ Instance fields --------------------------------------------------------

    private GeometryLoadListener listener;
    private GEOMETRYJOBTYPE type;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GeometryLoadJob object.
     *
     * @param  id        DOCUMENT ME!
     * @param  prio      DOCUMENT ME!
     * @param  listener  DOCUMENT ME!
     * @param  type      DOCUMENT ME!
     */
    public GeometryLoadJob(final Integer id,
            final LOADPRIORITY prio,
            final GeometryLoadListener listener,
            final GEOMETRYJOBTYPE type) {
        super(id, prio);
        this.listener = listener;
        this.type = type;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void doJob(final AssetManager am) {
        if (isProcessable(getId())) {
            String name = "";
            switch (type) {
                case TIN: {
                    name = "_" + getId() + ".j3o";
                    break;
                }
                case BUILDING: {
                    name = "" + getId() + ".j3o";
                    break;
                }
            }
            Node n = null;
            try {
                n = (Node)am.loadModel(name);
            } catch (Exception e) {
                addUnProcessableId(getId());
//                              System.err.println("Geometry of type "+type.name()+" \""+name+"\"");
            }
            if (n != null) {
                listener.fireGeometryLoaded(getId(), n);
            }
        }
    }
}
