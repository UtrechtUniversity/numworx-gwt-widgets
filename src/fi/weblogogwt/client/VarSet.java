package fi.weblogogwt.client;

import java.util.*;
import fi.weblogogwt.client.expressies.*;

public class VarSet 
{
	/**
	 * 'Level' in the stack of calls. Every call of a Deeltaak will increase the level, returning
	 * from a Deeltaak will decrease the level. In this way we will be able to remove local variables
	 * when their block finishes and print vars in a stack-trace manner.
	 */
	private int level;
	/**
	 * List of all variables. New vars will be added to the end, so the list will be ordered
	 * with repect to the level that we're in.
	 */
	private ArrayList<TAVariable> variabelen;
	/**
	 * Descriptions associated with each level, such as 'In Deeeltaak vierkant'.
	 * Using an array as a stack :-)
	 */
	private String[] levelDescriptions;
	
	public VarSet()
	{	
		variabelen = new ArrayList<TAVariable>();
		level = 0;
		levelDescriptions = new String[100];
		levelDescriptions[0] = "Tekenalgoritme";
	}
	
	public int getLevel()
	{
		return level;
	}

	void increaseLevel(String description)
	{
		level++;
		levelDescriptions[level] = description;
		//System.out.println(toString());
	}
	
	void decreaseLevel()
	{
		//System.out.println(toString());
		Iterator<TAVariable> it = variabelen.iterator();
		while ( it.hasNext() )
		{
			TAVariable tav = it.next();
			if ( tav.getLevel()==level )
			{
				it.remove();
			}
		}
		level--;
		//System.out.println(toString());
	}
	
	private TAVariable findVariable(String s)
	{
		// Note: if we introduce parameters to deeltaken, we may have to change the search order
		// to 'last to first', because we want to find a parameter before we find a global variable
		// of the same name, that was declared before te deeltaak-call.
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{
			if (s.equals(variabelen.get(i).getName()) ) return variabelen.get(i);
		}
		return null;
	}
	
	public void setVar(String varName, Expressie e)
	{	
		double value = getExpressionValue(e);
		if( !Double.isNaN(value) )
		{	
			TAVariable tav = findVariable(varName);
			if ( tav == null )
			{
				variabelen.add(new TAVariable(varName, value, level));
			} else
			{
				tav.setValue(value);
			}
		}
	}
	
	/**
	 * Add a variable to the varset that is a parameter. Parameters are 'call by value',
	 * so a value is added, not an expression!
	 * 
	 * @param varName
	 * @param value
	 */
	public void setParameter(String varName, double value)
	{
		variabelen.add(new TAVariable(varName, value, level));
	}
	
	public double getExpressionValue(Expressie e)
	{	
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{	
			e = e.substitueer(variabelen.get(i).getValue(), variabelen.get(i).getName());
		}
		return e.geefWaarde();
	}
	
	public VergelijkingMeerv getSubstEquation(VergelijkingMeerv v)
	{	
		for ( int i=variabelen.size()-1; i>=0; i-- )
		{	
			Expressie e = new BasisExpressie(variabelen.get(i).getValue());
			v = v.substitueer(e, variabelen.get(i).getName());
		}
		return v;
	}

	public String toString()
	{
		int runninglevel = 0;					// we want to print the description as soon as the leve in a variable is up.
		String indent = "  ";
		String s = levelDescriptions[0]+"\n";
		for ( TAVariable tav: variabelen )
		{
			while ( runninglevel < tav.getLevel() )
			{
				runninglevel++;
				s = s + indent + levelDescriptions[runninglevel]+"\n";
				indent = indent+"  ";
			}
			s = s + indent + " " + tav.getName()+": "+tav.getValue()+"\n";
		}
		while ( runninglevel < level )			// also print descriptions of remaining blocks that don't have variables
		{
			runninglevel++;
			s = s + indent + levelDescriptions[runninglevel]+"\n";
			indent = indent+"  ";
		}
		return s;
	}
}
