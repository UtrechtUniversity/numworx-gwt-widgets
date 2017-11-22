package fi.weblogogwt.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import fi.weblogogwt.client.logotekenap.TraceBeheerder;
import fi.weblogogwt.client.logotekenap.Uitvoerblad;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CssColor;
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

/**
 * class representing the program area: on the left it contains heaps (piles) of the basic program components,
 * that can be dragged into the actual program or into subroutines (the latter are optional);  
 * the class also contains a Canvas on which the program components, the program and the subroutines
 * are drawn, and this Canvas processes any mouse (touch) actions on the components  
 *  
 * @author Peter Boon
 */
public class JavaLogoSchuifVeld extends LayoutPanel 
{
	/**
	 * Number of subroutines
	 */
	public static final int aantalDeeltaken = 5;
	
	/**
	 * Maximum number of parameters in a subroutines
	 */
	public static final int maxParamCount = 4;

	/**
	 * commCompLargeWidth: width of two-column component (in pile)
	 */
	public static final int cclw = 160;
	/**
	 * commCompSmallWidth: width of single column component (in pile)
	 */
	public static final int ccsw = cclw/2-5;
	/**
	 * deeltaakCompWidth: width of deeltaak call (in pile)
	 */
	public static final int dtcw = cclw-30;
	/**
	 * commCompLargeHeight: width of herhaal/keuze component (in pile)
	 */
	public static final int cclh = 50;
	/**
	 * commCompSmallHeight: height of simple component
	 */
	public static final int ccsh = 25;
	/**
	 * CommandComponentX: x-pos of left column
	 */
	public static final int ccx = 10;
	/**
	 * CommandComponentX2: x-pos of right column
	 */
	public static final int ccx2 = ccx+cclw/2+5;
	/**
	 * CommandComponentY: y-pos of TOP cc
	 */
	public static final int ccy = 10;
	
	/**
	 * ProgrammaPanelWidth
	 */
	public static final int ppw = 400;
	/**
	 * ProgrammaPanelHeight
	 */
	public static int pph = 525;
	/**
	 * ProgrammaPanelX
	 */
	public static final int ppx = 190;
	/**
	 * ProgrammaPanelY
	 */
	public static final int ppy = 10;

	/**
	 * rectangle containing the ProgrammaComponent and the 
	 * DeeltaakBodyComponents; used for dragging CC's
	 */
	private Rectangle programmaPanel;
	/**
	 * the component containing the actual program, see class ProgrammaComponent
	 */
	private ProgrammaComponent programmaComponent;
	/**
	 * the subroutines, see class DeeltaakBodyComponent
	 */
	private DeeltaakBodyComponent[] deeltaakComponenten;

	/**
	 * if true, prevents dragging outside the Canvas 
	 */
	private boolean gesloten;
	
	/**
	 * the available CommandComponents
	 */
	private CommandComponent vooruitCC;
	private CommandComponent stapCC;
	private CommandComponent linksCC;
	private CommandComponent rechtsCC;
	private CommandComponent penAanCC;
	private CommandComponent penUitCC;
	private CommandComponent vulAanCC;
	private CommandComponent vulUitCC;
	private CommandComponent vulbladCC;
	private CommandComponent printCC;
	private CommandComponent printlCC;
	private CommandComponent varCC;
	private CommandComponent herhaalCC;
	private CommandComponent whileCC;
	private CommandComponent keuzeCC;
	private CommandComponent[] deeltaakCC;
	
	/**
	 * the variables in the current progam
	 */
	private HashMap<String, Object> inputVars = new HashMap<String, Object>();
	
	/**
	 * simulating a Java Component
	 */
	int xPos, yPos, breedte, hoogte;
	/**
	 * Canvas to be drawn on
	 */
	Canvas jlsvCanvas;
	/**
	 * Context2d to be drawn with
	 */
	Context2d jlsvContext2d;
	
	/**
	 * all CommandComponents contained in this JavaLogoSchuifVeld, necessary for painting and
	 * event handling
	 */
	Vector<CommandComponent> ccs = new Vector<CommandComponent>();
	
	/**
	 * reference to any visible PopupPanel for editing parameters; keep track of open
	 * PopupPanels here, so that they can be processed and closed when a new one is
	 * opened elsewhere; see class ParameterCommandComponent
	 */
	ParameterTextField paramEditor;
	
	/**
	 * a PopupPanel for exporting code, see class ExportPopup 
	 */
	ExportPopup exportPopup;
	/**
	 * a PopupPanel for importing code, see class ImportPopup 
	 */
	ImportPopup importPopup;
	
	/**
	 * the last CommandComponent with a caret, see method traceComponent 
	 */
	CommandComponent traceC;
	/**
	 * the last CommandContainer with a caret, see method traceComponent 
	 */
	CommandContainer traceCC;
	
