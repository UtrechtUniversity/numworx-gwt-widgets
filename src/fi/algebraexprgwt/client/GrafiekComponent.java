package fi.algebraexprgwt.client;

//import java.awt.*;
//import java.awt.event.*;
//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;
import java.util.HashMap;

import fi.algebraexprgwt.client.expressies_ap.*;
//import fi.algebraexpressies.schuifobjects.*;

//import javax.swing.*;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;


public class GrafiekComponent extends AlgebraSchuifComponent 
							  //implements ActionListener, MouseListener, MouseMotionListener
{
  	private int eenheid = 16;

//GWT  	
  	//private PlusMinKnop pmKnopY,pmKnopX; 
	private ZoomKnop zoomInX, zoomUitX, zoomInY, zoomUitY, zoomIn, zoomUit, zoomStandaard; 
	
	 private Expressie[] expressies;
	 private int maxAantalExpressies;
	 private int aantalExpressies;
	 
	 private boolean gevuld;
	 private int beginwaarde;
	 private int selectnummer;
	 private String varNaam = "qq";
	 private String formuleNaam;
	 private int xmin, xmax, ymin, ymax;
	 private double beginx, beginy;
	 private int veldx, veldy, veldb, veldh;
	 private int eenheidx, eenheidy;
	 private double eenheidxD, eenheidyD;
	 private double schaalFactorY;
	 private int factorRijNummerY;
	 private double schaalFactorX;
	 private int factorRijNummerX;
//GWT	 
	 //private ZoomDraad zoomDraad;
	 
	 int startxv = 0;
	 int startyv = 0;
	 int startxrs = 0;
	 int startyrs = 0;
	 
	private GrafiekVeld gv;
	
	//private DecimalFormatSymbols dfs;
	//private DecimalFormat df, dfTrace;
	//private FontMetrics fm;
		
	Pijl[] pijlenIn;
	int aantalPijlenIn;
		
	private boolean[] isPuntGrafiek;
	private boolean[] isMeerPuntenGrafiek;
	private boolean[] isLijnGrafiek;
	private double[] puntXWaarde;
	 
	//private Font font = new Font("SansSerif", Font.PLAIN, 10);
	String fontString = "10px, sans-serif";
	 
	private boolean resize;
	private boolean trace=false;
	private boolean tracing=false;
	
//GWT	
	//private Slider slider;
	
	private int tracex=-2;
	private double tracexD = tracex;
//GWT
	//private JCheckBox traceCheckbox;
//GWT	
	//private JPopupMenu popup;
	private boolean kettingZichtbaar = true;
	private int movex, movey;
	 
	private CssColor[] colors;
	private CssColor traceKleur = CssColor.make(255,0,0); //red;
		
	
    public GrafiekComponent(AlgebraSchuifVeld sv,int x, int y, int b, int h)
	{	
		super(1,sv,x,y,b,h);

		maxAantalExpressies = 10;
		isStapel = false;
		expressies = new Expressie[maxAantalExpressies];
		aantalExpressies = 0;
		
		pijlenIn = new Pijl[maxAantalExpressies];
		aantalPijlenIn = 0;
		
		isPuntGrafiek = new boolean[10];
		isMeerPuntenGrafiek = new boolean[10];
		puntXWaarde = new double[10];
		isLijnGrafiek = new boolean[10];
		
		beginwaarde = 0;
		selectnummer = 999;
		xmin = 0; 
		ymin = 0;
		xmax = 10;
		ymax = 10;
		eenheidx = eenheid;
		eenheidy = eenheid;
		eenheidxD = eenheid;
		eenheidyD = eenheid;
		beginx = eenheidx;
		beginy = eenheidy;
		veldx = 30;
		veldy = 50;
		veldb = b-50;//160;
		veldh = h-85;//160;
		schaalFactorY = 1;
		factorRijNummerY = 99;
		schaalFactorX = 1;
		factorRijNummerX = 99;
		varNaam = "qq";
		formuleNaam = "";
		
		//dfs = new DecimalFormatSymbols();
		//dfs.setDecimalSeparator('.');
		//df = new DecimalFormat("0.####", dfs);
		//dfTrace = new DecimalFormat("0.##",dfs);
		
		//gv = new GrafiekVeld(veldx,veldy,veldb,veldh);
		gv = new GrafiekVeld(xPos+veldx,yPos+veldy,veldb,veldh);
		//gv.addMouseListener(this);
		//gv.addMouseMotionListener(this);
		
		//add(gv);
		
		zoomStandaard = new ZoomKnop("standaard",xPos+22,yPos+12,25,25,asv.asvContext2d);
		//zoomStandaard = new ZoomKnop("standaard");
		//zoomStandaard.setBounds(22,12,25,25);
		//zoomStandaard.addActionListener(this);
		//zoomStandaard.setToolTip("Standaard weergave");
		//add(zoomStandaard);

		zoomIn	= new ZoomKnop("zoomin",xPos+47,yPos+12,25,25,asv.asvContext2d);
		//zoomIn	= new ZoomKnop("zoomin");
		//zoomIn.setBounds(47,12,25,25);
		//zoomIn.addActionListener(this);
		//zoomIn.setToolTip("Zoom in");
		//add(zoomIn);
		
		zoomUit	= new ZoomKnop("zoomuit",xPos+72,yPos+12,25,25,asv.asvContext2d);
		//zoomUit	= new ZoomKnop("zoomuit");
		//zoomUit.setBounds(72,12,25,25);
		//zoomUit.addActionListener(this);
		//zoomUit.setToolTip("Zoom uit");
		//add(zoomUit);	
		
		zoomInX	= new ZoomKnop("zoominx",xPos+97,yPos+12,25,25,asv.asvContext2d);
		//zoomInX	= new ZoomKnop("zoominx");
		//zoomInX.setBounds(97,12,25,25);
		//zoomInX.addActionListener(this);
		//zoomInX.setToolTip("Zoom in horizontaal");
		//add(zoomInX);
		
		zoomUitX= new ZoomKnop("zoomuitx",xPos+122,yPos+12,25,25,asv.asvContext2d);		
		//zoomUitX= new ZoomKnop("zoomuitx");
		//zoomUitX.setBounds(122,12,25,25);
		//zoomUitX.addActionListener(this);
		//zoomUitX.setToolTip("Zoom uit horizontaal");
		//add(zoomUitX);
		
		zoomInY	= new ZoomKnop("zoominy",xPos+147,yPos+12,25,25,asv.asvContext2d);
		//zoomInY	= new ZoomKnop("zoominy");
		//zoomInY.setBounds(147,12,25,25);
		//zoomInY.addActionListener(this);
		//zoomInY.setToolTip("Zoom in vertikaal");
		//add(zoomInY);
		
		zoomUitY= new ZoomKnop("zoomuity",xPos+172,yPos+12,25,25,asv.asvContext2d);
		//zoomUitY= new ZoomKnop("zoomuity");
		//zoomUitY.setBounds(172,12,25,25);
		//zoomUitY.addActionListener(this);
		//zoomUitY.setToolTip("Zoom uit vertikaal");
		//add(zoomUitY);
		
//GWT		
/*		
		slider = new Slider(veldb,0);
		slider.setLocation(veldx-5,h-13);
		slider.addActionListener(this);
		slider.setBackground(new Color(210,210,210));
		slider.setVisible(trace);
		add(slider);
*/
		
//GWT
/*		
		traceCheckbox = new JCheckBox();
		traceCheckbox.setBounds(0,getSize().height-13,17,10);
		traceCheckbox.setBackground(Color.white);
		traceCheckbox.addActionListener(this);
		add(traceCheckbox);
		traceCheckbox.setOpaque(false);
*/		
		
//GWT		
/*		
		popup = new JPopupMenu();
		
		JMenuItem mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label5"));
		mi.addActionListener(this);
		popup.add(mi);
		
		mi = new JMenuItem(AlgebraExpressies.rb.getString("popup1Label6"));
		mi.addActionListener(this);
		popup.add(mi);
		
		add(popup);
*/
/*		
		colors = new Color[10];
		colors[0] = new Color(0,0,255);
		colors[1] = new Color(0,200,0);
		colors[2] = new Color(255,50,50);
		colors[3] = new Color(00,220,220);
		colors[4] = new Color(220,0,220);
		colors[5] = new Color(200,200,0);
		colors[6] = Color.black;
		colors[7] = Color.black;
		colors[8] = Color.black;
		colors[9] = Color.black;
*/
		colors = new CssColor[10];
		colors[0] = CssColor.make(0,0,255);
		colors[1] = CssColor.make(0,200,0);
		colors[2] = CssColor.make(255,50,50);
		colors[3] = CssColor.make(00,220,220);
		colors[4] = CssColor.make(220,0,220);
		colors[5] = CssColor.make(200,200,0);
		CssColor black = CssColor.make(0,0,0);
		colors[6] = black;
		colors[7] = black;
		colors[8] = black;
		colors[9] = black;

        
	}

	public void setSize(int b, int h)
	{	
		super.setSize(b,h);
		veldb = b-50;
		veldh = h-85;
		gv.setSize(veldb,veldh);
//GWT		
		//slider.zetLengte(veldb);
		//slider.setLocation(veldx-5,h-13);
		//traceCheckbox.setBounds(0,getSize().height-13,17,10);
	}
    
	public HashMap getState()
	{	int sizeB = 0;
		int sizeH = 0;
		boolean trace = false;
		double tracexD = 0;
		double beginy = 0;
		boolean kettingZichtbaar = true;
		double schaalFactorY  = 1;
		int factorRijNummerY = 99;
							
		//sizeB = getSize().width;
		//sizeH = getSize().height;
		sizeB = breedte;
		sizeH = hoogte;

		trace = this.trace;
		tracexD = this.tracexD;
		beginy = this.beginy;
		kettingZichtbaar = this.kettingZichtbaar;
		schaalFactorY = this.schaalFactorY;
		factorRijNummerY = this.factorRijNummerY;
		
		HashMap h = super.getState();
		
	    h.put("sizeB", new Integer(sizeB));
	    h.put("sizeH", new Integer(sizeH));
	    h.put("trace", new Boolean(trace));
	    h.put("tracexD", new Double(tracexD));
	    h.put("beginy", new Double(beginy));
	    h.put("kettingZichtbaar", new Boolean(kettingZichtbaar));
	    h.put("schaalFactorY", new Double(schaalFactorY));
	    h.put("factorRijNummerY", new Integer(factorRijNummerY));
	    return h;
	}
	
	public void setState(HashMap h)
    {	int sizeB = 0;
		int sizeH = 0;
		boolean trace = false;
		double tracexD = 0;
		double beginy = 0;
		boolean kettingZichtbaar = true;
		double schaalFactorY  = 1;
		int factorRijNummerY = 99;
		
		if (h.containsKey("sizeB")) sizeB = ((Integer)h.get("sizeB")).intValue();
    	if (h.containsKey("sizeH")) sizeH = ((Integer)h.get("sizeH")).intValue();
    	if (h.containsKey("trace")) trace = ((Boolean)h.get("trace")).booleanValue();
    	if (h.containsKey("tracexD")) tracexD = ((Double)h.get("tracexD")).doubleValue();
    	if (h.containsKey("beginy")) beginy = ((Double)h.get("beginy")).doubleValue();
    	if (h.containsKey("kettingZichtbaar")) kettingZichtbaar = ((Boolean)h.get("kettingZichtbaar")).booleanValue();
    	if (h.containsKey("schaalFactorY")) schaalFactorY = ((Double)h.get("schaalFactorY")).doubleValue();
    	if (h.containsKey("factorRijNummerY")) factorRijNummerY = ((Integer)h.get("factorRijNummerY")).intValue();
    	
		
		setSize(sizeB,sizeH);
		
		this.trace = trace;
		this.tracexD = tracexD;
		this.beginy = beginy;
		tracex = (int)Math.round(tracexD);
//GWT		
		//slider.zetStand(tracex);
		
		this.kettingZichtbaar = kettingZichtbaar;
		if(!kettingZichtbaar)zetBoomZichtbaarHier(kettingZichtbaar);
		this.schaalFactorY = schaalFactorY;
		this.factorRijNummerY = factorRijNummerY;

//GWT		
		//traceCheckbox.setSelected(trace);
		//slider.setVisible(trace);
		
    }
	
	//public void drawDottedLine(Graphics g, int x0, int y0, int x1, int y1)
	public void drawDottedLine(Context2d g, int x0, int y0, int x1, int y1)
	{
		int dx = 3;
		double length = Math.sqrt((double)((x1-x0)*(x1-x0)) + (double)((y1-y0)*(y1-y0)));
//System.out.println(""+length);
		int n = (int)Math.round(length/dx);
//System.out.println(""+n);
		for(int i=0 ; i<n ; i+=2)
		{
			int xn0 = x0 + (int)Math.round((double)(x1-x0)*i/n);
			int yn0 = y0 + (int)Math.round((double)(y1-y0)*i/n);
			int xn1 = x0 + (int)Math.round((double)(x1-x0)*(i+1)/n);
			int yn1 = y0 + (int)Math.round((double)(y1-y0)*(i+1)/n);
			
			//g.drawLine(xn0,yn0,xn1,yn1);
			g.beginPath();
			g.moveTo(xPos+xn0, yPos+yn0);
			g.lineTo(xPos+xn1, yPos+yn1);
			g.stroke();

			
		}
	}
	
	//public void paint(Graphics g)
	public void paint(Context2d g)
	{	
		//g.setFont(font);
		g.setFont(fontString);
	
		//int breedte = getSize().width;
		//int hoogte = getSize().height;
		
		
		//g.setColor(new Color(210,210,210));
		//g.fillRect(0,10,breedte - 1,hoogte - 11);
		//g.setColor(Color.black);
		//g.drawRect(0,10,breedte-1,hoogte - 11);
		
		g.setFillStyle(CssColor.make(210,210,210));
		g.fillRect(xPos+0,yPos+10,breedte-1,hoogte - 11);
		g.setStrokeStyle(CssColor.make(0,0,0));
		g.strokeRect(xPos+0,yPos+10,breedte-1,hoogte - 11);
				
		//g.setColor(Color.white);
		//g.drawLine(breedte-10, hoogte-2, breedte-2, hoogte-10);
		//g.drawLine(breedte-7, hoogte-2, breedte-2, hoogte-7);
		
		g.setStrokeStyle(CssColor.make(255,255,255));
		g.beginPath();
		g.moveTo(xPos+breedte-10, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-10);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+breedte-7, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-7);
		g.stroke();

		//g.setColor(Color.gray.darker());
		//g.drawLine(breedte-9, hoogte-2, breedte-2, hoogte-9);
		//g.drawLine(breedte-6, hoogte-2, breedte-2, hoogte-6);
		
		g.setStrokeStyle(CssColor.make(155,155,155));
		g.beginPath();
		g.moveTo(xPos+breedte-9, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-9);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+breedte-6, yPos+hoogte-2);
		g.lineTo(xPos+breedte-2, yPos+hoogte-6);
		g.stroke();

		
		//g.setColor(Color.white);
		//g.fillRect(veldx-1,veldy-1,veldb+1,veldh+1);
		
		g.setFillStyle(CssColor.make(255,255,255));
		g.fillRect(xPos+veldx-1,yPos+veldy-1,veldb+1,veldh+1);

		gv.paint(g);

		
		//g.setColor(Color.gray.darker());
		//g.drawLine(veldx-1,veldy-1,veldx+veldb,veldy-1);
		//g.drawLine(veldx-1,veldy-1,veldx-1,veldy+veldh);
		//g.drawLine(veldx-1,veldy-2,veldx+veldb,veldy-2);
		//g.drawLine(veldx-2,veldy-1,veldx-2,veldy+veldh);
		//g.setColor(new Color(180,180,180));
		//g.drawLine(veldx+veldb,veldy-1,veldx+veldb,veldy+veldh);
		//g.drawLine(veldx-1,veldy+veldh,veldx+veldb,veldy+veldh);
		//g.setColor(Color.white);
		//g.drawLine(veldx+veldb+1,veldy-1,veldx+veldb+1,veldy+veldh);
		//g.drawLine(veldx-1,veldy+veldh+1,veldx+veldb,veldy+veldh+1);
		
		g.setStrokeStyle(CssColor.make(155,155,155));
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy-1);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-1);
		g.lineTo(xPos+veldx-1,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy-2);
		g.lineTo(xPos+veldx+veldb,yPos+veldy-2);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-2,yPos+veldy-1);
		g.lineTo(xPos+veldx-2,yPos+veldy+veldh);
		g.stroke();
		g.setStrokeStyle(CssColor.make(180,180,180));
		g.beginPath();
		g.moveTo(xPos+veldx+veldb,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy+veldh);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh);
		g.stroke();
		g.setStrokeStyle(CssColor.make(255,255,255));
		g.beginPath();
		g.moveTo(xPos+veldx+veldb+1,yPos+veldy-1);
		g.lineTo(xPos+veldx+veldb+1,yPos+veldy+veldh);
		g.stroke();
		g.beginPath();
		g.moveTo(xPos+veldx-1,yPos+veldy+veldh+1);
		g.lineTo(xPos+veldx+veldb,yPos+veldy+veldh+1);
		g.stroke();

		
		//g.setColor(Color.black);
		g.setFillStyle(CssColor.make(0,0,0));
		
		//FontMetrics fm = g.getFontMetrics();
		
		g.setFont(fontString);
		
		TextMetrics tm = g.measureText(varNaam);
		
		//int woordbreedte = fm.stringWidth(varNaam);
		int woordbreedte = (int) Math.round(tm.getWidth());
		
		boolean b = varNaam.equals("qq") || varNaam.length()>2 && varNaam.substring(0,2).equals("qq");
		if(!b) 
		{	//g.drawString(varNaam, veldx+veldb+5,veldy+veldh+5);
//yPos		
			g.fillText(varNaam, xPos+veldx+veldb+5,yPos+veldy+veldh+5);
		
		}
		//g.drawString(formuleNaam,veldx,veldy-8);
