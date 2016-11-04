package fi.stroomdiagrammengwt.client;

import java.util.Vector;


//deze klasse maakt m.b.v. de DiagramManager een kopie
//van het huidige flowdiagram
//maak, om status te saven, van de kopie een aparte klasse 

public class DiagramCopier
{   DiagramManager owner;
 	int layerDistance;
 	int numLayers;
 	Dimension size;
 	int flowMode;
 	int thickMode;
 	int labelHeight;
 	boolean flowOn;
 	Vector vertexCopies = new Vector();
 	Vector edgeCopies = new Vector();
 	public DiagramCopier(DiagramManager o)
 	{   owner = o;
     	layerDistance = owner.owner.layerDistance;
     	numLayers = owner.owner.numLayers;
     	size = owner.owner.getSize(); // DrawingPanel 
     	flowMode = owner.owner.flowMode;        
     	thickMode = owner.owner.thickMode;        
     	labelHeight = owner.owner.labelHeight;
     	flowOn = owner.owner.flowOn;
     	// vector containing references to all vertices
     	Vector vertexRefs = owner.getVertexRefs();
     	// make copies in same order
     	for (int i = 0; i < vertexRefs.size(); i++)
     	{   Vertex v = (Vertex) vertexRefs.elementAt(i);
         	VertexCopy vc = new VertexCopy(v.code,
         						v.layerNum, v.getLocation().y,
         						v.flow, v.decimals, v.root,
         						//v.vLabel.getText());
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
     
/*        
     // NOTE: copies and originals are now in same order
     // now fix the interrelations in the graph
     for (int i = 0; i < vertexRefs.size(); i++)
     {   // vertex i
         Vertex v = (Vertex) vertexRefs.elementAt(i);
         // copy of vertex i 
         VertexCopy vc = (VertexCopy) vertexCopies.elementAt(i);
         for (int m = 0; m < v.inEdges.size(); m++)
         {   Edge ine = (Edge) v.inEdges.elementAt(m);
             int inIndex = owner.edges.indexOf(ine);
             vc.inEdgeCopies.addElement(edgeCopies.elementAt(inIndex));
         }
         for (int n = 0; n < v.outEdges.size(); n++)
         {   Edge oute = (Edge) v.outEdges.elementAt(n);
             int outIndex = owner.edges.indexOf(oute);            
             vc.outEdgeCopies.addElement(edgeCopies.elementAt(outIndex));                
         }
         
     }
*/        
     	for (int j = 0; j < edgeCopies.size(); j++)
     	{   Edge e = (Edge) owner.edges.elementAt(j);
         	EdgeCopy ec = (EdgeCopy) edgeCopies.elementAt(j);
         	int fromIndex = vertexRefs.indexOf(e.fromVertex);
         	int toIndex = vertexRefs.indexOf(e.toVertex);
         	ec.fromVertexCopy = (VertexCopy) vertexCopies.elementAt(fromIndex);
         	ec.toVertexCopy = (VertexCopy) vertexCopies.elementAt(toIndex);            
     	}    
     
 	}
 	
    public DiagramCopy getDiagramCopy()
    {	DiagramCopy dc = new DiagramCopy();
    	dc.layerDistance = layerDistance;
    	dc.numLayers = numLayers;
    	dc.size = size;
    	dc.flowMode = flowMode;        
        dc.thickMode = thickMode;        
        dc.labelHeight = labelHeight;
        dc.flowOn = flowOn;
		dc.vertexCopies = vertexCopies;
		dc.edgeCopies = edgeCopies;        
		return dc;
    }
}
