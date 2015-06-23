package fi.verknippengwt.client;


//import java.awt.*;
//import java.awt.event.*;
import java.util.*;

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

import com.google.gwt.user.client.ui.PopupPanel;

//import javax.swing.*;


public class DrawingPanel2 extends LayoutPanel //extends JPanel
{	
	//VerknippenInteractiePanel owner;
	VerknippenGWT owner;
	
	//Color bgColor = Color.white;
	CssColor bgColor = CssColor.make(255,255,255);
	//Color outlineColor = Color.black;
	CssColor outlineColor = CssColor.make(0,0,0);
	//Color gridColor = Color.lightGray;
	CssColor gridColor = CssColor.make(146,146,146);
	
	
	//Color polyColorTransparent = new Color(255, 0, 0, 175);
	//CssColor polyColorTransparent = CssColor.make(255,0,0);
	
	//Color polyColorSolid = Color.red;
	CssColor polyColorSolid = CssColor.make(255,0,0);
	
	
	//Color polyColor = polyColorTransparent;
	CssColor polyColor = polyColorSolid;
	boolean figuurTransparant = false;
	
	//Color shadowColor = Color.lightGray;
	CssColor shadowColor = CssColor.make(146,146,126);
	//Color grijsPolyColor = Color.gray;
	CssColor grijsPolyColor = CssColor.make(128,128,128);
	//Color sizeColor = Color.black;
	CssColor sizeColor = CssColor.make(0,0,0);
	//Color labelColor = Color.green;
	CssColor labelColor = CssColor.make(0,255,0);
	
	boolean showGrid = false;
	
	boolean showBorder = true;
	
	boolean gridOnTop = false;
	// dit is de default
	int gridSize = 20;

	KnipPolygon2 shadowPolygon;
	KnipPolygon2 grijsPolygon;
	boolean showSizes = false;

	Vector knipPolygons = new Vector();

	Point oval1Pos = null;
	//Color oval1Color = Color.blue;
	CssColor oval1Color = CssColor.make(0,0,255);
	Point oval2Pos = null;
	//Color oval2Color = Color.blue;	
	CssColor oval2Color = CssColor.make(0,0,255);
	Point oval3Pos = null;
	//Color oval3Color = Color.orange;
	CssColor oval3Color = CssColor.make(255, 165, 0);
	int ovalSize = 13;

	public int clickDis = 7;
	
	boolean knippen = false;

	Point cursorPoint = null;		
	RealPoint firstCutPoint = null;
	Vector firstCutPolygons = new Vector();
	Vector firstCutPolygons2 = new Vector();
	RealPoint secondCutPoint = null;

	boolean dragging = false;

	KnipPolygon2 draggPolygon = null;
	Vector draggVertices = new Vector();
	Vector rotateVertices = new Vector();	

	Rectangle allPolyRect = null;
	
	boolean figureIsRectangle = false;

//GWT	
	//JTextField invulVeld;
	
	KnipPolygon2 labelPolygon = null;

	int labelWidth;
	int labelHeight;
	
	boolean frozen = false;
	
	
//GWT	
	//JPanel tekenGumPanel;
	//JToggleButton tekenButton;
	//JToggleButton gumButton, geenButton;
	//ButtonGroup tekenGumGroup;
	
	boolean itemChanged = false;
	
	boolean tekenGumOptie = false;
	
	boolean tekenen = false;
	
	Point gridPointClicked = null;
	Rectangle draggRectangle = null;
	Vector rectangles = new Vector();
	//Color rectangleColor = Color.blue;
	CssColor rectangleColor = CssColor.make(0,0,255);
	
	int breedte;
	int hoogte;
	
	Canvas dp2Canvas;
	Context2d dp2Context2d;
	boolean mouseDown = false;
	
	String fontString = "bold 16px sans-serif";
	
	TekstPopup tf;
	
	KnipPolygon2 edgeClickedPolygon = null;
	KnipPolygon2 oldEdgeClickedPolygon = null;
	int edgeClickedIndex = -1;
	int oldEdgeClickedIndex = -1;
	boolean gridOnEdgeVisible = false;
	Vector gridPointsOnEdge = new Vector();
	
