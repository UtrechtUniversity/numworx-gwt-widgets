package fi.geomalggwt.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;


public class AlgebraVeld 
{	
	private int breedte, hoogte;

	Figuur[] fg;
	private Figuur[] fgNieuw ;
	int aantalFg;
	private int aantalFgNieuw;
	private Buffer buffer;
	State docentState = null;
	
	private Figuur basisFiguur, basisFiguurX,  basisFiguurY;
	private Figuur actiefFg, selectFiguur;
	private Point basisPosX,basisPosY;
	private Lijnstuk  basisLijnstuk, basisLijnstukX,  basisLijnstukY;
	private boolean pak, veranderVar, gesplitst, splitsV, maakLos;
	private boolean muisAan;
	int laatstex = 0;
	int laatstey = 0;
	private int drukx = 0;
	private int druky = 0;
	private int cursorx = 0;
	private int cursory = 0;
	
	int[] var;
	private int[] varTek;
	private int varHuidig;
	
	String formule;
	private double oppervlakte;
	
//GWT	
	//private Font fc,fl;
	//private FontMetrics fcm, flm;
	String fcString = "12px sans-serif";
	String fOppString = "20px bold sans-serif";
	String fOppExpString = "15px bold sans-serif";
	
//GWT	
	//private DecimalFormatSymbols dfs;
	//private DecimalFormat df;
	//private FontMetrics fm;
	
//GWT	
	//JPopupMenu popup;
	//JMenuItem mi, menuSplitsItem, menuSVItem, menuVSItem, menuMLItem, menuMALItem;
	
	PopupPanel menuPopup;
	MenuBar menuBar;
		
	
	private boolean varWaardeZichtbaar;
	private boolean oppWaardeZichtbaar;
	private boolean formuleZichtbaar = true;
	private boolean constructieTools = true;
	private boolean alleenOppervlaktes;
	private boolean werkblad;
	private boolean oppervlaktesZichtbaar = true;
	private boolean lengtesBreedtesZichtbaar = true;
	private boolean negatieveWaarden = true; 
	private boolean puzzelen = false;

	Rectangle werkbladRectangle = null;
	CssColor werkbladColor = CssColor.make(245, 245, 245);
	
	Rectangle werkbladBigRectangle = null;
	
//GWT	
	//JButton resetButton;
	//ImageIcon resetIcon;
	
	Context2d gIm;
	GeomAlgGWT owner;
	
	protected boolean press;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
    boolean dragging = false;

String testString = "testString";
String[] testStrings = new String[30];
	
	public AlgebraVeld(int b, int h, Context2d ct2d, GeomAlgGWT o)
	{	hoogte = h;
		breedte = b;
		
		gIm = ct2d;
		owner = o;
		
		
//GWT		
		//dfs = new DecimalFormatSymbols();
		//dfs.setDecimalSeparator('.');
		//df = new DecimalFormat("0.##", dfs);


		//buffer = new Buffer(50);
		
		basisPosX = new Point(breedte / 2, hoogte - 69);
		basisPosY = new Point(28,(hoogte - 60) / 2);
		
		basisLijnstukX = new Lijnstuk(0, 3, Lijnstuk.HOR, basisPosX.x, basisPosX.y);
		basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
		basisFiguurX.voegToe(basisLijnstukX);
		basisLijnstukY = new Lijnstuk(0, 3, Lijnstuk.VER, basisPosY.x, basisPosY.y);
		basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
		basisFiguurY.voegToe(basisLijnstukY);
		
		fg = new Figuur[200];
		fgNieuw = new Figuur[200];
		aantalFg = 0;
		aantalFgNieuw = 0;
		pak = false;
		veranderVar = false;
		gesplitst = true;
		muisAan = true;
		maakLos = false;
		
		var = new int[4];
		var[1] = 84;
		var[2] = 60;
		var[3] = 36;
		varTek = new int[4];
		varTek[1] = 1;
		varTek[2] = 1;
		varTek[3] = 1;
		
		buffer = new Buffer(20);
		buffer.voegToe(new State(aantalFg, fg, var));
		
//GWT
//		fc = new Font("Helvetica", Font.PLAIN, 12);
//		fl = new Font("Helvetica", Font.PLAIN, 12);
//		fcm = getFontMetrics(fc);
	
		
		menuBar = new MenuBar(true);
		menuBar.addItem(GeomAlgGWT.rb.menuDraaiLabel(), new MenuCommand("draai"));
		menuBar.addItem(GeomAlgGWT.rb.menuKopieerLabel(), new MenuCommand("kopieer"));
		menuBar.addSeparator();
		menuBar.addItem(GeomAlgGWT.rb.menuSplitsLabel(), new MenuCommand("splits"));
		menuBar.addItem(GeomAlgGWT.rb.menuSplitsVolledigLabel(), new MenuCommand("splitsvolledig"));
		menuBar.addItem(GeomAlgGWT.rb.menuVoegSamenLabel(), new MenuCommand("voegsamen"));
		menuBar.addSeparator();
		menuBar.addItem(GeomAlgGWT.rb.menuMaakLosLabel(), new MenuCommand("maaklos"));
		menuBar.addItem(GeomAlgGWT.rb.menuMaakAllesLosLabel(), new MenuCommand("maakalleslos"));

/* GWT
  		
 	    popup = new JPopupMenu();
		mi = new JMenuItem(GeomAlgebra.rb.getString("menuDraaiLabel"));
		mi.addActionListener(this);
		popup.add(mi);
		
		mi = new JMenuItem(GeomAlgebra.rb.getString("menuSpiegelLabel"));
		mi.addActionListener(this);
		//popup.add(mi);
		
		mi = new JMenuItem(GeomAlgebra.rb.getString("menuMinLabel"));
		mi.addActionListener(this);
		//popup.add(mi);
		
		
		mi = new JMenuItem(GeomAlgebra.rb.getString("menuKopieerLabel"));
		mi.addActionListener(this);
		popup.add(mi);
		
		popup.addSeparator();
		
		menuSplitsItem = new JMenuItem(GeomAlgebra.rb.getString("menuSplitsLabel"));
		menuSplitsItem.addActionListener(this);
		popup.add(menuSplitsItem);
		
		menuSVItem = new JMenuItem(GeomAlgebra.rb.getString("menuSVLabel"));
		menuSVItem.addActionListener(this);
		popup.add(menuSVItem);
		 
		menuVSItem = new JMenuItem(GeomAlgebra.rb.getString("menuVSLabel"));
		menuVSItem.addActionListener(this);
		popup.add(menuVSItem);
		
		popup.addSeparator();
		
		menuMLItem = new JMenuItem(GeomAlgebra.rb.getString("menuMLLabel"));
		menuMLItem.addActionListener(this);
		popup.add(menuMLItem);
		
		menuMALItem = new JMenuItem(GeomAlgebra.rb.getString("menuMALLabel"));
		menuMALItem.addActionListener(this);
		popup.add(menuMALItem);
		
		
		add(popup);
*/		
		if (constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 25, (breedte - 40) / 3, hoogte - 82 - 25);
			werkbladBigRectangle = new Rectangle(0, 25, (breedte - 40) / 3 + 42, hoogte - 25);
		}
		else if (constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 1, (breedte - 40) / 3, hoogte - 82 - 1);
			werkbladBigRectangle = new Rectangle(0, 1, (breedte - 40) / 3 + 42, hoogte - 1);
		}
		else if (!constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 - 1);
			werkbladBigRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 + 5);
		}
		else if (!constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
			werkbladBigRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
		}
	}
	
	public void showPopupMenu(int x, int y)
	{
		int popupX = x + owner.geomAlgGWTCanvas.getAbsoluteLeft();
		int popupY = y + owner.geomAlgGWTCanvas.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}	
	
	void reset()
	{
		buffer = new Buffer(20);
		if (docentState != null)
		{	buffer.voegToe(docentState);
			setState(docentState);
		}
		tekenOpnieuw();
	}

