package fi.doorziengwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * a box, with different length, width and height
 * @author huub
 */
public class Box extends Object3D
{   double length, width, height;
    // indices for oriented box
    // normal to the outside
    // vertices are arranged as bottom (counterclock) top (counterclock)
    // bottom
    int[] iFacet0 = {0, 3, 2, 1}; 
    // top
    int[] iFacet1 = {4, 5, 6, 7};
    // front face
    int[] iFacet2 = {0, 1, 5, 4};
    // right face
    int[] iFacet3 = {1, 2, 6, 5};
    // back face
    int[] iFacet4 = {2, 3, 7, 6};
    // left face
    int[] iFacet5 = {0, 4, 7, 3};
    
    String[] vLabels = {"A", "B", "C", "D",
                        "E", "F", "G", "H"};
    
    public Box()
    {}
    public Box(double l, double w, double h, CssColor oc)
    {   // default constructor Object3D called here
        length = l;
        width = w;
        height = h;
        numVertices = 8;
        numVertexLabels = 8;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(length, 0, 0);
        vertices[2] = new Vector3D(length, width, 0);
        vertices[3] = new Vector3D(0, width, 0);
        // top
        vertices[4] = new Vector3D(0, 0, height);
        vertices[5] = new Vector3D(length, 0, height);
        vertices[6] = new Vector3D(length, width, height);
        vertices[7] = new Vector3D(0, width, height);
        numFacets = 6;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Box copy = new Box();
        copy.length = length;
        copy.width = width;
        copy.height = height;
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // box


/**
 * very simple axes
 * @author huub
 */
class Axes extends Object3D
{
    int[] iFacet0 = {0, 1};
    int[] iFacet1 = {0, 2};
    int[] iFacet2 = {0, 3};
    public Axes()
    {}
    public Axes(CssColor xColor, CssColor yColor, CssColor zColor)
    {
        numVertices = 4;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(1, 0, 0);
        vertices[2] = new Vector3D(0, 1, 0);
        vertices[3] = new Vector3D(0, 0, 1);
        
        numFacets = 3;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, xColor);
        facets[1] =  new Facet3D(vertices, iFacet1, yColor);
        facets[2] =  new Facet3D(vertices, iFacet2, zColor);
        // by construction center is (0, 0, 0), 
        // do NOT center the object in worldspace
        initObject3D(true, new Vector3D(), false);

    }
    public Object3D deepCopy()
    {   Axes copy = new Axes();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
}

/**
 * a tetrahedron with edge length sqrt(3) (thus height sqrt(2)!)
 * @author huub
 */
class Tetrahedron extends Object3D
{   // bottom
    int[] iFacet0 = {0, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 3};
    int[] iFacet2 = {1, 2, 3};
    int[] iFacet3 = {2, 0, 3};     

    String[] vLabels = {"B", "C", "A", "D"};

    public Tetrahedron()
    {};
    public Tetrahedron(CssColor oc)
    {   numVertices = 4;
        numVertexLabels = 4;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(-5e-1d, Math.sqrt(3) / 2, 0);
        vertices[2] = new Vector3D(-5e-1d, - Math.sqrt(3) / 2, 0);
        // top
        vertices[3] = new Vector3D(0, 0, Math.sqrt(2));

        numFacets = 4;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];

        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Tetrahedron copy = new Tetrahedron();
        makeDeepObjectCopy(copy);
        return copy;        
    }   

}

/**
 * an octahedron
 * @author huub
 */
class Octahedron extends Object3D
{   // bottom facets
    int[] iFacet0 = {0, 2, 1}; 
    int[] iFacet1 = {0, 3, 2};
    int[] iFacet2 = {0, 4, 3};
    int[] iFacet3 = {0, 1, 4};    
    // top facets
    int[] iFacet4 = {1, 2, 5};     
    int[] iFacet5 = {2, 3, 5};     
    int[] iFacet6 = {3, 4, 5};     
    int[] iFacet7 = {4, 1, 5};     
    
    String[] vLabels = {"A", "B", "C", "D", "E", "F"};

