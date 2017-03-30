package fi.weblogo3dgwt.client;

import fi.weblogo3dgwt.client.logotekenap3d.StringUtils;



//import java.awt.*;
//import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import fi.weblogo3dgwt.client.logotekenap3d.TraceBeheerder;
import fi.weblogo3dgwt.client.logotekenap3d.Rectangle;

//import javax.swing.ImageIcon;
//import javax.swing.JPanel;
//import javax.swing.JLabel;
//import javax.swing.JButton;
//import javax.swing.JTextArea;

import fi.weblogo3dgwt.client.logotekenap3d.TekenApplet3D;

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


/**
 * @author PBgv, changes made
 * 	19/2/2015 changed commandComponents ffrom array to ArrayList, deleted int: aantalCC
 *
 */
public class JavaLogoSchuifVeld extends LayoutPanel //extends JPanel implements  MouseListener, MouseMotionListener
{
	/**
	 * Number of deeltaken
	 */
	public static final int aantalDeeltaken = 5;
	
	/**
	 * Maximum number of parameters in a deeltaak
	 */
	public static final int maxParamCount = 4;		// temp: must go to JavaLogoWeb

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
//GWT	
	public static int pph = 525;
	/**
	 * ProgrammaPanelX
	 */
	public static final int ppx = 190;
	/**
	 * ProgrammaPanelY
	 */
	public static final int ppy = 10;
		
	//private JPanel programmaPanel;
	private Rectangle programmaPanel;
	private ProgrammaComponent programmaComponent;
	
	private DeeltaakBodyComponent[] deeltaakComponenten;
	private TekenApplet3D uitvoerblad;
	
	//private VardisplayPanel vartracer = null;
	//private boolean isVartracing = false;
	private boolean gesloten;
	
	private CommandComponent vooruitCC;
	private CommandComponent stapCC;
	private CommandComponent linksCC;
	private CommandComponent rechtsCC;
	private CommandComponent stapzCC;
	private CommandComponent stap3dCC;
	private CommandComponent xDraaiCC;
	private CommandComponent yDraaiCC;
	private CommandComponent penAanCC;
	private CommandComponent penUitCC;
	private CommandComponent vulAanCC;
	private CommandComponent vulUitCC;
//	private CommandComponent vulbladCC;
//	private CommandComponent printCC;
//	private CommandComponent printlCC;
	private CommandComponent varCC;
	private CommandComponent herhaalCC;
	private CommandComponent whileCC;
	private CommandComponent keuzeCC;
	private CommandComponent[] deeltaakCC;
	
	//private HashMap<String, Double> inputVars = new HashMap<String, Double>();
	private HashMap<String, Object> inputVars = new HashMap<String, Object>();
	
	int xPos, yPos, breedte, hoogte;
	Canvas jlsvCanvas;
	Context2d jlsvContext2d;
	
	Vector ccs = new Vector();
	
	ParameterTextField paramEditor;
	
	ExportPopup exportPopup;
	ImportPopup importPopup;
	
String[] messages = new String[10];	
int messageCnt = 0;
		
