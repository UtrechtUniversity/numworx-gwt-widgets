package fi.stroomdiagrammengwt.client;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class representing an edge in the flow diagram; <br>
 * each edge has a capacity, a number between 0 and 1; <br>
 * the sum of the capacities of the edges out of a vertex equals 1; <br>
 * the capacity is shown in the capacity field, a box drawn on the edge, see 
 * class CapacityField, and can be changed by doubleClick or longClick on the capacity field;<br> 
 * when changing the capacity of an edge, the sum the sum of the capacities from
 * all edges out of fromVertex of that edge is maintained at 1 by altering the 
 * capacity of the out edge that was set the longest time ago; <br>
 * the user can enter the capacity in another format then the global format in which case  
 * all the capacities of the edges out of fromVertex are displayed in this local 
 * format, e.g. one can enter a fraction while the global format is a decimal number; <br> 
 * the edge can be deleted by longClick on the edge polygon outside the capacity field and this is allowed if:<br>
 * 1) the toVertex of this edge has other incoming edges than this edge<br>
 * 2) the toVertex of this edge has only this edge as incoming edge and
 * has no outgoing edges; in this case the toVertex is also deleted;<br> 
 * see class DrawingPanel for Mouse/Touch action and class EdgePopup for changing the capacity;<br> 
 * capacities can be entered as decimal fractions, fractions or percentages.  
 */

public class Edge 
{
	/**
	 * class owning the edge
	 */
	DrawingPanel owner;
	/**
	 * the edge starts at fromVertex
	 */
    Vertex fromVertex;
    /**
     * the edge ends at toVertex
     */
    Vertex toVertex;
    /**
     * can the capacity of the edge be changed?
     */
    boolean frozen = false;
    /**
     * a class drawing a box showing the capacity of the edge   
     */
    CapacityPanel capacityField;
    /**
     * top right point of the fromVertex (not including label)
     */
    Point edgeStart = new Point();
    /**
     * top left point of the toVertex (not including label)
     */
    Point edgeEnd = new Point();
    /**
     * arc tangens of (edgeEnd.y-edgeStart.y)/(edgeEnd.x-edgeStart.x)
     */
    double alpha;
    /**
     * required thickness of the edge (perpendicular to the edge 
     */
    double thickness;
    /** 
     * corrected thickness of the edge (pixels)
     */
    int corrThickness;
    /**
     * the offset of the Edge from edgeStart.y (pixels)
     */
    int vOffSet;
    /**
     * vertical thickness of the edge (pixels)
     */
    int vThickness;
    /**
     * the polygon for drawing the edge
     */
    Polygon p;
    /**
     * the polygon for clicking on the edge, larger than 
     * the edge polygon when      * the edge polygon is small
     */
    Polygon cp;
    /**
     * is the edge highlighted for backwards tracing?
     * see class DrawingPanel
     */
    boolean highlighted = false;
    /**
     * the number of colors to simulate waves
     */
    public static int numWaveColors = 4;
    /**
     * the index of the color of the first wave
     */
    int waveStep = randomInteger(0, numWaveColors);
    /**
     * Date instance for fixing time of capacity change
     */
    Date date;
    /**
     * the capicty of this edge
     */
    Rational capacity;
    /**
     * the time the capacity of this edge was last changed
     */
    long lastTimeChanged;
    /**
     * the display mode of this edge (decimal number/fraction)
     * see class DrawingPanel
     */
    int mode = DrawingPanel.decMode;
    /**
     * the mode of displaying the Edge thickness
     * see class DrawingPanel
     */
    int thickMode = DrawingPanel.relMode;
    /**
     * constructor
     * @param o DrawingPanle class owning the edge
     * @param from the fromVertex of the edge
     * @param to the toVertex of the edge
     * @param c the capacity of the edge
     */
    public Edge(DrawingPanel o, Vertex from, Vertex to, Rational c)
    {   owner = o;
        fromVertex = from;
        toVertex = to;
        capacity = new Rational(c);
        capacityField = new CapacityPanel(DrawingPanel.edgeNumberWidth, DrawingPanel.edgeNumberHeight);
        capacityField.setText(UF.format(capacity.decVal, DrawingPanel.capDecs));
        from.outEdges.addElement(this);
        to.inEdges.addElement(this);
        thickMode = owner.thickMode;
    }
    
