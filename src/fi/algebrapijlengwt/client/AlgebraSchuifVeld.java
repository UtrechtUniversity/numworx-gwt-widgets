package fi.algebrapijlengwt.client;

//import fi.algebrapijlengwt.client.UitvoerSchuifComponent.MenuCommand;
import fi.algebrapijlengwt.client.expressies_ap.*;

import java.util.*;
import java.util.logging.Logger;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

public class AlgebraSchuifVeld 
{	
	//private static Logger logger = Logger.getLogger("ASV");
	
	public boolean changed = false;
	
	int aantalSc;	
	AlgebraSchuifComponent[] schuifcomponenten;
	AlgebraSchuifComponent actieveComponent;
	Pijl actievePijl;
	TabelComponent actieveTabel;
	
	public static int basisB = 50;
	public static int basisH = 20;
	
	GrafiekComponent grafiekComponent;
	
	//JButton kijkNaKnop;

// nodig?	
	private boolean selecterenMogelijk;
	private boolean selecterenBezig;
	private boolean selectieGemaakt;
	private Rectangle clip;
	
	boolean mouseDown;
	
//niet nodig, dit is voor de docent 	
	//private JPopupMenu popup;
	//JMenuItem copyItem; 

	boolean links = false;
	
	Map<String,Object> editmodeState;
	
// alleen voor de leraar?	
	boolean fixed = false;
	boolean frozen = false;
	
	public ZoomStateHolder zoomStateHolder;
	
	private boolean buttonsAdded;

	// parametrisatie
	boolean toolkit = true;
	boolean alleenInvullen = false;
	boolean isDemo = false;
	
	boolean brugklas = false;
	boolean terugHeen = true;
	boolean tabelOptie = true;
	boolean grafiekOptie = true;
	
//GWT scrollen in de invoervakjes	
	//boolean scrollOptie = true;
	boolean scrollOptie = false;
	boolean zoomOptie = true; 
	
	int xPos, yPos, breedte, hoogte;
	Context2d asvContext2d;
	int toolsWidth = 100;
	
	AlgebraPijlenGWT owner;
	
	boolean toonTabellen = false;

	PopupPanel menuPopup;
	MenuBar menuBar;
	MenuItem copyItem, pasteItem;

	LayoutPanel inputOwner;
	
	public AlgebraSchuifVeld(int x, int y, int b, int h, Context2d ct2d, AlgebraPijlenGWT o)
	{	//super(x, y, b, h);
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
		asvContext2d = ct2d;
		owner = o;
	
		inputOwner = owner.canvasPanel;
		
		zoomStateHolder = new ZoomStateHolder(this);

// in AlgebraPijlen wordt ip niet gebruikt		
/*		
		ip = new InvulPanel(this, 8, 399, 90, 45);
		ip.setBackground(Color.lightGray);
		ip.setVisible(false);
		add(ip);
*/
		
		
/*		
		kijkNaKnop = new JButton(AlgebraPijlenOpdr.rb.getString("kijkNaTekst"));
		kijkNaKnop.setFont(font);
		kijkNaKnop.setBounds(110 + (getSize().width - 110 - 90) / 2, getSize().height - 30, 90, 20);
		kijkNaKnop.setVisible(false);
		add(kijkNaKnop,0);
*/		
		
		
//		add(schuiflaag, 0);
		
		//HIER
		toolkit = owner.toolkit;
		alleenInvullen = owner.alleenInvullen;
		isDemo = owner.isDemo;

//System.out.println("toolkit " + toolkit);
//System.out.println("alleenInvullen " + alleenInvullen);
//System.out.println("isDemo " + isDemo);

		brugklas = owner.brugklas;
		
		menuBar = new MenuBar(true);
		copyItem = new MenuItem("kopieren", new MenuCommand("copy"));
		pasteItem = new MenuItem("plakken", new MenuCommand("paste"));
		menuBar.addItem(copyItem);
		menuBar.addItem(pasteItem);

		
		if (toolkit)
			maakStapel();
		
// zie beneden		
		//grafiekComponent = new GrafiekComponent(this, 400, 200, 200, 130);
		//grafiekComponent.isStapel = false;

	}

/*	
	public void disableElements(boolean b)
	{
//		terugKnop.setEnabled(!b);
//		tabelCheckbox.setEnabled(!b);
//		grafiekCheckbox.setEnabled(!b);
//		wisKnop.setEnabled(!b);
//		kijkNaKnop.setEnabled(!b);
		frozen = b;
	}
*/	
	public void answerChanged()
	{
		
//logger.info("ASV answerChanged");		
		changed = true;
		removeGoedFout();
		owner.answerChanged();
	}

	public void removeGoedFout()
	{	 
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{
			if (schuifcomponenten[cCnt] != null)
			{
				for (int pCnt = 0; pCnt < schuifcomponenten[cCnt].pijlUit.length; pCnt++)
   				{
   					if (schuifcomponenten[cCnt].pijlUit[pCnt] != null)	
   					{
   						schuifcomponenten[cCnt].pijlUit[pCnt].im = null;
   					}
   					
   				}
   				
			}
		}	
	}

	public boolean isEindUVS(UitvoerSchuifComponent uvs)
	{	if (uvs.isStapel)
			return false;
		if ((uvs.geefUitvoer(0) == null) && (uvs.geefVerborgenUitvoer(0) == null))
			return false;
		// dit gebeurt niet?
		if (uvs.pijlUit[0] == null)
			return true;
		boolean einde = true;
		for (int pCnt = 0; pCnt < uvs.pijlUit.length; pCnt++)
		{	
			
			if ((uvs.pijlUit[pCnt] != null) && uvs.pijlUit[pCnt].vast 
				&& !(uvs.pijlUit[pCnt].ontvanger instanceof GrafiekComponent)
				)
				einde = false;
			
		}
		return einde;
	}
	
	public Vector vindExpressieUVS()
	{	Vector result = new Vector();
		
		for (int sCnt = 0; sCnt < aantalSc; sCnt++)
		{
			if (schuifcomponenten[sCnt] instanceof UitvoerSchuifComponent)
			{	
	
				UitvoerSchuifComponent uvs = (UitvoerSchuifComponent) schuifcomponenten[sCnt];
				if (//(uvs.pijlIn1 != null) &&
					//!uvs.isStapel &&	
					//((uvs.pijlUit[0] == null) || 
					 //(!uvs.pijlUit[0].actief && (!uvs.pijlUit[0].vast || 
					  //(uvs.pijlUit[0].vast && (uvs.pijlUit[0].ontvanger instanceof GrafiekComponent)))))
					isEindUVS(uvs)	
				   ) 	
				{
//System.out.println("pijlen OK");

					result.addElement(uvs);
				}
				
			}
		}
	
		return result;
	}
	
	public void zetBeginExpressie(Expressie exp)
	{	// kijk of er al een beginExpUVS is
		UitvoerSchuifComponent beginExpUVS = null;
		for (int cnt = 0; cnt < aantalSc; cnt++)
		{
			if (schuifcomponenten[cnt] instanceof UitvoerSchuifComponent && 
				((UitvoerSchuifComponent) schuifcomponenten[cnt]).isBeginExpressie)
			{
				beginExpUVS = (UitvoerSchuifComponent) schuifcomponenten[cnt];
			}
				
		}
		
		if (beginExpUVS != null)
		{	
			beginExpUVS.zetExpressie(exp);
		}
		else
		{
			//beginExpUVS = new UitvoerSchuifComponent(this, 130, 45, 50, 20);
			beginExpUVS = new UitvoerSchuifComponent(this, 130, 45, basisB, basisH);
			
			
			beginExpUVS.zetTabelAan(toonTabellen);
			
			beginExpUVS.zetScroll(true);
			beginExpUVS.isBeginExpressie = true;
			beginExpUVS.isStapel = false;
			beginExpUVS.zetExpressie(exp);
			
			schuifcomponenten[aantalSc] = beginExpUVS;
			schuifcomponenten[aantalSc].zetLinks(links);
			Pijl p = new Pijl(this);
			p.zetLinks(links);
			schuifcomponenten[aantalSc].voegPijlToe(p);
			
//			add(schuifcomponenten[aantalSc]);
			
			aantalSc++;
		}
		
		tekenOpnieuw();
		
	}
	public boolean veldIsLeeg()
	{	int veldCnt = 0;
		for (int vCnt = 0; vCnt < aantalSc; vCnt++)
		{
//GWT			
			if (!schuifcomponenten[vCnt].isStapel 
				//&& !(schuifcomponenten[vCnt] instanceof GrafiekComponent)
				)
			{
				veldCnt++;
			}
		}
		
		return (veldCnt == 0);
	}
	
