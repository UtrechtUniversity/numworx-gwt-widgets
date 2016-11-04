package fi.stroomdiagrammengwt.client;

/*
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
*/
import java.util.Vector;
//import java.awt.Dimension;

//import javax.swing.JTextField;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

import com.google.gwt.user.client.ui.Label;

public class Vertex 
{
	// attributes

	boolean frozen = false;
	
	int code;
    int layerNum;
    Rational flow = DrawingPanel.unDef;
//GWT
    //JTextField flowField;
    String flowText = "";
    String fontString = "12px arial, sans-serif";

    boolean hasLabel = false;
    String labelText = "";
    
    int decimals = 2;
    
    LWArrowButton colorButton;
    LWArrowButton addEdgeButton;
    boolean root;  
    Vector inEdges = new Vector();
    Vector outEdges = new Vector();
    
    int xPos; 
    int yPos;
    int breedte;
    int hoogte;
    
    // constructor
    public Vertex(boolean rt, int ln)
    {   
    	//setLayout(null);
    	
    	
    	code = DrawingPanel.vertexCode;
    	DrawingPanel.vertexCode++;
    	root = rt;
        layerNum = ln;
        //setSize(DrawingPanel.vertexWidth, 
        //        DrawingPanel.vertexHeight + DrawingPanel.labelHeight);
        breedte = DrawingPanel.vertexWidth;
        hoogte = DrawingPanel.vertexHeight + DrawingPanel.labelHeight;
        int currentX = 1;
        if (!root)
        {
//GWT        	
           	colorButton = new LWArrowButton(3, CssColor.make(192,192,192));
            colorButton.setBounds(xPos, yPos + 1 + DrawingPanel.labelHeight, 
                        DrawingPanel.leftButtonWidth, 
                        hoogte - 1 - DrawingPanel.labelHeight);
            //add(colorButton);
            //currentX += colorButton.getSize().width;

//GWT            
            //flowField = new JTextField();
            //flowField.setEditable(false);
            //flowField.setHorizontalAlignment(JTextField.CENTER);
            //flowField.setSize(
            //      getSize().width - DrawingPanel.leftButtonWidth - DrawingPanel.arrowButtonWidth,
            //      getSize().height - DrawingPanel.labelHeight - 2);
            
            //flowField.setLocation(currentX - 1, DrawingPanel.labelHeight + 1);
            //flowField.setBackground(new Color(255, 255, 150));
            //add(flowField);
            //currentX += flowField.getSize().width;
        }
        else // root of diagram
        {   

//GWT        	
        	//flowField = new JTextField();
        	//flowField.setSize(
        	//		getSize().width - DrawingPanel.arrowButtonWidth - 1,
            //       getSize().height - DrawingPanel.labelHeight - 2);
        	
            //flowField.setLocation(currentX, DrawingPanel.labelHeight + 1);
            //add(flowField);
            //VertexIAL listener = new VertexIAL();
            //flowField.addActionListener(listener);
            //flowField.addFocusListener(listener);
            //currentX += flowField.getSize().width;
        }    
//GWT        
        addEdgeButton = new LWArrowButton(1, CssColor.make(192,192,192));
        addEdgeButton.setBounds(xPos + breedte - DrawingPanel.arrowButtonWidth,
        						yPos + 1 + DrawingPanel.labelHeight,
        						DrawingPanel.arrowButtonWidth,
        						hoogte - 1 - DrawingPanel.labelHeight);
        						//currentX - 1, 1 + DrawingPanel.labelHeight, 
        						//DrawingPanel.arrowButtonWidth, 
        						//getSize().height - 1 - DrawingPanel.labelHeight);
        //add(addEdgeButton);

//GWT        
        //vLabel = new JTextField();
        //vLabel.setSize(DrawingPanel.vertexWidth - 2,
        //			   DrawingPanel.LABELHEIGHT - 2);
        //VertexLabelIAL listener = new VertexLabelIAL();
        //vLabel.addFocusListener(listener);
        //vLabel.addActionListener(listener);
                                 
        //vLabel.setLocation(1, 1);
        //vLabel.setVisible(false);
        //add(vLabel);
        if (DrawingPanel.labelHeight > 0)
        {	hasLabel = true;

        }
    }    

    public boolean vertexClicked(int x, int y)
    {
    	Rectangle vertexRect = new Rectangle(xPos, yPos, breedte, hoogte);
    	return vertexRect.contains(x, y);
    }
    
    public boolean addEdgeButtonClicked(int x, int y)
    {
    	if (addEdgeButton == null)
    		return false;
    	Rectangle addEdgeRect = new Rectangle(addEdgeButton.xPos, addEdgeButton.yPos, 
    										  addEdgeButton.breedte, addEdgeButton.hoogte);
    	return addEdgeRect.contains(x, y);
    }

    public boolean colorButtonClicked(int x, int y)
    {
    	if (colorButton == null)
    		return false;
    	Rectangle colorRect = new Rectangle(colorButton.xPos, colorButton.yPos, 
    										colorButton.breedte, colorButton.hoogte);
    	return colorRect.contains(x, y);
    }
    