//GWT
//alleen bij !constructieTools, op Canvas?	
/* 
	void makeResetButton(ImageIcon resetIcon)
	{
		resetButton = new JButton(resetIcon);
		resetButton.setBounds(breedte - 25, hoogte - 25, 16, 16);
		resetButton.setVisible(false);
		add(resetButton);
		resetButton.addActionListener(new ResetAL());
	}
*/
//zie hierboven	
/*GWT	
	class ResetAL implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			reset();
		}
	}
	
*/
	
	public void setSize(int b, int h)
	{
		//if ((getSize().width == b) && (getSize().height == h))
		//if ((breedte == b) && (hoogte == h))
		//	return;
		
		hoogte = h;
		breedte = b;
		
//GWT		
		//if (resetButton != null)
		//	resetButton.setBounds(breedte - 25, hoogte - 25, 16, 16);		

		if (negatieveWaarden)
		{	
			basisPosX = new Point(breedte / 2, hoogte - 69);
			basisPosY = new Point(28,(hoogte - 60) / 2);
		}
		else
		{	
			basisPosX = new Point(2 * 24 + 10, hoogte - 69);
			basisPosY = new Point(28, hoogte - 60 - 29);
		}
		
		basisLijnstukX = new Lijnstuk(0, 3, Lijnstuk.HOR, basisPosX.x, basisPosX.y);
		basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
		basisFiguurX.voegToe(basisLijnstukX);
		basisLijnstukY = new Lijnstuk(0, 3, Lijnstuk.VER, basisPosY.x, basisPosY.y);
		basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
		basisFiguurY.voegToe(basisLijnstukY);

		//super.setSize(b, h);
		
		if (constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 25, (breedte - 40) / 3, hoogte - 82 - 25);
			werkbladBigRectangle = new Rectangle(0, 25, (breedte - 40) / 3 + 42, hoogte - 25);
		}
		else if (constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 1, (breedte - 40) / 3, hoogte - 82 - 1);
			werkbladBigRectangle = new Rectangle(0, 1, (breedte - 40) / 3 + 42, hoogte - 1);
		}
		else if (!constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 - 1);
			werkbladBigRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 + 5);
		}
		else if (!constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
			werkbladBigRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
		}
		
		
	}
	public void paint()
	{
		//paint(gIm);
		tekenOpImage();
	}
	
	public void paint(Context2d g)
  	{
//		gIm = g;
		tekenOpImage();
		
  	}
	 
  	public void tekenOpImage()
  	{ 	if (gIm == null)
  			return;
  	
  		//gIm.setColor(new Color(220, 220, 220));
  		gIm.setFillStyle(CssColor.make(220, 220, 220));
    	gIm.fillRect(0, 0, breedte, hoogte);
    	
		//gIm.setColor(Color.white);
		gIm.setFillStyle(CssColor.make(255, 255, 255));
    	if (constructieTools) 
    		gIm.fillRect(42, 0, breedte - 40, hoogte - 82);
    	else 
    		gIm.fillRect(0, 0, breedte, hoogte);
    	
		if (werkblad)
		{	//gIm.setColor(werkbladColor);
			gIm.setFillStyle(werkbladColor);
			gIm.fillRect(werkbladRectangle.x, werkbladRectangle.y, werkbladRectangle.width, werkbladRectangle.height);
			
			//gIm.setColor(Color.yellow);
			//gIm.fillRect(werkbladBigRectangle.x, werkbladBigRectangle.y, werkbladBigRectangle.width, werkbladBigRectangle.height);
			//gIm.setColor(werkbladColor);
			//gIm.fillRect(werkbladRectangle.x, werkbladRectangle.y, werkbladRectangle.width, werkbladRectangle.height);
		}
    	
		tekenprogramma();
		//gIm.setColor(Color.gray);
		gIm.setStrokeStyle(CssColor.make(220, 220, 220));
		if(!werkblad)
		{	//gIm.drawRect(0, 0, breedte - 1, hoogte - 1);
			gIm.strokeRect(0, 0, breedte - 1, hoogte - 1);
		}
		if (constructieTools)
		{	//gIm.drawLine(1, hoogte - 42, breedte - 1, hoogte - 42);
			gIm.beginPath();
			gIm.moveTo(1, hoogte - 42);
			gIm.lineTo(breedte - 1, hoogte - 42);
			gIm.stroke();
		}

gIm.setFillStyle(CssColor.make(255, 0, 0));
//gIm.fillText(testString, 50, 50);
//for (int cnt = 0; cnt < testStrings.length; cnt++)
//{
//	gIm.fillText(testStrings[cnt], 50, 50 + 15 * cnt);
//}
		
	}
	
 	void tekenOpnieuw()
	{	owner.paint();
 		
	}	
	
	public void tekenprogramma()
	{	if (constructieTools)
		{	tekenFiguur(basisFiguurX);
			tekenFiguur(basisFiguurY);
			tekenVarPunt();
		}
		
		//if(actiefFg!=null)tekenFiguur(actiefFg);
		tekenFiguren();
		//gIm.setColor(new Color(220, 220, 220));
		gIm.setFillStyle(CssColor.make(220, 220, 220));
		if (formuleZichtbaar)
			gIm.fillRect(0, 0, breedte - 1, 25);
		
//GWT		
//		gIm.setFont(new Font("Helvetica", Font.PLAIN, 20));
		gIm.setFont(fOppString);
		
		//gIm.setColor(Color.black);
		gIm.setFillStyle(CssColor.make(0, 0, 0));
		if (formule != null && formuleZichtbaar)
			tekenFormule(formule, 50, 20);
		
		//gIm.setColor(Color.black);
		//gIm.drawRect(breedte-125, 0, 125, 100);
		//gIm.drawLine(breedte-125,25,breedte,25);
		if (oppWaardeZichtbaar)
		{	
			gIm.setFillStyle(CssColor.make(0, 0, 0));
			gIm.setStrokeStyle(CssColor.make(0, 0, 0));
			//gIm.setColor(Color.black);
			//gIm.drawRect(breedte - 125, 0, 125, 25);
			gIm.strokeRect(breedte - 125, 0, 125, 25);
//GWT			
//			gIm.drawString( GeomAlgebra.rb.getString("oppLabel") + " = " + df.format(oppervlakte), breedte - 120, 20);
			int oppint = 100 * (int) Math.round(oppervlakte);
			int oppdec = (int) Math.round(100 * oppervlakte);
			String oppString;
			if (Math.abs(oppdec - oppint) > 0)
				oppString = UF.format(oppervlakte, 2);
			else
				oppString = UF.format(oppervlakte, 0);
			//gIm.fillText("opp = " + oppString, breedte - 120, 20);
			gIm.fillText(GeomAlgGWT.rb.oppervlakteLabel()+ " = " + oppString, breedte - 120, 20);
			
		}
		if (varWaardeZichtbaar)
		{	//gIm.setColor(new Color(220, 220, 220));
			gIm.setFillStyle(CssColor.make(220, 220, 220));
			//gIm.fillRect(breedte - 125, 25, 125, 75);
			gIm.fillRect(breedte - 125, 25, 125, 75);
			    		
			//gIm.setColor(Color.black);
			gIm.setFillStyle(CssColor.make(0, 0, 0));
			gIm.setStrokeStyle(CssColor.make(0, 0, 0));
			//gIm.drawRect(breedte - 125, 25, 125, 75);
			gIm.strokeRect(breedte - 125, 25, 125, 75);
//GWT			
			//gIm.drawString( "    x = " + df.format(1.0 * var[1] / 24), breedte - 120, 50);
			//gIm.drawString( "    y = " + df.format(1.0 * var[2] / 24), breedte - 120, 70);
			//gIm.drawString( "    z = " + df.format(1.0 * var[3] / 24), breedte - 120, 90);
			int xint = 100 * (int) Math.round( ((double)var[1]) / 24);
			int xdec = (int) Math.round(100 * ((double)var[1]) / 24);

			String xString;
			if (Math.abs(xdec - xint) > 0)
				xString = UF.format( ((double)var[1]) / 24, 2);
			else
				xString = UF.format( ((double)var[1]) / 24, 0);
			gIm.fillText( "    x = " + xString, breedte - 120, 50);
			
			int yint = 100 * (int) Math.round(1.0 * var[2] / 24);
			int ydec = (int) Math.round(100 * 1.0 * var[2] / 24);
			String yString;
			if (Math.abs(ydec - yint) > 0)
				yString = UF.format(1.0 * var[2] / 24, 2);
			else
				yString = UF.format(1.0 * var[2] / 24, 0);
			gIm.fillText( "    y = " + yString, breedte - 120, 70);
			
			int zint = 100 * (int) Math.round(1.0 * var[3] / 24);
			int zdec = (int) Math.round(100 * 1.0 * var[3] / 24);
			String zString;
			if (Math.abs(zdec - zint) > 0)
				zString = UF.format(1.0 * var[3] / 24, 2);
			else
				zString = UF.format(1.0 * var[3] / 24, 0);
			gIm.fillText( "    z = " + zString, breedte - 120, 90);
			
			
		}
		if (!muisAan)
		{	//gIm.drawString("is gedaan", 20, 50);
			gIm.fillText("is gedaan", 20, 50);
		}
//GWT		
//		gIm.setFont(new Font("Helvetica", Font.PLAIN, 10));
		gIm.setFont(fcString);
		
		if (maakLos)
		{	//gIm.drawString(GeomAlgebra.rb.getString("menuMLLabel"), cursorx, cursory + 30);
//GWT geen cursorx			
			//gIm.fillText("Maak los", 50, 50);
			gIm.fillText(GeomAlgGWT.rb.menuMaakLosLabel(), 50, 50);
		}
	}
	
	public void zetFormuleZichtbaar(boolean b)
	{	formuleZichtbaar = b;
	
		if (constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 25, (breedte - 40) / 3, hoogte - 82 - 25);
			werkbladBigRectangle = new Rectangle(0, 25, (breedte - 40) / 3 + 42, hoogte - 25);
		}
		else if (constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 1, (breedte - 40) / 3, hoogte - 82 - 1);
			werkbladBigRectangle = new Rectangle(0, 1, (breedte - 40) / 3 + 42, hoogte - 1);
		}
		else if (!constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 - 1);
			werkbladBigRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 + 5);
		}
		else if (!constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
			werkbladBigRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
		}
	
		//paint();
	}
	
	public void zetVarWaardeZichtbaar(boolean b)
	{	varWaardeZichtbaar = b;
		//paint();
	}
	
	public void zetOppWaardeZichtbaar(boolean b)
	{	oppWaardeZichtbaar = b;
		//paint();
	}
	
	public void zetConstructieTools(boolean b)
	{	constructieTools = b;
			
		if (constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 25, (breedte - 40) / 3, hoogte - 82 - 25);
			werkbladBigRectangle = new Rectangle(0, 25, (breedte - 40) / 3 + 42, hoogte - 25);
		}
		else if (constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(42, 1, (breedte - 40) / 3, hoogte - 82 - 1);
			werkbladBigRectangle = new Rectangle(0, 1, (breedte - 40) / 3 + 42, hoogte - 1);
		}
		else if (!constructieTools && formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 - 1);
			werkbladBigRectangle = new Rectangle(0, 25, breedte / 3, hoogte - 25 + 5);
		}
		else if (!constructieTools && !formuleZichtbaar)
		{
			werkbladRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
			werkbladBigRectangle = new Rectangle(0, 1, breedte / 3, hoogte - 2);
		}
