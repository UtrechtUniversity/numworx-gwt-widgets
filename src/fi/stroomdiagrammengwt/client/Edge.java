package fi.stroomdiagrammengwt.client;

//import java.awt.Point;
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.Polygon;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusEvent;
//import java.awt.event.FocusListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class Edge 
{
	DrawingPanel owner;
    Vertex fromVertex, toVertex;
    
    boolean frozen = false;

    
    CapacityPanel capacityField;
    
    Point edgeStart = new Point(), edgeEnd = new Point();
    // angle (edgeEnd.y - edgeStart.y) / (edgeEnd.x - edgeStart.x)
    double alpha;
    // required thickness (perpendicular to flow)
    // will be corrected later
    double thickness;
    // corrected thickness (pixels) perpendicular to flow
    int corrThickness;
    // offSet from edgeStart.y (pixels)
    int vOffSet;
    // vertical thickness (pixels)
    int vThickness;
    // edge polygon, click polygon
    Polygon p, cp;
    // highlighting
    boolean highlighted = false;
    // bubbles
    int bOffMaxInit = 30;
    int bOffMax = bOffMaxInit;
    int bOffStep = 5;
    
    // offset for bubbles
    int bOffSet = randomInteger(1, bOffMaxInit);
    public static int numWaveColors = 4;
    int waveStep = randomInteger(0, numWaveColors);
    
    // Date instance for fixing time
    Date date;
    //arrows om LWTextField langs pijl te schuiven??
    //hoe??
    //paint
    //teken pijl
    Rational capacity;
    long lastTimeChanged;
    int mode = DrawingPanel.decMode;
    int thickMode = DrawingPanel.relMode;
    public Edge(DrawingPanel o, Vertex from, Vertex to, Rational c)
    {   owner = o;
        fromVertex = from;
        toVertex = to;
        capacity = new Rational(c);

        
        capacityField = new CapacityPanel(DrawingPanel.edgeNumberWidth, DrawingPanel.edgeNumberHeight);
        
        
        capacityField.setText(UF.format(capacity.decVal, DrawingPanel.capDecs));

//GWT        
        //EdgeIAL listener = new EdgeIAL();
        //capacityField.capacityTextField.addActionListener(listener);
        //capacityField.capacityTextField.addFocusListener(listener);
        //capacityField.addMouseListener(new DelEdgeML());
        
        from.outEdges.addElement(this);
        to.inEdges.addElement(this);
        thickMode = owner.thickMode;
//System.out.println("edge thickMode = " + thickMode);        

    }
    public void setFrozen(boolean b)
    {
    	frozen = b;

//GWT    	
    	//capacityField.capacityTextField.setEditable(!frozen);
    }
    public void setCapacity(Rational c, boolean newTime)
    {   capacity = new Rational(c);
    

    
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
                                 
        if (newTime)
        {   date = new Date();
            lastTimeChanged = date.getTime();
        }
        // thickness 
        setThickness();
        //thickness = DrawingPanel.vertexHeight * capacity.decVal;        
        owner.paint();
        // no updates here, infinite loop!
    }    

    public void setMode(int m)
    {   mode = m;
    
//GWT
/*    
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
*/                                  
    }    
    
    // used when creating, redrawing
    public void setEdge()
    {   //edgeStart.x = fromVertex.getLocation().x + owner.vertexWidth;
        //edgeStart.y = fromVertex.getLocation().y + owner.labelHeight;
        //edgeEnd.x = toVertex.getLocation().x;
        //edgeEnd.y = toVertex.getLocation().y + owner.labelHeight;
        edgeStart.x = fromVertex.xPos + owner.vertexWidth;
        edgeStart.y = fromVertex.yPos + owner.labelHeight;
        edgeEnd.x = toVertex.xPos;
        edgeEnd.y = toVertex.yPos + owner.labelHeight;
        alpha = Math.atan(((double) (edgeEnd.y - edgeStart.y)) / (edgeEnd.x - edgeStart.x));
        // thickness
        setThickness();
        //thickness = DrawingPanel.vertexHeight * capacity.decVal;
        
    }    
    
    public void setThicknessMode(int tMode)
    {   thickMode = tMode;
        setThickness();
    }    
    
    // determine required thickness
    public void setThickness()
    {   
    	
//System.out.println("edge setThickness " + thickMode);    	
    	
    	// relative to capacity
        if (thickMode == DrawingPanel.relMode)
            thickness = DrawingPanel.vertexHeight * capacity.decVal;        
        else // absolute
        {   // max of flow in all roots
            Rational sFlow = owner.getMaxRootFlow();
            // if not all roots filled in
            // finds maximum of roots connected to fromVertex
            if (sFlow.isUndefined())
                sFlow = owner.getSourceFlow(fromVertex);
            if (sFlow.decVal <= 1e-6d)
                thickness = 0;
            else    
                thickness = DrawingPanel.vertexHeight * 
                            capacity.decVal *
                            (fromVertex.flow.decVal / sFlow.decVal);        
        }    
    }
    
    // generate a random integer between min and max
    public int randomInteger(int min, int max)
    {   double num = min + Math.random() * (max - min);
        // cast long to int
        return (int) Math.round(num);
    }
    
    //public void drawEdge(Graphics g)
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
            // draw line
//            g.drawLine(edgeStart.x, lTop, edgeEnd.x, rTop);
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
            //g.fillPolygon(p);
            g.beginPath();
            g.moveTo(p.geefPuntXD(0), p.geefPuntYD(0));
            for (int i = 1; i < p.aantalPunten; i++)
            {  	g.lineTo(p.geefPuntXD(i), p.geefPuntYD(i));
            }
            g.lineTo(p.geefPuntXD(0), p.geefPuntYD(0));
            g.closePath();
            g.fill();
            
            cyPoints[0] = yPoints[0] + 4;
            cyPoints[1] = yPoints[1] - 4;            
            cyPoints[2] = yPoints[2] - 4;
            cyPoints[3] = yPoints[3] + 4;            
            cp = new Polygon(xPoints, cyPoints, nPoints);            
            
//g.drawPolygon(cp);
            

            
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
            //g.fillPolygon(p);
            g.beginPath();
            g.moveTo(p.geefPuntXD(0), p.geefPuntYD(0));
            for (int i = 1; i < p.aantalPunten; i++)
            {  	g.lineTo(p.geefPuntXD(i), p.geefPuntYD(i));
            }
            g.lineTo(p.geefPuntXD(0), p.geefPuntYD(0));
            g.closePath();
            g.fill();

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
                

/*
            // simulation
            // distance between bubbles
            double frac = ((double) (edgeEnd.x - edgeStart.x)) / owner.maxLayerDistance;
            bOffMax = (int) Math.round(Math.min(1, frac) * bOffMaxInit);
            int steps = (int) Math.round(frac * 4);
            int step = (edgeEnd.x - edgeStart.x) / steps;
            int numBubbles = (int) Math.round(frac * 5);
//            for (int i = 0; i < numBubbles; i++)
//                drawBubbleAt(g, bOffSet + i * (step - 1));
*/

//GWT            
            drawWavesAt(g, waveStep);           
                       
            // "smaller" polygon for outline    
            xPoints[0]--;
            xPoints[1]--;
            yPoints[0]--;
            yPoints[3]--;
            Polygon q = new Polygon(xPoints, yPoints, nPoints);
            g.setStrokeStyle(CssColor.make(0,0,0));
            //g.drawPolygon(q);
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

    
//GWT
/*    
    public void drawBubbleAt(Context2d g, int step)
    {   if (highlighted)
            g.setColor(StroomDiagrammenGWT.highBubbleColor);                
        else
            g.setColor(StroomDiagrammenGWT.bubbleColor);            
        // left top of polygon
        int lTop = edgeStart.y + vOffSet;
        // right top of polygon
        // middle of flow end in middle of right vertex
        int rTop = edgeEnd.y + (DrawingPanel.vertexHeight - vThickness) / 2;        
        double tan = ((double) (rTop - lTop)) / (edgeEnd.x - edgeStart.x);           
        int stepy = (int) Math.round(step * tan);
        int cx = edgeStart.x + step;            
        int cy = lTop + stepy + vThickness / 2;
        if (cx <= edgeEnd.x)
        {   if (highlighted)
                g.setColor(DrawingPanel.hsbChange(Stroomdiagrammen.highBubbleColor, 2));        
            else
                g.setColor(DrawingPanel.hsbChange(Stroomdiagrammen.bubbleColor, 2));
            g.fillArc(cx - corrThickness / 2 + 1, 
                      cy - corrThickness / 2 + 1,
                      corrThickness - 1, corrThickness - 1,
                      90, 90); 
            if (highlighted)
                g.setColor(DrawingPanel.hsbChange(Stroomdiagrammen.highBubbleColor, - 2));            
            else
                g.setColor(DrawingPanel.hsbChange(Stroomdiagrammen.bubbleColor, - 2));
            g.fillArc(cx - corrThickness / 2 + 1, 
                      cy - corrThickness / 2 + 1,
                      corrThickness - 1, corrThickness - 1,
                      270, 90); 
            if (highlighted)
                g.setColor(Stroomdiagrammen.highBubbleColor);                                  
            else
                g.setColor(Stroomdiagrammen.bubbleColor);                      
            g.fillArc(cx - corrThickness / 2 + 1, 
                      cy - corrThickness / 2 + 1,
                      corrThickness - 1, corrThickness - 1,
                      0, 90); 
            g.fillArc(cx - corrThickness / 2 + 1, 
                      cy - corrThickness / 2 + 1,
                      corrThickness - 1, corrThickness - 1,                      
                      180, 90); 
            g.fillOval(cx - corrThickness / 4 + 1, 
                      cy - corrThickness / 4 + 1,
                      (corrThickness - 1) / 2, (corrThickness - 1) / 2);                      
                      
        }               
    }
*/
    //public void drawWavesAt(Graphics g, int step)
    public void drawWavesAt(Context2d g, int step)
    {   if (vThickness < 1)
            return; // no waves

        // left top of polygon
        int lTop = edgeStart.y + vOffSet;
        // right top of polygon
        int rTop = edgeEnd.y + (DrawingPanel.vertexHeight - vThickness) / 2;        
        double tan = ((double) (rTop - lTop)) /
                     (edgeEnd.x - edgeStart.x);           
        double atan = Math.atan(tan);             
        double absAtan = Math.abs(tan);
        
        double waveXThickness = 5;
        double waveYThickness = Math.abs(tan) * waveXThickness;
        int numWaves = (int) Math.round( ((double) (edgeEnd.x - edgeStart.x)) / waveXThickness) + 1;
        Vector waves = new Vector();        
        int nPoints;
        int[] xPoints;
        int[] yPoints;
        Polygon w;
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
        for (int i = 0; i < waves.size(); i++)
        {   g.setFillStyle(colors[(i + step) % 4]);
            
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
    
    public void processDouble(String t, boolean percentage)
    {   
//GWT
/*    	
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
            if (Stroomdiagrammen.rb.getString("decSep") == ",")
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
            if ((value >= 0) && (value <= 1))
            {   
                int newMode;
                if (percentage)
                    newMode = DrawingPanel.percMode;
                else
                    newMode = DrawingPanel.decMode;
                boolean remember = (value != capacity.decVal) || (mode != newMode);
                setCapacity(new Rational(value), true);
                fromVertex.updateOutCapacities(Edge.this, newMode);
                // includes this one
                fromVertex.setOutModes(newMode);
                ((DrawingPanel) capacityField.getParent()).diagramManager.calculateDiagram();                        
                if (remember)
                    ((DrawingPanel) capacityField.getParent()).addToHistory();
            }
            else // reset
                setCapacity(capacity, false);
        }
*/            
    }    
    
    public void processRational(String t)
    {   
//GWT
/*    	
    	boolean error = false;
        int nom = 0, denom = 1;
        String nomStr = null, denomStr = null;
        Rational value;
        // error handling here
        try
        {   t = removeAllBlanks(t);
            int slash = t.indexOf('/');
//            if (slash >= 0)
//            {   
                nomStr = t.substring(0, slash);
                if (slash == (t.length() - 1))
                {   error = true;
                    // reset
                    setCapacity(capacity, false); 
                }
                else
                    denomStr = t.substring(slash + 1);
//            }
//            else
//            {   nomStr = t; 
//                denomStr = "1";
//            }
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
            int newMode = DrawingPanel.fracMode;
            if (value.isLargerOrEqual(new Rational(0, 1, 0), newMode) && 
                value.isSmallerOrEqual(new Rational(1, 1, 1), newMode))
            {   boolean remember = !capacity.equals(value) || (mode != newMode);
                setCapacity(value, true);
                fromVertex.updateOutCapacities(Edge.this, newMode);
                // includes this one
                fromVertex.setOutModes(newMode);                
                ((DrawingPanel) capacityField.getParent()).diagramManager.calculateDiagram();                        
                if (remember)
                    ((DrawingPanel) capacityField.getParent()).addToHistory();
                
            }
            else // reset
                setCapacity(capacity, false);
        }
*/            
    }    
    
    public void processInput()
    {   
//GWT
/*    	
    	// get current text
        String t = capacityField.getText();
        // undo wrapping
        capacityField.setText(t);
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
*/            
//        if ((mode == DrawingPanel.decMode) ||
//            (mode == DrawingPanel.percMode))
//             processDouble(t);
//        else // mode == DrawingPanel.fracMode    
//            processRational(t);
    }    
    public String removeAllBlanks(String s)
    {   int index = s.indexOf(' ');
        while (index >= 0)
        {   s = s.substring(0, index) + s.substring(index + 1);
            index = s.indexOf(' ');
        }
        return s;
    }


//GWT
/*    
    class DelEdgeML extends MouseAdapter
    {   public void mousePressed(MouseEvent e)
        {   DrawingPanel dp = (DrawingPanel) e.getComponent().getParent();
            // put capacity field on top
//            if (!dp.deleteMode)
//            { 

//System.out.println("mp capacityField on top");

        		// Java 8 resistent !
        		dp.setComponentZOrder(capacityField, 0);
                //dp.remove(capacityField);
                //dp.add(capacityField, 0);
        		
        		dp.repaint();
                
//            }  
//            else // deleteMode on
//            {   // see if this edge can be deleted
//                //dp.diagramManager.deleteEdge(Edge.this, true);
//                //dp.owner.unDelete();
//            }
        }    
    }    
*/    
    
    
 //GWT
/*     
  
    class EdgeIAL implements FocusListener, ActionListener
    {      
        public void focusGained(FocusEvent e)
        {}
        public void focusLost(FocusEvent e)
        {   if (frozen)
    			return;
        	
        	processInput();
        }
        public void actionPerformed(ActionEvent e)
        {   if (frozen)
    			return;
        	
        	processInput();
        }
        
        
    } // inner class EdgeIAL
*/    
}

