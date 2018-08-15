package fi.nabouwenaanzichtengwt.client;

import java.util.*;

/**
 * klasse die een Vector met Objecten van type Variable representeert;<br>
 * zie klasse Variable   
 * @author Peter Boon
 */
public class VariableCollection
{
	/**
	 * Vector met Objecten van type Variable
	 */
	Vector variables;
	
	/**
	 * constructor: initialiseer de Vector variables
	 */
	public VariableCollection()
	{	variables = new Vector();
	}	
	
	/**
	 * gegeven een String van de vorm naam=range(s) (zie klasse Variable)
	 * verwijder variables met name == naam en voeg eem nieuwe Variable toe
	 * met name naam en values de grenzen van de range(s)<br>
	 * zie methode setValues in klasse Variable
	 * @param s String van de vorm naam=range(s)
	 */
	public void setVariable(String s)
	{	s = s.trim();
		int index = s.indexOf("=");
		String name = s.substring(0,index);
		name = name.trim();
		for(int i=0 ; i<variables.size(); i++)
	    {	Variable v = (Variable)variables.elementAt(i);
	    	if(v.getName().equals(name))
	    	{	variables.removeElementAt(i);
	    		break;
	    	}
	    }
		String valueString = s.substring(index+1);
		valueString = valueString.trim();
		Variable var = new Variable(name);
		var.setValues(valueString);
		variables.addElement(var);
	}
		
	/**
	 * maak een array met alle Variables in deze VariableCollecton
	 * @return array met alle Variables
	 */
	public Variable[] getVariables()
	{	Variable[] vars = new Variable[variables.size()];
		for(int i=0 ; i<variables.size(); i++)
	    {	vars[i] = (Variable)variables.elementAt(i);
	    }
	    return vars;
	}
	
	/**
	 * vindt de Variable met naam name (if any)
	 * @param name naam van de te zoeken Variable
	 * @return de Variable met naam name of null
	 */
	public Variable getVariable(String name)
	{	Variable[] vars = getVariables();
		for(int i=0 ; i<vars.length; i++)
		{	if(vars[i].getName().equals(name))
			{	return vars[i];
			}
		}
		return null;
	}
}