//GWT
//		if (resetButton != null)
//			resetButton.setVisible(!constructieTools);
		//paint();	
	}
	
	public void zetAlleenOppervlaktes(boolean b)
	{	alleenOppervlaktes = b;
		//paint();
	}
	
	public void zetWerkBlad(boolean b)
	{
		werkblad = b;
		maakFormule();
		//paint();
	}
	
	public void zetOppervlaktesZichtbaar(boolean b)
	{
		oppervlaktesZichtbaar = b;
		//paint();
	}

	public void zetLengtesBreedtesZichtbaar(boolean b)
	{
		lengtesBreedtesZichtbaar = b;
		//paint();
	}
	
	public void zetNegatieveWaarden(boolean b)
	{
		negatieveWaarden = b;
		if (negatieveWaarden)
		{
			basisPosX = new Point(breedte / 2, hoogte - 69);
			basisPosY = new Point(28,(hoogte - 60) / 2);
			basisLijnstukX = new Lijnstuk(0, 3, Lijnstuk.HOR, basisPosX.x, basisPosX.y);
			basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
			basisFiguurX.voegToe(basisLijnstukX);
			basisLijnstukY = new Lijnstuk(0, 3, Lijnstuk.VER, basisPosY.x, basisPosY.y);
			basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
			basisFiguurY.voegToe(basisLijnstukY);
			
		}
		else
		{
			basisPosX = new Point(2 * 24 + 10, hoogte - 69);
			basisPosY = new Point(28, hoogte - 60 - 29);
			basisLijnstukX = new Lijnstuk(0, 3, Lijnstuk.HOR, basisPosX.x, basisPosX.y);
			basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
			basisFiguurX.voegToe(basisLijnstukX);
			basisLijnstukY = new Lijnstuk(0, 3, Lijnstuk.VER, basisPosY.x, basisPosY.y);
			basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
			basisFiguurY.voegToe(basisLijnstukY);
			
		}
		//paint();
	}
	
	public void zetPuzzelen(boolean b)
	{
		puzzelen = b;
		
//GWT		
//		menuSplitsItem.setEnabled(!puzzelen);
//		menuSVItem.setEnabled(!puzzelen);
//		menuVSItem.setEnabled(!puzzelen);
//		menuMLItem.setEnabled(!puzzelen);
//		menuMALItem.setEnabled(!puzzelen);
		
		
		
	}
	
	public void tekenFormule(String f, int x, int y)
	{	int lio = f.lastIndexOf("^");
		
		if(lio!=-1)
		{	int aantalExp = 0;
			int startpunt = 0;
			int expHuidig;
			while(startpunt < lio)
			{	expHuidig = f.indexOf("^",startpunt);
				startpunt = expHuidig+2;
				aantalExp++;
			}
			int[] lengtes = new int[2*aantalExp+1];
			String[] deelformules = new String[2*aantalExp+1];
			aantalExp = 0;
			startpunt = 0;
			
//GWT			
//			Font ft = gIm.getFont();
//			FontMetrics fm = getFontMetrics(ft);
//			Font ftExp = new Font(ft.getName(), Font.BOLD, ft.getSize()*3/5);
//			FontMetrics fmExp = getFontMetrics(ftExp);
			
			while(startpunt < lio)
			{	expHuidig = f.indexOf("^",startpunt);
				deelformules[2*aantalExp] = f.substring(startpunt,expHuidig);
				TextMetrics tm = gIm.measureText(deelformules[2*aantalExp]);
//GWT				
				//lengtes[2*aantalExp] = fm.stringWidth(deelformules[2*aantalExp]);
				lengtes[2*aantalExp] = (int) Math.round(tm.getWidth());
				deelformules[2*aantalExp+1] = f.substring(expHuidig+1,expHuidig+2);
				tm = gIm.measureText(deelformules[2*aantalExp+1]);
//GWT				
				//lengtes[2*aantalExp+1] = fmExp.stringWidth(deelformules[2*aantalExp+1]);
				lengtes[2*aantalExp+1] = (int) Math.round(tm.getWidth());
				startpunt = expHuidig+2;
				aantalExp++;
			}
			deelformules[2*aantalExp] = f.substring(startpunt);
			TextMetrics tm = gIm.measureText(deelformules[2*aantalExp]);
//GWT			
			//lengtes[2*aantalExp] = fm.stringWidth(deelformules[2*aantalExp]);
			lengtes[2*aantalExp] = (int) Math.round(tm.getWidth());
			
			int cx = x;
			int cy = y;
			for(int i=0 ; i<aantalExp ; i++)
			{	
//GWT				
				//gIm.setFont(ft);
				//gIm.drawString(deelformules[2*i],cx,cy);
				gIm.fillText(deelformules[2*i],cx,cy);
				cx += lengtes[2*i]+1;
//GWT				
				//gIm.setFont(ftExp);
//GWT				
				//gIm.drawString(deelformules[2*i+1],cx,cy - ft.getSize()/3);
				gIm.fillText(deelformules[2*i+1],cx,cy - 5);
				
				cx += lengtes[2*i+1];
			}
//GWT			
			//gIm.setFont(ft);
			//gIm.drawString(deelformules[2*aantalExp],cx,cy);
			gIm.fillText(deelformules[2*aantalExp],cx,cy);
		}
		else
		{	//gIm.drawString(f,x,y);
			gIm.fillText(f,x,y);
		}
	}
	
	public void maakFormule()
	{	
		String oudeFormule = formule;
		formule = "";
		
		int[] posities = new int[aantalFg];
		int[] nummers = new int[aantalFg];
		for (int i = 0; i <aantalFg; i++)
		{	posities[i] = fg[i].positie.x;
			nummers[i] = i;
		}
		// sorteer op x positie (bubble)
		for(int i = 0; i < aantalFg; i++)
		{	for (int j = i + 1; j <aantalFg; j++)
			{	if(posities[i] > posities[j])
				{	int res = posities[i];
					posities[i] = posities[j];
					posities[j] = res;
					res = nummers[i];
					nummers[i] = nummers[j];
					nummers[j] = res;
				}
			}
		}
		
		for (int m = 0; m < aantalFg; m++)
		{	int i = nummers[m];
		
			boolean meenemen = true;
			//if (werkblad && werkbladBigRectangle.contains(fg[i].positie.x, fg[i].positie.y))
			if (werkblad && werkbladBigRectangle.contains(fg[i].positie.x, hoogte / 2))
			{	meenemen = false;
//System.out.println("pos x " + i + " = " + fg[i].positie.x);
//System.out.println("pos y " + i + " = " + fg[i].positie.y);			
			}
			
			if (fg[i].aantalx != 0 && fg[i].aantaly != 0 && meenemen)
			{	if ((fg[i].aantalx != 1 || fg[i].aantaly != 1) && formule.equals(""))
					formule += "(";
				else if (fg[i].aantalx != 1 || fg[i].aantaly != 1)
					formule += "+(";
				for (int j = 1; j < fg[i].aantalx + 1; j++)
				{	for (int k = 1; k < fg[i].aantaly + 1; k++)
					{	
						String s = parse(fg[i].lsx[j - 1], fg[i].lsy[k - 1]);
						String minteken = "";
						String plusteken = "";
						if (!s.equals("") && s.charAt(0)== '-')
							minteken = "-";
						if (!formule.equals("") && minteken.equals("") && formule.charAt(formule.length() - 1) !=  '(')
							plusteken = "+";
						formule = formule + plusteken  + s;
					}
				}
				if (fg[i].aantalx != 1 || fg[i].aantaly != 1)
					formule += ")";
			}
		}
		
/*		
		if ((formule.length() > 1) && (formule.charAt(0) == '(') && (formule.charAt(formule.length() - 1) == ')'))
		{
			formule = formule.substring(1);
			formule = formule.substring(0, formule.length() - 1);
		}
*/				
		
		if (gIm !=null) 
		{	
//GWT			
			//gIm.setFont(new Font("Helvetica", Font.PLAIN, 20));
		
		}
		
		oppervlakte = 0;
		for (int i = 0; i < aantalFg; i++)
		{	
			
			boolean meenemen = true;
			//if (werkblad && werkbladBigRectangle.contains(fg[i].positie.x, fg[i].positie.y))
			if (werkblad && werkbladBigRectangle.contains(fg[i].positie.x, hoogte / 2))
				meenemen = false;
			
			if (fg[i].aantalx != 0 && fg[i].aantaly != 0 && meenemen)
			{	double b = 0;
				double h = 0;
				int lengteX = 0;
				for (int j = 0; j < fg[i].aantalx; j++)
				{	for (int k = 0; k < 4; k++)
					{	lengteX += fg[i].lsx[j].lengte[k] * fg[i].lsx[j].varD[k];
					}
				}
				b = 1.0 * lengteX / 24;
				int lengteY = 0;
				for (int j = 0; j < fg[i].aantaly; j++)
				{	for (int k = 0; k < 4; k++)
					{	lengteY += fg[i].lsy[j].lengte[k] * fg[i].lsy[j].varD[k];
					}
				}
				h = 1.0 * lengteY / 24;
				oppervlakte = oppervlakte + b * h;
			}
		}


		if (!formule.equals(oudeFormule))
			owner.changed();
		
	}
	void tekenVarPunt()
	{	if (varHuidig != 0)
		{	int xp = basisFiguurX.lsx[0].d;
			int yp = 0;
			//gIm.setColor(Color.green);
			gIm.setFillStyle(CssColor.make(0,255,0));
//GWT			
			//gIm.fillOval(basisFiguurX.positie.x + xp - 2, basisFiguurX.positie.y - yp - 2, 5, 5);
			gIm.fillRect(basisFiguurX.positie.x + xp - 2, basisFiguurX.positie.y - yp - 2, 5, 5);
			
			xp = 0;
			yp = basisFiguurY.lsy[0].d;
			//gIm.setColor(Color.green);
			gIm.setFillStyle(CssColor.make(0,255,0));
//GWT			
			//gIm.fillOval(basisFiguurY.positie.x + xp - 2, basisFiguurY.positie.y - yp - 2, 5, 5);
			gIm.fillRect(basisFiguurY.positie.x + xp - 2, basisFiguurY.positie.y - yp - 2, 5, 5);
		}
	}
	
	void tekenFiguren()
	{	for (int i = aantalFg - 1; i > -1; i--)
		{	tekenFiguur(fg[i]);
		}
	}
	
	void tekenFiguur(Figuur f)	
	{	f.maxx = f.minx = f.positie.x;
		f.maxy = f.miny = f.positie.y;			 
		int[] posx = new int[f.aantalx + 1];
		int[] posy = new int[f.aantaly + 1];
		posx[0] = f.positie.x;
		posy[0] = f.positie.y;
		
		for (int i = 1; i < f.aantalx + 1; i++)
		{	posx[i] = f.lsx[i - 1].positie.x + f.lsx[i - 1].d;
			if (posx[i] > f.maxx)
				f.maxx = posx[i];
			if (posx[i] < f.minx)
				f.minx = posx[i];
		}
				
		for (int i = 1; i < f.aantaly + 1; i++)
		{	posy[i] = f.lsy[i - 1].positie.y - f.lsy[i - 1].d;
			if (posy[i] > f.maxy)
				f.maxy = posy[i];
			if (posy[i] < f.miny)
				f.miny = posy[i];
		}
		
		f.posx = new Point(posx[f.aantalx], posy[0]);
		f.posy = new Point(posx[0], posy[f.aantaly]) ;
		
		
		for (int i = 0; i < f.aantalx; i++)
		{	if (i == 0)
				f.lsx[i].zetPositie(f.positie.x, f.positie.y);
			else 
				f.lsx[i].zetPositie(f.lsx[i - 1].positie.x + f.lsx[i - 1].d , f.lsx[i - 1].positie.y);
			if (!alleenOppervlaktes)
				tekenLijnstuk(f.lsx[i], f);
			else if (lengtesBreedtesZichtbaar)
				tekenLengtesBreedtes(f.lsx[i], f);
		}
		
		for (int i = 0; i < f.aantaly; i++)
		{	if (i == 0)
				f.lsy[i].zetPositie(f.positie.x, f.positie.y);
			else 
				f.lsy[i].zetPositie(f.lsy[i - 1].positie.x, f.lsy[i - 1].positie.y - f.lsy[i - 1].d );
			if (!alleenOppervlaktes)
				tekenLijnstuk(f.lsy[i], f);
			else if (lengtesBreedtesZichtbaar)
				tekenLengtesBreedtes(f.lsy[i], f);
			
		}
//GWT	
		//gIm.setFont(fl);
		
		for (int j = 1; j < f.aantalx + 1; j++)
		{	for (int k = 1; k < f.aantaly + 1; k++)
			{	
				int dxx = posx[j] - posx[j - 1];
				int dyy = posy[k - 1] - posy[k];
				
				if (dxx > 0 && dyy > 0)
				{	//gIm.setColor(new Color(150,230,150));
					gIm.setFillStyle(CssColor.make(150,230,150));
					gIm.fillRect(posx[j - 1], posy[k], dxx, dyy);
					//gIm.setColor(Color.black);
					gIm.setStrokeStyle(CssColor.make(0,0,0));
					//gIm.drawRect(posx[j - 1], posy[k], dxx, dyy);
					gIm.strokeRect(posx[j - 1], posy[k], dxx, dyy);
				}
				if (dxx < 0 && dyy < 0)
				{	//gIm.setColor(new Color(150,230,150));
					gIm.setFillStyle(CssColor.make(150,230,150));
					gIm.fillRect(posx[j-1] + dxx, posy[k - 1], -dxx, -dyy);
					//gIm.setColor(Color.black);
					gIm.setStrokeStyle(CssColor.make(0,0,0));
					//gIm.drawRect(posx[j-1] + dxx, posy[k - 1], -dxx, -dyy);
					gIm.strokeRect(posx[j-1] + dxx, posy[k - 1], -dxx, -dyy);
				}
				if(dxx<0 && dyy>0)
				{	//gIm.setColor(new Color(255,200,200));
					gIm.setFillStyle(CssColor.make(255,200,200));
					gIm.fillRect(posx[j-1]+dxx,posy[k],-dxx,dyy);
					//gIm.setColor(Color.black);
					gIm.setStrokeStyle(CssColor.make(0,0,0));
					//gIm.drawRect(posx[j-1]+dxx,posy[k],-dxx,dyy);
					gIm.strokeRect(posx[j-1]+dxx,posy[k],-dxx,dyy);
				}
				if(dxx>0 && dyy<0)
				{	//gIm.setColor(new Color(255,200,200));
					gIm.setFillStyle(CssColor.make(255,200,200));
					gIm.fillRect(posx[j-1],posy[k-1],dxx,-dyy);
					//gIm.setColor(Color.black);
					gIm.setStrokeStyle(CssColor.make(0,0,0));
					//gIm.drawRect(posx[j-1],posy[k-1],dxx,-dyy);
					gIm.strokeRect(posx[j-1],posy[k-1],dxx,-dyy);
				}
				
				String s = parse(f.lsx[j-1], f.lsy[k-1]);
//GWT				
				TextMetrics tm = gIm.measureText(s);
				//int w = fcm.stringWidth(s)/2;
				int w = (int) Math.round(tm.getWidth());
				if (oppervlaktesZichtbaar)
				{	gIm.setFillStyle(CssColor.make(0,0,0));
					tekenFormule(s, (posx[j]+posx[j-1])/2-w, (posy[k-1]+posy[k])/2+5);
					
				}
				
			}
		}
	}	

	void tekenLijnstuk(Lijnstuk s, Figuur f)
	{	int x = s.positie.x;
		int y = s.positie.y;
		int d = s.d;
		int dAbs = Math.abs(d);
		int dk = 0;
		if (s.stand == Lijnstuk.HOR) 
			dk = f.positie.y - f.posy.y;
		if (s.stand == Lijnstuk.VER) 
			dk = f.posx.x - f.positie.x;
			
		CssColor kleurBalk, kleurKop, kleurFont;
		if(s.isVar && d>0)
		{	//kleurBalk=new Color(0,150,0);
			//kleurFont=new Color(0,150,0);
			kleurBalk = CssColor.make(0,150,0);
			kleurFont = CssColor.make(0,150,0);
		}
		else if(d<0)
		{	//kleurBalk=Color.red;
			//kleurFont=Color.red;
			kleurBalk = CssColor.make(255,0,0);
			kleurFont = CssColor.make(255,0,0);
		}
		else
		{	//kleurBalk=Color.black;
			//kleurFont=Color.black;
			kleurBalk = CssColor.make(0,0,0);
			kleurFont = CssColor.make(0,0,0);
		}
		if((s==f.lsx[0] && f.aantaly==0) || (s==f.lsy[0] && f.aantalx==0))
		{	//kleurKop=Color.red;
			kleurKop = CssColor.make(255,0,0);
		}
		else
		{	//kleurKop=Color.black;
			kleurKop = CssColor.make(0,0,0);
		}
		if (s.stand == 1)
		{			
			//gIm.setColor(kleurBalk);
			gIm.setFillStyle(kleurBalk);
			gIm.fillRect(x+3+Math.min(0,d), y-1, dAbs-6, 3);
			
			//gIm.setColor(kleurKop);
			gIm.setFillStyle(kleurKop);
		
			//gIm.fillOval(x-4, y-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x,y,4,0,2*Math.PI);
			gIm.fill();
			
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(CssColor.make(0,0,0));
		
			//gIm.drawOval(x-4, y-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x,y,4,0,2*Math.PI);
			gIm.stroke();
			
			//gIm.drawOval(x+d-4, y-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x+d,y,4,0,2*Math.PI);
			gIm.stroke();
			
			
			//gIm.setColor(kleurFont);
			gIm.setFillStyle(kleurFont);
//GWT			
			//gIm.setFont(fl);
			TextMetrics tm = gIm.measureText(s.varNaam);
			//int w = fcm.stringWidth(s.varNaam);
			int w = (int) Math.round(tm.getWidth());
			int corr = 0;
			if (dk < 0 || s == basisLijnstukX)
				corr = -16;
			if (lengtesBreedtesZichtbaar || constructieTools)
			{	//gIm.drawString(s.varNaam, x + d / 2 - w / 2, y + 13 + corr);
				gIm.fillText(s.varNaam, x + d / 2 - w / 2, y + 13 + corr);
			}
			
			Polygon pol = new Polygon();
			pol.addPoint(f.posx.x, f.posx.y);
			if(f.lsx[f.aantalx-1].d>0)
			{	pol.addPoint(f.posx.x-7, f.posx.y-7);
				pol.addPoint(f.posx.x-7, f.posx.y+7);
			}
			else
			{	pol.addPoint(f.posx.x+7, f.posx.y-7);
				pol.addPoint(f.posx.x+7, f.posx.y+7);
			}
			//gIm.fillPolygon(pol);
			gIm.beginPath();
			gIm.moveTo(pol.puntenX[0], pol.puntenY[0]);
			for (int k = 1; k < pol.aantalPunten; k++) 
			{	gIm.lineTo(pol.puntenX[k], pol.puntenY[k]);
			}
			gIm.lineTo(pol.puntenX[0], pol.puntenY[0]);
			gIm.closePath();
			gIm.fill();
		}
		else if (s.stand == 2)
		{	
			//gIm.setColor(kleurBalk);
			gIm.setFillStyle(kleurBalk);
			gIm.fillRect(x-1, y+3-Math.max(0,d), 3,dAbs-6 );
			//gIm.setColor(kleurKop);
			gIm.setFillStyle(kleurKop);
			
			//gIm.fillOval(x-4, y-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x,y,4,0,2*Math.PI);
			gIm.fill();
			
			//gIm.setColor(Color.black);
			gIm.setStrokeStyle(CssColor.make(0,0,0));
			
			//gIm.drawOval(x-4, y-d-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x,y-d,4,0,2*Math.PI);
			gIm.stroke();

			//gIm.drawOval(x-4, y-4, 8, 8);
			gIm.beginPath();
			gIm.arc(x,y,4,0,2*Math.PI);
			gIm.stroke();

			
			//gIm.setColor(kleurFont);
			gIm.setFillStyle(kleurFont);
//GWT			
			//gIm.setFont(fl);
			TextMetrics tm = gIm.measureText(s.varNaam);
			//int w = fcm.stringWidth(s.varNaam);
			int w = (int) Math.round(tm.getWidth());
			int corr = 0;
			if (dk<0 || s == basisLijnstukY)
				corr = 6 + w;
			if (lengtesBreedtesZichtbaar || constructieTools)
			{	//gIm.drawString(s.varNaam, x - w - 3 + corr, y - d / 2 + 5);
				gIm.fillText(s.varNaam, x - w - 3 + corr, y - d / 2 + 5);
			
			}
			
			Polygon pol = new Polygon();
			pol.addPoint(f.posy.x, f.posy.y);
			if(f.lsy[f.aantaly-1].d>0)
			{	pol.addPoint(f.posy.x+7, f.posy.y+7);
				pol.addPoint(f.posy.x-7, f.posy.y+7);
			}
			else
			{	pol.addPoint(f.posy.x+7, f.posy.y-7);
				pol.addPoint(f.posy.x-7, f.posy.y-7);
			}
			//gIm.fillPolygon(pol);
			gIm.beginPath();
			gIm.moveTo(pol.puntenX[0], pol.puntenY[0]);
			for (int k = 1; k < pol.aantalPunten; k++) 
			{	gIm.lineTo(pol.puntenX[k], pol.puntenY[k]);
			}
			gIm.lineTo(pol.puntenX[0], pol.puntenY[0]);
			gIm.closePath();
			gIm.fill();

		}	
	}

	void tekenLengtesBreedtes(Lijnstuk s, Figuur f)
	{	int x = s.positie.x;
		int y = s.positie.y;
		int d = s.d;
		int dAbs = Math.abs(d);
		int dk = 0;
		if (s.stand == Lijnstuk.HOR) 
			dk = f.positie.y - f.posy.y;
		if (s.stand == Lijnstuk.VER) 
			dk = f.posx.x - f.positie.x;
			
		CssColor kleurBalk, kleurKop, kleurFont;
		if(s.isVar && d>0)
		{	//kleurBalk=new Color(0,150,0);
			//kleurFont=new Color(0,150,0);
			kleurBalk = CssColor.make(0,150,0);
			kleurFont = CssColor.make(0,150,0);
		}
		else if(d<0)
		{	//kleurBalk=Color.red;
			//kleurFont=Color.red;
			kleurBalk = CssColor.make(255,0,0);
			kleurFont = CssColor.make(255,0,0);
		}
		else
		{	//kleurBalk=Color.black;
			//kleurFont=Color.black;
			kleurBalk = CssColor.make(0,0,0);
			kleurFont = CssColor.make(0,0,0);
		}
		if((s==f.lsx[0] && f.aantaly==0) || (s==f.lsy[0] && f.aantalx==0))
		{	//kleurKop=Color.red;
			kleurKop = CssColor.make(255,0,0);
		}
		else
		{	//kleurKop=Color.black;
			kleurKop = CssColor.make(0,0,0);
		}
		if (s.stand == 1 && s != basisLijnstukX)
		{	//gIm.setColor(kleurBalk);
			//gIm.fillRect(x+3+Math.min(0,d), y-1, dAbs-6, 3);
			//gIm.setColor(kleurKop);
			//gIm.fillOval(x-4, y-4, 8, 8);
			//gIm.setColor(Color.black);
			//gIm.drawOval(x-4, y-4, 8, 8);
			//gIm.drawOval(x+d-4, y-4, 8, 8);
			
			//gIm.setColor(kleurFont);
			gIm.setFillStyle(kleurFont);
//GWT			
			//gIm.setFont(fl);
			TextMetrics tm = gIm.measureText(s.varNaam);
			//int w = fcm.stringWidth(s.varNaam);
			int w = (int) Math.round(tm.getWidth());
			int corr = 0;
			if (dk < 0 || s == basisLijnstukX)
				corr = -16;
			//gIm.drawString(s.varNaam, x + d / 2 - w / 2, y + 13 + corr);
			gIm.fillText(s.varNaam, x + d / 2 - w / 2, y + 13 + corr);
			
/*			
			Polygon pol = new Polygon();
			pol.addPoint(f.posx.x, f.posx.y);
			if(f.lsx[f.aantalx-1].d>0)
			{	pol.addPoint(f.posx.x-7, f.posx.y-7);
				pol.addPoint(f.posx.x-7, f.posx.y+7);
			}
			else
			{	pol.addPoint(f.posx.x+7, f.posx.y-7);
				pol.addPoint(f.posx.x+7, f.posx.y+7);
			}
*/			
			//gIm.fillPolygon(pol);
		}
		else if (s.stand == 2 && s != basisLijnstukY)
		{	//gIm.setColor(kleurBalk);
			//gIm.fillRect(x-1, y+3-Math.max(0,d), 3,dAbs-6 );
			//gIm.setColor(kleurKop);
			//gIm.fillOval(x-4, y-4, 8, 8);
			//gIm.setColor(Color.black);
			//gIm.drawOval(x-4, y-d-4, 8, 8);
			//gIm.drawOval(x-4, y-4, 8, 8);
			
			//gIm.setColor(kleurFont);
			gIm.setFillStyle(kleurFont);
//GWT			
			//gIm.setFont(fl);
			TextMetrics tm = gIm.measureText(s.varNaam);
			//int w = fcm.stringWidth(s.varNaam);
			int w = (int) Math.round(tm.getWidth());
			int corr = 0;
			if (dk<0 || s == basisLijnstukY)
				corr = 6 + w;
			//gIm.drawString(s.varNaam, x - w - 3 + corr, y - d / 2 + 5);
			gIm.fillText(s.varNaam, x - w - 3 + corr, y - d / 2 + 5);
/*			
			Polygon pol = new Polygon();
			pol.addPoint(f.posy.x, f.posy.y);
			if(f.lsy[f.aantaly-1].d>0)
			{	pol.addPoint(f.posy.x+7, f.posy.y+7);
				pol.addPoint(f.posy.x-7, f.posy.y+7);
			}
			else
			{	pol.addPoint(f.posy.x+7, f.posy.y-7);
				pol.addPoint(f.posy.x-7, f.posy.y-7);
			}
*/			
			//gIm.fillPolygon(pol);
		}	
	}
	
	String parse(Lijnstuk ls1, Lijnstuk ls2)
	{	String s = "";
		if(ls1.geefAantalTermen() > 1 && ls2.geefAantalTermen() > 1)
		{	if(ls1.varNaam.equals(ls2.varNaam))
			{	s = "("+ls1.varNaam+")" + "^2";
			}
			else
			{	s = "("+ls1.varNaam+")" + "("+ls2.varNaam+")";
			}
			return s;
		}
		else if(ls1.geefAantalTermen() > 1)
		{	s = ls2.varNaam + "("+ls1.varNaam+")";
			if(ls2.varNaam.equals("1"))s = "("+ls1.varNaam+")";
			if(ls2.varNaam.equals("-1"))s = "-("+ls1.varNaam+")";
			return s;
		}
		else if(ls2.geefAantalTermen() > 1)
		{	s = ls1.varNaam + "("+ls2.varNaam+")";
			if(ls1.varNaam.equals("1"))s = "("+ls2.varNaam+")";
			if(ls1.varNaam.equals("-1"))s = "-("+ls2.varNaam+")";
			return s;
		}
		else
		{	String[] varNamen= {"", "x", "y", "z"};
			int getal = ls1.lengte[ls1.geefTermNr()]*ls2.lengte[ls2.geefTermNr()];
			String getalString = Integer.toString(getal);
			String letters1 = varNamen[ls1.geefTermNr()];
			String letters2 = varNamen[ls2.geefTermNr()];
			String letters = letters1+letters2;
			if (getal==1 && !letters.equals(""))getalString = "";
			if (getal==-1 && !letters.equals(""))getalString = "-";
			if(ls1.geefTermNr() < ls2.geefTermNr()) s = getalString + letters1 + letters2;
			else if(ls1.geefTermNr() > ls2.geefTermNr()) s = getalString + letters2 + letters1;
			else if(ls1.geefTermNr() == 0 && ls2.geefTermNr() == 0) s = getalString;
			else s = getalString + letters1 + "^2";
			return s;
		}
	}
	
	void maakBasisNegatief()
	{	basisLijnstukX.maakNegatief();
		basisLijnstukY.maakNegatief();
		varTek[varHuidig] = -varTek[varHuidig];
		tekenOpnieuw();
	}
	void zetBasis(int lengte)
	{	basisLijnstukX = new Lijnstuk(0, lengte, basisLijnstukX.stand, basisPosX.x, basisPosX.y);
		basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
		basisFiguurX.voegToe(basisLijnstukX);
		basisLijnstukY = new Lijnstuk(0, lengte, basisLijnstukY.stand, basisPosY.x, basisPosY.y);
		basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
		basisFiguurY.voegToe(basisLijnstukY);
		zetVars();
		varHuidig = 0;
		tekenOpnieuw();
	}
	
	void zetVarBasis(int varnr)
	{	
		basisLijnstukX = new Lijnstuk(varnr, 1, basisLijnstukX.stand,basisPosX.x, basisPosX.y);
		basisLijnstukX.zetVar(varnr, var[varnr]);
		basisFiguurX = new Figuur(basisPosX.x, basisPosX.y);
		basisFiguurX.voegToe(basisLijnstukX);
		basisLijnstukY = new Lijnstuk(varnr, 1, basisLijnstukY.stand,basisPosY.x, basisPosY.y);
		basisLijnstukY.zetVar(varnr, var[varnr]);
		basisFiguurY = new Figuur(basisPosY.x, basisPosY.y);
		basisFiguurY.voegToe(basisLijnstukY);
		varTek[varnr] = 1;
		varHuidig = varnr;
		tekenOpnieuw();
	}
	void pasAanVar(int varnr, int waarde)
	{	if (varTek[varnr] == 1)
		 var[varnr] += waarde;
		else 
		 var[varnr] -=waarde;
		
		basisLijnstukX.zetVar(varnr, var[varnr]);
		basisLijnstukY.zetVar(varnr, var[varnr]);
		
		for(int i=0 ; i<aantalFg ; i++)
		{	fg[i].pasAanVar(varnr, var[varnr]);
		}
		tekenOpnieuw();
	}
	void zetVars()
	{	for (int i = 1; i < 4; i++)
		{	for (int j = 0; j < aantalFg; j++)
			{	fg[j].pasAanVar(i, var[i]);
			}
		}
	}
	void zetDirectOptellen(boolean b)
	{	if(b)gesplitst = false;
		else gesplitst = true;
	}
	
	void wis()
	{	aantalFg = 0;
		maakFormule();
		buffer.voegToe(new State(aantalFg,fg,var));		
		tekenOpnieuw();
	}
	void maakOngedaan()
	{	State vs = buffer.geefVorigeState();
		if(vs==null)return;
		else
		{	aantalFg = vs.geefAantalFiguren();
			fg = vs.geefFigurenRij();
			var = vs.geefVars();
			basisLijnstukX.zetVar(varHuidig, var[varHuidig]);
			basisLijnstukY.zetVar(varHuidig, var[varHuidig]);
			maakFormule();
			tekenOpnieuw();
		}
	}
	void setState(State s)
	{	if (s == null)
		{	
//testString += " null";
			return;
		}
		else
		{	aantalFg = s.geefAantalFiguren();

owner.logger.info("aantalFg = " + aantalFg);
			fg = s.geefFigurenRij();
			var = s.geefVars();
			basisLijnstukX.zetVar(varHuidig, var[varHuidig]);
			basisLijnstukY.zetVar(varHuidig, var[varHuidig]);
			maakFormule();
			buffer.voegToe(new State(aantalFg,fg,var));
		}
	}
	
