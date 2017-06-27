package fi.algebrapijlengwt.client;

import fi.algebrapijlengwt.client.expressies_ap.*;

import java.util.*;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.TextMetrics;
import nl.uu.fi.dwo.interaction.client.JSONUtilities;
import nl.uu.fi.dwo.interaction.client.json.ObjectList;
import nl.uu.fi.dwo.interaction.client.json.ObjectMap;

/**
 * klasse verantwoordelijk de administratie van de elementen van alle
 * pijlenkettingen op het werkveld en het tekenen daarvan (op het Canvas uit de
 * klasse AlgebraPijlenGWT); de klasse handelt alle Mouse/Touch Events of dit
 * Canvas af, door bij een MouseDown/TouchStart Event te bepalen op welk element
 * (if any) dit MouseDown/TouchStart Event plaatsvond en vervolgens de
 * coordinaten van alle Mouse/Touch Events door te sturen naar dit element.<br>
 * elementen van een pijlenketting worden met elkaar verbonden (van elkaar
 * losgemaakt) door de uitgaande pijlpunt te verslepen tot het balletje voor de
 * inkomende pijl (een vastgemaakte pijlpunt "los" the slepen).
 */

public class AlgebraSchuifVeld
{
	/**
	 * flagg om te kijken of pijlenkettingen veranderd zijn<br>
	 * zie AlgebraPijlenGWT answerChanged()
	 */
	public boolean changed = false;

	/**
	 * het aantal SchuifComponenten op het werkveld
	 */
	int aantalSc;
	/**
	 * de AlgebraSchuifComponenten op het werkveld
	 */
	AlgebraSchuifComponent[] schuifcomponenten;

	/**
	 * de ASV de aangeklikt is (if any)
	 */
	AlgebraSchuifComponent actieveComponent;
	/**
	 * de Pijl die aangeklikt is (if any)
	 */
	Pijl actievePijl;
	/**
	 * de TabelComponent die aangeklikt is (if any)
	 */
	TabelComponent actieveTabel;
	/**
	 * basisbreedte van een ASV
	 */
	public static int basisB = 50;
	/**
	 * basishoogte van een ASV
	 */
	public static int basisH = 20;
	/**
	 * de GrafiekComponent (if any)
	 */
	GrafiekComponent grafiekComponent;
	/**
	 * t.b.v. slepen met de muis
	 */
	boolean mouseDown;
	/**
	 * uitgaande Pijl(en) naar links?
	 */
	boolean links = false;
	/**
	 * de docentdata op het werkveld
	 */
	Map<String, Object> editmodeState;
	/**
	 * de zoomStates voor alle variabelen (zie klasse zoomStateHolder)
	 */
	public ZoomStateHolder zoomStateHolder;
	/**
	 * parametrisatie: stapels?
	 */
	boolean toolkit = true;
	/**
	 * parametrisatie: kettingen alleen invullen?
	 */
	boolean alleenInvullen = false;
	/**
	 * parametrisatie: demo (er kan op het werkveld niets veranderd worden)?
	 */
	boolean isDemo = false;
	/**
	 * parametrisatie: alleen optellen/aftrekken/vermeningvuldigen/delen?
	 */
	boolean brugklas = false;
	/**
	 * parametrisatie: knop Pijlen naar links/rechts?
	 */
	boolean terugHeen = true;
	/**
	 * parametrisatie: CheckBox voor Tabellen?
	 */
	boolean tabelOptie = true;
	/**
	 * parametrisatie: CheckBox voor grafiekComponent
	 */
	boolean grafiekOptie = true;
	/**
	 * parametrisatie: zoom in tabellen?
	 */
	boolean zoomOptie = true;

	/*
	 * x-positie
	 */
	int xPos;
	/**
	 * y-positie
	 */
	int yPos;
	/**
	 * breedte
	 */
	int breedte;
	/**
	 * hoogte
	 */
	int hoogte;
	/**
	 * Context2d voor tekenen
	 */
	Context2d asvContext2d;
	/**
	 * breedte toolkit in pixels
	 */
	int toolsWidth = 100;
	/**
	 * eigenaar
	 */
	AlgebraPijlenGWT owner;
	/**
	 * moeten globaal tabellen getoond worden?
	 */
	boolean toonTabellen = false;

	/**
	 * constructor
	 * 
	 * @param x
	 *            x-positie
	 * @param y
	 *            y-positie
	 * @param b
	 *            breedte
	 * @param h
	 *            hoogte
	 * @param ct2d
	 *            Context2d voor tekenen
	 * @param o
	 *            eigenaar
	 */
	public AlgebraSchuifVeld(int x, int y, int b, int h, Context2d ct2d, AlgebraPijlenGWT o)
	{
		xPos = x;
		yPos = y;
		breedte = b;
		hoogte = h;
		asvContext2d = ct2d;
		owner = o;
		zoomStateHolder = new ZoomStateHolder(this);
		// HIER
		toolkit = owner.toolkit;
		alleenInvullen = owner.alleenInvullen;
		isDemo = owner.isDemo;
		brugklas = owner.brugklas;
		if (toolkit)
			maakStapel();
	}

	/**
	 * er is een pijlenketting verander, verwijder goed-V/fout-X en informeer
	 * AlgebraPijlenGWT (zie aldaar)
	 */
	public void answerChanged()
	{
		changed = true;
		removeGoedFout();
		owner.answerChanged();
	}

	/**
	 * verwijder goed-V/fout-X bij alle uitgaande Pijlen
	 */
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

