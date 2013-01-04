package fi.nabouwenaanzichtengwt.client;

import java.util.ArrayList;
import java.util.HashMap;

//import fi.wiskopdr.AntwoordVakChecker;
//import fi.wiskopdr.expressies.Algebra;

public class NabouwenAanzichtenChecker {

	public static final int GOED = 0;
	public static final int DOOR = 1;
	public static final int HALF = 2;
	public static final int FOUT = 3;
	public static final int GEEN = 4;
	
	private String[] randomVarNamen = null;
	private HashMap<String,Object> randomVarWaarden = null;
	
	private int score;
	private int scoreMax;
	private boolean correct;
	private boolean fout;
	private int goedHalfFout;
	private String feedback;
	
	KubusRooster docentKr = null;
	boolean checkBlokkenBouwsel;
	boolean checkDrieAanzichten;
	boolean checkVoorZijAanzicht;
	boolean checkAantalKubus;
	
	public NabouwenAanzichtenChecker(HashMap<String,Object> nbCheckerModel, String[] randomVars, HashMap<String,Object> randomValues )
	{	
		randomVarNamen = randomVars;
		randomVarWaarden = randomValues;
		
		ArrayList<ArrayList<ArrayList<Boolean>>> docentStateNew = null;
		if (nbCheckerModel.containsKey("docentStateNew")) 
			docentStateNew = (ArrayList<ArrayList<ArrayList<Boolean>>>) nbCheckerModel.get("docentStateNew");
		
		if (docentStateNew != null) {
			int maxAantal = docentStateNew.size();
			boolean[][][] b = new boolean[maxAantal][maxAantal][maxAantal];
			for (int i = 0; i < docentStateNew.size(); i++) {
				for (int j = 0; j < docentStateNew.get(i).size(); j++) {
					for (int k = 0; k < docentStateNew.get(i).get(j).size(); k++) {
						b[i][j][k] = (Boolean) docentStateNew.get(i).get(j).get(k);
					}
				}
			}
			docentKr = new KubusRooster(b,1);
		}
		
		
		boolean checkBlokkenBouwsel = true;
		if (nbCheckerModel.containsKey("checkBlokkenBouwsel"))
			checkBlokkenBouwsel = ((Boolean) nbCheckerModel.get("checkBlokkenBouwsel")).booleanValue();

		boolean checkDrieAanzichten = false;
		if (nbCheckerModel.containsKey("checkDrieAanzichten"))
			checkDrieAanzichten = ((Boolean) nbCheckerModel.get("checkDrieAanzichten")).booleanValue();

	    boolean checkVoorZijAanzicht = false;
		if (nbCheckerModel.containsKey("checkVoorZijAanzicht"))
			checkVoorZijAanzicht = ((Boolean) nbCheckerModel.get("checkVoorZijAanzicht")).booleanValue();
	    
	    boolean checkBovenVoorAanzicht = false;
		if (nbCheckerModel.containsKey("checkBovenVoorAanzicht"))
			checkBovenVoorAanzicht = ((Boolean) nbCheckerModel.get("checkBovenVoorAanzicht")).booleanValue();
	    
	    boolean checkBovenZijAanzicht = false;
		if (nbCheckerModel.containsKey("checkBovenZijAanzicht"))
			checkBovenZijAanzicht = ((Boolean) nbCheckerModel.get("checkBovenZijAanzicht")).booleanValue();
		
		boolean checkBovenAanzicht = false;
		if (nbCheckerModel.containsKey("checkBovenAanzicht"))
			checkBovenAanzicht = ((Boolean) nbCheckerModel.get("checkBovenAanzicht")).booleanValue();
		
		boolean checkVoorAanzicht = false;
		if (nbCheckerModel.containsKey("checkVoorAanzicht"))
			checkVoorAanzicht = ((Boolean) nbCheckerModel.get("checkVoorAanzicht")).booleanValue();
				
		boolean checkRechtsAanzicht = false;
		if (nbCheckerModel.containsKey("checkRechtsAanzicht"))
			checkRechtsAanzicht = ((Boolean) nbCheckerModel.get("checkRechtsAanzicht")).booleanValue();
				
		boolean checkAantalKubus = false;
		if (nbCheckerModel.containsKey("checkAantalKubus"))
			checkAantalKubus = ((Boolean) nbCheckerModel.get("checkAantalKubus")).booleanValue();
		
		int scoreMax = 10;
		if (nbCheckerModel.containsKey("scoreMax"))
			scoreMax = ((Integer) nbCheckerModel.get("scoreMax")).intValue();
		//this.scoreMax = scoreMax;
		
		this.checkBlokkenBouwsel = checkBlokkenBouwsel;
		this.checkDrieAanzichten = checkDrieAanzichten;
		this.checkVoorZijAanzicht = checkVoorZijAanzicht;
		this.checkAantalKubus = checkAantalKubus;
		
			
		//zetKijkNaActief(kijkNaActief);
		//this.kijkNaActief = kijkNaActief;
		//zetCheckBlokkenBouwsel(checkBlokkenBouwsel);
		//(checkDrieAanzichten);
	    //zetCheckVoorZijAanzicht(checkVoorZijAanzicht);
	    //zetCheckBovenVoorAanzicht(checkBovenVoorAanzicht);
	    //zetCheckBovenZijAanzicht(checkBovenZijAanzicht);
		//zetCheckBovenAanzicht(checkBovenAanzicht);		
		//zetCheckVoorAanzicht(checkVoorAanzicht);		
		//zetCheckRechtsAanzicht(checkRechtsAanzicht);		
		//zetCheckAantalKubus(checkAantalKubus);
	}
	
	public HashMap<String,Object> checkAnswer(KubusRooster answer)
	{
		HashMap<String,Object> checkResult = new HashMap<String,Object>();
		
		this.score = 0;
		this.correct = false;
		this.fout = false;
		this.feedback = "";
		this.goedHalfFout = 4;
		
		check(answer);
			
		HashMap checkResults = new HashMap();
		checkResults.put("correct", new Boolean(correct));
		checkResults.put("fout", new Boolean(fout));
		checkResults.put("goedHalfFout", new Integer(goedHalfFout));
		checkResults.put("score", new Integer(score));
		checkResults.put("feedback", feedback);
	
		return checkResults;
	}
	
	private void check(KubusRooster answer)
	{
		if(checkBlokkenBouwsel)
		{
			if (!answer.isGelijk(docentKr))
			{	score = 0;
				correct = false;
				fout = true;
				goedHalfFout = FOUT;
			}
        	else
        	{	score = scoreMax;
				score = 0;
				correct = true;
				fout = false;
				goedHalfFout = GOED;
        	}
		}
		else if(checkDrieAanzichten)
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
		else if(checkVoorZijAanzicht)
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
		
	}
	
	private void evaluate()
	{
		
	}
}