	public int getAantalVeldSc()
	{
		int veldCnt = 0;
		for (int vCnt = 0; vCnt < aantalSc; vCnt++)
		{
			if (!schuifcomponenten[vCnt].isStapel)
			{
				veldCnt++;
			}
		}
		
		return veldCnt;
	}
	public void maakVeldLeeg()
	{
		for (int vCnt = (aantalSc - 1); vCnt >= 0; vCnt--)
		{
			if (!schuifcomponenten[vCnt].isStapel)
				verwijder(schuifcomponenten[vCnt]);
		}
	}
	
/*	
	public void zetPlaatjes(Image gk, Image fk, Image kh)
	{	GOEDKRUL = gk;
		GOEDKRULHALF = kh;
		FOUTKRUIS = fk;
	}
*/	
	public void setFixed(boolean b)
	{	fixed = b;
/*	
		hidePanel.setVisible(b);
		terugKnop.setVisible(!b);
		heenKnop.setVisible(!b);
		wisKnop.setVisible(!b);
		tabelCheckbox.setVisible(!b);
		grafiekCheckbox.setVisible(!b);
*/		
	}
	
	public void zetToolkit(boolean b)
	{
		toolkit = b;
		if (toolkit)
		{
			alleenInvullen = false;
			isDemo = false;
			
		}
	}
	
	public void zetAlleenInvullen(boolean b)
	{
		alleenInvullen = b;
		if (alleenInvullen)
		{
			toolkit = false;
			isDemo = false;

		}
	}
	
	public void zetIsDemo(boolean b)
	{
		isDemo = b;
		if (isDemo)
		{
			toolkit = false;
			alleenInvullen = false;
			
		}
	}
	
// als b==true, wat te doen met kettingen die niet-brugklas dingen bevatten?
// voorlopig maar even niets	
	public void zetBrugklas(boolean b)
	{
		brugklas = b;
		for (int i = 0; i < aantalSc; i++)
		{	if ((schuifcomponenten[i].isStapel) &&
				((schuifcomponenten[i] instanceof OmkeringSchuifComponent) ||
				 (schuifcomponenten[i] instanceof WortelSchuifComponent) ||
				 (schuifcomponenten[i] instanceof MachtSchuifComponent))
				)
			{
				
				schuifcomponenten[i].setVisible(!b);
				
				
				for (int pCnt = 0; pCnt < schuifcomponenten[i].aantalPu; pCnt++)
				{	if (schuifcomponenten[i].pijlUit[pCnt] != null)
						schuifcomponenten[i].pijlUit[pCnt].setVisible(!b);
					
				}
			}
		}
		
		//layoutKnoppen();
		
		//tekenOpnieuw();
	}
	
	public void zetTerugHeen(boolean b)
	{
		terugHeen = b;
		//tekenOpnieuw();
	}
	
	public void zetTabelOptie(boolean b)
	{
		tabelOptie = b;
		//tekenOpnieuw();
	}

	public void zetGrafiekOptie(boolean b)
	{
		grafiekOptie = b;
		//tekenOpnieuw();
	}
	
	public void zetScrollOptie(boolean b)
	{
		scrollOptie = b;
		
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				((UitvoerSchuifComponent) schuifcomponenten[i]).zetScroll(scrollOptie);
			}
		}
			
	}

	public void zetZoomOptie(boolean b)
	{
		zoomOptie = b;

		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				((UitvoerSchuifComponent) schuifcomponenten[i]).zetZoomInTabel(zoomOptie);
			}
		}
		
		//tekenOpnieuw();
		
	}
	
/*	
	public void zetKijkNaActief(boolean b)
	{
		kijkNaKnop.setVisible(b);
		
	}
*/	
	
	public String getClassName(AlgebraSchuifComponent asv)
	{	String result = ""; 
	
		if (asv instanceof AftrekSchuifComponent)
			return "AftrekSchuifComponent";
		else if (asv instanceof DeelSchuifComponent)
			return "DeelSchuifComponent";
		else if (asv instanceof MachtSchuifComponent)
			return "MachtSchuifComponent";
		else if (asv instanceof OmkeringSchuifComponent)
			return "OmkeringSchuifComponent";
		else if (asv instanceof OptelSchuifComponent)
			return "OptelSchuifComponent";
		else if (asv instanceof UitvoerSchuifComponent)
			return "UitvoerSchuifComponent";
		else if (asv instanceof VermenigvuldigSchuifComponent)
			return "VermenigvuldigSchuifComponent";
		else if (asv instanceof WortelSchuifComponent)
			return "WortelSchuifComponent";
		else if (asv instanceof GrafiekComponent)
			return "GrafiekComponent";
	
		return result;
	}

	public void copy()
	{
//System.out.println("copy");		
		//HashMap<String,Object> h = getCopyTable();
//owner.logger.info("pre encode");		
		//String s = StringCodeObject.encodeObjectToString(h);
//owner.logger.info("post encode");		
		AlgebraPijlenGWT.clipBoard = getCopyTable();
		
//if ((s != null) && !s.equals(""))
//System.out.println("s not null");	
	}

	public HashMap<String,Object> getCopyTable()
	{	int aantalSc = 0;
		List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean tabel = false;
		boolean grafiek = false;
		//boolean expressie = false;
		//boolean links = false;
		Map<String,Object> zoomStateHolderState = null;
	
		aantalSc = this.aantalSc;
		
int stapelsCnt = 0;

		for (int i = 0; i < aantalSc; i++)
	    {	
			classNamesList.add(getClassName(schuifcomponenten[i]));
	    	posXList.add(new Integer(schuifcomponenten[i].xPos));
	    	posYList.add(new Integer(schuifcomponenten[i].yPos));
	    	scStatesList.add(schuifcomponenten[i].getState());
	    	
if (schuifcomponenten[i].isStapel)
stapelsCnt++;	
	    }
		//connections = new boolean[aantalSc][aantalSc];
		for (int i = 0; i < aantalSc; i++)
	    {	for (int j = 0; j < aantalSc; j++)
			{	//connections[i][j] = schuifcomponenten[j].pijlIn1 != null && 
	    		//					schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i];
				boolean b = schuifcomponenten[j].pijlIn1 != null && 
							schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i];
				connectionsList.add(new Boolean(b));
			}
	    }
	    
		
		if (owner.tabelBox != null)
			tabel = owner.tabelBox.getValue();
		if (owner.grafiekBox != null)
			grafiek = owner.grafiekBox.getValue();
			
//ip is er niet		
//	    expressie = ip.isExpr();
		
	    //links = this.links;
	    zoomStateHolderState = zoomStateHolder.getState();
	    	
	    //graphConnections = new int[10];
	    for (int i = 0; i < 10; i++)
		{	//graphConnections[i] = -1;
			graphConnectionsList.add(new Integer(-1));
		}
	    
	    
		if (grafiek)
	    {	for (int i = 0; i < 10; i++)
			{	Pijl p = grafiekComponent.pijlenIn[i];
				for (int j = 0; j < aantalSc; j++)
		   		{	if (p != null && schuifcomponenten[j] == p.zender)
		   			{	//graphConnections[i] = j;
		   				graphConnectionsList.set(i, new Integer(j));
		   			}
				}
			}
	    }
		
	    HashMap<String,Object> h = new HashMap<String,Object>();
	    h.put("aantalSc", new Integer(aantalSc));