	public JavaLogoSchuifVeld(int x, int y, int b, int h, TekenApplet3D tb)
	{	
		
for (int i = 0; i < messages.length; i++)
	messages[i] = "message " + i; 
		//setLayout(null);
		//setBounds(x,y,b,h);
		xPos = x; yPos = y; breedte = b; hoogte = h;
	
		//addMouseListener(this);
		//addMouseMotionListener(this);
		uitvoerblad = tb;
		
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
	
	public void setMessage(String s)
	{
		messages[messageCnt] = s;
		messageCnt++;
		if (messageCnt == messages.length)
			messageCnt = 0;
		paint();
	}
	
	public Canvas getCanvas()
	{
		return jlsvCanvas;
	}
	
	public void initContext2d() 
	{
		jlsvContext2d = jlsvCanvas.getContext2d();
		
	}

	public Dimension getSize()
	{
		return new Dimension(breedte,hoogte);
	}
	
	public int getHeight()
	{
		return hoogte;
	}

	public int getWidth()
	{
		return breedte;
	}

	public void setInputVar(String name, double value)
	{	inputVars.put("input"+name, new Double(value));
	}
	
	public void setInputVars(HashMap<String, Object> inputVars)
	{	this.inputVars = inputVars;
	}
	
	public HashMap<String, Object> getInputVars()
	{	return inputVars;
	}
	
	public void execute(TraceBeheerder trb, TekenApplet3D ub)
	{	
		VarSet varSet = new VarSet();
		Iterator iter = (inputVars.keySet()).iterator();
		while (iter.hasNext()) {
			String key = (String)iter.next();
			double value = ((Double) inputVars.get(key)).doubleValue();
			varSet.setParameter(key, value);
		}
		programmaComponent.execute(trb,ub, varSet);
	}
	
	public void initialize()
	{	
		// programmaPanel bevat de 'programma's' waar we componenten op kunnen droppen
		//programmaPanel = new JPanel();
		//programmaPanel.setBounds(ppx, ppy, ppw, pph);
		pph = hoogte - 20;
		programmaPanel = new Rectangle(ppx, ppy, ppw, pph);
		
		//programmaPanel.setBackground(Color.WHITE);
		//programmaPanel.setLayout(null);
		//add(programmaPanel,0);
		
		programmaComponent = new ProgrammaComponent(ppx, ppy, ProgrammaComponent.pcsw, pph, 
													WebLogo3dGWT.rb.tekenalgorithmeTekst(), this);
		programmaComponent.zetVast(true);
		
		//programmaPanel.add(programmaComponent);
		ccs.addElement(programmaComponent);
				
		
		//vartracer = new VardisplayPanel(2*ccsw+10, 515);
		//add(vartracer);
		//setWidgetLeftWidth(vartracer, ccx, Style.Unit.PX, 2*ccsw+10, Style.Unit.PX);
		//setWidgetTopHeight(vartracer, ccy, Style.Unit.PX, 515, Style.Unit.PX);
		//vartracer.setBounds(ccx, ccy, 2*ccsw+10, 515);
		
		// turtle in xy-vlak
		int rijNum = 1;
		// links
		vooruitCC = new VooruitCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(vooruitCC,0);
		ccs.addElement(vooruitCC);
		// rechts
		stapCC = new StapCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(stapCC,0);
		ccs.addElement(stapCC);
		
		rijNum = 2;
		// links		
		linksCC = new LinksCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(linksCC,0);
		ccs.addElement(linksCC);
		// rechts
		rechtsCC = new RechtsCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(rechtsCC,0);
		ccs.addElement(rechtsCC);
		
		// 3d-drawing
		rijNum = 3;
		// links
		stapzCC = new StapZCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(stapzCC,0);
		ccs.addElement(stapzCC);
		// rechts
		stap3dCC = new Stap3DCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(stap3dCC,0);
		ccs.addElement(stap3dCC);
		
		rijNum = 4; 
		// links
		xDraaiCC = new XDraaiCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(xDraaiCC,0);
		ccs.addElement(xDraaiCC);
		// rechts
		yDraaiCC = new YDraaiCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(yDraaiCC,0);
		ccs.addElement(yDraaiCC);
		
		// tekenen
		rijNum = 5;
		// links
		penAanCC = new PenAanCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(penAanCC,0);
		ccs.addElement(penAanCC);
		// rechts	
		penUitCC = new PenUitCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(penUitCC,0);
		ccs.addElement(penUitCC);
		
		rijNum = 6;
		// links	
		vulAanCC = new VulAanCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(vulAanCC,0);
		ccs.addElement(vulAanCC);
		//rechts	
		vulUitCC = new VulUitCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		//add(vulUitCC,0);
		ccs.addElement(vulUitCC);
		
		//vulbladCC = new VulBladCComponent(ccx,ccy+120,ccsw,ccsh, this);
		//add(vulbladCC,0);
		//ccs.addElement(vulbladCC);
			
		//printCC = new PrintCComponent(ccx,ccy+150,ccsw,ccsh, this);
		//add(printCC,0);
		//ccs.addElement(printCC);
			
		//printlCC = new PrintlCComponent(ccx2,ccy+150,ccsw,ccsh, this);
		//add(printlCC,0);
		//ccs.addElement(printlCC);
		
		varCC = new VarCComponent(ccx,ccy+190,cclw,ccsh, this);
		//add(varCC,0);
		ccs.addElement(varCC);
	
		herhaalCC = new ForLoopCommandComponent(ccx,ccy+230,cclw,ccsh+10, this);
		//add(herhaalCC,0);
		ccs.addElement(herhaalCC);
		
		whileCC = new WhileLoopCommandComponent(ccx,ccy+270,cclw,ccsh+10, this);
		//add(whileCC,0);
		ccs.addElement(whileCC);
		
		keuzeCC = new KeuzeCommandComponent(ccx,ccy+310,cclw,ccsh+10, this);
		//add(keuzeCC,0);
		ccs.addElement(keuzeCC);
        
	
		deeltaakComponenten = new DeeltaakBodyComponent[aantalDeeltaken];
		deeltaakCC = new DeeltaakCallCComponent[aantalDeeltaken];
		for(int i=0; i < aantalDeeltaken; i++)
		{
			deeltaakCC[i] = new DeeltaakCallCComponent(xPos+ccx,yPos+ccy+360+30*i,cclw,ccsh, i+1, this);
			//add(deeltaakCC[i],0);
			ccs.addElement(deeltaakCC[i]);

			// create with dummy location and height
			
			deeltaakComponenten[i] = new DeeltaakBodyComponent(0,0,ProgrammaComponent.pcsw,ProgrammaComponent.pcclosedh, 
															   WebLogo3dGWT.rb.deeltaakTekst()+(i+1), this);
			deeltaakComponenten[i].zetVast(false);
			((DeeltaakCallCComponent)deeltaakCC[i]).setBody(deeltaakComponenten[i]);
			//programmaPanel.add(deeltaakComponenten[i]);
			ccs.addElement(deeltaakComponenten[i]);
		}
		// set location and height right, one by one...

		
		deeltaakComponenten[0].setLocation(xPos+ppx+ProgrammaComponent.pcsw+40, yPos+ccy+0);
		deeltaakComponenten[0].changeHeight();			// was initialialized as closed, so this will open it.
		deeltaakComponenten[1].setLocation(xPos+ppx+ProgrammaComponent.pcsw+10, yPos+ccy+180);
		deeltaakComponenten[1].changeHeight();
		deeltaakComponenten[2].setLocation(xPos+ppx+ProgrammaComponent.pcsw+20, yPos+ccy+400);
		deeltaakComponenten[3].setLocation(xPos+ppx+ProgrammaComponent.pcsw+30, yPos+ccy+415);
		deeltaakComponenten[4].setLocation(xPos+ppx+ProgrammaComponent.pcsw+40, yPos+ccy+430);
		
		
//einde GWT		
	}
	
	
	void addToProgrammaPanel(CommandComponent c)
	{
		//programmaPanel.add(c, 0);
		ccs.addElement(c);
	}
	
	
	public void putOnTop(CommandComponent cc)
	{
		ccs.removeElement(cc);
		ccs.insertElementAt(cc,0);
	}
	
	public void zetStapel(CommandComponent cc)
	{	int x = cc.getLocation().x;
		int y = cc.getLocation().y;
		int b = cc.getSize().width;
		int h = cc.getSize().height;
		
		CommandComponent currentCC;
//		if(cc == printCC)
//		{ 	printCC = new PrintCComponent(x,y,b,h, this);
//			ccs.addElement(printCC);
//		}
		
//		if(cc == vulbladCC)
//		{ 	vulbladCC = new VulBladCComponent(x,y,b,h, this);
//			ccs.addElement(vulbladCC);
//		}
		
//		if(cc == printlCC)
//		{ 	printlCC = new PrintlCComponent(x,y,b,h, this);
//			ccs.addElement(printlCC);
//		}

		if(cc == penAanCC)
		{ 	penAanCC = new PenAanCComponent(x,y,b,h, this);
			//add(penAanCC,0);
			ccs.addElement(penAanCC);
		}
		if(cc == penUitCC)
		{ 	penUitCC = new PenUitCComponent(x,y,b,h, this);
			//add(penUitCC,0);
			ccs.addElement(penUitCC);
		}		
		if(cc == vooruitCC)
		{ 	vooruitCC = new VooruitCComponent(x,y,b,h, this);
			//add(vooruitCC,0);
			ccs.addElement(vooruitCC);
		}
		if(cc == linksCC)
		{ 	linksCC = new LinksCComponent(x,y,b,h, this);
			//add(linksCC,0);
			ccs.addElement(linksCC);
		}
		if(cc == rechtsCC)
		{ 	rechtsCC = new RechtsCComponent(x,y,b,h, this);
			//add(rechtsCC,0);
			ccs.addElement(rechtsCC);
		}
		if(cc == stapCC)
		{ 	stapCC = new StapCComponent(x,y,b,h, this);
			//add(stapCC,0);
			ccs.addElement(stapCC);
		}

		if(cc == stapzCC)
		{ 	stapzCC = new StapZCComponent(x,y,b,h, this);
			//add(stapzCC,0);
			ccs.addElement(stapzCC);
		}
		if(cc == stap3dCC)
		{ 	stap3dCC = new Stap3DCComponent(x,y,b,h, this);
			//add(stap3dCC,0);
			ccs.addElement(stap3dCC);
		}
		if(cc == xDraaiCC)
		{ 	xDraaiCC = new XDraaiCComponent(x,y,b,h, this);
			//add(xDraaiCC,0);
			ccs.addElement(xDraaiCC);
		}
		if(cc == yDraaiCC)
		{ 	yDraaiCC = new YDraaiCComponent(x,y,b,h, this);
			//add(yDraaiCC,0);
			ccs.addElement(yDraaiCC);
		}
		
		
		if(cc == vulAanCC)
		{ 	vulAanCC = new VulAanCComponent(x,y,b,h, this);
			ccs.addElement(vulAanCC);
		}
		if(cc == vulUitCC)
		{ 	vulUitCC = new VulUitCComponent(x,y,b,h, this);
			ccs.addElement(vulUitCC);
		}
		
		if(cc == herhaalCC)
		{ 	herhaalCC = new ForLoopCommandComponent(x,y,b,h, this);
			//	add(herhaalCC,0);
			ccs.addElement(herhaalCC);
			cc.setSize(cc.getWidth(), cclh);
		}
		
		if(cc == whileCC)
		{ 	whileCC = new WhileLoopCommandComponent(x,y,b,h, this);
			//	add(whileCC,0);
			ccs.addElement(whileCC);
			cc.setSize(cc.getWidth(), cclh);
		}
	
		if(cc == keuzeCC)
        {   keuzeCC = new KeuzeCommandComponent(x,y,b,h, this);
			//	add(keuzeCC,0);
        	ccs.addElement(keuzeCC);
        	cc.setSize(cc.getWidth(), cclh);
        }
		
		if(cc == varCC)
		{ 	varCC = new VarCComponent(x,y,b,h, this);
			//	add(varCC,0);
			ccs.addElement(varCC);
		}
		
		for(int i=0; i<aantalDeeltaken; i++)
		{	if(cc == deeltaakCC[i])
			{ 	deeltaakCC[i] = new DeeltaakCallCComponent((DeeltaakCallCComponent) cc, this);
				//add(deeltaakCC[i],0);
				ccs.addElement(deeltaakCC[i]);
			}
		}
	}
	
	private void herschikStapel()
	{	int yLocation = ccy;
		
		if (vooruitCC.isVisible())
			yLocation += 190;
		
//		printCC.setLocation(printCC.getX(), yLocation);
//		printlCC.setLocation(printlCC.getX(), yLocation);
		
//		if (printCC.isVisible())
//			yLocation += 40;
//		else
//			yLocation += 10;
		
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
		{	//deeltaakCC[i].setLocation(ccx,yLocation);
			deeltaakCC[i].setLocation(deeltaakCC[i].getX(),yLocation);
			yLocation +=30;
		}
	}

	public void verwijder(CommandComponent cc)
	{	
		//remove(cc);
		ccs.removeElement(cc);
		paint();
	}
	
	public void paint()
	{
//System.out.println("JLSV paint");		
		paintComponent(jlsvContext2d);
//System.out.println("JLSV painted");		
	}
	
	//public void paintComponent(Graphics g)
	public void paintComponent(Context2d g)
	{	//Dimension dd = getSize();
		//g.setColor(getBackground());
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos,yPos,breedte,hoogte);
		//g.setColor(new Color(205,230,255));
		g.setFillStyle(CssColor.make(205,230,255));
		g.fillRect(xPos+4,yPos+4,172,hoogte-8);

//System.out.println("ccs = " + ccs.size());		
		
		//for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		for (int cCnt = ccs.size() - 1; cCnt >= 0; cCnt--)
		{
			((CommandComponent) ccs.elementAt(cCnt)).paintComponent(g);
		}

//		for (int pCnt = 0; pCnt < pps.size(); pCnt++)
//		{
//			((CompositeCommandComponent) pps.elementAt(pCnt)).paintComponent(g);
//		}

//		g.setFillStyle(CssColor.make(0,0,0));
//		int yMess = 300;
//		for (int i = 0; i < messages.length; i++)
//		{	g.fillText(messages[i],programmaComponent.xPos + 5, yMess);
//			yMess += 20;
//		}
			
		
	}
	
// zoek meteen de CommandContainer, maar wel de diepste, dus recursief	
	
