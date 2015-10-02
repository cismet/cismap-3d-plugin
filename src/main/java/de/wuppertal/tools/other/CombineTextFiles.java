/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.other;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class CombineTextFiles {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
//              File folderSrc = new File("\\\\s102gs\\_102-hoehendaten\\DATEN\\LASERDATEN-PUNKTWOLKE\\2009\\BODENPUNKTE\\ASCII\\ETRS89");
//              File fileDest = new File("C:\\temp\\","ls_points2009.txt");
//
        final File folderSrc = new File("C:\\Temp\\ddddaten\\DGM5_land");
        final File fileDest = new File("C:\\Temp\\ddddaten\\", "dgm50_land.txt");

        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(fileDest));
            int iLines = 0;
            int iFile = 0;
            String strFileName;
            for (final File f : folderSrc.listFiles()) {
                strFileName = f.getName();
                System.out.println("reading File " + strFileName + " File " + iFile++ + " of "
                            + folderSrc.listFiles().length);
//                              if(strFileName.contains("_LpB.txt") && !strFileName.equals("inhalt_LpB.txt")){
                final BufferedReader br = new BufferedReader(new FileReader(f));
                String line = null;
                while ((line = br.readLine()) != null) {
                    bw.write(line);
                    bw.newLine();
                    if ((iLines % 10000) == 0) {
                        bw.flush();
                    }
                    iLines++;
                }
                br.close();
//                              }
            }
            bw.close();
            System.out.println(iLines + " lines copied. ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