	public DrawingPanel2(int b, int h, VerknippenGWT o, boolean largeOvals)
	{	
		owner = o;
		breedte = b;
		hoogte = h;
		
		dp2Canvas = Canvas.createIfSupported();
		dp2Canvas.setWidth(b + "px");
		dp2Canvas.setHeight(h + "px");
		dp2Canvas.setCoordinateSpaceWidth(b);
		dp2Canvas.setCoordinateSpaceHeight(h);
		add(dp2Canvas);
		setWidgetLeftWidth(dp2Canvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(dp2Canvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		MouseHandler mouseHandler = new MouseHandler();
		dp2Canvas.addMouseDownHandler(mouseHandler);
		dp2Canvas.addMouseMoveHandler(mouseHandler);
		dp2Canvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		dp2Canvas.addTouchStartHandler(touchHandler);
		dp2Canvas.addTouchMoveHandler(touchHandler);
		dp2Canvas.addTouchEndHandler(touchHandler);

		
	
		if (owner.groteBalletjes)
		{	clickDis = 7;
			ovalSize = 13;
		}
		else
		{	clickDis = 4;
			ovalSize = 8;
		}
	
//GWT		
		labelWidth = 50; //owner.theBoldFM.stringWidth("XXXXXXX");
		labelHeight = 25; //5 * owner.theBoldFM.getHeight() / 4;
	
		// dit werkt niet als je een paintComponent hebt!
		//setBackground(bgColor);
		
		//setLayout(null);
		
		
		//MLMML listener = new MLMML();
		//addMouseListener(listener);
		//addMouseMotionListener(listener);
		
//GWT		
		//tekenGumPanel = new JPanel();
		//tekenGumPanel.setLayout(null);
		//tekenGumPanel.setBounds(4, 4, 47, 25);
		//tekenGumPanel.setBackground(new Color(210,210,210));
		//tekenGumPanel.setVisible(false);
		//add(tekenGumPanel);
		
		//tekenGumGroup = new ButtonGroup();

		//tekenButton = new JToggleButton(new ImageIcon(owner.penDefault), false);
		//tekenButton.setRolloverIcon(new ImageIcon(owner.penRollover));
		//tekenButton.setSelectedIcon(new ImageIcon(owner.penSelected));
		//tekenButton.setBorder(null);
		//tekenButton.setBounds(3, 3, 20, 20);
		//tekenGumPanel.add(tekenButton);		
		//tekenButton.addActionListener(new TekenGumAL());
		//tekenButton.addItemListener(new TekenGumIL());

		//gumButton = new JToggleButton(new ImageIcon(owner.gumDefault), false);
		//gumButton.setRolloverIcon(new ImageIcon(owner.gumRollover));
		//gumButton.setSelectedIcon(new ImageIcon(owner.gumRollover));
		//gumButton.setBorder(null);
		//gumButton.setBounds(25, 3, 20, 20);
		//tekenGumPanel.add(gumButton);				
		//gumButton.addActionListener(new TekenGumAL());
		//gumButton.addItemListener(new TekenGumIL());
		
		//geenButton = new JToggleButton("None selected", true);
		
		//tekenGumGroup.add(tekenButton);
		//tekenGumGroup.add(gumButton);
		//tekenGumGroup.add(geenButton);
		
//GWT		
		//invulVeld = new JTextField("");
		//invulVeld.setFont(owner.theBoldFont);
		//if (owner.taakNummer == 2)
		//	invulVeld.setBackground(labelColor);
		//invulVeld.setSize(labelWidth, labelHeight);
		//invulVeld.setVisible(false);
		
		//add(invulVeld);
		
		//invulVeld.addFocusListener(new TextFL());
		//invulVeld.addActionListener(new TextAL());
		//invulVeld.addKeyListener(new InputKL());
		
		
	}
	
	public Canvas getCanvas()
	{
		return dp2Canvas;
	}
	
	public void initContext2d() 
	{
		dp2Context2d = dp2Canvas.getContext2d();
		
	}

	
//GWT	
	public void zetTekenGumOptie(boolean b)
	{
		tekenGumOptie = b; 
		//tekenGumPanel.setVisible(tekenGumOptie);
		//geenButton.setSelected(true);
		tekenen = false;
		paint();
	}
	
/*	
	public void zetFiguurTransparant(boolean b)
	{	if (b)
			polyColor = polyColorTransparent;
		else
			polyColor = polyColorSolid;
		
		paint();
	}
*/	
	public void zetBorder(boolean b)
	{	showBorder = b;		
	}
	
	public void zetBalletjesGrootte()
	{
		if (owner.groteBalletjes)
		{	clickDis = 7;
			ovalSize = 13;
		}
		else
		{	clickDis = 4;
			ovalSize = 8;
		}
		
	}
	public void addKnipPolygon(KnipPolygon2 kp)
	{	knipPolygons.addElement(kp);
		paint();
	}
	
	public void removeKnipPolygon(KnipPolygon2 kp)
	{	knipPolygons.removeElement(kp);
		paint();
	}

	public void removeAllKnipPolygons()
	{	knipPolygons.removeAllElements();
		paint();
	}

	public void putOnTop(KnipPolygon2 kp)
	{	knipPolygons.removeElement(kp);
		// maak kp de laatste, dan als laatste getekend
		knipPolygons.addElement(kp);
		paint();
	}

	// slepen/selecteren/roteren
	// dit vindt de laatste die (x,y) bevat, dat is dan
	// meteen de bovenste
	public KnipPolygon2 knipPolygonContains(int x, int y)
	{	KnipPolygon2 result = null;
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			boolean contains = kp.contains(x, y);
			if (contains)
				result = kp;
		}	
		return result;
	}

	public int knipPolygonsContain(int x, int y)
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			boolean contains = kp.contains(x, y);
			if (contains)
				result.addElement(kp);
		}	
		return result.size();
	}
	
	public boolean isOnGrid(Point p)
	{	return ((p.x % gridSize) == 0) && ((p.y % gridSize) == 0);
	}
	
	// alle vertices van alle knipPolygons
	public Vector listRealVertices()
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			for (int pCnt = 0; pCnt < kp.aantalPunten; pCnt++)
			{	RealPoint aVertex = kp.realPoints[pCnt];
				if (!result.contains(aVertex))
					result.addElement(aVertex);
			}
		}
		return result;
		
	}
	
	// is het aangeklikte punt voldoende dichtbij een grid point? 
	public Point gridPointClicked(int clickX, int clickY)
	{	Point result = null;
		Vector gridPoints  = findGridPoints();
		for (int pCnt = 0; pCnt < gridPoints.size(); pCnt++)
		{	Point gridPoint = (Point) gridPoints.elementAt(pCnt);
			if (Math.sqrt((gridPoint.x - clickX) * (gridPoint.x - clickX) +
						  (gridPoint.y - clickY) * (gridPoint.y - clickY)) < clickDis)
				result = gridPoint;			
		}
		return result;
	}
	
	// is het aangeklikte punt voldoende dichtbij een van 
	// de vertices?
	public RealPoint vertexClicked(int clickX, int clickY)
	{	RealPoint result = null;
		RealPoint clickPoint = new RealPoint(clickX, clickY);
		Vector vertexList = listRealVertices();
		for (int vCnt = 0; vCnt < vertexList.size(); vCnt++)
		{	RealPoint aVertex = (RealPoint) vertexList.elementAt(vCnt);
			if (aVertex.distance(clickPoint) < clickDis)
				result = aVertex;			
		}
		return result;
	}
	
	// alle knipPolygons die RealPoint v als vertex bevatten
	public Vector containVertex(RealPoint v)
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			int index = kp.containsVertex(v);
			if (index >= 0)
				result.addElement(kp);
		}		
		return result;
	}

	// alle knipPolygons uit subset die RealPoint v als vertex bevatten
	public Vector containVertex(Vector subset, RealPoint v)
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < subset.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) subset.elementAt(kpCnt);
			int index = kp.containsVertex(v);
			if (index >= 0)
				result.addElement(kp);
		}		
		return result;
	}

	// alle knipPolygons die RealPoint v als strikt als edgepoint bevatten
	public Vector containStrictEdgePoint(RealPoint v)
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			int index = kp.edgeStrictlyContainsPoint(v);
			if (index >= 0)
				result.addElement(kp);
		}		
		return result;
	}

	// alle knipPolygons uit subset die RealPoint v als strikt als edgepoint bevatten
	public Vector containStrictEdgePoint(Vector subset, RealPoint v)
	{	Vector result = new Vector();
		for (int kpCnt = 0; kpCnt < subset.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) subset.elementAt(kpCnt);
			int index = kp.edgeStrictlyContainsPoint(v);
			if (index >= 0)
				result.addElement(kp);
		}		
		return result;
	}

	// is het aangeklikte punt voldoende dichtbij een edge
	// incl. de eindpunten	
	public RealPoint edgeClicked(int clickX, int clickY)
	{	RealPoint result = null;
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			RealPoint kpResult = kp.edgeClicked(clickX, clickY);
			if (kpResult != null)
			{	result = kpResult;
				edgeClickedPolygon = kp;
			}	
		}	
		return result;
	}

	// is het aangeklikte punt voldoende dicht bij een gridpoint 
	// op een edge, excl. de eindpunten
	public RealPoint gridPointOnEdgeClicked(int clickX, int clickY)
	{	RealPoint result = null;
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			RealPoint kpResult = kp.gridPointOnEdgeClicked(clickX, clickY);
			if (kpResult != null)
				result = kpResult;
		}	
		return result;
	}
	
	public void knipPolygons()
	{	// 1) vindt de polygons die firstCutPoint als vertex bevatten
 		// opm: er is er maximaal 1 die je vanuit een vertex kan snijden!	
 		Vector vPolygons = containVertex(firstCutPoint);
//System.out.println("vpol = " + vPolygons.size()); 		
 		for (int vCnt = 0; vCnt < vPolygons.size(); vCnt++)
 		{	KnipPolygon2 kp = (KnipPolygon2) vPolygons.elementAt(vCnt);
 			Vector gekniptePolygons = new Vector();
			// kijk of kp secondCutPoint bevat als vertex or edgepoint
			if ((kp.containsVertex(secondCutPoint) >= 0) ||
				(kp.edgeStrictlyContainsPoint(secondCutPoint) >= 0))
 				gekniptePolygons = knipVanuitPunt(kp, firstCutPoint);		
 			if (gekniptePolygons.size() > 0)
 			{	knipPolygons.removeElement(kp);
 				// eerst toevoegen
 				for (int kCnt = 0; kCnt < gekniptePolygons.size(); kCnt++)
 				{	KnipPolygon2 knip = (KnipPolygon2) gekniptePolygons.elementAt(kCnt);
 					knipPolygons.addElement(knip);
// aangeven dat deze geknipt zijn? 					
 				}
				for (int kCnt = 0; kCnt < gekniptePolygons.size(); kCnt++)
 				{	KnipPolygon2 knip = (KnipPolygon2) gekniptePolygons.elementAt(kCnt);
if ((owner.taakNummer == 2) || (owner.taakNummer == 3))
knip.setLabelPoint(); 				 				
				}
 				paint();
 			}
// else aangeven dat kp geknipt is 			
 		}
 		// 2) vindt de polygons die firstCutPoint als strikt edgepoint bevatten
 		// opm: er is er maximaal 1 die je vanuit een strikt edgepoint kan snijden!	
 		Vector ePolygons = containStrictEdgePoint(firstCutPoint);
//System.out.println("epol = " + ePolygons.size()); 		 		
 		for (int eCnt = 0; eCnt < ePolygons.size(); eCnt++)
 		{	KnipPolygon2 kp = (KnipPolygon2) ePolygons.elementAt(eCnt);
 			Vector gekniptePolygons = new Vector();
			// kijk of kp secondCutPoint bevat als vertex or edgepoint
			if ((kp.containsVertex(secondCutPoint) >= 0) ||
				(kp.edgeStrictlyContainsPoint(secondCutPoint) >= 0))
 				gekniptePolygons = knipVanuitPunt(kp, firstCutPoint);		
 			if (gekniptePolygons.size() > 0)
 			{	knipPolygons.removeElement(kp);
 				for (int kCnt = 0; kCnt < gekniptePolygons.size(); kCnt++)
 				{	KnipPolygon2 knip = (KnipPolygon2) gekniptePolygons.elementAt(kCnt);
 					knipPolygons.addElement(knip);
// aangeven dat deze geknipt zijn? 					
 				}
				for (int kCnt = 0; kCnt < gekniptePolygons.size(); kCnt++)
 				{	KnipPolygon2 knip = (KnipPolygon2) gekniptePolygons.elementAt(kCnt);
if ((owner.taakNummer == 2) || (owner.taakNummer == 3))
knip.setLabelPoint(); 				 				
				}
 				
 				paint();
 			}
// else aangeven dat kp geknipt is 			
 		}
/*
 	
NB het is niet of-of!! 	
 	
 	1) vindt de polygons die firstCutPoint als vertex bevatten
 	opm: er is er maar 1 die je kan snijden!
 	snij alle edges behalve die grenzend aan de vertex
 	met het segment firstCutPoint->secondCutPoint
 	let op: dit segment mag niet samenvallen met
 	de aangrenzende edges
 	2) vindt de polygons die firstCutPoint als edgepoint bevatten
 	ook hier maar 1 die je kan snijden
 	snij alle edges behalve die met edgepoint
 	met het segment firstCutPoint->secondCutPoint
 	let op: dit segment mag niet samenvallen met
 	het segment met edgepoint
 	
// misschien dit maar niet toelaten? 	
 	3) snij de rest door (if possible)
 	
 Hoe vermijd je kleine afsnijdingen? via grid of via oppervlakte?	
 als je niet toelaat dat je meerdere polygons doorsnijdt
 kan je de grid gebruiken
 	
*/ 	
	}
	
	public Vector knipVanuitPunt(KnipPolygon2 kp, RealPoint p)
	{	
		Vector gekniptePolygons = new Vector();
		boolean fromVertex = true;	
		int pIndex = kp.containsVertex(p);
		if (pIndex < 0)
		{	fromVertex = false;
			pIndex = kp.edgeStrictlyContainsPoint(p);
			// just in case
			if (pIndex < 0)
				return gekniptePolygons;
		}
		
		Vector knipPunten = new Vector();
		Vector knipIndices = new Vector();
		knipPunten.addElement(p);
		knipIndices.addElement(new Integer(pIndex));
		
		// snij segment p->secondCutPoint met elke edge
		// onderscheidt: 
		// p is een vertex
		// begin dan met de edge pIndex+1->pIndex+2
		// doe de laatste edge die weer eindigt in p NIET
		// 2 minder dus
		// p is geen vertex
		// begin dan met de edge pIndex+1->pIndex+2
		// doe de laatste edge WEL
		// 1 minder dus
		
		// opm: kp heeft evenveel edges als vertices
		int maxEdges = kp.aantalPunten - 1;
		if (!fromVertex)
			maxEdges = kp.aantalPunten;
			
		// onthoudt aantal dubbele knippunten, dit zijn vertices!
		int dubbelCnt = 0;	
		
		for (int eCnt = 1; eCnt < maxEdges; eCnt++)
		{	RealPoint e1 = kp.realPoints[(pIndex + eCnt) % kp.aantalPunten];
			RealPoint e2 = kp.realPoints[(pIndex + eCnt + 1) % kp.aantalPunten];
			RealPoint knipPunt = 
				RealPoint.intersectSegments(p, secondCutPoint, e1, e2);
			// knipPunten die een vertex zijn 1 keer! 	
			if ((knipPunt != null) && !knipPunten.contains(knipPunt))
			{	knipPunten.addElement(knipPunt);
				knipIndices.addElement(new Integer((pIndex + eCnt) % kp.aantalPunten));
			}	
			// wel bijhouden
			else if ((knipPunt != null) && knipPunten.contains(knipPunt))
			{	dubbelCnt++;
			}
		}

//System.out.println("kp = " + knipPunten.size());
//System.out.println("dc = " + dubbelCnt);
		
		// bij een oneven aantal knippunten (inclusief dubbele) niet snijden
//		if (((knipPunten.size() + dubbelCnt) % 2) != 0)
//			return gekniptePolygons;

// hier gaatjes knippen ondervangen
if (knipPunten.size() == 2)
{	RealPoint knipPunt1 = (RealPoint) knipPunten.elementAt(0);
	RealPoint knipPunt2 = (RealPoint) knipPunten.elementAt(1);
	RealPoint midPoint = new RealPoint(
			(knipPunt1.x + knipPunt2.x) / 2,
			(knipPunt1.y + knipPunt2.y) / 2);
	boolean inside = kp.contains(midPoint.toPoint());
	if (!inside)
		return gekniptePolygons;
	
}

		// voeg voor het gemak het eerste knippunt p nogmaals toe
		knipPunten.addElement(p);
		knipIndices.addElement(new Integer(pIndex));

		Vector polygon1 = new Vector();
		Vector polygon2 = new Vector();
		Vector polygon1Stack = new Vector();
		Vector polygon2Stack = new Vector();
		
		for (int kCnt = 0; kCnt < knipPunten.size() - 1; kCnt++)
		{	// eerste
			RealPoint knipPunt1 = (RealPoint) knipPunten.elementAt(kCnt);
			int knipIndex1 = ((Integer) knipIndices.elementAt(kCnt)).intValue();
			// tweede
			RealPoint knipPunt2 = (RealPoint) knipPunten.elementAt(kCnt + 1);
			int	knipIndex2 = ((Integer) knipIndices.elementAt(kCnt + 1)).intValue();

			// drie criteria
			// 1) is knipPunt2 verder van p als knipPunt1
			boolean forward = knipPunt2.distance(p) > knipPunt1.distance(p);
			// 2) bevat het segment knipPunt1->knipPunt2 nog andere knipPunten
			// dan knipPunt1 en knipPunt2
			int otherCnt = 0;
			for (int oCnt = 0; oCnt < knipPunten.size(); oCnt++)
			{	RealPoint oPoint = (RealPoint) knipPunten.elementAt(oCnt);
				if (!oPoint.equals(knipPunt1) && !oPoint.equals(knipPunt2) &&
					oPoint.isOnSegment(knipPunt1, knipPunt2))
				{	otherCnt++;
				}	
			}			
			boolean others = (otherCnt > 0);
			
			// maak maar even alle mogelijkheden (4 x 2)
			if (forward && !others)
			{	
//System.out.println("for & !oth");			
				// doe de middencheck
				// ligt het segment knipPunt1->knipPunt2 binnen het polygon kp
				RealPoint midPoint = new RealPoint(
						(knipPunt1.x + knipPunt2.x) / 2,
						(knipPunt1.y + knipPunt2.y) / 2);
				boolean inside = kp.contains(midPoint.toPoint());
				
				// segment ligt in kp
				if (inside)
				{
//System.out.println("inside");								

					if (polygon1.size() > 0)
					{	polygon1Stack.addElement(polygon1.clone());
//System.out.println("f & !o & i pol1 on stack");					
					}

					// dit is altijd een nieuw polygon
					polygon1.removeAllElements();
					// polygon1: voeg toe
					// knipPunt1 + 
					// vertices tussen knipPunt1 en knipPunt2 +
					// knipPunt2 
					// vermijdt dubbelen EN maak nieuwe punten
					if (!polygon1.contains(knipPunt1))
						polygon1.addElement(new RealPoint(knipPunt1));
					int index = (knipIndex1 + 1) % kp.aantalPunten;
					while (index != knipIndex2)
					{	RealPoint aVertex = kp.realPoints[index];
						if (!polygon1.contains(aVertex))
							polygon1.addElement(new RealPoint(aVertex));
						index = (index + 1) % kp.aantalPunten;
					}	
					RealPoint lastVertex = kp.realPoints[knipIndex2];
					// dit voor de edgepoints
					if (!polygon1.contains(lastVertex))
						polygon1.addElement(new RealPoint(lastVertex));
					if (!polygon1.contains(knipPunt2))
						polygon1.addElement(new RealPoint(knipPunt2));
					// en klaar!
					if (polygon1.size() > 2)
					{	// maak van polygon1 een knipPolygon
						KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
						// voeg het toe aan gekniptePolygons
						gekniptePolygons.addElement(geknipt);
					}	
//System.out.println("pol1 klaar");
					polygon1.removeAllElements();

					// er is nog een polygon2 dat niet af is
					if ((polygon2.size() == 0) && (polygon2Stack.size() > 0))
					{	polygon2 = (Vector) polygon2Stack.lastElement();
						polygon2Stack.remove(polygon2);
//System.out.println("f & !o & i pol2 from stack");						
					}

					// polygon2: voegtoe
					// knipPunt1 + knipPunt2
					if (!polygon2.contains(knipPunt1))
						polygon2.addElement(new RealPoint(knipPunt1));
					if (!polygon2.contains(knipPunt2))
					{	polygon2.addElement(new RealPoint(knipPunt2));	
					}
					else // we zijn rond
					{	if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
						polygon2.removeAllElements();
					}

// dit gebeurt nooit: als knipPunt2=knipPunten.lastElement()=p
// dan is knipPunt1->knipPunt2 nooit forward  
					
/*					
					// knipPunt2 is het laatste knippunt
					// maak polygon2 af
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon geknipt = new KnipPolygon(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
						polygon2.removeAllElements();
					}
*/					
				}
				else // segment ligt niet in kp
				{	
//System.out.println("not inside");												
					// polygon1
					// nothing to do, laat onveranderd	
/*
					// er is nog een polygon2 dat niet af is
					if ((polygon2.size() == 0) && (polygon2Stack.size() > 0))
					{	polygon2 = (Vector) polygon2Stack.lastElement();
						polygon2Stack.remove(polygon2);
System.out.println("f & !o & !i pol2 from stack");						
					}
*/					
					// polygon2: voeg toe
					// knipPunt1 + 
					// vertices tussen knipPunt1 en knipPunt2 +
					// knipPunt2 
					// vermijdt dubbelen EN maak nieuwe punten
					if (!polygon2.contains(knipPunt1))
						polygon2.addElement(new RealPoint(knipPunt1));
					int index = (knipIndex1 + 1) % kp.aantalPunten;
					while (index != knipIndex2)
					{	RealPoint aVertex = kp.realPoints[index];
						if (!polygon2.contains(aVertex))
							polygon2.addElement(new RealPoint(aVertex));
						index = (index + 1) % kp.aantalPunten;
					}	
					RealPoint lastVertex = kp.realPoints[knipIndex2];
					if (!polygon2.contains(lastVertex))
						polygon2.addElement(new RealPoint(lastVertex));					
					if (!polygon2.contains(knipPunt2))
					{	polygon2.addElement(new RealPoint(knipPunt2));
					}
					else // we zijn rond
					{	if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
						polygon2.removeAllElements();
					}

// dit gebeurt nooit: als knipPunt2=knipPunten.lastElement()=p
// dan is knipPunt1->knipPunt2 nooit forward  

/*
					// knipPunt2 is het laatste knippunt
					// maak polygon2 af
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	
						if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon geknipt = new KnipPolygon(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
				
						polygon2.removeAllElements();

					}
// wat als snede samenvalt met edge en dit niet als inside wordt
// aangemerkt? lijkt van wel
*/

				}	
			}
			else if (!forward && !others)
			{	// doe de middencheck
				// ligt het segment knipPunt1->knipPunt2 binnen het polygon kp
				RealPoint midPoint = new RealPoint(
						(knipPunt1.x + knipPunt2.x) / 2,
						(knipPunt1.y + knipPunt2.y) / 2);
				boolean inside = kp.contains(midPoint.toPoint());
//System.out.println("!for & !oth");							
				if (inside)
				{
//System.out.println("inside");													
// dit is niet nodig?					
					// er is nog een polygon1 dat niet af is
					if ((polygon1.size() == 0) && (polygon1Stack.size() > 0))
					{	polygon1 = (Vector) polygon1Stack.lastElement();
						polygon1Stack.remove(polygon1);
					}

					// polygon1: voegtoe
					// knipPunt1 + knipPunt2
					if (!polygon1.contains(knipPunt1))
						polygon1.addElement(new RealPoint(knipPunt1));
					if (!polygon1.contains(knipPunt2))
					{	polygon1.addElement(new RealPoint(knipPunt2));	
					}
					else // we zijn rond
					{	if (polygon1.size() > 2)
						{	// maak van polygon1 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
						polygon1.removeAllElements();
					}

					// knipPunt2 is het laatste knippunt
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	
						// maak de polygon1Stack leeg
						for (int s1Cnt = 0; s1Cnt < polygon1Stack.size(); s1Cnt++)
						{	polygon1 = (Vector) polygon1Stack.lastElement();
							polygon1Stack.remove(polygon1);

							if (polygon1.size() > 2)
							{	// maak van polygon2 een knipPolygon
								KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
								// voeg het toe aan gekniptePolygons
								gekniptePolygons.addElement(geknipt);
							}	
				
							polygon1.removeAllElements();
						}	
						
					}
					
					if (polygon2.size() > 0)
						polygon2Stack.addElement(polygon2.clone());

					// dit is altijd een nieuw polygon
					polygon2.removeAllElements();
					// polygon2: voeg toe
					// knipPunt1 + 
					// vertices tussen knipPunt1 en knipPunt2 +
					// knipPunt2 
					// vermijdt dubbelen EN maak nieuwe punten
					if (!polygon2.contains(knipPunt1))
						polygon2.addElement(new RealPoint(knipPunt1));
					int index = (knipIndex1 + 1) % kp.aantalPunten;
					while (index != knipIndex2)
					{	RealPoint aVertex = kp.realPoints[index];
						if (!polygon2.contains(aVertex))
							polygon2.addElement(new RealPoint(aVertex));
						index = (index + 1) % kp.aantalPunten;
					}	
					RealPoint lastVertex = kp.realPoints[knipIndex2];
					// dit voor edgepoints					
					if (!polygon2.contains(lastVertex))
						polygon2.addElement(new RealPoint(lastVertex));										
					if (!polygon2.contains(knipPunt2))
						polygon2.addElement(new RealPoint(knipPunt2));
					// en klaar!

					if (polygon2.size() > 2)
					{	// maak van polygon2 een knipPolygon
						KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
						// voeg het toe aan gekniptePolygons
						gekniptePolygons.addElement(geknipt);
					}	
					
					polygon2.removeAllElements();


					// knipPunt2 is het laatste knippunt
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	
						// maak de polygon2Stack leeg
						for (int s2Cnt = 0; s2Cnt < polygon2Stack.size(); s2Cnt++)
						{	polygon2 = (Vector) polygon2Stack.lastElement();
							polygon2Stack.remove(polygon2);

							if (polygon2.size() > 2)
							{	// maak van polygon2 een knipPolygon
								KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
								// voeg het toe aan gekniptePolygons
								gekniptePolygons.addElement(geknipt);
							}	
				
							polygon2.removeAllElements();
						}	
						
					}
					
				}
				else // not inside
				{	

//System.out.println("not inside");																
					// er is nog een polygon1 dat niet af is
					if ((polygon1.size() == 0) && (polygon1Stack.size() > 0))
					{	polygon1 = (Vector) polygon1Stack.lastElement();
						polygon1Stack.remove(polygon1);
					}
				
					// polygon1: voeg toe
					// knipPunt1 + 
					// vertices tussen knipPunt1 en knipPunt2 +
					// knipPunt2 
					// vermijdt dubbelen EN maak nieuwe punten
					if (!polygon1.contains(knipPunt1))
						polygon1.addElement(new RealPoint(knipPunt1));
					int index = (knipIndex1 + 1) % kp.aantalPunten;
					while (index != knipIndex2)
					{	RealPoint aVertex = kp.realPoints[index];
						if (!polygon1.contains(aVertex))
							polygon1.addElement(new RealPoint(aVertex));
						index = (index + 1) % kp.aantalPunten;
					}	
					RealPoint lastVertex = kp.realPoints[knipIndex2];
					if (!polygon1.contains(lastVertex))
						polygon1.addElement(new RealPoint(lastVertex));
					if (!polygon1.contains(knipPunt2))
					{	polygon1.addElement(new RealPoint(knipPunt2));
					}
					else // we zijn rond
					{	if (polygon1.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
						polygon1.removeAllElements();
					}

/*						
					// maar nog niet klaar!	
					
					// knipPunt2 is het laatste knippunt
					// maak polygon1 af
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	//index = (knipIndex2 + 1) % kp.aantalPunten;
						//while (index != vIndex)
						//{	RealPoint aVertex = kp.realPoints[index];
						//	if (!polygon1.contains(aVertex))
						//		polygon1.addElement(new RealPoint(aVertex));
						//	index = (index + 1) % kp.aantalPunten;
						//}	
						// laatste punt is p
//						if (!polygon1.contains(p))
//							polygon1.addElement(new RealPoint(p));
						// en klaar!
						
						if (polygon1.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon geknipt = new KnipPolygon(polygon1, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
				
						polygon1.removeAllElements();
						
					}

*/
/*
// dit gebeurt nooit?					
					// knipPunt2 is het laatste knippunt
					// maak polygon2 af
					if (knipPunt2.equals(knipPunten.lastElement()))
					{	
						
						if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon geknipt = new KnipPolygon(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
				
						polygon2.removeAllElements();
						
					}
*/					
					

				}
			}
			else if (forward && others)
			{	
				// hier geen middencheck

				// dit polygon is nog niet af
				// op de stack
				if (polygon1.size() > 0)
				{	polygon1Stack.addElement(polygon1.clone());
//System.out.println("f & o pol1 on stack");				
				}

				polygon1.removeAllElements();
/*
				// er is nog een polygon1 dat niet af is
				if ((polygon1.size() == 0) && (polygon1Stack.size() > 0))
				{	polygon1 = (Vector) polygon1Stack.lastElement();
					polygon1Stack.remove(polygon1);
				}
*/
				// polygon1: voeg toe
				// knipPunt1 + 
				// vertices tussen knipPunt1 en knipPunt2 +
				// knipPunt2 
				// vermijdt dubbelen EN maak nieuwe punten
				if (!polygon1.contains(knipPunt1))
					polygon1.addElement(new RealPoint(knipPunt1));
				int index = (knipIndex1 + 1) % kp.aantalPunten;
				while (index != knipIndex2)
				{	RealPoint aVertex = kp.realPoints[index];
					if (!polygon1.contains(aVertex))
						polygon1.addElement(new RealPoint(aVertex));
					index = (index + 1) % kp.aantalPunten;
				}	
				RealPoint lastVertex = kp.realPoints[knipIndex2];
				// dit voor edgepoints
				if (!polygon1.contains(lastVertex))
					polygon1.addElement(new RealPoint(lastVertex));
				if (!polygon1.contains(knipPunt2))
				{	polygon1.addElement(new RealPoint(knipPunt2));
				}
				else
				{	if (polygon1.size() > 2)
					{	// maak van polygon2 een knipPolygon
						KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
						// voeg het toe aan gekniptePolygons
						gekniptePolygons.addElement(geknipt);
					}	
					polygon1.removeAllElements();
				}
				
				
				// polygon2: 
				// nothing to do, laat onveranderd
				
				if (polygon2.size() > 0)
					polygon2Stack.insertElementAt(polygon2.clone(), 0);

				polygon2.removeAllElements();
				
//System.out.println("p2s f o = " + polygon2Stack.size());				

/*
 				// knipPunt2 is het laatste knippunt
				if (knipPunt2.equals(knipPunten.lastElement()))
				{	
					// maak de polygon2Stack leeg
					for (int s2Cnt = 0; s2Cnt < polygon2Stack.size(); s2Cnt++)
					{	polygon2 = (Vector) polygon2Stack.lastElement();
						polygon2Stack.remove(polygon2);

						if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon geknipt = new KnipPolygon(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
		
						polygon2.removeAllElements();
					}	
						
				}
*/				
				
			}
			else if (!forward && others)
			{	// hier geen middencheck
				// polygon1: nothing to do, laat onveranderd
				
				if (polygon1.size() > 0)
					polygon1Stack.insertElementAt(polygon1.clone(), 0);

				polygon1.removeAllElements();
				

				// knipPunt2 is het laatste knippunt
				if (knipPunt2.equals(knipPunten.lastElement()))
				{	
					// maak de polygon1Stack leeg
					for (int s1Cnt = 0; s1Cnt < polygon1Stack.size(); s1Cnt++)
					{	polygon1 = (Vector) polygon1Stack.lastElement();
						polygon1Stack.remove(polygon1);

						if (polygon1.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon1, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
		
						polygon1.removeAllElements();
					}	
						
				}

//System.out.println("p2sa = " + polygon2Stack.size());

				// dit polygon is nog niet af
				// op de stack
//				if ((polygon2.size() > 0) && 
//					!knipPunt2.equals(knipPunten.lastElement())
//				   )	
//				{	polygon2Stack.insertElementAt(polygon2.clone(), 0);
//					polygon2.removeAllElements();
//				}

//System.out.println("p2sb = " + polygon2Stack.size());
				
				
				// er is nog een polygon2 dat niet af is
				if ((polygon2.size() == 0) && (polygon2Stack.size() > 0))
				{	polygon2 = (Vector) polygon2Stack.lastElement();
					polygon2Stack.remove(polygon2);
//System.out.println("!f & o pol2 from stack");					
				}
				
				// polygon2: voeg toe
				// knipPunt1 + 
				// vertices tussen knipPunt1 en knipPunt2 +
				// knipPunt2 
				// vermijdt dubbelen EN maak nieuwe punten
				if (!polygon2.contains(knipPunt1))
					polygon2.addElement(new RealPoint(knipPunt1));
				int index = (knipIndex1 + 1) % kp.aantalPunten;
				while (index != knipIndex2)
				{	RealPoint aVertex = kp.realPoints[index];
					if (!polygon2.contains(aVertex))
						polygon2.addElement(new RealPoint(aVertex));
					index = (index + 1) % kp.aantalPunten;
				}	
				RealPoint lastVertex = kp.realPoints[knipIndex2];
				// dit voor edgepoints					
				if (!polygon2.contains(lastVertex))
					polygon2.addElement(new RealPoint(lastVertex));										
				if (!polygon2.contains(knipPunt2))
				{	polygon2.addElement(new RealPoint(knipPunt2));
				}
				else // we zijn rond
				{	if (polygon2.size() > 2)
					{	// maak van polygon2 een knipPolygon
						KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
						// voeg het toe aan gekniptePolygons
						gekniptePolygons.addElement(geknipt);
					}	
					polygon2.removeAllElements();
				}	
				
				// dit polygon is nog niet af
				// op de stack
				if ((polygon2.size() > 0) //&& 
//					!knipPunt2.equals(knipPunten.lastElement()
				   )
				{	polygon2Stack.insertElementAt(polygon2.clone(), 0);
					polygon2.removeAllElements();
//System.out.println("!f & o & !last pol2 to stack");					
				}
				
				
				
 				// knipPunt2 is het laatste knippunt
				if (knipPunt2.equals(knipPunten.lastElement()))
				{	
					// maak de polygon2Stack leeg
					for (int s2Cnt = 0; s2Cnt < polygon2Stack.size(); s2Cnt++)
					{	polygon2 = (Vector) polygon2Stack.lastElement();
						polygon2Stack.remove(polygon2);

						if (polygon2.size() > 2)
						{	// maak van polygon2 een knipPolygon
							KnipPolygon2 geknipt = new KnipPolygon2(polygon2, this);
							// voeg het toe aan gekniptePolygons
							gekniptePolygons.addElement(geknipt);
						}	
		
						polygon2.removeAllElements();
					}	
						
				}
				
			} // !forward && others
			
			
		} // for
		
		return gekniptePolygons;				
	}
	
	public void paint()
	{
		dp2Context2d.setGlobalAlpha(1);
		paintComponent(dp2Context2d);
		if (figuurTransparant)
			dp2Context2d.setGlobalAlpha(5e-1d);
		paintComponent(dp2Context2d);
	}
	
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
	{	
		
		//g.setGlobalAlpha(5e-1d);
		
//System.out.println("" + g.getGlobalAlpha());
//System.out.println("" + g.getGlobalCompositeOperation());

		//g.setColor(bgColor);
		g.setFillStyle(bgColor);
		//g.fillRect(0, 0, getSize().width, getSize().height);
		g.fillRect(0, 0, breedte, hoogte);
		
		//g.setColor(outlineColor);
		g.setStrokeStyle(outlineColor);
		if (showBorder)
			g.strokeRect(0, 0, breedte, hoogte);
		
		if (showGrid && !gridOnTop)
			paintGrid(g);

		if (grijsPolygon != null)
		{	//g.setColor(grijsPolyColor);
			g.setFillStyle(grijsPolyColor);
			//g.fillPolygon(grijsPolygon.intPolygon);
			
			g.beginPath();		
			g.moveTo(grijsPolygon.intPolygon.doubleX[0], grijsPolygon.intPolygon.doubleY[0]);
			for (int k = 1; k < grijsPolygon.intPolygon.aantalPunten; k++) 
			{	g.lineTo(grijsPolygon.intPolygon.doubleX[k], grijsPolygon.intPolygon.doubleY[k]);
			}
			g.lineTo(grijsPolygon.intPolygon.doubleX[0], grijsPolygon.intPolygon.doubleY[0]);
			g.closePath();
			g.fill();

		}

		if (shadowPolygon != null)
			paintShadowPolygon(g);	
		
			
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			//g.setColor(polyColor);
			g.setFillStyle(polyColor);
			//g.fillPolygon(kp.intPolygon);
			
			g.beginPath();		
			g.moveTo(kp.intPolygon.doubleX[0], kp.intPolygon.doubleY[0]);
			for (int k = 1; k < kp.intPolygon.aantalPunten; k++) 
			{	g.lineTo(kp.intPolygon.doubleX[k], kp.intPolygon.doubleY[k]);
			}
			g.lineTo(kp.intPolygon.doubleX[0], kp.intPolygon.doubleY[0]);
			g.closePath();
			g.fill();

			//g.setColor(outlineColor);
			g.setStrokeStyle(outlineColor);
			//g.drawPolygon(kp.intPolygon);	

			g.beginPath();		
			g.moveTo(kp.intPolygon.doubleX[0], kp.intPolygon.doubleY[0]);
			for (int k = 1; k < kp.intPolygon.aantalPunten; k++) 
			{	g.lineTo(kp.intPolygon.doubleX[k], kp.intPolygon.doubleY[k]);
			}
			g.lineTo(kp.intPolygon.doubleX[0], kp.intPolygon.doubleY[0]);
			g.closePath();
			g.stroke();

//g.setColor(Color.magenta);
//Rectangle bb = kp.getBoundingBox();
//g.drawRect(bb.x, bb.y, bb.width, bb.height);			


			// oude versie				
			if ((owner.taakNummer == 2) && (kp.labelPoint != null))
			{	
				// rondje op polygon
				//g.setColor(labelColor);
				g.setFillStyle(labelColor);
				//g.fillOval((int) Math.round(kp.labelPoint.x) - ovalSize / 2, 
				//	   	   (int) Math.round(kp.labelPoint.y) - ovalSize / 2, ovalSize, ovalSize);
				
				g.beginPath();
	            g.arc((int) Math.round(kp.labelPoint.x), (int) Math.round(kp.labelPoint.y), ovalSize / 2, 0, 2 * Math.PI);
	            g.fill();
					   		
				// groen label mat zwarte rand,
				// leeg of met de oppervlakte ingevuld	   		
				if ((kp.labelRect != null) && kp.labelVisible)
				{	//g.setColor(labelColor);
					g.setFillStyle(labelColor);
					g.fillRect(kp.labelRect.x, kp.labelRect.y,
						       kp.labelRect.width, kp.labelRect.height);
					//g.setColor(Color.black);
					g.setStrokeStyle(CssColor.make(0,0,0));
					g.strokeRect(kp.labelRect.x, kp.labelRect.y,
						         kp.labelRect.width, kp.labelRect.height);

//tijdelijk
//kp.oppervlakte = 64;
					
					if (kp.oppervlakte > 0)
					{	String oString = "" + kp.oppervlakte;

					
						TextMetrics tm = g.measureText(oString);
						double width = tm.getWidth(); 

						int oWidth = (int) Math.round(width);
						int hSpace = (KnipPolygon2.labelWidth - oWidth) / 2;
						if (hSpace < 0)
							hSpace = 0;
						int bx = kp.labelRect.x + hSpace;	
						//g.setColor(Color.black);
						g.setFillStyle(CssColor.make(0,0,0));
						//	g.setFont(owner.theBoldFont);
						g.setFont(fontString);
						//g.drawString(oString, bx,kp.labelRect.y + owner.theBoldFM.getHeight());
						g.fillText(oString, bx,kp.labelRect.y + 19);
							
							
					}	       
						       
				}	   		
			} // oude versie
			
			// nieuwe versie				
			if ((owner.taakNummer == 3) && (kp.labelPoint != null))
			{	
				// rondje op polygon
				//g.setColor(labelColor);
				g.setFillStyle(labelColor);
				//g.fillOval((int) Math.round(kp.labelPoint.x) - ovalSize / 2, 
				//	   	   (int) Math.round(kp.labelPoint.y) - ovalSize / 2, 
				//	   		ovalSize, ovalSize);
				g.beginPath();
	            g.arc((int) Math.round(kp.labelPoint.x), (int) Math.round(kp.labelPoint.y), ovalSize / 2, 0, 2 * Math.PI);
	            g.fill();
					   		
				// groen label mat zwarte rand,
				// leeg of met de oppervlakte ingevuld	   		
				if ((kp.labelRect != null))// && kp.labelVisible)
				{	//g.setColor(labelColor);
					//g.fillRect(kp.labelRect.x, kp.labelRect.y,
					//	       kp.labelRect.width, kp.labelRect.height);
					//g.setColor(Color.black);
					//g.drawRect(kp.labelRect.x, kp.labelRect.y,
					//	       kp.labelRect.width, kp.labelRect.height);
					
//tijdelijk
//kp.oppervlakte = 64;
					
					if (kp.oppervlakte > 0)
					{	String oString = "" + kp.oppervlakte;
					
						TextMetrics tm = g.measureText(oString);
						double width = tm.getWidth(); 

						int oWidth = (int) Math.round(width);
						//int oWidth = owner.theBoldFM.stringWidth(oString);
						int hSpace = 0;
						if (kp.labelAlign == kp.LEFT)
						{	hSpace = 3;
						}
						else
						{	hSpace = kp.labelRect.width - oWidth;
						}
						int bx = kp.labelRect.x + hSpace;	
						//g.setColor(Color.black);
						g.setFillStyle(CssColor.make(0,0,0));
						//g.setFont(owner.theBoldFont);
						g.setFont(fontString);
						//g.drawString(oString, bx, kp.labelRect.y + owner.theBoldFM.getHeight());
						g.fillText(oString, bx,kp.labelRect.y + 19);	
							
					}
					else
					{	String oString = ". . . . .";
					
						TextMetrics tm = g.measureText(oString);
						double width = tm.getWidth(); 

						int oWidth = (int) Math.round(width);
					
						//int oWidth = owner.theBoldFM.stringWidth(oString);
						int hSpace = 0;
						if (kp.labelAlign == kp.LEFT)
						{	hSpace = 0;
						}
						else
						{	hSpace = kp.labelRect.width - oWidth;
						}
						int bx = kp.labelRect.x + hSpace;	
						//g.setColor(Color.black);
						g.setFillStyle(CssColor.make(0,0,0));
						//g.setFont(owner.theBoldFont);
						g.setFont(fontString);
						//g.drawString(oString, bx,kp.labelRect.y + owner.theBoldFM.getHeight());
						g.fillText(oString, bx,kp.labelRect.y + 19);
						       
					}	       
				}	   		
			} // nieuwe versie
			
		} 		
			

		if (oval1Pos != null)
		{	//g.setColor(oval1Color);
			g.setFillStyle(oval1Color);
			//g.fillOval(oval1Pos.x - ovalSize / 2, oval1Pos.y - ovalSize / 2, ovalSize, ovalSize);
			
			g.beginPath();
            g.arc(oval1Pos.x, oval1Pos.y, ovalSize / 2, 0, 2 * Math.PI);
            g.fill();
		}			
		if (oval2Pos != null)
		{	//g.setColor(oval2Color);
			g.setFillStyle(oval2Color);
			//g.fillOval(oval2Pos.x - ovalSize / 2, oval2Pos.y - ovalSize / 2, ovalSize, ovalSize);
			
			g.beginPath();
            g.arc(oval2Pos.x, oval2Pos.y, ovalSize / 2, 0, 2 * Math.PI);
            g.fill();

		}			
		if (oval3Pos != null)
		{	//g.setColor(oval3Color);
			g.setFillStyle(oval3Color);
			//g.fillOval(oval3Pos.x - ovalSize / 2, oval3Pos.y - ovalSize / 2, ovalSize, ovalSize);
			
			g.beginPath();
            g.arc(oval3Pos.x, oval3Pos.y, ovalSize / 2, 0, 2 * Math.PI);
            g.fill();

		}			
			
		g.setFillStyle(oval1Color);
		for (int gpCnt = 0; gpCnt < gridPointsOnEdge.size(); gpCnt++)
		{
			Point gpOnEdge = ((RealPoint) gridPointsOnEdge.elementAt(gpCnt)).toPoint();
			g.beginPath();
            g.arc(gpOnEdge.x, gpOnEdge.y, ovalSize / 2, 0, 2 * Math.PI);
            g.fill();
			
		}
		
		if (showGrid && gridOnTop)
			paintGrid(g);
			
		if (knippen)
		{	//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			//g.drawLine(firstCutPoint.toPoint().x, firstCutPoint.toPoint().y,
			//		   cursorPoint.x, cursorPoint.y);
			g.beginPath();
			g.moveTo(firstCutPoint.toPoint().x, firstCutPoint.toPoint().y);
			g.lineTo(cursorPoint.x, cursorPoint.y);
			g.stroke();
		}	
		
		if (tekenGumOptie)
		{	
//GWT niet nodig			
			//g.setColor(Color.black);
			//g.drawRect(tekenGumPanel.getLocation().x - 1, tekenGumPanel.getLocation().y - 1, 
			//		   tekenGumPanel.getSize().width+ 1, tekenGumPanel.getSize().height + 1);
		}
		
		if (tekenen && draggRectangle != null)
		{
			
			//g.setColor(rectangleColor);
			g.setStrokeStyle(rectangleColor);
			//g.drawRect(draggRectangle.x, draggRectangle.y, draggRectangle.width, draggRectangle.height);
			g.strokeRect(draggRectangle.x, draggRectangle.y, draggRectangle.width, draggRectangle.height);
			//g.drawRect(draggRectangle.x - 1, draggRectangle.y - 1, draggRectangle.width, draggRectangle.height);
			
		}
		if (tekenGumOptie)
		{	
			
			//g.setColor(rectangleColor);
			g.setStrokeStyle(rectangleColor);
			for (int rCnt = 0; rCnt < rectangles.size(); rCnt++)
			{	Rectangle aRect = (Rectangle) rectangles.elementAt(rCnt);
				//g.drawRect(aRect.x, aRect.y, aRect.width, aRect.height);
				g.strokeRect(aRect.x, aRect.y, aRect.width, aRect.height);
				//g.drawRect(aRect.x - 1, aRect.y - 1, aRect.width, aRect.height);				
			}
			
		}
		//paintComponents(g);
		
	}
	
	//public void paintShadowPolygon(Graphics g)
	public void paintShadowPolygon(Context2d g)
	{	//g.setColor(shadowColor);
		g.setFillStyle(shadowColor);
	
		//g.fillPolygon(shadowPolygon.intPolygon);
		g.beginPath();		
		g.moveTo(shadowPolygon.intPolygon.doubleX[0], shadowPolygon.intPolygon.doubleY[0]);
		for (int k = 1; k < shadowPolygon.intPolygon.aantalPunten; k++) 
		{	g.lineTo(shadowPolygon.intPolygon.doubleX[k], shadowPolygon.intPolygon.doubleY[k]);
		}
		g.lineTo(shadowPolygon.intPolygon.doubleX[0], shadowPolygon.intPolygon.doubleY[0]);
		g.closePath();
		g.fill();

		if (showSizes)
		{	
			for (int eCnt = 0; eCnt < shadowPolygon.aantalPunten; eCnt++)
			{	Point e1 = shadowPolygon.intPoints[eCnt];
				Point e2 = shadowPolygon.intPoints[
								(eCnt + 1) % shadowPolygon.aantalPunten];
				drawSize(g, e1, e2);						
			}
		}	

	}	
	
	//public void drawSize(Graphics g, Point p1, Point p2)
	public void drawSize(Context2d g, Point p1, Point p2)
	{	int hOffset = 4;
	
		g.setFont(fontString);
	
		// horizontaal van links naar rechts, size boven
		if ((p1.x < p2.x) && (p1.y == p2.y))
		{	String s = "" + (p2.x - p1.x) / gridSize;

			//int width = owner.theBoldFM.stringWidth(s);
			int height = hOffset; //owner.theBoldFM.getDescent();
		
			TextMetrics tm = g.measureText(s);
			double width = tm.getWidth(); 
			
			double bx = p1.x + (p2.x - p1.x - width) / 2;
			int by = p1.y - height;
			//g.setColor(sizeColor);
			g.setFillStyle(sizeColor);
			//g.setFont(owner.theBoldFont);
			//g.setFont(fontString);
			//g.drawString(s, bx, by);
			g.fillText(s, bx, by);
		}
		// horizontaal van rechts naar links, size onder
		if ((p1.x > p2.x) && (p1.y == p2.y))
		{	String s = "" + (p1.x - p2.x) / gridSize;
			
	
			TextMetrics tm = g.measureText(s);
			double width = tm.getWidth(); 

			//int width = owner.theBoldFM.stringWidth(s);
			int height = hOffset - 2 + 15; //owner.theBoldFM.getAscent();
			double bx = p2.x + (p1.x - p2.x - width) / 2;
			int by = p1.y + height;
			//g.setColor(sizeColor);
			g.setFillStyle(sizeColor);
			//g.setFont(owner.theBoldFont);
			//g.setFont(fontString);
			//g.drawString(s, bx, by);
			g.fillText(s, bx, by);
		} 
		// vertikaal van boven naar beneden, size rechts
		if ((p1.x == p2.x) && (p1.y < p2.y))
		{	String s = "" + (p2.y - p1.y) / gridSize;

		
			//int width = owner.theBoldFM.stringWidth(s);
			int height = 15; //owner.theBoldFM.getAscent();
			
			TextMetrics tm = g.measureText(s);
			double width = tm.getWidth(); 

			double bx = p1.x + hOffset;
			int by = p1.y + (p2.y - p1.y + height) / 2;
			//g.setColor(sizeColor);
			g.setFillStyle(sizeColor);
			//g.setFont(owner.theBoldFont);
			//g.setFont(fontString);
			//g.drawString(s, bx, by);
			g.fillText(s, bx, by);
		} 		
		// vertikaal van beneden naar boven, size links
		if ((p1.x == p2.x) && (p1.y > p2.y))
		{	String s = "" + (p1.y - p2.y) / gridSize;
			
		
			//int width = owner.theBoldFM.stringWidth(s);
			int height = 15; //owner.theBoldFM.getAscent();
			
			TextMetrics tm = g.measureText(s);
			double width = tm.getWidth(); 

			double bx = p1.x - hOffset - width;
			int by = p2.y + (p1.y - p2.y + height) / 2;
			//g.setColor(sizeColor);
			g.setFillStyle(sizeColor);
			//g.setFont(owner.theBoldFont);
			//g.setFont(fontString);
			//g.drawString(s, bx, by);
			g.fillText(s, bx, by);
		} 		
		
	}
	
	public Vector findGridPoints()
	{	Vector result = new Vector();
		int horGridElts = breedte / gridSize;
		int vertGridElts = hoogte / gridSize;
		for (int hCnt = 1; hCnt <= horGridElts; hCnt++)
			for (int vCnt = 1; vCnt <= vertGridElts; vCnt++)
			{
				result.addElement(new Point(gridSize * hCnt, gridSize * vCnt));
			}
		
		return result;
	}
	
	public Point findClosestGridPoint(Point p)
	{	Point result = null;
		Vector gridPoints = findGridPoints();
		Point gridPoint = (Point) gridPoints.elementAt(0);
		result = gridPoint;
		double dis = Math.sqrt((gridPoint.x - p.x) * (gridPoint.x - p.x) +
				               (gridPoint.y - p.y) * (gridPoint.y - p.y));
		for (int pCnt = 1; pCnt < gridPoints.size(); pCnt++)
		{	gridPoint = (Point) gridPoints.elementAt(pCnt);
			double pDis = Math.sqrt((gridPoint.x - p.x) * (gridPoint.x - p.x) +
		                            (gridPoint.y - p.y) * (gridPoint.y - p.y));
			if (pDis < dis)
			{	dis = pDis;	
				result = gridPoint;
			}
		}
	
		return result;
	}
	
	//public void paintGrid(Graphics g)
	public void paintGrid(Context2d g)
	{	
//System.out.println("paintGrid");

		int horGridElts = breedte / gridSize;
		int vertGridElts = hoogte / gridSize;
		//g.setColor(gridColor);
		g.setStrokeStyle(gridColor);
		// horizontale lijnen
		for (int hCnt = 1; hCnt <= vertGridElts; hCnt++)
		{	//g.drawLine(1, gridSize * hCnt, getSize().width - 2, gridSize * hCnt);
			g.beginPath();
			g.moveTo(1, gridSize * hCnt);
			g.lineTo(breedte - 1, gridSize * hCnt);
			g.stroke();
		}
		// vertikale lijnen
		for (int vCnt = 1; vCnt <= horGridElts; vCnt++)
		{	//g.drawLine(gridSize * vCnt, 1, gridSize * vCnt, getSize().height - 2);
			g.beginPath();
			g.moveTo(gridSize * vCnt, 1);
			g.lineTo(gridSize * vCnt, hoogte - 1);
			g.stroke();
		}
		
	}

	public void checkForRectangle()
	{	// vindt de bounding box voor alle stukjes op
		// het werkveld
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			if (kpCnt == 0)
			{	allPolyRect = kp.intPolygon.getBounds();
			}	
			else
			{	Rectangle kpRect = kp.intPolygon.getBounds();
				int topLeftX = Math.min(allPolyRect.x, kpRect.x);
				int topLeftY = Math.min(allPolyRect.y, kpRect.y);
				int bottomRightX = Math.max(
					allPolyRect.x + allPolyRect.width,
					kpRect.x + kpRect.width);
				int bottomRightY = Math.max(
					allPolyRect.y + allPolyRect.height,
					kpRect.y + kpRect.height);
				allPolyRect = new Rectangle(topLeftX, topLeftY,
					bottomRightX - topLeftX, bottomRightY - topLeftY);	
			}
		}
		// loop nu alle roosterblokjes na binnen deze boundingbox
		boolean isRectangle = true;
		int hStart = allPolyRect.x / gridSize;
		int vStart = allPolyRect.y / gridSize;
		int hBlokjes = allPolyRect.width / gridSize;
		int vBlokjes = allPolyRect.height / gridSize;
		