    /**
     * set flagg indicating of capacity field can be accessed
     * @param b true/false
     */
    public void setFrozen(boolean b)
    {
    	frozen = b;
    }
    
    /**
     * check if the capacity field contains the point (x,y)
     * @param x x coordinate in pixels of point clicked
     * @param y y coordinate in pixels of point clicked
     * @return true/false
     */
	public boolean capacityClicked(int x, int y)
	{
		if (frozen)
			return false;
		else
			return capacityField.capacityClicked(x, y);
	}

	/**
	 * set the capacity to the Rational c and update lastTimeChanged
	 * if newTime == true 
	 * @param c the Rational c
	 * @param newTime true/false
	 */
    public void setCapacity(Rational c, boolean newTime)
    {   capacity = new Rational(c);
    	// format capacityField depending on mode
        if (mode == DrawingPanel.decMode)
            capacityField.setText(UF.format(capacity.decVal, DrawingPanel.capDecs));      
        else if (mode == DrawingPanel.percMode)    
            capacityField.setText(UF.format(capacity.decVal * 100, 0) + "%");              
        else  // mode = DrawingPanel.fracMode  
        {   if (capacity.isInteger())
                capacityField.setText(UF.format(capacity.nom, 0));        
            else
                capacityField.setText(UF.format(capacity.nom, 0) + "/" +
                                      UF.format(capacity.denom, 0));  
        }
        // update                         
        if (newTime)
        {   date = new Date();
            lastTimeChanged = date.getTime();
        }
        // set thickness 
        setThickness();
        owner.paint();
        // no updates here, infinite loop!
    }    

    /**
     * set the display mode for the capacity of this Edge
     * @param m the required display mode (see class DrawingPanel)
     */
    public void setMode(int m)
    {   mode = m;
        if (mode == DrawingPanel.decMode)
            capacityField.setText(UF.format(capacity.decVal, DrawingPanel.capDecs));      
        else if (mode == DrawingPanel.percMode)    
            capacityField.setText(UF.format(capacity.decVal * 100, 0) + "%");              
        else  // mode = DrawingPanel.fracMode  
        {   if (capacity.isInteger())
                capacityField.setText(UF.format(capacity.nom, 0));        
            else
                capacityField.setText(UF.format(capacity.nom, 0) + "/" +
                                      UF.format(capacity.denom, 0));  
        }
    }    
    
    /**
     * set parameters of this Edge, used when creating and redrawing
     */
    public void setEdge()
    {   edgeStart.x = fromVertex.xPos + DrawingPanel.vertexWidth;
        edgeStart.y = fromVertex.yPos + DrawingPanel.labelHeight;
        edgeEnd.x = toVertex.xPos;
        edgeEnd.y = toVertex.yPos + DrawingPanel.labelHeight;
        alpha = Math.atan(((double) (edgeEnd.y - edgeStart.y)) / (edgeEnd.x - edgeStart.x));
        setThickness();
    }    
    
    /**
     * set the thickness mode of this Edge
     * @param tMode the required thichkness mode (see class DrawingPanel)
     */
    public void setThicknessMode(int tMode)
    {   thickMode = tMode;
        setThickness();
    }    
    
    /**
     * determine the requires thickness of this edge depending
     * on the global thickness mode (class DrawingPanel)
     */
    public void setThickness()
    {  	// relative thickness is proportional to to capacity
        if (thickMode == DrawingPanel.relMode)
            thickness = DrawingPanel.vertexHeight * capacity.decVal;        
        else // absolute thiskness
        {   // find flow in all roots connected to fromVertex
            Rational sFlow = owner.getSourceFlow(fromVertex);
            // this includes unDefined
            if (sFlow.decVal <= 1e-6d)
                thickness = 0;
            else    
                thickness = DrawingPanel.vertexHeight * 
                            capacity.decVal *
                            (fromVertex.flow.decVal / sFlow.decVal);        
        }    
    }
    
    /**
     * generate a random integer between
     * @param min min value and
     * @param max max value
     * @return random integer
     */
    public int randomInteger(int min, int max)
    {   double num = min + Math.random() * (max - min);
        // cast double to int
        return (int) Math.round(num);
    }
    
