package fi.stroomdiagrammengwt.client;


import java.util.Vector;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class representing a vertex in the flow diagram;<br> 
 * each vertex has a flow, calculated from the flow entered in the root(s) to which the vertex is connected;<br> 
 * edges out of the vertex are added by pressing the right arrow button and end in a new vertex; <br>
 * the flow in a vertex, which is not a root, can be traced back by pressing the left arrow button; <br>
 * vertices can be moved vertically in the same layer or horizontally when possible; putting two vertices on top
 * of each other fuses them into one vertex; if a vertex is a terminal vertex (no outgoing edges) it can be deleted
 * by dragging it onto the border of the work area (this also deletes its incoming edges)<br>
 * Mouse/Touch action on vertices is handled in class DrawingPanel;<br> 
 * the initial flow in a root can be entered/changed by a doubleClick or longClick on the root, see also class VertexPopup; <br>
 * the layout of the flow diagram is controlled by the class DiagramManager; <br>
 * vertices can have a label: additional text in a rectangle above the vertex; text for the label is entered/changed
 * by a doubleClick or longClick on the label, see also class VertexPopup.    
 */

public class Vertex 
{
	/**
	 * left/right arrowbutton enabled?
	 */
	boolean frozen = false;
	/**
	 * unique vertex code
	 */
	int code;
	/**
	 * horizontal position of the vertex, that is the number of the vertical layer it is in 
	 */
    int layerNum;
    /**
     * the flow through the vertex
     */
    Rational flow = DrawingPanel.unDef;
    /**
     * String for displaying the flow
     */
    String flowText = "";
    /**
     * font for displaying the flow
     */
    String fontString = "12px arial, sans-serif";
    /**
     * does the vertex have a label?
     */
    boolean hasLabel = false;
    /**
     * text of the vertex label
     */
    String labelText = "";
    /**
     * number of decimals if flow displayed as a double
     */
    int decimals = 2;
    /**
     * left arrow button for back tracing the flow
     */
    LWArrowButton colorButton;
    /**
     * right arrow button for adding an edge out of the vertex
     */
    LWArrowButton addEdgeButton;
    /**
     * is the vertex a root?
     */
    boolean root;  
    /**
     * the edges into the vertex
     */
    Vector inEdges = new Vector();
    /**
     * the edges out of the vertex
     */
    Vector outEdges = new Vector();
    
    /**
     * parameters to simulate a component: x position
     */
    int xPos; 
    /**
     * parameters to simulate a component: y position
     */
    int yPos;
    /**
     * parameters to simulate a component: width
     */
    int breedte;
    /**
     * parameters to simulate a component: height
     */
    int hoogte;

    /**
     * @param root is the vertex a root?
     * @param layerNum number of layer (horizontal position) to which the vertex should be assigned
     */
    public Vertex(boolean root, int layerNum)
    {   
    	// determine the code of the vertex
    	code = DrawingPanel.vertexCode;
    	DrawingPanel.vertexCode++;
    	this.root = root;
        this.layerNum = layerNum;
        // size fixed in DrawingPanel
        breedte = DrawingPanel.vertexWidth;
        hoogte = DrawingPanel.vertexHeight + DrawingPanel.labelHeight;
        // only non-roots have a trace button
        if (!root)
        {  	colorButton = new LWArrowButton(3, CssColor.make(192,192,192));
            colorButton.setBounds(xPos, yPos + 1 + DrawingPanel.labelHeight, 
                        DrawingPanel.leftButtonWidth, 
                        hoogte - 1 - DrawingPanel.labelHeight);
        }
        // all vertices have a button for adding edges
        addEdgeButton = new LWArrowButton(1, CssColor.make(192,192,192));
        addEdgeButton.setBounds(xPos + breedte - DrawingPanel.arrowButtonWidth,
        						yPos + 1 + DrawingPanel.labelHeight,
        						DrawingPanel.arrowButtonWidth,
        						hoogte - 1 - DrawingPanel.labelHeight);

        // the static DrawingPanel.labelHeight determines if vertices have Labela
        if (DrawingPanel.labelHeight > 0)
        {	hasLabel = true;
        }
    }    

