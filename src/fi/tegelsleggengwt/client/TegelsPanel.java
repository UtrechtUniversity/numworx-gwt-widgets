package fi.tegelsleggengwt.client;

/*
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.*;
*/
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import javax.swing.*;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PopupPanel;


public class TegelsPanel //extends JPanel implements MouseListener, MouseMotionListener, ActionListener 
{
	//TegelsInteractiePanel tip;
	TegelsLeggenGWT tlGWT;
	
	
	//ControlPanel2 cp;
	
	TekenPanel2 tekenPanel;
	
	int breedte, hoogte;
	
	int aantalSs;
	private int laatstex = 0;
	private int laatstey = 0;
	boolean mouseDown;
	int lastMoveX = 0;
	int lastMoveY = 0;
	
	boolean dragging = false;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
	
	SchuifStuk[] ss;
	SchuifStuk actiefSs;
	SchuifStuk basisv;
	SchuifStuk basisvOud;
	Point posBasis;
	
	int aantalNieuwHp;
	Point[] nieuwHp;
	Polygon zeshok;

	boolean pak, maakVorm, tegelKlaar, wisTegelEerst;
	
	String[] abc = {"A","B","C","D","E","F","G","H","I"};
	String code;

//GWT	
	//JPopupMenu popup;
	//JMenuItem mi;
	
	PopupPanel menuPopup;
	MenuBar menuBar;

	// parametrisatie
	// toon de transversie (het vroegere applet TegelsTr)
	// default false
	boolean transVersion = false;
	// toon de demo-versie (alleen nuttig voor de DWO)
	boolean demoVersion = false;
	
	int hokBreedte = 180;
	int controlHoogte = 90;//60;

	Vector basisVormen = new Vector();
	int actualBasisVorm = 0;
	
	Canvas tegelsCanvas;
	Context2d tegelsContext2d;
	
