package fi.stroomdiagrammengwt.client;


public class EdgeCopy 
{
	VertexCopy fromVertexCopy, toVertexCopy;
    Rational capacity;
    long lastTimeChanged;
    int mode;
    public EdgeCopy(Rational c, long t, int m)
    {   capacity = new Rational(c);
        lastTimeChanged = t;
        mode = m;
    }
}
