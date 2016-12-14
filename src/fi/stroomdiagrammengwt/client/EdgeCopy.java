package fi.stroomdiagrammengwt.client;

/**
 * class containing basic attributes of an edge 
 */
public class EdgeCopy 
{
	/**
	 * the edge starts at fromVertex
	 */
	VertexCopy fromVertexCopy;
	/**
	 * the edge ends at toVertex
	 */
	VertexCopy toVertexCopy;
	/**
	 * the capacity of the edge
	 */
    Rational capacity;
    /**
     * time of last change of the edge
     */
    long lastTimeChanged;
    /**
     * mode of the edge, see class Edge
     */
    int mode;
    /**
     * @param capacity the capacity of the edge
     * @param lastTimeChanged time of last change of the edge
     * @param mode mode of the edge, see class Edge
     */
    public EdgeCopy(Rational capacity, long lastTimeChanged, int mode)
    {   this.capacity = new Rational(capacity);
        this.lastTimeChanged = lastTimeChanged;
        this.mode = mode;
    }
}
