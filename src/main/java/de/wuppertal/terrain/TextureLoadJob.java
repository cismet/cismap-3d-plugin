/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.terrain;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;

import de.wuppertal.abstractAndInterfaces.AbstractLoadJob;
import de.wuppertal.abstractAndInterfaces.TextureLoadListener;
import de.wuppertal.terrain.Wuppertal3D.LOADPRIORITY;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TextureLoadJob extends AbstractLoadJob {

    //~ Instance fields --------------------------------------------------------

    private TextureLoadListener listener;
    private int resolution;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TextureLoadJob object.
     *
     * @param  id          DOCUMENT ME!
     * @param  resolution  DOCUMENT ME!
     * @param  prio        DOCUMENT ME!
     * @param  listener    DOCUMENT ME!
     */
    public TextureLoadJob(final Integer id,
            final int resolution,
            final LOADPRIORITY prio,
            final TextureLoadListener listener) {
        super(id, prio);
        this.resolution = resolution;
        this.listener = listener;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void doJob(final AssetManager am) {
        if (isProcessable(getId())) {
            Texture t = null;
            ;
            try {
                t = am.loadTexture(resolution + "_" + getId() + ".jpg");
            } catch (Exception e) {
                // System.err.println("Texture \""+resolution+"_"+getId()+".png"+"\" could not be loaded");
                // e.printStackTrace();
                addUnProcessableId(getId());
            }
            if (t != null) {
                listener.fireTextureLoaded(getId(), t);
            }
        }
    }
}
