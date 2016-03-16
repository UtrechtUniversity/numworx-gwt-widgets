package fi.nabouwenaanzichtengwt.client;

import java.util.*;
import fi.nabouwenaanzichtengwt.client.expressies.*;

public class Interpreter 
{
	VariableCollection variableCollection;
	KubusRooster kr;
	
	int aantalBouwOpdrachten, aantalSloopOpdrachten;
	
	public Interpreter(KubusRooster kr)
	{	this.kr = kr;
		variableCollection = new VariableCollection();
	}	
	
	public void execute(String s)
	{	
		aantalBouwOpdrachten = 0;
		aantalSloopOpdrachten = 0;
		
		//StringTokenizer tokenizer = new StringTokenizer(s,";\n");
		
		String[] regels = s.split("\n");
System.out.println("reg = " + regels.length);		
		for (int regCnt = 0; regCnt < regels.length; regCnt++)
		{	String[] statements = regels[regCnt].split(";");
			for (int statCnt = 0; statCnt < statements.length; statCnt++)
			{	String tok = statements[statCnt];
				try
				{	executeCommand(tok);
System.out.println("tok = " + tok);				
				}
				catch(Exception e)
				{	
				}
			}
		}
		
		//while(tokenizer.hasMoreTokens())
	    //{	String tok = tokenizer.nextToken();
		//	try
		//	{	executeCommand(tok);
		//	}
		//	catch(Exception e)
		//	{	
		//	}
		//}
	}
		
	public void executeCommand(String s)
	{	s = s.trim();
		String bouwOpdracht = NabouwenAanzichtenGWT.rb.bouwOpdracht();
		String sloopOpdracht = NabouwenAanzichtenGWT.rb.sloopOpdracht();
		
//System.out.println(bouwOpdracht);
//System.out.println(sloopOpdracht);
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
//System.out.println("bouwen");

			aantalBouwOpdrachten++;
			
			//StringTokenizer tokenizer = new StringTokenizer(s.substring(bouwOpdrachtLengte), ",");
			String[] bouwCoord = s.substring(bouwOpdrachtLengte).split(",");
	    	
	    	//String tok1 = tokenizer.nextToken();
			String tok1 = bouwCoord[0];
	    	tok1 = tok1.trim();
	    	Expressie e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok1 + "@")));
	    	//String tok2 = tokenizer.nextToken();
	    	String tok2 = bouwCoord[1];
	    	tok2 = tok2.trim();
	    	Expressie e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok2 + "@")));
	    	//String tok3 = tokenizer.nextToken();
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
//System.out.println("slopen");			
			aantalSloopOpdrachten++;			
			
			//StringTokenizer tokenizer = new StringTokenizer(s.substring(sloopOpdrachtLengte),",");
			String[] sloopCoord = s.substring(sloopOpdrachtLengte).split(",");
	    	
	    	//String tok1 = tokenizer.nextToken();
			String tok1 = sloopCoord[0];
	    	tok1 = tok1.trim();
	    	Expressie e1 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok1 + "@")));
	    	//String tok2 = tokenizer.nextToken();
	    	String tok2 = sloopCoord[1];
	    	tok2 = tok2.trim();
	    	Expressie e2 = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok2 + "@")));
	    	//String tok3 = tokenizer.nextToken();
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
	
	public void remove(Expressie e1, Expressie e2, Expressie e3, Variable[] vars)
	{	if(vars.length==0)
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
	
	public void build(int x, int y, int z)
	{	kr.voegKubusToe(x - 1, y - 1, z - 1);
	}
	
	public void remove(int x, int y, int z)
	{	kr.verwijderKubus(x - 1, y - 1, z - 1);
	}
	
	
}