    /**
     * draw the edge using Context2d g
     * NB the thickness of all edges out of fromVertex MUST have been
     * set before drawing; drawing the edge sets the click polygon
     * @param g the Context2d to be used
     */
    public void drawEdge(Context2d g)
    {   // thickness of all relevant edges MUST have been set
        // before drawing    
        
        // vertical thickness is thickness / cos(alpha)
        // but these do not sum to DrawingPanel.vertexHeight anymore
        // so find totalThickness and rescale
        double dVOffSet = 0;
        double totalThickness = 0;
        boolean found = false;
        for (int i = 0; i < fromVertex.outEdges.size(); i++)
        {   Edge oe = (Edge) fromVertex.outEdges.elementAt(i);
            totalThickness += (oe.thickness / Math.cos(oe.alpha)); 
            if (oe == this)
                found = true;
            if (!found)
            {    dVOffSet += (oe.thickness / Math.cos(oe.alpha));
            }    
        }    
        // vertical offSet of this edge
        if ((thickMode == DrawingPanel.relMode) ||
            (totalThickness > DrawingPanel.vertexHeight))
            vOffSet = (int) Math.round(dVOffSet * 
                            DrawingPanel.vertexHeight / totalThickness);        
        else 
        {    vOffSet = (int) Math.round(dVOffSet + 
                             (DrawingPanel.vertexHeight - totalThickness) / 2);        
        }                    
        // horizontal offset of center of capacity field
        int hOffSet = Math.max((edgeEnd.x - edgeStart.x) / 4, capacityField.breedte / 2); 
        int lTop = 0, rTop = 0;
        double tan = 0;
        if ((capacity.decVal == 0) || (thickness == 0))
        {   g.setFillStyle(StroomDiagrammenGWT.zeroEdgeColor);                 
            // no vThickness
            lTop = edgeStart.y + vOffSet;
            // endPoint at edgeEnd.y + DrawingPanel.VertexHeight / 2
            rTop = edgeEnd.y + DrawingPanel.vertexHeight / 2;
            tan = ((double) (rTop - lTop)) / (edgeEnd.x - edgeStart.x);           
            int nPoints = 4;
            int[] xPoints = new int[nPoints];
            int[] yPoints = new int[nPoints];
            int[] cyPoints = new int[nPoints];
            // clockwise: start at left bottom
            xPoints[0] = edgeStart.x;
            xPoints[1] = edgeStart.x;
            xPoints[2] = edgeEnd.x;
            xPoints[3] = edgeEnd.x;
            yPoints[0] = edgeStart.y + vOffSet + 1;
            yPoints[1] = edgeStart.y + vOffSet;            
            // middle of flow end in middle of 
            // to vertex
            yPoints[2] = edgeEnd.y + (DrawingPanel.vertexHeight - 1) / 2;
            yPoints[3] = edgeEnd.y + (DrawingPanel.vertexHeight + 1) / 2;            
            p = new Polygon(xPoints, yPoints, nPoints);
            g.beginPath();
            g.moveTo(p.geefPuntXD(0), p.geefPuntYD(0));
            for (int i = 1; i < p.aantalPunten; i++)
            {  	g.lineTo(p.geefPuntXD(i), p.geefPuntYD(i));
            }
            g.lineTo(p.geefPuntXD(0), p.geefPuntYD(0));
            g.closePath();
            g.fill();
            // create the click polygon
            cyPoints[0] = yPoints[0] + 4;
            cyPoints[1] = yPoints[1] - 4;            
            cyPoints[2] = yPoints[2] - 4;
            cyPoints[3] = yPoints[3] + 4;            
            cp = new Polygon(xPoints, cyPoints, nPoints);            
            capacityField.setLocation(edgeStart.x + hOffSet - capacityField.breedte / 2, 
                lTop + (int) Math.round(hOffSet * tan) - capacityField.hoogte / 2);
            capacityField.paintComponent(g);
        }    
        else // positive capacity/thickness   
        {   if (highlighted)
                g.setFillStyle(StroomDiagrammenGWT.highEdgeColor);                         
            else
                g.setFillStyle(StroomDiagrammenGWT.edgeColor);                 
            if ((thickMode == DrawingPanel.relMode) ||
                (totalThickness > DrawingPanel.vertexHeight))
                vThickness = (int) Math.round((thickness / Math.cos(alpha)) * 
                                       DrawingPanel.vertexHeight / totalThickness);
            else
            {    vThickness = (int) Math.round((thickness / Math.cos(alpha)));// * 
            }
            corrThickness = (int) Math.round(vThickness * Math.cos(alpha));            
            lTop = edgeStart.y + vOffSet;
            rTop = edgeEnd.y + (DrawingPanel.vertexHeight - vThickness) / 2;        
            tan = ((double) (rTop - lTop)) / (edgeEnd.x - edgeStart.x);                       
            int nPoints = 4;
            int[] xPoints = new int[nPoints];
            int[] yPoints = new int[nPoints];
            int[] cyPoints = new int[nPoints];
            // clockwise: start at left bottom
            xPoints[0] = edgeStart.x;
            xPoints[1] = edgeStart.x;
            xPoints[2] = edgeEnd.x;
            xPoints[3] = edgeEnd.x;
            yPoints[0] = edgeStart.y + vOffSet + vThickness;
            yPoints[1] = edgeStart.y + vOffSet;            
            // middle of flow end in middle of 
            // to vertex
            yPoints[2] = edgeEnd.y + (DrawingPanel.vertexHeight - vThickness) / 2;
            yPoints[3] = edgeEnd.y + (DrawingPanel.vertexHeight + vThickness) / 2;            
            p = new Polygon(xPoints, yPoints, nPoints);
            g.beginPath();
            g.moveTo(p.geefPuntXD(0), p.geefPuntYD(0));
            for (int i = 1; i < p.aantalPunten; i++)
            {  	g.lineTo(p.geefPuntXD(i), p.geefPuntYD(i));
            }
            g.lineTo(p.geefPuntXD(0), p.geefPuntYD(0));
            g.closePath();
            g.fill();
            // create the click polygon
            if (vThickness < 9)
            {   int shift = (9 - vThickness) / 2 + 1;
                cyPoints[0] = yPoints[0] + shift;
                cyPoints[1] = yPoints[1] - shift;            
                cyPoints[2] = yPoints[2] - shift;
                cyPoints[3] = yPoints[3] + shift;            
                cp = new Polygon(xPoints, cyPoints, nPoints);            
            }    
            else
                cp = new Polygon(xPoints, yPoints, nPoints);            
            capacityField.setLocation(edgeStart.x + hOffSet - capacityField.breedte / 2, 
            		lTop + (int) Math.round(hOffSet * tan) + vThickness / 2 - capacityField.hoogte / 2);
            drawWavesAt(g, waveStep);           
            // "smaller" polygon for outline    
            xPoints[0]--;
            xPoints[1]--;
            yPoints[0]--;
            yPoints[3]--;
            Polygon q = new Polygon(xPoints, yPoints, nPoints);
            g.setStrokeStyle(CssColor.make(0,0,0));
            g.beginPath();
            g.moveTo(q.geefPuntXD(0), q.geefPuntYD(0));
            for (int i = 1; i < q.aantalPunten; i++)
            {  	g.lineTo(q.geefPuntXD(i), q.geefPuntYD(i));
            }
            g.lineTo(q.geefPuntXD(0), q.geefPuntYD(0));
            g.closePath();
            g.stroke();
            capacityField.paintComponent(g);
        }
    }    
    
