package fi.nabouwenaanzichtengwt.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * klasse die genuanceerd nakijken mogelijk maakt;
 * leest de launchdate om te bepalen wat nagekeken
 * moet worden; <br>  
 * merk op dat een gegeven aanzicht m.b.v. meerdere 
 * kubusbouwsels verkregen kan worden; daarom is 
 * er een instelbare optie om ook het aantal gebruikte
 * kubusjes in het antwoord te checken; een fout kubusaantal en
 * een correct aanzicht levert dan een halfgoed antwoord
 * en een score aangepast aan het verschil tussen
 * het aantal docent- en leerlingkubusjes.   
 */

public class NabouwenAanzichtenChecker {

	/**
	 * nakijk constanten
	 */
	public static final int GOED = 0;
	public static final int DOOR = 1;
	public static final int HALF = 2;
	public static final int FOUT = 3;
	public static final int GEEN = 4;
	
	/**
	 * launch data
	 */
	private String[] randomVarNamen = null;
	private Map<String,?> randomVarWaarden = null;
	
	/**
	 * nakijk attributen
	 */
	private int score;
	private int scoreMax;
	private boolean correct;
	private boolean fout;
	private int goedHalfFout;
	private String feedback;
	
	/**
	 * kubusbouwsel van de docent (antwoord)
	 */
	KubusRooster docentKr = null;
	/**
	 * nakijkopties
	 */
	boolean checkBlokkenBouwsel;
	boolean checkDrieAanzichten;
	boolean checkVoorZijAanzicht;
	boolean checkBovenVoorAanzicht;
	boolean checkBovenZijAanzicht;
	boolean checkBovenAanzicht;
	boolean checkVoorAanzicht;
	boolean checkRechtsAanzicht;
	boolean checkAantalKubus;
	
	/**
	 * constructor, lees het antwoord (docentState) en de nakijkopties
	 * uit de launchdata
	 * @param launchState launchdata
	 * @param randomVars launchdata
	 * @param randomVarWaarden2 launchdata
	 */
	public NabouwenAanzichtenChecker(Map<String, Object> launchState, String[] randomVars, Map<String, ?> randomVarWaarden2 )
	{	
		randomVarNamen = randomVars;
		randomVarWaarden = randomVarWaarden2;
		
		Object docentStateNew = null;
		if (launchState.containsKey("docentStateNew")) 
			docentStateNew = launchState.get("docentStateNew");
		else if(launchState.containsKey("docentState"))
		{
			docentStateNew = launchState.get("docentState");
			if(docentStateNew instanceof List) docentStateNew = ((List)docentStateNew).get(0);
			else if(docentStateNew instanceof Object[]) docentStateNew = ((Object[])docentStateNew)[0];

		}
		if (docentStateNew != null) {
			if(docentStateNew instanceof List) {
				List<List<List<Boolean>>> docentStateLst = (List) docentStateNew;		
				int maxAantal = docentStateLst.size();
				boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
				for (int i = 0; i < docentStateLst.size(); i++) {
					for (int j = 0; j < docentStateLst.get(i).size(); j++) {
						for (int k = 0; k < docentStateLst.get(i).get(j).size(); k++) {
						b[i][j][k] = docentStateLst.get(i).get(j).get(k);
						}
					}
				}
				docentKr = new KubusRooster(b,1);
			} else if(docentStateNew instanceof Object[] ) {
				boolean[][][] b = KubusRooster.toBooleanArray((Object[]) docentStateNew);
				docentKr = new KubusRooster(b,1);
			}
		}
		
		
		boolean checkBlokkenBouwsel = true;
		if (launchState.containsKey("checkBlokkenBouwsel"))
			checkBlokkenBouwsel = ((Boolean) launchState.get("checkBlokkenBouwsel")).booleanValue();

		boolean checkDrieAanzichten = false;
		if (launchState.containsKey("checkDrieAanzichten"))
			checkDrieAanzichten = ((Boolean) launchState.get("checkDrieAanzichten")).booleanValue();

	    boolean checkVoorZijAanzicht = false;
		if (launchState.containsKey("checkVoorZijAanzicht"))
			checkVoorZijAanzicht = ((Boolean) launchState.get("checkVoorZijAanzicht")).booleanValue();
	    
	    boolean checkBovenVoorAanzicht = false;
		if (launchState.containsKey("checkBovenVoorAanzicht"))
			checkBovenVoorAanzicht = ((Boolean) launchState.get("checkBovenVoorAanzicht")).booleanValue();
	    
	    boolean checkBovenZijAanzicht = false;
		if (launchState.containsKey("checkBovenZijAanzicht"))
			checkBovenZijAanzicht = ((Boolean) launchState.get("checkBovenZijAanzicht")).booleanValue();
		
		boolean checkBovenAanzicht = false;
		if (launchState.containsKey("checkBovenAanzicht"))
			checkBovenAanzicht = ((Boolean) launchState.get("checkBovenAanzicht")).booleanValue();
		
		boolean checkVoorAanzicht = false;
		if (launchState.containsKey("checkVoorAanzicht"))
			checkVoorAanzicht = ((Boolean) launchState.get("checkVoorAanzicht")).booleanValue();
				
		boolean checkRechtsAanzicht = false;
		if (launchState.containsKey("checkRechtsAanzicht"))
			checkRechtsAanzicht = ((Boolean) launchState.get("checkRechtsAanzicht")).booleanValue();
				
		boolean checkAantalKubus = false;
		if (launchState.containsKey("checkAantalKubus"))
			checkAantalKubus = ((Boolean) launchState.get("checkAantalKubus")).booleanValue();
		
		int scoreMax = 10;
		if (launchState.containsKey("scoreMax"))
			scoreMax = ((Number) launchState.get("scoreMax")).intValue();
		this.scoreMax = scoreMax;
		
		this.checkBlokkenBouwsel = checkBlokkenBouwsel;
		this.checkDrieAanzichten = checkDrieAanzichten;
		this.checkVoorZijAanzicht = checkVoorZijAanzicht;
		this.checkBovenVoorAanzicht = checkBovenVoorAanzicht;
		this.checkBovenZijAanzicht = checkBovenZijAanzicht;
		this.checkBovenAanzicht = checkBovenAanzicht;
		this.checkVoorAanzicht = checkVoorAanzicht;
		this.checkRechtsAanzicht = checkRechtsAanzicht;
		
		this.checkAantalKubus = checkAantalKubus;
		
	}
	