    public Octahedron()
    {};
    public Octahedron(CssColor oc)
    {   
        numVertices = 6;
        numVertexLabels = 6;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(0, 0, -1);
        // middle
        vertices[1] = new Vector3D(1, 0, 0);
        vertices[2] = new Vector3D(0, 1, 0);
        vertices[3] = new Vector3D(-1, 0, 0);
        vertices[4] = new Vector3D(0, - 1, 0);
        // top
        vertices[5] = new Vector3D(0, 0, 1);

        numFacets = 8;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];

        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Octahedron copy = new Octahedron();
        makeDeepObjectCopy(copy);
        return copy;        
    }   

}

/**
 * a three sided piramid
 * height = edge length bottom, i.e. sqrt(3)
 * @author huub
 */
class Piramid3 extends Object3D
{   // bottom
    int[] iFacet0 = {0, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 3};
    int[] iFacet2 = {1, 2, 3};
    int[] iFacet3 = {2, 0, 3};     

//    String[] vLabels = {"A", "B", "C", "D"};
    String[] vLabels = {"B", "C", "A", "D"};
    public Piramid3()
    {};
    public Piramid3(CssColor oc)
    {   numVertices = 4;
        numVertexLabels = 4;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(-5e-1d, Math.sqrt(3) / 2, 0);
        vertices[2] = new Vector3D(-5e-1d, - Math.sqrt(3) / 2, 0);
        // top
        vertices[3] = new Vector3D(0, 0, Math.sqrt(3));

        numFacets = 4;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];

        initObject3D(true, new Vector3D(0, 0, Math.sqrt(3) / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid3 copy = new Piramid3();
        makeDeepObjectCopy(copy);
        return copy;        
    }   

}

/**
 * a three sided prism
 * height = 3 * edge length bottom, i.e. 3 * sqrt(3)
 * @author huub
 */
class Prism3 extends Object3D
{   // bottom
    int[] iFacet0 = {0, 2, 1}; 
    // top
    int[] iFacet1 = {3, 4, 5};     
    // sides
    int[] iFacet2 = {0, 1, 4, 3};
    int[] iFacet3 = {1, 2, 5, 4};
    int[] iFacet4 = {2, 0, 3, 5};     

    String[] vLabels = {"B", "C", "A", "E", "F", "D"};
    public Prism3()
    {};
    public Prism3(CssColor oc)
    {   numVertices = 6;
        numVertexLabels = 6;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(-5e-1d, Math.sqrt(3) / 2, 0);
        vertices[2] = new Vector3D(-5e-1d, - Math.sqrt(3) / 2, 0);
        // top
        vertices[3] = new Vector3D(1, 0, 3 * Math.sqrt(3));
        vertices[4] = new Vector3D(-5e-1d, Math.sqrt(3) / 2, 3 * Math.sqrt(3));
        vertices[5] = new Vector3D(-5e-1d, - Math.sqrt(3) / 2, 3 * Math.sqrt(3));


        numFacets = 5;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);        

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];

        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Prism3 copy = new Prism3();
        makeDeepObjectCopy(copy);
        return copy;        
    }   

}

/**
 * a four sided pyramid with edge length 1 and height h
 * @author huub
 *
 */
class Piramid extends Object3D
{   double height;
    // bottom
    int[] iFacet0 = {0, 3, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 4};
    int[] iFacet2 = {1, 2, 4};
    int[] iFacet3 = {2, 3, 4};     
    int[] iFacet4 = {3, 0, 4};     
    
    String[] vLabels = {"A", "B", "C", "D", "E"};    
    
    public Piramid()
    {};
    public Piramid(double h, CssColor oc)
    {   height = h;
        
        numVertices = 5;
        numVertexLabels = 5;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(1, 0, 0);
        vertices[2] = new Vector3D(1, 1, 0);
        vertices[3] = new Vector3D(0, 1, 0);
        // top
        vertices[4] = new Vector3D(5e-1d, 5e-1d, height);

        numFacets = 5;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, new Vector3D(5e-1d, 5e-1d, height / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid copy = new Piramid();
        copy.height = height;
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}

/**
 * a four sided prism with edge length 1 and height 3
 * @author huub
 */
class Prism4 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 3, 2, 1}; 
    // top
    int[] iFacet1 = {4, 5, 6, 7}; 
    // sides
    int[] iFacet2 = {0, 1, 5, 4};
    int[] iFacet3 = {1, 2, 6, 5};
    int[] iFacet4 = {2, 3, 7, 6};     
    int[] iFacet5 = {3, 0, 4, 7};     
    
