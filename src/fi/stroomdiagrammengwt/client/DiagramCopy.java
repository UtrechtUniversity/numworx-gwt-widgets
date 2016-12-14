package fi.stroomdiagrammengwt.client;

import java.util.Vector;

/**
 * a class containing the attributes of a 
 * flow diagram  
 * */
public class DiagramCopy 
{
	/**
	 * the maximum of the codes of the vertices in the diagram 
	 */
	int maxCode;
	/**
	 * the horizontal distance between the layers
	 */
	int layerDistance;
	/**
	 * the number of layers
	 */
    int numLayers;
    /**
     * width of the diagram
     */
    int breedte;
    /**
     * height of the diagram
     */
    int hoogte;
    /**
     * flow in decimal numbers, percentages or fractions
     * see class DrawingPanel and class Vertex
     */
    int flowMode;
    /**
     * thickness of edges absolute or relative
     * see class DrawingPanel and class Edge
     */
    int thickMode;
    /** 
     * height of the labels, this is a flagg:
     * 0: no labels, larger than 0: labels 
     */
    int labelHeight;
    /**
     * copies of the vertices in the diagram
     */
    Vector vertexCopies = new Vector();
    /**
     * copies of the edges in the diagram
     */
    Vector edgeCopies = new Vector();
    
    public DiagramCopy()
    {   
    }

}