	public CommandContainer getCommandContainerAt(int x, int y)
	{	
		if (!programmaPanel.contains(x, y))
		{	
//System.out.println("outside pP");

			return null;
		
		}
	
		CommandContainer result = findCContainerAt(x,y);
		return result;
		

 
/*		
		Object cc = getContainerAt(x,y);
		if (cc == null)
			return null;
		Object tcc = ((CommandContainer) cc).getContainerAt(x,y);;
		while (tcc != null)
		{	cc = tcc;
			tcc = ((CommandContainer) tcc).getContainerAt(x,y);;
		}
		
		return cc;
		
		// find the deepest component in programmaPanel
		Component c = programmaPanel.findComponentAt(x-ppx,y-ppy);
		// if c is a CommandComponent, move to the CommandContainer that holds this object
		if ( c instanceof CommandComponent)
		{
			c = c.getParent();
		}
		if( c instanceof CommandContainer) 
		{	
			CommandContainer cc =  (CommandContainer)c;
			// don't add to the Commands that are in the piles, for pickup of new ones
			//if ( cc.getOwner().isStapel ) not needed anymore (find in programmaPanel)
			//{
			//	return null;
			//}
			return cc;
		}
*/		
	}
	
	public Object getContainerAt(int x, int y)
	{
		Object result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{
			Object o = ccs.elementAt(cCnt);
			if ((o instanceof CommandContainer) &&
				((CommandContainer) o).contains(x, y))
				result = o;
				
		}
		return result;
	}

