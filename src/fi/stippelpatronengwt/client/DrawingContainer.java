package fi.stippelpatronengwt.client;

//import java.awt.*;
//import java.awt.event.*;
import java.util.*;

//import javax.swing.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.LayoutPanel;

import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.dom.client.TouchEndHandler;

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;

// class representing a grid element with spot
class GridElement
{   // attributes
    // grid coordinates, grid size, spot size
    int gX, gY, size, gSize;
    // spot/nospot (drawing)
    boolean visible = false;
    // selected/not selected (a.o. counting)
    boolean selected = false;
    // default spot color 
    CssColor gColor = DrawingContainer.spotColors[0];
    // sensitive area for clicking in draw mode
    Polygon sensitive;
    Point[] sensPts;
    // contour for drawing
    Rectangle contour;
    // constructor
    public GridElement(int x, int y, int s)
    {   gX = x;
        gY = y;
        size = s;
        gSize = size / 2;
        contour = new Rectangle(gX * size, gY * size,
                                size, size);
        sensitive = new Polygon();
        sensPts = new Point[4];
        sensPts[0] = new Point(gX * size + size / 2, 
                               gY * size);
        sensPts[1] = new Point(gX * size + size, 
                               gY * size + size / 2);
        sensPts[2] = new Point(gX * size + size / 2, 
                               gY * size + size);        
        sensPts[3] = new Point(gX * size, 
                               gY * size + size / 2);                
        for (int i = 0; i < 4; i++)
            sensitive.addPoint(sensPts[i].x, sensPts[i].y);
    } // constructor   
    // for mouse events in DRAWMODE
    public boolean contains(int x, int y)
    {   return sensitive.contains(x, y);
    }    
    // drawing
    //public void paint(Graphics g)
    public void paint(Context2d g)
    {   // selection is filled with very light gray
        if (selected)
        {   // very light gray
            //g.setColor(new Color(222, 222, 222));
        	g.setFillStyle(CssColor.make(222, 222, 222));
            g.fillRect(contour.x, contour.y,
                       contour.width, contour.height);
        }
        // always gray contour
        //g.setColor(Color.lightGray);
        g.setStrokeStyle(CssColor.make(192, 192, 192));
        //g.drawRect(contour.x, contour.y,
        //           contour.width, contour.height);
        g.strokeRect(contour.x, contour.y,
                   contour.width, contour.height);

        // if visible spot with black border                
        if (visible)
        {   //g.setColor(gColor);
        	g.setFillStyle(gColor);
            //g.fillOval(gX * size + gSize / 2,
            //           gY * size + gSize / 2, 
            //           gSize, gSize);
            g.beginPath();
            g.arc(gX * size + gSize, gY * size + gSize, gSize / 2, 0, 2 * Math.PI);
            g.fill();
            //g.setColor(Color.black);
            g.setStrokeStyle(CssColor.make(0, 0, 0));
            //g.drawOval(gX * size + gSize / 2,
            //           gY * size + gSize / 2, 
            //           gSize, gSize);
            g.beginPath();
            g.arc(gX * size + gSize, gY * size + gSize, gSize / 2, 0, 2 * Math.PI);
            g.stroke();

        } // if (visible)               
    } // paint   
} // class GridElement   

// the class where number spotting takes place
public class DrawingContainer //extends 
{   // attributes
    // the size of a grid element in pixels
    static final int GRIDSIZE = 16;
    // available colors
    static final int MAXCOLORS = 8;
	static CssColor black = CssColor.make(0, 0, 0);
	static CssColor gray = CssColor.make(192, 192, 192);
	static CssColor red = CssColor.make(255, 0, 0);
	static CssColor orange = CssColor.make(255, 127, 0);
	static CssColor green = CssColor.make(0, 255, 0);
	static CssColor cyan = CssColor.make(0, 255, 255);
	static CssColor blue = CssColor.make(0, 0, 255);
	static CssColor magenta = CssColor.make(255, 0, 255);
	static CssColor yellow = CssColor.make(255, 255, 0);

    static final CssColor[] spotColors =
        {red, blue, magenta,
         green, orange,
         cyan, yellow, gray};   
    // default color    
    CssColor drawColor = spotColors[0];         
    
    static final int DRAWCELLMODE = 0;
    int userMode = DRAWCELLMODE;
    
    // constants for directions
    static final int NORTH = 0;
    static final int NORTHEAST = 1;
    static final int EAST = 2;
    static final int SOUTHEAST = 3;
    static final int SOUTH = 4;
    static final int SOUTHWEST = 5;
    static final int WEST = 6;
    static final int NORTHWEST = 7;
    // the grid and its size
    GridElement[][] grid;
    int horSize, vertSize;
    // turtle
    Point turtlePosition = new Point(0, 0);
    int turtleDirection = NORTH;
    // a reference to the main applet for accessing GUI components
    //Spot_Problems_dwo owner;
    StippelPatronenGWT owner;
    // the current problem type
    int problemNumber;
    // maximum number of patterns
    int maxPatterns;
    // patterns visible
    int patternsShown = 0;
    // points for layout
    Point[] basePoints = new Point[MAXCOLORS];
    // strings for labeling
    String[] labels = new String[MAXCOLORS];
    // pixelcorrection for labels
    int[] corrections = new int[MAXCOLORS];
    // font for labels
    //Font labelFont;
    String labelFont = "bold 14px helvetics, sans-serif";
    //FontMetrics labelFM;
    TextMetrics labelFM;
    
    //MLMML listener;
    
    //SPInteractiePanel spip;
    
    int breedte, hoogte;
    Canvas stippelCanvas;
    Context2d stippelContext2d;
    
    boolean mouseDown = false;
/*    
    public DrawingContainer(Spot_Problems_dwo o)
    {   setLayout(null);
        owner = o;
        problemNumber = owner.problemNumber;
    } // constructor
*/    
    public DrawingContainer(StippelPatronenGWT o, int b, int h)
    {   //setLayout(null);
        //this.spip = spip;
    	owner = o;
    	breedte = b;
    	hoogte = h;
        problemNumber = 0;
        
		stippelCanvas = Canvas.createIfSupported();
		
		stippelCanvas.setWidth(breedte + "px");
		stippelCanvas.setHeight(hoogte + "px");
		stippelCanvas.setCoordinateSpaceWidth(breedte);
		stippelCanvas.setCoordinateSpaceHeight(hoogte);

	
		MouseHandler mouseHandler = new MouseHandler();
		stippelCanvas.addMouseDownHandler(mouseHandler);
		stippelCanvas.addMouseMoveHandler(mouseHandler);
		stippelCanvas.addMouseUpHandler(mouseHandler);
	
		TouchHandler touchHandler = new TouchHandler();
		stippelCanvas.addTouchStartHandler(touchHandler);
		stippelCanvas.addTouchMoveHandler(touchHandler);
		stippelCanvas.addTouchEndHandler(touchHandler);

    } // constructor
    
	public Canvas getCanvas()
	{
		return stippelCanvas;
	}
	
	public void initContext2d() 
	{
		stippelContext2d = stippelCanvas.getContext2d();
		
	}
    
    // initialization of attributes
    public void initialize()
    {       	
    	//if (getParent() != null)
    	//{	
    	//	horSize = getParent().getSize().width / GRIDSIZE;
    	//	vertSize = getParent().getSize().height / GRIDSIZE;
    	//}
    	//else
    	//{
    		//horSize = getSize().width / GRIDSIZE;
    		horSize = breedte / GRIDSIZE;
    		//vertSize = getSize().height / GRIDSIZE;
    		vertSize = hoogte / GRIDSIZE;
    	//}
        grid = new GridElement[horSize][vertSize];
        for (int i = 0; i < horSize; i++)
            for (int j = 0; j < vertSize; j++)
            {   grid[i][j] = new GridElement(i, j, GRIDSIZE);
            }    
        
        //listener = new MLMML();
        //addMouseListener(listener);
        //addMouseMotionListener(listener);
        //if (spip == null)
        //	initProblem(problemNumber);
        //else
        	initDWOProblem(problemNumber);
    } // initialize