	public TegelsPanel(int b, int h, TegelsLeggenGWT tlGWT, boolean version, boolean demo)
	{
		//setBounds(0, 0, b, h);
		
		this.tlGWT = tlGWT;
		
		hoogte = h; //getSize().height;
		breedte = b; //getSize().width;
		
		transVersion = version;
		demoVersion = demo;
		
		tegelsCanvas = Canvas.createIfSupported();
		if (tegelsCanvas != null)
		{	
			tegelsCanvas.setWidth(b + "px");
			tegelsCanvas.setHeight(h + "px");
			tegelsCanvas.setCoordinateSpaceWidth(b);
			tegelsCanvas.setCoordinateSpaceHeight(h);
		}	
		
		
		MouseHandler mouseHandler = new MouseHandler();
		tegelsCanvas.addMouseDownHandler(mouseHandler);
		tegelsCanvas.addMouseMoveHandler(mouseHandler);
		tegelsCanvas.addMouseUpHandler(mouseHandler);
		
		TouchHandler touchHandler = new TouchHandler();
		tegelsCanvas.addTouchStartHandler(touchHandler);
		tegelsCanvas.addTouchMoveHandler(touchHandler);
		tegelsCanvas.addTouchEndHandler(touchHandler);

		tegelsContext2d = tegelsCanvas.getContext2d();
		//setLayout(null);
		
		
		tekenPanel = new TekenPanel2(this);
		//tekenPanel.setBounds(0, 0, breedte, hoogte);
		
		
		//cp = new ControlPanel2(this);
		//cp.addStyleName(tlGWT.tegelsLeggenGWTCssResource.bottom());

		
		//naar TegelsleggenGWT anders ligt het onderop		
		//tlGWT.canvasPanel.add(cp);
		//tlGWT.canvasPanel.setWidgetLeftWidth(cp, hokBreedte + 1, Style.Unit.PX, breedte - hokBreedte - 2, Style.Unit.PX);
		//tlGWT.canvasPanel.setWidgetTopHeight(cp, hoogte - controlHoogte - 1, Style.Unit.PX, controlHoogte, Style.Unit.PX);
		
		//cp.setLayout(null);
		//cp.setBounds(hokBreedte + 1, hoogte - controlHoogte - 1, breedte - hokBreedte - 2, controlHoogte);
		
		
//		if (demoVersion)
//		{
//			tlGWT.canvasPanel.setWidgetVisible(cp, false);
//		}
		
		// deze HIER!!
		//add(tekenPanel);		
		
		
		pak = false;
		maakVorm = false;
		wisTegelEerst = false;
		aantalSs = 0;
		 
		if (transVersion)
			posBasis = new Point(85, hoogte - 85);
		else	
			posBasis = new Point(95, hoogte - 85);
		
		ss = new SchuifStuk[500];
		Point p1, p2, p3, p4;
		if (transVersion)
		{	p1 = new Point(-2, -2);
			p2 = new Point(2, -2);
			p3 = new Point(2, 2);
			p4 = new Point(-2, 2);
		}
		else
		{	p1 = new Point(-20, -20);
			p2 = new Point(20, -20);
			p3 = new Point(20, 40);
			p4 = new Point(-20, 40);
		}
		Point[] pnt = {p1, p2, p3, p4};
		
//System.out.println("basisv");		
		basisv = new SchuifStuk(transVersion, 4, pnt, CssColor.make(255,0,0));
//System.out.println("basisvOud");		
		basisvOud = new SchuifStuk(transVersion, 4, pnt, CssColor.make(230, 230, 230));
		basisv.zetPositie(posBasis.x, posBasis.y);
		basisvOud.zetPositie(posBasis.x, posBasis.y);
		nieuwHp = new Point[50];
		aantalNieuwHp = 0;
		for (int i = 0; i < 4; i++)
		{	nieuwHp[i] = new Point(pnt[i]);
			aantalNieuwHp++;
		}
		nieuwHp[4] = new Point(pnt[0]);
		aantalNieuwHp++;
		tegelKlaar = true;
		maakCodeString();
		
//System.out.println("kopie basisv");
		basisVormen.addElement(new SchuifStuk(transVersion, basisv.aantalPunten, basisv.punten,
				               posBasis, basisv.kleur));
		
		int n = 10 / Trans.factor;
		zeshok = new Polygon();
		zeshok.addPoint(posBasis.x + Trans.geefx(n, 0), posBasis.y + Trans.geefy(n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, n), posBasis.y + Trans.geefy(0, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, n), posBasis.y + Trans.geefy(-n, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, 0), posBasis.y + Trans.geefy(-n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, -n), posBasis.y + Trans.geefy(0, -n));
		zeshok.addPoint(posBasis.x + Trans.geefx(n, -n), posBasis.y + Trans.geefy(n, -n));
		
		menuBar = new MenuBar(true);
		menuBar.addItem(TegelsLeggenGWT.rb.menuDraaiLabel(), new MenuCommand("draai"));
		menuBar.addItem(TegelsLeggenGWT.rb.menuSpiegelLabel(), new MenuCommand("spiegel"));
		menuBar.addItem(TegelsLeggenGWT.rb.menuKleurLabel(), new MenuCommand("kleur"));
		menuBar.addSeparator();
		menuBar.addItem(TegelsLeggenGWT.rb.menuKopieerLabel(), new MenuCommand("kopieer"));
		
//GWT		
		//popup = new JPopupMenu();
				
		//mi = new JMenuItem(Tegels.rb.getString("menuDraaiLabel") + "  (Shift + Click)");
		//mi.addActionListener(this);
		//popup.add(mi);
		
		//mi = new JMenuItem(Tegels.rb.getString("menuSpiegelLabel"));
		//mi.addActionListener(this);
		//popup.add(mi);
		
		//mi = new JMenuItem(Tegels.rb.getString("menuKleurLabel") + "  (Alt + Click)");
		//mi.addActionListener(this);
		//popup.add(mi);
		
		//popup.addSeparator();
		
		//mi = new JMenuItem(Tegels.rb.getString("menuKopieerLabel"));
		//mi.addActionListener(this);
		//popup.add(mi);
		
		//add(popup);
		
	}