	/**
	 * kijk of UitvoerSchuifComponent uvs aan het einde van een valide
	 * pijlenketting zit, d.w.z.:<br>
	 * uvs ligt niet op de stapel, uvs heeft een waarde, uvs heeft geen
	 * uitgaande pijlen, of als er wel uitgaande pijlen zijn, dan zitten die
	 * vast aan een component die niet de GrafiekComponent is
	 * 
	 * @param uvs
	 *            de UitvoerSchuifComponent
	 * @return true/false
	 */
	public boolean isEindUVS(UitvoerSchuifComponent uvs)
	{
		if (uvs.isStapel)
			return false;
		if ((uvs.geefUitvoer(0) == null) && (uvs.geefVerborgenUitvoer(0) == null))
			return false;
		// geen uitgaande pijlen
		if (uvs.pijlUit[0] == null)
			return true;
		boolean einde = true;
		for (int pCnt = 0; pCnt < uvs.pijlUit.length; pCnt++)
		{ // pijl zit vast aan iets anders dan een grafiek
			if ((uvs.pijlUit[pCnt] != null) && uvs.pijlUit[pCnt].vast
				&& !(uvs.pijlUit[pCnt].ontvanger instanceof GrafiekComponent))
				einde = false;
		}
		return einde;
	}

	/**
	 * vindt alle UitvoerSchuifComponenten op het werkveld die zich aan het
	 * einde van een valide pijlenketting bevinden, zie methode isEindUVS
	 * 
	 * @return een vector met UitvoerSchuifComponenten
	 */
	public Vector vindExpressieUVS()
	{
		Vector result = new Vector();
		for (int sCnt = 0; sCnt < aantalSc; sCnt++)
		{
			if (schuifcomponenten[sCnt] instanceof UitvoerSchuifComponent)
			{
				UitvoerSchuifComponent uvs = (UitvoerSchuifComponent) schuifcomponenten[sCnt];
				if (isEindUVS(uvs))
				{
					result.addElement(uvs);
				}
			}
		}
		return result;
	}

	/**
	 * zet een beginExpressie, i.e. een UVS met de Expressie exp erin (deze komt
	 * uit de launchdata)
	 * 
	 * @param exp
	 *            de beginExpressie
	 */
	public void zetBeginExpressie(Expressie exp)
	{
		// kijk of er al een beginExpUVS is, neem die dan
		UitvoerSchuifComponent beginExpUVS = null;
		for (int cnt = 0; cnt < aantalSc; cnt++)
		{
			if (schuifcomponenten[cnt] instanceof UitvoerSchuifComponent
				&& ((UitvoerSchuifComponent) schuifcomponenten[cnt]).isBeginExpressie)
			{
				beginExpUVS = (UitvoerSchuifComponent) schuifcomponenten[cnt];
			}
		}
		if (beginExpUVS != null)
		{
			beginExpUVS.zetExpressie(exp);
		}
		else // maak een nieuwe
		{
			beginExpUVS = new UitvoerSchuifComponent(this, 130, 45, basisB, basisH);
			beginExpUVS.zetTabelAan(toonTabellen);
			beginExpUVS.isBeginExpressie = true;
			beginExpUVS.isStapel = false;
			beginExpUVS.zetExpressie(exp);
			schuifcomponenten[aantalSc] = beginExpUVS;
			schuifcomponenten[aantalSc].zetLinks(links);
			Pijl p = new Pijl(this);
			p.zetLinks(links);
			schuifcomponenten[aantalSc].voegPijlToe(p);
			aantalSc++;
		}
		tekenOpnieuw();
	}

	/**
	 * setter voor toolkit, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
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

	/**
	 * setter voor alleenInvullen, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetAlleenInvullen(boolean b)
	{
		alleenInvullen = b;
		if (alleenInvullen)
		{
			toolkit = false;
			isDemo = false;
		}
	}

	/**
	 * setter voor isDemo, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetIsDemo(boolean b)
	{
		isDemo = b;
		if (isDemo)
		{
			toolkit = false;
			alleenInvullen = false;
		}
	}

	/**
	 * setter voor brrugklas, toon/verberg stapels, nodig voor setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetBrugklas(boolean b)
	{
		brugklas = b;
		for (int i = 0; i < aantalSc; i++)
		{
			if ((schuifcomponenten[i].isStapel) && ((schuifcomponenten[i] instanceof OmkeringSchuifComponent)
				|| (schuifcomponenten[i] instanceof WortelSchuifComponent)
				|| (schuifcomponenten[i] instanceof MachtSchuifComponent)))
			{
				schuifcomponenten[i].setVisible(!b);
				for (int pCnt = 0; pCnt < schuifcomponenten[i].aantalPu; pCnt++)
				{
					if (schuifcomponenten[i].pijlUit[pCnt] != null)
						schuifcomponenten[i].pijlUit[pCnt].setVisible(!b);
				}
			}
		}
	}

	/**
	 * setter voor links/rechts knop, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetTerugHeen(boolean b)
	{
		terugHeen = b;
	}

	/**
	 * setter voor tabelOptie, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetTabelOptie(boolean b)
	{
		tabelOptie = b;
	}

	/**
	 * setter voor grafiekOptie, gebruikt in setState
	 * 
	 * @param b
	 *            true/false
	 */
	public void zetGrafiekOptie(boolean b)
	{
		grafiekOptie = b;
	}

