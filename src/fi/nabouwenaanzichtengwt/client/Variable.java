package fi.nabouwenaanzichtengwt.client;

import java.util.*;

import fi.nabouwenaanzichtengwt.client.expressies.*;

/**
 * klasse die een variabele die bestaat uit een of meer ranges representeert;
 * zie klasse Interpreter 
 * @author Peter Boon
 */
public class Variable
{
	/**
	 * naam van de Variabele
	 */
	String name;
	/**
	 * Vector met paren Expressies die staan voor de grenzen van
	 * de ranges waarbinnen de Variablele waarden kan aannemen
	 */
	Vector borders;
	/**
	 * HashMap met als Keys alle namen van variabelen in de Expressies
	 * uit borders 
	 */
	HashMap borderValues;
	/**
	 * een Vector met alle waarden (Integers) tussen elk tweetal grenzen van elke range
	 */
	Vector values;
	
	/**
	 * constructor, initialiseer attributen
	 * @param name naam van de Variable
	 */
	public Variable(String name)
	{	this.name = name;
		borders = new Vector();
		borderValues = new HashMap();
	}	
	
	/**
	 * gegeven is een String bestaande uit een of meer ranges gescheiden door
	 * komma's; voor elke range stop de Expressies voor linker- en rechter grens
	 * in borders en de variabelen in deze Expressies in borderValues;
	 * een range bestaande uit een enkele Expressie is toegestaan 
	 * @param s String met range(s)
	 */
	public void setValues(String s)
	{	s = s.trim();

		String[] valueStrings = s.split(",");
		for (int tokCnt = 0; tokCnt < valueStrings.length; tokCnt++)
		{	
			String tok = valueStrings[tokCnt];
			tok = tok.trim();
			int index = tok.indexOf("..");
			if(index>0)
			{	String leftExprString = tok.substring(0,index);
				leftExprString = leftExprString.trim();
				Expressie leftExpr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + leftExprString + "@")));
				borders.addElement(leftExpr);
				
				Vector namenLeft = leftExpr.geefVarNamen();
				for(int i=0 ; i<namenLeft.size(); i++)
			    {	borderValues.put(namenLeft.elementAt(i),"leeg");
			    }
			    
				String rightExprString = tok.substring(index+2);
				rightExprString = rightExprString.trim();
				Expressie rightExpr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + rightExprString + "@")));
				borders.addElement(rightExpr);
				
				Vector namenRight = rightExpr.geefVarNamen();
		    	for(int i=0 ; i<namenRight.size(); i++)
			    {	borderValues.put(namenRight.elementAt(i),"leeg");
			    }
			}
			else if(index==-1)
			{	Expressie expr = FormuleParser.parse(FormuleParser.schoon(FormuleParser.formuleString("$f" + tok + "@")));
				borders.addElement(expr);
				borders.addElement(expr);
				
				Vector namen = expr.geefVarNamen();
		    	for(int i=0 ; i<namen.size(); i++)
			    {	borderValues.put(namen.elementAt(i),"leeg");
			    }
			}	
		}
	}
	
	/**
	 * check of de String s een naam is die voorkomt als
	 * Key in de HashMap borderValues  
	 * @param s zoek deze String
	 * @return true/false
	 */
	public boolean isUsedVar(String s)
	{	return borderValues.containsKey(s);
	}
	
	/**
	 * vervang de entry met Key varnaam in borderValues
	 * door (varnaam,value)
	 * @param value waarde van de Key
	 * @param varnaam naam van de Key
	 */
	public void substitueer(int value, String varnaam)
	{	if(borderValues.containsKey(varnaam))
			borderValues.put(varnaam,new Integer(value));
	}
	
	/**]
	 * maak een Vector met alle paren borders (grenzen van een range); als 
	 * deze borders variabelen bevatten, subsitueer deze dan
	 * @return Vector met alle paren borders
	 */
	public Vector substitueerBorders()
	{	Vector v = new Vector();
		for(int i=0 ; i< borders.size(); i++)
		{	v.addElement(((Expressie) borders.elementAt(i)).substitueer(0,"geen"));
		}
		for (Iterator<String> e = borderValues.keySet().iterator() ; e.hasNext() ;)
		{	String key = (String)e.next();
			int value = ((Integer) borderValues.get(key)).intValue();
			for(int i=0 ; i<v.size(); i++)
		    {	Expressie b = (Expressie)v.elementAt(i);
		    	b = b.substitueer(value,key);
		    	v.setElementAt(b,i);
		    }
     	}
     	return v;

	}
	
	/**
	 * initialiseer de Vector values met alle waarden (Integers) tussen elk tweetal grenzen van elke range)
	 */
	public void makeValues()
	{	values = new Vector();
		Vector expSub = substitueerBorders();
		for(int i=0 ; i<expSub.size(); i+=2)
		{	int leftBorder = (int)((Expressie)expSub.elementAt(i)).geefWaarde();

			int rightBorder = (int)((Expressie)expSub.elementAt(i+1)).geefWaarde();

			for(int j=leftBorder ; j<=rightBorder; j++)
		    {	values.addElement(new Integer(j));
		    }
		}
	}
	
	/**
	 * creeer de Vector (of Integers) values en maak
	 * hiervan een array of integer
	 * @return values als array of integer
	 */
	public int[] getValues()
	{	makeValues();
		int[] intValues = new int[values.size()];
		for(int i=0 ; i<values.size(); i++)
	    {	intValues[i] = ((Integer)values.elementAt(i)).intValue();
	    }
	    return intValues;
	}
	
	/**
	 * geef de naam van deze Variable
	 * @return name
	 */
	public String getName()
	{	return name;
	}
}