	//nodig?
	public void setSize(int b, int h)
	{
		//super.setSize(b, h);

		hoogte = h; //getSize().height;
		breedte = b; //getSize().width;
		
//GWT?		
		//tekenPanel.setBounds(0, 0, breedte, hoogte);		
		//cp.setBounds(hokBreedte + 1, hoogte - controlHoogte - 1, breedte - hokBreedte - 2, controlHoogte);		
		
		if (transVersion)
			posBasis = new Point(85, hoogte - 85);
		else	
			posBasis = new Point(95, hoogte - 85);
		
		basisv.zetPositie(posBasis.x, posBasis.y);
		basisvOud.zetPositie(posBasis.x, posBasis.y);

		for (int bCnt = 0; bCnt < basisVormen.size(); bCnt++)
		{	SchuifStuk ss = (SchuifStuk) basisVormen.elementAt(bCnt);
			ss.zetPositie(posBasis.x, posBasis.y);
		}

		int n = 10 / Trans.factor;
		zeshok = new Polygon();
		zeshok.addPoint(posBasis.x + Trans.geefx(n, 0), posBasis.y + Trans.geefy(n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, n), posBasis.y + Trans.geefy(0, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, n), posBasis.y + Trans.geefy(-n, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, 0), posBasis.y + Trans.geefy(-n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, -n), posBasis.y + Trans.geefy(0, -n));
		zeshok.addPoint(posBasis.x + Trans.geefx(n, -n), posBasis.y + Trans.geefy(n, -n));

		tekenOpnieuw();
	}
	
	public void zetTransVersion(boolean b)
	{
		transVersion = b;
		
//GWT		
		//cp.controlLeggen();
		//cp.gridKeuze.setVisible(false);
		//if (b)
		//	cp.codeveld.setVisible(false);
		
		basisVormen.removeAllElements();

		pak = false;
		maakVorm = false;
		wisTegelEerst = false;
		aantalSs = 0;
		 
		if (transVersion)
			posBasis = new Point(85, hoogte - 85);
		else	
			posBasis = new Point(95, hoogte - 85);
		
//		ss = new SchuifStuk[500];
		Point p1, p2, p3, p4;
		if (transVersion)
		{	p1 = new Point(-2, -2);
			p2 = new Point(2, -2);
			p3 = new Point(2, 2);
			p4 = new Point(-2, 2);
		}
		else
		{	p1 = new Point(-20, -20);
			p2 = new Point(20, -20);
			p3 = new Point(20, 40);
			p4 = new Point(-20, 40);
		}
		Point[] pnt = {p1, p2, p3, p4};
		
		basisv = new SchuifStuk(transVersion, 4, pnt, CssColor.make(255,0,0));
		basisvOud = new SchuifStuk(transVersion, 4, pnt, CssColor.make(230, 230, 230));
		basisv.zetPositie(posBasis.x, posBasis.y);
		basisvOud.zetPositie(posBasis.x, posBasis.y);
//		nieuwHp = new Point[50];
		aantalNieuwHp = 0;
		for (int i = 0; i < 4; i++)
		{	nieuwHp[i] = new Point(pnt[i]);
			aantalNieuwHp++;
		}
		nieuwHp[4] = new Point(pnt[0]);
		aantalNieuwHp++;
		tegelKlaar = true;
		maakCodeString();
		
		basisVormen.addElement(new SchuifStuk(transVersion, basisv.aantalPunten, basisv.punten,
				               posBasis, basisv.kleur));

		int n = 10 / Trans.factor;
		zeshok = new Polygon();
		zeshok.addPoint(posBasis.x + Trans.geefx(n, 0), posBasis.y + Trans.geefy(n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, n), posBasis.y + Trans.geefy(0, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, n), posBasis.y + Trans.geefy(-n, n));
		zeshok.addPoint(posBasis.x + Trans.geefx(-n, 0), posBasis.y + Trans.geefy(-n, 0));
		zeshok.addPoint(posBasis.x + Trans.geefx(0, -n), posBasis.y + Trans.geefy(0, -n));
		zeshok.addPoint(posBasis.x + Trans.geefx(n, -n), posBasis.y + Trans.geefy(n, -n));
		
		tekenOpnieuw();
	}
	
