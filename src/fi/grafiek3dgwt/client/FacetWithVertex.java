package fi.grafiek3dgwt.client;

/**
 * a wrapper class containg a Facte3D and one of its vertices
 * @author huub
 */
class FacetWithVertex
{   public Vector3D vertex;
    public Facet3D facet;
    public FacetWithVertex(Vector3D v, Facet3D f)
    {   vertex = v;
        facet = f;
    }
}