//System.out.println("hs = " + hStart);
//System.out.println("hb = " + hBlokjes);								
//System.out.println("vs = " + vStart);
//System.out.println("vb = " + vBlokjes);								

		
		int inside = 1;
		int x, y, num;
int chkCnt = 0;		
		for (int hCnt = hStart; hCnt < (hStart + hBlokjes); hCnt++)
			for (int vCnt = vStart; vCnt < (vStart + vBlokjes); vCnt++)
			{	// alleen checken zolang het tot nu toe goed ging
				if (isRectangle)
				{	// punt midden boven, net binnenin blokje
					x = hCnt * gridSize + gridSize / 2;
					y = vCnt * gridSize + inside;
					num = knipPolygonsContain(x, y);	
					isRectangle = isRectangle && (num == 1);	
					chkCnt++;
				}
				if (isRectangle)
				{	// punt rechts middenin, net binnenin blokje
					x = (hCnt + 1) * gridSize - inside;
					y = vCnt * gridSize + gridSize / 2;
					num = knipPolygonsContain(x, y);	
					isRectangle = isRectangle && (num == 1);	
					chkCnt++;
				}
				if (isRectangle)
				{	// punt midden onder, net binnenin blokje
					x = hCnt * gridSize + gridSize / 2;
					y = (vCnt + 1) * gridSize - inside;
					num = knipPolygonsContain(x, y);	
					isRectangle = isRectangle && (num == 1);	
					chkCnt++;
				}
				if (isRectangle)
				{	// punt links middenin, net binnenin blokje
					x = hCnt * gridSize + inside;
					y = vCnt * gridSize + gridSize / 2;
					num = knipPolygonsContain(x, y);	
					isRectangle = isRectangle && (num == 1);	
					chkCnt++;
				}
				
			}
