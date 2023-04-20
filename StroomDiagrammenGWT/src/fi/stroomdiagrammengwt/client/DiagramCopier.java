package fi.stroomdiagrammengwt.client;

import java.util.Vector;

/**
 * class containing as attributes the attributes of the flow diagram; <br> 
 * the copy is made when calling the constructor and a DiagramCopy
 * containing the attributes of the flow diagram can be requested     
 */
public class DiagramCopier
{   
	/**
	 * owner of the DiagramCopier 
	 */
	DiagramManager owner;
	/**
	 * the horizontal distance between the layers
	 */
 	int layerDistance;
 	int numLayers;
 	int breedte;
 	int hoogte;
 	int flowMode;
 	int thickMode;
 	int labelHeight;
 	/**
 	 * VertexCopies for the vertices in the flow diagram
 	 */
 	Vector<VertexCopy> vertexCopies = new Vector<VertexCopy>();
 	/**
 	 * EdgeCopies for the edges in the diagram
 	 */
 	Vector<EdgeCopy> edgeCopies = new Vector<EdgeCopy>();
 	/**
 	 * constructor
 	 * @param o the DiagramManager owning the DiagramCopier
 	 */
 	public DiagramCopier(DiagramManager o)
 	{   owner = o;
     	layerDistance = owner.owner.layerDistance;
     	numLayers = owner.owner.numLayers;
     	breedte = owner.owner.breedte;
     	hoogte = owner.owner.hoogte;
     	flowMode = DrawingPanel.flowMode;        
     	thickMode = owner.owner.thickMode;        
     	labelHeight = DrawingPanel.labelHeight;
     	// vector containing references to all vertices
     	Vector<Vertex> vertexRefs = owner.getVertexRefs();
     	// make copies in same order
     	for (int i = 0; i < vertexRefs.size(); i++)
     	{   Vertex v = (Vertex) vertexRefs.elementAt(i);
         	VertexCopy vc = new VertexCopy(v.code,
         						v.layerNum, v.getLocation().y,
         						v.flow, v.decimals, v.root,
         						v.labelText);
         	if (owner.owner.traceFrom != null)
         	{   if (owner.owner.traceFrom == v)
         			vc.traceFrom = true;
             	else    
             		vc.traceFrom = false;
         	}    
         	vertexCopies.addElement(vc);
     	}    
     	// make copies in same order
     	for (int j = 0; j < owner.edges.size(); j++)
     	{   Edge e = (Edge) owner.edges.elementAt(j);
         	EdgeCopy ec = new EdgeCopy(e.capacity, e.lastTimeChanged, e.mode);
         	edgeCopies.addElement(ec);
     	}    
     
     	for (int j = 0; j < edgeCopies.size(); j++)
     	{   Edge e = (Edge) owner.edges.elementAt(j);
         	EdgeCopy ec = (EdgeCopy) edgeCopies.elementAt(j);
         	int fromIndex = vertexRefs.indexOf(e.fromVertex);
         	int toIndex = vertexRefs.indexOf(e.toVertex);
         	ec.fromVertexCopy = (VertexCopy) vertexCopies.elementAt(fromIndex);
         	ec.toVertexCopy = (VertexCopy) vertexCopies.elementAt(toIndex);            
     	}    
 	}

 	/**
 	 * get a DiagramCopy containing the attributes of the flow diagram
 	 * @return the DiagramCopy 
 	 */
    public DiagramCopy getDiagramCopy()
    {	DiagramCopy dc = new DiagramCopy();
    	dc.layerDistance = layerDistance;
    	dc.numLayers = numLayers;
    	dc.breedte = breedte;
    	dc.hoogte = hoogte;
    	dc.flowMode = flowMode;        
        dc.thickMode = thickMode;        
        dc.labelHeight = labelHeight;
		dc.vertexCopies = vertexCopies;
		dc.edgeCopies = edgeCopies;        
		return dc;
    }
    
}