    public boolean labelClicked(int x, int y)
    {
    	if (!hasLabel)
    		return false;
    	
    	Rectangle labelRect = new Rectangle(xPos, yPos, 
    										breedte, DrawingPanel.labelHeight);
    	return labelRect.contains(x, y);
    }
    

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
    
    public Point getLocation()
    {
    	return new Point(xPos, yPos);
    }
    
    public Dimension getSize()
    {
    	return new Dimension(breedte, hoogte);
    }
    
    public Rectangle getBoundingRect()
    {
    	return new Rectangle(xPos, yPos, breedte, hoogte);
    }
    
    public void setFrozen(boolean b)
    {
/*    	
    	frozen = b;
    	vLabel.setEditable(!frozen);
    	if (root)
    		flowField.setEditable(!frozen);
*/    		
    	addEdgeButton.setEnabled(!frozen);
    	if (colorButton != null)
    		colorButton.setEnabled(!frozen);
    		
    }
    
    public void setLabel(boolean b)
    {   

    	
    	// add label
        if (b)
        {   hoogte += DrawingPanel.LABELHEIGHT; 
        	//setSize(DrawingPanel.vertexWidth,
            //        DrawingPanel.vertexHeight + DrawingPanel.LABELHEIGHT);
            if (!root)
                colorButton.setLocation(colorButton.xPos, colorButton.yPos + DrawingPanel.LABELHEIGHT);
            //flowField.setLocation(flowField.getLocation().x,
            //    flowField.getLocation().y + DrawingPanel.LABELHEIGHT);
            addEdgeButton.setLocation(addEdgeButton.xPos, addEdgeButton.yPos + DrawingPanel.LABELHEIGHT);
            //vLabel.setVisible(true);
            hasLabel = true;
            
            //add(vLabel);
        }
        else // remove label
        {   //remove(vLabel);
        	//vLabel.setVisible(false);
        	hasLabel = false;
        	hoogte = DrawingPanel.vertexHeight;
            //setSize(DrawingPanel.vertexWidth,
            //        DrawingPanel.vertexHeight);
            if (!root)
                colorButton.setLocation(colorButton.xPos, colorButton.yPos - DrawingPanel.LABELHEIGHT);
            //flowField.setLocation(flowField.getLocation().x,
            //    flowField.getLocation().y - DrawingPanel.LABELHEIGHT);
            addEdgeButton.setLocation(addEdgeButton.xPos,addEdgeButton.yPos - DrawingPanel.LABELHEIGHT);            
        }
        
    }    
    public void setFlow(Rational f)
    {   

    	
        flow = f;
        if (f.isUndefined())
            flowText = "";
        else if (DrawingPanel.flowMode == DrawingPanel.fracMode)
        {   if (flow.isInteger())
                flowText = UF.format(flow.nom, 0);        
            else
                flowText = UF.format(flow.nom, 0) + "/" + UF.format(flow.denom, 0);  
        }    
        else if (DrawingPanel.flowMode == DrawingPanel.decMode)
            flowText = UF.format(flow.decVal, decimals);

    }    

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

    // bubble sort outEdges by y-location of toVertex
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


