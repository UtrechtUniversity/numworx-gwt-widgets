package fi.doorziengwt.client;

/**
 * wrapper class, see class FacetWithEdgepoint in Grafiek3DGWT
 * @author huub
 */

public class FacetWithEdgePoint
{   public Vector3D[] edgeWithPoint;
    public Facet3D facet;
    public FacetWithEdgePoint(Vector3D[] ewp, Facet3D f)
    {   edgeWithPoint = ewp;
        facet = f;
    }
}