    /**
     * does the vertex Rectangle contain the clicked point (x,y)?
     * note: the vertex Rectangle contains the label (if any) and the buttons 
     * @param x clicked x
     * @param y clicked y
     * @return true/false
     */
    public boolean vertexClicked(int x, int y)
    {
    	Rectangle vertexRect = new Rectangle(xPos, yPos, breedte, hoogte);
    	return vertexRect.contains(x, y);
    }
    
    /**
     * was the addEdgeButton of the vertex clicked?
     * @param x clicked x
     * @param y clickec y
     * @return true/false
     */
    public boolean addEdgeButtonClicked(int x, int y)
    {
    	if (addEdgeButton == null)
    		return false;
    	Rectangle addEdgeRect = new Rectangle(addEdgeButton.xPos, addEdgeButton.yPos, 
    										  addEdgeButton.breedte, addEdgeButton.hoogte);
    	return addEdgeRect.contains(x, y);
    }

    /**
     * was the colorButton of the vertex clicked?
     * @param x clicked x
     * @param y clickec y
     * @return true/false
     */
    public boolean colorButtonClicked(int x, int y)
    {
    	if (colorButton == null)
    		return false;
    	Rectangle colorRect = new Rectangle(colorButton.xPos, colorButton.yPos, 
    										colorButton.breedte, colorButton.hoogte);
    	return colorRect.contains(x, y);
    }
    
    /**
     * was the label of the vertex clicked?
     * @param x clicked x
     * @param y clickec y
     * @return true/false
     */
    public boolean labelClicked(int x, int y)
    {
    	if (!hasLabel)
    		return false;
    	
    	Rectangle labelRect = new Rectangle(xPos, yPos, 
    										breedte, DrawingPanel.labelHeight);
    	return labelRect.contains(x, y);
    }
    
    /**
     * set the location of the vertex, do not forget to set the location of the buttons
     * @param x xPos
     * @param y yPos
     */
    public void setLocation(int x, int y)
    {
    	xPos = x; 
    	yPos = y;
    	if (addEdgeButton != null)
    		addEdgeButton.setLocation(xPos + breedte - DrawingPanel.arrowButtonWidth,
									  yPos + 1 + DrawingPanel.labelHeight);
    	if (colorButton != null)
    		colorButton.setLocation(xPos, yPos + 1 + DrawingPanel.labelHeight);

    }

    /**
     * get the location of the vertex
     * @return a Point containing xPos and yPos
     */
    public Point getLocation()
    {
    	return new Point(xPos, yPos);
    }
    
    /**
     * get the size of the vertex
     * @return a Dimension containing breedte and hoogte
     */
    public Dimension getSize()
    {
    	return new Dimension(breedte, hoogte);
    }
    
    /**
     * get the bounding Rectangle of the vertex
     * @return the vertex Rectangle, label (if any) included 
     */
    public Rectangle getBoundingRect()
    {
    	return new Rectangle(xPos, yPos, breedte, hoogte);
    }
    
    /**
     * freeze/unfreeze the vertex: buttons are disabled/enabled
     * user input (label text, flow in a root) is disabled/enabled
     * @param b true/false
     */
    public void setFrozen(boolean b)
    {
    	frozen = b;
    	if (addEdgeButton != null)
    		addEdgeButton.setEnabled(!frozen);
    	if (colorButton != null)
    		colorButton.setEnabled(!frozen);
    		
    }