	public void losSchuiver(CommandComponent sc, int x, int y)
	{	
//System.out.println("losSchuiver " + x + " " + y);		
		CommandContainer cc = getCommandContainerAt(x,y);
		
		// nieuwe parent
		if (cc != null )
		{	
//System.out.println("cc " + cc.containerName);			
			// schuifveld
			if (sc.parent == null)
			{	verwijder(sc);
//System.out.println("sc.parent == null");			
			}
// dit is niet nodig, parent wordt "genulld" in mouseDragged			
			else
			{	sc.parent.remove(sc);
			}
			cc.addCComponent(sc);
			if (sc instanceof LoopCommandComponent)
			{	LoopCommandComponent lcc = (LoopCommandComponent) sc;
				lcc.loopBlock.setMinimumHeight(ccsh);
				lcc.loopBlock.reArrange();
			}
			if (sc instanceof KeuzeCommandComponent)
			{	KeuzeCommandComponent kcc = (KeuzeCommandComponent) sc;
				kcc.ifBlock.setMinimumHeight(ccsh);
				// elseBlock heeft meteen de goede minimumHeight
				kcc.ifBlock.reArrange();
				if (kcc.elseBlock != null)
					kcc.elseBlock.reArrange();
			}
			
			paint();
		} 
		else // geen nieuwe parent
		{	
//System.out.println("cc == null");
			// schuifveld
			if (sc.parent == null)
			{	verwijder(sc);
//System.out.println("sc.par == null");
			}
// dit is niet nodig, parent wordt "genulld" in mouseDragged			
			else
			{	sc.parent.remove(sc);
			}
			paint();
		}
		
		
		paint();
	}
	