//yPos?
		g.fillText(formuleNaam,xPos+veldx,yPos+veldy-8);
		
		
		int imin = -(int)Math.round(beginx/eenheid); 
		int imax = 1+veldb/eenheid-(int)Math.round(beginx/eenheid);
		int jmin = -(int)Math.round(beginy/eenheid); 
		int jmax = 1+veldb/eenheid-(int)Math.round(beginy/eenheid);
		int bx = (int)beginx;
		int by = (int)beginy;
		
		
		for(int i=imin+1 ; i<imax ; i++)
		{	new Expressie();
			//String getal = df.format(schaalFactorX*(i));
			String getal = UF.format0(schaalFactorX*(i),4);
			tm = g.measureText(getal);

			//woordbreedte = fm.stringWidth(getal);
			woordbreedte = (int) Math.round(tm.getWidth());
			
			if(schaalFactorX>0.5 && schaalFactorX<5 && woordbreedte<eenheidx)
			{	//g.drawString(getal,(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),veldy+veldh+15);
//yPos?
			    g.fillText(getal,xPos+(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),yPos+veldy+veldh+15);
			
			}
			else if(i%2==0)
			{	//g.drawString(getal,(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),veldy+veldh+15);
//yPos?			
				g.fillText(getal,xPos+(int)(veldx+beginx+i*eenheidxD-woordbreedte/2),yPos+veldy+veldh+15);
			
			}
		}
		for(int j=jmin+1 ; j<jmax ; j++)
		{	//String getal = df.format(schaalFactorY*(j));
			String getal = UF.format0(schaalFactorY*(j),4);
			tm = g.measureText(getal);
			//woordbreedte = fm.stringWidth(getal);
			woordbreedte = (int) Math.round(tm.getWidth());
			//g.drawString(getal,veldx-5-woordbreedte,(int)(veldy+veldh+5-(beginy+j*eenheidyD)));
//yPos			
			g.fillText(getal,xPos+veldx-5-woordbreedte,yPos+(int)(veldy+veldh+5-(beginy+j*eenheidyD)));
			
		}
		
		//g.drawString("trace",12,hoogte-15);