    /**
     * add a label to/remove the label from the vertex 
     * @param b true/false
     */
    public void setLabel(boolean b)
    {   
    	// add label
        if (b)
        {   // increase hoogte
        	hoogte += DrawingPanel.LABELHEIGHT;
        	// move the arrow buttons down
            if (!root)
                colorButton.setLocation(colorButton.xPos, colorButton.yPos + DrawingPanel.LABELHEIGHT);
            addEdgeButton.setLocation(addEdgeButton.xPos, addEdgeButton.yPos + DrawingPanel.LABELHEIGHT);
            hasLabel = true;
        }
        else // remove label
        {   hasLabel = false;
        	// decrease hoogte
        	hoogte = DrawingPanel.vertexHeight;
        	// move the arrow buttons up
            if (!root)
                colorButton.setLocation(colorButton.xPos, colorButton.yPos - DrawingPanel.LABELHEIGHT);
            addEdgeButton.setLocation(addEdgeButton.xPos,addEdgeButton.yPos - DrawingPanel.LABELHEIGHT);            
        }
        
    }    
    
    /**
     * set the flow of the vertex to Rational f, correctly format the flowText
     * String as a fraction or a decimal number  
     * @param f the flow to be set
     */
    public void setFlow(Rational f)
    {   flow = f;
        if (f.isUndefined())
            flowText = "";
        else if (DrawingPanel.flowMode == DrawingPanel.fracMode)
        {  	if (flow.isInteger())
                flowText = UF.format(flow.nom, 0);        
            else
                flowText = UF.format(flow.nom, 0) + "/" + UF.format(flow.denom, 0);  
        }    
        else if (DrawingPanel.flowMode == DrawingPanel.decMode)
        {    flowText = UF.format(flow.decVal, decimals);
        }
    }    

    /**
     * check if the vertex can move to a layer on the left: determine the maximum layer number
     * where any fromVertex of an incoming edge of the vertex is located and add 1      
     * @return the smallest number of a layer where the vertex could move to or the layernumber
     * of the vertex if it cannot be moved to the left 
     */
    public int canMoveLeftTo()
    {   if (root)
            return 0;
        int lNum = 0;
        for (int i = 0; i < inEdges.size(); i++)
        {   Edge ie = (Edge) inEdges.elementAt(i);
            lNum = Math.max(ie.fromVertex.layerNum + 1, lNum);
        }    
        return lNum;    
    }    

    /**
     * check if the vertex can move to a layer on the right: determine the minimum layer number
     * where any toVertex of an outgoing edge of the vertex is located and subtract 1      
     * @return the largest number of a layer where the vertex could move to or the layernumber
     * of the vertex if it cannot be moved to the right 
     */
    public int canMoveRightTo()
    {   if (root)
            return 0;
        int lNum = 16; //(big)    
        for (int i = 0; i < outEdges.size(); i++)
        {   Edge oe = (Edge) outEdges.elementAt(i);
            lNum = Math.min(oe.toVertex.layerNum - 1, lNum);
        }    
        return lNum;    
    }    

    /**
     * bubble sort the outEdges of the vertex by the y-location of toVertex
     */
    public void sortOutEdges()
    {   // bubble sort on y location of toVertex
        Edge tEdge;
        boolean swapped;
        for (int i = outEdges.size() - 1; i >= 0; i--)
        {   swapped = false;
            for (int j = 0; j < i; j++)
            {   Edge e1 = (Edge) outEdges.elementAt(j);
                Edge e2 = (Edge) outEdges.elementAt(j + 1);
                double angle1 = getAngle(e1.toVertex);
                double angle2 = getAngle(e2.toVertex);                
                if (angle2 > angle1)
                {   tEdge = e1;
                    outEdges.setElementAt(e2, j);
                    outEdges.setElementAt(tEdge, j + 1);
                    swapped = true;
                }
            } // for       
            if (!swapped)
                return;
        } // for
    }

    /**
     * arc tangens of angle between this vertex and vertex v
     * @param v vertex v
     * @return arc tangens
     */
    public double getAngle(Vertex v)
    {   double x = v.xPos - xPos + DrawingPanel.vertexWidth;
        double y = yPos - v.yPos;
        return Math.atan(y/x);
    }    
    
