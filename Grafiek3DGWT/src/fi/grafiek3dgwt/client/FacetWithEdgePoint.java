package fi.grafiek3dgwt.client;

/**
 * a wrapper class containg a Facet3D and one of its edges with an additional point on that edge, see class EWP  
 * @author huub
 */
class FacetWithEdgePoint
{   public Vector3D[] edgeWithPoint;
    public Facet3D facet;
    public FacetWithEdgePoint(Vector3D[] ewp, Facet3D f)
    {   edgeWithPoint = ewp;
        facet = f;
    }
}