//yPos		
		g.fillText("trace",xPos+12,yPos+hoogte-15);
		
		super.paint(g);
		
		zoomStandaard.paint();
		zoomIn.paint();
		zoomUit.paint();
		zoomInX.paint();
		zoomUitX.paint();
		zoomInY.paint();
		zoomUitY.paint();

	}	
		
	public void zetBegin(int x, int y)
	{	beginx = eenheidx*x;
		beginy = eenheidy*y;
	}
	

	public void zetExpressie(int nr,Expressie e)
	{	Expressie exp = null;
		if(e!=null && e.geefVarNaam()!=null )
		{	exp = e;
			if(varNaam.equals("qq")|| aantalPijlenIn==1)varNaam = e.geefVarNaam();
			isPuntGrafiek[nr] = false;
		}
		else if(e!=null && !Double.isNaN(e.geefWaarde().doubleValue()) && pijlenIn[nr]!=null)
		{	
			AlgebraSchuifComponent asc = pijlenIn[nr].zender;
			int teller = 20;
			puntXWaarde[nr] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
			while(asc.pijlIn1 !=null && teller > 0)
			{	teller--;
				asc = asc.pijlIn1.zender;
				if(!Double.isNaN(asc.geefUitvoer(teller).geefWaarde().doubleValue()))
					puntXWaarde[nr] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
				isPuntGrafiek[nr] = true;
			}
			exp = e;
		}
		else 
		{	exp = null;
			isPuntGrafiek[nr] = false;
		}
		expressies[nr] = exp;
		gv.tekenOpnieuw();
	}
	
	public void setZoomState(String varNaam, ZoomState zoomState)
	{	if(varNaam.equals(this.varNaam) && zoomState!=null)
		{	double beginxOud = beginx;
			double factorXOud = schaalFactorX;
			int factorRijNummerXOud = factorRijNummerX;
//System.out.println("beginx3 = "+beginx);
			//this.beginwaarde = zoomState.getBeginwaarde();
			//this.selectnummer = zoomState.getSelectnummer();
			//this.schaalFactorX = zoomState.getSchaalFactorX();
			//this.schaalFactorY = zoomState.getSchaalFactorY();
			this.factorRijNummerX = zoomState.getFactorRijNummerX();
			//this.factorRijNummerY = zoomState.getFactorRijNummerY();
			//this.beginx = ((double)zoomState.getBeginx()*eenheid)/14+eenheid;
			//this.beginy = (double)zoomState.getBeginy();
			//this.tracexD = (double)zoomState.getTracexD();
			
			{	this.schaalFactorX = zoomState.getSchaalFactorX();
				this.beginwaarde = zoomState.getBeginwaarde();
				this.beginx = ((double)zoomState.getBeginx()*eenheid)/14+eenheid;
				this.selectnummer = zoomState.getSelectnummer();
				
			}
			
//			System.out.println("dx = "+beginwaarde);
//			System.out.println("dx = "+beginx);
			
			double dx  = beginx-beginxOud;
			double factor = schaalFactorX/factorXOud;
			
			if(trace && tracex!=-2) {
				tracexD = beginx+(tracexD-beginx)/factor+dx;
				tracex = (int)Math.round(tracexD);
//GWT				
				//slider.zetStand(tracex);
			}
			
			//beginx = eenheidx-eenheidx*beginwaarde;
			if(selectnummer!=999)tracing = false;
			else
			{	
			}
			gv.tekenOpnieuw();
		}
	}
	
	public void zetBoomZichtbaarHier(boolean b)
    {   for(int i=0 ; i<aantalPijlenIn ; i++)
		{	if(pijlenIn[i]!=null)pijlenIn[i].zender.zetBoomZichtbaar(b);
	        kettingZichtbaar = b;
	        asv.tekenOpnieuw();
	    }
    }

	public void zetVeranderd(int max)
	{	for(int i=0 ; i<aantalPijlenIn ; i++)
		{	Expressie e = pijlenIn[i].zender.geefUitvoer(20);
			Expressie ev = pijlenIn[i].zender.geefVerborgenUitvoer(20);
			zetExpressie(i,e);
			formuleNaam = ((UitvoerSchuifComponent)pijlenIn[i].zender).geefLabelTekst();
			if ((e==null || !Double.isNaN(e.geefWaarde().doubleValue())) && 
				!(ev instanceof BasisExpressie) && ((UitvoerSchuifComponent)pijlenIn[i].zender).tabelZichtbaar)
			{	zetExpressie(i,ev);
				isMeerPuntenGrafiek[i] = true;
				isLijnGrafiek[i] = false;
				if (e!=null && !Double.isNaN(e.geefWaarde().doubleValue())) 
				{	AlgebraSchuifComponent asc = pijlenIn[i].zender;
					int teller = 20;
					puntXWaarde[i] = asc.geefUitvoer(teller).geefWaarde().doubleValue();
					while(asc.pijlIn1 !=null && teller > 0)
					{	teller--;
						asc = asc.pijlIn1.zender;
						Double d = asc.geefUitvoer(teller).geefWaarde();
						if(!Double.isNaN(d.doubleValue())) 
							puntXWaarde[i] = d.doubleValue();
						isPuntGrafiek[i] = true;
					}
				}
			}
			else if(((UitvoerSchuifComponent)pijlenIn[i].zender).tabelZichtbaar)
			{	isMeerPuntenGrafiek[i] = true;
				isLijnGrafiek[i] = true;
			}
			else 
			{	isMeerPuntenGrafiek[i] = false;
				isLijnGrafiek[i] = true;
			}
			
		}
		
		//if(getParent()!=null)
			setZoomState(varNaam,asv.zoomStateHolder.getZoomState(varNaam));
        super.zetVeranderd(max);
	}
	
	public void verbind(Pijl p, int nr)
	{	pijlenIn[nr] = p;
		//p.zetEind(getLocation().x + 10 + nr * 15, getLocation().y + 10);
		p.zetEind(xPos + 10 + nr * 15, yPos + 10);
		aantalPijlenIn++;
		pijlenIn[nr].setColor(colors[nr]);
	}
	
	public boolean meldAan(Pijl p, int x, int y)
	{	if (!(p.zender instanceof UitvoerSchuifComponent))
			return false;
	
		for (int i = 0; i < aantalPijlenIn; i++)
		{	if (pijlenIn[i].zender == p.zender) 
				return false;
		}
		AlgebraSchuifComponent asc = p.zender;
		int teller = 20;
		Expressie e = asc.geefUitvoer(teller);
		while (asc.pijlIn1 != null && teller > 0)
		{	teller--;
			asc = asc.pijlIn1.zender;
			e = asc.geefUitvoer(teller);
		}
	
		if (e != null && e.geefVarNaam() != null && varNaam != "qq" && !e.geefVarNaam().equals(varNaam)) 
			return false;
		

		//Rectangle ingang = new Rectangle(0, -10, getSize().width,  getSize().height + 10);
		Rectangle ingang = new Rectangle(xPos+ 0, yPos-10, breedte,  hoogte + 10);
		
		//if (aantalPijlenIn < 10 && ingang.contains(x - getLocation().x, y - getLocation().y))
		if (aantalPijlenIn < 10 && ingang.contains(x, y))
		{	
			
			//p.zetEind(getLocation().x + 10 + aantalPijlenIn * 15, getLocation().y + 10);
			p.zetEind(xPos + 10 + aantalPijlenIn * 15, yPos + 10);
			pijlenIn[aantalPijlenIn] = p;
			aantalPijlenIn++;
			if (e != null && e.geefVarNaam() != null) 
				varNaam = e.geefVarNaam();
			
			zetVeranderd(20);
			if (p != null) 
				p.setColor(colors[aantalPijlenIn - 1]);
			asv.tekenOpnieuw();
			return true;
		}
		return false;
	}
	
	public void maakLos(Pijl p)
	{	for(int i=0 ; i<aantalPijlenIn ; i++)
		{	//p.setColor(Color.black);
			p.setColor(CssColor.make(0,0,0));
			if(p==pijlenIn[i])
			{	
				CssColor colorRes = colors[i];
				for(int j=i ; j<aantalPijlenIn-1 ; j++)
				{	pijlenIn[j] = pijlenIn[j+1];
					colors[j] = colors[j+1];
					//pijlenIn[j].zetEind(getLocation().x+10+j*15 , getLocation().y+10);
					pijlenIn[j].zetEind(xPos+10+j*15 , yPos+10);
					expressies[j] = expressies[j+1];
				}
				colors[aantalPijlenIn-1] = colorRes;
				
				pijlenIn[aantalPijlenIn-1]=null;
				aantalPijlenIn--;
				break;
			}
		}
		selectnummer = 999;
		if(aantalPijlenIn==0)
		{	
			asv.zoomStateHolder.setBeginy(varNaam, eenheidy);
			varNaam = "qq";
			formuleNaam = "";
			beginy = eenheidy;
		}
	}

	public void verplaatsKnoppen(int dx, int dy)
	{
		zoomInX.translate(dx,dy);
		zoomUitX.translate(dx,dy);
		zoomInY.translate(dx,dy);
		zoomUitY.translate(dx,dy);
		zoomIn.translate(dx,dy);
		zoomUit.translate(dx,dy);
		zoomStandaard.translate(dx,dy);	
	}

	//public void mousePressed(MouseEvent e)
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		//requestFocus();
		