//System.out.println("chkCnt = " + chkCnt);
		
		

		
			if (isRectangle)
			{	figureIsRectangle = true;

				owner.kijkNa();
			
				//owner.bottomPanel2.opdrachtLabel.setText(Verknippen.rb.getString("rechthoekTekst"));
				//owner.antwoord = 1;
				//owner.antwoordOK = true;
//				if (owner.ipa != null)
				//owner.produceAction("changed");
				

//System.out.println("rectangle");				
			}
			else
			{	figureIsRectangle = false;
				
				owner.kijkNa();
			
				//owner.bottomPanel2.opdrachtLabel.setText(Verknippen.rb.getString("maakRechthoekTekst"));			
				
				//owner.antwoord = 0;
				//owner.antwoordOK = false;
				
//				if (owner.ipa != null)				
				//owner.produceAction("changed");
			}			
		

	}

	public void handleRotation(KnipPolygon2 rotatePolygon, RealPoint rotateCenter)
	{	rotateVertices.removeAllElements();
		for (int pCnt = 0; pCnt < rotatePolygon.aantalPunten; pCnt++)
		{	if (isOnGrid(rotatePolygon.intPoints[pCnt]))
				rotateVertices.addElement(
					new RealPoint(rotatePolygon.realPoints[pCnt]));
		}
		// dit eerst!!
		RealPoint rp = rotatePolygon.getRotationPoint();
		// draai het polygon, dit is automatisch rond rp
		rotatePolygon.rotate(90);
		// draai de vertices die eerder op de grid lagen
		for (int rCnt = 0; rCnt < rotateVertices.size(); rCnt++)
		{	RealPoint rVertex = (RealPoint) rotateVertices.elementAt(rCnt);
			rotateVertices.remove(rVertex);
			// nieuw punt!
			rVertex = rVertex.rotate(90, rp.x, rp.y);
			rotateVertices.insertElementAt(rVertex, rCnt);
		}
		// zet hier het rotatePolygon op het rooster						
		if (rotateVertices.size() > 0)
		{	
// is 1 punt genoeg??
//System.out.println("rv = " + rotateVertices.size());
			RealPoint rPoint = (RealPoint) rotateVertices.elementAt(0);
			Point aPoint = rPoint.toPoint();
			int gridX = aPoint.x / gridSize;
			int gridDX = aPoint.x % gridSize;
			int gridY = aPoint.y / gridSize;
			int gridDY = aPoint.y % gridSize;
			if (gridDX > (gridSize / 2))
				gridX++;
			if (gridDY > (gridSize / 2))
				gridY++;
			// in doubles vanwege preciezie!!	
			double dx = gridX * gridSize - rPoint.x;
			double dy = gridY * gridSize - rPoint.y;
			rotatePolygon.translate(dx, dy);	
		}	
//System.out.println("put on grid");
		// nieuwe rotatiePunt
		RealPoint rotPoint = rotatePolygon.getRotationPoint();
		oval3Pos = rotPoint.toPoint();
		
if ((owner.taakNummer == 2) || (owner.taakNummer == 3))
	rotatePolygon.setLabelPoint();

		if (owner.taakNummer == 1)
			checkForRectangle();
		
		paint();
	}

	public KnipPolygon2 knipPolygonLabelContains(int x, int y)
	{	KnipPolygon2 result = null;
	
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			if ((kp.labelVisible || (owner.taakNummer == 3)) && 
				(kp.labelRect != null) && 
				kp.labelRect.contains(x, y))
				result = kp;
		}
		
		return result;
	}
	
	public KnipPolygon2 knipPolygonLabelPointContains(RealPoint realClicked)
	{	KnipPolygon2 result = null;
	
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			if ((kp.labelPoint != null) && (owner.taakNummer == 2) && 
				(realClicked.distance(kp.labelPoint) < clickDis))
				result = kp;
		}
		
		return result;
	}
	

	public boolean knipPolygonIntersects(Rectangle r, KnipPolygon2 skip)
	{	boolean result = false;
	
		for (int kpCnt = 0; kpCnt < knipPolygons.size(); kpCnt++)
		{	KnipPolygon2 kp = (KnipPolygon2) knipPolygons.elementAt(kpCnt);
			if (kp != skip)
			{
				Rectangle kpBb = kp.getBoundingBox();
				result = result || kpBb.intersects(r);
				
				if (kp.labelRect != null)
				{	result = result || kp.labelRect.intersects(r);
				}
			}	
		}
		
		return result;
	}

	public void showTekstPopup(int xPos, int yPos, String tfString, KnipPolygon2 labelPoly)
	{
		int popupX = xPos + dp2Canvas.getAbsoluteLeft();
		
		int popupY = yPos + dp2Canvas.getAbsoluteTop();
		
		
		
		if ((tf != null) && tf.isVisible())
		{
			//zetInvulWaarde();
			tf.setVisible(false);
		}

		tf = new TekstPopup(this, labelPoly);
		tf.setText(tfString);
		tf.setWidth("35px");
		tf.setHeight("20px");
		//tf.setModal(true);
		tf.setPopupPosition(popupX, popupY);
		tf.show();
		tf.textBox.setFocus(true);

	}

	public void removeGridOnEdge()
	{
		edgeClickedPolygon = null;
		oldEdgeClickedPolygon = null;
		edgeClickedIndex = -1;
		oldEdgeClickedIndex = -1;
		gridOnEdgeVisible = false;
		gridPointsOnEdge.removeAllElements();
	}
	
	//class MLMML extends MouseAdapter implements MouseMotionListener
	//{	
		int startX, startY;
		int draggCnt;
		int lastMoveX, lastMoveY;
	
		//public void mousePressed(MouseEvent e)
		public void mouseDownTouchStartAction(int eventX, int eventY)
		{	
			if (frozen)
				return;

//GWT			
			//if (invulVeld.isVisible())					
			//	focusLostAction();
			
			if (tekenen)
			{
//System.out.println("tekenen");

				gridPointClicked = gridPointClicked(eventX, eventY);

				if (gridPointClicked != null)
				{
					draggRectangle = new Rectangle(gridPointClicked.x, gridPointClicked.y, 1, 1);
				}
				return;
			}
			
			draggCnt = 0;	
			// let op: firstCutPoint kan voor sommige polygons een vertex zijn,
			// maar voor andere een edgepoint!!
			
			if (firstCutPoint == null)
			{

//System.out.println("fcp == null balletjes zoeken");

				//if (edgeClickedPolygon != null)
				//{	
					oldEdgeClickedPolygon = edgeClickedPolygon;
					oldEdgeClickedIndex = edgeClickedIndex;
				//}
				// dit zet edgeClickedPolygon opnieuw (of null)	
				RealPoint pointClicked = edgeClicked(eventX, eventY);
				if (pointClicked != null)
				{
					
//System.out.println("pointClicked != null");

					// kijk of het nieuwe KnipPolygon2 het oude is
					if (edgeClickedPolygon == oldEdgeClickedPolygon)
					{	
//System.out.println("same Polygon");						
						
						// ja: kijk of een dezelfde edge aangeklikt is
						edgeClickedIndex = edgeClickedPolygon.edgeContainsPoint(pointClicked);
						if (edgeClickedIndex == oldEdgeClickedIndex)
						{	
//System.out.println("same edge");							
							// ja-ja: balletjes weg en verder
							removeGridOnEdge();
							paint();
						}
						else
						{	
//System.out.println("other edge");							
							// ja-nee: balletjes oude weg, balletjes nieuwe verschijnen, return?
							gridPointsOnEdge.removeAllElements();
							// maak de nieuwe balletjes mbv edgeClickedPolygon en edgeClickedIndex 
							RealPoint e1 = edgeClickedPolygon.realPoints[edgeClickedIndex];
							RealPoint e2 = 
								edgeClickedPolygon.realPoints[(edgeClickedIndex + 1) % edgeClickedPolygon.aantalPunten];
							gridPointsOnEdge = edgeClickedPolygon.gridPointsOnEdge(e1, e2, true);
							paint(); 
							return;
						}	
					}
					else
					{	
//System.out.println("other Polygon");
						edgeClickedIndex = edgeClickedPolygon.edgeContainsPoint(pointClicked);
						// nee: balletjes oude weg, balletjes nieuwe verschijnen, return?
						gridPointsOnEdge.removeAllElements();
						// maak de nieuwe balletjes mbv edgeClickedPolygon en edgeClickedIndex
						RealPoint e1 = edgeClickedPolygon.realPoints[edgeClickedIndex];
						RealPoint e2 = 
							edgeClickedPolygon.realPoints[(edgeClickedIndex + 1) % edgeClickedPolygon.aantalPunten];
						gridPointsOnEdge = edgeClickedPolygon.gridPointsOnEdge(e1, e2, true);
//System.out.println("balletjes: " + gridPointsOnEdge.size());						
						paint();
						return;
					}
				}	
				else
				{	
//System.out.println("pointClicked == null");					
					removeGridOnEdge();
					paint();
					//return;
				}
			}
		
			if (firstCutPoint == null)
			{	// kijk eerst of er een vertex aangeklikt is
				RealPoint vClicked = vertexClicked(eventX, eventY);
				if (vClicked != null)
				{	firstCutPoint = vClicked;
					firstCutPolygons = containVertex(firstCutPoint);
					firstCutPolygons2 = containStrictEdgePoint(firstCutPoint);
					for (int pCnt = 0; pCnt < firstCutPolygons2.size(); pCnt++)
					{	KnipPolygon2 kp = (KnipPolygon2) firstCutPolygons2.elementAt(pCnt);
						firstCutPolygons.addElement(kp);
					}
					knippen = true;
					
				}
				else // niet geklikt op een vertex
				{	// kijk of er een gridpoint op een edge is aangeklikt
					// dit dit is dan geen vertex
					RealPoint eClicked = gridPointOnEdgeClicked(eventX, eventY);
					if (eClicked != null)
					{	firstCutPoint = eClicked;
						firstCutPolygons = containStrictEdgePoint(firstCutPoint);
						knippen = true;
					}
					else // geen vertex of edge aangeklikt
					{	// kijk welk knipPolygon (if any)
						// het klikpunt bevat 
						draggPolygon = knipPolygonContains(eventX, eventY);
						if (draggPolygon != null)
						{	
							putOnTop(draggPolygon);
							
							RealPoint realClicked = new RealPoint(eventX, eventY);							
							
							// kijk of (toevallig) het labelPoint van 
							// draggPolygon aangeklikt is
							if ((owner.taakNummer == 2) && 
								(draggPolygon.labelPoint != null)
							   )
							{	if (realClicked.distance(
										draggPolygon.labelPoint) < clickDis)
								{	draggPolygon.labelVisible =
										!draggPolygon.labelVisible;
									paint();	
								
									return;
								}	
							}
							
							
						
							// kijk of (toevallig) het rotationPoint van 
							// draggPolygon aangeklikt is
							//RealPoint realClicked = new RealPoint(e.getX(), e.getY());
							RealPoint rotPoint = draggPolygon.getRotationPoint();
							if (realClicked.distance(rotPoint) < clickDis)
							{	// roteer en stop ermee(?)
								handleRotation(draggPolygon, rotPoint);
								return;
							}
							// maak sowieso het rotationPoint zichtbaar
							oval3Pos = rotPoint.toPoint();						

							// bereid de dragg voor
							draggVertices.removeAllElements();
							for (int pCnt = 0; pCnt < draggPolygon.aantalPunten; pCnt++)
							{	if (isOnGrid(draggPolygon.intPoints[pCnt]))
									draggVertices.addElement(
										new Point(draggPolygon.intPoints[pCnt]));
							}
							dragging = true;
							knippen = false;
							startX = eventX;
							startY = eventY;
						} // draggPolygon != null
						
						else // kijk of er op een label of op een deel van een labelpunt buiten het polygon geklikt is
						{	
							KnipPolygon2 labelPoly = knipPolygonLabelContains(eventX, eventY);
							if (labelPoly != null)
							{	
//System.out.println("label");							
//System.out.println("in-w = " + invulVeld.getSize().width);
//System.out.println("in-h = " + invulVeld.getSize().height);

								//if (invulVeld.isVisible())
								//{	focusLostAction();
									
									
								//}
									
//System.out.println("labelPoly found");
								
								labelPolygon = labelPoly;
								//invulVeld.setLocation(labelPoly.labelRect.x, labelPoly.labelRect.y);
								String tfString = "";
								if (labelPolygon.oppervlakte > 0)	
									tfString = "" + labelPoly.oppervlakte;
								//else
								//	invulVeld.setText("");
								
								showTekstPopup(labelPoly.labelRect.x, labelPoly.labelRect.y, tfString, labelPolygon);
								
								//invulVeld.setVisible(true);		
								//invulVeld.requestFocus();
								
								//labelPolygon = labelPoly;								
								
								//paint();
							}
							else
							{
								RealPoint realClicked = new RealPoint(eventX, eventY);							
								
								// kijk of het labelPoint van een draggPolygon aangeklikt is
								KnipPolygon2 labelPointPolygon = knipPolygonLabelPointContains(realClicked);
								
								if ((owner.taakNummer == 2) && (labelPointPolygon != null))
								{	//if (realClicked.distance(
									//		draggPolygon.labelPoint) < clickDis)
									//{	
									labelPointPolygon.labelVisible =
										!labelPointPolygon.labelVisible;
									paint();	
									
									return;
									//}	
								}
															
							}
						}
						
					}
				}	
			}
			else // eerst knippunt is gekozen
			// nu tweede knippunt
			{	// kijk eerst of er een vertex aangeklikt is
				// die moet een vertex zijn van een polygon dat
				// firstCutPoint bevat
				RealPoint vClicked = vertexClicked(eventX, eventY);
				if ((vClicked != null) && 
					(containVertex(firstCutPolygons, vClicked).size() > 0))
				{	secondCutPoint = vClicked;
					
					// niet op firstCutPoint klikken
					if (firstCutPoint.equals(secondCutPoint))
					{	firstCutPoint = null;				
						knippen = false;
						oval1Pos = null;
						oval2Pos = null;
						paint();
						return;
					}
					
				} // vClicked != null
				else // niet geklikt op een vertex
				{	// kijk of er een gridpunt op een edge is aangeklikt
					// dit is dan geen vertex
					RealPoint eClicked = gridPointOnEdgeClicked(eventX, eventY);
					// dit moet een edgepoint zijn van een polygon dat
					// firstCutPoint bevat
					if ((eClicked != null) &&
					    (containStrictEdgePoint(firstCutPolygons, eClicked).size() > 0))						
					{	secondCutPoint = eClicked;
						
						// niet op firstCutPoint klikken
						if (firstCutPoint.equals(secondCutPoint))
						{	firstCutPoint = null;				
							knippen = false;
							oval1Pos = null;
							oval2Pos = null;
							paint();
							return;
						}	
						
					}
					else // geen valide klik, einde knippen
					{	// het tweede aangeklikte punt kan elk punt zijn
						firstCutPoint = null;				
						knippen = false;
						oval1Pos = null;
						oval2Pos = null;
						paint();
						return;
					}	
					
				}
				
				oval1Pos = null;
				oval2Pos = null;
				// hier doorsnijden!!
				knipPolygons();
				
				oval3Pos = null;
				firstCutPoint = null;				
				knippen = false;
				paint();
			}
		}
		
		//public void mouseReleased(MouseEvent e)
		public void mouseUpTouchEndAction(int eventX, int eventY)
		{	
			if (frozen)
				return;
			
			if (tekenen && gridPointClicked != null)
			{
				Point releasePoint = findClosestGridPoint(new Point(eventX, eventY));
				if ((gridPointClicked.x != releasePoint.x) && 
					(gridPointClicked.y != releasePoint.y))
				{
					Rectangle aRect = null;
					if ((releasePoint.x > gridPointClicked.x) &&
						(releasePoint.y > gridPointClicked.y))
					{	aRect = new Rectangle(gridPointClicked.x, gridPointClicked.y, 
											  releasePoint.x - gridPointClicked.x, releasePoint.y - gridPointClicked.y); 
					}	
					else if ((releasePoint.x > gridPointClicked.x) &&
							 (releasePoint.y < gridPointClicked.y))
					{	aRect = new Rectangle(gridPointClicked.x, releasePoint.y, 
											  releasePoint.x - gridPointClicked.x, gridPointClicked.y - releasePoint.y); 
					}	
					else if ((releasePoint.x < gridPointClicked.x) &&
							 (releasePoint.y > gridPointClicked.y))
					{	aRect = new Rectangle(releasePoint.x, gridPointClicked.y, 
							                  gridPointClicked.x - releasePoint.x, releasePoint.y - gridPointClicked.y); 
					}	
					else if ((releasePoint.x < gridPointClicked.x) &&
							 (releasePoint.y < gridPointClicked.y))
					{	aRect = new Rectangle(releasePoint.x, releasePoint.y, 
							                  gridPointClicked.x - releasePoint.x, gridPointClicked.y - releasePoint.y); 
					}	
						
					gridPointClicked = null;
					draggRectangle = null;
					
					rectangles.addElement(aRect);
					
					paint();
					
					
				}
				
				return;
			}
			
			if (knippen && (draggCnt >= 2)) // eerst knippunt is gekozen
			// nu tweede knippunt
			{	// kijk eerst of er een vertex onder de mouse-up ligt
				// die moet een vertex zijn van een polygon dat
				// firstCutPoint bevat
				RealPoint vClicked = vertexClicked(eventX, eventY);
				if ((vClicked != null) && 
					(containVertex(firstCutPolygons, vClicked).size() > 0))
				{	secondCutPoint = vClicked;
				
					// niet firstCutPoint
					if (firstCutPoint.equals(secondCutPoint))
					{	firstCutPoint = null;				
						knippen = false;
						oval1Pos = null;
						oval2Pos = null;
						paint();
						return;
					}
					
				} // vClicked != null
				else // geen vertex onder de mouse-up
				{	// kijk of er een gridpunt op een edge onder de mouse-up
					// ligt, dit is dan geen vertex
					RealPoint eClicked = gridPointOnEdgeClicked(eventX, eventY);
					// dit moet een edgepoint zijn van een polygon dat
					// firstCutPoint bevat
					if ((eClicked != null) &&
					    (containStrictEdgePoint(firstCutPolygons, eClicked).size() > 0))						
					{	secondCutPoint = eClicked;
					
						// niet firstCutPoint
						if (firstCutPoint.equals(secondCutPoint))
						{	firstCutPoint = null;				
							knippen = false;
							oval1Pos = null;
							oval2Pos = null;
							paint();
							return;
						}	
						
					}
					else // geen valide mouse-up, einde knippen
					{	// het tweede punt kan elk punt zijn
						firstCutPoint = null;				
						knippen = false;
						oval1Pos = null;
						oval2Pos = null;
						paint();
						return;
					}	
					
				}
				
				oval1Pos = null;
				oval2Pos = null;
				// hier doorsnijden!!
				if (!firstCutPoint.equals(secondCutPoint))
				{	knipPolygons();
//removeGridOnEdge();				
				}
				
				oval3Pos = null;
				firstCutPoint = null;				
				knippen = false;
				paint();
			} // if knippen
		
		
			if (!knippen && dragging)
			{
				// zet hier het draggPolygon op het rooster						
				if (draggVertices.size() > 0)
				{	
// is 1 punt genoeg??
					Point aPoint = (Point) draggVertices.elementAt(0);
					int gridX = aPoint.x / gridSize;
					int gridDX = aPoint.x % gridSize;
					int gridY = aPoint.y / gridSize;
					int gridDY = aPoint.y % gridSize;
					if (gridDX > (gridSize / 2))
						gridX++;
					if (gridDY > (gridSize / 2))
						gridY++;
					int dx = gridX * gridSize - aPoint.x;
					int dy = gridY * gridSize - aPoint.y;
					draggPolygon.translate(dx, dy);	
										
				}
				RealPoint rotPoint = draggPolygon.getRotationPoint();
				oval3Pos = rotPoint.toPoint();
				
if ((owner.taakNummer == 2) || (owner.taakNummer == 3))		
	draggPolygon.setLabelPoint();
				
				dragging = false;
				draggPolygon = null;
	
				if (owner.taakNummer == 1)			
					checkForRectangle();			
				
				paint();
			}
		}
		
		
		//public void mouseDragged(MouseEvent e)
		public void mouseMoveTouchMoveAction(int eventX, int eventY)
		{	
			if (frozen)
				return;
			
			lastMoveX = eventX;
			lastMoveY = eventY;
			
			if (tekenen && gridPointClicked != null)
			{
				if ((eventX > gridPointClicked.x) &&
					(eventY > gridPointClicked.y))
				{	draggRectangle = new Rectangle(gridPointClicked.x, gridPointClicked.y, 
						                           eventX - gridPointClicked.x, eventY - gridPointClicked.y); 
				}	
				else if ((eventX > gridPointClicked.x) &&
						 (eventY < gridPointClicked.y))
				{	draggRectangle = new Rectangle(gridPointClicked.x, eventY, 
							                       eventX - gridPointClicked.x, gridPointClicked.y - eventY); 
				}	
				else if ((eventX < gridPointClicked.x) &&
						 (eventY > gridPointClicked.y))
				{	draggRectangle = new Rectangle(eventX, gridPointClicked.y, 
							                       gridPointClicked.x - eventX, eventY - gridPointClicked.y); 
				}	
				else if ((eventX < gridPointClicked.x) &&
						 (eventY < gridPointClicked.y))
				{	draggRectangle = new Rectangle(eventX, eventY, 
							                       gridPointClicked.x - eventX, gridPointClicked.y - eventY); 
				}	
				
				paint();
				
				return;
			}
			
			draggCnt++;
		
			if (knippen)
			{	cursorPoint = new Point(eventX, eventY);
				RealPoint vertexMoved = vertexClicked(eventX, eventY);
				// cursor is mogelijk boven een vertex
				// if yes, this must be a vertex on one of the polygons containing
				// firstCutPoint 
				if ((vertexMoved != null) &&
					(containVertex(firstCutPolygons, vertexMoved).size() > 0))
				{	oval2Pos = vertexMoved.toPoint();
				}
				else // cursor niet boven een vertex of boven een vertex
				// van een niet toegestaan polygon
				{	RealPoint edgePointMoved =
						gridPointOnEdgeClicked(eventX, eventY);
					// cursor mogelijk boven een gridPoint op een edge
					// if yes, this must be on an edge of one of the polygons
					// containing firstCutPoint 	
					if ((edgePointMoved != null) && 
					    (containStrictEdgePoint(firstCutPolygons, edgePointMoved).size() > 0))						
						oval2Pos = edgePointMoved.toPoint();
					else	
						oval2Pos = null;
				}				
				paint();
			}
			if (dragging)
			{	int dx = eventX - startX;
				int dy = eventY - startY;
				draggPolygon.translate(dx, dy);
				if (oval3Pos != null)
					oval3Pos.translate(dx, dy);
				for (int pCnt = 0; pCnt < draggVertices.size(); pCnt++)
				{	Point aPoint = (Point) draggVertices.elementAt(pCnt);
					aPoint.translate(dx, dy);
				}
		if ((owner.taakNummer == 2) || (owner.taakNummer == 3))
			{	//draggPolygon.setLabelPoint();
				if ((draggPolygon.labelPoint != null) &&
					(draggPolygon.labelRect != null))
				{	draggPolygon.labelPoint.translate(dx, dy);
					draggPolygon.labelRect.translate(dx, dy);
				}	
			}

				paint();
				startX = eventX;
				startY = eventY;
			}
		}
		
