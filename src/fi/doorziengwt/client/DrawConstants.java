package fi.doorziengwt.client;

import com.google.gwt.canvas.dom.client.CssColor;

public class DrawConstants 
{
    // defined colors
    // #299C39
	public static final CssColor darkGreen = CssColor.make(41, 156, 57);
    // #ADDE63
    public static final CssColor mediumGreen = CssColor.make(173, 222, 99);
    // #D60000
    public static final CssColor brownRed = CssColor.make(214, 0, 0);
    // #FF8429
    public static final CssColor lightRed = CssColor.make(255, 156, 74);
    // #63C6DE
    public static final CssColor mediumBlue = CssColor.make(99, 198, 222);    
    // #94D6E7
    public static final CssColor lightBlue = CssColor.make(148, 214, 231);
    // #FFE7C6
    public static final CssColor lightOrange = CssColor.make(255, 231, 198);
    // #EFE8AD
    public static final CssColor lightYellow = CssColor.make(239, 232, 173);
    
    public static final CssColor black = CssColor.make(0,0,0);
    public static final CssColor blue = CssColor.make(0,0,255);
    public static final CssColor yellow = CssColor.make(255,255,0);
    public static final CssColor red = CssColor.make(255,0,0);
    public static final CssColor lightGray = CssColor.make(192,192,192); 
    public static final CssColor magenta = CssColor.make(255,0,255); 
    public static final CssColor white = CssColor.make(255,255,255);
	
    public static CssColor objectColor = yellow;
    public static CssColor outlineColor = black;
    public static CssColor lineColor = blue;
    public static CssColor planeColor = yellow; 
    public static CssColor planeOutlineColor = brownRed;    
    public static CssColor pointColor = darkGreen;
    public static CssColor tickColor = darkGreen;
    public static CssColor hiddenTickColor = mediumGreen;    
    
    public static CssColor flatButtonColor = mediumGreen;

	public static CssColor[] edgeColors =
    	{outlineColor, lineColor, planeOutlineColor, pointColor};
	
    // color indices in edgeColors   
    public static int lineColorIndex = 1;
    public static int planeOutlineColorIndex = 2;
    public static int pointColorIndex = 3;
	
	public static int hiddenOutlineMode = 0;
	
	public static boolean letters = false;
	
    public static int TICKNUM = 0;    
    public static boolean TICKSVISIBLE = false;
	
    public static double llFactor = 0;
    
    public static void reset()
    {
    	letters = false;
    	TICKNUM = 0;
    	TICKSVISIBLE = false;
    	llFactor = 0;
    }
    
    // sensitivity for clicking vertices and edges in pixels
    public static int mouseSSTT = 8;
    public static int touchSSTT = 8;
    public static int SSTT = mouseSSTT;

    // pinching
    public static int READY = 0;
    public static int ONE_FINGER = 1;
    public static int TWO_FINGERS = 2;
    
}
