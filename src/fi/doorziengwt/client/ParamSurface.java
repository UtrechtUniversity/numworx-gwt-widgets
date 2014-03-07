package fi.doorziengwt.client;


//import java.awt.Color;
import com.google.gwt.canvas.dom.client.CssColor;

// for parametrized surfaces use another abstract class in between
// Object3D and the actual object
public abstract class ParamSurface extends Object3D
{   int paramNum;
    double[] params;
    double uMin, uMax, vMin, vMax, uStep, vStep;
    int uSteps, vSteps;
    boolean reverseNormal = false;
    CssColor sColor;
    // create vertices then facets    
    public void create()
    {   
        
        numVertices = (uSteps + 1) * (vSteps + 1);
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        for (int i = 0; i < uSteps + 1; i++)        
            for (int j = 0; j < vSteps + 1; j++)
            {   vertices[i + j * (uSteps + 1)] = 
                    getValueAt(uMin + i * uStep, vMin + j * vStep);
            }
        numFacets = uSteps * vSteps;
        facets = new Facet3D[numFacets];
        for (int i = 0; i < uSteps; i++)        
            for (int j = 0; j < vSteps; j++)
            {   int[] temp = new int[4];
                temp[0] = i + j * (uSteps + 1);
                temp[2] = (i + 1) + (j + 1) * (uSteps + 1);
                if (reverseNormal)
                {   temp[3] = (i + 1) + j * (uSteps + 1);
                    temp[1] = i + (j + 1) * (uSteps + 1);
                }
                else
                {   temp[1] = (i + 1) + j * (uSteps + 1);
                    temp[3] = i + (j + 1) * (uSteps + 1);
                }
                facets[i + j * uSteps] = new Facet3D(vertices, temp, sColor);
            }
           
    }    
    // redefined??
    public void setFiner(boolean b)
    {   if (b && (uSteps * vSteps < 1000))
        {   uSteps *= 2; vSteps *= 2;
        }    
        else
        if (!b && (uSteps * vSteps > 100))
        {   uSteps /= 2; vSteps /= 2;
        }    
        // finds stepsizes
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        create();
        // center/diameter remain unchanged
    }    
    // set the parameters of copy equal to the parameters of
    // this surface
    public void copyParameters(ParamSurface copy)
    {   copy.paramNum = paramNum;
        copy.params = new double[paramNum];
        for (int i = 0; i < paramNum; i++)
            copy.params[i] = params[i];  
        copy.uMin = uMin;
        copy.uMax = uMax;
        copy.vMin = vMin;
        copy.vMax = vMax;
        copy.uStep = uStep;
        copy.vStep = vStep;
        copy.uSteps = uSteps;
        copy.vSteps = vSteps;
        copy.reverseNormal = reverseNormal;
        copy.sColor = sColor;
        
    }    
    // A is label number 1, Z is number 26
    // AA is number 27
    public String getLabel(int i)
    {   String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int cycles = (i - 1) / 26;
        int character = (i - 1) % 26;
        String result = "";
        // assume maximum 26*26=676 labels
        if (cycles >= 1)
            result += alphabet.charAt(cycles - 1);
        result += alphabet.charAt(character);
    
        return result;
    }
    
    abstract public Vector3D getValueAt(double u, double v);
}


// an ellipsoid
class Ellipsoid extends ParamSurface
{   // "empty" ellipsoid, use for copying
    public Ellipsoid()
    {
    }    
    public Ellipsoid(double bigC, double smallC1, double smallC2, CssColor oc)
    {   paramNum = 3;
        params = new double[3];
        params[0] = bigC;
        params[1] = smallC1;
        params[2] = smallC2;
        // u-range, v-range
        uMin = 0;
        uMax = 2 * Math.PI;
        vMin = - Math.PI / 2;
        vMax = Math.PI / 2;
        // step number
        uSteps = 15;
        vSteps = 15;
        // finds steps
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        // create facets
        sColor = oc;
        create();
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
            {   int index = facets[fCnt].indices[vCnt];
                int cycleNum = index / (uSteps + 1);
                int cyclePos = index % (uSteps + 1);
                // bottom point
                if (cycleNum == 0)
                {   
                    facets[fCnt].vertexLabels[vCnt] = getLabel(1);                
                }
                // middle part
                else if (cycleNum < vSteps)
                {
                    if (cyclePos < uSteps)
                    {    facets[fCnt].vertexLabels[vCnt] =
                            getLabel(uSteps * (cycleNum - 1) + cyclePos + 2);
//                         numVertexLabels++;              
                    }        
                    else // cyclePos == uSteps
                    {   facets[fCnt].vertexLabels[vCnt] =
                            getLabel(uSteps * (cycleNum - 1) + 2);                
                    }    
                }        
                // top point
                else if (cycleNum == vSteps)
                {
                    facets[fCnt].vertexLabels[vCnt] =
                        getLabel(uSteps * (cycleNum - 1) + 2);
                }        
                
            }    
        
        numVertexLabels = uSteps * (vSteps - 1) + 2;
        
        double d = 2 * Math.max(params[0], Math.max(params[1], params[2]));
        // center at (0,0,0) no need to calculate
        diameter = d;
        initObject3D(true, new Vector3D(), false);        
    } // constructor
    