/*	
	void setState(String s)
	{	
		Object o = StringCodeObject.decodeStringToObject(s);
		if (o == null)
			return;
		State state = (State) o;
		if (state != null) 
			setState(state);
	}
*/	
	State getStateState()
	{	State state = buffer.geefHuidigeState();
//System.out.println("get af = " + state.geefAantalFiguren());	
		return state;
	}
	
/*	
	String getState()
	{	State state = buffer.geefHuidigeState();
		String s = StringCodeObject.encodeObjectToString(state);
		return s;
	}
*/			
	void breekFiguur(Figuur f)
	{	
//System.out.println("break started");		
//System.out.println("aantalx = " + f.aantalx);
//System.out.println("aantaly = " + f.aantaly);
		for (int i = 0; i < f.aantalx; i++)
		{	for (int j = 0; j < f.aantaly; j++)
			{	Figuur fn = new Figuur(f.lsx[i].positie.x, f.lsy[j].positie.y);
				fn.minx = fn.maxx = f.lsx[i].positie.x;
				fn.miny = fn.maxy = f.lsy[j].positie.y;
				Lijnstuk lstx = new Lijnstuk(f.lsx[i]);
				Lijnstuk lsty = new Lijnstuk(f.lsy[j]);
				fn.voegToe(lstx);
				fn.voegToe(lsty);
				fn.veranderPositie(6*i, -6*j);
				fgNieuw[aantalFgNieuw] = fn;
				aantalFgNieuw++;
			}
		}
//System.out.println("break finished");	
	}
	void breekFiguurInTwee(Figuur f, int x, int y)
	{	for(int i=1 ; i<f.aantalx ; i++)
		{	if(f.raakLijnX(i,x,y))
			{	Figuur fn = new Figuur(f.lsx[0].positie.x, f.lsx[0].positie.y);
				fn.minx = fn.maxx = f.lsx[0].positie.x;
				fn.miny = fn.maxy = f.lsx[0].positie.y;
				for(int k=0 ; k<i ; k++)
				{	Lijnstuk lstx = new Lijnstuk(f.lsx[k]);
					fn.voegToe(lstx);
				}
				for(int k=0 ; k<f.aantaly ; k++)
				{	Lijnstuk lsty = new Lijnstuk(f.lsy[k]);
					fn.voegToe(lsty);
				}
				fgNieuw[aantalFgNieuw] = fn;
				aantalFgNieuw++;
				
				fn = new Figuur(f.lsx[i].positie.x, f.lsx[i].positie.y);
				fn.minx = fn.maxx = f.lsx[i].positie.x;
				fn.miny = fn.maxy = f.lsx[i].positie.y;
				for(int k=i ; k<f.aantalx ; k++)
				{	Lijnstuk lstx = new Lijnstuk(f.lsx[k]);
					fn.voegToe(lstx);
				}
				for(int k=0 ; k<f.aantaly ; k++)
				{	Lijnstuk lsty = new Lijnstuk(f.lsy[k]);
					fn.voegToe(lsty);
				}
				fn.veranderPositie(6,0);
				fgNieuw[aantalFgNieuw] = fn;
				aantalFgNieuw++;
				return;
			}
		}
		for(int i=1 ; i<f.aantaly ; i++)
		{	if(f.raakLijnY(i,x,y))
			{	Figuur fn = new Figuur(f.lsy[0].positie.x, f.lsy[0].positie.y);
				fn.minx = fn.maxx = f.lsy[0].positie.x;
				fn.miny = fn.maxy = f.lsy[0].positie.y;
			
				for(int k=0 ; k<f.aantalx ; k++)
				{	Lijnstuk lstx = new Lijnstuk(f.lsx[k]);
					fn.voegToe(lstx);
				}
				for(int k=0 ; k<i ; k++)
				{	Lijnstuk lsty = new Lijnstuk(f.lsy[k]);
					fn.voegToe(lsty);
				}
				fgNieuw[aantalFgNieuw] = fn;
				aantalFgNieuw++;
				
				fn = new Figuur(f.lsy[i].positie.x, f.lsy[i].positie.y);
				fn.minx = fn.maxx = f.lsy[i].positie.x;
				fn.miny = fn.maxy = f.lsy[i].positie.y;
				
				for(int k=0 ; k<f.aantalx ; k++)
				{	Lijnstuk lstx = new Lijnstuk(f.lsx[k]);
					fn.voegToe(lstx);
				}
				for(int k=i ; k<f.aantaly ; k++)
				{	Lijnstuk lsty = new Lijnstuk(f.lsy[k]);
					fn.voegToe(lsty);
				}
				fn.veranderPositie(0,-6);
				fgNieuw[aantalFgNieuw] = fn;
				aantalFgNieuw++;
				return;
			}
		}
	}
	
	public void zetMuisAan(boolean b)
	{	muisAan = b;
		tekenOpnieuw();
	}
	
	boolean zoekEnKlikVast()
	{	for(int i=1 ; i<aantalFg ; i++)
		{	if(actiefFg!=null && Figuur.past(fg[i],actiefFg))
			{	fg[i] = Figuur.verbind(fg[i],actiefFg,!gesplitst);
				if(fg[i].aantalx==0 && fg[i].aantaly==0)
				{	for(int j=i ; j<aantalFg-1 ; j++)
					{	fg[j]= fg[j+1];
						
					}
					aantalFg--;
				}
				actiefFg = fg[i];
				fg[0] = fg[i];
				for(int j=i ; j<aantalFg-1 ; j++)
				{	fg[j] = fg[j+1];
				}
				aantalFg--;
				//zetVars();
				maakFormule();
				tekenOpnieuw();
				return true;
			}
		}
		return false;
	}
	
	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	if (!muisAan)
		{	return;
		}

		laatstex = eventX;
		laatstey = eventY;
		drukx = eventX;
		druky = eventY;
		
		press = true;
        taptime = System.currentTimeMillis();
        doubletap.add(taptime);