	public void zetDemoVersion(boolean b)
	{
		demoVersion = b;
		
//GWT		
		//cp.setVisible(!demoVersion);
		
		tekenOpnieuw();
	}
	
 	void tekenOpnieuw()
	{	
 		paint();
	}	
	
 	public void paint()
 	{
 		tekenPanel.paintComponent(tegelsContext2d);
 	}
 	
 	//public void update(Graphics g)
 	//{	paint(g);
 	//}
 	
	void vermenigvuldigPunten(double factor)
	{	for (int i = 0; i < aantalNieuwHp; i++)
		{	nieuwHp[i].x *= factor;
			nieuwHp[i].y *= factor;
		}
		basisv = new SchuifStuk(transVersion, aantalNieuwHp, nieuwHp, posBasis, CssColor.make(255,0,0));
	}

	void zetTekenen()
	{	maakVorm = true;
		//if(basisv!=null)
		//{	basisvOud = new SchuifStuk(basisv,basisv.positie.x,basisv.positie.y);
		//	basisvOud.zetKleur(new Color(230,230,230));
		//}
		tekenOpnieuw();
	}

	public boolean basisVormenContains(SchuifStuk ss)
	{	boolean found = false;
		for (int vCnt = 0; vCnt < basisVormen.size(); vCnt++)
		{	SchuifStuk bv = (SchuifStuk) basisVormen.elementAt(vCnt);
			if (SchuifStuk.equalSS(ss,bv))
				found = true;
			
		}
		
		return found;
	}

	void zetLeggen()
	{	maakVorm = false;
		wisTegelEerst = false;
		if (tegelKlaar)
		{	
//System.out.println("tegelKlaar");

			basisvOud = new SchuifStuk(transVersion, aantalNieuwHp, nieuwHp, posBasis, CssColor.make(230, 230, 230));
		
			//if (!basisVormen.contains(basisv))
			if (!basisVormenContains(basisv))
			{	
				
System.out.println("!basisVormenContains");

				basisVormen.addElement(
					new SchuifStuk(transVersion, basisv.aantalPunten, basisv.punten, posBasis, basisv.kleur));
				actualBasisVorm = basisVormen.size() - 1;
				
System.out.println("basisVormen = " + basisVormen.size());				
				
				tlGWT.cp.downButton.setEnabled(false);
				tlGWT.cp.upButton.setEnabled(true);
			
			}
		
		}
		tekenOpnieuw();
	}
	
	void vorigeBasisVorm()
	{	if (actualBasisVorm > 0)
		{	actualBasisVorm--;
			zetBasisVorm((SchuifStuk) basisVormen.elementAt(actualBasisVorm));
			if (actualBasisVorm == 0)
			{	
				tlGWT.cp.upButton.setEnabled(false);
			}
			if (basisVormen.size() > 1)
			{	
				tlGWT.cp.downButton.setEnabled(true);
			}
			tekenOpnieuw();
		}
		
	}
	
	void volgendeBasisVorm()
	{	if (actualBasisVorm < (basisVormen.size() - 1))
		{	actualBasisVorm++;
			zetBasisVorm((SchuifStuk) basisVormen.elementAt(actualBasisVorm));
			if (actualBasisVorm == (basisVormen.size() - 1))
			{	
				tlGWT.cp.downButton.setEnabled(false);
			}
			if (basisVormen.size() > 1)
			{
				tlGWT.cp.upButton.setEnabled(true);
			}	
			tekenOpnieuw();
		}
		
	}
	void draaiBasisvorm()
	{	if (basisv != null)
			basisv.draaiVorm();
		
		// dit ook niet doen in transverie!
		//if (basisvOud != null)
		//	basisvOud.draaiVorm();

		tekenOpnieuw();
	}
	
