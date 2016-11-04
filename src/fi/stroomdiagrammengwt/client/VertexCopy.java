package fi.stroomdiagrammengwt.client;

public class VertexCopy 
{
	// attributes
	int code;
    int layerNum;
    int yLocation;
    Rational flow = Rational.unDefined();
    int decimals;
    boolean root;  
    boolean traceFrom; 
    String labelText;
    //Vertex vertex;
    //Vector inEdgeCopies = new Vector();
    //Vector outEdgeCopies = new Vector();
    public VertexCopy(int co, int ln, int yl, Rational f, int d, boolean r,
                      String lText)
    {   code = co;
    	layerNum = ln;
        yLocation = yl;
        if (!f.isUndefined())
            flow = new Rational(f);
        decimals = d;
        root = r;
        labelText = lText;
    }
}
