/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.wuppertal.tools.gmlGeometryParser;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

import de.wuppertal.Coord;
import de.wuppertal.CoordVector;
import de.wuppertal.tools.triangulation.J3DTriangulator;

import org.jdom2.Element;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class CityGML2J3O extends AbstractGmlParser {

    //~ Instance fields --------------------------------------------------------

    private double offsetx;
    private double offsety;
    private boolean createpseudoNormals;
    private boolean lightModel;
    private File fileRootDst;
    private AssetManager assetManager;
    private HashMap<File, HashMap<GMLTYPE, Vector<CoordVector>>> mapGml;
    private Vector<Geometry> geoms;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CityGML2J3O object.
     *
     * @param  fileRootSrc          DOCUMENT ME!
     * @param  fileRootDst          DOCUMENT ME!
     * @param  createpseudoNormals  DOCUMENT ME!
     * @param  lightModel           DOCUMENT ME!
     */
    public CityGML2J3O(final File fileRootSrc,
            final File fileRootDst,
            final boolean createpseudoNormals,
            final boolean lightModel) {
        super(fileRootSrc);
        this.fileRootDst = fileRootDst;
        this.createpseudoNormals = createpseudoNormals;
        this.lightModel = lightModel;
        assetManager = new DesktopAssetManager(true);
        offsetx = 0;
        offsety = 0;
        mapGml = new HashMap<File, HashMap<GMLTYPE, Vector<CoordVector>>>();
        geoms = new Vector<Geometry>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        // CityGML2J3O app = new CityGML2J3O(new File("C:\\Temp\\ddddaten\\rohdaten\\citygml"), new
        // File("C:\\Temp\\ddddaten\\buildings")); //                 app.setOffsetx(0.03613953E7); //
        // app.setOffsety(5669917.94); //            app.setOffsetx(0.03613953E7); //
        // app.setOffsety(5686617.94); app.startParsingMultiThreaded(1);
    }

    @Override
    protected void handleCoordVector(final CoordVector vec,
            final File f,
            final Element e,
            final GMLTYPE gmlType,
            final Iterator<Element> processDescendants) {
        HashMap<GMLTYPE, Vector<CoordVector>> typevecvec = mapGml.get(f);
        if (typevecvec == null) {
            typevecvec = new HashMap<GMLTYPE, Vector<CoordVector>>();
            mapGml.put(f, typevecvec);
        }
        Vector<CoordVector> vecvec = typevecvec.get(gmlType);
        if (vecvec == null) {
            vecvec = new Vector<CoordVector>();
            typevecvec.put(gmlType, vecvec);
        }
        vecvec.add(vec);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  offsetx  DOCUMENT ME!
     */
    public void setOffsetx(final double offsetx) {
        this.offsetx = offsetx;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  offsety  DOCUMENT ME!
     */
    public void setOffsety(final double offsety) {
        this.offsety = offsety;
    }

    @Override
    protected void finishedParsing(final File f) {
        final Node n = new Node(f.getName());
        final HashMap<GMLTYPE, Vector<CoordVector>> map = mapGml.remove(f);
        for (final Entry<GMLTYPE, Vector<CoordVector>> entry : map.entrySet()) {
            final GMLTYPE gmltype = entry.getKey();
            final Vector<CoordVector> vecvecTriangles = polygons2GLTriangles(entry.getValue());
            final Vector3f[] vertices = new Vector3f[vecvecTriangles.size() * 3];
            final Vector3f[] normals = new Vector3f[vertices.length];
            final int[] indexes = new int[vertices.length]; // { 2,0,1, 1,3,2 };
            int iVertices = 0;
            for (final CoordVector vec : vecvecTriangles) {
                for (final Coord c : vec.getCoords()) {
                    vertices[iVertices] = new Vector3f((float)c.x(), ((float)c.z()), (float)c.y());
                    indexes[iVertices] = iVertices;
                    iVertices++;
                }
                Vector3f normal = null;

                if (createpseudoNormals) {
                    normal = getNormal(vertices[iVertices - 1], vertices[iVertices - 2], vertices[iVertices - 3]);
                    normal.multLocal(-1, 1, -1);
                    normal.setY(1);
                    normal.normalizeLocal();
                }

                normals[iVertices - 1] = normal;
                normals[iVertices - 2] = normal;
                normals[iVertices - 3] = normal;
            }
            iVertices = 0;
            final Mesh mesh = new Mesh();
            Material mat = null;
            if (lightModel) {
                mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
                mesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
                mesh.updateBound();
                mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                mat.setBoolean("UseMaterialColors", true);
                if (gmltype.equals(GMLTYPE.WALLSURFACE)) {
                    mat.setColor("Diffuse", ColorRGBA.Gray);
                    mat.setColor("Specular", ColorRGBA.Gray);
                    mat.setFloat("Shininess", 5f);
                } else if (gmltype.equals(GMLTYPE.ROOFSURFACE)) {
                    mat.setColor("Diffuse", ColorRGBA.Red.mult(0.8f));
                    mat.setColor("Specular", ColorRGBA.Red.mult(0.8f));
                    mat.setFloat("Shininess", 3f);
                }
                mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
            } else {
                mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
                mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
//                              mesh.setBuffer(Type.Normal,    3, BufferUtils.createFloatBuffer(normals));
                mesh.updateBound();
                mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                if (gmltype.equals(GMLTYPE.WALLSURFACE)) {
                    mat.setColor("Color", ColorRGBA.Gray.mult(0.7f));
                } else if (gmltype.equals(GMLTYPE.ROOFSURFACE)) {
                    mat.setColor("Color", ColorRGBA.Red.mult(0.4f));
                }
                mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
            }
            final Geometry geo = new Geometry("mymesh", mesh); // using our custom mesh object

            geo.setMaterial(mat);
            geo.updateModelBound();
            n.attachChild(geo);
            System.out.println(n.getWorldBound().toString());
        }
        // Expoprt to j3o
        final BinaryExporter exporter = new BinaryExporter();
        try {
            final String name = f.getName();
            String x = name.substring(2, 5);
            String y = name.substring(7, 10);
            while (x.length() < 3) {
                x = "0" + x;
            }
            while (y.length() < 3) {
                y = "0" + y;
            }

            exporter.save(n, new File(fileRootDst, x + y + ".j3o"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vecvec  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Vector<CoordVector> polygons2GLTriangles(final Vector<CoordVector> vecvec) {
        final Vector<CoordVector> vecvecret = new Vector<CoordVector>();
        for (final CoordVector vec : vecvec) {
            final CoordVector vecTemp = new CoordVector();
            for (final Coord c : vec.getCoords()) {
                final Coord cret = new Coord(c.x() - offsetx, c.y() - offsety, c.z());
                vecTemp.add(cret);
            }
            vecTemp.remove(vecTemp.size() - 1);
            for (final CoordVector vecret2 : J3DTriangulator.triangulate(vecTemp)) {
                // if(getNormal(vecret2).z<0){
                // vecvecret.add(vecret2.getReversed());
                // }
                // else{
                vecvecret.add(vecret2);
                // }
            }

            // CoordVector vecret = new CoordVector();
            // int i=0;
            // int size = vec.size();
            // System.out.println(size);
            // switch (size){
            // //triangle
            // case 4:{
            // for(Coord c:vec.getCoords()){
            // Coord cret = new Coord(c.x()-offsetx, c.y()-offsety, c.z());
            // vecret.add(cret);
            // if(i==2){
            // break;
            // }
            // i++;
            // }
            // vecvecret.add(vecret);
            // break;
            // }
            // //Quad
            // case 5:{
            // for(Coord c:vec.getCoords()){
            // Coord cret = new Coord(c.x()-offsetx, c.y()-offsety, c.z());
            // vecret.add(cret);
            // i++;
            // }
            // CoordVector vecret2 = new CoordVector();
            // vecret2.add(vecret.getCoord(0));
            // vecret2.add(vecret.getCoord(1));
            // vecret2.add(vecret.getCoord(2));
            // vecvecret.add(vecret2);
            // CoordVector vecret3 = new CoordVector();
            // vecret3.add(vecret.getCoord(0));
            // vecret3.add(vecret.getCoord(2));
            // vecret3.add(vecret.getCoord(3));
            // vecvecret.add(vecret3);
            // break;
            // }
            // //Quick And Dirty triangulation => NO ISLANDS! FOR CONVEX POLYGONS ONLY!!!
            // default:{
            // for(Coord c:vec.getCoords()){
            // Coord cret = new Coord(c.x()-offsetx, c.y()-offsety, c.z());
            // vecret.add(cret);
            // i++;
            // }
            // for(int j=0;j<size-3;j++){
            // CoordVector vecret2 = new CoordVector();
            // vecret2.add(vecret.getCoord(0));
            // vecret2.add(vecret.getCoord(j+1));
            // vecret2.add(vecret.getCoord(j+2));
            // vecvecret.add(vecret2);
            // }
            // break;
            // }
            // }

        }
        return vecvecret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  am  DOCUMENT ME!
     */
    public void setAssetManager(final AssetManager am) {
        this.assetManager = am;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Vector<Geometry> getGeoms() {
        return geoms;
    }

    @Override
    public Vector3f getNormal(final Vector3f p00, final Vector3f p10, final Vector3f p01) {
        final Vector3f v0 = p10.subtract(p00);
        final Vector3f v1 = p01.subtract(p00);
        return v0.cross(v1).normalizeLocal().multLocal(-1);
    }

    @Override
    protected void parseGMLClass(final String currentName,
            final File f,
            Element e,
            final Iterator<Element> processDescendants) {
        GMLTYPE type = null;
        switch (currentName) {
            case "WallSurface": {
                type = GMLTYPE.WALLSURFACE;
                break;
            }
            case "RoofSurface": {
                type = GMLTYPE.ROOFSURFACE;
                break;
            }
            case "lod4Geometry": {
                type = GMLTYPE.LOD4GEOMETRY;
                break;
            }
            default: {
                return;
            }
        }

        while (!(e = processDescendants.next()).getName().equals(GMLTYPE.POSLIST.getName())) {
            // skip Elements
        }
        parsePosList(f, e, type, processDescendants);
    }
}