    /**
     * calculate the flow through the vertex, by adding all flows
     * coming in through the inEdges   
     */
    public void calculateFlow()
    {   Rational inFlow = new Rational(0, 1, 0);
        for (int i = 0; i < inEdges.size(); i++)
        {   Edge ie = (Edge) inEdges.elementAt(i);
            if (ie.fromVertex.flow.isUndefined())
            {   setFlow(DrawingPanel.unDef);
                return;
            }
            else
                inFlow = inFlow.plus(ie.fromVertex.flow.times(ie.capacity));            
        }
        setFlow(inFlow);
    }    

    /**
     * find the outEdge which was changed the longest time ago 
     * @return the "oldest" outEdge or null (case no outEdges)
     */
    public Edge oldestOutChanged()
    {   if (outEdges.size() == 0)
            return null;
        else
        {   int oldestChange = 0;
            long changeTime = Long.MAX_VALUE;
            // find "oldest" edge that was changed 
            for (int i = 0; i < outEdges.size(); i++)
            {   Edge ed = (Edge) outEdges.elementAt(i);
                long temp = ed.lastTimeChanged;
                if (temp < changeTime)
                {   changeTime = temp;
                    oldestChange = i;
                }    
            }    
            return (Edge) outEdges.elementAt(oldestChange);
        }
    }    
    
    /**
     * make sure that after changing the capacity of the Edge
     * changedEdge out of this vertex, the sum of the capacities
     * of all edges out of this vertex equals 1; do this by
     * changing the capacity of the out edge which remained 
     * unchanged the longest time 
     * set all out edges to the display mode that was used
     * in changing the capacity of changedEdge
     * @param changedEdge the out Edge changed
     * @param mode the display mode to be set
     */
    public void updateOutCapacities(Edge changedEdge, int mode)
    {   Rational total = new Rational(0, 1, 0);
        int oldestChange = 0;
        long changeTime = Long.MAX_VALUE;
        // find sum of all capacities and find the 
        // "oldest" edge that was changed 
        for (int i = 0; i < outEdges.size(); i++)
        {   Edge ed = (Edge) outEdges.elementAt(i);
            total = total.plus(ed.capacity);
            long temp = ed.lastTimeChanged;
            if (temp < changeTime)
            {   changeTime = temp;
                oldestChange = i;
            }    
        }    
        Rational temp = total.minus(new Rational(1, 1, 1));
        if (temp.isLarger(new Rational(0, 1, 0), mode))
        {   // note: case only one edge does not occur here
            Edge led = (Edge) outEdges.elementAt(oldestChange);
            // if possible decrease capacity of oldestChange
            // by total - 1
            if (led.capacity.isLargerOrEqual(temp, mode))
                led.setCapacity(led.capacity.minus(temp), false);
            else // not elegant??
            // set capacity of oldestChange to 1-changed
            // all others to zero
            {   Rational t1 = new Rational(1, 1, 1);
                led.setCapacity(t1.minus(changedEdge.capacity), false);
                for (int j = 0; j < outEdges.size(); j++)
                {    Edge oed = (Edge) outEdges.elementAt(j);
                     if ((oed != led) && (oed != changedEdge))
                         oed.setCapacity(new Rational(0, 0, 0), false);
                }    
            }    
        }    
        else if (temp.isSmaller(new Rational(0, 1, 0), mode))
        {   // increase capacity of oldestChange by
            // 1 - total, i.e. decrease by total - 1
            Edge led = (Edge) outEdges.elementAt(oldestChange);
            led.setCapacity(led.capacity.minus(temp), false);
        }
        // else total = 1, nothing to do
    }    

    /**
     * set the display mode of all edges out of this vertex to mode 
     * @param mode the display mode (see class DrawingPanel)
     */
    public void setOutModes(int mode)
    {   for (int i = 0; i < outEdges.size(); i++)
        {   Edge oe = (Edge) outEdges.elementAt(i);
            oe.setMode(mode);
        }    
    }    