    /**
     * draw waves in the edge, that is subdivide the edge polygon into
     * smaller polygons which have different wave colors
     * @param g Context2d to use for drawing
     * @param step index of first wave color
     */
    public void drawWavesAt(Context2d g, int step)
    {   if (vThickness < 1)
            return; // no waves
        // left top of edge polygon
        int lTop = edgeStart.y + vOffSet;
        // right top of edge polygon
        int rTop = edgeEnd.y + (DrawingPanel.vertexHeight - vThickness) / 2;        
        double tan = ((double) (rTop - lTop)) /
                     (edgeEnd.x - edgeStart.x);           
        double atan = Math.atan(tan);             
        double waveXThickness = 5; // pixels
        double waveYThickness = Math.abs(tan) * waveXThickness;
        int numWaves = (int) Math.round( ((double) (edgeEnd.x - edgeStart.x)) / waveXThickness) + 1;
        Vector<Polygon> waves = new Vector<Polygon>();        
        int nPoints;
        int[] xPoints;
        int[] yPoints;
        Polygon w;
        // contruct the wave polygons
        for (int cnt = 0; cnt < numWaves; cnt++)
        {   nPoints = 4;
            xPoints = new int[4];
            yPoints = new int[4];
            if (atan >= 0)
            {    
                xPoints[0] = edgeStart.x + (int) Math.round(
                             cnt * waveXThickness
                             );
                xPoints[1] = edgeStart.x + (int) Math.round(
                             (cnt + 1) * waveXThickness
                             );
                xPoints[2] = edgeStart.x + (int) Math.round(
                             (cnt + 1) * waveXThickness - 
                             vThickness * Math.cos(atan) * Math.sin(atan)
                             );
                xPoints[3] = edgeStart.x + (int) Math.round(
                             cnt * waveXThickness -
                             vThickness * Math.cos(atan) * Math.sin(atan)                             
                             );
                
                yPoints[0] = lTop + (int) Math.round(
                             cnt * waveYThickness
                             );
                yPoints[1] = lTop + (int) Math.round(
                             (cnt + 1) * waveYThickness
                             );
                yPoints[2] = lTop + (int) Math.round(
                             (cnt + 1) * waveYThickness + 
                             vThickness * Math.cos(atan) * Math.cos(atan)                             
                             );
                yPoints[3] = lTop + (int) Math.round(
                             cnt * waveYThickness +
                             vThickness * Math.cos(atan) * Math.cos(atan)
                             );
            }
            else //if (atan < 0
            {
                xPoints[0] = edgeStart.x + (int) Math.round(
                             cnt * waveXThickness
                             );
                xPoints[1] = edgeStart.x + (int) Math.round(
                             cnt * waveXThickness +   
                             vThickness * Math.cos(- atan) * Math.sin(- atan)
                             );
                xPoints[3] = edgeStart.x + (int) Math.round(
                             (cnt - 1) * waveXThickness
                             );
                xPoints[2] = edgeStart.x + (int) Math.round(
                             (cnt - 1) * waveXThickness +
                             vThickness * Math.cos(- atan) * Math.sin( - atan)                             
                             );              
                
                
                yPoints[0] = lTop - (int) Math.round(
                             cnt * waveYThickness
                             );
                yPoints[1] = lTop - (int) Math.round(
                             cnt * waveYThickness - 
                             vThickness * Math.cos(atan) * Math.cos(atan)                                                          
                             );
                yPoints[3] = lTop - (int) Math.round(
                             (cnt - 1) * waveYThickness
                             );
                yPoints[2] = lTop - (int) Math.round(
                             (cnt - 1) * waveYThickness -
                             vThickness * Math.cos(atan) * Math.cos(atan)                                                          
                             );
                
            }
            w = new Polygon(xPoints, yPoints, nPoints);
            waves.addElement(w);
        }
        CssColor[] colors = new CssColor[numWaveColors];
        if (highlighted)
        {
        	colors[0] = StroomDiagrammenGWT.highBubbleColor1;
        	colors[1] = StroomDiagrammenGWT.highBubbleColor2;
        	colors[2] = StroomDiagrammenGWT.highBubbleColor1;
        	colors[3] = StroomDiagrammenGWT.highBubbleColor;        
            
        }
        else
        {
        	colors[0] = StroomDiagrammenGWT.bubbleColor1;
        	colors[1] = StroomDiagrammenGWT.bubbleColor2;
        	colors[2] = StroomDiagrammenGWT.bubbleColor1;
        	colors[3] = StroomDiagrammenGWT.bubbleColor;        
        }
        // draw the wave polygons
        for (int i = 0; i < waves.size(); i++)
        {  	g.setFillStyle(colors[(i + step) % 4]);
            Polygon pw = (Polygon) waves.elementAt(i);    
            //g.fillPolygon(pw);
            g.beginPath();
            g.moveTo(pw.geefPuntXD(0), pw.geefPuntYD(0));
            for (int j = 1; j < pw.aantalPunten; j++)
            {  	g.lineTo(pw.geefPuntXD(j), pw.geefPuntYD(j));
            }
            g.lineTo(pw.geefPuntXD(0), pw.geefPuntYD(0));
            g.closePath();
            g.fill();
        }    
    }
    