    public void initProblem(int num)
    {   problemNumber = num;
        removeAll();
        patternsShown = 0;
        switch (problemNumber)
        {   case 1://owner.VNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 2://owner.WNUMBERS: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(11, 5);
                basePoints[2] = new Point(23, 5);
                basePoints[3] = new Point(9, 12);
                basePoints[4] = new Point(20, 18);
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 3://owner.SQRNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(14, 16);
                basePoints[6] = new Point(23, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (j % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 101://owner.OBLNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(15, 16);
                basePoints[6] = new Point(25, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = -((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 102://owner.TRIANUMBERS1: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(14, 16);
                basePoints[6] = new Point(23, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (j % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 103://owner.TRIANUMBERS2: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 201://owner.PENTANUMBERS: 
            {   basePoints[0] = new Point(2, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(16, 6);
                basePoints[3] = new Point(26, 8);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(18, 18);
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 4://owner.BOXNUMBERS: 
            {   basePoints[0] = new Point(4, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(16, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(6, 17);
                basePoints[5] = new Point(14,17); 
                basePoints[6] = new Point(24,17); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 5://owner.PLUSNUMBERS: 
            {   basePoints[0] = new Point(3, 4);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(16, 8);
                basePoints[3] = new Point(26, 10);
                basePoints[4] = new Point(6, 17);
                basePoints[5] = new Point(19, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 202://owner.SPIRALNUMBERS: 
            {   basePoints[0] = new Point(2, 6);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 6);
                basePoints[3] = new Point(19, 6);
                basePoints[4] = new Point(26, 6);
                basePoints[5] = new Point(5, 17);
                basePoints[6] = new Point(14, 17);
                basePoints[7] = new Point(24, 17);
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = -((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 104://owner.TRIANUMBERS3: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 6://owner.TOWERNUMBERS: 
            {   basePoints[0] = new Point(3, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(15, 7);
                basePoints[3] = new Point(21, 7);
                basePoints[4] = new Point(27, 7);
                basePoints[5] = new Point(5, 17);
                basePoints[6] = new Point(14, 17);
                basePoints[7] = new Point(24, 17);
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //-((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 203://owner.BLOCKNUMBERS1: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 6);
                basePoints[3] = new Point(18, 6);
                basePoints[4] = new Point(26, 6);
                basePoints[5] = new Point(6, 17);
                basePoints[6] = new Point(20, 17);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (GRIDSIZE / 2);
                }
            }
            break;
            case 7://owner.FNUMBERS1: 
            {   
                basePoints[0] = new Point(7, 5);
                basePoints[1] = new Point(10, 7);
                basePoints[2] = new Point(14, 9);
                basePoints[3] = new Point(19, 11);
                basePoints[4] = new Point(3, 19);
                basePoints[5] = new Point(25, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 105://owner.FNUMBERS2: 
            {   
                basePoints[0] = new Point(1, 5);
                basePoints[1] = new Point(1, 15);
                basePoints[2] = new Point(6, 13);
                basePoints[3] = new Point(13, 17);
                basePoints[4] = new Point(22, 20);
//                basePoints[5] = new Point(25, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - GRIDSIZE / 2; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 8://owner.FLAPNUMBERS: 
            {   basePoints[0] = new Point(4, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(16, 7);
                basePoints[3] = new Point(23, 7);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(14,18); 
                basePoints[6] = new Point(24,18); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 106://owner.FLIPNUMBERS: 
            {   basePoints[0] = new Point(4, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(16, 7);
                basePoints[3] = new Point(23, 7);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(14,18); 
                basePoints[6] = new Point(24,18); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 107://owner.TILENUMBERS1: 
            {   basePoints[0] = new Point(2, 3);
                basePoints[1] = new Point(3, 9);
                basePoints[2] = new Point(4, 17);
                basePoints[3] = new Point(13, 9);
                basePoints[4] = new Point(13, 20);
                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 108://owner.TILENUMBERS2: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 9://owner.HNUMBERS1: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(3, 12);
                basePoints[2] = new Point(8, 9);
                basePoints[3] = new Point(13, 11);
                basePoints[4] = new Point(18, 13);
                basePoints[5] = new Point(23, 15);
                basePoints[6] = new Point(28, 17);
//                basePoints[7] = new Point(24, 17);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //-((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 10://owner.HNUMBERS2: 
            {   
                basePoints[0] = new Point(2, 4);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 8);
                basePoints[3] = new Point(19, 10);
                basePoints[4] = new Point(4, 19);
                basePoints[5] = new Point(27, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 11://owner.XNUMBERS1: 
            {   basePoints[0] = new Point(2, 4);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(14, 8);
                basePoints[3] = new Point(23, 10);
                basePoints[4] = new Point(6, 19);
                basePoints[5] = new Point(24, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 12://owner.XNUMBERS2: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(7, 4);
                basePoints[2] = new Point(14, 6);
                basePoints[3] = new Point(23, 8);
                basePoints[4] = new Point(6, 19);
                basePoints[5] = new Point(21, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 13://owner.LNUMBERS1: 
            {   
                basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 7);
                basePoints[3] = new Point(23, 8);
                basePoints[4] = new Point(5, 17);
                basePoints[5] = new Point(15, 18);                 
                basePoints[6] = new Point(25, 19); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 14://owner.LNUMBERS2: 
            {   
                basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(3, 18);
                basePoints[2] = new Point(6, 11);
                basePoints[3] = new Point(13, 14);
                basePoints[4] = new Point(22, 17);
//                basePoints[5] = new Point(25, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE / 2; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 204://owner.TILENUMBERS3: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 18);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 205://owner.TILENUMBERS4: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(3, 8);
                basePoints[2] = new Point(4, 16);
                basePoints[3] = new Point(13, 8);
                basePoints[4] = new Point(13, 19);
                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 15://owner.TABLENUMBERS: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(10, 5);
                basePoints[2] = new Point(17, 5);
                basePoints[3] = new Point(26, 5);
                basePoints[4] = new Point(5, 11);
                basePoints[5] = new Point(16, 11); 
                basePoints[6] = new Point(6, 17); 
                basePoints[7] = new Point(19, 17);                 
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 109://owner.STAIRNUMBERS: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(7, 3);
                basePoints[2] = new Point(14, 4);
                basePoints[3] = new Point(23, 5);
                basePoints[4] = new Point(6, 10);
                basePoints[5] = new Point(19, 13); 
                basePoints[6] = new Point(13, 19); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 110://owner.ZNUMBERS: 
            {   
                basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(15, 8);
                basePoints[3] = new Point(23, 9);
                basePoints[4] = new Point(5, 19);
                basePoints[5] = new Point(17, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 111://owner.INSECTNUMBERS: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 112://owner.SPIDERNUMBERS: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 206://owner.BLOCKNUMBERS2: 
            {   basePoints[0] = new Point(9, 3);
                basePoints[1] = new Point(14, 4);
                basePoints[2] = new Point(21, 5);
                basePoints[3] = new Point(3, 9);
                basePoints[4] = new Point(3, 16);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 207://owner.BLOCKNUMBERS3: 
            {   basePoints[0] = new Point(3, 3);
                basePoints[1] = new Point(4, 12);
                basePoints[2] = new Point(12, 11);
                basePoints[3] = new Point(24, 18);
//                basePoints[4] = new Point(23, 19);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 4;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 208://owner.TRIANUMBERS4: 
            {   basePoints[0] = new Point(4, 4);
                basePoints[1] = new Point(6, 12);
                basePoints[2] = new Point(17, 9);
                basePoints[3] = new Point(29, 19);
//                basePoints[4] = new Point(23, 19);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 4;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 209://owner.PIRAMIDNUMBERS: 
            {   basePoints[0] = new Point(2, 3);
                basePoints[1] = new Point(4, 9);
                basePoints[2] = new Point(6, 18);
                basePoints[3] = new Point(14, 16);
                basePoints[4] = new Point(25, 17);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 210://owner.PIRAMIDNUMBERS: 
            {   basePoints[0] = new Point(2, 3);
                basePoints[1] = new Point(4, 9);
                basePoints[2] = new Point(6, 18);
                basePoints[3] = new Point(14, 16);
                basePoints[4] = new Point(25, 17);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            
            default: // nothing
        }    
        showPattern(0);
        showNextPattern();
        showNextPattern();
    }    

    public void showPattern(int n)
    {   drawColor = spotColors[n];
        switch(problemNumber)
        {   case 1://owner.VNUMBERS: // start at n = 1
            {   drawVNumber(n + 1, basePoints[n].x - (n + 1),
                    basePoints[n].y - (n + 2));
            }
            break;
            case 2://owner.WNUMBERS: // start at n = 1
            {   drawWNumber(n + 1, basePoints[n].x - (2 * (n + 1)),
                    basePoints[n].y - (n + 2));
            }
            break;
            case 3://owner.SQRNUMBERS: // start at n = 1
            {   drawSquare(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 101://owner.OBLNUMBERS: // start at n = 1
            {   drawOblong(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 102://owner.TRIANUMBERS1: // start at n = 1
            {   drawTriang1Number(n + 2, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));            
            }    
            break;
            case 103://owner.TRIANUMBERS2: // start at n = 1
            {   drawTriang2Number(n + 2, basePoints[n].x - n,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 201://owner.PENTANUMBERS: // start at n = 1
            {   // nothing for n = 1
                drawTriang2Number(n + 1, basePoints[n].x - (n - 1),
                    basePoints[n].y - (2 * n + 1));
                // single dot for n = 1   
                drawSpacedSquare(n + 1, basePoints[n].x - n,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 4://owner.BOXNUMBERS: // start at n = 1
            {   drawBox(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 2));
            }    
            break;
            case 5://owner.PLUSNUMBERS: // start at n = 1
            {   drawPlus(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 202://owner.SPIRALNUMBERS: // start at n = 1
            {   drawSpiral(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 104://owner.TRIANUMBERS3: // start at n = 1
            {   drawTriang3Number(n + 1, basePoints[n].x - n,
                    basePoints[n].y - n - 1);
            }    
            break;
            case 6://owner.TOWERNUMBERS: // start at n = 1
            {   drawTowerNumber(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - n - 2);
            }    
            break;
            case 203://owner.BLOCKNUMBERS1: // start at n = 1
            {   switch (n)
                {   case 0: drawRectangle(2, 1, basePoints[0].x - 1,
                                basePoints[0].y - 1);
                    break;
                    case 1: drawRectangle(2, 2, basePoints[1].x - 1,
                                basePoints[1].y - 2);
                    break;
                    case 2: drawRectangle(4, 2, basePoints[2].x - 2,
                                basePoints[2].y - 2);
                    break;
                    case 3: drawRectangle(4, 4, basePoints[3].x - 2,
                                basePoints[3].y - 4);
                    break;
                    case 4: drawRectangle(8, 4, basePoints[4].x - 4,
                                basePoints[4].y - 4);
                    break;
                    case 5: drawRectangle(8, 8, basePoints[5].x - 4,
                                basePoints[5].y - 8);
                    break;
                    case 6: drawRectangle(16, 8, basePoints[6].x - 8,
                                basePoints[6].y - 8);
                    break;
                    default:
                }    
            }    
            break;
            case 7://owner.FNUMBERS1: // start at n = 1
            {   drawFNumber1(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - (2 * n + 4));
            }    
            break;
            case 105://owner.FNUMBERS2: // start at n = 1
            {   drawFNumber2(n + 1, basePoints[n].x,
                    basePoints[n].y - (4 * n + 4));
            }    
            break;
            case 8://owner.FLAPNUMBERS: // start at n = 1
            {   drawFlapNumber(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 106://owner.FLIPNUMBERS: // start at n = 1
            {   drawFlipNumber(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 107://owner.TILENUMBERS1: // start at n = 1
            {   drawTileNumber1(n + 1, basePoints[n].x - (2 * n + 1) / 2 - 1,
                    basePoints[n].y - (2 * n + 2));
            }    
            break;
            case 108://owner.TILENUMBERS2: // start at n = 1
            {   drawTileNumber2(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 9://owner.HNUMBERS1: // start at n = 1
            {   drawHNumber1(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - 2 * n - 3);
            }    
            break;
            case 10://owner.HNUMBERS2: // start at n = 1
            {   drawHNumber2(n + 1, basePoints[n].x - ((n + 2) / 2) - ((n + 2) % 2),
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 11://owner.XNUMBERS1: // start at n = 1
            {   drawXNumber1(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 12://owner.XNUMBERS2: // start at n = 0
            {   drawXNumber1(n, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 1));
            }    
            break;
            case 13://owner.LNUMBERS1: // start at n = 1
            {   drawLNumber1(n + 1, basePoints[n].x - (n / 2) - (n % 2),
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 14://owner.LNUMBERS2: // start at n = 1
            {   drawLNumber2(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - (3 * n + 3));
            }    
            break;
            case 204://owner.TILENUMBERS3: // start at n = 1
            {   drawTileNumber3(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 205://owner.TILENUMBERS4: // start at n = 1
            {   drawTileNumber4(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 1));
            }    
            break;
            case 15://owner.TABLENUMBERS: // start at n = 1
            {   drawTableNumber(n + 1, basePoints[n].x - (n + 2) / 2 - (n + 2) % 2,
                    basePoints[n].y - 3);
            }    
            break;
            case 109://owner.STAIRNUMBERS: // start at n = 1
            {   drawStairNumber(n + 1, basePoints[n].x - n,
                    basePoints[n].y - n - 1);
            }    
            break;
            case 110://owner.ZNUMBERS: // start at n = 1
            {   drawZNumber(n + 1, basePoints[n].x - (n + 2) / 2 - (n + 2) % 2,
                    basePoints[n].y - (n + 5));
            }    
            break;
            case 111://owner.INSECTNUMBERS: // start at n = 1
            {   drawInsectNumber(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 112://owner.SPIDERNUMBERS: // start at n = 1
            {   drawSpiderNumber(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 206://owner.BLOCKNUMBERS2: // start at n = 1
            {   drawBlockNumber2(n + 1, basePoints[n].x, // - (n * n) / 2 - (n * n) % 2,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 207://owner.BLOCKNUMBERS3: // start at n = 1
            {   drawBlockNumber3(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (n * n + 2 * n + 1));
            }    
            break;
            case 208://owner.TRIANUMBERS4: // start at n = 1
            {   int width = (int) Math.pow(2, n + 2) - 1;
                int height = (int) Math.pow(2, n + 1) - 1;
                drawTriang4Number(n + 1, basePoints[n].x - (width) / 2 - (width) % 2,
                    basePoints[n].y - (height + 1));
            }    
            break;
            case 209://owner.PIRAMIDNUMBERS: // start at n = 1
            {   int width = (n + 1) * (n + 2) / 2;
                int height = (n + 1) * (n + 2) / 2;
                drawPiramidNumber(n + 1, basePoints[n].x - (width - n - 1) - (width - n - 1) % 2,
                    basePoints[n].y - (height));
            }    
            break;
            case 210://owner.PIRAMIDNUMBERS: // start at n = 1
            {   int width = (n + 1) * (n + 2) / 2;
                int height = (n + 1) * (n + 2) / 2;
                drawPiramidNumber(n + 1, basePoints[n].x - (width - n - 1) - (width - n - 1) % 2,
                    basePoints[n].y - (height));
            }    
            break;
            
            
            default: // nothing
        }    
    }
    
    public void initDWOProblem(int num)
    {   problemNumber = num;
        removeAll();
        patternsShown = 0;
        switch (problemNumber)
        {   case 0://spip.VNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 1://spip.WNUMBERS: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(11, 5);
                basePoints[2] = new Point(23, 5);
                basePoints[3] = new Point(9, 12);
                basePoints[4] = new Point(20, 18);
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 2://spip.SQRNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(14, 16);
                basePoints[6] = new Point(23, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (j % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 102://spip.OBLNUMBERS: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(15, 16);
                basePoints[6] = new Point(25, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = -((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 103://spip.TRIANUMBERS1: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(5, 16);
                basePoints[5] = new Point(14, 16);
                basePoints[6] = new Point(23, 16);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (j % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 104://spip.TRIANUMBERS2: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 203://spip.PENTANUMBERS: 
            {   basePoints[0] = new Point(2, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(16, 6);
                basePoints[3] = new Point(26, 8);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(18, 18);
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 3://spip.BOXNUMBERS: 
            {   basePoints[0] = new Point(4, 6);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(16, 6);
                basePoints[3] = new Point(23, 6);
                basePoints[4] = new Point(6, 17);
                basePoints[5] = new Point(14,17); 
                basePoints[6] = new Point(24,17); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 4://spip.PLUSNUMBERS: 
            {   basePoints[0] = new Point(3, 4);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(16, 8);
                basePoints[3] = new Point(26, 10);
                basePoints[4] = new Point(6, 17);
                basePoints[5] = new Point(19, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 204://spip.SPIRALNUMBERS: 
            {   basePoints[0] = new Point(2, 6);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 6);
                basePoints[3] = new Point(19, 6);
                basePoints[4] = new Point(26, 6);
                basePoints[5] = new Point(5, 17);
                basePoints[6] = new Point(14, 17);
                basePoints[7] = new Point(24, 17);
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = -((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 105://spip.TRIANUMBERS3: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(8, 6);
                basePoints[2] = new Point(15, 6);
                basePoints[3] = new Point(24, 6);
                basePoints[4] = new Point(7, 15);
                basePoints[5] = new Point(20,15); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0;
                }
            }
            break;
            case 5://spip.TOWERNUMBERS: 
            {   basePoints[0] = new Point(3, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(15, 7);
                basePoints[3] = new Point(21, 7);
                basePoints[4] = new Point(27, 7);
                basePoints[5] = new Point(5, 17);
                basePoints[6] = new Point(14, 17);
                basePoints[7] = new Point(24, 17);
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //-((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 205://spip.BLOCKNUMBERS1: 
            {   basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 6);
                basePoints[3] = new Point(18, 6);
                basePoints[4] = new Point(26, 6);
                basePoints[5] = new Point(6, 17);
                basePoints[6] = new Point(20, 17);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = (GRIDSIZE / 2);
                }
            }
            break;
            case 6://spip.FNUMBERS1: 
            {   
                basePoints[0] = new Point(7, 5);
                basePoints[1] = new Point(10, 7);
                basePoints[2] = new Point(14, 9);
                basePoints[3] = new Point(19, 11);
                basePoints[4] = new Point(3, 19);
                basePoints[5] = new Point(25, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 106://spip.FNUMBERS2: 
            {   
                basePoints[0] = new Point(1, 5);
                basePoints[1] = new Point(1, 15);
                basePoints[2] = new Point(6, 13);
                basePoints[3] = new Point(13, 17);
                basePoints[4] = new Point(22, 20);
//                basePoints[5] = new Point(25, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - GRIDSIZE / 2; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 7://spip.FLAPNUMBERS: 
            {   basePoints[0] = new Point(4, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(16, 7);
                basePoints[3] = new Point(23, 7);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(14,18); 
                basePoints[6] = new Point(24,18); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 107://spip.FLIPNUMBERS: 
            {   basePoints[0] = new Point(4, 7);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(16, 7);
                basePoints[3] = new Point(23, 7);
                basePoints[4] = new Point(6, 18);
                basePoints[5] = new Point(14,18); 
                basePoints[6] = new Point(24,18); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 108://spip.TILENUMBERS1: 
            {   basePoints[0] = new Point(2, 3);
                basePoints[1] = new Point(3, 9);
                basePoints[2] = new Point(4, 17);
                basePoints[3] = new Point(13, 9);
                basePoints[4] = new Point(13, 20);
                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 109://spip.TILENUMBERS2: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 8://spip.HNUMBERS1: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(3, 12);
                basePoints[2] = new Point(8, 9);
                basePoints[3] = new Point(13, 11);
                basePoints[4] = new Point(18, 13);
                basePoints[5] = new Point(23, 15);
                basePoints[6] = new Point(28, 17);
//                basePoints[7] = new Point(24, 17);
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //-((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 9://spip.HNUMBERS2: 
            {   
                basePoints[0] = new Point(2, 4);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(12, 8);
                basePoints[3] = new Point(19, 10);
                basePoints[4] = new Point(4, 19);
                basePoints[5] = new Point(27, 19); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 10://spip.XNUMBERS1: 
            {   basePoints[0] = new Point(2, 4);
                basePoints[1] = new Point(7, 6);
                basePoints[2] = new Point(14, 8);
                basePoints[3] = new Point(23, 10);
                basePoints[4] = new Point(6, 19);
                basePoints[5] = new Point(24, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 100://spip.XNUMBERS2: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(7, 4);
                basePoints[2] = new Point(14, 6);
                basePoints[3] = new Point(23, 8);
                basePoints[4] = new Point(6, 19);
                basePoints[5] = new Point(21, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 11://spip.LNUMBERS1: 
            {   
                basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(9, 6);
                basePoints[2] = new Point(15, 7);
                basePoints[3] = new Point(23, 8);
                basePoints[4] = new Point(5, 17);
                basePoints[5] = new Point(15, 18);                 
                basePoints[6] = new Point(25, 19); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = - ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 200://spip.LNUMBERS2: 
            {   
                basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(3, 18);
                basePoints[2] = new Point(6, 11);
                basePoints[3] = new Point(13, 14);
                basePoints[4] = new Point(22, 17);
//                basePoints[5] = new Point(25, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE / 2; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 206://spip.TILENUMBERS3: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 18);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 207://spip.TILENUMBERS4: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(3, 8);
                basePoints[2] = new Point(4, 16);
                basePoints[3] = new Point(13, 8);
                basePoints[4] = new Point(13, 19);
                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 101://spip.TABLENUMBERS: 
            {   basePoints[0] = new Point(3, 5);
                basePoints[1] = new Point(10, 5);
                basePoints[2] = new Point(17, 5);
                basePoints[3] = new Point(26, 5);
                basePoints[4] = new Point(5, 11);
                basePoints[5] = new Point(16, 11); 
                basePoints[6] = new Point(6, 17); 
                basePoints[7] = new Point(19, 17);                 
                maxPatterns = 8;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 110://spip.STAIRNUMBERS: 
            {   basePoints[0] = new Point(2, 2);
                basePoints[1] = new Point(7, 3);
                basePoints[2] = new Point(14, 4);
                basePoints[3] = new Point(23, 5);
                basePoints[4] = new Point(6, 10);
                basePoints[5] = new Point(19, 13); 
                basePoints[6] = new Point(13, 19); 
                maxPatterns = 7;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 111://spip.ZNUMBERS: 
            {   
                basePoints[0] = new Point(3, 6);
                basePoints[1] = new Point(9, 7);
                basePoints[2] = new Point(15, 8);
                basePoints[3] = new Point(23, 9);
                basePoints[4] = new Point(5, 19);
                basePoints[5] = new Point(17, 20); 
                maxPatterns = 6;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 201://spip.INSECTNUMBERS: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 202://spip.SPIDERNUMBERS: 
            {   basePoints[0] = new Point(5, 4);
                basePoints[1] = new Point(13, 6);
                basePoints[2] = new Point(23, 8);
                basePoints[3] = new Point(8, 17);
                basePoints[4] = new Point(23, 20);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 208://spip.BLOCKNUMBERS2: 
            {   basePoints[0] = new Point(9, 3);
                basePoints[1] = new Point(14, 4);
                basePoints[2] = new Point(21, 5);
                basePoints[3] = new Point(3, 9);
                basePoints[4] = new Point(3, 16);
//                basePoints[5] = new Point(19, 19); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; // ((j + 1) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 209://spip.BLOCKNUMBERS3: 
            {   basePoints[0] = new Point(3, 3);
                basePoints[1] = new Point(4, 12);
                basePoints[2] = new Point(12, 11);
                basePoints[3] = new Point(24, 18);
//                basePoints[4] = new Point(23, 19);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 4;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //GRIDSIZE / 2; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 210://spip.TRIANUMBERS4: 
            {   basePoints[0] = new Point(4, 4);
                basePoints[1] = new Point(6, 12);
                basePoints[2] = new Point(17, 9);
                basePoints[3] = new Point(29, 19);
//                basePoints[4] = new Point(23, 19);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 4;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = GRIDSIZE; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            case 211://spip.PIRAMIDNUMBERS: 
            {   basePoints[0] = new Point(2, 3);
                basePoints[1] = new Point(4, 9);
                basePoints[2] = new Point(6, 18);
                basePoints[3] = new Point(14, 16);
                basePoints[4] = new Point(25, 17);
//                basePoints[5] = new Point(25, 16); 
//                basePoints[6] = new Point(24,18); 
                maxPatterns = 5;
                for (int j = 0; j < maxPatterns; j++)
                {   labels[j] = "n = " + (j + 1);
                    corrections[j] = 0; //- ((j) % 2) * (GRIDSIZE / 2);
                }
            }
            break;
            
            default: // nothing
        }    
        showDWOPattern(0);
        showNextPattern();
        showNextPattern();
    }    

    public void showDWOPattern(int n)
    {   drawColor = spotColors[n];
        switch(problemNumber)
        {   case 0://spip.VNUMBERS: // start at n = 1
            {   drawVNumber(n + 1, basePoints[n].x - (n + 1),
                    basePoints[n].y - (n + 2));
            }
            break;
            case 1://spip.WNUMBERS: // start at n = 1
            {   drawWNumber(n + 1, basePoints[n].x - (2 * (n + 1)),
                    basePoints[n].y - (n + 2));
            }
            break;
            case 2://spip.SQRNUMBERS: // start at n = 1
            {   drawSquare(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 102://spip.OBLNUMBERS: // start at n = 1
            {   drawOblong(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 103://spip.TRIANUMBERS1: // start at n = 1
            {   drawTriang1Number(n + 2, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));            
            }    
            break;
            case 104://spip.TRIANUMBERS2: // start at n = 1
            {   drawTriang2Number(n + 2, basePoints[n].x - n,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 203://spip.PENTANUMBERS: // start at n = 1
            {   // nothing for n = 1
                drawTriang2Number(n + 1, basePoints[n].x - (n - 1),
                    basePoints[n].y - (2 * n + 1));
                // single dot for n = 1   
                drawSpacedSquare(n + 1, basePoints[n].x - n,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 3://spip.BOXNUMBERS: // start at n = 1
            {   drawBox(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 2));
            }    
            break;
            case 4://spip.PLUSNUMBERS: // start at n = 1
            {   drawPlus(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 204://spip.SPIRALNUMBERS: // start at n = 1
            {   drawSpiral(n + 1, basePoints[n].x - n / 2 - n % 2,
                    basePoints[n].y - (n + 1));
            }
            break;
            case 105://spip.TRIANUMBERS3: // start at n = 1
            {   drawTriang3Number(n + 1, basePoints[n].x - n,
                    basePoints[n].y - n - 1);
            }    
            break;
            case 5://spip.TOWERNUMBERS: // start at n = 1
            {   drawTowerNumber(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - n - 2);
            }    
            break;
            case 205://spip.BLOCKNUMBERS1: // start at n = 1
            {   switch (n)
                {   case 0: drawRectangle(2, 1, basePoints[0].x - 1,
                                basePoints[0].y - 1);
                    break;
                    case 1: drawRectangle(2, 2, basePoints[1].x - 1,
                                basePoints[1].y - 2);
                    break;
                    case 2: drawRectangle(4, 2, basePoints[2].x - 2,
                                basePoints[2].y - 2);
                    break;
                    case 3: drawRectangle(4, 4, basePoints[3].x - 2,
                                basePoints[3].y - 4);
                    break;
                    case 4: drawRectangle(8, 4, basePoints[4].x - 4,
                                basePoints[4].y - 4);
                    break;
                    case 5: drawRectangle(8, 8, basePoints[5].x - 4,
                                basePoints[5].y - 8);
                    break;
                    case 6: drawRectangle(16, 8, basePoints[6].x - 8,
                                basePoints[6].y - 8);
                    break;
                    default:
                }    
            }    
            break;
            case 6://spip.FNUMBERS1: // start at n = 1
            {   drawFNumber1(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - (2 * n + 4));
            }    
            break;
            case 106://spip.FNUMBERS2: // start at n = 1
            {   drawFNumber2(n + 1, basePoints[n].x,
                    basePoints[n].y - (4 * n + 4));
            }    
            break;
            case 7://spip.FLAPNUMBERS: // start at n = 1
            {   drawFlapNumber(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 107://spip.FLIPNUMBERS: // start at n = 1
            {   drawFlipNumber(n + 1, basePoints[n].x - (n + 1) / 2 - (n + 1) % 2,
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 108://spip.TILENUMBERS1: // start at n = 1
            {   drawTileNumber1(n + 1, basePoints[n].x - (2 * n + 1) / 2 - 1,
                    basePoints[n].y - (2 * n + 2));
            }    
            break;
            case 109://spip.TILENUMBERS2: // start at n = 1
            {   drawTileNumber2(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 8://spip.HNUMBERS1: // start at n = 1
            {   drawHNumber1(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - 2 * n - 3);
            }    
            break;
            case 9://spip.HNUMBERS2: // start at n = 1
            {   drawHNumber2(n + 1, basePoints[n].x - ((n + 2) / 2) - ((n + 2) % 2),
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 10://spip.XNUMBERS1: // start at n = 1
            {   drawXNumber1(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 100://spip.XNUMBERS2: // start at n = 0
            {   drawXNumber1(n, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 1));
            }    
            break;
            case 11://spip.LNUMBERS1: // start at n = 1
            {   drawLNumber1(n + 1, basePoints[n].x - (n / 2) - (n % 2),
                    basePoints[n].y - (n + 3));
            }    
            break;
            case 200://spip.LNUMBERS2: // start at n = 1
            {   drawLNumber2(n + 1, basePoints[n].x - 1,
                    basePoints[n].y - (3 * n + 3));
            }    
            break;
            case 206://spip.TILENUMBERS3: // start at n = 1
            {   drawTileNumber3(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 207://spip.TILENUMBERS4: // start at n = 1
            {   drawTileNumber4(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (2 * n + 1));
            }    
            break;
            case 101://spip.TABLENUMBERS: // start at n = 1
            {   drawTableNumber(n + 1, basePoints[n].x - (n + 2) / 2 - (n + 2) % 2,
                    basePoints[n].y - 3);
            }    
            break;
            case 110://spip.STAIRNUMBERS: // start at n = 1
            {   drawStairNumber(n + 1, basePoints[n].x - n,
                    basePoints[n].y - n - 1);
            }    
            break;
            case 111://spip.ZNUMBERS: // start at n = 1
            {   drawZNumber(n + 1, basePoints[n].x - (n + 2) / 2 - (n + 2) % 2,
                    basePoints[n].y - (n + 5));
            }    
            break;
            case 201://spip.INSECTNUMBERS: // start at n = 1
            {   drawInsectNumber(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 202://spip.SPIDERNUMBERS: // start at n = 1
            {   drawSpiderNumber(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2 - 1,
                    basePoints[n].y - (2 * n + 3));
            }    
            break;
            case 208://spip.BLOCKNUMBERS2: // start at n = 1
            {   drawBlockNumber2(n + 1, basePoints[n].x, // - (n * n) / 2 - (n * n) % 2,
                    basePoints[n].y - (n + 1));
            }    
            break;
            case 209://spip.BLOCKNUMBERS3: // start at n = 1
            {   drawBlockNumber3(n + 1, basePoints[n].x - (2 * n + 1) / 2 - (2 * n + 1) % 2,
                    basePoints[n].y - (n * n + 2 * n + 1));
            }    
            break;
            case 210://spip.TRIANUMBERS4: // start at n = 1
            {   int width = (int) Math.pow(2, n + 2) - 1;
                int height = (int) Math.pow(2, n + 1) - 1;
                drawTriang4Number(n + 1, basePoints[n].x - (width) / 2 - (width) % 2,
                    basePoints[n].y - (height + 1));
            }    
            break;
            case 211://owner.PIRAMIDNUMBERS: // start at n = 1
            {   int width = (n + 1) * (n + 2) / 2;
                int height = (n + 1) * (n + 2) / 2;
                drawPiramidNumber(n + 1, basePoints[n].x - (width - n - 1) - (width - n - 1) % 2,
                    basePoints[n].y - (height));
            }    
            break;
            
            
            default: // nothing
        }    
    }    
    public void showNextPattern()
    {   patternsShown++;
        if (patternsShown < maxPatterns)
        {   //if (spip == null)
        	//	showPattern(patternsShown);
        	//else
        		showDWOPattern(patternsShown);
            paint();
        }    
    }    
    
    public void showAllPatterns()
    {   for (int i = patternsShown + 1; i < maxPatterns; i++)
        {   //if (spip == null)
    		//	showPattern(i);
        	//else
        		showDWOPattern(i);
        }    
        patternsShown = maxPatterns - 1;
        paint();
    }    
    
    public void paint()
    {
    	paintComponent(stippelContext2d);
    }
    
    // paint method
    //public void paintComponent(Graphics g)
    public void paintComponent(Context2d g)
    {   // white background
        //g.setColor(Color.white);
    	g.setFillStyle(CssColor.make(255,255,255));
        //g.fillRect(0, 0, getSize().width, getSize().height);
    	g.fillRect(0, 0, breedte, hoogte);
        // grid elements
        for (int i = 0; i < horSize; i++)
            for (int j = 0; j < vertSize; j++)
                grid[i][j].paint(g);
        // outline the drawing container in black, right border is one pixel to the right
        //g.setColor(Color.lightGray);
        g.setStrokeStyle(CssColor.make(220,220,220));
        //g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        g.strokeRect(0, 0, breedte, hoogte);
        // labels
        //g.setColor(Color.black);
        g.setFillStyle(CssColor.make(0,0,0));
        //labelFont = new Font(getFont().getName(), Font.BOLD, getFont().getSize());
        //labelFM = getFontMetrics(labelFont);
        g.setFont(labelFont);
        for (int k = 0; k <= patternsShown; k++)
        {   
// tijdelijk if????           
            if ((labels[k] != null) &&
                (basePoints[k] != null))
               //g.drawString(labels[k], 
               //    basePoints[k].x * GRIDSIZE - GRIDSIZE / 3 - corrections[k],
               //     basePoints[k].y * GRIDSIZE + labelFM.getHeight());
            	g.fillText(labels[k], 
                    basePoints[k].x * GRIDSIZE - GRIDSIZE / 2 - corrections[k],
                    basePoints[k].y * GRIDSIZE + 20);
            
        }    
/*        
        // version info
        Font fo = getFont();
        FontMetrics fm = getFontMetrics(fo);
        g.setFont(fo);
        int bx = fm.stringWidth(" ");
        int by = getSize().height - fm.getDescent();
        g.drawString(
            owner.languageTable.lookUp("spotProblemsText"),
            bx, by);
*/            
    } // paint


    public void drawVNumber(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(SOUTHEAST);
        drawLine(n + 1);
        turnTurtle(NORTHEAST);
        drawLine(n + 1);
    }

    public void drawWNumber(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(SOUTHEAST);
        drawLine(n + 1);
        turnTurtle(NORTHEAST);
        drawLine(n + 1);
        turnTurtle(SOUTHEAST);
        drawLine(n + 1);
        turnTurtle(NORTHEAST);
        drawLine(n + 1);        
    }

    public void drawSquare(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= n; i++)
        {   turnTurtle(EAST);
            drawLine(n);
            turnTurtle(WEST);
            moveTurtle(n - 1);
            turnTurtle(SOUTH);
            moveTurtle(1);
        }    
        
    }    
    public void drawOblong(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        Point start = turtlePosition;
        drawSquare(n, xPos, yPos);
        turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        moveTurtle(n);
        turnTurtle(SOUTH);
        drawLine(n);
    }    
    
    public void drawTriang1Number(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 2; i <= n; i++)
        {   turnTurtle(SOUTH);
            drawLine(n + 1 - i);
            turnTurtle(NORTH);
            moveTurtle(n - 1 - i);
            turnTurtle(EAST);
            moveTurtle(1);
        }    
        
        
    }    

    public void drawTriang2Number(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos + n - 2);
        for (int i = 2; i <= n; i++)
        {   turnTurtle(NORTHEAST);
            drawLine(n + 1 - i);
            turnTurtle(SOUTHWEST);
            moveTurtle(n - i);
            turnTurtle(EAST);
            moveTurtle(2);
        }    
    }    

    public void drawSpacedSquare(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= n; i++)
        {   turnTurtle(EAST);
            drawSpacedLine(n);
            turnTurtle(WEST);
            moveTurtle(2 * n - 2);
            turnTurtle(SOUTH);
            moveTurtle(1);
        }    
        
    }    

    public void drawBox(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        drawLine(n + 1);
        turnTurtle(SOUTH);
        drawLine(n + 1);
        turnTurtle(WEST);
        drawLine(n + 1);
        turnTurtle(NORTH);
        drawLine(n + 1);
    }    

    public void drawPlus(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        moveTurtle(n);
        turnTurtle(SOUTH);
        drawLine(2 * n + 1);
        turnTurtle(WEST);
        moveTurtle(n);
        turnTurtle(NORTH);
        moveTurtle(n);
        turnTurtle(EAST);
        drawLine(2 * n + 1);
        
    }    

    public void drawSpiral(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        int count = n;
        while (count >= 1)
        {   drawLine(count + 1);
            rotateTurtle(2);
            count--;
        }
        
    }    
    
    public void drawTriang3Number(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= n; i++)
        {   turnTurtle(EAST);
            moveTurtle(n - i);
            drawLine(2 * i - 1);
            turtlePosition = new Point(xPos, yPos + i);
        }    
    }    

    public void drawTowerNumber(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        moveTurtle(1);
        drawLine(1);
        turtlePosition = new Point(xPos, yPos + 1);
        for (int i = 1; i <= n; i++)
        {   turnTurtle(EAST);
            drawLine(3);
            turtlePosition = new Point(xPos, yPos + i + 1);
        }    
    }    
    
    public void drawFNumber1(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(EAST);
        drawLine(n + 1);
        turnTurtle(WEST);
        moveTurtle(n);
        turnTurtle(SOUTH);
        drawLine(2 * n + 2);
        turnTurtle(NORTH);
        moveTurtle(n);
        turnTurtle(EAST);
        drawLine(n + 1);
    }    
    
    public void drawFNumber2(int n, int xPos, int yPos)
    {   drawRectangle(2 * n, n, xPos, yPos);
        drawRectangle(2 * n, n, xPos, yPos + 2 * n);
        drawRectangle(n, 4 * n, xPos, yPos);
    }    

    public void drawFlapNumber(int n, int xPos, int yPos)
    {   drawRectangle(n, 1, xPos + 1, yPos);
        drawRectangle(n, 1, xPos + 1, yPos + n + 1);
        drawRectangle(1, n, xPos, yPos + 1);
        drawRectangle(1, n, xPos + n + 1, yPos + 1);
    }    

    public void drawFlipNumber(int n, int xPos, int yPos)
    {   drawRectangle(1, 1, xPos, yPos);
        drawRectangle(1, 1, xPos + n + 1, yPos);
        drawRectangle(1, 1, xPos, yPos + n + 1 );
        drawRectangle(1, 1, xPos + n + 1, yPos + n + 1 );
        drawRectangle(n, n, xPos + 1, yPos + 1);
    }    

    
    public void drawTileNumber1(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= 2 * n; i++)
        {   turnTurtle(EAST);
            if ((i % 2) == 0)
               moveTurtle(1);
            drawSpacedLine(n);
            turnTurtle(WEST);
            moveTurtle(2 * n - 2);
            if ((i % 2) == 0)
               moveTurtle(1);
            turnTurtle(SOUTH);
            moveTurtle(1);
        }    
    }    
    

    public void drawTileNumber2(int n, int xPos, int yPos)
    {   drawPlus(n, xPos + 1, yPos);
        drawRectangle(1, n, xPos, yPos);
        drawRectangle(1, n, xPos, yPos + n + 1);
        drawRectangle(1, n, xPos + 2 * n + 2, yPos);
        drawRectangle(1, n, xPos + 2 * n + 2, yPos + n + 1);
        
    }    
    
    public void drawHNumber1(int n, int xPos, int yPos)
    {   drawRectangle(1, 2 * n + 1, xPos, yPos);
        drawRectangle(1, 2 * n + 1, xPos + 2, yPos);
        drawRectangle(1, 1, xPos + 1, yPos + n);
    }    
    
    public void drawHNumber2(int n, int xPos, int yPos)
    {   drawRectangle(1, 2 * n + 1, xPos, yPos);
        drawRectangle(1, 2 * n + 1, xPos + n + 1, yPos);
        drawRectangle(n, 1, xPos + 1, yPos + n);
    }    
    
    public void drawLNumber1(int n, int xPos, int yPos)
    {   drawRectangle(1, n + 2, xPos, yPos);
        drawRectangle(n + 1, 1, xPos, yPos + n + 1);
    }    

    public void drawLNumber2(int n, int xPos, int yPos)
    {   drawRectangle(n, 3 * n, xPos, yPos);
        drawRectangle(2 * n, n, xPos, yPos + 2 * n);
    }    


    public void drawXNumber1(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(SOUTHEAST);
        drawLine(2 * n + 1);
        turnTurtle(WEST);
        moveTurtle(2 * n);
        turnTurtle(NORTHEAST);
        drawLine(2 * n + 1);
    }    
    
    public void drawTileNumber3(int n, int xPos, int yPos)
    {   drawRectangle(n, n, xPos + 1, yPos);
        drawRectangle(n, n, xPos + 1, yPos + n + 1);
        drawRectangle(n, n, xPos + n + 2, yPos);
        drawRectangle(n, n, xPos + n + 2, yPos + n + 1);        
        drawRectangle(1, 1, xPos, yPos + n);
        drawRectangle(1, 1, xPos + 2 * n + 2, yPos + n);
    }    

    public void drawTileNumber4(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= 2 * n - 1; i++)
        {   turnTurtle(EAST);
            if ((i % 2) == 0)
            {   moveTurtle(1);
                drawSpacedLine(n - 1);
                turnTurtle(WEST);
                moveTurtle(2 * n - 2);
            }
            else
            {   moveTurtle(1);
                drawSpacedLine(n);
                turnTurtle(WEST);
                moveTurtle(2 * n - 2);
            }    
            turnTurtle(SOUTH);
            moveTurtle(1);
        }    
    }    

    public void drawZNumber(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos + n + 2);
        turnTurtle(NORTHEAST);
        drawLine(n + 2);
        drawRectangle(n + 2, 1, xPos, yPos);
        drawRectangle(n + 2, 1, xPos, yPos + n + 3);
    }    

    public void drawStairNumber(int n, int xPos, int yPos)
    {   for (int i = 1; i <= n; i++)
        {   drawRectangle(1 + (i - 1) * 2, 1, xPos, yPos + (i - 1));
        }
    }    
    

    public void drawTableNumber(int n, int xPos, int yPos)
    {   drawRectangle(n, 1, xPos + 1, yPos);
        drawRectangle(n, 1, xPos + 1, yPos + 2);
        drawRectangle(1, 1, xPos, yPos + 1);
        drawRectangle(1, 1, xPos + n + 1, yPos + 1);        
    }    
    
    public void drawInsectNumber(int n, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        turnTurtle(SOUTHEAST);
        drawLine(n);
        turtlePosition = new Point(xPos, yPos + 2 * n);
        turnTurtle(NORTHEAST);
        drawLine(n);
        turtlePosition = new Point(xPos + 2 * n + 2, yPos);
        turnTurtle(SOUTHWEST);
        drawLine(n);
        turtlePosition = new Point(xPos + 2 * n + 2, yPos + 2 * n);
        turnTurtle(NORTHWEST);
        drawLine(n);
        
        drawRectangle(3, 1, xPos + n, yPos + n);
        drawRectangle(1, 2 * n + 1, xPos + n + 1, yPos);
    }    

    public void drawSpiderNumber(int n, int xPos, int yPos)
    {   drawInsectNumber(n, xPos, yPos);
        drawRectangle(2 * n + 3, 1, xPos, yPos + n);
    }    

    public void drawBlockNumber2(int n, int xPos, int yPos)
    {   for (int i = 1; i <= n; i++)
        { drawRectangle(n, n, xPos + (i - 1) * n, yPos + (i - 1));
        }
    }    

    public void drawBlockNumber3(int n, int xPos, int yPos)
    {   for (int i = 1; i <= n; i++)
        { drawRectangle(2 * n, n, xPos + (i - 1), yPos + (i - 1) * n);
        }
    }    
    
    public void drawTriang4Number(int n, int xPos, int yPos)
    {   if (n == 0)
            drawRectangle(1, 1, xPos, yPos);
        else
        {   int w = (int) Math.pow(2, n) - 1;
            int h = (int) Math.pow(2, n - 1);
            drawTriang4Number(n - 1, xPos + h, yPos);
            drawTriang4Number(n - 1, xPos, yPos + h);
            drawTriang4Number(n - 1, xPos + w / 2 + 1, yPos + h);
        }    
    }    

    public void drawPiramidNumber(int n, int xPos, int yPos)
    {   for (int i = 1; i <= n; i++)
        {   drawRectangle(i, i, xPos + (i - 1) * i / 2, yPos + (i - 1) * i / 2);
        }    
    }    
    
    
    public void drawRectangle(int w, int h, int xPos, int yPos)
    {   turtlePosition = new Point(xPos, yPos);
        for (int i = 1; i <= h; i++)
        {   turnTurtle(EAST);
            drawLine(w);
            turnTurtle(WEST);
            moveTurtle(w - 1);
            turnTurtle(SOUTH);
            moveTurtle(1);
        }    
        
    }    
    
    public void drawLine(int length)
    {   for (int i = 1; i <= length; i++)
        {   if ((turtlePosition.x >= 0) &&
                (turtlePosition.x < horSize) &&
                (turtlePosition.y >= 0) &&
                (turtlePosition.y < vertSize))
            {
            GridElement ge = 
                grid[turtlePosition.x][turtlePosition.y];
            ge.gColor = drawColor;    
            ge.visible = true;
            if (i < length)
                moveTurtle(1);
            }    
        }    
    }    

    public void drawSpacedLine(int length)
    {   for (int i = 1; i <= length; i++)
        {   if ((turtlePosition.x >= 0) &&
                (turtlePosition.x < horSize) &&
                (turtlePosition.y >= 0) &&
                (turtlePosition.y < vertSize))
            {
            GridElement ge = 
                grid[turtlePosition.x][turtlePosition.y];
            ge.gColor = drawColor;    
            ge.visible = true;
            if (i < length)
                moveTurtle(2);
            }    
        }    
    }    

    public void turnTurtle(int dir)
    {   turtleDirection = dir;
    }    
    // in steps of 45 degrees
    public void rotateTurtle(int step)
    {   turtleDirection =
            (turtleDirection + step) % 8;
    }    
    public void moveTurtle(int steps)
    {   switch (turtleDirection)
        {   case NORTH:
            {   turtlePosition.y -= steps;
            }
            break;
            case NORTHEAST:
            {   turtlePosition.x += steps;
                turtlePosition.y -= steps;
            }
            break;                
            case EAST:
            {   turtlePosition.x += steps;
            }
            break;                
            case SOUTHEAST:
            {   turtlePosition.x += steps;
                turtlePosition.y += steps;
            }
            break;                
            case SOUTH:
            {   turtlePosition.y += steps;
            }
            break;                
            case SOUTHWEST:
            {   turtlePosition.x -= steps;
                turtlePosition.y += steps;
            }
            break;                
            case WEST:
            {   turtlePosition.x -= steps;
            }
            break;                
            case NORTHWEST:
            {   turtlePosition.x -= steps;
                turtlePosition.y -= steps;
            }
            break;                
            default: // nothing                
        }    
    }    

    public void removeAll()
    {   draw(new Rectangle(0, 0, horSize, vertSize), false);
    }    

    // fill/remove all spots in rectangle r    
    // use current color!
    public void draw(Rectangle r, boolean on)
    {   for (int i = 0; i < r.width; i++)
            for (int j = 0; j < r.height; j++)
            {   grid[r.x + i][r.y + j].gColor = drawColor;
                grid[r.x + i][r.y + j].visible = on;
            }
    }    

/*
    // count all spots by color, find total
    public void updateCounts()
    {   if (counting)
        {   int totalCount = 0;
            int[] colorCount = new int[MAXCOLORS];
            for (int i = 0; i < horSize; i++)
                for (int j = 0; j < vertSize; j++)
                {   // count all spots if none are selected
                    if (selectedRect == null)
                    {   if (grid[i][j].visible)
                        {   totalCount++;
                            for (int k = 0; k < MAXCOLORS; k++)
                                if (grid[i][j].gColor == spotColors[k])
                                    colorCount[k]++;
                        }
                    }    
                    else // count only selected spots
                    {   if ((grid[i][j].visible) && (grid[i][j].selected))
                        {   totalCount++;
                            for (int k = 0; k < MAXCOLORS; k++)
                                if (grid[i][j].gColor == spotColors[k])
                                    colorCount[k]++;
                        }
                    }    
                
                }
            if (selectedRect == null)    
                owner.totalLabel.setText(
                    owner.languageTable.lookUp("totalText") + 
                    "  " + totalCount);
            else
                owner.totalLabel.setText(
                    owner.languageTable.lookUp("totalSelectedText") + 
                    "  " + totalCount);
            for (int m = 0; m < MAXCOLORS; m++)
                owner.colorLabels[m].setText("" + colorCount[m]);
        } // if (counting)
        else // numbers off
        {   for (int m = 0; m < MAXCOLORS; m++)
                owner.colorLabels[m].setText("");
        }        
    } // updateCounts    
*/

    public Vector getUserSpots()
    {	
    	Vector result = new Vector();
    	
    	for (int hCnt = 0; hCnt < horSize; hCnt++)
    		for (int vCnt = 0; vCnt < vertSize; vCnt++)
    		{
    			if (grid[hCnt][vCnt].visible && grid[hCnt][vCnt].gColor.equals(CssColor.make(220,220,220)))
    				result.addElement(new Point(grid[hCnt][vCnt].gX, grid[hCnt][vCnt].gY));
    		}
    	
    	return result;
    }
    
    public void setUserSpots(Vector v)
    {
    	for (int sCnt = 0; sCnt < v.size(); sCnt++)
   		{
    		Point p = (Point) v.elementAt(sCnt);
    		grid[p.x][p.y].visible = true;
//GWT    		
    		grid[p.x][p.y].gColor = CssColor.make(220,220,220); //lightGray;
   		}
    	
    }
    
    public void mouseDownTouchStartAction(int eventX, int eventY)
    {   
    	drawColor = CssColor.make(220,220,220); //Color.lightGray;
    	
    	// calculate grid element where
        // press took place
        int i = eventX / GRIDSIZE;
        int j = eventY / GRIDSIZE;
        // avoid going over the edges
        if ((i >= 0) && (i < horSize) &&
            (j >= 0) && (j < vertSize))
        {   // check userMode
            // drawing/removing individual spots 
            //if (userMode == DRAWCELLMODE)
            {   // check if grid element contains press            
                if (grid[i][j].contains(eventX, eventY))
                {   // if colored, change color
                    if (!grid[i][j].visible)
                    {	grid[i][j].visible = true;
                    	grid[i][j].gColor = drawColor;
                    }
                    //else if (grid[i][j].visible && grid[i][j].gColor.equals(drawColor))
                    else if (grid[i][j].visible && grid[i][j].gColor.value().equals(drawColor.value()))
                    {	grid[i][j].visible = false;
                    }
                            
                } // if contains
            } // if (userMode == DRAWCELLMODE)
            paint();
        } // if within edges    
    } // mouseDownTouchStart
    
    
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			
			// prevent scrolling 
			e.stopPropagation();
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouse move veld");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();


//System.out.println("sp = " + shiftPressed);

			//mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			//mouseUpTouchEndAction();

		}

	} //MLMML


	// tablet, dwo 
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
				int eventX = touch.getPageX() - stippelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - stippelCanvas.getAbsoluteTop();				
				
				mouseDownTouchStartAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchMove(TouchMoveEvent e)
		{
			
			e.preventDefault();
			e.stopPropagation();
			
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				
			    int eventX = touch.getPageX() - stippelCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - stippelCanvas.getAbsoluteTop();				
			    
				//mouseMoveTouchMoveAction(eventX, eventY, shiftPressed);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			//mouseUpTouchEndAction();
		}

	}

/*    
   // inner class processing mouse events
    class MLMML extends MouseAdapter implements MouseMotionListener
    {   // mouse pressed events
        public void mousePressed(MouseEvent e)
        {   
        	drawColor = Color.lightGray;
        	
        	// calculate grid element where
            // press took place
            int i = e.getX() / GRIDSIZE;
            int j = e.getY() / GRIDSIZE;
            // avoid going over the edges
            if ((i >= 0) && (i < horSize) &&
                (j >= 0) && (j < vertSize))
            {   // check userMode
                // drawing/removing individual spots 
                //if (userMode == DRAWCELLMODE)
                {   // check if grid element contains press            
                    if (grid[i][j].contains(e.getX(), e.getY()))
                    {   // if colored, change color
                        if (!grid[i][j].visible)
                        {	grid[i][j].visible = true;
                        	grid[i][j].gColor = drawColor;
                        }
                        else if (grid[i][j].visible && grid[i][j].gColor==drawColor)
                        {	grid[i][j].visible = false;
                        }
                                
                    } // if contains
                } // if (userMode == DRAWCELLMODE)
                paint();
            } // if within edges    
        } // mousePressed
        
        // mouse released events
        public void mouseReleased(MouseEvent e)
        {  
        } // mouseReleased
        
        public void mouseDragged(MouseEvent e)
        {   // calculate grid element where dragg
            // took place
            int i = e.getX() / GRIDSIZE;
            int j = e.getY() / GRIDSIZE;
            // check user mode
            // drawing/removing individual spots
            if (userMode == DRAWCELLMODE)
            {   if ((i >= 0) && (i < horSize) &&
                    (j >= 0) && (j < vertSize) &&
                    grid[i][j].contains(e.getX(), e.getY()))
                {   // if colored, change color
                    //if (grid[i][j].visible);
                        //grid[i][j].gColor = drawColor;
                } // if contains
            } // if (userMode == DRAWCELLMODE)
            paint();
        } // mouseDragged    
        // mouse moved events, not used
        public void mouseMoved(MouseEvent e) {}
    } // class MLMML
*/
} // class DrawContainer