	public void zetSchuiver(CommandComponent sc)
	{	int newLx = sc.getAbsoluteLocation().x;
		int newLy = sc.getAbsoluteLocation().y;
		sc.setBounds(newLx,newLy,sc.getDragWidth(),sc.getSize().height);

		//setComponentZOrder(sc, 0);
		putOnTop(sc);
		if(sc instanceof CommandComponent)
		{
			CommandContainer cc = getCommandContainerAt(newLx,newLy);
			if (cc != null) 
				cc.reArrange();
		}
		
	}
	
	void traceComponent(CommandComponent sc, int ex, int ey)
	{	
		if ( !sc.isTraceable() ) 
		{	
//System.out.println("traceCC not traceble");			
			return;
		}
		
		
		if ( !programmaPanel.contains(ex, ey))
		{	
//System.out.println("traceCC outside pp " + ex + " " + ey);			
			return;
		
		}
		// find component in programmaPanel, so nothing on the left side nor the dragged CC itself will be found
		
//System.out.println("traceCC " + ex + " " + ey);		
		
		
		//CommandComponent c = findCComponentAt(ex-ppx,ey-ppy);
		CommandComponent c = findCComponentAt(ex, ey, sc);
		CommandContainer cc = findCContainerAt(ex,ey);
		
		if ((c != null) && c.commandName.equals("Herhaal") && 
			(cc!= null) && cc.containerName.equals("loop"))
			c = null;
		if ((c != null) && c.commandName.equals("Keuze") && 
				(cc!= null) && cc.containerName.equals("if"))
				c = null;
		if ((c != null) && c.commandName.equals("Keuze") && 
				(cc!= null) && cc.containerName.equals("else"))
				c = null;
				
		// if c is a CommandComponent set Caret on that component
		if (c != null && c instanceof CommandComponent && c != sc)
		{
//System.out.println("c traced " + c.getCommandName());			
			((CommandComponent)c).setCaret(ey);
//System.out.println("c");			
		}
		
		//CommandContainer cc = findCContainerAt(ex-ppx,ey-ppy);
		// naar boven
		//CommandContainer cc = findCContainerAt(ex,ey);
		// if c is a CommandContainer, then it must be over the empty space, so set caret
		// to top of the container if it is empty, bottom of last component otherwise
		else if (cc != null && cc instanceof CommandContainer) 
		{	
//System.out.println("cc traced " + ((CommandContainer)cc).containerName);			
			
			((CommandContainer)cc).setCaret(ey);
//System.out.println("cc traced");			
		}
	}
	
	
	void exportFrame(String contents) 
	{
		
		int popupX = xPos + getAbsoluteLeft();
		
		int popupY = yPos + getAbsoluteTop();
		
		
		// kijk of er ergens nog een popup open is
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
		//paramEditor.textBox.setFocus(true);
		
		paint();

//GWT
/*		
		final TextArea area = new TextArea(contents, 0, 0, TextArea.SCROLLBARS_NONE);
		Frame f = new Frame("Code van het algoritme");
		f.setLayout(new BorderLayout());
		f.add(area,BorderLayout.CENTER);
		f.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				area.requestFocus();
				area.setCaretPosition(0);
			}
			public void windowActivated(WindowEvent e) {
				area.selectAll();
			}
			public void windowClosing(WindowEvent e) {
					e.getWindow().dispose();
			} });
		f.pack();
		f.setVisible(true);
		f.toFront();
*/
//einde GWT		
	}
	