    /**
     * process String t which might contain a decimal point
     * and/or a percentage sign % and set the capacity of
     * this Edge to this value if no error
     * @param t the String to process
     * @param percentage percentage or not?
     */
    public void processDouble(String t, boolean percentage)
    {   
    	boolean error = false;
        double value = 0;
        // error handling here
        try
        {   if (percentage)
            {   int pc = t.indexOf('%');
                if (pc >= 0)
                    t = t.substring(0, pc);
            }
            // change decimal separator to "."
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
                    setCapacity(capacity, false); 
                }
            } // if (j >= 0)
            if (!error)
            {   // this line generates exception and activates catch
                value = (Double.valueOf(t)).doubleValue();
            }                        
        } // try
        catch (NumberFormatException nfe)
        {   error = true;
            // reset
            setCapacity(capacity, false); 
        }  // catch
        if (!error)
        {   if (percentage)
                value /= 100;
        	// value must be between 0 and 1
            if ((value >= 0) && (value <= 1))
            {   // determine new local flow format
            	int newMode = DrawingPanel.flowMode;
                if (percentage)
                   newMode = DrawingPanel.percMode;
                else
                   newMode = DrawingPanel.decMode;
            	// value or format changed, so add to history
                boolean remember = (value != capacity.decVal) || (mode != newMode);
                setCapacity(new Rational(value), true);
                // make sure the capacities of all edges out of fromVertex sum to 1
                fromVertex.updateOutCapacities(Edge.this, newMode);
                // include this one in new format
                fromVertex.setOutModes(newMode);
                DrawingPanel.diagramManager.calculateDiagram();
                if (remember)
                {   DrawingPanel.addToHistory();
                }
            }
            else // reset
                setCapacity(capacity, false);
        }
    }    
    
    /**
     * process String t which contains a fraction and set the 
     * capacity of this Edge to this fraction if no error
     * @param t the String to be processed
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
            nomStr = t.substring(0, slash);
            // empty denomStr
            if (slash == (t.length() - 1))
            {   error = true;
                // reset
                setCapacity(capacity, false); 
            }
            else
                denomStr = t.substring(slash + 1);
            if (!error)    
            {   // these lines generate exceptions and activate catch
                nom = Integer.parseInt(nomStr);
                denom = Integer.parseInt(denomStr);
            }    
        } // try
        catch (NumberFormatException nfe)
        {   error = true;
            // reset
            setCapacity(capacity, false); 
        }  // catch
        if (!error)
        {   value = new Rational(nom, denom);
        	// new local format will be fractions
            int newMode = DrawingPanel.fracMode;
            // we must have 0 <= value <= 1 
            if (value.isLargerOrEqual(new Rational(0, 1, 0), newMode) && 
                value.isSmallerOrEqual(new Rational(1, 1, 1), newMode))
            {   // value or format changed, so add to history
            	boolean remember = !capacity.equals(value) || (mode != newMode);
                setCapacity(value, true);
             // make sure the capacities of all edges out of fromVertex sum to 1
                fromVertex.updateOutCapacities(Edge.this, newMode);
                // includes this one in the new format
                fromVertex.setOutModes(newMode);                
                DrawingPanel.diagramManager.calculateDiagram();
                if (remember)
                {   DrawingPanel.addToHistory();
                }
                
            }
            else // reset
                setCapacity(capacity, false);
        }
    }    

    /**
     * process the String entered in the EdgePopup, find out if it
     * is a fraction, percentage or decimal number
     * @param t String entered 
     */
    public void processInput(String t)
    {   
        int divIndex = t.indexOf('/');
        if (divIndex >= 0)
            processRational(t);
        else
        {   int percIndex = t.indexOf('%');
            if (percIndex >= 0)
                processDouble(t, true);   
            else
                processDouble(t, false);
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
}