	/**
	 * constructor, create drawing Canvas and add mouse/touch listeners to this Canvas
	 * @param x x-position
	 * @param y y-position
	 * @param b width
	 * @param h height
	 */
	public JavaLogoSchuifVeld(int x, int y, int b, int h)
	{	
		xPos = x; yPos = y; breedte = b; hoogte = h;
		
		jlsvCanvas = Canvas.createIfSupported();
		jlsvCanvas.setWidth(b + "px");
		jlsvCanvas.setHeight(h + "px");
		jlsvCanvas.setCoordinateSpaceWidth(b);
		jlsvCanvas.setCoordinateSpaceHeight(h);
		add(jlsvCanvas);
		setWidgetLeftWidth(jlsvCanvas, 0, Style.Unit.PX, breedte, Style.Unit.PX);
		setWidgetTopHeight(jlsvCanvas, 0, Style.Unit.PX, hoogte, Style.Unit.PX);

		MouseHandler mouseHandler = new MouseHandler();
		jlsvCanvas.addMouseDownHandler(mouseHandler);
		jlsvCanvas.addMouseMoveHandler(mouseHandler);
		jlsvCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		jlsvCanvas.addTouchStartHandler(touchHandler);
		jlsvCanvas.addTouchMoveHandler(touchHandler);
		jlsvCanvas.addTouchEndHandler(touchHandler);
	}
	
	/**
	 * getter for the drawing Canvas
	 * @return  jlsvCanvas
	 */
	public Canvas getCanvas()
	{
		return jlsvCanvas;
	}
	
	/**
	 * initialize the Context2d used for drawing on jlsvCanvas 
	 */
	public void initContext2d() 
	{
		jlsvContext2d = jlsvCanvas.getContext2d();
		
	}

	/**
	 * simulating a Java Component
	 * @return dimension of this JavaLogoSchuifVeld
	 */
	public Dimension getSize()
	{
		return new Dimension(breedte,hoogte);
	}
	
	/**
	 * simulating a Java Component
	 * @return height of this JavaLogoSchuifVeld
	 */
	public int getHeight()
	{
		return hoogte;
	}

	/**
	 * simulating a Java Component
	 * @return width of this JavaLogoSchuifVeld
	 */
	public int getWidth()
	{
		return breedte;
	}

	/**
	 * add a variable to the HashMap inputVars
	 * @param name name of the vaiable
	 * @param value value of the variable
	 */
	public void setInputVar(String name, double value)
	{	inputVars.put("input" + name, new Double(value));
	}
	
	/**
	 * setter for inputVars (setState)
	 * @param inputVars new HashMap for inputVars
	 */
	public void setInputVars(HashMap<String, Object> inputVars)
	{	this.inputVars = inputVars;
	}
	
	/**
	 * setter for inputVars (getState)
	 * @return inputVars
	 */
	public HashMap<String, Object> getInputVars()
	{	return inputVars;
	}
	
	/**
	 * execute the program: create a varSet with all variable names and their values
	 * from the HashMap inputVars, then call the main program 
	 * @param trb the tracing controller
	 * @param ub drawing area
	 */
	public void execute(TraceBeheerder trb,Uitvoerblad ub)
	{	
		VarSet varSet = new VarSet();
		Iterator iter = (inputVars.keySet()).iterator();
		while (iter.hasNext()) 
		{
			String key = (String)iter.next();
			double value = ((Double) inputVars.get(key)).doubleValue();
			varSet.setParameter(key, value);
		}
		programmaComponent.execute(trb,ub, varSet);
	}
	
