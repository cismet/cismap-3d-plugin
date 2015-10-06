/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.c3d;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import de.wuppertal.protoTypes.TestWuppertal3DSwitchable;

import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import de.cismet.cismap.navigatorplugin.CismapPlugin;

import de.cismet.tools.gui.BasicGuiComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@ServiceProvider(service = BasicGuiComponentProvider.class)
public class Cismap3DCanvas implements BasicGuiComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CISMAP_3D_PANEL_ID = "cismet_3d_canvas"; // NOI18N

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon icon;

    private TestWuppertal3DSwitchable sw;
    private JmeCanvasContext ctx;
    private JPanel panel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Cismap3DCanvas object.
     */
    public Cismap3DCanvas() {
        icon = ImageUtilities.loadImageIcon(Cismap3DCanvas.class.getPackage().getName().replaceAll("\\.", "/")
                        + "/globe_16.png",
                false);

        panel = new JPanel();

//        final Runnable r = new Runnable() {
//
//                @Override
//                public void run() {
//        init();
//                }
//            };
//
//        if (EventQueue.isDispatchThread()) {
//            r.run();
//        } else {
//            EventQueue.invokeLater(r);
//        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        System.out.println("init");
        sw = new TestWuppertal3DSwitchable();
        final AppSettings settings = new AppSettings(true);
        settings.setWidth(1024);
        settings.setHeight(768);
        sw.setSettings(settings);
        sw.createCanvas();
        ctx = (JmeCanvasContext)sw.getContext();
        ctx.getCanvas().setPreferredSize(new Dimension(1024, 768));
        panel.add(ctx.getCanvas());
        sw.startCanvas();

        panel.addComponentListener(new ComponentListener() {

                @Override
                public void componentResized(final ComponentEvent e) {
                    System.out.println("resized");
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
        // what's this good for
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final JFrame frame = new JFrame("3D");
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    frame.setSize(1024, 768);
                    frame.setPreferredSize(new Dimension(1024, 768));
                    frame.setLayout(new BorderLayout());
                    final Cismap3DCanvas c = new Cismap3DCanvas();
                    c.init();
                    frame.add(c.getComponent());
                    frame.pack();
                    frame.setVisible(true);
                    frame.toFront();
                }
            });
    }
}