//GWT??
/*		
		public void mouseMoved(MouseEvent e)
		{	
			if (frozen)
				return;
			
			if (tekenen)
			{
				return;
			}
			
			// nog geen eerste punt gekozen, knippen=false
			if (!knippen)
			{	RealPoint vertexMoved = vertexClicked(e.getX(), e.getY());
				if (vertexMoved != null)
				{	oval1Pos = vertexMoved.toPoint();
// tijdelijk
//oval1Color = Color.blue;				

				}
				else // geen vertex
				{	RealPoint edgePointMoved =
						gridPointOnEdgeClicked(e.getX(), e.getY());
					if (edgePointMoved != null)
					{	oval1Pos = edgePointMoved.toPoint();
// tijdelijk
//oval1Color = Color.green;					

					}
					else // geen edge
					{	oval1Pos = null;
					}
				}
			}	
			// eerste punt is gekozen		
			if (knippen)
			{	cursorPoint = new Point(e.getX(), e.getY());
				RealPoint vertexMoved = vertexClicked(e.getX(), e.getY());
				// cursor is mogelijk boven een vertex
				// if yes, this must be a vertex on one of the polygons containing
				// firstCutPoint 
				if ((vertexMoved != null) &&
					(containVertex(firstCutPolygons, vertexMoved).size() > 0))
				{	oval2Pos = vertexMoved.toPoint();
				}
				else // cursor niet boven een vertex of boven een vertex
				// van een niet toegestaan polygon
				{	RealPoint edgePointMoved =
						gridPointOnEdgeClicked(e.getX(), e.getY());
					// cursor mogelijk boven een gridPoint op een edge
					// if yes, this must be on an edge of one of the polygons
					// containing firstCutPoint 	
					if ((edgePointMoved != null) && 
					    (containStrictEdgePoint(firstCutPolygons, edgePointMoved).size() > 0))						
					{	oval2Pos = edgePointMoved.toPoint();
					}
					else	
						oval2Pos = null;
				}
				
			}
			repaint();			
		}
*/		
	