//GWT		
		//if(e.getModifiers()== e.BUTTON3_MASK || e.isControlDown())
		{	
			if (asv.alleenInvullen)
				return;
//GWT			
			//popup.show(this,e.getX(),e.getY());
			//return;
		//}
		
		// grafiekveld	
		//if (e.getSource() == gv)
		if (new Rectangle(xPos+veldx,yPos+veldy,veldb,veldh).contains(eventX, eventY))	
		{	startxv = eventX; //e.getX();
			startyv = eventY; //e.getY();
//GWT			
			//setCursor(new Cursor(Cursor.MOVE_CURSOR));
		}
		// resize corner
		//else if (e.getX() > getSize().width - 10 && e.getY()> getSize().height - 10)
		else if (eventX > (yPos + breedte - 10) && eventY > (yPos + hoogte - 10))	
		{	resize = true;
			startxrs = eventX; //e.getX();
			startyrs = eventY; //e.getY();
//GWT			
			//setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		}
		// een van de zoomknoppen
		else if (zoomAction(eventX, eventY))
		{
			
		}

		else 
		{	//super.mousePressed(e);
			super.mouseDownTouchStartAction(eventX, eventY);
		}
		}
	}	
	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		
		//if (e.getSource() == gv)
		if (new Rectangle(xPos+veldx,yPos+veldy,veldb,veldh).contains(eventX, eventY))	
		{	//int dx = e.getX() - startxv;
			//int dy = e.getY() - startyv;
		
			int dx = eventX - startxv;
			int dy = eventY - startyv;
			
			beginx = beginx + dx;
			beginy = beginy - dy;
			
			if(trace && tracex!=-2) 
			{
				tracexD = tracexD+dx;
				tracex = tracex+dx;
//GWT				
				//slider.zetStand(tracex);
			}
			
			int b = beginwaarde;
			if (beginx > 0) 
				beginwaarde = 1 - (int) Math.round((beginx - eenheidx / 2) / eenheidx);
			else 
				beginwaarde = 1 - (int) Math.round((beginx + eenheidx / 2) / eenheidx);
			selectnummer = selectnummer + b - beginwaarde;

//			System.out.println("beginx1 = "+beginx);
			asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
			asv.zoomStateHolder.setBeginx(varNaam, ((beginx-eenheid)*14)/eenheid);
			asv.zoomStateHolder.setBeginy(varNaam, beginy);
			asv.zoomStateHolder.setTracexD(varNaam, tracexD);
			asv.zoomStateHolder.setZoomStates(varNaam);
		
			startxv = eventX; //e.getX();
			startyv = eventY; //e.getY();
		}
		else if(resize)
		{	//int dx = e.getX() - startx;
			//int dy = e.getY() - starty;
			
			int rsdx = eventX - startxrs;
			int rsdy = eventY - startyrs;
			
			//setSize(getSize().width + dx, getSize().height + dy);
			setSize(breedte + rsdx, hoogte + rsdy);
			
			asv.tekenOpnieuw();
			
			startxrs = eventX; //e.getX();
			startyrs = eventY; //e.getY();
		
		}
	
		else
		{	//super.mouseDragged(e);
			super.mouseMoveTouchMoveAction(eventX, eventY);
			//int dx = e.getX() - startx;
			//int dy = e.getY() - starty;
			
			//int dx = eventX - startx;
			//int dy = eventY - starty;
			
			for(int i=0 ; i<aantalPijlenIn ; i++)
			{	pijlenIn[i].verplaatsEind(dx,dy);
			}
			verplaatsKnoppen(dx,dy);
			gv.verplaats(dx,dy);
			
			asv.tekenOpnieuw();

		}
	}
	
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX, int eventY)
	{	
		if (asv.isDemo)
			return;
		if (asv.frozen)
			return;
		
		
		resize = false;
		
//GWT de laatsts touchMove nemen 
		
		//if(e.getSource()==gv)
		if (new Rectangle(xPos+veldx,yPos+veldy,veldb,veldh).contains(eventX, eventY))	
		{	
//GWT			
			//setCursor(new Cursor(Cursor.HAND_CURSOR));
		
			double beginxR = beginx;
			beginx = eenheid*Math.round(beginx/eenheid);
			beginy = eenheid*Math.round(beginy/eenheid);

			if(trace && tracex!=-2) 
			{
				tracexD += beginx-beginxR;
				tracex += beginx-beginxR;
//GWT				
				//slider.zetStand(tracex);
			}
			
			if(aantalPijlenIn>0)
			{					
				
				asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
	            asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
	            asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
	            asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
	            asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
	            asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
	            asv.zoomStateHolder.setBeginx(varNaam, Math.round(beginx-eenheidx)*14/16);
	            asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
	            asv.zoomStateHolder.setTracexD(varNaam, tracexD);
				asv.zoomStateHolder.setZoomStates(varNaam);
	            
			}
			gv.tekenOpnieuw();
			asv.tekenOpnieuw();
		}
		else
		{	//super.mouseReleased(e);
			super.mouseUpTouchEndAction();
		}
	}
	
//GWT
/*	
	public void mouseMoved(MouseEvent e)
	{	
		if (((AlgebraSchuifVeld) schuifveld).isDemo)
			return;
		if (((AlgebraSchuifVeld) schuifveld).frozen)
			return;
		
		
		movex = e.getX();
		movey = e.getY();
		if(e.getX()>getSize().width-10 && e.getY()>getSize().height-10)
		{	setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
		}
		else if(e.getX()>getSize().width-20 && e.getY()>getSize().height-20)
		{	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
		gv.tekenOpnieuw();
		schuifveld.tekenOpnieuw();
	}
*/
	
//GWT
/*	
	public void mouseExited(MouseEvent e)
	{	//if(e.getSource()==gv)
		{	
			if (((AlgebraSchuifVeld) schuifveld).isDemo)
				return;
			if (((AlgebraSchuifVeld) schuifveld).frozen)
				return;
			
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR ));
			gv.tekenOpnieuw();
		}
	
	}
*/	
	
