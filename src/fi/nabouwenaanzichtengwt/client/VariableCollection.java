package fi.nabouwenaanzichtengwt.client;

import java.util.*;

public class VariableCollection
{
	Vector variables;
	
	public VariableCollection()
	{	variables = new Vector();
	}	
	
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
		
	public Variable[] getVariables()
	{	Variable[] vars = new Variable[variables.size()];
		for(int i=0 ; i<variables.size(); i++)
	    {	vars[i] = (Variable)variables.elementAt(i);
	    }
	    return vars;
	}
	
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
