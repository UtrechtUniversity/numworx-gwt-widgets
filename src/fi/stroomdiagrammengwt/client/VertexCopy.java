package fi.stroomdiagrammengwt.client;

/**
 * class containing basic attributes of a vertex 
 */
public class VertexCopy 
{
	int code; 
    int layerNum;
    int yLocation;
    Rational flow = Rational.unDefined();
    int decimals;
    boolean root;
    /**
     * back tracing the flow in the vertex
     */
    boolean traceFrom; 
    String labelText;
    /**
     * @param code unique code of the vertex
     * @param layerNum layer number (horizontal position) of the vertex
     * @param yLocation vertical location of the vertex
     * @param flow flow through the vertex
     * @param decimals number of decimals if flow displayed as a double
     * @param root is the vertex a root?
     * @param labelText text of the vertex label
     */
    public VertexCopy(int code, int layerNum, int yLocation, Rational flow, int decimals, boolean root,
                      String labelText)
    {   this.code = code;
    	this.layerNum = layerNum;
        this.yLocation = yLocation;
        if (!flow.isUndefined())
        {   this.flow = new Rational(flow);
        }
        this.decimals = decimals;
        this.root = root;
        this.labelText = labelText;
    }
    
}
