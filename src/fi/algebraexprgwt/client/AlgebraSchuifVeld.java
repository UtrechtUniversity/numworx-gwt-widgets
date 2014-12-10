package fi.algebraexprgwt.client;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

import com.google.gwt.canvas.dom.client.Context2d;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;

public class AlgebraSchuifVeld //extends SchuifVeld 
							   //implements //ItemListener, 
							              //MouseListener, MouseMotionListener,ActionListener
{	

	//Image GOEDKRUL,FOUTKRUIS, GOEDKRULHALF;	
	//private JButton wisKnop;
	//InvulPanel ip;
	//private JCheckBox grafiekCheckbox;
	
	AlgebraSchuifComponent[] schuifcomponenten;
	AlgebraSchuifComponent actieveComponent;
	Pijl actievePijl;
	TabelComponent actieveTabel;
	
	GrafiekComponent grafiekComponent;
	int aantalSc;
	//JButton kijkNaKnop;
	
	private Map<String,Object> editmodeState;

//niet nodig, dit is voor de docent
	//private JPopupMenu popup;
	//JMenuItem copyItem; 

// alleen voor leraar	
	boolean frozen = false;
	//boolean buttonsAdded;

	public ZoomStateHolder zoomStateHolder;
	//private Font font;
	
	boolean toolkit = true;
	boolean alleenInvullen = false;
	boolean isDemo = false;
	
	boolean brugklas = false;
	boolean tabelOptie = true;	
	boolean grafiekOptie = true;
	
//GWT scrollen in de invoervakjes	
	//boolean scrollOptie = true;
	boolean scrollOptie = false;
	boolean zoomOptie = true; 
	
	boolean mouseDown;
	
	int xPos, yPos, breedte, hoogte;
	Context2d asvContext2d;
	int toolsWidth = 100;
	
	AlgebraExprGWT owner;
	boolean toonWaarde = false;
	
	boolean toonTabellen = false;
	
String testString = "test";	
	
	public AlgebraSchuifVeld(int x, int y, int b, int h, Context2d ct2d, AlgebraExprGWT o)
	{	
		asvContext2d = ct2d;
		owner = o;
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h; 
		
		zoomStateHolder = new ZoomStateHolder(this);
		
/*		
		
		kijkNaKnop = new JButton(AlgebraExpressies.rb.getString("kijkNaTekst"));
		kijkNaKnop.setFont(font);
//		kijkNaKnop.setBounds(5, 385, 90, 20);
		kijkNaKnop.setBounds(110 + (getSize().width - 110 - 90) / 2, getSize().height - 30, 90, 20);		
//		kijkNaKnop.addActionListener(this);
		kijkNaKnop.setVisible(false);
		add(kijkNaKnop,0);
*/		
		
		//add(schuiflaag, 0);		
		
		//HIER
		toolkit = owner.toolkit;
		alleenInvullen = owner.alleenInvullen;
		isDemo = owner.isDemo;

		brugklas = owner.brugklas;

//System.out.println("toolkit = " + toolkit);
		
		if (toolkit)
			maakStapel();
		
//zie beneden		
		//grafiekComponent = new GrafiekComponent(this, 400, 200, 200, 230);
		//grafiekComponent.isStapel = false;		
		
	}

/*	
	public void disableElements(boolean b)
	{
// deze worden niet disabled		
//		terugKnop.setEnabled(!b);
//		tabelCheckbox.setEnabled(!b);
		
// deze wel		
		//ip.setEnabled(!b);
		//grafiekCheckbox.setEnabled(!b);
		//wisKnop.setEnabled(!b);
		//kijkNaKnop.setEnabled(!b);
		frozen = b;
	}
*/	
	
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
		{	if ((uvs.pijlUit[pCnt] != null) && uvs.pijlUit[pCnt].vast 
				&&
				!(uvs.pijlUit[pCnt].ontvanger instanceof GrafiekComponent)
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
	
	
	public boolean veldIsLeeg()
	{	int veldCnt = 0;
		for (int vCnt = 0; vCnt < aantalSc; vCnt++)
		{
			if (!schuifcomponenten[vCnt].isStapel 
//GWT					
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
				((schuifcomponenten[i] instanceof KwadraatSchuifComponent) ||
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
		tekenOpnieuw();
	}
// deze is er niet in Java	
/*
	public void zetTabelOptie(boolean b)
	{
		tabelOptie = b;
		tabelCheckbox.setVisible(b);
	}
*/	
	public void zetGrafiekOptie(boolean b)
	{
		grafiekOptie = b;
		tekenOpnieuw();
		
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
		
		tekenOpnieuw();
		
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
		else if (asv instanceof KwadraatSchuifComponent)
			return "KwadraatSchuifComponent";
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
	
	public HashMap<String,Object> getState()
	{	
		
		int aantalSc = 0;
		List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Integer> connectionsList = new ArrayList<Integer>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean grafiek = false;
		boolean expressie = false;
		Map<String,Object> zoomStateHolderState = null;

		aantalSc = this.aantalSc;
		for (int i = 0; i < aantalSc; i++)
	    {	
			classNamesList.add(getClassName(schuifcomponenten[i]));
			posXList.add(new Integer(schuifcomponenten[i].xPos));
			posYList.add(new Integer(schuifcomponenten[i].yPos));
	    	scStatesList.add(schuifcomponenten[i].getState());
	    }
	    
		//connections = new int[aantalSc][aantalSc];
		for (int i = 0; i < aantalSc; i++)
	    {	for (int j = 0; j < aantalSc; j++)
			{	int result = 0;
	    		if (schuifcomponenten[j].pijlIn1 != null && 
	    			schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i])
	    			//connections[i][j] = 1;
	    			result = 1;
	    		if (schuifcomponenten[j].pijlIn2 != null && 
	    			schuifcomponenten[j].pijlIn2.zender == schuifcomponenten[i])
	    			//connections[i][j] = 2;
	    			result = 2;
			
	    		connectionsList.add(new Integer(result));
			}
	    }
	    
// niet in Java		
//	    tabel = tabelCheckbox.getState();
//		tabel = tabelCheckbox.isSelected();

		if (owner.grafiekBox != null)
			grafiek = owner.grafiekBox.getValue();
		
		if (owner.expressieBox != null)
			expressie = owner.expressieBox.getValue();

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

	    h.put("grafiek", new Boolean(grafiek));
	    h.put("expressie", new Boolean(expressie));
	    h.put("zoomStateHolderState", zoomStateHolderState);
	    
	    h.put("toolkit", new Boolean(toolkit));
	    h.put("alleenInvullen", new Boolean(alleenInvullen));
	    h.put("isDemo", new Boolean(isDemo));
	    
	    h.put("brugklas", new Boolean(brugklas));
	    h.put("grafiekOptie", new Boolean(grafiekOptie));
	    
	    h.put("zoomOptie", new Boolean(zoomOptie));
	    
	    return h;
	}
	

	public void setEditModeState(Map<String,Object> h)
	{	editmodeState = h;
		setState(h);
	}
		
    public void setState(Map<String,Object> map)
    {	
    	ObjectMap h = JSONUtilities.wrapMap(map);
    	
// niet echt nodig, want de leerling kan dit niet veranderen    	
    	boolean toolkit = true;
    	if (h.containsKey("toolkit"))
    		toolkit = h.getBoolean("toolkit");
    	//this.toolkit = toolkit;

    	boolean alleenInvullen = false;
    	if (h.containsKey("alleenInvullen"))
    		alleenInvullen = h.getBoolean("alleenInvullen");
    	//this.alleenInvullen = alleenInvullen;

    	boolean isDemo = false;
    	if (h.containsKey("isDemo"))
    		isDemo = h.getBoolean("isDemo");
    	//this.isDemo = isDemo;

    	int aantalSc = 0;
    	List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String,Object>> scStatesList = new ArrayList<Map<String,Object>>();
		List<Integer> connectionsList = new ArrayList<Integer>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean grafiek = false;
		boolean expressie = false;

		Map<String,Object> zoomStateHolderState = null;
	
		
		if (h.containsKey("aantalSc"))
			aantalSc = h.getInt("aantalSc");
		if (h.containsKey("classNamesList"))
			classNamesList = h.getStringList("classNamesList");
		if (h.containsKey("posXList"))
			posXList = h.getIntegerList("posXList");
		if (h.containsKey("posYList"))
			posYList = h.getIntegerList("posYList");
		if (h.containsKey("scStatesList"))		
			scStatesList = h.getMapList("scStatesList");
		if (h.containsKey("connectionsList"))			
			connectionsList = h.getIntegerList("connectionsList");
		if (h.containsKey("graphConnectionsList"))			
			graphConnectionsList = h.getIntegerList("graphConnectionsList");
		if (h.containsKey("grafiek"))		
			grafiek = h.getBoolean("grafiek");
		if (h.containsKey("expressie"))	
			expressie = h.getBoolean("expressie");
		if (h.containsKey("zoomStateHolderState"))	
			zoomStateHolderState = h.getMap("zoomStateHolderState");
		
		zoomStateHolder.setState(zoomStateHolderState);
		
		int n = this.aantalSc;
		for (int i = 0; i < n; i++)
		{	
			verwijder(schuifcomponenten[0]);
		}
		
		this.aantalSc = aantalSc;
		
		schuifcomponenten = new AlgebraSchuifComponent[200];
		for (int i = 0; i < aantalSc; i++)
	    {	
			
			String className = (String) classNamesList.get(i);
			int posX = ((Integer) posXList.get(i)).intValue();
			int posY = ((Integer) posYList.get(i)).intValue();
		
    		if (className.equals("AftrekSchuifComponent") || 
    			className.equals("fi.algebraexpressies.AftrekSchuifComponent"))
    		{
    			AlgebraSchuifComponent asv = new AftrekSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
    		}
    		else if (className.equals("DeelSchuifComponent") ||
    				 className.equals("fi.algebraexpressies.DeelSchuifComponent"))
    		{	
    			AlgebraSchuifComponent asv = new DeelSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
    		}
			else if (className.equals("MachtSchuifComponent") ||
					 className.equals("fi.algebraexpressies.MachtSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new MachtSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("KwadraatSchuifComponent") ||
					 className.equals("fi.algebraexpressies.KwadraatSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new KwadraatSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("OptelSchuifComponent") ||
					 className.equals("fi.algebraexpressies.OptelSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new OptelSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("UitvoerSchuifComponent") ||
					 className.equals("fi.algebraexpressies.UitvoerSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new UitvoerSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("VermenigvuldigSchuifComponent") ||
					 className.equals("fi.algebraexpressies.VermenigvuldigSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new VermenigvuldigSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("WortelSchuifComponent") ||
					 className.equals("fi.algebraexpressies.WortelSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new WortelSchuifComponent(this, posX, posY, 40, 30);
    			schuifcomponenten[i] = asv;
			}
			else if (className.equals("GrafiekComponent") ||
					 className.equals("fi.algebraexpressies.GrafiekComponent"))
			{
				AlgebraSchuifComponent asv = new GrafiekComponent(this, posX, posY, 200, 230);
    			schuifcomponenten[i] = asv;
    			grafiekComponent = (GrafiekComponent) schuifcomponenten[i];
    			
			}
	    }
	    
	    for (int i = 0; i < aantalSc; i++)
	    {	
	    	
	    	if (!(schuifcomponenten[i] instanceof GrafiekComponent)) 
	    	{	//schuifcomponenten[i].setState(scStates[i]);
	    		schuifcomponenten[i].setState((HashMap<String,Object>)scStatesList.get(i));
	    		// bij een GrafiekComponent lukt dit niet omdat die bij setState de parent nodig heeft en die heeft ie nog niet
//IS DIT ZO?/	    			
	    	}
	    }

//testString = "comp + states";
//tekenOpnieuw();
/*   
if (toolkit)	    
{	    
	    // nu weten we wie stapel is
	    // nodig voor launchData, in Java staan de stapels anders
	    // doe dit eerst voordat je de pijlen maakt
	    for (int i = 0; i < aantalSc; i++)
		{	if (schuifcomponenten[i].isStapel)
			{	if(schuifcomponenten[i] instanceof UitvoerSchuifComponent)schuifcomponenten[i].zetPlaats(30, 30);
				if(schuifcomponenten[i] instanceof OptelSchuifComponent)schuifcomponenten[i].zetPlaats(7, 100);
				if(schuifcomponenten[i] instanceof AftrekSchuifComponent)schuifcomponenten[i].zetPlaats(54, 100);
				if(schuifcomponenten[i] instanceof VermenigvuldigSchuifComponent)schuifcomponenten[i].zetPlaats(7, 145);
				if(schuifcomponenten[i] instanceof DeelSchuifComponent)schuifcomponenten[i].zetPlaats(54, 145);
				if(schuifcomponenten[i] instanceof KwadraatSchuifComponent)schuifcomponenten[i].zetPlaats(7, 190);
				if(schuifcomponenten[i] instanceof WortelSchuifComponent)schuifcomponenten[i].zetPlaats(54, 190);
				if(schuifcomponenten[i] instanceof MachtSchuifComponent)schuifcomponenten[i].zetPlaats(7, 235);
			}
		}
//testString += " posCorr";	    
}
else // alleenInvullen || isDemo
{
System.out.println("before " + this.aantalSc);	
	boolean launching = verwijderStapels();
System.out.println("after " + this.aantalSc);	
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
					asc.verbind(asc.pijlIn1, true);
				if (asc.pijlIn2 != null)
					asc.verbind(asc.pijlIn2, false);
			
			}
		}
		
		//paint();
	
	}
}
*/
	    
	    
		int max = aantalSc;
		for (int i = 0; i < max; i++)
		{	
			
			if (!(schuifcomponenten[i] instanceof GrafiekComponent))
			{	
				Pijl p = new Pijl(this);
				schuifcomponenten[i].voegPijlToe(p);
			}	
			//add(schuifcomponenten[i]);
		}
		
//testString += " pijlen";


	    for (int i = 0; i < aantalSc; i++)
	    {	for(int j = 0; j < aantalSc; j++)
			{	int cij = ((Integer) connectionsList.get(i*aantalSc+j)).intValue();
	    		//if (connections[i][j] == 1)
				if (cij == 1)
				{	
					Pijl p = schuifcomponenten[i].pijlUit[schuifcomponenten[i].aantalPu - 1];
					schuifcomponenten[j].verbind(p, true);
					p.zetVerbonden(schuifcomponenten[j]);
				}
				//if (connections[i][j] == 2)
				if (cij == 2)
				{	
					Pijl p = schuifcomponenten[i].pijlUit[schuifcomponenten[i].aantalPu - 1];
					schuifcomponenten[j].verbind(p, false);
					p.zetVerbonden(schuifcomponenten[j]);
				}
			}
	    }
	    
	    
//testString += " connections";


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
//	    		((UitvoerSchuifComponent) schuifcomponenten[i]).zetScroll(true);
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
    

	    if (toolkit)	    
	    {	    
	    	    // nu weten we wie stapel is
	    	    // nodig voor launchData, in Java staan de stapels anders
	    	    // doe dit eerst voordat je de pijlen maakt
	    	    for (int i = 0; i < aantalSc; i++)
	    		{	if (schuifcomponenten[i].isStapel)
	    			{	if(schuifcomponenten[i] instanceof UitvoerSchuifComponent)schuifcomponenten[i].zetPlaats(30, 30);
	    				if(schuifcomponenten[i] instanceof OptelSchuifComponent)schuifcomponenten[i].zetPlaats(7, 100);
	    				if(schuifcomponenten[i] instanceof AftrekSchuifComponent)schuifcomponenten[i].zetPlaats(54, 100);
	    				if(schuifcomponenten[i] instanceof VermenigvuldigSchuifComponent)schuifcomponenten[i].zetPlaats(7, 145);
	    				if(schuifcomponenten[i] instanceof DeelSchuifComponent)schuifcomponenten[i].zetPlaats(54, 145);
	    				if(schuifcomponenten[i] instanceof KwadraatSchuifComponent)schuifcomponenten[i].zetPlaats(7, 190);
	    				if(schuifcomponenten[i] instanceof WortelSchuifComponent)schuifcomponenten[i].zetPlaats(54, 190);
	    				if(schuifcomponenten[i] instanceof MachtSchuifComponent)schuifcomponenten[i].zetPlaats(7, 235);
	    			}
	    		}
	    //testString += " posCorr";	    
	    }
	    else // alleenInvullen || isDemo
	    {
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
//	    			else
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
	    					asc.verbind(asc.pijlIn1, true);
	    				if (asc.pijlIn2 != null)
	    					asc.verbind(asc.pijlIn2, false);
	    			
	    			}
	    		}
	    		
	    		//paint();
	    	
	    	}
	    }

	    
// niet in Java	    
//	    tabelCheckbox.setSelected(tabel);
	    
	    if (owner.expressieBox != null)
	    {	owner.expressieBox.setValue(expressie);
	    }
	    if (owner.waardeBox != null)
	    {	owner.waardeBox.setValue(!expressie);
	    }
	    if (owner.grafiekBox != null)
	    	owner.grafiekBox.setValue(grafiek);	    

	    //Enumeration en = zoomStateHolder.keys();
	    Set keySet = zoomStateHolder.keySet();
		Object[] keys = keySet.toArray();

		//while(en.hasMoreElements())
		for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{	
			//String key = (String) en.nextElement();
			String key = (String) keys[kCnt];
			
			setZoomStates(key, zoomStateHolder.getZoomState(key));
		}
	
  
		zetToolkit(toolkit);
		zetAlleenInvullen(alleenInvullen);
		zetIsDemo(isDemo);

		boolean brugklas = false;
		if (h.containsKey("brugklas"))
			brugklas = ((Boolean) h.get("brugklas")).booleanValue();
		zetBrugklas(brugklas);
		
		boolean grafiekOptie = true;
		if (h.containsKey("grafiekOptie"))
			grafiekOptie = ((Boolean) h.get("grafiekOptie")).booleanValue();
		zetGrafiekOptie(grafiekOptie);

		boolean zoomOptie = true;
		if (h.containsKey("zoomOptie"))
			zoomOptie = ((Boolean) h.get("zoomOptie")).booleanValue();
		zetZoomOptie(zoomOptie);
		
		tekenOpnieuw();
			
    }

    
	public void maakStapel()
	{	
		aantalSc = 0;
		schuifcomponenten = new AlgebraSchuifComponent[200];
		schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this, 30, 30, 40, 30); // was 55			
		aantalSc++;
		schuifcomponenten[aantalSc] = new OptelSchuifComponent(this, 7, 100, 40, 30); // was 130			
		aantalSc++;
		schuifcomponenten[aantalSc] = new AftrekSchuifComponent(this, 54, 100, 40, 30);			
		aantalSc++;
		schuifcomponenten[aantalSc] = new VermenigvuldigSchuifComponent(this, 7, 145, 40, 30); // was 170	
		aantalSc++;
		schuifcomponenten[aantalSc] = new DeelSchuifComponent(this, 54, 145, 40, 30);			
		aantalSc++;
		
		if (!brugklas)
		{	
			schuifcomponenten[aantalSc] = new KwadraatSchuifComponent(this, 7, 190, 40, 30); // was 210		
			aantalSc++;
			schuifcomponenten[aantalSc] = new WortelSchuifComponent(this, 54, 190, 40, 30);			
			aantalSc++;
			schuifcomponenten[aantalSc] = new MachtSchuifComponent(this, 7, 235, 40, 30); // was 250			
			aantalSc++;
		}
		
		int max = aantalSc;
		for (int i = 0; i < max; i++)
		{	schuifcomponenten[i].voegPijlToe(new Pijl(this));
			//add(schuifcomponenten[i]);
		}
		
//System.out.println("ms, as = " + aantalSc);		
		//add(grafiekCheckbox, 0);
		//add(wisKnop, 0);
		//add(ip, 0);
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
    	paint(asvContext2d);
    }
    
    
    public void paint()
    {
    	paint(asvContext2d);
    }
	//public void paint(Graphics g)
    public void paint(Context2d g)
	{	
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
				if (schuifcomponenten[ascCnt].pijlIn2 != null)
				{	schuifcomponenten[ascCnt].pijlIn2.paint();
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
				if (schuifcomponenten[ascCnt].pijlIn2 != null)
				{	schuifcomponenten[ascCnt].pijlIn2.paint();
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
    	
	}	
	
    //public void tekenAchtergrond(Graphics g)
    public void tekenAchtergrond(Context2d g)
	{	//Dimension dd = getSize();				
		//g.setColor(Color.white);
		g.setFillStyle(CssColor.make(255, 255, 255));
		g.fillRect(0, 0, breedte, hoogte);
		
		
		
		if (toolkit)
		{	
			
//System.out.println("toolkit " + toolsWidth);

			//g.setColor(Color.lightGray);
			//g.setColor(new Color(210, 210, 210));
			g.setFillStyle(CssColor.make(210, 210, 210));
			
			g.fillRect(0, 0, toolsWidth, hoogte);
			//g.setColor(Color.black);
			//g.setColor(Color.gray);
			g.setStrokeStyle(CssColor.make(125, 125, 125));
			
			
			//g.drawLine(100, 0, 100, dd.height - 1);
			g.beginPath();
			g.moveTo(toolsWidth, 0);
			g.lineTo(toolsWidth, hoogte - 1);
			g.stroke();
			
			//g.drawRect(0, 0, dd.width - 1, dd.height - 1);
			g.strokeRect(0, 0, breedte - 1, hoogte - 1);
		
			String fontString = "12px sans-serif";
			g.setFont(fontString);
		
			//FontMetrics fm = g.getFontMetrics();
		
			//int lengte = 0;
		
			//g.setColor(Color.black);
			g.setFillStyle(CssColor.make(0, 0, 0));
		
			//s = AlgebraExpressies.rb.getString("invoerVakLabel");
			String s = "In-/Uitvoer";
			TextMetrics tm = g.measureText(s);
		
			//lengte = fm.stringWidth(s);
			int lengte = (int) Math.round(tm.getWidth());
		
			//g.drawString(s, 50 - lengte / 2, 25); // was 50
			g.fillText(s, 50 - lengte / 2, 25);
		
			//s = AlgebraExpressies.rb.getString("bewerkingenLabel");
			s = "Bewerkingen";
			tm = g.measureText(s);
		
			lengte = (int) Math.round(tm.getWidth());
		
			//g.drawString(s, 50 - lengte / 2, 95); // was 125
			g.fillText(s, 50 - lengte / 2, 90);
		}
		else
		{
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			
			g.strokeRect(0, 0, breedte, hoogte);
		}
		
//g.setFillStyle(CssColor.make(255, 0, 0));
//g.fillText(testString, 150, 40);
		
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

		//if(asc instanceof InvoerSchuifComponent)
		//{ schuifcomponenten[aantalSc] = new InvoerSchuifComponent(this ,x,y,b,h);
		//}
		if (asc instanceof UitvoerSchuifComponent)
		{    schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof OptelSchuifComponent)
		{    schuifcomponenten[aantalSc] = new OptelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof AftrekSchuifComponent)
		{    schuifcomponenten[aantalSc] = new AftrekSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof VermenigvuldigSchuifComponent)
		{    schuifcomponenten[aantalSc] = new VermenigvuldigSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof DeelSchuifComponent)
		{    schuifcomponenten[aantalSc] = new DeelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof KwadraatSchuifComponent)
		{    schuifcomponenten[aantalSc] = new KwadraatSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof WortelSchuifComponent)
		{    schuifcomponenten[aantalSc] = new WortelSchuifComponent(this ,x, y, b, h);
		}
		else if (asc instanceof MachtSchuifComponent)
		{    schuifcomponenten[aantalSc] = new MachtSchuifComponent(this ,x, y, b, h);
		}
		else 
			return;
		schuifcomponenten[aantalSc].voegPijlToe(new Pijl(this));
		//add(schuifcomponenten[aantalSc]);
		aantalSc++;
		
//GWT		
		//copyItem.setEnabled(!veldIsLeeg());
		
/*		
		if(!buttonsAdded)
		{	getParent().add(grafiekCheckbox,0);
			getParent().add(wisKnop,0);
			getParent().add(ip,0);
			buttonsAdded = true;
		}
*/		
	}
	
	
	
	public void verwijder(AlgebraSchuifComponent sc)
	{	for (int i = 0; i < aantalSc; i++)
		{	if (schuifcomponenten[i] == sc)
			{	

			
				if (sc instanceof GrafiekComponent)
				{	GrafiekComponent gsc = (GrafiekComponent) sc;
					while(gsc.aantalPijlenIn > 0)
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
						//if (verander)
						as.zetVeranderd(20);
					}
				
					//remove(sc.pijlUit[k]);
				}
				
				//remove(sc);
				for (int j = i; j < aantalSc; j++)
				{	schuifcomponenten[j] = schuifcomponenten[j + 1];
				}
				aantalSc--;
				tekenOpnieuw();
				
//GWT				
				//copyItem.setEnabled(!veldIsLeeg());
				
				return;
			}
		}
	}
	
	public void zetVeranderd()
	{	for (int i = 0; i <aantalSc; i++)
		{	if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{	
				((UitvoerSchuifComponent) schuifcomponenten[i]).zetToonWaarde(toonWaarde);
				
			}
		}
		tekenOpnieuw();
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
		tekenOpnieuw();
	}
	
	
	public void wisAction()
	{
		if (editmodeState == null)
		{	
			int n = aantalSc;
			for (int i = 0; i < n; i++)
			{	verwijder(schuifcomponenten[0]);
			}
			maakStapel();
			if (owner.grafiekBox.getValue())
			{	
				toonGrafiekComponent(true);
				
			}
			zoomStateHolder = new ZoomStateHolder(this);				
		}
		else
			setState(editmodeState);
	}
	
	public void toonGrafiekComponent(boolean b)
	{
		if (b)
		{	
//tijdelijk	meer naar links		
			grafiekComponent = new GrafiekComponent(this, breedte - 220, 200, 200, 230);
			//grafiekComponent = new GrafiekComponent(this, breedte - 100, 200, 200, 230);
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

	
/*	
	public void actionPerformed(ActionEvent e)
	{	
		else if ((e.getSource() instanceof JMenuItem) &&
				 ((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("kopieerTekst")))
		{	if (!veldIsLeeg())
			{
				copy();
			}
		}
		else if ((e.getSource() instanceof JMenuItem) &&
				 ((JMenuItem)e.getSource()).getText().equals(AlgebraExpressies.rb.getString("plakTekst")))
		{	if ((AlgebraExpressies.clipBoard != null) && !AlgebraExpressies.clipBoard.equals(""))
			{	maakVeldLeeg();  
			
				paste();
			}
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
				
				if ((schuifcomponenten[cCnt].pijlIn2 != null) &&
					 schuifcomponenten[cCnt].pijlIn2.visible &&	
					 schuifcomponenten[cCnt].pijlIn2.contains(eventX, eventY))	
				{
	  						return schuifcomponenten[cCnt].pijlIn1;
	   			}
			} // if
		} // for
	
		return ap; 
	}
	
	//public void mousePressed(MouseEvent e)
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
		
	}
	
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
	}
	
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

	}	

}