	void kleurBasisvorm(CssColor c)
	{	if (basisv != null)
			basisv.zetKleur(c);
		tekenOpnieuw();
	}
	
	void wisSs()
	{	aantalSs = 0;
		tekenOpnieuw();
	}
	
	void wisTegel()
	{	tegelKlaar = false;
		
		basisv = null;
		aantalNieuwHp = 0;
		maakCodeString();
		if (wisTegelEerst)
		{	basisvOud = null;
			wisTegelEerst = false;
		}
		else
		{	wisTegelEerst = true;
		}
		tekenOpnieuw();
	}
	
	void tekenStapTerug()
	{	if (aantalNieuwHp != 0)
		{	aantalNieuwHp--;
			tegelKlaar = false;
			maakCodeString();
			tekenOpnieuw();
		}
		if (aantalNieuwHp == 0)
		{	basisv = null;
		}
	}
	
	void maakCodeString()
	{	code = "";
		if (aantalNieuwHp != 0)
		{	for (int i = 0; i < aantalNieuwHp - 1; i++)
			{	code = code + abc[nieuwHp[i].x / 20 + 4] + Integer.toString(nieuwHp[i].y / 20 + 5);
				if (!tegelKlaar || i < aantalNieuwHp - 2) 
					code = code + ",";
			}
			if (!tegelKlaar)
				code = code + abc[nieuwHp[aantalNieuwHp - 1].x / 20 + 4] + Integer.toString(nieuwHp[aantalNieuwHp - 1].y / 20 + 5);
		}
		
		if (tlGWT.cp != null && tlGWT.cp.codeveld != null)
			tlGWT.cp.codeveld.setText(code);
	}
	
	public void voegNieuwPuntToe(int x, int y)
	{	if (tegelKlaar)
			return;
		
		if ((aantalNieuwHp == 1 || aantalNieuwHp == 2) && x == nieuwHp[0].x && y == nieuwHp[0].y)
		{	aantalNieuwHp = 0;
			maakCodeString();
			return;
		}
		for (int i = 1; i < aantalNieuwHp; i++)
		{	if (x == nieuwHp[i].x && y == nieuwHp[i].y)
			{	aantalNieuwHp = 0;
				maakCodeString();
				return;
			}
		}
		nieuwHp[aantalNieuwHp] = new Point(x,y);
		aantalNieuwHp++;
		
		if (aantalNieuwHp > 2 && x == nieuwHp[0].x && y == nieuwHp[0].y)
		{	tegelKlaar = true;
			Point[] hp = new Point[aantalNieuwHp];
			for (int i = 0; i < aantalNieuwHp; i++)
			{	hp[i] = new Point(nieuwHp[i]);
			}
			basisv = new SchuifStuk(transVersion, aantalNieuwHp, hp, posBasis, CssColor.make(255,0,0));
			tekenOpnieuw();
			
			// toevoegen aan basisVormen mits nog niet gemaakt
//			if (!basisVormen.contains(basisv))
//				basisVormen.addElement(basisv);
			
		}
		maakCodeString();
	}
	
	public void zetBasisVorm(SchuifStuk bVorm)
	{
		aantalNieuwHp = bVorm.aantalPunten;
		for (int i = 0; i < bVorm.aantalPunten; i++)
		{	nieuwHp[i] = new Point(bVorm.punten[i]);
		}
		basisv = new SchuifStuk(transVersion, bVorm.aantalPunten, bVorm.punten, 
				                posBasis, bVorm.kleur);
		basisvOud = new SchuifStuk(transVersion, bVorm.aantalPunten, bVorm.punten, 
                				posBasis, CssColor.make(230, 230, 230));
		
		maakCodeString();
	}
	