    String[] vLabels = {"A", "B", "C", "D", "E", "F", "G", "H"};    
    
    public Prism4()
    {};
    public Prism4(CssColor oc)
    {   
        
        numVertices = 8;
        numVertexLabels = 8;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(1, 0, 0);
        vertices[2] = new Vector3D(1, 1, 0);
        vertices[3] = new Vector3D(0, 1, 0);
        // top
        vertices[4] = new Vector3D(0, 0, 3);
        vertices[5] = new Vector3D(1, 0, 3);
        vertices[6] = new Vector3D(1, 1, 3);
        vertices[7] = new Vector3D(0, 1, 3);


        numFacets = 6;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Prism4 copy = new Prism4();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}


/**
 * a five sided pyramid with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/5 and height h = 1.5 * edge length
 * @author huub
 *
 */
class Piramid5 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 4, 3, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 5};
    int[] iFacet2 = {1, 2, 5};
    int[] iFacet3 = {2, 3, 5};     
    int[] iFacet4 = {3, 4, 5};     
    int[] iFacet5 = {4, 0, 5};     
    
    String[] vLabels = {"C", "D", "E", "A", "B", "F"};        
    
    public Piramid5()
    {};
    public Piramid5(CssColor oc)
    {   
        
        numVertices = 6;
        numVertexLabels = 6;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 5;
        double height = 15e-1d * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        // top
        vertices[5] = new Vector3D(0, 0, height);

        numFacets = 6;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, new Vector3D(0, 0, height / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid5 copy = new Piramid5();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}


/**
 * a five sided prism with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/5 and height h = 3 * edge length
 * @author huub
 */
class Prism5 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 4, 3, 2, 1}; 
    // top
    int[] iFacet1 = {5, 6, 7, 8, 9}; 
    
    // sides
    int[] iFacet2 = {0, 1, 6, 5};
    int[] iFacet3 = {1, 2, 7, 6};
    int[] iFacet4 = {2, 3, 8, 7};     
    int[] iFacet5 = {3, 4, 9, 8};     
    int[] iFacet6 = {4, 0, 5, 9};     
    
    String[] vLabels = {"C", "D", "E", "A", "B", 
                        "H", "I", "J", "F", "G"};    
    
    public Prism5()
    {};
    public Prism5(CssColor oc)
    {   
        
        numVertices = 10;
        numVertexLabels = 10;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 5;
        double height = 3 * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        // top
        vertices[5] = new Vector3D(1, 0, height);
        vertices[6] = new Vector3D(Math.cos(alpha), Math.sin(alpha), height);
        vertices[7] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), height);
        vertices[8] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), height);
        vertices[9] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), height);        

        numFacets = 7;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Prism5 copy = new Prism5();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}

/**
 * a six sided pyramid with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/6 and height h = 2 * edge length
 * @author huub
 */