//	public void mouseClicked(MouseEvent e){;}
	
	public void zoomStandaard()
	{
		double beginxVorig = beginx;
		beginx = eenheidx;
		beginy = eenheidy;
		tracexD = beginx -(beginxVorig - tracexD)*schaalFactorX;
		factorRijNummerX = 99;
		factorRijNummerY = 99;
		schaalFactorX = 1;
		schaalFactorY = 1;
		beginwaarde = 0;
		selectnummer = 999;
		if(aantalPijlenIn>0)
		{	
			asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
			asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
			asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
			asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
			asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
			asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
			asv.zoomStateHolder.setBeginx(varNaam, (beginx-eenheidx)*14/16);
			asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
			asv.zoomStateHolder.setTracexD(varNaam, tracexD);
			asv.zoomStateHolder.setZoomStates(varNaam);
            
		}
		
		tracex = (int) Math.round(tracexD);
//GWT		
		//slider.zetStand(tracex);
		
		//gv.tekenOpnieuw();
		asv.tekenOpnieuw();
		
	}
	
	public void zoom(boolean x, boolean y, boolean in)
	{
		if(x) 
			selectnummer = 999;
        eenheidxD = eenheid;
		eenheidyD = eenheid;
		eenheidx = eenheid;
		eenheidy = eenheid;
		double stapx, stapy;
		double factorx = 1;
		double factory = 1;
		
		double middenx = eenheidx;
		double middeny = eenheidy;
		
		double beginxOud = beginx;
		
		if (in && x)
		{	if (factorRijNummerX % 3 == 2)
			{	factorx = 0.4;
			}
			else if(factorRijNummerX % 3 == 0)
			{	factorx = 0.5;
			}
			else 
			{	factorx = 0.5;
			}
			
		}
		
		else if (!in && x)
		{	if (factorRijNummerX%3 == 1)
			{	factorx = 2.5;
			}
			else if(factorRijNummerX % 3 == 2)
			{	factorx = 2;
			}
			else 
			{	factorx = 2;
			}
		}
		
		if(in && y)
		{	if(factorRijNummerY%3==2)
			{	factory =0.4;
			}
			else if(factorRijNummerY%3==0)
			{	factory=0.5;
			}
			else 
			{	factory=0.5;
			}
		}
		
		else if(!in && y)
		{	if(factorRijNummerY%3==1)
			{	factory =2.5;
			}
			else if(factorRijNummerY%3==2)
			{	factory=2;
			}
			else 
			{	factory=2;
			}
		}
		
		
		stapx = Math.pow(factorx,0.1);
		stapy = Math.pow(factory,0.1);
		
		
		for (int i = 0; i < 5; i++)
		{	
			//int delay = 20;
			//long t = System.currentTimeMillis();
			//try
			//{	t = t+delay;
			//	sleep(Math.max(1, t-System.currentTimeMillis()));
			//}
			//catch(InterruptedException e)    // geen ;
			//{   };
			eenheidxD = eenheidxD/stapx;
			eenheidyD = eenheidyD/stapy;
			eenheidx = (int) Math.round(eenheidxD);
			eenheidy = (int) Math.round(eenheidyD);
			double beginxVorig = beginx;
			beginx =  middenx -(middenx - beginx)/stapx;
			beginy =  middeny -(middeny - beginy)/stapy;
			
			tracexD = middenx -(middenx - tracexD)/stapx;
			
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			tracex = (int) Math.round(tracexD);
//GWT				
			//slider.zetStand(tracex);
			
			//gv.tekenOpnieuw();
			//asv.tekenOpnieuw();
			
		}
		
		
		
		schaalFactorX *= factorx;
		if (in && x)
			factorRijNummerX--;
		else if (!in && x)
			factorRijNummerX++;
		schaalFactorY *= factory;
		if (in && y)
			factorRijNummerY--;
		if (!in && y)
			factorRijNummerY++;
	
		
		eenheidxD = eenheidxD * factorx;
		eenheidyD = eenheidyD * factory;
		
		
		for(int i = 0; i < 5; i++)
		{	//int delay = 20;
			//long t = System.currentTimeMillis();
			//try
			//{	t = t+delay;
			//	sleep(Math.max(1, t-System.currentTimeMillis()));
			//}
			//catch(InterruptedException e)    // geen ;
			//{   };
			eenheidxD = eenheidxD/stapx;
			eenheidyD = eenheidyD/stapy;
			eenheidx = (int) Math.round(eenheidxD);
			eenheidy = (int) Math.round(eenheidyD);
			double beginxVorig = beginx;
			beginx =  middenx -(middenx - beginx)/stapx;
			beginy =  middeny -(middeny - beginy)/stapy;
			
			tracexD = middenx -(middenx - tracexD)/stapx;
			
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			tracex = (int) Math.round(tracexD);
//GWT				
			//slider.zetStand(tracex);
			//gv.tekenOpnieuw();
			//asv.tekenOpnieuw();
		}
		
		
		beginwaarde = 1-(int)Math.round(beginx/eenheidx);
		double beginwaardeD = 1.0-(beginx/eenheidx);
//System.out.println(""+beginx);
//System.out.println(""+eenheidx);
//System.out.println(""+beginwaardeD);
		
		tracexD = tracexD + eenheid*(beginwaardeD - beginwaarde);
		tracex = (int) Math.round(tracexD);
//GWT			
		//slider.zetStand(tracex);
		
		if(x)
			selectnummer = 999;
		
		beginx = eenheidx - eenheidx * beginwaarde;
		
        if(aantalPijlenIn>0)
		{	
        	asv.zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
        	asv.zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
        	asv.zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
        	asv.zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
        	asv.zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
        	asv.zoomStateHolder.setSelectnummer(varNaam, selectnummer);
        	asv.zoomStateHolder.setBeginx(varNaam, (beginx-eenheid)*14/eenheid);
        	asv.zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
        	asv.zoomStateHolder.setTracexD(varNaam, tracexD);
        	asv.zoomStateHolder.setZoomStates(varNaam);
            
		}
        
		//gv.tekenOpnieuw();
		asv.tekenOpnieuw();
	}

	public boolean zoomAction(int eventX, int eventY)
	{
		
		if (new Rectangle(zoomStandaard.xPos,zoomStandaard.yPos,zoomStandaard.breedte,zoomStandaard.hoogte).contains(eventX, eventY))
		{
			zoomStandaard();
			return true;
		}
		else if (new Rectangle(zoomIn.xPos,zoomIn.yPos,zoomIn.breedte,zoomIn.hoogte).contains(eventX, eventY))
		{
			if (factorRijNummerX>87 && factorRijNummerY>87)
			{	zoom(true,true,true);
			}

			return true;
		}
		else if (new Rectangle(zoomUit.xPos,zoomUit.yPos,zoomUit.breedte,zoomUit.hoogte).contains(eventX, eventY))
		{
			if (factorRijNummerX<120 && factorRijNummerY<120)
			{	zoom(true,true,false);
			}
			return true;
		}
		else if (new Rectangle(zoomInX.xPos,zoomInX.yPos,zoomInX.breedte,zoomInX.hoogte).contains(eventX, eventY))
		{
			if (factorRijNummerX>87)
			{	zoom(true,false,true);
			}
			return true;
		}
		else if (new Rectangle(zoomUitX.xPos,zoomUitX.yPos,zoomUitX.breedte,zoomUitX.hoogte).contains(eventX, eventY))
		{	
			if (factorRijNummerX<120)
			{	zoom(true,false,false);
			}
			return true;
		}
		else if (new Rectangle(zoomInY.xPos,zoomInY.yPos,zoomInY.breedte,zoomInY.hoogte).contains(eventX, eventY))
		{	
			if (factorRijNummerY>87)
			{	zoom(false,true,true);
			}
			return true;
		}
		else if (new Rectangle(zoomUitY.xPos,zoomUitY.yPos,zoomUitY.breedte,zoomUitY.hoogte).contains(eventX, eventY))
		{
			if (factorRijNummerY < 120)
			{	zoom(false, true, false);
			}
			return true;
		}
		
		return false;
	}