	/**
	 * kijk het antwoord (een KubusRooster) na, zet
	 * de nakijkattributen en sla deze op in een HashMap 
	 * @param answer leerling KubusRooster
	 * @return HashMap met nakijkattributen
	 */
	public HashMap<String,Object> checkAnswer(KubusRooster answer)
	{
		this.score = 0;
		this.correct = false;
		this.fout = false;
		this.feedback = "";
		this.goedHalfFout = 4;
		
		check(answer);
			
		HashMap<String,Object> checkResults = new HashMap<String,Object>();
		checkResults.put("correct", new Boolean(correct));
		checkResults.put("fout", new Boolean(fout));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
	
		return checkResults;
	}
	
	/**
	 * vergelijk per nakijkoptie het antwoord (een KubusRooster) met
	 * het kubusrooster van de docent en zet de nakijkattributen
	 * @param answer leerling KubusRooster
	 */
	private void check(KubusRooster answer)
	{
		if (checkBlokkenBouwsel)
		{
			if (!answer.isGelijk(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
        	else
        	{	score = scoreMax;
				correct = true;
				fout = false;
				goedHalfFout = GOED;
        	}
		}
		else if (checkDrieAanzichten)
		{
			if (!answer.isGelijkAanzichten(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkVoorZijAanzicht)
		{
			if (!answer.isGelijkVoorEnRechtsAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkBovenVoorAanzicht)
		{
			if (!answer.isGelijkBovenEnVoorAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkBovenZijAanzicht)
		{
			if (!answer.isGelijkBovenEnRechtsAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkBovenAanzicht)
		{
			if (!answer.isGelijkBovenAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkVoorAanzicht)
		{
			if (!answer.isGelijkVoorAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}
		else if (checkRechtsAanzicht)
		{
			if (!answer.isGelijkRechtsAanzicht(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
			else
        	{	if (checkAantalKubus)
        		{	score = Math.max(scoreMax / 2, scoreMax - Math.abs(answer.aantalKubussen - docentKr.aantalKubussen));
	        		correct = answer.aantalKubussen <= docentKr.aantalKubussen;
					fout = false;
					goedHalfFout = correct?GOED:DOOR;
        		}
        		else
        		{	score = scoreMax;
	        		correct = true;
					fout = false;
					goedHalfFout = GOED;
        		}
        	}
		}


	}
}