class Piramid6 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 5, 4, 3, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 6};
    int[] iFacet2 = {1, 2, 6};
    int[] iFacet3 = {2, 3, 6};     
    int[] iFacet4 = {3, 4, 6};     
    int[] iFacet5 = {4, 5, 6};     
    int[] iFacet6 = {5, 0, 6};         
    
    String[] vLabels = {"D", "E", "F", "A", "B", "C", "G"};        
    public Piramid6()
    {};
    public Piramid6(CssColor oc)
    {   
        
        numVertices = 7;
        numVertexLabels = 7;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 6;
        double height = 2 * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        vertices[5] = new Vector3D(Math.cos(5 * alpha), Math.sin(5 * alpha), 0);                
        // top
        vertices[6] = new Vector3D(0, 0, height);

        numFacets = 7;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, new Vector3D(0, 0, height / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid6 copy = new Piramid6();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}

/**
 * a six sided prism with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/6 and height h = 3 * edge length
 * @author huub
 */
class Prism6 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 5, 4, 3, 2, 1}; 
    // top
    int[] iFacet1 = {6, 7, 8, 9, 10, 11}; 
    // sides
    int[] iFacet2 = {0, 1, 7, 6};
    int[] iFacet3 = {1, 2, 8, 7};
    int[] iFacet4 = {2, 3, 9, 8};     
    int[] iFacet5 = {3, 4, 10, 9};     
    int[] iFacet6 = {4, 5, 11, 10};     
    int[] iFacet7 = {5, 0, 6, 11};         
    
    String[] vLabels = {"D", "E", "F", "A", "B", "C", 
                        "J", "K", "L", "G", "H", "I"};    
    
    public Prism6()
    {};
    public Prism6(CssColor oc)
    {   
        
        numVertices = 12;
        numVertexLabels = 12;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 6;
        double height = 3 * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        vertices[5] = new Vector3D(Math.cos(5 * alpha), Math.sin(5 * alpha), 0);                
        // top
        vertices[6] = new Vector3D(1, 0, height);
        vertices[7] = new Vector3D(Math.cos(alpha), Math.sin(alpha), height);
        vertices[8] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), height);
        vertices[9] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), height);
        vertices[10] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), height);        
        vertices[11] = new Vector3D(Math.cos(5 * alpha), Math.sin(5 * alpha), height);                


        numFacets = 8;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);        
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Prism6 copy = new Prism6();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}


/**
 * a seven sided pyramid with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/7 and height h = 2.5 * edge length
 * @author huub
 */
class Piramid7 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 6, 5, 4, 3, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 7};
    int[] iFacet2 = {1, 2, 7};
    int[] iFacet3 = {2, 3, 7};     
    int[] iFacet4 = {3, 4, 7};     
    int[] iFacet5 = {4, 5, 7};     
    int[] iFacet6 = {5, 6, 7};         
    int[] iFacet7 = {6, 0, 7};         
    
    String[] vLabels = {"D", "E", "F", "G", "A", "B", "C", "H"};        
    
    public Piramid7()
    {};
    public Piramid7(CssColor oc)
    {   
        
        numVertices = 8;
        numVertexLabels = 8;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 7;
        double height = 25e-1d * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        vertices[5] = new Vector3D(Math.cos(5 * alpha), Math.sin(5 * alpha), 0);                
        vertices[6] = new Vector3D(Math.cos(6 * alpha), Math.sin(6 * alpha), 0);                        
        // top
        vertices[7] = new Vector3D(0, 0, height);

        numFacets = 8;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);        
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, new Vector3D(0, 0, height / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid7 copy = new Piramid7();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}


/**
 * an eight sided pyramid with edge length sqrt(2 - 2 cos(alpha)) where
 * alpha = 2*PI/8 and height h = 3 * edge length
 * @author huub
 */
class Piramid8 extends Object3D
{   
    // bottom
    int[] iFacet0 = {0, 7, 6, 5, 4, 3, 2, 1}; 
    // sides
    int[] iFacet1 = {0, 1, 8};
    int[] iFacet2 = {1, 2, 8};
    int[] iFacet3 = {2, 3, 8};     
    int[] iFacet4 = {3, 4, 8};     
    int[] iFacet5 = {4, 5, 8};     
    int[] iFacet6 = {5, 6, 8};         
    int[] iFacet7 = {6, 7, 8};         
    int[] iFacet8 = {7, 0, 8};             
    