//GWT
/*	
	public void actionPerformed(ActionEvent e)
	{	
		
		if (((AlgebraSchuifVeld) schuifveld).isDemo)
			return;
		if (((AlgebraSchuifVeld) schuifveld).frozen)
			return;
		
		if (zoomDraad != null && zoomDraad.isAlive())
			return;
		
		if (e.getActionCommand().equals("focus")) 
			schuifveld.tekenOpnieuw();
		else 
		{	
		
			if (e.getSource() instanceof JMenuItem && 
					((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label5")))
				{	zetBoomZichtbaarHier(true);
				}
				else if (e.getSource() instanceof JMenuItem && 
						((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("popup1Label6")))
				{	zetBoomZichtbaarHier(false);
				}
				else if (e.getSource() == zoomUitY && factorRijNummerY < 120)
				{	zoomDraad = new ZoomDraad(false, true, false);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomInY  && factorRijNummerY>87)
				{	zoomDraad = new ZoomDraad(false,true,true);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomUitX && factorRijNummerX<120)
				{	zoomDraad = new ZoomDraad(true,false,false);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomInX  && factorRijNummerX>87)
				{	zoomDraad = new ZoomDraad(true,false,true);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomUit && factorRijNummerX<120 && factorRijNummerY<120)
				{	zoomDraad = new ZoomDraad(true,true,false);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomIn && factorRijNummerX>87 && factorRijNummerY>87)
				{	zoomDraad = new ZoomDraad(true,true,true);
					zoomDraad.start();
				}
				else if(e.getSource()==zoomStandaard)
				{	//beginx = veldb/2/eenheidx*eenheidx;
					//beginy = veldh/2/eenheidy*eenheidy;
					double beginxVorig = beginx;
					beginx = eenheidx;
					beginy = eenheidy;
					tracexD = beginx -(beginxVorig - tracexD)*schaalFactorX;
					factorRijNummerX = 99;
					factorRijNummerY = 99;
					schaalFactorX = 1;
					schaalFactorY = 1;
					beginwaarde = 0;
					selectnummer = 999;
					if(aantalPijlenIn>0)
					{	//((AlgebraSchuifVeld)getParent()).zetTabellen(beginwaarde,selectnummer, varNaam, schaalFactorX);
						//String varnaam = null;
			            //if(expressie!=null) varnaam = expressie.geefVarNaam();
			            //if(varnaam==null && verborgenExpressie!=null) varnaam = verborgenExpressie.geefVarNaam();
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSelectnummer(varNaam, selectnummer);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginx(varNaam, (beginx-eenheidx)*14/16);
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
			            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setTracexD(varNaam, tracexD);
						((AlgebraSchuifVeld)getParent()).zoomStateHolder.setZoomStates(varNaam);
			            
					}
					
					tracex = (int) Math.round(tracexD);
					//tracex=-2;
					slider.zetStand(tracex);
					
					gv.tekenOpnieuw();
					schuifveld.tekenOpnieuw();
					
					
				}
				
			}
			if(e.getSource()==slider)
			{ 	if(e.getActionCommand().equals("start")) 
				{	tracing = true;
// ???					((AlgebraSchuifVeld)getParent()).zetTabellen(beginwaarde, 999, varNaam, schaalFactorX);
				}
				//else if(e.getActionCommand().equals("stop")) tracing = false;
				tracex = slider.geefStand();
				tracexD = tracex;
				gv.tekenOpnieuw();
				schuifveld.tekenOpnieuw();
				
			}
			if (e.getSource()==traceCheckbox)
			{	
				//trace = traceCheckbox.aan;
				trace = traceCheckbox.isSelected();
				slider.setVisible(trace);
				gv.tekenOpnieuw();
				schuifveld.tekenOpnieuw();
			}
	}
*/
	
//	public void mouseEntered(MouseEvent e){;}	
	
/*	
	class ZoomDraad extends Thread 
	{	boolean dood = false;
		boolean x,y,in;
		
		ZoomDraad(boolean x, boolean y, boolean in)
		{	this.x = x;
			this.y = y;
			this.in = in;
		}
		
		public void run()
		{	if(x) selectnummer = 999;
            eenheidxD = eenheid;
			eenheidyD = eenheid;
			eenheidx = eenheid;
			eenheidy = eenheid;
			double stapx, stapy;
			double factorx = 1;
			double factory = 1;
			//int middenx = veldb/2/eenheidx*eenheidx;
			//int middeny = veldh/2/eenheidy*eenheidy;
			
			double middenx = eenheidx;
			double middeny = eenheidy;
			
			double beginxOud = beginx;
			
			if(in && x)
			{	if(factorRijNummerX%3==2)
				{	factorx=0.4;
				}
				else if(factorRijNummerX%3==0)
				{	factorx=0.5;
				}
				else 
				{	factorx=0.5;
				}
				
			}
			
			else if(!in && x)
			{	if(factorRijNummerX%3==1)
				{	factorx=2.5;
				}
				else if(factorRijNummerX%3==2)
				{	factorx=2;
				}
				else 
				{	factorx=2;
				}
			}
			
			if(in && y)
			{	if(factorRijNummerY%3==2)
				{	factory =0.4;
				}
				else if(factorRijNummerY%3==0)
				{	factory=0.5;
				}
				else 
				{	factory=0.5;
				}
			}
			
			else if(!in && y)
			{	if(factorRijNummerY%3==1)
				{	factory =2.5;
				}
				else if(factorRijNummerY%3==2)
				{	factory=2;
				}
				else 
				{	factory=2;
				}
			}
			
			//if(factorx!=1)
			//{	tracex=-2;
			//	slider.zetStand(tracex);
			//}
			stapx= Math.pow(factorx,0.1);
			stapy= Math.pow(factory,0.1);
			
			
			for(int i=0 ; i<5 ; i++)
			{	int delay = 20;
				long t = System.currentTimeMillis();
				try
				{	t = t+delay;
					sleep(Math.max(1, t-System.currentTimeMillis()));
				}
    			catch(InterruptedException e)    // geen ;
				{   };
				eenheidxD = eenheidxD/stapx;
				eenheidyD = eenheidyD/stapy;
				eenheidx = (int) Math.round(eenheidxD);
				eenheidy = (int) Math.round(eenheidyD);
				double beginxVorig = beginx;
				beginx =  middenx -(middenx - beginx)/stapx;
				beginy =  middeny -(middeny - beginy)/stapy;
				
				tracexD = middenx -(middenx - tracexD)/stapx;
				
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				//double beginwaardeD = 1.0-(beginx/eenheidx);
				//tracexD = tracexD + eenheid*(beginwaardeD - beginwaarde);
				tracex = (int) Math.round(tracexD);
				slider.zetStand(tracex);
				
				gv.tekenOpnieuw();
				schuifveld.tekenOpnieuw();
				
			}
			
			
			
			schaalFactorX*=factorx;
			if(in && x)factorRijNummerX--;
			else if(!in && x)factorRijNummerX++;
			schaalFactorY*=factory;
			if(in && y)factorRijNummerY--;
			if(!in && y)factorRijNummerY++;
			
			eenheidxD = eenheidxD*factorx;
			eenheidyD = eenheidyD*factory;
			
			
			for(int i=0 ; i<5 ; i++)
			{	int delay = 20;
				long t = System.currentTimeMillis();
				try
				{	t = t+delay;
					sleep(Math.max(1, t-System.currentTimeMillis()));
				}
    			catch(InterruptedException e)    // geen ;
				{   };
				eenheidxD = eenheidxD/stapx;
				eenheidyD = eenheidyD/stapy;
				eenheidx = (int) Math.round(eenheidxD);
				eenheidy = (int) Math.round(eenheidyD);
				double beginxVorig = beginx;
				beginx =  middenx -(middenx - beginx)/stapx;
				beginy =  middeny -(middeny - beginy)/stapy;
				
				tracexD = middenx -(middenx - tracexD)/stapx;
				
				beginwaarde = 1-(int)Math.round(beginx/eenheidx);
				//double beginwaardeD = 1.0-(beginx/eenheidx);
				//tracexD = tracexD + eenheid*(beginwaardeD - beginwaarde);
				tracex = (int) Math.round(tracexD);
				slider.zetStand(tracex);
				gv.tekenOpnieuw();
				schuifveld.tekenOpnieuw();
			}
			
			//beginx = Math.round(beginx/eenheid)*eenheid;
			
			beginwaarde = 1-(int)Math.round(beginx/eenheidx);
			double beginwaardeD = 1.0-(beginx/eenheidx);
			System.out.println(""+beginx);
			System.out.println(""+eenheidx);
			System.out.println(""+beginwaardeD);
			
			tracexD = tracexD + eenheid*(beginwaardeD - beginwaarde);
			tracex = (int) Math.round(tracexD);
			slider.zetStand(tracex);
			
			if(x)selectnummer = 999;
			
			beginx = eenheidx-eenheidx*beginwaarde;
			
            if(aantalPijlenIn>0)
			{	//((AlgebraSchuifVeld)getParent()).zetTabellen(beginwaarde,selectnummer, varNaam, schaalFactorX);
            	((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginwaarde(varNaam, beginwaarde);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSchaalFactorX(varNaam, schaalFactorX);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setFactorRijNummerX(varNaam, factorRijNummerX);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSchaalFactorY(varNaam, schaalFactorY);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setFactorRijNummerY(varNaam, factorRijNummerY);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setSelectnummer(varNaam, selectnummer);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginx(varNaam, (beginx-eenheid)*14/eenheid);
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setBeginy(varNaam, Math.round(beginy));
	            ((AlgebraSchuifVeld)getParent()).zoomStateHolder.setTracexD(varNaam, tracexD);
				((AlgebraSchuifVeld)getParent()).zoomStateHolder.setZoomStates(varNaam);
	            
			}
            
			gv.tekenOpnieuw();
			schuifveld.tekenOpnieuw();
		}
		public void maakDood()
		{	dood = true;
		}
	}
*/
	
	class GrafiekVeld //extends JComponent 
	{
		//private Image im;
  		//private Graphics gIm;
		//private boolean veranderd;
		
		int gvX, gvY, gvBreedte, gvHoogte;
	
		public GrafiekVeld(int x, int y, int b, int h)
		{	
			//setBounds(x,y,b,h);
			//veranderd = true;
			gvX = x;
			gvY = y;
			gvBreedte = b;
			gvHoogte = h;
		}
		
		public void verplaats(int dx, int dy)
		{
			gvX += dx;
			gvY += dy;
		}

		public void paint()
		{
			paint(asv.asvContext2d);
		}
		//public void paint(Graphics g)
		public void paint(Context2d g)
		{	//int breedte = getSize().width;
			//int hoogte = getSize().height;
			//if (veranderd)			
			//{	if (im == null || resize)
			//	{	im = createImage(breedte, hoogte);
			//		gIm = im.getGraphics();
			//	}
				//gIm.setColor(Color.white);
				g.setFillStyle(CssColor.make(255,255,255));
				//gIm.fillRect(0,0,breedte,hoogte);
				g.fillRect(gvX,gvY,gvBreedte,gvHoogte);
				tekenFunctie(g);
			//	veranderd = false;
			//}
			//g.drawImage(im, 0, 0, null);
		}
		
		public void tekenOpnieuw()
		{	//veranderd = true;
			paint();
		}
		
		public void setSize(int b, int h)
		{	//super.setSize(b,h);
			gvBreedte = b;
			gvHoogte = h;
			
			tekenOpnieuw();
		}
		
		//public void tekenFunctie(Graphics g)
		public void tekenFunctie(Context2d g)
		{	
			
			//int breedte = getSize().width;
			//int hoogte = getSize().height;
			//g.setClip(0, 0, breedte, hoogte);
			
			int imin = - (int) Math.round(beginx / eenheidx); 
			int imax = 1 + gvBreedte / eenheidx - (int) Math.round(beginx / eenheidx);
			int bx = (int)Math.round(beginx);
			for (int i = imin; i < imax; i++)
			{	//g.setColor(Color.lightGray);
				g.setStrokeStyle(CssColor.make(192,192,192));
				
				//g.drawLine((int) (bx + i * eenheidxD), 0, (int) (bx + i * eenheidxD), hoogte);
				g.beginPath();
				g.moveTo(gvX + bx + i * eenheidxD, gvY + 0);
				g.lineTo(gvX + bx + i * eenheidxD, gvY + gvHoogte);
				g.stroke();
			}
			int jmin = -(int) Math.round(beginy / eenheidy); 
			int jmax = 1 + gvHoogte / eenheidy - (int) Math.round(beginy / eenheidy);
			int by = (int)Math.round(beginy);
			for(int j=jmin ; j<jmax ; j++)
			{	//g.setColor(Color.lightGray);
				g.setStrokeStyle(CssColor.make(192,192,192));
				
				//g.drawLine(0,(int)(hoogte-(by+j*eenheidyD)),breedte,(int)(hoogte-(by+j*eenheidyD)));
				g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by+j*eenheidyD));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by+j*eenheidyD));
				g.stroke();
			}	
			//g.setColor(Color.black);
			g.setStrokeStyle(CssColor.make(0,0,0));
			if(bx>1 && bx<gvBreedte)
			{	//g.drawLine(bx-1,0,bx-1,hoogte);	
				g.beginPath();
				g.moveTo(gvX + bx-1,gvY + 0);
				g.lineTo(gvX + bx-1,gvY + gvHoogte);
				g.stroke();
					
				//g.drawLine(bx,0,bx,hoogte);
				g.beginPath();
				g.moveTo(gvX + bx,gvY + 0);
				g.lineTo(gvX + bx,gvY + gvHoogte);
				g.stroke();

			}
			if(by>0 && by<gvHoogte)
			{	
				//g.drawLine(0,hoogte-(by+1),breedte,hoogte-(by+1));
				g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by+1));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by+1));
				g.stroke();
								
				//g.drawLine(0,hoogte-(by),breedte,hoogte-(by));
				g.beginPath();
				g.moveTo(gvX + 0,gvY + gvHoogte-(by));
				g.lineTo(gvX + gvBreedte,gvY + gvHoogte-(by));
				g.stroke();
			}
			g.setFillStyle(CssColor.make(0,0,0));
			
			//g.drawString("O",bx-10,hoogte-by+12);
