package fi.doorziengwt.client;

/**
 * wrapper class, see class FacetWithVertex in Grafiek3DGWT
 * @author huub
 */
public class FacetWithVertex
{   public Vector3D vertex;
    public Facet3D facet;
    public FacetWithVertex(Vector3D v, Facet3D f)
    {   vertex = v;
        facet = f;
    }
}
