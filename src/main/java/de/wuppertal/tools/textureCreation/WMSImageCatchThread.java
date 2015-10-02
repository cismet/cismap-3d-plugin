/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.textureCreation;

import de.wuppertal.BBox;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class WMSImageCatchThread extends Thread {

    //~ Static fields/initializers ---------------------------------------------

    private static volatile int iJobs2Do;

    //~ Instance fields --------------------------------------------------------

    private long lSleepTime;
    private boolean isSleeping;
    private boolean bAlive;
    private WMSImageCatcher catcher;
    private volatile Stack<WMSImageCatchJob> boxesToDo;
    private WMSImageCatchable receiver;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WMSImageCatchThread object.
     *
     * @param  receiver  DOCUMENT ME!
     * @param  catcher   DOCUMENT ME!
     */
    public WMSImageCatchThread(final WMSImageCatchable receiver, final WMSImageCatcher catcher) {
        lSleepTime = 300;
        bAlive = true;
        isSleeping = true;
        boxesToDo = new Stack<WMSImageCatchJob>();
        this.catcher = catcher;
        this.receiver = receiver;
        start();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        try {
            BBox box2Catch;
            while (bAlive) {
                if (boxesToDo.isEmpty()) {
                    isSleeping = true;
                    sleep(lSleepTime);
                } else {
                    isSleeping = false;
                    final WMSImageCatchJob job = boxesToDo.pop();
                    box2Catch = job.getBBox();
                    catcher.setBBox(box2Catch);
                    job.setImage(catcher.getImage(true));
                    receiver.fireImageReciveEvent(job);
                    iJobs2Do--;
                }
            }
        } catch (Exception e) {
            System.err.println("");
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void kill() {
        bAlive = false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  box2Catch  DOCUMENT ME!
     * @param  id         DOCUMENT ME!
     */
    public void addJob(final BBox box2Catch, final String id) {
        boxesToDo.add(new WMSImageCatchJob(box2Catch, id));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSleeping() {
        return isSleeping;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strWmsExampleURL  DOCUMENT ME!
     * @param  mapBoxesById      DOCUMENT ME!
     * @param  iImageSize        DOCUMENT ME!
     * @param  iThreads          DOCUMENT ME!
     * @param  receiver          DOCUMENT ME!
     */
    public static void createThreadsAndAddJobsMultiThreaded(final String strWmsExampleURL,
            final HashMap<String, BBox> mapBoxesById,
            final int iImageSize,
            final int iThreads,
            final WMSImageCatchable receiver) {
        final WMSImageCatcher[] catchers = WMSImageCatcher.getInstances(iThreads, strWmsExampleURL);
        final WMSImageCatchThread[] threads = new WMSImageCatchThread[iThreads];
        int i = 0;
        for (final WMSImageCatcher catcher : catchers) {
            catcher.setImageHeight(iImageSize);
            catcher.setImageWidth(iImageSize);
            threads[i] = new WMSImageCatchThread(receiver, catcher);
            i++;
        }
        addJobsMuliThreaded(mapBoxesById, threads);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mapBoxesById  DOCUMENT ME!
     * @param  threads       DOCUMENT ME!
     */
    private static void addJobsMuliThreaded(final HashMap<String, BBox> mapBoxesById,
            final WMSImageCatchThread[] threads) {
        final int iThreads = threads.length;
        int iBoxCounter = 0;
        iJobs2Do = mapBoxesById.size();
        for (final Entry<String, BBox> entry : mapBoxesById.entrySet()) {
            final BBox box = entry.getValue();
            final String id = entry.getKey();
            final int index = iBoxCounter % iThreads;
            threads[index].addJob(box, id);
            iBoxCounter++;
        }
        // Wait for jobs
        while (iJobs2Do > 0) {
            try {
                System.out.println("Image2Do = " + iJobs2Do);
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