//owner.logger.info("ASV getCopyTable " + aantalSc);
//owner.logger.info("ASV getCopyTable " + stapelsCnt);
	    h.put("classNamesList", classNamesList);
	    h.put("posXList", posXList);
	    h.put("posYList", posYList);
	    h.put("scStatesList", scStatesList);
	    h.put("connectionsList", connectionsList);
	    h.put("graphConnectionsList", graphConnectionsList);
	    h.put("tabel", new Boolean(tabel));
	    h.put("grafiek", new Boolean(grafiek));
	    //h.put("expressie", new Boolean(expressie));
	    //h.put("links", new Boolean(links));
	    h.put("zoomStateHolderState", zoomStateHolderState);
	    
	    //h.put("toolkit", new Boolean(toolkit));
	    //h.put("alleenInvullen", new Boolean(alleenInvullen));
	    //h.put("isDemo", new Boolean(isDemo));
	    
	    //h.put("brugklas", new Boolean(brugklas));
	    //h.put("terugHeen", new Boolean(terugHeen));
	    //h.put("tabelOptie", new Boolean(tabelOptie));
	    //h.put("grafiekOptie", new Boolean(grafiekOptie));
	    
	    //h.put("scrollOptie", new Boolean(scrollOptie));
	    //h.put("zoomOptie", new Boolean(zoomOptie));
	    
	    return h;
	}

	
	public HashMap<String,Object> getState()
	{	int aantalSc = 0;
		List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean tabel = false;
		boolean grafiek = false;
		boolean expressie = false;
		boolean links = false;
		Map<String,Object> zoomStateHolderState = null;
	
		aantalSc = this.aantalSc;
		for (int i = 0; i < aantalSc; i++)
	    {	
			classNamesList.add(getClassName(schuifcomponenten[i]));
	    	posXList.add(new Integer(schuifcomponenten[i].xPos));
	    	posYList.add(new Integer(schuifcomponenten[i].yPos));
	    	scStatesList.add(schuifcomponenten[i].getState());
	    }
		//connections = new boolean[aantalSc][aantalSc];
		for (int i = 0; i < aantalSc; i++)
	    {	for (int j = 0; j < aantalSc; j++)
			{	//connections[i][j] = schuifcomponenten[j].pijlIn1 != null && 
	    		//					schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i];
				boolean b = schuifcomponenten[j].pijlIn1 != null && 
							schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i];
				connectionsList.add(new Boolean(b));
			}
	    }
	    
		
		if (owner.tabelBox != null)
			tabel = owner.tabelBox.getValue();
		if (owner.grafiekBox != null)
			grafiek = owner.grafiekBox.getValue();
			
//ip is er niet		
//	    expressie = ip.isExpr();
		
	    links = this.links;
	    zoomStateHolderState = zoomStateHolder.getState();
	    	
	    //graphConnections = new int[10];
	    for (int i = 0; i < 10; i++)
		{	//graphConnections[i] = -1;
			graphConnectionsList.add(new Integer(-1));
		}
	    
	    
		if (grafiek)
	    {	for (int i = 0; i < 10; i++)
			{	Pijl p = grafiekComponent.pijlenIn[i];
				for (int j = 0; j < aantalSc; j++)
		   		{	if (p != null && schuifcomponenten[j] == p.zender)
		   			{	//graphConnections[i] = j;
		   				graphConnectionsList.set(i, new Integer(j));
		   			}
				}
			}
	    }
		
	    HashMap<String,Object> h = new HashMap<String,Object>();
	    h.put("aantalSc", new Integer(aantalSc));
	    h.put("classNamesList", classNamesList);
	    h.put("posXList", posXList);
	    h.put("posYList", posYList);
	    h.put("scStatesList", scStatesList);
	    h.put("connectionsList", connectionsList);
	    h.put("graphConnectionsList", graphConnectionsList);
	    h.put("tabel", new Boolean(tabel));
	    h.put("grafiek", new Boolean(grafiek));
	    h.put("expressie", new Boolean(expressie));
	    h.put("links", new Boolean(links));
	    h.put("zoomStateHolderState", zoomStateHolderState);
	    
	    h.put("toolkit", new Boolean(toolkit));
	    h.put("alleenInvullen", new Boolean(alleenInvullen));
	    h.put("isDemo", new Boolean(isDemo));
	    
	    h.put("brugklas", new Boolean(brugklas));
	    h.put("terugHeen", new Boolean(terugHeen));
	    h.put("tabelOptie", new Boolean(tabelOptie));
	    h.put("grafiekOptie", new Boolean(grafiekOptie));
	    
	    h.put("scrollOptie", new Boolean(scrollOptie));
	    h.put("zoomOptie", new Boolean(zoomOptie));
	    
	    return h;
	}

/*	
	public void setEditModeState(Map<String,Object> h)
	{	editmodeState = h;
		setState(h);
	}
*/	
	
    public void paste()
    {
    	if (AlgebraPijlenGWT.clipBoard != null)
    	{
    		
//System.out.println("paste");    		
    		//Object o = StringCodeObject.decodeStringToObject(AlgebraPijlenGWT.clipBoard);
    		//if (o == null)
    		//	return;
    		//HashMap<String,Object> h = (HashMap<String,Object>) o;
    		setPasteTable(AlgebraPijlenGWT.clipBoard);
//System.out.println("o not null");    		
    	}
    }

    public void setPasteTable(Map<String,Object> map)
    {	
    	
//owner.logger.info("ASV setPasteTable");    	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
    	int aantalPasteSc = 0;
    	List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean tabel = false;
		boolean grafiek = false;
		//boolean expressie = false;
		//boolean links = false;
		Map<String,Object> zoomStateHolderState = null;
		
		if (h.containsKey("aantalSc"))
			aantalPasteSc = h.getInt("aantalSc");
		if (h.containsKey("classNamesList"))
			classNamesList = h.getStringList("classNamesList");
		if (h.containsKey("posXList"))
			posXList = h.getIntegerList("posXList");
		if (h.containsKey("posYList"))
			posYList = h.getIntegerList("posYList");
		if (h.containsKey("scStatesList"))		
			scStatesList = h.getMapList("scStatesList");
		if (h.containsKey("connectionsList"))			
			connectionsList = h.getBooleanList("connectionsList");
		if (h.containsKey("graphConnectionsList"))			
			graphConnectionsList = h.getIntegerList("graphConnectionsList");
		//if (h.containsKey("tabel"))	
		//	tabel = h.getBoolean("tabel");
		if (h.containsKey("grafiek"))		
			grafiek = h.getBoolean("grafiek");
		//if (h.containsKey("expressie"))	
		//	expressie = h.getBoolean("expressie");
		//if (h.containsKey("links"))		
		//	links = h.getBoolean("links");
		if (h.containsKey("zoomStateHolderState"))	
			zoomStateHolderState = h.getMap("zoomStateHolderState");
		
		zoomStateHolder.setState(zoomStateHolderState);
		
//System.out.println("constr " + this.aantalSc);
//System.out.println("state " + aantalSc);
		
//owner.logger.info("ASV setPasteTable pasted " + aantalPasteSc);		
		
		int aantalStapels = aantalSc;
		
		
//owner.logger.info("ASV setPasteTable orig" + aantalStapels);		
				
		for (int i = 0; i < aantalPasteSc; i++)
	    {	
				String className = (String) classNamesList.get(i);
				int posX = ((Integer) posXList.get(i)).intValue();
				int posY = ((Integer) posYList.get(i)).intValue();

//System.out.println("posX = " + posX);

//if (alleenInvullen || isDemo)
//posX -= 110;	

	    		if (className.equals("AftrekSchuifComponent"))
	    			//|| 
	    			//className.equals("fi.algebrapijlenopdr.AftrekSchuifComponent"))
	    		{
	    			AlgebraSchuifComponent asv = new AftrekSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
	    		}
	    		else if (className.equals("DeelSchuifComponent"))
	    				//||
	    				// className.equals("fi.algebrapijlenopdr.DeelSchuifComponent"))
	    		{	
	    			AlgebraSchuifComponent asv = new DeelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
	    		}
				else if (className.equals("MachtSchuifComponent"))
						//||
						// className.equals("fi.algebrapijlenopdr.MachtSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new MachtSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("OmkeringSchuifComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.OmkeringSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new OmkeringSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("OptelSchuifComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.OptelSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new OptelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("UitvoerSchuifComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.UitvoerSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new UitvoerSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("VermenigvuldigSchuifComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.VermenigvuldigSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new VermenigvuldigSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("WortelSchuifComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.WortelSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new WortelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
				}
				else if (className.equals("GrafiekComponent"))
					//||
					//	 className.equals("fi.algebrapijlenopdr.GrafiekComponent"))
				{
					AlgebraSchuifComponent asv = new GrafiekComponent(this, posX, posY, 210, 200);
	    			schuifcomponenten[aantalStapels + i] = asv;
	    			aantalSc++;
	    			grafiekComponent = (GrafiekComponent) schuifcomponenten[i];
	    			
				}
	    	
	    }