    /**
     * check if this vertex has an incoming edge
     * connecting it with Vertex v
     * @param v the Vertex v
     * @return the Edge or null
     */
    public Edge hasInEdgeFrom(Vertex v)
    {   Edge result = null;
        for (int i = 0; i < inEdges.size(); i++)
        {   Edge ie = (Edge) inEdges.elementAt(i);
            if (ie.fromVertex == v)
                result = ie;
        }
        return result;
    }

    /**
     * check if this vertex has an outgoing edge
     * connecting it with Vertex v
     * @param v the Vertex v
     * @return the Edge or null
     */
    public Edge hasOutEdgeTo(Vertex v)
    {   Edge result = null;
        for (int i = 0; i < outEdges.size(); i++)
        {   Edge ie = (Edge) outEdges.elementAt(i);
            if (ie.toVertex == v)
                result = ie;
        }
        return result;
    }
    
    /**
     * paint the vertex and its button(s) using a Context2d 
     * @param g the Context2d
     */
    public void paintComponent(Context2d g)
    {   
    	//non-roots have a yellowish background
    	if (!root)
    	{	g.setFillStyle(CssColor.make(255, 255, 150));
    		g.fillRect(xPos, yPos + DrawingPanel.labelHeight, breedte, hoogte - DrawingPanel.labelHeight);
    	}	
    	// black outline
    	g.setStrokeStyle(CssColor.make(0,0,0));
    	g.strokeRect(xPos, yPos + DrawingPanel.labelHeight, breedte, hoogte - DrawingPanel.labelHeight);
    	// paint the arrow buttons
    	if (addEdgeButton != null)
    		addEdgeButton.paintComponent(g);
    	if (colorButton != null)
    		colorButton.paintComponent(g);
        if (hasLabel)
        {  	// label has a red outline
        	g.setStrokeStyle(CssColor.make(255,0,0));        	
        	g.strokeRect(xPos, yPos, breedte, DrawingPanel.LABELHEIGHT);
        	// black label text
        	g.setFillStyle(CssColor.make(0,0,0));
        	g.fillText(labelText, xPos + 2, yPos + 16, breedte - 4);
        }
        // paint the flow text in black
        g.setStrokeStyle(CssColor.make(0,0,0));
		g.setFont(fontString);
		int maxTextWidth = 0; 
		if (root)
		{	maxTextWidth = breedte - addEdgeButton.breedte - 2;
			g.fillText(flowText, xPos + 2, yPos + DrawingPanel.labelHeight + 16, maxTextWidth);
		}
		else
		{	maxTextWidth = breedte - addEdgeButton.breedte - colorButton.breedte - 2;
			g.fillText(flowText, xPos + colorButton.breedte + 2, yPos + DrawingPanel.labelHeight + 16, maxTextWidth);
		}
    }    

    /**
     * process the fraction in String t, if there is
     * no error, set the flow in this vertex to this fraction 
     * @param t String containing the fraction 
     */
    public void processRational(String t)
    {   
    	boolean error = false;
        int nom = 0, denom = 1;
        String nomStr = null, denomStr = null;
        Rational value;
        // error handling here
        try
        {   t = removeAllBlanks(t);
            int slash = t.indexOf('/');
            if (slash >= 0)
            {   
                nomStr = t.substring(0, slash);
                if (slash == (t.length() - 1))
                {   error = true;
                    // reset
                    setFlow(flow); 
                }
                else
                    denomStr = t.substring(slash + 1);
            }
            else
            {   nomStr = t; 
                denomStr = "1";
            }
            if (!error)    
            {   // these lines generate exceptions and activate catch
            	// this interceps double slash
                nom = Integer.parseInt(nomStr);
                denom = Integer.parseInt(denomStr);
            }    
        } // try
        catch (NumberFormatException nfe)
        {   error = true;
            // reset
            setFlow(flow); 
        }  // catch
        if (!error)
        {   value = new Rational(nom, denom);
            if (value.decVal < 0)
                setFlow(flow); // reset
            else
            {   setDecimals(value.decVal);
                boolean remember = !value.equals(flow);
                setFlow(value);
                DrawingPanel.diagramManager.calculateDiagram();
                if (remember)
                    DrawingPanel.addToHistory();
            }
        }
    }    