	public void showPopupMenu(int x, int y)
	{
		int popupX = x + tegelsCanvas.getAbsoluteLeft();
		int popupY = y + tegelsCanvas.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}	

	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		
		if (demoVersion)
			return;
		
		laatstex = eventX;
		laatstey = eventY;
		mouseDown = true;
		
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);
		
		if (transVersion)
		{
			if (maakVorm && zeshok.contains(eventX, eventY))
			{	int x = eventX - posBasis.x;
				int y = eventY - posBasis.y;
				
				int n = 10 / Trans.factor;
				for (int i = -n; i < n + 1; i++)
				{	for (int j = -n; j < n + 1; j++)
					{	int xp = Trans.geefx(i, j);
						int yp = Trans.geefy(i, j);
						if (Math.abs(x - xp) < 5 && Math.abs(y - yp) < 5)
						{	voegNieuwPuntToe(i, j);
							return;
						}
					}
				}
			}
		}
		else
		{	
			if (maakVorm && eventX < 180 && hoogte - eventY < 180)
			{	int x = eventX - posBasis.x + 200;
				int y = eventY - posBasis.y + 200;
				int ex = (x + 5) % 20;
				int ey = (y + 5) % 20;
				if (ex < 10 && ey < 10)
				{	voegNieuwPuntToe(x - 200 - ex + 5, y - 200 - ey + 5);
				}
				tekenOpnieuw();
				return;
			}
		}

		if (basisv != null && basisv.bevat(eventX, eventY))
		{	//setCursor(new Cursor(Cursor.MOVE_CURSOR));
			pak = true;
			return;
		}
		for (int i = 0; i < aantalSs; i++)
		{	if (ss[i].bevat(eventX, eventY))
			{	//setCursor(new Cursor(Cursor.MOVE_CURSOR));
				actiefSs = ss[i];
				for (int j = i; j > 0; j--)
				{	ss[j] = ss[j - 1];
				}
				ss[0] = actiefSs;
				
//GWT				
				//if ((e.getModifiers() == e.BUTTON3_MASK || e.isControlDown()) && actiefSs.bevat(e.getX(), e.getY()))
				//{	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				//	popup.show(this,e.getX(), e.getY());
				//}
				//else if ((e.isShiftDown()) && actiefSs.bevat(e.getX(), e.getY()))
				//{	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				//	actiefSs.draaiVorm();
				//}
				//else if ((e.isAltDown()) && actiefSs.bevat(e.getX(), e.getY()))
				//{	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				//	actiefSs.zetKleur(basisv.kleur);
				//}
				return;
			}
		}
	}	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		
		if (demoVersion)
			return;
		
		int dx = eventX - laatstex;
		int dy = eventY - laatstey;
		
		if ((dx != 0) || (dy != 0))
			dragging = true;

		if (pak)
		{	ss[aantalSs] = new SchuifStuk(transVersion, basisv, posBasis.x, posBasis.y);
			actiefSs = ss[aantalSs];
			for (int j = aantalSs; j > 0; j--)
			{	ss[j] = ss[j - 1];
			}
			ss[0] = actiefSs;
			aantalSs++;
			pak = false;
		}
		if (actiefSs != null)
			actiefSs.veranderPositie(dx, dy);
		tekenOpnieuw();
		laatstex = eventX;
		laatstey = eventY;
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	
		if (demoVersion)
			return;
		
		if (isLongClick()) 
		{
			if (!dragging)
			{
				showPopupMenu(laatstex,laatstey);
				doubletap.clear();
				return;
			}
		} 
		//else 
		//{
			if (doubletap.size() >= 2) 
			{	//doubletap.clear();
				doubletap.remove(0);
			}
		//}

			
		dragging = false;

		
		if (new Rectangle(0, hoogte - 180, 180, 180).contains(eventX, eventY) && actiefSs != null)
		{	for (int j = 1; j < aantalSs; j++)
			{	ss[j - 1] = ss[j];
			}
			aantalSs--;
		}
		if (actiefSs != null)	
			actiefSs.plaatsOpGrid();

		//setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		pak = false;
		actiefSs = null;
		tekenOpnieuw();	
	}
	
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
			//boolean shiftPressed = e.isShiftKeyDown();