//GWT
/*		
		for (int i = 0; i < aantalFg; i++)
		{	if ((e.getModifiers()== e.BUTTON3_MASK || e.isControlDown()) && 
				(fg[i].raakRechthoek(e.getX(),e.getY()) || fg[i].raakKop(e.getX(),e.getY())))
			{	popup.show(this, e.getX(), e.getY());
//System.out.println("popup.show");
			}
		}
*/		
		
		selectFiguur  = null;
		
		if (!alleenOppervlaktes)
		{	if (varHuidig != 0 && basisFiguurX.raakStaartX(eventX,eventY))
			{	veranderVar = true;
				basisLijnstuk = basisLijnstukX;
//System.out.println("raakStaartX");				
				return;
			}
			if (varHuidig != 0 && basisFiguurY.raakStaartY(eventX,eventY))
			{	veranderVar = true;
				basisLijnstuk = basisLijnstukY;
//System.out.println("raakStaartY");				
				return;
			}
					
			if (basisFiguurX.raakKop(eventX,eventY) || basisFiguurX.raakRechthoek(eventX,eventY))
			{	maakLos = false;
				pak = true;
				basisFiguur = basisFiguurX;
//System.out.println("raakKopX");				
				return;
			}
			if (basisFiguurY.raakKop(eventX,eventY) || basisFiguurY.raakRechthoek(eventX,eventY))
			{	maakLos = false;
				pak = true;
				basisFiguur = basisFiguurY;
//System.out.println("raakKopY");				
				return;
			}
		}
		
		for (int i = 0; i <aantalFg; i++)
		{	if ((fg[i].aantalx == 0 || fg[i].aantaly == 0) && fg[i].raakKop(eventX,eventY))
			{	maakLos = false;
				selectFiguur  = fg[i];
				actiefFg = fg[i];
				for (int j = i; j > 0; j--)
				{	fg[j] = fg[j - 1];
				}
				fg[0] = actiefFg;
				tekenOpnieuw();
				return;
			}
		}
		
		if (!alleenOppervlaktes)
		{	
			
			for (int i = 0; i < aantalFg; i++)
			{	
//System.out.println("raakSplits");

				if (fg[i].raakSplits(eventX,eventY))
				{	maakLos = false;
					fg[aantalFg] = fg[i].splitsLijnstukAf(eventX, eventY);
					actiefFg = fg[aantalFg];
					for (int j = aantalFg; j > 0; j--)
					{	fg[j] = fg[j - 1];
					}
					fg[0] = actiefFg;
					aantalFg++;
					tekenOpnieuw();
					return;
				}
			}
		
		}
		for (int i = 0; i < aantalFg; i++)
		{	
			
			if (fg[i].raakRechthoek(eventX,eventY))
			{	selectFiguur = fg[i];
				actiefFg = fg[i];
				for (int j = i; j > 0; j--)
				{	fg[j] = fg[j - 1];
				}
				fg[0] = actiefFg;
				
				if (maakLos && actiefFg.raakLijn(eventX,eventY))
				{	for (int j = 0; j < aantalFg; j++)
					{	fg[j]=fg[j + 1];
					}
					aantalFg--;
					aantalFgNieuw = aantalFg;
					int aantalRes = aantalFg;
					
					breekFiguurInTwee(actiefFg, eventX, eventY);
					
					aantalFg = aantalFgNieuw;
					for (int j = aantalRes; j < aantalFgNieuw; j++)
					{	fg[j] = fgNieuw[j];
					}
					actiefFg = null;
				}
				else 
					maakLos = false;
				maakFormule();
				tekenOpnieuw();
				return;
			}
		}
	}	
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	if (!muisAan)
			return;
	
		int dx = eventX - laatstex;
		int dy = eventY - laatstey;
		
		if ((dx != 0) || (dy != 0))
			dragging = true;
		
		if (pak)
		{	
//System.out.println("pak");			
			
			fg[aantalFg]= new Figuur(basisFiguur);
			actiefFg = fg[aantalFg];
			for(int j=aantalFg ; j>0 ; j--)
				{	fg[j] = fg[j-1];
				}
				fg[0] = actiefFg;
			aantalFg++;
			pak = false;
		}
		if(actiefFg != null )
			actiefFg.veranderPositie(dx,dy);
		
		
		if (veranderVar)
		{	if(basisLijnstuk.stand==Lijnstuk.HOR)pasAanVar(varHuidig,dx);
			else pasAanVar(varHuidig,-dy);
			maakFormule();
		}
		
		tekenOpnieuw();
		
		laatstex = eventX;
		laatstey = eventY;
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	if (!muisAan)
		{	return;
		}
		
