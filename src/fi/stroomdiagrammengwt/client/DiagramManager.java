package fi.stroomdiagrammengwt.client;

//import java.awt.Graphics;
//import java.awt.Point;
//import java.awt.Rectangle;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class DiagramManager 
{

	DrawingPanel owner;
    Vector[] vertexLayers = new Vector[owner.maxLayers];
    Vector edges = new Vector();
    Vector vertices = new Vector();
    // constructor    
    public DiagramManager(DrawingPanel o)
    {   owner = o;
        for (int i = 0; i < vertexLayers.length; i++)
            vertexLayers[i] = new Vector();
    }    
    // this procedure produces a tree    
    public void insertVertex(Vertex v, Vertex o)
    {   boolean numLayersChanged = false;
        // adding a root 
        if (o == null)
        {   vertexLayers[v.layerNum].addElement(v);                        
        }    
        else // some other vertex
        // note: here it is assumed the vertices in both the o-layer
        // and the v-layer (the next one) are sorted by
        // vertical position
        // note also: outEdges of o are supposed to be sorted
        // by y-location of toVertex
// optimize for minimal number of crossings, how??
// als o "onderaan" gaat het al goed
// als o "bovenaan" vertex "bovenaan" inserten
// bekijk uitgaande vertices vanaf o (die als enige in een vertex komen??)
// en kijk of je voor de eerste of na de laatste kunt
// inserten zonder crossings te maken
// als dat niet lukt: ertussen??
// methode om aan edgeLayer crossings te vragen??
        {   // number of vertices in layer where v should be inserted            
            int vIndex = vertexLayers[v.layerNum].size();
            if (vIndex == 0)
            {   if (v.layerNum > owner.numLayers)
                {   owner.numLayers++;
                    owner.setLayerDistance();
                    numLayersChanged = true;
                }    
            }    
            // start at bottom of layer where v should be
            // inserted
            boolean allCrosses = true;
            for (int k = vertexLayers[v.layerNum].size() - 1; k >= 0; k--)
            {   Vertex tv = (Vertex) vertexLayers[v.layerNum].elementAt(k);
                int endy = tv.getLocation().y + 
                           (owner.vertexHeight + owner.labelHeight) / 2;
                if (allCrosses)
                {   if (createsCrossing(v.layerNum, o, endy))
                       vIndex--;
                    else
                    {    allCrosses = false;
                    }
                }    
            }
            // start at vIndex = layer size
            // if crosses at each step vIndex ends at 0
            // try top
            if ((vIndex == 0) && (vertexLayers[v.layerNum].size() > 0))
            {   Vertex topv = (Vertex) vertexLayers[v.layerNum].elementAt(0);
                int topendy = topv.getLocation().y - 
                              (owner.labelHeight + owner.vertexHeight) / 2;
                if (!createsCrossing(v.layerNum, o, topendy))
                {   allCrosses = false;
                    vIndex = 0;
                }
            }
            
            if (allCrosses) // take old algo
            {
                int oIndex = vertexLayers[o.layerNum].indexOf(o);
                vIndex = 0; // reset
                Vector targetVertices = new Vector();             
                for (int i = 0; i <= oIndex; i++)
                {   Vertex w = (Vertex) vertexLayers[o.layerNum].elementAt(i);
                    for (int j = 0; j < w.outEdges.size(); j++) 
                    {   Edge te = (Edge) w.outEdges.elementAt(j);
                        Vertex tw = te.toVertex;
                        if (tw.layerNum == v.layerNum)
                        {   int twIndex = vertexLayers[tw.layerNum].indexOf(tw);
                            if (!targetVertices.contains(tw)
                                && (twIndex == vIndex))
                            {   if (i < oIndex)
                                {   targetVertices.addElement(tw);
                                    vIndex++;
                                }
                                else
                                {    if (tw.inEdges.size() == 1)
                                         vIndex++;
                                }    
                            }    
                        }    
                    }    
                } // for   
            } // if (allCrosses)
            if (vertexLayers[v.layerNum].size() > 0)            
            {   if (vIndex == 0) // top
                {   Vertex ov = (Vertex) vertexLayers[v.layerNum].elementAt(vIndex);                                   
                    v.setLocation(v.getLocation().x, 
                        Math.max(owner.workSpace.y + owner.topSpace,
                                 ov.getLocation().y -
                                 (ov.getSize().height + owner.minSpace)));
                }
                else 
                {   Vertex ov = (Vertex) vertexLayers[v.layerNum].elementAt(vIndex - 1);                                   
                    v.setLocation(v.getLocation().x, 
                                  ov.getLocation().y +
                                  ov.getSize().height + owner.minSpace);
                }    
            }    
            vertexLayers[v.layerNum].insertElementAt(v, vIndex);                        
        } // else for non roots
//GWT        
        //owner.add(v);
        vertices.addElement(v);
        
        // roots are nicely spaced
        if (o == null)
            updateVertexLayer(v.layerNum);
        else if (vertexLayers[v.layerNum].size() > 1)
            updateVertexLayer(v.layerNum);
        else // first vertex in layer
            v.setLocation(owner.getLayerStart(v.layerNum), o.getLocation().y);
        updateEdges();
        if (numLayersChanged)
            resizeDiagram(false);    
    }   
    
    public void moveVertexTo(Vertex v, int layerNum)
    {   v.setLocation(owner.workSpace.x + owner.leftSpace +
                      layerNum * 
                      (owner.vertexWidth + owner.layerDistance),
                      v.getLocation().y);
        vertexLayers[v.layerNum].removeElement(v);
        v.layerNum = layerNum;
        // maintain sorting
        int index = 0;
        for (int i = 0; i < vertexLayers[v.layerNum].size(); i++)
        {   Vertex av = (Vertex) vertexLayers[v.layerNum].elementAt(i);
            if (av.getLocation().y < v.getLocation().y)
                index++;
        }    
        vertexLayers[v.layerNum].insertElementAt(v, index);
        if (v.layerNum > owner.numLayers)
            owner.numLayers = v.layerNum;
        for (int k = 0; k < v.inEdges.size(); k++)
        {   Edge ie = (Edge) v.inEdges.elementAt(k);
            ie.fromVertex.sortOutEdges();
        }    
        
            
// do not update vertex layers
// edges are updated in mouseReleased
    }    
    
    // vertical "spreading" / "cascading"
    // vertices are put in the same vertical order as in
    // vertexLayers[layerNum]
    public void updateVertexLayer(int layerNum)
    {   
    	if (owner.workSpace == null)
    		owner.defineSpaces();
    	
//System.out.println("wsp y = " + owner.workSpace.y);    	
    	
    	// horizontal position
        int horPos = owner.workSpace.x + owner.leftSpace +
                     layerNum * (owner.vertexWidth + owner.layerDistance);
        // vertical positioning
        int spacing = owner.workSpace.height - (owner.topSpace + owner.bottomSpace) -
                      vertexLayers[layerNum].size() * 
                      (owner.vertexHeight + owner.labelHeight);

//System.out.println("wsh = " + owner.workSpace.height);        
//System.out.println("sp = " + spacing); 
        
        if (spacing >= 0)              
            spacing /= vertexLayers[layerNum].size() + 1;             
        else
        {    spacing /= Math.max(1, vertexLayers[layerNum].size() - 1);
        
        }
        // set positions
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
            if (spacing >= 0)
                w.setLocation(horPos, owner.workSpace.y + owner.topSpace + spacing +
                                      i * (owner.vertexHeight + owner.labelHeight + spacing));  
            else 
                w.setLocation(horPos, owner.workSpace.y + owner.topSpace +
                                      i * (owner.vertexHeight + owner.labelHeight + spacing));
            
//System.out.println("vl = " + layerNum + " w #" + i + " y = " + w.getLocation().y);            
        }    
        owner.paint();
        
    }  // updateVertexLayer