	/**
	 * initialize this instance of JavaLogoSchuifVeld: create all CC-piles,
	 * the main program and the subroutines; depending on the parametrization,
	 * elements are removed at a later stage 
	 */
	public void initialize()
	{	
		zetGesloten(true);
		pph = hoogte - 20;
		programmaPanel = new Rectangle(ppx, ppy, ppw, pph);
		// main program
		programmaComponent = new ProgrammaComponent(ppx, ppy, ProgrammaComponent.pcsw, pph, 
													WebLogoGWT.rb.tekenalgorithmeTekst(), this);
		// not draggeble
		programmaComponent.zetVast(true);
		
		ccs.addElement(programmaComponent);
				
		vooruitCC = new VooruitCComponent(ccx,ccy,ccsw,ccsh, this);
		ccs.addElement(vooruitCC);
				
		stapCC = new StapCComponent(ccx2,ccy,ccsw,ccsh, this);
		ccs.addElement(stapCC);
	
		linksCC = new LinksCComponent(ccx,ccy+30,ccsw,ccsh, this);
		ccs.addElement(linksCC);
		
		rechtsCC = new RechtsCComponent(ccx2,ccy+30,ccsw,ccsh, this);
		ccs.addElement(rechtsCC);
			
		penAanCC = new PenAanCComponent(ccx,ccy+60,ccsw,ccsh, this);
		ccs.addElement(penAanCC);
			
		penUitCC = new PenUitCComponent(ccx2,ccy+60,ccsw,ccsh, this);
		ccs.addElement(penUitCC);
			
		vulAanCC = new VulAanCComponent(ccx,ccy+90,ccsw,ccsh, this);
		ccs.addElement(vulAanCC);
			
		vulUitCC = new VulUitCComponent(ccx2,ccy+90,ccsw,ccsh, this);
		ccs.addElement(vulUitCC);
		
		vulbladCC = new VulBladCComponent(ccx,ccy+120,ccsw,ccsh, this);
		ccs.addElement(vulbladCC);
			
		printCC = new PrintCComponent(ccx,ccy+150,ccsw,ccsh, this);
		ccs.addElement(printCC);
			
		printlCC = new PrintlCComponent(ccx2,ccy+150,ccsw,ccsh, this);
		ccs.addElement(printlCC);
		
		varCC = new VarCComponent(ccx,ccy+190,cclw,ccsh, this);
		ccs.addElement(varCC);
	
		herhaalCC = new ForLoopCommandComponent(ccx,ccy+230,cclw,ccsh+10, this);
		ccs.addElement(herhaalCC);
		
		whileCC = new WhileLoopCommandComponent(ccx,ccy+270,cclw,ccsh+10, this);
		ccs.addElement(whileCC);
		
		keuzeCC = new KeuzeCommandComponent(ccx,ccy+310,cclw,ccsh+10, this);
		ccs.addElement(keuzeCC);
        
		deeltaakComponenten = new DeeltaakBodyComponent[aantalDeeltaken];
		deeltaakCC = new DeeltaakCallCComponent[aantalDeeltaken];
		for(int i=0; i < aantalDeeltaken; i++)
		{
			deeltaakCC[i] = new DeeltaakCallCComponent(xPos+ccx,yPos+ccy+360+30*i,cclw,ccsh, i+1, this);
			ccs.addElement(deeltaakCC[i]);

			// create with dummy location and height
			deeltaakComponenten[i] = new DeeltaakBodyComponent(0,0,ProgrammaComponent.pcsw,ProgrammaComponent.pcclosedh, 
															   WebLogoGWT.rb.deeltaakTekst()+(i+1), this);
			deeltaakComponenten[i].zetVast(false);
			((DeeltaakCallCComponent)deeltaakCC[i]).setBody(deeltaakComponenten[i]);
			ccs.addElement(deeltaakComponenten[i]);
		}
		// set location and height right, one by one...
		deeltaakComponenten[0].setLocation(xPos+ppx+ProgrammaComponent.pcsw+40, yPos+ccy+0);
		// was initialialized as closed, so this will open it
		deeltaakComponenten[0].changeHeight();
		deeltaakComponenten[1].setLocation(xPos+ppx+ProgrammaComponent.pcsw+10, yPos+ccy+180);
		// was initialialized as closed, so this will open it
		deeltaakComponenten[1].changeHeight();
		deeltaakComponenten[2].setLocation(xPos+ppx+ProgrammaComponent.pcsw+20, yPos+ccy+400);
		deeltaakComponenten[3].setLocation(xPos+ppx+ProgrammaComponent.pcsw+30, yPos+ccy+415);
		deeltaakComponenten[4].setLocation(xPos+ppx+ProgrammaComponent.pcsw+40, yPos+ccy+430);
	}
	
	/**
	 * add a CC to the Vector of CC's in this JavaLogoSchuifVeld   
	 * @param c CC to add
	 */
	void addToProgrammaPanel(CommandComponent c)
	{
		//programmaPanel.add(c, 0);
		ccs.addElement(c);
	}
	
	/**
	 * put CommandComponent cc at index 0 in css, so that it will be drawn
	 * as the last CC (thus on top of all others)
	 * @param cc CC to be put on top
	 */
	public void putOnTop(CommandComponent cc)
	{	ccs.removeElement(cc);
		ccs.insertElementAt(cc,0);
	}
	