//owner.logger.info("paste componenten gemaakt " + (aantalSc - aantalStapels));
	    for (int i = 0; i < (aantalSc - aantalStapels); i++)
	    {	
	    	
	    	if (!(schuifcomponenten[aantalStapels + i] instanceof GrafiekComponent)) 
	    	{	//schuifcomponenten[i].setState(scStates[i]);
//owner.logger.info("paste componenten " + (aantalStapels+i) + " setState");	    		
	    		schuifcomponenten[aantalStapels + i].setState((HashMap<String,Object>)scStatesList.get(i));
	    		
	    	}
	    	// bij een GrafiekComponent lukt dit niet omdat die bij setState de parent nodig heeft en die heeft ie nog niet
	    }
	    
//owner.logger.info("paste state componenten gezet");	    
		int max = aantalSc;
		for (int i = 0; i < (max - aantalStapels); i++)
		{	
			
			if (!(schuifcomponenten[aantalStapels + i] instanceof GrafiekComponent))
			{	
				Pijl p = new Pijl(this);
				if (schuifcomponenten[aantalStapels + i].isStapel) 
				{	schuifcomponenten[aantalStapels + i].zetLinks(links);
					p.zetLinks(links);
				}	
				schuifcomponenten[aantalStapels + i].voegPijlToe(p);
			}	
			//add(schuifcomponenten[i]);
		}
		
//owner.logger.info("paste pijlen gemaakt");

	    for (int i = 0; i < (aantalSc - aantalStapels); i++)
	    {	for(int j = 0; j < (aantalSc - aantalStapels); j++)
			{	boolean cij = ((Boolean) connectionsList.get(i*(aantalSc-aantalStapels)+j)).booleanValue();
	    		//if (connections[i][j])
				if (cij)
				{	Pijl p = schuifcomponenten[aantalStapels + i].pijlUit[schuifcomponenten[aantalStapels + i].aantalPu - 1];
					//schuifcomponenten[j].zetLinks(links);
					schuifcomponenten[aantalStapels + j].verbind(p);
					p.zetVerbonden(schuifcomponenten[aantalStapels + j]);
				}
			}
	    }
	    
//owner.logger.info("paste connecties gemaakt");
	    
	    if (grafiek)
	    {  	for (int i = 0; i < 10; i++)
			{	int gc = ((Integer) graphConnectionsList.get(i)).intValue(); 
	    		//if (graphConnections[i] != -1)
				if (gc != -1)
				{	//Pijl p = schuifcomponenten[graphConnections[i]].pijlUit[schuifcomponenten[graphConnections[i]].aantalPu - 1];
					Pijl p = schuifcomponenten[aantalStapels + gc].pijlUit[schuifcomponenten[aantalStapels + gc].aantalPu - 1];
					grafiekComponent.verbind(p, i);
					p.zetVerbonden(grafiekComponent);
				}
		    }
		}
		
		for (int i = 0; i < (aantalSc - aantalStapels); i++)
	    {	//schuifcomponenten[i].setState(scStates[i]);
	    	schuifcomponenten[aantalStapels + i].setState((HashMap<String,Object>)scStatesList.get(i));
	    }
	    
	    for (int i = 0; i < (aantalSc - aantalStapels); i++)
	    {	schuifcomponenten[aantalStapels + i].zetVeranderd(20);
	    	if (schuifcomponenten[aantalStapels + i] instanceof UitvoerSchuifComponent)
	    	{	((UitvoerSchuifComponent) schuifcomponenten[aantalStapels + i]).zetToonWaarde(true);
	    		((UitvoerSchuifComponent) schuifcomponenten[aantalStapels + i]).zetScroll(true);
	    		schuifcomponenten[aantalStapels + i].zetVeranderd(20);
	    	}
	    }
	    

	    
	    for (int i = 0; i < (aantalSc - aantalStapels); i++)
	    {	if (schuifcomponenten[aantalStapels + i] instanceof GrafiekComponent)
	    	{	//schuifcomponenten[i].setState(scStates[i]);
	    		schuifcomponenten[aantalStapels + i].setState((HashMap<String,Object>)scStatesList.get(i));
	    		schuifcomponenten[aantalStapels + i].zetVeranderd(20);
	    	}
	    }
    
	    if (owner.tabelBox != null)
	    	owner.tabelBox.setValue(tabel);
	    if (owner.grafiekBox != null)
	    	owner.grafiekBox.setValue(grafiek);	    
	    
/*	    
	    this.links = links;
	    if (links)
		{	for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i].isStapel)
				{	//schuifcomponenten[i].setLocation(schuifcomponenten[i].getLocation().x+10, schuifcomponenten[i].getLocation().y);
					schuifcomponenten[i].zetLinks(true);
				}
			}
		}
*/	    
	    
	    //Enumeration en = zoomStateHolder.keys();
	    Set keySet = zoomStateHolder.keySet();
		Object[] keys = keySet.toArray();
//owner.logger.info("zoomStateHolder keys = " + keys.length);	    
		//while(en.hasMoreElements())
		for (int kCnt = 0; kCnt < keys.length; kCnt++)	
		{	
			//String key = (String) en.nextElement();
			String key = (String) keys[kCnt];
			
			setZoomStates(key, zoomStateHolder.getZoomState(key));
		}

//owner.logger.info("paste zoomStates gezet");

//owner.logger.info("paste pre " + aantalSc);		

		for (int i = (aantalSc - 1); i >= aantalStapels; i--)
	    {	if (schuifcomponenten[i].isStapel)
	    		verwijder(schuifcomponenten[i]);
	    }
		
//owner.logger.info("paste post " + aantalSc);
		