/*
// dit is niet zo mooi als de oude methode
// i.h.b. als weinig vertices per laag
    // version avoiding overlap, if unavoidable
    // vertical "cascading"
    // vertices are put in the same vertical order as in
    // vertexLayers[layerNum]
    public void updateVertexLayer2(int layerNum)
    {   // horizontal position
        int horPos = owner.workSpace.x + owner.leftSpace +
                     layerNum * (owner.vertexWidth + owner.layerDistance);
        // vertical positioning
        int spacing = owner.workSpace.height - 
                      (owner.topSpace + owner.bottomSpace) -
                      vertexLayers[layerNum].size() * 
                      (owner.vertexHeight + owner.labelHeight) -
                      Math.max(0, vertexLayers[layerNum].size() - 1) *
                      owner.minSpace;
        if (spacing >= 0) // nice spacing possible             
            correctOverlap(layerNum);
        else // use old procedure
            updateVertexLayer(layerNum);
        // set only horizontal positions
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
            w.setLocation(horPos, w.getLocation().y);
        }    
        owner.repaint();
    }  // updateVertexLayer2
*/    
    public void correctOverlap(int layerNum)
    {   Vertex v = firstOverlap(layerNum);
// hier oneindige loop, waarom    
//        while (v != null)
//        {   
            int index = vertexLayers[layerNum].indexOf(v);
            Vertex above = (Vertex) vertexLayers[layerNum].elementAt(index - 1);
            // shift v downward
            v.setLocation(v.getLocation().x,
                          above.getLocation().y +
                          above.getSize().height + owner.minSpace);
            v = firstOverlap(layerNum);              
//        }    
            
    }
    
    public Vertex firstOverlap(int layerNum)
    {   if (vertexLayers[layerNum].size() == 1)
            return null;
        Vertex result = null;    
        for (int i = 0; i < vertexLayers[layerNum].size() - 1; i++)
        {   Vertex v1 = (Vertex) vertexLayers[layerNum].elementAt(i);
            Vertex v2 = (Vertex) vertexLayers[layerNum].elementAt(i + 1);
            if ((v1.getLocation().y + 
                 v1.getSize().height + owner.minSpace) >=
                 v2.getLocation().y)
                return v2; // exit for-loop and method
        }    
        return result;
    }
    public void fuseVertices(Vertex v1, Vertex v2)
    {   // v1 will disappear, v2 gets all the edges
        // incoming edges
        for (int i = 0; i < v1.inEdges.size(); i++)
        {   // edge to relocate/fuse
            Edge ie1 = (Edge) v1.inEdges.elementAt(i);
            // check if v2 has an edge starting at the same
            // vertex ie1.fromVertex
            Edge ie2 = v2.hasInEdgeFrom(ie1.fromVertex);
            if (ie2 != null)
            {   // remove ie1 from ie1.fromVertex
                ie1.fromVertex.outEdges.removeElement(ie1);
                // sum the capacities
                Rational temp = ie1.capacity.plus(ie2.capacity);
                ie2.setCapacity(temp, false);
                edges.removeElement(ie1);
//                edgeLayers[v1.layerNum - 1].removeElement(ie1);
//GWT                
                //owner.remove(ie1.capacityField);
            }    
            else
            {   ie1.toVertex = v2;
                v2.inEdges.addElement(ie1);
            }    
        }    
        // outgoing edges
        // remember old number of v2 outEdges
        int oldV2Out = v2.outEdges.size();
        for (int j = 0; j < v1.outEdges.size(); j++)
        {   // edge to relocate/fuse
            Edge oe1 = (Edge) v1.outEdges.elementAt(j);
            // check if v2 has an edge ending at the same
            // vertex oe1.toVertex
            Edge oe2 = v2.hasOutEdgeTo(oe1.toVertex);
            if (oe2 != null)
            {   // remove oe1 from oe1.toVertex
                oe1.toVertex.inEdges.removeElement(oe1);
                Rational temp = oe1.capacity.plus(oe2.capacity);
                oe2.setCapacity(temp, false);
                edges.removeElement(oe1);
//                edgeLayers[v1.layerNum].removeElement(oe1);
//GWT                
                //owner.remove(oe1.capacityField);                
            }    
            else 
            {   oe1.fromVertex = v2;
                v2.outEdges.addElement(oe1);
                // for writing???
                oe1.setCapacity(oe1.capacity, false);
            }    
        }    
        // if v1 had any outgoing edges and v2 had any outgoing
        // edges BEFORE the fusion
        // "new" capacities add up to 2 so divide
        if ((v1.outEdges.size() > 0) && (oldV2Out > 0))
            for (int k = 0; k < v2.outEdges.size(); k++)
            {   Edge e2 = (Edge) v2.outEdges.elementAt(k);
                Rational temp = new Rational(1, 2, 5e-1d);
                e2.setCapacity(e2.capacity.times(temp), false);
            }    
        // remove from vector    
        vertexLayers[v1.layerNum].removeElement(v1);
        // remove from screen
//GWT        
        //owner.remove(v1);
        vertices.removeElement(v1);
        // fusing roots
        if (owner.roots.contains(v1))
            owner.roots.removeElement(v1);
// do not update vertex layer
//        updateVertexLayer(v1.layerNum);
        // edgeLayers are updated in mouseReleased
        calculateDiagram();
        // bubblesort on v2 outEdges on y-locations
        v2.sortOutEdges();
        for (int k = 0; k < v2.inEdges.size(); k++)
        {   Edge ie = (Edge) v2.inEdges.elementAt(k);
            ie.fromVertex.sortOutEdges();
        }    
        if (owner.traceFrom != null)
        {   if (owner.traceFrom == v1)
                owner.traceFrom = v2;
            lowLightEdges();
            owner.traceBack(owner.traceFrom);
        }    
    }
    
    // find a vertex "close enough" to v (in same layer)
    public Vertex fuseWith(Vertex v)
    {   Vertex result = null;
    // zoek in alle vertices    
        for (int j = 0; j < vertexLayers.length; j++)
        {   for (int i = 0; i < vertexLayers[j].size(); i++)
            {   Vertex av = (Vertex) vertexLayers[j].elementAt(i);
                if ((v != av) && 
                    (Math.abs(v.getLocation().x - av.getLocation().x) <=
                     owner.vertexWidth / 5) &&  
                    (Math.abs(v.getLocation().y - av.getLocation().y) <= 
                     (owner.vertexHeight + owner.labelHeight) / 5))
                    result = av;     
            }
        }        
        return result;
    }    
