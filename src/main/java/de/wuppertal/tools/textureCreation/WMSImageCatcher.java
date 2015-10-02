/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.textureCreation;

import de.wuppertal.BBox;

import java.awt.image.BufferedImage;

import java.net.URL;

import java.util.StringTokenizer;

import javax.imageio.ImageIO;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class WMSImageCatcher {

    //~ Static fields/initializers ---------------------------------------------

    private static WMSImageCatcher instance;
    private static StringBuilder AND = new StringBuilder("&");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    enum ImageType {

        //~ Enum constants -----------------------------------------------------

        JPG(new StringBuilder("FORMAT=image/jpeg")), PNG(new StringBuilder("FORMAT=image/png")),
        TIFF(new StringBuilder("FORMAT=image/tiff"));

        //~ Instance fields ----------------------------------------------------

        private StringBuilder strType;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageType object.
         *
         * @param  strType  DOCUMENT ME!
         */
        ImageType(final StringBuilder strType) {
            this.strType = strType;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public StringBuilder getAsStringBuilder() {
            return this.strType;
        }
        /**
         * DOCUMENT ME!
         *
         * @param   s  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public static ImageType parseImageType(final String s) {
            switch (s) {
                case "image/jpg":
                case "image/jpeg": {
                    return JPG;
                }
                case "image/png": {
                    return PNG;
                }
                case "image/tiff": {
                    return TIFF;
                }
            }
            return null;
        }
    }

    //~ Instance fields --------------------------------------------------------

    private StringBuilder urlSaticPart;
    private StringBuilder urlDynamicPart;
    private StringBuilder imageWidth;
    private StringBuilder imageHeight;
    private StringBuilder styles;
    private StringBuilder layers;
    private StringBuilder request;
    private StringBuilder bbox;
    private StringBuilder srs;
    private ImageType imageType;

    //~ Methods ----------------------------------------------------------------

    /**
     * get WMSCatcher instance.
     *
     * @param   getAsSingleton  true-> uses static instance (singleton) false -> creates new instance
     *
     * @return  DOCUMENT ME!
     */
    public static WMSImageCatcher getInstance(final boolean getAsSingleton) {
        if (getAsSingleton) {
            return new WMSImageCatcher();
        } else {
            if (instance == null) {
                instance = new WMSImageCatcher();
            }
            return instance;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  imageType  DOCUMENT ME!
     */
    public void setImageType(final ImageType imageType) {
        this.imageType = imageType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strImageType  DOCUMENT ME!
     */
    public void setImageType(final String strImageType) {
        this.imageType = ImageType.parseImageType(strImageType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  iWidth  DOCUMENT ME!
     */
    public void setImageWidth(final int iWidth) {
        this.imageWidth = new StringBuilder("WIDTH=");
        imageWidth.append(iWidth);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  iHeight  DOCUMENT ME!
     */
    public void setImageHeight(final int iHeight) {
        this.imageHeight = new StringBuilder("HEIGHT=");
        imageHeight.append(iHeight);
    }

    /**
     * URL must start with "http://" and end width "GetMap" like.
     *
     * @param  url  DOCUMENT ME!
     */
    public void setUrl(final StringBuilder url) {
        this.urlSaticPart = url;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  style  DOCUMENT ME!
     */
    public void setStyle(final StringBuilder style) {
        this.styles = new StringBuilder("STYLES=");
        this.styles.append(style);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  layers  DOCUMENT ME!
     */
    public void setLayers(final StringBuilder layers) {
        this.layers = new StringBuilder("LAYERS=");
        this.layers.append(layers);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bbox  DOCUMENT ME!
     */
    public void setBBox(final BBox bbox) {
        this.bbox = bbox.getAsWMSRequestParameter();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StringBuilder getRequest() {
        updateRequest();
        return request;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StringBuilder getRequestStaticPart() {
        return urlSaticPart;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public StringBuilder getDynamicPart() {
        updateRequest();
        return urlDynamicPart;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  epsg  DOCUMENT ME!
     */
    public void setEPSG(final int epsg) {
        this.srs = new StringBuilder("SRS=EPSG:");
        srs.append(epsg);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url      DOCUMENT ME!
     * @param  bbox     DOCUMENT ME!
     * @param  iWidth   DOCUMENT ME!
     * @param  iHeight  DOCUMENT ME!
     * @param  epsg     DOCUMENT ME!
     * @param  layers   DOCUMENT ME!
     * @param  styles   DOCUMENT ME!
     */
    public void setParameter(final StringBuilder url,
            final BBox bbox,
            final int iWidth,
            final int iHeight,
            final int epsg,
            final StringBuilder layers,
            final StringBuilder styles) {
        setUrl(url);
        setBBox(bbox);
        setImageWidth(iWidth);
        setImageHeight(iHeight);
        setEPSG(epsg);
        setLayers(layers);
        setStyle(styles);
        setImageType(imageType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  exampleRequest  DOCUMENT ME!
     */
    public void setParameterByExampleGetMapRequest(String exampleRequest) {
        // http://s102w384:8399/arcgis/services/WuNDa-Orthophoto-NRW/MapServer/WMSServer?&VERSION=1.1.1&REQUEST=GetMap&BBOX=374327.,5681000,375327.,5682000&WIDTH=4096&HEIGHT=4096&SRS=EPSG:25832&FORMAT=image/png&LAYERS=1&STYLES=default
        if (exampleRequest.startsWith("http://")) {
            setUrl(new StringBuilder(exampleRequest.substring(0, exampleRequest.indexOf("GetMap") + 6)));
            exampleRequest = exampleRequest.substring(exampleRequest.indexOf("GetMap") + 6, exampleRequest.length());
        } else {
            new RuntimeException("unknown WMS example URL");
        }
        final StringTokenizer st = new StringTokenizer(exampleRequest, "&");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final String key = token.substring(0, token.indexOf("="));
            String value = token.substring(token.indexOf("=") + 1, token.length());
            switch (key) {
                case "BBOX": {
                    setBBox(BBox.getBBoxByWMSValue(value));
                    break;
                }
                case "WIDTH": {
                    setImageWidth(Integer.parseInt(value));
                    break;
                }
                case "HEIGHT": {
                    setImageHeight(Integer.parseInt(value));
                    break;
                }
                case "LAYERS": {
                    setLayers(new StringBuilder(value));
                    break;
                }
                case "STYLES": {
                    setStyle(new StringBuilder(value));
                    break;
                }
                case "FORMAT": {
                    setImageType(value);
                    break;
                }
                case "SRS": {
                    final int iIndex = value.indexOf(":");
                    value = value.substring(iIndex + 1, value.length());
                    setEPSG(Integer.parseInt(value));
                    break;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void updateRequest() {
        request = new StringBuilder();
        request.append(urlSaticPart);
        urlDynamicPart = new StringBuilder();
        urlDynamicPart.append(AND);
        urlDynamicPart.append(bbox);
        urlDynamicPart.append(AND);
        urlDynamicPart.append(imageWidth);
        urlDynamicPart.append(AND);
        urlDynamicPart.append(imageHeight);
        urlDynamicPart.append(AND);
        urlDynamicPart.append(srs);
        urlDynamicPart.append(AND);
        urlDynamicPart.append(imageType.getAsStringBuilder());
        urlDynamicPart.append(AND);
        urlDynamicPart.append(layers);
        urlDynamicPart.append(AND);
        urlDynamicPart.append(styles);
        request.append(urlDynamicPart);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   doUpdateRequest  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BufferedImage getImage(final boolean doUpdateRequest) {
        if (doUpdateRequest) {
            updateRequest();
        }
//              System.out.println("Starting request:"+getRequest());
        try {
            return ImageIO.read(new URL(getRequest().toString()));
        } catch (Exception e) {
            System.err.println(getRequest() + " \n request failed");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   iInstances         DOCUMENT ME!
     * @param   wmsExampleRequest  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static WMSImageCatcher[] getInstances(final int iInstances, final String wmsExampleRequest) {
        final WMSImageCatcher[] retInstances = new WMSImageCatcher[iInstances];
        for (int i = 0; i < iInstances; i++) {
            retInstances[i] = new WMSImageCatcher();
            retInstances[i].setParameterByExampleGetMapRequest(wmsExampleRequest);
        }
        return retInstances;
    }

    // http://s102w384:8399/arcgis/services/WuNDa-Orthophoto-NRW/MapServer/WMSServer?&VERSION=1.1.1&REQUEST=GetMap&BBOX=374327.,5681000,375327.,5682000&WIDTH=4096&HEIGHT=4096&SRS=EPSG:25832&FORMAT=image/png&LAYERS=1&STYLES=default
}
