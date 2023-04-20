package fi.stroomdiagrammengwt.client;

import java.util.Vector;
import java.util.HashMap;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * the class managing the layout of the flow diagram, in particular
 * after inserting a vertex or deleting edges;<br>
 * the flow diagram is shown from left (where the root(s) are) to right with the vertices
 * positioned from top to bottom in vertical layers <br> 
 * it also saves the flow diagram in a DiagramCopy for getState and
 * recreates a flow diagram from a DiagramCopy for setState and 
 * for the launchData
 */
public class DiagramManager 
{
	/**
	 * owner of the DiagramManager instance
	 */
	DrawingPanel owner;
	/**
	 * for each vertical vertex layer a Vector containing its vertices
	 */
    Vector[] vertexLayers = new Vector[DrawingPanel.maxLayers];
    /**
     * the edges of the flow diagram
     */
    Vector<Edge> edges = new Vector<Edge>();
    /**
     * the vertices of the flow diagram
     */
    Vector<Vertex> vertices = new Vector<Vertex>();

    /**
     * constructor
     * @param o the Drawing Panel owning this class
     */
    public DiagramManager(DrawingPanel o)
    {   owner = o;
    	// create the (empty) Vectors
        for (int i = 0; i < vertexLayers.length; i++)
            vertexLayers[i] = new Vector<Vertex>();
    }    
    