    /**
     * process the decimal number in String t, if there is
     * no error, set the flow in this vertex to this decimal 
     * @param t String containing the decimal number 
     */
    public void processDecimal(String t)
    {   
    	boolean error = false;
        double value = 0;
        // error handling here
        try
        {   // change decimal separator to "."
        	
            if (StroomDiagrammenGWT.rb.getString("decSep") == ",")
                t = t.replace(',', '.');
            // note:  "." is always allowed
            // check for double ..
            int k = 0;
            int j = t.indexOf('.');
            // only if there is a .
            if (j >= 0)
            {   k = t.lastIndexOf('.');
                if (j != k)
                {   error = true;
                    // reset
                    setFlow(flow);
                }
            }
            if (!error)
            {   // this line generates exception and activates catch
                value = (Double.valueOf(t)).doubleValue();
            }
        } // try
        catch (NumberFormatException nfe)
        {   error = true;
            // reset
            setFlow(flow);
        }  
        if (!error)
        {   if (value < 0)
                setFlow(flow); // reset
            else
            {   setDecimals(value);
                boolean remember = (value != flow.decVal);            
                setFlow(new Rational(value, decimals));
                DrawingPanel.diagramManager.calculateDiagram();
                if (remember)
                    DrawingPanel.addToHistory();                
            }
        }
    }    
  
    /**
     * process the String entered in the VertexPopup, find out if it
     * is empty, if it is a fraction, percentage or decimal number
     * @param t String entered 
     */
    public void processInput(String t)
    {   
    	boolean error = false;
        double value = 0;
        // input empty, flow was erased
        if (t.equals(""))
        {   boolean remember = !flow.isUndefined();
            setFlow(DrawingPanel.unDef);
            DrawingPanel.diagramManager.calculateDiagram();
            if (remember)
                DrawingPanel.addToHistory();
        }
        else // input some nonempty String
        {   if (StroomDiagrammenGWT.rb.getString("decSep") == ",")
                t = t.replace(',', '.');
            int mode = DrawingPanel.flowMode;
            int divIndex = t.indexOf('/');
            int decIndex = t.indexOf('.');
            // no slash and point: t is an integer as String 
            if ((divIndex < 0) && (decIndex < 0))
                processRational(t);
            // s;ash and point: error
            if ((divIndex >= 0) && (decIndex >= 0))
            	setFlow(flow); // reset
            // point, no slash: decimal
            if ((decIndex >= 0) && (divIndex < 0))
            	processDecimal(t);
            // slash, no point: fraction
           	if ((divIndex >= 0) && (decIndex < 0))
           		processRational(t);	
        }
    }    

    /**
     * remove all blanks in the String s
     * @param s String s
     * @return s with blanks removed
     */
    public String removeAllBlanks(String s)
    {   int index = s.indexOf(' ');
        while (index >= 0)
        {   s = s.substring(0, index) + s.substring(index + 1);
            index = s.indexOf(' ');
        }
        return s;
    }

    /**
     * set the number of decimals for displaying
     * the flow in this vertex in an ad hoc way
     * @param value the flow
     */
    public void setDecimals(double value)
    {   int decs;
        if (value >= 100)
            decs = 0;
        else if ((value >= 10) && (value < 100))
            decs = 1;
        else if ((value > 1) && (value < 10))
            decs = 2;
        else
            decs = 3;
        // do not forget this one
        decimals = decs;
        DrawingPanel.diagramManager.setDecimals(decs);                            
    }                
}