	private void clearProgram()
	{
		programmaComponent.clearProgram();
		
		
		for ( int i=0; i<aantalDeeltaken; i++ )
		{
			deeltaakComponenten[i].clearProgram();
			deeltaakComponenten[i].setDeeltaakHeader("deeltaak"+(i+1), "");
		}
	}
	
	
	DeeltaakBodyComponent getDeeltaakBody(int i)
	{
		return deeltaakComponenten[i];
	}
	
	ProgrammaComponent getProgramma()
	{
		return programmaComponent;
	}
	

	
	void importeer(String s)
	{
		
//System.out.println("importeer");

//System.out.println("code = " + s);
		clearProgram();
		paint();
		ProgrammaImporter pi = new ProgrammaImporter(this);
		pi.importProgramma(s);
		
		paint();
	}


	void importFrame() 
	{
		
		int popupX = xPos + getAbsoluteLeft();
		
		int popupY = yPos + getAbsoluteTop();
		
		
		// kijk of er ergens nog een popup open is
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
		//paramEditor.textBox.setFocus(true);
		
		paint();

/*	
		try
		{
			ImporterFrame imf = new ImporterFrame("Importeer code", this);
			imf.pack();
			imf.setVisible(true);
			imf.toFront();
		} 
		catch ( Exception e )
		{ 
			System.out.println("Mis!  "+e.getMessage());
		}
*/		
	}

	public String getCode()
	{	String s0 = programmaComponent.getCode("");
		for(int i=0 ; i<aantalDeeltaken ; i++)
		{
			
			s0 = s0 + deeltaakComponenten[i].getCode("");
		}
		return s0+"\n";
	}
	
/*	
	public void setVartracing(boolean vt)
	{
		if ( vt )
		{
//GWT			
			//add(vartracer, 0);
			isVartracing = true;
		} 
		else
		{
			isVartracing = false;
//GWT			
			//this.remove(vartracer);
			//vartracer.setContent("");
		}
		paint();
	}
*/	
	public boolean isGesloten()
	{	return gesloten;
	}
	
	public void zetGesloten(boolean b)
	{	gesloten = b;
	}
	