//	}
	
//GWT
/*		
	public void focusLostAction()
	{	String text = invulVeld.getText();

//System.out.println("fl = " + text);			

		String text1 = trimTrailingZeros(text);
		boolean changed1 = (text.length() != text1.length());
		String text2 = addLeadingZero(text1);
		boolean changed2 = (text1.length() != text2.length());
		if (changed1 || changed2)
		{	text = text2;
			invulVeld.setText(text);
		}

		invulVeld.setVisible(false);
		
		boolean error = false;
		int oNum = 0;
		try
		{	oNum = Integer.parseInt(text);
		}
		catch (NumberFormatException nfe)
		{	error = true;
		}
		if (!error)
		{	labelPolygon.oppervlakte = oNum;
//System.out.println("o = " + oNum);			
		}	
			
		repaint();

	}
*/
// niet gebruikt
//GWT
/*	
	class TextFL implements FocusListener
	{	public void focusGained(FocusEvent e)
		{
		}
		public void focusLost(FocusEvent e)
		{	focusLostAction();
		}
	}
*/	
//GWT
/*	
	class TextAL implements ActionListener
	{	public void actionPerformed(ActionEvent e)
		{	
			String text = invulVeld.getText();
			
			String text1 = trimTrailingZeros(text);
			boolean changed1 = (text.length() != text1.length());
			String text2 = addLeadingZero(text1);
			boolean changed2 = (text1.length() != text2.length());
			if (changed1 || changed2)
			{	text = text2;
				invulVeld.setText(text);
			}
			
			invulVeld.setVisible(false);
			
			boolean error = false;
			int oNum = 0;
			try
			{	oNum = Integer.parseInt(text);
			}
			catch (NumberFormatException nfe)
			{	error = true;
			}
			if (!error)
			{	labelPolygon.oppervlakte = oNum;
			}	
			
			repaint();

		}
	}
*/
	public String trimTrailingZeros(String s)
	{	String txt = new String(s);
		if (txt.indexOf('.') < 0)
			return txt;
		char c = txt.charAt(txt.length() - 1);
		while (c == '0')
		{	txt = removeCharAt(txt, txt.length() - 1);
			c = txt.charAt(txt.length() - 1);
		}	
		c = txt.charAt(txt.length() - 1);
		if (c == '.')
			txt = removeCharAt(txt, txt.length() - 1);
		return txt;		
	}				
		
	public String addLeadingZero(String s)
	{	String txt = new String(s);
		// met minteken
		if ((txt.length() >= 2) && (txt.charAt(0) == '-') &&
			(txt.charAt(1) == '.'))
		{	txt = "-0" + txt.substring(1);
		}	
		// zonder minteken
		if ((txt.length() >= 1) && (txt.charAt(0) == '.'))
		{	txt = "0" + txt;
		}
		return txt;
	}

	public String removeCharAt(String s, int index)
	{	String txt = new String(s);
		// eerste
		if (index == 0)
			txt = txt.substring(1);
		// laatste	
		else if (index == (txt.length() - 1))
			txt = txt.substring(0, txt.length() - 1);
		// middenin	
		else
		{	String txt1 = txt.substring(0, index);
			String txt2 = txt.substring(index + 1);
			txt = txt1 + txt2;
		}
		return txt;
	}		
	
