package fi.grafiek3dgwt.client;

/**
 * a wrapper class containing three 3d-points (in world space):
 * the start-point of an edge, the end point of an edge and a point on that edge  
 * @author huub
 */
class EWP
{   public Vector3D[] edgeWithPoint;
    public EWP(Vector3D[] ewp)
    {   edgeWithPoint = ewp;
    }
}
