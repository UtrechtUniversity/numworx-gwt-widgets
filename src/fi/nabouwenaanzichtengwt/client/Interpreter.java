package fi.nabouwenaanzichtengwt.client;

import java.util.*;
import fi.nabouwenaanzichtengwt.client.expressies.*;

/**
 * klasse die een String bestaande uit meerdere regels met op elke 
 * regel een aantal bouw- of sloopopdrachten gescheiden door puntkomma's
 * interpreteert, d.w.z deze bouw- en sloopopdrachten uitvoert; <br>
 * behalve "kale" bouw- en sloopopdrachten zoals bouw 1,2,3 
 * (plaats een blokje op positie (1,2,3) in het kubusrooster),
 * kan men ranges definieren via (b.v.) a = 1..5 en deze gebruiken
 * in bouw 1,2,a (plaatst 5 blokjes in het kubusrooster);
 * de grenzen van de ranges mogen Expressies zijn 
 * @author Peter Boon
 */
public class Interpreter 
{
	/**
	 * alle variabelen in het bouw/sloopprogramma
	 */
	VariableCollection variableCollection;
	/**
	 * het kubusrooster waarin gebouwd/gesloopt wordt
	 */
	KubusRooster kr;
	
	int aantalBouwOpdrachten, aantalSloopOpdrachten;

	/**
	 * contructor, initialiseer de variableCollection
	 * @param kr het actuele KubusRooster
	 */
	public Interpreter(KubusRooster kr)
	{	this.kr = kr;
		variableCollection = new VariableCollection();
	}	
	
	/**
	 * de String s bevat het bouw/sloopprogramma:<br>
	 * splits s in de verschillende regels en splits elke regel
	 * in de veschillende opdrachten; voer daarna alle opdrachten uit 
	 * @param s String met bouw/sloopprogramma
	 */
	public void execute(String s)
	{	
		aantalBouwOpdrachten = 0;
		aantalSloopOpdrachten = 0;
		
		String[] regels = s.split("\n");
		
		for (int regCnt = 0; regCnt < regels.length; regCnt++)
		{	String[] statements = regels[regCnt].split(";");
			for (int statCnt = 0; statCnt < statements.length; statCnt++)
			{	String tok = statements[statCnt];
				try
				{	executeCommand(tok);
				}
				catch(Exception e)
				{	
				}
			}
		}
	}
		