	/**
	 * CommandComponent cc was dragged from a pile (heap); at the start of the dragg
	 * put cc's isStapel to false and create a new CC of the same type to represent
	 * the pile (heap); see method mouseDragged in class CommandComponent 
	 * @param cc CC needing a new pile 
	 */
	public void zetStapel(CommandComponent cc)
	{	int x = cc.getLocation().x;
		int y = cc.getLocation().y;
		int b = cc.getSize().width;
		int h = cc.getSize().height;
		
		if(cc == printCC)
		{ 	printCC = new PrintCComponent(x,y,b,h, this);
			ccs.addElement(printCC);
		}
		if(cc == vulbladCC)
		{ 	vulbladCC = new VulBladCComponent(x,y,b,h, this);
			ccs.addElement(vulbladCC);
		}
		if(cc == printlCC)
		{ 	printlCC = new PrintlCComponent(x,y,b,h, this);
			ccs.addElement(printlCC);
		}
		if(cc == penAanCC)
		{ 	penAanCC = new PenAanCComponent(x,y,b,h, this);
			ccs.addElement(penAanCC);
		}
		if(cc == penUitCC)
		{ 	penUitCC = new PenUitCComponent(x,y,b,h, this);
			ccs.addElement(penUitCC);
		}		
		if(cc == vooruitCC)
		{ 	vooruitCC = new VooruitCComponent(x,y,b,h, this);
			ccs.addElement(vooruitCC);
		}
		if(cc == linksCC)
		{ 	linksCC = new LinksCComponent(x,y,b,h, this);
			ccs.addElement(linksCC);
		}
		if(cc == rechtsCC)
		{ 	rechtsCC = new RechtsCComponent(x,y,b,h, this);
			ccs.addElement(rechtsCC);
		}
		if(cc == vulAanCC)
		{ 	vulAanCC = new VulAanCComponent(x,y,b,h, this);
			ccs.addElement(vulAanCC);
		}
		if(cc == vulUitCC)
		{ 	vulUitCC = new VulUitCComponent(x,y,b,h, this);
			ccs.addElement(vulUitCC);
		}
		if(cc == stapCC)
		{ 	stapCC = new StapCComponent(x,y,b,h, this);
			ccs.addElement(stapCC);
		}
		
		if(cc == herhaalCC)
		{ 	herhaalCC = new ForLoopCommandComponent(x,y,b,h, this);
			ccs.addElement(herhaalCC);
			cc.setSize(cc.getWidth(), cclh);
		}
		
		if(cc == whileCC)
		{ 	whileCC = new WhileLoopCommandComponent(x,y,b,h, this);
			ccs.addElement(whileCC);
			cc.setSize(cc.getWidth(), cclh);
		}
	
		if(cc == keuzeCC)
        {   keuzeCC = new KeuzeCommandComponent(x,y,b,h, this);
        	ccs.addElement(keuzeCC);
        	cc.setSize(cc.getWidth(), cclh);
        }
		
		if(cc == varCC)
		{ 	varCC = new VarCComponent(x,y,b,h, this);
			ccs.addElement(varCC);
		}
		
		for(int i=0; i<aantalDeeltaken; i++)
		{	if(cc == deeltaakCC[i])
			{ 	deeltaakCC[i] = new DeeltaakCallCComponent((DeeltaakCallCComponent) cc, this);
				ccs.addElement(deeltaakCC[i]);
			}
		}
	}

	/**
	 * reposition the piles after changing the visibility of one or more
	 * heaps of CommandComponenets 
	 */
	private void herschikStapel()
	{	int yLocation = ccy;
		
		if (vooruitCC.isVisible())
			yLocation += 150;
		
		printCC.setLocation(printCC.getX(), yLocation);
		printlCC.setLocation(printlCC.getX(), yLocation);
		
		if (printCC.isVisible())
			yLocation += 40;
		else
			yLocation += 10;
		
		varCC.setLocation(varCC.getX(), yLocation);
		
		yLocation += 40;
		herhaalCC.setLocation(herhaalCC.getX(), yLocation);
		
		yLocation += 40;
		whileCC.setLocation(whileCC.getX(), yLocation);
		
		if (whileCC.isVisible())
			yLocation += 40;

		keuzeCC.setLocation(keuzeCC.getX(), yLocation);
		
		if (keuzeCC.isVisible())
			yLocation += 50;
		else
			yLocation += 10;
			
		for(int i=0 ; i<5 ; i++)
		{	
			deeltaakCC[i].setLocation(deeltaakCC[i].getX(),yLocation);
			yLocation +=30;
		}
	}

	/**
	 * remove a CC from the Vector of CC's in this JavaLogoSchuifVeld 
	 * @param cc CC to remove
	 */
	public void verwijder(CommandComponent cc)
	{	
		ccs.removeElement(cc);
		paint();
	}
	
	public void paint()
	{
		paintComponent(jlsvContext2d);
	}
	
	/**
	 * paint this JavaLogoSchuifVled
	 * @param g Context2d for drawing
	 */
	public void paintComponent(Context2d g)
	{	
		// white
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos,yPos,breedte,hoogte);
		// light blue for pile part on the left 
		g.setFillStyle(CssColor.make(205,230,255));
		g.fillRect(xPos+4,yPos+4,172,hoogte-8);

