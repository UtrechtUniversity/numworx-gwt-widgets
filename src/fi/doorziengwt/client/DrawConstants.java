package fi.doorziengwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

/**
 * class with global constants
 * @author huub
 */
public class DrawConstants 
{
	/**
	 * predefined colors
	 */
	public static final CssColor darkGreen = CssColor.make(41, 156, 57);
    public static final CssColor mediumGreen = CssColor.make(173, 222, 99);
    public static final CssColor brownRed = CssColor.make(214, 0, 0);
    public static final CssColor lightRed = CssColor.make(255, 156, 74);
    public static final CssColor mediumBlue = CssColor.make(99, 198, 222);    
    public static final CssColor lightBlue = CssColor.make(148, 214, 231);
    public static final CssColor lightOrange = CssColor.make(255, 231, 198);
    public static final CssColor lightYellow = CssColor.make(239, 232, 173);
    public static final CssColor black = CssColor.make(0,0,0);
    public static final CssColor blue = CssColor.make(0,0,255);
    public static final CssColor yellow = CssColor.make(255,255,0);
    public static final CssColor red = CssColor.make(255,0,0);
    public static final CssColor lightGray = CssColor.make(192,192,192); 
    public static final CssColor magenta = CssColor.make(255,0,255); 
    public static final CssColor white = CssColor.make(255,255,255);

    /**
     * colors of various parts of 3d-objects
     */
    public static CssColor objectColor = yellow;
    public static CssColor outlineColor = black;
    public static CssColor lineColor = blue;
    public static CssColor planeColor = yellow; 
    public static CssColor planeOutlineColor = brownRed;    
    public static CssColor pointColor = darkGreen;
    public static CssColor tickColor = darkGreen;
    public static CssColor hiddenTickColor = mediumGreen;    
    
    /**
     * button for flattening foldout
     */
    public static CssColor flatButtonColor = mediumGreen;

    /**
     * possible edgecolors, see class Facet3D
     */
	public static CssColor[] edgeColors =
    	{outlineColor, lineColor, planeOutlineColor, pointColor};
	
	/**
	 * color indices in edgeColors
	 */
    public static int lineColorIndex = 1;
    public static int planeOutlineColorIndex = 2;
    public static int pointColorIndex = 3;
	
    /**
     * drawing "hidden" edges, see class Facet3D
     */
	public static int hiddenOutlineMode = 0;
	
	/**
	 * lettering vertices
	 */
	public static boolean letters = false;
	
	/**
	 * number of tickmarks, visibility of tickmarks on edges
	 */
    public static int TICKNUM = 0;    
    public static boolean TICKSVISIBLE = false;
	
    /**
     * constants for lengthening lines
     */
    public static double MAXLLFACTOR = 3;
    public static double LLSTEP = 2e-1d;
    /**
     * factor by which lines are lengthened
     */
    public static double llFactor = 0;
    
    /**
     * reset
     */
    public static void reset()
    {
    	letters = false;
    	TICKNUM = 0;
    	TICKSVISIBLE = false;
    	llFactor = 0;
    }
    
    /**
     * sensitivity for clicking/touching vertices and edges in pixels
     */
    public static int mouseSSTT = 8;
    public static int touchSSTT = 8;
    public static int SSTT = mouseSSTT;

    /**
     * pinching on the tablet for zooming
     */
    public static int READY = 0;
    public static int ONE_FINGER = 1;
    public static int TWO_FINGERS = 2;
    
}