//System.out.println("setState end");		
//System.out.println("b = " + getSize().width);
//owner.logger.info("ASV setPasteTable einde");		
		
		tekenOpnieuw();
			
    }

    public void setState(Map<String,Object> map)
    {	
    	
//owner.logger.info("ASV setState");    	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
    	int aantalSc = 0;
    	List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean tabel = false;
		boolean grafiek = false;
		boolean expressie = false;
		boolean links = false;
		Map<String,Object> zoomStateHolderState = null;
		
		if (h.containsKey("aantalSc"))
			aantalSc = h.getInt("aantalSc");
		if (h.containsKey("classNamesList"))
			classNamesList = h.getStringList("classNamesList");
		else if (h.containsKey("classNames"))
			classNamesList = h.getStringList("classNames");			
		if (h.containsKey("posXList"))
			posXList = h.getIntegerList("posXList");
		else if (h.containsKey("posX"))
			posXList = h.getIntegerList("posX");
		if (h.containsKey("posYList"))
			posYList = h.getIntegerList("posYList");
		else if (h.containsKey("posY"))
			posYList = h.getIntegerList("posY");
		if (h.containsKey("scStatesList"))		
			scStatesList = h.getMapList("scStatesList");
		else if (h.containsKey("scStates"))
			scStatesList = h.getMapList("scStates");
		if (h.containsKey("connectionsList"))			
			connectionsList = h.getBooleanList("connectionsList");
		else if (h.containsKey("connections"))
		{  // connections is boolean[][], flatten
			ObjectList list = h.getObjectList("connections");
			int size = list.size();
			for(int i = 0; i < size;i++) connectionsList.addAll(list.getBooleanList(i));
		}
		if (h.containsKey("graphConnectionsList"))			
			graphConnectionsList = h.getIntegerList("graphConnectionsList");
		else if (h.containsKey("graphConnections")) 
			graphConnectionsList = h.getIntegerList("graphConnections");
		if (h.containsKey("tabel"))	
			tabel = h.getBoolean("tabel");
		if (h.containsKey("grafiek"))		
			grafiek = h.getBoolean("grafiek");
		if (h.containsKey("expressie"))	
			expressie = h.getBoolean("expressie");
		if (h.containsKey("links"))		
			links = h.getBoolean("links");
		if (h.containsKey("zoomStateHolderState"))	
			zoomStateHolderState = h.getMap("zoomStateHolderState");
		
		zoomStateHolder.setState(zoomStateHolderState);
		
//System.out.println("constr " + this.aantalSc);
//System.out.println("state " + aantalSc);
		
		int n = this.aantalSc;
		for (int i = 0; i < n; i++)
		{	verwijder(schuifcomponenten[0]);
		}
//owner.logger.info("verwijderd");		
		this.aantalSc = aantalSc;
		schuifcomponenten = new AlgebraSchuifComponent[200];
		for (int i = 0; i < aantalSc; i++)
	    {	
				String className = (String) classNamesList.get(i);
				int posX = ((Integer) posXList.get(i)).intValue();
				int posY = ((Integer) posYList.get(i)).intValue();

//System.out.println("posX = " + posX);

//if (alleenInvullen || isDemo)
//posX -= 110;	

	    		if (className.equals("AftrekSchuifComponent") || 
	    			className.equals("fi.algebrapijlenopdr.AftrekSchuifComponent"))
	    		{
	    			AlgebraSchuifComponent asv = new AftrekSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
	    		}
	    		else if (className.equals("DeelSchuifComponent") ||
	    				 className.equals("fi.algebrapijlenopdr.DeelSchuifComponent"))
	    		{	
	    			AlgebraSchuifComponent asv = new DeelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
	    		}
				else if (className.equals("MachtSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.MachtSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new MachtSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("OmkeringSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.OmkeringSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new OmkeringSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("OptelSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.OptelSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new OptelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("UitvoerSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.UitvoerSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new UitvoerSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("VermenigvuldigSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.VermenigvuldigSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new VermenigvuldigSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("WortelSchuifComponent") ||
						 className.equals("fi.algebrapijlenopdr.WortelSchuifComponent"))
				{
					AlgebraSchuifComponent asv = new WortelSchuifComponent(this, posX, posY, 50, 20);
	    			schuifcomponenten[i] = asv;
				}
				else if (className.equals("GrafiekComponent") ||
						 className.equals("fi.algebrapijlenopdr.GrafiekComponent"))
				{
					AlgebraSchuifComponent asv = new GrafiekComponent(this, posX, posY, 210, 200);
	    			schuifcomponenten[i] = asv;
	    			grafiekComponent = (GrafiekComponent) schuifcomponenten[i];
	    			
				}
	    	
	    }
//owner.logger.info("componenten gemaakt " + aantalSc);
	    for (int i = 0; i < aantalSc; i++)
	    {	
	    	
	    	if (!(schuifcomponenten[i] instanceof GrafiekComponent)) 
	    	{	//schuifcomponenten[i].setState(scStates[i]);
//owner.logger.info("componenten " + i + " setState");	    		
	    		schuifcomponenten[i].setState((HashMap<String,Object>)scStatesList.get(i));
	    		
	    	}
	    	// bij een GrafiekComponent lukt dit niet omdat die bij setState de parent nodig heeft en die heeft ie nog niet
	    }
	    
//owner.logger.info("state componenten gezet");	    
		int max = aantalSc;
		for (int i = 0; i < max; i++)
		{	
			
			if (!(schuifcomponenten[i] instanceof GrafiekComponent))
			{	
				Pijl p = new Pijl(this);
				if (schuifcomponenten[i].isStapel) 
				{	schuifcomponenten[i].zetLinks(links);
					p.zetLinks(links);
				}	
				schuifcomponenten[i].voegPijlToe(p);
			}	
			//add(schuifcomponenten[i]);
		}
		
//owner.logger.info("pijlen gemaakt");

	    for (int i = 0; i < aantalSc; i++)
	    {	for(int j = 0; j < aantalSc; j++)
			{	boolean cij = ((Boolean) connectionsList.get(i*aantalSc+j)).booleanValue();
	    		//if (connections[i][j])
				if (cij)
				{	Pijl p = schuifcomponenten[i].pijlUit[schuifcomponenten[i].aantalPu - 1];
					//schuifcomponenten[j].zetLinks(links);
					schuifcomponenten[j].verbind(p);
					p.zetVerbonden(schuifcomponenten[j]);
				}
			}
	    }
	    
//owner.logger.info("connecties gemaakt");
	    
	    if (grafiek)
	    {  	for (int i = 0; i < 10; i++)
			{	int gc = ((Integer) graphConnectionsList.get(i)).intValue(); 
	    		//if (graphConnections[i] != -1)
				if (gc != -1)
				{	//Pijl p = schuifcomponenten[graphConnections[i]].pijlUit[schuifcomponenten[graphConnections[i]].aantalPu - 1];
					Pijl p = schuifcomponenten[gc].pijlUit[schuifcomponenten[gc].aantalPu - 1];
					grafiekComponent.verbind(p, i);
					p.zetVerbonden(grafiekComponent);
				}
		    }
		}
		
		for (int i = 0; i < aantalSc; i++)
	    {	//schuifcomponenten[i].setState(scStates[i]);
	    	schuifcomponenten[i].setState((HashMap<String,Object>)scStatesList.get(i));
	    }
	    
	    for (int i = 0; i < aantalSc; i++)
	    {	schuifcomponenten[i].zetVeranderd(20);
	    	if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
	    	{	((UitvoerSchuifComponent) schuifcomponenten[i]).zetToonWaarde(!expressie);
	    		((UitvoerSchuifComponent) schuifcomponenten[i]).zetScroll(true);
	    		schuifcomponenten[i].zetVeranderd(20);
	    	}
	    }
	    

	    
	    for (int i = 0; i < aantalSc; i++)
	    {	if (schuifcomponenten[i] instanceof GrafiekComponent)
	    	{	//schuifcomponenten[i].setState(scStates[i]);
	    		schuifcomponenten[i].setState((HashMap<String,Object>)scStatesList.get(i));
	    		schuifcomponenten[i].zetVeranderd(20);
	    	}
	    }
    
	    if (owner.tabelBox != null)
	    	owner.tabelBox.setValue(tabel);
	    if (owner.grafiekBox != null)
	    	owner.grafiekBox.setValue(grafiek);	    
	    
	    this.links = links;
	    if (links)
		{	for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i].isStapel)
				{	//schuifcomponenten[i].setLocation(schuifcomponenten[i].getLocation().x+10, schuifcomponenten[i].getLocation().y);
					schuifcomponenten[i].zetLinks(true);
				}
			}
		}
	    
//GWT zoek even lokaties op in maakStapel
	    
if (toolkit)	    
{	    
//owner.logger.info("toolkit");	
	    // nodig voor launchdata, bij de Java versie staan de componenten iets hoger
	    for (int i = 0; i < aantalSc; i++)
		{	if (schuifcomponenten[i].isStapel)
			{	if(schuifcomponenten[i] instanceof UitvoerSchuifComponent)schuifcomponenten[i].zetPlaats(20, 35);
				if(schuifcomponenten[i] instanceof OptelSchuifComponent)schuifcomponenten[i].zetPlaats(20, 90);
				if(schuifcomponenten[i] instanceof AftrekSchuifComponent)schuifcomponenten[i].zetPlaats(20, 115);
				if(schuifcomponenten[i] instanceof VermenigvuldigSchuifComponent)schuifcomponenten[i].zetPlaats(20, 140);
				if(schuifcomponenten[i] instanceof DeelSchuifComponent)schuifcomponenten[i].zetPlaats(20, 165);
				if(schuifcomponenten[i] instanceof OmkeringSchuifComponent)schuifcomponenten[i].zetPlaats(20, 190);
				if(schuifcomponenten[i] instanceof WortelSchuifComponent)schuifcomponenten[i].zetPlaats(20, 215);
				if(schuifcomponenten[i] instanceof MachtSchuifComponent)schuifcomponenten[i].zetPlaats(20, 240);
			}
		}
//owner.logger.info("einde toolkit");	    
}
else // alleenInvullen || isDemo
{
//owner.logger.info("alleen");	
//System.out.println("before " + this.aantalSc);	
	boolean launching = verwijderStapels();
//System.out.println("after " + this.aantalSc);	
	if (launching)
	{	for (int cnt = 0; cnt < this.aantalSc; cnt++)
		{
			if (schuifcomponenten[cnt] != null)
			{	int x = schuifcomponenten[cnt].xPos;
				int y = schuifcomponenten[cnt].yPos;
				AlgebraSchuifComponent asc = (AlgebraSchuifComponent) schuifcomponenten[cnt];
				for (int pCnt = 0; pCnt < asc.aantalPu; pCnt++)
					asc.zetPlaats(x - 100,y,asc.pijlUit[pCnt]);
//System.out.println("aantalPu " + asc.aantalPu);				
			}
//			else
//System.out.println("sc " + cnt + " null");				
		}
		for (int cnt = 0; cnt < this.aantalSc; cnt++)
		{
			if (schuifcomponenten[cnt] != null)
			{	//int x = schuifcomponenten[cnt].xPos;
				//int y = schuifcomponenten[cnt].yPos;
				AlgebraSchuifComponent asc = (AlgebraSchuifComponent) schuifcomponenten[cnt];
				//asc.zetPlaats(x - 100,y);
				if (asc.pijlIn1 != null)
					asc.verbind(asc.pijlIn1);
			
			}
		}
		
		//paint();
	
	}
}
	    //Enumeration en = zoomStateHolder.keys();
	    Set keySet = zoomStateHolder.keySet();
		Object[] keys = keySet.toArray();
//owner.logger.info("zoomStateHolder keys = " + keys.length);	    
		//while(en.hasMoreElements())
		for (int kCnt = 0; kCnt < keys.length; kCnt++)	
		{	
			//String key = (String) en.nextElement();
			String key = (String) keys[kCnt];
			
			setZoomStates(key, zoomStateHolder.getZoomState(key));
		}

//owner.logger.info("zoomStates gezet");

		zetToolkit(toolkit);
		zetAlleenInvullen(alleenInvullen);
		zetIsDemo(isDemo);
		
		boolean brugklas = false;
		if (h.containsKey("brugklas"))
			brugklas = h.getBoolean("brugklas");
		zetBrugklas(brugklas);
		
		boolean terugHeen = true;
		if (h.containsKey("terugHeen"))
			terugHeen = h.getBoolean("terugHeen");
		zetTerugHeen(terugHeen);
		
		boolean tabelOptie = true;
		if (h.containsKey("tabelOptie"))
			tabelOptie = h.getBoolean("tabelOptie");
		zetTabelOptie(tabelOptie);
		
		boolean grafiekOptie = true;
		if (h.containsKey("grafiekOptie"))
			grafiekOptie = h.getBoolean("grafiekOptie");
		zetGrafiekOptie(grafiekOptie);

		boolean scrollOptie = true;
		if (h.containsKey("scrollOptie"))
			scrollOptie = h.getBoolean("scrollOptie");
		zetScrollOptie(scrollOptie);
		
		boolean zoomOptie = true;
		if (h.containsKey("zoomOptie"))
			zoomOptie = h.getBoolean("zoomOptie");
		zetZoomOptie(zoomOptie);
		
//System.out.println("setState end");		
//System.out.println("b = " + getSize().width);
//owner.logger.info("ASV setState einde");		
		
		tekenOpnieuw();
			
    }

    public boolean verwijderStapels()
    {
    	boolean stapel = false;
    	SchuifComponent[] stapels = new SchuifComponent[20];
    	int sCnt = 0;
    	for (int i = 0; i < aantalSc; i++)
    	{	if (schuifcomponenten[i].isStapel)
    		{	stapels[sCnt] = schuifcomponenten[i];
    			sCnt++;
    			stapel = true;
    		}
    	}
//System.out.println("verwijder before " + aantalSc);    	
    	for (int j = 0; j < sCnt; j++)
    	{	verwijder((AlgebraSchuifComponent) stapels[j]);
    	}
    	
//System.out.println("verwijder after " + aantalSc);

    	return stapel;
    }

    public void tekenOpnieuw()
    {
//owner.logger.info("ASV tekenOpnieuw");    	
    	paint(asvContext2d);
    }
    
    
    public void paint()
    {
//owner.logger.info("ASV paint");    	
    	paint(asvContext2d);
    }
	//public void paint(Graphics g)
    public void paint(Context2d g)
	{	
    	
//logger.info("ASV paint " + aantalSc);

    	tekenAchtergrond(g);
    	

    	// teken de pijlen er direct bij
   		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
   		{	if ((schuifcomponenten[ascCnt] != null) && schuifcomponenten[ascCnt].isStapel)
   			{	schuifcomponenten[ascCnt].paint();
   				for (int pCnt = 0; pCnt < schuifcomponenten[ascCnt].pijlUit.length; pCnt++)
   				{	if (schuifcomponenten[ascCnt].pijlUit[pCnt] != null)
   					{	schuifcomponenten[ascCnt].pijlUit[pCnt].paint();
   					}
   				}
				if (schuifcomponenten[ascCnt].pijlIn1 != null)
				{	schuifcomponenten[ascCnt].pijlIn1.paint();
   				}
				
   			}
   		}
   		
   		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
   		{	if ((schuifcomponenten[ascCnt] != null) && !schuifcomponenten[ascCnt].isStapel &&
   				(schuifcomponenten[ascCnt] instanceof GrafiekComponent))
   			{	schuifcomponenten[ascCnt].paint();
   			}
   		}
   		
   		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
   		{	if ((schuifcomponenten[ascCnt] != null) && !schuifcomponenten[ascCnt].isStapel &&
   				!(schuifcomponenten[ascCnt] instanceof GrafiekComponent))
   			{	schuifcomponenten[ascCnt].paint();
   				for (int pCnt = 0; pCnt < schuifcomponenten[ascCnt].pijlUit.length; pCnt++)
   				{	if (schuifcomponenten[ascCnt].pijlUit[pCnt] != null)
   					{	schuifcomponenten[ascCnt].pijlUit[pCnt].paint();
   					}
   				}
				if (schuifcomponenten[ascCnt].pijlIn1 != null)
				{	schuifcomponenten[ascCnt].pijlIn1.paint();
   				}
				if (schuifcomponenten[ascCnt] instanceof UitvoerSchuifComponent)
				{
					UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) schuifcomponenten[ascCnt];
					if (uvsc.tabel != null)
					{	uvsc.tabel.paint();
						if (uvsc.zoomInTabel)
						{
							uvsc.zoomInKnop.paint();
							uvsc.zoomUitKnop.paint();
						}
					}
					
				}
   			}
   		}
   		
   		
   		