    // this procedure produces a tree
    /**
     * insert a Vertex v (already created) which will (elsewhere) be connected with an Edge to 
     * an existing Vertex o; if o == null, add a root; <br> note that the number of the vertex layer
     * into which Vertex v will be inserted must have been set;<br>
     * try to avoid crossings between existing Edges and the Edge to be created, and update the 
     * vertical layout of the vertex layer into which Vertex v is inserted   
     * @param v Vertex v to be inserted
     * @param o Vertex o to which v will be connected or null if v is a new root
     */
    public void insertVertex(Vertex v, Vertex o)
    {   boolean numLayersChanged = false;
        // adding a root 
        if (o == null)
        {   vertexLayers[v.layerNum].addElement(v);                        
        }    
        else // some other vertex
        // note: here it is assumed the vertices in both the o-layer
        // and the v-layer (the next one) are sorted by vertical position
        // note also: outEdges of o are supposed to be sorted
        // by y-location of toVertex
        // optimize for minimal number of crossings of existing Edges with new Edge
        {   // number of vertices in layer where v should be inserted            
            int vIndex = vertexLayers[v.layerNum].size();
            // v will be the only Vertex in its layer
            // increase actual number of vertex layers
            if (vIndex == 0)
            {   if (v.layerNum > owner.numLayers)
                {   owner.numLayers++;
                    owner.setLayerDistance();
                    numLayersChanged = true;
                }    
            }    
            // start at the Vertex tv at bottom of the vertex layer where v should be inserted
            // and check if inserting v below this vertex will create a crossing of existing Edges with 
            // the nre edge to be created
            boolean allCrosses = true;
            for (int k = vertexLayers[v.layerNum].size() - 1; k >= 0; k--)
            {   Vertex tv = (Vertex) vertexLayers[v.layerNum].elementAt(k);
                int endy = tv.getLocation().y + 
                           (DrawingPanel.vertexHeight + DrawingPanel.labelHeight) / 2;
                if (allCrosses)
                {   if (createsCrossing(v.layerNum, o, endy))
                       vIndex--;
                    else
                    {    allCrosses = false;
                    }
                }    
            }
            // if crosses at each step vIndex ends at 0, 
            // try inserting v as the topmost Vertex
            if ((vIndex == 0) && (vertexLayers[v.layerNum].size() > 0))
            {   Vertex topv = (Vertex) vertexLayers[v.layerNum].elementAt(0);
                int topendy = topv.getLocation().y - 
                              (DrawingPanel.labelHeight + DrawingPanel.vertexHeight) / 2;
                if (!createsCrossing(v.layerNum, o, topendy))
                {   allCrosses = false;
                    vIndex = 0;
                }
            }
            // still crosses, use alternate algorithm minimizing the
            // number of crosses
            if (allCrosses) 
            {   int oIndex = vertexLayers[o.layerNum].indexOf(o);
                vIndex = 0; // reset
                Vector<Vertex> targetVertices = new Vector<Vertex>();             
                for (int i = 0; i <= oIndex; i++)
                {   Vertex w = (Vertex) vertexLayers[o.layerNum].elementAt(i);
                    for (int j = 0; j < w.outEdges.size(); j++) 
                    {   Edge te = (Edge) w.outEdges.elementAt(j);
                        Vertex tw = te.toVertex;
                        if (tw.layerNum == v.layerNum)
                        {   int twIndex = vertexLayers[tw.layerNum].indexOf(tw);
                            if (!targetVertices.contains(tw) && (twIndex == vIndex))
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
            // nor insert the new Vertex v
            if (vertexLayers[v.layerNum].size() > 0)            
            {   if (vIndex == 0) // top
                {   Vertex ov = (Vertex) vertexLayers[v.layerNum].elementAt(vIndex);                                   
                    v.setLocation(v.getLocation().x, 
                        Math.max(owner.workSpace.y + DrawingPanel.ofSpace,
                                 ov.getLocation().y -
                                 (ov.getSize().height + DrawingPanel.minSpace)));
                }
                else 
                {   Vertex ov = (Vertex) vertexLayers[v.layerNum].elementAt(vIndex - 1);                                   
                    v.setLocation(v.getLocation().x, 
                                  ov.getLocation().y +
                                  ov.getSize().height + DrawingPanel.minSpace);
                }    
            }    
            vertexLayers[v.layerNum].insertElementAt(v, vIndex);                        
        } // else for non roots
        vertices.addElement(v);
        // update vertical spacing 
        if (o == null)
            updateVertexLayer(v.layerNum);
        else if (vertexLayers[v.layerNum].size() > 1)
            updateVertexLayer(v.layerNum);
        else // first vertex in layer
            v.setLocation(owner.getLayerStart(v.layerNum), o.getLocation().y);
        updateEdges();
        if (numLayersChanged)
            resizeDiagram();    
    }   
    
    /**
     * move Vertex v to vertex layer layerNum
     * @param v Vertex v
     * @param layerNum new number of vertex layer for Vertex v 
     */
    public void moveVertexTo(Vertex v, int layerNum)
    {   // set the new x-position of Vertex v, leave the y-position unchanged
    	v.setLocation(owner.workSpace.x + DrawingPanel.ofSpace +
                      layerNum * (DrawingPanel.vertexWidth + owner.layerDistance),
                      v.getLocation().y);
    	// remove Vertex v from its old vertex layer
        vertexLayers[v.layerNum].removeElement(v);
        // set the new vertex layer number
        v.layerNum = layerNum;
        // maintain sorting within new vertex layer:
        // assume the new vertex layer is sorted by y-position and find the index
        // where Vertex v should be inserted
        int index = 0;
        for (int i = 0; i < vertexLayers[v.layerNum].size(); i++)
        {   Vertex av = (Vertex) vertexLayers[v.layerNum].elementAt(i);
            if (av.getLocation().y < v.getLocation().y)
                index++;
        }    
        // insert Vertex v into the new vertex layer at index  
        vertexLayers[v.layerNum].insertElementAt(v, index);
        // check if Vertex v was added to an empty vertex layer on the right
        // and update the actual number of vertex layers used
        if (v.layerNum > owner.numLayers)
            owner.numLayers = v.layerNum;
        // sort the edges out of of all vertices for which one 
        // outgoing edge ends at Vertex v   
        for (int k = 0; k < v.inEdges.size(); k++)
        {   Edge ie = (Edge) v.inEdges.elementAt(k);
            ie.fromVertex.sortOutEdges();
        }    
        // do not update vertex layers
        // edges are updated in mouseReleased
    }    
    
    /**
     * update vertex layer layerNum: this happens when the number of 
     * non-empty vertex layers has been changed (horizontal update) and/or
     * when the number of vertices in the layer has been changed
     * (vertical update); update the edges elsewhere
     * @param layerNum the vertex layer to update
     */
    public void updateVertexLayer(int layerNum)
    {   // make sure workspace is set
    	if (owner.workSpace == null)
    		owner.defineSpaces();
    	// new x position of the vertex layer, owner.layerDistance is already changed
        int horPos = owner.workSpace.x + DrawingPanel.ofSpace +
                     layerNum * (DrawingPanel.vertexWidth + owner.layerDistance);
        // new total spacing available between vertices in the vertex layer, vertexLayers[layerNum] is already changed
        int spacing = owner.workSpace.height - (DrawingPanel.ofSpace + DrawingPanel.ofSpace) -
                      vertexLayers[layerNum].size() * 
                      (DrawingPanel.vertexHeight + DrawingPanel.labelHeight);
        // enough total spacing, find individual spacings
        if (spacing >= 0)              
        {    spacing /= vertexLayers[layerNum].size() + 1;
        }
        else // cascade the vertices in layer layerNum
        {    spacing /= Math.max(1, vertexLayers[layerNum].size() - 1);
        }
        // set positions of the vertices in layer layerNum
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
            if (spacing >= 0)
                w.setLocation(horPos, owner.workSpace.y + DrawingPanel.ofSpace + spacing +
                                      i * (DrawingPanel.vertexHeight + DrawingPanel.labelHeight + spacing));  
            else 
                w.setLocation(horPos, owner.workSpace.y + DrawingPanel.ofSpace +
                                      i * (DrawingPanel.vertexHeight + DrawingPanel.labelHeight + spacing));
        }    
        owner.paint();
    }  // updateVertexLayer


    /**
     * fuse Vertex v1 and Vertex v2 into one new Vertex v2 by adding all
     * Edges into Vertex v1 and all Edges out of Vertex v1 to Vertex v2
     * @param v1 Vertex that will be fused with Vertex v2
     * @param v2 Vertex that will be fused with Vertex v1
     */
    public void fuseVertices(Vertex v1, Vertex v2)
    {   // transfer incoming edges of v1 to v2
        for (int i = 0; i < v1.inEdges.size(); i++)
        {   // edge to relocate/fuse
            Edge ie1 = (Edge) v1.inEdges.elementAt(i);
            // check if v2 already has an edge starting at the same
            // vertex ie1.fromVertex, then remove and update capacity
            Edge ie2 = v2.hasInEdgeFrom(ie1.fromVertex);
            if (ie2 != null)
            {   // remove ie1 from ie1.fromVertex
                ie1.fromVertex.outEdges.removeElement(ie1);
                // sum the capacities
                Rational temp = ie1.capacity.plus(ie2.capacity);
                ie2.setCapacity(temp, false);
                edges.removeElement(ie1);
            }    
            else // transfer the edge to v2 as incoming edge
            {   ie1.toVertex = v2;
                v2.inEdges.addElement(ie1);
            }    
        }    
        // transfer outgoing edges of v1 to v2
        // remember old number of v2 outEdges
        int oldV2Out = v2.outEdges.size();
        for (int j = 0; j < v1.outEdges.size(); j++)
        {   // edge to relocate/fuse
            Edge oe1 = (Edge) v1.outEdges.elementAt(j);
            // check if v2 has an edge ending at the same
            // vertex oe1.toVertex, then remove and update capacity
            Edge oe2 = v2.hasOutEdgeTo(oe1.toVertex);
            if (oe2 != null)
            {   // remove oe1 from oe1.toVertex
                oe1.toVertex.inEdges.removeElement(oe1);
                Rational temp = oe1.capacity.plus(oe2.capacity);
                oe2.setCapacity(temp, false);
                edges.removeElement(oe1);
            }    
            else // transfer the edge to v2 as outgoing edge  
            {   oe1.fromVertex = v2;
                v2.outEdges.addElement(oe1);
                // for display
                oe1.setCapacity(oe1.capacity, false);
            }    
        }    
        // if v1 had any outgoing edges and v2 had any outgoing
        // edges BEFORE the fusion, "new" capacities add up to 2 so divide
        if ((v1.outEdges.size() > 0) && (oldV2Out > 0))
            for (int k = 0; k < v2.outEdges.size(); k++)
            {   Edge e2 = (Edge) v2.outEdges.elementAt(k);
                Rational temp = new Rational(1, 2, 5e-1d);
                e2.setCapacity(e2.capacity.times(temp), false);
            }    
        // remove v1 from layer vector    
        vertexLayers[v1.layerNum].removeElement(v1);
        vertices.removeElement(v1);
        // v1 was a root (then so was v2, since roots cannot be moved forward
        // onto a vertex and vertices cannot be moved backward onto a root
        if (owner.roots.contains(v1))
            owner.roots.removeElement(v1);
        // do not update vertex layer
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
    
    /**
     * check if Vertex v, which s being dragged, is sufficiently on top
     * of another vertex (if any)  
     * @param v the Vertex v
     * @return a Vertex or null
     */
    public Vertex fuseWith(Vertex v)
    {   Vertex result = null;
    	// search all vertices, since Vertex v is being dragged    
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

    /**
     * check if Vertex v, which is being dragged, intersects any other vertex
     * @param v the Vertex v
     * @return true/false
     */
    public boolean intersectsVertex(Vertex v)
    {   boolean result = false;
    	Rectangle vRec = new Rectangle(v.xPos, v.yPos, v.breedte, v.hoogte);
    	// search all vertices, since Vertex v is being dragged
        for (int j = 0; j < vertexLayers.length; j++)
        {   for (int i = 0; i < vertexLayers[j].size(); i++)
            {   Vertex av = (Vertex) vertexLayers[j].elementAt(i);
                Rectangle avRec = new Rectangle(av.xPos, av.yPos, av.breedte, av.hoogte);
                if (v != av)
                    result = result || vRec.intersects(avRec);
            }
        }        
        return result;
    }    
    
    /**
     * delete Vertex v, which is allowed if Vertex v has no outEdges
     * @param v the Vertex to be deleted if allowed
     */
    public void deleteVertex(Vertex v)
    {   
        boolean deletable = (v.outEdges.size() == 0);
        boolean numLayersChanged = false;
        if (deletable)
        {   int vIndex = vertexLayers[v.layerNum].size();
            // if v is the only vertex in the last vertex layer
            // remove this vertex layer
            if ((v.layerNum == owner.numLayers) && (vIndex == 1))
            {   owner.numLayers--;
                owner.setLayerDistance();
                numLayersChanged = true;
            }   
            vertices.removeElement(v);
            vertexLayers[v.layerNum].removeElement(v);
            if (numLayersChanged)
                resizeDiagram();
            if (owner.traceFrom == v)
            {   lowLightEdges();
                owner.traceFrom = null;
            }
        }
    }    

    /**
     * add an Edge e, note that the fromVertex of e must be known
     * @param e the Edge to be added
     */
    public void addEdge(Edge e)
    {   // add
        edges.addElement(e);
        // sort all edges out of fromVertex
        e.fromVertex.sortOutEdges();
        // update edge position
        updateEdges();        
        owner.paint();
    }    

    /**
     * delete Edge e if allowed; deleting is allowed when:<br>
     * e.toVertex has more than one inEdge<br>
     * e.toVertex has one inEdge (must be e) and has no outEdges, in this case remove toVertex also
     * @param e the Edge e to be deleted if allowed
     * @return true/false when the Edge was deleted/not deleted
     */
    public boolean deleteEdge(Edge e)
    {   // toVertex has one inEdge, no outEdges
        boolean toVertexIsLeaf = (e.toVertex.inEdges.size() == 1) &&
                                 (e.toVertex.outEdges.size() == 0);       
        // edge is deletable
        boolean deletable = (e.toVertex.inEdges.size() > 1) ||
                            toVertexIsLeaf;
        if (deletable)
        {   e.fromVertex.outEdges.removeElement(e);
            Edge oce = e.fromVertex.oldestOutChanged();
            // this ensures the sum of capacities of the outEdges of fromVertex remains 1
            if (oce != null)
            {    oce.setCapacity(e.capacity.plus(oce.capacity), true);
            }
            // else e was the only outEdge of fromVertex    
            e.toVertex.inEdges.removeElement(e);
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
        return deletable;
    }    
    
    /**
     * after changing (some) vertex positions, this method recalculates
     * the edge positions
     */
    public void updateEdges()
    {   for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            e.setEdge();
        } // for
        owner.paint();
    }    
    
    /**
     * get the Edge whose click polygon contains (x,y)
     * @param x x-value
     * @param y y-value
     * @return the Edge or null
     */
    public Edge getClickedEdge(int x, int y)
    {   Edge result = null;
        for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            if (e.cp.contains(x, y))
                result = e;
        } // for
        return result;
    }    

    /**
     * get the Edge whose capacityField contains (x,y)
     * @param x x-value
     * @param y y-value
     * @return the Edge or null
     */
    public Edge getClickedCapacity(int x, int y)
    {   Edge result = null;
        for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            if (e.capacityClicked(x, y))
                result = e;
        } // for
        return result;
    }    
    
    /**
     * add/remove labels to the vertices by changing the parameter
     * labelHeight in class DrawingPanel 
     * @param b true/false
     */
    public void setVertexLabels(boolean b)
    {   if (b)
    		DrawingPanel.labelHeight = DrawingPanel.LABELHEIGHT;
        else
        	DrawingPanel.labelHeight = 0;
        for (int i = 0; i < vertexLayers.length; i++)
                for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                v.setLabel(b);                
            }
        redrawDiagram();    
        owner.paint();       
    }
    
    /**
     * check if a possible Edge starting at Vertex start and ending in vertex layer toLayerNum 
     * at y-position endy crosses any existing Edge    
     * @param toLayerNum the vertex layer number where the possible Edge ends 
     * @param start the Vertex where the possible Edge starts
     * @param endy y-position where the possible Edge ends
     * @return true/false
     */
    public boolean createsCrossing(int toLayerNum, Vertex start, int endy)
    {   boolean cross = false;
        Point end = new Point(owner.getLayerStart(toLayerNum), endy);
                
        for (int i = 0; i < edges.size(); i++)
        {   Edge e = (Edge) edges.elementAt(i);
            Vertex v = e.fromVertex;          
            if (v != start)
                cross = cross || intersects(e, start, end);                   
        }                    
        return cross;
    }    
    
    /**
     * check if Edge e (or more precise the segment [edgeStart,edgeEnd] intersects the
     * segment starting at the top right of Vertex st and ending in Point end  
     * @param e the Edge e
     * @param st the Vertex st
     * @param end the Point end
     * @return true/false
     */
    public boolean intersects(Edge e, Vertex st, Point end)
    {   // top right of st
    	Point start = new Point(st.getLocation().x + owner.vertexWidth, 
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

    /**
     * draw the vertices in the flow diagram using Context2d g
     * @param g the Context2d g
     */
    public void drawVertices(Context2d g)
    {    for (int j = 0; j < vertices.size(); j++)
         {   Vertex v = (Vertex) vertices.elementAt(j);
             v.paintComponent(g);
         }    
    }    

    /**
     * draw the edges in the flow diagram using Context2d g
     * @param g the Context2d g
     */
    public void drawEdges(Context2d g)
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.drawEdge(g);
         }    
    }    
    
    /**
     * freeze/unfreeze all edges of the flow diagram, i.e.
     * capacities cannot/can be changed
     * @param freeze true/false
     */
    public void freezeEdges(boolean freeze)
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.setFrozen(freeze);
         }    
    }    
    