// yPos??
			g.fillText("O",gvX + bx-10,gvY + gvHoogte-by+12);
			
			
			for(int j=0 ; j<aantalPijlenIn ; j++)
			{	if(isLijnGrafiek[j] && expressies[j]!=null && expressies[j].geefVarNaam()!=null && 
					varNaam.equals(expressies[j].geefVarNaam())&& !expressies[j].geefVarNaam().equals("qq"))
				{	
					//g.setColor(pijlenIn[j].getColor());
					g.setStrokeStyle(pijlenIn[j].getColor());
					
					for(int i=0 ; i<gvBreedte ; i++)
					{	double ii = i;
						double d0 = expressies[j].geefW(schaalFactorX*(-beginx)/eenheidxD + schaalFactorX*ii/eenheidxD);
						double d1 = expressies[j].geefW(schaalFactorX*(-beginx)/eenheidxD + schaalFactorX*(ii+1)/eenheidxD);
						if(!Double.isNaN(d0) && !Double.isNaN(d1))
						{	int x0 = i;
							int x1 = i+1;
							double dy0 = Math.round(gvHoogte -(beginy+eenheidyD*d0/schaalFactorY));
							double dy1 = Math.round(gvHoogte -(beginy+eenheidyD*d1/schaalFactorY));
							if(dy0>1000)dy0 = 1000;
							if(dy0<-1000)dy0 = -1000;
							if(dy1>1000)dy1 = 1000;
							if(dy1<-1000)dy1 = -1000;
							int y0 = (int)dy0;
							int y1 = (int)dy1;
							//g.drawLine(x0,y0,x1,y1);
							if ((y0 >= 0) && (y0 <= gvHoogte) && (y1 >= 0) && (y1 <= gvHoogte))
							{
							g.beginPath();
							g.moveTo(gvX + x0,gvY + y0);
							g.lineTo(gvX + x1,gvY + y1);
							g.stroke();
							}

						}
					}
				}
				else if(isPuntGrafiek[j] && expressies[j]!=null && expressies[j].geefVarNaam()==null && !Double.isNaN(puntXWaarde[j]))
				{	double d = bx+1.0*((puntXWaarde[j])*eenheidx/schaalFactorX);
					int x = (int)d;
					double d0 = expressies[j].geefW((puntXWaarde[j])*schaalFactorX);
					int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
					//g.setColor(pijlenIn[j].getColor());
					g.setFillStyle(pijlenIn[j].getColor());
					//g.fillOval(x-2,y-2,5,5);
					if ((y >= 0) && (y <= gvHoogte))
					{
					g.beginPath();
                    g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
               	 	g.fill();
					}

					//g.setFont(font);
					g.setFont(fontString);
					//fm = g.getFontMetrics();
					
					//String xString = dfTrace.format(puntXWaarde[j]);
					//String yString = dfTrace.format(d0);
					String xString = UF.format0(puntXWaarde[j], 4);
               	 	String yString = UF.format0(d0, 4);
					
					int woordBreedte = 40;
					
					TextMetrics tm = g.measureText(xString + yString);
					woordBreedte = (int) Math.round(tm.getWidth());

					//if(fm!=null) woordBreedte = fm.stringWidth(xString+yString);
					
					if ((y >= 0) && (y <= gvHoogte))
					{
					//g.setColor(new Color(255,255,225));
					g.setFillStyle(CssColor.make(255,255,225));
					//g.fillRect(x+6,y-7,woordBreedte+20,15);
					g.fillRect(gvX + x+6,gvY + y-7,woordBreedte+20,15);
					//g.setColor(Color.black);
					g.setFillStyle(CssColor.make(0,0,0));
					//g.drawString("(" + xString + " , " + yString + ")", x+8,y+5);
// yPos??					
					g.fillText("(" + xString + " , " + yString + ")", gvX + x+8,gvY + y+5);
					}
					
				}
				if(isMeerPuntenGrafiek[j] && expressies[j]!=null)
				{	//g.setColor(pijlenIn[j].getColor());
					g.setFillStyle(pijlenIn[j].getColor());
					for (int k = 0; k<8; k++) 
					{	double d = bx+1.0*((k+beginwaarde)*eenheidx);
						int x = (int)d;
						if(expressies[j].isWaarde((k+beginwaarde)*schaalFactorX) && k<8 )
						{	double d0 = expressies[j].geefW((k+beginwaarde)*schaalFactorX);
							int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
							//g.fillOval(x-2,y-2,5,5);
							if ((y >= 0) && (y <= gvHoogte))
							{
							g.beginPath();
		                    g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
		               	 	g.fill();
							}
						}
				    }
					for (int k = 0; k<8; k++) 
					{	double d = bx+1.0*((k+beginwaarde)*eenheidx);
						int x = (int)d;
						if(expressies[j].isWaarde((k+beginwaarde)*schaalFactorX) && k<8 )
						{	double d0 = expressies[j].geefW((k+beginwaarde)*schaalFactorX);
							int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
							
							if(new Rectangle(x-2,y-2,5,5).contains(movex, movey))
							{	//g.setFont(font);
								g.setFont(fontString);
								
								//fm = g.getFontMetrics();
								//String xString = dfTrace.format((k+beginwaarde)*schaalFactorX);
								//String yString = dfTrace.format(d0);
								String xString = UF.format0((k+beginwaarde)*schaalFactorX,4);
								String yString = UF.format0(d0,4);
								int woordBreedte = 40;
								
								TextMetrics tm = g.measureText(xString + yString);
								woordBreedte = (int) Math.round(tm.getWidth());
							
								if ((y >= 0) && (y <= gvHoogte))
								{
								//if(fm!=null) woordBreedte = fm.stringWidth(xString+yString);
								//g.setColor(new Color(255,255,225));
								g.setFillStyle(CssColor.make(255,255,225));
								//g.fillRect(x+6,y-7,woordBreedte+20,15);
								g.fillRect(gvX + x+6,gvY + y-7,woordBreedte+20,15);
								//g.setColor(Color.black);
								g.setFillStyle(CssColor.make(0,0,0));
								
								//g.drawString("(" + xString + " , " + yString + ")", x+8,y+5);
// yPos??					
								g.fillText("(" + xString + " , " + yString + ")", gvX + x+8,gvY + y+5);
								}
								
							}
						}
				    }
					
					if(isPuntGrafiek[j] && !Double.isNaN(puntXWaarde[j]))
					{	double d = bx+1.0*((selectnummer+beginwaarde)*eenheidx);
                        int x = (int)d;
                        d = bx+1.0*((puntXWaarde[j])*eenheidx/schaalFactorX);
						x = (int)d;
						double d0 = expressies[j].geefW((puntXWaarde[j]));
						int y = (int)Math.round(gvHoogte -(beginy+eenheidy*d0/schaalFactorY));
						//g.setColor(Color.black);
						g.setFillStyle(CssColor.make(0,0,0));
						//g.fillOval(x-2,y-2,5,5);
						if ((y >= 0) && (y <= gvHoogte))
						{
						g.beginPath();
	                    g.arc(gvX + x,gvY + y,2,0,2 * Math.PI);
	               	 	g.fill();
						}
                        
                        //g.setFont(font);
                        g.setFont(fontString);
                        
                        //fm = g.getFontMetrics();
                        //String xString = dfTrace.format(puntXWaarde[j]);
                        //String yString = dfTrace.format(d0);
                        
                        String xString = UF.format0(puntXWaarde[j],4);
                        String yString = UF.format0(d0,4);
                        
                        int woordBreedte = 40;
                        
                        //if(fm!=null) woordBreedte = fm.stringWidth(xString+yString);
                        
    					TextMetrics tm = g.measureText(xString + yString);
    					woordBreedte = (int) Math.round(tm.getWidth());

    					if ((y >= 0) && (y <= gvHoogte))
						{
                        //g.setColor(new Color(255,255,225));
                        g.setFillStyle(CssColor.make(255,255,225));
                        //g.fillRect(x+6,y-7,woordBreedte+20,15);
                        g.fillRect(gvX + x+6,gvY + y-7,woordBreedte+20,15);
                        //g.setColor(Color.black);
                        g.setFillStyle(CssColor.make(0,0,0));
                        //g.drawString("(" + xString + " , " + yString + ")", x+8,y+5);
//yPos                        
                        g.fillText("(" + xString + " , " + yString + ")", gvX + x+8,gvY + y+5);
						}
                        
					}
							
				}
				
//check dit nog voor tekenen buiten grafiekveld				
				if(expressies[j]!=null && trace || expressies[j]!=null && selectnummer<8 && selectnummer>-1)
				{	double d = bx+1.0*((selectnummer+beginwaarde)*eenheidx);
                    int x = (int)Math.round(d);
                    if(!tracing && selectnummer<8 && selectnummer>-1)
                    {   tracexD = d;
                        tracex = x;
// GWT                        
                        //slider.zetStand(tracex);
                    }
                    boolean b = varNaam.equals("qq") || varNaam.length()>2 && varNaam.substring(0,2).equals("qq");
            		
                    if ((isLijnGrafiek[j])&& trace && !isPuntGrafiek[j] || isMeerPuntenGrafiek[j])
                    {   
//GWT                    	
                    	//slider.zetStand(tracex);
                        double dTraceX = schaalFactorX*(-beginx)/eenheidxD + schaalFactorX*tracexD/eenheidxD;
    					double dTraceY = expressies[j].geefW(dTraceX);
    					if(!b && !Double.isNaN(dTraceY) && tracex<veldb && tracex>-1)
    					{	int tracey = (int)Math.round(gvHoogte -(beginy+eenheidy*dTraceY/schaalFactorY));
    					
                            //g.setColor(traceKleur);
                            g.setFillStyle(traceKleur);
                            //g.fillOval(tracex-2,tracey-2,5,5);
                            g.beginPath();
    	                    g.arc(gvX + tracex,gvY + tracey,2,0,2 * Math.PI);
    	               	 	g.fill();
    	               	 	
    						//drawDottedLine(g,tracex,hoogte,tracex,tracey);
    						//drawDottedLine(g,0,tracey,tracex,tracey);
    						drawDottedLine(g,gvX + tracex,gvY + gvHoogte,gvX + tracex,gvY + tracey);
    						drawDottedLine(g,gvX + 0,gvY + tracey,gvX + tracex,gvY + tracey);
    					
                            if ((isLijnGrafiek[j] || isMeerPuntenGrafiek[j])&& trace && !isPuntGrafiek[j])
                            {    
        						//String xWaarde = dfTrace.format(dTraceX);
        						//String yWaarde = dfTrace.format(dTraceY);
        						String xWaarde = UF.format0(dTraceX,2);
        						String yWaarde = UF.format0(dTraceY,2);
        						
        						//g.setFont(font);
        						g.setFont(fontString);
        						
        						//fm = g.getFontMetrics();
        						//int woordBreedteX = fm.stringWidth(xWaarde);
        						//int woordHoogteX = fm.getAscent();
        						
								TextMetrics tm = g.measureText(xWaarde);
								int woordBreedteX = (int) Math.round(tm.getWidth());
//check								
								int woordHoogteX = 10; 
        						
        						//int woordBreedteY = fm.stringWidth(yWaarde);
        						//int woordHoogteY = fm.getAscent();
        						
								tm = g.measureText(yWaarde);
								int woordBreedteY = (int) Math.round(tm.getWidth());
//check								
								int woordHoogteY = 10; 
        						
        						//g.setColor(new Color(255,255,200));
        						g.setFillStyle(CssColor.make(255,255,200));
        						//g.fillRect(tracex-woordBreedteX/2-2, hoogte-woordHoogteX-2, woordBreedteX+4, woordHoogteX+2);
        						g.fillRect(gvX + tracex-woordBreedteX/2-2, gvY + gvHoogte-woordHoogteX-2, woordBreedteX+4, woordHoogteX+2);
        						//g.fillRect(0, tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
        						g.fillRect(gvX + 0, gvY + tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
        						//g.setColor(Color.black);
        						g.setStrokeStyle(CssColor.make(0,0,0));
        						//g.drawRect(tracex-woordBreedteX/2-2, hoogte-woordHoogteX-2, woordBreedteX+4, woordHoogteX+2);
        						g.strokeRect(gvX + tracex-woordBreedteX/2-2, gvY + gvHoogte-woordHoogteX-2, woordBreedteX+4, woordHoogteX+2);
        						//g.drawRect(0, tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
        						g.strokeRect(gvX + 0, gvY + tracey-woordHoogteY/2-2, woordBreedteY+4, woordHoogteY+4);
        						
        						g.setFillStyle(CssColor.make(0,0,0));
        						
        						//g.drawString(xWaarde, tracex-woordBreedteX/2, hoogte-2);
//yPos?        						
        						g.fillText(xWaarde, gvX + tracex-woordBreedteX/2, gvY + gvHoogte-2);
        						        						
        						//g.drawString(yWaarde, 2, tracey+woordHoogteY/2);
//yPos?        						
        						g.fillText(yWaarde, gvX + 2, gvY + tracey+woordHoogteY/2);
        						
                            }
    					}
                    }
				}
			} // for
			

		}
	}
}
