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
 * see class JavaLogoSchuifVeld in WebLogoGWT
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
	public static int pph = 525;
	/**
	 * ProgrammaPanelX
	 */
	public static final int ppx = 190;
	/**
	 * ProgrammaPanelY
	 */
	public static final int ppy = 10;
		
	private Rectangle programmaPanel;
	private ProgrammaComponent programmaComponent;
	
	private DeeltaakBodyComponent[] deeltaakComponenten;
	
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
// not implemented for lack of space
//	private CommandComponent vulbladCC;
//	private CommandComponent printCC;
//	private CommandComponent printlCC;
	private CommandComponent varCC;
	private CommandComponent herhaalCC;
	private CommandComponent whileCC;
	private CommandComponent keuzeCC;
	private CommandComponent[] deeltaakCC;
	
	private HashMap<String, Object> inputVars = new HashMap<String, Object>();
	
	int xPos, yPos, breedte, hoogte;
	Canvas jlsvCanvas;
	Context2d jlsvContext2d;
	
	Vector ccs = new Vector();
	
	ParameterTextField paramEditor;
	
	ExportPopup exportPopup;
	ImportPopup importPopup;
	
	CommandComponent traceC;
	CommandContainer traceCC;
	
		
	public JavaLogoSchuifVeld(int x, int y, int b, int h, TekenApplet3D tb)
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
		pph = hoogte - 20;
		programmaPanel = new Rectangle(ppx, ppy, ppw, pph);
		
		programmaComponent = new ProgrammaComponent(ppx, ppy, ProgrammaComponent.pcsw, pph, 
													WebLogo3dGWT.rb.tekenalgorithmeTekst(), this);
		programmaComponent.zetVast(true);
		
		ccs.addElement(programmaComponent);
				
		// turtle in xy-vlak
		int rijNum = 1;
		// links
		vooruitCC = new VooruitCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(vooruitCC);
		// rechts
		stapCC = new StapCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(stapCC);
		
		rijNum = 2;
		// links		
		linksCC = new LinksCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(linksCC);
		// rechts
		rechtsCC = new RechtsCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(rechtsCC);
		
		// 3d-drawing
		rijNum = 3;
		// links
		stapzCC = new StapZCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(stapzCC);
		// rechts
		stap3dCC = new Stap3DCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(stap3dCC);
		
		rijNum = 4; 
		// links
		xDraaiCC = new XDraaiCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(xDraaiCC);
		// rechts
		yDraaiCC = new YDraaiCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(yDraaiCC);
		
		// tekenen
		rijNum = 5;
		// links
		penAanCC = new PenAanCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(penAanCC);
		// rechts	
		penUitCC = new PenUitCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(penUitCC);
		
		rijNum = 6;
		// links	
		vulAanCC = new VulAanCComponent(ccx,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(vulAanCC);
		//rechts	
		vulUitCC = new VulUitCComponent(ccx2,ccy+(rijNum-1)*30,ccsw,ccsh, this);
		ccs.addElement(vulUitCC);
		
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
	}
	
	
	void addToProgrammaPanel(CommandComponent c)
	{
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
		if(cc == stapCC)
		{ 	stapCC = new StapCComponent(x,y,b,h, this);
			ccs.addElement(stapCC);
		}

		if(cc == stapzCC)
		{ 	stapzCC = new StapZCComponent(x,y,b,h, this);
			ccs.addElement(stapzCC);
		}
		if(cc == stap3dCC)
		{ 	stap3dCC = new Stap3DCComponent(x,y,b,h, this);
			ccs.addElement(stap3dCC);
		}
		if(cc == xDraaiCC)
		{ 	xDraaiCC = new XDraaiCComponent(x,y,b,h, this);
			ccs.addElement(xDraaiCC);
		}
		if(cc == yDraaiCC)
		{ 	yDraaiCC = new YDraaiCComponent(x,y,b,h, this);
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
	
	private void herschikStapel()
	{	int yLocation = ccy;
		
		if (vooruitCC.isVisible())
			yLocation += 190;
		
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

	public void verwijder(CommandComponent cc)
	{	
		ccs.removeElement(cc);
		paint();
	}
	
	public void paint()
	{
		paintComponent(jlsvContext2d);
	}
	
	public void paintComponent(Context2d g)
	{	
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos,yPos,breedte,hoogte);

		g.setFillStyle(CssColor.make(205,230,255));
		g.fillRect(xPos+4,yPos+4,172,hoogte-8);

		for (int cCnt = ccs.size() - 1; cCnt >= 0; cCnt--)
		{
			((CommandComponent) ccs.elementAt(cCnt)).paintComponent(g);
		}

	}
	
	// zoek meteen de CommandContainer, maar wel de diepste, dus recursief	
	public CommandContainer getCommandContainerAt(int x, int y)
	{	
		if (!programmaPanel.contains(x, y))
		{	
			return null;
		
		}
		CommandContainer result = findCContainerAt(x,y);
		return result;
	}
	
	public void losSchuiver(CommandComponent sc, int x, int y)
	{	
		
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
			
			cc.removeCaret();
			
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
			return;
		}
		// find component in programmaPanel, so nothing on the left side nor the dragged CC itself will be found
		if ( !programmaPanel.contains(ex, ey))
		{	
			return;
		}
		
		if (traceC != null)
			traceC.removeCaret();
		if (traceCC != null)
			traceCC.removeCaret();
		paint();
		
		traceC = findCComponentAt(ex, ey, sc);
		traceCC = findCContainerAt(ex,ey);
		
		if ((traceC != null) && traceC.commandName.equals("Herhaal") && 
			(traceCC!= null) && traceCC.containerName.equals("loop"))
			traceC = null;
		if ((traceC != null) && traceC.commandName.equals("Keuze") && 
			(traceCC!= null) && traceCC.containerName.equals("if"))
			traceC = null;
		if ((traceC != null) && traceC.commandName.equals("Keuze") && 
			(traceCC!= null) && traceCC.containerName.equals("else"))
			traceC = null;
				
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
//System.out.println("c traced " + traceC.getCommandName());			
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
		
		paint();

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
		
		paint();

	}

	public String getCode()
	{	String s0 = programmaComponent.getCode("");
		for(int i=0 ; i<aantalDeeltaken ; i++)
		{
			
			s0 = s0 + deeltaakComponenten[i].getCode("");
		}
		return s0+"\n";
	}
	
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
	
	// not used
	public void zetPrintCommandsZichtbaar(boolean b)
	{	//printCC.setVisible(b);
		//printlCC.setVisible(b);
		//herschikStapel();
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
		herschikStapel();
	}
	
	/**
	 * given a CC, it must be part of some tree (or form a tree by itself)
	 * whose root is a CC contained in the Vector css of JavaSchuifVeld;
	 * find this root CC
	 * @param c CC whose root must be found
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
			if (result != null)
				break;
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

	//modifiers are not used
	private CommandComponent mouseTargetComponent = null;
	
	public void mousePressed(int x, int y, int modifiers) 
	{
		CommandComponent c = this.findCComponentAt(x, y);
		if (c != null)
		{	
//System.out.println("jlsv mousePressed " + c.commandName);				
			mouseTargetComponent = c;
			mouseTargetComponent.mousePressed(x, y, modifiers);
		}	
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
	
	
	boolean mouseDown = false;
	int lastMoveX, lastMoveY;
	
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