    /**
     * remove the highlighting in the edges used in tracing back the flow
     * from the a vertex 
     */
    public void lowLightEdges()
    {    for (int j = 0; j < edges.size(); j++)
         {   Edge e = (Edge) edges.elementAt(j);
             e.highlighted = false;
         }    
    }    

    /**
     * display flows and capacities as decimal numbers or fractions; 
     * @param mode display mode, see class DrawingPanel, Vertex and Edge
     */
    public void setFlowMode(int mode)
    {   boolean remember = (DrawingPanel.flowMode != mode);
    	DrawingPanel.flowMode = mode; 
        calculateDiagram();
        for (int i = 0; i < vertexLayers.length; i++)
            for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
            	// this also sets the required display mode
                v.setFlow(v.flow);
            }
        for (int j = 0; j < edges.size(); j++)
        {   Edge e = (Edge) edges.elementAt(j);
            e.setMode(mode);
        }
        owner.paint();
        if (remember)
            owner.addToHistory();
    }    
    
    /**
     * draw the Edge thickness as relative or absolute, see class Edge
     * @param tMode the required edge thickness mode; <br>
     * use this also for updating the edge thickness in absolute mode when
     * flows have been changed
     */
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
    
    /**
     * clear the flow diagram, if newRoot == true, 
     * create one or more roots
     * @param newRoot true: create roots
     */
    public void clearDiagram(boolean newRoot)
    {   
        for (int j = edges.size() - 1; j >= 0; j--)
        {   Edge e = (Edge) edges.elementAt(j);
            edges.removeElement(e);
        }    
        for (int i = 0; i < vertexLayers.length; i++)
            for (int j = vertexLayers[i].size() - 1; j >= 0; j--)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                vertexLayers[i].removeElement(v);
                vertices.removeElement(v);
            }
        owner.traceFrom = null;    
        owner.roots.removeAllElements();    
        owner.layerDistance = DrawingPanel.maxLayerDistance;
        owner.numLayers = 0;
        // reset
        DrawingPanel.vertexCode = 1;
        if (newRoot)
        {   // clear history
        	DrawingPanel.history.removeAllElements();
        	DrawingPanel.owner.terugButton.setEnabled(false);
           	// user can add extra roots via the menu, so add one root
            if (DrawingPanel.owner.toonOptiesMenu)
            	owner.addNewRoot(true);
            else // add roots according to numRoots in StroomDiagrammenGWT 
            {	for (int rCnt = 1; rCnt <= DrawingPanel.owner.aantalBronnen; rCnt++)
            	{	owner.addNewRoot(false);
            	}
            	owner.addToHistory();
            }
        }
        owner.paint();    
    }    
    
    /**
     * set the number of decimals for displaying flows in vertices
     * as decimal numbers; the number of decimals is determined when
     * changing the flow in a root
     * @param decs the number of decimals required
     */
    public void setDecimals(int decs)
    {   owner.vDecimals = decs;
        for (int i = 1; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 v.decimals = decs;
             }   
    }    
    
    /**
     * recalculate the flows in the vertices of the diagram form left to right; 
     * flows in roots (layer 0) do not need recalculating; adjust the edge
     * thickness in absolute mode 
     */
    public void calculateDiagram()
    {   for (int i = 1; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 v.calculateFlow();
             }   
        if (owner.thickMode == DrawingPanel.absMode)
            setEdgeThicknessMode(DrawingPanel.absMode);
        owner.paint();     
    }    
    
    /**
     * get a Vector containing the (references to) the vertices in the flow diagram
     * ordered by vertex layer (left to right); note: vertices within the same vertex
     * layer are sorted top to bottom 
     * @return the Vertex reference Vector
     */
    public Vector<Vertex> getVertexRefs()
    {   Vector<Vertex> refs = new Vector<Vertex>();
        for (int i = 0; i < vertexLayers.length; i++)
             for (int j = 0; j < vertexLayers[i].size(); j++)
             {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                 refs.addElement(v);
             }   
        return refs;     
    }    
    
    /**
     * disable/enable any Mouse/Touch Events on the verices
     * @param freeze true/false
     */
    public void freezeVertices(boolean freeze)
    {  	for (int i = 0; i < vertexLayers.length; i++)
            for (int j = 0; j < vertexLayers[i].size(); j++)
            {   Vertex v = (Vertex) vertexLayers[i].elementAt(j);
                v.setFrozen(freeze);
            }
    }

    /**
     * a complete new layout: update the vertex layers
     * and then the edge positions
     */
    public void redrawDiagram()
    {   for (int i = 0; i < vertexLayers.length; i++)
            updateVertexLayer(i);
        updateEdges();    
        owner.paint();    
    }    
    
    /**
     * if the distance between the vertex layers has been changed, put the
     * vertices in layer layerNum in the correct (horizontal) position  
     * @param layerNum the layer to be corrected
     */
    public void resizeVertexLayer(int layerNum)
    {   // new horizontal position for layer layerNum
        int horPos = owner.workSpace.x + DrawingPanel.ofSpace +
                     layerNum * (DrawingPanel.vertexWidth + owner.layerDistance);
        // set new x positions of the vertices in layer layerNum
        for (int i = 0; i < vertexLayers[layerNum].size(); i++)
        {   Vertex w = (Vertex) vertexLayers[layerNum].elementAt(i);
                w.setLocation(horPos, w.getLocation().y);  
        }    
        owner.paint();
    }  // resizeVertexLayer

    /**
     * if the distance between the vertical layers has been changed, 
     * put all vertices and edges again in the correct position
     */
    public void resizeDiagram()
    {   // put the vertices in the correct position, each layer separately
    	for (int i = 0; i < vertexLayers.length; i++)
            resizeVertexLayer(i);
    	// this takes care of the correct position of the edges    	
        updateEdges();    
        owner.paint();    
    }    
    
    /**
     * copy the current flow diagram to an instance of DiagramCopy 
     * @return the DiagramCopy of the current flow diagram
     */
    public DiagramCopy copyDiagram()
    {   
    	DiagramCopier diagramCopier = new DiagramCopier(this);
    	DiagramCopy dc = diagramCopier.getDiagramCopy();
        return dc;
    }
    
    /**
     * copy the current flow diagram to a HashMap
     * @return the HashMap describing the current flow diagram
     */
    public HashMap<String,Object> copyDiagramToHashMap()
    {   
    	DiagramCopier diagramCopier = new DiagramCopier(this);
    	DiagramCopy dc = diagramCopier.getDiagramCopy();
    	HashMap<String,Object> diagramCopyHM = NoSer.diagramCopyToDiagramHashMap(dc);
        return diagramCopyHM;
    }    

    /**
     * create a flow diagram whose attributes are in HashMap dhm
     * @param dhm the HashMap
     */
    public void recreateDiagram(HashMap<String,Object> dhm)
    {
    	DiagramCopy dc = NoSer.diagramHashMapToDiagramCopy(dhm);
    	recreateDiagram(dc);
    }
    
    /**
     * create a flow diagram whose attributes are in DiagramCopy dc
     * @param dc the DiagramCopy
     */
    public void recreateDiagram(DiagramCopy dc)
    {   // remove the current flow diagram
        clearDiagram(false);
        // first arrange the global diagram attributes
        owner.layerDistance = dc.layerDistance;
        owner.numLayers = dc.numLayers;
        DrawingPanel.flowMode = dc.flowMode;
        // highlight the menu item corresponding to the flowMode
        if (DrawingPanel.owner.toonBerekeningenMenu)
        {  	if (DrawingPanel.flowMode == DrawingPanel.decMode)
        	{	
        		DrawingPanel.owner.decimaalItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.boldmenuitem());
        		DrawingPanel.owner.breukenItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.normalmenuitem());
        	}
        	else
        	{	
        		DrawingPanel.owner.decimaalItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.normalmenuitem());
        		DrawingPanel.owner.breukenItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.boldmenuitem());
        	}	
        }
        owner.thickMode = dc.thickMode;
        // highlight the menu item corresponding to the thickMode
        if (DrawingPanel.owner.toonStroombreedteMenu)
        {	if (owner.thickMode == DrawingPanel.relMode)
        	{	
        		DrawingPanel.owner.relatiefItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.boldmenuitem());
        		DrawingPanel.owner.absoluutItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.normalmenuitem());		
        	}
        	else
        	{	
        		DrawingPanel.owner.relatiefItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.normalmenuitem());
        		DrawingPanel.owner.absoluutItem.setStyleName(DrawingPanel.owner.stroomDiagrammenGWTCssResource.boldmenuitem());		
        	}	
        
        }
        DrawingPanel.labelHeight = dc.labelHeight;
        // show the correct label item in the Option Menu
        if (DrawingPanel.owner.toonOptiesMenu)
        {	if (DrawingPanel.labelHeight == 0) // no labels
        	{	
        		DrawingPanel.owner.labelsItem.setText(StroomDiagrammenGWT.rb.knooppuntenMetLabelsLabel());
        	}
        	else // labels
        	{	
        		DrawingPanel.owner.labelsItem.setText(StroomDiagrammenGWT.rb.knooppuntenZonderLabelsLabel());
        	}	
        }
        vertices = new Vector<Vertex>();
        // create the vertices, to identify them use their code !
        for (int i = 0; i < dc.vertexCopies.size(); i++)
        {   VertexCopy vc = (VertexCopy) dc.vertexCopies.elementAt(i);
            Vertex v = new Vertex(vc.root, vc.layerNum);
            // set the code, not necessary in the Java-version
            v.code = vc.code;
            // update the maximal vertexCode in DrawingPanel
            DrawingPanel.vertexCode = Math.max(DrawingPanel.vertexCode, v.code+1);
            if (vc.traceFrom)
                owner.traceFrom = v;
            // attribute label is taken care of globally
            v.decimals = vc.decimals;
            v.labelText = vc.labelText;
            v.setLocation(owner.getLayerStart(v.layerNum), vc.yLocation);  
            if (v.root)
            {	owner.roots.addElement(v);    
            }
            vertices.addElement(v); 
            // save in the correct layer
            vertexLayers[v.layerNum].addElement(v);
            if (v.root)
                v.setFlow(new Rational(vc.flow));
        }    
        // create the edges, use vertex codes to identify the fromVertex and toVertex
        // in the Java version this works by reference
        for (int j = 0; j < dc.edgeCopies.size(); j++)
        {   EdgeCopy ec = (EdgeCopy) dc.edgeCopies.elementAt(j);
            Vertex fromVertex = findVertex(vertices,ec.fromVertexCopy.code);
            Vertex toVertex = findVertex(vertices,ec.toVertexCopy.code);
            Edge e = new Edge(owner, fromVertex, toVertex, 
                              new Rational(ec.capacity));
            e.lastTimeChanged = ec.lastTimeChanged;
            e.setEdge(); // needs coordinates of from-toVertex
            e.setMode(ec.mode);
            edges.addElement(e);
        }
        setEdgeThicknessMode(owner.thickMode);
        if (owner.traceFrom != null)
        {   owner.traceBack(owner.traceFrom);
        }
        calculateDiagram();
        resizeDiagram();
        owner.paint();
        owner.addToHistory();
    }  // recreateDiagram

    /**
     * giving a Vector containing Vertices and a code, find
     * the Vertex with that code (if any)
     * @param vertices Vector containing Vertices
     * @param code the code of the Vertex to be found
     * @return the Vertex with code or null
     */
    public Vertex findVertex(Vector<Vertex> vertices, int code)
    {
    	Vertex v = null;
    	for (int vCnt = 0; vCnt < vertices.size(); vCnt++)
    	{
    		Vertex aVertex = (Vertex) vertices.elementAt(vCnt);
    		if (aVertex.code == code)
    			v = aVertex;
    	}
    	return v;
    }
}