/*  
    public boolean vertexLabelsChanged()
    {   boolean result = false;
        // zoek in alle vertices    
        for (int j = 0; j < vertexLayers.length; j++)
        {   for (int i = 0; i < vertexLayers[j].size(); i++)
            {   Vertex av = (Vertex) vertexLayers[j].elementAt(i);
                result = result || av.vLabel.textValueChanged;
            }
        }    
        return result;
    }
*/    
    // check if v intersects any other vertex
    public boolean intersectsVertex(Vertex v)
    {   boolean result = false;
        //Rectangle vRec = v.getBounds();
    	Rectangle vRec = new Rectangle(v.xPos, v.yPos, v.breedte, v.hoogte);
        for (int j = 0; j < vertexLayers.length; j++)
        {   for (int i = 0; i < vertexLayers[j].size(); i++)
            {   Vertex av = (Vertex) vertexLayers[j].elementAt(i);
                //Rectangle avRec = av.getBounds();
                Rectangle avRec = new Rectangle(av.xPos, av.yPos, av.breedte, av.hoogte);
                if (v != av)
                    result = result || vRec.intersects(avRec);
            }
        }        
            return result;
    }    
    
    // delete vertex v under circumstances
    public void deleteVertex(Vertex v)
    {   // a vertex is deletable if
        // it is a leaf i.e. no outEdges
// dit later        
        // if it has outedges a minimal subgraph has to be removed
        // so that connectivity is maintained
        boolean deletable = (v.outEdges.size() == 0);
        boolean numLayersChanged = false;
        if (deletable)
        {   int vIndex = vertexLayers[v.layerNum].size();
            // last vertex in layer
            // must be last layer (connectivity)
            if (vIndex == 1)
            {   owner.numLayers--;
                owner.setLayerDistance();
                numLayersChanged = true;
            }   
//            while (v.inEdges.size() > 0)
//            {   Edge ie = (Edge) v.inEdges.elementAt(0);
//                deleteEdge(ie, false);
//            }    
            // skipped at the moment
//            while (v.outEdges.size() > 0)
//            {   Edge oe = (Edge) v.outEdges.elementAt(0);
//                deleteEdge(oe, false);
//            }    
//GWT            
            //owner.remove(v);
            vertices.removeElement(v);
            vertexLayers[v.layerNum].removeElement(v);
            if (numLayersChanged)
                resizeDiagram(false);
            if (owner.traceFrom == v)
            {   lowLightEdges();
                owner.traceFrom = null;
            }
        }
    }    
    // add edge e
    public void addEdge(Edge e)
    {   // add to correct layer
        edges.addElement(e);
        e.fromVertex.sortOutEdges();
        updateEdges();        
//GWT        
        //owner.add(e.capacityField);
        owner.paint();
    }    
    // delete edge e under circumstances
    public void deleteEdge(Edge e)
    {   // deleting is allowed when:
        // e.toVertex has more than one inEdge
        // e.toVertex has one inEdge (must be e) and has no outEdges
        // in the last case remove the vertex also
        boolean toVertexIsLeaf = (e.toVertex.inEdges.size() == 1) &&
                                 (e.toVertex.outEdges.size() == 0);       
        // edge is deletable
        boolean deletable = (e.toVertex.inEdges.size() > 1) ||
                            toVertexIsLeaf;
        if (deletable)
        {   e.fromVertex.outEdges.removeElement(e);
            Edge oce = e.fromVertex.oldestOutChanged();
            if (oce != null)
                oce.setCapacity(e.capacity.plus(oce.capacity), true);
            // else e was the only outEdge of fromVertex    
            e.toVertex.inEdges.removeElement(e);
//GWT            
            //owner.remove(e.capacityField);
            edges.removeElement(e);
            if (toVertexIsLeaf)
                deleteVertex(e.toVertex);
            else    
                owner.paint();
            if (owner.traceFrom != null)
            {   lowLightEdges();
                owner.traceBack(owner.traceFrom);
            }    
            calculateDiagram();
        }
    }    
    
    // recalculates the edges positions from the vertex info
    public void updateEdges()
    {   for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            e.setEdge();
        } // for
        owner.paint();

    }    
    
    public Edge getClickedEdge(int x, int y)
    {   Edge result = null;
        for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            if (e.cp.contains(x, y))
                result = e;
        } // for
        return result;
    }    
    public void setVertexLabels(boolean b)
    {   if (b)
            owner.labelHeight = owner.LABELHEIGHT;
        else
            owner.labelHeight = 0;
        for (int i = 0; i < vertexLayers.length; i++)
                for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                v.setLabel(b);                
            }
        redrawDiagram();    
        owner.paint();       
        //owner.addToHistory();
    }    
    public boolean createsCrossing(int toLayerNum, Vertex start, int endy)
    {   boolean cross = false;
        Point end = new Point(owner.getLayerStart(toLayerNum), endy);
    
/*        
         for (int i = 0; i < vertexLayers[toLayerNum - 1].size(); i++)
         {   Vertex v = (Vertex) vertexLayers[toLayerNum - 1].elementAt(i);
             for (int j = 0; j < v.outEdges.size(); j++)
             {   Edge e = (Edge) v.outEdges.elementAt(j);
*/                
                
         for (int i = 0; i < edges.size(); i++)
         {   Edge e = (Edge) edges.elementAt(i);
             Vertex v = e.fromVertex;          
             if (v != start)
                 cross = cross || intersects(e, start, end);                   
         }                    
//         }
        return cross;
    }    
    // check if edge e intersects the segment starting at
    // topright of vertex st and ending in point end
    public boolean intersects(Edge e, Vertex st, Point end)
    {   Point start = new Point(st.getLocation().x + owner.vertexWidth, 
                                st.getLocation().y);
        Point temp;
        // left to right, not necessary??
        if (start.x > end.x)
        {   temp = start;
            start = end;
            end = temp;
        }    
        if ((start.x > e.edgeEnd.x) || (e.edgeStart.x > end.x))
            return false;
        double angle1 = ((double) (e.edgeEnd.y - e.edgeStart.y)) /
                        (e.edgeEnd.x - e.edgeStart.x);          
        double angle2 = ((double) (end.y - start.y)) /
                        (end.x - start.x);                                            
        double ic1 = e.edgeStart.y - angle1 * e.edgeStart.x;
        double ic2 = start.y - angle2 * start.x;
        if ((Math.abs(angle1 - angle2) < 1e-6d) &&
            (Math.abs(ic1 - ic2) < 1e-6d))
            return true;
        else if (Math.abs(angle1 - angle2) < 1e-6d)
            return false;
        else
        {   double xi = (ic2 - ic1) / (angle1 - angle2);
            int ii = (int) Math.round(xi);
            return (ii > start.x) && (ii < end.x); 
        }
    }

    public void drawVertices(Context2d g)
    {    for (int j = 0; j < vertices.size(); j++)
         {   Vertex v = (Vertex) vertices.elementAt(j);
             v.paintComponent(g);
         }    
    }    

    //public void drawEdges(Graphics g)
    public void drawEdges(Context2d g)
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.drawEdge(g);
         }    
    }    
    
    public void freezeEdges(boolean freeze)
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.setFrozen(freeze);
         }    
    }    
    
    public void lowLightEdges()
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.highlighted = false;
         }    
    }    

    public void moveBubbles()
    {   for (int j = 0; j < edges.size(); j++)
        {   Edge e = (Edge) edges.elementAt(j);
            int tStep = e.waveStep - 1;
            if (tStep < 0)
                tStep += Edge.numWaveColors;
            e.waveStep = tStep;
/*            
            e.bOffSet += e.bOffStep;
            if (e.bOffSet > e.bOffMax)
                e.bOffSet = 1;
*/                
        }    
        owner.paint();
    }    
    
    public void setFlowMode(int mode)
    {   boolean remember = (owner.flowMode != mode);
        owner.flowMode = mode; 
        calculateDiagram();
        for (int i = 0; i < vertexLayers.length; i++)
            for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                v.setFlow(v.flow);
            }
        if (remember)
            owner.addToHistory();
    }    
    
    // use this also for updating edge thickness
    // in abslute mode
    public void setEdgeThicknessMode(int tMode)
    {   boolean remember = (owner.thickMode != tMode);
        owner.thickMode = tMode; 
        for (int j = 0; j < edges.size(); j++)
        {   Edge e = (Edge) edges.elementAt(j);
            e.setThicknessMode(tMode);
        }   
        owner.paint();        
        if (remember)
            owner.addToHistory();
    }    
    
    public void clearDiagram(boolean newRoot)
    {   
        // kill flow
        owner.flowOn = false;
//GWT
/*        
        if (owner.flowThread != null)
            owner.flowThread.stop();
        if (owner.realDWO)
        	owner.sdip.bottomPanel.bubbleButton.setLabel(Stroomdiagrammen.rb.getString("flowOnText"));
        else	
        	owner.owner.bPanel.bubbleButton.setLabel(Stroomdiagrammen.rb.getString("flowOnText"));
*/        
        for (int j = edges.size() - 1; j >= 0; j--)
        {   Edge e = (Edge) edges.elementAt(j);
            edges.removeElement(e);
//GWT            
            //owner.remove(e.capacityField);
        }    
        for (int i = 0; i < vertexLayers.length; i++)
            for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                vertexLayers[i].removeElement(v);
//GWT                
                //owner.remove(v);
                vertices.removeElement(v);
            }
        owner.traceFrom = null;    
        owner.roots.removeAllElements();    