/*	
		if (isDoubleClick()) 
		{
			doubletap.clear();
			return;
		} 
		else
*/		 
		if (isLongClick()) 
		{
			if (!dragging)
			{
				showPopupMenu(drukx,druky);
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
		
		pak = false;
		if (veranderVar)
		{	if(var[varHuidig]>breedte/2-20)
				var[varHuidig]=breedte/2-20;
			if(var[varHuidig]<-breedte/2+20) 
				var[varHuidig]=-breedte/2+20;
			int x = var[varHuidig]+900;
			int ex = x%6;
			if(ex<3)pasAanVar(varHuidig,-ex);
			else pasAanVar(varHuidig,6-ex);
		}
		veranderVar = false;

		if (actiefFg!=null)
		{	actiefFg.plaatsOpGrid();
//System.out.println("actiefFg != null");		
		}
		
		
		if (constructieTools && actiefFg != null && 
			//!new Rectangle(42, 0, breedte-43, hoogte-82).contains(eventX,eventY))
			((actiefFg.minx < 42) || (actiefFg.miny < 0) || (actiefFg.maxx > breedte-43) || (actiefFg.maxy > hoogte-82)))	
		{	
			
//System.out.println("mouseUp weggooien");			
			actiefFg = null;
			fg[0] = null;
			for(int j=0 ; j<aantalFg-1 ; j++)
			{	fg[j] = fg[j+1];
			}
			aantalFg--;
			maakFormule();
			tekenOpnieuw();
			buffer.voegToe(new State(aantalFg,fg,var));
			return;
		}
		

		//boolean b = true;
		boolean b = !puzzelen;
		while(b)
		{	b = zoekEnKlikVast();
		}
		
		actiefFg = null;
		maakFormule();
		tekenOpnieuw();	
		buffer.voegToe(new State(aantalFg,fg,var));
		
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

//	public void mouseClicked(MouseEvent e){;}
	
/*
//GWT ???  	
	public void mouseMoved(MouseEvent e)
	{	if(maakLos)
		{	cursorx = e.getX();
			cursory = e.getY();
			tekenOpnieuw();
		}
	}
*/	
	
	
// dit is voor het menu
//GWT
	
	public void menuAction(String s)
	{
	
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuDraaiLabel")))
		if (s.equals("draai"))
		{	for(int i=0 ; i<aantalFg ; i++)
			{	if(fg[i]==selectFiguur)
				{	fg[i]=fg[i].draai();
					fg[i].veranderPositie(0,0);
				}
			}
		}
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuKopieerLabel")))
		if (s.equals("kopieer"))
		{	for(int i=0 ; i<aantalFg ; i++)
			{	if(fg[i]==selectFiguur)
				{	Figuur fn = fg[i].dupliceer();
					fn.veranderPositie(20,-20);
					fg[aantalFg] = fn;
					Figuur fRes = fg[aantalFg];
					for(int j=aantalFg ; j>0 ; j--)
					{	fg[j] = fg[j-1];
					}
					fg[0] = fRes;
					aantalFg++;
					break;
				}
			}
		}
		
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuSplitsLabel")))
		if (s.equals("splits"))	
		{	for(int i=0 ; i<aantalFg ; i++)
			{	if(fg[i]==selectFiguur)fg[i]=fg[i].splits();
				
			}
		}
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuSVLabel")))
		if (s.equals("splitsvolledig"))
		{	for(int i=0 ; i<aantalFg ; i++)
			{	if(fg[i]==selectFiguur)fg[i]=fg[i].splitsVolledig();
				
			}
		}
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuVSLabel")))
		if (s.equals("voegsamen"))
		{	for(int i=0 ; i<aantalFg ; i++)
			{	if(fg[i]==selectFiguur)
				{	fg[i].maakGeheel();
					if(fg[i].aantalx==0 && fg[i].aantaly==0)
					{	for(int j=i ; j<aantalFg-1 ; j++)
						{	fg[j]= fg[j+1];
						}
						aantalFg--;
					}
				}
			}
		}
		//if(((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuMLLabel")))
		if (s.equals("maaklos"))	
		{	maakLos = true;
		}
		
		//if (((JMenuItem)e.getSource()).getLabel().equals(GeomAlgebra.rb.getString("menuMALLabel")))
		if (s.equals("maakalleslos"))
		{	for(int i = 0; i < aantalFg; i++)
			{
			
				if (fg[i] == selectFiguur)
				{	for (int j = i; j < aantalFg; j++)
					{	fg[j] = fg[j + 1];
					}
					aantalFg--;
					aantalFgNieuw = aantalFg;
					int aantalRes = aantalFg;
					
					breekFiguur(selectFiguur);
					
					aantalFg = aantalFgNieuw;
					for (int j = aantalRes; j < aantalFgNieuw; j++)
					{	fg[j] = fgNieuw[j];
					}
				}
				
			}
		}
		selectFiguur = null;
		zetVars();
		maakFormule();
		tekenOpnieuw();	
		
		buffer.voegToe(new State(aantalFg,fg,var));
		
		menuPopup.setVisible(false);
	
	
	}		
}