    String[] vLabels = {"E", "F", "G", "H", "A", "B", "C", "D", "I"};        
    public Piramid8()
    {};
    public Piramid8(CssColor oc)
    {   
        
        numVertices = 9;
        numVertexLabels = 9;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        double alpha = 2 * Math.PI / 8;
        double height = 3 * Math.sqrt(2 - 2 * Math.cos(alpha));
        // bottom
        vertices[0] = new Vector3D(1, 0, 0);
        vertices[1] = new Vector3D(Math.cos(alpha), Math.sin(alpha), 0);
        vertices[2] = new Vector3D(Math.cos(2 * alpha), Math.sin(2 * alpha), 0);
        vertices[3] = new Vector3D(Math.cos(3 * alpha), Math.sin(3 * alpha), 0);
        vertices[4] = new Vector3D(Math.cos(4 * alpha), Math.sin(4 * alpha), 0);        
        vertices[5] = new Vector3D(Math.cos(5 * alpha), Math.sin(5 * alpha), 0);                
        vertices[6] = new Vector3D(Math.cos(6 * alpha), Math.sin(6 * alpha), 0);                        
        vertices[7] = new Vector3D(Math.cos(7 * alpha), Math.sin(7 * alpha), 0);                                
        // top
        vertices[8] = new Vector3D(0, 0, height);

        numFacets = 9;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);        
        facets[8] =  new Facet3D(vertices, iFacet8, oc);        
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, new Vector3D(0, 0, height / 2), false);
    }
    
    public Object3D deepCopy()
    {   Piramid8 copy = new Piramid8();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}

/**
 * an icosahedron with edge length 2
 * @author huub
 */
class Icosahedron extends Object3D
{   // normal to the outside
    // vertices are arranged as bottom (counterclock) top (counterclock)
    // top to bottom
    int[] iFacet0 = {0, 1, 2}; 
    int[] iFacet1 = {0, 3, 1};
    
    int[] iFacet2 = {0, 2, 4};
    int[] iFacet3 = {0, 4, 5};     
    int[] iFacet4 = {0, 5, 3};
    int[] iFacet5 = {1, 3, 6};
    int[] iFacet6 = {1, 6, 7};    
    int[] iFacet7 = {1, 7, 2};
    
    int[] iFacet8 = {2, 8, 4};
    int[] iFacet9 = {3, 5, 9};
    int[] iFacet10 = {3, 9, 6};
    int[] iFacet11 = {2, 7, 8};
    
    int[] iFacet12 = {4, 8, 10};
    int[] iFacet13 = {4, 10, 5};
    int[] iFacet14 = {5, 10, 9};
    int[] iFacet15 = {6, 9, 11};
    int[] iFacet16 = {6, 11, 7}; 
    int[] iFacet17 = {7, 11, 8};
    
    int[] iFacet18 = {8, 11, 10};
    int[] iFacet19 = {9, 10, 11};
    
    String[] vLabels = {"J", "K", "L", "G", "I", "D",
                        "E", "H", "F", "A", "C", "B"};        
                        
