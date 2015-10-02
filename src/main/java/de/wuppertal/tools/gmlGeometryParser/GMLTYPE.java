/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.gmlGeometryParser;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public enum GMLTYPE {

    //~ Enum constants ---------------------------------------------------------

    NAME("name", "gml", GMLTYPETYPE.CLASS), BOUNDEDBY("bounedby", "gml", GMLTYPETYPE.CLASS),
    ENVELOPE("Envelop", "gml", GMLTYPETYPE.CLASS), LOWERCORNER("lowerCorner", "gml", GMLTYPETYPE.CLASS),
    UPPERCORNER("UpperCorner", "gml", GMLTYPETYPE.CLASS),

    CITYOBJECTMEMBER("cityObjectMember", "core", GMLTYPETYPE.CLASS), BUILDING("Building", "bldg", GMLTYPETYPE.CLASS),

    WALLSURFACE("WallSurface", "bldg", GMLTYPETYPE.CLASS), FEATUREMEMBER("featureMember", "gml", GMLTYPETYPE.CLASS),
    GROUNDSURFACE("GroundSurface", "bldg", GMLTYPETYPE.CLASS),

    ROOFSURFACE("RoofSurface", "bldg", GMLTYPETYPE.CLASS),
    LOD2MULTISURFACE("lod2MultiSurface", "bldg", GMLTYPETYPE.CLASS),
    LOD4GEOMETRY("lod4Geometry", "bldg", GMLTYPETYPE.CLASS), MULTISURFACE("MultiSurface", "gml", GMLTYPETYPE.CLASS),
    SURFACEMEMBER("surfaceMember", "gml", GMLTYPETYPE.CLASS),
    SURFACEPROPERTY("surfaceProperty", "gml", GMLTYPETYPE.CLASS), POLYGON("POLYGON", "gml", GMLTYPETYPE.CLASS),
    EXTERIOR("exterior", "gml", GMLTYPETYPE.CLASS), LINEARRING("LinearRing", "gml", GMLTYPETYPE.CLASS),
    MULTISURFACEPROPERTY("multiSurfaceProperty", "gml", GMLTYPETYPE.CLASS),
    POSLIST("posList", "gml", GMLTYPETYPE.CLASS);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum GMLTYPETYPE {

        //~ Enum constants -----------------------------------------------------

        CLASS, ATTRIBUTE
    }

    //~ Instance fields --------------------------------------------------------

    private String name;
    private int nameHashcode;
    private String nameSpace;
    private String namefull;
    private GMLTYPETYPE type;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GMLTYPE object.
     *
     * @param  name       DOCUMENT ME!
     * @param  nameSpace  DOCUMENT ME!
     * @param  type       DOCUMENT ME!
     */
    private GMLTYPE(final String name, final String nameSpace, final GMLTYPETYPE type) {
        this.name = name;
        this.nameHashcode = name.hashCode();
        this.nameSpace = nameSpace;
        this.namefull = this.nameSpace + ":" + this.name;
        this.type = type;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getNameSpace() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String get() {
        return namefull;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GMLTYPETYPE getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type          DOCUMENT ME!
     * @param   typeHashcode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean matches(final String type, final int typeHashcode) {
        if ((typeHashcode == nameHashcode) && type.equals(name)) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   strType  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static GMLTYPE parseTye(final String strType) {
        final int typeHashCode = strType.hashCode();
        for (final GMLTYPE type : GMLTYPE.values()) {
            if (type.matches(strType, typeHashCode)) {
                return type;
            }
        }
        return null;
    }
}