	/**
	 * voer de opdracht String s uit, deze heeft een van de volgende gedaantes:<br>
	 * 1) naam = range(s) gescheiden door komma's, ranges mogen begrenst worden door Expressies<br>
	 * 2) bouw Expressie1,Expressie2,Expressie3<br>
	 * 3) sloop Expressie1,Expressie2,Expressie3<br>
	 * in geval 1) creeer een nieuwe Variabele m.b.v. String s<br>
	 * zie methode setVariable in klasse VariableCollection<br>
	 * in geval 2) en 3) parse de Expressies en evalueer deze in alle waarden die 
	 * de variabelen in deze Expressies kunnen aannemen; voeg op deze posities kubusjes toe
	 * (geval 2) of verwijder op deze positie kubusjes (geval 3) 
	 * @param s de opdract String
	 */
	public void executeCommand(String s)
	{	s = s.trim();
		String bouwOpdracht = NabouwenAanzichtenGWT.rb.bouwOpdracht();
		String sloopOpdracht = NabouwenAanzichtenGWT.rb.sloopOpdracht();
		
		int bouwOpdrachtLengte = bouwOpdracht.length();
		int sloopOpdrachtLengte = sloopOpdracht.length();
		int index = s.indexOf("=");
		if (index > 0)
		{	String name = s.substring(0,index);
			if (Character.isLetter(name.charAt(0)))
			{	variableCollection.setVariable(s);
			}
			else return;
		}
		else if (s.indexOf(bouwOpdracht) == 0)
	    {	
			aantalBouwOpdrachten++;

			String[] bouwCoord = s.substring(bouwOpdrachtLengte).split(",");
	    	
			String tok1 = bouwCoord[0];
	    	tok1 = tok1.trim();
	    	Expressie e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok1 + "@")));

	    	String tok2 = bouwCoord[1];
	    	tok2 = tok2.trim();
	    	Expressie e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok2 + "@")));

	    	String tok3 = bouwCoord[2];
	    	tok3 = tok3.trim();
	    	Expressie e3 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok3 + "@")));
	    
	        Expressie eTot = new Optelling(new Optelling(e1, e2), e3);
	    	Vector varnamenVector = eTot.geefVarNamen();
	    	Vector varsVector = new Vector();
	    	for (int i = 0; i < variableCollection.getVariables().length; i++)
		    {	Variable v = variableCollection.getVariables()[i];
		    	
		    	for (int j = 0; j < varnamenVector.size(); j++)
			    {	String nameUsed = (String) varnamenVector.elementAt(j);
			    	Variable varUsed = variableCollection.getVariable(nameUsed);
			    	if (v == varUsed || varUsed.isUsedVar(v.getName())) 
			    	{	varsVector.addElement(v);
			    		break;
			    	}
			    }
		    }
		    Variable[] vars = new Variable[varsVector.size()];
		    for (int i = 0; i < varsVector.size(); i++)
		    {	vars[i] = (Variable)varsVector.elementAt(i);
		    }
		    
	    	if(Double.isNaN(e1.geefWaarde()) || Double.isNaN(e2.geefWaarde()) || Double.isNaN(e3.geefWaarde()))
	    	{	build(e1, e2, e3, vars);
	    	}
	    	else
	    	{  	build((int) e1.geefWaarde(), (int) e2.geefWaarde(), (int) e3.geefWaarde());
	    	}
		}
		else if(s.indexOf(sloopOpdracht)==0)
	    {	
			
			aantalSloopOpdrachten++;			
			
			String[] sloopCoord = s.substring(sloopOpdrachtLengte).split(",");
	    	
			String tok1 = sloopCoord[0];
	    	tok1 = tok1.trim();
	    	Expressie e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok1 + "@")));

	    	String tok2 = sloopCoord[1];
	    	tok2 = tok2.trim();
	    	Expressie e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok2 + "@")));

	    	String tok3 = sloopCoord[2];
	    	tok3 = tok3.trim();
	    	Expressie e3 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok3 + "@")));
	    	
	    	Expressie eTot = new Optelling(new Optelling(e1,e2),e3);
	    	Vector varnamenVector = eTot.geefVarNamen();
	    	Vector varsVector = new Vector();
	    	for(int i=0 ; i<variableCollection.getVariables().length; i++)
		    {	Variable v = variableCollection.getVariables()[i];
		    	
		    	for(int j=0 ; j<varnamenVector.size(); j++)
			    {	String nameUsed = (String)varnamenVector.elementAt(j);
			    	Variable varUsed = variableCollection.getVariable(nameUsed);
			    	if(v==varUsed || varUsed.isUsedVar(v.getName())) 
			    	{	varsVector.addElement(v);
			    	}
			    }
		    }
		    Variable[] vars = new Variable[varsVector.size()];
		    for(int i=0 ; i<varsVector.size(); i++)
		    {	vars[i] = (Variable)varsVector.elementAt(i);
		    }
		    
	    	if(Double.isNaN(e1.geefWaarde()) || Double.isNaN(e2.geefWaarde()) || Double.isNaN(e3.geefWaarde()))
	    	{	remove(e1,e2,e3,vars);
	    	}
	    	else
	    	{  	remove((int)e1.geefWaarde(),(int)e2.geefWaarde(),(int)e3.geefWaarde());
	    	}
		}
	}
	
	/**
	 * als vars.length == 0, voeg een kubusje toe op positie 
	 * (e1.geefWaarde(),e2.geefWaarde(),e3.geefWaarde())<br>
	 * als vars.length is groter dan 0, bepaal (recursief) de waarden van alle variabelen,
	 * subsitueer die in de Expressies en voeg kubusjes toe op de posities
	 * die ontstaan na substitutie 
	 * @param e1 x-Expressie
	 * @param e2 y-Expressie
	 * @param e3 z-Expressie
	 * @param vars array met Variables
	 */
	public void build(Expressie e1, Expressie e2, Expressie e3, Variable[] vars)
	{	if (vars.length == 0)
		{	build((int) e1.geefWaarde(),(int) e2.geefWaarde(),(int) e3.geefWaarde());
			return;
		}
		int[] values = vars[0].getValues();
		for (int i = 0; i < values.length; i++)
	    {	Expressie e1s = e1.substitueer(values[i],vars[0].getName());
	    	Expressie e2s = e2.substitueer(values[i],vars[0].getName());
	    	Expressie e3s = e3.substitueer(values[i],vars[0].getName());
			Variable[] newVars = new Variable[vars.length - 1];
	    	for (int j = 0; j < vars.length - 1; j++)
			{	vars[j + 1].substitueer(values[i], vars[0].getName());
				newVars[j] = vars[j + 1];
			}
			build(e1s, e2s, e3s, newVars);
	    }
	}
	
	/**
	 * als vars.length == 0, verwijder een kubusje op positie 
	 * (e1.geefWaarde(),e2.geefWaarde(),e3.geefWaarde())<br>
	 * als vars.length is groetr dan 0, bepaal (recursief) de waarden van alle variabelen,
	 * subsitueer die in de Expressies en verwijder kubusjes op de posities
	 * die ontstaan na substitutie 
	 * @param e1 x-Expressie
	 * @param e2 y-Expressie
	 * @param e3 z-Expressie
	 * @param vars array met Variables
	 */
	public void remove(Expressie e1, Expressie e2, Expressie e3, Variable[] vars)
	{	if (vars.length==0)
		{	remove((int)e1.geefWaarde(),(int)e2.geefWaarde(),(int)e3.geefWaarde());
			return;
		}
		int[] values = vars[0].getValues();
		for(int i=0 ; i<values.length; i++)
	    {	Expressie e1s = e1.substitueer(values[i],vars[0].getName());
	    	Expressie e2s = e2.substitueer(values[i],vars[0].getName());
	    	Expressie e3s = e3.substitueer(values[i],vars[0].getName());
	    	Variable[] newVars = new Variable[vars.length-1];
	    	for(int j=0 ; j<vars.length-1; j++)
			{	newVars[j] = vars[j+1];
			}
			remove(e1s, e2s, e3s, newVars);
	    }
	}
	
	/**
	 * voeg een kubusje toe op positie (x,y,z),
	 * dus op coordinaten (x-1,y-1,z-1) in het kubusrooster
	 * @param x x-positie
	 * @param y y-positie
	 * @param z z-positie
	 */
	public void build(int x, int y, int z)
	{	kr.voegKubusToe(x - 1, y - 1, z - 1);
	}

	/**
	 * verwijder een kubusje op positie (x,y,z),
	 * dus op coordinaten (x-1,y-1,z-1) in het kubusrooster
	 * @param x x-positie
	 * @param y y-positie
	 * @param z z-positie
	 */
	public void remove(int x, int y, int z)
	{	kr.verwijderKubus(x - 1, y - 1, z - 1);
	}
	
	
}