//        owner.root.outEdges.removeAllElements();    
//        owner.root.setFlow(owner.unDef);    
//        owner.roots.addElement(owner.root);
//        insertVertex(owner.root, null);
        owner.layerDistance = owner.maxLayerDistance;
        owner.numLayers = 0;
        if (newRoot)
        {   // clear history
            owner.history.removeAllElements();
            //if (owner.realDWO)
            //	owner.sdip.bottomPanel.previousButton.setEnabled(false);
            //else	
            	owner.owner.terugButton.setEnabled(false);
            if (owner.owner.toonOptiesMenu)
            	owner.addNewRoot(true);
            else
            {	for (int rCnt = 1; rCnt <= owner.owner.numRoots; rCnt++)
            	{	owner.addNewRoot(false);
            	}
            	owner.addToHistory();
            }
        }
        owner.paint();    
    }    
    
    // root treated in listener
    public void setDecimals(int decs)
    {   owner.vDecimals = decs;
        for (int i = 1; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 v.decimals = decs;
             }   
    }    
    
    public void calculateDiagram()
    {   for (int i = 1; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 v.calculateFlow();
             }   
        if (owner.thickMode == owner.absMode)
            setEdgeThicknessMode(owner.absMode);
        owner.paint();     
    }    
    
    public Vector getVertexRefs()
    {   Vector refs = new Vector();
        for (int i = 0; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 refs.addElement(v);
             }   
        return refs;     
    }    
    
    public void freezeVertices(boolean freeze)
    {
    	for (int i = 0; i < vertexLayers.length; i++)
            for (int j = 0; j < vertexLayers[i].size(); j++)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                v.setFrozen(freeze);
            }
    }
    
    public void redrawDiagram()
    {   for (int i = 0; i < vertexLayers.length; i++)
            updateVertexLayer(i);
        updateEdges();    
        owner.paint();    
    }    
    

    // vertices are put in the same vertical order as in
    // vertexLayers[layerNum]
    public void resizeVertexLayer(int layerNum, boolean vertical)
    {   // horizontal position
        // layerDistance MUST have been adapted
        int horPos = owner.workSpace.x + owner.leftSpace +
                     layerNum * (owner.vertexWidth + owner.layerDistance);
        // vertical positioning
        // number of spacings present
        int spacings = vertexLayers[layerNum].size() + 1; 
        // difference per vertex
        int ds = 0;
        if (vertical)
            ds = (int) Math.round(
                    ((double) owner.workSpace.height - owner.oldWorkSpace.height) /
                    spacings);   
        
//System.out.println("wsh = " + owner.workSpace.height);
//System.out.println("owsh = " + owner.oldWorkSpace.height);
//System.out.println("sp = " + spacings);
//System.out.println("ds = " + ds);

        // set positions
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
            if (ds >= 0)
                w.setLocation(horPos, w.getLocation().y + ds);  
            else 
                w.setLocation(horPos, w.getLocation().y + ds);              
//                w.setLocation(horPos, owner.workSpace.y + owner.topSpace +
//                                      i * (owner.vertexHeight + owner.labelHeight + spacing));  
        }    
        owner.paint();
    }  // resizeVertexLayer

    public void resizeDiagram(boolean vertical)
    {   for (int i = 0; i < vertexLayers.length; i++)
            resizeVertexLayer(i, vertical);
        updateEdges();    
        owner.paint();    
    }    
    
    public DiagramCopy copyDiagram()
    {   
    	
    	DiagramCopier diagramCopier = new DiagramCopier(this);
    	DiagramCopy dc = diagramCopier.getDiagramCopy();
        return dc;
    }    

    public void recreateDiagram(DiagramCopy dc)
    {   // remove all
        clearDiagram(false);
        // first arrange the global diagram attributes
        owner.layerDistance = dc.layerDistance;
        owner.numLayers = dc.numLayers;
//        owner.sizeSet = true;
//        owner.owner.setSize(dc.size); // FlowFrame
//        owner.sizeSet = false;

/*        
        owner.flowMode = dc.flowMode;
        
        OptionsMenu m = owner.owner.mainMenus.getMenu(Table.lookUp("represText"));
        if (owner.flowMode == DrawingPanel.decMode)
		    m.switchto(Table.lookUp("decimalText"));
        else
		    m.switchto(Table.lookUp("fractionText"));
        owner.thickMode = dc.thickMode;
        m = owner.owner.mainMenus.getMenu(Table.lookUp("thicknessText"));        
        if (owner.thickMode == DrawingPanel.relMode)
		    m.switchto(Table.lookUp("relativeText"));
        else
		    m.switchto(Table.lookUp("absoluteText"));
		    
        owner.labelHeight = dc.labelHeight;
        m = owner.owner.mainMenus.getMenu(Table.lookUp("optionsText"));
        CheckboxMenuItem cmi = (CheckboxMenuItem) m.getItem(Table.lookUp("labelsText"));        
        if (owner.labelHeight == 0)
            cmi.setState(false);
        else
            cmi.setState(true);
*/            
        owner.flowMode = dc.flowMode;
        if (owner.flowMode == DrawingPanel.decMode)
        {	//if (owner.realDWO)
        	//	owner.sdip.rbDecimaalItem.setSelected(true);
        	//else
//GWT        	
        	//owner.owner.rbDecimaalItem.setSelected(true);
        }
        else
        {	//if (owner.realDWO)
        	//	owner.sdip.rbBreukenItem.setSelected(true);
        	//else
//GWT        	
        	//owner.owner.rbBreukenItem.setSelected(true);
        
        }
        
        owner.thickMode = dc.thickMode;
        if (owner.thickMode == DrawingPanel.relMode)
        {	//if (owner.realDWO)
        	//	owner.sdip.rbRelatiefItem.setSelected(true);
        	//else
//GWT        	
        	//owner.owner.rbRelatiefItem.setSelected(true);
        
        }
        else
        {	//if (owner.realDWO)
        	//	owner.sdip.rbAbsoluutItem.setSelected(true);
        	//else
//GWT        	
        	//owner.owner.rbAbsoluutItem.setSelected(true);
        
        }
        
        owner.labelHeight = dc.labelHeight;        
        if (owner.labelHeight == 0)
        {	//if (owner.realDWO)
        	//	owner.sdip.cbLabelsItem.setSelected(false);
        	//else
//GWT        	
        	//owner.owner.cbLabelsItem.setSelected(false);
        
        }
        else
        {	//if (owner.realDWO)
        	//	owner.sdip.cbLabelsItem.setSelected(true);
        	//else
//GWT        	
        	//owner.owner.cbLabelsItem.setSelected(true);
        
        }
            
            
        owner.flowOn = dc.flowOn;
        // recreate vertices, in same order as copies!!
        vertices = new Vector();
        for (int i = 0; i < dc.vertexCopies.size(); i++)
        {   VertexCopy vc = (VertexCopy) dc.vertexCopies.elementAt(i);
            Vertex v = new Vertex(vc.root, vc.layerNum);
            if (vc.traceFrom)
                owner.traceFrom = v;
            // label is taken care of globally
            v.decimals = vc.decimals;
            
            //v.vLabel.setText(vc.labelText);
            v.labelText = vc.labelText;
            v.setLocation(owner.getLayerStart(v.layerNum), vc.yLocation);  
            // add listeners
//GWT            
            //v.addEdgeButton.addMouseListener(owner.getAddEdgeML());            
            if (!v.root)
            {	    
//GWT            	
            	//v.colorButton.addMouseListener(owner.getTraceML());
            }
            else
                owner.roots.addElement(v);
//GWT            
            //DrawingPanel.MLMML lis = owner.getMLMML();
            //v.flowField.addMouseListener(lis);
            //v.flowField.addMouseMotionListener(lis);
            
            vertices.addElement(v); // also save in array form
            // save as usual
            vertexLayers[v.layerNum].addElement(v);
//GWT            
            //owner.add(v);
            // v must have been added
            if (v.root)
                v.setFlow(new Rational(vc.flow));
            
        }    
        
        for (int j = 0; j < dc.edgeCopies.size(); j++)
        {   EdgeCopy ec = (EdgeCopy) dc.edgeCopies.elementAt(j);
            int fromIndex = dc.vertexCopies.indexOf(ec.fromVertexCopy);
            int toIndex = dc.vertexCopies.indexOf(ec.toVertexCopy);
            Vertex fromVertex = (Vertex) vertices.elementAt(fromIndex);
            Vertex toVertex = (Vertex) vertices.elementAt(toIndex);
            Edge e = new Edge(owner, fromVertex, toVertex, 
                              new Rational(ec.capacity));
            e.lastTimeChanged = ec.lastTimeChanged;
            e.setEdge(); // needs coordinates of from-toVertex
            e.setMode(ec.mode);
            //e.thickMode = owner.thickMode;
            edges.addElement(e);
//GWT            
            //owner.add(e.capacityField);
        }
// this is already done in the Edge constructor!!        
/*        
        // now fix in- and outedges
        for (int k = 0; k < vertices.size(); k++)
        {   Vertex v = (Vertex) vertices.elementAt(k);
            VertexCopy vc = (VertexCopy) dc.vertexCopies.elementAt(k);
            for (int m = 0; m < vc.inEdgeCopies.size(); m++)
            {   EdgeCopy inec = (EdgeCopy) vc.inEdgeCopies.elementAt(m);
                int inIndex = dc.edgeCopies.indexOf(inec);
                v.inEdges.addElement(edges.elementAt(inIndex));
            }
            for (int n = 0; n < vc.outEdgeCopies.size(); n++)
            {   EdgeCopy outec = (EdgeCopy) vc.outEdgeCopies.elementAt(n);
                int outIndex = dc.edgeCopies.indexOf(outec);            
                v.outEdges.addElement(edges.elementAt(outIndex));                
            }
            
            
        }
*/        
        setEdgeThicknessMode(owner.thickMode);
        if (owner.traceFrom != null)
        {   owner.traceBack(owner.traceFrom);
        }
//GWT
/*        
        if (owner.flowOn)
        {    owner.flowThread = new Thread(owner);
             owner.flowThread.start();   
             if (owner.realDWO)
            	 owner.sdip.bottomPanel.bubbleButton.setLabel(Stroomdiagrammen.rb.getString("flowOffText"));
             else	 
            	 owner.owner.bPanel.bubbleButton.setLabel(Stroomdiagrammen.rb.getString("flowOffText"));
        }
*/            
        calculateDiagram();
        resizeDiagram(true);
        owner.paint();
        
    }  // recreateDiagram  