//System.out.println("sp = " + shiftPressed);

			mouseMoveTouchMoveAction(eventX, eventY);
			
			
			
		} // onMouseMove
		
		//public void mouseReleased(MouseEvent e)
		public void onMouseUp(MouseUpEvent e)	
		{
			e.preventDefault();
			
			// prevent scrolling
			e.stopPropagation();
			
			mouseDown = false;
		
			mouseUpTouchEndAction(e.getX(), e.getY());

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
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				
				int eventX = touch.getPageX() - tegelsCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - tegelsCanvas.getAbsoluteTop();				
				
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
				
				//Widget sender = (Widget) e.getSource();
			    //Element elem = sender.getElement();
				//int eventX = touch.getRelativeX(elem);
				//int eventY = touch.getRelativeY(elem);
				//boolean shiftPressed = e.isShiftKeyDown();

			    //boolean shiftPressed = false;
			    int eventX = touch.getPageX() - tegelsCanvas.getAbsoluteLeft();
				int eventY = touch.getPageY() - tegelsCanvas.getAbsoluteTop();				
			    
				mouseMoveTouchMoveAction(eventX, eventY);
				
				lastMoveX = eventX;
				lastMoveY = eventY;

				
		    }
			e.preventDefault();
			e.stopPropagation();
			
		}
		public void onTouchEnd(TouchEndEvent e)
		{
			mouseUpTouchEndAction(lastMoveX, lastMoveY);
		}

	}
	
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

	protected boolean isDoubleClick() 
	{
	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
		//return (doubletap.size() >= 2) && doubletap.get(doubletap.size() - 1) - doubletap.get(doubletap.size() - 2) < 700;
	}

	class MenuCommand implements Command
	{
		String cmdString = "";
		
		public MenuCommand(String s)
		{
			cmdString = s;
		}
		public void execute()
		{
			menuAction(cmdString);
		}
	}

	//public void mouseMoved(MouseEvent e){;}
	//public void mouseExited(MouseEvent e){;}
	//public void mouseClicked(MouseEvent e){;}
	//public void mouseEntered(MouseEvent e){;}
	
//menuListener
/*	
	public void actionPerformed(ActionEvent e)
	
*/
	public void menuAction(String s)
	{	
		
		//if (((JMenuItem) e.getSource()).getText().indexOf(Tegels.rb.getString("menuDraaiLabel")) > -1)
		if (s.equals("draai"))
		{	ss[0].draaiVorm();
		}
		//if (((JMenuItem) e.getSource()).getText().indexOf(Tegels.rb.getString("menuSpiegelLabel")) > -1)
		if (s.equals("spiegel"))
		{	ss[0].spiegel();
		}
		//if (((JMenuItem) e.getSource()).getText().indexOf(Tegels.rb.getString("menuKleurLabel")) > -1)
		if (s.equals("kleur"))
		{	ss[0].zetKleur(basisv.kleur);
		}
		//if (((JMenuItem) e.getSource()).getText().indexOf(Tegels.rb.getString("menuKopieerLabel")) > -1)
		if (s.equals("kopieer"))
		{	ss[aantalSs] = new SchuifStuk(transVersion, ss[0], ss[0].positie.x + 20 ,ss[0].positie.y - 20);
			actiefSs = ss[aantalSs];
			for (int j = aantalSs; j > 0; j--)
			{	ss[j] = ss[j - 1];
			}
			ss[0] = actiefSs;
			aantalSs++;
		}
		
		menuPopup.setVisible(false);
		
		tekenOpnieuw();
	}
	
}