//GWT??
/*    	
		if ((selecterenBezig || selectieGemaakt) && clip != null)
		{	int b = clip.getSize().width;
			int h = clip.getSize().height;
			int x = clip.getLocation().x;
			int y = clip.getLocation().y;
			g.setColor(Color.red);
			g.drawRect(x,y,b,h);
		}
*/		
	}
	
	public void maakStapel()
	{	
		
//System.out.println("maakStapel");
//owner.logger.info("maakStapel");
		
		aantalSc = 0;
		int b = basisB; //50;
		int h = basisH; //20;
		
		schuifcomponenten = new AlgebraSchuifComponent[200];
		schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this, 20, 35, b, h);
		
		
		((UitvoerSchuifComponent) schuifcomponenten[aantalSc]).zetTabelAan(toonTabellen);
		
		((UitvoerSchuifComponent) schuifcomponenten[aantalSc]).zetScroll(scrollOptie);
		aantalSc++;
		
		schuifcomponenten[aantalSc] = new OptelSchuifComponent(this, 20, 90, b, h);
		aantalSc++;
		
		schuifcomponenten[aantalSc] = new AftrekSchuifComponent(this, 20, 115, b, h);
		aantalSc++;
		
		schuifcomponenten[aantalSc] = new VermenigvuldigSchuifComponent(this, 20, 140, b, h);
		aantalSc++;
		
		schuifcomponenten[aantalSc] = new DeelSchuifComponent(this, 20, 165, b, h);
		aantalSc++;
		
		if (!brugklas)
		{	
			schuifcomponenten[aantalSc] = new OmkeringSchuifComponent(this, 20, 190, b, h);		
			aantalSc++;
		
			schuifcomponenten[aantalSc] = new WortelSchuifComponent(this, 20, 215, b, h);			
			aantalSc++;
		
			schuifcomponenten[aantalSc] = new MachtSchuifComponent(this, 20, 240, b, h);			
			aantalSc++;
		}
		
		int max = aantalSc;
		for (int i = 0; i < max; i++)
		{	schuifcomponenten[i].zetLinks(links);
			Pijl p = new Pijl(this);
			p.zetLinks(links);
			schuifcomponenten[i].voegPijlToe(p);
			
			//add(schuifcomponenten[i]);
		}
		
