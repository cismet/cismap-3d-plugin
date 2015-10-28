/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.c3d;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.cismet.cismap.navigatorplugin.CismapPlugin;
import de.cismet.cismap.navigatorplugin.LayoutListener;

import de.cismet.tools.gui.BasicGuiComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@ServiceProvider(service = BasicGuiComponentProvider.class)
public class Cismap3DCanvas implements BasicGuiComponentProvider, LayoutListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CISMAP_3D_PANEL_ID = "cismet_3d_canvas"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon icon;

//    private TestWuppertal3DSwitchable sw;
    private Simple3D sw;
    private JmeCanvasContext ctx;
    private final JPanel panel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Cismap3DCanvas object.
     */
    public Cismap3DCanvas() {
        icon = ImageUtilities.loadImageIcon(Cismap3DCanvas.class.getPackage().getName().replaceAll("\\.", "/")
                        + "/globe_16.png",
                false);

        panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("xxx"));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        System.out.println("init");

        panel.addComponentListener(new ComponentListener() {

                boolean setCanvas = true;

                @Override
                public void componentResized(final ComponentEvent e) {
                    final int width = e.getComponent().getWidth();
                    final int height = e.getComponent().getHeight();

                    if ((width > 0) && (height > 0)) {
                        final Thread t = new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        final AppSettings settings = new AppSettings(true);
                                        System.out.println(
                                            "w="
                                                    + e.getComponent().getWidth()
                                                    + "|h="
                                                    + e.getComponent().getHeight());
                                        settings.setWidth(width);
                                        settings.setHeight(height);

                                        if (setCanvas) {
//                                            sw = new TestWuppertal3DSwitchable();
                                            sw = new Simple3D();
                                            sw.setPauseOnLostFocus(false);
                                            sw.setSettings(settings);
                                            sw.createCanvas();
                                            sw.startCanvas();
                                            ctx = (JmeCanvasContext)sw.getContext();
                                            setCanvas = false;

                                            final Dimension d = new Dimension(width, height);
                                            ctx.getCanvas().setPreferredSize(d);
                                            ctx.getCanvas().setSize(d);

                                            try {
                                                Thread.sleep(500);
                                            } catch (final InterruptedException ex) {
                                                // ignore
                                            }

                                            EventQueue.invokeLater(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        sw.startCanvas();
                                                        panel.removeAll();
                                                        final Canvas c = ctx.getCanvas();
                                                        c.setBounds(panel.getBounds());
                                                        panel.add(c, BorderLayout.CENTER);
//                        ctx.getCanvas().repaint();
                                                    }
                                                });
//                                        } else {
//                                            sw.setSettings(settings);
                                        }
                                    }
                                });

                        t.start();
                    }
                }

                @Override
                public void componentMoved(final ComponentEvent e) {
                    System.out.println("moved");
                }

                @Override
                public void componentShown(final ComponentEvent e) {
                    System.out.println("shown");
                }

                @Override
                public void componentHidden(final ComponentEvent e) {
                    System.out.println("hidden");
                }
            });
        System.out.println("init finished");
    }

    @Override
    public String getId() {
        return CISMAP_3D_PANEL_ID;
    }

    @Override
    public String getName() {
        return "cismap 3D";
    }

    @Override
    public String getDescription() {
        return "cismap 3D Visualisation Panel";
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public JComponent getComponent() {
        System.out.println("getcomp");
        return panel;
    }

    @Override
    public GuiType getType() {
        return GuiType.GUICOMPONENT;
    }

    @Override
    public Object getPositionHint() {
        return CismapPlugin.ViewSection.MAP;
    }

    @Override
    public void setLinkObject(final Object link) {
        if (link instanceof CismapPlugin) {
            final CismapPlugin cismap = (CismapPlugin)link;
            cismap.addLayoutListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final Simple3D s = new Simple3D();
        final AppSettings settings = new AppSettings(true);
        settings.setWidth(640);
        settings.setHeight(480);
        s.setSettings(settings);
        s.createCanvas();
        final JmeCanvasContext ctx = (JmeCanvasContext)s.getContext();
        ctx.setSystemListener(s);
        ctx.getCanvas().setPreferredSize(new Dimension(640, 480));
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JFrame frame = new JFrame("3D");
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.setSize(1024, 768);
                    frame.setPreferredSize(new Dimension(1024, 768));
                    frame.setLayout(new BorderLayout());
//                    final Cismap3DCanvas c = new Cismap3DCanvas();

                    frame.add(ctx.getCanvas(), BorderLayout.CENTER);
                    frame.pack();
                    System.out.println("after pack");
                    frame.setVisible(true);
                    frame.toFront();
                }
            });
    }

    @Override
    public void layoutLoaded() {
        init();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class Simple3D extends SimpleApplication {

        //~ Methods ------------------------------------------------------------

        @Override
        public void simpleInitApp() {
            final Box b = new Box(1, 1, 1);
            final Geometry geom = new Geometry("Box", b);
            final Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geom.setMaterial(mat);
            rootNode.attachChild(geom);
        }
    }
}