	/**
	 * setter voor zoomOptie, zet zoomInTabel voor alle UVS, nodig voor setState
	 * 
	 * @param b
	 *            true/false
	 */
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
	}

	/**
	 * bepaal de Class Name van een ASC als String
	 * 
	 * @param asc
	 *            de ASC
	 * @return de Class Name
	 */
	public String getClassName(AlgebraSchuifComponent asc)
	{
		String result = "";

		if (asc instanceof AftrekSchuifComponent)
			return "AftrekSchuifComponent";
		else if (asc instanceof DeelSchuifComponent)
			return "DeelSchuifComponent";
		else if (asc instanceof MachtSchuifComponent)
			return "MachtSchuifComponent";
		else if (asc instanceof OmkeringSchuifComponent)
			return "OmkeringSchuifComponent";
		else if (asc instanceof OptelSchuifComponent)
			return "OptelSchuifComponent";
		else if (asc instanceof UitvoerSchuifComponent)
			return "UitvoerSchuifComponent";
		else if (asc instanceof VermenigvuldigSchuifComponent)
			return "VermenigvuldigSchuifComponent";
		else if (asc instanceof WortelSchuifComponent)
			return "WortelSchuifComponent";
		else if (asc instanceof GrafiekComponent)
			return "GrafiekComponent";
		return result;
	}

	/**
	 * getState voor het hele werkveld: Lists met de class names, x-posities,
	 * y-posities en States van alle ASC's op het werkveld, List voor
	 * verbindingen van ASC's met Pijlen en zoomStates voor alle variabelen
	 * 
	 * @return een HashMap
	 */
	public HashMap<String, Object> getState()
	{
		int aantalSc = 0;
		// class names van alle ASC's
		List<String> classNamesList = new ArrayList<String>();
		// x-posities van alle ASC's
		List<Integer> posXList = new ArrayList<Integer>();
		// y-posities van alle ASC's
		List<Integer> posYList = new ArrayList<Integer>();
		// States van alle ASC's
		List<Map<String, Object>> scStatesList = new ArrayList<Map<String, Object>>();
		// verbindingen tussen ASC's
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		// verbindingen ASC's met GrafiekComponent
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		// instelling tabelBox, grafiekBox, links/rechts knop
		boolean tabel = false;
		boolean grafiek = false;
		boolean links = false;
		// zoomStates
		Map<String, Object> zoomStateHolderState = null;
		aantalSc = this.aantalSc;
		for (int i = 0; i < aantalSc; i++)
		{
			classNamesList.add(getClassName(schuifcomponenten[i]));
			posXList.add(new Integer(schuifcomponenten[i].xPos));
			posYList.add(new Integer(schuifcomponenten[i].yPos));
			scStatesList.add(schuifcomponenten[i].getState());
		}
		for (int i = 0; i < aantalSc; i++)
		{
			for (int j = 0; j < aantalSc; j++)
			{
				boolean b = schuifcomponenten[j].pijlIn1 != null
					&& schuifcomponenten[j].pijlIn1.zender == schuifcomponenten[i];
				connectionsList.add(new Boolean(b));
			}
		}
		if (owner.tabelBox != null)
			tabel = owner.tabelBox.getValue();
		if (owner.grafiekBox != null)
			grafiek = owner.grafiekBox.getValue();
		links = this.links;
		zoomStateHolderState = zoomStateHolder.getState();
		// initialiseer
		for (int i = 0; i < 10; i++)
		{
			graphConnectionsList.add(new Integer(-1));
		}
		if (grafiek)
		{
			for (int i = 0; i < 10; i++)
			{
				Pijl p = grafiekComponent.pijlenIn[i];
				for (int j = 0; j < aantalSc; j++)
				{
					if (p != null && schuifcomponenten[j] == p.zender)
					{
						graphConnectionsList.set(i, new Integer(j));
					}
				}
			}
		}
		HashMap<String, Object> h = new HashMap<String, Object>();
		h.put("aantalSc", new Integer(aantalSc));
		h.put("classNamesList", classNamesList);
		h.put("posXList", posXList);
		h.put("posYList", posYList);
		h.put("scStatesList", scStatesList);
		h.put("connectionsList", connectionsList);
		h.put("graphConnectionsList", graphConnectionsList);
		h.put("tabel", new Boolean(tabel));
		h.put("grafiek", new Boolean(grafiek));
		h.put("links", new Boolean(links));
		h.put("zoomStateHolderState", zoomStateHolderState);
		h.put("toolkit", new Boolean(toolkit));
		h.put("alleenInvullen", new Boolean(alleenInvullen));
		h.put("isDemo", new Boolean(isDemo));
		h.put("brugklas", new Boolean(brugklas));
		h.put("terugHeen", new Boolean(terugHeen));
		h.put("tabelOptie", new Boolean(tabelOptie));
		h.put("grafiekOptie", new Boolean(grafiekOptie));
		h.put("zoomOptie", new Boolean(zoomOptie));
		return h;
	}

	/**
	 * gebruikt in init(....): bewaar de initiele State in editmodeState, en
	 * setState
	 * 
	 * @param h
	 *            de docentdata
	 */
	public void setEditModeState(Map<String, Object> h)
	{
		editmodeState = h;
		setState(h);
	}

	/**
	 * set de State van het werkveld m.b.v. map: map bevat Lists met de class
	 * names, x-posities, y-posities en States van alle ASC's op het werkveld,
	 * List voor verbindingen van ASC's met Pijlen en zoomStates voor alle
	 * variabelen
	 * 
	 * @param map
	 *            de Map met alle State info
	 */
	public void setState(Map<String, Object> map)
	{
		ObjectMap h = JSONUtilities.wrapMap(map);
		int aantalSc = 0;
		List<String> classNamesList = new ArrayList<String>();
		List<Integer> posXList = new ArrayList<Integer>();
		List<Integer> posYList = new ArrayList<Integer>();
		List<Map<String, Object>> scStatesList = new ArrayList<Map<String, Object>>();
		List<Boolean> connectionsList = new ArrayList<Boolean>();
		List<Integer> graphConnectionsList = new ArrayList<Integer>();
		boolean tabel = false;
		boolean grafiek = false;
		boolean links = false;
		Map<String, Object> zoomStateHolderState = null;
		if (h.containsKey("aantalSc"))
			aantalSc = h.getInt("aantalSc");
		// binnen GWT
		if (h.containsKey("classNamesList"))
			classNamesList = h.getStringList("classNamesList");
		// vanuit Java
		else if (h.containsKey("classNames"))
			classNamesList = h.getStringList("classNames");
		// binnen GWT
		if (h.containsKey("posXList"))
			posXList = h.getIntegerList("posXList");
		// vanuit Java
		else if (h.containsKey("posX"))
			posXList = h.getIntegerList("posX");
		// binnen GWT
		if (h.containsKey("posYList"))
			posYList = h.getIntegerList("posYList");
		// vanuit Java
		else if (h.containsKey("posY"))
			posYList = h.getIntegerList("posY");
		// binnen GWT
		if (h.containsKey("scStatesList"))
			scStatesList = h.getMapList("scStatesList");
		// vanuit Java
		else if (h.containsKey("scStates"))
			scStatesList = h.getMapList("scStates");
		if (h.containsKey("connectionsList"))
			connectionsList = h.getBooleanList("connectionsList");
		else if (h.containsKey("connections"))
		{
			ObjectList list = h.getObjectList("connections");
			int size = list.size();
			for (int i = 0; i < size; i++)
				connectionsList.addAll(list.getBooleanList(i));
		}
		if (h.containsKey("graphConnectionsList"))
			graphConnectionsList = h.getIntegerList("graphConnectionsList");
		else if (h.containsKey("graphConnections"))
			graphConnectionsList = h.getIntegerList("graphConnections");
		if (h.containsKey("tabel"))
			tabel = h.getBoolean("tabel");
		if (h.containsKey("grafiek"))
			grafiek = h.getBoolean("grafiek");
		if (h.containsKey("links"))
			links = h.getBoolean("links");
		if (h.containsKey("zoomStateHolderState"))
			zoomStateHolderState = h.getMap("zoomStateHolderState");

		zoomStateHolder.setState(zoomStateHolderState);
		// maak veld leeg
		int n = this.aantalSc;
		for (int i = 0; i < n; i++)
		{
			verwijder(schuifcomponenten[0]);
		}
		// vul m.b.v. state info
		this.aantalSc = aantalSc;
		schuifcomponenten = new AlgebraSchuifComponent[200];
		for (int i = 0; i < aantalSc; i++)
		{
			String className = (String) classNamesList.get(i);
			int posX = ((Integer) posXList.get(i)).intValue();
			int posY = ((Integer) posYList.get(i)).intValue();
			// naam binnen GWT of uit Java
			if (className.equals("AftrekSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.AftrekSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new AftrekSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("DeelSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.DeelSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new DeelSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("MachtSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.MachtSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new MachtSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("OmkeringSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.OmkeringSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new OmkeringSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("OptelSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.OptelSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new OptelSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("UitvoerSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.UitvoerSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new UitvoerSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("VermenigvuldigSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.VermenigvuldigSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new VermenigvuldigSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("WortelSchuifComponent")
				|| className.equals("fi.algebrapijlenopdr.WortelSchuifComponent"))
			{
				AlgebraSchuifComponent asv = new WortelSchuifComponent(this, posX, posY, 50, 20);
				schuifcomponenten[i] = asv;
			}
			else if (className.equals("GrafiekComponent") || className.equals("fi.algebrapijlenopdr.GrafiekComponent"))
			{
				AlgebraSchuifComponent asv = new GrafiekComponent(this, posX, posY, 210, 200);
				schuifcomponenten[i] = asv;
				grafiekComponent = (GrafiekComponent) schuifcomponenten[i];
			}

		}
		// states
		for (int i = 0; i < aantalSc; i++)
		{
			if (!(schuifcomponenten[i] instanceof GrafiekComponent))
			{
				schuifcomponenten[i].setState(scStatesList.get(i));
			}
		}
		// maak een uitgaande pijl voor de AlgebraSchuifComponenten die stapel
		// zijn
		// en zet al die pijlen de goede kant op (boolean links)
		int max = aantalSc;
		for (int i = 0; i < max; i++)
		{
			if (!(schuifcomponenten[i] instanceof GrafiekComponent))
			{
				Pijl p = new Pijl(this);
				if (schuifcomponenten[i].isStapel)
				{
					schuifcomponenten[i].zetLinks(links);
					p.zetLinks(links);
				}
				schuifcomponenten[i].voegPijlToe(p);
			}
		}
		// maak verbindingen
		for (int i = 0; i < aantalSc; i++)
		{
			for (int j = 0; j < aantalSc; j++)
			{
				boolean cij = ((Boolean) connectionsList.get(i * aantalSc + j)).booleanValue();
				if (cij)
				{
					Pijl p = schuifcomponenten[i].pijlUit[schuifcomponenten[i].aantalPu - 1];
					schuifcomponenten[j].verbind(p);
					p.zetVerbonden(schuifcomponenten[j]);
				}
			}
		}
		// maak verbindingen met grafiekComponent
		if (grafiek)
		{
			for (int i = 0; i < 10; i++)
			{
				int gc = ((Integer) graphConnectionsList.get(i)).intValue();
				if (gc != -1)
				{
					Pijl p = schuifcomponenten[gc].pijlUit[schuifcomponenten[gc].aantalPu - 1];
					grafiekComponent.verbind(p, i);
					p.zetVerbonden(grafiekComponent);
				}
			}
		}
		// states
		for (int i = 0; i < aantalSc; i++)
		{
			schuifcomponenten[i].setState((HashMap<String, Object>) scStatesList.get(i));
		}
		// update
		for (int i = 0; i < aantalSc; i++)
		{
			schuifcomponenten[i].zetVeranderd(20);
		}
		// grafiekComponent
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof GrafiekComponent)
			{
				schuifcomponenten[i].setState(scStatesList.get(i));
				schuifcomponenten[i].zetVeranderd(20);
			}
		}
		// stand ChaecBoxes
		if (owner.tabelBox != null)
			owner.tabelBox.setValue(tabel);
		if (owner.grafiekBox != null)
		{
			owner.grafiekBox.setValue(grafiek);
		}
		// links/rechts
		this.links = links;
		if (links)
		{
			for (int i = 0; i < aantalSc; i++)
			{
				if (schuifcomponenten[i].isStapel)
				{
					schuifcomponenten[i].zetLinks(true);
				}
			}
		}
		// bij de Java versie staan de stapels iets hoger
		// dus de launchdata positie van de stapels is niet correct
		if (toolkit)
		{
			for (int i = 0; i < aantalSc; i++)
			{
				if (schuifcomponenten[i].isStapel)
				{
					if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 35);
					if (schuifcomponenten[i] instanceof OptelSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 90);
					if (schuifcomponenten[i] instanceof AftrekSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 115);
					if (schuifcomponenten[i] instanceof VermenigvuldigSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 140);
					if (schuifcomponenten[i] instanceof DeelSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 165);
					if (schuifcomponenten[i] instanceof OmkeringSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 190);
					if (schuifcomponenten[i] instanceof WortelSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 215);
					if (schuifcomponenten[i] instanceof MachtSchuifComponent)
						schuifcomponenten[i].zetPlaats(20, 240);
				}
			}
		}
		// geen stapels links, maar de launchdata bevatten wel stapels, die zijn
		// in Java
		// buiten beeld geschoven; verwijder dus de stapels en
		// schuif alle componenten op het ASVelsd 100 pixels naar links
		else // alleenInvullen || isDemo
		{
			boolean launching = verwijderStapels();
			if (launching)
			{
				for (int cnt = 0; cnt < this.aantalSc; cnt++)
				{
					if (schuifcomponenten[cnt] != null)
					{
						int x = schuifcomponenten[cnt].xPos;
						int y = schuifcomponenten[cnt].yPos;
						AlgebraSchuifComponent asc = (AlgebraSchuifComponent) schuifcomponenten[cnt];
						for (int pCnt = 0; pCnt < asc.aantalPu; pCnt++)
							asc.zetPlaats(x - 100, y, asc.pijlUit[pCnt]);
					}
				}
				for (int cnt = 0; cnt < this.aantalSc; cnt++)
				{
					if (schuifcomponenten[cnt] != null)
					{
						AlgebraSchuifComponent asc = (AlgebraSchuifComponent) schuifcomponenten[cnt];
						if (asc.pijlIn1 != null)
							asc.verbind(asc.pijlIn1);
					}
				}
			}
		}
		// set de zoomStates
		Set keySet = zoomStateHolder.keySet();
		Object[] keys = keySet.toArray();
		for (int kCnt = 0; kCnt < keys.length; kCnt++)
		{
			String key = (String) keys[kCnt];
			setZoomStates(key, zoomStateHolder.getZoomState(key));
		}

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
		boolean zoomOptie = true;
		if (h.containsKey("zoomOptie"))
			zoomOptie = h.getBoolean("zoomOptie");
		zetZoomOptie(zoomOptie);
		tekenOpnieuw();
	}

	/**
	 * verijder de stapels, geberuikt in setState
	 * 
	 * @return true indien iets verwijderd is, anders false
	 */
	public boolean verwijderStapels()
	{
		boolean stapel = false;
		SchuifComponent[] stapels = new SchuifComponent[20];
		int sCnt = 0;
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i].isStapel)
			{
				stapels[sCnt] = schuifcomponenten[i];
				sCnt++;
				stapel = true;
			}
		}
		for (int j = 0; j < sCnt; j++)
		{
			verwijder((AlgebraSchuifComponent) stapels[j]);
		}
		return stapel;
	}

	/**
	 * dit vraagt een paint van het werkveld
	 */
	public void tekenOpnieuw()
	{
		paint(asvContext2d);
	}

	public void paint()
	{
		paint(asvContext2d);
	}

	/**
	 * teken het ASV
	 * 
	 * @param g
	 *            de Contect2d
	 */
	public void paint(Context2d g)
	{
		tekenAchtergrond(g);
		// stapels, teken de pijlen er direct bij
		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
		{
			if ((schuifcomponenten[ascCnt] != null) && schuifcomponenten[ascCnt].isStapel)
			{
				schuifcomponenten[ascCnt].paint();
				for (int pCnt = 0; pCnt < schuifcomponenten[ascCnt].pijlUit.length; pCnt++)
				{
					if (schuifcomponenten[ascCnt].pijlUit[pCnt] != null)
					{
						schuifcomponenten[ascCnt].pijlUit[pCnt].paint();
					}
				}
				if (schuifcomponenten[ascCnt].pijlIn1 != null)
				{
					schuifcomponenten[ascCnt].pijlIn1.paint();
				}

			}
		}
		// grafiekComponent
		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
		{
			if ((schuifcomponenten[ascCnt] != null) && !schuifcomponenten[ascCnt].isStapel
				&& (schuifcomponenten[ascCnt] instanceof GrafiekComponent))
			{
				schuifcomponenten[ascCnt].paint();
			}
		}
		// niet stapels, teken de pijlen en de de tabellen (if any)
		for (int ascCnt = 0; ascCnt < aantalSc; ascCnt++)
		{
			if ((schuifcomponenten[ascCnt] != null) && !schuifcomponenten[ascCnt].isStapel
				&& !(schuifcomponenten[ascCnt] instanceof GrafiekComponent))
			{
				schuifcomponenten[ascCnt].paint();
				for (int pCnt = 0; pCnt < schuifcomponenten[ascCnt].pijlUit.length; pCnt++)
				{
					if (schuifcomponenten[ascCnt].pijlUit[pCnt] != null)
					{
						schuifcomponenten[ascCnt].pijlUit[pCnt].paint();
					}
				}
				if (schuifcomponenten[ascCnt].pijlIn1 != null)
				{
					schuifcomponenten[ascCnt].pijlIn1.paint();
				}
				if (schuifcomponenten[ascCnt] instanceof UitvoerSchuifComponent)
				{
					UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) schuifcomponenten[ascCnt];
					if (uvsc.tabel != null)
					{
						uvsc.tabel.paint();
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

	/**
	 * maak stapels (een examplaar) van de benodigde typen ASC
	 */
	public void maakStapel()
	{
		aantalSc = 0;
		int b = basisB;
		int h = basisH;
		schuifcomponenten = new AlgebraSchuifComponent[200];
		schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this, 20, 35, b, h);
		((UitvoerSchuifComponent) schuifcomponenten[aantalSc]).zetTabelAan(toonTabellen);
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
		{
			schuifcomponenten[i].zetLinks(links);
			Pijl p = new Pijl(this);
			p.zetLinks(links);
			schuifcomponenten[i].voegPijlToe(p);
		}
	}

	/**
	 * teken de achtergrond van het werkveld m.b.v. Context2d g
	 * 
	 * @param g
	 *            de Contect2d
	 */
	public void tekenAchtergrond(Context2d g)
	{
		// wit
		g.setFillStyle(CssColor.make(255, 255, 255));
		g.fillRect(0, 0, breedte, hoogte);
		// stapels
		if (toolkit)
		{ // grijs
			g.setFillStyle(CssColor.make(210, 210, 210));
			g.fillRect(0, 0, toolsWidth, hoogte);
			// outline donkerder
			g.setStrokeStyle(CssColor.make(125, 125, 125));
			g.beginPath();
			g.moveTo(toolsWidth, 0);
			g.lineTo(toolsWidth, hoogte - 1);
			g.stroke();
			g.strokeRect(0, 0, breedte - 1, hoogte - 1);
			// labels in tool-area
			String fontString = "12px sans-serif";
			g.setFont(fontString);
			g.setFillStyle(CssColor.make(0, 0, 0));
			String s = AlgebraPijlenGWT.rb.invoerLabel();
			TextMetrics tm = g.measureText(s);
			int lengte = (int) Math.round(tm.getWidth());
			g.fillText(s, 55 - lengte / 2, 25);
			s = AlgebraPijlenGWT.rb.bewerkingenLabel();
			tm = g.measureText(s);
			lengte = (int) Math.round(tm.getWidth());
			g.fillText(s, 55 - lengte / 2, 80);
		}
		else // outline
		{
			g.setStrokeStyle(CssColor.make(0, 0, 0));
			g.strokeRect(0, 0, breedte, hoogte);
		}
	}

	/**
	 * de ASC acc is op het werkveld gesleept, maak dus een nieuwe kopie voor de
	 * stapel
	 * 
	 * @param asc
	 *            de versleepte ASC
	 */
	public void zetStapel(AlgebraSchuifComponent asc)
	{
		int x = asc.xPos;
		int y = asc.yPos;
		int b = asc.breedte;
		int h = asc.hoogte;
		if (asc instanceof UitvoerSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new UitvoerSchuifComponent(this, x, y, b, h);
			UitvoerSchuifComponent uvs = (UitvoerSchuifComponent) asc;
			((UitvoerSchuifComponent) schuifcomponenten[aantalSc]).zetZoomInTabel(uvs.zoomInTabel);
		}
		else if (asc instanceof OptelSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new OptelSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof AftrekSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new AftrekSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof VermenigvuldigSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new VermenigvuldigSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof DeelSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new DeelSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof OmkeringSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new OmkeringSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof WortelSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new WortelSchuifComponent(this, x, y, b, h);
		}
		else if (asc instanceof MachtSchuifComponent)
		{
			schuifcomponenten[aantalSc] = new MachtSchuifComponent(this, x, y, b, h);
		}
		else
			return;
		schuifcomponenten[aantalSc].zetLinks(links);
		Pijl p = new Pijl(this);
		p.zetLinks(links);
		schuifcomponenten[aantalSc].voegPijlToe(p);
		aantalSc++;
	}

	/**
	 * verwijder de ASC sc uit het array schuifcomponenten[] en vul de "lege"
	 * plek op door alles naar links te schuiven; verwijder de inkomende pijl
	 * uit zijn zender, en verwijder de uitgaande pijlen uit hun ontvangers
	 * 
	 * @param sc
	 *            de te verwijderen ASC
	 */
	public void verwijder(AlgebraSchuifComponent sc)
	{
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] == sc)
			{
				if (sc instanceof GrafiekComponent)
				{
					GrafiekComponent gsc = (GrafiekComponent) sc;
					while (gsc.aantalPijlenIn > 0)
					{
						Pijl p = gsc.pijlenIn[0];
						gsc.maakLos(gsc.pijlenIn[0]);
						p.zender.verwijderPijl();
						p.pijlTerug();
					}
				}
				if (sc.pijlIn1 != null)
				{
					Pijl p = sc.pijlIn1;
					sc.maakLos(sc.pijlIn1);
					p.zender.verwijderPijl();
					p.pijlTerug();
				}
				for (int k = 0; k < sc.aantalPu; k++)
				{
					if (sc.pijlUit[k].ontvanger != null)
					{
						AlgebraSchuifComponent as = sc.pijlUit[k].ontvanger;
						as.maakLos(sc.pijlUit[k]);
						as.zetVeranderd(20);
					}
				}
				for (int j = i; j < aantalSc; j++)
				{
					schuifcomponenten[j] = schuifcomponenten[j + 1];
				}
				aantalSc--;
				// geen repaint bij setState
				if (!owner.asvSetState)
					tekenOpnieuw();
				return;
			}
		}
	}

	/**
	 * de globale optie tabellen is veranderd, pas de UVS's aan
	 */
	public void zetVeranderd()
	{
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				((UitvoerSchuifComponent) schuifcomponenten[i]).zetTabelAan(toonTabellen);
			}
		}
		tekenOpnieuw();
	}

	/**
	 * zet de nieuwe zoomState voor elke UitvoerSchuifComponent en de
	 * GrafiekComponent indien deze de variabele varnaam bevatten
	 * 
	 * @param varnaam
	 *            de variabele naam
	 * @param zoomState
	 *            de zoomState voor varnaam
	 */
	public void setZoomStates(String varnaam, ZoomState zoomState)
	{
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				((UitvoerSchuifComponent) schuifcomponenten[i]).setZoomState(varnaam, zoomState);

			}

			if (schuifcomponenten[i] instanceof GrafiekComponent)
			{
				((GrafiekComponent) schuifcomponenten[i]).setZoomState(varnaam, zoomState);
			}
		}
		if (!owner.asvSetState)
			tekenOpnieuw();
	}

	/**
	 * aktie bij gebruik links/rechts knop (klasse AlgebraPijlenGWT): verander
	 * de pijlrichting van de stapels
	 */
	public void linksRechtsAction()
	{
		if (!links)
		{
			links = true;
			for (int i = 0; i < aantalSc; i++)
			{
				if (schuifcomponenten[i].isStapel)
				{
					schuifcomponenten[i].xPos += 10;
					schuifcomponenten[i].zetLinks(true);
				}
			}
			tekenOpnieuw();
		}
		else // links
		{
			links = false;
			for (int i = 0; i < aantalSc; i++)
			{
				if (schuifcomponenten[i].isStapel)
				{
					schuifcomponenten[i].xPos -= 10;
					schuifcomponenten[i].zetLinks(false);
				}
			}
			tekenOpnieuw();
		}
	}

	/**
	 * wis het werkveld: als er geen docentdata zijn, verwijder alle ASC's, maak
	 * nieuwe stapels, maak als nodig een nieuwe GrafiekComponent en maak nieuwe
	 * zoomStates; als er wel docentdata zijn, zet die dan terug via SetState
	 */
	public void wisAction()
	{
		if (editmodeState == null)
		{
			links = false;
			int n = aantalSc;
			for (int i = 0; i < n; i++)
			{
				verwijder(schuifcomponenten[0]);
			}
			maakStapel();
			if (owner.grafiekBox != null && owner.grafiekBox.getValue())
			{
				toonGrafiekComponent(true);
			}
			zoomStateHolder = new ZoomStateHolder(this);
		}
		else
		{
			setState(editmodeState);
		}
	}

	/**
	 * maak (als nodig) een GrafiekComponent en toon die resp. verwijder de
	 * GrafiekComponent
	 * 
	 * @param b
	 *            true/false
	 */
	public void toonGrafiekComponent(boolean b)
	{
		if (b && grafiekComponent == null)
		{
			grafiekComponent = new GrafiekComponent(this, breedte - 220, 200, 210, 220);
			schuifcomponenten[aantalSc] = grafiekComponent;
			aantalSc++;
		}
		else if (!b && grafiekComponent != null)
		{
			if (actieveComponent == grafiekComponent)
				actieveComponent = null;
			verwijder(grafiekComponent);
		}
		tekenOpnieuw();
	}

	/**
	 * vindt de (zichtbare) AlgebraSchuifComponent aasc (als die er is) die het
	 * punt met coordinaten (eventX,eventY) bevat
	 * 
	 * @param eventX
	 *            de x-coordinaat
	 * @param eventY
	 *            de y-coordinaat
	 * @return aasc of null
	 */
	public AlgebraSchuifComponent vindActieveComponent(int eventX, int eventY)
	{
		AlgebraSchuifComponent aasc = null;
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{ // zoek alleen onder de zichtbare schuifcomponenten
			if ((schuifcomponenten[cCnt] != null) && schuifcomponenten[cCnt].visible
				&& schuifcomponenten[cCnt].contains(eventX, eventY))
			{
				return schuifcomponenten[cCnt];
			}
		}
		return aasc;
	}

	/**
	 * vindt de TabelCompoment (als die er is) die het punt met coordinaten
	 * (eventX,eventY) bevat zie methode contains() in klasse TabelComponent
	 * 
	 * @param eventX
	 *            x-coordinaat
	 * @param eventY
	 *            y-coordinaat
	 * @return de TabelComponent tc of null
	 */
	public TabelComponent vindActieveTabel(int eventX, int eventY)
	{
		TabelComponent tc = null;
		// zoek in de zichtbare UitvoerSchuifComponenten
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{
			if ((schuifcomponenten[cCnt] != null) && schuifcomponenten[cCnt].visible
				&& (schuifcomponenten[cCnt] instanceof UitvoerSchuifComponent))
			{
				UitvoerSchuifComponent uvsc = (UitvoerSchuifComponent) schuifcomponenten[cCnt];
				// kijk of uvsc een tabel heeft
				if ((uvsc.tabel != null) && uvsc.tabel.contains(eventX, eventY))
					return uvsc.tabel;
			}
		}
		return tc;
	}

	/**
	 * vindt de (zichtbare) Pijl ap (als die er is) waarvan de pijlpunt het punt
	 * met coordinaten (eventX,eventY) bevat zie methode contains() in klasse
	 * Pijl
	 * 
	 * @param eventX
	 *            x-coordinaat
	 * @param eventY
	 *            y-coordinaat
	 * @return de Pijl ap of null
	 */
	public Pijl vindActievePijl(int eventX, int eventY)
	{
		Pijl ap = null;
		for (int cCnt = 0; cCnt < aantalSc; cCnt++)
		{ // voor elke schuifcomponent != null
			if (schuifcomponenten[cCnt] != null)
			{ // check de uitgaande pijlen van de schuifcomponenet
				for (int pCnt = 0; pCnt < schuifcomponenten[cCnt].pijlUit.length; pCnt++)
				{ // de uitgaande pijlen moeten er zijn en zichtbaar zijn
					if ((schuifcomponenten[cCnt].pijlUit[pCnt] != null) && schuifcomponenten[cCnt].pijlUit[pCnt].visible
						&& schuifcomponenten[cCnt].pijlUit[pCnt].contains(eventX, eventY))
					{
						return schuifcomponenten[cCnt].pijlUit[pCnt];
					}
				}
				// check de ingaande pijl van de schuifcomponent
				// deze moet er zijn en zichtbaar zijn
				if ((schuifcomponenten[cCnt].pijlIn1 != null) && schuifcomponenten[cCnt].pijlIn1.visible
					&& schuifcomponenten[cCnt].pijlIn1.contains(eventX, eventY))
				{
					return schuifcomponenten[cCnt].pijlIn1;
				}
				// NB de ingaande pijl van een schuifcomponent kan een uitgaande
				// pijl van een andere
				// schuifcomponent zijn; je neemt de eerste die gevonden wordt
			}
		}
		return ap;
	}

	protected boolean press;
	protected long taptime;
	protected List<Long> doubletap = new ArrayList<Long>();

	boolean popupOpened = false;

	protected boolean isLongClick()
	{
		return System.currentTimeMillis() - taptime > 300;
	}

	protected boolean isDoubleClick()
	{
		return doubletap.size() >= 2 && doubletap.get(1) - doubletap.get(0) < 700;
		// return (doubletap.size() >= 2) && doubletap.get(doubletap.size() - 1)
		// - doubletap.get(doubletap.size() - 2) < 700;
	}

	/**
	 * afhandeling MouseDown/TouchStart Events
	 * 
	 * @param eventX
	 *            de x-coordinaat van het MouseDown/TouchStart Event
	 * @param eventY
	 *            de y-coordinaat van het MouseDown/TouchStart Event
	 */
	public void mouseDownTouchStartAction(int eventX, int eventY)
	{ // zoek uit waar precies geklikt is
		actieveComponent = vindActieveComponent(eventX, eventY);
		actievePijl = vindActievePijl(eventX, eventY);
		actieveTabel = vindActieveTabel(eventX, eventY);
		// Tabel eerst
		if (actieveTabel != null)
		{
			actieveTabel.mouseDownTouchStartAction(eventX, eventY);
			actievePijl = null;
			actieveComponent = null;
		}
		// dan Pijl
		else if (actievePijl != null)
		{
			actievePijl.mouseDownTouchStartAction(eventX, eventY);
			actieveComponent = null;
		}
		// dan ASC
		else if (actieveComponent != null)
		{
			if (actieveComponent instanceof GrafiekComponent)
				((GrafiekComponent) actieveComponent).mouseDownTouchStartAction(eventX, eventY);
			else
				actieveComponent.mouseDownTouchStartAction(eventX, eventY);
		}
	}

	/**
	 * afhandeling MouseMove/TouchMove Events
	 * 
	 * @param eventX
	 *            de x-coordinaat van het MouseMove/TouchMove Event
	 * @param eventY
	 *            de y-coordinaat van het MouseMove/TouchMove Event
	 */
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

	/**
	 * afhandeling MouseUp/TouchEnd Events
	 * 
	 * @param eventX
	 *            de x-coordinaat van het MouseUp/TouchEnd Event
	 * @param eventY
	 *            de y-coordinaat van het MouseUp/TouchEnd Event
	 */
	public void mouseUpTouchEndAction(int eventX, int eventY)
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
			if (isDoubleClick())
			{
				setPopupOpened(true);
			}
		}
		else
		{
			if (isDoubleClick())
			{
				doubletap.clear();
				setPopupOpened(true);
			}
			else if (isLongClick())
			{
				// showPopupMenu(eventX, eventY);

				doubletap.clear();
				setPopupOpened(false);
				closeOpenedPopups();
			}
			else
			{
				if (doubletap.size() >= 2)
				{ // doubletap.clear();
					doubletap.remove(0);
				}
				setPopupOpened(false); // hier kom ik als ik een popup geopend
										// heb en er naast klik
				closeOpenedPopups();
			}

		}

	}

	/**
	 * Set popupOpened en bij de popup van de schuifcomponent.
	 * 
	 * @param b
	 */
	void setPopupOpened(boolean b)
	{
		// nodig voor TekstPopup.onPreviewNativeEvent()
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				if (((UitvoerSchuifComponent) schuifcomponenten[i]).tf != null)
					((UitvoerSchuifComponent) schuifcomponenten[i]).tf.setPopupOpened(b);
			}
		}

		popupOpened = b;
	}

	/**
	 * Sluit de geopende popups.
	 */
	public void closeOpenedPopups()
	{
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent)
			{
				// sluit invoer-popup
				if (((UitvoerSchuifComponent) schuifcomponenten[i]).tf != null)
					((UitvoerSchuifComponent) schuifcomponenten[i]).tf.hidePopup();

				// sluit label-popup
				if (((UitvoerSchuifComponent) schuifcomponenten[i]).label != null)
					((UitvoerSchuifComponent) schuifcomponenten[i]).label.hidePopup();
			}
			else if (schuifcomponenten[i] instanceof BewerkingSchuifComponent)
			{
				// sluit bewerking-popup
				if (((BewerkingSchuifComponent) schuifcomponenten[i]).tf != null)
					((BewerkingSchuifComponent) schuifcomponenten[i]).tf.hidePopup();
			}
		}

		popupOpened = false;
	}

	/**
	 * Resize de geopende popup.
	 */
	public void resizePopup()
	{
		for (int i = 0; i < aantalSc; i++)
		{
			if (schuifcomponenten[i] instanceof UitvoerSchuifComponent
				&& ((UitvoerSchuifComponent) schuifcomponenten[i]).tf != null)
			{
				((UitvoerSchuifComponent) schuifcomponenten[i]).tf.resizePopup();
			}
		}

		popupOpened = false;
	}

	AlgebraPijlenGWT getOwner()
	{
		return owner;
	}
}