    // parametrization
    // [0,2Pi)x[-Pi/2,Pi/2)->space
    // ( a*cos(v)*cos(u), b*cos(v)*sin(u), c*sin(v) )
    public Vector3D getValueAt(double u, double v)
    {   return new Vector3D( params[0] * Math.cos(v) * Math.cos(u),
                             params[1] * Math.cos(v) * Math.sin(u),
                             params[2] * Math.sin(v));
    }
    
    public Object3D deepCopy()
    {   Ellipsoid copy = new Ellipsoid();
        copyParameters(copy);
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // class Ellipsoid

// a cylinder
class Cylinder extends ParamSurface
{   boolean top, bottom;
    // "empty" cylinder, use for copying
    public Cylinder()
    {
    }    
    public Cylinder(double radius, double height, CssColor oc, boolean t, boolean b)
    {   top = t;
        bottom = b;
        paramNum = 2;
        params = new double[2];
        params[0] = radius;
        params[1] = height;
        // u-range, v-range
        uMin = - Math.PI;
        uMax = Math.PI;
        vMin = - params[1] / 2;
        vMax = params[1] / 2;
        // step number
        uSteps = 19;
        vSteps = 1;
        // finds steps
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        // create facets
        sColor = oc;
        create();
        
        
//        numVertexLabels = 0;
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
            {   int index = facets[fCnt].indices[vCnt];
                int cycleNum = index / (uSteps + 1);
                int cyclePos = index % (uSteps + 1);
                // bottom
                if (cycleNum == 0)
                {   // first uSteps points
                    if (cyclePos < uSteps)
                    {   facets[fCnt].vertexLabels[vCnt] =
                            getLabel(cyclePos + 1);                
//                         numVertexLabels++;           
                    }
                    else // cyclePos == uSteps
                        facets[fCnt].vertexLabels[vCnt] =
                            getLabel(1);                
                }
                // top
                else if (cycleNum == 1)
                {
                    if (cyclePos < uSteps)
                    {    facets[fCnt].vertexLabels[vCnt] =
                            getLabel(uSteps * cycleNum + cyclePos + 1);
//                         numVertexLabels++;              
                    }        
                    else // cyclePos == uSteps
                    {   facets[fCnt].vertexLabels[vCnt] =
                            getLabel(uSteps * cycleNum + 1);                
                    }    
                }        
            }    
        
        
        if (top)
        {
            // by construction the vertices of the top are in positions
            // 0+vSteps*(uSteps+1),...,uSteps+vSteps*(uSteps+1)with first
            // and last equal
            int[] inds = new int[uSteps];
            for (int i = 0; i < uSteps; i++)
                inds[i] = i + vSteps*(uSteps+1);
            Facet3D f = new Facet3D(vertices, inds, sColor);
            for (int vCnt = 0; vCnt < f.numPoints; vCnt++)
            {   if (vCnt < uSteps)
                    f.vertexLabels[vCnt] = getLabel(uSteps + vCnt + 1);
                else // vCnt == uSteps
                    f.vertexLabels[vCnt] = getLabel(uSteps + 1);
            }    
            addFacet(f);
            
        }
        if (bottom)
        {
            // by construction the vertices of the bottom are in positions
            // 0...uSteps with first and last equal
            int[] inds = new int[uSteps];
            for (int i = 0; i < uSteps; i++)
                inds[i] = uSteps - i - 1;
            Facet3D f = new Facet3D(vertices, inds, sColor);
            for (int vCnt = 0; vCnt < f.numPoints; vCnt++)
            {   if (vCnt > 0)
                    f.vertexLabels[vCnt] = getLabel(uSteps - vCnt);
                else // vCnt == 0
                    f.vertexLabels[vCnt] = getLabel(uSteps);
            }    
            
            addFacet(f);
            
        }    
        
                
        numVertexLabels = (vSteps + 1) * uSteps;      
        
        // find diameter
        // center is at (0,0,0) no need to calculate and center
        initObject3D(true, new Vector3D(), false);        
        
    } // constructor

/*    
    // A is label number 1, Z is number 26
    // AA is number 27
    public String getLabel(int i)
    {   String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int cycles = (i - 1) / 26;
        int character = (i - 1) % 26;
        String result = "";
        // assume maximum 26*26=676 labels
        if (cycles >= 1)
            result += alphabet.charAt(cycles - 1);
        result += alphabet.charAt(character);
    
        return result;
    }
*/    
    // parametrization
    // [0,2Pi)x[-height/2,height/2]->space
    // ( radius*cos(u), radius*sin(u), v )
    public Vector3D getValueAt(double u, double v)
    {   return new Vector3D( params[0] * Math.cos(u),
                             params[0] * Math.sin(u),
                             v);
    }
    
    public Object3D deepCopy()
    {   Cylinder copy = new Cylinder();
        copyParameters(copy);
        copy.top = top;
        copy.bottom = bottom;
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // class Cylinder


// a (single) cone
class Cone extends ParamSurface
{   boolean bottom;
    // "empty" cone, use for copying
    public Cone()
    {
    }    
    public Cone(double radius, double height, CssColor oc, boolean b)
    {   bottom = b;
        paramNum = 2;
        params = new double[2];
        params[0] = radius;
        params[1] = height;
        // u-range, v-range
        uMin = - Math.PI;
        uMax = Math.PI;
        vMin = - params[1] / 2;
        vMax = params[1] / 2;
        // step number
        uSteps = 19;
        vSteps = 1;
        // finds steps
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        // create facets
        sColor = oc;
        create();
        
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
            {   int index = facets[fCnt].indices[vCnt];
                int cycleNum = index / (uSteps + 1);
                int cyclePos = index % (uSteps + 1);
                // bottom
                if (cycleNum == 0)
                {   // first uSteps points
                    if (cyclePos < uSteps)
                    {   facets[fCnt].vertexLabels[vCnt] =
                            getLabel(cyclePos + 1);                
                    }
                    else // cyclePos == uSteps
                        facets[fCnt].vertexLabels[vCnt] =
                            getLabel(1);                
                }
                // top
                else if (cycleNum == 1)
                {
                    facets[fCnt].vertexLabels[vCnt] =
                        getLabel(uSteps * cycleNum + 1);                
                }        
            }    
        
        if (bottom)
        {  
            // by construction the vertices of the bottom are in positions
            // 0, uSteps with first and last equal
            int[] inds = new int[uSteps];
            for (int i = 0; i < uSteps; i++)
                inds[i] = uSteps - i - 1;
            Facet3D f = new Facet3D(vertices, inds, sColor);
            for (int vCnt = 0; vCnt < f.numPoints; vCnt++)
            {   if (vCnt > 0)
                    f.vertexLabels[vCnt] = getLabel(uSteps - vCnt);
                else // vCnt == 0
                    f.vertexLabels[vCnt] = getLabel(uSteps);
            }    
            
            addFacet(f);
        }    
        
        numVertexLabels = uSteps + 1;
        // find diameter
        // set center at (0,0, -h/6) no need to calculate or center
        initObject3D(true, new Vector3D(0, 0, - params[0] / 6), false);        
    } // constructor
    
    
    // A is label number 1, Z is number 26
    // AA is number 27
    public String getLabel(int i)
    {   String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int cycles = (i - 1) / 26;
        int character = (i - 1) % 26;
        String result = "";
        // assume maximum 26*26=676 labels
        if (cycles >= 1)
            result += alphabet.charAt(cycles - 1);
        result += alphabet.charAt(character);
    
        return result;
    }
    
    
    // parametrization
    // [0,2Pi)x[-height/2,height/2]->space
    // ( radius*cos(u), radius*sin(u), (-radius/height)*v+radius/2 )
    public Vector3D getValueAt(double u, double v)
    {   // factor(v) is choosen such that 
        // factor(-height/2)=radius and factor(height/2)=0
        double factorv = (-params[0]/params[1]) * v + params[0] / 2;
        return new Vector3D( factorv * params[0] * Math.cos(u),
                             factorv * params[0] * Math.sin(u),
                             v);
    }
    
    public Object3D deepCopy()
    {   Cone copy = new Cone();
        copyParameters(copy);
        copy.bottom = bottom;
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // class Cone


/*
// a double cone
class DoubleCone extends ParamSurface
{   boolean top, bottom;
    // "empty" cone, use for copying
    public DoubleCone()
    {
    }    
    public DoubleCone(double radius, double height, Color oc, 
                      boolean t, boolean b)
    {   top = t;
        bottom = b;
        paramNum = 2;
        params = new double[2];
        params[0] = radius;
        params[1] = height;
        // u-range, v-range
        uMin = 0;
        uMax = 2 * Math.PI;
        vMin = - params[1] / 2;
        vMax = params[1] / 2;
        // step number
        uSteps = 20;
        vSteps = 10;
        // finds steps
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        // create facets
        sColor = oc;
        create();
        if (top)
        {
            // by construction the vertices of the top are in positions
            // 0+vSteps*(uSteps+1),...,uSteps+vSteps*(uSteps+1)with first
            // and last equal
            int[] inds = new int[uSteps];
            for (int i = 0; i < uSteps; i++)
                inds[i] = i + vSteps*(uSteps+1);
            Facet3D f = new Facet3D(vertices, inds, sColor);
            addFacet(f);
        }
        if (bottom)
        {  
            // by construction the vertices of the bottom are in positions
            // 0, uSteps with first and last equal
            int[] inds = new int[uSteps];
            for (int i = 0; i < uSteps; i++)
                inds[i] = uSteps - i - 1;
            Facet3D f = new Facet3D(vertices, inds, sColor);
            addFacet(f);
        }    
        // find diameter
        // center is at (0,0,0) no need to calculate or center
        initObject3D(true, new Vector3D(), false);        
    }
    // parametrization
    // [0,2Pi)x[-height/2,height/2]->space
    // ( radius*cos(u), radius*sin(u), (-radius/height)*v+radius/2 )
    public Vector3D getValueAt(double u, double v)
    {   // factor(v) is choosen such that 
        // factor(-height/2)=radius and factor(0)=0
        double factorv = (-2*params[0]/params[1])*v;
        return new Vector3D( factorv * params[0] * Math.cos(u),
                             factorv * params[0] * Math.sin(u),
                             v);
    }
    
    public Object3D deepCopy()
    {   DoubleCone copy = new DoubleCone();
        copyParameters(copy);
        copy.top = top;
        copy.bottom = bottom;
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // class DoubleCone
*/
/*
// a torus
class Torus extends ParamSurface
{   public Torus()
    {
    }
    public Torus(double bigC, double smallC1, double smallC2, Color oc)
    {   
        paramNum = 3;
        params = new double[3];
        params[0] = bigC;
        params[1] = smallC1;
        params[2] = smallC2;
        // u-range, v-range
        uMin = 0;
        uMax = 2 * Math.PI;
        vMin = 0;
        vMax = 2 * Math.PI;
        // step number
        uSteps = 20;
        vSteps = 10;
        // finds steps
        uStep = (uMax - uMin) / uSteps;
        vStep = (vMax - vMin) / vSteps;
        // find facet number
//        numFacets = uSteps * vSteps;
        // create facets
        sColor = oc;
        create();
        double d = 2 * (params[0] + params[1]);
        // center is at (0,0,0) no need to calculate or center        
        initObject3D(true, new Vector3D(), false);
        
    }
    // parametrization
    // [0,2Pi)x[0,2Pi)->space
    // ( (a+b*cos(v))*cos(u), (a+b*cos(v))*sin(u), c*sin(v) )
    public Vector3D getValueAt(double u, double v)
    {   return new Vector3D( (params[0] + params[1] * Math.cos(v)) * Math.cos(u),
                             (params[0] + params[1] * Math.cos(v)) * Math.sin(u),
                             params[2] * Math.sin(v));
    }
    
    public Object3D deepCopy()
    {   Torus copy = new Torus();
        copyParameters(copy);
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // class Torus
*/