    public Icosahedron()
    {}
    public Icosahedron(CssColor oc)
    {   numVertices = 12;
        numVertexLabels = 12;        
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // g-factor
        double g = (1 + Math.sqrt(5)) / 2;
        // from top to bottom
        vertices[0] = new Vector3D(-1, 0, g);
        vertices[1] = new Vector3D(1, 0, g);
        vertices[2] = new Vector3D(0, g, 1);
        vertices[3] = new Vector3D(0, -g, 1);
        // middle
        vertices[4] = new Vector3D(-g, 1, 0);
        vertices[5] = new Vector3D(-g, -1, 0);
        vertices[6] = new Vector3D(g, -1, 0);
        vertices[7] = new Vector3D(g, 1, 0);
        
        vertices[8] = new Vector3D(0, g, -1);
        vertices[9] = new Vector3D(0, -g, -1);
        vertices[10] = new Vector3D(-1, 0, -g);
        vertices[11] = new Vector3D(1, 0, -g);
        
        
        numFacets = 20;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);
        facets[8] =  new Facet3D(vertices, iFacet8, oc);
        facets[9] =  new Facet3D(vertices, iFacet9, oc);
        facets[10] =  new Facet3D(vertices, iFacet10, oc);
        facets[11] =  new Facet3D(vertices, iFacet11, oc);
        facets[12] =  new Facet3D(vertices, iFacet12, oc);
        facets[13] =  new Facet3D(vertices, iFacet13, oc);
        facets[14] =  new Facet3D(vertices, iFacet14, oc);
        facets[15] =  new Facet3D(vertices, iFacet15, oc);
        facets[16] =  new Facet3D(vertices, iFacet16, oc);
        facets[17] =  new Facet3D(vertices, iFacet17, oc);
        facets[18] =  new Facet3D(vertices, iFacet18, oc);
        facets[19] =  new Facet3D(vertices, iFacet19, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Icosahedron copy = new Icosahedron();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // Icosahedron

/**
 * a Dodecahedron
 * @author huub
 */
class Dodecahedron extends Object3D
{   // normal to the outside
    // vertices are arranged as bottom (counterclock) top (counterclock)
    // top to bottom
    int[] iFacet0 = {0, 1, 5, 6, 2};     
    int[] iFacet1 = {0, 2, 8, 9, 3};
    int[] iFacet2 = {0, 3, 7, 4, 1};
    int[] iFacet3 = {1, 4, 10, 11, 5};     

    int[] iFacet4 = {5, 11, 17, 12, 6};    
    int[] iFacet5 = {2, 6, 12, 14, 8};
    int[] iFacet6 = {3, 9, 15, 13, 7};    
    int[] iFacet7 = {4, 7, 13, 16, 10};
    
    int[] iFacet8 = {18, 14, 12, 17, 19};
    int[] iFacet9 = {18, 15, 9, 8, 14};
    int[] iFacet10 = {18, 19, 16, 13, 15};
    int[] iFacet11 = {10, 16, 19, 17, 11};

    String[] vLabels = {"P", "T", "O", "Q", "S", 
                        "N", "J", "R", "F", "K", 
                        "M", "I", "E", "L", "A",
                        "G", "H", "D", "B", "C"};        
    
    public Dodecahedron()
    {}
    public Dodecahedron(CssColor oc)
    {   numVertices = 20;
        numVertexLabels = 20;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // g-factor
        double g = (1 + Math.sqrt(5)) / 2;
        double g2 = g * g;
        // from top to bottom
        vertices[0] = new Vector3D(0, -1, g2);
        vertices[1] = new Vector3D(0, 1, g2);
        
        vertices[2] = new Vector3D(-g, -g, g);
        vertices[3] = new Vector3D(g, -g, g);
        vertices[4] = new Vector3D(g, g, g);
        vertices[5] = new Vector3D(-g, g, g);

        vertices[6] = new Vector3D(-g2, 0, 1);
        vertices[7] = new Vector3D(g2, 0, 1);
        
        vertices[8] = new Vector3D(-1, -g2, 0);
        vertices[9] = new Vector3D(1, -g2, 0);
        vertices[10] = new Vector3D(1, g2, 0);
        vertices[11] = new Vector3D(-1, g2, 0);
        
        vertices[12] = new Vector3D(-g2, 0, -1);
        vertices[13] = new Vector3D(g2, 0, -1);
        
        vertices[14] = new Vector3D(-g, -g, -g);
        vertices[15] = new Vector3D(g, -g, -g);
        vertices[16] = new Vector3D(g, g, -g);
        vertices[17] = new Vector3D(-g, g, -g);
        
        vertices[18] = new Vector3D(0, -1, -g2);
        vertices[19] = new Vector3D(0, 1, -g2);

        
        numFacets = 12;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);
        facets[8] =  new Facet3D(vertices, iFacet8, oc);
        facets[9] =  new Facet3D(vertices, iFacet9, oc);
        facets[10] =  new Facet3D(vertices, iFacet10, oc);
        facets[11] =  new Facet3D(vertices, iFacet11, oc);

        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   Dodecahedron copy = new Dodecahedron();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
    
} // Dodecahedron

/**
 * a house with piramidal roof, height roof = height house
 * @author huub
 */
class PirHouse extends Object3D
{   
    // bottom house
    int[] iFacet0 = {0, 3, 2, 1}; 
    // sides house
    int[] iFacet1 = {0, 1, 5, 4};
    int[] iFacet2 = {1, 2, 6, 5};
    int[] iFacet3 = {2, 3, 7, 6};     
    int[] iFacet4 = {3, 0, 4, 7};     
    // sides roof
    int[] iFacet5 = {4, 5, 8};     
    int[] iFacet6 = {5, 6, 8};     
    int[] iFacet7 = {6, 7, 8};         
    int[] iFacet8 = {7, 4, 8};         
    
    String[] vLabels = {"A", "B", "C", "D", 
                        "E", "F", "G", "H", "I"};    
    
    public PirHouse()
    {};
    public PirHouse(CssColor oc)
    {   
        
        numVertices = 9;
        numVertexLabels = 9;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom house
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(1, 0, 0);
        vertices[2] = new Vector3D(1, 1, 0);
        vertices[3] = new Vector3D(0, 1, 0);
        // top house
        vertices[4] = new Vector3D(0, 0, 1);
        vertices[5] = new Vector3D(1, 0, 1);
        vertices[6] = new Vector3D(1, 1, 1);
        vertices[7] = new Vector3D(0, 1, 1);
        // top roof
        vertices[8] = new Vector3D(5e-1d, 5e-1d, 2);


        numFacets = 9;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);
        facets[8] =  new Facet3D(vertices, iFacet8, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   PirHouse copy = new PirHouse();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}

/**
 * a house with a hip roof (nl: schilddak), height roof = height house
 * @author huub
 */
class EdgeHouse extends Object3D
{   
    // bottom house
    int[] iFacet0 = {0, 3, 2, 1}; 
    // sides house
    int[] iFacet1 = {0, 1, 5, 4};
    int[] iFacet2 = {1, 2, 6, 5};
    int[] iFacet3 = {2, 3, 7, 6};     
    int[] iFacet4 = {3, 0, 4, 7};     
    // sides roof
    int[] iFacet5 = {4, 5, 9, 8};     
    int[] iFacet6 = {5, 6, 9};     
    int[] iFacet7 = {6, 7, 8, 9};         
    int[] iFacet8 = {7, 4, 8};         
    
    String[] vLabels = {"A", "B", "C", "D", 
                        "E", "F", "G", "H", 
                        "I", "J"};    
    
    public EdgeHouse()
    {};
    public EdgeHouse(CssColor oc)
    {   
        
        numVertices = 10;
        numVertexLabels = 10;
        vertices = new Vector3D[numVertices];
        // do NOT forget this
        trVertices = new Vector3D[numVertices];
        // bottom house
        vertices[0] = new Vector3D(0, 0, 0);
        vertices[1] = new Vector3D(12, 0, 0);
        vertices[2] = new Vector3D(12, 8, 0);
        vertices[3] = new Vector3D(0, 8, 0);
        // top house
        vertices[4] = new Vector3D(0, 0, 6);
        vertices[5] = new Vector3D(12, 0, 6);
        vertices[6] = new Vector3D(12, 8, 6);
        vertices[7] = new Vector3D(0, 8, 6);
        // top roof
        vertices[8] = new Vector3D(3, 4, 12);
        vertices[9] = new Vector3D(9, 4, 12);

        numFacets = 9;
        facets = new Facet3D[numFacets];
        // take care of correct orientation
        facets[0] =  new Facet3D(vertices, iFacet0, oc);
        facets[1] =  new Facet3D(vertices, iFacet1, oc);
        facets[2] =  new Facet3D(vertices, iFacet2, oc);
        facets[3] =  new Facet3D(vertices, iFacet3, oc);
        facets[4] =  new Facet3D(vertices, iFacet4, oc);
        facets[5] =  new Facet3D(vertices, iFacet5, oc);
        facets[6] =  new Facet3D(vertices, iFacet6, oc);
        facets[7] =  new Facet3D(vertices, iFacet7, oc);
        facets[8] =  new Facet3D(vertices, iFacet8, oc);
        
        for (int fCnt = 0; fCnt < numFacets; fCnt++)
            for (int vCnt = 0; vCnt < facets[fCnt].numPoints; vCnt++)
                facets[fCnt].vertexLabels[vCnt] =
                    vLabels[facets[fCnt].indices[vCnt]];
        
        initObject3D(true, false);
    }
    
    public Object3D deepCopy()
    {   EdgeHouse copy = new EdgeHouse();
        makeDeepObjectCopy(copy);
        return copy;        
    }   
}