	public void zetDeeltaken(boolean b)
	{	
		for (int i = 0; i <aantalDeeltaken; i++)
		{	deeltaakCC[i].setVisible(b);
			((DeeltaakCallCComponent)deeltaakCC[i]).getBody().setVisible(b);
		}
	}
	
	public void zetWhileLoopZichtbaar(boolean b)
	{	
		whileCC.setVisible(b);
		herschikStapel();
	}
	
	public void zetKeuzeCommandZichtbaar(boolean b)
	{
		keuzeCC.setVisible(b);
		herschikStapel();
	}
	
	public void zetPrintCommandsZichtbaar(boolean b)
	{	//printCC.setVisible(b);
		//printlCC.setVisible(b);
		
//System.out.println("printCC " + printCC.isVisible());
//System.out.println("printlCC " + printlCC.isVisible());
		herschikStapel();
	}
	
	public void zetTekenCommandsZichtbaar(boolean b)
	{	vooruitCC.setVisible(b);
		stapCC.setVisible(b);
		linksCC.setVisible(b);
		rechtsCC.setVisible(b);
		stapzCC.setVisible(b);
		stap3dCC.setVisible(b);
		xDraaiCC.setVisible(b);
		yDraaiCC.setVisible(b);
		penAanCC.setVisible(b);
		penUitCC.setVisible(b);
		vulAanCC.setVisible(b);
		vulUitCC.setVisible(b);
		//vulbladCC.setVisible(b);
		herschikStapel();
	}
	