    public double getAngle(Vertex v)
    {   //DrawingPanel dp = (DrawingPanel) getParent();
        //double x = v.getLocation().x - getLocation().x + DrawingPanel.vertexWidth;
        //double y = getLocation().y - v.getLocation().y;
        double x = v.xPos - xPos + DrawingPanel.vertexWidth;
        double y = yPos - v.yPos;
        
        return Math.atan(y/x);
    }    
    
/*
    public boolean hasFlow()
    {   double inCap = 0;
        for (int i = 0; i < inEdges.size(); i++)
        {   Edge ie = (Edge) inEdges.elementAt(i);
            inCap += ie.capacity.decVal;
        }    
        return inCap > 0;
    }    
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
//            else if ( ((DrawingPanel) getParent()).flowMode == DrawingPanel.fracMode)
//            {   
                inFlow = inFlow.plus(ie.fromVertex.flow.times(ie.capacity));            
//            }
//            else
//            {   inFlow.decVal += ie.fromVertex.flow.decVal * ie.capacity.decVal;
//            }
        }
        setFlow(inFlow);
    }    

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
    public void updateOutCapacities(Edge changedEdge, int mode)
    {   Rational total = new Rational(0, 1, 0);
        int oldestChange = 0;
        long changeTime = Long.MAX_VALUE;
        // find sum of all capacities and
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

    public void setOutModes(int mode)
    {   for (int i = 0; i < outEdges.size(); i++)
        {   Edge oe = (Edge) outEdges.elementAt(i);
            oe.setMode(mode);
        }    
    }    

    public Edge hasInEdgeFrom(Vertex v)
    {   Edge result = null;
        for (int i = 0; i < inEdges.size(); i++)
        {   Edge ie = (Edge) inEdges.elementAt(i);
            if (ie.fromVertex == v)
                result = ie;
        }
        return result;
    }
    
    public Edge hasOutEdgeTo(Vertex v)
    {   Edge result = null;
        for (int i = 0; i < outEdges.size(); i++)
        {   Edge ie = (Edge) outEdges.elementAt(i);
            if (ie.toVertex == v)
                result = ie;
        }
        return result;
    }
    
    //public void paintComponent(Graphics g)
    public void paintComponent(Context2d g)
    {   
    	if (!root)
    	{	
    		g.setFillStyle(CssColor.make(255, 255, 150));
    		g.fillRect(xPos, yPos + DrawingPanel.labelHeight, breedte, hoogte - DrawingPanel.labelHeight);
    	}	
    	
    	//g.setColor(Color.black);
    	g.setStrokeStyle(CssColor.make(0,0,0));
        //g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
    	g.strokeRect(xPos, yPos + DrawingPanel.labelHeight, breedte, hoogte - DrawingPanel.labelHeight);
    	
    	if (addEdgeButton != null)
    		addEdgeButton.paintComponent(g);
        
    	if (colorButton != null)
    		colorButton.paintComponent(g);
        
        
        if (hasLabel)
        {
        	//{	g.setColor(Color.red);
        	g.setStrokeStyle(CssColor.make(255,0,0));        	
        	//	g.drawRect(0, 0, getSize().width - 1, DrawingPanel.LABELHEIGHT - 1);
        	g.strokeRect(xPos, yPos, breedte, DrawingPanel.LABELHEIGHT);
        	g.setFillStyle(CssColor.make(0,0,0));
        	g.fillText(labelText, xPos + 2, yPos + 16, breedte - 4);
        }
        
        g.setStrokeStyle(CssColor.make(0,0,0));
		g.setFont(fontString);
		//int maxTextWidthRoot = breedte - addEdgeButton.breedte - 2;
		int maxTextWidth = 0; //breedte - addEdgeButton.breedte - colorButton.breedte - 2;
		if (root)
		{	maxTextWidth = breedte - addEdgeButton.breedte - 2;
			g.fillText(flowText, xPos + 2, yPos + DrawingPanel.labelHeight + 16, maxTextWidth);
		
		}
		else
		{	maxTextWidth = breedte - addEdgeButton.breedte - colorButton.breedte - 2;
			g.fillText(flowText, xPos + colorButton.breedte + 2, yPos + DrawingPanel.labelHeight + 16, maxTextWidth);
		
		}
		

        
        
        //}
        
        //super.paint(g);   
/*        
        g.setColor(Color.black);
        g.drawRoundRect(0, 0, getSize().width - 1, 
                              getSize().height - 1, 
                              DrawingPanel.roundWidth, 
                              DrawingPanel.roundHeight);         
*/                              
    }    

    
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
  
    public void processInput(String t)
    {   

    	
    	boolean error = false;
        double value = 0;
        // get current text
        //String t = flowField.getText();
        // undo wrapping
        //flowField.setText(t);
        if (t.equals(""))
        {   boolean remember = !flow.isUndefined();
            setFlow(DrawingPanel.unDef);
            DrawingPanel.diagramManager.calculateDiagram();
            if (remember)
                DrawingPanel.addToHistory();
        }
        else
        {   if (StroomDiagrammenGWT.rb.getString("decSep") == ",")
                t = t.replace(',', '.');
            int mode = DrawingPanel.flowMode;
            int divIndex = t.indexOf('/');
            int decIndex = t.indexOf('.');
            if ((divIndex < 0) && (decIndex < 0))
                processRational(t);
            else if (mode == DrawingPanel.fracMode)
            {   if (decIndex >= 0)
                    setFlow(flow); // reset
                else    
                    processRational(t);
            }    
            else if (mode == DrawingPanel.decMode)
            {   if (divIndex >= 0)
                    setFlow(flow); // reset
                else    
                    processDecimal(t);
            }    
        }
            
    }    

    public String removeAllBlanks(String s)
    {   int index = s.indexOf(' ');
        while (index >= 0)
        {   s = s.substring(0, index) + s.substring(index + 1);
            index = s.indexOf(' ');
        }
        return s;
    }
    
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
//GWT        
        //((DrawingPanel) getParent()).diagramManager.setDecimals(decs);                            
    }                
    
    // inner class for root numberfield (later all vertices??)
    // starts calculations on Enter
//GWT
/*    
    class VertexIAL implements FocusListener, ActionListener
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
        
        
    } // inner class VertexIAL   
*/    
    // inner class vertex labels
/*    
    class VertexLabelIAL implements FocusListener, ActionListener
    {   	
    	String labelText;
    	
        public void focusGained(FocusEvent e)
        {	if (frozen)
    			return;
        	
        	labelText = vLabel.getText();
        }
        public void focusLost(FocusEvent e)
        {   if (frozen)
    			return;
        	
        	if (!labelText.equals(vLabel.getText()))
        	{	
        		((DrawingPanel) getParent()).updateHistoryLabels();
        		labelText = vLabel.getText();
        	}
        }
        public void actionPerformed(ActionEvent e)
        {	if (frozen)
    			return;
        	
        	if (!labelText.equals(vLabel.getText()))
    		{	
    			((DrawingPanel) getParent()).updateHistoryLabels();
    			labelText = vLabel.getText();
    		}
        }
        
    } // inner class VertexIAL
*/           

}