//GWT
/*	
	class InputKL extends KeyAdapter
	{	public void keyReleased(KeyEvent e)
		{	
			String txt = invulVeld.getText();
			boolean corrected = false;
			// kijk of txt illegale characters bevat
			// dit zou er maximaal 1 moeten zijn
			int index = -1;
			for (int cCnt = 0; cCnt < txt.length(); cCnt++)
			{	char c = txt.charAt(cCnt);
				if (!isLegal(c))
					index = cCnt;
			}
			// verwijder illegaal karakter
			if (index >= 0)
			{	txt = removeCharAt(txt, index);
				corrected = true;
			}
			// leading zeros, leiden niet tot een NumberFormatException
			// geen minteken
			if (//(txt.indexOf('-') < 0) && 
				(txt.length() >= 2) &&
				(txt.charAt(0) == '0') && Character.isDigit(txt.charAt(1)))
			{	txt = removeCharAt(txt, 0);
				corrected = true;
			}
			
// trailing zeros na(!) decimale punt oplossen 
// bij actionPerformed of focusLost			

			
			if (corrected)
				invulVeld.setText(txt);
			
		}
		
		public boolean isLegal(char c)
		{	return Character.isDigit(c); // || (c == '-') || (c == '.');
		}
	}
*/	
//GWT
/*	
	class TekenGumIL implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			if (frozen)
				return;

			itemChanged = true;
			
			if (tekenButton.isSelected())
			{  
				tekenen = true;
				boolean error = false;
				Cursor drawCursor = null;
				try
				{	
					drawCursor = Toolkit.getDefaultToolkit().
						createCustomCursor(owner.tekenCursor,
							new Point(10, 10), "TEKEN_CURSOR");
				}
				catch (IndexOutOfBoundsException ioobe)
				//catch (HeadlessException he)
				{	error = true;
				}
				if (!error)
				{	setCursor(drawCursor);
				}				
			}
			else if (gumButton.isSelected())
			{
				tekenen = false;
				rectangles.removeAllElements();
				geenButton.setSelected(true);
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			else
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				tekenen = false;
			}
			
			repaint();
		}
	}
*/	
//GWT
/*	
	class TekenGumAL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (!itemChanged)
			{	geenButton.setSelected(true);
			}
			
			itemChanged = false;
		}
	}
*/	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			
//System.out.println("mouseDown");

			//e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			mouseDown = true;
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			mouseDownTouchStartAction(eventX, eventY);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			//e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouseMov");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();

			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			//e.preventDefault();
			// prevent scrolling
			e.stopPropagation();

//System.out.println("mouseUp");

			mouseDown = false;
		
			mouseUpTouchEndAction(lastMoveX, lastMoveY);

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
				
				int eventX = touch.getPageX() - dp2Canvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - dp2Canvas.getAbsoluteTop();				
				
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
				
			    int eventX = touch.getPageX() - dp2Canvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - dp2Canvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
//GWT check het TouchEndEvent
			
			mouseUpTouchEndAction(lastMoveX, lastMoveY);
		}

	}

}