		// paint all CC's starting from the end of the Vector!!
		for (int cCnt = ccs.size() - 1; cCnt >= 0; cCnt--)
		{
			((CommandComponent) ccs.elementAt(cCnt)).paintComponent(g);
		}
	}
	
	/**
	 * check if Rectangle programmaPanel contains a  CommandContainer containing the 
	 * point (x,y), find the "deepest", see method findContainerAt 
	 * @param x x-coordinate for checking
	 * @param y y-coordinate for checking
	 * @return null or the "deepest" CommandContainerAt
	 */
	public CommandContainer getCommandContainerAt(int x, int y)
	{	
		if (!programmaPanel.contains(x, y))
		{	
			return null;
		}
		CommandContainer result = findCContainerAt(x,y);
		return result;
	}

	/**
	 * dragging CommandComponent sc was ended position (x,y)
	 * (by means of mouseReleased); find the CommandContainer at (x,y) (if any)
	 * and add sc to this CommandContainer; if no CommandContainer was found
	 * remove sc  
	 * @param sc CC that was released 
	 * @param x x-position of release
	 * @param y y-position of release
	 */
	public void losSchuiver(CommandComponent sc, int x, int y)
	{	
		// find potential new parent 
		CommandContainer cc = getCommandContainerAt(x,y);
		// new parent
		if (cc != null )
		{	
			// remove sc from the schuifveld (only effective if sc originated from a pile)
			if (sc.parent == null)
			{	verwijder(sc);
			}
			// not necessary, parent of sc was put to null after removing in mouseDragged			
			else
			{	sc.parent.remove(sc);
			}
			cc.addCComponent(sc);
			// set sc back to normal height  
			if (sc instanceof LoopCommandComponent)
			{	LoopCommandComponent lcc = (LoopCommandComponent) sc;
				lcc.loopBlock.setMinimumHeight(ccsh);
				lcc.loopBlock.reArrange();
			}
			if (sc instanceof KeuzeCommandComponent)
			{	KeuzeCommandComponent kcc = (KeuzeCommandComponent) sc;
				kcc.ifBlock.setMinimumHeight(ccsh);
				// elseBlock directly has correct minimum height
				kcc.ifBlock.reArrange();
				if (kcc.elseBlock != null)
					kcc.elseBlock.reArrange();
			}
			cc.removeCaret();
			paint();
		} 
		else // no new parent
		{	// remove sc from schuifveld (only effective if sc originated from a pile)
			if (sc.parent == null)
			{	verwijder(sc);
			}
			// not necessary, parent of sc was put to null after removing from parent sc in mouseDragged			
			else
			{	sc.parent.remove(sc);
			}
			paint();
		}
		paint();
	}
	
	/**
	 * CommandComponent CC will be dragged; remember position at dragg start,
	 * set the width to dragg width; put on top for drawing and re-arrange the
	 * CommandContainer CC used to belong to (CC was already removed from this 
	 * CommandContainer 
	 * @param sc CC to be dragged
	 */
	public void zetSchuiver(CommandComponent sc)
	{	int newLx = sc.getAbsoluteLocation().x;
		int newLy = sc.getAbsoluteLocation().y;
		sc.setBounds(newLx,newLy,sc.getDragWidth(),sc.getSize().height);
		putOnTop(sc);
		if(sc instanceof CommandComponent)
		{	CommandContainer cc = getCommandContainerAt(newLx,newLy);
			if (cc != null) 
				cc.reArrange();
		}
	}
	
	/**
	 * CommandComponent sc is being dragged and currently at position (ex,ey); find out if sc is 
	 * 1. hoovering over another CC in the program and display this CC's relevant caret 
	 * (so at mouseReleased sc will be inserted before or after this CC in this CC's CommandContainer) or
	 * 2. CC is hoovering over a CommandContainer and display this CommandContainer's caret 
	 * (so at mouseReleased sc will be added (at the bottom of) this CommandContainer);
	 * remove previous carets 
	 * if sc hoovers over a CC it automatically hoovers over the CCont containing the CC, however
	 * 1. if sc hoovers over a CCont it can hoover over a CC underneath the CCont, correct for this
	 * 2. in case CC is a loop or choice and CCont its command block, sc should be added to the
	 * command block, correct for this  
	 * @param sc CC being dragged
	 * @param ex current x-position of sc
	 * @param ey current x-position of sc
	 */
	void traceComponent(CommandComponent sc, int ex, int ey)
	{	
		if ( !sc.isTraceable() ) 
		{			
			return;
		}
		// find CC or CContainer in programmaPanel, so nothing on the left side nor the dragged CC itself will be found
		if ( !programmaPanel.contains(ex, ey))
		{	
			return;
		}
		// remove previous carets
		if (traceC != null)
			traceC.removeCaret();
		if (traceCC != null)
			traceCC.removeCaret();
		paint();
		
		// find CommandComponent traceC (if any)
		traceC = findCComponentAt(ex, ey, sc);
		// find CommandContainer traceCC (if any)
		traceCC = findCContainerAt(ex,ey);
		
		// here traceC is a loop/choice block and traceCC its command block 
		if ((traceC != null) && 
			(traceC.commandName.equals(WebLogoGWT.rb.herhaal1Tekst()) || 
			 traceC.commandName.equals(WebLogoGWT.rb.zolangTekst())) && 
			(traceCC!= null) && traceCC.containerName.equals("loop"))
			traceC = null;
		if ((traceC != null) && traceC.commandName.equals("Keuze") && 
			(traceCC!= null) && traceCC.containerName.equals("if"))
			traceC = null;
		if ((traceC != null) && traceC.commandName.equals("Keuze") && 
			(traceCC!= null) && traceCC.containerName.equals("else"))
			traceC = null;
				
//if (traceC != null)		
//System.out.println("traceC " + traceC.getCommandName());
//if (traceCC != null)		
//System.out.println("traceCC " + ((CommandContainer) traceCC).containerName);

 		int topParentCIndex = -1;
		if (traceC != null)
			topParentCIndex = findTopParentIndex(traceC);
//System.out.println("topPCI " + topParentCIndex);

		int topParentCCIndex = -1;
		if (traceCC != null)
		{	topParentCCIndex = findTopParentIndex(traceCC.parent);
		}	
//System.out.println("topPCCI " + topParentCCIndex);

		// traceC is under traceCC
		if (topParentCIndex > topParentCCIndex)
			traceC = null;

		// if c is a CommandComponent set Caret on that component
		if (traceC != null && traceC instanceof CommandComponent && traceC != sc)
		{
//System.out.println("c traced " + traceC.getCommandName() + " " + ey);			
			((CommandComponent) traceC).setCaret(ey);
		}
		// if c is a CommandContainer, then it must be over the empty space, so set caret
		// to top of the container if it is empty, bottom of last component otherwise
		else if (traceCC != null && traceCC instanceof CommandContainer) 
		{	
//System.out.println("cc traced " + ((CommandContainer) traceCC).containerName);			
			((CommandContainer) traceCC).setCaret(ey);
		}
	}
	

	/**
	 * open the PopupPanel for exporting code and fill it with the current code
	 * see class ExportPopup
	 * @param contents current code as a String
	 */
	void exportFrame(String contents) 
	{
		int popupX = xPos + getAbsoluteLeft();
		int popupY = yPos + getAbsoluteTop();
		// check if some other Popup is open
		if ((paramEditor != null) && paramEditor.isVisible())
		{
			paramEditor.owner.parameterEdited(paramEditor.getText());
			paramEditor.setVisible(false);
		}
		if ((importPopup != null) && importPopup.isVisible())
			importPopup.setVisible(false);

		exportPopup = new ExportPopup(220, 560, this);
		exportPopup.export(contents);
		exportPopup.setPopupPosition(popupX, popupY);
		exportPopup.show();
		paint();

	}

	/**
	 * reset: remobe all statements from main program and subroutines
	 */
	private void clearProgram()
	{
		programmaComponent.clearProgram();
		for ( int i=0; i<aantalDeeltaken; i++ )
		{
			deeltaakComponenten[i].clearProgram();
			deeltaakComponenten[i].setDeeltaakHeader("deeltaak"+(i+1), "");
		}
	}
	
	/**
	 * getter for body of subroutine i 
	 * @param i index of subroutine 
	 * @return body of subroutine i CC
	 */
	DeeltaakBodyComponent getDeeltaakBody(int i)
	{
		return deeltaakComponenten[i];
	}
	/**
	 * getter for the main program
	 * @return main program CC
	 */
	ProgrammaComponent getProgramma()
	{
		return programmaComponent;
	}
	
	/**
	 * turn the code in String s into an active program
	 * se class ProgrammaImporter
	 * @param s code String
	 */
	
	void importeer(String s)
	{
		clearProgram();
		paint();
		ProgrammaImporter pi = new ProgrammaImporter(this);
		pi.importProgramma(s);
		paint();
	}

	/**
	 * open the PopupPanel for importing code
	 * see class ImportPopup
	 */
	void importFrame() 
	{
		int popupX = xPos + getAbsoluteLeft();
		int popupY = yPos + getAbsoluteTop();
		// check if some other PopupPanel is open
		if ((paramEditor != null) && paramEditor.isVisible())
		{
			paramEditor.owner.parameterEdited(paramEditor.getText());
			paramEditor.setVisible(false);
		}
		if ((exportPopup != null) && exportPopup.isVisible())
			exportPopup.setVisible(false);

		importPopup = new ImportPopup(300, 560, this);
		importPopup.setPopupPosition(popupX, popupY);
		importPopup.show();
		paint();
	}

	/**
	 * get a String containing all code in ProgrammaComponent followed
	 * by all code in the deelTaakBodyComponents; used for code export; <br>
	 * note the specific format used
	 * @return code String
	 */
	public String getCode()
	{	String s0 = programmaComponent.getCode("");
		for(int i=0 ; i<aantalDeeltaken ; i++)
		{	s0 = s0 + deeltaakComponenten[i].getCode("");
		}
		return s0+"\n";
	}
	
	/**
	 * getter for gesloten
	 * @return vlaue of gesloten
	 */
	public boolean isGesloten()
	{	return gesloten;
	}

	/**
	 * setter for gesloten
	 * @param b value of gesloten
	 */
	public void zetGesloten(boolean b)
	{	gesloten = b;
	}
	
	/**
	 * set the visiblity of the subroutines (call and bodies)
	 * @param b true/false
	 */
	public void zetDeeltaken(boolean b)
	{	
		for (int i = 0; i <aantalDeeltaken; i++)
		{	deeltaakCC[i].setVisible(b);
			((DeeltaakCallCComponent)deeltaakCC[i]).getBody().setVisible(b);
		}
	}

	/**
	 * set the visiblity of the while command
	 * @param b true/false
	 */
	public void zetWhileLoopZichtbaar(boolean b)
	{	
		whileCC.setVisible(b);
		herschikStapel();
	}

	/**
	 * set the visiblity of the keuze command
	 * @param b true/false
	 */
	public void zetKeuzeCommandZichtbaar(boolean b)
	{	keuzeCC.setVisible(b);
		herschikStapel();
	}
	
	/**
	 * set the visibilty of the print commands
	 * @param b true/false
	 */
	public void zetPrintCommandsZichtbaar(boolean b)
	{	printCC.setVisible(b);
		printlCC.setVisible(b);
		herschikStapel();
	}

	/**
	 * set the visiblity of the drawing commands
	 * @param b true/false
	 */
	public void zetTekenCommandsZichtbaar(boolean b)
	{	vooruitCC.setVisible(b);
		stapCC.setVisible(b);
		linksCC.setVisible(b);
		rechtsCC.setVisible(b);
		penAanCC.setVisible(b);
		penUitCC.setVisible(b);
		vulAanCC.setVisible(b);
		vulUitCC.setVisible(b);
		vulbladCC.setVisible(b);
		herschikStapel();
	}
	
	/**
	 * given a CC, it must be part of some tree (or form a tree by itself)
	 * whose root is a CC contained in the Vector css of JavaSchuifVeld;
	 * find this root CC
	 * @param c CC whos root must be found
	 * @return root CC of c
	 */
	public int findTopParentIndex(CommandComponent c)
	{	CommandComponent topParent = c;
		while (topParent.parent != null)
		{	CommandContainer cParent = topParent.parent;
			topParent = cParent.parent;
		}
		return ccs.indexOf(topParent);
	}
	
	

	/**
	 * check if JavaLogoSchuifveld contains a CC that contains the coordinates (x,y); 
	 * note that if a CC contains (x,y) it can be a composite containing
	 * other CC's containing (x,y), so find the "deepest" CC in the tree below a CC satisfying the 
	 * criteria; <br>
	 * important: the CC's in JavaLogoSchuifVeld áre painted starting at the CC with the highest index
	 * in the Vector css; since things can overlap, we start searching at index 0 and quit whenever we find a CC
	 * satisfying the criteria, so we always find the top CC;  
	 * @param x x-coordinate for search
	 * @param y y-coordinate for search
	 * @return CC sough or null
	 */
	public CommandComponent findCComponentAt(int x, int y)
	{
		CommandComponent result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{	Object o = ccs.elementAt(cCnt);
			CommandComponent tResult = null;
			if (o instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) o;
				if (cc.contains(x, y))
				{	result = cc;
					tResult = cc.findCComponentAt(x, y, cc);
					if (tResult != null)
						result = tResult;
				}
			}
			if (result != null)
				break;
		}
		return result;
	}	

	/**
	 * check if JavaLogoSchuifveld contains a CC that is NOT equal to CC sc and that contains 
	 * the coordinates (x,y); note that if a CC contains (x,y) it can be a composite containing
	 * other CC's containing (x,y), so find the "deepest" CC in the tree below a CC satisfying the 
	 * criteria; <br>
	 * important: the CC's in JavaLogoSchuifVeld áre painted starting at the CC with the highest index
	 * in the Vector css; since things can overlap, we start searching at index 0 and quit whenever we find a CC
	 * satisfying the criteria, so we always find the top CC;  
	 * @param x x-coordinate for search
	 * @param y y-coordinate for search
	 * @param sc CC to be excluded
	 * @return CC sough or null
	 */
	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		CommandComponent result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{	Object o = ccs.elementAt(cCnt);
			CommandComponent tResult = null;
			if (o instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) o;
//System.out.println("findCCAt " + cc.commandName);			
				if ((cc != sc) && cc.contains(x,y))
				{	result = cc;
					tResult = cc.findCComponentAt(x, y, sc);
					if (tResult != null)
						result = tResult;
					// top geklikt of de lege CommandContainer van een Composite
					if ((tResult == null) && (cc instanceof ProgrammaComponent))
						result = null;	
				}
			}
			if (result != null)
				break;
		}
		return result;
	}	
	
	/**
	 * check if JavaLogoSchuifVeld contains a CC containing a CContainer that contains 
	 * the coordinates (x,y); note that such CC must be a ProgrammaComponent (or its subclass
	 * DeelTaakBodyComponent; 
	 * KeuzeCC and that the latter can contain two CContainers
	 * note that if a CC contains (x,y) it can be a composite containing
	 * another CContainer containing (x,y), so find the "deepest" CContainer in the tree
	 * important: the CC's in JavaLogoSchuifVeld áre painted starting at the CC with the highest index
	 * in the Vector css; since things can overlap, we start searching at index 0 and quit whenever we find a CC
	 * satisfying the criteria, so we always find the top CC;  
	 * @param x x-coordinate for search
	 * @param y y-coordinate for search
	 * @return CContainer sought or null
	 */
	public CommandContainer findCContainerAt(int x, int y)
	{
		CommandContainer result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{
			Object o = ccs.elementAt(cCnt);
			CommandContainer tResult = null;
			// also takes care of deeltaakBodyComponents
			if ((o instanceof ProgrammaComponent) && ((CommandComponent) o).contains(x, y))
			{	ProgrammaComponent pc = (ProgrammaComponent) o;
				if (pc.commandBlock.contains(x, y))
				{	result = pc.commandBlock;
					tResult = pc.commandBlock.findCContainerAt(x,y);
					if (tResult != null)
					{	result = tResult;
					}
				}	
			} 
			// quit at top CC if this complies
			if (result != null)
			{	break;
			}
		}
		return result;
	}


	/**
	 * the CommandComponent containing the x- and y-coordinates of a MouseDown/TouchStart Event  
	 */
	private CommandComponent mouseTargetComponent = null;
	/**
	 * find the CommandComponent containing the x- and y-coordinates of a MouseDown/TouchStart Event (if any)
	 * and pass on the event coordinates
	 * @param x x-coordinate of a MouseDown/TouchStart Event
	 * @param y y-coordinate of a MouseDown/TouchStart Event
	 * @param modifiers not used, maybe in the future
	 */
	public void mousePressed(int x, int y, int modifiers) 
	{
		CommandComponent c = this.findCComponentAt(x, y);
		if (c != null)
		{	mouseTargetComponent = c;
			mouseTargetComponent.mousePressed(x, y, modifiers);
		}	
	}
	/**
	 * pass on the x- and y-coordinates of a MouseUp/TouchEnd Event (if possible)
	 * @param x-coordinate of a MouseUp/TouchEnd Event
	 * @param y-coordinate of a MouseUp/TouchEnd Event
	 * @param modifiers modifiers not used, maybe in the future
	 */
	public void mouseReleased(int x, int y, int modifiers) 
	{	if ( mouseTargetComponent != null )
		{	mouseTargetComponent.mouseReleased(x, y, modifiers);
			mouseTargetComponent = null;
		}
	}
	/**
	 * pass on the x- and y-coordinates of a MouseMove/TouchMove Event (if possible)
	 * @param x-coordinate of a MouseMove/TouchMove Event
	 * @param y-coordinate of a MouseMove/TouchMove Event
	 * @param modifiers modifiers not used, maybe in the future
	 */
	public void mouseDragged(int x, int y, int modifiers) 
	{	if ( mouseTargetComponent != null )
		{	mouseTargetComponent.mouseDragged(x, y, modifiers);
		}
	}
	
	/**
	 * remembering mouseDown
	 */
	boolean mouseDown = false;
	/**
	 * remember x- and y-coordinates of the last TouchMove Event, since the the coordinates of the
	 * TouchEnd Event e are not stored in e.touches  
	 */
	int lastMoveX, lastMoveY;
	/**
	 * inner class for handling mouse Events
	 * @author huub
	 */
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		public void onMouseDown(MouseDownEvent e)
		{
			e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			mouseDown = true;
			int eventX = e.getX();
			int eventY = e.getY();
			mousePressed(eventX, eventY,0);
		}

		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			if (!mouseDown)
				return;
			int eventX = e.getX();
			int eventY = e.getY();
			mouseDragged(eventX, eventY,0);
		} // onMouseMove
		
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();
			mouseDown = false;
			int eventX = e.getX();
			int eventY = e.getY();
			mouseReleased(eventX, eventY,0);
		}
	} 

	/**
	 * inner class for handling touch Events 
	 * @author huub
	 */
	class TouchHandler implements TouchStartHandler, TouchMoveHandler, TouchEndHandler
	{
		public void onTouchStart(TouchStartEvent e)
		{
			e.preventDefault();
			e.stopPropagation();
			if (e.getTouches().length() > 0)
			{
				Touch touch = e.getTouches().get(0);
				int eventX = touch.getPageX() - jlsvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - jlsvCanvas.getAbsoluteTop();				
				lastMoveX = eventX;
				lastMoveY = eventY;
				mousePressed(eventX, eventY,0);
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
			    int eventX = touch.getPageX() - jlsvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - jlsvCanvas.getAbsoluteTop();				
				lastMoveX = eventX;
				lastMoveY = eventY;
				mouseDragged(eventX, eventY,0);
		    }
			e.preventDefault();
			e.stopPropagation();
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseReleased(lastMoveX, lastMoveY, 0);
		}
	}
	
}