/*    
BEWAREN VOOR KANSENBOMEN
    // this procedure produces a tree    
    public void insertVertex(Vertex v, Vertex o)
    {   // adding the root (only once)
        if (o == null)
        {   vertexLayers[v.layerNum].addElement(v);                        
        }    
        else // some other vertex
        // note: here it is assumed the vertices in both the o-layer
        // and the v-layer (the next one) are sorted by
        // vertical position
        // determine the number of outgoing edges from all 
        // vertices preceeding o and the number of outgoing
        // edges from o and insert v in the next layer after
        // these outgoing vertices
        // this works for trees since each of these outgoing edges 
        // ends in a DIFFERENT vertex
        {   int oIndex = vertexLayers[o.layerNum].indexOf(o);
            int vIndex = 0;
            for (int i = 0; i <= oIndex; i++)
            {   Vertex w = (Vertex) vertexLayers[o.layerNum].elementAt(i);
                vIndex += w.outEdges.size(); TREE version
            }    
            vertexLayers[v.layerNum].insertElementAt(v, vIndex);                        
        }    
        updateVertexLayer(v.layerNum);
        if (edgeLayers[v.layerNum].size() > 0)
            updateEdgeLayer(v.layerNum);
        owner.add(v);
        owner.repaint();
    }   
    // vertical "spreading" / "cascading"
    // vertices are put in the same vertical order as in
    // vertexLayers[layerNum]
    public void updateVertexLayer(int layerNum)
    {   // horizontal position
        int horPos = owner.workSpace.x + owner.leftSpace +
                     layerNum * (owner.vertexWidth + owner.layerDistance);
        // vertical positioning
        int spacing = owner.workSpace.height - (owner.topSpace + owner.bottomSpace) -
                      vertexLayers[layerNum].size() * 
                      (owner.vertexHeight + owner.labelHeight);
        if (spacing >= 0)              
            spacing /= vertexLayers[layerNum].size() + 1;             
        else
            spacing /= vertexLayers[layerNum].size() - 1;                     
        // set positions
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
            if (spacing >= 0)
                w.setLocation(horPos, owner.workSpace.y + owner.topSpace + spacing +
                                      i * (owner.vertexHeight + owner.labelHeight + spacing));  
            else 
                w.setLocation(horPos, owner.workSpace.y + owner.topSpace +
                                      i * (owner.vertexHeight + owner.labelHeight + spacing));  
        }    
        owner.repaint();
    }  // updateVertexLayer
    
EINDE BEWAREN    
*/
}