	/**
	 * Reapint this component and update the component holding the trace of the variables.
	 * This method will be called from the execute-methods in the CC's, when trace is on.
	 * 
	 * @param varset	the current set of variables in tracing mode
	 */
/*	
	void updateView(VarSet varset)
	{
		if ( isVartracing )
		{
			vartracer.setContent(varset.toString());
		}
		paint();
	}
*/	
//GWT niet nodig?	
/*	
	public void setSize(int b, int h)
	{	
		if ((getSize().width == b) && (getSize().height == h))
			return;
		programmaPanel.setSize(b-ppx, h);
		programmaComponent.setSize(programmaComponent.getWidth(), h-20);
		super.setSize(b, h);
	
	}
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

	public CommandComponent findCComponentAt(int x, int y, CommandComponent sc)
	{
		CommandComponent result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{	Object o = ccs.elementAt(cCnt);
			CommandComponent tResult = null;
			if (o instanceof CommandComponent)
			{	CommandComponent cc = (CommandComponent) o;
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
		}
		return result;
		
	}	
	
	public CommandContainer findCContainerAt(int x, int y)
	{
		CommandContainer result = null;
		for (int cCnt = 0; cCnt < ccs.size(); cCnt++)
		{
			Object o = ccs.elementAt(cCnt);
			CommandContainer tResult = null;
// dit kan niet 			
/*			
			if ((o instanceof LoopCommandComponent) && ((CommandComponent) o).contains(x, y))
			{	LoopCommandComponent lcc = (LoopCommandComponent) o;
				tResult = lcc.loopBlock.findCContainerAt(x,y);
				if (tResult != null)
					result = tResult;
			}
*/			
			// dit doet ook de deeltaakBodyComponenten
			if ((o instanceof ProgrammaComponent) && ((CommandComponent) o).contains(x, y))
			{	ProgrammaComponent pc = (ProgrammaComponent) o;
				if (pc.commandBlock.contains(x, y))
				{	result = pc.commandBlock;
					tResult = pc.commandBlock.findCContainerAt(x,y);
					if (tResult != null)
						result = tResult;
				}	
			} 
			// stop bij de bovenste CCContainer
			if (result != null)
				break;
	
			
		}
		return result;
		
	}


	/* PBgv: Fix voor het probleem van het verlies van de MouseListeners in Java8.
	 * Outline:
	 * Listeners move to main Panel (this). At mousePressed the CC that's being clicked is locate, 
	 * and remembered. The event is passed on, just as the ensueing drag and release events. 
	 * Outline phase 2:
	 * For scrolling, we want the ProgrammaComponents to implement MouseWheelListener.
	 * Unfortunately, when you implement this interface, ALL mouseEvents will be passed to the
	 * ProgrammaComponent. So PC needs to implement MouseListener and MouseMotionListener. 
	 * But then, these events have wrong x,y: local to the ProgrammaComponent.
	 * This is solved by having methods like mousePressed(x, y, modifiers) which do the real work.
	 * The methods from the interfaces will compute the right (x,y) from the event and the CC's
	 * absolute position and call the 'work-methods'. Pfff, Bloody hell...
	 * Note: modifiers are as yet unused. Maybe in the future: shift-click to select & drag >1 CC!
	 */
	
	private CommandComponent mouseTargetComponent = null;
	
	public void mousePressed(int x, int y, int modifiers) 
	{
		CommandComponent c = this.findCComponentAt(x, y);
		//if ( c instanceof CommandComponent )
		//{
		
			if (c != null)
			{	
//System.out.println("jlsv mousePressed " + c.commandName);				
				mouseTargetComponent = c;
				mouseTargetComponent.mousePressed(x, y, modifiers);
			}	
		//}
		
		
		//else 
		//{
//GWT			
			//requestFocus();		// end possible editing of parameters, see ParameterTextField for details	
			//if ( c instanceof CommandContainer)
			//{	
				//Component c2 = c.getParent();
				//if ( c2 instanceof CompositeCommandComponent )
				//{
				//	mouseTargetComponent = (CommandComponent)c2;
				//	mouseTargetComponent.mousePressed(x, y, modifiers);
				//}
			//}
		//}
	}
	
	public void mouseReleased(int x, int y, int modifiers) 
	{
		if ( mouseTargetComponent != null )
		{
			mouseTargetComponent.mouseReleased(x, y, modifiers);
			mouseTargetComponent = null;
		}
	}
	
	public void mouseDragged(int x, int y, int modifiers) 
	{
		if ( mouseTargetComponent != null )
		{
			mouseTargetComponent.mouseDragged(x, y, modifiers);
		}
	}
	
	/*
	 * The methods form the mouse(Motion)Listener interfaces
	 */
	
	boolean mouseDown = false;
	int lastMoveX, lastMoveY;
	
	class MouseHandler implements MouseDownHandler, MouseMoveHandler, MouseUpHandler
	{
		
		//public void mousePressed(MouseEvent e)
		public void onMouseDown(MouseDownEvent e)
		{
			
//System.out.println("mouseDown");

			e.preventDefault();
			// prevent scrolling 
			e.stopPropagation();
			
			mouseDown = true;
			
			int eventX = e.getX();
			int eventY = e.getY();
			
			//mouseDownTouchStartAction(eventX, eventY);
			mousePressed(eventX, eventY,0);
			
		}
		
		//public void mouseDragged(MouseEvent e)
		public void onMouseMove(MouseMoveEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
//System.out.println("mouseMov");			
			
			if (!mouseDown)
				return;

			int eventX = e.getX();
			int eventY = e.getY();
			
			//mouseMoveTouchMoveAction(eventX, eventY);
			mouseDragged(eventX, eventY,0);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			// prevent scrolling
			e.stopPropagation();

//System.out.println("mouseUp");

			mouseDown = false;

			int eventX = e.getX();
			int eventY = e.getY();

			//mouseUpTouchEndAction(lastMoveX, lastMoveY);
			mouseReleased(eventX, eventY,0);

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
				
				int eventX = touch.getPageX() - jlsvCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - jlsvCanvas.getAbsoluteTop();				
				
				lastMoveX = eventX;
				lastMoveY = eventY;

				//mouseDownTouchStartAction(eventX, eventY);
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
				
				//mouseMoveTouchMoveAction(eventX, eventY);
				mouseDragged(eventX, eventY,0);
				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
//GWT check het TouchEndEvent
			
			//mouseUpTouchEndAction(lastMoveX, lastMoveY);
			mouseReleased(lastMoveX, lastMoveY, 0);
		}

	}
	
/*	
	@Override
	public void mousePressed(MouseEvent e) 
	{
		mousePressed(e.getX(), e.getY(), e.getModifiersEx());
	}
*/
/*	
	@Override
	public void mouseReleased(MouseEvent e) 
	{
		mouseReleased(e.getX(), e.getY(), e.getModifiersEx());
	}
*/
/*	
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		mouseDragged(e.getX(), e.getY(), e.getModifiersEx());
	}
*/	
//	@Override
//	public void mouseMoved(MouseEvent e) 
//	{
		// unused		
//	}
	
//	@Override
//	public void mouseClicked(MouseEvent e) 
//	{
		// unused		
//	}
	
//	@Override
//	public void mouseEntered(MouseEvent e) 
//	{
		// unused
//	}
	
//	@Override
//	public void mouseExited(MouseEvent e) 
//	{
		// unused
//	}

}