//System.out.println("sc = " + schuifcomponenten.length);		

	}
	
	
	
	//public void tekenAchtergrond(Graphics g)
	public void tekenAchtergrond(Context2d g)
	{	
		//Dimension dd = getSize();
		
		//g.setColor(Color.white);
		g.setFillStyle(CssColor.make(255, 255, 255));
		
		g.fillRect(0, 0, breedte, hoogte);
		if (fixed)
		{	
//System.out.println("fixed");			
			
//GWT			
			//g.setColor(getParent().getBackground());//(Color.white);
			
			g.fillRect(0, 0, breedte, hoogte);
//GWT			
			//hidePanel.setBackground(getParent().getParent().getBackground());
			return;
		}
		if (toolkit)
		{	
			
			//g.setColor(new Color(210, 210, 210));
			g.setFillStyle(CssColor.make(210, 210, 210));
			
			g.fillRect(0, 0, toolsWidth, hoogte);
			
			//g.setColor(Color.gray);
			g.setStrokeStyle(CssColor.make(125, 125, 125));
			
			//g.drawLine(toolsWidth, 0, toolsWidth, dd.height - 1);
			g.beginPath();
			g.moveTo(toolsWidth, 0);
			g.lineTo(toolsWidth, hoogte - 1);
			g.stroke();
			
			g.strokeRect(0, 0, breedte - 1, hoogte - 1);
		
//System.out.println("achtergrond w = " + dd.width);		
		
		
			String fontString = "12px sans-serif";
			//g.setFont(font);
			g.setFont(fontString);
		
			//FontMetrics fm = g.getFontMetrics();
		
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0, 0, 0));
		
			//String s = AlgebraPijlenOpdr.rb.getString("invoerVakLabel");
			//String s = "In-/Uitvoer";
			String s = AlgebraPijlenGWT.rb.invoerLabel();
			TextMetrics tm = g.measureText(s);
		
			//int lengte = fm.stringWidth(s);
			int lengte = (int) Math.round(tm.getWidth());
		
			//g.drawString(s, 55 - lengte / 2, 25);
			g.fillText(s, 55 - lengte / 2, 25);
		
			//s = AlgebraPijlenOpdr.rb.getString("bewerkingenLabel");
			//s = "Bewerkingen";
			s = AlgebraPijlenGWT.rb.bewerkingenLabel();
			tm = g.measureText(s);
		
			//lengte = fm.stringWidth(s);
			lengte = (int) Math.round(tm.getWidth());
		
			//g.drawString(s, 55 - lengte / 2, 80);
			g.fillText(s, 55 - lengte / 2, 80);
		}
		else
		{
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			g.strokeRect(0, 0, breedte, hoogte);
			
		}
		
	}
	
	public void zetStapel(AlgebraSchuifComponent asc)
	{	
		//int x = asc.getLocation().x;
		//int y = asc.getLocation().y;
		//int b = asc.getSize().width;
		//int h = asc.getSize().height;
		
		int x = asc.xPos;
		int y = asc.yPos;
		int b = asc.breedte;
		int h = asc.hoogte;
		
//System.out.println("zetStapel " + x + "," + y + "," + b + "," + h);		
		
		if (asc instanceof UitvoerSchuifComponent)
		{	schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this ,x, y, b, h);
		  	
//GWT		
		  	//((UitvoerSchuifComponent) schuifcomponenten[aantalSc]).zetTabelAan(tabelCheckbox.isSelected());
		}
		else if (asc instanceof OptelSchuifComponent)
		{	schuifcomponenten[aantalSc] = new OptelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof AftrekSchuifComponent)
		{	schuifcomponenten[aantalSc] = new AftrekSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof VermenigvuldigSchuifComponent)
		{	schuifcomponenten[aantalSc] = new VermenigvuldigSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof DeelSchuifComponent)
		{	schuifcomponenten[aantalSc] = new DeelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof OmkeringSchuifComponent)
		{	schuifcomponenten[aantalSc] = new OmkeringSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof WortelSchuifComponent)
		{	schuifcomponenten[aantalSc] = new WortelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof MachtSchuifComponent)
		{	schuifcomponenten[aantalSc] = new MachtSchuifComponent(this ,x, y, b, h);
		}
		else 
			return;
		schuifcomponenten[aantalSc].zetLinks(links);
		Pijl p = new Pijl(this);
		p.zetLinks(links);
		schuifcomponenten[aantalSc].voegPijlToe(p);
		
		//add(schuifcomponenten[aantalSc]);
		
		aantalSc++;

	}
	
	public void verwijder(AlgebraSchuifComponent sc)
	{	for (int i = 0; i < aantalSc; i++)
		{	if (schuifcomponenten[i] == sc)
			{	
				if (sc instanceof GrafiekComponent)
				{	GrafiekComponent gsc = (GrafiekComponent) sc;
					while (gsc.aantalPijlenIn > 0)
					{	Pijl p = gsc.pijlenIn[0];
						gsc.maakLos(gsc.pijlenIn[0]);
						p.zender.verwijderPijl();
						p.pijlTerug();
					}
				}
			
				if (sc.pijlIn1 != null)
				{	Pijl p = sc.pijlIn1;
					sc.maakLos(sc.pijlIn1);
					p.zender.verwijderPijl();
					p.pijlTerug();
				}
				if (sc.pijlIn2 != null)
				{	Pijl p = sc.pijlIn2;
					sc.maakLos(sc.pijlIn2);
					p.zender.verwijderPijl();
					p.pijlTerug();
					
				}	
				for (int k = 0; k < sc.aantalPu; k++)
				{	if (sc.pijlUit[k].ontvanger != null)
					{	AlgebraSchuifComponent as = sc.pijlUit[k].ontvanger;
						as.maakLos(sc.pijlUit[k]);
						as.zetVeranderd(20);
					}	
//GWT??				
					//remove(sc.pijlUit[k]);
				}
//GWT??				
				//remove(sc);
				for (int j = i; j < aantalSc; j++)
				{	schuifcomponenten[j] = schuifcomponenten[j + 1];
				}
				aantalSc--;
				
				if (!owner.asvSetState)
					tekenOpnieuw();
				
//GWT				
				//copyItem.setEnabled(!veldIsLeeg());
				
				return;
			}
		}
	}
	
	public void zetVeranderd()
	{	for (int i = 0; i < aantalSc; i++)
		{	if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{	
				((UitvoerSchuifComponent) schuifcomponenten[i]).zetTabelAan(toonTabellen);
			}
		
//System.out.println(schuifcomponenten[i].getClass().getName());			
		}
		tekenOpnieuw();
	}
	
	
	public void zetTabellen(int beginwaarde, int selectnummer, String varN, double schaalFactorX)
	{	
	}
	
	public void setZoomStates(String varnaam, ZoomState zoomState)
	{	for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
				{	((UitvoerSchuifComponent) schuifcomponenten[i]).setZoomState(varnaam, zoomState);
					
				}
			
				if (schuifcomponenten[i] instanceof GrafiekComponent)
				{	((GrafiekComponent) schuifcomponenten[i]).setZoomState(varnaam, zoomState);
				}
			}
		if (!owner.asvSetState)
			tekenOpnieuw();
	}
	
	public void linksRechtsAction()
	{
		if (!links)
		{	links = true;
			for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i].isStapel)
				{	schuifcomponenten[i].xPos += 10;
					schuifcomponenten[i].zetLinks(true);
				}
			}
			tekenOpnieuw();
		}
	
		else // links
		{
			links = false;
			for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i].isStapel)
				{	schuifcomponenten[i].xPos -= 10;
					schuifcomponenten[i].zetLinks(false);
				}
			}
			tekenOpnieuw();
		}
	}
	
	public void wisAction()
	{
		if (editmodeState == null)
		{	links = false;
			int n = aantalSc;
			for (int i = 0; i < n; i++)
			{	verwijder(schuifcomponenten[0]);
			}
			maakStapel();
			if (owner.grafiekBox != null && owner.grafiekBox.getValue())
			{	
				
				//schuifcomponenten[aantalSc] = grafiekComponent;
				//aantalSc++;
				
				//add(grafiekComponent);
				toonGrafiekComponent(true);
				
			}
			zoomStateHolder = new ZoomStateHolder(this);
		}
		else
		{	setState(editmodeState);
		}
	}
	
	public void toonGrafiekComponent(boolean b)
	{
		if (b)
		{	
//tijdelijk	meer naar links		
			grafiekComponent = new GrafiekComponent(this, breedte - 220, 200, 210, 220);
			//grafiekComponent = new GrafiekComponent(this, breedte - 100, 200, 210, 220);
			schuifcomponenten[aantalSc] = grafiekComponent;
			aantalSc++;
			//add(grafiekComponent);
		}
		else
		{	
			if (actieveComponent == grafiekComponent)
				actieveComponent = null;
			verwijder(grafiekComponent);
			
		}
		tekenOpnieuw();
	}	
	
	
