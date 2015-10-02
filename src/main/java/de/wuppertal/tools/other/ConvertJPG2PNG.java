/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.other;

import java.awt.image.BufferedImage;

import java.io.File;

import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class ConvertJPG2PNG {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final File folder = new File("C:\\Temp\\ddddaten\\terrain_patched");
//              final File folder = new File("\\\\s102x003\\Wuppertal3D$\\terrain_patched");
        final int iThreads = 8;

        final Vector<File>[] filesPerThread = new Vector[iThreads];

        int iCounter = 0;
        for (final File f : folder.listFiles()) {
            if (f.getName().endsWith(".png")) {
                if (filesPerThread[iCounter % iThreads] == null) {
                    filesPerThread[iCounter % iThreads] = new Vector<File>();
                }
                filesPerThread[iCounter % iThreads].add(f);
                iCounter++;
            }
        }

        final Thread[] threads = new Thread[iThreads];
        for (int i = 0; i < iThreads; i++) {
            final int j = i;
            threads[i] = new Thread() {

                    @Override
                    public void run() {
                        try {
                            int iFiles = 0;
                            for (final File f : filesPerThread[j]) {
                                final BufferedImage buff = ImageIO.read(f);
                                ImageIO.write(
                                    buff,
                                    "JPG",
                                    new File(folder, f.getName().substring(0, f.getName().lastIndexOf(".")) + ".jpg"));
                                System.out.println("Thread " + j + ": " + iFiles + " of " + filesPerThread[j].size());
                                iFiles++;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            threads[i].start();
        }
    }
}