//GWT !!!!!!!!!!	
	
/*	
	public void actionPerformed(ActionEvent e)
	{	
		
		else if ((e.getSource() instanceof JMenuItem) &&
				 ((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("kopieerTekst")))
		{	if (!veldIsLeeg())
			{
				copy();
			}
		}
		else if ((e.getSource() instanceof JMenuItem) &&
				 ((JMenuItem)e.getSource()).getText().equals(AlgebraPijlenOpdr.rb.getString("plakTekst")))
		{	if ((AlgebraPijlenOpdr.clipBoard != null) && !AlgebraPijlenOpdr.clipBoard.equals(""))
			{	maakVeldLeeg();  
			
				paste();
			}
		}	
		else if (e.getSource() == tabelCheckbox)
		{	//boolean b = tabelCheckbox.getState();
			boolean b = tabelCheckbox.isSelected();
			for (int i = 0; i < aantalSc; i++)
			{	if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
				{	((UitvoerSchuifComponent) schuifcomponenten[i]).zetTabelAan(b);
				}
			}
			grafiekComponent.zetVeranderd(20);
			tekenOpnieuw();
		}
	}
*/	
	
	public AlgebraSchuifComponent vindActieveComponent(int eventX, int eventY)
	{	AlgebraSchuifComponent aasc = null; 
		
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{
			if ((schuifcomponenten[cCnt] != null) && schuifcomponenten[cCnt].visible && 
				schuifcomponenten[cCnt].contains(eventX, eventY))
			{
				return schuifcomponenten[cCnt];
			}
		}
	
		return aasc; 
	}
	
	public TabelComponent vindActieveTabel(int eventX, int eventY)
	{	TabelComponent tc = null; 
		
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{
			if ((schuifcomponenten[cCnt] != null) && schuifcomponenten[cCnt].visible &&
				(schuifcomponenten[cCnt] instanceof UitvoerSchuifComponent))
			{
				UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) schuifcomponenten[cCnt];
				
				if ((uvsc.tabel != null) && uvsc.tabel.contains(eventX, eventY))
					return uvsc.tabel;
			}
		}
	
		return tc; 
	}
	

	public Pijl vindActievePijl(int eventX, int eventY)
	{	Pijl ap = null; 
		
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{
			if (schuifcomponenten[cCnt] != null)
			{
				for (int pCnt = 0; pCnt < schuifcomponenten[cCnt].pijlUit.length; pCnt++)
   				{
   					if ((schuifcomponenten[cCnt].pijlUit[pCnt] != null) &&
   						schuifcomponenten[cCnt].pijlUit[pCnt].visible &&	
   						schuifcomponenten[cCnt].pijlUit[pCnt].contains(eventX, eventY))	
   					{
   						return schuifcomponenten[cCnt].pijlUit[pCnt];
   					}
   					
   				}
   				
				if ((schuifcomponenten[cCnt].pijlIn1 != null) && 
					schuifcomponenten[cCnt].pijlIn1.visible &&	
					schuifcomponenten[cCnt].pijlIn1.contains(eventX, eventY))	
				{
  						return schuifcomponenten[cCnt].pijlIn1;
   				}				
			}
		}
	
		return ap; 
	}

	protected boolean press;
    protected long taptime;
    protected List<Long> doubletap = new ArrayList<Long>();
    
    protected boolean isLongClick() 
    {
    	return System.currentTimeMillis() - taptime > 300;
	}

	protected boolean isDoubleClick() 
	{
	    return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
		//return (doubletap.size() >= 2) && doubletap.get(doubletap.size() - 1) - doubletap.get(doubletap.size() - 2) < 700;
	}

	
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{	
		
		actieveComponent = vindActieveComponent(eventX, eventY);
		actievePijl = vindActievePijl(eventX, eventY);
		actieveTabel = vindActieveTabel(eventX, eventY);
		
		if (actieveTabel != null)
		{
			actieveTabel.mouseDownTouchStartAction(eventX, eventY);
			actievePijl = null;
			actieveComponent = null;
		}
		else if (actievePijl != null)
		{
			actievePijl.mouseDownTouchStartAction(eventX, eventY);
			actieveComponent = null;
		}
		else if (actieveComponent != null)
		{
			if (actieveComponent instanceof GrafiekComponent)
				((GrafiekComponent) actieveComponent).mouseDownTouchStartAction(eventX, eventY);
			else	
				actieveComponent.mouseDownTouchStartAction(eventX, eventY);
		}
		else
		{
			taptime = System.currentTimeMillis();
	        doubletap.add(taptime);
		}
		
		
	}	
	//public void mouseDragged(MouseEvent e)
	public void mouseMoveTouchMoveAction(int eventX, int eventY)
	{	
		
		if (actieveTabel != null)
		{
			actieveTabel.mouseMoveTouchMoveAction(eventX, eventY);
		}
		else if (actievePijl != null)
		{
			actievePijl.mouseMoveTouchMoveAction(eventX, eventY);
		}
		else if (actieveComponent != null)
		{
			actieveComponent.mouseMoveTouchMoveAction(eventX, eventY);
		}
		
		if (selecterenBezig)
		{	
//GWT??
/*		
			int b = clip.getSize().width;
			int h = clip.getSize().height;
			int x = clip.getLocation().x;
			int y = clip.getLocation().y;
			
			if (eventX - x > 0 && eventY - y > 0)
			{	clip = new Rectangle(x, y, eventX - x, eventY - y);
				this.tekenOpnieuw();
			}
*/			
		}
	}
	//public void mouseReleased(MouseEvent e)
	public void mouseUpTouchEndAction(int eventX,int eventY)
	{	
		
		if (actieveTabel != null)
		{
			actieveTabel.mouseUpTouchEndAction(eventX, eventY);
		}
		else if (actievePijl != null)
		{
			actievePijl.mouseUpTouchEndAction();
		}
		else if (actieveComponent != null)
		{
			actieveComponent.mouseUpTouchEndAction();
		}
		else
		{
			if (isDoubleClick()) 
			{
	            doubletap.clear();
	        } 
			else if (isLongClick()) 
			{
				//showPopupMenu(eventX, eventY);
		
				doubletap.clear();
	        } 
			else 
			{
	            if (doubletap.size() >= 2) 
	            {	//doubletap.clear();
	            	doubletap.remove(0);
	            }
	        }

		}

		
		if (selecterenBezig)
		{	selectieGemaakt = true;
			selecterenBezig = false;
//			setCursor(new Cursor(Cursor.DEFAULT_CURSOR ));
		}
	}

	public void showPopupMenu(int x, int y)
	{
		int popupX = x + inputOwner.getAbsoluteLeft();
		int popupY = y + inputOwner.getAbsoluteTop();
		menuPopup = new PopupPanel(true);
		
		menuPopup.setWidget(menuBar);
		menuPopup.setPopupPosition(popupX, popupY);
		menuPopup.show();
	}	

	public void menuAction(String s)
	{
		
		if (s.equals("copy"))
		{	
			if (!veldIsLeeg())
			{
owner.logger.info("pre copy");				
				copy();
owner.logger.info("post copy");				
			}
		}
		
		else if (s.equals("paste"))
		{	
			if (AlgebraPijlenGWT.clipBoard != null)
			{	
				maakVeldLeeg();  
owner.logger.info("pre paste");			
				paste();
owner.logger.info("post paste");				
				
			}
			else
			{
owner.logger.info("clip = null");				
			}

		}
		menuPopup.setVisible(false);
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

}
